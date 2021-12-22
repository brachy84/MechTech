package com.brachy84.mechtech;

import com.brachy84.mechtech.comon.CommonProxy;
import com.brachy84.mechtech.comon.cover.MTCoverBehaviors;
import com.brachy84.mechtech.comon.machines.MTTileEntities;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MechTech.MODID, name = MechTech.NAME, version = MechTech.VERSION, dependencies = "required-after:gregtech;")
public class MechTech {
    public static final String MODID = "mechtech";
    public static final String NAME = "MechTech";
    public static final String VERSION = "0.0.3";

    public static final Logger logger = LogManager.getLogger("MechTech");

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    @SidedProxy(modId = MODID, clientSide = "com.brachy84.mechtech.comon.ClientProxy", serverSide = "com.brachy84.mechtech.comon.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preLoad();
        MTTileEntities.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MTCoverBehaviors.init();
    }
}
