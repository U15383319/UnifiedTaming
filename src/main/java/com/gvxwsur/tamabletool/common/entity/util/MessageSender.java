package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.MinionEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class MessageSender {
    private static boolean quiet = false;

    public static boolean isQuiet() {
        return quiet;
    }

    public static void setQuiet(boolean quiet) {
        MessageSender.quiet = quiet;
    }

    private static boolean checkSendCondition(Mob mob) {
        return !quiet && TamableToolConfig.showTamableMessage.get() && !((MinionEntity)mob).tamabletool$isTameNonPlayer();
    }

    public static void sendTamingMessage(Mob mob, Player player) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (!((MinionEntity)mob).tamabletool$isTameNonPlayer()) {
            player.displayClientMessage(Component.translatable("tamabletool.tame", mob.getDisplayName()), true);
        }
    }

    public static void sendDeathMessage(Mob mob, Component deathMessage) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity)mob).getOwner() instanceof ServerPlayer player) {
            if (mob.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                player.displayClientMessage(deathMessage, false);
            }
        }
    }

    public static void sendCommandMessage(Mob mob, String command) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity)mob).getOwner() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("tamabletool.command." + command, mob.getDisplayName()), true);
        }
    }
}
