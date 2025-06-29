package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.*;
import org.chubby.github.mobcontroller.core.config.Config;

import java.lang.reflect.Field;

public final class MobController {

    public static void init() {
        ItemRegistry.init();
        CreativeTabRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        MenusRegistry.init();
        PotionRegistry.init();
        RecipeRegistry.init();
        handleConfig();
    }

    public static void handleConfig()
    {

        if(Config.saveConfig()){
            Config.loadConfig();
        }
        else{
            Constants.LOGGER.warn("No Config File Found!, Creating....");
            Config.saveConfig();
        }
    }
}
