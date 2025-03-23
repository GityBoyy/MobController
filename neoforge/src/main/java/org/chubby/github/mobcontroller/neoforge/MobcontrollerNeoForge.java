package org.chubby.github.mobcontroller.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.Mobcontroller;
import net.neoforged.fml.common.Mod;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.menu.NeuralInterfaceStationMenu;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.neoforge.wrapper.NeoForgeEnergyWrapper;
import org.chubby.github.mobcontroller.util.Utils;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {
    public MobcontrollerNeoForge() {
        IEventBus eventBus = EventBusesHooks.getModEventBus(Constants.MOD_ID).get();

        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }

        Mobcontroller.init();

        eventBus.addListener(this::registerMenuScreens);
        eventBus.addListener(NeoForgeEnergyWrapper::registerCapabilities);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenusRegistry.MONSTER_MENU.get(), MonsterInventoryScreen::new);
        event.register(MenusRegistry.DATA_DISPLAYER_MENU.get(), DataDisplayerScreen::new);
        event.register(MenusRegistry.NEURAL_INTERFACE_STATION_MENU.get(), NeuralInterfaceStationScreen::new);
    }

    public static void registerGuiLayerEvent()
    {
        //event.registerAboveAll(Utils.resource("debug_screen"),DEBUG_SCREEN.layer);
    }

}
