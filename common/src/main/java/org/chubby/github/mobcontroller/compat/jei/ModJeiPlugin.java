package org.chubby.github.mobcontroller.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.chubby.github.mobcontroller.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"jei_plugin");
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

        registration.addRecipes(NeuralInterfaceRecipeCategory.NEURAL_STATION_RECIPE_RECIPE_TYPE, this.getRecipes(RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get()));

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

    private <C extends RecipeInput, T extends Recipe<C>> List<T> getRecipes(RecipeType<T> type)
    {
        return getRecipeManager().getAllRecipesFor(type).stream().map(RecipeHolder::value).toList();
    }

    public static RecipeManager getRecipeManager()
    {
        ClientPacketListener listener = Objects.requireNonNull(Minecraft.getInstance().getConnection());
        return listener.getRecipeManager();
    }

    private static RegistryAccess getRegistryAccess()
    {
        ClientPacketListener listener = Objects.requireNonNull(Minecraft.getInstance().getConnection());
        return listener.registryAccess();
    }

    public static ItemStack getResult(Recipe<?> recipe)
    {
        return recipe.getResultItem(getRegistryAccess());
    }
}
