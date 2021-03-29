package com.brachy84.gtforegoing.machines.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.brachy84.gtforegoing.capability.MTCapabilities;
import com.brachy84.gtforegoing.capability.ILimitedItemHandler;
import com.brachy84.gtforegoing.capability.impl.LimitedItemHandlerContainer;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.render.Textures;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LimitedItemBusTile extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<ILimitedItemHandler> {

    LimitedItemHandlerContainer itemHandler;

    public LimitedItemBusTile(ResourceLocation metaTileEntityId, int tier, boolean isWhiteList, ItemStack... filterItems) {
        super(metaTileEntityId, tier);
        int sizeRoot = (1 + getTier());
        int size = sizeRoot * sizeRoot;
        itemHandler = new LimitedItemHandlerContainer(size, isWhiteList, filterItems);
    }

    public LimitedItemBusTile(ResourceLocation metaTileEntityId, int tier, LimitedItemHandlerContainer itemHandler) {
        super(metaTileEntityId, tier);
        this.itemHandler = itemHandler;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return null;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        int rowSize = (int) Math.sqrt(getInventorySize());
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176,
                18 + 18 * rowSize + 94)
                .label(10, 5, getMetaFullName());

        for (int y = 0; y < rowSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                int index = y * rowSize + x;
                builder.widget(new SlotWidget(itemHandler, index, 89 - rowSize * 9 + x * 18, 18 + y * 18, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT));
            }
        }
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 18 + 18 * rowSize + 12);
        return builder.build(getHolder(), entityPlayer);
    }

    private int getInventorySize() {
        return itemHandler.getSlots();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    public MultiblockAbility<ILimitedItemHandler> getAbility() {
        return MTCapabilities.LIMITED_ITEM_INPUT;
    }

    @Override
    public void registerAbilities(List<ILimitedItemHandler> abilityList) {
        abilityList.add(itemHandler);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.universal.tooltip.item_storage_capacity", getInventorySize()));
        if(itemHandler.isWhitelist()) {
            tooltip.add(I18n.format("gtforegoing.universal.tooltip.item_storage_accept"));
        } else {
            tooltip.add(I18n.format("gtforegoing.universal.tooltip.item_storage_reject"));
        }
        for(ItemStack stack1 : itemHandler.getFilterItems()) {
            tooltip.add(" - " + stack1.getDisplayName());
        }
    }
}
