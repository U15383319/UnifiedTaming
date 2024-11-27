package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.UniformPartEntity;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity, UniformPartEntity {

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    protected EntityMixin(Class<Entity> baseClass, boolean isLazy) {
        super(baseClass, isLazy);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interact(Player p_19978_, InteractionHand p_19979_, CallbackInfoReturnable<InteractionResult> cir) {
        if (TamableToolConfig.compatiblePartEntity.get() && this.getParent() != null) {
            cir.setReturnValue(this.getParent().interact(p_19978_, p_19979_));
        }
    }
}
