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

    public boolean tamabletool$isFood(ItemStack stack);

    public boolean tamabletool$isRider(ItemStack stack);

    public boolean tamabletool$isCommander(ItemStack stack);

    public boolean tamabletool$isRideModeSwitcher(ItemStack stack);

    public boolean tamabletool$isMoveModeSwitcher(ItemStack stack);

    public boolean tamabletool$isCarrier(ItemStack stack);

    public boolean tamabletool$isCarryReleaser(ItemStack stack);

    public boolean tamabletool$isTamer(ItemStack stack);

    public boolean tamabletool$isTamingConditionSatisfied();

    public default boolean tamabletool$isCheatTamer(ItemStack stack) {
        ResourceLocation location = new ResourceLocation(TamableToolConfig.cheatTameItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return stack.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return stack.is(Items.STRUCTURE_VOID);
    }

    public default boolean tamabletool$isModAssistant(ItemStack stack) {
        if (!TamableToolConfig.needModAssistItem.get()) {
            return true;
        }
        ResourceLocation location = new ResourceLocation(TamableToolConfig.modAssistItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return stack.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return stack.is(Items.ENCHANTED_BOOK);
    }
}
