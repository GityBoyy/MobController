package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.UnaryOperator;

public class DataComponentRegistry
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Constants.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<CompoundTag>> CONTROLLER = DATA_COMPONENTS.register(
            "controller",
            () -> DataComponentType.<CompoundTag>builder().persistent(CompoundTag.CODEC).build()
    );
    private static <T> RegistrySupplier<DataComponentType<T> > register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENTS.register
                (Utils.resource(name), Suppliers.memoize(()-> builder.apply((DataComponentType.Builder<T>) DataComponentType.builder().build()).build()));
    }
}
