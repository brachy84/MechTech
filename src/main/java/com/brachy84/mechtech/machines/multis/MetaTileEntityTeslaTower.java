package com.brachy84.mechtech.machines.multis;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.capability.MTCapabilities;
import com.brachy84.mechtech.capability.MTEnergyContainerList;
import com.brachy84.mechtech.cover.CoverWirelessReceiver;
import com.brachy84.mechtech.integration.crafttweaker.CTMath;
import com.brachy84.mechtech.integration.crafttweaker.IEnergyLossFunction;
import com.brachy84.mechtech.utils.BlockPosDim;
import com.brachy84.mechtech.utils.TorusBlock;
import crafttweaker.annotations.ZenRegister;
import gregicadditions.GAMaterials;
import gregicadditions.GAValues;
import gregicadditions.capabilities.GregicAdditionsCapabilities;
import gregicadditions.capabilities.impl.QubitContainerList;
import gregicadditions.item.GAHeatingCoil;
import gregicadditions.item.GAMetaBlocks;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.GregtechTileCapabilities;
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
import gregtech.api.util.GTLog;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenProperty;

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

    private final int torusBlockAmount = 124;

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
     * The maximum voltage the tower can provide
     */
    private long maxVoltage;

    /**
     * The amount of amps per pulse
     */
    private long amps;

    /**
     * Gets reset before each pulse
     */
    private long ampsUsed;

    private static final Material casingMaterial = getCasingMaterial(Materials.Titanium, MTConfig.multis.teslaTower.casingMaterial);

    @ZenProperty
    public static IEnergyLossFunction lossFunction = (tower, distance) -> {
        double a = 0.62, b = 0.067;
        double distanceDeci = distance / tower.range;
        return 1 - (1 / (1 + Math.exp((distanceDeci - a) / b)));
    };

    /**
     * How much qubits should be consumed per pulse per machine out of range
     */
    private final int qubitCost = MTConfig.multis.teslaTower.qubitCost;

    private List<IItemHandlerModifiable> inputInventorys = new ArrayList<>();
    private QubitContainerList inputQubit;
    private IItemHandlerModifiable dataInventory;
    private MTEnergyContainerList inputEnergy;
    private BlockPos center = getPos();
    private float rangeModifier = 0;
    private float energyLossModifier = 0;

    private Map<String, Integer> failedPositions = new HashMap<>();

    //private List<BlockPos> allBlocks = new ArrayList<>();
    private Set<BlockPosDim> receivers = new HashSet<>();
    private List<BlockPosDim> removeLater = new ArrayList<>();
    private boolean shouldCreateBox = true;

    public MetaTileEntityTeslaTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        if (TorusBlock.getTorusBlocks().size() <= 0) {
            TorusBlock.addTorusBlock(Materials.Plastic, 0, 0.0001f, 0.0001f, 0);
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        coilHeight = context.getOrDefault("height", 0);
        maxVoltage = GAValues.V[context.getOrDefault("maxVoltage", 0)];
        coilTier = context.getOrDefault("CoilTier", -1) + 1;
        range = Math.max(1, getBaseRange() * rangeModifier);
        amps = coilTier * 2;

        inputQubit = new QubitContainerList(getAbilities(GregicAdditionsCapabilities.INPUT_QBIT));
        inputInventorys = getAbilities(MultiblockAbility.IMPORT_ITEMS);
        if (inputInventorys.size() > 0) {
            dataInventory = inputInventorys.get(0);
        }
        if (dataInventory == null)
            dataInventory = getImportItems();
        inputEnergy = new MTEnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputVoltage = inputEnergy.getMaxInputVoltage();
        center = getPos().offset(getFrontFacing().getOpposite(), 4).offset(EnumFacing.UP, 1 + coilHeight / 2);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        rangeModifier = 0;
        energyLossModifier = 0;
        shouldCreateBox = true;
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            if (getHolder().getOffsetTimer() % 100 == 0) {
                receivers.clear();
                scanRange();
            }
            if (getHolder().getOffsetTimer() % 20 == 0) {
                if (MTConfig.multis.teslaTower.allowInterdimensionalTransfer || MTConfig.multis.teslaTower.allowOutOfRangeTransfer)
                    scanDataInventory();
            }
            ampsUsed = 0L;
            for (BlockPosDim pos : receivers) {
                if (inputEnergy.getEnergyStored() <= 0)
                    break;
                feedEnergy(pos);
                if (ampsUsed >= amps) {
                    break;
                }
            }
        }
    }

    protected void scanDataInventory() {
        for (IItemHandlerModifiable itemHandler : inputInventorys) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.hasTagCompound()) {
                    NBTTagCompound tag = stack.getSubCompound("BlockPos");
                    if (tag != null) {
                        int dim = tag.getShort("dim");
                        BlockPosDim pos = new BlockPosDim(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), dim);
                        if (!receivers.contains(pos)) {
                            receivers.add(pos);
                        }
                    }
                }
            }
            if (removeLater.size() > 0) {
                for (BlockPosDim pos : removeLater) {
                    receivers.remove(pos);
                }
                removeLater.clear();
            }
        }
    }

    @Override
    protected void checkStructurePattern() {
        rangeModifier = 0;
        energyLossModifier = 0;
        super.checkStructurePattern();
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
                .where('B', statePredicate(getCasingState()).or(abilityPartPredicate(getAllowedAbilities())))
                .where('P', blockPredicate(MetaBlocks.COMPRESSED.get(Materials.Plastic)))
                .where('C', coilPredicate())
                .where('G', torusPredicate())
                .where('S', selfPredicate())
                .where('#', (tile) -> true)
                .build();

    }

    public Predicate<BlockWorldState> torusPredicate() {
        return (blockWorldState -> {
            IBlockState block = blockWorldState.getBlockState();
            TorusBlock torusBlock = TorusBlock.get(block);
            if (torusBlock != null) {
                energyLossModifier += torusBlock.getConductivity() / torusBlockAmount;
                rangeModifier += torusBlock.getRangeModifier() / torusBlockAmount;
                blockWorldState.getMatchContext().set("maxVoltage", Math.min(torusBlock.getMaxVoltageTier(), blockWorldState.getMatchContext().getOrPut("maxVoltage", torusBlock.getMaxVoltageTier())));
                return true;
            }
            return false;
        });
    }

    public Predicate<BlockWorldState> coilPredicate() {
        return (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            int coilTier, currentCoilTier;
            if (blockState.getBlock() instanceof BlockWireCoil) {
                BlockWireCoil coil = (BlockWireCoil) blockState.getBlock();
                coilTier = coil.getState(blockState).ordinal();
                currentCoilTier = blockWorldState.getMatchContext().getOrPut("CoilTier", coilTier);
            } else if (blockState.getBlock() instanceof GAHeatingCoil) {
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
        return GAMetaBlocks.METAL_CASING.get(casingMaterial);
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
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            textList.add(new TextComponentTranslation("mechtech.multiblock.tesla_tower.range", df.format(range)));
            textList.add(new TextComponentTranslation("mechtech.multiblock.tesla_tower.voltage", inputVoltage));
            textList.add(new TextComponentTranslation("mechtech.multiblock.tesla_tower.receivers", receivers.size()));
            textList.add(new TextComponentTranslation("mechtech.multiblock.tesla_tower.amps", amps, ampsUsed));
            //textList.add(new TextComponentTranslation("gtforegoing.multiblock.tesla_tower.loss", energyLoss));
            if (failedPositions.size() > 0) {
                textList.add(new TextComponentTranslation("mechtech.multiblock.tesla_tower.fail"));
                for (Map.Entry<String, Integer> pos : failedPositions.entrySet()) {
                    textList.add(new TextComponentString(" - " + pos.getKey() + " E:" + pos.getValue()));
                }
            }
        }
    }

    protected IBlockState getCasingState() {
        return GAMetaBlocks.getMetalCasingBlockState(casingMaterial);
    }

    protected static Material getCasingMaterial(Material defaultMaterial, String materialString) {
        Material mat = Material.MATERIAL_REGISTRY.getObject(materialString);
        if (mat != null && mat.hasFlag(GAMaterials.GENERATE_METAL_CASING)) {
            return mat;
        }
        return defaultMaterial;
    }

    /**
     * feeds the machine at pos
     *
     * @param pos the position to fill
     * @return the amount of energy fed
     */
    public void feedEnergy(BlockPosDim pos) {
        boolean consumeQubit = false;
        World world = getWorld();

        if (!isInRange(pos)) {
            if (!canTransmittOutOfRange()) {
                return;
            }
            consumeQubit = true;
        }
        if (!isInDim(pos.getDim())) {
            if (!canTransmittInterdimensional()) {
                return;
            }
            consumeQubit = true;
            world = DimensionManager.getWorld(pos.getDim());
        }

        TileEntity tile = world.getTileEntity(pos);
        String sPos = pos.toString();
        if (tile == null) {
            removeLater.add(pos);
            failedPositions.remove(sPos);
            return;
        }
        EnumFacing facing = getCover(tile);
        if (facing == null) {
            removeLater.add(pos);
            failedPositions.remove(sPos);
            return;
        }
        IEnergyContainer container = getEnergyContainer(tile, facing);
        if (container == null) {
            removeLater.add(pos);
            failedPositions.remove(sPos);
            return;
        }

        if (container.inputsEnergy(facing)) {
            if (container.getInputVoltage() > Math.min(inputVoltage, maxVoltage)) {
                failed(sPos, 4);
                return;
            }
            if (container.getEnergyStored() == container.getEnergyCapacity()) {
                success(sPos);
                return;
            }
            if (consumeQubit) {
                if (inputQubit == null) {
                    failed(sPos, 1);
                    return;
                }
                if (inputQubit.getQubitStored() >= getQubitCost() && inputQubit.changeQubit(-getQubitCost()) < getQubitCost()) {
                    failed(sPos, 2);
                    return;
                }
            }
            long v = (long) (inputEnergy.getInputVoltage() * lossFunction.run(this, pos.getDistance(getPos())) * energyLossModifier + 0.5);
            long amperage = amps - ampsUsed;
            int m = 1;
            if(v > container.getInputVoltage()) {
                m = (int) Math.ceil(v / (container.getInputVoltage() + 0.0));
                v /= m;
                amperage *= m;
            }
            long a = container.acceptEnergyFromNetwork(facing, v, amperage);
            inputEnergy.removeEnergy(v * a);
            ampsUsed += a / m;

            success(sPos);
        } else {
            failed(sPos, 3);
        }
    }

    public boolean canInsertEnergy(TileEntity tile) {
        if (tile == null) return false;
        EnumFacing facing = getCover(tile);
        return facing != null && tile.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
    }

    public IEnergyContainer getEnergyContainer(TileEntity tile, EnumFacing side) {
        if (tile == null) return null;
        return tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
    }

    /**
     * error
     * 1: no qubt input
     * 2: not enough qubit
     * 3: container is null or cannot input energy
     *
     * @param pos   position in string form
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


    /**
     * @return if the tile has a receiver cover
     */
    public EnumFacing getCover(TileEntity tile) {
        if (tile == null) return null;
        ICoverable coverable = null;
        for (EnumFacing face : EnumFacing.values()) {
            coverable = tile.getCapability(GregtechTileCapabilities.CAPABILITY_COVERABLE, face);
            if (coverable != null)
                break;
        }
        if (coverable == null) return null;
        for (EnumFacing face : EnumFacing.values()) {
            if (coverable.getCoverAtSide(face) instanceof CoverWirelessReceiver) {
                return face;
            }
        }
        return null;
    }

    public void scanRange() {
        int intRange = CTMath.clamp((int) range, 1, 256);
        int d = intRange * 2 + 1;
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        pos.setPos(center.getX() - intRange, center.getY() - intRange, center.getZ() - intRange);
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                for (int k = 0; k < d; k++) {
                    if (isInRange(pos) && canInsertEnergy(getWorld().getTileEntity(pos))) {
                        receivers.add(new BlockPosDim(pos, getWorld().provider.getDimension()));
                    }
                    pos.move(EnumFacing.EAST);
                }
                pos.move(EnumFacing.WEST, d);
                pos.move(EnumFacing.SOUTH);
            }
            pos.move(EnumFacing.NORTH, d);
            pos.move(EnumFacing.UP);
        }
        pos.release();
    }

    protected void handleButtonClick(Widget.ClickData clickData) {
        /*if(shouldCreateBox) {
            createCircularBox();
            shouldCreateBox = false;
        }*/
        scanRange();
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = this.createUITemplate(entityPlayer);
        // scans automatically every second making this redundant
        // I will keep this here just in case
        //builder.widget(new ClickButtonWidget(125, 103, 40, 18, I18n.format("mechtech.multiblock.tesla_tower.scan"), this::handleButtonClick));
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

    public Set<BlockPosDim> getReceivers() {
        return receivers;
    }

    protected MultiblockAbility<?>[] getAllowedAbilities() {
        return ALLOWED_ABILITIES;
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

    @Override
    public long getOffsetTimer() {
        return getHolder().getOffsetTimer();
    }
}
