package com.gvxwsur.unified_taming.entity.ai.goal;

import com.gvxwsur.unified_taming.entity.api.CommandEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CustomMoveTowardsRestrictionGoal extends Goal {
    private final Mob mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private int teleportTickCount;
    private static final int maxCheckRate = 12;

    public CustomMoveTowardsRestrictionGoal(Mob p_25633_, double p_25634_) {
        this.mob = p_25633_;
        this.speedModifier = p_25634_;
        this.teleportTickCount = maxCheckRate;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.mob.isWithinRestriction()) {
            return false;
        } else {
            if (!((CommandEntity) this.mob).unified_taming$isOrderedToStroll()) {
                return false;
            }
            Vec3 $$0 = null;
            if (this.mob instanceof PathfinderMob pathfinderMob) {
                $$0 = DefaultRandomPos.getPosTowards(pathfinderMob, 16, 7, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()), (float)Math.PI / 2F);
            }

            if ($$0 == null) {
                if (--teleportTickCount <= 0) {
                    return true;
                }
                return false;
            } else {
                this.wantedX = $$0.x;
                this.wantedY = $$0.y;
                this.wantedZ = $$0.z;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public void start() {
        if (teleportTickCount <= 0) {
            this.mob.teleportTo(this.mob.getRestrictCenter().getX(), this.mob.getRestrictCenter().getY() + .5F, this.mob.getRestrictCenter().getZ());
            teleportTickCount = maxCheckRate;
            return;
        }
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
}
