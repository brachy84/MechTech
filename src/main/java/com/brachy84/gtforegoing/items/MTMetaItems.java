package com.brachy84.gtforegoing.items;

import gregtech.api.items.metaitem.MetaItem;

public class MTMetaItems {

    public static MetaItem<?>.MetaValueItem WIRELESS_RECEIVER;

    public static void init() {
        MTMetaItem item = new MTMetaItem((short) 0);
        item.setRegistryName("gf_metaitem");
    }
}
