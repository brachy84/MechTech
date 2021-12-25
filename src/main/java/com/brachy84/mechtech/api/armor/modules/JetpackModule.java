package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.Modules;
import com.google.common.collect.Lists;
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

import java.util.Collection;

public class JetpackModule implements IModule {

    private static final int ENERGY_PER_USE = 125;

    public JetpackModule() {
    }

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
        IElectricItem container = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if (container != null && container.canUse(ENERGY_PER_USE) && !player.isInWater() && !player.isInLava()) {
            byte toggleTimer = 0;
            boolean hover = false;
            boolean res = false;
            if (armorData.hasKey("toggleTimer")) toggleTimer = armorData.getByte("toggleTimer");
            if (armorData.hasKey("hover")) hover = armorData.getBoolean("hover");

            if (ArmorUtils.isKeyDown(player, EnumKey.MODE_SWITCH) && ArmorUtils.isKeyDown(player, EnumKey.JUMP) && toggleTimer == 0) {
                hover = !hover;
                toggleTimer = 10;
                if (!world.isRemote) {
                    armorData.setBoolean("hover", hover);
                    if (hover) {
                        player.sendMessage(new TextComponentTranslation("metaarmor.jetpack.hover.enable"));
                    } else {
                        player.sendMessage(new TextComponentTranslation("metaarmor.jetpack.hover.disable"));
                    }
                }
            }

            if (!hover) {
                if (ArmorUtils.isKeyDown(player, EnumKey.JUMP)) {
                    if (player.motionY < 0.6D) player.motionY += 0.2D;
                    if (ArmorUtils.isKeyDown(player, EnumKey.FORWARD)) {
                        player.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
                    }
                    ArmorUtils.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.6D);
                    ArmorUtils.playJetpackSound(player);
                    res = true;
                }
            } else {
                if (!player.onGround) {
                    ArmorUtils.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.3D);
                    ArmorUtils.playJetpackSound(player);
                }
                if (ArmorUtils.isKeyDown(player, EnumKey.FORWARD) && player.motionX < 0.5D && player.motionZ < 0.5D) {
                    player.moveRelative(0.0F, 0.0F, 1.0F, 0.025F);
                }

                if (ArmorUtils.isKeyDown(player, EnumKey.JUMP)) {
                    if (player.motionY < 0.5D) {
                        player.motionY += 0.125D;
                    }
                } else if (ArmorUtils.isKeyDown(player, EnumKey.SHIFT)) {
                    if (player.motionY < -0.5D) player.motionY += 0.1D;
                } else if (!ArmorUtils.isKeyDown(player, EnumKey.JUMP) && !ArmorUtils.isKeyDown(player, EnumKey.SHIFT) && !player.onGround) {
                    if (player.motionY < 0 && player.motionY >= -0.03D) player.motionY = -0.025D;
                    if (player.motionY < -0.025D) {
                        if (player.motionY + 0.2D > -0.025D) {
                            player.motionY = -0.025D;
                        } else {
                            player.motionY += 0.2D;
                        }
                    }
                }
                player.fallDistance = 0.0F;
                res = true;
            }

            if (res && !player.onGround) {
                container.discharge(ENERGY_PER_USE, Integer.MAX_VALUE, false, false, false);
            }

            if (world.getWorldTime() % 40 == 0 && !player.onGround) {
                ArmorUtils.resetPlayerFloatingTime(player);
            }

            if (toggleTimer > 0) toggleTimer--;

            armorData.setByte("toggleTimer", toggleTimer);
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return MetaItems.IMPELLER_JETPACK.getStackForm();
    }

    @Override
    public String getModuleId() {
        return "jetpack";
    }
}
