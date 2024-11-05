package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<String> modAssitItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTamingItem;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Default Tamable Settings").push("General");
        modAssitItem = builder.define("modAssitItem", "minecraft:clock");
        cheatTamingItem = builder.define("cheatTamingItem", "minecraft:debug_stick");
        builder.pop();
        CFG = builder.build();
    }
}
