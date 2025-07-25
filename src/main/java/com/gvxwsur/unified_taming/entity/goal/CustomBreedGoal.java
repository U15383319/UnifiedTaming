package com.gvxwsur.unified_taming.entity.goal;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.entity.api.BreedableHelper;
import com.gvxwsur.unified_taming.entity.api.CommandEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CustomBreedGoal extends Goal {
    protected final float MAX_PARTNER_FINDING_DISTANCE;
    protected final float MAX_CAN_BREED_DISTANCE;
    protected final Mob mob;
    protected final TamableEntity tamableHelper;
    protected final CommandEntity commandHelper;
    protected final BreedableHelper breedableHelper;
    protected final Level level;
    @Nullable protected Player partner;
    private int loveTime;
    private final double speedModifier;

    public CustomBreedGoal(Mob p_25125_, double p_25126_) {
        this.mob = p_25125_;
        this.tamableHelper = (TamableEntity) p_25125_;
        this.commandHelper = (CommandEntity) p_25125_;
        this.breedableHelper = (BreedableHelper) p_25125_;
        this.level = p_25125_.level();
        this.speedModifier = p_25126_;
        this.MAX_PARTNER_FINDING_DISTANCE = 8.0F * UnifiedTamingUtils.getScaleFactorBySize(p_25125_);
        this.MAX_CAN_BREED_DISTANCE = 3.0F * UnifiedTamingUtils.getScaleFactorBySize(p_25125_);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!UnifiedTamingUtils.isTame(this.mob)) {
            return false;
        } else if (this.commandHelper.unified_taming$unableToMove()) {
            return false;
        } else if (!this.breedableHelper.unified_taming$isInLove()) {
            return false;
        } else {
            if (this.mob instanceof TamableAnimal && !CompatibilityConfig.COMPATIBLE_VANILLA_TAMABLE_TAMING.get()) {
                return false;
            }
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        return this.partner != null && this.partner.isAlive() && this.breedableHelper.unified_taming$isInLove() && this.loveTime < 60;
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        if (this.partner != null) {
            this.mob.getLookControl().setLookAt(this.partner, 10.0F, (float)this.mob.getMaxHeadXRot());
            this.mob.getNavigation().moveTo(this.partner, this.speedModifier);
            ++this.loveTime;
            if (this.loveTime >= this.adjustedTickDelay(60) && this.mob.distanceToSqr(this.partner) < MAX_CAN_BREED_DISTANCE * MAX_CAN_BREED_DISTANCE) {
                this.breed();
            }
        }
    }

    @Nullable
    private Player getFreePartner() {
        Player player = null;
        if (this.tamableHelper.getOwner() instanceof Player player1 && this.breedableHelper.unified_taming$canMate(player1) && player1.distanceToSqr(this.mob) < MAX_PARTNER_FINDING_DISTANCE * MAX_PARTNER_FINDING_DISTANCE) {
            player = player1;
        }

        return player;
    }

    protected void breed() {
        this.breedableHelper.unified_taming$spawnChildFromBreeding((ServerLevel)this.level, this.partner);
    }
}
