package com.brachy84.gtforegoing.cover;

import com.brachy84.gtforegoing.MechTech;
import com.brachy84.gtforegoing.items.MTMetaItems;
import gregtech.api.cover.CoverBehavior;
import gregtech.api.cover.CoverDefinition;
import gregtech.api.cover.ICoverable;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.common.items.behaviors.CoverPlaceBehavior;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

public class MTCoverBehaviors {

    public static void init() {
        registerBehavior(200, new ResourceLocation(MechTech.MODID, "wireless_receiver"), MTMetaItems.WIRELESS_RECEIVER, CoverWirelessReceiver::new);
    }

    public static void registerBehavior(int coverNetworkId, ResourceLocation coverId, MetaItem<?>.MetaValueItem placerItem, BiFunction<ICoverable, EnumFacing, CoverBehavior> behaviorCreator) {
        CoverDefinition coverDefinition = new CoverDefinition(coverId, behaviorCreator, placerItem.getStackForm());
        CoverDefinition.registerCover(coverNetworkId, coverDefinition);
        placerItem.addComponents(new CoverPlaceBehavior(coverDefinition));
    }
}
