package org.chubby.github.mobcontroller.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.util.Utils;

public class BlockEntityRegistry
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(Constants.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<NeuralInterfaceStationBE>> NEURAL_INTERFACE_STATION_BE =
            BLOCK_ENTITIES.register(Utils.resource("neural_interface_station_be"
            ), ()->BlockEntityType.Builder.of(NeuralInterfaceStationBE::new,BlockRegistry.NEURAL_INTERFACE_STATION.get())
                            .build(null));
}
