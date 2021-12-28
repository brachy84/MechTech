package com.brachy84.mechtech.network.packets;

import com.brachy84.mechtech.client.render.Lightning;
import gregtech.api.net.IPacket;
import gregtech.api.util.GTLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class SModuleParticles implements IPacket {

    private Vec3d source;
    private Vec3d target;

    public SModuleParticles() {
    }

    public SModuleParticles(final Vec3d source, final Vec3d target) {
        this.source = source;
        this.target = target;
    }

    public Vec3d getSource() {
        return source;
    }

    public Vec3d getTarget() {
        return target;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeDouble(source.x);
        buf.writeDouble(source.y);
        buf.writeDouble(source.z);
        buf.writeDouble(target.x);
        buf.writeDouble(target.y);
        buf.writeDouble(target.z);

    }

    @Override
    public void decode(PacketBuffer buf) {
        source = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        target = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void executeClient(NetHandlerPlayClient handler) {
        Lightning lightning = new Lightning(Minecraft.getMinecraft().world, source, target);
        Minecraft.getMinecraft().effectRenderer.addEffect(lightning);
        GTLog.logger.info("Spawning Particle");
    }
}
