package org.chubby.github.mobcontroller.common.items.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import org.chubby.github.mobcontroller.common.items.ControllerType;

import java.util.HashMap;
import java.util.Map;

import static org.chubby.github.mobcontroller.common.items.ControllerType.*;

public class ControllerChances {
    private static final Map<EntityType<?>, ControlChanceData> CONTROL_CHANCES = new HashMap<>();

    static {
        // Basic hostile mobs
        registerChance(EntityType.ZOMBIE, new ControlChanceData(80, 1.2f));
        registerChance(EntityType.SKELETON, new ControlChanceData(75, 1.1f));
        registerChance(EntityType.SPIDER, new ControlChanceData(70, 1.0f));
        registerChance(EntityType.CREEPER, new ControlChanceData(60, 1.3f));
        registerChance(EntityType.DROWNED, new ControlChanceData(75, 1.1f));
        registerChance(EntityType.HUSK, new ControlChanceData(78, 1.2f));
        registerChance(EntityType.STRAY, new ControlChanceData(72, 1.1f));

        // Stronger mobs
        registerChance(EntityType.WITCH, new ControlChanceData(50, 1.5f));
        registerChance(EntityType.ENDERMAN, new ControlChanceData(40, 1.6f));
        registerChance(EntityType.BLAZE, new ControlChanceData(45, 1.4f));
        registerChance(EntityType.WITHER_SKELETON, new ControlChanceData(35, 1.7f));
        registerChance(EntityType.PILLAGER, new ControlChanceData(65, 1.2f));
        registerChance(EntityType.VINDICATOR, new ControlChanceData(50, 1.4f));
        registerChance(EntityType.EVOKER, new ControlChanceData(30, 1.7f));
        registerChance(EntityType.ILLUSIONER, new ControlChanceData(25, 1.8f));

        // Mini-bosses
        registerChance(EntityType.RAVAGER, new ControlChanceData(30, 2.0f));
        registerChance(EntityType.ELDER_GUARDIAN, new ControlChanceData(25, 2.0f));
        registerChance(EntityType.WARDEN, new ControlChanceData(5, 3.5f));

        // Nether mobs
        registerChance(EntityType.PIGLIN_BRUTE, new ControlChanceData(35, 1.7f));
        registerChance(EntityType.HOGLIN, new ControlChanceData(45, 1.5f));
        registerChance(EntityType.GHAST, new ControlChanceData(40, 1.6f));
        registerChance(EntityType.ZOGLIN, new ControlChanceData(30, 1.8f));
        registerChance(EntityType.MAGMA_CUBE, new ControlChanceData(50, 1.3f));

        // End mobs
        registerChance(EntityType.SHULKER, new ControlChanceData(35, 1.8f));
        registerChance(EntityType.ENDERMITE, new ControlChanceData(70, 1.0f));
        registerChance(EntityType.PHANTOM, new ControlChanceData(55, 1.2f));

        // New hostile mobs in 1.21
        registerChance(EntityType.BREEZE, new ControlChanceData(30, 2.2f));
        registerChance(EntityType.BOGGED, new ControlChanceData(60, 1.5f));
    }

    /**
     * @param baseChance           Base chance percentage (0-100)
     * @param controllerMultiplier Multiplier for different controller types
     */
    public record ControlChanceData(int baseChance, float controllerMultiplier) {
    }

    private static void registerChance(EntityType<?> entityType, ControlChanceData data) {
        CONTROL_CHANCES.put(entityType, data);
    }

    public static int getControlChance(Monster monster, ControllerType controllerType) {
        ControlChanceData data = CONTROL_CHANCES.getOrDefault(monster.getType(),
                new ControlChanceData(50, 1.0f));

        float typeMultiplier;
        if (controllerType == COPPER) {
            typeMultiplier = 0.8f;
        } else if (controllerType == IRON) {
            typeMultiplier = 1.0f;
        } else if (controllerType == GOLD) {
            typeMultiplier = 1.2f;
        } else if (controllerType == DIAMOND) {
            typeMultiplier = 1.5f;
        } else if (controllerType == NETHERITE) {
            typeMultiplier = 2.0f;
        } else {
            typeMultiplier = 1.0f;
        }

        return Math.min(100, Math.round(data.baseChance * data.controllerMultiplier * typeMultiplier));
    }

    public static boolean rollControlAttempt(Monster monster, ControllerType controllerType) {
        int chance = getControlChance(monster, controllerType);
        return monster.getRandom().nextInt(100) < chance;
    }
}