package com.brachy84.mechtech.common.machines;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.common.machines.multis.MetaTileEntityTeslaTower;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.integration.jei.multiblock.MultiblockInfoCategory;
import net.minecraft.util.ResourceLocation;

/**
 * Claimed range 10100-10499
 */
public class MTTileEntities {

    public static MetaTileEntityTeslaTower TESLA_TOWER;
    public static MetaTileEntityArmorWorkbench ARMOR_WORKBENCH;
    public static EnergySink ENERGY_SINK;

    public static void init() {
        ARMOR_WORKBENCH = register(10100, new MetaTileEntityArmorWorkbench(loc("armor_workbench")));
        TESLA_TOWER = register(10101, new MetaTileEntityTeslaTower(loc("tesla_tower")));
        ENERGY_SINK = register(10490, new EnergySink(loc("energy_sink")));
    }

    private static <T extends MetaTileEntity> T register(int id, T t) {
        if (t instanceof MultiblockControllerBase && GTValues.isModLoaded(GTValues.MODID_JEI)) {
            MultiblockInfoCategory.registerMultiblock((MultiblockControllerBase) t);
        }
        GregTechAPI.MTE_REGISTRY.register(id, t.metaTileEntityId, t);
        return t;
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
