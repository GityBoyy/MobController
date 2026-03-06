package org.chubby.github.mobcontroller.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;

import java.util.function.Supplier;

public class ModItemModelGenerator extends FabricModelProvider {

    public ModItemModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        Block quartzGlassBlock = BlockRegistry.QUARTZ_GLASS.get();
        blockWithItem(quartzGlassBlock,blockModelGenerators);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        ItemRegistry.ITEM_LIST.forEach(itemSupplier ->{
            Item item = itemSupplier.get();
            itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });
    }
    private void blockWithItem(Block blockRegistryObject,BlockModelGenerators generators) {
        generators.createTrivialCube(blockRegistryObject);
        generators.createSimpleFlatItemModel(blockRegistryObject);
    }
}
