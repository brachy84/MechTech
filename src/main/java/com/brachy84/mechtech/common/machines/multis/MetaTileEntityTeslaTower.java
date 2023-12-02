package com.brachy84.mechtech.common.machines.multis;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.api.ToroidBlock;
import com.brachy84.mechtech.api.capability.GoodEnergyContainerList;
import com.brachy84.mechtech.common.MTConfig;
import com.brachy84.mechtech.common.cover.CoverWirelessReceiver;
import com.brachy84.mechtech.network.NetworkHandler;
import com.brachy84.mechtech.network.packets.STeslaTowerEffect;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerBatteryBuffer;
import gregtech.api.cover.Cover;
import gregtech.api.cover.CoverableView;
import gregtech.api.damagesources.DamageSources;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.*;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityTeslaTower extends MultiblockWithDisplayBase {

    public static void initTorusBlocks() {
        ToroidBlock.create(Materials.Copper)
                .setAmpsPerBlock(0.11f)
                .setDmgModifier(7)
                .setRangeModifier(6)
                .register();
    }

    public static final int TORUS_BLOCK_COUNT = 92;
    public static final int SCAN_TICKS = 100;

    // TODO: coil tier & coil height effects?
    private int coilHeight;
    private float dmg;
    private double range;
    private int maxAmps;
    private long voltage;

    private int scanX, scanY, scanZ;
    private Vec3d minPos, maxPos;
    private BlockPos center;

    private long ampsUsed;

    private boolean defenseMode;

    private GoodEnergyContainerList energyContainerList = null;

    private final Map<BlockPos, EnumFacing> energyHandlers = new HashMap<>();
    private final List<BlockPos> toRemove = new ArrayList<>();
    private final Set<BlockPos> effectQueue = new HashSet<>();
    private final List<EntityLivingBase> livings = new ArrayList<>();

    public MetaTileEntityTeslaTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            if (defenseMode)
                updateDefenseMode();
            else
                updateWirelessEnergyMode();
        }
    }

    private boolean isEntityInRange(Entity entity) {
        return Math.sqrt(center.distanceSq(entity.posX, entity.posY, entity.posZ)) < range;
    }

    private void scanRange() {
        int d = (int) (maxPos.x - minPos.x);
        int totalBlocks = d * d * d;
        int blocksPerTick = totalBlocks / SCAN_TICKS;
        int scannedBlocks = 0;

        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        while (scannedBlocks <= blocksPerTick) {
            for (; scanX < d; scanX++) {
                for (; scanZ < d; scanZ++) {
                    for (; scanY < d; scanY++) {
                        if (++scannedBlocks > blocksPerTick) {
                            pos.release();
                            return;
                        }
                        pos.setPos(minPos.x + scanX, minPos.y + scanY, minPos.z + scanZ);
                        if (center.getDistance(pos.getX(), pos.getY(), pos.getZ()) > range) {
                            continue;
                        }
                        TileEntity te = getWorld().getTileEntity(pos);
                        if (te == null) {
                            continue;
                        }
                        CoverableView coverable = te.getCapability(GregtechTileCapabilities.CAPABILITY_COVER_HOLDER, null);
                        if (coverable == null) {
                            continue;
                        }
                        for (EnumFacing facing : EnumFacing.VALUES) {
                            Cover cover = coverable.getCoverAtSide(facing);
                            if (cover instanceof CoverWirelessReceiver) {
                                energyHandlers.put(pos.toImmutable(), facing);
                                break;
                            }
                        }
                    }
                    scanY = 0;
                }
                scanZ = 0;
            }
            scanX = 0;
        }
        pos.release();
    }

    private void updateDefenseMode() {
        // gather living entities every 4 seconds
        if (getOffsetTimer() % 60 == 0) {
            livings.clear();
            for (Entity entity : getWorld().getEntitiesInAABBexcluding(null, new AxisAlignedBB(minPos, maxPos), entity -> entity.isEntityAlive() && entity instanceof EntityLivingBase)) {
                livings.add((EntityLivingBase) entity);
            }
        }

        // randomly damage a entity every 5 ticks
        if (getOffsetTimer() % 4 == 0 && livings.size() > 0 && energyContainerList.getEnergyStored() > 0) {
            int maxEntities = 10;
            int entitiesHit = 0;
            float dmg = this.dmg / 4f;
            Collections.shuffle(livings);
            Iterator<EntityLivingBase> iterator1 = livings.iterator();
            while (entitiesHit < maxEntities && iterator1.hasNext()) {
                EntityLivingBase living = iterator1.next();
                if (living == null || !living.isEntityAlive() || !isEntityInRange(living) || living instanceof EntityPlayer) {
                    iterator1.remove();
                    continue;
                }
                dmg = Math.min(dmg, living.getHealth());
                long energy = (long) (dmg * MTConfig.modularArmor.modules.teslaCoilDamageEnergyRatio);
                if (energyContainerList.getEnergyStored() < energy)
                    break;
                energyContainerList.removeEnergy(energy);
                living.attackEntityFrom(DamageSources.getElectricDamage(), dmg);
                playEffects(living);
                entitiesHit++;
            }
        }
    }

    private void updateWirelessEnergyMode() {
        // scan range for receivers every tick
        scanRange();

        // transmit energy to receivers every second
        long stored = energyContainerList.getEnergyStored();
        if (stored > 0 && getOffsetTimer() % 20 == 0) {
            ampsUsed = 0;
            for (Map.Entry<BlockPos, EnumFacing> entry : energyHandlers.entrySet()) {
                if (!transferEnergy(entry.getKey(), entry.getValue()) || ampsUsed >= maxAmps)
                    break;
            }
            toRemove.forEach(energyHandlers::remove);
            toRemove.clear();
        }

        // spawn bolts and make sounds to transmitted receivers every tick
        int toTick = effectQueue.size() / 20;
        double rest = effectQueue.size() / 20.0 - toTick;
        if (rest > 0 && GTValues.RNG.nextFloat() <= rest)
            toTick++;

        Iterator<BlockPos> iterator = effectQueue.iterator();
        while (toTick > 0 && iterator.hasNext()) {
            BlockPos pos = iterator.next();
            playEffects(pos);
            toTick--;
            iterator.remove();
        }

        // gather living entities every 4 seconds
        if (MTConfig.teslaTower.attackChance > 0 && getOffsetTimer() % 80 == 0) {
            double entityRange = (range * 2 + 1) / 3;
            livings.clear();
            for (Entity entity : getWorld().getEntitiesInAABBexcluding(null, new AxisAlignedBB(minPos, maxPos).shrink(entityRange), entity -> entity.isEntityAlive() && entity instanceof EntityLivingBase)) {
                livings.add((EntityLivingBase) entity);
            }
        }

        // randomly damage a entity every 5 ticks
        if (getOffsetTimer() % 5 == 0 && livings.size() > 0 && MTConfig.teslaTower.attackChance > 0 && stored > 0 && GTValues.RNG.nextFloat() < MTConfig.teslaTower.attackChance) {
            Collections.shuffle(livings);
            Iterator<EntityLivingBase> iterator1 = livings.iterator();
            while (iterator1.hasNext()) {
                EntityLivingBase living = iterator1.next();
                if (living == null || !living.isEntityAlive() || !isEntityInRange(living)) {
                    iterator1.remove();
                    continue;
                }
                living.attackEntityFrom(DamageSources.getElectricDamage(), dmg);
                break;
            }
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        coilHeight = context.getInt("Count") / 12 - 1;

        float amps = 0;
        for (Map.Entry<String, ToroidBlock> entry : ToroidBlock.getRegistryMap().entrySet()) {
            int count = context.getInt(entry.getKey());
            ToroidBlock block = entry.getValue();
            dmg += block.getDmgModifier() * count;
            range += block.getRangeModifier() * count;
            amps += block.getAmpsPerBlock() * count;
        }
        dmg /= TORUS_BLOCK_COUNT;
        range /= TORUS_BLOCK_COUNT;
        maxAmps = (int) Math.floor(amps);
        range *= Math.pow(coilHeight, 0.85);
        range = round(range);
        dmg = (float) round(dmg);
        energyContainerList = new GoodEnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        voltage = energyContainerList.getInputVoltage();
        center = getPos().offset(getFrontFacing().getOpposite(), 3).offset(EnumFacing.UP, 5 + coilHeight);
        Vec3d centerD = MechTech.getMiddleOf(center);
        double d = range * 2 + 1;
        minPos = new Vec3d(centerD.x - range, centerD.y - range, centerD.z - range);
        maxPos = minPos.add(d, d, d);
    }

    private double round(double num) {
        return (int) (num * 100 + 0.5) / 100.0;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("###########",
                        "###########",
                        "####CSC####",
                        "###CCCCC###",
                        "##CCCCCCC##",
                        "##CCCCCCC##",
                        "##CCCCCCC##",
                        "###CCCCC###",
                        "####CCC####",
                        "###########",
                        "###########")
                .aisle("###########",
                        "###########",
                        "###########",
                        "####LLL####",
                        "###L#P#L###",
                        "###LP#PL###",
                        "###L#P#L###",
                        "####LLL####",
                        "###########",
                        "###########",
                        "###########")
                .aisle("###########",
                        "###########",
                        "###########",
                        "###########",
                        "#####P#####",
                        "####P#P####",
                        "#####P#####",
                        "###########",
                        "###########",
                        "###########",
                        "###########")
                .aisle("###########",
                        "###########",
                        "###########",
                        "####LLL####",
                        "###L#P#L###",
                        "###LP#PL###",
                        "###L#P#L###",
                        "####LLL####",
                        "###########",
                        "###########",
                        "###########").setRepeatable(3, 32)
                .aisle("###########",
                        "###########",
                        "###########",
                        "###########",
                        "#####P#####",
                        "####P#P####",
                        "#####P#####",
                        "###########",
                        "###########",
                        "###########",
                        "###########")
                .aisle("###########",
                        "####TTT####",
                        "##TT###TT##",
                        "##T#####T##",
                        "#T###P###T#",
                        "#T##P#P##T#",
                        "#T###P###T#",
                        "##T#####T##",
                        "##TT###TT##",
                        "####TTT####",
                        "###########")
                .aisle("####TTT####",
                        "##TT###TT##",
                        "#T##TTT##T#",
                        "#T#T#P#T#T#",
                        "T#T##P##T#T",
                        "T#TPP#PPT#T",
                        "T#T##P##T#T",
                        "#T#T#P#T#T#",
                        "#T##TTT##T#",
                        "##TT###TT##",
                        "####TTT####")
                .aisle("###########",
                        "####TTT####",
                        "##TT###TT##",
                        "##T#####T##",
                        "#T#######T#",
                        "#T#######T#",
                        "#T#######T#",
                        "##T#####T##",
                        "##TT###TT##",
                        "####TTT####",
                        "###########")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(8).setMinGlobalLimited(1, 2))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1)))
                .where('T', ToroidBlock.traceabilityPredicate())
                .where('L', coilPredicate())
                .where('P', states(materialBlockState(Materials.Polyethylene)))
                .where('#', any())
                .build();
    }

    private IBlockState materialBlockState(Material material) {
        BlockCompressed block = MetaBlocks.COMPRESSED.get(material);
        if (block == null)
            return Blocks.AIR.getDefaultState();
        return block.getBlock(material);
    }

    private TraceabilityPredicate coilPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (blockState.getBlock() instanceof BlockWireCoil) {
                BlockWireCoil blockWireCoil = (BlockWireCoil) blockState.getBlock();
                BlockWireCoil.CoilType coilType = blockWireCoil.getState(blockState);
                Object currentCoilType = blockWorldState.getMatchContext().getOrPut("CoilType", coilType);
                if (!currentCoilType.toString().equals(coilType.getName())) {
                    blockWorldState.setError(new PatternStringError("gregtech.multiblock.pattern.error.coils"));
                    return false;
                } else {
                    blockWorldState.getMatchContext().increment("Count", 1);
                    blockWorldState.getMatchContext().getOrPut("VABlock", new LinkedList()).add(blockWorldState.getPos());
                    return true;
                }
            } else {
                return false;
            }
        }, () -> Arrays.stream(BlockWireCoil.CoilType.values()).map((type) -> new BlockInfo(MetaBlocks.WIRE_COIL.getState(type), null)).toArray(BlockInfo[]::new)).addTooltips("gregtech.multiblock.pattern.error.coils");
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE);
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = super.createUITemplate(entityPlayer);
        builder.widget(new CycleButtonWidget(61, 97, 100, 20, () -> defenseMode, val -> defenseMode = val, "Wireless Energy Mode", "Defense Mode")
                .setTooltipHoverString("mechtech.tesla_tower.ui.mode.tooltip"));
        return builder;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentString("Volt: " + voltage));
            textList.add(new TextComponentString("Amps: " + maxAmps));
            textList.add(new TextComponentString("Dmg: " + dmg));
            textList.add(new TextComponentString("Range: " + range));
            textList.add(new TextComponentString("Receivers: " + energyHandlers.size()));
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.STABLE_TITANIUM_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityTeslaTower(metaTileEntityId);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.MULTIBLOCK_WORKABLE_OVERLAY.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isStructureFormed() && energyHandlers.size() > 0, true);
    }

    private boolean hasCover(EnumFacing facing, TileEntity te) {
        CoverableView coverable = te.getCapability(GregtechTileCapabilities.CAPABILITY_COVER_HOLDER, facing);
        if (coverable == null)
            return false;
        return hasCover(facing, coverable);
    }

    private boolean hasCover(EnumFacing facing, CoverableView coverable) {
        return coverable.getCoverAtSide(facing) instanceof CoverWirelessReceiver;
    }

    private boolean transferEnergy(BlockPos pos, EnumFacing facing) {
        TileEntity te = getWorld().getTileEntity(pos);
        if (te == null || !hasCover(facing, te)) {
            toRemove.add(pos);
            return true;
        }
        IEnergyContainer energyContainer = te.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
        if (energyContainer == null || !energyContainer.inputsEnergy(facing) || energyContainer instanceof EnergyContainerBatteryBuffer) {
            toRemove.add(pos);
            return true;
        }

        if (voltage < energyContainer.getInputVoltage()) {
            GTLog.logger.info("Tier to high at {}" + MechTech.blockPosToString(pos));
            return true;
        }

        long volt = energyContainer.getInputVoltage() * 20;
        // The energy that will be lost (voltage * factor)
        long lost = (long) (volt * (1 - getLossFactor(center.getDistance(pos.getX(), pos.getY(), pos.getZ()) / range)));
        volt = Math.min(energyContainer.getEnergyCapacity() - energyContainer.getEnergyStored(), volt);
        // tesla does not have more power than what will be lost, abort
        if (energyContainerList.getEnergyStored() <= lost) {
            GTLog.logger.info("Not enough stored energy at {}", MechTech.blockPosToString(pos));
            return true;
        }
        energyContainerList.removeEnergy(lost);
        long stored = energyContainerList.getEnergyStored();
        if (stored == 0)
            return false;
        volt = Math.min(stored, volt);

        long changed = energyContainer.addEnergy(volt);
        if (changed == 0) {
            GTLog.logger.info("Nothing inserted at {}" + MechTech.blockPosToString(pos));
            return true;
        }
        ampsUsed++;
        if (-energyContainerList.removeEnergy(changed) != changed) {
            GTLog.logger.info("Could not drain enough energy");
            return false;
        }

        if (MTConfig.teslaTower.lightningChance > 0 && (MTConfig.teslaTower.lightningChance == 1 || GTValues.RNG.nextDouble() < MTConfig.teslaTower.lightningChance)) {
            //playEffects(pos);
            effectQueue.add(pos);
        }
        return true;
    }

    private double getLossFactor(double distance) {
        if (distance <= 0.8) {
            return -distance * 0.125 + 1;
        }
        return (1 + Math.exp(-0.030612249)) / (1 + Math.exp((distance - 0.98) / 0.03)) * 0.902;
    }

    private void playEffects(BlockPos target) {
        STeslaTowerEffect packet = new STeslaTowerEffect(center, MechTech.getMiddleOf(target));
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), center.getX(), center.getY(), center.getZ(), 64);
        NetworkHandler.sendToAllAround(packet, targetPoint);
    }

    private void playEffects(Entity target) {
        STeslaTowerEffect packet = new STeslaTowerEffect(center, MechTech.getMiddleOf(target)).setScale(5f);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), center.getX(), center.getY(), center.getZ(), 64);
        NetworkHandler.sendToAllAround(packet, targetPoint);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("DefenseMode", defenseMode);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        defenseMode = data.getBoolean("DefenseMode");
    }
}
