package com.brachy84.mechtech.client;

import gregtech.api.gui.widgets.SlotWidget;
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
        if(isShiftDown()) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onSlotChanged() {
        if (listener != null)
            listener.run();
    }
}
