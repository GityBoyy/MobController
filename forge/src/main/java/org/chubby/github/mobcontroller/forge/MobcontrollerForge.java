package org.chubby.github.mobcontroller.forge;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.MobController;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.forge.platform.service.ForgeRegistryHelper;
import org.chubby.github.mobcontroller.forge.wrapper.forgeEnergyWrapper;

@Mod(Constants.MOD_ID)
public final class MobcontrollerForge {
    public MobcontrollerForge(IEventBus eventBus) {
        MobController.init();
        ForgeRegistryHelper.registerAll(eventBus);
        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }



        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(forgeEnergyWrapper::registerCapabilities);
        //MinecraftForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    public void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(()->{
            MenuScreens.register(MenusRegistry.MONSTER_MENU.get(),MonsterInventoryScreen::new);
            MenuScreens.register(MenusRegistry.DATA_DISPLAYER_MENU.get(), DataDisplayerScreen::new);
            MenuScreens.register(MenusRegistry.NEURAL_INTERFACE_STATION_MENU.get(), NeuralInterfaceStationScreen::new);
        });
    }


//    @SuppressWarnings("unchecked")
//    public void registerBrewingRecipes(RegisterBrew event)
//    {
//        PotionBrewing.Builder builder = event.getBuilder();
//
//        builder.addMix();
//        builder.addMix(Holder.direct(PotionRegistry.CHARGED_POTION.get()), Items.REDSTONE, Holder.direct(PotionRegistry.ELECTROLYTE_POTION.get()));
//    }
//
//    private void commonSetup(final FMLCommonSetupEvent event) {
//        event.enqueueWork(() -> {
//
//            BrewingRecipeRegistry.addRecipe(Potions.WATER, Items.QUARTZ, Holder.direct(PotionRegistry.CHARGED_POTION.get()));
//
//        });
//    }

}
