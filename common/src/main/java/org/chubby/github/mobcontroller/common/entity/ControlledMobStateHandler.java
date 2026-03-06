package org.chubby.github.mobcontroller.common.entity;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ControlledMobStateHandler
{
    private final Map<UUID,ControlledMobState> controlledMobStateMap = new WeakHashMap<>();

    public static void updateControlledMobState()
    {

    }
}
