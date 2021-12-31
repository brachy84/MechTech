package com.brachy84.mechtech.common.machines.multis;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.api.TorusBlock;
import com.brachy84.mechtech.api.capability.GoodEnergyContainerList;
import com.brachy84.mechtech.common.MTConfig;
import com.brachy84.mechtech.common.cover.CoverWirelessReceiver;
import com.brachy84.mechtech.network.packets.STeslaTowerEffect;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.cover.CoverBehavior;
import gregtech.api.cover.ICoverable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.net.NetworkHandler;
import gregtech.api.pattern.*;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityTeslaTower extends MultiblockWithDisplayBase {

    public static void initTorusBlocks() {
        TorusBlock copper = TorusBlock.ofMaterial(Materials.Copper)
                .setAmpsPerBlock(0.2f)
                .setDmgModifier(20)
                .setRangeModifier(6)
                .setVoltageModifier(1);
        TorusBlock.register("copper", copper);
    }

    public static final int TORUS_BLOCK_COUNT = 92;
    public static final int SCAN_TICKS = 100;

    private int coilHeight;
    private float dmg;
    private double range;
    private int maxAmps;
    private long voltage;

    private int scanX, scanY, scanZ;
    private BlockPos center;

    private long ampsUsed;

    private GoodEnergyContainerList energyContainerList = null;

    private final Map<BlockPos, EnumFacing> energyHandlers = new HashMap<>();
    private final List<BlockPos> toRemove = new ArrayList<>();

    public MetaTileEntityTeslaTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            scanRange();
            if (energyContainerList.getEnergyStored() > 0 && getOffsetTimer() % 20 == 0) {
                ampsUsed = 0;
                for (Map.Entry<BlockPos, EnumFacing> entry : energyHandlers.entrySet()) {
                    if (!transferEnergy(entry.getKey(), entry.getValue()) || ampsUsed >= maxAmps)
                        break;
                }
                toRemove.forEach(energyHandlers::remove);
                toRemove.clear();
            }
        }
    }

    private void scanRange() {
        int d = (int) (range * 2 + 1);
        int totalBlocks = d * d * d;
        int blocksPerTick = totalBlocks / SCAN_TICKS;
        int scannedBlocks = 0;

        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        int xMin = (int) (center.getX() - range), yMin = (int) (center.getY() - range), zMin = (int) (center.getZ() - range);
        while (scannedBlocks <= blocksPerTick) {
            for (; scanX < d; scanX++) {
                for (; scanZ < d; scanZ++) {
                    for (; scanY < d; scanY++) {
                        if (++scannedBlocks > blocksPerTick) {
                            pos.release();
                            return;
                        }
                        pos.setPos(xMin + scanX, yMin + scanY, zMin + scanZ);
                        if (center.getDistance(pos.getX(), pos.getY(), pos.getZ()) > range) {
                            continue;
                        }
                        TileEntity te = getWorld().getTileEntity(pos);
                        if (te == null) {
                            continue;
                        }
                        ICoverable coverable = te.getCapability(GregtechTileCapabilities.CAPABILITY_COVERABLE, null);
                        if (coverable == null) {
                            continue;
                        }
                        for (EnumFacing facing : EnumFacing.VALUES) {
                            CoverBehavior cover = coverable.getCoverAtSide(facing);
                            if (cover instanceof CoverWirelessReceiver) {
                                GTLog.logger.info("Found receiver");
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

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        coilHeight = context.getInt("Count") / 12 - 1;

        float amps = 0;
        for (Map.Entry<String, TorusBlock> entry : TorusBlock.getRegistryMap().entrySet()) {
            int count = context.getInt(entry.getKey());
            TorusBlock block = entry.getValue();

            dmg += block.getDmgModifier() * count;
            range += block.getRangeModifier() * count;
            amps += block.getAmpsPerBlock() * count;
        }
        dmg /= TORUS_BLOCK_COUNT;
        range /= TORUS_BLOCK_COUNT;
        maxAmps = (int) Math.floor(amps);
        range *= coilHeight;
        range = round(range);
        dmg = (float) round(dmg);
        energyContainerList = new GoodEnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        voltage = energyContainerList.getInputVoltage();
        center = getPos().offset(getFrontFacing().getOpposite(), 3).offset(EnumFacing.UP, 5 + coilHeight);

        int xMin = (int) (center.getX() - range), yMin = (int) (center.getY() - range), zMin = (int) (center.getZ() - range);
        int d = (int) (range * 2 + 1);
        GTLog.logger.info("MinPos {}, MaxPos {}", MechTech.blockPosToString(new BlockPos(xMin, yMin, zMin)), MechTech.blockPosToString(new BlockPos(xMin + d, yMin + d, zMin + d)));
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
                .where('T', TorusBlock.traceabilityPredicate())
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
                BlockWireCoil.CoilType coilType = (BlockWireCoil.CoilType) blockWireCoil.getState(blockState);
                Object currentCoilType = blockWorldState.getMatchContext().getOrPut("CoilType", coilType);
                if (!currentCoilType.toString().equals(coilType.getName())) {
                    blockWorldState.setError(new PatternStringError("gregtech.multiblock.pattern.error.coils"));
                    return false;
                } else {
                    blockWorldState.getMatchContext().increment("Count", 1);
                    ((LinkedList) blockWorldState.getMatchContext().getOrPut("VABlock", new LinkedList())).add(blockWorldState.getPos());
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
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentString("Volt: " + voltage));
            textList.add(new TextComponentString("Amps: " + maxAmps));
            textList.add(new TextComponentString("Dmg: " + dmg));
            textList.add(new TextComponentString("Range: " + range));
            textList.add(new TextComponentString("Receivers: " + energyHandlers.size()));
            textList.add(new TextComponentString("Center: " + MechTech.blockPosToString(center)));
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.STABLE_TITANIUM_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityTeslaTower(metaTileEntityId);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.MULTIBLOCK_WORKABLE_OVERLAY.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isStructureFormed() && energyHandlers.size() > 0, true);
    }

    private boolean hasCover(EnumFacing facing, TileEntity te) {
        ICoverable coverable = te.getCapability(GregtechTileCapabilities.CAPABILITY_COVERABLE, facing);
        if (coverable == null)
            return false;
        return hasCover(facing, coverable);
    }

    private boolean hasCover(EnumFacing facing, ICoverable coverable) {
        return coverable.getCoverAtSide(facing) instanceof CoverWirelessReceiver;
    }

    private boolean transferEnergy(BlockPos pos, EnumFacing facing) {
        TileEntity te = getWorld().getTileEntity(pos);
        if (te == null || !hasCover(facing, te)) {
            toRemove.add(pos);
            return true;
        }
        IEnergyContainer energyContainer = te.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
        if (energyContainer == null || !energyContainer.inputsEnergy(facing)/* || energyContainer instanceof EnergyContainerBatteryBuffer*/) {
            toRemove.add(pos);
            return true;
        }

        if(voltage < energyContainer.getInputVoltage())
            return true;

        // The energy that will be lost (max voltage * factor)
        long lost = (long) (voltage * getLossFactor(center.getDistance(pos.getX(), pos.getY(), pos.getZ()) /  range));

        long volt = energyContainer.getInputVoltage() * 20;
        volt = Math.min(energyContainer.getEnergyCapacity() - energyContainer.getEnergyStored(), volt);
        // tesla does not have more power than what will be lost, abort
        if(energyContainerList.getEnergyStored() <= lost) {
            return true;
        }
        energyContainerList.removeEnergy(lost);
        volt = Math.min(energyContainerList.getEnergyStored(), volt);

        if (volt == 0)
            return false;

        long changed = energyContainer.addEnergy(volt);
        if (changed == 0)
            return true;
        ampsUsed++;
        if (-energyContainerList.removeEnergy(changed) != changed) {
            GTLog.logger.info("Could not drain enough energy");
            return false;
        }

        if(MTConfig.teslaTower.lightningChance > 0 && (MTConfig.teslaTower.lightningChance == 1 || GTValues.RNG.nextDouble() < MTConfig.teslaTower.lightningChance)) {
            playEffects(pos);
        }
        return true;
    }

    private double getLossFactor(double distance) {
        if(distance <= 0.8) {
            return -distance * 0.125 + 1;
        }
        return (1 + Math.exp(-0.030612249)) / (1 + Math.exp((distance - 0.98) / 0.03)) * 0.902;
    }

    private void playEffects(BlockPos target) {
        STeslaTowerEffect packet = new STeslaTowerEffect(center, target);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), center.getX(), center.getY(), center.getZ(), 64);
        NetworkHandler.channel.sendToAllAround(packet.toFMLPacket(), targetPoint);
    }
}