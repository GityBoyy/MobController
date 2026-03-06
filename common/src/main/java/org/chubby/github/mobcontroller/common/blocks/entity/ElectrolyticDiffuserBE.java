package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.blocks.ElectrolyticDiffuser;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.LithiumBattery;
import org.chubby.github.mobcontroller.common.menu.ElectrolyticDiffuserMenu;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.util.ModTags;

import java.util.Objects;
import java.util.Optional;

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
        return 3;
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

    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyticDiffuserBE blockEntity) {
        if (level.isClientSide()) return;
        if (blockEntity.getItems().isEmpty()) return;


        ScepticTankBE tankBE = null;
        BlockEntity entity = level.getBlockEntity(pos.east());
        if (entity == null) entity = level.getBlockEntity(pos.west());

        if (entity instanceof ScepticTankBE tank) {
            tankBE = tank;
        }

        if(tankBE == null) return;

        if(blockEntity.getItems().get(0).getItem() instanceof LithiumBattery battery){
            addEnergy(blockEntity);
        }

        ItemStack stack = blockEntity.getItems().getFirst();
        if (!stack.isEmpty() && stack.getItem() == ItemRegistry.UN_PREPARED_CONTROLLER.get() && tankBE.getStoredAmount() > 500) {
            tankBE.deductFluid(500);
            blockEntity.removeItem(1, 1);
            ItemStack stack1 = new ItemStack(ItemRegistry.PREPARED_CONTROLLER.get());
            blockEntity.setItem(2, stack1);
            blockEntity.setChanged();
        }
        if(!stack.isEmpty() && stack.is(ModTags.Items.CONTROLLER))
        {
            if(stack.has(DataComponentRegistry.CONTROLLER_TIER.get())){
                Objects.requireNonNull(stack.get(DataComponentRegistry.CONTROLLER_TIER.get())).addElectrolyte(1);
                tankBE.deductFluid(1);
            }
        }
    }

    public static void addEnergy(ElectrolyticDiffuserBE be) {
        ItemStack batteryStack = be.getItem(0);
        if (batteryStack.getItem() instanceof LithiumBattery battery) {
            if (battery.getEnergyCapacity() <= 0) return;

            int energyToTransfer = battery.getEnergyTransfer();
            int actuallyInserted = be.getEnergyStorage().insert(energyToTransfer, false);

            if (actuallyInserted > 0) {
                batteryStack.setDamageValue(batteryStack.getDamageValue() + 1);

                if (batteryStack.getDamageValue() >= batteryStack.getMaxDamage()) {
                    batteryStack.shrink(1);
                }
            }
        }
    }

    public EnergyStorageImpl getEnergyStorage() {
        return energyStorage;
    }
}
