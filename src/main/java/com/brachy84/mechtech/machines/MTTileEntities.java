package com.brachy84.mechtech.machines;

import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.machines.multiblockpart.LimitedItemBusTile;
import com.brachy84.mechtech.machines.multis.MetaTileEntityTeslaTower;
import com.brachy84.mechtech.machines.multis.MetaTileEntityTokamak;
import gregicadditions.Gregicality;
import gregicadditions.machines.GATileEntities;
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
            //LIMITED_ITEM_BUS[i] = new LimitedItemBusTile(MechTech.loc("limited_item_bus.import." + GTValues.VN[i].toLowerCase()), i, true, LimitedItemHandlerContainer.DATA_ITEMS);
            //LIMITED_ITEM_BUS[i] = GregTechAPI.registerMetaTileEntity(id+i, LIMITED_ITEM_BUS[i]);
        }
        id = 6010;
        if(Loader.isModLoaded("gtadditions")) {
            if(MTConfig.multis.teslaTower.enabled) {
                TESLA_TOWER = GregTechAPI.registerMetaTileEntity(id, new MetaTileEntityTeslaTower(loc("tesla_tower")));
            }
        }
        id = 6011;
        if(MTConfig.multis.tokamak.enableTokamak) {
            GATileEntities.ADVANCED_FUSION_REACTOR = GregTechAPI.registerMetaTileEntity(id, new MetaTileEntityTokamak(new ResourceLocation(Gregicality.MODID, "advanced_fusion_reactor")));
        }
        id = 6012;
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
