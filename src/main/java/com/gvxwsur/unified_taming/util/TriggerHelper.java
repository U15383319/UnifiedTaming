package com.gvxwsur.unified_taming.util;

import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nullable;

public class TriggerHelper {

    public static void unified_taming$BredAnimals$trigger(BredAnimalsTrigger bredAnimalsTrigger, ServerPlayer p_147279_, Mob p_147280_, Player p_147281_, @Nullable Mob p_147282_) {
        LootContext $$4 = EntityPredicate.createContext(p_147279_, p_147280_);
        LootContext $$5 = EntityPredicate.createContext(p_147279_, p_147281_);
        LootContext $$6 = p_147282_ != null ? EntityPredicate.createContext(p_147279_, p_147282_) : null;
        bredAnimalsTrigger.trigger(p_147279_, (p_18653_) -> {
            return p_18653_.matches($$4, $$5, $$6);
        });
    }

    public static void unified_taming$TameAnimal$trigger(TameAnimalTrigger tameAnimalTrigger, ServerPlayer p_68830_, Mob p_68831_) {
        LootContext $$2 = EntityPredicate.createContext(p_68830_, p_68831_);
        tameAnimalTrigger.trigger(p_68830_, (p_68838_) -> {
            return p_68838_.matches($$2);
        });
    }
}
