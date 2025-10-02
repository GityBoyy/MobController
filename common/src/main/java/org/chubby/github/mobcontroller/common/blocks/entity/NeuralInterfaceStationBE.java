package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.items.LithiumBattery;
import org.chubby.github.mobcontroller.common.menu.NeuralInterfaceStationMenu;
import org.chubby.github.mobcontroller.common.recipe.NeuralAssemblerRecipe;
import org.chubby.github.mobcontroller.common.recipe.input.NeuralInterfaceStationRecipeInput;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class NeuralInterfaceStationBE extends BaseStorageTickingBE {
    private final EnergyStorageImpl energyStorage;
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public NeuralInterfaceStationBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get(), pos, blockState);

        this.energyStorage = new EnergyStorageImpl(1000, 100, 50) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> NeuralInterfaceStationBE.this.progress;
                    case 1 -> NeuralInterfaceStationBE.this.maxProgress;
                    case 2 -> NeuralInterfaceStationBE.this.energyStorage.getStoredEnergy();
                    case 3 -> NeuralInterfaceStationBE.this.energyStorage.getCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> NeuralInterfaceStationBE.this.progress = value;
                    case 1 -> NeuralInterfaceStationBE.this.maxProgress = value;
                    case 2 -> NeuralInterfaceStationBE.this.energyStorage.setEnergy(value);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        energyStorage.save(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        energyStorage.load(tag, registries);
    }

    @Override
    public int getSize() {
        return 11;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.mobcontroller.neural_interface_station");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new NeuralInterfaceStationMenu(containerId, inventory, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide() || !(be instanceof NeuralInterfaceStationBE neuralBE)) {
            return;
        }

        if (hasItemInSpecialSlot(neuralBE, ItemRegistry.LITHIUM_BATTERY.get())) {
            addEnergy(neuralBE);
        }

        boolean hasRecipe = false;
        RecipeHolder<NeuralAssemblerRecipe> currentRecipe = null;

        for (int i = 1; i < neuralBE.getItems().size() - 1; i++) {
            ItemStack stack = neuralBE.getItems().get(i);
            if (!stack.isEmpty()) {
                NeuralInterfaceStationRecipeInput recipeInput = new NeuralInterfaceStationRecipeInput(stack);
                Optional<RecipeHolder<NeuralAssemblerRecipe>> recipe = level.getRecipeManager()
                        .getRecipeFor(RecipeRegistry.NEURAL_ASSEMBLER_TYPE.get(), recipeInput, level);

                if (recipe.isPresent()) {
                    currentRecipe = recipe.get();
                    break;
                }
            }
        }

        if (currentRecipe != null) {
            NeuralAssemblerRecipe actualRecipe = currentRecipe.value();
            neuralBE.maxProgress = actualRecipe.getCraftingTime();

            ItemStack resultItem = actualRecipe.getResultItem(level.registryAccess());
            ItemStack outputSlot = neuralBE.getItems().getLast();
            boolean canOutput = outputSlot.isEmpty() ||
                    (ItemStack.isSameItem(outputSlot, resultItem) &&
                            outputSlot.getCount() + resultItem.getCount() <= outputSlot.getMaxStackSize());

            boolean hasAllIngredients = hasAllIngredients(neuralBE, actualRecipe.getIngredients());

            if (neuralBE.energyStorage.getStoredEnergy() >= actualRecipe.getEnergyRequired()
                    && canOutput && hasAllIngredients) {
                hasRecipe = true;
                neuralBE.progress++;

                int energyPerTick = Math.max(1, actualRecipe.getEnergyRequired() / actualRecipe.getCraftingTime());
                neuralBE.energyStorage.extract(energyPerTick, false);

                if (neuralBE.progress >= neuralBE.maxProgress) {
                    craftItem(neuralBE, currentRecipe);
                }
            }
        }

        if (!hasRecipe) {
            neuralBE.progress = 0;
        }

        setChanged(level, pos, state);
    }

    private static boolean hasAllIngredients(NeuralInterfaceStationBE be, List<Ingredient> ingredients) {
        List<ItemStack> inventory = be.getItems();

        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int j = 1; j < inventory.size() - 1; j++) {
                ItemStack stack = inventory.get(j);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasItemInSpecialSlot(NeuralInterfaceStationBE be, Item item) {
        return be.getItem(0).is(item);
    }

    public static void addEnergy(NeuralInterfaceStationBE be) {
        ItemStack batteryStack = be.getItem(0);
        if (batteryStack.getItem() instanceof LithiumBattery battery) {
            if (battery.getEnergyCapacity() <= 0) return;

            int energyToTransfer = battery.getEnergyTransfer();
            int actuallyInserted = be.getEnergyStorage().insert(energyToTransfer, false);

            if (actuallyInserted > 0) {
                batteryStack.setDamageValue(batteryStack.getDamageValue() + 1);

                if (batteryStack.getDamageValue() >= batteryStack.getMaxDamage()) {
                    batteryStack.shrink(1);
                }
            }
        }
    }

    private static void craftItem(NeuralInterfaceStationBE be, RecipeHolder<NeuralAssemblerRecipe> recipe) {
        Level level = be.getLevel();
        if (level == null) return;

        ItemStack resultItem = recipe.value().getResultItem(level.registryAccess()).copy();
        int outputSlot = be.getItems().size() - 1;
        ItemStack outputStack = be.getItems().get(outputSlot);

        if (outputStack.isEmpty()) {
            be.getItems().set(outputSlot, resultItem);
        } else if (ItemStack.isSameItem(outputStack, resultItem) &&
                outputStack.getCount() + resultItem.getCount() <= outputStack.getMaxStackSize()) {
            outputStack.grow(resultItem.getCount());
        } else {
            return;
        }

        List<Ingredient> ingredients = recipe.value().getIngredients();
        List<ItemStack> inventory = be.getItems();

        for (Ingredient ingredient : ingredients) {
            for (int j = 1; j < inventory.size() - 1; j++) {
                ItemStack stack = inventory.get(j);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    break;
                }
            }
        }

        be.progress = 0;
    }

    public EnergyStorageImpl getEnergyStorage() {
        return energyStorage;
    }
}