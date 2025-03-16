package org.chubby.github.mobcontroller.common.menu;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataDisplayerMenu extends AbstractContainerMenu {
    private List<Monster> controlledMobs;
    public DataDisplayerMenu(int containerId, Inventory playerInventory, List<Integer> controlledIds) {
        super(MenusRegistry.DATA_DISPLAYER_MENU.get(), containerId);
        controlledMobs = new ArrayList<>();
        for(int i=0;i<controlledIds.size();i++){
            Level level = playerInventory.player.level();
            controlledMobs.add((Monster) level.getEntity(controlledIds.get(i)));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public List<Monster> getControlledIds() {
        return controlledMobs;
    }
}
