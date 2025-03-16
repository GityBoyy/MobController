package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.data.DataDisplayerData;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.util.Utils;

public class DataComponentRegistry
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Constants.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<MobControllerData>> CONTROLLER = DATA_COMPONENTS.register(
            Utils.resource("controller"),
            Suppliers.memoize(()-> DataComponentType.<MobControllerData>builder().persistent(MobControllerData.CODEC).networkSynchronized(MobControllerData.STREAM_CODEC).build())
    );

    public static final RegistrySupplier<DataComponentType<ControllerTierData>> CONTROLLER_TIER = DATA_COMPONENTS.register(
            Utils.resource("controller_tier"),
            Suppliers.memoize(() -> DataComponentType.<ControllerTierData>builder().persistent(ControllerTierData.CODEC).networkSynchronized(ControllerTierData.STREAM_CODEC).build())
    );

    public static final RegistrySupplier<DataComponentType<DataDisplayerData>> DISPLAYER = DATA_COMPONENTS.register(
            Utils.resource("displayer"),
            Suppliers.memoize(() -> DataComponentType.<DataDisplayerData>builder().persistent(DataDisplayerData.CODEC).networkSynchronized(DataDisplayerData.STREAM_CODEC).build())
    );

    public static final RegistrySupplier<DataComponentType<SoulEssenceData>> SOUL_ESSENCE = DATA_COMPONENTS.register(
            Utils.resource("soul_essence_comp"),
            Suppliers.memoize(()-> DataComponentType.<SoulEssenceData>builder().persistent(SoulEssenceData.CODEC).networkSynchronized(SoulEssenceData.STREAM_CODEC).build())
            );
}
