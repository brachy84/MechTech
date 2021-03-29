package com.brachy84.gtforegoing.machines;

import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenClass("mods.gregicality.EnergyRelayLoss")
public interface IEnergyRelayLoss {

    /**
     * @param voltage of the relay
     * @param range of the relay
     * @param distance from the relay to the receiver
     * @return rangeFactor: loss of 25% is 0.25
     */
    double get(long voltage, int range, double distance);
}
