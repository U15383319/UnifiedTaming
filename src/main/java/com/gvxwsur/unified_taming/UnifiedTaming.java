package com.gvxwsur.unified_taming;

import com.gvxwsur.unified_taming.config.UnifiedTamingConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(UnifiedTaming.MOD_ID)
public class UnifiedTaming {
    public static final String MOD_ID = "unified_taming";

    public UnifiedTaming() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, UnifiedTamingConfig.CFG);
    }
}
