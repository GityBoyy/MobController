package org.chubby.github.mobcontroller.common.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class ItemController extends Item implements Equipable {

    public final ControllerType type;
    private final int ATTACK_DURATION;
    public static final Map<Player, Monster> playerMobControlMap = new HashMap<>();


    public ItemController(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
        this.ATTACK_DURATION = type.getControlTime();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof Player player) {
            Monster controlledMob = findControlledMob(player);
            if (controlledMob != null) {
                LivingEntity target = player.getLastHurtMob();
                if (target != null) {
                    startControlledAttack(player, controlledMob, target, ATTACK_DURATION);
                }
            }
        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    /**
     * Finds the mob controlled by the given player.
     *
     * @param player The player controlling the mob.
     * @return The controlled mob or null if none is found.
     */
    private Monster findControlledMob(Player player) {
        return playerMobControlMap.get(player);
    }

    /**
     * Assigns a mob to a player for control, preventing the mob from attacking the player.
     *
     * @param player The player controlling the mob.
     * @param mob The mob being controlled.
     */
    public static void assignControlledMob(Player player, Monster mob) {
        playerMobControlMap.put(player, mob);
        try {
            Field goalSelectorField = Mob.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);
            PreventPlayerTargetGoal goal = new PreventPlayerTargetGoal(mob, player);
            var goalSelector = goalSelectorField.get(mob);
            if (goalSelector instanceof GoalSelector selector) {
                selector.addGoal(1, goal);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initiates an attack by the controlled mob on the specified target for a limited duration.
     *
     * @param owner The player controlling the mob.
     * @param controlledMob The mob being controlled.
     * @param target The target of the attack.
     * @param duration The duration of the attack in ticks.
     */
    private void startControlledAttack(Player owner, Monster controlledMob, LivingEntity target, int duration) {
        if (controlledMob.getTarget() instanceof Player) {
            controlledMob.setAggressive(false);
            controlledMob.setTarget(target);
        }

        controlledMob.setTarget(target);
        try {
            Field goalSelectorField = Mob.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);
            var goalSelector = goalSelectorField.get(controlledMob);

            if (goalSelector instanceof GoalSelector selector) {
                selector.addGoal(0, new ControlledAttackGoal(owner, controlledMob, target, duration));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public ControllerType getControllerType()

    {
        return type;
    }

    /**
     * Custom goal for controlled mob attacks, allowing the mob to attack the target for a specified duration.
     */
    public static class ControlledAttackGoal extends TargetGoal {
        private final Player owner;
        private final Monster controlledMob;
        private final LivingEntity target;
        private final int duration;
        private int tickCount = 0;

        public ControlledAttackGoal(Player player, Monster controlledMob, LivingEntity target, int duration) {
            super(controlledMob, true);
            this.owner = player;
            this.controlledMob = controlledMob;
            this.target = target;
            this.duration = duration;
        }

        /**
         * Checks if the goal can start.
         *
         * @return True if the mob is not targeting the owner and can start attacking the target.
         */
        @Override
        public boolean canUse() {
            return this.controlledMob.getTarget() == this.target && this.owner != null && this.controlledMob.getTarget() != owner;
        }

        /**
         * Handles the goal logic every tick.
         */
        @Override
        public void tick() {
            super.tick();
            tickCount++;

            if (tickCount > duration) {
                this.controlledMob.setTarget(null);

            }
        }

        /**
         * Checks if the goal can continue.
         *
         * @return True if the mob is still attacking the target and the duration has not elapsed.
         */
        @Override
        public boolean canContinueToUse() {
            return tickCount <= duration && this.controlledMob.getTarget() == this.target;
        }

        /**
         * Stops the goal.
         */
        @Override
        public void stop() {
            this.controlledMob.setTarget(null);
            super.stop();
        }

    }

    /**
     * Custom goal to prevent the controlled mob from attacking its owner.
     */
    public static class PreventPlayerTargetGoal extends NearestAttackableTargetGoal<Player> {

        private final Monster controlledMob;
        private final Player owner;
        NearestAttackableTargetGoal<Player> goal ;


        /**
         * Constructs a PreventPlayerTargetGoal.
         *
         * @param controlledMob The mob being controlled.
         * @param owner The player controlling the mob.
         */
        public PreventPlayerTargetGoal(Monster controlledMob, Player owner) {
            super(controlledMob, Player.class, true);
            this.controlledMob = controlledMob;
            this.owner = owner;
            this.goal = new NearestAttackableTargetGoal<>(controlledMob, Player.class,false);

        }

        /**
         * Checks if the mob can attack players, excluding the owner.
         *
         * @return True if the mob is not targeting the owner and can attack other players.
         */
        @Override
        public boolean canUse() {
            return controlledMob.getTarget() != owner && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            if(controlledMob.getTarget() == owner) {
                controlledMob.setTarget(null);
                controlledMob.setAggressive(false);
            }
        }

        /**
         * Checks if the goal can continue to execute.
         *
         * @return True if the mob is still not targeting the owner and can continue attacking other players.
         */
        @Override
        public boolean canContinueToUse() {
            return controlledMob.getTarget() != owner && super.canContinueToUse();
        }

        public NearestAttackableTargetGoal<Player> getGoal() {
            return goal;
        }
    }

}
