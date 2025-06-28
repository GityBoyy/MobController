package org.chubby.github.mobcontroller.neoforge;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.MobController;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.common.registry.PotionRegistry;
import org.chubby.github.mobcontroller.debug.screen.DebugScreen;
import org.chubby.github.mobcontroller.neoforge.platform.service.NeoforgeRegistryHelper;
import org.chubby.github.mobcontroller.neoforge.wrapper.NeoForgeEnergyWrapper;
import org.chubby.github.mobcontroller.util.Utils;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {
    public MobcontrollerNeoForge(IEventBus eventBus) {
        MobController.init();
        NeoforgeRegistryHelper.registerAll(eventBus);
        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }



        eventBus.addListener(this::registerMenuScreens);
        eventBus.addListener(NeoForgeEnergyWrapper::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenusRegistry.MONSTER_MENU.get(), MonsterInventoryScreen::new);
        event.register(MenusRegistry.DATA_DISPLAYER_MENU.get(), DataDisplayerScreen::new);
        event.register(MenusRegistry.NEURAL_INTERFACE_STATION_MENU.get(), NeuralInterfaceStationScreen::new);
    }


    @SuppressWarnings("unchecked")
    public void registerBrewingRecipes(RegisterBrewingRecipesEvent event)
    {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.WATER, Items.QUARTZ, Holder.direct(PotionRegistry.CHARGED_POTION.get()));
        builder.addMix(Holder.direct(PotionRegistry.CHARGED_POTION.get()), Items.REDSTONE, Holder.direct(PotionRegistry.ELECTROLYTE_POTION.get()));
    }

}
