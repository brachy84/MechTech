package com.brachy84.gtforegoing.client;

import com.brachy84.gtforegoing.MechTech;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = MechTech.MODID, value = Side.CLIENT)
public class ClientHandler {
    public static SimpleOverlayRenderer COVER_WIRELESS_RECEIVER = new SimpleOverlayRenderer("cover/cover_wireless_overlay");
    public static TextureArea ERROR_SLOT = TextureArea.fullImage("");
}
