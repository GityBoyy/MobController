package org.chubby.github.mobcontroller.forge.wrapper;

import net.minecraftforge.energy.IEnergyStorage;
import org.chubby.github.mobcontroller.api.energy.EnergyStorage;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.blocks.entity.NeuralInterfaceStationBE;
import org.chubby.github.mobcontroller.common.registry.BlockEntityRegistry;

public class ForgeEnergyWrapper implements IEnergyStorage {
    private final EnergyStorageImpl energyStorage;
    public ForgeEnergyWrapper(EnergyStorageImpl energyStorage) {
        this.energyStorage = energyStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) energyStorage.insert(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) energyStorage.extract(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) energyStorage.getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) energyStorage.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canInsert();
    }

//    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        event.registerBlockEntity(
//                Capabilities.EnergyStorage.BLOCK,
//                BlockEntityRegistry.NEURAL_INTERFACE_STATION_BE.get(),
//                (be, side) -> {
//                    if (be instanceof NeuralInterfaceStationBE neuralBE) {
//                        return new forgeEnergyWrapper(neuralBE.getEnergyStorage());
//                    }
//                    return null;
//                }
//        );
//    }
}