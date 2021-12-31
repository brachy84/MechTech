package com.brachy84.mechtech.network.packets;

import com.brachy84.mechtech.client.Sounds;
import com.brachy84.mechtech.client.render.Lightning;
import gregtech.api.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class STeslaTowerEffect implements IPacket {

    private BlockPos source;
    private BlockPos target;

    public STeslaTowerEffect() {
    }

    public STeslaTowerEffect(final BlockPos source, final BlockPos target) {
        this.source = source;
        this.target = target;
    }

    public BlockPos getSource() {
        return source;
    }

    public BlockPos getTarget() {
        return target;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(source);
        buf.writeBlockPos(target);
    }

    @Override
    public void decode(PacketBuffer buf) {
        source = buf.readBlockPos();
        target = buf.readBlockPos();
    }

    @Override
    public void executeClient(NetHandlerPlayClient handler) {
        Vec3d s = new Vec3d(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5);
        Vec3d t = new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        Vec3d d = t.subtract(s);    // difference of start & end
        double x = d.x, z = d.z;
        double l = Math.sqrt(x * x + z * z);    //
        x /= l;                                 // normalize d on horizontal axis
        z /= l;                                 //
        s = s.add(x * 4, 0, z * 4); // move start by normalized d * 4
        // result is a position inside the toroid

        Lightning lightning = new Lightning(Minecraft.getMinecraft().world, s, t)
                .setColor(new Color(83, 166, 189, 205).getRGB(), new Color(167, 192, 199, 235).getRGB())
                .setScale(25f)
                .setup();
        Minecraft.getMinecraft().effectRenderer.addEffect(lightning);
        Minecraft.getMinecraft().world.playSound(s.x, s.y, s.z, Sounds.TESLA_ZAP, SoundCategory.BLOCKS, 3, 1, false);
    }
}
