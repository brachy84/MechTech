package com.brachy84.mechtech.api.armor;

import com.brachy84.mechtech.api.armor.modules.ProtectionModule;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class MaterialArmorModuleBuilder {

    public final int id;
    public final Material material;
    public double armor, toughness;
    public int durability = 0;
    public ISpecialArmorModule specialArmorModule;
    private ProtectionModule module;

    public MaterialArmorModuleBuilder(int id, Material material) {
        this.id = id;
        this.material = material;
        this.armor = 3.75;
        this.toughness = 0;
    }

    public MaterialArmorModuleBuilder armor(double armor) {
        this.armor = armor;
        return this;
    }

    public MaterialArmorModuleBuilder toughness(double toughness) {
        this.toughness = toughness;
        return this;
    }

    public MaterialArmorModuleBuilder armor(double armor, double toughness) {
        this.armor = armor;
        this.toughness = toughness;
        return this;
    }

    public MaterialArmorModuleBuilder durability(int durability) {
        this.durability = durability;
        return this;
    }

    public MaterialArmorModuleBuilder armor(double armor, double toughness, int durability) {
        this.armor = armor;
        this.toughness = toughness;
        this.durability = durability;
        return this;
    }

    public MaterialArmorModuleBuilder specialArmor(ISpecialArmorModule specialArmorModule) {
        this.specialArmorModule = specialArmorModule;
        return this;
    }

    public MaterialArmorModuleBuilder specialArmor(DamageSource source, double absorbtion, int maxAbsorbtion) {
        this.specialArmorModule = ((entity, modularArmorPiece, moduleData, source1, damage, slot) -> {
            if(source == source1)
                return new AbsorbResult(absorbtion, maxAbsorbtion);
            return AbsorbResult.ZERO;
        });
        return this;
    }

    public void registerModule() {
        armor = Math.max(armor, 0);
        toughness = Math.max(toughness, 0);
        if(durability <= 0) {
            if(material.hasProperty(PropertyKey.TOOL)) {
                durability = material.getProperty(PropertyKey.TOOL).getToolDurability();
            } else {
                durability = 128;
            }
        }
        this.module = new ProtectionModule(material, ItemStack.EMPTY, armor, toughness, durability, specialArmorModule);
        Modules.registerModule(id, module);
    }

    public ProtectionModule getModule() {
        return module;
    }

    public boolean isRegistered() {
        return module != null && Modules.getModule(id) == module;
    }
}
