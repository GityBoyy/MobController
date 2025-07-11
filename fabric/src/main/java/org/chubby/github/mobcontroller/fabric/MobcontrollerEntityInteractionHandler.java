package org.chubby.github.mobcontroller.fabric;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.SoulEssence;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.jetbrains.annotations.NotNull;

public class MobcontrollerEntityInteractionHandler
{
    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof Monster monster)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack existingHelmet = monster.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasController = existingHelmet.has(DataComponentRegistry.CONTROLLER.get());

            if (player.isShiftKeyDown() && hasController) {
                openMonsterInventory(serverPlayer, monster);
                return InteractionResult.CONSUME;
            }

            if (!(heldItem.getItem() instanceof ItemController controller)) return InteractionResult.PASS;

            // Controller Data
            MobControllerData controllerData = new MobControllerData(player.getUUID(), monster.getId());
            ControllerTierData tierData = new ControllerTierData(controller.getType());

            // Create the helmet item
            ItemStack helmetItem = new ItemStack(controller);
            helmetItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
            helmetItem.set(DataComponentRegistry.CONTROLLER_TIER.get(), tierData);

            monster.setItemSlot(EquipmentSlot.HEAD, helmetItem);
            heldItem.shrink(1);
            ItemController.getPlayerMobControlMap().put(player.getUUID(), monster.getId());

            // Soul Essence
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

            return InteractionResult.CONSUME;
        });
    }

    private static void openMonsterInventory(ServerPlayer player, Monster monster) {
        Services.MENU_HELPER().openMenu(player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p) {
                return new MonsterInventoryMenu(id, inventory, monster);
            }
        }, buf -> buf.writeInt(monster.getId()));
    }
}
