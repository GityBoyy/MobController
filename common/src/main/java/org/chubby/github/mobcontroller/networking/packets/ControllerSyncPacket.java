package org.chubby.github.mobcontroller.networking.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.UUID;

public record ControllerSyncPacket(UUID playerUUID, int mobId, boolean isAssigning) implements CustomPacketPayload
{
    public static final Type<ControllerSyncPacket> TYPE = new Type<>(Utils.resource("controller_sync_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ControllerSyncPacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ControllerSyncPacket>() {
        @Override
        public ControllerSyncPacket decode(RegistryFriendlyByteBuf buf) {
            return new ControllerSyncPacket(buf.readUUID(),buf.readInt(),buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ControllerSyncPacket packet) {
            buf.writeUUID(packet.playerUUID());
            buf.writeInt(packet.mobId());
            buf.writeBoolean(packet.isAssigning());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}