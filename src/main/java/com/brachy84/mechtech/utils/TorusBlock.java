package com.brachy84.mechtech.utils;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import gregicadditions.GAMaterials;
import gregicadditions.GAValues;
import gregtech.api.unification.material.type.Material;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@ZenClass("mods.mechtech.TorusBlock")
@ZenRegister
public class TorusBlock {

    private static final List<TorusBlock> TORUS_BLOCKS = Lists.newArrayList(
            new TorusBlock(0.75f, 0.65f, GAValues.LV, GAMaterials.HVSuperconductor, 9),
            new TorusBlock(1.0f, 0.6f, GAValues.MV, GAMaterials.EVSuperconductor, 8),
            new TorusBlock(2.0f, 0.65f, GAValues.HV, GAMaterials.IVSuperconductor, 7),
            new TorusBlock(2.6f, 0.7f, GAValues.EV, GAMaterials.LuVSuperconductor, 6),
            new TorusBlock(3.2f, 0.75f, GAValues.IV, GAMaterials.ZPMSuperconductor, 4),
            new TorusBlock(4.0f, 0.8f, GAValues.LuV, GAMaterials.UVSuperconductor, 8),
            new TorusBlock(4.8f, 0.85f, GAValues.ZPM, GAMaterials.UHVSuperconductor, 3),
            new TorusBlock(5.4f, 0.89f, GAValues.UV, GAMaterials.UEVSuperconductor, 1),
            new TorusBlock(6.0f, 0.92f, GAValues.UHV, GAMaterials.UIVSuperconductor, 6),
            new TorusBlock(7.0f, 0.95f, GAValues.UEV, GAMaterials.UMVSuperconductor, 4),
            new TorusBlock(8.0f, 0.99f, GAValues.UIV, GAMaterials.UXVSuperconductor, 2)
    );

    public static List<TorusBlock> getTorusBlocks() {
        return Collections.unmodifiableList(TORUS_BLOCKS);
    }

    /**
     * @param material     the name of the material
     * @param rangeMod     the modifier when all blocks are this material. values below 0 will make it always 0
     * @param conductivity how much energyLoss will be subtracted when all blocks are this material
     */
    @ZenMethod
    public static void addTorusBlock(@NotNull Material material, int meta, float rangeMod, float conductivity, int maxVoltage) {
        TORUS_BLOCKS.add(new TorusBlock(rangeMod, conductivity, maxVoltage, material, meta));
    }

    @ZenMethod
    public static void addTorusBlock(@NotNull crafttweaker.api.block.IBlockState blockState, float rangeMod, float conductivity, int maxVoltage) {
        TORUS_BLOCKS.add(new TorusBlock(rangeMod, conductivity, maxVoltage, CraftTweakerMC.getBlockState(blockState)));
    }

    @ZenMethod
    public static void removeTorusBlock(@NotNull Material material, int meta) {
        TorusBlock block = TorusBlock.get(material, meta);
        if (block != null) {
            TORUS_BLOCKS.remove(block);
        }
    }

    @ZenMethod
    public static void removeTorusBlock(@NotNull crafttweaker.api.block.IBlockState blockState) {
        TorusBlock block = TorusBlock.get(CraftTweakerMC.getBlockState(blockState));
        TORUS_BLOCKS.remove(block);
    }

    private IBlockState blockState;

    /**
     * How much range should be added (ore subtracted) if all blocks would be this material
     * min is 0
     * 1 would do nothing (100%)
     * 1.2 -> range increased by 20%
     */
    private final float rangeModifier;

    /**
     * Basically a energyLoss modifier
     * min is 0
     * 1 would do loose everything (100%)
     * 0.2 -> 20% energy lost
     */
    private final float conductivity;

    /**
     * The max voltage that will be supported
     * 0 (ULV) - 14 (MAX)
     */
    private final int maxVoltageTier;

    public TorusBlock(float rangeModifier, float conductivity, int maxVoltageTier, IBlockState blockState) {
        //if(rangeModifier < 0) {
        //    throw new IllegalArgumentException("RangeModifier must be greater than 0");
        //}
        this.rangeModifier = rangeModifier;
        this.conductivity = conductivity;
        if (maxVoltageTier > 14 || maxVoltageTier < 0) {
            throw new IllegalArgumentException("Max Voltage can't be higher than 14 or lower than 0");
        }
        this.maxVoltageTier = maxVoltageTier;
        this.blockState = blockState;
    }

    //public TorusBlock(float rangeModifier, float conductivity, int maxVoltageTier, IBlockState block) {
    //    this(rangeModifier, conductivity, maxVoltageTier, block.getBlock());
    //}

    public TorusBlock(float rangeModifier, float conductivity, int maxVoltageTier, Material material, int meta) {
        this(rangeModifier, conductivity, maxVoltageTier, MetaBlocks.COMPRESSED.get(material).getStateFromMeta(meta));
    }

    @Nullable
    public static TorusBlock get(IBlockState blockState) {
        for (TorusBlock block : TORUS_BLOCKS) {
            if (blockState == block.blockState) {
                return block;
            }
        }
        return null;
    }

    @Nullable
    public static TorusBlock get(Material material, int meta) {
        return get(MetaBlocks.COMPRESSED.get(material).getStateFromMeta(meta));
    }

    public float getRangeModifier() {
        return rangeModifier;
    }

    public float getConductivity() {
        return conductivity;
    }

    public int getMaxVoltageTier() {
        return maxVoltageTier;
    }

    public IBlockState getBlock() {
        return blockState;
    }

    public ItemStack getItemBlock() {
        return new ItemStack(blockState.getBlock(), 1, blockState.getBlock().getMetaFromState(blockState));
    }
}