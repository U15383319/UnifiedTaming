package com.gvxwsur.unified_taming;

import com.gvxwsur.unified_taming.config.UnifiedTamingConfig;
import com.gvxwsur.unified_taming.init.InitItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UnifiedTaming.MOD_ID)
public class UnifiedTaming {
    public static final String MOD_ID = "unified_taming";

    public UnifiedTaming() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, UnifiedTamingConfig.CFG);
        initRegister(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void initRegister(IEventBus bus) {
        InitItems.ITEMS.register(bus);
    }
}
