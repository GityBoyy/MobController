// UtilityMethods.java
package org.chubby.github.mobcontroller.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.util.ControllerChances;
import org.chubby.github.mobcontroller.networking.PacketHandler;
import org.chubby.github.mobcontroller.networking.packets.ControllerSyncPacket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UtilityMethods {

    public static boolean assignControl(UUID player, Monster monster, ItemStack stack, ItemController controller) {
        if (!ControllerChances.rollControlAttempt(monster, controller.getControllerType())) {
            return false;
        }
        PacketHandler.sendToServer(new ControllerSyncPacket(player, monster.getId(), true));
        ItemController.assignControlledMob(player, monster,controller.getControllerType());
        return true;
    }

    public static boolean isPlayerControllingMob(UUID player, Monster mob) {
        if (player == null || mob == null) {
            return false;
        }

        return ItemController.getPlayerMobControlMap()
                .get(player)
                .map(controlledMob -> controlledMob.getId() == mob.getId())
                .orElse(false);
    }

    public static boolean isMobEligibleForRide(Monster mob) {
        if (mob == null) {
            return false;
        }

        ItemStack headStack = mob.getItemBySlot(EquipmentSlot.HEAD);
        if (!(headStack.getItem() instanceof ItemController controller)) {
            return false;
        }

        ControllerType type = controller.getControllerType();
        return type == ControllerType.DIAMOND || type == ControllerType.NETHERITE;
    }

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
}