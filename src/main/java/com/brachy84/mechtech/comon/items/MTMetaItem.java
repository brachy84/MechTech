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

        MetaItems.NIGHTVISION_GOGGLES.addComponents(ModularArmor.Modules.nightVision).addComponents();
        //MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }
}
