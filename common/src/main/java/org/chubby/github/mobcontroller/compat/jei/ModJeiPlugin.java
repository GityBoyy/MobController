package org.chubby.github.mobcontroller.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.chubby.github.mobcontroller.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID,"jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new NeuralInterfaceRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;

//        List<NeuralInterfaceStationRecipe> recipes = level.getRecipeManager().getAllRecipesFor(
//                RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get()
//        ).stream().map(RecipeHolder::value).toList();
//        registration.addRecipes(NeuralInterfaceRecipeCategory.NEURAL_STATION_RECIPE_RECIPE_TYPE, recipes);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(NeuralInterfaceStationScreen.class, 70, 30, 25, 20,
                NeuralInterfaceRecipeCategory.NEURAL_STATION_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(BlockRegistry.NEURAL_INTERFACE_STATION.get()),
                NeuralInterfaceRecipeCategory.NEURAL_STATION_RECIPE_RECIPE_TYPE
        );
    }
}
