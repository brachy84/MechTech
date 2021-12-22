package com.brachy84.mechtech.client;

import com.brachy84.mechtech.MechTech;
import gregtech.api.gui.resources.TextureArea;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = MechTech.MODID, value = Side.CLIENT)
public class ClientHandler {
    public static SimpleOverlayRenderer COVER_WIRELESS_RECEIVER;
    public static TextureArea ERROR_SLOT;

    public static void preInit(){
        COVER_WIRELESS_RECEIVER = new SimpleOverlayRenderer("cover/cover_wireless_overlay");
        ERROR_SLOT = TextureArea.fullImage("");
    }
}
