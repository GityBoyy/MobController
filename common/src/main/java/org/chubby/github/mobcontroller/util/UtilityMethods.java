// UtilityMethods.java
package org.chubby.github.mobcontroller.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.items.ItemController;

import java.util.List;
import java.util.Optional;

public class UtilityMethods {


    public static Optional<Entity> isLookingAtEntity(Player player, Level level, double reachDistance) {
        if (player == null || level == null || reachDistance <= 0) {
            return Optional.empty();
        }

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(
                lookVector.x * reachDistance,
                lookVector.y * reachDistance,
                lookVector.z * reachDistance
        );

        AABB searchBox = new AABB(eyePosition, endPos).inflate(1.0);

        List<Entity> entities = level.getEntities(player, searchBox,
                entity -> entity instanceof LivingEntity && entity.isAlive());

        return entities.stream()
                .filter(e -> {
                    AABB bbox = e.getBoundingBox();
                    return bbox.clip(eyePosition, endPos).isPresent();
                })
                .min((e1, e2) -> {
                    double d1 = e1.distanceToSqr(eyePosition);
                    double d2 = e2.distanceToSqr(eyePosition);
                    return Double.compare(d1, d2);
                });
    }

    public static void updateMobBehavior(Monster monster, Player controller) {
        ItemController.MonsterStates state = ItemController.currentState;

        if (state != ItemController.MonsterStates.AGGRESSIVE) {
            double followDistance = 3.0;
            if (monster.distanceToSqr(controller) > followDistance * followDistance) {
                monster.getNavigation().moveTo(controller, 1.0);
            }
        }

        switch (state) {
            case AGGRESSIVE -> {
                if (controller.getLastHurtMob() != null) {
                    monster.setTarget(controller.getLastHurtMob());
                }
            }
            case DEFENSIVE -> {
                if (controller.getLastHurtByMob() != null) {
                    monster.setTarget(controller.getLastHurtByMob());
                }
            }
            case PASSIVE -> {
                monster.setTarget(null);
            }
        }
    }
}