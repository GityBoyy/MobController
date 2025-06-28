package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.chubby.github.mobcontroller.common.blocks.NeuralInterfaceStation;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class BlockRegistry
{
    public static final Supplier<Block> NEURAL_INTERFACE_STATION = Services.REGISTRY_HELPER.registerBlock(Utils
            .resource("neural_interface_station"),()-> new NeuralInterfaceStation(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static void init(){}
}
