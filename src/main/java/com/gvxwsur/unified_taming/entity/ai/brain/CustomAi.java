package com.gvxwsur.unified_taming.entity.ai.brain;

import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CustomAi {
    public static Brain<?> makeBrain(Mob mob, Brain<Mob> brain) {
        initIdleActivity(mob, brain);
        initFightActivity(mob, brain);
        return brain;
    }

    private static void initIdleActivity(Mob mob, Brain<Mob> brain) {
        for (Map<Activity, Set<BehaviorControl<? super Mob>>> activitySetMap : brain.availableBehaviorsByPriority.values()) {

            Set<BehaviorControl<? super Mob>> idleBehaviorControlSet = activitySetMap.getOrDefault(Activity.IDLE, null);
            if (idleBehaviorControlSet != null) {
                for (BehaviorControl<? super Mob> behaviorControl : idleBehaviorControlSet) {
                    if (behaviorControl.debugString().contains("StartAttacking")) {
                        idleBehaviorControlSet.add(StartAttacking.create(CustomAi::findNearestValidAttackTarget));
                        idleBehaviorControlSet.remove(behaviorControl);
                    }
                }
            }
        }
    }

    private static void initFightActivity(Mob mob, Brain<Mob> brain) {
        for (Map<Activity, Set<BehaviorControl<? super Mob>>> activitySetMap : brain.availableBehaviorsByPriority.values()) {

            Set<BehaviorControl<? super Mob>> fightBehaviorControlSet = activitySetMap.getOrDefault(Activity.FIGHT, null);
            if (fightBehaviorControlSet != null) {
                for (BehaviorControl<? super Mob> behaviorControl : fightBehaviorControlSet) {
                    if (behaviorControl.debugString().contains("StopAttackingIfTargetInvalid")) {
                        fightBehaviorControlSet.add(StopAttackingIfTargetInvalid.create((living) -> !isNearestValidAttackTarget(mob, living)));
                        fightBehaviorControlSet.remove(behaviorControl);
                    }
                }
            }
        }
    }

    private static Optional<LivingEntity> findNearestValidAttackTarget(LivingEntity living) {
        return living.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(mobs -> mobs.findClosest(
                e -> living.canAttack(e) && e.closerThan(living, 12.0 * UnifiedTamingUtils.getScaleFactorBySize((Mob) living)) && wantsToAttack((Mob) living, e)
                ));
    }

    private static boolean isNearestValidAttackTarget(Mob mob, LivingEntity living) {
        return findNearestValidAttackTarget(mob).filter((p_35085_) -> p_35085_ == living).isPresent();
    }

    private static boolean wantsToAttack(Mob mob, LivingEntity target) {
        if (UnifiedTamingUtils.getOwner(mob) instanceof Player player) {

            LivingEntity lastHurtByMob = player.getLastHurtByMob();
            if (target.equals(lastHurtByMob)) {
                return true;
            }

            LivingEntity lastHurtMob = player.getLastHurtMob();
            if (target.equals(lastHurtMob)) {
                return true;
            }
        }

        LivingEntity mobLastHurtByMob = mob.getLastHurtByMob();
        return target.equals(mobLastHurtByMob);
    }
}
