package com.brachy84.gtforegoing.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public interface ILimitedItemHandler extends IItemHandlerModifiable {

    /**
     * @return the items used to determine what is allowed and what not
     * if {@link #isWhitelist()} is true then this is a whitelist otherwise a blacklist
     */
    List<ItemStack> getFilterItems();

    default boolean isWhitelist() {
        return true;
    }

    @Override
    default boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        for(ItemStack stack1 : getFilterItems()) {
            if(stack1.getItem().equals(stack.getItem()) && stack1.getMetadata() == stack.getMetadata()) {
                return isWhitelist();
            }
        }
        return !isWhitelist();
    }
}
