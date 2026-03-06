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

import java.util.*;

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

    private final List<String> originalPattern;
    private final Map<String, Ingredient> originalKey;

    private CustomShapedRecipePattern(int width, int height, NonNullList<Ingredient> ingredients,
                                      Optional<Data> data, List<String> originalPattern, Map<String, Ingredient> originalKey) {
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.data = data;
        this.originalPattern = originalPattern;
        this.originalKey = originalKey;
        this.ingredientCount = (int) ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).count();
        this.symmetrical = isSymmetrical();
    }

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

        List<String> originalPattern = new ArrayList<>(pattern);
        Map<String, Ingredient> originalKey = new LinkedHashMap<>(key);

        return new CustomShapedRecipePattern(width, height, ingredients,
                Optional.of(new Data(showNotification)), originalPattern, originalKey);
    }

    public boolean matches(RecipeInput input) {
        if (input.size() < width * height) {
            return false;
        }

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

                if (x >= startX && x < startX + width && y >= startY && y < startY + height) {
                    int patternX = x - startX;
                    int patternY = y - startY;
                    expectedIngredient = ingredients.get(patternY * width + patternX);
                }

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

        buf.writeVarInt(pattern.originalPattern.size());
        for (String row : pattern.originalPattern) {
            buf.writeUtf(row);
        }

        buf.writeVarInt(pattern.originalKey.size());
        for (Map.Entry<String, Ingredient> entry : pattern.originalKey.entrySet()) {
            buf.writeUtf(entry.getKey());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, entry.getValue());
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

        int patternSize = buf.readVarInt();
        List<String> originalPattern = new ArrayList<>();
        for (int i = 0; i < patternSize; i++) {
            originalPattern.add(buf.readUtf());
        }

        int keySize = buf.readVarInt();
        Map<String, Ingredient> originalKey = new LinkedHashMap<>();
        for (int i = 0; i < keySize; i++) {
            String key = buf.readUtf();
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            originalKey.put(key, ingredient);
        }

        return new CustomShapedRecipePattern(width, height, ingredients, data, originalPattern, originalKey);
    }

    @VisibleForTesting
    List<String> getPatternStrings() {
        return originalPattern != null ? originalPattern : Collections.emptyList();
    }

    @VisibleForTesting
    java.util.Map<String, Ingredient> getKeyMap() {
        return originalKey != null ? originalKey : Collections.emptyMap();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public NonNullList<Ingredient> getIngredients() { return ingredients; }
    public int getIngredientCount() { return ingredientCount; }

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