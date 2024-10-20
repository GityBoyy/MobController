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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemController extends Item implements Equipable {

    public final ControllerType type;
    private final int ATTACK_DURATION;
    private static final Map<Player, Monster> PLAYER_MONSTER_MAP = new HashMap<>();


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
        return getplayerMobControlMap().get(player);
    }

    /**
     * Assigns a mob to a player for control, preventing the mob from attacking the player.
     *
     * @param player The player controlling the mob.
     * @param mob The mob being controlled.
     */
    public static void assignControlledMob(Player player, Monster mob) {
        getplayerMobControlMap().put(player, mob);
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
        if (controlledMob.getTarget() instanceof Player player) {
            if(ItemController.getplayerMobControlMap().containsKey(owner)){
                controlledMob.setAggressive(false);
                controlledMob.setTarget(target);
            }

        }

        controlledMob.setTarget(target);
    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public ControllerType getControllerType()
    {
        return type;
    }

    public static Map<Player, Monster> getplayerMobControlMap()
    {
        return PLAYER_MONSTER_MAP;
    }
}