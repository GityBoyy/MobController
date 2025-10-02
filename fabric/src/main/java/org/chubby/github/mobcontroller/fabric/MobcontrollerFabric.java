package org.chubby.github.mobcontroller.fabric;

import org.chubby.github.mobcontroller.MobController;
import net.fabricmc.api.ModInitializer;

public final class MobcontrollerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MobController.getInstance().init();
        MobcontrollerEntityInteractionHandler.register();
    }
}
