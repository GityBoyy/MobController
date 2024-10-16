package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.ItemRegistry;

public final class Mobcontroller {

    public static void init() {
        ItemRegistry.ITEMS.register();
    }
}
