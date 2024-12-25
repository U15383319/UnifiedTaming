package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.goal.*;
import com.gvxwsur.tamabletool.common.entity.helper.*;
import com.gvxwsur.tamabletool.common.entity.util.MessageSender;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
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
public abstract class MobMixin extends LivingEntity implements Targeting, TamableEntity, InteractEntity, MinionEntity, CommandEntity, RideableEntity, AgeableEntity, EnvironmentHelper {

    @Shadow protected PathNavigation navigation;

    @Shadow @Final public GoalSelector goalSelector;

    @Shadow @Final public GoalSelector targetSelector;

    @Shadow public abstract boolean isLeashed();

    @Shadow public abstract void setTarget(@Nullable LivingEntity p_21544_);

    @Shadow public abstract void restrictTo(BlockPos p_21447_, int p_21448_);

    @Shadow @Nullable public abstract Entity getLeashHolder();

    @Shadow public abstract PathNavigation getNavigation();

    @Unique
    private static final EntityDataAccessor<Byte> tamabletool$DATA_FLAGS_ID; // sit 0 manual 1 tame(0) 2 tame(1) 3 baby 4

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> tamabletool$DATA_OWNERUUID_ID;

    @Unique
    private TamableCommand tamabletool$command;

    @Unique
    private static final EntityDataAccessor<Optional<UUID>> tamabletool$DATA_NONPLAYEROWNERUUID_ID;

    @Unique
    private static final EntityDataAccessor<Byte> tamabletool$DATA_TYPES_ID;

    @Unique
    private boolean tamabletool$keyForward;
    @Unique
    private boolean tamabletool$keyBack;
    @Unique
    private boolean tamabletool$keyLeft;
    @Unique
    private boolean tamabletool$keyRight;

    @Unique
    private boolean tamabletool$isManual;

    @Unique
    private TamableEnvironment tamabletool$environment;
    @Unique
    private int tamabletool$age;
    @Unique
    private int tamabletool$inLove;

