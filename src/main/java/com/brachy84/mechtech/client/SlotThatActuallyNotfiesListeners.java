package com.brachy84.mechtech.client;

import gregtech.api.gui.impl.ModularUIGui;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

public class SlotThatActuallyNotfiesListeners extends SlotWidget {

    public SlotThatActuallyNotfiesListeners(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseOverElement(mouseX, mouseY) && this.gui != null) {
            ItemStack oldStack = getHandle().getStack().copy();
            ModularUIGui modularUIGui = this.gui.getModularUIGui();
            boolean last = modularUIGui.getDragSplitting();
            this.gui.getModularUIGui().superMouseClicked(mouseX, mouseY, button);
            if (last != modularUIGui.getDragSplitting()) {
                modularUIGui.dragSplittingButton = button;
                if (button == 0) {
                    modularUIGui.dragSplittingLimit = 0;
                } else if (button == 1) {
                    modularUIGui.dragSplittingLimit = 1;
                } else if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isActiveAndMatches(button - 100)) {
                    modularUIGui.dragSplittingLimit = 2;
                }
            }
            if(!ItemStack.areItemStackTagsEqual(oldStack, getHandle().getStack()) && getHandle().getStack().isEmpty()) {
                changeListener.run();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        changeListener.run();
    }
}
