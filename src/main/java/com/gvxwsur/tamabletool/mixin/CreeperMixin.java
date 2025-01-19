package com.gvxwsur.tamabletool.mixin;

import com.gvxwsur.tamabletool.common.config.TamableToolConfig;
import com.gvxwsur.tamabletool.common.entity.helper.SelfDestructEntity;
import com.gvxwsur.tamabletool.common.entity.util.TamableToolUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster implements PowerableMob, SelfDestructEntity {

    @Shadow protected abstract void spawnLingeringCloud();

    @Shadow public abstract void setTarget(@Nullable LivingEntity p_149691_);

    @Shadow public abstract void setSwellDir(int p_32284_);

    @Unique
    private static final EntityDataAccessor<Boolean> tamabletool$DATA_IS_DESTRUCTED;

    protected CreeperMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(tamabletool$DATA_IS_DESTRUCTED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag p_32304_, CallbackInfo ci) {
        p_32304_.putBoolean("IsDestructed", this.tamabletool$isDestructed());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag p_32286_, CallbackInfo ci) {
        this.tamabletool$setDestructed(p_32286_.getBoolean("IsDestructed"));
    }

    public boolean tamabletool$isDestructed() {
        return this.entityData.get(tamabletool$DATA_IS_DESTRUCTED);
    }

    public void tamabletool$setDestructed(boolean destructed) {
        this.entityData.set(tamabletool$DATA_IS_DESTRUCTED, destructed);
    }

    @Inject(method = "explodeCreeper", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;discard()V"), cancellable = true)
    private void explodeCreeper(CallbackInfo ci) {
        if (TamableToolConfig.selfDestructMobNotDead.get() && TamableToolUtils.isTame(this)) {
            this.dead = false;
            if (!this.tamabletool$isDestructed()) {
                this.spawnLingeringCloud();
                this.tamabletool$setDestructed(true);
            }
            this.setSwellDir(-1);
            this.setTarget(null);
            ci.cancel();
        }
    }

    static {
        tamabletool$DATA_IS_DESTRUCTED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    }
}
