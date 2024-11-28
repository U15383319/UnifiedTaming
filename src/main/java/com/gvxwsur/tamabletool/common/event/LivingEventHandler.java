package com.gvxwsur.tamabletool.common.event;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.NeutralEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.helper.UniformPartEntity;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LivingEventHandler {
    @SubscribeEvent
    public static void onLivingTargetChanged(LivingChangeTargetEvent event) {
        LivingEntity living = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();

        if (!living.level().isClientSide) {
            if (newTarget != null && living instanceof Mob mob && ((TamableEntity) mob).tamabletool$isTame()) {
                if (!mob.canAttack(newTarget)) {
                    event.setCanceled(true);
                }
            }

            if (newTarget != null && TamableToolConfig.compatiblePartEntity.get()) {
                Entity livingParent = living, newTargetParent = newTarget;
                while (livingParent != null && !(livingParent instanceof Mob)) {
                    livingParent = ((UniformPartEntity) livingParent).getParent();
                }
                while (newTargetParent != null && !(newTargetParent instanceof Mob)) {
                    newTargetParent = ((UniformPartEntity) newTargetParent).getParent();
                }

                if (livingParent == null || newTargetParent == null) {
                    return;
                } else {
                    Mob livingParentMob = (Mob) livingParent, newTargetParentMob = (Mob) newTargetParent;
                    if (((TamableEntity) livingParentMob).tamabletool$isTame()) {
                        if (livingParentMob == newTargetParentMob || !livingParentMob.canAttack(newTargetParentMob)) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity living = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (!living.level().isClientSide && attacker != null) {
            if (attacker instanceof TamableEntity tamable && tamable.tamabletool$isTame()) {
                LivingEntity owner = tamable.getOwner();
                if (owner instanceof Player player) {
                    ((NeutralEntity) living).tamabletool$setLastHurtByPlayer(player, 100);
                } else {
                    living.setLastHurtByPlayer(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide && entity instanceof Mob mob && TamableToolUtils.isTame(mob)) {
            if (((CommandEntity) mob).tamabletool$unableToMove()) {
                event.setCanceled(true);
            }
        }
    }

}
