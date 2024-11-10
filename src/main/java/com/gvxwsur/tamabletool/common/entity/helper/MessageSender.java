package com.gvxwsur.tamabletool.common.entity.helper;

import com.gvxwsur.tamabletool.common.config.TamableConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;

public class MessageSender {
    public static boolean checkSendCondition(Mob mob) {
        return !((MinionEntity)mob).tamabletool$isTameNonPlayer() && TamableConfig.showTamableMessage.get();
    }

    public static void sendDeathMessage(Mob mob) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity)mob).getOwner() instanceof ServerPlayer player) {
            if (mob.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                Component deathMessage = mob.getCombatTracker().getDeathMessage();
                player.sendSystemMessage(deathMessage);
            }
        }
    }

    public static void sendCommandMessage(Mob mob, String command) {
        if (!checkSendCondition(mob)) {
            return;
        }
        if (((TamableEntity)mob).getOwner() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable("tamabletool.command." + command, mob.getDisplayName()));
        }
    }
}
