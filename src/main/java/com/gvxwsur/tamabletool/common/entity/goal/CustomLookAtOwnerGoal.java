package com.gvxwsur.tamabletool.common.entity.goal;

import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import com.ibm.icu.impl.Assert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CustomLookAtOwnerGoal extends Goal {
    public static final float DEFAULT_PROBABILITY = 0.02F;
    protected final Mob mob;
    @Nullable
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final TargetingConditions lookAtContext;

    public CustomLookAtOwnerGoal(Mob p_25520_, Class<? extends LivingEntity> p_25521_, float p_25522_) {
        this(p_25520_, p_25521_, p_25522_, DEFAULT_PROBABILITY);
    }

    public CustomLookAtOwnerGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_, float p_25527_) {
        this(p_25524_, p_25525_, p_25526_, p_25527_, false);
    }

    public CustomLookAtOwnerGoal(Mob p_148118_, Class<? extends LivingEntity> p_148119_, float p_148120_, float p_148121_, boolean p_148122_) {
        Assert.assrt(p_148119_ == Player.class);
        this.mob = p_148118_;
        this.lookDistance = p_148120_;
        this.probability = p_148121_;
        this.onlyHorizontal = p_148122_;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.lookAtContext = TargetingConditions.forNonCombat().range((double) p_148120_).selector((p_25531_) -> {
            return EntitySelector.notRiding(p_148118_).and(entity -> entity instanceof LivingEntity living && ((TamableEntity) p_148118_).tamabletool$isOwnedBy(living)).test(p_25531_);
        });
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (!TamableToolUtils.isTame(this.mob)) {
                return false;
            }

            this.lookAt = this.mob.level().getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            return this.lookAt != null;
        }
    }

    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) {
            return false;
        } else if (this.mob.distanceToSqr(this.lookAt) > (double) (this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    public void stop() {
        this.lookAt = null;
    }

    public void tick() {
        if (this.lookAt.isAlive()) {
            double $$0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
            this.mob.getLookControl().setLookAt(this.lookAt.getX(), $$0, this.lookAt.getZ());
            --this.lookTime;
        }
    }
}
