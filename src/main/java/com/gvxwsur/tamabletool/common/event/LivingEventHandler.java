package com.gvxwsur.tamabletool.common.event;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.CommandEntity;
import com.gvxwsur.tamabletool.common.entity.helper.NeutralEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.helper.UniformPartEntity;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
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

            if (TamableToolConfig.compatiblePartEntity.get() && newTarget != null && !(living instanceof Mob)) {
                Entity livingAncestry = ((UniformPartEntity) living).getAncestry();
                if (livingAncestry instanceof Mob livingAncestryMob)  {
                    if (((TamableEntity) livingAncestryMob).tamabletool$isTame()) {
                        if (livingAncestryMob == newTarget || !livingAncestryMob.canAttack(newTarget)) {
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

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        boolean isLoadedFromDisk = event.loadedFromDisk();
        Level level = entity.level();
        if (!level.isClientSide && entity instanceof Mob mob) {
            if (TamableToolConfig.compatibleMobSummonedTamed.get() && mob.getSpawnType() == MobSpawnType.MOB_SUMMONED && !isLoadedFromDisk) {
                Mob mobOwner = level.getNearestEntity(Mob.class, TargetingConditions.forNonCombat().copy().range(8).selector(owner -> owner.getClass() != mob.getClass() && owner.getType().getCategory() == mob.getType().getCategory()), mob, mob.getX(), mob.getY(), mob.getZ(), mob.getBoundingBox().inflate(8));
                if (mobOwner != null) {
                    TamableToolUtils.tameMob(mob, mobOwner);
                }
            }
            if (TamableToolConfig.golemCreatedTamed.get() && mob instanceof AbstractGolem && mob.getSpawnType() != MobSpawnType.COMMAND && !isLoadedFromDisk) {
                Player player = level.getNearestPlayer(mob, 6);
                if (player != null) {
                    ((TamableEntity) mob).tamabletool$tame(player);
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
                if (TamableToolConfig.playerFriendlyFire.get() && attacker instanceof ServerPlayer player) {
                    if (living instanceof Mob mob && ((TamableEntity) mob).tamabletool$isOwnedBy(player)) {
                        event.setCanceled(true);
                    }
                    if (TamableToolConfig.compatiblePartEntity.get() && !(living instanceof Mob)) {
                        Entity livingAncestry = ((UniformPartEntity) living).getAncestry();
                        if (livingAncestry instanceof Mob livingAncestryMob) {
                            if (((TamableEntity) livingAncestryMob).tamabletool$isOwnedBy(player)) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
                if (attacker instanceof Mob mob && ((TamableEntity) mob).tamabletool$isTame()) {
                    if (!mob.canAttack(living)) {
                        event.setCanceled(true);
                    }
                }
                if (TamableToolConfig.compatiblePartEntity.get() && !(attacker instanceof Mob)) {
                    Entity attackerAncestry = ((UniformPartEntity) attacker).getAncestry();
                    if (attackerAncestry instanceof Mob attackerAncestryMob) {
                        if (((TamableEntity) attackerAncestryMob).tamabletool$isTame()) {
                            if (attackerAncestryMob == living || !attackerAncestryMob.canAttack(living)) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffect(MobEffectEvent.Added event) {
        LivingEntity living = event.getEntity();
        Entity source = event.getEffectSource();
        if (!living.level().isClientSide && source != null) {
            if (TamableToolConfig.playerFriendlyFire.get() && source instanceof ServerPlayer player) {
                if (living instanceof Mob mob && ((TamableEntity) mob).tamabletool$isOwnedBy(player)) {
                    event.getEffectInstance().duration = 0;
                }
                if (TamableToolConfig.compatiblePartEntity.get() && !(living instanceof Mob)) {
                    Entity livingAncestry = ((UniformPartEntity) living).getAncestry();
                    if (livingAncestry instanceof Mob livingAncestryMob) {
                        if (((TamableEntity) livingAncestryMob).tamabletool$isOwnedBy(player)) {
                            event.getEffectInstance().duration = 0;
                        }
                    }
                }
            }
            if (source instanceof Mob mob && ((TamableEntity) mob).tamabletool$isTame()) {
                if (!mob.canAttack(living)) {
                    event.getEffectInstance().duration = 0;
                }
            }
            if (TamableToolConfig.compatiblePartEntity.get() && !(source instanceof Mob)) {
                Entity sourceAncestry = ((UniformPartEntity) source).getAncestry();
                if (sourceAncestry instanceof Mob sourceAncestryMob) {
                    if (((TamableEntity) sourceAncestryMob).tamabletool$isTame()) {
                        if (sourceAncestryMob == living || !sourceAncestryMob.canAttack(living)) {
                            event.getEffectInstance().duration = 0;
                        }
                    }
                }
            }
        }
    }
}
