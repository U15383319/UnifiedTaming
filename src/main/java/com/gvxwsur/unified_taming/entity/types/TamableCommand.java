package com.gvxwsur.unified_taming.entity.types;

import com.gvxwsur.unified_taming.UnifiedTaming;

import java.util.Locale;

public enum TamableCommand {
    FOLLOW,
    SIT,
    STROLL;

    public String getLang() {
        return UnifiedTaming.MOD_ID + ".command." + this.toString().toLowerCase(Locale.ROOT);
    }
}
