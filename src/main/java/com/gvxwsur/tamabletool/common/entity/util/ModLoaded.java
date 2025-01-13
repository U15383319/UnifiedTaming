package com.gvxwsur.tamabletool.common.entity.util;

import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentNodeProcessor;
import com.github.alexthe666.iceandfire.pathfinding.NodeProcessorFly;
import com.github.alexthe668.domesticationinnovation.server.enchantment.DIEnchantmentRegistry;
import com.github.alexthe668.domesticationinnovation.server.entity.TameableUtils;
import com.github.mechalopa.hmag.world.entity.AbstractFlyingMonsterEntity;
import gaia.client.renderer.GaiaBabyMobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraftforge.fml.ModList;
import twilightforest.entity.passive.Bird;

public class ModLoaded {

    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isLavaNodeEvaluator(NodeEvaluator nodeEvaluator) {
        return isLoaded("alexsmobs") && nodeEvaluator instanceof BoneSerpentNodeProcessor;
    }

    public static boolean isFlyNodeEvaluator(NodeEvaluator nodeEvaluator) {
        return isLoaded("iceandfire") && nodeEvaluator instanceof NodeProcessorFly;
    }

    public static boolean isAmphibiousMob(Mob mob) {
        return isLoaded("domesticationinnovation") && TameableUtils.isTamed(mob) && TameableUtils.hasEnchant(mob, DIEnchantmentRegistry.AMPHIBIOUS);
    }

    public static boolean isFlyingMob(Mob mob) {
        return isLoaded("twilightforest") && mob instanceof Bird
                || isLoaded("hmag") && mob instanceof AbstractFlyingMonsterEntity;
    }

    public static boolean hasTameModified() {
        return isLoaded("domesticationinnovation");
    }

    public static boolean hasYoungModel(Entity entity) {
        return isLoaded("grimoireofgaia") && TamableToolUtils.getRenderer(entity) instanceof GaiaBabyMobRenderer;
    }
}
