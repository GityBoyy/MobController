package org.chubby.github.mobcontroller.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.datagen.custom.NeuralAssemblerRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends RecipeProvider {

    public ModRecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, completableFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        super.buildRecipes(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,new ItemStack(BlockRegistry.QUARTZ_GLASS.get(),6))
                .pattern("GQG")
                .pattern("GQG")
                .pattern("GQG")
                .define('G',Items.GLASS)
                .define('Q',Items.QUARTZ)
                .unlockedBy("has_quartz",has(Items.QUARTZ))
                .save(pRecipeOutput);

        NeuralAssemblerRecipeProvider.shaped(ItemRegistry.COPPER_CONTROLLER.get(),1)
                .pattern("BHB")
                .pattern("ERE")
                .pattern("LLL")
                .define('B',getIngredient(ItemRegistry.BRAIN.get()))
                .define('b', ItemRegistry.EMPTY_ELECTROLYTE_BOTTLE.get())
                .define('H',Items.LEATHER_HELMET)
                .define('L',ItemRegistry.LITHIUM_BATTERY.get())
                .define('R',Items.REDSTONE)
                .craftingTime(200)
                .energyRequired(1000)
                .showNotification(true)
                .unlockedBy("has_brain",has(ItemRegistry.BRAIN.get()))
                .save(pRecipeOutput);

    }

    public Ingredient getIngredient(Item item)
    {
        return Ingredient.of(item);
    }
}
