package com.brachy84.mechtech.machines;

import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.MechTech;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 * Claimed range 15000 - 15100
 */
public class MTTileEntities {

    //public static MetaTileEntityTeslaTower TESLA_TOWER;

    public static void init() {
        if (Loader.isModLoaded("gtadditions")) {
            if (MTConfig.multis.teslaTower.enabled) {
                //TESLA_TOWER = GregTechAPI.registerMetaTileEntity(15000, new MetaTileEntityTeslaTower(loc("tesla_tower")));
            }
        }
        if (MTConfig.multis.tokamak.enableTokamak) {
            //GATileEntities.ADVANCED_FUSION_REACTOR = register(15001, new MetaTileEntityTokamak(new ResourceLocation(Gregicality.MODID, "advanced_fusion_reactor")));
        }
    }

    private static <T extends MetaTileEntity> T register(int id, T t) {
        GregTechAPI.MTE_REGISTRY.register(id, t.metaTileEntityId, t);
        return t;
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
