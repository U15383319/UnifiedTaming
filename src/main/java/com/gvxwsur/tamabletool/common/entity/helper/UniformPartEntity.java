package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public interface UniformPartEntity {
    public default Entity getParent() {
        return null;
    }

    public default Entity getAncestry() {
        if (this instanceof Mob mob) {
            return mob;
        }
        Entity ancestry = this.getParent();
        while (ancestry != null && !(ancestry instanceof Mob)) {
            ancestry = ((UniformPartEntity) ancestry).getParent();
        }
        return ancestry;
    }
}
