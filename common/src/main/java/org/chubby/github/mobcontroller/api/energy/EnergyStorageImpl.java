package org.chubby.github.mobcontroller.api.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public abstract class EnergyStorageImpl implements EnergyStorage {
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public EnergyStorageImpl(long capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorageImpl(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageImpl(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageImpl(long capacity, long maxReceive, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Mth.clamp(energy, 0, capacity);
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (!canInsert() || amount <= 0) {
            return 0;
        }

        long energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, amount));
        if (!simulate) {
            this.energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public long extract(long amount, boolean simulate) {
        if (!canExtract() || amount <= 0) {
            return 0;
        }

        long energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, amount));
        if (!simulate) {
            this.energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public long getStoredEnergy() {
        return this.energy;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canInsert() {
        return this.maxReceive > 0;
    }

    public long getEnergy() {
        return energy;
    }

    public long getMaxReceive() {
        return maxReceive;
    }

    public long getMaxExtract() {
        return maxExtract;
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("EnergyStored", this.energy);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.energy = tag.getLong("EnergyStored");
    }

    public abstract void onChange();
}
