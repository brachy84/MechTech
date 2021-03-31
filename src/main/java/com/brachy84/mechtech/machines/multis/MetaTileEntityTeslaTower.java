package com.brachy84.gtforegoing.machines.multis;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.gtforegoing.MTConfig;
import com.brachy84.gtforegoing.capability.MTCapabilities;
import com.brachy84.gtforegoing.capability.MTEnergyContainerList;
import com.brachy84.gtforegoing.cover.CoverWirelessReceiver;
import com.brachy84.gtforegoing.integration.crafttweaker.IRangeFunction;
import com.brachy84.gtforegoing.utils.BlockPosDim;
import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.minecraft.CraftTweakerMC;
import gregicadditions.GAMaterials;
import gregicadditions.capabilities.GregicAdditionsCapabilities;
import gregicadditions.capabilities.impl.QubitContainerList;
import gregicadditions.item.GAHeatingCoil;
import gregicadditions.item.GAMetaBlocks;
import gregicadditions.machines.overrides.GAMetaTileEntityBatteryBuffer;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.cover.ICoverable;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.BlockWorldState;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;
import stanhebben.zenscript.annotations.*;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;

@ZenClass("mods.mechtech.TeslaTower")
@ZenRegister
public class MetaTileEntityTeslaTower extends MultiblockWithDisplayBase {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
            MultiblockAbility.INPUT_ENERGY,
            MultiblockAbility.IMPORT_ITEMS,
            MTCapabilities.LIMITED_ITEM_INPUT,
            GregicAdditionsCapabilities.INPUT_QBIT
    };

    private static final List<TorusBlock> TORUS_BLOCKS = Lists.newArrayList(
            new TorusBlock(0.01f, -100, Materials.Plastic),
            new TorusBlock(0.92f, 5, Materials.BlackBronze),
            new TorusBlock(1.04f, -10, Materials.Steel),
            new TorusBlock(1.09f, 20, GAMaterials.UHVSuperconductor),
            new TorusBlock(1.13f, 25, GAMaterials.UEVSuperconductor),
            new TorusBlock(1.16f, 30, GAMaterials.UIVSuperconductor),
            new TorusBlock(1.19f, 35, GAMaterials.UMVSuperconductor),
            new TorusBlock(1.23f, 40, GAMaterials.UXVSuperconductor)
    );

    /**
     * @param material the name of the material
     * @param rangeMod the modifier when all blocks are this material. values below 0 will make it always 0
     * @param conductivity how much energyLoss will be subtracted when all blocks are this material
     */
    @ZenMethod
    public static void addTorusBlock(@NotNull Material material, float rangeMod, float conductivity) {
        TORUS_BLOCKS.add(new TorusBlock(rangeMod, conductivity, material));
    }

    @ZenMethod
    public static void addTorusBlock(@NotNull IBlock block, float rangeMod, float conductivity) {
        TORUS_BLOCKS.add(new TorusBlock(rangeMod, conductivity, CraftTweakerMC.getBlock(block)));
    }

    @ZenMethod
    public static void removeTorusBlock(@NotNull Material material) {
        TorusBlock block = TorusBlock.get(material);
        if(block != null) {
            TORUS_BLOCKS.remove(block);
        }
    }

    @ZenMethod
    public static void removeTorusBlock(@NotNull IBlock iblock) {
        TorusBlock block = TorusBlock.get(CraftTweakerMC.getBlock(iblock));
        TORUS_BLOCKS.remove(block);
    }

    private static final int TORUS_BLOCK_AMOUNT = 124;

    private final double rangeFactor = MTConfig.multis.teslaTower.rangeFactor;

    /**
     * The amount of coil layers
     */
    private int coilHeight;

    /**
     * The coil tier used. All coils must be the same tier
     */
    private int coilTier;

    /**
     * The range where machines gets powered
     * center is {@link #center}
     * = pow(coilHeight, 0.6) * rangeFactor
     */
    private double range;

    /**
     * This is the voltage of the energy input with the highest voltage
     */
    private long inputVoltage;

    /**
     * The amount of amps per pulse
     */
    private long amps;

    /**
     * Gets reset before each pulse
     */
    private long ampsUsed;

    /**
     * The maximum amount of energy send per pulse
     */
    private long voltagePerPulse;

    @ZenProperty
    public IRangeFunction rangeFunction = (tower) -> Math.pow(tower.coilHeight, 0.6) * tower.rangeFactor;

    /**
     * How much qubits should be consumed per pulse per machine out of range
     */
    private final int qubitCost = MTConfig.multis.teslaTower.qubitCost;

    private final float energyLossBase = MTConfig.multis.teslaTower.baseEnergyLossPerecentage;

    private float energyLoss;

    private List<IItemHandlerModifiable> inputInventorys = new ArrayList<>();
    private QubitContainerList inputQubit;
    private IItemHandlerModifiable dataInventory;
    private MTEnergyContainerList inputEnergy;
    private BlockPos center = getPos();
    private float rangeModifier = 0;
    private float energyLossModifier = 0;

    private Map<String, Integer> failedPositions = new HashMap<>();

    private List<BlockPos> allBlocks = new ArrayList<>();
    private List<BlockPosDim> receivers = new ArrayList<>();
    private List<BlockPosDim> removeLater = new ArrayList<>();

    public MetaTileEntityTeslaTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        coilHeight = context.getOrDefault("height", 0);
        // there is no way CoilType doesn't exist
        coilTier = ((GAHeatingCoil.CoilType) context.get("CoilType")).ordinal()+1;
        range = Math.max(0, rangeFunction.run(this) * rangeModifier);
        energyLoss = energyLossBase - energyLossModifier;
        amps = 8 + coilTier * 8;

        inputQubit = new QubitContainerList(getAbilities(GregicAdditionsCapabilities.INPUT_QBIT));
        inputInventorys = getAbilities(MultiblockAbility.IMPORT_ITEMS);
        if (inputInventorys.size() > 0) {
            dataInventory = inputInventorys.get(0);
        }
        if (dataInventory == null)
            dataInventory = getImportItems();
        inputEnergy = new MTEnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputVoltage = inputEnergy.getMaxInputVoltage();
        center = getPos().offset(getFrontFacing().getOpposite(), 4).offset(EnumFacing.UP, 2 + coilHeight / 2);
        allBlocks = createCircularBox();
        receivers = scanRange();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        rangeModifier = 0;
        energyLossModifier = 0;
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            // send energy every 0.5 seconds (10 ticks)
            if (getTimer() % 10 == 0) {
                if(MTConfig.multis.teslaTower.allowInterdimensionalTransfer || MTConfig.multis.teslaTower.allowOutOfRangeTransfer)
                    scanDataInventory();
                ampsUsed = 0L;
                for(BlockPosDim pos : receivers) {
                    feedEnergy(pos, getFeedAmount());
                    if(ampsUsed >= amps) {
                        break;
                    }
                }
            }
        }
    }

    protected void scanDataInventory() {
        for(IItemHandlerModifiable itemHandler : inputInventorys) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.hasTagCompound()) {
                    NBTTagCompound tag = stack.getSubCompound("BlockPos");
                    if (tag != null) {
                        int dim = tag.getShort("dim");
                        BlockPosDim pos = new BlockPosDim(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), dim);
                        if(!receivers.contains(pos)) {
                            receivers.add(pos);
                        }
                    }
                }
            }
            if(removeLater.size() > 0) {
                for(BlockPosDim pos : removeLater) {
                    receivers.remove(pos);
                }
                removeLater.clear();
            }
        }
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(BlockPattern.RelativeDirection.RIGHT, BlockPattern.RelativeDirection.BACK, BlockPattern.RelativeDirection.UP)
                .aisle("#############",
                        "#############",
                        "#####BBB#####",
                        "####BBBBB####",
                        "###BBBBBBB###",
                        "##BBBBBBBBB##",
                        "##BBBBBBBBB##",
                        "##BBBBBBBBB##",
                        "###BBBBBBB###",
                        "####BBBBB####",
                        "#####BSB#####",
                        "#############",
                        "#############")
                .aisle("#############",
                        "#############",
                        "#############",
                        "#####CCC#####",
                        "####C###C####",
                        "###C##P##C###",
                        "###C#P#P#C###",
                        "###C##P##C###",
                        "####C###C####",
                        "#####CCC#####",
                        "#############",
                        "#############",
                        "#############")
                .aisle("#############",
                        "#############",
                        "#############",
                        "#############",
                        "#############",
                        "######P######",
                        "#####P#P#####",
                        "######P######",
                        "#############",
                        "#############",
                        "#############",
                        "#############",
                        "#############")
                .aisle("#############",
                        "#############",
                        "#############",
                        "#############",
                        "#####CCC#####",
                        "####C#P#C####",
                        "####CP#PC####",
                        "####C#P#C####",
                        "#####CCC#####",
                        "#############",
                        "#############",
                        "#############",
                        "#############")
                .setRepeatable(5, 32)
                .aisle("#############",
                        "#############",
                        "#############",
                        "#############",
                        "#############",
                        "######P######",
                        "#####P#P#####",
                        "######P######",
                        "#############",
                        "#############",
                        "#############",
                        "#############",
                        "#############")
                .aisle("#############",
                        "####GGGGG####",
                        "###G#####G###",
                        "##G#######G##",
                        "#G#########G#",
                        "#G####P####G#",
                        "#G###P#P###G#",
                        "#G####P####G#",
                        "#G#########G#",
                        "##G#######G##",
                        "###G#####G###",
                        "####GGGGG####",
                        "#############")
                .aisle("####GGGGG####",
                        "###G#####G###",
                        "##G#GGGGG#G##",
                        "#G#G##G##G#G#",
                        "G#G###G###G#G",
                        "G#G##PGP##G#G",
                        "G#GGGG#GGGG#G",
                        "G#G##PGP##G#G",
                        "G#G###G###G#G",
                        "#G#G##G##G#G#",
                        "##G#GGGGG#G##",
                        "###G#####G###",
                        "####GGGGG####")
                .aisle("#############",
                        "####GGGGG####",
                        "###G#####G###",
                        "##G#######G##",
                        "#G#########G#",
                        "#G####P####G#",
                        "#G###P#P###G#",
                        "#G####P####G#",
                        "#G#########G#",
                        "##G#######G##",
                        "###G#####G###",
                        "####GGGGG####",
                        "#############")
                .where('B', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
                .where('P', blockPredicate(MetaBlocks.COMPRESSED.get(Materials.Plastic)))
                .where('C', coilPredicate())
                .where('G', torusPredicate())
                .where('S', selfPredicate())
                .where('#', (tile) -> true)
                .build();
    }

    public Predicate<BlockWorldState> torusPredicate() {
        return (blockWorldState -> {
            Block block = blockWorldState.getBlockState().getBlock();
            TorusBlock torusBlock = TorusBlock.get(block);
            if(torusBlock != null) {
                energyLossModifier += torusBlock.getConductivity();
                rangeModifier += torusBlock.getRangeModifier();
                return true;
            }
            return false;
        });
    }

    /*public Predicate<BlockWorldState> torusPredicateCopy() {
        Block[] blocks = new Block[ALLOWED_TORUS_MATERIALS.length];
        for(int i = 0; i < ALLOWED_TORUS_MATERIALS.length; i++) {
            blocks[i] = MetaBlocks.COMPRESSED.get(ALLOWED_TORUS_MATERIALS[i]);
        }
        return blockPredicate(blocks);
    }*/

    public Predicate<BlockWorldState> coilPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            int coilTier, currentCoilTier;
            if(blockState.getBlock() instanceof BlockWireCoil) {
                BlockWireCoil coil = (BlockWireCoil) blockState.getBlock();
                coilTier = coil.getState(blockState).ordinal();
                currentCoilTier = blockWorldState.getMatchContext().getOrPut("CoilTier", coilTier);
            } else if(blockState.getBlock() instanceof GAHeatingCoil) {
                GAHeatingCoil coil = (GAHeatingCoil) blockState.getBlock();
                coilTier = coil.getState(blockState).ordinal() + 9;
                currentCoilTier = blockWorldState.getMatchContext().getOrPut("CoilTier", coilTier);
            } else {
                return false;
            }

            if (currentCoilTier == coilTier) {
                if (!blockWorldState.getLayerContext().getOrDefault("counted", false)) {
                    blockWorldState.getLayerContext().set("counted", true);
                    blockWorldState.getMatchContext().increment("height", 1);
                }
                return true;
            } else {
                return false;
            }
        };
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GAMetaBlocks.METAL_CASING.get(GAMaterials.TungstenTitaniumCarbide);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.MULTIBLOCK_WORKABLE_OVERLAY.render(renderState, translation, pipeline, getFrontFacing(),
                isStructureFormed() && receivers.size() > 0 && inputEnergy.getEnergyStored() > 0);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityTeslaTower(metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        Style red = new Style().setColor(TextFormatting.RED);

        if (isStructureFormed()) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.range", df.format(range)));
            textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.voltage", inputVoltage));
            textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.receivers", receivers.size()));
            textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.amps", amps, ampsUsed));
            textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.loss", energyLoss));
            if (failedPositions.size() > 0) {
                textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.fail"));
                for (Map.Entry<String, Integer> pos : failedPositions.entrySet()) {
                    textList.add(new TextComponentString(" - " + pos.getKey() + " E:" + pos.getValue()));
                }
            }
        }
    }

    protected IBlockState getCasingState() {
        return GAMetaBlocks.getMetalCasingBlockState(GAMaterials.TungstenTitaniumCarbide);
    }

    public long getMaxFeedAmount() {
        return inputVoltage * 4 * coilTier;
    }

    public long getFeedAmount() {
        long stored = inputEnergy.getEnergyStored();
        return Math.min(stored, inputVoltage);
    }

    /**
     * feeds the machine at pos
     *
     * @param pos    the position to fill
     * @param amount the amount of energy
     * @return the amount of energy fed
     */
    public long feedEnergy(BlockPosDim pos, long amount) {
        boolean consumeQubit = false;
        World world = getWorld();

        if(!isInRange(pos)) {
            if(!MTConfig.multis.teslaTower.allowOutOfRangeTransfer) {
                return 0L;
            }
            consumeQubit = true;
        }
        if(!isInDim(pos.getDim())) {
            if(!MTConfig.multis.teslaTower.allowInterdimensionalTransfer) {
                return 0L;
            }
            consumeQubit = true;
            world = DimensionManager.getWorld(pos.getDim());
        }

        long charged = 0L;
        TileEntity tile = world.getTileEntity(pos);
        IEnergyContainer container = null;
        String sPos = pos.toString();
        if(world.getBlockState(pos) == Blocks.AIR.getDefaultState()) {
            // block is broken
            removeLater.add(pos);
            failedPositions.remove(sPos);
            return 0;
        }
        if (tile instanceof MetaTileEntityHolder) {
            MetaTileEntity tileEntity = ((MetaTileEntityHolder) tile).getMetaTileEntity();
            if (canInsertInto(tileEntity) && hasCover(tileEntity)) {
                container = ((MetaTileEntityHolder) tile).getMetaTileEntity().getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
            } else {
                removeLater.add(pos);
                failedPositions.remove(sPos);
                return 0;
            }
        }

        if (container != null && container.inputsEnergy(EnumFacing.NORTH)) {
            long ampsToConsume = container.getInputAmperage() * (container.getInputVoltage() / inputVoltage);
            if(ampsToConsume < 1 || ampsUsed + ampsToConsume > amps) {
                return 0L;
            }
            if(consumeQubit) {
                if(inputQubit == null) {
                    failed(sPos, 1);
                    return 0;
                }
                if(inputQubit.getQubitStored() >= qubitCost && inputQubit.changeQubit(-qubitCost) < qubitCost) {
                    failed(sPos, 2);
                    return 0;
                }
            }
            float lossFactor = 1 - Math.max(0, energyLoss) / 100f;
            charged = container.addEnergy((long) (amount * lossFactor));

            ampsUsed += ampsToConsume;
            inputEnergy.removeEnergy((long) (charged * (1 + (1 - lossFactor))));
            success(sPos);
        } else {
            failed(sPos, 3);
        }
        return charged;
    }

    /**
     * error
     * 1: no qubt input
     * 2: not enough qubit
     * 3: container is null or cannot input energy
     * @param pos position in string form
     * @param error an error code for easier debuging
     */
    protected void failed(String pos, int error) {
        if (!failedPositions.containsKey(pos)) {
            failedPositions.put(pos, error);
        }
    }

    protected void success(String pos) {
        failedPositions.remove(pos);
    }

    /**
     * checks if the pos is in range of the multiblock
     *
     * @param pos of the machine
     * @return if pos is in range
     */
    public boolean isInRange(BlockPos pos) {
        return center.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= range;
    }

    public boolean isInDim(int dim) {
        return dim == getWorld().provider.getDimension();
    }

    public boolean canInsertInto(MetaTileEntity mte) {
        return mte instanceof GAMetaTileEntityBatteryBuffer;
    }

    /**
     * @return if the tile has a receiver cover
     */
    public boolean hasCover(ICoverable coverable) {
        for (EnumFacing face : EnumFacing.values()) {
            if (coverable.getCoverAtSide(face) instanceof CoverWirelessReceiver) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return a list of positions that are in range
     */
    public List<BlockPos> createCircularBox() {
        int intRange = (int) range;
        List<BlockPos> posList = new ArrayList<>();
        Iterable<BlockPos> poss = BlockPos.getAllInBox(center.getX() - intRange, center.getY() - intRange, center.getZ() - intRange, center.getX() + intRange, center.getY() + intRange, center.getZ() + intRange);

        poss.forEach((blockPos -> {
            if (isInRange(blockPos))
                posList.add(blockPos);
        }));
        return posList;
    }

    /**
     * scans the list created in {@link #createCircularBox()}
     * @return all MetaTileEntities that have wireless cover
     */
    public List<BlockPosDim> scanRange() {
        failedPositions.clear();
        List<BlockPosDim> blocks = new ArrayList<>();
        for (BlockPos pos : allBlocks) {
            TileEntity tile = getWorld().getTileEntity(pos);
            if (tile instanceof MetaTileEntityHolder) {
                if (((MetaTileEntityHolder) tile).getMetaTileEntity() != null) {
                    if (hasCover(((MetaTileEntityHolder) tile).getMetaTileEntity())) {
                        blocks.add(new BlockPosDim(pos, getWorld().provider.getDimension()));
                    }
                }
            }
        }
        return blocks;
    }

    protected void handleButtonClick(Widget.ClickData clickData) {
        receivers = scanRange();
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = this.createUITemplate(entityPlayer);
        builder.widget(new ClickButtonWidget(125, 103, 40, 18, I18n.format("gtforegoing.multiblock.tesla_tower.scan"), this::handleButtonClick));
        return builder.build(this.getHolder(), entityPlayer);
    }

    public IItemHandlerModifiable getDataInventory() {
        return dataInventory;
    }

    public MTEnergyContainerList getEnergyInput() {
        return inputEnergy;
    }

    public BlockPos getCenter() {
        return center;
    }

    public Map<String, Integer> getFailedPositions() {
        return failedPositions;
    }

    public List<BlockPosDim> getReceivers() {
        return receivers;
    }

    @ZenGetter("coilHeight")
    public int getCoilHeight() {
        return coilHeight;
    }

    @ZenGetter("coilTier")
    public int getCoilTier() {
        return coilTier;
    }

    @ZenGetter("range")
    public double getRange() {
        return range;
    }

    @ZenGetter("inputVoltage")
    public long getInputVoltage() {
        return inputVoltage;
    }

    @ZenGetter("maxVoltage")
    public long getMaxVoltage() {
        return maxVoltage;
    }

    @ZenGetter("baseRange")
    public int getBaseRange() {
        return MTConfig.multis.teslaTower.baseRange;
    }

    @ZenGetter("lossFunction")
    public IEnergyLossFunction getLossFunction() {
        return lossFunction;
    }

    @ZenSetter("lossFunction")
    public void setLossFunction(IEnergyLossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    @ZenGetter("ampsPerTier")
    public int getAmpsPerTier() {
        return MTConfig.multis.teslaTower.ampsPerCoilTier;
    }

    public boolean canTransmittInterdimensional() {
        return MTConfig.multis.teslaTower.allowInterdimensionalTransfer;
    }

    public boolean canTransmittOutOfRange() {
        return MTConfig.multis.teslaTower.allowOutOfRangeTransfer;
    }

    public int getQubitCost() {
        return MTConfig.multis.teslaTower.qubitCost;
    }
}
