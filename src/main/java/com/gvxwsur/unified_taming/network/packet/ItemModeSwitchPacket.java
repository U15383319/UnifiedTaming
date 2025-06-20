package com.gvxwsur.unified_taming.network.packet;

import com.gvxwsur.unified_taming.item.ControllingStaffItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemModeSwitchPacket {
    public void encode(FriendlyByteBuf buf) {

    }

    public static ItemModeSwitchPacket decode(FriendlyByteBuf buf) {
        return new ItemModeSwitchPacket();
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof ControllingStaffItem) {
                    ControllingStaffItem.switchMode(stack, player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
