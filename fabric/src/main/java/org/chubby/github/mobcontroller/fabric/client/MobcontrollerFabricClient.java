package org.chubby.github.mobcontroller.fabric.client;

import dev.architectury.event.events.common.PlayerEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.chubby.github.mobcontroller.client.screen.GogglesScreen;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.util.UtilityMethods;

public final class MobcontrollerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UseEntityCallback.EVENT.register(this::playerRightClickEntity);
        PlayerEvent.PLAYER_JOIN.register(this::onPlayerJoinWorld);
        PlayerEvent.PLAYER_QUIT.register(this::onPlayerLeaveWorld);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen != null) {
                ScreenEvents.afterRender(client.screen).register((screen, guiGraphics, mouseX, mouseY, delta) -> {
                    GogglesScreen.onRenderGui(guiGraphics);
                });
            }
        });
    }

    private InteractionResult playerRightClickEntity(Player player, Level world, InteractionHand hand, Entity targetEntity ,EntityHitResult hitResult) {

        if (!(targetEntity instanceof Monster monster)) return InteractionResult.FAIL;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemController controller)) return InteractionResult.FAIL;
        if (UtilityMethods.assignControl(player.getUUID(), monster, stack, controller)) {
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
            return InteractionResult.CONSUME;
        }
        if (player.isShiftKeyDown()) {

            if (UtilityMethods.isPlayerControllingMob(player.getUUID(), monster) && UtilityMethods.isMobEligibleForRide(monster)) {
                player.startRiding(monster, true);
            }
        }
        return InteractionResult.PASS;
    }

    private void onPlayerJoinWorld(ServerPlayer player)
    {
        ServerLevel level = player.serverLevel();
        SaveControlledMob data = SaveControlledMob.get(level);
        data.loadControlledMobs(level);
    }

    private void onPlayerLeaveWorld(ServerPlayer player)
    {
        SaveControlledMob savedData = SaveControlledMob.get(player.serverLevel());
        savedData.setDirty();
    }
}
