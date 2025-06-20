package com.gvxwsur.unified_taming.event;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.config.subconfig.MiscConfig;
import com.gvxwsur.unified_taming.entity.api.CommandEntity;
import com.gvxwsur.unified_taming.entity.api.NeutralEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import com.gvxwsur.unified_taming.init.InitItems;
import com.gvxwsur.unified_taming.item.ControllingStaffItem;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
                if (UnifiedTamingUtils.shouldMobFireFriendly(living, newTarget)) {
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
                if (tamable.unified_taming$isTame()) {
                    LivingEntity owner = tamable.getOwner();
                    if (owner instanceof Player player) {
                        ((NeutralEntity) living).unified_taming$setLastHurtByPlayer(player, 100);
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
        if (!entity.level().isClientSide && entity instanceof Mob mob && UnifiedTamingUtils.isTame(mob)) {
            if (((CommandEntity) mob).unified_taming$unableToMove()) {
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
            if (CompatibilityConfig.COMPATIBLE_MOB_SUMMONED_TAMED.get() && mob.getSpawnType() == MobSpawnType.MOB_SUMMONED && !isLoadedFromDisk) {
                Mob mobOwner = level.getNearestEntity(Mob.class, TargetingConditions.forNonCombat().copy().range(8).selector(owner -> owner.getClass() != mob.getClass() && owner.getType().getCategory().isFriendly() == mob.getType().getCategory().isFriendly()), mob, mob.getX(), mob.getY(), mob.getZ(), mob.getBoundingBox().inflate(8));
                if (mobOwner != null) {
                    UnifiedTamingUtils.tameMob(mob, mobOwner);
                }
            }
            if (MiscConfig.GOLEM_CREATED_TAMED.get() && mob instanceof AbstractGolem && !isLoadedFromDisk) {
                if (mob.getSpawnType() == null) {
                    Player player = level.getNearestPlayer(mob, 6);
                    if (player != null) {
                        ((TamableEntity) mob).unified_taming$tame(player);
                        // no need to check tamable animal
                        UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message.unified_taming.tame", mob.getDisplayName()), true);
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
                if (UnifiedTamingUtils.getOwner(mob) instanceof ServerPlayer player) {
                    ((TamableEntity) outcomeMob).unified_taming$tame(player);
                    if (outcomeMob instanceof TamableAnimal tamableAnimal) {
                        tamableAnimal.tame(player);
                    }
                    UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message.unified_taming.convert", mob.getDisplayName(), outcomeMob.getDisplayName()), false);
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
                if (UnifiedTamingUtils.shouldFireFriendly(attacker, living)) {
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
            if (UnifiedTamingUtils.shouldFireFriendly(source, living)) {
                event.getEffectInstance().duration = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        Entity passenger = player.getFirstPassenger();
        boolean stopRiding = stack.is(InitItems.CONTROLLING_STAFF.get()) && ControllingStaffItem.getModeDesc(stack).equals("STOP_RIDING");
        if (stopRiding && passenger instanceof Mob) {
            passenger.stopRiding();
        }
        Entity vehicle = player.getVehicle();
        if (stopRiding && vehicle instanceof Mob) {
            player.stopRiding();
        }
    }
}
