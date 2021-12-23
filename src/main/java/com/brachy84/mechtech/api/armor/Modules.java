package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.NightVision;
import com.brachy84.mechtech.api.armor.modules.SolarGen;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gregtech.api.GTValues;
import gregtech.common.items.MetaItems;

public class Modules {

    private static final BiMap<Integer, IArmorModule> REGISTRY = HashBiMap.create(64);
    public static final IArmorModule nightVision = new NightVision();
    public static final IArmorModule solarGen1 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_LV.getStackForm(), GTValues.V[1], 1);
    public static final IArmorModule solarGen2 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_MV.getStackForm(), GTValues.V[2], 2);
    public static final IArmorModule solarGen3 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_HV.getStackForm(), GTValues.V[3], 3);

    static {
        registerModule(0, nightVision);
        registerModule(1, solarGen1);
        registerModule(2, solarGen2);
        registerModule(3, solarGen3);
    }

    public static void registerModule(int id, IArmorModule module) {
        REGISTRY.put(id, module);
    }

    public static IArmorModule getModule(int id) {
        return REGISTRY.get(id);
    }

    public static int getModuleId(IArmorModule module) {
        return REGISTRY.inverse().get(module);
    }
}