    protected MobMixin(EntityType<? extends LivingEntity> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.tamabletool$keyForward = false;
        this.tamabletool$keyBack = false;
        this.tamabletool$keyLeft = false;
        this.tamabletool$keyRight = false;
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean tamabletool$keyForward() {
        return Minecraft.getInstance().options.keyUp.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean tamabletool$keyBack() {
        return Minecraft.getInstance().options.keyDown.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean tamabletool$keyLeft() {
        return Minecraft.getInstance().options.keyLeft.isDown();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static boolean tamabletool$keyRight() {
        return Minecraft.getInstance().options.keyRight.isDown();
    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(tamabletool$DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(tamabletool$DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(tamabletool$DATA_NONPLAYEROWNERUUID_ID, Optional.empty());
        this.entityData.define(tamabletool$DATA_TYPES_ID, (byte) 0);
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
            p_21819_.putInt("Command", this.tamabletool$getCommand().ordinal());
            p_21819_.putBoolean("RideMode", this.tamabletool$isManual());
            p_21819_.putInt("Age", this.tamabletool$getAge());
            p_21819_.putInt("Environment", this.tamabletool$getEnvironment().ordinal());
        }

        if (this.tamabletool$getNonPlayerOwnerUUID() != null) {
            p_21819_.putUUID("NonPlayerOwner", this.tamabletool$getNonPlayerOwnerUUID());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag p_21815_, CallbackInfo ci) {
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
            this.tamabletool$setCommand(TamableCommand.values()[p_21815_.getInt("Command")]);
            this.tamabletool$setInSittingPose(this.tamabletool$isOrderedToSit());
            this.tamabletool$setManual(p_21815_.getBoolean("RideMode"));
            this.tamabletool$setAge(p_21815_.getInt("Age"));
            this.tamabletool$setEnvironment(TamableEnvironment.values()[p_21815_.getInt("Environment")]);
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

        this.tamabletool$registerTameGoals();
    }

    @Unique
    public void tamabletool$registerTameGoals() {
        this.goalSelector.addGoal(1, new CustomSitWhenOrderedToGoal((Mob) (Object) this));
        this.goalSelector.addGoal(6, new CustomFollowOwnerGoal((Mob) (Object) this, 1.0, 8.0F, 2.0F));
        this.goalSelector.addGoal(7, new CustomBreedGoal((Mob) (Object) this, 1.0));
        this.goalSelector.addGoal(8, new CustomRandomStrollGoal((Mob) (Object) this, 1.0));
        this.goalSelector.addGoal(10, new CustomLookAtOwnerGoal((Mob) (Object) this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal((Mob) (Object) this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal((Mob) (Object) this));
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo ci) {
        if (this.isAlive()) {
            if (this.tickCount % 40 == 0) {
                this.tamabletool$updateEnvironment();
            }

            int $$0 = this.tamabletool$getAge();
            if ($$0 < 0) {
                ++$$0;
                this.tamabletool$setAge($$0);
            } else if ($$0 > 0) {
                --$$0;
                this.tamabletool$setAge($$0);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        this.tamabletool$resetLove();
        Entity source = p_21016_.getEntity();
        if (!(source instanceof Player player && this.tamabletool$isOwnedBy(player))) {
            if (this.tamabletool$isOrderedToSit()) {
                this.tamabletool$setOrderedToFollow(true);
                if (!this.level().isClientSide) {
                    MessageSender.sendHurtWhenStopMessage((Mob) (Object) this, false);
                }
            }
        }
        return super.hurt(p_21016_, p_21017_);
    }

    public void tamabletool$travel(Vec3 vec3) {
        Entity entity = this.getControllingPassenger();
        if (entity instanceof Player player && this.isVehicle()) {
            if (level().isClientSide) {
                tamabletool$keyForward = tamabletool$keyForward();
                tamabletool$keyBack = tamabletool$keyBack();
                tamabletool$keyLeft = tamabletool$keyLeft();
                tamabletool$keyRight = tamabletool$keyRight();
            }

            float strafe = tamabletool$keyLeft ? 0.5f : (tamabletool$keyRight ? -0.5f : 0);
            float vertical = tamabletool$keyForward ? -(player.getXRot() - 10) / 22.5f : 0;
            float forward = tamabletool$keyForward ? 3 : (tamabletool$keyBack ? -0.5f : 0);

            boolean canVerticalMove = this.tamabletool$getEnvironment().isFly() || (this.tamabletool$getEnvironment().isSwim() && this.isInWater());
            vertical = canVerticalMove ? vertical : 0;

            float speed = (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * Mth.clamp(TamableToolConfig.rideSpeedModifier.get(), 0.0, 1.0));

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

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player && this.tamabletool$isManual()) {
            return player;
        }
        return null;
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.isPassenger() || TamableToolUtils.isTame((Mob) (Object) this));
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    public void canBeLeashed(Player p_21813_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isLeashed() && (!TamableToolConfig.leashedNeedTamed.get() || TamableToolUtils.isOwnedBy((Mob) (Object) this, p_21813_)));
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
        } else if (p_21807_ == 18) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
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
            ((AnimalTriggerHelper) CriteriaTriggers.TAME_ANIMAL).tamabletool$TameAnimal$trigger(player, (Mob)(Object) this);
        }
    }

    @Override
    public boolean canAttack(LivingEntity livingEntity) {
        if (this.tamabletool$isOwnedBy(livingEntity)) {
            return false;
        }
        if (livingEntity instanceof Mob mob && TamableToolUtils.hasSameOwner((Mob) (Object) this, mob)) {
            return false;
        }
        if (TamableToolConfig.compatiblePartEntity.get() && !(livingEntity instanceof Mob)) {
            Entity targetAncestry = ((UniformPartEntity) livingEntity).getAncestry();
            if (targetAncestry instanceof Mob targetAncestryMob) {
                if ((Object) this == targetAncestryMob) {
                    return false;
                }
                if (TamableToolUtils.hasSameOwner((Mob) (Object) this, targetAncestryMob)) {
                    return false;
                }
            }
        }
        return super.canAttack(livingEntity);
    }

    public boolean tamabletool$isOwnedBy(LivingEntity p_21831_) {
        return p_21831_ == this.getOwner();
    }

    @Override
    public Team getTeam() {
        if (this.tamabletool$isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
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

    @Override
    public void die(DamageSource p_21809_) {
        Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(p_21809_);
        if (!this.level().isClientSide && this.dead) {
            MessageSender.sendDeathMessage((Mob)(Object) this, deathMessage, false);
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
    }

    static {
        tamabletool$DATA_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
        tamabletool$DATA_OWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
        tamabletool$DATA_NONPLAYEROWNERUUID_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.OPTIONAL_UUID);
        tamabletool$DATA_TYPES_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
    }

    public boolean tamabletool$isFood(ItemStack p_30440_) {
        return p_30440_.isEdible();
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

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public final void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult interactionresult = this.tamabletool$tameInteract(player, hand);
        if (interactionresult.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            cir.setReturnValue(interactionresult);
        }
    }

    @Unique
    public InteractionResult tamabletool$tameInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack assistItemstack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (this.tamabletool$isModAssistant(assistItemstack)) {
            if (this.level().isClientSide) {
                boolean flag = this.tamabletool$isTame() || (this.tamabletool$isTamer(itemstack) || this.tamabletool$isCheatTamer(itemstack)) && !this.tamabletool$isTame();
                boolean flag2 = this.tamabletool$isOwnedBy(player) && this.tamabletool$isRider(itemstack) && !this.isVehicle() && !player.isSecondaryUseActive();
                boolean flag3 = this.tamabletool$isOwnedBy(player) && this.tamabletool$isRideModeSwitcher(itemstack) && !player.isSecondaryUseActive();
                boolean flag4 = this.tamabletool$isTame() && this.isBaby() && this.tamabletool$isFood(itemstack) && this.getHealth() >= this.getMaxHealth();
                if (flag2 || flag3 || flag4) {
                    return InteractionResult.SUCCESS;
                } else if (flag) {
                    return InteractionResult.CONSUME;
                } else {
                    return InteractionResult.PASS;
                }
            } else if (this.tamabletool$isTameNonPlayer()) {
              return InteractionResult.PASS;
            } else if (this.tamabletool$isTame()) {
                if (this.tamabletool$isFood(itemstack)) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        if (itemstack.isEdible()) {
                            FoodProperties foodProperties = itemstack.getFoodProperties(this);
                            float totalValue = foodProperties.getNutrition() + foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2;
                            this.heal(totalValue);
                        }
                        this.eat(this.level(), itemstack);
                        return InteractionResult.SUCCESS;
                    }
                    if (this.isBaby()) {
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.tamabletool$ageUp(100);
                        return InteractionResult.CONSUME;
                    }
                } else {
                    if (this.tamabletool$isOwnedBy(player)) {
                        if (this.tamabletool$isBreedFood(itemstack)) {
                            if (this.tamabletool$getAge() == 0 && this.tamabletool$canFallInLove()) {
                                if (!player.getAbilities().instabuild) {
                                    itemstack.shrink(1);
                                }
                                this.tamabletool$setInLove();
                                return InteractionResult.SUCCESS;
                            }
                        }
                        if (this.tamabletool$isCommander(itemstack)) {
                            if (player.isSecondaryUseActive()) {
                                this.tamabletool$setOrderedToSit(!this.tamabletool$isOrderedToSit());
                                MessageSender.sendCommandMessage((Mob) (Object) this, true);
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget(null);
                                return InteractionResult.SUCCESS;
                            }
                        }
                        if (this.tamabletool$isRider(itemstack)) {
                            if (!player.isSecondaryUseActive() && !this.isVehicle()) {
                                player.startRiding(this);
                                return InteractionResult.CONSUME;
                            }
                        }
                        if (this.tamabletool$isMoveModeSwitcher(itemstack)) {
                            if (player.isSecondaryUseActive()) {
                                this.tamabletool$setOrderedToStroll(!this.tamabletool$isOrderedToStroll());
                                MessageSender.sendCommandMessage((Mob) (Object) this, true);
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget(null);
                                return InteractionResult.SUCCESS;
                            }
                        }
                        if (this.tamabletool$isRideModeSwitcher(itemstack)) {
                            if (!player.isSecondaryUseActive()) {
                                this.tamabletool$setManual(!this.tamabletool$isManual());
                                MessageSender.sendRideModeSwitchMessage((Mob) (Object) this, this.tamabletool$isManual(), true);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    return InteractionResult.PASS;
                }
            } else if ((this.tamabletool$isTamer(itemstack) && this.tamabletool$isTamingConditionSatisfied()) || this.tamabletool$isCheatTamer(itemstack)) {
                if (this.tamabletool$isTamer(itemstack) && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.tamabletool$isCheatTamer(itemstack) || this.random.nextInt(3) == 0) {
                    this.tamabletool$tame(player);
                    MessageSender.sendTamingMessage((Mob) (Object) this, player, true);
                    this.navigation.stop();
                    this.setTarget(null);
                    // this.tamabletool$setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Inject(method = "tickLeash", at = @At("TAIL"))
    protected void tickLeash(CallbackInfo ci) {
        if (!TamableToolConfig.compatibleAnimalLeashed.get() && (Mob) (Object) this instanceof Animal) {
            return;
        }
        Entity $$0 = this.getLeashHolder();
        if ($$0 != null && $$0.level() == this.level()) {
            float distanceFactor = TamableToolUtils.getScaleFactor((Mob) (Object) this);
            int restrictRadius = (int) (5.0 * distanceFactor);
            float limitDistance = 6.0F * distanceFactor;
            double deltaSpeed = 0.4 * distanceFactor;
            float stopDistance = 2.0F * distanceFactor;
            double followSpeed = 1.0 * distanceFactor;
            this.restrictTo($$0.blockPosition(), restrictRadius);
            float $$1 = this.distanceTo($$0);
            if ($$1 > limitDistance) {
                double $$2 = ($$0.getX() - this.getX()) / (double)$$1;
                double $$3 = ($$0.getY() - this.getY()) / (double)$$1;
                double $$4 = ($$0.getZ() - this.getZ()) / (double)$$1;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign($$2 * $$2 * deltaSpeed, $$2), Math.copySign($$3 * $$3 * deltaSpeed, $$3), Math.copySign($$4 * $$4 * deltaSpeed, $$4)));
                this.checkSlowFallDistance();
            } else {
                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                Vec3 $$6 = (new Vec3($$0.getX() - this.getX(), $$0.getY() - this.getY(), $$0.getZ() - this.getZ())).normalize().scale((double)Math.max($$1 - stopDistance, 0.0F));
                this.getNavigation().moveTo(this.getX() + $$6.x, this.getY() + $$6.y, this.getZ() + $$6.z, followSpeed);
            }
        }
    }

    public UUID tamabletool$getNonPlayerOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(tamabletool$DATA_NONPLAYEROWNERUUID_ID)).orElse(null);
    }

    public boolean tamabletool$isTameNonPlayer() {
        return (this.entityData.get(tamabletool$DATA_FLAGS_ID) & 8) != 0;
    }

    public void tamabletool$setTameNonPlayer(boolean p_21836_) {
        byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 8));
        } else {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -9));
        }
    }

    public void tamabletool$setNonPlayerOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(tamabletool$DATA_NONPLAYEROWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public void tamabletool$tameNonPlayer(Mob p_21829_) {
        this.tamabletool$setTameNonPlayer(true);
        this.tamabletool$setNonPlayerOwnerUUID(p_21829_.getUUID());
    }

    public boolean tamabletool$unableToMove() {
        return this.tamabletool$isOrderedToSit() || this.isPassenger() || this.isLeashed();
    }

    public boolean tamabletool$isManual() {
        if (this.level().isClientSide) {
            return (this.entityData.get(tamabletool$DATA_FLAGS_ID) & 2) != 0;
        } else {
            return tamabletool$isManual;
        }
    }

    public void tamabletool$setManual(boolean p_21836_) {
        this.tamabletool$isManual = p_21836_;
        byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 2));
        } else {
            this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -3));
        }
    }

