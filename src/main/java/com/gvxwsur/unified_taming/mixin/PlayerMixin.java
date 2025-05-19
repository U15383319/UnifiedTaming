package com.gvxwsur.unified_taming.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IForgePlayer {
    @Shadow public abstract @NotNull EntityDimensions getDimensions(@NotNull Pose p_36166_);

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    protected void positionRider(Entity p_19957_, MoveFunction p_19958_) {
        if (this.hasPassenger(p_19957_)) {
            double d0 = this.getY() + this.getEyeHeight() + 0.01;
            p_19958_.accept(p_19957_, this.getX(), d0, this.getZ());
        }
    }
}
