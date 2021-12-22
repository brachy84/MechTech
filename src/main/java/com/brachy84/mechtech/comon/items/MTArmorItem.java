package com.brachy84.mechtech.comon.items;

import com.brachy84.mechtech.api.armor.ModularArmor;
import gregtech.api.items.armor.ArmorMetaItem;
import net.minecraft.inventory.EntityEquipmentSlot;

import static com.brachy84.mechtech.comon.items.MTMetaItems.*;

public class MTArmorItem extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {

    @Override
    public void registerSubItems() {
        MODULAR_HELMET = addItem(100, "modular_helmet")
                .setArmorLogic(new ModularArmor(EntityEquipmentSlot.HEAD, 3, 3, 1000));
        MODULAR_CHESTPLATE = addItem(101, "modular_chestplate").setArmorLogic(new ModularArmor(EntityEquipmentSlot.CHEST, 3, 3, 1000));
        MODULAR_LEGGINGS = addItem(102, "modular_leggings").setArmorLogic(new ModularArmor(EntityEquipmentSlot.LEGS, 3, 3, 1000));
        MODULAR_BOOTS = addItem(103, "modular_boots").setArmorLogic(new ModularArmor(EntityEquipmentSlot.FEET, 3, 3, 1000));
    }
}
