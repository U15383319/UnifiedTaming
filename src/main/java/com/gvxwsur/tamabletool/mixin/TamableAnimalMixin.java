package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.util.MessageSender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "tame", at = @At("HEAD"), cancellable = true)
    public void tame(Player p_21829_, CallbackInfo ci) {
        if (TamableToolConfig.compatibleVanillaTamable.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "setOrderedToSit", at = @At("HEAD"))
    public void setOrderedToSit(boolean p_21840_, CallbackInfo ci) {
        ((CommandEntity) this).tamabletool$setOrderedToSit(p_21840_);
        // MessageSender.sendCommandMessage(this, true);
    }
}
