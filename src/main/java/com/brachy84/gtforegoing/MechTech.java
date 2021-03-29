package com.brachy84.gtforegoing;

import com.brachy84.gtforegoing.cover.MTCoverBehaviors;
import com.brachy84.gtforegoing.items.MTMetaItems;
import com.brachy84.gtforegoing.machines.MTTileEntities;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = MechTech.MODID, name = MechTech.NAME, version = MechTech.VERSION, dependencies = "required-after:gregtech;after:gtadditions")
public class MechTech {
    public static final String MODID = "mechtech";
    public static final String NAME = "MechTech";
    public static final String VERSION = "0.0.1";

    private static Logger logger;

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MTMetaItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        MTTileEntities.init();
        MTCoverBehaviors.init();
    }
}
