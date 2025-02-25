package org.chubby.github.mobcontroller.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ControllerTierData implements DataComponentType<ControllerTierData>
{
    public static final Codec<ControllerTierData> CODEC = RecordCodecBuilder.create(inst->inst.group(
       Codec.STRING.fieldOf("controllerName").forGetter(data->data.type.getName()),
       Codec.INT.fieldOf("controlTIme").forGetter(data->data.type.getControlTime())
    ).apply(inst,ControllerTierData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,ControllerTierData> STREAM_CODEC =
            StreamCodec.of(ControllerTierData::writeControllerData,ControllerTierData::readControllerData);



    private final ControllerType type;

    public ControllerTierData(ControllerType type) {
        this.type = type;
    }

    public ControllerTierData(String name, int controlTime)
    {
        this.type = new ControllerType(name,controlTime);
    }

    public ControllerType getType() {
        return type;
    }

    private static void writeControllerData(RegistryFriendlyByteBuf buf, ControllerTierData data)
    {
        buf.writeUtf(data.type.getName());
        buf.writeInt(data.type.getControlTime());
    }

    private static ControllerTierData readControllerData(RegistryFriendlyByteBuf buf)
    {
        return new ControllerTierData(buf.readUtf(),buf.readInt());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ControllerTierData that = (ControllerTierData) object;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public @Nullable Codec<ControllerTierData> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ControllerTierData> streamCodec() {
        return STREAM_CODEC;
    }
}
