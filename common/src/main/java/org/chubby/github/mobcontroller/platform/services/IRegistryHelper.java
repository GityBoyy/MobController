package org.chubby.github.mobcontroller.platform.services;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.chubby.github.mobcontroller.api.menu.IMenuData;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IRegistryHelper {
    interface IServerRegistry {
        Supplier<Item> registerItem(ResourceLocation id, Supplier<Item> supplier);

        Supplier<Block> registerBlock(ResourceLocation id, Supplier<Block> supplier);

        Supplier<BlockItem> registerBlockItem(ResourceLocation id, Supplier<BlockItem> supplier);

        <T> Supplier<DataComponentType<T>> registerDataComponent(ResourceLocation id, Supplier<DataComponentType<T>> supplier);

        <T  extends Recipe<?>> Supplier<RecipeType<T>> registerRecipe(ResourceLocation id, Supplier<RecipeType<T>> supplier);

        <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(ResourceLocation id, Supplier<RecipeSerializer<T>> supplier);

        Supplier<CreativeModeTab> registerCreativeTab(ResourceLocation id, Supplier<CreativeModeTab> supplier);

        <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBE(ResourceLocation neuralInterfaceStationBe, Supplier<BlockEntityType<T>> supplier);

        <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier);
    }

    interface IClientRegistry {
        <M extends AbstractContainerMenu> Supplier<MenuType<M>> registerMenu(ResourceLocation id, ScreenConstructor<M> constructor);
        Supplier<Potion> registerPotion(ResourceLocation id, Supplier<Potion> potion);
    }

    @FunctionalInterface
    interface ScreenConstructor<M extends AbstractContainerMenu> {
        M create(int containerID, Inventory inventory,RegistryFriendlyByteBuf extraData);
    }

}
