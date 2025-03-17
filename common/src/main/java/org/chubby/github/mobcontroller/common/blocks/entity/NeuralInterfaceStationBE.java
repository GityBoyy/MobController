package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.common.recipe.input.NeuralInterfaceStationRecipeInput;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;

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

    private int craftingProgress = 0;
    private int craftingTotalTime = 0;
    private int energyConsumed = 0;
    private int totalEnergyRequired = 0;
    private boolean isCrafting = false;

    public NeuralInterfaceStationBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get(), pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.mobcontroller.nis");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if(level.isClientSide()) return;
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(!(be instanceof NeuralInterfaceStationBE blockEntity)) return;

        Optional<RecipeHolder<NeuralInterfaceStationRecipe>> recipeOptional = recipeOptional(blockEntity, serverLevel);

        if(recipeOptional.isEmpty()) {
            if(blockEntity.isCrafting) {
                blockEntity.resetCrafting();
            }
            return;
        }

        RecipeHolder<NeuralInterfaceStationRecipe> recipeHolder = recipeOptional.get();
        NeuralInterfaceStationRecipe recipe = recipeHolder.value();
        ItemStack resultStack = recipe.assemble(
                new NeuralInterfaceStationRecipeInput(blockEntity.getItems()),
                serverLevel.registryAccess()
        );

        if(resultStack.isEmpty()) {
            blockEntity.resetCrafting();
            return;
        }

        if(!blockEntity.isCrafting) {
            blockEntity.startCrafting(recipe.craftTime(), recipe.energyRequired());
        }

        int energyNeededThisTick = Math.min(50, blockEntity.totalEnergyRequired - blockEntity.energyConsumed);
        if(blockEntity.energyStorage.getStoredEnergy() < energyNeededThisTick) {
            return;
        }

        blockEntity.energyStorage.extract(energyNeededThisTick, false);
        blockEntity.energyConsumed += energyNeededThisTick;

        blockEntity.craftingProgress++;

        if(blockEntity.craftingProgress >= blockEntity.craftingTotalTime &&
                blockEntity.energyConsumed >= blockEntity.totalEnergyRequired) {

            ItemStack outputSlot = blockEntity.getItem(blockEntity.getContainerSize() - 1); // Assuming last slot is output

            if(outputSlot.isEmpty() || (ItemStack.isSameItem(outputSlot, resultStack) &&
                    outputSlot.getCount() + resultStack.getCount() <= outputSlot.getMaxStackSize())) {

                for(int i = 0; i < recipe.inputItems().size(); i++) {
                    blockEntity.removeItem(i, 1);
                }

                if(outputSlot.isEmpty()) {
                    blockEntity.setItem(blockEntity.getContainerSize() - 1, resultStack.copy());
                } else {
                    outputSlot.grow(resultStack.getCount());
                }

                blockEntity.resetCrafting();
            }
        }

        blockEntity.setChanged();
    }

    private void startCrafting(int craftTime, int energyRequired) {
        this.craftingProgress = 0;
        this.craftingTotalTime = craftTime;
        this.energyConsumed = 0;
        this.totalEnergyRequired = energyRequired;
        this.isCrafting = true;
    }

    private void resetCrafting() {
        this.craftingProgress = 0;
        this.craftingTotalTime = 0;
        this.energyConsumed = 0;
        this.totalEnergyRequired = 0;
        this.isCrafting = false;
    }

    private static Optional<RecipeHolder<NeuralInterfaceStationRecipe>> recipeOptional(NeuralInterfaceStationBE be, ServerLevel level) {
        return level.getRecipeManager().getRecipeFor(
                RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get(),
                new NeuralInterfaceStationRecipeInput(be.getItems()),
                level
        );
    }

    private static ItemStack getResult(NeuralInterfaceStationBE blockEntity, ServerLevel serverLevel) {
        NeuralInterfaceStationRecipeInput input = new NeuralInterfaceStationRecipeInput(blockEntity.getItems());
        return recipeOptional(blockEntity, serverLevel)
                .map(RecipeHolder::value)
                .map(e -> e.assemble(input, serverLevel.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    private static int getCraftingTime(NeuralInterfaceStationBE be, ServerLevel level) {
        return recipeOptional(be, level)
                .map(RecipeHolder::value)
                .map(NeuralInterfaceStationRecipe::craftTime)
                .orElse(0);
    }

    private static int getEnergyRequirement(NeuralInterfaceStationBE be, ServerLevel level) {
        return recipeOptional(be, level)
                .map(RecipeHolder::value)
                .map(NeuralInterfaceStationRecipe::energyRequired)
                .orElse(0);
    }

    public int getCraftingProgress() {
        return craftingProgress;
    }

    public int getCraftingTotalTime() {
        return craftingTotalTime;
    }

    public boolean isCrafting() {
        return isCrafting;
    }

    public EnergyStorageImpl getEnergyStorage() {
        return energyStorage;
    }
}