package com.brachy84.gtforegoing.machines;

import com.brachy84.gtforegoing.MTConfig;
import com.brachy84.gtforegoing.MechTech;
import com.brachy84.gtforegoing.capability.impl.LimitedItemHandlerContainer;
import com.brachy84.gtforegoing.machines.multiblockpart.LimitedItemBusTile;
import com.brachy84.gtforegoing.machines.multis.MetaTileEntityTeslaTower;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class MTTileEntities {

    private static int id = 6000;

    public static LimitedItemBusTile[] LIMITED_ITEM_BUS = new LimitedItemBusTile[GTValues.V.length];
    public static MetaTileEntityTeslaTower TESLA_TOWER;

    public static void init() {
        for(int i = 0; i < LIMITED_ITEM_BUS.length; i++) {
            LIMITED_ITEM_BUS[i] = new LimitedItemBusTile(MechTech.loc("limited_item_bus.import." + GTValues.VN[i].toLowerCase()), i, true, LimitedItemHandlerContainer.DATA_ITEMS);
            LIMITED_ITEM_BUS[i] = GregTechAPI.registerMetaTileEntity(id+i, LIMITED_ITEM_BUS[i]);
        }
        id += 10;
        if(Loader.isModLoaded("gtadditions")) {
            if(MTConfig.multis.teslaTower.enabled) {
                TESLA_TOWER = GregTechAPI.registerMetaTileEntity(id, new MetaTileEntityTeslaTower(loc("tesla_tower")));
            }
        }
        id++;
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
