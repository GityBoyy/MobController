package org.chubby.github.mobcontroller.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.Mobcontroller;
import net.neoforged.fml.common.Mod;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.neoforge.wrapper.NeoForgeEnergyWrapper;

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
    }

}
