package org.chubby.github.mobcontroller.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.blocks.NeuralInterfaceStation;
import org.chubby.github.mobcontroller.util.Utils;

public class BlockRegistry
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Constants.MOD_ID,
            Registries.BLOCK);

    public static final RegistrySupplier<Block> NEURAL_INTERFACE_STATION = BLOCKS.register(Utils
            .resource("neural_interface_station"),()-> new NeuralInterfaceStation(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
}
