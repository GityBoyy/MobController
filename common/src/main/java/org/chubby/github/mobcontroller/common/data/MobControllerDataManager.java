package org.chubby.github.mobcontroller.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.networking.PacketHandler;
import org.chubby.github.mobcontroller.networking.packets.ControllerSyncPacket;
import org.chubby.github.mobcontroller.util.SafeConcurrentMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MobControllerDataManager {
    private static final SafeConcurrentMap<UUID, Monster> playerMobMap = new SafeConcurrentMap<>();
    private static final Map<Integer, SimpleContainer> monsterInventories = new HashMap<>();


    public static SimpleContainer getMonsterInventory(Monster monster) {
        return monsterInventories.computeIfAbsent(monster.getId(), id -> {
            SimpleContainer container = new SimpleContainer(14);
            syncArmorToInventory(monster, container);
            return container;
        });
    }

        public static void saveControllerData(ItemStack stack, Monster monster) {
            CompoundTag controllerData = new CompoundTag();
            controllerData.putInt("mobId", monster.getId());
            controllerData.putString("dimension", monster.level().dimension().location().toString());

            SimpleContainer inventory = getMonsterInventory(monster);

            syncArmorToInventory(monster, inventory);

            ListTag inventoryTag = new ListTag();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    CompoundTag slotTag = new CompoundTag();
                    slotTag.putInt("Slot", i);
                    itemStack.save(monster.level().registryAccess(),slotTag);
                    inventoryTag.add(slotTag);
                }
            }

            controllerData.put("Inventory", inventoryTag);
            stack.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
        }

        public static void loadControllerData(Monster monster, CompoundTag data) {
            if (data.contains("Inventory")) {
                SimpleContainer inventory = getMonsterInventory(monster);

                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }

                ListTag inventoryTag = data.getList("Inventory", 10);
                for (int i = 0; i < inventoryTag.size(); i++) {
                    CompoundTag slotTag = inventoryTag.getCompound(i);
                    int slot = slotTag.getInt("Slot");
                    if (slot >= 0 && slot < inventory.getContainerSize()) {
                        inventory.setItem(slot, ItemStack.parseOptional(monster.level().registryAccess(),slotTag));
                    }
                }

                updateMonsterArmor(monster, inventory);
            }
        }

        private static void updateMonsterArmor(Monster monster, SimpleContainer inventory) {
            monster.setItemSlot(EquipmentSlot.HEAD, inventory.getItem(0).copy());
            monster.setItemSlot(EquipmentSlot.CHEST, inventory.getItem(1).copy());
            monster.setItemSlot(EquipmentSlot.LEGS, inventory.getItem(2).copy());
            monster.setItemSlot(EquipmentSlot.FEET, inventory.getItem(3).copy());
        }

        public static void syncArmorToInventory(Monster monster, SimpleContainer container) {
            ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
            if (!headItem.isEmpty() && headItem.getItem() instanceof ItemController) {
                container.setItem(4, headItem.copy());
            }

            container.setItem(0, monster.getItemBySlot(EquipmentSlot.HEAD).copy());
            container.setItem(1, monster.getItemBySlot(EquipmentSlot.CHEST).copy());
            container.setItem(2, monster.getItemBySlot(EquipmentSlot.LEGS).copy());
            container.setItem(3, monster.getItemBySlot(EquipmentSlot.FEET).copy());
        }


    public static void saveControllerOwnership(Monster mob, UUID playerUUID, ControllerType type) {
        CompoundTag controllerData = new CompoundTag();
        controllerData.putUUID("controllerUUID", playerUUID);
        controllerData.putString("controllerType", type.getName());
        ItemStack headItem = mob.getItemBySlot(EquipmentSlot.HEAD);
        headItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
    }

    public static void registerControlledMob(UUID playerUUID, Monster mob) {
        playerMobMap.put(playerUUID, mob);
        PacketHandler.sendToServer(new ControllerSyncPacket(playerUUID, mob.getId(), true));
    }

    public static void unregisterControlledMob(UUID playerUUID) {
        getControlledMob(playerUUID).ifPresent(mob -> {
            mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            mob.getItemBySlot(EquipmentSlot.HEAD).set(DataComponentRegistry.CONTROLLER.get(), null);
        });
        PacketHandler.sendToServer(new ControllerSyncPacket(playerUUID, -1, false));
        playerMobMap.remove(playerUUID);
    }

    public static Optional<Monster> getControlledMob(UUID playerUUID) {
        return playerMobMap.get(playerUUID);
    }

    public static void syncFromWorld(ServerLevel level) {
        playerMobMap.clear();
        monsterInventories.clear();

        level.getAllEntities().forEach(entity -> {
            if (entity instanceof Monster monster) {
                ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
                if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
                    CompoundTag data = headItem.get(DataComponentRegistry.CONTROLLER.get());
                    if (data != null && data.hasUUID("controllerUUID")) {
                        UUID controllerUUID = data.getUUID("controllerUUID");
                        playerMobMap.put(controllerUUID, monster);

                        SimpleContainer inventory = getMonsterInventory(monster);
                        loadControllerData(monster, data);
                    }
                }
            }
        });
    }

    public static void handlePlayerLogout(UUID playerUUID) {
        getControlledMob(playerUUID).ifPresent(monster -> {
            ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
            if (!headItem.isEmpty()) {
                CompoundTag controllerData = new CompoundTag();
                controllerData.putUUID("controllerUUID", playerUUID);

                SimpleContainer inventory = getMonsterInventory(monster);
                ListTag inventoryTag = inventory.createTag(monster.level().registryAccess());
                controllerData.put("Inventory", inventoryTag);

                headItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData);
            }
        });
    }

    public static boolean isControlled(Monster mob) {
        if (mob == null) {
            return false;
        }

        ItemStack headItem = mob.getItemBySlot(EquipmentSlot.HEAD);
        if (!(headItem.getItem() instanceof ItemController)) {
            return false;
        }

        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) {
            return false;
        }

        CompoundTag controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (controllerData == null || !controllerData.hasUUID("controllerUUID")) {
            return false;
        }

        UUID controllerUUID = controllerData.getUUID("controllerUUID");
        return playerMobMap.containsValue(mob) ||
                playerMobMap.get(controllerUUID).map(mappedMob -> mappedMob.getId() == mob.getId()).orElse(false);
    }
    public static SafeConcurrentMap<UUID, Monster> getPlayerMobMap() {
        return playerMobMap;
    }
}