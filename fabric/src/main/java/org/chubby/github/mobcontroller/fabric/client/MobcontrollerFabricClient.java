package org.chubby.github.mobcontroller.fabric.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MobcontrollerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UseEntityCallback.EVENT.register(this::playerRightClickEntity);
        EntityEvent.LIVING_DEATH.register(this::onLivingDeath);
        ServerWorldEvents.LOAD.register(this::onWorldLoad);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen != null) {
                ScreenEvents.afterRender(client.screen).register((screen, guiGraphics, mouseX, mouseY, delta) -> {
                    GogglesScreen.onRenderGui(guiGraphics);
                });
            }
        });
    }

    private void onWorldLoad(MinecraftServer minecraftServer, ServerLevel level)
    {
        if(level.getLevel().isClientSide()) return;

        for (var entry : ItemController.getPlayerMobControlMap().entrySet()) {
            UUID playerUUID = entry.getKey();
            int mobId = entry.getValue();

            var player = level.getLevel().getPlayerByUUID(playerUUID);
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


    private InteractionResult playerRightClickEntity(Player player, Level world, InteractionHand hand, Entity targetEntity ,EntityHitResult hitResult) {

        if (!(targetEntity instanceof Monster monster)) return InteractionResult.FAIL;

        var heldItem = player.getMainHandItem();

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.FAIL;

        ItemStack existingHelmet = monster.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasController = existingHelmet.has(DataComponentRegistry.CONTROLLER.get());

        if (player.isShiftKeyDown() && hasController) {
            openMonsterInventory(serverPlayer, monster);
            return InteractionResult.PASS;
        }

        if (!(heldItem.getItem() instanceof ItemController controller)) return InteractionResult.FAIL;

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
        return InteractionResult.SUCCESS;
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

    private EventResult onLivingDeath(LivingEntity entity, DamageSource damageSource)
    {
        if(!(entity instanceof Monster monster)) return EventResult.interrupt(true);

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return EventResult.interrupt(true);

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return EventResult.interrupt(true);

        attachment.mobInventory.getItems().forEach(item ->
                Containers.dropItemStack(monster.level(), monster.getX(), monster.getY(), monster.getZ(), item));

        return EventResult.pass();
    }
}
