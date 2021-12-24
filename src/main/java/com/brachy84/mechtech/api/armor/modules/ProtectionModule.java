package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import gregtech.api.unification.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.items.IItemHandler;

public class ProtectionModule implements IArmorModule {

    private final Material material;
    private ItemStack stack;
    public final double armor, toughness, absorption;
    public final int durability;
    public boolean doGenerateMaterialRecipe = true;

    public ProtectionModule(Material material, ItemStack stack, double armor, double toughness, double absorption, int durability) {
        this.material = material;
        this.stack = stack;
        this.armor = armor;
        this.toughness = toughness;
        this.absorption = absorption;
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
    public ActionResult<ISpecialArmor.ArmorProperties> modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot) {
        if (!source.isUnblockable()) {
            properties.Armor += armor * ModularArmor.armorDamageSpread(slot);
            properties.Toughness += toughness * ModularArmor.armorDamageSpread(slot);
            properties.AbsorbRatio += absorption * ModularArmor.armorDamageSpread(slot);
        }
        return ActionResult.newResult(EnumActionResult.PASS, properties);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int damage(EntityLivingBase entityLivingBase, ItemStack modularArmorPiece, NBTTagCompound moduleData, DamageSource damageSource, int amount, EntityEquipmentSlot entityEquipmentSlot) {
        if (!moduleData.hasKey("Dmg")) {
            amount = Math.min(amount, durability);
            moduleData.setInteger("Dmg", amount);
            if (amount == durability) moduleData.setBoolean("Destroyed", true);
        } else {
            int dmg = moduleData.getInteger("Dmg");
            amount = Math.min(amount, durability - dmg);
            moduleData.setInteger("Dmg", dmg + amount);
            if (dmg + amount == durability) moduleData.setBoolean("Destroyed", true);
        }
        return amount;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armorPiece, int slot) {
        return 6;
        /*EntityEquipmentSlot equipmentSlot;
        switch (slot) {
            case 0:
                equipmentSlot = EntityEquipmentSlot.FEET;
                break;
            case 1:
                equipmentSlot = EntityEquipmentSlot.LEGS;
                break;
            case 2:
                equipmentSlot = EntityEquipmentSlot.CHEST;
                break;
            default:
                equipmentSlot = EntityEquipmentSlot.HEAD;
        }
        return (int) (absorption * ModularArmor.armorDamageSpread(equipmentSlot) * 4);*/
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
