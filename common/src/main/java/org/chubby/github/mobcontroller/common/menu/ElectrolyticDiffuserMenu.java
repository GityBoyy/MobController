package org.chubby.github.mobcontroller.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.blocks.entity.ElectrolyticDiffuserBE;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElectrolyticDiffuserMenu extends SimpleContainerMenu{

    private final Level level;

    public ElectrolyticDiffuserMenu(int windowId, Inventory container) {
        super(MenusRegistry.ELECTROLYTIC_DIFFUSER_MENU.get(), windowId, container);
        this.level = container.player.level();

        this.addContainerSlots(16,16,1,1,0);
        this.addContainerSlots(62,32,1,1,1);
        this.addContainerSlots(116,32,1,1,2);
        this.addPlayerInventorySlots(8,84,container);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if(slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if(slotIndex < this.container.getContainerSize())
            {
                if(!this.moveItemStackTo(slotStack, this.container.getContainerSize(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(slotIndex < this.container.getContainerSize() + 27)
            {
                if(!this.moveItemStackTo(slotStack, this.container.getContainerSize() + 27, this.slots.size(), false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, this.container.getContainerSize(), this.slots.size() - 9, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }
        return stack;
    }
}
