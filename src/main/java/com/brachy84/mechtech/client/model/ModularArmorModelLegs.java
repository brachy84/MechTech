package com.brachy84.mechtech.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ModularArmorModelLegs extends ArmorModel {

    public static final ModularArmorModelLegs INSTANCE = new ModularArmorModelLegs();

    private final ModelRenderer bb_main;

    public ModularArmorModelLegs() {
        textureWidth = 32;
        textureHeight = 32;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 6, -5.0F, -1.0F, 2.0F, 10, 1, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 4, -5.0F, -1.0F, -3.0F, 10, 1, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 21, 0, -5.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 16, 18, 4.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 12, 8, 4.0F, -15.0F, 2.0F, 1, 14, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 8, 8, -5.0F, -15.0F, 2.0F, 1, 14, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 4, 8, -5.0F, -15.0F, -3.0F, 1, 14, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 8, 4.0F, -15.0F, -3.0F, 1, 14, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 2, -5.0F, -16.0F, -3.0F, 10, 1, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -5.0F, -16.0F, 2.0F, 10, 1, 1, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 16, 13, 4.0F, -16.0F, -2.0F, 1, 1, 4, 0.0F, false));
        bb_main.cubeList.add(new ModelBox(bb_main, 16, 8, -5.0F, -16.0F, -2.0F, 1, 1, 4, 0.0F, false));
    }

    @Override
    public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        bb_main.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
