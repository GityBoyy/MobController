package org.chubby.github.mobcontroller.neoforge.platform.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import org.chubby.github.mobcontroller.platform.services.IMenuHelper;

import java.util.function.Consumer;

public class NeoforgeMenuHelper implements IMenuHelper {
    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        player.openMenu(menuProvider);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        player.openMenu(menuProvider, extraDataWriter);
    }
}