package org.chubby.github.mobcontroller.neoforge.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.common.data.SaveControlledMob;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.chubby.github.mobcontroller.util.Utils;

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
            if (UtilityMethods.assignControl(player, monster, stack, controller)) {
                player.displayClientMessage(Component.translatable("message.mobcontroller.control_success")
                        .withStyle(ChatFormatting.GREEN), true);

                player.level().playSound(null, monster.getX(), monster.getY(), monster.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);

                for (int i = 0; i < 10; i++) {
                    player.level().addParticle(ParticleTypes.END_ROD,
                            monster.getX() + (player.level().random.nextDouble() - 0.5D) * 2.0D,
                            monster.getY() + player.level().random.nextDouble() * 2.0D,
                            monster.getZ() + (player.level().random.nextDouble() - 0.5D) * 2.0D,
                            0, 0, 0);
                }
            }

            if (UtilityMethods.isPlayerControllingMob(player, monster) && UtilityMethods.isMobEligibleForRide(monster)) {
                player.startRiding(monster, true);
            }
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityTarget(LivingChangeTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getNewAboutToBeSetTarget();

        if (entity instanceof Monster monster && target instanceof Player player) {
            if (ItemController.getplayerMobControlMap().containsKey(player) &&
                    ItemController.getplayerMobControlMap().get(player) == monster) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            SaveControlledMob savedData = SaveControlledMob.get(serverLevel);
            savedData.loadControlledMobs(serverLevel);
        }
    }


    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SaveControlledMob savedData = SaveControlledMob.get((ServerLevel) event.getEntity().level());
        savedData.setDirty();
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event)
    {
        GogglesScreen.onRenderGui(event.getGuiGraphics());
    }
}
