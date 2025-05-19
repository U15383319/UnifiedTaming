package com.gvxwsur.tamabletool.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TamableToolConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<Boolean> needModAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> modAssistItem;
    public static final ForgeConfigSpec.ConfigValue<String> cheatTameItem;
    public static final ForgeConfigSpec.BooleanValue showImportantTamableMessage;
    public static final ForgeConfigSpec.DoubleValue rideSpeedModifier;
    public static final ForgeConfigSpec.BooleanValue canRiderInteract;
    public static final ForgeConfigSpec.BooleanValue playerFriendlyFire;
    public static final ForgeConfigSpec.BooleanValue leashedNeedTamed;
    public static final ForgeConfigSpec.BooleanValue golemCreatedTamed;
    public static final ForgeConfigSpec.BooleanValue selfDestructMobNotDead;
    public static final ForgeConfigSpec.IntValue merchantTamedReputation;

    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamableTaming;
    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamableMovingGoals;
    public static final ForgeConfigSpec.BooleanValue compatiblePartEntity;
    public static final ForgeConfigSpec.BooleanValue compatibleMobSummonedTamed;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Tamable Tool Config");
        builder.push("General Settings");
        needModAssistItem = builder.comment("Whether an item should be held in the player's assisting hand to perform interactions in this mod").define("needModAssistItem", false);
        modAssistItem = builder.comment("The item that held in the player's assisting hand to perform interactions in this mod").define("modAssistItem", "minecraft:enchanted_book");
        cheatTameItem = builder.comment("The item that used to tame mobs without any cost").define("cheatTameItem", "minecraft:structure_void");
        showImportantTamableMessage = builder.comment("Whether some important messages should be sent to players when something happened to their pets").define("showImportantTamableMessage", true);
        rideSpeedModifier = builder.comment("The speed modifier of the ride").defineInRange("rideSpeedModifier", 0.318, 0.0, 1.0);
        canRiderInteract = builder.comment("Whether the rider can interact with the mob").define("canRiderInteract", false);
        playerFriendlyFire = builder.comment("Whether players can attack their own pets").define("playerFriendlyFire", false);
        leashedNeedTamed = builder.comment("Whether leash mobs need to be tamed").define("leashedNeedTamed", true);
        golemCreatedTamed = builder.comment("Whether golem will be tamed by the nearest player when created").define("golemCreatedTamed", true);
        selfDestructMobNotDead = builder.comment("Whether the mob will not die when self-destructed").define("selfDestructMobNotDead", false);
        merchantTamedReputation = builder.comment("The separately counted reputation value of the merchant tamed by player").defineInRange("merchantTamedReputation", 90, 0, 1000);
        builder.pop();
        builder.push("Tamable Permission");
        builder.comment("Some of these configs are dangerous and increase the risk of mods conflicts, please use them with caution");
        compatibleVanillaTamableTaming = builder.comment("Whether vanilla tamable mobs will use taming system of this mod").define("compatibleVanillaTamableTaming", false);
        compatibleVanillaTamableMovingGoals = builder.comment("Whether vanilla tamable mobs will use moving goal system of this mod").define("compatibleVanillaTamableMovingGoals", false);
        compatiblePartEntity = builder.comment("Whether multipart mobs will use taming system of this mod").define("compatiblePartEntity", true);
        compatibleMobSummonedTamed = builder.comment("Whether a mob summoned by another mob will be tamed by the nearest mob").define("compatibleMobSummonedTamed", true);
        builder.pop();
        CFG = builder.build();
    }
}
