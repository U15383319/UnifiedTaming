package com.gvxwsur.tamabletool.common.entity.helper;

import com.gvxwsur.tamabletool.common.config.TamableConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public interface InteractEntity {

    public boolean tamabletool$isFood(ItemStack p_30440_);

    public float tamabletool$healValue(ItemStack p_30440_);

    public boolean tamabletool$isControl(ItemStack p_30440_);

    public boolean tamabletool$isTamingItem(ItemStack p_30440_);

    public boolean tamabletool$isTamingConditionSatisfied();

    public default boolean tamabletool$isCheatTamingItem(ItemStack p_30440_) {
        ResourceLocation location = new ResourceLocation(TamableConfig.cheatTamingItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return p_30440_.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return p_30440_.is(Items.DEBUG_STICK);
    }

    public default boolean tamabletool$isAssistItem(ItemStack p_30440_) {
        ResourceLocation location = new ResourceLocation(TamableConfig.modAssistItem.get());
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return p_30440_.is(ForgeRegistries.ITEMS.getValue(location));
        }
        return p_30440_.is(Items.CLOCK);
    }
}