    @Override
    public boolean isBaby() {
        return this.tamabletool$getAge() < 0;
    }

    @Inject(method = "setBaby", at = @At("HEAD"))
    public void setBaby(boolean p_146756_, CallbackInfo ci) {
        this.tamabletool$setAge(p_146756_ ? -24000 : 0);
    }

    public int tamabletool$getAge() {
        if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
            return ageableMob.getAge();
        }
        if (this.level().isClientSide) {
            return ((this.entityData.get(tamabletool$DATA_FLAGS_ID) & 16) != 0) ? -1 : 1;
        } else {
            return this.tamabletool$age;
        }
    }

    public void tamabletool$ageUp(int p_146759_) {
        if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
            ageableMob.ageUp(p_146759_, true);
            return;
        }
        int $$2 = this.tamabletool$getAge();
        $$2 += p_146759_ * 20;
        if ($$2 > 0) {
            $$2 = 0;
        }
        this.tamabletool$setAge($$2);
    }

    public void tamabletool$setAge(int p_146763_) {
        if ((Mob) (Object) this instanceof AgeableMob ageableMob) {
            ageableMob.setAge(p_146763_);
            return;
        }
        int $$1 = this.tamabletool$getAge();
        this.tamabletool$age = p_146763_;
        if ($$1 < 0 && p_146763_ >= 0 || $$1 >= 0 && p_146763_ < 0) {
            byte b0 = this.entityData.get(tamabletool$DATA_FLAGS_ID);
            if (p_146763_ < 0) {
                this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 | 16));
            } else {
                this.entityData.set(tamabletool$DATA_FLAGS_ID, (byte) (b0 & -17));
            }
        }
    }

    public boolean tamabletool$isBreedFood(ItemStack stack) {
        return !stack.isEdible() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CakeBlock;
    }

    public boolean tamabletool$canFallInLove() {
        return tamabletool$getInLoveTime() <= 0 && this.getHealth() >= this.getMaxHealth();
    }

    public void tamabletool$setInLove() {
        this.tamabletool$inLove = 600;

        this.level().broadcastEntityEvent(this, (byte)18);
    }

    public void tamabletool$setInLoveTime(int p_27602_) {
        this.tamabletool$inLove = p_27602_;
    }

    public int tamabletool$getInLoveTime() {
        return this.tamabletool$inLove;
    }

    public boolean tamabletool$canMate(Player p_27569_) {
        return TamableToolUtils.isOwnedBy((Mob) (Object) this, p_27569_) && this.tamabletool$isInLove() && p_27569_.getHealth() >= p_27569_.getMaxHealth() && p_27569_.getMainHandItem().isEmpty() && p_27569_.getOffhandItem().isEmpty();
    }

    public Mob tamabletool$getBreedOffspring(ServerLevel serverLevel, Player player) {
        Entity entity = this.getType().create(serverLevel, null, null, this.blockPosition(), MobSpawnType.BREEDING, false, false);
        if (entity instanceof Mob mob) {
            if (TamableToolUtils.isOwnedBy((Mob) (Object) this, player)) {
                ((TamableEntity) mob).tamabletool$tame(player);
                ((TamableEntity) mob).tamabletool$registerTameGoals();
            }
            return mob;
        }
        return null;
    }

    public void tamabletool$spawnChildFromBreeding(ServerLevel p_27564_, Player p_27565_) {
        Mob mob = this.tamabletool$getBreedOffspring(p_27564_, p_27565_);
        if (mob != null) {
            mob.setBaby(true);
            mob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.tamabletool$finalizeSpawnChildFromBreeding(p_27564_, p_27565_, mob);
            p_27564_.addFreshEntityWithPassengers(mob);
        }
    }

    public void tamabletool$finalizeSpawnChildFromBreeding(ServerLevel p_277963_, Player p_277357_, @Nullable Mob p_277516_) {
        p_277357_.awardStat(Stats.ANIMALS_BRED);
        ((AnimalTriggerHelper) CriteriaTriggers.BRED_ANIMALS).tamabletool$BredAnimals$trigger((ServerPlayer) p_277357_, (Mob) (Object) this, p_277357_, p_277516_);
        this.tamabletool$setAge(6000);
        this.tamabletool$resetLove();
        p_277963_.broadcastEntityEvent(this, (byte)18);
        if (p_277963_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            p_277963_.addFreshEntity(new ExperienceOrb(p_277963_, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    public TamableEnvironment tamabletool$getEnvironment() {
        if (this.level().isClientSide) {
            int ordinal = this.entityData.get(tamabletool$DATA_TYPES_ID) & 7;
            return TamableEnvironment.values()[ordinal];
        } else {
            if (this.tamabletool$environment == null) {
                this.tamabletool$setEnvironment(TamableToolUtils.getMobEnvironment((Mob) (Object) this));
            }
            return this.tamabletool$environment;
        }
    }

    public void tamabletool$setEnvironment(TamableEnvironment environment) {
        byte b0 = this.entityData.get(tamabletool$DATA_TYPES_ID);
        this.entityData.set(tamabletool$DATA_TYPES_ID, (byte) (b0 & -8 | environment.ordinal() & 7));
        this.tamabletool$environment = environment;
    }

    public void tamabletool$updateEnvironment() {
        TamableEnvironment oldEnvironment = this.tamabletool$getEnvironment();
        TamableEnvironment newEnvironment = TamableToolUtils.getMobEnvironment((Mob) (Object) this);
        if (oldEnvironment != newEnvironment) {
            if (oldEnvironment == TamableEnvironment.AMPHIBIOUS) {
                return;
            }
            if ((oldEnvironment == TamableEnvironment.GROUND && newEnvironment == TamableEnvironment.WATER) || (oldEnvironment == TamableEnvironment.WATER && newEnvironment == TamableEnvironment.GROUND)) {
                this.tamabletool$setEnvironment(TamableEnvironment.AMPHIBIOUS);
            } else {
                this.tamabletool$setEnvironment(newEnvironment);
            }
        }
    }
}
