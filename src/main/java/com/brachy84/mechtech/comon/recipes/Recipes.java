package com.brachy84.mechtech.comon.recipes;

import com.brachy84.mechtech.comon.items.MTMetaItems;
import com.brachy84.mechtech.comon.machines.MTTileEntities;
import com.brachy84.mechtech.comon.recipes.recipes.MetaTileEntityRecipes;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public class Recipes {

    public static void init() {
        MetaTileEntityRecipes.init();
        modularArmor();
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
}
