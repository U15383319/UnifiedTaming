package com.gvxwsur.unified_taming.network;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.network.packet.ItemModeSwitchPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {
    private static final String VERSION = "1.0.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(UnifiedTaming.MOD_ID, "network"),
            () -> VERSION, it -> it.equals(VERSION), it -> it.equals(VERSION));

    public static void init() {
        CHANNEL.registerMessage(0, ItemModeSwitchPacket.class,
                ItemModeSwitchPacket::encode,
                ItemModeSwitchPacket::decode,
                ItemModeSwitchPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
