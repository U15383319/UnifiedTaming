package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.config.UnifiedTamingConfig;
import com.gvxwsur.unified_taming.entity.api.*;
import com.gvxwsur.unified_taming.entity.goal.*;
import com.gvxwsur.unified_taming.entity.types.TamableCommand;
import com.gvxwsur.unified_taming.entity.types.TamableEnvironment;
import com.gvxwsur.unified_taming.util.MessageSender;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
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
public abstract class MobMixin extends LivingEntity implements Targeting, TamableEntity, InteractEntity, MinionEntity, CommandEntity, AiRideableEntity, BreedableHelper, EnvironmentHelper {

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

    @Shadow
    @Nullable
    public abstract Entity getLeashHolder();

    @Shadow
    public abstract PathNavigation getNavigation();

    @Shadow
    public abstract void setBaby(boolean p_21451_);

    @Unique
    private static final EntityDataAccessor<Byte> unified_taming$DATA_FLAGS_ID; // sit 0 manual 1 tame(0) 2 tame(1) 3 baby 4

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> unified_taming$DATA_OWNERUUID_ID;

    @Unique
    private TamableCommand unified_taming$command;

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> unified_taming$DATA_NONPLAYEROWNERUUID_ID;

    @Unique
    private static final EntityDataAccessor<Byte> unified_taming$DATA_TYPES_ID; // environment 0 1 2 3

    @Unique
    private boolean unified_taming$keyForward;
    @Unique
    private boolean unified_taming$keyBack;
    @Unique
    private boolean unified_taming$keyLeft;
    @Unique
    private boolean unified_taming$keyRight;

    @Unique
    private boolean unified_taming$isManual;

    @Unique
    private TamableEnvironment unified_taming$environment;

    @Unique
    private int unified_taming$inLove;

