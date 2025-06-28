package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class BlockEntityRegistry
{

    public static final Supplier<BlockEntityType<NeuralInterfaceStationBE>> NEURAL_INTERFACE_STATION_BE =
            Services.REGISTRY_HELPER.registerBE(Utils.resource("neural_interface_station_be"
            ), ()-> Services.REGISTRY_HELPER.createBlockEntityType(NeuralInterfaceStationBE::new,()->new Block[]{
                    BlockRegistry.NEURAL_INTERFACE_STATION.get()
            }));
    public static void init(){}

}
