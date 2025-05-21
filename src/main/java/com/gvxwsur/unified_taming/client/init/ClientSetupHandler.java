package com.gvxwsur.unified_taming.client.init;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.client.input.ClientKeyHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = UnifiedTaming.MOD_ID)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent event) {
        event.register(ClientKeyHandler.MULTI_TOOL_SWITCH_KEY);
    }
}
