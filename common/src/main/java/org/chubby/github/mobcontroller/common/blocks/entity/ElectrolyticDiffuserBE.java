package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.menu.ElectrolyticDiffuserMenu;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;

public class ElectrolyticDiffuserBE extends BaseStorageTickingBE{

    private final EnergyStorageImpl energyStorage = new EnergyStorageImpl(1000, 100, 50) {
        @Override
        public void onChange() {
            if(level != null)
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            setChanged();
        }
    };

    public ElectrolyticDiffuserBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ELECTROLYTIC_DIFFUSER_BE.get(), pos, blockState);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("container.mobcontroller.electrolytic_diffuser");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new ElectrolyticDiffuserMenu(i,inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        energyStorage.save(tag,registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.load(tag,registries);
    }
}
