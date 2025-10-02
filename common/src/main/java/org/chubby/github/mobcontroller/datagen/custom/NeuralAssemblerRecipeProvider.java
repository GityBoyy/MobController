package org.chubby.github.mobcontroller.datagen.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.chubby.github.mobcontroller.common.recipe.NeuralAssemblerRecipe;
import org.chubby.github.mobcontroller.common.recipe.custom.CustomShapedRecipePattern;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NeuralAssemblerRecipeProvider implements RecipeBuilder {

    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<String, Ingredient> key = Maps.newLinkedHashMap();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;
    private int energyRequired = 1000;
    private int craftingTime = 200;
    private RecipeCategory category = RecipeCategory.MISC;

    public NeuralAssemblerRecipeProvider(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    public static NeuralAssemblerRecipeProvider shaped(ItemLike itemLike) {
        return shaped(itemLike, 1);
    }

    public static NeuralAssemblerRecipeProvider shaped(ItemLike itemLike, int count) {
        return new NeuralAssemblerRecipeProvider(itemLike, count);
    }

    public NeuralAssemblerRecipeProvider define(Character character, TagKey<Item> tagKey) {
        return this.define(character, Ingredient.of(tagKey));
    }

    public NeuralAssemblerRecipeProvider define(Character character, ItemLike itemLike) {
        return this.define(character, Ingredient.of(itemLike));
    }

    public NeuralAssemblerRecipeProvider define(Character character, Ingredient ingredient) {
        String keyStr = String.valueOf(character);
        if (this.key.containsKey(keyStr)) {
            throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
        } else if (character == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(keyStr, ingredient);
            return this;
        }
    }

    public NeuralAssemblerRecipeProvider pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    public NeuralAssemblerRecipeProvider unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public NeuralAssemblerRecipeProvider group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public NeuralAssemblerRecipeProvider showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public NeuralAssemblerRecipeProvider energyRequired(int energyRequired) {
        this.energyRequired = energyRequired;
        return this;
    }

    public NeuralAssemblerRecipeProvider craftingTime(int craftingTime) {
        this.craftingTime = craftingTime;
        return this;
    }

    public NeuralAssemblerRecipeProvider category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);

        CustomShapedRecipePattern pattern = CustomShapedRecipePattern.of(this.rows, this.key, this.showNotification);

        ItemStack resultStack = new ItemStack(this.result, this.count);

        NeuralAssemblerRecipe recipe = new NeuralAssemblerRecipe(
                pattern,
                resultStack,
                this.energyRequired,
                this.craftingTime,
                Objects.requireNonNullElse(this.group, "")
        );

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        recipeOutput.accept(
                resourceLocation,
                recipe,
                advancementBuilder.build(resourceLocation.withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern defined for recipe " + resourceLocation);
        }
        if (this.key.isEmpty()) {
            throw new IllegalStateException("No ingredients defined for recipe " + resourceLocation);
        }

        for (String row : this.rows) {
            for (char c : row.toCharArray()) {
                if (c != ' ' && !this.key.containsKey(String.valueOf(c))) {
                    throw new IllegalStateException("Pattern uses undefined symbol '" + c + "' in recipe " + resourceLocation);
                }
            }
        }
    }
}