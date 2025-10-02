package org.chubby.github.mobcontroller.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.common.blocks.entity.ScepticTankBE;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScepticTank extends BaseEntityBlock
{

    public ScepticTank(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ScepticTank::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScepticTankBE(pos,state);
    }
}