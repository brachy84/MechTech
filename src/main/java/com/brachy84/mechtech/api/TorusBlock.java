package com.brachy84.mechtech.api;

import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Material;
import gregtech.api.util.GTLog;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TorusBlock {

    private static final Map<IBlockState, TorusBlock> TORUS_BLOCK_MAP = new HashMap<>();
    private static final Map<String, TorusBlock> TORUS_BLOCK_NAME_MAP = new HashMap<>();

    @Nullable
    public static TorusBlock get(IBlockState state) {
        return TORUS_BLOCK_MAP.get(state);
    }

    @Nullable
    public static TorusBlock get(String name) {
        return TORUS_BLOCK_NAME_MAP.get(name);
    }

    public static Map<String, TorusBlock> getRegistryMap() {
        return Collections.unmodifiableMap(TORUS_BLOCK_NAME_MAP);
    }

    public static void register(String id, TorusBlock block) {
        if(block.state.equals(Blocks.AIR.getDefaultState())) {
            GTLog.logger.error("Can not register TorusBlock with AIR block state");
            return;
        }
        TORUS_BLOCK_MAP.put(block.state, block);
        TORUS_BLOCK_NAME_MAP.put(id, block);
        block.name = id;
    }

    private final IBlockState state;
    private float dmgModifier;
    private float rangeModifier;
    private float voltageModifier;
    private float ampsPerBlock;
    private String name = "unregistered";

    public TorusBlock(IBlockState state) {
        this.state = state;
    }

    public static TorusBlock ofMaterial(Material material) {
        BlockCompressed block = MetaBlocks.COMPRESSED.get(material);
        if(block == null)
            return new TorusBlock(Blocks.AIR.getDefaultState());
        return new TorusBlock(block.getBlock(material));
    }

    public TorusBlock setDmgModifier(float dmgModifier) {
        this.dmgModifier = dmgModifier;
        return this;
    }

    public TorusBlock setRangeModifier(float rangeModifier) {
        this.rangeModifier = rangeModifier;
        return this;
    }

    public TorusBlock setVoltageModifier(float voltageModifier) {
        this.voltageModifier = voltageModifier;
        return this;
    }

    public TorusBlock setAmpsPerBlock(float ampsPerBlock) {
        this.ampsPerBlock = ampsPerBlock;
        return this;
    }

    public float getDmgModifier() {
        return dmgModifier;
    }

    public float getRangeModifier() {
        return rangeModifier;
    }

    public float getVoltageModifier() {
        return voltageModifier;
    }

    public float getAmpsPerBlock() {
        return ampsPerBlock;
    }

    public static TraceabilityPredicate traceabilityPredicate() {
        return new TraceabilityPredicate(blockWorldState -> {
            TorusBlock torusBlock = get(blockWorldState.getBlockState());
            if(torusBlock == null)
                return false;
            PatternMatchContext matchContext = blockWorldState.getMatchContext();
            matchContext.increment(torusBlock.name, 1);
            return true;
        });
    }
}
