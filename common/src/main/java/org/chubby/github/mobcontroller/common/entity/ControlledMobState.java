package org.chubby.github.mobcontroller.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.chubby.github.mobcontroller.util.Utils;

public record ControlledMobState
        (
                String id,
                int transitionTicks,
                ResourceLocation icon
        )
{
    public static final Codec<ControlledMobState> CODEC = RecordCodecBuilder.create(inst->inst.group(
            Codec.STRING.fieldOf("id").forGetter(ControlledMobState::id),
            Codec.INT.fieldOf("id").forGetter(ControlledMobState::transitionTicks),
            Codec.STRING.fieldOf("id").forGetter(o -> o.icon.getPath())
    ).apply(inst,(id, transitionTicks, icon) -> new ControlledMobState(id,transitionTicks, Utils.resource(icon))));

    public static final StreamCodec<RegistryFriendlyByteBuf,ControlledMobState> STREAM_CODEC = StreamCodec.of(ControlledMobState::writeToBuf,ControlledMobState::readFromBuf);

    private static ControlledMobState readFromBuf(RegistryFriendlyByteBuf buf)
    {
        return new ControlledMobState(buf.readUtf(),buf.readInt(),buf.readResourceLocation());
    }

    private static void writeToBuf(RegistryFriendlyByteBuf buf, ControlledMobState state)
    {
        buf.writeUtf(state.id);
        buf.writeInt(state.transitionTicks);
        buf.writeResourceLocation(state.icon);
    }
}
