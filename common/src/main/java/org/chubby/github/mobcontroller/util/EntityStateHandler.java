package org.chubby.github.mobcontroller.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.monster.Monster;
import org.chubby.github.mobcontroller.common.enums.EnumControlledStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityStateHandler {
    private EntityStateHolder stateHolder;
    private final List<StateChangeListener> listeners = new ArrayList<>();

    public EntityStateHandler(EntityStateHolder stateHolder) {
        this.stateHolder = stateHolder;
    }

    public EntityStateHandler(EnumControlledStates initialState) {
        this.stateHolder = new EntityStateHolder(initialState, false, true, 0, System.currentTimeMillis());
    }

    public EntityStateHolder getStateHolder() {
        return stateHolder;
    }

    public EnumControlledStates getCurrentState() {
        return stateHolder.currentState();
    }

    public boolean isInCooldown() {
        return stateHolder.stateCooldown() > 0;
    }

    public boolean canSwitchState() {
        return stateHolder.switchState() && !isInCooldown();
    }

    public boolean canTransitionTo(EnumControlledStates newState) {
        if (newState == stateHolder.currentState()) return false;
        if (isInCooldown()) return false;

        return switch (stateHolder.currentState()) {
            case STAY -> newState != EnumControlledStates.ATTACK;
            case FOLLOW -> true;
            case DEFEND -> newState != EnumControlledStates.GO_HOME;
            case ATTACK -> newState == EnumControlledStates.FOLLOW || newState == EnumControlledStates.DEFEND;
            case GO_HOME -> newState == EnumControlledStates.STAY || newState == EnumControlledStates.FOLLOW;
        };
    }

    public boolean transitionTo(EnumControlledStates newState) {
        return transitionTo(newState, 20);
    }

    public boolean transitionTo(EnumControlledStates newState, int cooldownTicks) {
        if (!canTransitionTo(newState)) {
            return false;
        }

        EnumControlledStates oldState = stateHolder.currentState();
        long currentTime = System.currentTimeMillis();

        this.stateHolder = new EntityStateHolder(
                newState,
                true,
                stateHolder.canMoveToNextStateWithoutLevelUpdate(),
                cooldownTicks,
                currentTime
        );

        notifyStateChange(oldState, newState);
        return true;
    }

    public void forceTransition(EnumControlledStates newState) {
        EnumControlledStates oldState = stateHolder.currentState();
        this.stateHolder = new EntityStateHolder(
                newState,
                true,
                stateHolder.canMoveToNextStateWithoutLevelUpdate(),
                0,
                System.currentTimeMillis()
        );
        notifyStateChange(oldState, newState);
    }

    public void tick() {
        if (stateHolder.stateCooldown() > 0) {
            this.stateHolder = new EntityStateHolder(
                    stateHolder.currentState(),
                    stateHolder.switchState(),
                    stateHolder.canMoveToNextStateWithoutLevelUpdate(),
                    stateHolder.stateCooldown() - 1,
                    stateHolder.lastStateChange()
            );
        }
    }

    public void addStateChangeListener(StateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeStateChangeListener(StateChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyStateChange(EnumControlledStates oldState, EnumControlledStates newState) {
        for (StateChangeListener listener : listeners) {
            try {
                listener.onStateChange(oldState, newState);
            } catch (Exception e) {
                System.err.println("Error in state change listener: " + e.getMessage());
            }
        }
    }

    public long getTimeSinceLastStateChange() {
        return System.currentTimeMillis() - stateHolder.lastStateChange();
    }

    public boolean hasBeenInStateFor(long milliseconds) {
        return getTimeSinceLastStateChange() >= milliseconds;
    }

    public boolean isPassiveState() {
        return stateHolder.currentState() == EnumControlledStates.STAY ||
                stateHolder.currentState() == EnumControlledStates.FOLLOW;
    }

    public boolean isAggressiveState() {
        return stateHolder.currentState() == EnumControlledStates.ATTACK ||
                stateHolder.currentState() == EnumControlledStates.DEFEND;
    }

    public boolean isMovementState() {
        return stateHolder.currentState() == EnumControlledStates.FOLLOW ||
                stateHolder.currentState() == EnumControlledStates.GO_HOME;
    }

    public record EntityStateHolder(
            EnumControlledStates currentState,
            boolean switchState,
            boolean canMoveToNextStateWithoutLevelUpdate,
            int stateCooldown,
            long lastStateChange
    ) {
//        public static final Codec<EntityStateHolder> CODEC = RecordCodecBuilder.create(inst -> inst.group(
//                EnumControlledStates.CODEC.fieldOf("currentState").forGetter(EntityStateHolder::currentState),
//                Codec.BOOL.fieldOf("switchState").forGetter(EntityStateHolder::switchState),
//                Codec.BOOL.fieldOf("canMoveToNextStateWithoutLevelUpdate").forGetter(EntityStateHolder::canMoveToNextStateWithoutLevelUpdate),
//                Codec.INT.optionalFieldOf("stateCooldown", 0).forGetter(EntityStateHolder::stateCooldown),
//                Codec.LONG.optionalFieldOf("lastStateChange", 0L).forGetter(EntityStateHolder::lastStateChange)
//        ).apply(inst, EntityStateHolder::new));
//
//        public static final StreamCodec<RegistryFriendlyByteBuf, EntityStateHolder> STREAM_CODEC =
//                new StreamCodec<RegistryFriendlyByteBuf, EntityStateHolder>() {
//                    @Override
//                    public EntityStateHolder decode(RegistryFriendlyByteBuf buf) {
//                        return new EntityStateHolder(
//                                buf.readEnum(EnumControlledStates.class),
//                                buf.readBoolean(),
//                                buf.readBoolean(),
//                                buf.readInt(),
//                                buf.readLong()
//                        );
//                    }
//
//                    @Override
//                    public void encode(RegistryFriendlyByteBuf buf, EntityStateHolder holder) {
//                        buf.writeEnum(holder.currentState());
//                        buf.writeBoolean(holder.switchState());
//                        buf.writeBoolean(holder.canMoveToNextStateWithoutLevelUpdate());
//                        buf.writeInt(holder.stateCooldown());
//                        buf.writeLong(holder.lastStateChange());
//                    }
//                };
    }

    @SuppressWarnings("unused") // Will be shortly used but IDK how and why
    public interface StateHandler {
        void onEnterState(Monster entity);
        void onExitState(Monster entity);
        void tick(Monster entity);

        default boolean canEnterState(Monster entity, EnumControlledStates newState) {
            return true;
        }

        default int getStateCooldown() {
            return 20;
        }
    }

    public interface StateChangeListener {
        void onStateChange(EnumControlledStates oldState, EnumControlledStates newState);
    }

    @SuppressWarnings("unused") // It will be used to make custom state handlers probably in the future
    public static class Builder {
        private EnumControlledStates currentState = EnumControlledStates.STAY;
        private boolean switchState = false;
        private boolean canMoveToNextStateWithoutLevelUpdate = true;
        private int stateCooldown = 0;
        private long lastStateChange = System.currentTimeMillis();

        public Builder currentState(EnumControlledStates state) {
            this.currentState = state;
            return this;
        }

        public Builder switchState(boolean switchState) {
            this.switchState = switchState;
            return this;
        }

        public Builder canMoveToNextStateWithoutLevelUpdate(boolean canMove) {
            this.canMoveToNextStateWithoutLevelUpdate = canMove;
            return this;
        }

        public Builder stateCooldown(int cooldown) {
            this.stateCooldown = cooldown;
            return this;
        }

        public Builder lastStateChange(long timestamp) {
            this.lastStateChange = timestamp;
            return this;
        }

        public EntityStateHandler build() {
            return new EntityStateHandler(new EntityStateHolder(
                    currentState, switchState, canMoveToNextStateWithoutLevelUpdate,
                    stateCooldown, lastStateChange
            ));
        }
    }
}