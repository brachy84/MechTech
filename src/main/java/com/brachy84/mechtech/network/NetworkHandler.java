package com.brachy84.mechtech.network;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.network.packets.SModuleParticles;
import gregtech.api.GTValues;
import gregtech.api.net.IPacket;
import gregtech.api.net.NetworkUtils;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static gregtech.api.net.PacketHandler.*;

public class NetworkHandler {
    public static FMLEventChannel channel;

    private NetworkHandler() {
    }

    // Register your packets here
    public static void init() {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MechTech.MODID);
        channel.register(new NetworkHandler());

        registerPacket(SModuleParticles.class);

        initServer();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            initClient();
        }
    }

    // Register packets as "received on server" here
    protected static void initServer() {
    }

    // Register packets as "received on client" here
    @SideOnly(Side.CLIENT)
    protected static void initClient() {
        registerClientExecutor(SModuleParticles.class);
    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) throws Exception {
        IPacket packet = NetworkUtils.proxy2packet(event.getPacket());
        if (hasClientExecutor(packet.getClass())) {
            NetHandlerPlayClient handler = (NetHandlerPlayClient) event.getHandler();
            IThreadListener threadListener = FMLCommonHandler.instance().getWorldThread(handler);
            if (threadListener.isCallingFromMinecraftThread()) {
                packet.executeClient(handler);
            } else {
                threadListener.addScheduledTask(() -> packet.executeClient(handler));
            }
        }
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) throws Exception {
        IPacket packet = NetworkUtils.proxy2packet(event.getPacket());
        if (hasServerExecutor(packet.getClass())) {
            NetHandlerPlayServer handler = (NetHandlerPlayServer) event.getHandler();
            IThreadListener threadListener = FMLCommonHandler.instance().getWorldThread(handler);
            if (threadListener.isCallingFromMinecraftThread()) {
                packet.executeServer(handler);
            } else {
                threadListener.addScheduledTask(() -> packet.executeServer(handler));
            }
        }
    }
}
