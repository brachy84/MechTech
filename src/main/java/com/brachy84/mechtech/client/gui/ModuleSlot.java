package com.brachy84.mechtech.client.gui;

import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class ModuleSlot extends SlotWidget {

    private Predicate<ItemStack> armorModulePredicate;

    public ModuleSlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public ModuleSlot setPredicate(Predicate<ItemStack> armorModulePredicate) {
        this.armorModulePredicate = armorModulePredicate;
        return this;
    }

    @Override
    public boolean canPutStack(ItemStack stack) {
        if(!isEnabled())
            return false;
        return armorModulePredicate.test(stack);
    }
}
