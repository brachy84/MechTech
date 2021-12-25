package com.brachy84.mechtech.api.armor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public interface IDurabilityModule {

    /**
     * Called when the armor is damaged
     *
     * @param entityLivingBase    wearing entity
     * @param modularArmorPiece   armor piece
     * @param damageSource        damage source
     * @param damage              amount
     * @param entityEquipmentSlot current slot
     * @return the damage applied to this module
     */
     default double damage(EntityLivingBase entityLivingBase, ItemStack modularArmorPiece, NBTTagCompound moduleData, DamageSource damageSource, float damage, EntityEquipmentSlot entityEquipmentSlot) {
         damage *= 100;
         int durability = getMaxDurability(moduleData) * 100;
         if (!moduleData.hasKey("Dmg")) {
             damage = Math.min(damage, durability);
             moduleData.setInteger("Dmg", (int) (damage));
         } else {
             int dmg = moduleData.getInteger("Dmg");
             damage = Math.min(damage, durability - dmg);
             moduleData.setInteger("Dmg", (int) (dmg + damage));
         }
         if(getDamage(moduleData) >= durability)
             moduleData.setBoolean("Destroyed", true);
         return damage / 100;
     }

     default double getDamage(NBTTagCompound moduleData) {
         return moduleData.getInteger("Dmg") / 100.0;
     }

     int getMaxDurability(NBTTagCompound moduleData);
}
