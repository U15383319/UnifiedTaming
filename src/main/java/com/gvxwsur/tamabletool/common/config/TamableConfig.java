package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<String> modAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTamingItem;
    public static final ForgeConfigSpec.BooleanValue showTamableMessage;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Default Tamable Settings").push("General");
        modAssistItem = builder.define("modAssistItem", "minecraft:clock");
        cheatTamingItem = builder.define("cheatTamingItem", "minecraft:debug_stick");
        showTamableMessage = builder.define("showTamableMessage", true);
        builder.pop();
        CFG = builder.build();
    }
}
