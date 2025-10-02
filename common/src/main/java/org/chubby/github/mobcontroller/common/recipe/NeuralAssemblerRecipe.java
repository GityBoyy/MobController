package org.chubby.github.mobcontroller.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.recipe.custom.CustomShapedRecipePattern;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;

import java.util.List;
import java.util.Map;

public class NeuralAssemblerRecipe implements Recipe<RecipeInput> {

    private final CustomShapedRecipePattern pattern;
    private final ItemStack result;
    private final int energyRequired;
    private final int craftingTime;
    private final String group;

    public NeuralAssemblerRecipe(CustomShapedRecipePattern pattern, ItemStack result,
                                 int energyRequired, int craftingTime, String group) {
        this.pattern = pattern;
        this.result = result;
        this.energyRequired = energyRequired;
        this.craftingTime = craftingTime;
        this.group = group;
    }

    public NeuralAssemblerRecipe(List<String> patternStrings, Map<String, Ingredient> key,
                                 ItemStack result, int energyRequired, int craftingTime, String group) {
        this(CustomShapedRecipePattern.of(patternStrings, key, true), result, energyRequired, craftingTime, group);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return pattern.matches(input);
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= pattern.getWidth() && height >= pattern.getHeight();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.NEURAL_ASSEMBLER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.NEURAL_ASSEMBLER_TYPE.get();
    }

    @Override
    public String getGroup() {
        return group;
    }

    // Getters
    public CustomShapedRecipePattern getPattern() { return pattern; }
    public ItemStack getResultItemStack() { return result; }
    public int getEnergyRequired() { return energyRequired; }
    public int getCraftingTime() { return craftingTime; }

    public static class Serializer implements RecipeSerializer<NeuralAssemblerRecipe> {

        public static final MapCodec<NeuralAssemblerRecipe> CODEC = RecordCodecBuilder.<NeuralAssemblerRecipe>mapCodec(
                instance -> instance.group(
                        CustomShapedRecipePattern.MAP_CODEC.fieldOf("pattern").forGetter(NeuralAssemblerRecipe::getPattern),
                        ItemStack.CODEC.fieldOf("result").forGetter(NeuralAssemblerRecipe::getResultItemStack),
                        Codec.INT.fieldOf("energyRequired").forGetter(NeuralAssemblerRecipe::getEnergyRequired),
                        Codec.INT.fieldOf("craftingTime").forGetter(NeuralAssemblerRecipe::getCraftingTime),
                        Codec.STRING.optionalFieldOf("group", "").forGetter(NeuralAssemblerRecipe::getGroup)
                ).apply(instance, NeuralAssemblerRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, NeuralAssemblerRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::encode, Serializer::decode);

        private static NeuralAssemblerRecipe decode(RegistryFriendlyByteBuf buf) {
            CustomShapedRecipePattern pattern = CustomShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            int energyRequired = buf.readInt();
            int craftingTime = buf.readInt();
            String group = buf.readUtf();

            return new NeuralAssemblerRecipe(pattern, result, energyRequired, craftingTime, group);
        }

        private static void encode(RegistryFriendlyByteBuf buf, NeuralAssemblerRecipe recipe) {
            CustomShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.getPattern());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItemStack());
            buf.writeInt(recipe.getEnergyRequired());
            buf.writeInt(recipe.getCraftingTime());
            buf.writeUtf(recipe.getGroup());
        }

        @Override
        public MapCodec<NeuralAssemblerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NeuralAssemblerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}