package com.gvxwsur.tamabletool.common.entity.util;

import com.gvxwsur.tamabletool.common.entity.helper.MinionEntity;
import com.gvxwsur.tamabletool.common.entity.helper.TamableEntity;
import net.minecraft.world.entity.Mob;
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
}
