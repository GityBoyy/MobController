package org.chubby.github.mobcontroller.neoforge.event;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.items.ItemController;

import java.util.Set;
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerRightClickMob(PlayerInteractEvent.EntityInteract event)
    {
        Player player = event.getEntity();
        Level level = event.getLevel();
        Entity entity = event.getTarget();
        if(entity instanceof Monster mob)
        {
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof ItemController && player.isShiftKeyDown()){
                mob.setItemSlot(EquipmentSlot.HEAD,stack.copyWithCount(1));
                stack.consume(1,player);
                ItemController.assignControlledMob(player,mob);
            }

            if (ItemController.getplayerMobControlMap().containsKey(player) && ItemController.getplayerMobControlMap().get(player).equals(mob)) {
                if (!mob.isPassenger()) {
                    player.startRiding(mob,true);
                }
            }
        }

    }

    @SubscribeEvent
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Monster monster)
        {
            if(ItemController.getplayerMobControlMap().containsValue(monster))
            {
                monster.getNavigation().recomputePath();
                monster.setAggressive(false);
            }
        }
    }
}
