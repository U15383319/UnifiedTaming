package com.gvxwsur.tamabletool.common.event;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.*;
import com.gvxwsur.tamabletool.common.entity.util.MessageSender;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LivingEventHandler {
    @SubscribeEvent
    public static void onLivingTargetChanged(LivingChangeTargetEvent event) {
        LivingEntity living = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();

        if (!living.level().isClientSide) {
            if (newTarget != null) {
                if (TamableToolUtils.shouldMobFriendly(living, newTarget)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity living = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (!living.level().isClientSide && attacker != null) {
            if (attacker instanceof TamableEntity tamable) {
                if (tamable.tamabletool$isTame()) {
                    LivingEntity owner = tamable.getOwner();
                    if (owner instanceof Player player) {
                        ((NeutralEntity) living).tamabletool$setLastHurtByPlayer(player, 100);
                    } else {
                        living.setLastHurtByPlayer(null);
                    }
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

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Start event) {
        Entity entity = event.getExplosion().getExploder();
        if (entity != null && !entity.level().isClientSide && entity instanceof Mob mob && TamableToolUtils.isTame(mob)) {
            if (((CommandEntity) mob).tamabletool$unableToMove() || (mob instanceof SelfDestructEntity selfDestructEntity && selfDestructEntity.tamabletool$isDestructed())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        boolean isLoadedFromDisk = event.loadedFromDisk();
        Level level = entity.level();
        if (!level.isClientSide && entity instanceof Mob mob) {
            if (TamableToolConfig.compatibleMobSummonedTamed.get() && mob.getSpawnType() == MobSpawnType.MOB_SUMMONED && !isLoadedFromDisk) {
                Mob mobOwner = level.getNearestEntity(Mob.class, TargetingConditions.forNonCombat().copy().range(8).selector(owner -> owner.getClass() != mob.getClass() && owner.getType().getCategory().isFriendly() == mob.getType().getCategory().isFriendly()), mob, mob.getX(), mob.getY(), mob.getZ(), mob.getBoundingBox().inflate(8));
                if (mobOwner != null) {
                    TamableToolUtils.tameMob(mob, mobOwner);
                }
            }
            if (TamableToolConfig.golemCreatedTamed.get() && mob instanceof AbstractGolem && !isLoadedFromDisk) {
                if (mob.getSpawnType() == null) {
                    Player player = level.getNearestPlayer(mob, 6);
                    if (player != null) {
                        ((TamableEntity) mob).tamabletool$tame(player);
                        // no need to check tamable animal
                        MessageSender.sendTamingMessage(mob, player, true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Post event) {
        LivingEntity living = event.getEntity();
        LivingEntity outcome = event.getOutcome();
        if (!living.level().isClientSide) {
            if (living instanceof Mob mob && outcome instanceof Mob outcomeMob) {
                if (TamableToolUtils.getOwner(mob) instanceof ServerPlayer player) {
                    ((TamableEntity) outcomeMob).tamabletool$tame(player);
                    if (outcomeMob instanceof TamableAnimal tamableAnimal) {
                        tamableAnimal.tame(player);
                    }
                    MessageSender.sendConvertingMessage(mob, outcomeMob, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        DamageSource source = event.getSource();
        if (!living.level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker != null) {
                if (TamableToolUtils.shouldFireFriendly(attacker, living)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffect(MobEffectEvent.Added event) {
        LivingEntity living = event.getEntity();
        Entity source = event.getEffectSource();
        if (!living.level().isClientSide && source != null) {
            if (TamableToolUtils.shouldFireFriendly(source, living)) {
                event.getEffectInstance().duration = 0;
            }
        }
    }
}
