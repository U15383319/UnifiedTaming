package com.gvxwsur.unified_taming.config.subconfig;

import net.minecraftforge.common.ForgeConfigSpec;

public class MiscConfig {
    public static ForgeConfigSpec.IntValue showTamableMessage;
    public static ForgeConfigSpec.DoubleValue rideSpeedModifier;
    public static ForgeConfigSpec.BooleanValue canRiderInteract;
    public static ForgeConfigSpec.BooleanValue playerFriendlyFire;
    public static ForgeConfigSpec.BooleanValue leashedNeedTamed;
    public static ForgeConfigSpec.BooleanValue golemCreatedTamed;
    public static ForgeConfigSpec.IntValue merchantTamedReputation;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("misc");
        builder.comment("Whether some of the messages should be sent to players when something happened to their pets, 0 means should not send messages, 1 means should send important messages, 2 means should always send messages");
        showTamableMessage = builder.defineInRange("showImportantTamableMessage", 2, 0, 2);
        builder.comment("The speed modifier of the ride");
        rideSpeedModifier = builder.defineInRange("rideSpeedModifier", 0.318, 0.0, 1.0);
        builder.comment("Whether the rider can interact with the mob");
        canRiderInteract = builder.define("canRiderInteract", false);
        builder.comment("Whether players can attack their own pets");
        playerFriendlyFire = builder.define("playerFriendlyFire", false);
        builder.comment("Whether leash mobs need to be tamed");
        leashedNeedTamed = builder.define("leashedNeedTamed", true);
        builder.comment("Whether golem will be tamed by the nearest player when created");
        golemCreatedTamed = builder.define("golemCreatedTamed", true);
        builder.comment("The separately counted reputation value of the merchant tamed by player");
        merchantTamedReputation = builder.defineInRange("merchantTamedReputation", 90, 0, 1000);
        builder.pop();
    }
}
