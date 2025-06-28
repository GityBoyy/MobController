package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class RecipeRegistry
{

    public static final Supplier<RecipeType<NeuralInterfaceStationRecipe>> NEURAL_INTERFACE_STATION_TYPE =
            Services.REGISTRY_HELPER.registerRecipe(Utils.resource("neural_crafting"),
                    ()-> new Type<>("neural_crafting"));

    public static final Supplier<RecipeSerializer<NeuralInterfaceStationRecipe>> NEURAL_INTERFACE_STATION_SERIALIZER =
            Services.REGISTRY_HELPER.registerRecipeSerializer(Utils.resource("neural_crafting"),
                    NeuralInterfaceStationRecipe.Serializer::new);
    public record Type<T extends Recipe<?>>(String id) implements RecipeType<T> {
        @Override
        public String toString() {
            return Constants.MOD_ID + ":" + id;
        }
    }

    public static void init(){}
}
