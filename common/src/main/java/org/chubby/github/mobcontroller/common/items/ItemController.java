package org.chubby.github.mobcontroller.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.data.MobControllerDataManager;
import org.chubby.github.mobcontroller.networking.PacketHandler;
import org.chubby.github.mobcontroller.networking.packets.ControllerSyncPacket;
import org.chubby.github.mobcontroller.util.SafeConcurrentMap;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemController extends Item implements Equipable {
    private final ControllerType type;
    private static ItemController INSTANCE;

    public ItemController(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
        INSTANCE = this;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }

        Monster controlledMob = MobControllerDataManager.getControlledMob(player.getUUID()).orElse(null);
        if(controlledMob == null) return;
        if (!controlledMob.isAlive()) {
            removeControlledMob(player.getUUID());
            return;
        }

        updateControlledMobBehavior(player, controlledMob);
        ItemStack controllerItem = controlledMob.getItemBySlot(EquipmentSlot.HEAD);
        if (!controllerItem.isEmpty() && controllerItem.getItem() instanceof ItemController) {
            MobControllerDataManager.saveControllerData(controllerItem, controlledMob);
            controlledMob.setItemSlot(EquipmentSlot.HEAD, controllerItem);
        }
        MobControllerDataManager.saveControllerData(stack, controlledMob);

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void updateControlledMobBehavior(Player player, Monster controlledMob) {
        if (controlledMob.getTarget() == player) {
            controlledMob.setTarget(null);
            controlledMob.setAggressive(false);
        }

        LivingEntity lastHurtMob = player.getLastHurtMob();
        if (lastHurtMob != null) {
            startControlledAttack(player.getUUID(), controlledMob, lastHurtMob);
        }

        LivingEntity lastHurtByMob = player.getLastHurtByMob();
        if (lastHurtByMob != null && lastHurtByMob != controlledMob) {
            startControlledAttack(player.getUUID(), controlledMob, lastHurtByMob);
        }
    }

    public static void assignControlledMob(UUID playerUUID, Monster mob, ControllerType type) {
        if (!(mob.level() instanceof ServerLevel)) {
            return;
        }

        Player player = mob.level().getPlayerByUUID(playerUUID);
        if (player == null) {
            return;
        }

        ItemStack controllerItem = player.getMainHandItem();
        if (!(controllerItem.getItem() instanceof ItemController)) {
            return;
        }

        ItemStack mobController = controllerItem.copy();
        MobControllerDataManager.saveControllerOwnership(mob, playerUUID, type);

        mob.setItemSlot(EquipmentSlot.HEAD, mobController);

        controllerItem.shrink(1);

        MobControllerDataManager.registerControlledMob(playerUUID, mob);

        MobControllerDataManager.saveControllerData(mobController, mob);
    }

    public static void removeControlledMob(UUID playerUUID) {
        MobControllerDataManager.getControlledMob(playerUUID).ifPresent(mob -> {
            ItemStack controller = mob.getItemBySlot(EquipmentSlot.HEAD);
            if (!controller.isEmpty() && controller.getItem() instanceof ItemController) {
                MobControllerDataManager.saveControllerData(controller, mob);
            }
            mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        });
        PacketHandler.sendToServer(new ControllerSyncPacket(playerUUID, -1, false));
        MobControllerDataManager.unregisterControlledMob(playerUUID);
    }

    private void startControlledAttack(UUID owner, Monster controlledMob, LivingEntity target) {
        if (!MobControllerDataManager.isControlled(controlledMob)) {
            return;
        }

        Player controller = controlledMob.level().getPlayerByUUID(owner);
        if (target != controller) {
            controlledMob.setTarget(target);
            controlledMob.setAggressive(true);
        }
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public ControllerType getControllerType() {
        return type;
    }

    public static SafeConcurrentMap<UUID, Monster> getPlayerMobControlMap() {
        return MobControllerDataManager.getPlayerMobMap();
    }
}