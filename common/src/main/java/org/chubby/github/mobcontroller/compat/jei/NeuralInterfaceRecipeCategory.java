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
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.jetbrains.annotations.Nullable;

public class NeuralInterfaceRecipeCategory implements IRecipeCategory<NeuralInterfaceStationRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "neural_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,
            "textures/gui/menu/neural_interface_station_gui.png");

    public static final RecipeType<NeuralInterfaceStationRecipe> NEURAL_STATION_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, NeuralInterfaceStationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public NeuralInterfaceRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 166);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.NEURAL_INTERFACE_STATION.get()));
    }

    @Override
    public RecipeType<NeuralInterfaceStationRecipe> getRecipeType() {
        return NEURAL_STATION_RECIPE_RECIPE_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, NeuralInterfaceStationRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> items = recipe.getIngredients();
        ItemStack resultItem = recipe.getResultItem(null);
        ItemStack specialItem = recipe.specialItemHolder();
        ShapedRecipePattern pattern = recipe.getPattern();

        int width = pattern.width();
        int height = pattern.height();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = x + y * width;
                if (index < items.size()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, 30 + x * 18, 17 + y * 18)
                            .addIngredients(items.get(index));
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 10, 17)
                .addItemStack(specialItem);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 35)
                .addItemStack(resultItem);
    }
}