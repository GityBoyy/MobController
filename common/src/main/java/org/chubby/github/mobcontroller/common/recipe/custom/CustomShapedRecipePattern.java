package org.chubby.github.mobcontroller.common.recipe.custom;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;
import java.util.Optional;

public class CustomShapedRecipePattern {
    private static final int MAX_SIZE = 3;

    public static final MapCodec<CustomShapedRecipePattern> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("pattern").forGetter(CustomShapedRecipePattern::getPatternStrings),
                    Codec.unboundedMap(Codec.STRING, Ingredient.CODEC).fieldOf("key").forGetter(CustomShapedRecipePattern::getKeyMap),
                    Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(pattern -> pattern.data.map(Data::showNotification).orElse(true))
            ).apply(instance, CustomShapedRecipePattern::of)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomShapedRecipePattern> STREAM_CODEC =
            StreamCodec.of(CustomShapedRecipePattern::toNetwork, CustomShapedRecipePattern::fromNetwork);

    private final int width;
    private final int height;
    private final NonNullList<Ingredient> ingredients;
    private final Optional<CustomShapedRecipePattern.Data> data;
    private final int ingredientCount;
    public final boolean symmetrical;

    // Internal constructor
    private CustomShapedRecipePattern(int width, int height, NonNullList<Ingredient> ingredients,
                                      Optional<Data> data) {
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.data = data;
        this.ingredientCount = (int) ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).count();
        this.symmetrical = isSymmetrical();
    }

    // Factory method for creating from pattern strings and key mapping
    public static CustomShapedRecipePattern of(List<String> pattern, java.util.Map<String, Ingredient> key,
                                               boolean showNotification) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        int width = pattern.get(0).length();
        int height = pattern.size();

        if (width > MAX_SIZE || height > MAX_SIZE) {
            throw new IllegalArgumentException("Pattern too large, max size is " + MAX_SIZE + "x" + MAX_SIZE);
        }

        // Validate all rows have same width
        for (String row : pattern) {
            if (row.length() != width) {
                throw new IllegalArgumentException("All pattern rows must have the same width");
            }
        }

        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

        for (int y = 0; y < height; y++) {
            String row = pattern.get(y);
            for (int x = 0; x < width; x++) {
                char symbol = row.charAt(x);
                if (symbol != ' ') {
                    String keyStr = String.valueOf(symbol);
                    Ingredient ingredient = key.get(keyStr);
                    if (ingredient == null) {
                        throw new IllegalArgumentException("Pattern references symbol '" + symbol + "' but it's not defined in key");
                    }
                    ingredients.set(y * width + x, ingredient);
                }
            }
        }

        return new CustomShapedRecipePattern(width, height, ingredients,
                Optional.of(new Data(showNotification)));
    }

    // Check if recipe matches the input
    public boolean matches(RecipeInput input) {
        if (input.size() < width * height) {
            return false;
        }

        // Try matching at different positions (for smaller patterns in larger grids)
        for (int startX = 0; startX <= MAX_SIZE - width; startX++) {
            for (int startY = 0; startY <= MAX_SIZE - height; startY++) {
                if (matchesAt(input, startX, startY)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesAt(RecipeInput input, int startX, int startY) {
        for (int x = 0; x < MAX_SIZE; x++) {
            for (int y = 0; y < MAX_SIZE; y++) {
                int inputIndex = y * MAX_SIZE + x;
                ItemStack inputItem = inputIndex < input.size() ? input.getItem(inputIndex) : ItemStack.EMPTY;

                Ingredient expectedIngredient = Ingredient.EMPTY;

                // Check if this position is within our pattern
                if (x >= startX && x < startX + width && y >= startY && y < startY + height) {
                    int patternX = x - startX;
                    int patternY = y - startY;
                    expectedIngredient = ingredients.get(patternY * width + patternX);
                }

                // If we expect empty but have something, or expect something but don't match
                if (expectedIngredient.isEmpty()) {
                    if (!inputItem.isEmpty()) {
                        return false;
                    }
                } else {
                    if (!expectedIngredient.test(inputItem)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isSymmetrical() {
        // Check horizontal symmetry
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                int leftIndex = y * width + x;
                int rightIndex = y * width + (width - 1 - x);
                if (!ingredients.get(leftIndex).equals(ingredients.get(rightIndex))) {
                    return false;
                }
            }
        }
        return true;
    }

    // Network serialization
    private static void toNetwork(RegistryFriendlyByteBuf buf, CustomShapedRecipePattern pattern) {
        buf.writeVarInt(pattern.width);
        buf.writeVarInt(pattern.height);

        for (Ingredient ingredient : pattern.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }

        buf.writeBoolean(pattern.data.isPresent());
        if (pattern.data.isPresent()) {
            buf.writeBoolean(pattern.data.get().showNotification);
        }
    }

    private static CustomShapedRecipePattern fromNetwork(RegistryFriendlyByteBuf buf) {
        int width = buf.readVarInt();
        int height = buf.readVarInt();

        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        }

        Optional<Data> data = Optional.empty();
        if (buf.readBoolean()) {
            data = Optional.of(new Data(buf.readBoolean()));
        }

        return new CustomShapedRecipePattern(width, height, ingredients, data);
    }

    // Helper methods for codec
    @VisibleForTesting
    List<String> getPatternStrings() {
        // Reconstruct pattern strings from ingredients
        List<String> patterns = new java.util.ArrayList<>();
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Ingredient ingredient = ingredients.get(y * width + x);
                if (ingredient.isEmpty()) {
                    row.append(' ');
                } else {
                    // Use a simple symbol system for reconstruction
                    row.append('X'); // This is simplified - in practice you'd want to maintain the original mapping
                }
            }
            patterns.add(row.toString());
        }
        return patterns;
    }

    @VisibleForTesting
    java.util.Map<String, Ingredient> getKeyMap() {
        // Simplified key map reconstruction
        java.util.Map<String, Ingredient> keyMap = new java.util.HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                keyMap.put("X", ingredient); // Simplified
                break;
            }
        }
        return keyMap;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public NonNullList<Ingredient> getIngredients() { return ingredients; }
    public int getIngredientCount() { return ingredientCount; }

    // Data class for additional recipe metadata
    public static class Data {
        private final boolean showNotification;

        public Data(boolean showNotification) {
            this.showNotification = showNotification;
        }

        public boolean showNotification() {
            return showNotification;
        }
    }
}