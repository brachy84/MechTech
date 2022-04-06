package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.Modules;
import com.google.common.collect.Lists;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.util.input.KeyBind;
import gregtech.common.items.armor.IJetpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Collection;

public class JetpackModule implements IJetpack, IModule {

    @Override
    public Collection<IModule> getIncompatibleModules() {
        return Lists.newArrayList(Modules.ADVANCED_JETPACK);
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.CHEST;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        byte toggleTimer = 0;
        boolean hover = false;
        if (armorData.hasKey("toggleTimer")) {
            toggleTimer = armorData.getByte("toggleTimer");
        }

        if (armorData.hasKey("hover")) {
            hover = armorData.getBoolean("hover");
        }

        if (toggleTimer == 0 && KeyBind.ARMOR_HOVER.isKeyDown(player)) {
            hover = !hover;
            toggleTimer = 5;
            armorData.setBoolean("hover", hover);
            if (!world.isRemote) {
                if (hover) {
                    player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.enable"), true);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.disable"), true);
                }
            }
        }

        this.performFlying(player, hover, modularArmorPiece);
        if (toggleTimer > 0) {
            --toggleTimer;
        }

        armorData.setBoolean("hover", hover);
        armorData.setByte("toggleTimer", toggleTimer);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public int getEnergyPerUse() {
        return 32;
    }

    @Override
    public boolean canUseEnergy(@Nonnull ItemStack stack, int amount) {
        IElectricItem container = this.getIElectricItem(stack);
        return container != null && container.canUse(amount);
    }

    @Override
    public void drainEnergy(@Nonnull ItemStack stack, int amount) {
        IElectricItem container = this.getIElectricItem(stack);
        if (container != null) {
            container.discharge(amount, Integer.MAX_VALUE, true, false, false);
        }
    }

    @Override
    public boolean hasEnergy(@Nonnull ItemStack stack) {
        IElectricItem container = this.getIElectricItem(stack);
        if (container == null) {
            return false;
        } else {
            return container.getCharge() > 0L;
        }
    }

    private IElectricItem getIElectricItem(@Nonnull ItemStack stack) {
        return stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    }

    @Override
    public void drawHUD(ItemStack item, NBTTagCompound armorData) {
        ModularArmor.drawEnergyHUD(item);
        IElectricItem cont = item.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if (cont == null) return;
        if (armorData != null) {
            String status = "metaarmor.hud.status.disabled";
            if (armorData.getBoolean("hover")) {
                status = "metaarmor.hud.status.enabled";
            }
            ModularArmor.drawHUDText(item, Lists.newArrayList(status));
        }
    }

    @Override
    public double getVerticalHoverSpeed() {
        return 0.18D;
    }

    @Override
    public double getVerticalHoverSlowSpeed() {
        return 0.1D;
    }

    @Override
    public double getVerticalAcceleration() {
        return 0.12D;
    }

    @Override
    public double getVerticalSpeed() {
        return 0.3D;
    }

    @Override
    public double getSidewaysSpeed() {
        return 0.08D;
    }

    @Override
    public EnumParticleTypes getParticle() {
        return EnumParticleTypes.SMOKE_NORMAL;
    }

    @Override
    public String getModuleId() {
        return "jetpack";
    }

}
