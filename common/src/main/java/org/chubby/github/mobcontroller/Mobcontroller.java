package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.core.config.Config;

public final class Mobcontroller {

    public static void init() {
        ItemRegistry.ITEMS.register();
        Config.loadConfig();
    }
}
