package org.chubby.github.mobcontroller.core.config;

import org.chubby.github.mobcontroller.core.config.property.IntProperty;
import org.chubby.github.mobcontroller.core.config.property.impl.ConfigProperty;

public class MCConfig
{
    @ConfigProperty
    public static IntProperty controlTick = Config.createIntProp("controlTick",250,"DeterminesForHowLongTheMobControllerWillWork");

}
