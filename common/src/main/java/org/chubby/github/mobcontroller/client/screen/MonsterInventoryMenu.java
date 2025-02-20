package org.chubby.github.mobcontroller.client.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import org.chubby.github.mobcontroller.common.data.MobControllerDataManager;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.MenuRegistry;
import org.jetbrains.annotations.NotNull;

public class MonsterInventoryMenu extends AbstractContainerMenu {
    private final SimpleContainer monsterInventory;
    public final Monster monster;
    private static final int INVENTORY_SIZE = 14;

    private static final int HELMET_SLOT = 0;
    private static final int CHESTPLATE_SLOT = 1;
    private static final int LEGGINGS_SLOT = 2;
    private static final int BOOTS_SLOT = 3;
    private static final int SPECIAL_SLOT = 4;
    private static final int INVENTORY_START = 5;
    private static final int INVENTORY_END = 13;

    public MonsterInventoryMenu(int containerId, Inventory playerInventory, SimpleContainer monsterContainer, Monster monster) {
        super(MenuRegistry.MONSTER_MENU.get(), containerId);
        this.monsterInventory = monsterContainer;
        this.monster = monster;
        MobControllerDataManager.syncArmorToInventory(monster, monsterContainer);
        this.addSlot(new ArmorSlot(monsterInventory, HELMET_SLOT, 8, 8, EquipmentSlot.HEAD));
        this.addSlot(new ArmorSlot(monsterInventory, CHESTPLATE_SLOT, 8, 26, EquipmentSlot.CHEST));
        this.addSlot(new ArmorSlot(monsterInventory, LEGGINGS_SLOT, 8, 44, EquipmentSlot.LEGS));
        this.addSlot(new ArmorSlot(monsterInventory, BOOTS_SLOT, 8, 62, EquipmentSlot.FEET));

        this.addSlot(new ControllerSlot(monsterInventory, SPECIAL_SLOT, 81, 8));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(monsterInventory,
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
        return this.monsterInventory.stillValid(player) &&
                this.monster.isAlive() &&
                this.monster.distanceTo(player) < 8.0F;
    }

    private int getArmorSlotIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HELMET_SLOT;
            case CHEST -> CHESTPLATE_SLOT;
            case LEGS -> LEGGINGS_SLOT;
            case FEET -> BOOTS_SLOT;
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