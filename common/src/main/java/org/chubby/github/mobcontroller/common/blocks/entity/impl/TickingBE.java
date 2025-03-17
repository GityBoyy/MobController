package org.chubby.github.mobcontroller.common.blocks.entity.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface TickingBE<T extends BlockEntity>
{
    void tick(Level level, BlockPos pos, BlockState state, T blockEntity);
}
