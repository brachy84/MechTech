package com.brachy84.mechtech.jei.multis;

import gregicadditions.item.GAMetaBlocks;
import gregicadditions.item.fusion.GACryostatCasing;
import gregicadditions.item.fusion.GADivertorCasing;
import gregicadditions.item.fusion.GAFusionCasing;
import gregicadditions.item.fusion.GAVacuumCasing;
import gregicadditions.jei.FusionReactor4Info;
import gregicadditions.machines.GATileEntities;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class TokamakInfo extends FusionReactor4Info {

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapes = new ArrayList<>();
        MultiblockShapeInfo.Builder shapeInfo = MultiblockShapeInfo.builder()
                .aisle("#####################", "#####################", "#####################", "#####################", "#####################", "#######cccCccc#######", "#####################", "#####################", "#####################", "#####################", "#####################")
                .aisle("#####################", "#####################", "#####################", "#######cccCccc#######", "#######cccCccc#######", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#######cccCccc#######", "#####################", "#####################", "#####################")
                .aisle("#####################", "#####################", "#######EEECEEE#######", "#####ccVVVVVVVcc#####", "#####ccVVVVVVVcc#####", "####cVV#######VVc####", "#####ccVVVVVVVcc#####", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#####################", "#####################")
                .aisle("#####################", "#######cccCccc#######", "#####EEVVVVVVVEE#####", "###CcVV#######VVcC###", "###CcVV#######VVcC###", "###CV###########VC###", "###CcVV#######VVcC###", "###CcVV#######VVcC###", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#####################")
                .aisle("##########C##########", "####CccDDDDDDDccC####", "####CVV#######VVC####", "###cV###########Vc###", "###cV###########Vc###", "##cV#############Vc##", "###cV###########Vc###", "###cV###########Vc###", "####CVV#######VVC####", "####CccBBBBBBBccC####", "##########C##########")
                .aisle("#####C####C####C#####", "####cDDDDDDDDDDDc####", "###EV###########VE###", "##cV#############Vc##", "##cV#############Vc##", "#cV###############Vc#", "##cV#############Vc##", "##cV#############Vc##", "###cV###########Vc###", "####cBBBBBBBBBBBc####", "#####C####C####C#####")
                .aisle("######C#######C######", "####cDDD##C##DDDc####", "###cV###VVVVV###Vc###", "##cV#############Vc##", "##cV#############Vc##", "#cV###############Vc#", "##cV#############Vc##", "##cV#############Vc##", "###cV###VVVVV###Vc###", "####cBBB##C##BBBc####", "######C#######C######")
                .aisle("#####################", "###cDDDC#####CDDDc###", "##cV###V#####V###Vc##", "#cV#####VVVVV#####Vc#", "#cV######VVV######Vc#", "cV#######VVV#######Vc", "#cV######VVV######Vc#", "#cV#####VVVVV#####Vc#", "##cV###V#####V###Vc##", "###cBBBC#####CBBBc###", "#####################")
                .aisle("#####################", "###cDD#########DDc###", "##cV##V#C###C#V##Vc##", "#cV####VC###CV####Vc#", "#cV#####V###V#####Vc#", "cV######V###V######Vc", "#cV#####V###V#####Vc#", "#cV####VC###CV####Vc#", "##cV##V#C###C#V##Vc##", "###cBB#########BBc###", "#####################")
                .aisle("#####################", "###cDD#########DDc###", "##cV##V###X###V##Vc##", "#cV####V#XCX#V####Vc#", "#cV####V#CCC#V####Vc#", "cV#####V#CCC#V#####Vc", "#cV####V#CCC#V####Vc#", "#cV####V#XCX#V####Vc#", "##cV##V###X###V##Vc##", "###cBB#########BBc###", "#####################")
                .aisle("####CC#########CC####", "###CDDC###X###CDDC###", "##CV##V##XCX##V##VC##", "#CV####V#CCC#V####VC#", "#CV####V#CCC#V####VC#", "CV#####V#CCC#V#####VC", "#CV####V#CCC#V####VC#", "#CV####V#CCC#V####VC#", "##CV##V##XCX##V##VC##", "###CBBC###X###CBBC###", "####CC#########CC####")
                .aisle("#####################", "###cDD#########DDc###", "##cV##V###X###V##Vc##", "#cV####V#XCX#V####Vc#", "#cV####V#CCC#V####Vc#", "cV#####V#CCC#V#####Vc", "#cV####V#CCC#V####Vc#", "#cV####V#XCX#V####Vc#", "##cV##V###X###V##Vc##", "###cBB#########BBc###", "#####################")
                .aisle("#####################", "###cDD#########DDc###", "##cV##V#C###C#V##Vc##", "#cV####VC###CV####Vc#", "#cV#####V###V#####Vc#", "cV######V###V######Vc", "#cV#####V###V#####Vc#", "#cV####VC###CV####Vc#", "##cV##V#C###C#V##Vc##", "###cBB#########BBc###", "#####################")
                .aisle("#####################", "###cDDDC#####CDDDc###", "##cV###V#####V###Vc##", "#cV#####VVVVV#####Vc#", "#cV######VVV######Vc#", "cV#######VVV#######Vc", "#cV######VVV######Vc#", "#cV#####VVVVV#####Vc#", "##cV###V#####V###Vc##", "###cBBBC#####CBBBc###", "#####################")
                .aisle("######C#######C######", "####cDDD##C##DDDc####", "###cV###VVVVV###Vc###", "##cV#############Vc##", "##cV#############Vc##", "#cV###############Vc#", "##cV#############Vc##", "##cV#############Vc##", "###cV###VVVVV###Vc###", "####cBBB##C##BBBc####", "######C#######C######")
                .aisle("#####C####C####C#####", "####cDDDDDDDDDDDc####", "###cV###########Vc###", "##cV#############Vc##", "##cV#############Vc##", "#cV###############Vc#", "##cV#############Vc##", "##cV#############Vc##", "###cV###########Vc###", "####cBBBBBBBBBBBc####", "#####C####C####C#####")
                .aisle("##########C##########", "####CccDDDDDDDccC####", "####CVV#######VVC####", "###cV###########Vc###", "###cV###########Vc###", "##cV#############Vc##", "###cV###########Vc###", "###cV###########Vc###", "####CVV#######VVC####", "####CccBBBBBBBccC####", "##########C##########")
                .aisle("#####################", "#######cccCccc#######", "#####ccVVVVVVVcc#####", "###CcVV#######VVcC###", "###CcVV#######VVcC###", "###CV###########VC###", "###CcVV#######VVcC###", "###CcVV#######VVcC###", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#####################")
                .aisle("#####################", "#####################", "#######cccCccc#######", "#####ccVVVVVVVcc#####", "#####ccVVVVVVVcc#####", "####cVV#######VVc####", "#####ccVVVVVVVcc#####", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#####################", "#####################")
                .aisle("#####################", "#####################", "#####################", "#######cccCccc#######", "#######ffcCcFF#######", "#####ccVVVVVVVcc#####", "#######cccCccc#######", "#######cccCccc#######", "#####################", "#####################", "#####################")
                .aisle("#####################", "#####################", "#####################", "#####################", "###########S#########", "#######cccCccc#######", "#####################", "#####################", "#####################", "#####################", "#####################")
                .where('S', GATileEntities.ADVANCED_FUSION_REACTOR, EnumFacing.SOUTH)
                .where('#', Blocks.AIR.getDefaultState())
                .where('X', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_CASING))
                .where('f', MetaTileEntities.FLUID_IMPORT_HATCH[8], EnumFacing.SOUTH)
                .where('F', MetaTileEntities.FLUID_EXPORT_HATCH[8], EnumFacing.SOUTH)
                .where('E', GATileEntities.ENERGY_INPUT[0], EnumFacing.NORTH)
                .where('B', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.FUSION_BLANKET));

        shapes.add(shapeInfo
                .where('C', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_COIL_1))
                .where('c', GAMetaBlocks.CRYOSTAT_CASING.getState(GACryostatCasing.CasingType.CRYOSTAT_1))
                .where('V', GAMetaBlocks.VACUUM_CASING.getState(GAVacuumCasing.CasingType.VACUUM_1))
                .where('D', GAMetaBlocks.DIVERTOR_CASING.getState(GADivertorCasing.CasingType.DIVERTOR_1)).build());

        shapes.add(shapeInfo
                .where('C', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_COIL_2))
                .where('c', GAMetaBlocks.CRYOSTAT_CASING.getState(GACryostatCasing.CasingType.CRYOSTAT_2))
                .where('V', GAMetaBlocks.VACUUM_CASING.getState(GAVacuumCasing.CasingType.VACUUM_2))
                .where('D', GAMetaBlocks.DIVERTOR_CASING.getState(GADivertorCasing.CasingType.DIVERTOR_2)).build());

        shapes.add(shapeInfo
                .where('C', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_COIL_3))
                .where('c', GAMetaBlocks.CRYOSTAT_CASING.getState(GACryostatCasing.CasingType.CRYOSTAT_3))
                .where('V', GAMetaBlocks.VACUUM_CASING.getState(GAVacuumCasing.CasingType.VACUUM_3))
                .where('D', GAMetaBlocks.DIVERTOR_CASING.getState(GADivertorCasing.CasingType.DIVERTOR_3)).build());

        shapes.add(shapeInfo
                .where('C', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_COIL_4))
                .where('c', GAMetaBlocks.CRYOSTAT_CASING.getState(GACryostatCasing.CasingType.CRYOSTAT_4))
                .where('V', GAMetaBlocks.VACUUM_CASING.getState(GAVacuumCasing.CasingType.VACUUM_4))
                .where('D', GAMetaBlocks.DIVERTOR_CASING.getState(GADivertorCasing.CasingType.DIVERTOR_4)).build());

        shapes.add(shapeInfo
                .where('C', GAMetaBlocks.FUSION_CASING.getState(GAFusionCasing.CasingType.ADV_FUSION_COIL_5))
                .where('c', GAMetaBlocks.CRYOSTAT_CASING.getState(GACryostatCasing.CasingType.CRYOSTAT_5))
                .where('V', GAMetaBlocks.VACUUM_CASING.getState(GAVacuumCasing.CasingType.VACUUM_5))
                .where('D', GAMetaBlocks.DIVERTOR_CASING.getState(GADivertorCasing.CasingType.DIVERTOR_5)).build());
        return shapes;
    }
}
