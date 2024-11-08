package com.gvxwsur.tamabletool.common.entity.goal;

import com.gvxwsur.tamabletool.common.entity.helper.MinionEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CustomSitWhenOrderedToGoal extends Goal {
    private final Mob mob;
    private final TamableEntity tamableHelper;
    private final MinionEntity minionHelper;
    private final boolean canHighFly;

    public CustomSitWhenOrderedToGoal(Mob p_25898_) {
        this.mob = p_25898_;
        this.tamableHelper = (TamableEntity) p_25898_;
        this.minionHelper = (MinionEntity) p_25898_;
        this.canHighFly = p_25898_ instanceof FlyingMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.tamableHelper.tamabletool$isOrderedToSit();
    }

    public boolean canUse() {
        if (!this.tamableHelper.tamabletool$isTame()) {
            return false;
        } else if (this.minionHelper.tamabletool$isTameNonPlayer()) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.canHighFly && !this.mob.onGround()) {
            return false;
        } else {
            LivingEntity $$0 = this.tamableHelper.getOwner();
            if ($$0 == null) {
                return true;
            } else {
                return this.adjustedDistanceToSqr($$0) < 144.0 && $$0.getLastHurtByMob() != null ? false : this.tamableHelper.tamabletool$isOrderedToSit();
            }
        }
    }

    private double adjustedDistanceToSqr(LivingEntity p_25310_) {
        return !this.canHighFly ? this.mob.distanceToSqr(p_25310_) : this.mob.distanceToSqr(p_25310_.getX(), this.mob.getY(), p_25310_.getZ()) / 8;
    }

    public void start() {
        this.mob.getNavigation().stop();
        this.tamableHelper.tamabletool$setInSittingPose(true);
    }

    public void stop() {
        this.tamableHelper.tamabletool$setInSittingPose(false);
    }
}
