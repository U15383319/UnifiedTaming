package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface NeutralEntity {
    public void unified_taming$setLastHurtByPlayer(@Nullable Player pPlayer, int time);

    public void unified_taming$setLastHurtByMob(@Nullable LivingEntity p_21039_, int timestamp);
}
