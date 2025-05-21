package com.gvxwsur.unified_taming.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class UnifiedTamingConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.IntValue showTamableMessage;
    public static final ForgeConfigSpec.DoubleValue rideSpeedModifier;
    public static final ForgeConfigSpec.BooleanValue canRiderInteract;
    public static final ForgeConfigSpec.BooleanValue playerFriendlyFire;
    public static final ForgeConfigSpec.BooleanValue leashedNeedTamed;
    public static final ForgeConfigSpec.BooleanValue golemCreatedTamed;
    public static final ForgeConfigSpec.IntValue merchantTamedReputation;

    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamableTaming;
    public static final ForgeConfigSpec.BooleanValue compatibleVanillaTamableMovingGoals;
    public static final ForgeConfigSpec.BooleanValue compatiblePartEntity;
    public static final ForgeConfigSpec.BooleanValue compatibleMobSummonedTamed;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Tamable Tool Config");
        builder.push("General Settings");
        showTamableMessage = builder.comment("Whether some of the messages should be sent to players when something happened to their pets, 0 means should not send messages, 1 means should send important messages, 2 means should always send messages").defineInRange("showImportantTamableMessage", 2, 0, 2);
        rideSpeedModifier = builder.comment("The speed modifier of the ride").defineInRange("rideSpeedModifier", 0.318, 0.0, 1.0);
        canRiderInteract = builder.comment("Whether the rider can interact with the mob").define("canRiderInteract", false);
        playerFriendlyFire = builder.comment("Whether players can attack their own pets").define("playerFriendlyFire", false);
        leashedNeedTamed = builder.comment("Whether leash mobs need to be tamed").define("leashedNeedTamed", true);
        golemCreatedTamed = builder.comment("Whether golem will be tamed by the nearest player when created").define("golemCreatedTamed", true);
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
