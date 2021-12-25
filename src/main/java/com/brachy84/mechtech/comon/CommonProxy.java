package com.brachy84.mechtech.comon;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.Modules;
import com.brachy84.mechtech.api.armor.modules.ProtectionModule;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import com.brachy84.mechtech.comon.recipes.Recipes;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
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
        for(IModule module : Modules.getRegisteredModules()) {
            if(module instanceof ProtectionModule) {
                ProtectionModule protectionModule = (ProtectionModule) module;
                if(protectionModule.doGenerateMaterialRecipe && protectionModule.getMaterial() != null && protectionModule.getMaterial().hasFlag(MaterialFlags.GENERATE_PLATE)) {
                    generateArmorPlatingRecipe(protectionModule);
                }
            }
        }
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

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            for(int i = 0; i < 4; i++) {
                ItemStack stack = player.inventory.armorInventory.get(i);
                ModularArmor modularArmor = ModularArmor.get(stack);
                if(modularArmor != null) {
                    List<IModule> modules = ModularArmor.getModulesOf(stack);
                    for(IModule module : modules) {
                        if(module == Modules.SHOCK_ABSORBER) {
                            event.setStrength(event.getStrength() * 0.2f);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void generateArmorPlatingRecipe(ProtectionModule module) {
        ItemStack result = module.getAsItemStack(new NBTTagCompound());
        Material material = module.getMaterial();
        if(material.hasProperty(PropertyKey.INGOT)) {
            ModHandler.addShapedRecipe("armor_plating_" + material.toString(), result, "PPh", "PP ", "h  ", 'P', new UnificationEntry(OrePrefix.plate, material));
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                    .input(OrePrefix.plate, material, 2)
                    .input(OrePrefix.plate, material, 2)
                    .outputs(result)
                    .duration(160)
                    .EUt(44)
                    .buildAndRegister();
        } else if(material.hasProperty(PropertyKey.GEM)) {

            // TODO gem plating recipe
            ModHandler.addShapedRecipe("armor_plating_" + material.toString(), result, "PPh", "PP ", "h  ", 'P', new UnificationEntry(OrePrefix.plate, material));
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                    .input(OrePrefix.plate, material, 2)
                    .input(OrePrefix.plate, material, 2)
                    .outputs(result)
                    .duration(160)
                    .EUt(44)
                    .buildAndRegister();
        }
    }
}
