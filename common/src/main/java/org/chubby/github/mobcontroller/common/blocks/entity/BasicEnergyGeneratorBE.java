package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.blocks.entity.impl.DirectionalEnergyBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class BasicEnergyGeneratorBE extends BlockEntity implements WorldlyContainer, DirectionalEnergyBlock {
    private static final Map<Item, Integer> ITEM_TO_ENERGY_MAP = new WeakHashMap<>();
    private static final Map<Item, Integer> ITEM_BURN_TIME_MAP = new HashMap<>();
    private static final NonNullList<ItemStack> item = NonNullList.withSize(1, ItemStack.EMPTY);
    private final EnergyStorageImpl ENERGY_STORAGE;
    private Direction energyTransferDirection = Direction.UP;

    public BasicEnergyGeneratorBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.ENERGY_STORAGE = new EnergyStorageImpl(getMaxEnergyStorage(), getMaxReceive(), getMaxExtract()) {
            @Override
            public void onChange() {
                if (getLevel() == null) return;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
        populateItemToEnergyMap();
        populateItemBurnTimeMap();
    }

    @Override
    public Direction getDirectionForEnergyTransfer() {
        return energyTransferDirection;
    }

    public void setEnergyTransferDirection(Direction direction) {
        this.energyTransferDirection = direction;
    }

    protected abstract int getMaxExtract();

    protected abstract int getMaxReceive();

    protected abstract int getMaxEnergyStorage();

    protected abstract void populateItemToEnergyMap();
    protected abstract void populateItemBurnTimeMap();

    public void addFuel(Item item, int energyAmount) {
        getItemToEnergyMap().put(item, energyAmount);
    }

    public void addBurnTime(Item item, int burnTime) {
        getItemBurnTimeMap().put(item, burnTime);
    }

    public static Map<Item, Integer> getItemToEnergyMap() {
        return ITEM_TO_ENERGY_MAP;
    }

    public static Map<Item, Integer> getItemBurnTimeMap() {
        return ITEM_BURN_TIME_MAP;
    }

    public EnergyStorageImpl getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public static NonNullList<ItemStack> getItems() {
        return item;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BasicEnergyGeneratorBE blockEntity) {
        if(level.isClientSide()) return;


    }
}