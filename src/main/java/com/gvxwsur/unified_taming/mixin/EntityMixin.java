package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.entity.api.RideableEntity;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity, RideableEntity {

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (CompatibilityConfig.COMPATIBLE_PART_ENTITY.get()) {
            Entity ancestry = UnifiedTamingUtils.getAncestry((Entity) (Object) this);
            if (ancestry == null) {
                return;
            }
            if ((Entity) (Object) this != ancestry) {
                InteractionResult result = InteractionResult.PASS;
                if (ancestry instanceof LivingEntity ancestryLiving) {
                    result = (stack.interactLivingEntity(player, ancestryLiving, hand));
                }
                cir.setReturnValue(result != InteractionResult.PASS ? result : ancestry.interact(player, hand));
            }
        }
    }
}
