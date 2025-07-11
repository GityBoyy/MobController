package org.chubby.github.mobcontroller.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.recipe.input.NeuralInterfaceStationRecipeInput;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NeuralInterfaceStationRecipe implements Recipe<NeuralInterfaceStationRecipeInput> {

    private final ItemStack result;
    private final ShapedRecipePattern pattern;
    private final ItemStack specialItemHolder;
    private final int craftTime;
    private final int energyRequired;

    public NeuralInterfaceStationRecipe(ShapedRecipePattern pattern,
                                        ItemStack result, ItemStack specialItemHolder,
                                        int craftTime, int energyRequired) {
        this.pattern = pattern;
        this.result = result;
        this.specialItemHolder = specialItemHolder;
        this.craftTime = craftTime;
        this.energyRequired = energyRequired;
    }

    @Override
    public boolean matches(NeuralInterfaceStationRecipeInput input, Level level) {
        if (level == null || input.inputs().isEmpty()) {
            return false;
        }

        // First, check if we have the special item (if required)
        if (!specialItemHolder.isEmpty()) {
            ItemStack specialSlotItem = input.getItem(0); // Assuming first slot is special item
            if (specialSlotItem.isEmpty() || !ItemStack.isSameItem(specialSlotItem, specialItemHolder)) {
                return false;
            }
        }

        return matchesGrid(input);
    }

    private boolean matchesGrid(NeuralInterfaceStationRecipeInput input) {
        int width = this.pattern.width();
        int height = this.pattern.height();

        // Skip the special slot (index 0) and start checking from index 1
        // Make sure we have enough items for the recipe
        if (input.size() - 2 < width * height) { // -2 for special slot and output slot
            return false;
        }

        NonNullList<Ingredient> ingredients = this.pattern.ingredients();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int ingredientIndex = x + y * width;
                if (ingredientIndex >= ingredients.size()) {
                    continue; // Skip if out of range
                }

                Ingredient ingredient = ingredients.get(ingredientIndex);
                // Skip slot 0 (special item) and map to crafting grid (slot 1-9)
                int inputIndex = 1 + x + y * 3;

                if (inputIndex >= input.size() - 1) { // Don't check output slot
                    return false;
                }

                if (!ingredient.test(input.getItem(inputIndex))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(NeuralInterfaceStationRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.pattern.width() && height >= this.pattern.height();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.NEURAL_INTERFACE_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get();
    }

    public ShapedRecipePattern getPattern() {
        return this.pattern;
    }

    public ItemStack specialItemHolder() {
        return this.specialItemHolder.copy();
    }

    public int craftTime() {
        return this.craftTime;
    }

    public int energyRequired() {
        return this.energyRequired;
    }

    public static class Serializer implements RecipeSerializer<NeuralInterfaceStationRecipe> {

        public static final MapCodec<NeuralInterfaceStationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ShapedRecipePattern.MAP_CODEC.forGetter(NeuralInterfaceStationRecipe::getPattern),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ItemStack.CODEC.fieldOf("specialItem").forGetter(NeuralInterfaceStationRecipe::specialItemHolder),
                Codec.INT.fieldOf("craftTime").forGetter(NeuralInterfaceStationRecipe::craftTime),
                Codec.INT.fieldOf("energyRequired").forGetter(NeuralInterfaceStationRecipe::energyRequired)
        ).apply(inst, NeuralInterfaceStationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, NeuralInterfaceStationRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::writeToBuffer, Serializer::readFromBuffer);

        private static NeuralInterfaceStationRecipe readFromBuffer(RegistryFriendlyByteBuf buf) {
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);

            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            ItemStack specialItemHolder = ItemStack.STREAM_CODEC.decode(buf);
            int craftTime = buf.readVarInt();
            int energyRequired = buf.readVarInt();

            return new NeuralInterfaceStationRecipe(pattern, result, specialItemHolder, craftTime, energyRequired);
        }

        private static void writeToBuffer(RegistryFriendlyByteBuf buf, NeuralInterfaceStationRecipe recipe) {
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);

            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            ItemStack.STREAM_CODEC.encode(buf, recipe.specialItemHolder);
            buf.writeVarInt(recipe.craftTime);
            buf.writeVarInt(recipe.energyRequired);
        }

        @Override
        public MapCodec<NeuralInterfaceStationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NeuralInterfaceStationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}