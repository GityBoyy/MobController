package org.chubby.github.mobcontroller.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.datagen.custom.NeuralAssemblerRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, new ItemStack(BlockRegistry.QUARTZ_GLASS.get(),6).getItem())
                .pattern("GQG")
                .pattern("GQG")
                .pattern("GQG")
                .define('G', Items.GLASS)
                .define('Q',Items.QUARTZ)
                .unlockedBy("has_quartz",has(Items.QUARTZ))
                .save(pRecipeOutput);

        NeuralAssemblerRecipeProvider.shaped(ItemRegistry.UN_PREPARED_CONTROLLER.get(),1)
                .pattern("BHB")
                .pattern("ERE")
                .pattern("LLL")
                .define('B',getIngredient(ItemRegistry.BRAIN.get()))
                .define('E',getIngredient(ItemRegistry.EMPTY_ELECTROLYTE_BOTTLE.get()))
                .define('H',getIngredient(Items.LEATHER_HELMET))
                .define('L',getIngredient(ItemRegistry.LITHIUM_BATTERY.get()))
                .define('R',getIngredient(Items.REDSTONE))
                .craftingTime(200)
                .energyRequired(1000)
                .showNotification(true)
                .unlockedBy("has_brain",has(ItemRegistry.BRAIN.get()))
                .save(pRecipeOutput);

        SmithingTransformRecipeBuilder
                .smithing(
                        Ingredient.of(ItemRegistry.COPPER_UPGRADE.get()),
                        Ingredient.of(ItemRegistry.PREPARED_CONTROLLER.get()),
                        Ingredient.of(Items.COPPER_INGOT),
                        RecipeCategory.MISC,
                        ItemRegistry.COPPER_CONTROLLER.get())
                .unlocks("has_copper_upgrade",has(ItemRegistry.COPPER_UPGRADE.get()))
                .save(pRecipeOutput,"copper_controller");
    }

    public Ingredient getIngredient(Item item)
    {
        return Ingredient.of(item);
    }
}
