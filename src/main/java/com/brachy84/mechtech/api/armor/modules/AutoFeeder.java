package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.AbstractModule;
import com.brachy84.mechtech.common.items.MTMetaItems;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IFoodBehavior;
import gregtech.api.items.metaitem.stats.IItemComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class AutoFeeder extends AbstractModule {

    public AutoFeeder() {
        super("auto_feeder");
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot != EntityEquipmentSlot.FEET;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        int needed = 20 - player.getFoodStats().getFoodLevel();
        if (needed == 0)
            return;
        byte food = armorData.getByte("food");
        // try to feed stored food
        if (food > 0) {
            int toFeed = Math.min(food, needed);
            player.getFoodStats().addStats(toFeed, 0);
            armorData.setByte("food", (byte) (food - toFeed));
            return;
        }

        // find food item in inventory
        int hunger;
        float saturation;
        outer:
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty())
                continue;
            if (stack.getItem() instanceof ItemFood) {
                hunger = ((ItemFood) stack.getItem()).getHealAmount(stack);
                ItemStack remainder = stack.getItem().onItemUseFinish(stack, world, player);
                if (hunger > needed)
                    armorData.setByte("food", (byte) (hunger - needed));
                player.inventory.mainInventory.set(i, remainder);
                return;
            }
            if (stack.getItem() instanceof MetaItem) {
                for (IItemComponent component : ((MetaItem<?>) stack.getItem()).getItem(stack).getAllStats()) {
                    if (component instanceof IFoodBehavior) {
                        hunger = ((IFoodBehavior) component).getFoodLevel(stack, player);
                        saturation = ((IFoodBehavior) component).getSaturation(stack, player);
                        int toFeed = Math.min(needed, hunger);
                        player.getFoodStats().addStats(toFeed, saturation);
                        if (toFeed < hunger) {
                            armorData.setByte("food", (byte) (hunger - toFeed));
                        }
                        ((IFoodBehavior) component).onFoodEaten(stack, player);
                        stack.shrink(1);
                        break outer;
                    }
                }
            }
        }
    }

    @Override
    public MetaItem<?>.MetaValueItem getMetaValueItem() {
        return MTMetaItems.AUTO_FEEDER;
    }
}
