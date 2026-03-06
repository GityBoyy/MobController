package org.chubby.github.mobcontroller.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.datagen.custom.NeuralAssemblerRecipeProvider;
import org.chubby.github.mobcontroller.util.Utils;

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
                .save(pRecipeOutput, Utils.resource("copper_controller"));
        SmithingTransformRecipeBuilder
                .smithing(
                        Ingredient.of(ItemRegistry.COPPER_TO_IRON_UPGRADE.get()),
                        Ingredient.of(ItemRegistry.PREPARED_CONTROLLER.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.MISC,
                        ItemRegistry.IRON_CONTROLLER.get())
                .unlocks("has_copper_upgrade",has(ItemRegistry.COPPER_UPGRADE.get()))
                .save(pRecipeOutput, Utils.resource("iron_controller"));
        SmithingTransformRecipeBuilder
                .smithing(
                        Ingredient.of(ItemRegistry.COPPER_UPGRADE.get()),
                        Ingredient.of(ItemRegistry.IRON_TO_GOLD_UPGRADE.get()),
                        Ingredient.of(Items.GOLD_INGOT),
                        RecipeCategory.MISC,
                        ItemRegistry.GOLD_CONTROLLER.get())
                .unlocks("has_copper_upgrade",has(ItemRegistry.COPPER_UPGRADE.get()))
                .save(pRecipeOutput, Utils.resource("gold_controller"));
        SmithingTransformRecipeBuilder
                .smithing(
                        Ingredient.of(ItemRegistry.COPPER_UPGRADE.get()),
                        Ingredient.of(ItemRegistry.GOLD_TO_DIAMOND_UPGRADE.get()),
                        Ingredient.of(Items.DIAMOND),
                        RecipeCategory.MISC,
                        ItemRegistry.DIAMOND_CONTROLLER.get())
                .unlocks("has_copper_upgrade",has(ItemRegistry.COPPER_UPGRADE.get()))
                .save(pRecipeOutput, Utils.resource("diamond_controller"));
        SmithingTransformRecipeBuilder
                .smithing(
                        Ingredient.of(ItemRegistry.COPPER_UPGRADE.get()),
                        Ingredient.of(ItemRegistry.DIAMOND_TO_NETHERITE_UPGRADE.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ItemRegistry.NETHERITE_CONTROLLER.get())
                .unlocks("has_copper_upgrade",has(ItemRegistry.COPPER_UPGRADE.get()))
                .save(pRecipeOutput, Utils.resource("netherite_controller"));

    }

    public Ingredient getIngredient(Item item)
    {
        return Ingredient.of(item);
    }
}
