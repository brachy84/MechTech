package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import gregtech.api.items.armor.ArmorUtils;
import gregtech.api.util.input.EnumKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class Binoculars implements IModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        byte toggleTimer = 0;
        boolean zoom = false;
        if (armorData.hasKey("toggleTimer")) {
            toggleTimer = armorData.getByte("toggleTimer");
        }

        if (armorData.hasKey("zoom")) {
            zoom = armorData.getBoolean("zoom");
        }

        if (toggleTimer == 0 && ArmorUtils.isKeyDown(player, EnumKey.MODE_SWITCH)) {
            zoom = !zoom;
            toggleTimer = 5;
            armorData.setBoolean("zoom", zoom);
            if (!world.isRemote) {
                if (zoom) {
                    player.sendStatusMessage(new TextComponentTranslation("mechtech.modules.binoculars.zoom.enable"), true);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("mechtech.modules.binoculars.zoom.disable"), true);
                }
            }
        }

        if (toggleTimer > 0) {
            --toggleTimer;
        }

        armorData.setBoolean("zoom", zoom);
        armorData.setByte("toggleTimer", toggleTimer);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public String getModuleId() {
        return "binoculars";
    }
}
