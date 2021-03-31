package com.brachy84.mechtech.recipes.recipes;

import com.brachy84.mechtech.MTConfig;
import gregicadditions.GAValues;
import gregicadditions.item.GAHeatingCoil;
import gregicadditions.item.GAMetaBlocks;
import gregtech.common.blocks.BlockWireCoil;

import static gregicadditions.GAEnums.GAOrePrefix.*;
import static gregicadditions.GAMaterials.*;
import static gregicadditions.item.GAMetaItems.*;
import static gregicadditions.recipes.GARecipeMaps.*;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.MetaBlocks.*;
import static com.brachy84.mechtech.machines.MTTileEntities.*;

public class MetaTileEntityRecipes {

    public static void init() {
        if(MTConfig.multis.teslaTower.enabled) {
            ASSEMBLER_RECIPES.recipeBuilder().duration(1200).EUt(2048)
                    .input(gtMetalCasing, Titanium, 4)
                    .inputs(WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.TUNGSTENSTEEL, 8))
                    .input("circuitExtreme", 4)
                    .input(wireGtQuadruple, HVSuperconductor, 8)
                    .fluidInputs(SolderingAlloy.getFluid(1296))
                    .outputs(TESLA_TOWER.getStackForm())
                    .buildAndRegister();
        }
    }
}
