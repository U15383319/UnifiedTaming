package com.gvxwsur.tamabletool;

import com.gvxwsur.tamabletool.common.config.TamableConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TamableTool.MODID)
public class TamableTool {
    public static final String MODID = "tamabletool";

    public TamableTool() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TamableConfig.CFG);
    }
}
