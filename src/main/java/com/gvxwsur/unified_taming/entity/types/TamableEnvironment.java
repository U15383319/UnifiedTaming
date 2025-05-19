package com.gvxwsur.unified_taming.entity.types;

public enum TamableEnvironment {
    GROUND,
    GROUND_WALL,
    GROUND_HOVER,
    FLY_WANDER,
    FLY_PATH,
    WATER,
    AMPHIBIOUS,
    LAVA_SURFACE,
    LAVA,
    LAVA_AMPHIBIOUS;

    public boolean isGround() {
        return this == GROUND || this == GROUND_WALL || this == GROUND_HOVER;
    }

    public boolean isWalk() {
        return isGround() || this == AMPHIBIOUS || this == LAVA_AMPHIBIOUS || this == LAVA_SURFACE;
    }

    public boolean isWaterSwim() {
        return this == WATER || this == AMPHIBIOUS;
    }

    public boolean isFly() {
        return this == FLY_WANDER || this == FLY_PATH;
    }

    public boolean isFloat() {
        return isFly() || this == GROUND_HOVER;
    }

    public boolean isLavaSwim() {
        return this == LAVA || this == LAVA_AMPHIBIOUS;
    }

    public boolean isLava() {
        return isLavaSwim() || this == LAVA_SURFACE;
    }

    public boolean isAmphibious() {
        return this == AMPHIBIOUS || this == LAVA_AMPHIBIOUS;
    }
}
