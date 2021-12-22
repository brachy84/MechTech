package com.brachy84.mechtech.comon.machines;

import com.brachy84.mechtech.comon.MTConfig;
import com.brachy84.mechtech.MechTech;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 * Claimed range 10100-10499
 */
public class MTTileEntities {

    //public static MetaTileEntityTeslaTower TESLA_TOWER;
    public static MetaTileEntityArmorWorkbench ARMOR_WORKBENCH;

    public static void init() {
        if (Loader.isModLoaded("gtadditions")) {
            if (MTConfig.multis.teslaTower.enabled) {
                //TESLA_TOWER = GregTechAPI.registerMetaTileEntity(15000, new MetaTileEntityTeslaTower(loc("tesla_tower")));
            }
        }
        if (MTConfig.multis.tokamak.enableTokamak) {
            //GATileEntities.ADVANCED_FUSION_REACTOR = register(15001, new MetaTileEntityTokamak(new ResourceLocation(Gregicality.MODID, "advanced_fusion_reactor")));
        }
        ARMOR_WORKBENCH = register(10100, new MetaTileEntityArmorWorkbench(loc("armor_workbench")));
    }

    private static <T extends MetaTileEntity> T register(int id, T t) {
        GregTechAPI.MTE_REGISTRY.register(id, t.metaTileEntityId, t);
        return t;
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
