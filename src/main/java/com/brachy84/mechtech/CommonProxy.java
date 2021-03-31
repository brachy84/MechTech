package com.brachy84.mechtech;

import com.brachy84.mechtech.items.MTMetaItems;
import com.brachy84.mechtech.recipes.Recipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
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
}
