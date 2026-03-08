package com.gvxwsur.unified_taming.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;

public class GoalHelper {
    public static void addGoals(Mob mob) {
        int flt = 65535, sit = 65535, attack = 65535, follow = 65535, restrict = 65535, breed = 65535, lookPlayer = 65535, randomLook = 65535;
        for (WrappedGoal wrappedGoal : mob.goalSelector.getAvailableGoals()) {
            if (wrappedGoal.getGoal() instanceof FloatGoal) {
                flt = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof SitWhenOrderedToGoal) {
                sit = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof MeleeAttackGoal) {
                attack = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof FollowOwnerGoal) {
                follow = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof MoveTowardsRestrictionGoal) {
                restrict = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof BreedGoal) {
                breed = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof LookAtPlayerGoal) {
                lookPlayer = wrappedGoal.getPriority();
            }
            if (wrappedGoal.getGoal() instanceof RandomLookAroundGoal) {
                randomLook = wrappedGoal.getPriority();
            }
        }
        if (sit == 65535) {
            sit = 1;
            if (flt != 65535) {
                sit = flt + 1;
            }
            if (attack != 65535) {
                sit = Math.min(sit, attack - 1);
            }
            mob.goalSelector.addGoal(sit, new CustomSitWhenOrderedToGoal(mob));
        }

        if (follow == 65535) {
            follow = 6;
            if (breed != 65535) {
                follow = Math.min(follow, breed - 1);
            }
            if (lookPlayer != 65535) {
                follow = Math.min(follow, lookPlayer - 1);
            }
            if (randomLook != 65535) {
                follow = Math.min(follow, randomLook - 1);
            }
            if (attack != 65535) {
                follow = Math.max(follow, attack + 1);
            }
            mob.goalSelector.addGoal(follow, new CustomFollowOwnerGoal(mob, 1.0, 10.0F, 2.0F));
        }

        if (restrict == 65535) {
            restrict = follow;
        }
        mob.goalSelector.addGoal(restrict, new CustomMoveTowardsRestrictionGoal(mob, 1.0D));

        if (breed == 65535) {
            breed = follow + 1;
        }
        mob.goalSelector.addGoal(breed, new CustomBreedGoal(mob, 1.0));

        if (lookPlayer == 65535) {
            lookPlayer = 10;
            if (randomLook != 65535) {
                lookPlayer = Math.min(lookPlayer, randomLook - 1);
            }
            lookPlayer = Math.max(lookPlayer, breed + 1);
            mob.goalSelector.addGoal(lookPlayer, new CustomLookAtOwnerGoal(mob, Player.class, 8.0F));
        }

    }

    public static void addTargets(Mob mob) {
        mob.targetSelector.addGoal(0, new CustomOwnerHurtByTargetGoal(mob));
        mob.targetSelector.addGoal(1, new CustomOwnerHurtTargetGoal(mob));
    }
}
