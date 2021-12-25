package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.api.armor.IDurabilityModule;
import gregtech.api.unification.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.items.IItemHandler;

public class ProtectionModule implements IArmorModule, IDurabilityModule {

    private final Material material;
    private ItemStack stack;
    public final double armor, toughness;
    public final int durability;
    public boolean doGenerateMaterialRecipe = true;

    public ProtectionModule(Material material, ItemStack stack, double armor, double toughness, int durability) {
        this.material = material;
        this.stack = stack;
        this.armor = armor;
        this.toughness = toughness;
        this.durability = durability;
    }

    public Material getMaterial() {
        return material;
    }

    public ProtectionModule dontGenerateMaterialRecipe() {
        this.doGenerateMaterialRecipe = false;
        return this;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int maxModules() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return true;
    }

    @Override
    public double getArmor(EntityEquipmentSlot slot) {
        return armor;
    }

    @Override
    public double getToughness(EntityEquipmentSlot slot) {
        return toughness;
    }

    @Override
    public int getMaxDurability(NBTTagCompound moduleData) {
        return durability;
    }

    @Override
    public void writeExtraData(NBTTagCompound nbt, ItemStack moduleItem) {
        NBTTagCompound moduleNbt = moduleItem.getTagCompound();
        if (moduleNbt != null && moduleNbt.hasKey("Dmg")) {
            nbt.setInteger("Dmg", moduleNbt.getInteger("Dmg"));
        }
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        ItemStack stack = this.stack.copy();
        if (nbt.hasKey("Dmg")) {
            NBTTagCompound stackNbt = stack.getTagCompound();
            if (stackNbt == null) {
                stackNbt = new NBTTagCompound();
                stack.setTagCompound(stackNbt);
            }
            stackNbt.setInteger("Dmg", nbt.getInteger("Dmg"));
        }
        return stack;
    }

    @Override
    public String getLocalizedName() {
        return I18n.format("mechtech.modules.armor_plating.name", material.getLocalizedName());
    }

    @Override
    public String getModuleId() {
        return "armor_plating";
    }
}
