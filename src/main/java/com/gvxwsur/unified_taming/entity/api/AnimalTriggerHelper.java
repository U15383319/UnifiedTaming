package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public interface AnimalTriggerHelper {
    public default void unified_taming$TameAnimal$trigger(ServerPlayer p_68830_, Mob p_68831_) { }

    public default void unified_taming$BredAnimals$trigger(ServerPlayer p_147279_, Mob p_147280_, Player p_147281_, Mob p_147282_) { }
}
