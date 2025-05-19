package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.entity.api.AnimalTriggerHelper;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(BredAnimalsTrigger.class)
public abstract class BredAnimalsTriggerMixin extends SimpleCriterionTrigger<BredAnimalsTrigger.TriggerInstance> implements AnimalTriggerHelper {
    @Unique
    public void unified_taming$BredAnimals$trigger(ServerPlayer p_147279_, Mob p_147280_, Player p_147281_, @Nullable Mob p_147282_) {
        LootContext $$4 = EntityPredicate.createContext(p_147279_, p_147280_);
        LootContext $$5 = EntityPredicate.createContext(p_147279_, p_147281_);
        LootContext $$6 = p_147282_ != null ? EntityPredicate.createContext(p_147279_, p_147282_) : null;
        this.trigger(p_147279_, (p_18653_) -> {
            return p_18653_.matches($$4, $$5, $$6);
        });
    }
}
