package org.chubby.github.mobcontroller.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.recipe.input.NeuralInterfaceStationRecipeInput;
import org.chubby.github.mobcontroller.common.registry.RecipeRegistry;
import org.chubby.github.mobcontroller.util.MCCodec;

public record NeuralInterfaceStationRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems,
                                           ItemStack outputStack, ItemStack specialItemHolder, int craftTime,
                                           int energyRequired) implements Recipe<NeuralInterfaceStationRecipeInput> {

    public NeuralInterfaceStationRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems,
                                        ItemStack outputStack, ItemStack specialItemHolder,
                                        int craftTime, int energyRequired) {
        this.id = id;
        this.inputItems = inputItems;
        this.outputStack = outputStack.copy();
        this.specialItemHolder = specialItemHolder.copy();
        this.craftTime = craftTime;
        this.energyRequired = energyRequired;
    }

    @Override
    public boolean matches(NeuralInterfaceStationRecipeInput input, Level level) {
        if (level == null || input.inputs().isEmpty()) {
            return false;
        }

        if (input.size() < inputItems.size()) {
            return false;
        }

        for (int i = 0; i < inputItems.size(); i++) {
            if (!inputItems.get(i).test(input.getItem(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(NeuralInterfaceStationRecipeInput input, HolderLookup.Provider registries) {
        return outputStack.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true; // Recipe uses its own container, not a crafting grid
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputStack.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.NEURAL_INTERFACE_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.NEURAL_INTERFACE_STATION_TYPE.get();
    }

    @Override
    public ItemStack specialItemHolder() {
        return specialItemHolder.copy();
    }

    public static class Serializer implements RecipeSerializer<NeuralInterfaceStationRecipe> {

        public static final MapCodec<NeuralInterfaceStationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(NeuralInterfaceStationRecipe::id),
                MCCodec.nonNullList(Ingredient.CODEC).fieldOf("inputItems").forGetter(NeuralInterfaceStationRecipe::inputItems),
                ItemStack.CODEC.fieldOf("outputItem").forGetter(NeuralInterfaceStationRecipe::outputStack),
                ItemStack.CODEC.fieldOf("specialItem").forGetter(NeuralInterfaceStationRecipe::specialItemHolder),
                Codec.INT.fieldOf("craftTime").forGetter(NeuralInterfaceStationRecipe::craftTime),
                Codec.INT.fieldOf("energyRequired").forGetter(NeuralInterfaceStationRecipe::energyRequired)
        ).apply(inst, NeuralInterfaceStationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, NeuralInterfaceStationRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::writeToBuffer, Serializer::readFromBuffer);

        private static NeuralInterfaceStationRecipe readFromBuffer(RegistryFriendlyByteBuf buf) {
            ResourceLocation id = buf.readResourceLocation();
            int ingredientCount = buf.readVarInt();

            NonNullList<Ingredient> inputItems = NonNullList.create();
            for (int i = 0; i < ingredientCount; i++) {
                inputItems.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }

            ItemStack outputStack = ItemStack.STREAM_CODEC.decode(buf);
            ItemStack specialItemHolder = ItemStack.STREAM_CODEC.decode(buf);
            int craftTime = buf.readVarInt();
            int energyRequired = buf.readVarInt();

            return new NeuralInterfaceStationRecipe(id, inputItems, outputStack, specialItemHolder, craftTime, energyRequired);
        }

        private static void writeToBuffer(RegistryFriendlyByteBuf buf, NeuralInterfaceStationRecipe recipe) {
            buf.writeResourceLocation(recipe.id());
            buf.writeVarInt(recipe.inputItems().size());

            for (Ingredient ingredient : recipe.inputItems()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buf, recipe.outputStack());
            ItemStack.STREAM_CODEC.encode(buf, recipe.specialItemHolder());
            buf.writeVarInt(recipe.craftTime());
            buf.writeVarInt(recipe.energyRequired());
        }


        @Override
        public MapCodec<NeuralInterfaceStationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NeuralInterfaceStationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}