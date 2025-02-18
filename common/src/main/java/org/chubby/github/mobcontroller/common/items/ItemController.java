package org.chubby.github.mobcontroller.common.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.core.config.MCConfig;
import org.chubby.github.mobcontroller.util.SafeConcurrentMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemController extends Item implements Equipable {

    public final ControllerType type;
    private final int ATTACK_DURATION;
    private static final SafeConcurrentMap<UUID, Monster> PLAYER_MONSTER_MAP = new SafeConcurrentMap<>();

    public ItemController(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
        this.ATTACK_DURATION = type.getControlTime();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            Monster controlledMob = findControlledMob(player.getUUID());

            if (controlledMob != null) {
                if (controlledMob.getTarget() == player) {
                    controlledMob.setTarget(null);
                    controlledMob.setAggressive(false);
                }

                LivingEntity lastHurtMob = player.getLastHurtMob();
                if (lastHurtMob != null) {
                    startControlledAttack(player.getUUID(), controlledMob, lastHurtMob, MCConfig.controlTick.getValue());
                }

                LivingEntity lastHurtByMob = player.getLastHurtByMob();
                if (lastHurtByMob != null && lastHurtByMob != controlledMob) {
                    startControlledAttack(player.getUUID(), controlledMob, lastHurtByMob, MCConfig.controlTick.getValue());
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }


    /**
     * Finds the mob controlled by the given player.
     *
     * @param uuid The player controlling the mob.
     * @return The controlled mob or null if none is found.
     */
    private Monster findControlledMob(UUID uuid) {
        if(getplayerMobControlMap().get(uuid).isPresent()) return getplayerMobControlMap().get(uuid).get();
        return null;
    }

    /**
     * Assigns a mob to a player for control, preventing the mob from attacking the player.
     *
     * @param playerUUID The player's uuid controlling the mob.
     * @param mob The mob being controlled.
     */
    public static void assignControlledMob(UUID playerUUID, Monster mob) {

        getplayerMobControlMap().put(playerUUID, mob);
    }

    /**
     * Initiates an attack by the controlled mob on the specified target for a limited duration.
     *
     * @param owner The player controlling the mob.
     * @param controlledMob The mob being controlled.
     * @param target The target of the attack.
     * @param duration The duration of the attack in ticks.
     */
    private void startControlledAttack(UUID owner, Monster controlledMob, LivingEntity target, int duration) {
        if (ItemController.getplayerMobControlMap().containsKey(owner) && ItemController.getplayerMobControlMap().get(owner).isPresent() &&
                ItemController.getplayerMobControlMap().get(owner).get() == controlledMob) {

            if (target != controlledMob.level().getPlayerByUUID(owner)) {
                controlledMob.setTarget(target);
                controlledMob.setAggressive(true);
            }
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

    public static SafeConcurrentMap<UUID, Monster> getplayerMobControlMap()
    {
        return PLAYER_MONSTER_MAP;
    }

    private static class MonsterControl {
        final Monster monster;
        final long startTime;
        boolean inventoryOpen;

        MonsterControl(Monster monster) {
            this.monster = monster;
            this.startTime = System.currentTimeMillis();
            this.inventoryOpen = false;
        }
    }
}