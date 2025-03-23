package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.*;
import org.chubby.github.mobcontroller.core.config.Config;

public final class Mobcontroller {

    public static void init() {
        ItemRegistry.ITEMS.register();
        BlockRegistry.BLOCKS.register();
        BlockEntityRegistry.BLOCK_ENTITIES.register();
        CreativeTabRegistry.TABS.register();
        DataComponentRegistry.DATA_COMPONENTS.register();
        MenusRegistry.TYPES.register();
        RecipeRegistry.RECIPE_TYPES.register();
        RecipeRegistry.RECIPE_SERIALIZERS.register();
        if(Config.saveConfig()){
            Config.loadConfig();
        }

    }
}
