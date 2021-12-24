package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.items.IItemHandler;

public class ShockAbsorber implements IArmorModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return true;
    }

    @Override
    public ActionResult<ISpecialArmor.ArmorProperties> modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot) {
        if(slot == EntityEquipmentSlot.FEET && source == DamageSource.FALL) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, new ISpecialArmor.ArmorProperties(10, 0.9, Integer.MAX_VALUE));
        }
        return IArmorModule.super.modifyArmorProperties(properties, entity, modularArmorPiece, source, damage, slot);
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return MTMetaItems.SHOCK_ABSORBER.getStackForm();
    }

    @Override
    public String getModuleId() {
        return "shock_absorber";
    }
}
