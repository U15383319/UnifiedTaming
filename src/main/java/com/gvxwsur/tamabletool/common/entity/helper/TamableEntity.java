package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface TamableEntity extends OwnableEntity {
    public boolean tamabletool$isTame();

    public void tamabletool$setTame(boolean p_21836_);

    public boolean tamabletool$isInSittingPose();

    public void tamabletool$setOwnerUUID(@Nullable UUID p_21817_);

    public void tamabletool$tame(Player p_21829_);

    public boolean tamabletool$isOwnedBy(LivingEntity p_21831_);

    public boolean tamabletool$wantsToAttack(LivingEntity p_21810_, LivingEntity p_21811_);

    public void tamabletool$setInSittingPose(boolean p_21838_);

    public boolean tamabletool$isOrderedToSit();

    public void tamabletool$setOrderedToSit(boolean p_21840_);
}
