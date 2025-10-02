package org.chubby.github.mobcontroller.compat.jei;


import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.recipe.NeuralAssemblerRecipe;
import org.chubby.github.mobcontroller.common.recipe.custom.CustomShapedRecipePattern;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeuralInterfaceRecipeCategory implements IRecipeCategory<NeuralAssemblerRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "neural_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,
            "textures/gui/menu/neural_interface_station_gui.png");

    public static final mezz.jei.api.recipe.RecipeType<NeuralAssemblerRecipe> NEURAL_STATION_RECIPE_RECIPE_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(
                    Utils.resource( "neural_crafting"),
                    NeuralAssemblerRecipe.class
            );

    private static final int CRAFTING_GRID_START_X = 30;
    private static final int CRAFTING_GRID_START_Y = 17;
    private static final int SLOT_SIZE = 18;

    private static final int RESULT_X = 124;
    private static final int RESULT_Y = 35;
    private final IDrawable background;
    private final IDrawable icon;

    public NeuralInterfaceRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 166);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.NEURAL_INTERFACE_STATION.get()));
    }

    @Override
    public @NotNull RecipeType<NeuralAssemblerRecipe> getRecipeType() {
        return NEURAL_STATION_RECIPE_RECIPE_TYPE;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Neural Interface Station");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NeuralAssemblerRecipe recipe, IFocusGroup focuses) {
        CustomShapedRecipePattern pattern = recipe.getPattern();
        NonNullList<Ingredient> ingredients = pattern.getIngredients();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int slotIndex = y * 3 + x;
                int posX = CRAFTING_GRID_START_X + (x * SLOT_SIZE);
                int posY = CRAFTING_GRID_START_Y + (y * SLOT_SIZE);

                Ingredient ingredient = Ingredient.EMPTY;
                if (x < pattern.getWidth() && y < pattern.getHeight()) {
                    int patternIndex = y * pattern.getWidth() + x;
                    if (patternIndex < ingredients.size()) {
                        ingredient = ingredients.get(patternIndex);
                    }
                }

                if (!ingredient.isEmpty()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, posX, posY)
                            .addIngredients(ingredient);
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, RESULT_X, RESULT_Y)
                .addItemStack(recipe.getResultItemStack());

    }

}