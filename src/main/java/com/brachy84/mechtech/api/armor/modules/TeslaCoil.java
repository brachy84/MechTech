package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.network.packets.STeslaCoilEffect;
import com.brachy84.mechtech.common.MTConfig;
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

import java.util.Collections;
import java.util.List;

public class TeslaCoil implements IModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD || slot == EntityEquipmentSlot.CHEST;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        if (!world.isRemote && world.getTotalWorldTime() % 20 == 0) {

            double range = MTConfig.modularArmor.modules.teslaCoilRange;
            double damage = MTConfig.modularArmor.modules.teslaCoilDamage;
            double maxEntities = MTConfig.modularArmor.modules.teslaCoilMaxEntitiesPerSecond;
            double edRatio = MTConfig.modularArmor.modules.teslaCoilDamageEnergyRatio;

            IElectricItem electricItem = modularArmorPiece.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
            if (electricItem == null || electricItem.getMaxCharge() < edRatio)
                return;
            AxisAlignedBB box = new AxisAlignedBB(player.getPosition()).grow(range);
            List<Entity> livings = world.getEntitiesInAABBexcluding(player, box, entity -> entity instanceof EntityLivingBase && entity.isEntityAlive() && !(entity instanceof EntityPlayer));
            Collections.shuffle(livings);
            int count = 0;
            for (Entity entity : livings) {
                EntityLivingBase living = (EntityLivingBase) entity;
                float dmg = (float) Math.min(damage, living.getHealth());
                long energy = (long) (dmg * edRatio);
                if (electricItem.discharge(energy, Integer.MAX_VALUE, false, false, true) != energy)
                    break;
                electricItem.discharge(energy, Integer.MAX_VALUE, false, false, false);
                if (living.attackEntityFrom(DamageSources.getElectricDamage(), (float) damage)) {
                    playEffects(player, living);
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

    private void playEffects(EntityPlayer source, Entity target) {
        double targetY = target.posY + target.height / 2.0;
        double sourceY = source.posY + 2.2;
        STeslaCoilEffect packet = new STeslaCoilEffect(new Vec3d(source.posX, sourceY, source.posZ), new Vec3d(target.posX, targetY, target.posZ));
        NetworkHandler.channel.sendTo(packet.toFMLPacket(), (EntityPlayerMP) source);
    }
}
