package com.brachy84.mechtech.capability;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MTCapabilities {

    @CapabilityInject(ILimitedItemHandler.class)
    public static Capability<ILimitedItemHandler> LIMITED_ITEM_INPUT_CAPABILITY;

    public static MultiblockAbility<ILimitedItemHandler> LIMITED_ITEM_INPUT = new MultiblockAbility<>();
}
