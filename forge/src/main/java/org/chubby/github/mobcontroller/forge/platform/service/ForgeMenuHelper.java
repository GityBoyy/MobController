package org.chubby.github.mobcontroller.forge.platform.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import org.chubby.github.mobcontroller.platform.services.IMenuHelper;

import java.util.function.Consumer;

public class ForgeMenuHelper implements IMenuHelper {
    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        player.openMenu(menuProvider);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(menuProvider);

    }
}