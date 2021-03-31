package com.brachy84.mechtech.jei;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.jei.multis.TeslaTowerInfo;
import com.brachy84.mechtech.machines.MTTileEntities;
import com.brachy84.mechtech.utils.TorusBlock;
import com.google.common.collect.Lists;
import gregicadditions.jei.LargeMultiblockInfoRecipeWrapper;
import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class JEIMTPlugin implements IModPlugin {

    public JEIMTPlugin() {}

    public static final String TORUS_RECIPES = MechTech.MODID + ":torus_block";
    public static final String MUTLIBLOCK_INFOS = "gregtech:multiblock_info";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new TorusBlockRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void register(IModRegistry registry) {
        List<MultiblockInfoRecipeWrapper> multiblockInfos = Lists.newArrayList(
                new MultiblockInfoRecipeWrapper(new TeslaTowerInfo())
        );
        registry.addRecipes(multiblockInfos, MUTLIBLOCK_INFOS);

        registry.addRecipes(TorusBlock.getTorusBlocks()
                .stream().map((TorusBlockRecipeWrapper::new)).collect(Collectors.toList()), TORUS_RECIPES);

        registry.addRecipeCatalyst(MTTileEntities.TESLA_TOWER.getStackForm(), TORUS_RECIPES);
    }
}
