package com.gvxwsur.unified_taming.config;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.config.subconfig.MiscConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MiscConfig.init(builder);
        CompatibilityConfig.init(builder);
        return builder.build();
    }

}
