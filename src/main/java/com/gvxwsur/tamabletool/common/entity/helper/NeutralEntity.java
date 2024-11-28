package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface NeutralEntity {
    public void tamabletool$setLastHurtByPlayer(@Nullable Player pPlayer, int time);

    public void tamabletool$setLastHurtByMob(@Nullable LivingEntity p_21039_, int timestamp);
}
