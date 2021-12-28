package com.brachy84.mechtech.client.gui;

import com.brachy84.mechtech.api.armor.ModularArmor;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;

public class SlotThatActuallyNotfiesListeners extends SlotWidget {

    private Runnable listener;

    public SlotThatActuallyNotfiesListeners(IItemHandler inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    @Override
    public boolean canPutStack(ItemStack stack) {
        return super.canPutStack(stack) && ModularArmor.get(stack) != null;
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
        if(!current.isEmpty() && gui != null) {
            for(int i = 0; i < 4; i++) {
                ItemStack slotStack = gui.entityPlayer.inventory.armorInventory.get(i);
                EntityEquipmentSlot slot = current.getItem().getEquipmentSlot(current);
                if(slotStack.isEmpty() && slot != null && slot.getIndex() == i) {
                    gui.entityPlayer.inventory.armorInventory.set(i, current);
                    int finalI = i;
                    getHandle().putStack(ItemStack.EMPTY);
                    writeClientAction(-99999, buf -> {
                        buf.writeBoolean(true);
                        buf.writeVarInt(finalI);
                        buf.writeItemStack(current);
                    });
                    return true;
                }
            }
            for(int i = 0; i < gui.entityPlayer.inventory.mainInventory.size(); i++) {
                ItemStack slotStack = gui.entityPlayer.inventory.mainInventory.get(i);
                if(slotStack.isEmpty()) {
                    gui.entityPlayer.inventory.mainInventory.set(i, current);
                    getHandle().putStack(ItemStack.EMPTY);
                    int finalI = i;
                    writeClientAction(-99999, buf -> {
                        buf.writeBoolean(false);
                        buf.writeVarInt(finalI);
                        buf.writeItemStack(current);
                    });
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        if(id == -99999 && gui != null) {
            boolean b = buffer.readBoolean();
            int index = buffer.readVarInt();
            ItemStack stack = null;
            try {
                stack = buffer.readItemStack();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(stack != null) {
                InventoryPlayer inventoryPlayer = gui.entityPlayer.inventory;
                if(b) {
                    inventoryPlayer.armorInventory.set(index, stack);
                } else {
                    inventoryPlayer.mainInventory.set(index, stack);
                }
                getHandle().putStack(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onSlotChanged() {
        if (listener != null)
            listener.run();
    }
}
