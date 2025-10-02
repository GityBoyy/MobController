package org.chubby.github.mobcontroller.fabric.platform.service;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
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
import org.chubby.github.mobcontroller.platform.services.IRegistryHelper;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricRegistryHelper {
    public static class FabricServerRegistry implements IRegistryHelper.IServerRegistry {

        @Override
        public Supplier<Item> registerItem(ResourceLocation id, Supplier<Item> supplier) {
            return () -> Registry.register(BuiltInRegistries.ITEM, id, supplier.get());
        }

        @Override
        public Supplier<Block> registerBlock(ResourceLocation id, Supplier<Block> supplier) {
            return () -> Registry.register(BuiltInRegistries.BLOCK, id, supplier.get());
        }

        @Override
        public Supplier<BlockItem> registerBlockItem(ResourceLocation id, Supplier<BlockItem> supplier) {
            return () -> Registry.register(BuiltInRegistries.ITEM, id, supplier.get());
        }

        @Override
        public <T> Supplier<DataComponentType<T>> registerDataComponent(ResourceLocation id, Supplier<DataComponentType<T>> supplier) {
            return () -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, supplier.get());
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipe(ResourceLocation id, Supplier<RecipeType<T>> supplier) {
            return () -> Registry.register(BuiltInRegistries.RECIPE_TYPE, id, supplier.get());
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(ResourceLocation id, Supplier<RecipeSerializer<T>> supplier) {
            return () -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, supplier.get());
        }

        @Override
        public Supplier<CreativeModeTab> registerCreativeTab(ResourceLocation id, Supplier<CreativeModeTab> supplier) {
            return () -> Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, supplier.get());
        }

        @Override
        public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBE(ResourceLocation id, Supplier<BlockEntityType<T>> supplier) {
            return () -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, supplier.get());
        }

        @Override
        public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier) {
            return BlockEntityType.Builder.of(function::apply, validBlocksSupplier.get()).build(null);
        }
    }

    public static class FabricClientRegistry implements IRegistryHelper.IClientRegistry {

        @Override
        public <M extends AbstractContainerMenu> Supplier<MenuType<M>> registerMenu(ResourceLocation id, IRegistryHelper.ScreenConstructor<M> constructor) {
            ExtendedScreenHandlerType<M, RegistryFriendlyByteBuf> type = new ExtendedScreenHandlerType<>(constructor::create,null);
            return () -> Registry.register(BuiltInRegistries.MENU, id, type);
        }

        @Override
        public Supplier<Potion> registerPotion(ResourceLocation id, Supplier<Potion> supplier) {
            return () -> Registry.register(BuiltInRegistries.POTION, id, supplier.get());
        }
    }
}
