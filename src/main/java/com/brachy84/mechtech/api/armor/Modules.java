package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Modules {

    private static final BiMap<Integer, IArmorModule> REGISTRY = HashBiMap.create(2000);
    private static final Map<Integer, Material> ARMOR_MODULES = new HashMap<>();

    public static Iterable<IArmorModule> getRegisteredModules() {
        return REGISTRY.values();
    }

    public static Map<Integer, Material> getArmorModules() {
        return Collections.unmodifiableMap(ARMOR_MODULES);
    }

    public static final IArmorModule nightVision = new NightVision();
    public static final IArmorModule solarGen1 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_LV.getStackForm(), GTValues.V[1], 1);
    public static final IArmorModule solarGen2 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_MV.getStackForm(), GTValues.V[2], 2);
    public static final IArmorModule solarGen3 = new SolarGen(() -> MetaItems.COVER_SOLAR_PANEL_HV.getStackForm(), GTValues.V[3], 3);
    public static final IArmorModule JETPACK = new JetpackModule();
    public static final IArmorModule ADVANCED_JETPACK = new AdvancedJetpack();
    public static final IArmorModule SHOCK_ABSORBER = new ShockAbsorber();

    static {
        registerModule(0, nightVision);
        registerModule(1, solarGen1);
        registerModule(2, solarGen2);
        registerModule(3, solarGen3);
        registerModule(4, JETPACK);
        registerModule(5, ADVANCED_JETPACK);
        registerModule(6, SHOCK_ABSORBER);

        double MAX_ABSORB = 0.25807; // if the all armor pieces have a module with this absorbtion, the entity will get ~0 damage

        registerMaterialArmorModule(1000, Materials.Aluminium, 1, 0.0, MAX_ABSORB / 6, 256);
        registerMaterialArmorModule(1001, Materials.Polyethylene, 0.02, 0, MAX_ABSORB / 12, 6);
        registerMaterialArmorModule(2000, Materials.Neutronium, 2, 1, MAX_ABSORB, 16384);
    }

    public static void registerModule(int id, IArmorModule module) {
        REGISTRY.put(id, module);
    }

    public static void registerMaterialArmorModule(int id, Material material, double armor, double toughness, double absorbtion, int durability) {
        ARMOR_MODULES.put(id, material);
        ProtectionModule module = new ProtectionModule(material, ItemStack.EMPTY, armor, toughness, absorbtion, durability);
        registerModule(id, module);
    }

    public static IArmorModule getModule(int id) {
        return REGISTRY.get(id);
    }

    public static int getModuleId(IArmorModule module) {
        Integer id = REGISTRY.inverse().get(module);
        if(id == null) {
            throw new IllegalStateException("Module " + module.getModuleId() + " is not registered");
        }
        return id;
    }
}
