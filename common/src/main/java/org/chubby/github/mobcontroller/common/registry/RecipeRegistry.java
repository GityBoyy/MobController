package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.chubby.github.mobcontroller.common.recipe.NeuralAssemblerRecipe;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class RecipeRegistry
{

    public static final Supplier<RecipeType<NeuralAssemblerRecipe>> NEURAL_ASSEMBLER_TYPE =
            Services.REGISTRY_HELPER().registerRecipe(Utils.resource("neural_crafting"),
                    ()-> new Type<>("neural_crafting"));

    public static final Supplier<RecipeSerializer<NeuralAssemblerRecipe>> NEURAL_ASSEMBLER_SERIALIZER =
            Services.REGISTRY_HELPER().registerRecipeSerializer(Utils.resource("neural_crafting"),
                    NeuralAssemblerRecipe.Serializer::new);
    public record Type<T extends Recipe<?>>(String id) implements RecipeType<T> {
        @Override
        public String toString() {
            return Utils.resource(id).toString();
        }
    }


    public static void init(){}
}
