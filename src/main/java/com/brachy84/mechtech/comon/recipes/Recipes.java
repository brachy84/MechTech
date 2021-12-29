package com.brachy84.mechtech.comon.recipes;

import com.brachy84.mechtech.api.armor.MaterialArmorModuleBuilder;
import com.brachy84.mechtech.api.armor.Modules;
import com.brachy84.mechtech.comon.items.MTMetaItems;
import com.brachy84.mechtech.comon.machines.MTTileEntities;
import com.brachy84.mechtech.comon.recipes.recipes.MetaTileEntityRecipes;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.item.ItemStack;

public class Recipes {

    public static void init() {
        MetaTileEntityRecipes.init();
        modularArmor();
        for (MaterialArmorModuleBuilder builder : Modules.getArmorModules().values()) {
            if (builder.isRegistered()) {
                if (builder.doGenerateRecipe && builder.material != null && builder.material.hasFlag(MaterialFlags.GENERATE_PLATE)) {
                    generateArmorPlatingRecipe(builder);
                }
            }
        }
    }

    private static void modularArmor() {
        ModHandler.addShapedRecipe("armor_workbench", MTTileEntities.ARMOR_WORKBENCH.getStackForm(), " S ", "SWS", "hSw", 'S', new UnificationEntry(OrePrefix.stick, Materials.StainlessSteel), 'W', MetaTileEntities.WORKBENCH.getStackForm());

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.stick, Materials.StainlessSteel, 5)
                .input(MetaItems.CARBON_MESH, 3)
                .input(OrePrefix.plate, Materials.BlackBronze)
                .output(MTMetaItems.MODULAR_HELMET)
                .EUt(400)
                .duration(3600)
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.stick, Materials.StainlessSteel, 8)
                .input(MetaItems.CARBON_MESH, 6)
                .input(OrePrefix.plate, Materials.Tungsten)
                .output(MTMetaItems.MODULAR_CHESTPLATE)
                .EUt(400)
                .duration(3600)
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.stick, Materials.StainlessSteel, 7)
                .input(MetaItems.CARBON_MESH, 5)
                .input(OrePrefix.plate, Materials.NiobiumNitride)
                .output(MTMetaItems.MODULAR_LEGGINGS)
                .EUt(400)
                .duration(3600)
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.stick, Materials.StainlessSteel, 4)
                .input(MetaItems.CARBON_MESH, 2)
                .input(OrePrefix.plate, Materials.Nichrome)
                .output(MTMetaItems.MODULAR_BOOTS)
                .EUt(400)
                .duration(180)
                .buildAndRegister();
    }

    private static void generateArmorPlatingRecipe(MaterialArmorModuleBuilder builder) {
        ItemStack result = builder.getItemStack();
        if (result.isEmpty()) {
            throw new IllegalStateException("Result item can not be empty. Module was not properly registered");
        }
        Material material = builder.material;
        if (material.hasProperty(PropertyKey.INGOT)) {
            ModHandler.addShapedRecipe("armor_plating_" + material.toString(), result, "PPh", "PP ", "h  ", 'P', new UnificationEntry(OrePrefix.plate, material));
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                    .input(OrePrefix.plate, material, 2)
                    .input(OrePrefix.plate, material, 2)
                    .outputs(result)
                    .duration(160)
                    .EUt(44)
                    .buildAndRegister();
        } else if (material.hasProperty(PropertyKey.GEM)) {

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
