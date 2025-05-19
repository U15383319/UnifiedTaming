package com.gvxwsur.unified_taming.entity.goal;

import com.gvxwsur.unified_taming.entity.api.CommandEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class CustomOwnerHurtByTargetGoal extends TargetGoal {
    private final TamableEntity tamableHelper;
    private final CommandEntity commandHelper;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public CustomOwnerHurtByTargetGoal(Mob p_26107_) {
        super(p_26107_, false);
        this.tamableHelper = (TamableEntity) p_26107_;
        this.commandHelper = (CommandEntity) p_26107_;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tamableHelper.unified_taming$isTame() && !this.commandHelper.unified_taming$isOrderedToSit()) {
            LivingEntity $$0 = this.tamableHelper.getOwner();
            if ($$0 == null) {
                return false;
            } else {
                this.ownerLastHurtBy = $$0.getLastHurtByMob();
                int $$1 = $$0.getLastHurtByMobTimestamp();
                return $$1 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity $$0 = this.tamableHelper.getOwner();
        if ($$0 != null) {
            this.timestamp = $$0.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
