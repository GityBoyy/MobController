package org.chubby.github.mobcontroller.neoforge.wrapper;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.chubby.github.mobcontroller.api.energy.EnergyStorageImpl;
import org.chubby.github.mobcontroller.common.registry.BlockRegistry;

public class NeoForgeEnergyWrapper {

    public EnergyStorageImpl energyStorage;
    public static EnergyStorage STORAGE;
    public NeoForgeEnergyWrapper(EnergyStorageImpl energyStorage) {
        this.energyStorage = energyStorage;

        STORAGE = new EnergyStorage((int) energyStorage.getCapacity(),
                (int) energyStorage.getMaxReceive(), (int) energyStorage.getMaxExtract(), (int) energyStorage.getEnergy());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, be, side) -> STORAGE,
                BlockRegistry.NEURAL_INTERFACE_STATION.get()
        );
    }
}
