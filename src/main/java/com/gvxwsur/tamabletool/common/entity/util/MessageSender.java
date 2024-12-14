package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.MinionEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageSender {
    private static final Map<UUID, Boolean> quietMap = new HashMap<>();

    public static void setQuiet(boolean quiet, Mob mob) {
        LivingEntity owner = TamableToolUtils.getOwner(mob);
        if (owner != null) {
            quietMap.put(owner.getUUID(), quiet);
        }
    }

    private static boolean checkSendCondition(Mob mob) {
        if (!TamableToolConfig.showTamableMessage.get()) {
            return false;
        }
        LivingEntity owner = TamableToolUtils.getOwner(mob);
        return owner != null && !quietMap.getOrDefault(owner.getUUID(), false);
    }

    // taming message will not be sent automatically
    public static void sendTamingMessage(Mob mob, Player player) {
        if (!checkSendCondition(mob)) {
            return;
        }
        player.displayClientMessage(Component.translatable("tamabletool.tame", mob.getDisplayName()), true);
    }

    public static void sendDeathMessage(Mob mob, Component deathMessage) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            if (mob.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                if (!TamableToolConfig.compatibleVanillaTamable.get() && mob instanceof TamableAnimal) {
                    return;
                }
                player.displayClientMessage(deathMessage, false);
            }
        }
    }

    public static void sendCommandMessage(Mob mob, String command) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.command." + command, mob.getDisplayName()), true);
        }
    }

    public static void sendRideModeSwitchMessage(Mob mob, boolean manual) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.ride." + (manual ? "manual" : "automatic"), mob.getDisplayName()), true);
        }
    }

    public static void sendConvertingMessage(Mob mob, Mob outcomeMob) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.convert", mob.getDisplayName(), outcomeMob.getDisplayName()), false);
        }
    }
}
