package com.gvxwsur.unified_taming.entity.api;

import com.gvxwsur.unified_taming.entity.types.TamableCommand;

public interface CommandEntity {
    public TamableCommand tamabletool$getCommand();

    public void tamabletool$setCommand(TamableCommand command);

    public default boolean tamabletool$isOrderedToSit() {
        return tamabletool$getCommand() == TamableCommand.SIT;
    }

    public default void tamabletool$setOrderedToFollow(boolean p_21840_) {
        tamabletool$setCommand(p_21840_ ? TamableCommand.FOLLOW : TamableCommand.SIT);
    }

    public default boolean tamabletool$isOrderedToFollow() {
        return tamabletool$getCommand() == TamableCommand.FOLLOW;
    }

    public default void tamabletool$setOrderedToSit(boolean p_21840_) {
        tamabletool$setCommand(p_21840_ ? TamableCommand.SIT : TamableCommand.FOLLOW);
    }

    public default boolean tamabletool$isOrderedToStroll() {
        return tamabletool$getCommand() == TamableCommand.STROLL;
    }

    public default void tamabletool$setOrderedToStroll(boolean p_21840_) {
        tamabletool$setCommand(p_21840_ ? TamableCommand.STROLL : TamableCommand.FOLLOW);
    }

    public boolean tamabletool$unableToMove();
}

