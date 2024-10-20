package org.chubby.github.mobcontroller.client;

import dev.architectury.platform.Platform;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.ConfigScreen;

public class MobControllerClient
{
    public static void initClient()
    {
        Platform.getMod(Constants.MOD_ID).registerConfigurationScreen(ConfigScreen::new);
    }
}
