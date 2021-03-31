package com.brachy84.mechtech.machines;

import com.brachy84.mechtech.capability.impl.EnergyRelayItemHandler;
import com.brachy84.mechtech.client.ClientHandler;
import com.brachy84.mechtech.utils.BlockPosDim;
import gregicadditions.GAValues;
import gregicadditions.capabilities.IQubitContainer;
import gregicadditions.capabilities.impl.QubitContainerHandler;
import gregicadditions.machines.overrides.GAMetaTileEntityBatteryBuffer;
import gregicadditions.machines.overrides.GATieredMetaTileEntity;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTUtility;
import gregtech.common.metatileentities.electric.MetaTileEntityBatteryBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;
import stanhebben.zenscript.annotations.ZenProperty;

public class MetaTileEntityEnergyRelay extends GATieredMetaTileEntity {

    private final int slots;

    private final int range;

    private final int amps;

    private int ampsUsed;

    private final float energyLossBase;

    private final boolean canChargeOutOfRange;

    private final IQubitContainer qubitContainer;

    String errorMsg = "";

    @ZenProperty
    public static IEnergyRelayLoss lossFunction = (voltage, range, distance) -> {
        double a = 0.62, b = 0.067;
        double distanceDeci = distance / range;
        return 1 - (1 / (1 + Math.exp((distanceDeci-a)/b)));
    };

    public MetaTileEntityEnergyRelay(ResourceLocation metaTileEntityId, int tier, int slots, int range, int amps, float energyLossDecimal, boolean canChargeOutOfRange) {
        super(metaTileEntityId, tier);
        this.slots = slots;
        this.range = range;
        this.amps = amps;
        this.energyLossBase = energyLossDecimal;
        this.canChargeOutOfRange = canChargeOutOfRange;
        itemInventory = new EnergyRelayItemHandler(slots);
        qubitContainer = new QubitContainerHandler(this, 16, 1, 2, 0, 0);
    }

    @Override
    protected void reinitializeEnergyContainer() {
        long tierVoltage = GAValues.V[getTier()];
        this.energyContainer = new EnergyContainerHandler(this, tierVoltage * 32L, tierVoltage, 1, 0L, 0L);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityEnergyRelay(metaTileEntityId, getTier(), slots, range, amps, energyLossBase, canChargeOutOfRange);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.extendedBuilder();
        builder.bindPlayerInventory(entityPlayer.inventory, 132);
        int r, c, x = 5, y = 5;

        for(int i = 0; i < slots; i++) {
            r = (int) Math.floor(i / 8);
            c = i % 8;
            // TODO make my own slot widget
            SlotWidget slot = new SlotWidget((IItemHandlerModifiable) itemInventory, i, x + c * 18, y + r * 18).setBackgroundTexture(GuiTextures.SLOT);
            if(getDataInventory().hasError(i)) {
                slot.setBackgroundTexture(GuiTextures.SLOT, ClientHandler.ERROR_SLOT);
            }
            builder.widget(slot);
        }

        builder.label(5, 120, errorMsg);

        return builder.build(getHolder(), entityPlayer);
    }

    @Override
    public void update() {
        super.update();
        ampsUsed = 0;
        for(int i = 0; i < slots; i++) {
            ItemStack stack = getDataInventory().getStackInSlot(i);
            if(stack.isEmpty()) {
                getDataInventory().resolvedError(i);
                continue;
            }
            NBTTagCompound tag = stack.getSubCompound("BlockPos");
            if(tag != null) {
                tryEmittEnergy(new BlockPosDim(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), tag.getShort("dim")), i);
            } else {
                getDataInventory().errorSlot(i);
            }
        }
    }

    public long tryEmittEnergy(BlockPosDim pos, int invSlot) {
        World world = getWorld();
        boolean consumeQubit = false;
        if(!isInDimension(pos) || !isInRange(pos)) {
            if(canChargeOutOfRange) {
                consumeQubit = true;
                world = DimensionManager.getWorld(pos.getDim());
            } else {
                getDataInventory().errorSlot(invSlot);
                errorMsg = "Out of Range";
                return 0L;
            }
        }

        TileEntity tile = world.getTileEntity(pos);

        if(canInsertEnergy(tile)) {
            IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
            if(container != null && container.inputsEnergy(EnumFacing.NORTH)) {
                if(container.getInputVoltage() > energyContainer.getInputVoltage()) {
                    getDataInventory().errorSlot(invSlot);

                    //TODO why?
                    errorMsg = "Target Voltage to high: " + container.getInputVoltage() + " / " + energyContainer.getInputVoltage();
                    return 0L;
                }

                if(consumeQubit && qubitContainer.removeQubit(1) != 1) {
                    getDataInventory().errorSlot(invSlot);
                    errorMsg = "Not enough qubits";
                    return 0L;
                }

                double energyLoss = lossFunction.get(energyContainer.getInputVoltage(), range, pos.getDistance(getPos())) + energyLossBase;

                ampsUsed += consumeQubit ? amps : (int) container.getInputAmperage();
                long energyToEmitt = energyContainer.getInputVoltage() * container.getInputAmperage();
                if(energyContainer.getEnergyStored() > energyToEmitt * (1+ energyLoss)) {
                    energyToEmitt = (long) (energyContainer.getEnergyStored() * (1- energyLoss));
                }
                long charged = container.addEnergy(energyToEmitt);
                energyContainer.removeEnergy((long) (energyToEmitt * (1+ energyLoss)));
                getDataInventory().resolvedError(invSlot);
                errorMsg = "";
                return charged;
            }
        }
        errorMsg = "Is not correct tile: " + tile.getClass().getName();
        if(tile instanceof MetaTileEntityHolder) {
            errorMsg = "Is not correct tile: " + ((MetaTileEntityHolder) tile).getMetaTileEntity().getClass().getName();
        }
        getDataInventory().errorSlot(invSlot);
        return 0L;
    }

    public boolean isInDimension(BlockPosDim pos) {
        return pos.getDim() == getWorld().provider.getDimension();
    }

    public boolean isInRange(BlockPosDim pos) {
        return pos.getDistance(getPos().getX(), getPos().getY(), getPos().getZ()) <= range;
    }

    public boolean canInsertEnergy(TileEntity tile) {
        if(tile instanceof MetaTileEntityHolder) {
            MetaTileEntity mte = ((MetaTileEntityHolder) tile).getMetaTileEntity();
            return mte instanceof GAMetaTileEntityBatteryBuffer || mte instanceof MetaTileEntityBatteryBuffer;
        }
        return false;
    }

    public EnergyRelayItemHandler getDataInventory() {
        return (EnergyRelayItemHandler) itemInventory;
    }
/*
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if(capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER) {
            return GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER.cast(energyContainer);
        } else if (capability == GregicAdditionsCapabilities.QBIT_CAPABILITY){
            return GregicAdditionsCapabilities.QBIT_CAPABILITY.cast(qubitContainer);
        } else {
            return super.getCapability(capability, side);
        }
    }*/

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        GTUtility.writeItems(this.itemInventory, "ItemInventory", data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        GTUtility.readItems((IItemHandlerModifiable) itemInventory, "ItemInventory", data);
        super.readFromNBT(data);
    }
}
