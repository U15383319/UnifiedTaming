package com.gvxwsur.unified_taming.entity.api;

import com.gvxwsur.unified_taming.init.InitItems;
import net.minecraft.world.item.ItemStack;

public interface InteractEntity {

    public boolean unified_taming$isFood(ItemStack stack);

    public boolean unified_taming$isTamer(ItemStack stack);

    public boolean unified_taming$isTamingConditionSatisfied();

    public default boolean unified_taming$isCheatTamer(ItemStack stack) {
        return stack.is(InitItems.TAME_MATERIAL_CREATIVE.get());
    }

}
