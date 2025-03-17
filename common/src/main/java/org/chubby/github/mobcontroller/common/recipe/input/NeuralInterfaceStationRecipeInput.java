package org.chubby.github.mobcontroller.common.recipe.input;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NeuralInterfaceStationRecipeInput(NonNullList<ItemStack> inputs) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int index) {
        return inputs.get(index);
    }

    @Override
    public int size() {
        return inputs.size();
    }
}
