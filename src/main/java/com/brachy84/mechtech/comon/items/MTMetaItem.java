package com.brachy84.mechtech.comon.items;

import com.brachy84.mechtech.api.armor.MaterialArmorModuleBuilder;
import com.brachy84.mechtech.api.armor.Modules;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;

import java.util.Map;

import static com.brachy84.mechtech.comon.items.MTMetaItems.*;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem() {
        super((short) 0);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(0, "wireless_receiver");
        // Modules
        SHOCK_ABSORBER = addItem(1, "shock_absorber").addComponents(Modules.SHOCK_ABSORBER);
        THICK_ISOLATOR = addItem(2, "thick_insulator").addComponents(Modules.INSULATOR);
        BINOCULARS = addItem(3, "binoculars").addComponents(Modules.BINOCULARS);
        AUTO_FEEDER = addItem(4, "auto_feeder").addComponents(Modules.AUTO_FEEDER);
        TESLA_COIL = addItem(5, "tesla_coil").addComponents(Modules.TESLA_COIL);

        // Add modules to existing items
        MetaItems.NIGHTVISION_GOGGLES.addComponents(Modules.NIGHT_VISION);
        MetaItems.COVER_SOLAR_PANEL_LV.addComponents(Modules.SOLAR_GEN_I);
        MetaItems.COVER_SOLAR_PANEL_MV.addComponents(Modules.SOLAR_GEN_II);
        MetaItems.COVER_SOLAR_PANEL_HV.addComponents(Modules.SOLAR_GEN_III);
        MetaItems.ELECTRIC_JETPACK.addComponents(Modules.JETPACK);
        MetaItems.ELECTRIC_JETPACK_ADVANCED.addComponents(Modules.ADVANCED_JETPACK);

        // Armor Platings
        for (Map.Entry<Integer, MaterialArmorModuleBuilder> entry : Modules.getArmorModules().entrySet()) {
            MaterialArmorModuleBuilder builder = entry.getValue();
            if (!builder.isRegistered())
                continue;
            MetaItem<?>.MetaValueItem metaValueItem = addItem(entry.getKey(), "armor_plating_" + builder.material.toString())
                    .addComponents(Modules.getModule(entry.getKey()));
            MATERIAL_ARMOR_PLATINGS.put(builder.material, metaValueItem);
        }
        //MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }

    @Override
    protected String formatModelPath(MetaItem<?>.MetaValueItem metaValueItem) {
        if (metaValueItem.unlocalizedName.startsWith("armor_plating_")) {
            return "metaitems/armor_plating";
        }
        return super.formatModelPath(metaValueItem);
    }
}
