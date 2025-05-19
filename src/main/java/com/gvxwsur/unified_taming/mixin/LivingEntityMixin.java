package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.entity.api.NeutralEntity;
import com.gvxwsur.unified_taming.entity.api.RideableEntity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, IForgeLivingEntity, NeutralEntity {

    @Shadow protected int lastHurtByPlayerTime;

    @Shadow private int lastHurtByMobTimestamp;

    @Shadow @Nullable protected Player lastHurtByPlayer;

    @Shadow @Nullable private LivingEntity lastHurtByMob;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    public void baseTick(CallbackInfo ci) {
        if (this.isAlive()) {
            if (this.getEyeInFluidType().isAir()) {
                if (!this.level().isClientSide && this.isPassenger() && this.getVehicle() != null && this.getVehicle().onGround() && !((RideableEntity) this.getVehicle()).unified_taming$canBeRiddenInAir(this)) {
                    this.stopRiding();
                }
            }
        }
    }

    @Inject(method = "travelRidden", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER))
    private void travelRidden(Player p_278244_, Vec3 p_278231_, CallbackInfo ci) {
        ((RideableEntity) this).unified_taming$travel(p_278231_);
    }

    public void unified_taming$setLastHurtByPlayer(@Nullable Player pPlayer, int time) {
        this.lastHurtByPlayer = pPlayer;
        this.lastHurtByPlayerTime = time;
    }

    public void unified_taming$setLastHurtByMob(@Nullable LivingEntity p_21039_, int timestamp) {
        this.lastHurtByMob = p_21039_;
        this.lastHurtByMobTimestamp = timestamp;
    }
}
