package org.chubby.github.mobcontroller.common.enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Monster;

import java.util.Arrays;
import java.util.Optional;

public enum EnumControlledStates {
    STAY("textures/gui/button/stay.png", 1, "Stay", "Entity will remain in place"),
    FOLLOW("textures/gui/button/follow.png", 2, "Follow", "Entity will follow the owner"),
    DEFEND("textures/gui/button/defend.png", 3, "Defend", "Entity will defend the owner or area"),
    ATTACK("textures/gui/button/attack.png", 4, "Attack", "Entity will attack nearby enemies"),
    GO_HOME("textures/gui/button/go_home.png", 5, "Go Home", "Entity will return to home position");

//    public static final Codec<EnumControlledStates> CODEC = Codec.STRING.comapFlatMap(
//            name -> fromString(name)
//                    .map(DataResult::success)
//                    .orElse(DataResult.error(() -> "Invalid EnumControlledStates: " + name)),
//            EnumControlledStates::name
//    );


    private final String icon;
    private final int priority;
    private final String displayName;
    private final String description;

    EnumControlledStates(String icon, int priority, String displayName, String description) {
        this.icon = icon;
        this.priority = priority;
        this.displayName = displayName;
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Component getDisplayComponent() {
        return Component.literal(displayName);
    }

    public Component getDescriptionComponent() {
        return Component.literal(description);
    }

    public boolean canOverride(EnumControlledStates other) {
        return this.priority > other.priority;
    }

    public boolean hasHigherPriorityThan(EnumControlledStates other) {
        return this.priority > other.priority;
    }

    public boolean hasLowerPriorityThan(EnumControlledStates other) {
        return this.priority < other.priority;
    }

    public boolean isPassive() {
        return this == STAY || this == FOLLOW;
    }

    public boolean isAggressive() {
        return this == ATTACK || this == DEFEND;
    }

    public boolean requiresTarget() {
        return this == ATTACK || this == DEFEND;
    }

    public boolean requiresMovement() {
        return this == FOLLOW || this == GO_HOME;
    }

    public boolean allowsIdleBehavior() {
        return this == STAY;
    }

    public EnumControlledStates getNextState() {
        return switch (this) {
            case STAY -> FOLLOW;
            case FOLLOW -> DEFEND;
            case DEFEND -> ATTACK;
            case ATTACK -> GO_HOME;
            case GO_HOME -> STAY;
        };
    }

    public EnumControlledStates getPreviousState() {
        return switch (this) {
            case STAY -> GO_HOME;
            case FOLLOW -> STAY;
            case DEFEND -> FOLLOW;
            case ATTACK -> DEFEND;
            case GO_HOME -> ATTACK;
        };
    }

    public static Optional<EnumControlledStates> fromString(String name) {
        try {
            return Optional.of(EnumControlledStates.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<EnumControlledStates> fromPriority(int priority) {
        return Arrays.stream(values())
                .filter(state -> state.priority == priority)
                .findFirst();
    }

    public static EnumControlledStates getDefaultState() {
        return STAY;
    }

    public static EnumControlledStates getDefaultStateForEntity(Monster entity) {
        return FOLLOW;
    }

    public boolean isValidFor(Monster entity) {
        return true;
    }

    public boolean isValidTransitionFrom(EnumControlledStates fromState) {
        return switch (fromState) {
            case STAY -> this != ATTACK;
            case FOLLOW -> true;
            case DEFEND -> this != GO_HOME;
            case ATTACK -> this == FOLLOW || this == DEFEND;
            case GO_HOME -> this == STAY || this == FOLLOW;
        };
    }
}