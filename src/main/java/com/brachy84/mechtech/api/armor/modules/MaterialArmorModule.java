package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.AbsorbResult;
import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.api.armor.IDurabilityModule;
import com.brachy84.mechtech.api.armor.ISpecialArmorModule;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemColorProvider;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.api.items.metaitem.stats.IItemMaxStackSizeProvider;
import gregtech.api.items.metaitem.stats.IItemNameProvider;
import gregtech.api.unification.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class MaterialArmorModule implements IArmorModule, IDurabilityModule, ISpecialArmorModule {

    private final Material material;
    public final double armor, toughness;
    public final int durability;
    private final ISpecialArmorModule specialArmorModule;

    public MaterialArmorModule(Material material, double armor, double toughness, int durability, ISpecialArmorModule specialArmorModule) {
        this.material = material;
        this.armor = armor;
        this.toughness = toughness;
        this.durability = durability;
        this.specialArmorModule = specialArmorModule;
    }

    public Material getMaterial() {
        return material;
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
    public void writeExtraDataToArmor(NBTTagCompound nbt, ItemStack moduleItem) {
        NBTTagCompound moduleNbt = moduleItem.getTagCompound();
        if (moduleNbt != null && moduleNbt.hasKey("Dmg")) {
            nbt.setInteger("Dmg", moduleNbt.getInteger("Dmg"));
        }
    }

    @Override
    public NBTTagCompound writeExtraDataToModuleItem(NBTTagCompound nbt) {
        if (nbt.hasKey("Dmg")) {
            NBTTagCompound stackNbt = new NBTTagCompound();
            stackNbt.setInteger("Dmg", nbt.getInteger("Dmg"));
            return stackNbt;
        }
        return null;
    }

    @Override
    public String getLocalizedName() {
        return I18n.format("mechtech.modules.armor_plating.name", material.getLocalizedName());
    }

    @Override
    public String getModuleId() {
        return "armor_plating";
    }

    @Override
    public AbsorbResult getArmorProperties(EntityLivingBase entity, ItemStack modularArmorPiece, NBTTagCompound moduleData, DamageSource source, double damage, EntityEquipmentSlot slot) {
        return specialArmorModule == null ? AbsorbResult.ZERO : specialArmorModule.getArmorProperties(entity, modularArmorPiece, moduleData, source, damage, slot);
    }

    @Override
    public void addInformation(ItemStack itemStack, List<String> lines) {
        NBTTagCompound nbt = itemStack.getTagCompound();
        int damaged = 0;
        if (nbt != null)
            damaged = (int) getDamage(nbt);
        lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.1", durability - damaged, durability));
        lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.2", armor));
        lines.add(I18n.format("mechtech.modules.armor_plating.tooltip.3", toughness));
        lines.add(I18n.format("mechtech.modular_armor.usable"));
    }

    @Override
    public void onAddedToItem(MetaItem.MetaValueItem metaValueItem) {
        IArmorModule.super.onAddedToItem(metaValueItem);
        metaValueItem.addComponents(((IItemColorProvider) (stack, layer) -> material.getMaterialRGB()))
                // name provider
                .addComponents(((IItemNameProvider) (stack, name) -> I18n.format("mechtech.modules.armor_plating.name", material.getLocalizedName())))
                // stack size provider
                .addComponents((IItemMaxStackSizeProvider) (itemStack, i) -> 64)
                // durability handler
                .addComponents(new IItemDurabilityManager() {
                    @Override
                    public boolean showsDurabilityBar(ItemStack itemStack) {
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        return nbt != null && getDamage(nbt) > 0;
                    }

                    @Override
                    public double getDurabilityForDisplay(ItemStack itemStack) {
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        return nbt == null ? 0 : getDamage(nbt) / ((double) durability);
                    }

                    @Override
                    public int getRGBDurabilityForDisplay(ItemStack itemStack) {
                        return MathHelper.hsvToRGB((1.0f - (float) getDurabilityForDisplay(itemStack)) / 3.0f, 1.0f, 1.0f);
                    }
                });
    }
}
