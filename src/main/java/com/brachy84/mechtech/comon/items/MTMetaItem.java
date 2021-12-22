package com.brachy84.mechtech.comon.items;

import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.comon.items.behavior.DataStickBehavior;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;

import static com.brachy84.mechtech.comon.items.MTMetaItems.*;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem() {
        super((short) 0);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(0, "wireless_receiver");
        //NIGHTVISION_MODULE = addItem(100, "module.night_vision").addComponents(ModularArmor.Modules.nightVision);
        //WIRELESS_BINDER = addItem(2001, "wireless_binder");

        MetaItems.NIGHTVISION_GOGGLES.addComponents(ModularArmor.Modules.nightVision);
        MetaItems.COVER_SOLAR_PANEL_LV.addComponents(ModularArmor.Modules.solarGen1);
        MetaItems.COVER_SOLAR_PANEL_MV.addComponents(ModularArmor.Modules.solarGen2);
        MetaItems.COVER_SOLAR_PANEL_HV.addComponents(ModularArmor.Modules.solarGen3);
        //MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }
}
