package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableToolConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<String> modAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTamingItem;
    public static final ForgeConfigSpec.BooleanValue showTamableMessage;
    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamable;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Default Tamable Tool Settings").push("General");
        modAssistItem = builder.define("modAssistItem", "minecraft:clock");
        cheatTamingItem = builder.define("cheatTamingItem", "minecraft:debug_stick");
        showTamableMessage = builder.define("showTamableMessage", true);
        compatibleVanillaTamable = builder.define("compatibleVanillaTamable", false);
        builder.pop();
        CFG = builder.build();
    }
}
