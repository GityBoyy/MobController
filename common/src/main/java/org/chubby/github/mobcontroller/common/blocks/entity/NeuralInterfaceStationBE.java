package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.menu.NeuralInterfaceStationMenu;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.common.recipe.input.NeuralInterfaceStationRecipeInput;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NeuralInterfaceStationBE extends BaseStorageTickingBE
{
    private final EnergyStorageImpl energyStorage = new EnergyStorageImpl(1000, 100, 50) {
        @Override
        public void onChange() {
            if(level != null)
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            setChanged();
        }
    };

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public NeuralInterfaceStationBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> NeuralInterfaceStationBE.this.progress;
                    case 1 -> NeuralInterfaceStationBE.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0-> NeuralInterfaceStationBE.this.progress = value;
                    case 1-> NeuralInterfaceStationBE.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        energyStorage.save(tag,registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        energyStorage.load(tag,registries);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.mobcontroller.neural_interface_station");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new NeuralInterfaceStationMenu(containerId,inventory,this,this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide() || !(be instanceof NeuralInterfaceStationBE neuralBE)) {
            return;
        }

        boolean hasRecipe = false;
        NeuralInterfaceStationRecipeInput recipeInput = new NeuralInterfaceStationRecipeInput(neuralBE.getItems());

        Optional<RecipeHolder<NeuralInterfaceStationRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get(), recipeInput, level);

        if (recipe.isPresent()) {
            NeuralInterfaceStationRecipe actualRecipe = recipe.get().value();
            neuralBE.maxProgress = actualRecipe.craftTime();

            // Check special item (slot 0)
            ItemStack specialItemRequired = actualRecipe.specialItemHolder();
            boolean hasSpecialItem = specialItemRequired.isEmpty() ||
                    ItemStack.isSameItem(neuralBE.getItems().get(0), specialItemRequired);

            // Check if output slot can accept the result
            ItemStack resultItem = actualRecipe.getResultItem(level.registryAccess());
            ItemStack outputSlot = neuralBE.getItems().getLast();
            boolean canOutput = outputSlot.isEmpty() ||
                    (ItemStack.isSameItem(outputSlot, resultItem) &&
                            outputSlot.getCount() + resultItem.getCount() <= outputSlot.getMaxStackSize());

            if (neuralBE.energyStorage.getStoredEnergy() >= actualRecipe.energyRequired() &&
                    hasSpecialItem && canOutput) {

                hasRecipe = true;
                neuralBE.progress++;

                // Extract energy gradually during crafting process
                int energyPerTick = Math.max(1, actualRecipe.energyRequired() / actualRecipe.craftTime());
                neuralBE.energyStorage.extract(energyPerTick, false);

                if (neuralBE.progress >= neuralBE.maxProgress) {
                    craftItem(neuralBE, recipe.get());
                }
            }
        }

        if (!hasRecipe) {
            neuralBE.progress = 0;
        }

        setChanged(level, pos, state);
    }

    private static void craftItem(NeuralInterfaceStationBE be, RecipeHolder<NeuralInterfaceStationRecipe> recipe) {
        Level level = be.getLevel();
        if (level == null) return;

        ItemStack resultItem = recipe.value().getResultItem(level.registryAccess()).copy();

        // Get output slot (last slot)
        int outputSlot = be.getItems().size() - 1;
        ItemStack outputStack = be.getItems().get(outputSlot);

        if (outputStack.isEmpty()) {
            be.getItems().set(outputSlot, resultItem);
        } else if (ItemStack.isSameItem(outputStack, resultItem) &&
                outputStack.getCount() + resultItem.getCount() <= outputStack.getMaxStackSize()) {
            outputStack.grow(resultItem.getCount());
        } else {
            // Cannot output, so don't craft
            return;
        }

        // Consume crafting grid items (slots 1-9)
        NonNullList<Ingredient> ingredients = recipe.value().getIngredients();
        int width = recipe.value().getPattern().width();
        int height = recipe.value().getPattern().height();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int ingredientIndex = x + y * width;
                if (ingredientIndex >= ingredients.size()) continue;

                Ingredient ingredient = ingredients.get(ingredientIndex);
                if (Ingredient.EMPTY.equals(ingredient)) continue;

                // Map to inventory slots (skip special slot 0)
                int slotIndex = 1 + x + y * 3;

                ItemStack slotStack = be.getItems().get(slotIndex);
                if (!slotStack.isEmpty() && ingredient.test(slotStack)) {
                    slotStack.shrink(1);
                    // Don't break here - consume exactly one item per ingredient
                }
            }
        }

        be.progress = 0;
    }

    public EnergyStorageImpl getEnergyStorage() {
        return energyStorage;
    }
}