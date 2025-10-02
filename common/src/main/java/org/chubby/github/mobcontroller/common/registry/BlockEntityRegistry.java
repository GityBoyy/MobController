package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.chubby.github.mobcontroller.common.blocks.entity.ElectrolyticDiffuserBE;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.common.blocks.entity.ScepticTankBE;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class BlockEntityRegistry
{

    public static final Supplier<BlockEntityType<NeuralInterfaceStationBE>> NEURAL_INTERFACE_STATION_BE =
            Services.REGISTRY_HELPER().registerBE(Utils.resource("neural_interface_station_be"
            ), ()-> Services.REGISTRY_HELPER().createBlockEntityType(NeuralInterfaceStationBE::new,()->new Block[]{
                    BlockRegistry.NEURAL_INTERFACE_STATION.get()
            }));
    public static final Supplier<BlockEntityType<ElectrolyticDiffuserBE>> ELECTROLYTIC_DIFFUSER_BE =
            Services.REGISTRY_HELPER().registerBE(Utils.resource("electrolytic_diffuser_be"
            ), ()-> Services.REGISTRY_HELPER().createBlockEntityType(ElectrolyticDiffuserBE::new,()->new Block[]{
                    BlockRegistry.ELECTROLYTIC_DIFFUSER.get()
            }));
    public static final Supplier<BlockEntityType<ScepticTankBE>> SCEPTIC_TANK_BE =
            Services.REGISTRY_HELPER().registerBE(Utils.resource("sceptic_tank_be"
            ), ()-> Services.REGISTRY_HELPER().createBlockEntityType(ScepticTankBE::new,()->new Block[]{
                    BlockRegistry.SCEPTIC_TANK.get()
            }));

    public static void init(){}

}
