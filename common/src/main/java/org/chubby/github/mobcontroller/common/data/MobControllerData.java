package org.chubby.github.mobcontroller.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.MCCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MobControllerData implements DataComponentType<MobControllerData>
{
    public static final Codec<MobControllerData> CODEC = RecordCodecBuilder.create(inst-> inst.group(
            MCCodec.UUID_CODEC.fieldOf("controllingPlayer").forGetter(data -> data.controllingPlayer),
            Codec.INT.fieldOf("controlledMob").forGetter(data -> data.controlledMob),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("inventoryItems").forGetter(data -> data.getSerializableItems())
    ).apply(inst, MobControllerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,MobControllerData> STREAM_CODEC =
            StreamCodec.of(MobControllerData::writeControllerData,MobControllerData::readControllerData);

    public final UUID controllingPlayer;
    public final int controlledMob;
    public final SimpleContainer mobInventory;

    public MobControllerData(UUID controllingPlayer, int controlledMob) {
        this.controllingPlayer = controllingPlayer;
        this.controlledMob = controlledMob;
        this.mobInventory = new SimpleContainer(14);
    }

    public MobControllerData(UUID controllingPlayer, int controlledMob, List<ItemStack> inventoryItems) {
        this.controllingPlayer = controllingPlayer;
        this.controlledMob = controlledMob;
        this.mobInventory = new SimpleContainer(14);

        for (int i = 0; i < inventoryItems.size() && i < mobInventory.getContainerSize(); i++) {
            if (inventoryItems.get(i) != null && !inventoryItems.get(i).isEmpty()) {
                this.mobInventory.setItem(i, inventoryItems.get(i));
            }
        }
    }

    public MobControllerData(MobControllerData self){
        this.controllingPlayer = self.controllingPlayer;
        this.controlledMob = self.controlledMob;
        this.mobInventory = new SimpleContainer(14);

        for (int i = 0; i < self.mobInventory.getContainerSize(); i++) {
            ItemStack original = self.mobInventory.getItem(i);
            if (!original.isEmpty()) {
                // Prevent circular reference by not copying controller items that reference this data
                if (original.has(DataComponentRegistry.CONTROLLER.get())) {
                    MobControllerData itemData = original.get(DataComponentRegistry.CONTROLLER.get());
                    if (itemData != null && itemData.controlledMob == self.controlledMob &&
                            Objects.equals(itemData.controllingPlayer, self.controllingPlayer)) {
                        // Skip copying this item to prevent circular reference
                        continue;
                    }
                }
                this.mobInventory.setItem(i, original.copy());
            }
        }
    }

    /**
     * Get items for serialization, excluding items that would cause circular references
     */
    private List<ItemStack> getSerializableItems() {
        return mobInventory.getItems().stream()
                .map(stack -> {
                    if (stack.has(DataComponentRegistry.CONTROLLER.get())) {
                        MobControllerData itemData = stack.get(DataComponentRegistry.CONTROLLER.get());
                        if (itemData != null && itemData.controlledMob == this.controlledMob &&
                                Objects.equals(itemData.controllingPlayer, this.controllingPlayer)) {
                            // Return empty stack to prevent circular reference
                            return ItemStack.EMPTY;
                        }
                    }
                    return stack;
                })
                .toList();
    }

    public int getControlledMob() {
        return controlledMob;
    }

    public UUID getControllingPlayer() {
        return controllingPlayer;
    }

    public MobControllerData copy() {return new MobControllerData(this);}

    public MobControllerData create(UUID controllingPlayer, int controlledMob)
    {
        return new MobControllerData(controllingPlayer,controlledMob);
    }

    public CompoundTag createInventorySnapshot(HolderLookup.Provider levelRegistry) {
        CompoundTag tag = new CompoundTag();
        ListTag inventoryTag = new ListTag();
        for (int i = 0; i < this.mobInventory.getContainerSize(); i++) {
            ItemStack stack = this.mobInventory.getItem(i);
            if (!stack.isEmpty()) {
                // Check for circular reference
                if (stack.has(DataComponentRegistry.CONTROLLER.get())) {
                    MobControllerData itemData = stack.get(DataComponentRegistry.CONTROLLER.get());
                    if (itemData != null && itemData.controlledMob == this.controlledMob &&
                            Objects.equals(itemData.controllingPlayer, this.controllingPlayer)) {
                        // Skip saving this item to prevent circular reference
                        continue;
                    }
                }

                CompoundTag slotTag = new CompoundTag();
                slotTag.putInt("Slot", i);
                stack.save(levelRegistry, slotTag);
                inventoryTag.add(slotTag);
            }
        }
        tag.put("Inventory", inventoryTag);
        return tag;
    }

    public void loadInventorySnapshot(HolderLookup.Provider levelRegistry, CompoundTag tag) {
        if (tag.contains("Inventory", 9)) {
            ListTag listTag = tag.getList("Inventory", 10);

            for (int i = 0; i < this.mobInventory.getContainerSize(); i++) {
                this.mobInventory.setItem(i, ItemStack.EMPTY);
            }

            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag slotTag = listTag.getCompound(i);
                int slot = slotTag.getInt("Slot");

                if (slot >= 0 && slot < this.mobInventory.getContainerSize()) {
                    try {
                        ItemStack stack = ItemStack.parseOptional(levelRegistry, slotTag);
                        if (!stack.isEmpty()) {
                            this.mobInventory.setItem(slot, stack);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }
    }

    private static void writeControllerData(RegistryFriendlyByteBuf buf, MobControllerData data) {
        buf.writeUUID(data.controllingPlayer);
        buf.writeInt(data.controlledMob);

        ListTag itemsTag = new ListTag();
        for (int i = 0; i < data.mobInventory.getContainerSize(); i++) {
            ItemStack stack = data.mobInventory.getItem(i);
            if (!stack.isEmpty()) {
                // Check for circular reference
                if (stack.has(DataComponentRegistry.CONTROLLER.get())) {
                    MobControllerData itemData = stack.get(DataComponentRegistry.CONTROLLER.get());
                    if (itemData != null && itemData.controlledMob == data.controlledMob &&
                            Objects.equals(itemData.controllingPlayer, data.controllingPlayer)) {
                        // Skip writing this item to prevent circular reference
                        continue;
                    }
                }

                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stack.save(buf.registryAccess(), itemTag);
                itemsTag.add(itemTag);
            }
        }

        buf.writeInt(itemsTag.size());
        if (!itemsTag.isEmpty()) {
            CompoundTag allItemsTag = new CompoundTag();
            allItemsTag.put("Items", itemsTag);
            buf.writeNbt(allItemsTag);
        }
    }

    private static MobControllerData readControllerData(RegistryFriendlyByteBuf buf) {
        UUID controllingPlayer = buf.readUUID();
        int controlledMob = buf.readInt();

        MobControllerData data = new MobControllerData(controllingPlayer, controlledMob);

        int itemCount = buf.readInt();
        if (itemCount > 0) {
            CompoundTag tag = buf.readNbt();
            if (tag != null && tag.contains("Items", 9)) {
                ListTag itemsTag = tag.getList("Items", 10);

                for (int i = 0; i < itemsTag.size(); i++) {
                    CompoundTag itemTag = itemsTag.getCompound(i);
                    int slot = itemTag.getInt("Slot");

                    if (slot >= 0 && slot < data.mobInventory.getContainerSize()) {
                        try {
                            ItemStack stack = ItemStack.parseOptional(buf.registryAccess(), itemTag);
                            if (!stack.isEmpty()) {
                                data.mobInventory.setItem(slot, stack);
                            }
                        } catch (Exception e) {
                            // Ignore parsing errors
                        }
                    }
                }
            }
        }

        return data;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        MobControllerData that = (MobControllerData) object;
        return controlledMob == that.controlledMob &&
                Objects.equals(controllingPlayer, that.controllingPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controllingPlayer, controlledMob);
    }

    @Override
    public @Nullable Codec<MobControllerData> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MobControllerData> streamCodec() {
        return STREAM_CODEC;
    }
}