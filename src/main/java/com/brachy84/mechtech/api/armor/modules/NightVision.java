package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.common.items.MetaItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class NightVision implements IModule {

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound moduleData) {
        if (!world.isRemote) {
            IElectricItem item = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
            if (item != null && item.canUse(4) && item.discharge(4, Integer.MAX_VALUE, false, false, false) == 4) {
                if (moduleData.getBoolean("NiVi"))
                    return;
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 999999999, 0, true, false));
            } else {
                player.removePotionEffect(MobEffects.NIGHT_VISION);
            }
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
        if (!world.isRemote) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD;
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return MetaItems.NIGHTVISION_GOGGLES.getStackForm();
    }

    @Override
    public String getModuleId() {
        return "night_vision";
    }
}
