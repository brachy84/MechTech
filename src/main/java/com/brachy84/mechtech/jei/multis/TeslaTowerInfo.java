package com.brachy84.mechtech.jei.multis;

import com.brachy84.mechtech.machines.MTTileEntities;
import com.brachy84.mechtech.utils.TorusBlock;
import gregicadditions.item.GAHeatingCoil;
import gregicadditions.item.GAMetaBlocks;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class TeslaTowerInfo extends MultiblockInfoPage {
    @Override
    public MultiblockControllerBase getController() {
        return MTTileEntities.TESLA_TOWER;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shape = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
        BlockCompressed plastic = MetaBlocks.COMPRESSED.get(Materials.Plastic);
        builder.aisle("#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "#############")
                .aisle("#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "###G#####G###", "####GGGGG####")
                .aisle("#####BBB#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "###G#####G###", "##G#GGGGG#G##", "###G#####G###")
                .aisle("####BBBBB####", "#####CCC#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "##G#######G##", "#G#G##G##G#G#", "##G#######G##")
                .aisle("###BBBBBBB###", "####C###C####", "#############", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#############", "#G#########G#", "G#G###G###G#G", "#G#########G#")
                .aisle("##BBBBBBBBB##", "###C##P##C###", "######P######", "####C#P#C####", "####C#P#C####", "####C#P#C####", "####C#P#C####", "####C#P#C####", "######P######", "#G####P####G#", "G#G##PGP##G#G", "#G####P####G#")
                .aisle("##BBBBBBBBB##", "###C#P#P#C###", "#####P#P#####", "####CP#PC####", "####CP#PC####", "####CP#PC####", "####CP#PC####", "####CP#PC####", "#####P#P#####", "#G###P#P###G#", "G#GGGG#GGGG#G", "#G###P#P###G#")
                .aisle("##BBBBBBBBB##", "###C##P##C###", "######P######", "####C#P#C####", "####C#P#C####", "####C#P#C####", "####C#P#C####", "####C#P#C####", "######P######", "#G####P####G#", "G#G##PGP##G#G", "#G####P####G#")
                .aisle("###BBBBBBB###", "####C###C####", "#############", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#####CCC#####", "#############", "#G#########G#", "G#G###G###G#G", "#G#########G#")
                .aisle("####BBBBB####", "#####CCC#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "##G#######G##", "#G#G##G##G#G#", "##G#######G##")
                .aisle("#####ISB#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "###G#####G###", "##G#GGGGG#G##", "###G#####G###")
                .aisle("#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "###G#####G###", "####GGGGG####")
                .aisle("#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "#############")
                .where('S', MTTileEntities.TESLA_TOWER, EnumFacing.SOUTH)
                .where('I', MetaTileEntities.ENERGY_INPUT_HATCH[4], EnumFacing.SOUTH)
                .where('B', GAMetaBlocks.METAL_CASING.get(Materials.Titanium).getDefaultState())
                .where('P', plastic.getStateFromMeta(13))
                .where('#', Blocks.AIR.getDefaultState());
        for (BlockWireCoil.CoilType coilType : BlockWireCoil.CoilType.values()) {
            shape.add(builder.where('C', MetaBlocks.WIRE_COIL.getState(coilType))
                    .where('G', TorusBlock.getTorusBlocks().get(0).getBlock())
                    .build());
        }
        for (GAHeatingCoil.CoilType coilType : GAHeatingCoil.CoilType.values()) {
            shape.add(builder.where('C', GAMetaBlocks.HEATING_COIL.getState(coilType))
                    .where('G', TorusBlock.getTorusBlocks().get(0).getBlock())
                    .build());
        }
        for (TorusBlock block : TorusBlock.getTorusBlocks()) {
            shape.add(
                    builder.where('C', MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.CUPRONICKEL))
                            .where('G', block.getBlock())
                            .build());
        }
        return shape;
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("mechtech.multiblock.tesla_tower.description")};
    }

    @Override
    public float getDefaultZoom() {
        return 0.3f;
    }
}
