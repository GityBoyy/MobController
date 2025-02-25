package org.chubby.github.mobcontroller.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.ConfigScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;

public class MobControllerClient
{
    public static void initClient()
    {
        Platform.getMod(Constants.MOD_ID).registerConfigurationScreen(ConfigScreen::new);
    }
}
