package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface RideableEntity {

    public default void unified_taming$travel(Vec3 vec3) {}

    // public LivingEntity getControllingPassenger();

    // public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider);

    public default boolean unified_taming$canBeRiddenInAir(Entity rider) {
        return true;
    }

    public default boolean unified_taming$isManual() {
        return true;
    }

    public default void unified_taming$setManual(boolean manual) {

    }

}
