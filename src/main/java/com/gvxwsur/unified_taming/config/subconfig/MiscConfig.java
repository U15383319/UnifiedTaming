package com.gvxwsur.unified_taming.config.subconfig;

import net.minecraftforge.common.ForgeConfigSpec;

public class MiscConfig {
    public static ForgeConfigSpec.IntValue SHOW_TAMABLE_MESSAGE;
    public static ForgeConfigSpec.DoubleValue RIDE_SPEED_MODIFIER;
    public static ForgeConfigSpec.BooleanValue CAN_RIDER_INTERACT;
    public static ForgeConfigSpec.BooleanValue PLAYER_FRIENDLY_FIRE;
    public static ForgeConfigSpec.BooleanValue LEASHED_NEED_TAMED;
    public static ForgeConfigSpec.BooleanValue GOLEM_CREATED_TAMED;
    public static ForgeConfigSpec.IntValue MERCHANT_TAMED_REPUTATION;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("misc");
        builder.comment("Whether some of the messages should be sent to players when something happened to their pets, 0 means should not send messages, 1 means should send important messages, 2 means should always send messages")
                .translation(getLang("show_tamable_message"));
        SHOW_TAMABLE_MESSAGE = builder.defineInRange("ShowTamableMessage", 2, 0, 2);
        builder.comment("The speed modifier of the ride")
                .translation(getLang("ride_speed_modifier"));
        RIDE_SPEED_MODIFIER = builder.defineInRange("RideSpeedModifier", 0.565, 0.0, 1.0);
        builder.comment("Whether the rider can interact with the mob")
                .translation(getLang("can_rider_interact"));
        CAN_RIDER_INTERACT = builder.define("CanRiderInteract", false);
        builder.comment("Whether players can attack their own pets")
                .translation(getLang("player_friendly_fire"));
        PLAYER_FRIENDLY_FIRE = builder.define("PlayerFriendlyFire", false);
        builder.comment("Whether leash mobs need to be tamed")
                .translation(getLang("leashed_need_tamed"));
        LEASHED_NEED_TAMED = builder.define("LeashedNeedTamed", true);
        builder.comment("Whether golem will be tamed by the nearest player when created")
                .translation(getLang("golem_created_tamed"));
        GOLEM_CREATED_TAMED = builder.define("GolemCreatedTamed", true);
        builder.comment("The separately counted reputation value of the merchant tamed by player")
                .translation(getLang("merchant_tamed_reputation"));
        MERCHANT_TAMED_REPUTATION = builder.defineInRange("MerchantTamedReputation", 90, 0, 1000);
        builder.pop();
    }

    private static String getLang(String cfg) {
        return getLang(cfg, false);
    }

    private static String getLang(String cfg, boolean tooltip) {
        return "config.unified_taming.misc" + ((cfg == null) ? "" : ("." + cfg)) + (tooltip ? ".tooltip" : "");
    }
}
