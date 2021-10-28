package com.brachy84.mechtech.jei;

import com.brachy84.mechtech.utils.TorusBlock;
//import gregicadditions.GAValues;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class TorusBlockRecipeWrapper implements IRecipeWrapper {

    private final TorusBlock torusBlock;

    public TorusBlockRecipeWrapper(TorusBlock torusBlock) {
        this.torusBlock = torusBlock;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, torusBlock.getItemBlock());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        drawLine(minecraft, "mechtech.recipe.torus_block.loss", 0, 18, 0x111111, torusBlock.getConductivity());
        drawLine(minecraft, "mechtech.recipe.torus_block.range", 0, 27, 0x111111, torusBlock.getRangeModifier());
        //drawLine(minecraft, "mechtech.recipe.torus_block.voltage", 0, 36, 0x111111, GAValues.VN[torusBlock.getMaxVoltageTier()], GAValues.V[torusBlock.getMaxVoltageTier()]);
    }

    public void drawLine(Minecraft minecraft, String langKey, int x, int y, int color, Object... obj) {
        minecraft.fontRenderer.drawString(I18n.format(langKey, obj), x, y, color);
    }

}
