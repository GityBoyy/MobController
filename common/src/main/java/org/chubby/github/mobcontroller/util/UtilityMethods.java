package org.chubby.github.mobcontroller.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.enums.EnumControlledStates;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UtilityMethods {

    private static Vec3 homePos;
    /**
     * Determines if the player is looking at a living entity within a specified reach distance.
     *
     * @param player        the player
     * @param level         the world level
     * @param reachDistance max distance to check
     * @return Optional entity the player is looking at, if any
     */
    public static Optional<Entity> isLookingAtEntity(Player player, Level level, double reachDistance) {
        if (player == null || level == null || reachDistance <= 0) {
            return Optional.empty();
        }

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reachDistance, lookVector.y * reachDistance, lookVector.z * reachDistance);
        AABB searchBox = new AABB(eyePosition, endPos).inflate(1.0);

        List<Entity> entities = level.getEntities(player, searchBox, entity -> entity instanceof LivingEntity && entity.isAlive());

        return entities.stream()
                .filter(e -> e.getBoundingBox().clip(eyePosition, endPos).isPresent())
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(eyePosition)));
    }

    /**
     * Updates the behavior of a controlled monster based on its current state.
     *
     * @param monster       the monster being controlled
     * @param controller    the player controlling the monster
     * @param stateHandler  the handler managing monster's behavior states
     */
    public static void updateMobBehavior(Monster monster, Player controller, EntityStateHandler stateHandler) {
        if (monster == null || controller == null || stateHandler == null) {
            return;
        }

        stateHandler.tick();
        EnumControlledStates currentState = stateHandler.getCurrentState();

        switch (currentState) {
            case STAY -> handleStayBehavior(monster, controller);
            case FOLLOW -> handleFollowBehavior(monster, controller);
            case DEFEND -> handleDefendBehavior(monster, controller);
            case ATTACK -> handleAttackBehavior(monster, controller);
            case GO_HOME -> handleGoHomeBehavior(monster, controller);
        }
    }

    private static void handleStayBehavior(Monster monster, Player controller) {
        monster.getNavigation().stop();
        monster.setTarget(null);
        monster.getLookControl().setLookAt(controller, 10.0F, 10.0F);
    }

    private static void handleFollowBehavior(Monster monster, Player controller) {
        double followDistance = 3.0;
        double stopDistance = 2.0;
        double teleportDistance = 20.0;

        double distanceToController = monster.distanceToSqr(controller);

        if (distanceToController > teleportDistance * teleportDistance) {
            Vec3 controllerPos = controller.position();
            monster.teleportTo(controllerPos.x, controllerPos.y, controllerPos.z);
            return;
        }

        if (distanceToController > followDistance * followDistance) {
            monster.getNavigation().moveTo(controller, 1.2);
        } else if (distanceToController < stopDistance * stopDistance) {
            monster.getNavigation().stop();
        }

        monster.setTarget(null);
    }

    private static void handleDefendBehavior(Monster monster, Player controller) {
        double defendRadius = 8.0;
        double followDistance = 4.0;

        if (monster.distanceToSqr(controller) > followDistance * followDistance) {
            monster.getNavigation().moveTo(controller, 1.0);
        }

        LivingEntity threat = null;

        if (controller.getLastHurtByMob() != null &&
                controller.getLastHurtByMob().distanceToSqr(controller) <= defendRadius * defendRadius) {
            threat = controller.getLastHurtByMob();
        }

        if (threat == null && controller.getLastHurtMob() != null &&
                controller.getLastHurtMob().distanceToSqr(controller) <= defendRadius * defendRadius) {
            threat = controller.getLastHurtMob();
        }

        if (threat == null) {
            threat = findNearbyThreat(monster, controller, defendRadius);
        }

        monster.setTarget(threat);
    }

    private static void handleAttackBehavior(Monster monster, Player controller) {
        double attackRadius = 12.0;
        LivingEntity target = null;

        if (controller.getLastHurtMob() != null &&
                controller.getLastHurtMob().distanceToSqr(monster) <= attackRadius * attackRadius) {
            target = controller.getLastHurtMob();
        }

        if (target == null && controller.getLastHurtByMob() != null &&
                controller.getLastHurtByMob().distanceToSqr(monster) <= attackRadius * attackRadius) {
            target = controller.getLastHurtByMob();
        }

        if (target == null) {
            target = findNearestHostile(monster, controller, attackRadius);
        }

        monster.setTarget(target);

        if (target == null) {
            handleFollowBehavior(monster, controller);
        }
    }

    private static void handleGoHomeBehavior(Monster monster, Player controller) {
        Vec3 homePosition = getHomePosition(monster, controller);
        double homeDistance = 2.0;

        if (monster.distanceToSqr(homePosition) > homeDistance * homeDistance) {
            monster.getNavigation().moveTo(homePosition.x, homePosition.y, homePosition.z, 1.0);
        } else {
            monster.getNavigation().stop();
        }

        monster.setTarget(null);
    }

    /**
     * Searches for a nearby entity that is targeting the controller.
     *
     * @param monster the controlled monster
     * @param controller the controller player
     * @param radius search radius
     * @return the nearest threat, or null if none found
     */
    private static Monster findNearbyThreat(Monster monster, Player controller, double radius) {
        return controller.level().getEntitiesOfClass(Monster.class,
                        controller.getBoundingBox().inflate(radius))
                .stream()
                .filter(entity -> entity != monster)
                .filter(entity -> entity.getTarget() == controller)
                .filter(LivingEntity::isAlive)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(controller)))
                .orElse(null);
    }

    /**
     * Searches for the nearest hostile entity near the monster.
     *
     * @param monster the monster entity
     * @param controller the controlling player
     * @param radius search radius
     * @return nearest hostile entity, or null
     */
    private static Monster findNearestHostile(Monster monster, Player controller, double radius) {
        return monster.level().getEntitiesOfClass(Monster.class,
                        monster.getBoundingBox().inflate(radius))
                .stream()
                .filter(entity -> entity != monster)
                .filter(entity -> isHostileToPlayer(entity, controller))
                .filter(LivingEntity::isAlive)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(monster)))
                .orElse(null);
    }

    /**
     * Checks if an entity is considered hostile to the player.
     *
     * @param entity entity to check
     * @param player the player
     * @return true if hostile, otherwise false
     */
    private static boolean isHostileToPlayer(Monster entity, Player player) {
        if (entity.getTarget() == player) return true;
        return entity instanceof Monster;
    }

    /**
     * Returns the "home" position for a monster.
     * Default is the controller's respawn position or their current position.
     *
     * @param monster    the monster entity
     * @param controller the player controller
     * @return home position vector
     */
    private static Vec3 getHomePosition(Monster monster, Player controller) {
        return homePos;
    }

    /**
     * Updates monster behavior and applies automatic state transitions based on conditions.
     *
     * @param monster       the controlled monster
     * @param controller    the player controlling the monster
     * @param stateHandler  the handler managing states and transitions
     */
    public static void updateMobBehaviorWithAutoTransition(Monster monster, Player controller, EntityStateHandler stateHandler) {
        updateMobBehavior(monster, controller, stateHandler);

        EnumControlledStates currentState = stateHandler.getCurrentState();

        switch (currentState) {
            case GO_HOME -> {
                Vec3 homePosition = getHomePosition(monster, controller);
                if (monster.distanceToSqr(homePosition) <= 4.0) {
                    stateHandler.transitionTo(EnumControlledStates.STAY);
                }
            }
            case ATTACK -> {
                if (monster.getTarget() == null && stateHandler.hasBeenInStateFor(5000)) {
                    stateHandler.transitionTo(EnumControlledStates.FOLLOW);
                }
            }
            case DEFEND -> {
                if (monster.getTarget() == null && stateHandler.hasBeenInStateFor(3000)) {
                    stateHandler.transitionTo(EnumControlledStates.FOLLOW);
                }
            }
        }
    }

    /**
     * @deprecated Use {@link #updateMobBehavior(Monster, Player, EntityStateHandler)} instead.
     */
    @Deprecated(
            since = "1.0.0",
            forRemoval = true

    )
    public static void updateMobBehavior(Monster monster, Player controller) {
        handleFollowBehavior(monster, controller);
    }

    public static void setHomePos(Vec3 homePos) {
        UtilityMethods.homePos = homePos;
    }
}
