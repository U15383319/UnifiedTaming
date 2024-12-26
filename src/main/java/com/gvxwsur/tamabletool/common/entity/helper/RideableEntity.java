package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface RideableEntity {

    public default void tamabletool$travel(Vec3 vec3) {}

    // public LivingEntity getControllingPassenger();

    // public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider);

    public default boolean tamabletool$canBeRiddenInAir(Entity rider) {
        return true;
    }

}
