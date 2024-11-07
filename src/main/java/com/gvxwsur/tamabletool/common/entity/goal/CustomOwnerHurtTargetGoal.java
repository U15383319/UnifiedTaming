package com.gvxwsur.tamabletool.common.entity.goal;

import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class CustomOwnerHurtTargetGoal extends TargetGoal {
    private final Mob tameAnimal;
    private final TamableEntity tamableHelper;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public CustomOwnerHurtTargetGoal(Mob p_26114_) {
        super(p_26114_, false);
        this.tameAnimal = p_26114_;
        this.tamableHelper = (TamableEntity) p_26114_;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tamableHelper.tamabletool$isTame() && !this.tamableHelper.tamabletool$isOrderedToSit()) {
            LivingEntity $$0 = this.tamableHelper.getOwner();
            if ($$0 == null) {
                return false;
            } else {
                this.ownerLastHurt = $$0.getLastHurtMob();
                int $$1 = $$0.getLastHurtMobTimestamp();
                return $$1 != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tamableHelper.tamabletool$wantsToAttack(this.ownerLastHurt, $$0);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity $$0 = this.tamableHelper.getOwner();
        if ($$0 != null) {
            this.timestamp = $$0.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
