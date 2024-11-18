package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin extends HorizontalDirectionalBlock {

    protected CarvedPumpkinBlockMixin(Properties p_54120_) {
        super(p_54120_);
    }

    @Inject(method = "spawnGolemInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static void spawnGolemInWorld(Level p_249110_, BlockPattern.BlockPatternMatch p_251293_, Entity p_251251_, BlockPos p_251189_, CallbackInfo ci) {
        if (p_251251_ instanceof TamableEntity tamable) {
            tamable.tamabletool$tame(p_249110_.getNearestPlayer(p_251251_, 8.0));
        }
    }
}
