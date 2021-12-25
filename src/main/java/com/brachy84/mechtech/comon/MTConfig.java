package com.brachy84.mechtech.comon;

import com.brachy84.mechtech.MechTech;
import net.minecraftforge.common.config.Config;

@Config(modid = MechTech.MODID)
public class MTConfig {

    public static ModularArmor modularArmor = new ModularArmor();

    public static class ModularArmor {
        @Config.Comment("Amount of module slots of the helmet")
        @Config.Name("Helmet slots")
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 12)
        public int helmetSlots = 3;

        @Config.Comment("Amount of module slots of the chest plate")
        @Config.Name("Chest plate slots")
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 12)
        public int chestPlateSlots = 5;

        @Config.Comment("Amount of module slots of the leggings")
        @Config.Name("Leggings slots")
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 12)
        public int leggingsSlots = 4;

        @Config.Comment("Amount of module slots of the boots")
        @Config.Name("Boots slots")
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 12)
        public int bootsSlot = 2;
    }
}
