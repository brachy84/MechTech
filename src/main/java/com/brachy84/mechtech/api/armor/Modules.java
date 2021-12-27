package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import gregtech.common.items.MetaItems;
import org.apache.logging.log4j.Level;

import java.rmi.AlreadyBoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Modules {

    private static final BiMap<Integer, IModule> REGISTRY = HashBiMap.create(2000);
    private static final Map<Integer, MaterialArmorModuleBuilder> ARMOR_MODULES = new HashMap<>();

    public static Iterable<IModule> getRegisteredModules() {
        return REGISTRY.values();
    }

    public static Map<Integer, MaterialArmorModuleBuilder> getArmorModules() {
        return Collections.unmodifiableMap(ARMOR_MODULES);
    }

    public static final IModule nightVision = new NightVision();
    public static final IModule solarGen1 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_LV.getStackForm(), GTValues.V[1], 1);
    public static final IModule solarGen2 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_MV.getStackForm(), GTValues.V[2], 2);
    public static final IModule solarGen3 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_HV.getStackForm(), GTValues.V[3], 3);
    public static final IModule JETPACK = new JetpackModule();
    public static final IModule ADVANCED_JETPACK = new AdvancedJetpack();
    public static final IModule SHOCK_ABSORBER = new ShockAbsorber();

    static {
        registerModule(0, nightVision);
        registerModule(1, solarGen1);
        registerModule(2, solarGen2);
        registerModule(3, solarGen3);
        registerModule(4, JETPACK);
        registerModule(5, ADVANCED_JETPACK);
        registerModule(6, SHOCK_ABSORBER);

        materialArmorBuilder(1000, Materials.Aluminium)
                .armor(3.4)
                .registerModule();
        materialArmorBuilder(1001, Materials.Copper)
                .armor(3.6)
                .registerModule();
        materialArmorBuilder(1002, Materials.Bronze)
                .armor(3.6)
                .registerModule();
        materialArmorBuilder(1003, Materials.Iron)
                .armor(3.75) // Full vanilla has 15 Armor -> 15 / 4 = 3.75
                .registerModule();
        materialArmorBuilder(1004, Materials.Cobalt)
                .armor(4, 0.1)
                .registerModule();
        materialArmorBuilder(2000, Materials.Neutronium)
                .armor(15, 10)
                .specialArmor(((entity, modularArmorPiece, moduleData, source, damage, slot) -> new AbsorbResult(0.8, 100)))
                .registerModule();
    }

    public static void registerModule(int id, IModule module) {
        if (REGISTRY.containsKey(id)) {
            GTLog.logger.throwing(Level.ERROR, new AlreadyBoundException("Can't register module with id " + id + " as it already exists"));
            return;
        }
        REGISTRY.put(id, module);
    }

    public static MaterialArmorModuleBuilder materialArmorBuilder(int id, Material material) {
        MaterialArmorModuleBuilder builder = new MaterialArmorModuleBuilder(id, material);
        ARMOR_MODULES.put(id, builder);
        return builder;
    }

    public static IModule getModule(int id) {
        return REGISTRY.get(id);
    }

    public static int getModuleId(IModule module) {
        Integer id = REGISTRY.inverse().get(module);
        if (id == null) {
            throw new IllegalStateException("Module " + module.getModuleId() + " is not registered");
        }
        return id;
    }
}
