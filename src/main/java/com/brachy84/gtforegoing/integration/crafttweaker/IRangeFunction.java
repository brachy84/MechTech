package com.brachy84.gtforegoing.integration.crafttweaker;

import com.brachy84.gtforegoing.machines.multis.MetaTileEntityTeslaTower;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@FunctionalInterface
@ZenClass("mods.gtforegoing.IRangeFunction")
@ZenRegister
public interface IRangeFunction {

    /**
     * This is used to calculate the range
     * The material modifier will be applied later
     * default is Math.pow(coilHeight, 0.6) * 4.0
     * @param teslaTower the tower to calculate the range for
     * @return the range
     */
    @ZenMethod
    double run(MetaTileEntityTeslaTower teslaTower);
}
