package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.CreativeTabRegistry;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.core.config.Config;

public final class Mobcontroller {

    public static void init() {
        ItemRegistry.ITEMS.register();
        CreativeTabRegistry.TABS.register();
        DataComponentRegistry.DATA_COMPONENTS.register();
        MenusRegistry.TYPES.register();
        if(Config.saveConfig()){
            Config.loadConfig();
        }

    }
}
