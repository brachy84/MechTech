package com.brachy84.mechtech.items;

import gregtech.api.items.metaitem.MetaItem;

public class MTMetaItems {

    public static MetaItem<?>.MetaValueItem WIRELESS_RECEIVER;
    public static MetaItem<?>.MetaValueItem WIRELESS_BINDER;

    public static void init() {
        MTMetaItem item = new MTMetaItem();
        item.setRegistryName("mt_meta_item");
    }
}
