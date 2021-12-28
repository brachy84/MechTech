package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.network.packets.SModuleParticles;
import com.brachy84.mechtech.comon.MTConfig;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.damagesources.DamageSources;
import gregtech.api.net.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TeslaCoil implements IModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD || slot == EntityEquipmentSlot.CHEST;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        if (!world.isRemote && world.getTotalWorldTime() % 20 == 0) {

            double range = MTConfig.modules.teslaCoilRange;
            double damage = MTConfig.modules.teslaCoilDamage;
            double maxEntities = MTConfig.modules.teslaCoilMaxEntitiesPerSecond;
            double edRatio = MTConfig.modules.teslaCoilDamageEnergyRatio;

            IElectricItem electricItem = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
            if (electricItem == null || electricItem.getMaxCharge() < edRatio)
                return;
            AxisAlignedBB box = new AxisAlignedBB(player.getPosition()).grow(range);
            List<Entity> livings = world.getEntitiesInAABBexcluding(player, box, entity -> entity instanceof EntityLivingBase && entity.isEntityAlive() && !(entity instanceof EntityPlayer));
            int count = 0;
            for (Entity entity : livings) {
                EntityLivingBase living = (EntityLivingBase) entity;
                float dmg = (float) Math.min(damage, living.getMaxHealth() - living.getHealth());
                long energy = (long) (dmg * edRatio);
                if (electricItem.discharge(energy, Integer.MAX_VALUE, false, false, true) != energy)
                    break;
                electricItem.discharge(energy, Integer.MAX_VALUE, false, false, false);
                if (living.attackEntityFrom(DamageSources.getElectricDamage(), (float) damage)) {
                    spawnLightning(player, living);
                    if (++count == maxEntities)
                        break;
                }

            }
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public String getModuleId() {
        return "tesla_coil";
    }

    private void spawnLightning(EntityPlayer source, Entity target) {
        SModuleParticles packet = new SModuleParticles(new Vec3d(source.posX, source.posY, source.posZ), new Vec3d(target.posX, target.posY, target.posZ));
        NetworkHandler.channel.sendTo(packet.toFMLPacket(), (EntityPlayerMP) source);
    }
}
