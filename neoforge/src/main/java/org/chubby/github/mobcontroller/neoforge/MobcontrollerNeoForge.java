package org.chubby.github.mobcontroller.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.Mobcontroller;
import net.neoforged.fml.common.Mod;
import org.chubby.github.mobcontroller.client.MobControllerClient;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {
    public MobcontrollerNeoForge() {
        IEventBus eventBus = EventBusesHooks.getModEventBus(Constants.MOD_ID).get();

        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }

        Mobcontroller.init();
    }

}
