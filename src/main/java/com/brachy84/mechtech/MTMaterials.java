package com.brachy84.mechtech;

import gregtech.api.unification.material.IMaterialHandler;
import static gregicadditions.GAMaterials.*;

@IMaterialHandler.RegisterMaterialHandler
public class MTMaterials implements IMaterialHandler {
    @Override
    public void onMaterialsInit() {
        TungstenTitaniumCarbide.addFlag(GENERATE_METAL_CASING);
    }
}
