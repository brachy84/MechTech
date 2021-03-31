package com.brachy84.mechtech.capability.impl;

import com.brachy84.mechtech.capability.ILimitedItemHandler;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class LimitedItemHandlerContainer implements ILimitedItemHandler {

    public static final ItemStack[] DATA_ITEMS = {
            MetaItems.TOOL_DATA_STICK.getStackForm(),
            MetaItems.TOOL_DATA_ORB.getStackForm()
    };

    List<ItemStack> items;
    List<ItemStack> filterItems;

    boolean isWhitelist;

    public LimitedItemHandlerContainer(int size, boolean isWhitelist, ItemStack... filterItems) {
        ItemStack[] stacks = new ItemStack[size];
        Arrays.fill(stacks, ItemStack.EMPTY);
        this.items = Arrays.asList(stacks);
        this.filterItems = Arrays.asList(filterItems);
        this.isWhitelist = isWhitelist;
    }

    @Override
    public List<ItemStack> getFilterItems() {
        return filterItems;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if(slot < items.size()) {
            items.set(slot, stack);
        }
    }

    @Override
    public int getSlots() {
        return items.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack stackInSLot = getStackInSlot(slot);
        if(stackInSLot.isEmpty()) {
            if(!simulate) setStackInSlot(slot, stack);
            return stack;
        }
        if(stackInSLot.getItem().equals(stack.getItem()) && stackInSLot.getCount() < stackInSLot.getMaxStackSize()) {
            int added = stackInSLot.getMaxStackSize() - stackInSLot.getCount();
            if(!simulate) stackInSLot.grow(added);
            ItemStack newStack = stack.copy();
            newStack.setCount(stack.getCount() - added);
            return newStack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stackInSlot = getStackInSlot(slot);
        if(stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if(simulate) {
            ItemStack stack = stackInSlot.copy();
            return stack.splitStack(amount);
        }
        return stackInSlot.splitStack(amount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getStackInSlot(slot).getMaxStackSize();
    }

    @Override
    public boolean isWhitelist() {
        return isWhitelist;
    }
}
