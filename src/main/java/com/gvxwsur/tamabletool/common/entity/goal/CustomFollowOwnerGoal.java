package com.gvxwsur.tamabletool.common.entity.goal;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.EnvironmentHelper;
import com.gvxwsur.tamabletool.common.entity.helper.enumhelper.TamableEnvironment;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class CustomFollowOwnerGoal extends Goal {
    private final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING;
    private final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING;
    private final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING;
    private final Mob mob;
    private final CommandEntity commandHelper;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private final float teleportDistance;
    private float oldWaterCost;

    public CustomFollowOwnerGoal(Mob p_25294_, double p_25295_, float p_25296_, float p_25297_) {
        this(p_25294_, p_25295_, p_25296_, p_25297_, 12);
    }

    public CustomFollowOwnerGoal(Mob p_25294_, double p_25295_, float p_25296_, float p_25297_, float p_25298_) {
        this.mob = p_25294_;
        this.level = p_25294_.level();
        this.commandHelper = (CommandEntity) p_25294_;
        this.speedModifier = p_25295_;
        this.navigation = p_25294_.getNavigation();
        float distanceFactor = TamableToolUtils.getScaleFactor(p_25294_);
        this.MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2 * (int) distanceFactor;
        this.MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3 * (int) distanceFactor;
        this.MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1 * (int) distanceFactor;
        this.startDistance = p_25296_ * distanceFactor;
        this.stopDistance = p_25297_ * distanceFactor;
        this.teleportDistance = p_25298_ * distanceFactor;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity $$0 = TamableToolUtils.getOwner(this.mob);
        if ($$0 == null) {
            return false;
        } else if ($$0.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (!this.commandHelper.tamabletool$isOrderedToFollow()) {
            return false;
        } else {
            if (this.mob instanceof TamableAnimal && !TamableToolConfig.compatibleVanillaTamableMovingGoals.get()) {
                return false;
            }
            if (this.adjustedDistanceToSqr($$0) < (double) (this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = $$0;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(this.adjustedDistanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
        }
    }

    private boolean unableToMove() {
        return this.commandHelper.tamabletool$unableToMove();
    }

    private double adjustedDistanceToSqr(LivingEntity p_25310_) {
        return !(this.getEnvironment() == TamableEnvironment.FLY_WANDER) ? this.mob.distanceToSqr(p_25310_) : this.mob.distanceToSqr(p_25310_.getX(), this.mob.getY(), p_25310_.getZ());
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.mob.getLookControl().setLookAt(this.owner, 10.0F, (float) this.mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.adjustedDistanceToSqr(this.owner) >= teleportDistance * teleportDistance) {
                this.teleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }
        }
    }

    private void teleportToOwner() {
        BlockPos $$0 = this.owner.blockPosition();

        for (int $$1 = 0; $$1 < 10; ++$$1) {
            int $$2 = this.randomIntInclusive(-MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING, MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING);
            int $$3 = this.randomIntInclusive(-MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING, MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING);
            int $$4 = this.randomIntInclusive(-MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING, MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING);
            boolean $$5 = this.maybeTeleportTo($$0.getX() + $$2, $$0.getY() + $$3, $$0.getZ() + $$4);
            if ($$5) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
        if (this.getEnvironment() == TamableEnvironment.FLY_WANDER) {
            p_25305_ += Mth.ceil(this.mob.getBbHeight() + this.owner.getBbHeight() + .5F);
        }
        if (Math.abs((double) p_25304_ - this.owner.getX()) < MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING && Math.abs((double) p_25306_ - this.owner.getZ()) < MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
            return false;
        } else {
            this.mob.moveTo((double) p_25304_ + 0.5, (double) p_25305_, (double) p_25306_ + 0.5, this.mob.getYRot(), this.mob.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos blockPos) {
        if (this.getEnvironment().isFly()) {
            BlockState $$2 = this.level.getBlockState(blockPos.below());
            boolean fluidFlag = true;
            if (!$$2.getFluidState().isEmpty()) {
                fluidFlag = !this.mob.canDrownInFluidType($$2.getFluidState().getFluidType())
                        && (!$$2.getFluidState().is(FluidTags.WATER) || !this.mob.isSensitiveToWater())
                        && (!$$2.getFluidState().is(FluidTags.LAVA) || this.mob.fireImmune());
            }
            BlockPos $$3 = blockPos.subtract(this.mob.blockPosition());
            return fluidFlag && this.level.noCollision(this.mob, this.mob.getBoundingBox().move($$3));
        }
        if (this.getEnvironment().isWaterSwim()) {
            if (level.isWaterAt(blockPos)) {
                return true;
            } else if (!this.getEnvironment().isWalk()) {
                return false;
            }
        }
        if (this.getEnvironment().isLava()) {
            if (level.getFluidState(blockPos).is(FluidTags.LAVA)) {
                return true;
            } else if (!this.getEnvironment().isWalk()) {
                return false;
            }
        }
        BlockPathTypes $$1 = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, blockPos.mutable());
        if ($$1 == BlockPathTypes.WALKABLE) {
            BlockPos $$3 = blockPos.subtract(this.mob.blockPosition());
            return this.level.noCollision(this.mob, this.mob.getBoundingBox().move($$3));
        }
        return false;
    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.mob.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }

    private TamableEnvironment getEnvironment() {
        return ((EnvironmentHelper) mob).tamabletool$getEnvironment();
    }
}
