package com.gvxwsur.unified_taming.item;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.entity.api.InteractEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MagicPopsicleItem extends Item {
    public MagicPopsicleItem() {
        super(new Properties().setNoRepair());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity livingEntity, @NotNull InteractionHand hand) {
        Level level = player.level();
        Entity interactTarget = (livingEntity instanceof Mob) ? livingEntity : UnifiedTamingUtils.getAncestry(livingEntity);
        if (stack.getItem() instanceof MagicPopsicleItem && interactTarget instanceof Mob mob && !((TamableEntity) mob).unified_taming$isTame()) {
            if ((((InteractEntity)mob).unified_taming$isTamer(stack) && ((InteractEntity)mob).unified_taming$isTamingConditionSatisfied()) || ((InteractEntity)mob).unified_taming$isCheatTamer(stack)) {
                if (!(mob instanceof TamableAnimal && !CompatibilityConfig.COMPATIBLE_VANILLA_TAMABLE_TAMING.get())) {
                    if (!level.isClientSide()) {
                        if (((InteractEntity)mob).unified_taming$isTamer(stack) && !player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }

                        if (((InteractEntity)mob).unified_taming$isCheatTamer(stack) || mob.getRandom().nextInt(3) == 0) {
                            ((TamableEntity)mob).unified_taming$tame(player);
                            if (mob instanceof TamableAnimal tamableAnimal) {
                                tamableAnimal.tame(player);
                            }
                            UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message.unified_taming.tame", mob.getDisplayName()), true);
                            mob.getNavigation().stop();
                            mob.setTarget(null);
                            // mob.unified_taming$setOrderedToSit(true);
                            mob.level().broadcastEntityEvent(mob, (byte) 7);
                        } else {
                            mob.level().broadcastEntityEvent(mob, (byte) 6);
                        }
                    }
                    return InteractionResult.sidedSuccess(!level.isClientSide());
                }
            }
        }
        return InteractionResult.PASS;
    }
}
