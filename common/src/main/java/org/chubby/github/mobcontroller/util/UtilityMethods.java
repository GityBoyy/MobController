package org.chubby.github.mobcontroller.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.data.SaveControlledMob;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.util.ControllerChances;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

//This class will handle all the methods i need for Common Events class.
public class UtilityMethods
{
    /**
     * Assigns control of the mob to the player, sets the controller item on the mob's head, and consumes the item.
     */
    public static boolean assignControl(UUID player, Monster mob, ItemStack stack, ItemController controller) {

        if (!ControllerChances.rollControlAttempt(mob, controller.getControllerType())) {
            return false;
        }

        mob.setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
        stack.shrink(1);
        ItemController.assignControlledMob(player, mob);
        SaveControlledMob savedData;
        if(mob.level() instanceof ServerLevel level){
            savedData = SaveControlledMob.get(level);
            savedData.addControlledMob(Objects.requireNonNull(mob.level().getPlayerByUUID(player)), mob);
        }
        return true;
    }

    /**
     * Checks if the player is already controlling the specified mob.
     */
    public static boolean isPlayerControllingMob(UUID player, Monster mob)
    {
        if(ItemController.getplayerMobControlMap().get(player).isPresent()){
            return ItemController.getplayerMobControlMap().get(player).get() == mob;
        }
        return false;
    }

    /**
     * Checks if the mob is eligible to be ridden by the player (requires specific controller types).
     */
    public static boolean isMobEligibleForRide(Monster mob) {
        ItemStack headStack = mob.getItemBySlot(EquipmentSlot.HEAD);

        if (!(headStack.getItem() instanceof ItemController controller)) return false;

        ControllerType type = controller.getControllerType();
        return type == ControllerType.DIAMOND || type == ControllerType.NETHERITE;
    }

    public static Optional<Entity> isLookingAtEntity(Player player, Level level, double reachDistance) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();

        Vec3 endPos = eyePosition.add(lookVector.x * reachDistance, lookVector.y * reachDistance, lookVector.z * reachDistance);
        AABB searchBox = new AABB(eyePosition, endPos).inflate(1.0);

        List<Entity> entities = level.getEntities(player, searchBox, entity -> entity instanceof LivingEntity && entity.isAlive());

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
