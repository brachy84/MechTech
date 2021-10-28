package com.brachy84.mechtech.items;

import com.brachy84.mechtech.armor.ModularArmor;
import com.brachy84.mechtech.client.ModuleSlot;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.MetaItem;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MTMetaItems {

    public static MetaItem<?>.MetaValueItem WIRELESS_RECEIVER;
    public static MetaItem<?>.MetaValueItem WIRELESS_BINDER;
    public static MetaItem<?>.MetaValueItem NIGHTVISION_MODULE;

    public static ArmorMetaItem<?>.ArmorMetaValueItem MODULAR_HELMET;
    public static ArmorMetaItem<?>.ArmorMetaValueItem MODULAR_CHESTPLATE;
    public static ArmorMetaItem<?>.ArmorMetaValueItem MODULAR_LEGGINGS;
    public static ArmorMetaItem<?>.ArmorMetaValueItem MODULAR_BOOTS;

    public static void init() {
        MTMetaItem item = new MTMetaItem();
        item.setRegistryName("mt_meta_item");

        ArmorMetaItem<?> metaItem = null;
        for(MetaItem<?> mi : MetaItem.getMetaItems()) {
            if(mi instanceof ArmorMetaItem) {
                metaItem = (ArmorMetaItem<?>) mi;
                break;
            }
        }
        if(metaItem != null) {
            /*ModularArmor helmet = new ModularArmor(EntityEquipmentSlot.HEAD, 3, 3, 1000);
            helmet.setUiBuilder((handler, widgetGroup) -> {
                widgetGroup.addWidget(new ModuleSlot(handler, 1, 2, 2, module -> module.canPlaceIn(EntityEquipmentSlot.HEAD, MODULAR_HELMET.getStackForm())));
            });*/

            MODULAR_HELMET = metaItem.addItem(100, "modular_helmet")
                    .setArmorLogic(new ModularArmor(EntityEquipmentSlot.HEAD, 3, 3, 1000)
                        .setUiBuilder((handler, widgetGroup) -> {
                            widgetGroup.addWidget(new ModuleSlot(handler, 1, 2, 2, module -> module.canPlaceIn(EntityEquipmentSlot.HEAD, MODULAR_HELMET.getStackForm())));
                        }));
            MODULAR_CHESTPLATE = metaItem.addItem(101, "modular_chestplate").setArmorLogic(new ModularArmor(EntityEquipmentSlot.CHEST, 3, 3, 1000)
                    .setUiBuilder((handler, widgetGroup) -> {
                        widgetGroup.addWidget(new ModuleSlot(handler, 1, 2, 2, module -> module.canPlaceIn(EntityEquipmentSlot.CHEST, MODULAR_CHESTPLATE.getStackForm())));
                    }));
            MODULAR_LEGGINGS = metaItem.addItem(102, "modular_leggings").setArmorLogic(new ModularArmor(EntityEquipmentSlot.LEGS, 3, 3, 1000)
                    .setUiBuilder((handler, widgetGroup) -> {
                        widgetGroup.addWidget(new ModuleSlot(handler, 1, 2, 2, module -> module.canPlaceIn(EntityEquipmentSlot.LEGS, MODULAR_LEGGINGS.getStackForm())));
                    }));
            MODULAR_BOOTS = metaItem.addItem(103, "modular_boots").setArmorLogic(new ModularArmor(EntityEquipmentSlot.FEET, 3, 3, 1000)
                    .setUiBuilder((handler, widgetGroup) -> {
                        widgetGroup.addWidget(new ModuleSlot(handler, 1, 2, 2, module -> module.canPlaceIn(EntityEquipmentSlot.FEET, MODULAR_BOOTS.getStackForm())));
                    }));
        }
    }
}
