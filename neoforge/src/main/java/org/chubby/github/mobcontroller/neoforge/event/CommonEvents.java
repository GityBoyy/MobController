package org.chubby.github.mobcontroller.neoforge.event;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.data.MobControllerDataManager;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerRightClickMob(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(event.getTarget() instanceof Monster monster)) {
            return;
        }

        ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() instanceof ItemController && player.isShiftKeyDown()) {
            CompoundTag controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
            if (controllerData != null && controllerData.hasUUID("controllerUUID")) {
                UUID controllerUUID = controllerData.getUUID("controllerUUID");
                boolean isControlled = controllerUUID.equals(player.getUUID()) ||
                        MobControllerDataManager.isControlled(monster);

                if (isControlled) {
                    SimpleContainer inventory = MobControllerDataManager.getMonsterInventory(monster);
                    if (inventory == null) {
                        Constants.LOGGER.error("Monster inventory is null! Aborting menu opening.");
                        return;
                    }
                    if (inventory.isEmpty()) {
                        Constants.LOGGER.warn("Monster inventory is empty! It might close instantly.");
                    }

                    if (controllerData.contains("Inventory")) {
                        CompoundTag invData = new CompoundTag();
                        invData.put("Inventory", Objects.requireNonNull(controllerData.get("Inventory")));
                        MobControllerDataManager.loadControllerData(monster, invData);
                    }

                    Objects.requireNonNull(player.getServer()).execute(() -> {
                        MenuRegistry.openExtendedMenu(player, new MenuProvider() {
                            @Override
                            public @NotNull Component getDisplayName() {
                                return Component.empty();
                            }

                            @Override
                            public @NotNull AbstractContainerMenu createMenu(int i, Inventory arg, Player arg2) {
                                return new MonsterInventoryMenu(i, arg, inventory, monster);
                            }
                        }, buf -> buf.writeInt(monster.getId()));
                    });

                }
            }
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof ItemController controller) {
            handleControllerInteraction(player, monster, heldItem, controller);
        }
    }

    private static void handleControllerInteraction(ServerPlayer player, Monster monster, ItemStack stack, ItemController controller) {
        ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() instanceof ItemController) {
            player.displayClientMessage(
                    Component.translatable("message.mobcontroller.alreadyhascontroller")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        if (MobControllerDataManager.isControlled(monster)) {
            CompoundTag data = headItem.get(DataComponentRegistry.CONTROLLER.get());
            if (data != null && data.hasUUID("controllerUUID")) {
                UUID controllerUUID = data.getUUID("controllerUUID");
                if (controllerUUID.equals(player.getUUID())) {
                    player.displayClientMessage(
                            Component.translatable("message.mobcontroller.alreadycontrolled")
                                    .withStyle(ChatFormatting.RED),
                            true
                    );
                    return;
                }
            }
        }

        if (UtilityMethods.assignControl(player.getUUID(), monster, stack, controller)) {
            player.displayClientMessage(
                    Component.translatable("message.mobcontroller.assignedcontrol")
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (monster.getItemBySlot(EquipmentSlot.HEAD).has(DataComponentRegistry.CONTROLLER.get())) {
            monster.getNavigation().recomputePath();
            monster.setAggressive(false);
            monster.setTarget(null);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (!(event.getNewAboutToBeSetTarget() instanceof Player targetPlayer)) {
            return;
        }

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
            var controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
            if (controllerData != null && controllerData.hasUUID("controllerUUID")) {
                UUID controllerUUID = controllerData.getUUID("controllerUUID");
                if (controllerUUID.equals(targetPlayer.getUUID())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        MobControllerDataManager.syncFromWorld(serverLevel);

        MobControllerDataManager.getControlledMob(playerUUID).ifPresent(monster -> {
            player.displayClientMessage(
                    Component.translatable("message.mobcontroller.controlrestored")
                            .withStyle(ChatFormatting.GREEN)
                            .append(monster.getName()),
                    true
            );
        });
    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Player player = event.getEntity();

        MobControllerDataManager.handlePlayerLogout(player.getUUID());
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            MobControllerDataManager.syncFromWorld(serverLevel);
            Constants.LOGGER.info("World loaded - restored controlled mob relationships");
        }
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event) {
        GogglesScreen.onRenderGui(event.getGuiGraphics());
    }
}