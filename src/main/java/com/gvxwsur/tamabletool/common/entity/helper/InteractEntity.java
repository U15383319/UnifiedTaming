package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface InteractEntity {

    public boolean tamabletool$isFood(ItemStack p_30440_);

    public boolean tamabletool$isControl(ItemStack p_30440_);

    public boolean tamabletool$isTamingItem(ItemStack p_30440_);

    public default boolean tamabletool$isCreativeTamingItem(ItemStack p_30440_) {
        return p_30440_.is(Items.DEBUG_STICK);
    }

    public default boolean tamabletool$isMark(ItemStack p_30440_) {
        return p_30440_.is(Items.CLOCK);
    }
}
