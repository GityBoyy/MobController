package org.chubby.github.mobcontroller.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.chubby.github.mobcontroller.fabric.datagen.ModBlockStateGenerator;
import org.chubby.github.mobcontroller.fabric.datagen.ModItemModelGenerator;
import org.chubby.github.mobcontroller.fabric.datagen.ModRecipeProvider;

public class MobControllerFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack packGen = fabricDataGenerator.createPack();

        packGen.addProvider(ModItemModelGenerator::new);
        packGen.addProvider(ModRecipeProvider::new);
    }
}
