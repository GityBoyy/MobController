package org.chubby.github.mobcontroller.neoforge.event;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.enums.EnumControlledStates;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.SoulEssence;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.core.config.MCConfig;
import org.chubby.github.mobcontroller.debug.screen.DebugScreen;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.EntityStateHandler;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class CommonEvents {

    private static final DebugScreen DEBUG_SCREEN = new DebugScreen();
    private static final Map<Integer, EntityStateHandler> MONSTER_STATE_HANDLERS = new HashMap<>();

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

        MobControllerData controllerData = new MobControllerData(player.getUUID(), monster.getId());
        ControllerTierData tierData = new ControllerTierData(controller.getType(),controller.getType().getElectrolyteAmount());

        ItemStack helmetItem = new ItemStack(controller);
        helmetItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
        helmetItem.set(DataComponentRegistry.CONTROLLER_TIER.get(), tierData);

        monster.setItemSlot(EquipmentSlot.HEAD, helmetItem);

        heldItem.shrink(1);
        ItemController.getPlayerMobControlMap().put(player.getUUID(), monster.getId());

        if (heldItem.has(DataComponentRegistry.SOUL_ESSENCE.get())) {
            SoulEssenceData essenceData = heldItem.get(DataComponentRegistry.SOUL_ESSENCE.get());
            if (essenceData != null) {
                essenceData.addControlledMob(monster.getId(), player.getUUID());

                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() instanceof SoulEssence &&
                            !stack.has(DataComponentRegistry.SOUL_ESSENCE.get())) {
                        stack.set(DataComponentRegistry.SOUL_ESSENCE.get(), essenceData);
                        break;
                    }
                }
            }
        }

        if (player.isShiftKeyDown()) {
            openMonsterInventory(serverPlayer, monster);
        }

        event.setCanceled(true);
    }

    private static void openMonsterInventory(ServerPlayer player, Monster monster) {
        Services.MENU_HELPER().openMenu(player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new MonsterInventoryMenu(i,inventory,monster);
            }
        },buf -> buf.writeInt(monster.getId()));
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

        if(monster.getTarget() == player) {
            monster.setTarget(null);
            monster.setAggressive(false);
        }

        EntityStateHandler stateHandler = MONSTER_STATE_HANDLERS.computeIfAbsent(
                monster.getId(),
                id -> new EntityStateHandler(EnumControlledStates.FOLLOW)
        );

        if (player.getLastHurtMob() != null) {
            stateHandler.transitionTo(EnumControlledStates.ATTACK);
        } else if (player.getLastHurtByMob() != null) {
            stateHandler.transitionTo(EnumControlledStates.DEFEND);
        } else {
            if (stateHandler.isAggressiveState() && stateHandler.hasBeenInStateFor(3000)) {
                stateHandler.transitionTo(EnumControlledStates.FOLLOW);
            }
        }

        EquipmentSlot[] armorSlots = { EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        int[] inventorySlots = { 1, 2, 3 };
        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack armorPiece = attachment.mobInventory.getItem(inventorySlots[i]);
            if (armorPiece.getItem() instanceof ArmorItem) {
                monster.setItemSlot(armorSlots[i], armorPiece);
            }
        }

        ItemStack controllerSlotItem = attachment.mobInventory.getItem(4);
        if (controllerSlotItem.isEmpty()) {
            ItemStack simpleController = new ItemStack(headItem.getItem());

            if (headItem.has(DataComponentRegistry.CONTROLLER_TIER.get())) {
                simpleController.set(DataComponentRegistry.CONTROLLER_TIER.get(),
                        headItem.get(DataComponentRegistry.CONTROLLER_TIER.get()));
            }

            attachment.mobInventory.setItem(4, simpleController);

            headItem.set(DataComponentRegistry.CONTROLLER.get(), attachment);
            monster.setItemSlot(EquipmentSlot.HEAD, headItem);
        }

        UtilityMethods.updateMobBehaviorWithAutoTransition(monster, player, stateHandler);
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
    public static void onLivingDeathEvent(LivingDeathEvent event)
    {
        if (!(event.getEntity() instanceof Monster monster)) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        MONSTER_STATE_HANDLERS.remove(monster.getId());

        attachment.mobInventory.getItems().forEach(item ->
                Containers.dropItemStack(monster.level(), monster.getX(), monster.getY(), monster.getZ(), item));

    }

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Monster victim)) return;

        var victimHead = victim.getItemBySlot(EquipmentSlot.HEAD);
        if (!victimHead.has(DataComponentRegistry.CONTROLLER.get())) return;
        var victimData = victimHead.get(DataComponentRegistry.CONTROLLER.get());
        if (victimData == null) return;

        if (!(event.getSource().getEntity() instanceof Monster attacker)) return;

        var attackerHead = attacker.getItemBySlot(EquipmentSlot.HEAD);
        if (!attackerHead.has(DataComponentRegistry.CONTROLLER.get())) return;
        var attackerData = attackerHead.get(DataComponentRegistry.CONTROLLER.get());
        if (attackerData == null) return;

        if (victimData.getControllingPlayer().equals(attackerData.getControllingPlayer())) {
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
    public static void onCraftItem(PlayerEvent.ItemCraftedEvent event) {
        ItemStack craftedStack = event.getCrafting();
        Player player = event.getEntity();
        Level level = event.getEntity().level();

        if (craftedStack.getItem() instanceof ItemController controller) {
            MobControllerData controllerData = new MobControllerData(player.getUUID(), -1);
            craftedStack.set(DataComponentRegistry.CONTROLLER.get(), controllerData);

            ControllerTierData tierData = new ControllerTierData(controller.getType(),controller.getType().getElectrolyteAmount());
            craftedStack.set(DataComponentRegistry.CONTROLLER_TIER.get(), tierData);

            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack ingredient = event.getInventory().getItem(i);
                if (ingredient.getItem() instanceof SoulEssence &&
                        ingredient.has(DataComponentRegistry.SOUL_ESSENCE.get())) {
                    SoulEssenceData data = ingredient.get(DataComponentRegistry.SOUL_ESSENCE.get());
                    if (data != null) {
                        craftedStack.set(DataComponentRegistry.SOUL_ESSENCE.get(), data.copy());
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event) {
        GogglesScreen.onRenderGui(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (event.getName() == VanillaGuiLayers.DEBUG_OVERLAY && MCConfig.enableDebug.getValue()) {
            Minecraft minecraft = Minecraft.getInstance();
            //DEBUG_SCREEN.layer.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}