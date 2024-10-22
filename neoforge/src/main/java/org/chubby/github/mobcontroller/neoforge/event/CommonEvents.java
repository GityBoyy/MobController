package org.chubby.github.mobcontroller.neoforge.event;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.util.UtilityMethods;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerRightClickMob(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();

        if (!(targetEntity instanceof Monster monster)) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemController controller)) return;

        if (player.isShiftKeyDown()) {
            UtilityMethods.assignControl(player, monster, stack, controller);
        }

        if (UtilityMethods.isPlayerControllingMob(player, monster) && UtilityMethods.isMobEligibleForRide(monster)) {
            player.startRiding(monster, true);
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Monster monster && ItemController.getplayerMobControlMap().containsValue(monster)) {
            monster.getNavigation().recomputePath();
            monster.setAggressive(false);
        }
    }


}
