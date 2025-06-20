package com.gvxwsur.unified_taming.entity.api;

import com.gvxwsur.unified_taming.init.InitItems;
import net.minecraft.world.item.ItemStack;

public interface InteractEntity {

    boolean unified_taming$isFood(ItemStack stack);

    default boolean unified_taming$isTamer(ItemStack stack) {
        return stack.is(InitItems.MAGIC_POPSICLE.get());
    }

    boolean unified_taming$isTamingConditionSatisfied();

    default boolean unified_taming$isCheatTamer(ItemStack stack) {
        return stack.is(InitItems.MAGIC_POPSICLE_CREATIVE.get());
    }

}
