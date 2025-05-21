package com.gvxwsur.unified_taming.client.input;

import com.gvxwsur.unified_taming.init.InitItems;
import com.gvxwsur.unified_taming.item.MultiToolItem;
import com.gvxwsur.unified_taming.network.NetworkHandler;
import com.gvxwsur.unified_taming.network.packet.ItemModeSwitchPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientKeyHandler {
    public static KeyMapping MULTI_TOOL_SWITCH_KEY = new KeyMapping("key.unified_taming.multi_tool_switch_key.desc", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.category.unified_taming");

    @SubscribeEvent
    public static void onInput(InputEvent.Key event) {
        if (MULTI_TOOL_SWITCH_KEY.matches(event.getKey(), event.getScanCode()) && MULTI_TOOL_SWITCH_KEY.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() == InitItems.MULTI_TOOL_ITEM.get()) {
                    NetworkHandler.CHANNEL.sendToServer(new ItemModeSwitchPacket());
                }
            }
        }
    }
}
