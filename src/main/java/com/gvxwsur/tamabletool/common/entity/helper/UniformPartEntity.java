package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.Entity;

public interface UniformPartEntity {
    public default Entity getParent() {
        return null;
    }
}
