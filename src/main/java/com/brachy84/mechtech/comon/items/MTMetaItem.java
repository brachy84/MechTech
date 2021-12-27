package com.brachy84.mechtech.comon.items;

import com.brachy84.mechtech.api.armor.MaterialArmorModuleBuilder;
import com.brachy84.mechtech.api.armor.Modules;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.common.items.MetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.brachy84.mechtech.comon.items.MTMetaItems.*;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem() {
        super((short) 0);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(0, "wireless_receiver");
        SHOCK_ABSORBER = addItem(1, "shock_absorber")
                .addComponents(Modules.SHOCK_ABSORBER, canBeUsedinMATooltip());
        //WIRELESS_BINDER = addItem(2001, "wireless_binder");

        MetaItems.NIGHTVISION_GOGGLES.addComponents(Modules.nightVision);
        MetaItems.COVER_SOLAR_PANEL_LV.addComponents(Modules.solarGen1);
        MetaItems.COVER_SOLAR_PANEL_MV.addComponents(Modules.solarGen2);
        MetaItems.COVER_SOLAR_PANEL_HV.addComponents(Modules.solarGen3);
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

    private IItemBehaviour canBeUsedinMATooltip(String... additionalLines) {
        return new IItemBehaviour() {
            @Override
            public void addInformation(ItemStack itemStack, List<String> lines) {
                lines.addAll(Arrays.asList(additionalLines));
                lines.add(I18n.format("mechtech.modular_armor.usable"));
            }
        };
    }

    @Override
    protected String formatModelPath(MetaItem<?>.MetaValueItem metaValueItem) {
        if (metaValueItem.unlocalizedName.startsWith("armor_plating_")) {
            return "metaitems/armor_plating";
        }
        return super.formatModelPath(metaValueItem);
    }
}
