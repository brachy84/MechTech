package com.brachy84.mechtech.jei.multis;

import com.brachy84.mechtech.MTConfig;
import com.brachy84.mechtech.machines.MTTileEntities;
import com.brachy84.mechtech.utils.TorusBlock;
import gregicadditions.GAMaterials;
import gregicadditions.item.GAHeatingCoil;
import gregicadditions.item.GAMetaBlocks;
import gregicadditions.jei.GAMultiblockShapeInfo;
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
        GAMultiblockShapeInfo.Builder builder = GAMultiblockShapeInfo.builder();
        BlockCompressed plastic = MetaBlocks.COMPRESSED.get(Materials.Plastic);
        if(MTConfig.multis.teslaTower.useLargeStructure) {
            builder.aisle( "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "#############")
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
                    .aisle("#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "####GGGGG####", "#############");
        } else {
            builder.aisle( "#######", "#######", "#######", "#######", "#######", "#######", "###G###")
                    .aisle("###B###", "#######", "#######", "#######", "#######", "#######", "#GGGGG#")
                    .aisle("##BBB##", "#######", "###C###", "###C###", "###C###", "#######", "#G#G#G#")
                    .aisle("#BBBBB#", "###P###", "##CPC##", "##CPC##", "##CPC##", "###P###", "GGGPGGG")
                    .aisle("##IBB##", "#######", "###C###", "###C###", "###C###", "#######", "#G#G#G#")
                    .aisle("###S###", "#######", "#######", "#######", "#######", "#######", "#GGGGG#")
                    .aisle("#######", "#######", "#######", "#######", "#######", "#######", "###G###");
        }
        builder.where('S', MTTileEntities.TESLA_TOWER, EnumFacing.SOUTH)
                .where('I', MetaTileEntities.ENERGY_INPUT_HATCH[4], EnumFacing.SOUTH)
                .where('B', GAMetaBlocks.METAL_CASING.get(Materials.Titanium).getDefaultState())
                .where('P', plastic.getStateFromMeta(13))
                .where('#', Blocks.AIR.getDefaultState());
        for(BlockWireCoil.CoilType coilType : BlockWireCoil.CoilType.values()) {
            shape.add(builder.where('C', MetaBlocks.WIRE_COIL.getState(coilType))
                    .where('G', TorusBlock.getTorusBlocks().get(0).getBlock())
                    .build());
        }
        for(GAHeatingCoil.CoilType coilType : GAHeatingCoil.CoilType.values()) {
            shape.add(builder.where('C', GAMetaBlocks.HEATING_COIL.getState(coilType))
                    .where('G', TorusBlock.getTorusBlocks().get(0).getBlock())
                    .build());
        }
        for(TorusBlock block : TorusBlock.getTorusBlocks()) {
            shape.add(
                    builder.where('C', MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.CUPRONICKEL))
                            .where('G', block.getBlock())
                            .build());
        }
        return shape;
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("gtadditions.multiblock.tesla_tower.description")};
    }
}
