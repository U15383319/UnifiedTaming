package com.gvxwsur.unified_taming.network.packet;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.item.ControllingStaffItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SelectStaffModePacket {
    private final int modeId;

    public SelectStaffModePacket(int modeId) {
        this.modeId = modeId;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(modeId);
    }

    public static SelectStaffModePacket decode(FriendlyByteBuf buf) {
        return new SelectStaffModePacket(buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof ControllingStaffItem) {
                    // Set the mode directly
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putInt("ToolMode", modeId);

                    // Send feedback to player
                    String[] modes = {
                        "follow_or_sit",
                        "follow_or_stroll",
                        "ride_mode",
                        "ride",
                        "carry",
                        "stop_riding",
                        "feed"
                    };

                    if (modeId >= 0 && modeId < modes.length) {
                        String langKey = "item." + UnifiedTaming.MOD_ID + ".controlling_staff.mode." + modes[modeId];
                        player.displayClientMessage(Component.translatable(langKey), true);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

