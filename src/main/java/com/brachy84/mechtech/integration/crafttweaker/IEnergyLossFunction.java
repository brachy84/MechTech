package com.brachy84.mechtech.integration.crafttweaker;

import com.brachy84.mechtech.machines.multis.MetaTileEntityTeslaTower;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@FunctionalInterface
@ZenClass("mods.mechtech.IEnergyLossFunction")
@ZenRegister
public interface IEnergyLossFunction {

    /**
     * Use this to calculate the energy loss
     * the tesla tower provide a few properties like voltage, coil height, etc...
     * @param teslaTower the tower to use the function on
     * @param distance the distance from the tower to the current block in blocks
     * @return the fraction of energy lost as decimal
     * 0.2 -> 20% energy lost
     */
    double run(MetaTileEntityTeslaTower teslaTower, double distance);

}
