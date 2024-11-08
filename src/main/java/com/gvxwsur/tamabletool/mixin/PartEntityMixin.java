package com.gvxwsur.tamabletool.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(PartEntity.class)
public abstract class PartEntityMixin<T extends Entity> extends Entity {

    @Mutable
    @Final
    @Shadow(remap = false)
    private final T parent;

    public PartEntityMixin(T parent) {
        super(parent.getType(), parent.level());
        this.parent = parent;
    }

    public @NotNull InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        if (parent != null) {
            return parent.interact(p_19978_, p_19979_);
        }
        return InteractionResult.PASS;
    }
}
