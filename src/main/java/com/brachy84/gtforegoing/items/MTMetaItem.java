package com.brachy84.gtforegoing.items;

import com.brachy84.gtforegoing.items.behavior.DataStickBehavior;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;

import static com.brachy84.gtforegoing.items.MTMetaItems.WIRELESS_RECEIVER;

public class MTMetaItem extends StandardMetaItem {

    public MTMetaItem(short metaItemOffset) {
        super(metaItemOffset);
    }

    @Override
    public void registerSubItems() {
        WIRELESS_RECEIVER = addItem(2000, "wireless_receiver");

        MetaItems.TOOL_DATA_STICK.addComponents(new DataStickBehavior());
    }
}
