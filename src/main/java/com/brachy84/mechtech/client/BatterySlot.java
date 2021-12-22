package com.brachy84.mechtech.client;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class BatterySlot extends SlotWidget {

    public BatterySlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    @Override
    public boolean canPutStack(ItemStack stack) {
        if(!super.canPutStack(stack))
            return false;
        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        return electricItem != null && electricItem.chargeable() && electricItem.canProvideChargeExternally() && electricItem.getMaxCharge() > 0 && electricItem.getTransferLimit() > 0;
    }
}
