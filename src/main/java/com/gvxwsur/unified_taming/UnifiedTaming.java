package com.gvxwsur.unified_taming;

import com.gvxwsur.unified_taming.config.CommonConfig;
import com.gvxwsur.unified_taming.init.InitCreativeTabs;
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
        initRegister(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
    }

    public static void initRegister(IEventBus bus) {
        InitItems.ITEMS.register(bus);
        InitCreativeTabs.TABS.register(bus);
    }
}
