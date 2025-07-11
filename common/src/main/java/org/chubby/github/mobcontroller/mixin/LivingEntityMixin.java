package org.chubby.github.mobcontroller.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.enums.EnumControlledStates;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.EntityStateHandler;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private static final Map<Integer, EntityStateHandler> MONSTER_STATE_HANDLERS = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (!((Object) this instanceof Monster monster)) return;

        ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        UUID playerUUID = attachment.getControllingPlayer();
        var player = monster.level().getPlayerByUUID(playerUUID);
        if (player == null) return;

        if (monster.getTarget() == player) {
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
        } else if (stateHandler.isAggressiveState() && stateHandler.hasBeenInStateFor(3000)) {
            stateHandler.transitionTo(EnumControlledStates.FOLLOW);
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

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    public void onDropAllDeathLoot(ServerLevel level, DamageSource source, CallbackInfo ci) {
        if (!((Object) this instanceof Monster monster)) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        MONSTER_STATE_HANDLERS.remove(monster.getId());

        for (ItemStack item : attachment.mobInventory.getItems()) {
            Containers.dropItemStack(monster.level(), monster.getX(), monster.getY(), monster.getZ(), item);
        }
    }
}
