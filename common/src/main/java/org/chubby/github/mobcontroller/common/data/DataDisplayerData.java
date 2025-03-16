package org.chubby.github.mobcontroller.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.util.MCCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DataDisplayerData implements DataComponentType<DataDisplayerData>
{
    public static final Codec<DataDisplayerData> CODEC = RecordCodecBuilder.create(inst->inst.group(
            MCCodec.UUID_CODEC.fieldOf("controllerUUID").forGetter(data->data.data.getControllingPlayer()),
            Codec.INT.fieldOf("controllerID").forGetter(data->data.data.getControlledMob()),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("StoredItems").forGetter(data->data.inventory.getItems())
    ).apply(inst,DataDisplayerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,DataDisplayerData> STREAM_CODEC =
            StreamCodec.of(DataDisplayerData::writeData,DataDisplayerData::readData);

    public final MobControllerData data;
    public final SimpleContainer inventory;

    public DataDisplayerData(MobControllerData data) {
        this.data = data;
        this.inventory = new SimpleContainer(14);
    }

    public DataDisplayerData(UUID playerUUID, int monsterId,List<ItemStack> stacks)
    {
        this.data = new MobControllerData(playerUUID,monsterId);
        this.inventory = new SimpleContainer(14);
        for(int i=0;i<stacks.size();i++){
            this.inventory.setItem(i,stacks.get(i));
        }
    }

    public MobControllerData getData() {
        return data;
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    private static void writeData(RegistryFriendlyByteBuf buf, DataDisplayerData data) {
        buf.writeUUID(data.data.controllingPlayer);
        buf.writeInt(data.data.controlledMob);

        ListTag itemsTag = new ListTag();
        for (int i = 0; i < data.inventory.getContainerSize(); i++) {
            ItemStack stack = data.inventory.getItem(i);
            if (!stack.isEmpty()) {
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

    private static DataDisplayerData readData(RegistryFriendlyByteBuf buf) {
        UUID controllingPlayer = buf.readUUID();
        int controlledMob = buf.readInt();

        DataDisplayerData data = new DataDisplayerData(new MobControllerData(controllingPlayer, controlledMob));

        int itemCount = buf.readInt();
        if (itemCount > 0) {
            CompoundTag tag = buf.readNbt();
            if (tag != null && tag.contains("Items", 9)) {
                ListTag itemsTag = tag.getList("Items", 10);

                for (int i = 0; i < itemsTag.size(); i++) {
                    CompoundTag itemTag = itemsTag.getCompound(i);
                    int slot = itemTag.getInt("Slot");

                    if (slot >= 0 && slot < data.inventory.getContainerSize()) {
                        try {
                            ItemStack stack = ItemStack.parseOptional(buf.registryAccess(), itemTag);
                            if (!stack.isEmpty()) {
                                data.inventory.setItem(slot, stack);
                            }
                        } catch (Exception e) {
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

        DataDisplayerData that = (DataDisplayerData) object;
        return data == that.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, data);
    }

    @Override
    public @Nullable Codec<DataDisplayerData> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, DataDisplayerData> streamCodec() {
        return STREAM_CODEC;
    }
}
