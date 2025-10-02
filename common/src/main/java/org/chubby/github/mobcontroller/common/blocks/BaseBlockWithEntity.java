package org.chubby.github.mobcontroller.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.common.blocks.entity.BaseStorageTickingBE;

public abstract class BaseBlockWithEntity extends BaseEntityBlock {


    protected BaseBlockWithEntity(Properties p_49224_) {
        super(p_49224_);
    }

//    @Override
//    protected void onRemove(BlockState p_60515_, Level level, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
//        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
//        if()
//    }
}