    protected MobMixin(EntityType<? extends LivingEntity> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.unified_taming$keyForward = false;
        this.unified_taming$keyBack = false;
        this.unified_taming$keyLeft = false;
        this.unified_taming$keyRight = false;
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean unified_taming$keyForward() {
        return Minecraft.getInstance().options.keyUp.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean unified_taming$keyBack() {
        return Minecraft.getInstance().options.keyDown.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean unified_taming$keyLeft() {
        return Minecraft.getInstance().options.keyLeft.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean unified_taming$keyRight() {
        return Minecraft.getInstance().options.keyRight.isDown();
    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(unified_taming$DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(unified_taming$DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(unified_taming$DATA_NONPLAYEROWNERUUID_ID, Optional.empty());
        this.entityData.define(unified_taming$DATA_TYPES_ID, (byte) 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }

        if (this.unified_taming$getOwnerUUID() != null) {
            compoundTag.putUUID("PlayerOwner", this.unified_taming$getOwnerUUID());
        }

        if (this.unified_taming$getNonPlayerOwnerUUID() != null) {
            compoundTag.putUUID("NonPlayerOwner", this.unified_taming$getNonPlayerOwnerUUID());
        }

        if (this.unified_taming$getOwnerUUID() != null && this.unified_taming$getNonPlayerOwnerUUID() == null) {
            compoundTag.putInt("Command", this.unified_taming$getCommand().ordinal());
            compoundTag.putBoolean("RideMode", this.unified_taming$isManual());
            compoundTag.putInt("Environment", this.unified_taming$getEnvironment().ordinal());
            compoundTag.putBoolean("IsBaby", this.unified_taming$isBaby());
            compoundTag.putInt("InLove", this.unified_taming$getInLoveTime());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        UUID uuid;
        if (compoundTag.hasUUID("Owner")) {
            uuid = compoundTag.getUUID("Owner");
        } else {
            String s = compoundTag.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        // UUID Owner will not be set to ensure that this mod will not influence vanilla TamableAnimal
        /*
        if (uuid != null) {
            try {
                this.unified_taming$setOwnerUUID(uuid);
                this.unified_taming$setTame(true);
            } catch (Throwable var4) {
                this.unified_taming$setTame(false);
            }
        }
        */

        UUID playerUUID;
        if (compoundTag.hasUUID("PlayerOwner")) {
            playerUUID = compoundTag.getUUID("PlayerOwner");
        } else {
            String s = compoundTag.getString("PlayerOwner");
            playerUUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (playerUUID != null) {
            try {
                this.unified_taming$setOwnerUUID(playerUUID);
                this.unified_taming$setTame(true);
            } catch (Throwable var4) {
                this.unified_taming$setTame(false);
            }
        }

        UUID nonPlayerUUID;
        if (compoundTag.hasUUID("NonPlayerOwner")) {
            nonPlayerUUID = compoundTag.getUUID("NonPlayerOwner");
        } else {
            nonPlayerUUID = null;
        }

        if (nonPlayerUUID != null) {
            try {
                this.unified_taming$setNonPlayerOwnerUUID(nonPlayerUUID);
                this.unified_taming$setTameNonPlayer(true);
            } catch (Throwable var4) {
                this.unified_taming$setTameNonPlayer(false);
            }
        }

        if (playerUUID != null && nonPlayerUUID == null) {
            this.unified_taming$setCommand(TamableCommand.values()[compoundTag.getInt("Command")]);
            this.unified_taming$setManual(compoundTag.getBoolean("RideMode"));
            this.unified_taming$setEnvironment(TamableEnvironment.values()[compoundTag.getInt("Environment")]);
            this.setBaby(compoundTag.getBoolean("IsBaby"));
            this.unified_taming$setBaby(compoundTag.getBoolean("IsBaby"));
            this.unified_taming$setInLoveTime(compoundTag.getInt("InLove"));
        }

        if ((uuid != null || playerUUID != null) && nonPlayerUUID == null) {
            this.unified_taming$registerTameGoals();
        }
    }

    @Unique
    public void unified_taming$registerTameGoals() {
        this.goalSelector.addGoal(1, new CustomSitWhenOrderedToGoal((Mob) (Object) this));
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal((Mob) (Object) this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new CustomBreedGoal((Mob) (Object) this, 1.0));
        if ((Mob) (Object) this instanceof PathfinderMob pathfinderMob) {
            this.goalSelector.addGoal(8, new CustomRandomStrollGoal(pathfinderMob, 1.0));
        }
        this.goalSelector.addGoal(10, new CustomLookAtOwnerGoal((Mob) (Object) this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal((Mob) (Object) this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal((Mob) (Object) this));
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo ci) {
        if (this.isAlive()) {
            if (!this.firstTick && this.tickCount % 20 == 0) {
                if (this.getOwnerUUID() != null && this.getOwnerUUID() != this.unified_taming$getOwnerUUID()) {
                    this.unified_taming$setOwnerUUID(this.getOwnerUUID());
                    this.unified_taming$setTame(true);
                }
                if (this.isBaby() != this.unified_taming$isBaby()) {
                    this.unified_taming$setBaby(this.isBaby());
                }
            }

            if (this.unified_taming$isTame()) {
                int inLove = this.unified_taming$getInLoveTime();
                if (inLove < 0) {
                    ++inLove;
                    this.unified_taming$setInLoveTime(inLove);
                } else if (inLove > 0) {
                    --inLove;
                    this.unified_taming$setInLoveTime(inLove);
                    if (inLove % 10 == 0) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
                    }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        this.unified_taming$setInLoveTime(0);
        Entity source = p_21016_.getEntity();
        if (!(source instanceof Player player && this.unified_taming$isOwnedBy(player))) {
            if (this.unified_taming$isOrderedToSit()) {
                this.unified_taming$setOrderedToFollow(true);
                if (!this.level().isClientSide) {
                    MessageSender.sendHurtWhenStopMessage((Mob) (Object) this, false);
                }
            }
        }
        return super.hurt(p_21016_, p_21017_);
    }

    public void unified_taming$travel(Vec3 vec3) {
        Entity entity = this.getControllingPassenger();
        if (entity instanceof Player player && this.isVehicle()) {
            if (level().isClientSide) {
                unified_taming$keyForward = unified_taming$keyForward();
                unified_taming$keyBack = unified_taming$keyBack();
                unified_taming$keyLeft = unified_taming$keyLeft();
                unified_taming$keyRight = unified_taming$keyRight();
            }

            float strafe = unified_taming$keyLeft ? 0.5f : (unified_taming$keyRight ? -0.5f : 0);
            float vertical = unified_taming$keyForward ? -(player.getXRot() - 10) / 22.5f : 0;
            float forward = unified_taming$keyForward ? 3 : (unified_taming$keyBack ? -0.5f : 0);

            boolean canVerticalMove = this.unified_taming$getEnvironment().isFly()
                    || (this.unified_taming$getEnvironment().isWaterSwim() && this.isInWater())
                    || (this.unified_taming$getEnvironment().isLavaSwim() && this.isInLava());
            vertical = canVerticalMove ? vertical : 0;

            float speed = (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * Mth.clamp(UnifiedTamingConfig.rideSpeedModifier.get(), 0.0, 1.0));

            this.moveRelative(speed, new Vec3(strafe, vertical, forward));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 pTravelVector) {
        this.fallDistance = 0;

        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.setRot(player.getYRot(), player.getXRot());

        super.tickRidden(player, pTravelVector);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.unified_taming$isTame()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player player && this.unified_taming$isManual()) {
                cir.setReturnValue(player);
            } else {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.isPassenger() || UnifiedTamingUtils.isTame((Mob) (Object) this));
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    public void canBeLeashed(Player p_21813_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isLeashed() && (!UnifiedTamingConfig.leashedNeedTamed.get() || UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, p_21813_)));
    }

    @Unique
    protected void unified_taming$spawnTamingParticles(boolean p_21835_) {
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
            this.unified_taming$spawnTamingParticles(true);
        } else if (p_21807_ == 6) {
            this.unified_taming$spawnTamingParticles(false);
        } else if (p_21807_ == 18) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
        }
    }

    public boolean unified_taming$isTame() {
        return (this.entityData.get(unified_taming$DATA_FLAGS_ID) & 4) != 0;
    }

    public void unified_taming$setTame(boolean p_21836_) {
        byte b0 = this.entityData.get(unified_taming$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 | 4));
        } else {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 & -5));
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return unified_taming$getOwnerUUID();
    }

    @Nullable
    public UUID unified_taming$getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(unified_taming$DATA_OWNERUUID_ID)).orElse(null);
    }

    public void unified_taming$setOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(unified_taming$DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public void unified_taming$tame(Player player) {
        this.unified_taming$setTame(true);
        this.unified_taming$setOwnerUUID(player.getUUID());
        if (player instanceof ServerPlayer player1) {
            ((AnimalTriggerHelper) CriteriaTriggers.TAME_ANIMAL).unified_taming$TameAnimal$trigger(player1, (Mob) (Object) this);
        }
        this.unified_taming$registerTameGoals();
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity livingEntity) {
        return this.unified_taming$canTameAttack(livingEntity) && super.canAttack(livingEntity);
    }

    public boolean unified_taming$canTameAttack(LivingEntity livingEntity) {
        if (this.unified_taming$isOwnedBy(livingEntity)) {
            return false;
        }
        if (livingEntity instanceof Mob mob && UnifiedTamingUtils.hasSameOwner((Mob) (Object) this, mob)) {
            return false;
        }
        if (UnifiedTamingConfig.compatiblePartEntity.get() && !(livingEntity instanceof Mob)) {
            Entity targetAncestry = ((UniformPartEntity) livingEntity).getAncestry();
            if (targetAncestry instanceof Mob targetAncestryMob) {
                if ((Object) this == targetAncestryMob) {
                    return false;
                }
                if (UnifiedTamingUtils.hasSameOwner((Mob) (Object) this, targetAncestryMob)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean unified_taming$isOwnedBy(LivingEntity p_21831_) {
        return p_21831_ == this.getOwner();
    }

    @Override
    public Team getTeam() {
        if (this.unified_taming$isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity p_21833_) {
        if (this.unified_taming$isTame()) {
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

    @Override
    public void die(DamageSource p_21809_) {
        Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(p_21809_);
        if (!this.level().isClientSide && this.dead) {
            if ((Mob) (Object) this instanceof TamableAnimal) {
                return;
            }
            MessageSender.sendDeathMessage((Mob) (Object) this, deathMessage, false);
        }
    }

    public TamableCommand unified_taming$getCommand() {
        return this.unified_taming$command == null ? TamableCommand.FOLLOW : this.unified_taming$command;
    }

    public void unified_taming$setCommand(TamableCommand command) {
        this.unified_taming$command = command;
        if (!((Mob) (Object) this instanceof TamableAnimal && !UnifiedTamingConfig.compatibleVanillaTamableMovingGoals.get())) {
            if (command == TamableCommand.STROLL) {
                this.restrictTo(this.blockPosition(), 16);
            } else {
                this.restrictTo(BlockPos.ZERO, -1);
            }
            if ((Mob) (Object) this instanceof TamableAnimal tamableAnimal) {
                tamableAnimal.setOrderedToSit(command == TamableCommand.SIT);
            }
        }
    }

    static {
        unified_taming$DATA_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
        unified_taming$DATA_OWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
        unified_taming$DATA_NONPLAYEROWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
        unified_taming$DATA_TYPES_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
    }

    public boolean unified_taming$isFood(ItemStack stack) {
        return stack.isEdible();
    }

    public boolean unified_taming$isRider(ItemStack stack) {
        return stack.isEmpty();
    }

    public boolean unified_taming$isCommander(ItemStack stack) {
        return stack.isEmpty();
    }

    public boolean unified_taming$isRideModeSwitcher(ItemStack stack) {
        return stack.is(Items.COMPASS);
    }

    public boolean unified_taming$isMoveModeSwitcher(ItemStack stack) {
        return stack.is(Items.COMPASS);
    }

    public boolean unified_taming$isTamer(ItemStack stack) {
        return stack.is(Items.BOOK);
    }

    public boolean unified_taming$isCarrier(ItemStack stack) {
        return stack.is(Items.CLOCK);
    }

    public boolean unified_taming$isCarryReleaser(ItemStack stack) {
        return stack.is(Items.CLOCK);
    }

    public boolean unified_taming$isTamingConditionSatisfied() {
        return this.getHealth() <= Mth.clamp(this.getMaxHealth() / 5, 4.0, 12.0);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public final void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult interactionresult = this.unified_taming$tameInteract(player, hand);
        if (interactionresult.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            cir.setReturnValue(interactionresult);
        }
    }

    @Unique
    public InteractionResult unified_taming$tameInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack assistItemstack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (this.unified_taming$isModAssistant(assistItemstack)) {
            if (this.level().isClientSide) {
                // client side
                boolean flag = this.unified_taming$isTame() || (this.unified_taming$isTamer(itemstack) || this.unified_taming$isCheatTamer(itemstack)) && !this.unified_taming$isTame();
                boolean flag2 = UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, player) && !player.isSecondaryUseActive() && this.unified_taming$isRider(itemstack) && !this.isVehicle() && !this.unified_taming$isBaby();
                boolean flag3 = this.unified_taming$isTame() && this.unified_taming$isBaby() && this.unified_taming$isFood(itemstack) && this.getHealth() >= this.getMaxHealth();
                boolean flag4 = UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, player) && !player.isSecondaryUseActive() && this.unified_taming$isCarrier(itemstack) && !player.isVehicle() && !player.isBaby();
                if (flag2) {
                    player.startRiding(this);
                }
                if (flag4) {
                    this.startRiding(player);
                }
                if (flag3) {
                    if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        ageableMob.ageUp(100);
                    }
                }
                if (flag2 || flag3 || flag4) {
                    return InteractionResult.SUCCESS;
                } else if (flag) {
                    return InteractionResult.CONSUME;
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                // server side
                if (this.unified_taming$isTame()) {
                    if (this.unified_taming$isFood(itemstack)) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            if (itemstack.isEdible()) {
                                FoodProperties foodProperties = itemstack.getFoodProperties(this);
                                float totalValue = foodProperties.getNutrition() + foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2;
                                this.heal(totalValue);
                            }
                            this.eat(this.level(), itemstack);
                            return InteractionResult.SUCCESS;
                        }
                        if (this.unified_taming$isBaby()) {
                            if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
                                if (!player.getAbilities().instabuild) {
                                    itemstack.shrink(1);
                                }
                                ageableMob.ageUp(100);
                            }
                            return InteractionResult.CONSUME;
                        }
                    } else {
                        if (UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, player)) {
                            if (this.unified_taming$isBreedFood(itemstack)) {
                                if (this.unified_taming$canFallInLove()) {
                                    if (!player.getAbilities().instabuild) {
                                        itemstack.shrink(1);
                                    }
                                    this.unified_taming$setInLove();
                                    return InteractionResult.SUCCESS;
                                }
                            }
                            if (this.unified_taming$isCommander(itemstack)) {
                                if (player.isSecondaryUseActive()) {
                                    this.unified_taming$setOrderedToSit(!this.unified_taming$isOrderedToSit());
                                    MessageSender.sendCommandMessage((Mob) (Object) this, true);
                                    this.jumping = false;
                                    this.navigation.stop();
                                    this.setTarget(null);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                            if (this.unified_taming$isRider(itemstack)) {
                                if (!player.isSecondaryUseActive()
                                        && !this.isVehicle() && !this.unified_taming$isBaby()) {
                                    player.startRiding(this);
                                    return InteractionResult.CONSUME;
                                }
                            }
                            if (this.unified_taming$isMoveModeSwitcher(itemstack)) {
                                if (player.isSecondaryUseActive()) {
                                    this.unified_taming$setOrderedToStroll(!this.unified_taming$isOrderedToStroll());
                                    MessageSender.sendCommandMessage((Mob) (Object) this, true);
                                    this.jumping = false;
                                    this.navigation.stop();
                                    this.setTarget(null);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                            if (this.unified_taming$isRideModeSwitcher(itemstack)) {
                                if (!player.isSecondaryUseActive()
                                        && !this.unified_taming$isBaby()) {
                                    this.unified_taming$setManual(!this.unified_taming$isManual());
                                    MessageSender.sendRideModeSwitchMessage((Mob) (Object) this, this.unified_taming$isManual(), true);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                            if (this.unified_taming$isCarrier(itemstack)) {
                                if (!player.isSecondaryUseActive() && !player.isVehicle() && !player.isBaby()) {
                                    this.startRiding(player);
                                    return InteractionResult.CONSUME;
                                }
                            }
                        }
                        return InteractionResult.PASS;
                    }
                } else {
                    if ((this.unified_taming$isTamer(itemstack) && this.unified_taming$isTamingConditionSatisfied()) || this.unified_taming$isCheatTamer(itemstack)) {
                        if (!((Mob) (Object) this instanceof TamableAnimal && !UnifiedTamingConfig.compatibleVanillaTamableTaming.get())) {
                            if (this.unified_taming$isTamer(itemstack) && !player.getAbilities().instabuild) {
                                itemstack.shrink(1);
                            }

                            if (this.unified_taming$isCheatTamer(itemstack) || this.random.nextInt(3) == 0) {
                                this.unified_taming$tame(player);
                                if ((Mob) (Object) this instanceof TamableAnimal tamableAnimal) {
                                    tamableAnimal.tame(player);
                                }
                                MessageSender.sendTamingMessage((Mob) (Object) this, player, true);
                                this.navigation.stop();
                                this.setTarget(null);
                                // this.unified_taming$setOrderedToSit(true);
                                this.level().broadcastEntityEvent(this, (byte) 7);
                            } else {
                                this.level().broadcastEntityEvent(this, (byte) 6);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Inject(method = "tickLeash", at = @At("TAIL"))
    protected void tickLeash(CallbackInfo ci) {
        Entity $$0 = this.getLeashHolder();
        if ($$0 != null && $$0.level() == this.level()) {
            float distanceFactor = UnifiedTamingUtils.getScaleFactor((Mob) (Object) this);
            int restrictRadius = (int) (5.0 * distanceFactor);
            float limitDistance = 6.0F * distanceFactor;
            double deltaSpeed = 0.4 * distanceFactor;
            float stopDistance = 2.0F * distanceFactor;
            double followSpeed = 1.0 * distanceFactor;
            this.restrictTo($$0.blockPosition(), restrictRadius);
            float $$1 = this.distanceTo($$0);
            if ($$1 > limitDistance) {
                double $$2 = ($$0.getX() - this.getX()) / (double) $$1;
                double $$3 = ($$0.getY() - this.getY()) / (double) $$1;
                double $$4 = ($$0.getZ() - this.getZ()) / (double) $$1;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign($$2 * $$2 * deltaSpeed, $$2), Math.copySign($$3 * $$3 * deltaSpeed, $$3), Math.copySign($$4 * $$4 * deltaSpeed, $$4)));
                this.checkSlowFallDistance();
            } else {
                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                Vec3 $$6 = (new Vec3($$0.getX() - this.getX(), $$0.getY() - this.getY(), $$0.getZ() - this.getZ())).normalize().scale(Math.max($$1 - stopDistance, 0.0F));
                this.getNavigation().moveTo(this.getX() + $$6.x, this.getY() + $$6.y, this.getZ() + $$6.z, followSpeed);
            }
        }
    }

    @Override
    public boolean canRiderInteract() {
        return UnifiedTamingConfig.canRiderInteract.get();
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        if (!this.canDrownInFluidType(type) && this.unified_taming$getEnvironment().isFly()) {
            return true;
        }
        if (type == ForgeMod.WATER_TYPE.get()) {
            return this.unified_taming$getEnvironment().isWaterSwim();
        } else if (type == ForgeMod.LAVA_TYPE.get()) {
            return this.unified_taming$getEnvironment().isLava();
        }
        return super.canBeRiddenUnderFluidType(type, rider);
    }

    public boolean unified_taming$canBeRiddenInAir(Entity rider) {
        return !this.unified_taming$getEnvironment().isWaterSwim() || this.unified_taming$getEnvironment().isWalk();
    }

    public UUID unified_taming$getNonPlayerOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(unified_taming$DATA_NONPLAYEROWNERUUID_ID)).orElse(null);
    }

    public boolean unified_taming$isTameNonPlayer() {
        return (this.entityData.get(unified_taming$DATA_FLAGS_ID) & 8) != 0;
    }

    public void unified_taming$setTameNonPlayer(boolean p_21836_) {
        byte b0 = this.entityData.get(unified_taming$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 | 8));
        } else {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 & -9));
        }
    }

    public void unified_taming$setNonPlayerOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(unified_taming$DATA_NONPLAYEROWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public void unified_taming$tameNonPlayer(Mob p_21829_) {
        this.unified_taming$setTameNonPlayer(true);
        this.unified_taming$setNonPlayerOwnerUUID(p_21829_.getUUID());
    }

    public boolean unified_taming$unableToMove() {
        return this.unified_taming$isOrderedToSit() || this.isPassenger() || this.isLeashed();
    }

    public boolean unified_taming$isManual() {
        if (this.level().isClientSide) {
            return (this.entityData.get(unified_taming$DATA_FLAGS_ID) & 2) != 0;
        } else {
            return unified_taming$isManual;
        }
    }

    public void unified_taming$setManual(boolean p_21836_) {
        this.unified_taming$isManual = p_21836_;
        byte b0 = this.entityData.get(unified_taming$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 | 2));
        } else {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 & -3));
        }
    }

    @Override
    public boolean isBaby() {
        return this.unified_taming$isBaby();
    }

    @Inject(method = "setBaby", at = @At("HEAD"))
    public void setBaby(boolean p_146756_, CallbackInfo ci) {
        this.unified_taming$setBaby(p_146756_);
    }

    public boolean unified_taming$isBaby() {
        return (this.entityData.get(unified_taming$DATA_FLAGS_ID) & 16) != 0;
    }

    public void unified_taming$setBaby(boolean p_146756_) {
        byte b0 = this.entityData.get(unified_taming$DATA_FLAGS_ID);
        if (p_146756_) {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 | 16));
        } else {
            this.entityData.set(unified_taming$DATA_FLAGS_ID, (byte) (b0 & -17));
        }
    }

    public boolean unified_taming$isBreedFood(ItemStack stack) {
        return !stack.isEdible() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CakeBlock;
    }

    public boolean unified_taming$canFallInLove() {
        return !this.unified_taming$isBaby() && this.unified_taming$getInLoveTime() == 0 && this.getHealth() >= this.getMaxHealth();
    }

    public void unified_taming$setInLove() {
        this.unified_taming$inLove = 600;
        this.level().broadcastEntityEvent(this, (byte) 18);
    }

    public void unified_taming$setInLoveTime(int p_27602_) {
        this.unified_taming$inLove = p_27602_;
    }

    public int unified_taming$getInLoveTime() {
        return this.unified_taming$inLove;
    }

    public boolean unified_taming$canMate(Player player) {
        return UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, player) && this.unified_taming$isInLove() && player.getHealth() >= player.getMaxHealth() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty();
    }

    public Mob unified_taming$getBreedOffspring(ServerLevel serverLevel, Player player) {
        if (!UnifiedTamingUtils.hasYoungModel(this)) {
            return null;
        }
        Entity entity = this.getType().create(serverLevel, null, null, this.blockPosition(), MobSpawnType.BREEDING, false, false);
        if (entity instanceof Mob mob) {
            if (UnifiedTamingUtils.isOwnedBy((Mob) (Object) this, player)) {
                ((TamableEntity) mob).unified_taming$tame(player);
                if (mob instanceof TamableAnimal tamableAnimal) {
                    tamableAnimal.tame(player);
                }
            }
            return mob;
        }
        return null;
    }

    public void unified_taming$spawnChildFromBreeding(ServerLevel p_27564_, Player p_27565_) {
        Mob mob = this.unified_taming$getBreedOffspring(p_27564_, p_27565_);
        if (mob != null) {
            mob.setBaby(true);
            ((BreedableHelper) mob).unified_taming$setBaby(true);
            mob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.unified_taming$finalizeSpawnChildFromBreeding(p_27564_, p_27565_, mob);
            p_27564_.addFreshEntityWithPassengers(mob);
        }
    }

    public void unified_taming$finalizeSpawnChildFromBreeding(ServerLevel p_277963_, Player p_277357_, @Nullable Mob p_277516_) {
        p_277357_.awardStat(Stats.ANIMALS_BRED);
        ((AnimalTriggerHelper) CriteriaTriggers.BRED_ANIMALS).unified_taming$BredAnimals$trigger((ServerPlayer) p_277357_, (Mob) (Object) this, p_277357_, p_277516_);
        this.unified_taming$setInLoveTime(-6000);
        if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
            ageableMob.setAge(6000);
            if (ageableMob instanceof Animal animal) {
                animal.resetLove();
            }
        }
        p_277963_.broadcastEntityEvent(this, (byte) 18);
        if (p_277963_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            p_277963_.addFreshEntity(new ExperienceOrb(p_277963_, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    public TamableEnvironment unified_taming$getEnvironment() {
        if (this.level().isClientSide) {
            int ordinal = this.entityData.get(unified_taming$DATA_TYPES_ID) & 15;
            return TamableEnvironment.values()[ordinal];
        } else {
            if (this.unified_taming$environment == null) {
                this.unified_taming$setEnvironment(UnifiedTamingUtils.getMobEnvironment((Mob) (Object) this));
            }
            return this.unified_taming$environment;
        }
    }

    public void unified_taming$setEnvironment(TamableEnvironment environment) {
        byte b0 = this.entityData.get(unified_taming$DATA_TYPES_ID);
        this.entityData.set(unified_taming$DATA_TYPES_ID, (byte) (b0 & -16 | environment.ordinal() & 15));
        this.unified_taming$environment = environment;
    }
}
