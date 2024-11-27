package com.gvxwsur.tamabletool.common.event;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.helper.UniformPartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onLivingTargetChanged(LivingChangeTargetEvent event) {
        LivingEntity living = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();

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
