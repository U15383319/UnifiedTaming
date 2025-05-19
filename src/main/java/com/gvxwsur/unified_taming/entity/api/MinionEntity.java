package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.UUID;

public interface MinionEntity {
    public UUID unified_taming$getNonPlayerOwnerUUID();

    public boolean unified_taming$isTameNonPlayer();

    public void unified_taming$setTameNonPlayer(boolean p_21836_);

    public void unified_taming$setNonPlayerOwnerUUID(@Nullable UUID p_21817_);

    public void unified_taming$tameNonPlayer(Mob p_21829_);
}
