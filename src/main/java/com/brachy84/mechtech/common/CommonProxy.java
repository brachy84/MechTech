package com.brachy84.mechtech.common;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.Modules;
import com.brachy84.mechtech.common.items.MTMetaItems;
import com.brachy84.mechtech.common.recipes.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = MechTech.MODID)
public class CommonProxy {

    public void preLoad() {
        MTMetaItems.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Recipes.init();
    }

    @SubscribeEvent
    public static void onEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.getFrom().isEmpty())
            return;
        ModularArmor modularArmor = ModularArmor.get(event.getFrom());
        if (modularArmor != null) {
            modularArmor.onUnequip(event.getEntity().world, event.getEntityLiving(), event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            for (int i = 0; i < 4; i++) {
                ItemStack stack = player.inventory.armorInventory.get(i);
                ModularArmor modularArmor = ModularArmor.get(stack);
                if (modularArmor != null) {
                    List<IModule> modules = ModularArmor.getModulesOf(stack);
                    for (IModule module : modules) {
                        if (module == Modules.SHOCK_ABSORBER) {
                            event.setStrength(event.getStrength() * 0.2f);
                            return;
                        }
                    }
                }
            }
        }
    }


}
