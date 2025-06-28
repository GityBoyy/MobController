package org.chubby.github.mobcontroller.common.menu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MonsterInventoryMenu extends AbstractContainerMenu {

    private static final int INVENTORY_START = 5;
    private static final int INVENTORY_END = 13;

    public final SimpleContainer container;
    public final Monster monster;
    private final ItemStack headItem;

    public MonsterInventoryMenu(int containerId, Inventory playerInventory, Monster monster) {
        super(MenusRegistry.MONSTER_MENU.get(), containerId);
        this.monster = monster;

        this.headItem = monster.getItemBySlot(EquipmentSlot.HEAD);

        if (headItem.has(DataComponentRegistry.CONTROLLER.get()) &&
                headItem.get(DataComponentRegistry.CONTROLLER.get()) != null) {
            this.container = Objects.requireNonNull(headItem.get(DataComponentRegistry.CONTROLLER.get())).mobInventory;
        } else {
            this.container = new SimpleContainer(14);
        }

        setupSlots(playerInventory);
    }

    public MonsterInventoryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(MenusRegistry.MONSTER_MENU.get(), containerId);

        int monsterId = buf.readInt();

        this.monster = (Monster) playerInventory.player.level().getEntity(monsterId);

        if (this.monster != null) {
            this.headItem = monster.getItemBySlot(EquipmentSlot.HEAD);

            if (headItem.has(DataComponentRegistry.CONTROLLER.get()) &&
                    headItem.get(DataComponentRegistry.CONTROLLER.get()) != null) {
                this.container = Objects.requireNonNull(headItem.get(DataComponentRegistry.CONTROLLER.get())).mobInventory;
            } else {
                this.container = new SimpleContainer(14);
            }
        } else {
            this.headItem = ItemStack.EMPTY;
            this.container = new SimpleContainer(14);
        }

        setupSlots(playerInventory);
    }

    private void setupSlots(Inventory playerInventory) {
        this.addSlot(new ArmorSlot(container, 0, 8, 8, EquipmentSlot.HEAD));
        this.addSlot(new ArmorSlot(container, 1, 8, 26, EquipmentSlot.CHEST));
        this.addSlot(new ArmorSlot(container, 2, 8, 44, EquipmentSlot.LEGS));
        this.addSlot(new ArmorSlot(container, 3, 8, 62, EquipmentSlot.FEET));

        this.addSlot(new ControllerSlot(container, 4, 81, 8));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container,
                        INVENTORY_START + (row * 3) + col,
                        112 + (col * 18),
                        16 + (row * 18)));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < INVENTORY_END) {
                if (!this.moveItemStackTo(itemstack1, INVENTORY_END, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (itemstack1.getItem() instanceof ArmorItem armorItem) {
                    int armorSlot = getArmorSlotIndex(armorItem.getEquipmentSlot());
                    if (!this.moveItemStackTo(itemstack1, armorSlot, armorSlot + 1, false)) {
                        if (!this.moveItemStackTo(itemstack1, INVENTORY_START, INVENTORY_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
                else if (itemstack1.getItem() instanceof ItemController) {
                    if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                        if (!this.moveItemStackTo(itemstack1, INVENTORY_START, INVENTORY_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
                else if (!this.moveItemStackTo(itemstack1, INVENTORY_START, INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player) &&
                this.monster.isAlive() &&
                this.monster.distanceTo(player) < 8.0F;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (monster == null || headItem.isEmpty()) {
            return;
        }
        if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
            MobControllerData controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
            if(controllerData == null) return;
            MobControllerData updatedData = new MobControllerData(controllerData.getControllingPlayer(),
                    controllerData.getControlledMob());

            for (int i = 0; i < this.container.getContainerSize(); i++) {
                updatedData.mobInventory.setItem(i, this.container.getItem(i).copy());
            }

            headItem.set(DataComponentRegistry.CONTROLLER.get(), updatedData);

            monster.setItemSlot(EquipmentSlot.HEAD, headItem);
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (monster == null || headItem.isEmpty()) {
            return;
        }
        if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
            MobControllerData controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
            if(controllerData == null ) return;
            CompoundTag inventoryTag = controllerData.createInventorySnapshot(monster.level().registryAccess());

            MobControllerData updatedData = new MobControllerData(controllerData.getControllingPlayer(),
                    controllerData.getControlledMob());
            updatedData.loadInventorySnapshot(monster.level().registryAccess(), inventoryTag);

            headItem.set(DataComponentRegistry.CONTROLLER.get(), updatedData);

            monster.setItemSlot(EquipmentSlot.HEAD, headItem);
        }
    }

    private int getArmorSlotIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            default -> INVENTORY_START;
        };
    }

    private static class ArmorSlot extends Slot {
        private final EquipmentSlot equipmentSlot;

        public ArmorSlot(Container container, int index, int x, int y, EquipmentSlot equipmentSlot) {
            super(container, index, x, y);
            this.equipmentSlot = equipmentSlot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ArmorItem armorItem &&
                    armorItem.getEquipmentSlot() == this.equipmentSlot;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class ControllerSlot extends Slot {
        public ControllerSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ItemController;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}