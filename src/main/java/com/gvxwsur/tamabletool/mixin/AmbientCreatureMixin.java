package com.gvxwsur.tamabletool.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AmbientCreature.class)
public abstract class AmbientCreatureMixin extends Mob {
    protected AmbientCreatureMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    public void canBeLeashed(Player p_35272_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(super.canBeLeashed(p_35272_));
    }
}
