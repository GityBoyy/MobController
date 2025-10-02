package org.chubby.github.mobcontroller.neoforge.platform.service;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.networking.NetworkHandler;
import org.chubby.github.mobcontroller.networking.messages.MessageContext;
import org.chubby.github.mobcontroller.platform.services.INetworkHelper;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NeoforgeNetworkHelper implements INetworkHelper {
    private static PayloadRegistrar registrar;

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        registrar = event.registrar("1");
    }

    @Override
    public <T extends CustomPacketPayload> void registerMessage(Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler) {
        if (registrar == null) {
            Constants.LOGGER.error("PayloadRegistrar is null! Make sure register() is called first.");
            return;
        }

        CustomPacketPayload.Type<T> type = getPayloadType(messageClass);

        registrar.playToClient(type, codec, (payload, context) -> {
            MessageContext msgContext = new NeoForgeMessageContext(context, true);
            handler.accept(payload, msgContext);
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getPayloadType(Class<T> clazz) {
        try {
            return (CustomPacketPayload.Type<T>) clazz.getDeclaredField("TYPE").get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get TYPE field from " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public NetworkHandler.PlayMessages getPlayChannel() {
        return new NeoForgePlayMessages();
    }

    private static class NeoForgePlayMessages implements NetworkHandler.PlayMessages {
        @Override
        public <T extends CustomPacketPayload> void sendToPlayer(Supplier<ServerPlayer> player, T message) {
            PacketDistributor.sendToPlayer(player.get(), message);
        }

        @Override
        public <T extends CustomPacketPayload> void sendToServer(T message) {
            PacketDistributor.sendToServer(message);
        }

        @Override
        public <T extends CustomPacketPayload> void sendToAllPlayers(T message) {
            PacketDistributor.sendToAllPlayers(message);
        }
    }

    private static class NeoForgeMessageContext implements MessageContext {
        private final net.neoforged.neoforge.network.handling.IPayloadContext context;
        private final boolean clientSide;

        public NeoForgeMessageContext(net.neoforged.neoforge.network.handling.IPayloadContext context, boolean clientSide) {
            this.context = context;
            this.clientSide = clientSide;
        }

        @Override
        public void execute(Runnable task) {
            context.enqueueWork(task);
        }

        @Override
        public void setHandled(boolean handled) {
        }

        @Override
        public boolean isClientSide() {
            return clientSide;
        }

        @Override
        public boolean isServerSide() {
            return !clientSide;
        }
    }
}