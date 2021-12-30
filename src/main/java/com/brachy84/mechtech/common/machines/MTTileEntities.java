package com.brachy84.mechtech.common.machines;

import com.brachy84.mechtech.MechTech;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Claimed range 10100-10499
 */
public class MTTileEntities {

    //public static MetaTileEntityTeslaTower TESLA_TOWER;
    public static MetaTileEntityArmorWorkbench ARMOR_WORKBENCH;

    public static void init() {
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
