package org.chubby.github.mobcontroller;

import org.chubby.github.mobcontroller.common.registry.*;
import org.chubby.github.mobcontroller.core.config.Config;

public final class MobController
{

    private static MobController instance;
    private MobController() {}

    public static void init() {
        ItemRegistry.init();
        CreativeTabRegistry.init();
        BlockRegistry.init();
        BlockEntityRegistry.init();
        DataComponentRegistry.init();
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

    public static MobController getInstance() {
        return instance;
    }

}
