package com.gvxwsur.unified_taming.config.subconfig;

import net.minecraftforge.common.ForgeConfigSpec;

public class CompatibilityConfig {
    public static ForgeConfigSpec.BooleanValue compatibleVanillaTamableTaming;
    public static ForgeConfigSpec.BooleanValue compatibleVanillaTamableMovingGoals;
    public static ForgeConfigSpec.BooleanValue compatiblePartEntity;
    public static ForgeConfigSpec.BooleanValue compatibleMobSummonedTamed;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("compatibility");
        builder.comment("Whether vanilla tamable mobs will use taming system of this mod");
        compatibleVanillaTamableTaming = builder.define("compatibleVanillaTamableTaming", false);
        builder.comment("Whether vanilla tamable mobs will use moving goal system of this mod");
        compatibleVanillaTamableMovingGoals = builder.define("compatibleVanillaTamableMovingGoals", false);
        builder.comment("Whether multipart mobs will use taming system of this mod");
        compatiblePartEntity = builder.define("compatiblePartEntity", true);
        builder.comment("Whether a mob summoned by another mob will be tamed by the nearest mob");
        compatibleMobSummonedTamed = builder.define("compatibleMobSummonedTamed", true);
        builder.pop();
    }
}
