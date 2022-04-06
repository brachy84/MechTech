package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.Modules;
import com.google.common.collect.Lists;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.util.input.KeyBind;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;

public class AdvancedJetpack extends JetpackModule {

    @Override
    public int getEnergyPerUse() {
        return 512;
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.CHEST;
    }

    @Override
    public Collection<IModule> getIncompatibleModules() {
        return Lists.newArrayList(Modules.JETPACK);
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        IElectricItem cont = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if (cont == null) {
            return;
        }
        boolean hoverMode = armorData.getBoolean("hover");
        byte toggleTimer = armorData.getByte("toggleTimer");

        if (toggleTimer == 0 && KeyBind.ARMOR_HOVER.isKeyDown(player)) {
            hoverMode = !hoverMode;
            toggleTimer = 5;
            armorData.setBoolean("hover", hoverMode);
            if (!world.isRemote) {
                if (hoverMode)
                    player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.enable"), true);
                else
                    player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.disable"), true);
            }
        }

        performFlying(player, hoverMode, modularArmorPiece);

        if (toggleTimer > 0) toggleTimer--;

        armorData.setBoolean("hover", hoverMode);
        armorData.setByte("toggleTimer", toggleTimer);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public double getSprintEnergyModifier() {
        return 2.5D;
    }

    @Override
    public double getSprintSpeedModifier() {
        return 1.3D;
    }

    @Override
    public double getVerticalHoverSpeed() {
        return 0.34D;
    }

    @Override
    public double getVerticalHoverSlowSpeed() {
        return 0.03D;
    }

    @Override
    public double getVerticalAcceleration() {
        return 0.13D;
    }

    @Override
    public double getVerticalSpeed() {
        return 0.48D;
    }

    @Override
    public double getSidewaysSpeed() {
        return 0.14D;
    }

    @Override
    public EnumParticleTypes getParticle() {
        return EnumParticleTypes.CLOUD;
    }

    @Override
    public float getFallDamageReduction() {
        return 2.0f;
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
    public String getModuleId() {
        return "advanced_jetpack";
    }
}
