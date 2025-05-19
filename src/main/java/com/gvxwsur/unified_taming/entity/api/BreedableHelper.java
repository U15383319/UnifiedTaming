package com.gvxwsur.unified_taming.entity.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface BreedableHelper {

    public boolean unified_taming$isBaby();

    public void unified_taming$setBaby(boolean p_146756_);

    @Nullable
    public Mob unified_taming$getBreedOffspring(ServerLevel var1, Player var2);

    public boolean unified_taming$isBreedFood(ItemStack stack);

    public default boolean unified_taming$canFallInLove() {
        return unified_taming$getInLoveTime() == 0;
    }

    public void unified_taming$setInLove();

    public void unified_taming$setInLoveTime(int p_27602_);

    public int unified_taming$getInLoveTime();

    public default boolean unified_taming$isInLove() {
        return unified_taming$getInLoveTime() > 0;
    }

    public boolean unified_taming$canMate(Player p_27569_);

    public void unified_taming$spawnChildFromBreeding(ServerLevel p_27564_, Player p_27565_);

    public void unified_taming$finalizeSpawnChildFromBreeding(ServerLevel p_277963_, Player p_277357_, @Nullable Mob p_277516_);
}
