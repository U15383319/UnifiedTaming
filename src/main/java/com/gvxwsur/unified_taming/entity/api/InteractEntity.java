package com.gvxwsur.unified_taming.entity.api;

import com.gvxwsur.unified_taming.config.UnifiedTamingConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public interface InteractEntity {

    public boolean unified_taming$isFood(ItemStack stack);

    public boolean unified_taming$isRider(ItemStack stack);

    public boolean unified_taming$isCommander(ItemStack stack);

    public boolean unified_taming$isRideModeSwitcher(ItemStack stack);

    public boolean unified_taming$isMoveModeSwitcher(ItemStack stack);

    public boolean unified_taming$isCarrier(ItemStack stack);

    public boolean unified_taming$isCarryReleaser(ItemStack stack);

    public boolean unified_taming$isTamer(ItemStack stack);

    public boolean unified_taming$isTamingConditionSatisfied();

    public default boolean unified_taming$isCheatTamer(ItemStack stack) {
        ResourceLocation location = new ResourceLocation(UnifiedTamingConfig.cheatTameItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return stack.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return stack.is(Items.STRUCTURE_VOID);
    }

    public default boolean unified_taming$isModAssistant(ItemStack stack) {
        if (!UnifiedTamingConfig.needModAssistItem.get()) {
            return true;
        }
        ResourceLocation location = new ResourceLocation(UnifiedTamingConfig.modAssistItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return stack.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return stack.is(Items.ENCHANTED_BOOK);
    }
}
