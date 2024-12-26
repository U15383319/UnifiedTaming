package com.gvxwsur.tamabletool.common.entity.helper.enumhelper;

public enum TamableEnvironment {
    GROUND,
    FLY_WANDER,
    WATER,
    FLY_PATH,
    AMPHIBIOUS,
    LAVA;

    public boolean isWalk() {
        return this == GROUND || this == AMPHIBIOUS;
    }

    public boolean isSwim() {
        return this == WATER || this == AMPHIBIOUS;
    }

    public boolean isFly() {
        return this == FLY_WANDER || this == FLY_PATH;
    }
}
