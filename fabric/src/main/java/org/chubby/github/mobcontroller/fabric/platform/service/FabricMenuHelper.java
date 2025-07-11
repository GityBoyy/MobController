package org.chubby.github.mobcontroller.fabric.platform.service;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.chubby.github.mobcontroller.platform.services.IMenuHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FabricMenuHelper implements IMenuHelper
{

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        player.openMenu(menuProvider);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        player.openMenu(new ExtendedScreenHandlerFactory<>() {
            @Override
            public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return menuProvider.createMenu(i,inventory,player);
            }

            @Override
            public Component getDisplayName() {
                return null;
            }

            @Override
            public Object getScreenOpeningData(ServerPlayer serverPlayer) {
                return extraDataWriter;
            }
        });
    }
}
