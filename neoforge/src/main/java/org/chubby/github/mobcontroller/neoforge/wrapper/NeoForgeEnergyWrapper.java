package org.chubby.github.mobcontroller.neoforge.wrapper;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;

/**
 * Wrapper that adapts our common EnergyStorageImpl to NeoForge's IEnergyStorage interface
 */
public class NeoForgeEnergyWrapper implements IEnergyStorage {
    private final EnergyStorageImpl energyStorage;

    public NeoForgeEnergyWrapper(EnergyStorageImpl energyStorage) {
        this.energyStorage = energyStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage.insert(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extract(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canInsert();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get(),
                (be, side) -> {
                    if (side == Direction.UP || side == Direction.DOWN) {
                        return null;
                    }

                    if (be instanceof NeuralInterfaceStationBE neuralBE) {
                        return new NeoForgeEnergyWrapper(neuralBE.getEnergyStorage());
                    }
                    return null;
                }
        );
    }
}