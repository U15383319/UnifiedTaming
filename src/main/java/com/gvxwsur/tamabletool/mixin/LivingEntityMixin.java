package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.entity.helper.NeutralEntity;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, IForgeLivingEntity, NeutralEntity {
    @Shadow public abstract void setLastHurtByPlayer(@org.jetbrains.annotations.Nullable Player p_21248_);

    @Shadow protected int lastHurtByPlayerTime;

    @Shadow public abstract void setLastHurtByMob(@org.jetbrains.annotations.Nullable LivingEntity p_21039_);

    @Shadow private int lastHurtByMobTimestamp;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public void tamabletool$setLastHurtByPlayer(@Nullable Player pPlayer, int time) {
        this.setLastHurtByPlayer(pPlayer);
        this.lastHurtByPlayerTime = time;
    }

    public void tamabletool$setLastHurtByMob(@Nullable LivingEntity p_21039_, int timestamp) {
        this.setLastHurtByMob(p_21039_);
        this.lastHurtByMobTimestamp = timestamp;
    }
}
