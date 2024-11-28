package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Animal implements OwnableEntity {

    protected TamableAnimalMixin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Inject(method = "getOwnerUUID", at = @At("HEAD"), cancellable = true)
    public void getOwnerUUID(CallbackInfoReturnable<UUID> cir) {
        if (TamableToolConfig.compatibleVanillaTamable.get()) {
            cir.setReturnValue(((TamableEntity)this).tamabletool$getOwnerUUID());
        }
    }

    @Inject(method = "setOrderedToSit", at = @At("HEAD"))
    public void setOrderedToSit(boolean p_21840_, CallbackInfo ci) {
        if (TamableToolConfig.compatibleVanillaTamable.get()) {
            ((CommandEntity) this).tamabletool$setOrderedToSit(p_21840_);
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;sendSystemMessage(Lnet/minecraft/network/chat/Component;)V"), cancellable = true)
    public void die(DamageSource p_21809_, CallbackInfo ci) {
        if (TamableToolConfig.compatibleVanillaTamable.get()) {
            ci.cancel();
        }
    }
}
