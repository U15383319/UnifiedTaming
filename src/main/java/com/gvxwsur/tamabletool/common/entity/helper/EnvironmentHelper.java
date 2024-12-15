package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.phys.Vec3;

public interface EnvironmentHelper {
    public void tamabletool$travel(Vec3 vec3);

    public TamableEnvironment tamabletool$getEnvironment();

    public void tamabletool$setEnvironment(TamableEnvironment environment);

    public void tamabletool$updateEnvironment();
}
