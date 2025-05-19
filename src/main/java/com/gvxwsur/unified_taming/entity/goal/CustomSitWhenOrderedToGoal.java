package com.gvxwsur.unified_taming.entity.goal;

import com.gvxwsur.unified_taming.config.UnifiedTamingConfig;
import com.gvxwsur.unified_taming.entity.api.CommandEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CustomSitWhenOrderedToGoal extends Goal {
    private final Mob mob;
    private final TamableEntity tamableHelper;
    private final CommandEntity commandHelper;
    private final boolean canHighFly;

    public CustomSitWhenOrderedToGoal(Mob p_25898_) {
        this.mob = p_25898_;
        this.tamableHelper = (TamableEntity) p_25898_;
        this.commandHelper = (CommandEntity) p_25898_;
        this.canHighFly = p_25898_ instanceof FlyingMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.commandHelper.unified_taming$isOrderedToSit();
    }

    public boolean canUse() {
        if (!UnifiedTamingUtils.isTame(mob)) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.canHighFly && !this.mob.onGround()) {
            return false;
        } else {
            if (this.mob instanceof TamableAnimal && !UnifiedTamingConfig.compatibleVanillaTamableMovingGoals.get()) {
                return false;
            }
            LivingEntity $$0 = this.tamableHelper.getOwner();
            if ($$0 == null) {
                return true;
            } else {
                return this.adjustedDistanceToSqr($$0) < 144.0 && $$0.getLastHurtByMob() != null ? false : this.commandHelper.unified_taming$isOrderedToSit();
            }
        }
    }

    private double adjustedDistanceToSqr(LivingEntity p_25310_) {
        return !this.canHighFly ? this.mob.distanceToSqr(p_25310_) : this.mob.distanceToSqr(p_25310_.getX(), this.mob.getY(), p_25310_.getZ());
    }

    public void start() {
        this.mob.getNavigation().stop();
    }

    public void stop() {
    }
}
