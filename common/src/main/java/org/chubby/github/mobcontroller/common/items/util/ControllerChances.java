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
        registerChance(EntityType.ZOMBIE, ControlChanceData.create(64, 1.2f));
        registerChance(EntityType.SKELETON, ControlChanceData.create(60, 1.1f));
        registerChance(EntityType.SPIDER, ControlChanceData.create(56, 1.0f));
        registerChance(EntityType.CREEPER, ControlChanceData.create(48, 1.3f));
        registerChance(EntityType.DROWNED, ControlChanceData.create(60, 1.1f));
        registerChance(EntityType.HUSK, ControlChanceData.create(62, 1.2f));
        registerChance(EntityType.STRAY, ControlChanceData.create(58, 1.1f));

        // Stronger mobs
        registerChance(EntityType.WITCH, ControlChanceData.create(40, 1.5f));
        registerChance(EntityType.ENDERMAN, ControlChanceData.create(32, 1.6f));
        registerChance(EntityType.BLAZE, ControlChanceData.create(36, 1.4f));
        registerChance(EntityType.WITHER_SKELETON, ControlChanceData.create(28, 1.7f));
        registerChance(EntityType.PILLAGER, ControlChanceData.create(52, 1.2f));
        registerChance(EntityType.VINDICATOR, ControlChanceData.create(40, 1.4f));
        registerChance(EntityType.EVOKER, ControlChanceData.create(24, 1.7f));
        registerChance(EntityType.ILLUSIONER, ControlChanceData.create(20, 1.8f));

        // Mini-bosses
        registerChance(EntityType.RAVAGER, ControlChanceData.create(24, 2.0f));
        registerChance(EntityType.ELDER_GUARDIAN, ControlChanceData.create(20, 2.0f));
        registerChance(EntityType.WARDEN, ControlChanceData.create(4, 3.5f));

        // Nether mobs
        registerChance(EntityType.PIGLIN_BRUTE, ControlChanceData.create(28, 1.7f));
        registerChance(EntityType.HOGLIN, ControlChanceData.create(36, 1.5f));
        registerChance(EntityType.GHAST, ControlChanceData.create(32, 1.6f));
        registerChance(EntityType.ZOGLIN, ControlChanceData.create(24, 1.8f));
        registerChance(EntityType.MAGMA_CUBE, ControlChanceData.create(40, 1.3f));

        // End mobs
        registerChance(EntityType.SHULKER, ControlChanceData.create(28, 1.8f));
        registerChance(EntityType.ENDERMITE, ControlChanceData.create(56, 1.0f));
        registerChance(EntityType.PHANTOM, ControlChanceData.create(44, 1.2f));

        // New hostile mobs in 1.21
        registerChance(EntityType.BREEZE, ControlChanceData.create(24, 2.2f));
        registerChance(EntityType.BOGGED, ControlChanceData.create(48, 1.5f));
    }


    /**
     * @param baseChance           Base chance percentage (0-100)
     * @param controllerMultiplier Multiplier for different controller types
     */
    public record ControlChanceData(int baseChance, float controllerMultiplier)
    {
        public static ControlChanceData create(int baseChance, float controllerMultiplier)
        {
            return new ControlChanceData(baseChance,controllerMultiplier);
        }
        public static ControlChanceData withDefaultMultiplier(int baseChance)
        {
            return new ControlChanceData(baseChance,1.0F);
        }
        public static ControlChanceData withDefaultChance(float controllerMultiplier)
        {
            return new ControlChanceData(1,controllerMultiplier);
        }
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