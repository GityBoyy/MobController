package org.chubby.github.mobcontroller.common.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ControllerData
        (
                UUID controllerUUID, //UUID of player controlling entity.
                int mobId, // Entity Id of the controlled entity.
                MobInventory inventory, // Inventory of the controlled entity.
                int energy // Controller energy

        )
{

        public static class MobInventory
        {
                private static final int INVENTORY_SIZE = 9; // Current inventory size of the entity, maximum it can be increased to 5x5 from 3x3.
                private static final int HELMET_SLOT = 0;
                private static final int CHESTPLATE_SLOT = 1;
                private static final int LEGGINGS_SLOT = 2;
                private static final int BOOTS_SLOT = 3;
                private static final int CONTROLLER_SLOT = 4;

                private final List<ItemStack> inventory = new ArrayList<>(INVENTORY_SIZE);

                public void writeInventoryToBuf(MobInventory inv, RegistryFriendlyByteBuf buf)
                {
                        for (ItemStack itemStack : inv.inventory)
                        {
                                itemStack.save(buf.registryAccess());
                        }
                }

                public List<ItemStack> readInventoryFromBuf(RegistryFriendlyByteBuf buf)
                {
                        List<ItemStack> toRet = new ArrayList<>(INVENTORY_SIZE);
                        for(int i=0; i < INVENTORY_SIZE; i++)
                        {
                                toRet.add(ItemStack.parseOptional(buf.registryAccess(),));
                        }
                        return toRet;
                }
        }
}
