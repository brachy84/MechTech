package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.Modules;
import com.google.common.collect.Lists;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;
import java.util.function.Supplier;

public class SolarGen implements IModule {

    private final Supplier<ItemStack> item;
    private final long gen;
    private final int tier;

    public SolarGen(Supplier<ItemStack> item, long gen, int tier) {
        this.item = item;
        this.gen = gen;
        this.tier = tier;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        if(world.isRemote && world.canSeeSky(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ))) {
            float sunBrightness = world.getSunBrightness(Minecraft.getMinecraft().getRenderPartialTicks());
            if(sunBrightness > 0.2) { // for whatever reason sun brightness will never go below 0.2
                int generated = (int) (gen * sunBrightness);
                for (ItemStack stack : player.getArmorInventoryList()) {
                    if (stack.isEmpty())
                        continue;
                    IElectricItem item = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                    if (item == null)
                        continue;
                    generated -= item.charge(generated, Integer.MAX_VALUE, false, false);
                    if (generated <= 0)
                        break;
                }
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD;
    }

    @Override
    public Collection<IModule> getIncompatibleModules() {
        return Lists.newArrayList(Modules.solarGen1, Modules.solarGen2, Modules.solarGen3);
    }

    @Override
    public ItemStack getAsItemStack(NBTTagCompound nbt) {
        return item.get();
    }

    @Override
    public String getModuleId() {
        return "solar_gen." + tier;
    }
}
