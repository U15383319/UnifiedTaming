package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface TamableEntity extends OwnableEntity {
    public boolean tamabletool$canTameAttack(LivingEntity livingEntity);

    public UUID tamabletool$getOwnerUUID();

    public boolean tamabletool$isTame();

    public void tamabletool$setTame(boolean p_21836_);

    public boolean tamabletool$isInSittingPose();

    public void tamabletool$setOwnerUUID(@Nullable UUID p_21817_);

    public void tamabletool$tame(Player p_21829_);

    public boolean tamabletool$isOwnedBy(LivingEntity p_21831_);

    public void tamabletool$registerTameGoals();
}
