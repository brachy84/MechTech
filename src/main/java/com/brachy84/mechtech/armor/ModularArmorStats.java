package com.brachy84.mechtech.armor;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import gregtech.api.items.metaitem.stats.IItemMaxStackSizeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ModularArmorStats implements IItemMaxStackSizeProvider, IItemCapabilityProvider {
    @Override
    public ICapabilityProvider createProvider(ItemStack itemStack) {
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return getCapability(capability, facing) != null;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                if(capability == GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM) {
                    return GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM.cast(createElectricItem(itemStack));
                }
                return null;
            }
        };
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack, int i) {
        return 1;
    }

    private static IElectricItem createElectricItem(ItemStack stack) {
        return new IElectricItem() {
            private List<BiConsumer<ItemStack, Long>> listeners = new ArrayList<>();

            @Override
            public boolean canProvideChargeExternally() {
                return false;
            }

            @Override
            public boolean chargeable() {
                return true;
            }

            private void triggerListeners(long charge) {
                listeners.forEach(listener -> listener.accept(stack, charge));
            }

            @Override
            public void addChargeListener(BiConsumer<ItemStack, Long> biConsumer) {
                listeners.add(biConsumer);
            }

            @Override
            public long charge(long amount, int tier, boolean ignoreLimit, boolean simulate) {
                if(amount == 0 || tier < getTier())
                    return 0;
                long filled = ModularArmor.fill(stack, amount, tier, simulate);
                if(!simulate && filled > 0)
                    triggerListeners(getCharge());
                return filled;
            }

            @Override
            public long discharge(long amount, int tier, boolean ignoreLimit, boolean externally, boolean simulate) {
                if(amount == 0 ||externally || tier < getTier())
                    return 0;
                long drained = ModularArmor.drain(stack, amount, tier, simulate);
                if(!simulate && drained > 0)
                    triggerListeners(getCharge());
                return drained;
            }

            @Override
            public long getTransferLimit() {
                return GTValues.V[getTier()];
            }

            @Override
            public long getMaxCharge() {
                return ModularArmor.getCapacity(stack);
            }

            @Override
            public long getCharge() {
                return ModularArmor.getEnergy(stack);
            }

            @Override
            public int getTier() {
                ModularArmor armor = ModularArmor.get(stack);
                return armor == null ? GTValues.MAX : armor.getTier();
            }
        };
    }
}
