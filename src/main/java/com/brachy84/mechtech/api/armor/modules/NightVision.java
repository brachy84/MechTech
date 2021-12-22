package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

public class NightVision implements IArmorModule {

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece) {
        if(!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 999999999, 0, true, false));
        }
    }

    @Override
    public void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
        if(!world.isRemote) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece) {
        return slot == EntityEquipmentSlot.HEAD;
    }

    @Override
    public void modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot) {

    }

    @Override
    public ItemStack getAsItemStack() {
        return MTMetaItems.NIGHTVISION_MODULE.getStackForm();
    }

    @Override
    public String getLocalizedName() {
        return I18n.format("mechtech.modules.night_vision.name");
    }
}
