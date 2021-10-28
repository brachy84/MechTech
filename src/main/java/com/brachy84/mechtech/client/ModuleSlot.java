package com.brachy84.mechtech.client;

import com.brachy84.mechtech.armor.IArmorModule;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class ModuleSlot extends SlotWidget {

    private final Predicate<IArmorModule> armorModulePredicate;

    public ModuleSlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition, Predicate<IArmorModule> armorModulePredicate) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
        this.armorModulePredicate = armorModulePredicate;
    }

    @Override
    public boolean canPutStack(ItemStack stack) {
        if(!isEnabled())
            return false;
        IArmorModule module = IArmorModule.getOf(stack);
        if(module == null)
            return false;
        return armorModulePredicate.test(module);
    }
}
