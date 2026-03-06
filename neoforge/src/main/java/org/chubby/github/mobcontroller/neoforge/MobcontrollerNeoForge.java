package org.chubby.github.mobcontroller.neoforge;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.MobController;
import org.chubby.github.mobcontroller.api.model.obj.loader.ObjModelLoader;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.ElectrolyticDiffuserScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.menu.ElectrolyticDiffuserMenu;
import org.chubby.github.mobcontroller.common.registry.*;
import org.chubby.github.mobcontroller.core.CommandsInit;
import org.chubby.github.mobcontroller.neoforge.datagen.ModBlockStateGenerator;
import org.chubby.github.mobcontroller.neoforge.datagen.ModItemModelGenerator;
import org.chubby.github.mobcontroller.neoforge.datagen.ModRecipeGenerator;
import org.chubby.github.mobcontroller.neoforge.datagen.ModTagProvider;
import org.chubby.github.mobcontroller.neoforge.platform.service.NeoforgeNetworkHelper;
import org.chubby.github.mobcontroller.neoforge.platform.service.NeoforgeRegistryHelper;
import org.chubby.github.mobcontroller.neoforge.wrapper.NeoForgeEnergyWrapper;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {

    public MobcontrollerNeoForge(IEventBus eventBus) {
        NeoforgeRegistryHelper.registerAll(eventBus);
        MobController.init();
        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }

        eventBus.addListener(this::registerMenuScreens);
        eventBus.addListener(this::buildCreativeContent);
        eventBus.addListener(NeoForgeEnergyWrapper::registerCapabilities);
        eventBus.addListener(NeoforgeNetworkHelper::register);
        eventBus.addListener(this::onRegisterReloadListeners);
        eventBus.addListener(this::onGatherData);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    public void buildCreativeContent(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTab() == CreativeTabRegistry.MOB_CONTROLLER_TAB.get()){
            event.accept(ItemRegistry.COPPER_CONTROLLER.get());
            event.accept(ItemRegistry.IRON_CONTROLLER.get());
            event.accept(ItemRegistry.GOLD_CONTROLLER.get());
            event.accept(ItemRegistry.DIAMOND_CONTROLLER.get());
            event.accept(ItemRegistry.NETHERITE_CONTROLLER.get());

            event.accept(ItemRegistry.COPPER_TO_IRON_UPGRADE.get());
            event.accept(ItemRegistry.IRON_TO_GOLD_UPGRADE.get());
            event.accept(ItemRegistry.GOLD_TO_DIAMOND_UPGRADE.get());
            event.accept(ItemRegistry.DIAMOND_TO_NETHERITE_UPGRADE.get());

            event.accept(ItemRegistry.GOGGLES.get());
            event.accept(ItemRegistry.SOUL_ESSENCE.get());

            event.accept(ItemRegistry.BRAIN_PIECE.get());
            event.accept(ItemRegistry.BRAIN.get());
        }
    }

    private void registerCommands(RegisterCommandsEvent event)
    {
        CommandsInit.register(event.getDispatcher());
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenusRegistry.MONSTER_MENU.get(), MonsterInventoryScreen::new);
        event.register(MenusRegistry.NEURAL_INTERFACE_STATION_MENU.get(), NeuralInterfaceStationScreen::new);
        event.register(MenusRegistry.ELECTROLYTIC_DIFFUSER_MENU.get(), ElectrolyticDiffuserScreen::new);
    }

    public void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.WATER, Items.QUARTZ, Holder.direct(PotionRegistry.CHARGED_POTION.get()));
        builder.addMix(Holder.direct(PotionRegistry.CHARGED_POTION.get()), Items.REDSTONE, Holder.direct(PotionRegistry.ELECTROLYTE_POTION.get()));
    }

    public void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ObjModelLoader.getInstance());
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientSetup {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {

            });
        }
    }

    //TODO::NOT USE THIS AND FIX THIS BULLSHIT
    public void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> completableFuture = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        boolean strictValidation = false;
        if (!strictValidation) {
            System.out.println("=".repeat(60));
            System.out.println("WARNING: Texture validation is DISABLED");
            System.out.println("Missing textures will be allowed but logged as warnings");
            System.out.println("=".repeat(60));
        }
        generator.addProvider(event.includeClient(),new ModBlockStateGenerator(packOutput,fileHelper));
        generator.addProvider(event.includeClient(),new ModItemModelGenerator(packOutput,fileHelper));
        generator.addProvider(event.includeServer(),new ModRecipeGenerator(packOutput,completableFuture));
        BlockTagsProvider blockTagsProvider = generator.addProvider(event.includeServer(), new BlockTagsProvider(packOutput, completableFuture,Constants.MOD_ID,fileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider p_256380_) {

            }
        });
        generator.addProvider(event.includeServer(),new ModTagProvider(packOutput,completableFuture,blockTagsProvider.contentsGetter(),fileHelper));
    }
}
