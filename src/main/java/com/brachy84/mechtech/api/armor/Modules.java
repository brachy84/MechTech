package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Modules {

    private static final BiMap<Integer, IModule> REGISTRY = HashBiMap.create(2000);
    private static final Map<Integer, Material> ARMOR_MODULES = new HashMap<>();

    public static Iterable<IModule> getRegisteredModules() {
        return REGISTRY.values();
    }

    public static Map<Integer, Material> getArmorModules() {
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

        registerMaterialArmorModule(1000, Materials.Aluminium, 3.4, 0.0);
        registerMaterialArmorModule(1001, Materials.Copper, 3.6, 0.0, 150);
        registerMaterialArmorModule(1002, Materials.Bronze, 3.8, 0.0);
        registerMaterialArmorModule(2000, Materials.Neutronium, 20, 10);
    }

    public static void registerModule(int id, IModule module) {
        REGISTRY.put(id, module);
    }

    public static void registerMaterialArmorModule(int id, Material material, double armor, double toughness) {
        if(material.hasProperty(PropertyKey.TOOL)) {
            registerMaterialArmorModule(id, material, armor, toughness, material.getProperty(PropertyKey.TOOL).getToolDurability());
        }
    }

    public static void registerMaterialArmorModule(int id, Material material, double armor, double toughness, int durability) {
        ARMOR_MODULES.put(id, material);
        ProtectionModule module = new ProtectionModule(material, ItemStack.EMPTY, armor, toughness, durability);
        registerModule(id, module);
    }

    public static IModule getModule(int id) {
        return REGISTRY.get(id);
    }

    public static int getModuleId(IModule module) {
        Integer id = REGISTRY.inverse().get(module);
        if(id == null) {
            throw new IllegalStateException("Module " + module.getModuleId() + " is not registered");
        }
        return id;
    }
}
