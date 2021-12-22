package com.brachy84.mechtech.client;

import com.brachy84.mechtech.api.armor.IArmorModule;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class ModuleSlot extends SlotWidget {

    private Predicate<IArmorModule> armorModulePredicate;

    public ModuleSlot(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public ModuleSlot setArmorModulePredicate(Predicate<IArmorModule> armorModulePredicate) {
        this.armorModulePredicate = armorModulePredicate;
        return this;
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
