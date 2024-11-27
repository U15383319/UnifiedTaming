package com.gvxwsur.tamabletool.common.event;

import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
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
        if (living instanceof Mob mob && ((TamableEntity) mob).tamabletool$isTame()) {
            if (newTarget != null && !mob.canAttack(newTarget)) {
                event.setCanceled(true);
            }
        }
    }

}
