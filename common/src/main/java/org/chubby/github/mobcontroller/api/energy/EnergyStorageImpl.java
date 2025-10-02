package org.chubby.github.mobcontroller.api.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class EnergyStorageImpl implements EnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;
    protected boolean canExtract = true;
    protected boolean canReceive = true;

    public EnergyStorageImpl(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorageImpl(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageImpl(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageImpl(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Mth.clamp(energy, 0, capacity);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canInsert() || amount <= 0) {
            return 0;
        }

        int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, amount));
        if (!simulate && energyReceived > 0) {
            this.energy += energyReceived;
            onChange();
        }
        return energyReceived;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canExtract() || amount <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, amount));
        if (!simulate && energyExtracted > 0) {
            this.energy -= energyExtracted;
            onChange();
        }
        return energyExtracted;
    }

    @Override
    public int getStoredEnergy() {
        return this.energy;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.canExtract && this.maxExtract > 0;
    }

    @Override
    public boolean canInsert() {
        return this.canReceive && this.maxReceive > 0;
    }

    public void setCanExtract(boolean canExtract) {
        this.canExtract = canExtract;
    }

    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setEnergy(int energy) {
        this.energy = Mth.clamp(energy, 0, capacity);
        onChange();
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("EnergyStored", this.energy);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.energy = Mth.clamp(tag.getInt("EnergyStored"), 0, capacity);
    }

    protected void onChange() {
    }
}