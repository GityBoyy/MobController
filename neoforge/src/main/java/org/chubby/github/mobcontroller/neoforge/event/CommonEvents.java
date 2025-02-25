package org.chubby.github.mobcontroller.neoforge.event;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerRightClickMob(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Monster monster)) return;

        var player = event.getEntity();
        var heldItem = player.getMainHandItem();

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack existingHelmet = monster.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasController = existingHelmet.has(DataComponentRegistry.CONTROLLER.get());

        if (player.isShiftKeyDown() && hasController) {
            openMonsterInventory(serverPlayer, monster);
            event.setCanceled(true);
            return;
        }

        if (!(heldItem.getItem() instanceof ItemController controller)) return;

        ItemStack helmetItem = new ItemStack(controller);

        MobControllerData controllerData = new MobControllerData(player.getUUID(), monster.getId());
        helmetItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);

        ControllerTierData tierData = new ControllerTierData(controller.getType());
        helmetItem.set(DataComponentRegistry.CONTROLLER_TIER.get(), tierData);

        monster.setItemSlot(EquipmentSlot.HEAD, helmetItem);

        heldItem.shrink(1);

        ItemController.getPlayerMobControlMap().put(player.getUUID(), monster.getId());

        if (player.isShiftKeyDown()) {
            openMonsterInventory(serverPlayer, monster);
        }

        event.setCanceled(true);
    }

    private static void openMonsterInventory(ServerPlayer player, Monster monster) {
        MenuRegistry.openExtendedMenu(player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("container.mobcontroller.monster_inventory");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player playerEntity) {
                return new MonsterInventoryMenu(i, inventory, monster);
            }
        }, buf -> buf.writeInt(monster.getId()));
    }

    @SubscribeEvent
    public static void mobTickEvent(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Monster monster)) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        UUID playerUUID = attachment.getControllingPlayer();
        var player = monster.level().getPlayerByUUID(playerUUID);
        if (player == null) return;
        if(monster.getTarget() == player)
        {
            monster.setTarget(null);
            monster.setAggressive(false);
        }

        if (player.getLastHurtMob() != null) {
            ItemController.setMonsterState(ItemController.MonsterStates.AGGRESSIVE);
        } else if (player.getLastHurtByMob() != null) {
            ItemController.setMonsterState(ItemController.MonsterStates.DEFENSIVE);
        } else {
            ItemController.setMonsterState(ItemController.MonsterStates.PASSIVE);
        }

        UtilityMethods.updateMobBehavior(monster, player);
    }

    @SubscribeEvent
    public static void onLivingTargetChange(LivingChangeTargetEvent event)
    {
        if(!(event.getEntity() instanceof Monster monster)) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        UUID playerUUID = attachment.getControllingPlayer();
        var player = monster.level().getPlayerByUUID(playerUUID);
        if (player == null) return;

        if (event.getNewAboutToBeSetTarget() == player) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event)
    {
        if(event.getLevel().isClientSide()) return;

        for (var entry : ItemController.getPlayerMobControlMap().entrySet()) {
            UUID playerUUID = entry.getKey();
            int mobId = entry.getValue();

            var level = event.getLevel();
            var player = event.getLevel().getPlayerByUUID(playerUUID);
            if(player == null) return;
            var entity = level.getServer().getLevel(player.level().dimension()).getEntity(mobId);

            if (entity instanceof Monster monster) {
                var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
                if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
                    MobControllerData controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
                    headItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
                    monster.setItemSlot(EquipmentSlot.HEAD, headItem);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        if (ItemController.getPlayerMobControlMap().containsKey(playerUUID)) {
            int mobId = ItemController.getPlayerMobControlMap().get(playerUUID);
            var entity = player.level().getEntity(mobId);

            if (entity instanceof Monster monster) {
                var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
                if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
                    MobControllerData controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event) {
        GogglesScreen.onRenderGui(event.getGuiGraphics());
    }
}