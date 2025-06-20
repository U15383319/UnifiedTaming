package com.gvxwsur.unified_taming.util;

import com.gvxwsur.unified_taming.config.subconfig.CompatibilityConfig;
import com.gvxwsur.unified_taming.config.subconfig.MiscConfig;
import com.gvxwsur.unified_taming.entity.api.EnvironmentHelper;
import com.gvxwsur.unified_taming.entity.api.MinionEntity;
import com.gvxwsur.unified_taming.entity.api.TamableEntity;
import com.gvxwsur.unified_taming.entity.types.TamableEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UnifiedTamingUtils {

    public static final Log log = LogFactory.getLog(UnifiedTamingUtils.class);

    public static boolean isAlliedTo(Mob mob1, Mob mob2) {
        if (mob1.isAlliedTo(mob2)) {
            return true;
        }
        return !((TamableEntity) mob1).unified_taming$isTame() && !((TamableEntity) mob2).unified_taming$isTame() && !mob1.getType().getCategory().isFriendly() && !mob2.getType().getCategory().isFriendly();
    }

    public static boolean isTame(Mob mob) {
        return ((TamableEntity) mob).unified_taming$isTame() && !((MinionEntity) mob).unified_taming$isTameNonPlayer();
    }

    public static boolean isOwnedBy(Mob mob, Player player) {
        return isTame(mob) && ((TamableEntity) mob).unified_taming$isOwnedBy(player);
    }

    public static LivingEntity getOwner(Mob mob) {
        return isTame(mob) ? ((TamableEntity) mob).getOwner() : null;
    }

    public static boolean hasSameOwner(Mob mob1, Mob mob2) {
        return ((TamableEntity) mob2).getOwner() instanceof ServerPlayer player && ((TamableEntity) mob1).unified_taming$isOwnedBy(player);
    }

    public static void tameMob(Mob mob1, Mob mob2) {
        ((MinionEntity) mob1).unified_taming$tameNonPlayer(mob2);
        tameMobOwner(mob1, mob2);
    }

    public static void tameMobOwner(Mob mob1, Mob mob2) {
        if (((TamableEntity) mob2).getOwner() instanceof ServerPlayer player) {
            ((TamableEntity) mob1).unified_taming$tame(player);
            if (mob1 instanceof TamableAnimal tamableAnimal) {
                tamableAnimal.tame(player);
            }
        }
    }

    public static TamableEnvironment getMobEnvironment(Mob mob) {
        TamableEnvironment result = judgeMobEnvironmentByNavigation(mob, mob.getNavigation());
        try {
            Field[] fields = mob.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (PathNavigation.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    PathNavigation navigation = (PathNavigation) field.get(mob);
                    if (navigation.equals(mob.getNavigation())) {
                        continue;
                    }
                    TamableEnvironment environment = judgeMobEnvironmentByNavigation(mob, navigation);
                    if (environment != result) {
                        if (environment.isFloat()) {
                            result = environment;
                        } else if (environment.isGround() && result.isWaterSwim() || environment.isWaterSwim() && result.isGround()) {
                            result = TamableEnvironment.AMPHIBIOUS;
                        } else if (environment.isGround() && result.isLavaSwim() || environment.isLavaSwim() && result.isGround()) {
                            result = TamableEnvironment.LAVA_AMPHIBIOUS;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get mob environment", e);
        }
        return result;
    }

    private static TamableEnvironment judgeMobEnvironmentByNavigation(Mob mob, PathNavigation navigation) {
        NodeEvaluator nodeEvaluator = navigation.getNodeEvaluator();
        if (nodeEvaluator instanceof FlyNodeEvaluator || ModLoaded.isFlyNodeEvaluator(nodeEvaluator)) {
            return TamableEnvironment.FLY_PATH;
        }
        if (nodeEvaluator instanceof SwimNodeEvaluator) {
            return TamableEnvironment.WATER;
        }
        if (ModLoaded.isLavaNodeEvaluator(nodeEvaluator)) {
            return TamableEnvironment.LAVA;
        }
        if (nodeEvaluator instanceof AmphibiousNodeEvaluator || ModLoaded.isAmphibiousMob(mob)) {
            return TamableEnvironment.AMPHIBIOUS;
        }
        if (navigation instanceof GroundPathNavigation groundPathNavigation) {
            if (mob instanceof FlyingMob || mob instanceof FlyingAnimal || ModLoaded.isFlyingMob(mob)) {
                return TamableEnvironment.FLY_WANDER;
            } else if (groundPathNavigation.hasValidPathType(BlockPathTypes.LAVA)) {
                return TamableEnvironment.LAVA_SURFACE;
            } else if (groundPathNavigation instanceof WallClimberNavigation) {
                return TamableEnvironment.GROUND_WALL;
            }
        }
        if (mob.isNoGravity()) {
            return TamableEnvironment.FLY_WANDER;
        }
        return TamableEnvironment.GROUND;
    }

    public static float getScaleFactorBySize(Mob mob) {
        double basicFactor = mob.getBoundingBox().getSize();
        double resultFactor = 0.6 * (basicFactor / 1.05 - 1) + 1;
        TamableEnvironment environment = ((EnvironmentHelper) mob).unified_taming$getEnvironment();
        if (environment == TamableEnvironment.FLY_PATH) {
            resultFactor *= 1.05;
        }
        if (environment == TamableEnvironment.FLY_WANDER) {
            resultFactor *= 1.44;
        }
        if (mob.isBaby()) {
            resultFactor *= 2.0;
        }
        return (float) resultFactor;
    }

    public static float getScaledSpeed(Mob mob) {
        double baseFactor = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double resultFactor = 0.06 * (baseFactor / 0.23 - 1) + 1;
        return (float) (resultFactor * 0.23);
    }

    public static boolean shouldMobFireFriendly(Entity attacker, LivingEntity target) {
        if (attacker instanceof Mob mob && ((TamableEntity) mob).unified_taming$isTame()) {
            return !((TamableEntity) mob).unified_taming$canTameAttack(target);
        }
        if (CompatibilityConfig.COMPATIBLE_PART_ENTITY.get() && !(attacker instanceof Mob)) {
            Entity attackerAncestry = UnifiedTamingUtils.getAncestry(attacker);
            if (attackerAncestry instanceof Mob attackerAncestryMob) {
                if (((TamableEntity) attackerAncestryMob).unified_taming$isTame()) {
                    return attackerAncestryMob == target || !((TamableEntity) attackerAncestryMob).unified_taming$canTameAttack(target);
                }
            }
        }
        return false;
    }

    public static boolean shouldPlayerFireFriendly(Player player, LivingEntity target) {
        if (target instanceof Mob mob && ((TamableEntity) mob).unified_taming$isOwnedBy(player)) {
            return true;
        }
        if (CompatibilityConfig.COMPATIBLE_PART_ENTITY.get() && !(target instanceof Mob)) {
            Entity targetAncestry = UnifiedTamingUtils.getAncestry(target);
            if (targetAncestry instanceof Mob targetAncestryMob) {
                if (((TamableEntity) targetAncestryMob).unified_taming$isOwnedBy(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean shouldFireFriendly(Entity attacker, LivingEntity target) {
        if (MiscConfig.PLAYER_FRIENDLY_FIRE.get() && attacker instanceof ServerPlayer player) {
            return shouldPlayerFireFriendly(player, target);
        }
        return shouldMobFireFriendly(attacker, target);
    }

    public static Entity getAncestry(Entity entity) {
        if (entity instanceof Mob mob) {
            return mob;
        }
        Entity ancestry = null;
        try {
            Method method = entity.getClass().getDeclaredMethod("getParent");
            method.setAccessible(true);
            ancestry = (Entity) method.invoke(entity);
            while (ancestry != null && !(ancestry instanceof Mob)) {
                Method method1 = ancestry.getClass().getDeclaredMethod("getParent");
                method1.setAccessible(true);
                ancestry = (Entity) method1.invoke(ancestry);
            }
            if (ancestry instanceof Mob ancestryMob) {
                return ancestryMob;
            }
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                return ancestry != null ? ancestry : entity;
            } else {
                log.error("Failed to get ancestry", e);
            }
        }
        return entity;
    }

    public static void sendMessageToOwner(Mob mob, Component component, boolean pActionBar) {
        int messageLevel = MiscConfig.SHOW_TAMABLE_MESSAGE.get();
        if (messageLevel <= 0 || (messageLevel == 1 && !pActionBar)) {
            return;
        }
        LivingEntity owner = getOwner(mob);
        if (owner instanceof ServerPlayer player) {
            player.displayClientMessage(component, pActionBar);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Entity> EntityRenderer<T> getRenderer(T entity) {
        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        return (EntityRenderer<T>) renderManager.getRenderer(entity);
    }

    public static boolean hasYoungModel(Entity entity) {
        return ModLoaded.hasYoungModel(entity) || getRenderer(entity) instanceof LivingEntityRenderer<?,?> livingEntityRenderer && livingEntityRenderer.getModel() instanceof AgeableListModel;
    }
}
