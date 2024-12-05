package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface AgeableEntity {

    public boolean isBaby();

    public void setBaby(boolean p_146756_);

    @Nullable
    public Mob tamabletool$getBreedOffspring(ServerLevel var1, Player var2);

    public int tamabletool$getAge();

    public void tamabletool$ageUp(int p_146759_);

    public void tamabletool$setAge(int p_146763_);

    public boolean tamabletool$isBreedFood(ItemStack stack);

    public default boolean tamabletool$canFallInLove() {
        return tamabletool$getInLoveTime() <= 0;
    }

    public void tamabletool$setInLove();

    public void tamabletool$setInLoveTime(int p_27602_);

    public int tamabletool$getInLoveTime();

    public default boolean tamabletool$isInLove() {
        return tamabletool$getInLoveTime() > 0;
    }

    public default void tamabletool$resetLove() {
        tamabletool$setInLoveTime(0);
    }

    public boolean tamabletool$canMate(Player p_27569_);

    public void tamabletool$spawnChildFromBreeding(ServerLevel p_27564_, Player p_27565_);

    public void tamabletool$finalizeSpawnChildFromBreeding(ServerLevel p_277963_, Player p_277357_, @Nullable Mob p_277516_);
}
