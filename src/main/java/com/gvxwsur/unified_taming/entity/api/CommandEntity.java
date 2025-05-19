package com.gvxwsur.unified_taming.entity.api;

import com.gvxwsur.unified_taming.entity.types.TamableCommand;

public interface CommandEntity {
    public TamableCommand unified_taming$getCommand();

    public void unified_taming$setCommand(TamableCommand command);

    public default boolean unified_taming$isOrderedToSit() {
        return unified_taming$getCommand() == TamableCommand.SIT;
    }

    public default void unified_taming$setOrderedToFollow(boolean p_21840_) {
        unified_taming$setCommand(p_21840_ ? TamableCommand.FOLLOW : TamableCommand.SIT);
    }

    public default boolean unified_taming$isOrderedToFollow() {
        return unified_taming$getCommand() == TamableCommand.FOLLOW;
    }

    public default void unified_taming$setOrderedToSit(boolean p_21840_) {
        unified_taming$setCommand(p_21840_ ? TamableCommand.SIT : TamableCommand.FOLLOW);
    }

    public default boolean unified_taming$isOrderedToStroll() {
        return unified_taming$getCommand() == TamableCommand.STROLL;
    }

    public default void unified_taming$setOrderedToStroll(boolean p_21840_) {
        unified_taming$setCommand(p_21840_ ? TamableCommand.STROLL : TamableCommand.FOLLOW);
    }

    public boolean unified_taming$unableToMove();
}

