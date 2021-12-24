package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import com.brachy84.mechtech.api.armor.Modules;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorUtils;
import gregtech.api.util.input.EnumKey;
import gregtech.common.items.MetaItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class AdvancedJetpack implements IArmorModule {

    private static final int ENERGY_PER_USE = 512;

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.CHEST && IArmorModule.moduleCount(this, modularSlots) == 0 && IArmorModule.moduleCount(Modules.JETPACK, modularSlots) == 0;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        IElectricItem cont = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if(cont == null) {
            return;
        }
        boolean hoverMode = armorData.hasKey("Hover") && armorData.getBoolean("Hover");
        boolean flyEnabled = armorData.hasKey("FlyMode") && armorData.getBoolean("FlyMode");
        byte toggleTimer = armorData.hasKey("ToggleTimer") ? armorData.getByte("ToggleTimer") : 0;
        boolean result = false;

        // Mode toggle
        if (!world.isRemote) {
            if (ArmorUtils.isKeyDown(player, EnumKey.FLY_KEY) && toggleTimer == 0) {
                flyEnabled = !flyEnabled;
                toggleTimer = 10;
            }
        }

        if (ArmorUtils.isKeyDown(player, EnumKey.JUMP) && ArmorUtils.isKeyDown(player, EnumKey.MODE_SWITCH) && toggleTimer == 0) {
            hoverMode = !hoverMode;
            toggleTimer = 10;
            if (!world.isRemote) {
                String status = hoverMode ? "metaarmor.jetpack.hover.enable" : "metaarmor.jetpack.hover.disable";
                player.sendMessage(new TextComponentTranslation(status));
            }
        }

        if (player.onGround) hoverMode = false;

        // Fly mechanics
        if (flyEnabled && cont.canUse(ENERGY_PER_USE) && !player.isInWater() && !player.isInLava()) {
            if (hoverMode) {
                if (!ArmorUtils.isKeyDown(player, EnumKey.JUMP) || !ArmorUtils.isKeyDown(player, EnumKey.SHIFT)) {
                    if (player.motionY > 0.1D) {
                        player.motionY -= 0.1D;
                    }

                    if (player.motionY < -0.1D) {
                        player.motionY += 0.1D;
                    }

                    if (player.motionY <= 0.1D && player.motionY >= -0.1D) {
                        player.motionY = 0.0D;
                    }

                    if (player.motionY > 0.1D || player.motionY < -0.1D) {
                        if (player.motionY < 0) {
                            player.motionY += 0.05D;
                        } else {
                            player.motionY -= 0.0025D;
                        }
                    } else {
                        player.motionY = 0.0D;
                    }
                    ArmorUtils.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.6D);
                    ArmorUtils.playJetpackSound(player);
                }

                if (ArmorUtils.isKeyDown(player, EnumKey.FORWARD)) {
                    player.moveRelative(0.0F, 0.0F, 0.25F, 0.2F);
                }

                if (ArmorUtils.isKeyDown(player, EnumKey.JUMP)) {
                    player.motionY = 0.35D;
                }

                if (ArmorUtils.isKeyDown(player, EnumKey.SHIFT)) {
                    player.motionY = -0.35D;
                }

                if (ArmorUtils.isKeyDown(player, EnumKey.JUMP) && ArmorUtils.isKeyDown(player, EnumKey.SHIFT)) {
                    player.motionY = 0.0D;
                }

                player.fallDistance = 0.0F;
                result = true;
            } else {
                if (ArmorUtils.isKeyDown(player, EnumKey.JUMP)) {
                    if (player.motionY <= 0.8D) player.motionY += 0.2D;
                    if (ArmorUtils.isKeyDown(player, EnumKey.FORWARD)) {
                        player.moveRelative(0.0F, 0.0F, 0.85F, 0.1F);
                    }
                    ArmorUtils.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.6D);
                    ArmorUtils.playJetpackSound(player);
                    player.fallDistance = 0.0F;
                    result = true;
                }
            }
        }

        // Fly discharge
        if (result) {
            cont.discharge(ENERGY_PER_USE, Integer.MAX_VALUE, true, false, false);
            ArmorUtils.resetPlayerFloatingTime(player);
        }

        // Do not spam of server packets
        if (toggleTimer > 0) {
            toggleTimer--;
        }

        armorData.setBoolean("FlyMode", flyEnabled);
        armorData.setBoolean("Hover", hoverMode);
        armorData.setByte("ToggleTimer", toggleTimer);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return MetaItems.ADVANCED_IMPELLER_JETPACK.getStackForm();
    }

    @Override
    public String getModuleId() {
        return "advanced_jetpack";
    }
}
