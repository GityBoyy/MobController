package org.chubby.github.mobcontroller.platform.services;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import java.util.function.Consumer;

public interface IMenuHelper {
    /**
     * Opens a menu for the player
     *
     * @param player The player to open the menu for
     * @param menuProvider The menu provider
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider);

    /**
     * Opens a menu for the player with extra data
     *
     * @param player The player to open the menu for
     * @param menuProvider The menu provider
     * @param extraDataWriter Consumer to write extra data to the network packet
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter);
}