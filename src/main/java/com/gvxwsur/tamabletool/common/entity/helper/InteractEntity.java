package com.gvxwsur.tamabletool.common.entity.helper;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public interface InteractEntity {

    public InteractionResult tamabletool$tameInteract(Player player, InteractionHand hand);

    public ItemStack tamabletool$eat(Level level, ItemStack food);

    public boolean tamabletool$isFood(ItemStack p_30440_);

    public boolean tamabletool$isRider(ItemStack p_30440_);

    public boolean tamabletool$isCommander(ItemStack p_30440_);

    public boolean tamabletool$isRideModeSwitcher(ItemStack p_30440_);

    public boolean tamabletool$isMoveModeSwitcher(ItemStack p_30440_);

    public boolean tamabletool$isTamer(ItemStack p_30440_);

    public boolean tamabletool$isTamingConditionSatisfied();

    public default boolean tamabletool$isCheatTamer(ItemStack p_30440_) {
        ResourceLocation location = new ResourceLocation(TamableToolConfig.cheatTameItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return p_30440_.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return p_30440_.is(Items.DEBUG_STICK);
    }

    public default boolean tamabletool$isModAssistant(ItemStack p_30440_) {
        if (!TamableToolConfig.needModAssistItem.get()) {
            return true;
        }
        ResourceLocation location = new ResourceLocation(TamableToolConfig.modAssistItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return p_30440_.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return p_30440_.is(Items.CLOCK);
    }
}
