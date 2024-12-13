package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableToolConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<Boolean> needModAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> modAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTameItem;
    public static final ForgeConfigSpec.BooleanValue showTamableMessage;
    public static final ForgeConfigSpec.BooleanValue playerFriendlyFire;
    public static final ForgeConfigSpec.BooleanValue leashedNeedTamed;
    public static final ForgeConfigSpec.BooleanValue golemCreatedTamed;
    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamable;
    public static final ForgeConfigSpec.BooleanValue compatiblePartEntity;
    public static final ForgeConfigSpec.BooleanValue compatibleAnimalLeashed;
    public static final ForgeConfigSpec.BooleanValue compatibleMobSummonedTamed;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Tamable Tool Config");
        builder.push("General Settings");
        needModAssistItem = builder.comment("Whether an item should be held in the player's assisting hand to perform interactions in this mod").define("needModAssistItem", false);
        modAssistItem = builder.comment("The item that held in the player's assisting hand to perform interactions in this mod").define("modAssistItem", "minecraft:clock");
        cheatTameItem = builder.comment("The item that used to tame mobs without any cost").define("cheatTameItem", "minecraft:debug_stick");
        showTamableMessage = builder.comment("Whether messages should be sent to players when something happened to their pets").define("showTamableMessage", true);
        playerFriendlyFire = builder.comment("Whether players can attack their own pets").define("playerFriendlyFire", false);
        leashedNeedTamed = builder.comment("Whether leash mobs need to be tamed").define("leashedNeedTamed", true);
        golemCreatedTamed = builder.comment("Whether golem will be tamed by the nearest player when created").define("golemCreatedTamed", true);
        builder.pop();
        builder.push("Tamable Permission");
        builder.comment("Some of these configs are dangerous and increase the risk of mods conflicts, please use them with caution");
        compatibleVanillaTamable = builder.comment("Whether vanilla tamable mobs will use taming system of this mod").define("compatibleVanillaTamable", false);
        compatiblePartEntity = builder.comment("Whether multipart mobs will use taming system of this mod").define("compatiblePartEntity", true);
        compatibleAnimalLeashed = builder.comment("Whether animal will use leashed system of this mod").define("compatibleAnimalLeashed", true);
        compatibleMobSummonedTamed = builder.comment("Whether a mob summoned by another mob will be tamed by the nearest mob").define("compatibleMobSummonedTamed", true);
        builder.pop();
        CFG = builder.build();
    }
}
