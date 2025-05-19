package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface TamableEntity extends OwnableEntity {
    public boolean unified_taming$canTameAttack(LivingEntity livingEntity);

    public UUID unified_taming$getOwnerUUID();

    public boolean unified_taming$isTame();

    public void unified_taming$setTame(boolean p_21836_);

    public boolean unified_taming$isInSittingPose();

    public void unified_taming$setOwnerUUID(@Nullable UUID p_21817_);

    public void unified_taming$tame(Player p_21829_);

    public boolean unified_taming$isOwnedBy(LivingEntity p_21831_);

    public void unified_taming$registerTameGoals();
}
