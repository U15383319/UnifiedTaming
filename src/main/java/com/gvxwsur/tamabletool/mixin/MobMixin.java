package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.entity.goal.*;
import com.gvxwsur.tamabletool.common.entity.helper.*;
import com.gvxwsur.tamabletool.common.entity.util.MessageSender;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting, TamableEntity, InteractEntity, MinionEntity, CommandEntity {

    @Shadow
    protected PathNavigation navigation;

    @Shadow
    @Final
    public GoalSelector goalSelector;

    @Shadow
    @Final
    public GoalSelector targetSelector;

    @Shadow
    public abstract boolean isLeashed();

    @Shadow
    public abstract void setTarget(@Nullable LivingEntity p_21544_);

    @Shadow
    public abstract void restrictTo(BlockPos p_21447_, int p_21448_);

    @Unique
    private static final EntityDataAccessor<Byte> tamabletool$DATA_FLAGS_ID;

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> tamabletool$DATA_OWNERUUID_ID;

    @Unique
    private TamableCommand tamabletool$command;

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> tamabletool$DATA_NONPLAYEROWNERUUID_ID;

    protected MobMixin(EntityType<? extends LivingEntity> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(tamabletool$DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(tamabletool$DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(tamabletool$DATA_NONPLAYEROWNERUUID_ID, Optional.empty());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag p_21819_, CallbackInfo ci) {
        if (this.getOwnerUUID() != null) {
            p_21819_.putUUID("Owner", this.getOwnerUUID());
        }

        if (this.tamabletool$getOwnerUUID() != null) {
            p_21819_.putUUID("PlayerOwner", this.tamabletool$getOwnerUUID());
        }

        if (this.tamabletool$getOwnerUUID() != null) {
            p_21819_.putInt("Command", this.tamabletool$getCommandInt());
        }

        if (this.tamabletool$getNonPlayerOwnerUUID() != null) {
            p_21819_.putUUID("NonPlayerOwner", this.tamabletool$getNonPlayerOwnerUUID());
        }

    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag p_21815_, CallbackInfo ci) {
        MessageSender.setQuiet(true);

        // UUID Owner will be ignored to ensure that this mod will not influence vanilla TamableAnimal
        UUID uuid;
        if (p_21815_.hasUUID("PlayerOwner")) {
            uuid = p_21815_.getUUID("PlayerOwner");
        } else {
            String s = p_21815_.getString("PlayerOwner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.tamabletool$setOwnerUUID(uuid);
                this.tamabletool$setTame(true);
            } catch (Throwable var4) {
                this.tamabletool$setTame(false);
            }
        }

        if (uuid != null) {
            this.tamabletool$setCommandInt(p_21815_.getInt("Command"));
            this.tamabletool$setInSittingPose(this.tamabletool$isOrderedToSit());
        }

        UUID nonPlayerUUID;
        if (p_21815_.hasUUID("NonPlayerOwner")) {
            nonPlayerUUID = p_21815_.getUUID("NonPlayerOwner");
        } else {
            nonPlayerUUID = null;
        }

        if (nonPlayerUUID != null) {
            try {
                this.tamabletool$setNonPlayerOwnerUUID(nonPlayerUUID);
                this.tamabletool$setTameNonPlayer(true);
            } catch (Throwable var4) {
                this.tamabletool$setTameNonPlayer(false);
            }
        }

        MessageSender.setQuiet(false);

        this.goalSelector.addGoal(2, new CustomSitWhenOrderedToGoal((Mob) (Object) this));
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal((Mob) (Object) this, 1.0, 8.0F, 2.0F));
        this.goalSelector.addGoal(8, new CustomRandomStrollGoal((Mob) (Object) this, 1.0));
        this.goalSelector.addGoal(10, new CustomLookAtOwnerGoal((Mob) (Object) this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal((Mob) (Object) this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal((Mob) (Object) this));
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.isPassenger() || TamableToolUtils.isTame((Mob) (Object) this));
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    public void canBeLeashed(Player p_21813_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(TamableToolUtils.isTame((Mob) (Object) this) && !this.isLeashed());
    }

    @Unique
    protected void tamabletool$spawnTamingParticles(boolean p_21835_) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!p_21835_) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
        }

    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    public void handleEntityEvent(byte p_21807_, CallbackInfo ci) {
        if (p_21807_ == 7) {
            this.tamabletool$spawnTamingParticles(true);
        } else if (p_21807_ == 6) {
            this.tamabletool$spawnTamingParticles(false);
        }
    }

    public boolean tamabletool$isTame() {
        return (this.entityData.get(tamabletool$DATA_FLAGS_ID) & 4) != 0;
    }

    public void tamabletool$setTame(boolean p_21836_) {
        byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 4));
        } else {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -5));
        }
    }

    public boolean tamabletool$isInSittingPose() {
        return (this.entityData.get(tamabletool$DATA_FLAGS_ID) & 1) != 0;
    }

    public void tamabletool$setInSittingPose(boolean p_21838_) {
        byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
        if (p_21838_) {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -2));
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(tamabletool$DATA_OWNERUUID_ID)).orElse(null);
    }

    @Nullable
    public UUID tamabletool$getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(tamabletool$DATA_OWNERUUID_ID)).orElse(null);
    }

    public void tamabletool$setOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(tamabletool$DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public void tamabletool$tame(Player p_21829_) {
        this.tamabletool$setTame(true);
        this.tamabletool$setOwnerUUID(p_21829_.getUUID());
        if (p_21829_ instanceof ServerPlayer player) {
            MessageSender.sendTamingMessage((Mob)(Object) this, player);
            ((TameAnimalTriggerHelper) CriteriaTriggers.TAME_ANIMAL).tamabletool$trigger(player, (Mob)(Object) this);
        }
    }

    public boolean canAttack(LivingEntity p_21822_) {
        if (p_21822_ instanceof OwnableEntity owned && owned.getOwner() != null && this.tamabletool$isOwnedBy(owned.getOwner())) {
            return false;
        }
        return !this.tamabletool$isOwnedBy(p_21822_) && super.canAttack(p_21822_);
    }

    public boolean tamabletool$isOwnedBy(LivingEntity p_21831_) {
        return p_21831_ == this.getOwner();
    }

    public boolean tamabletool$wantsToAttack(LivingEntity p_21810_, LivingEntity p_21811_) {
        return true;
    }

    public Team getTeam() {
        if (this.tamabletool$isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isAlliedTo(Entity p_21833_) {
        if (this.tamabletool$isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (p_21833_ == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(p_21833_);
            }
        }

        return super.isAlliedTo(p_21833_);
    }

    public void die(DamageSource p_21809_) {
        Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(p_21809_);
        if (!this.level().isClientSide && this.dead) {
            MessageSender.sendDeathMessage((Mob)(Object) this, deathMessage);
        }
    }

    public TamableCommand tamabletool$getCommand() {
        return this.tamabletool$command == null ? TamableCommand.FOLLOW : this.tamabletool$command;
    }

    public void tamabletool$setCommand(TamableCommand command) {
        this.tamabletool$command = command;
        if (command == TamableCommand.STROLL) {
            this.restrictTo(this.blockPosition(), 16);
        } else {
            this.restrictTo(BlockPos.ZERO, -1);
        }
        if (!this.level().isClientSide) {
            MessageSender.sendCommandMessage((Mob) (Object) this, command.toString().toLowerCase());
        }
    }

    static {
        tamabletool$DATA_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
        tamabletool$DATA_OWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
        tamabletool$DATA_NONPLAYEROWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
    }

    public boolean tamabletool$isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible();
    }

    public float tamabletool$healValue(ItemStack p_30440_) {
        FoodProperties foodProperties = p_30440_.getFoodProperties(this);
        if (foodProperties == null) {
            return 1.0F;
        }
        return (float) foodProperties.getNutrition();
    }

    public boolean tamabletool$isRider(ItemStack p_30440_) {
        return p_30440_.isEmpty();
    }

    public boolean tamabletool$isCommander(ItemStack p_30440_) {
        return p_30440_.isEmpty();
    }

    public boolean tamabletool$isRideModeSwitcher(ItemStack p_30440_) {
        return p_30440_.is(Items.COMPASS);
    }

    public boolean tamabletool$isMoveModeSwitcher(ItemStack p_30440_) {
        return p_30440_.is(Items.COMPASS);
    }

    public boolean tamabletool$isTamer(ItemStack p_30440_) {
        return p_30440_.is(Items.BOOK);
    }

    public boolean tamabletool$isTamingConditionSatisfied() {
        return this.getHealth() <= Mth.clamp(this.getMaxHealth() / 5, 4.0, 12.0);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack assistItemstack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (this.tamabletool$isModAssistant(assistItemstack)) {
            if (this.level().isClientSide) {
                boolean flag = this.tamabletool$isOwnedBy(player) || this.tamabletool$isTame() || (this.tamabletool$isTamer(itemstack) || this.tamabletool$isCheatTamer(itemstack)) && !this.tamabletool$isTame();
                boolean flag2 = !this.isVehicle() && this.tamabletool$isOwnedBy(player) && this.tamabletool$isRider(itemstack) && !player.isSecondaryUseActive();
                if (flag2) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else if (flag) {
                    cir.setReturnValue(InteractionResult.CONSUME);
                } else {
                    cir.setReturnValue(InteractionResult.PASS);
                }
            } else if (this.tamabletool$isTameNonPlayer()) {
              cir.setReturnValue(InteractionResult.PASS);
            } else if (this.tamabletool$isTame()) {
                if (this.tamabletool$isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal(this.tamabletool$healValue(itemstack));
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.gameEvent(GameEvent.EAT, this);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    if (this.tamabletool$isOwnedBy(player)) {
                        if (this.tamabletool$isCommander(itemstack)) {
                            if (player.isSecondaryUseActive()) {
                                this.tamabletool$setOrderedToSit(!this.tamabletool$isOrderedToSit());
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget(null);
                                cir.setReturnValue(InteractionResult.SUCCESS);
                            }
                        }
                        if (this.tamabletool$isRider(itemstack)) {
                            if (!player.isSecondaryUseActive() && !this.isVehicle()) {
                                player.startRiding(this);
                                cir.setReturnValue(InteractionResult.CONSUME);
                            }
                        }
                        if (this.tamabletool$isMoveModeSwitcher(itemstack)) {
                            if (player.isSecondaryUseActive()) {
                                this.tamabletool$setOrderedToStroll(!this.tamabletool$isOrderedToStroll());
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget(null);
                                cir.setReturnValue(InteractionResult.SUCCESS);
                            }
                        }
                        if (this.tamabletool$isRideModeSwitcher(itemstack)) {
                            // TODO: switch ride mode?
                        }
                    }
                    cir.setReturnValue(InteractionResult.PASS);
                }
            } else if ((this.tamabletool$isTamer(itemstack) && this.tamabletool$isTamingConditionSatisfied()) || this.tamabletool$isCheatTamer(itemstack)) {
                if (this.tamabletool$isTamer(itemstack) && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.tamabletool$isCheatTamer(itemstack) || this.random.nextInt(3) == 0) {
                    this.tamabletool$tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    // this.tamabletool$setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }

                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    public UUID tamabletool$getNonPlayerOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(tamabletool$DATA_NONPLAYEROWNERUUID_ID)).orElse(null);
    }

    public boolean tamabletool$isTameNonPlayer() {
        return (this.entityData.get(tamabletool$DATA_FLAGS_ID) & 16) != 0;
    }

    public void tamabletool$setTameNonPlayer(boolean p_21836_) {
        byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 16));
        } else {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -17));
        }
    }

    public void tamabletool$setNonPlayerOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(tamabletool$DATA_NONPLAYEROWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public void tamabletool$tameNonPlayer(Mob p_21829_) {
        this.tamabletool$setTameNonPlayer(true);
        this.tamabletool$setNonPlayerOwnerUUID(p_21829_.getUUID());
    }

    @Unique
    public boolean tamabletool$unableToMove() {
        return this.tamabletool$isOrderedToSit() || this.isPassenger() || this.isLeashed();
    }
}
