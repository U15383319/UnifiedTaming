package com.gvxwsur.tamabletool.common.entity.helper;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class TamableUtils {
    public static boolean isAlliedTo(Mob mob1, Mob mob2) {
        if (mob1.isAlliedTo(mob2)) {
            return true;
        }
        return !((TamableEntity) mob1).tamabletool$isTame() && !((TamableEntity) mob2).tamabletool$isTame() && !mob1.getType().getCategory().isFriendly() && !mob2.getType().getCategory().isFriendly();
    }

    public static void tameMob(Mob mob1, Mob mob2) {
        ((TamableEntity) mob1).tamabletool$setOwnerUUID(mob2.getUUID());
    }

    public static void tameMob(Mob mob1, Mob mob2, boolean tameOwner) {
        if (tameOwner && ((TamableEntity) mob2).tamabletool$isTame()) {
            ((TamableEntity) mob1).tamabletool$tame((Player) ((TamableEntity) mob2).getOwner());
        } else {
            tameMob(mob1, mob2);
        }
    }
}
