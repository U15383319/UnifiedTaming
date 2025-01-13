package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import java.util.Locale;

public class MessageSender {

    private static boolean checkSendCondition(Mob mob, boolean pActionBar) {
        if (!TamableToolConfig.showImportantTamableMessage.get() && !pActionBar) {
            return false;
        }
        LivingEntity owner = TamableToolUtils.getOwner(mob);
        return owner != null;
    }

    public static void sendTamingMessage(Mob mob, Player player, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        player.displayClientMessage(Component.translatable("tamabletool.tame", mob.getDisplayName()), pActionBar);
    }

    public static void sendDeathMessage(Mob mob, Component deathMessage, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            if (mob.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                player.displayClientMessage(deathMessage, pActionBar);
            }
        }
    }

    public static void sendCommandMessage(Mob mob, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            String command = ((CommandEntity) mob).tamabletool$getCommand().toString().toLowerCase(Locale.ROOT);
            player.displayClientMessage(Component.translatable("tamabletool.command." + command, mob.getDisplayName()), pActionBar);
        }
    }

    public static void sendRideModeSwitchMessage(Mob mob, boolean manual, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.ride." + (manual ? "manual" : "automatic"), mob.getDisplayName()), pActionBar);
        }
    }

    public static void sendConvertingMessage(Mob mob, Mob outcomeMob, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.convert", mob.getDisplayName(), outcomeMob.getDisplayName()), pActionBar);
        }
    }

    public static void sendHurtWhenStopMessage(Mob mob, boolean pActionBar) {
        if (!checkSendCondition(mob, pActionBar)) {
            return;
        }
        if (((TamableEntity) mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.hurt.follow", mob.getDisplayName()), pActionBar);
        }
    }
}
