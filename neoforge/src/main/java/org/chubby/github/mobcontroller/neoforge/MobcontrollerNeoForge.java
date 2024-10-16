package org.chubby.github.mobcontroller.neoforge;

import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.Mobcontroller;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {
    public MobcontrollerNeoForge() {
        Mobcontroller.init();
    }
}
