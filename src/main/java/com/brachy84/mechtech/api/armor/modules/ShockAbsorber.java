package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.AbsorbResult;
import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ISpecialArmorModule;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.items.IItemHandler;

public class ShockAbsorber implements ISpecialArmorModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return true;
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return MTMetaItems.SHOCK_ABSORBER.getStackForm();
    }

    @Override
    public String getModuleId() {
        return "shock_absorber";
    }

    @Override
    public AbsorbResult getArmorProperties(EntityLivingBase entity, ItemStack modularArmorPiece, NBTTagCompound moduleData, DamageSource source, double damage, EntityEquipmentSlot slot) {
        if(slot == EntityEquipmentSlot.FEET && source == DamageSource.FALL) {
            return new AbsorbResult(0.9, 40, 10);
        }
        return new AbsorbResult();
    }
}
