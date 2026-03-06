package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import net.minecraft.core.component.DataComponentType;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class DataComponentRegistry
{

    public static final Supplier<DataComponentType<MobControllerData>> CONTROLLER = Services.REGISTRY_HELPER().registerDataComponent(
            Utils.resource("controller"),
            Suppliers.memoize(()-> DataComponentType.<MobControllerData>builder().persistent(MobControllerData.CODEC).networkSynchronized(MobControllerData.STREAM_CODEC).build())
    );

    public static final Supplier<DataComponentType<ControllerTierData>> CONTROLLER_TIER = Services.REGISTRY_HELPER().registerDataComponent(
            Utils.resource("controller_tier"),
            Suppliers.memoize(() -> DataComponentType.<ControllerTierData>builder().persistent(ControllerTierData.CODEC).networkSynchronized(ControllerTierData.STREAM_CODEC).build())
    );

    public static final Supplier<DataComponentType<SoulEssenceData>> SOUL_ESSENCE = Services.REGISTRY_HELPER().registerDataComponent(
            Utils.resource("soul_essence_comp"),
            Suppliers.memoize(()-> DataComponentType.<SoulEssenceData>builder().persistent(SoulEssenceData.CODEC).networkSynchronized(SoulEssenceData.STREAM_CODEC).build())
            );

    public static void init(){}
}
