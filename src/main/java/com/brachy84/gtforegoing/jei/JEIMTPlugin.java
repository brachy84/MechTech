package com.brachy84.gtforegoing.jei;

import com.brachy84.gtforegoing.jei.multis.TeslaTowerInfo;
import com.google.common.collect.Lists;
import gregicadditions.jei.LargeMultiblockInfoRecipeWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JEIMTPlugin implements IModPlugin {

    public JEIMTPlugin() {}

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(Lists.newArrayList(
                new LargeMultiblockInfoRecipeWrapper(new TeslaTowerInfo())
        ), "gregtech:multiblock_info");
    }
}
