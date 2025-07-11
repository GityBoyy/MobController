package org.chubby.github.mobcontroller.common.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;
import org.chubby.github.mobcontroller.platform.services.IMenuHelper;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.jetbrains.annotations.Nullable;

public class NeuralInterfaceStation extends BaseEntityBlock
{

    public NeuralInterfaceStation(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(NeuralInterfaceStation::new);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof NeuralInterfaceStationBE menuProvider) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Services.MENU_HELPER().openMenu(serverPlayer,menuProvider,buf -> buf.writeBlockPos(menuProvider.getBlockPos()));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NeuralInterfaceStationBE(pos,state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get()
                ,NeuralInterfaceStationBE::tick);
    }
}
