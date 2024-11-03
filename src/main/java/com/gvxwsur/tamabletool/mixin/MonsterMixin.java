package com.gvxwsur.tamabletool.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Mixin(Monster.class)
public abstract class MonsterMixin extends PathfinderMob implements Enemy, OwnableEntity {

    @Unique
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    @Unique
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
    @Unique
    private boolean tamabletool$orderedToSit;

    protected MonsterMixin(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    public void addAdditionalSaveData(CompoundTag p_21819_) {
        super.addAdditionalSaveData(p_21819_);
        if (this.getOwnerUUID() != null) {
            p_21819_.putUUID("Owner", this.getOwnerUUID());
        }

        p_21819_.putBoolean("Sitting", this.tamabletool$orderedToSit);
    }

    public void readAdditionalSaveData(CompoundTag p_21815_) {
        super.readAdditionalSaveData(p_21815_);
        UUID uuid;
        if (p_21815_.hasUUID("Owner")) {
            uuid = p_21815_.getUUID("Owner");
        } else {
            String s = p_21815_.getString("Owner");
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

        this.tamabletool$orderedToSit = p_21815_.getBoolean("Sitting");
        this.tamabletool$setInSittingPose(this.tamabletool$orderedToSit);
    }

    public boolean canBeLeashed(Player p_21813_) {
        return !this.isLeashed();
    }

    @Unique
    protected void tamabletool$spawnTamingParticles(boolean p_21835_) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!p_21835_) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
        }

    }

    public void handleEntityEvent(byte p_21807_) {
        if (p_21807_ == 7) {
            this.tamabletool$spawnTamingParticles(true);
        } else if (p_21807_ == 6) {
            this.tamabletool$spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(p_21807_);
        }

    }

    @Unique
    public boolean tamabletool$isTame() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    @Unique
    public void tamabletool$setTame(boolean p_21836_) {
        byte b0 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (p_21836_) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }
    }

    @Unique
    public boolean tamabletool$isInSittingPose() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    @Unique
    public void tamabletool$setInSittingPose(boolean p_21838_) {
        byte b0 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (p_21838_) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
        }

    }

    @Unique
    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse((UUID)null);
    }

    @Unique
    public void tamabletool$setOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    @Unique
    public void tamabletool$tame(Player p_21829_) {
        this.tamabletool$setTame(true);
        this.tamabletool$setOwnerUUID(p_21829_.getUUID());
    }

    public boolean canAttack(LivingEntity p_21822_) {
        return this.tamabletool$isOwnedBy(p_21822_) ? false : super.canAttack(p_21822_);
    }

    @Unique
    public boolean tamabletool$isOwnedBy(LivingEntity p_21831_) {
        return p_21831_ == this.getOwner();
    }

    @Unique
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
        if (this.dead && !this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendSystemMessage(deathMessage);
        }

    }

    @Unique
    public boolean tamabletool$isOrderedToSit() {
        return this.tamabletool$orderedToSit;
    }

    @Unique
    public void tamabletool$setOrderedToSit(boolean p_21840_) {
        this.tamabletool$orderedToSit = p_21840_;
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(MonsterMixin.class, EntityDataSerializers.BYTE);
        DATA_OWNERUUID_ID = SynchedEntityData.defineId(MonsterMixin.class, EntityDataSerializers.OPTIONAL_UUID);
    }

    public InteractionResult mobInteract(Player p_30412_, InteractionHand p_30413_) {
        ItemStack itemstack = p_30412_.getItemInHand(p_30413_);
        if (!this.level().isClientSide) {
            if (!this.tamabletool$isTame()) {
                if (itemstack.is(Items.DEBUG_STICK)) {
                    this.tamabletool$tame(p_30412_);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(p_30412_, p_30413_);
    }
}
