package org.chubby.github.mobcontroller.networking.messages;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.chubby.github.mobcontroller.util.Utils;

/**
 * Author: MrCrayfish
 */
public record MessageSyncFluid(BlockPos pos, Fluid fluid, long amount) implements CustomPacketPayload
{
    public static final Type<MessageSyncFluid> TYPE = new Type<>(Utils.resource("sync_fluid"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncFluid> STREAM_CODEC = StreamCodec.of((buf, message) -> {
        buf.writeBlockPos(message.pos);
        buf.writeResourceLocation(BuiltInRegistries.FLUID.getKey(message.fluid));
        buf.writeLong(message.amount);
    }, buf -> {
        BlockPos pos = buf.readBlockPos();
        Fluid fluid = BuiltInRegistries.FLUID.get(buf.readResourceLocation());
        long amount = buf.readLong();
        return new MessageSyncFluid(pos, fluid, amount);
    });

    public static void handle(MessageSyncFluid message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageSyncFluid(message));
        context.setHandled(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}