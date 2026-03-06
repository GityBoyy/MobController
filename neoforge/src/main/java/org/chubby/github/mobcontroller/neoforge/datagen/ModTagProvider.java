package org.chubby.github.mobcontroller.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModTagProvider extends ItemTagsProvider {

    public ModTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, ExistingFileHelper fileHelper) {
        super(p_275343_, p_275729_, p_275322_, Constants.MOD_ID,fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Items.CONTROLLER)
                .add(ItemRegistry.COPPER_CONTROLLER.get())
                .add(ItemRegistry.IRON_CONTROLLER.get())
                .add(ItemRegistry.GOLD_CONTROLLER.get())
                .add(ItemRegistry.DIAMOND_CONTROLLER.get())
                .add(ItemRegistry.NETHERITE_CONTROLLER.get());
    }
}
