package org.chubby.github.mobcontroller.fabric.client;

import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.chubby.github.mobcontroller.common.items.ItemController;

public final class MobcontrollerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UseEntityCallback.EVENT.register(this::playerRightClickEntity);
    }

    private InteractionResult playerRightClickEntity(Player player, Level world, InteractionHand hand, Entity entity ,EntityHitResult hitResult) {
        if (!world.isClientSide && hitResult.getEntity() instanceof Monster monster) {
            ItemStack itemStack = player.getItemInHand(hand);


            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof ItemController && player.isShiftKeyDown()){
                monster.setItemSlot(EquipmentSlot.HEAD,stack.copyWithCount(1));
                stack.consume(1,player);
                ItemController.assignControlledMob(player,monster);
                return InteractionResult.SUCCESS;
            }

            if (ItemController.getplayerMobControlMap().containsKey(player) && ItemController.getplayerMobControlMap().get(player).equals(monster)) {
                if (!monster.isPassenger()) {
                    player.startRiding(monster,true);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
