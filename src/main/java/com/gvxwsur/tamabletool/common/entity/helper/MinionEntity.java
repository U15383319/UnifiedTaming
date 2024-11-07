package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface MinionEntity {
    public UUID tamabletool$getNonPlayerOwnerUUID();

    public boolean tamabletool$isTameNonPlayer();

    public void tamabletool$setTameNonPlayer(boolean p_21836_);

    public void tamabletool$setNonPlayerOwnerUUID(@Nullable UUID p_21817_);

    public void tamabletool$tameNonPlayer(Mob p_21829_);
}
