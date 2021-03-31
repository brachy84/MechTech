package com.brachy84.mechtech.items;

import com.brachy84.mechtech.items.behavior.DataStickBehavior;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;

import static com.brachy84.mechtech.items.MTMetaItems.WIRELESS_BINDER;
import static com.brachy84.mechtech.items.MTMetaItems.WIRELESS_RECEIVER;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem() {
        super((short) 0);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(2000, "wireless_receiver");
        //WIRELESS_BINDER = addItem(2001, "wireless_binder");

        MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }
}
