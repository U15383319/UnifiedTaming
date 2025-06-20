package com.gvxwsur.unified_taming.config.subconfig;

import net.minecraftforge.common.ForgeConfigSpec;

public class CompatibilityConfig {
    public static ForgeConfigSpec.BooleanValue COMPATIBLE_VANILLA_TAMABLE_TAMING;
    public static ForgeConfigSpec.BooleanValue COMPATIBLE_VANILLA_TAMABLE_MOVING_GOALS;
    public static ForgeConfigSpec.BooleanValue COMPATIBLE_PART_ENTITY;
    public static ForgeConfigSpec.BooleanValue COMPATIBLE_MOB_SUMMONED_TAMED;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("compatibility");
        builder.comment("Whether vanilla tamable mobs will use taming system of this mod")
                .translation(getLang("compatible_vanilla_tamable_taming"));
        COMPATIBLE_VANILLA_TAMABLE_TAMING = builder.define("CompatibleVanillaTamableTaming", true);
        builder.comment("Whether vanilla tamable mobs will use moving goal system of this mod")
                .translation(getLang("compatible_vanilla_tamable_moving_goals"));
        COMPATIBLE_VANILLA_TAMABLE_MOVING_GOALS = builder.define("CompatibleVanillaTamableMovingGoals", false);
        builder.comment("Whether multipart mobs will use taming system of this mod")
                .translation(getLang("compatible_part_entity"));
        COMPATIBLE_PART_ENTITY = builder.define("CompatiblePartEntity", true);
        builder.comment("Whether a mob summoned by another mob will be tamed by the nearest mob")
                .translation(getLang("compatible_mob_summoned_tamed"));
        COMPATIBLE_MOB_SUMMONED_TAMED = builder.define("CompatibleMobSummonedTamed", true);
        builder.pop();
    }

    private static String getLang(String cfg) {
        return getLang(cfg, false);
    }

    private static String getLang(String cfg, boolean tooltip) {
        return "config.unified_taming.compatibility" + ((cfg == null) ? "" : ("." + cfg)) + (tooltip ? ".tooltip" : "");
    }
}
