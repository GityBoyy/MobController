package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.CreativeTabRegistry;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.common.registry.MenuRegistry;
import org.chubby.github.mobcontroller.core.config.Config;
import org.chubby.github.mobcontroller.networking.PacketHandler;

public final class Mobcontroller {

    public static void init() {
        ItemRegistry.ITEMS.register();
        CreativeTabRegistry.TABS.register();
        DataComponentRegistry.DATA_COMPONENTS.register();
        MenuRegistry.MENUS.register();
        if(Config.saveConfig()){
            Config.loadConfig();
        }

        PacketHandler.init();
    }
}
