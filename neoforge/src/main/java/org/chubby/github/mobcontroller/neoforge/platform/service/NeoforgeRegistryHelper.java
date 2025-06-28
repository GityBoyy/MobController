package org.chubby.github.mobcontroller.neoforge.platform.service;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.platform.services.IRegistryHelper;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NeoforgeRegistryHelper implements IRegistryHelper
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Constants.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, Constants.MOD_ID);

    public static void registerAll(IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        DATA_COMPONENTS.register(bus);
        RECIPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENU_TYPES.register(bus);
        POTIONS.register(bus);
    }

    public static class NeoforgeServerRegistryHelper implements IRegistryHelper.IServerRegistry
    {

        @Override
        public Supplier<Item> registerItem(ResourceLocation id, Supplier<Item> supplier) {
            return ITEMS.register(id.getPath(),supplier);
        }

        @Override
        public Supplier<Block> registerBlock(ResourceLocation id, Supplier<Block> supplier) {
            return BLOCKS.register(id.getPath(),supplier);
        }

        @Override
        public Supplier<BlockItem> registerBlockItem(ResourceLocation id, Supplier<BlockItem> supplier) {
            return null;
        }

        @Override
        public <T> Supplier<DataComponentType<T>> registerDataComponent(ResourceLocation id, Supplier<DataComponentType<T>> supplier) {
            return  DATA_COMPONENTS.register(id.getPath(),supplier);
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipe(ResourceLocation id, Supplier<RecipeType<T>> supplier) {
            return RECIPES.register(id.getPath(),supplier);
        }

        @Override
        public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(ResourceLocation id, Supplier<RecipeSerializer<T>> supplier) {
            return RECIPE_SERIALIZERS.register(id.getPath(),supplier);
        }

        @Override
        public Supplier<CreativeModeTab> registerCreativeTab(ResourceLocation id, Supplier<CreativeModeTab> supplier) {
            return DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID)
                    .register(id.getPath(), supplier);
        }

        @Override
        public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBE(ResourceLocation id, Supplier<BlockEntityType<T>> supplier) {
            return BLOCK_ENTITIES.register(id.getPath(),supplier);
        }

        @Override
        public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier) {
            return BlockEntityType.Builder.<T>of(function::apply,validBlocksSupplier.get()).build(null);
        }
    }

    public static class NeoforgeClientRegistryHelper implements IRegistryHelper.IClientRegistry
    {

        @Override
        public <M extends AbstractContainerMenu> Supplier<MenuType<M>> registerMenu(ResourceLocation id, ScreenConstructor<M> constructor) {
            return MENU_TYPES.register(id.getPath(), () -> IMenuTypeExtension.create(constructor::create));
        }

        @Override
        public Supplier<Potion> registerPotion(ResourceLocation id, Supplier<Potion> potion) {
            return POTIONS.register(id.getPath(), potion);
        }
    }
}
