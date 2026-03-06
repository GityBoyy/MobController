package org.chubby.github.mobcontroller.fabric.platform.service;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

    /**
     * Identity codec that passes through the RegistryFriendlyByteBuf without modification.
     * This is useful when the menu constructor reads directly from the buffer.
     */
    private static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> IDENTITY_CODEC =
            new StreamCodec<>() {
                @Override
                public RegistryFriendlyByteBuf decode(RegistryFriendlyByteBuf buffer) {
                    return buffer;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, RegistryFriendlyByteBuf data) {
                    // No-op: data is already in the buffer
                }
            };

    public static class FabricServerRegistry implements IRegistryHelper.IServerRegistry {

        @Override
        public Supplier<Item> registerItem(ResourceLocation id, Supplier<Item> supplier) {
            // CRITICAL: Get the instance ONCE during registration, not every time .get() is called
            Item item = supplier.get();
            Item registered = Registry.register(BuiltInRegistries.ITEM, id, item);
            // Return a supplier that always returns the same registered instance
            return () -> registered;
        }

        @Override
        public Supplier<Block> registerBlock(ResourceLocation id, Supplier<Block> supplier) {
            // CRITICAL: Get the instance ONCE during registration, not every time .get() is called
            Block block = supplier.get();
            Block registered = Registry.register(BuiltInRegistries.BLOCK, id, block);
            // Return a supplier that always returns the same registered instance
            return () -> registered;
        }

        @Override
        public Supplier<BlockItem> registerBlockItem(ResourceLocation id, Supplier<BlockItem> supplier) {
            // CRITICAL: Get the instance ONCE during registration, not every time .get() is called
            BlockItem item = supplier.get();
            BlockItem registered = Registry.register(BuiltInRegistries.ITEM, id, item);
            // Return a supplier that always returns the same registered instance
            return () -> registered;
        }

        @Override
        public <T> Supplier<DataComponentType<T>> registerDataComponent(ResourceLocation id, Supplier<DataComponentType<T>> supplier) {
            DataComponentType<T> component = supplier.get();
            DataComponentType<T> registered = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, component);
            return () -> registered;
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipe(ResourceLocation id, Supplier<RecipeType<T>> supplier) {
            RecipeType<T> recipe = supplier.get();
            RecipeType<T> registered = Registry.register(BuiltInRegistries.RECIPE_TYPE, id, recipe);
            return () -> registered;
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(ResourceLocation id, Supplier<RecipeSerializer<T>> supplier) {
            RecipeSerializer<T> serializer = supplier.get();
            RecipeSerializer<T> registered = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
            return () -> registered;
        }

        @Override
        public Supplier<CreativeModeTab> registerCreativeTab(ResourceLocation id, Supplier<CreativeModeTab> supplier) {
            CreativeModeTab tab = supplier.get();
            CreativeModeTab registered = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
            return () -> registered;
        }

        @Override
        public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBE(ResourceLocation id, Supplier<BlockEntityType<T>> supplier) {
            BlockEntityType<T> beType = supplier.get();
            BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, beType);
            return () -> registered;
        }

        @Override
        public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier) {
            return BlockEntityType.Builder.of(function::apply, validBlocksSupplier.get()).build(null);
        }
    }

    public static class FabricClientRegistry implements IRegistryHelper.IClientRegistry {

        @Override
        public <M extends AbstractContainerMenu> Supplier<MenuType<M>> registerMenu(
                ResourceLocation id,
                IRegistryHelper.ScreenConstructor<M> constructor) {

            // Use the identity codec - it passes the buffer through directly
            // Your constructor will receive the RegistryFriendlyByteBuf and can read from it
            ExtendedScreenHandlerType<M, RegistryFriendlyByteBuf> type =
                    new ExtendedScreenHandlerType<>(constructor::create, IDENTITY_CODEC);

            MenuType<M> registered = Registry.register(BuiltInRegistries.MENU, id, type);
            return () -> registered;
        }

        @Override
        public Supplier<Potion> registerPotion(ResourceLocation id, Supplier<Potion> supplier) {
            Potion potion = supplier.get();
            Potion registered = Registry.register(BuiltInRegistries.POTION, id, potion);
            return () -> registered;
        }
    }
}