package org.chubby.github.mobcontroller.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.chubby.github.mobcontroller.networking.packets.ControllerSyncPacket;

public class PacketHandler
{
    public static final CustomPacketPayload.Type<ControllerSyncPacket> CONTROLLER_SYNC_PACKET = ControllerSyncPacket.TYPE;
    public static void init()
    {
        registerC2SPacket(CONTROLLER_SYNC_PACKET,ControllerSyncPacket.STREAM_CODEC,ServerHandler::handleControllerSync);
    }

    public static <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> id,
                                                                         StreamCodec<RegistryFriendlyByteBuf,T> codec, NetworkManager.NetworkReceiver<T> receiver)
    {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,id,codec,receiver);
    }


    public static void sendToServer(CustomPacketPayload packet)
    {
        NetworkManager.sendToServer(packet);
    }
}
