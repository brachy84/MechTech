package com.brachy84.gtforegoing.machines.multis;

import gregicadditions.capabilities.impl.GARecipeMapMultiblockController;
import gregicadditions.machines.multi.advance.TileEntityAdvFusionReactor;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.ICubeRenderer;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityTokamak extends TileEntityAdvFusionReactor {

    public MetaTileEntityTokamak(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .aisle("####################")
                .build();
    }
}
