package com.brachy84.mechtech.jei;

import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.jei.multis.TeslaTowerInfo;
import com.brachy84.mechtech.jei.multis.TokamakInfo;
import com.brachy84.mechtech.machines.MTTileEntities;
import com.brachy84.mechtech.utils.TorusBlock;
import com.google.common.collect.Lists;
import gregicadditions.machines.GATileEntities;
import gregicadditions.recipes.GARecipeMaps;
import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMap;
import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class JEIMTPlugin implements IModPlugin {

    public JEIMTPlugin() {}

    public static final String TORUS_RECIPES = MechTech.MODID + ":torus_block";
    public static final String MUTLIBLOCK_INFOS = "gregtech:multiblock_info";

    List<MultiblockInfoRecipeWrapper> multiblockInfos = new ArrayList<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        /*registry.addRecipeCategories(
                new TorusBlockRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );*/
    }

    @Override
    public void register(IModRegistry registry) {
        multiblockInfos = Lists.newArrayList(
                new MultiblockInfoRecipeWrapper(new TeslaTowerInfo())

        );
        if(MTConfig.multis.tokamak.enableTokamak) {
            multiblockInfos.add(new MultiblockInfoRecipeWrapper(new TokamakInfo()));
        }
        /*registry.addRecipes(multiblockInfos, MUTLIBLOCK_INFOS);

        registry.addRecipes(TorusBlock.getTorusBlocks()
                .stream().map((TorusBlockRecipeWrapper::new)).collect(Collectors.toList()), TORUS_RECIPES);

        registry.addRecipeCatalyst(MTTileEntities.TESLA_TOWER.getStackForm(), TORUS_RECIPES);*/
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();

        if(MTConfig.multis.tokamak.enableTokamak) {
            IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, GATileEntities.ADVANCED_FUSION_REACTOR.getStackForm());
            IRecipeCategory category = registry.getRecipeCategory(MUTLIBLOCK_INFOS);
            if(category == null) return;
            registry.getRecipeWrappers(category, focus).forEach(wrapper -> {
                if(!multiblockInfos.contains(wrapper)) {
                    registry.hideRecipe((IRecipeWrapper) wrapper, MUTLIBLOCK_INFOS);
                }
            });
            //IRecipeWrapper recipe = registry.getRecipeWrapper(new FusionReactor4Info(), MUTLIBLOCK_INFOS);
            //if(recipe == null) return;
            //registry.hideRecipe(recipe, MUTLIBLOCK_INFOS);
        }
    }

    public String getRecipeMapJeiCategory(RecipeMap<?> recipeMap) {
        return GTValues.MODID + ":" + recipeMap.unlocalizedName;
    }
}
