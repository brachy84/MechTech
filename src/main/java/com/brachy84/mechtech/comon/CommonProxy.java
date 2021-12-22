package com.brachy84.mechtech.comon;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import com.brachy84.mechtech.comon.recipes.Recipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        if(event.getFrom().isEmpty())
            return;
        ModularArmor modularArmor = ModularArmor.get(event.getFrom());
        if(modularArmor != null) {
            modularArmor.onUnequip(event.getEntity().world, event.getEntityLiving(), event.getFrom(), event.getTo());
        }
    }
}
