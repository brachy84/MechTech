package com.brachy84.mechtech.cover;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import com.brachy84.mechtech.client.ClientHandler;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.cover.CoverBehavior;
import gregtech.api.cover.ICoverable;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public class CoverWirelessReceiver extends CoverBehavior {

    public CoverWirelessReceiver(ICoverable coverHolder, EnumFacing attachedSide) {
        super(coverHolder, attachedSide);
    }

    @Override
    public boolean canAttach() {
        IEnergyContainer container = this.coverHolder.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
        return container != null && container.inputsEnergy(attachedSide);
    }

    @Override
    public void renderCover(CCRenderState ccRenderState, Matrix4 matrix4, IVertexOperation[] iVertexOperations, Cuboid6 cuboid6, BlockRenderLayer blockRenderLayer) {
        ClientHandler.COVER_WIRELESS_RECEIVER.renderSided(attachedSide, cuboid6, ccRenderState, iVertexOperations, matrix4);
    }
}
