package com.gvxwsur.unified_taming.item;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.entity.api.*;
import com.gvxwsur.unified_taming.init.InitItems;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class MultiToolItem extends Item {

    private static final String MODE_TAG = "ToolMode";

    public MultiToolItem() {
        super(new Properties().setNoRepair());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity livingEntity, @NotNull InteractionHand hand) {
        Level level = player.level();
        Entity interactTarget = (livingEntity instanceof Mob) ? livingEntity : UnifiedTamingUtils.getAncestry(livingEntity);
        MultiToolItemMode current = getMode(stack);
        if (stack.is(InitItems.MULTI_TOOL_ITEM.get()) && interactTarget instanceof Mob mob && UnifiedTamingUtils.isOwnedBy(mob, player)) {
            switch (current) {
                case FOLLOW_OR_SIT -> {
                    if (!level.isClientSide()) {
                        ((CommandEntity)mob).unified_taming$setOrderedToSit(!((CommandEntity)mob).unified_taming$isOrderedToSit());
                        String command = ((CommandEntity)mob).unified_taming$getCommand().getLang();
                        UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message" + command, mob.getDisplayName()), true);
                        mob.setJumping(false);
                        mob.getNavigation().stop();
                        mob.setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(!level.isClientSide());
                }
                case FOLLOW_OR_STROLL -> {
                    if (!level.isClientSide()) {
                        ((CommandEntity)mob).unified_taming$setOrderedToStroll(!((CommandEntity)mob).unified_taming$isOrderedToStroll());
                        String command = ((CommandEntity)mob).unified_taming$getCommand().getLang();
                        UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message." + command, mob.getDisplayName()), true);
                        mob.setJumping(false);
                        mob.getNavigation().stop();
                        mob.setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(!level.isClientSide());
                }
                case RIDE_MODE -> {
                    if (!((BreedableHelper)mob).unified_taming$isBaby()) {
                        if (!level.isClientSide()) {
                            ((RideableEntity)mob).unified_taming$setManual(!((RideableEntity)mob).unified_taming$isManual());
                            String isManual = UnifiedTaming.MOD_ID + ".ride." + (((RideableEntity)mob).unified_taming$isManual() ? "manual" : "automatic");
                            UnifiedTamingUtils.sendMessageToOwner(mob, Component.translatable("message." + isManual, mob.getDisplayName()), true);
                        }
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                }
                case RIDE -> {
                    if (!mob.isVehicle() && !((BreedableHelper) mob).unified_taming$isBaby()) {
                        player.startRiding(mob);
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
                case CARRY -> {
                    if (!player.isVehicle()) {
                        mob.startRiding(player);
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
                case STOP_RIDING -> {
                    // not implemented here
                }
                case FEED -> {
                    InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack stack1 = player.getItemInHand(otherHand);
                    if (((BreedableHelper)mob).unified_taming$isBreedFood(stack1)) {
                        if (!level.isClientSide()) {
                            if (((BreedableHelper)mob).unified_taming$canFallInLove()) {
                                if (!player.getAbilities().instabuild) {
                                    stack1.shrink(1);
                                }
                                ((BreedableHelper)mob).unified_taming$setInLove();
                            }
                        }
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                    if (((InteractEntity)mob).unified_taming$isFood(stack1)) {
                        if (mob.getHealth() < mob.getMaxHealth()) {
                            if (!level.isClientSide()) {
                                if (stack1.isEdible()) {
                                    FoodProperties foodProperties = stack1.getFoodProperties(mob);
                                    float totalValue = foodProperties.getNutrition() + foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2;
                                    mob.heal(totalValue);
                                }
                                mob.eat(mob.level(), stack1);
                            }
                            return InteractionResult.sidedSuccess(!level.isClientSide());
                        }
                        if (((BreedableHelper)mob).unified_taming$isBaby()) {
                            if (mob instanceof AgeableMob ageableMob) {
                                if (!player.getAbilities().instabuild) {
                                    stack1.shrink(1);
                                }
                                ageableMob.ageUp(100);
                            }
                            return InteractionResult.sidedSuccess(level.isClientSide());
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        MultiToolItemMode current = getMode(stack);
        tooltip.add(Component.translatable(
                "tooltip." + UnifiedTaming.MOD_ID + ".multi_tool", Component.translatable(current.getLang())
        ));
    }

    private static MultiToolItemMode getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return MultiToolItemMode.values()[tag.getInt(MODE_TAG)];
    }

    public static String getModeDesc(ItemStack stack) {
        return getMode(stack).toString();
    }

    public static void switchMode(ItemStack stack, Player player) {
        int currentId = getMode(stack).ordinal();
        int nextId = (currentId + 1) % MultiToolItemMode.values().length;
        stack.getOrCreateTag().putInt(MODE_TAG, nextId);
        player.displayClientMessage(Component.translatable(MultiToolItemMode.values()[nextId].getLang()), true);
    }
}

enum MultiToolItemMode {
    FOLLOW_OR_SIT,
    FOLLOW_OR_STROLL,
    RIDE_MODE,
    RIDE,
    CARRY,
    STOP_RIDING,
    FEED;

    public String getLang() {
        return "item." + UnifiedTaming.MOD_ID + ".multi_tool." + "mode." + this.toString().toLowerCase(Locale.ROOT);
    }
}