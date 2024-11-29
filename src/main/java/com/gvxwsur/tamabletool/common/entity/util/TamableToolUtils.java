package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.MinionEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEnvironment;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;

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
        return ((TamableEntity) mob).tamabletool$isOwnedBy(player) && !((MinionEntity) mob).tamabletool$isTameNonPlayer();
    }

    public static void tameMob(Mob mob1, Mob mob2) {
        ((MinionEntity) mob1).tamabletool$tameNonPlayer(mob2);
        tameMobOwner(mob1, mob2);
    }

    public static void tameMobOwner(Mob mob1, Mob mob2) {
        if (((TamableEntity) mob2).tamabletool$isTame()) {
            ((TamableEntity) mob1).tamabletool$tame((Player) ((TamableEntity) mob2).getOwner());
        }
    }

    public static TamableEnvironment getMobEnvironment(Mob mob) {
        if (mob.getNavigation() instanceof GroundPathNavigation) {
            if (mob instanceof FlyingMob) {
                // FlyingMob use default GroundPathNavigation
                return TamableEnvironment.FLY_WANDER;
            } else {
                return TamableEnvironment.GROUND;
            }
        }
        if (mob.getNavigation() instanceof FlyingPathNavigation) {
            return TamableEnvironment.FLY_PATH;
        }
        if (mob.getNavigation() instanceof WaterBoundPathNavigation) {
            return TamableEnvironment.WATER;
        }
        if (mob.getNavigation() instanceof AmphibiousPathNavigation) {
            return TamableEnvironment.AMPHIBIOUS;
        }
        if (mob instanceof Strider) {
            return TamableEnvironment.LAVA;
        }
        return TamableEnvironment.GROUND;
    }

    public static float getScaleFactor(Mob mob) {
        return getScaleFactor(mob, 0.6, 1.05, 1.44);
    }

    public static float getScaleFactor(Mob mob, double mul1, double mul2, double mul3) {
        double basicFactor = mob.getBoundingBox().getSize();
        double resultFactor = mul1 * (basicFactor / 1.05 - 1) + 1;
        TamableEnvironment environment = getMobEnvironment(mob);
        boolean canPathFly = environment == TamableEnvironment.FLY_PATH;
        boolean canSwim = environment == TamableEnvironment.WATER;
        boolean canWanderFly = environment == TamableEnvironment.FLY_WANDER;
        if (canPathFly || canSwim) {
            resultFactor *= mul2;
        }
        if (canWanderFly) {
            resultFactor *= mul3;
        }
        return (float) resultFactor;
    }
}
