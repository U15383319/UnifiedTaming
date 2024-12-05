package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.LivingEntity;

public interface RideableEntity {
    public LivingEntity getControllingPassenger();

    public boolean tamabletool$isManual();

    public void tamabletool$setManual(boolean manual);
}
