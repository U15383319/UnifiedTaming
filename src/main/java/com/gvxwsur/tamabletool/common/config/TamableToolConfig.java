package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableToolConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<Boolean> needModAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> modAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTameItem;
    public static final ForgeConfigSpec.BooleanValue showTamableMessage;
    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamable;
    public static final ForgeConfigSpec.BooleanValue compatiblePartEntity;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Tamable Tool Config");
        builder.push("General Settings");
        needModAssistItem = builder.define("needModAssistItem", false);
        modAssistItem = builder.define("modAssistItem", "minecraft:clock");
        cheatTameItem = builder.define("cheatTameItem", "minecraft:debug_stick");
        showTamableMessage = builder.define("showTamableMessage", true);
        builder.pop();
        builder.push("Tamable Permission");
        compatibleVanillaTamable = builder.define("compatibleVanillaTamable", false);
        compatiblePartEntity = builder.define("compatiblePartEntity", false);
        builder.pop();
        CFG = builder.build();
    }
}
