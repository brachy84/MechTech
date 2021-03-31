package com.brachy84.mechtech;

import net.minecraftforge.common.config.Config;

@Config(modid = MechTech.MODID)
public class MTConfig {

    public static Multis multis = new Multis();

    public static class Multis {
        public TeslaTower teslaTower = new TeslaTower();
        public Tokamak tokamak = new Tokamak();
    }

    public static class TeslaTower {
        @Config.Comment("If the multiblock should be loaded")
        @Config.Name("Tesla Tower")
        @Config.RequiresMcRestart
        public boolean enabled = true;

        @Config.Comment("If this is false, the structure will be replaced with a smaller one")
        public boolean useLargeStructure = true;

        @Config.Name("Tesla Tower base range")
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int baseRange = 8;

        @Config.Comment("How many amps the tower can emit per coil tier")
        @Config.RequiresMcRestart()
        public int ampsPerCoilTier = 2;

        @Config.Comment("If the tower should be able to power machines in other dimensions")
        @Config.RequiresMcRestart
        public boolean allowInterdimensionalTransfer = true;

        @Config.Comment("If the tower should be able to power machines out of range")
        @Config.RequiresMcRestart
        public boolean allowOutOfRangeTransfer = true;

        @Config.Comment("How much qubits should be consumed per tick (for out of range transmission)")
        @Config.Name("Tesla Tower qubit cost")
        @Config.RequiresMcRestart
        public int qubitCost = 1;

        @Config.Comment("The casing material to use for the TeslaTower.")
        @Config.Name("Tesla Tower casing material")
        @Config.RequiresMcRestart
        public String casingMaterial = "titanium";
    }

    public static class Tokamak {
        @Config.Comment("If this is true, Gregicalitys Advanced Fusion Reactor will be replaced with a much larger one")
        @Config.Name("Override Advanced Fusion Rector")
        @Config.RequiresMcRestart
        public boolean enableTokamak = true;

    }
}
