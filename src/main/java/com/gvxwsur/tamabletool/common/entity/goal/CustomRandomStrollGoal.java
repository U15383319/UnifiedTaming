package com.gvxwsur.tamabletool.common.entity.goal;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import com.ibm.icu.impl.Assert;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CustomRandomStrollGoal extends Goal {
    public static final int DEFAULT_INTERVAL = 120;
    protected final PathfinderMob pathfinderMob;
    protected final TamableEntity tamableHelper;
    protected final CommandEntity commandHelper;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;

    public CustomRandomStrollGoal(Mob p_25734_, double p_25735_) {
        this(p_25734_, p_25735_, DEFAULT_INTERVAL);
    }

    public CustomRandomStrollGoal(Mob p_25737_, double p_25738_, int p_25739_) {
        this(p_25737_, p_25738_, p_25739_, true);
    }

    public CustomRandomStrollGoal(Mob p_25741_, double p_25742_, int p_25743_, boolean p_25744_) {
        Assert.assrt(p_25741_ instanceof PathfinderMob);
        this.pathfinderMob = (PathfinderMob) p_25741_;
        this.tamableHelper = (TamableEntity) p_25741_;
        this.commandHelper = (CommandEntity) p_25741_;
        this.speedModifier = p_25742_;
        this.interval = p_25743_;
        this.checkNoActionTime = p_25744_;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!TamableToolUtils.isTame(pathfinderMob)) {
            return false;
        } else if (this.pathfinderMob.isVehicle()) {
            return false;
        } else if (!this.commandHelper.tamabletool$isOrderedToStroll()) {
            return false;
        } else {
            if (this.pathfinderMob instanceof TamableAnimal && !TamableToolConfig.compatibleVanillaTamableMovingGoals.get()) {
                return false;
            }

            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.pathfinderMob.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.pathfinderMob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 $$0 = this.getPosition();
            if ($$0 == null) {
                return false;
            } else {
                this.wantedX = $$0.x;
                this.wantedY = $$0.y;
                this.wantedZ = $$0.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.pathfinderMob, 10, 7);
    }

    public boolean canContinueToUse() {
        return !this.pathfinderMob.getNavigation().isDone() && !this.pathfinderMob.isVehicle();
    }

    public void start() {
        this.pathfinderMob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    public void stop() {
        this.pathfinderMob.getNavigation().stop();
        super.stop();
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    public void setInterval(int p_25747_) {
        this.interval = p_25747_;
    }
}
