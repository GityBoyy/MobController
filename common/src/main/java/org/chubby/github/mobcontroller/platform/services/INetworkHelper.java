package org.chubby.github.mobcontroller.platform.services;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.chubby.github.mobcontroller.networking.NetworkHandler;
import org.chubby.github.mobcontroller.networking.messages.MessageContext;

public interface INetworkHelper {
    <T extends CustomPacketPayload> void registerMessage(
            Class<T> messageClass,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            java.util.function.BiConsumer<T, MessageContext> handler
    );

    NetworkHandler.PlayMessages getPlayChannel();
}