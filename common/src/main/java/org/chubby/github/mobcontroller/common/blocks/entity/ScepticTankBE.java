package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;

public class ScepticTankBE extends FluidStorageBE
{
    public ScepticTankBE(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SCEPTIC_TANK_BE.get(), pos, state,1000);
    }


}
