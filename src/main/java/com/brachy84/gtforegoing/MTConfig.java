package com.brachy84.gtforegoing;

import net.minecraftforge.common.config.Config;

@Config(modid = MechTech.MODID)
public class MTConfig {

    public static Multis multis = new Multis();

    public static class Multis {
        public TeslaTower teslaTower = new TeslaTower();
    }

    public static class TeslaTower {
        @Config.Comment("If the multiblock should be loaded")
        @Config.Name("Tesla Tower")
        @Config.RequiresMcRestart
        public boolean enabled = true;

        @Config.Name("Tesla Tower range factor")
        @Config.RangeDouble(min = 0.0)
        @Config.RequiresMcRestart
        public double rangeFactor = 4.0;

        @Config.Comment("If the tower should be able to power machines in other dimensions")
        @Config.RequiresMcRestart
        public boolean allowInterdimensionalTransfer = true;

        @Config.Comment("If the tower should be able to power machines out of range")
        @Config.RequiresMcRestart
        public boolean allowOutOfRangeTransfer = true;

        @Config.Comment("How much qubits should be consumed per pulse (every 10 ticks). So 10 would be 1/tick")
        @Config.Name("Tesla Tower qubit cost")
        @Config.RequiresMcRestart
        public int qubitCost = 10;

        @Config.Comment("How much energy should be voided each pulse in percentage")
        @Config.Name("Tesla Tower energy loss percentage")
        @Config.RangeInt(min = 0, max = 100)
        @Config.RequiresMcRestart
        public int baseEnergyLossPerecentage = 40;
    }
}
