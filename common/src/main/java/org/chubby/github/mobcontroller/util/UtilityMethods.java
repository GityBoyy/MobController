package org.chubby.github.mobcontroller.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.data.SaveControlledMob;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.util.ControllerChances;

//This class will handle all the methods i need for Common Events class.
public class UtilityMethods
{
    /**
     * Assigns control of the mob to the player, sets the controller item on the mob's head, and consumes the item.
     */
    public static boolean assignControl(Player player, Monster mob, ItemStack stack, ItemController controller) {

        if (!ControllerChances.rollControlAttempt(mob, controller.getControllerType())) {
            return false;
        }

        mob.setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
        stack.shrink(1);
        ItemController.assignControlledMob(player, mob);
        SaveControlledMob savedData = SaveControlledMob.get((ServerLevel) mob.level());
        savedData.addControlledMob(player, mob);
        return true;
    }

    /**
     * Checks if the player is already controlling the specified mob.
     */
    public static boolean isPlayerControllingMob(Player player, Monster mob) {
        return ItemController.getplayerMobControlMap().get(player) == mob;
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
}
