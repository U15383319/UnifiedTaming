package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.entity.api.AnimalTriggerHelper;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TameAnimalTrigger.class)
public abstract class TameAnimalTriggerMixin extends SimpleCriterionTrigger<TameAnimalTrigger.TriggerInstance> implements AnimalTriggerHelper {
    @Unique
    public void unified_taming$TameAnimal$trigger(ServerPlayer p_68830_, Mob p_68831_) {
        LootContext $$2 = EntityPredicate.createContext(p_68830_, p_68831_);
        this.trigger(p_68830_, (p_68838_) -> {
            return p_68838_.matches($$2);
        });
    }
}
