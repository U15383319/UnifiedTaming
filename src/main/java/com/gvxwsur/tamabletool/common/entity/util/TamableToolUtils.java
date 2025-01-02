package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.*;
import com.gvxwsur.tamabletool.common.entity.helper.enumhelper.TamableEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.*;

public class TamableToolUtils {

    public static boolean isAlliedTo(Mob mob1, Mob mob2) {
        if (mob1.isAlliedTo(mob2)) {
            return true;
        }
        return !((TamableEntity) mob1).tamabletool$isTame() && !((TamableEntity) mob2).tamabletool$isTame() && !mob1.getType().getCategory().isFriendly() && !mob2.getType().getCategory().isFriendly();
    }

    public static boolean isTame(Mob mob) {
        return ((TamableEntity) mob).tamabletool$isTame() && !((MinionEntity) mob).tamabletool$isTameNonPlayer();
    }

    public static boolean isOwnedBy(Mob mob, Player player) {
        return isTame(mob) && ((TamableEntity) mob).tamabletool$isOwnedBy(player);
    }

    public static LivingEntity getOwner(Mob mob) {
        return isTame(mob) ? ((TamableEntity) mob).getOwner() : null;
    }

    public static boolean hasSameOwner(Mob mob1, Mob mob2) {
        return ((TamableEntity) mob2).getOwner() instanceof ServerPlayer player && ((TamableEntity) mob1).tamabletool$isOwnedBy(player);
    }

    public static void tameMob(Mob mob1, Mob mob2) {
        ((MinionEntity) mob1).tamabletool$tameNonPlayer(mob2);
        tameMobOwner(mob1, mob2);
    }

    public static void tameMobOwner(Mob mob1, Mob mob2) {
        if (((TamableEntity) mob2).getOwner() instanceof ServerPlayer player) {
            ((TamableEntity) mob1).tamabletool$tame(player);
        }
    }

    public static TamableEnvironment getMobEnvironment(Mob mob) {
        PathNavigation navigation = mob.getNavigation();
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
        if (nodeEvaluator instanceof AmphibiousNodeEvaluator) {
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

    public static float getScaleFactor(Mob mob) {
        return getScaleFactor(mob, 0.6, 1.05, 1.44, 2.0);
    }

    public static float getScaleFactor(Mob mob, double mul1, double mul2, double mul3, double mul4) {
        double basicFactor = mob.getBoundingBox().getSize();
        double resultFactor = mul1 * (basicFactor / 1.05 - 1) + 1;
        TamableEnvironment environment = ((EnvironmentHelper) mob).tamabletool$getEnvironment();
        if (environment == TamableEnvironment.FLY_PATH) {
            resultFactor *= mul2;
        }
        if (environment == TamableEnvironment.FLY_WANDER) {
            resultFactor *= mul3;
        }
        if (mob.isBaby()) {
            resultFactor *= mul4;
        }
        return (float) resultFactor;
    }

    public static boolean shouldMobFriendly(Entity attacker, LivingEntity target) {
        if (attacker instanceof Mob mob && ((TamableEntity) mob).tamabletool$isTame()) {
            return !((TamableEntity) mob).tamabletool$canTameAttack(target);
        }
        if (TamableToolConfig.compatiblePartEntity.get() && !(attacker instanceof Mob)) {
            Entity attackerAncestry = ((UniformPartEntity) attacker).getAncestry();
            if (attackerAncestry instanceof Mob attackerAncestryMob) {
                if (((TamableEntity) attackerAncestryMob).tamabletool$isTame()) {
                    return attackerAncestryMob == target || !((TamableEntity) attackerAncestryMob).tamabletool$canTameAttack(target);
                }
            }
        }
        return false;
    }

    public static boolean shouldFireFriendly(Entity attacker, LivingEntity target) {
        if (TamableToolConfig.playerFriendlyFire.get() && attacker instanceof ServerPlayer player) {
            if (target instanceof Mob mob && ((TamableEntity) mob).tamabletool$isOwnedBy(player)) {
                return true;
            }
            if (TamableToolConfig.compatiblePartEntity.get() && !(target instanceof Mob)) {
                Entity targetAncestry = ((UniformPartEntity) target).getAncestry();
                if (targetAncestry instanceof Mob targetAncestryMob) {
                    if (((TamableEntity) targetAncestryMob).tamabletool$isOwnedBy(player)) {
                        return true;
                    }
                }
            }
        }
        return shouldMobFriendly(attacker, target);
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
