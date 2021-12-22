package com.brachy84.mechtech.api.armor;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IArmorModule extends IItemComponent {

    static IArmorModule getOf(ItemStack stack) {
        if(stack.getItem() instanceof MetaItem) {
            return getOf(((MetaItem<?>) stack.getItem()).getItem(stack));
        }
        return null;
    }

    static IArmorModule getOf(MetaItem<?>.MetaValueItem mvi) {
        for(IItemComponent component : mvi.getAllStats()) {
            if(component instanceof IArmorModule)
                return (IArmorModule) component;
        }
        return null;
    }

    void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData);

    default void onUnequip(World world, EntityLivingBase player, ItemStack modularArmorPiece, ItemStack newStack) {
    }

    boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece);

    void modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot);

    default void addTooltip(@Nonnull ItemStack itemStack, @Nullable World worldIn, @Nonnull List<String> lines, @Nonnull ITooltipFlag tooltipFlag) {
    }

    ItemStack getAsItemStack();

    @SideOnly(Side.CLIENT)
    String getLocalizedName();
}
