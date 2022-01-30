package com.brachy84.mechtech.client.gui;

import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class BatterySlot extends SlotWidget {

    private Predicate<ItemStack> filter;

    public BatterySlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public BatterySlot setFilter(Predicate<ItemStack> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public boolean canPutStack(ItemStack stack) {
        return super.canPutStack(stack) && filter.test(stack);
    }
}
