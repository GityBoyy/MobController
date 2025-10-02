package org.chubby.github.mobcontroller.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.chubby.github.mobcontroller.networking.messages.MessageSyncFluid;
import org.chubby.github.mobcontroller.platform.services.Services;

public class NetworkHandler
{

    public static void init()
    {
        Services.NETWORK_HELPER().registerMessage(MessageSyncFluid.class, MessageSyncFluid.STREAM_CODEC, MessageSyncFluid::handle);
    }

    public static PlayMessages getPlay() {
        return Services.NETWORK_HELPER().getPlayChannel();
    }

    public interface PlayMessages {
        <T extends CustomPacketPayload> void sendToPlayer(java.util.function.Supplier<net.minecraft.server.level.ServerPlayer> player, T message);
        <T extends CustomPacketPayload> void sendToServer(T message);
        <T extends CustomPacketPayload> void sendToAllPlayers(T message);
    }
}
