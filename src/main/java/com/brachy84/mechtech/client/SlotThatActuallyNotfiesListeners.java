package com.brachy84.mechtech.client;

import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

public class SlotThatActuallyNotfiesListeners extends SlotWidget {

    private Runnable listener;

    public SlotThatActuallyNotfiesListeners(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    @Override
    public SlotWidget setChangeListener(Runnable changeListener) {
        this.listener = changeListener;
        return this;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if(isShiftDown()) {
            return this.isMouseOverElement(mouseX, mouseY) && this.gui != null && insert();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // the change listener is so jank that i have to implement my one shift click function
    private boolean insert() {
        ItemStack current = getHandle().getStack();
        if(gui != null) {
            for(int i = 0; i < 4; i++) {
                ItemStack slotStack = gui.entityPlayer.inventory.armorInventory.get(i);
                EntityEquipmentSlot slot = current.getItem().getEquipmentSlot(current);
                if(slotStack.isEmpty() && slot != null && slot.getIndex() == i) {
                    gui.entityPlayer.inventory.armorInventory.set(i, current);
                    getHandle().putStack(ItemStack.EMPTY);
                    return true;
                }
            }
            for(int i = 0; i < gui.entityPlayer.inventory.mainInventory.size(); i++) {
                ItemStack slotStack = gui.entityPlayer.inventory.mainInventory.get(i);
                if(slotStack.isEmpty()) {
                    gui.entityPlayer.inventory.mainInventory.set(i, current);
                    getHandle().putStack(ItemStack.EMPTY);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onSlotChanged() {
        if (listener != null)
            listener.run();
    }
}
