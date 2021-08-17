package com.brachy84.mechtech.machines;

import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.machines.multis.MetaTileEntityTeslaTower;
import com.brachy84.mechtech.machines.multis.MetaTileEntityTokamak;
import gregicadditions.Gregicality;
import gregicadditions.machines.GATileEntities;
import gregtech.api.GregTechAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 * Claimed range 15000 - 15100
 */
public class MTTileEntities {

    public static MetaTileEntityTeslaTower TESLA_TOWER;

    public static void init() {
        if(Loader.isModLoaded("gtadditions")) {
            if(MTConfig.multis.teslaTower.enabled) {
                TESLA_TOWER = GregTechAPI.registerMetaTileEntity(15000, new MetaTileEntityTeslaTower(loc("tesla_tower")));
            }
        }
        if(MTConfig.multis.tokamak.enableTokamak) {
            GATileEntities.ADVANCED_FUSION_REACTOR = GregTechAPI.registerMetaTileEntity(15001, new MetaTileEntityTokamak(new ResourceLocation(Gregicality.MODID, "advanced_fusion_reactor")));
        }
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
