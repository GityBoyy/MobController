package org.chubby.github.mobcontroller.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.recipe.NeuralInterfaceStationRecipe;
import org.chubby.github.mobcontroller.util.Utils;

public class RecipeRegistry
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Constants.MOD_ID, Registries.RECIPE_TYPE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Constants.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeType<NeuralInterfaceStationRecipe>> NEURAL_INTERFACE_STATION_TYPE =
            RECIPE_TYPES.register(Utils.resource("neural_crafting"),
                    ()-> new Type<>("neural_crafting"));

    public static final RegistrySupplier<RecipeSerializer<NeuralInterfaceStationRecipe>> NEURAL_INTERFACE_STATION_SERIALIZER =
            RECIPE_SERIALIZERS.register(Utils.resource("neural_crafting"),
                    NeuralInterfaceStationRecipe.Serializer::new);

    public record Type<T extends Recipe<?>>(String id) implements RecipeType<T> {
        @Override
        public String toString() {
            return Constants.MOD_ID + ":" + id;
        }
    }
}
