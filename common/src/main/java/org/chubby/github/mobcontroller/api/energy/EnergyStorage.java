package org.chubby.github.mobcontroller.api.energy;

/**
 * Generic energy storage interface for cross-mod compatibility.
 * Supports both Forge (IEnergyStorage) and Fabric (EnergyStorage).
 */
public interface EnergyStorage {
    /**
     * Inserts energy into the storage.
     *
     * @param amount The amount of energy to insert.
     * @param simulate If true, does not actually modify the storage.
     * @return The amount of energy successfully inserted.
     */
    long insert(long amount, boolean simulate);

    /**
     * Extracts energy from the storage.
     *
     * @param amount The amount of energy to extract.
     * @param simulate If true, does not actually modify the storage.
     * @return The amount of energy successfully extracted.
     */
    long extract(long amount, boolean simulate);

    /**
     * Gets the amount of energy currently stored.
     *
     * @return The stored energy value.
     */
    long getStoredEnergy();

    /**
     * Gets the maximum energy capacity of this storage.
     *
     * @return The maximum energy capacity.
     */
    long getCapacity();

    /**
     * Checks if energy can be inserted into this storage.
     *
     * @return True if energy can be inserted, false otherwise.
     */
    boolean canInsert();

    /**
     * Checks if energy can be extracted from this storage.
     *
     * @return True if energy can be extracted, false otherwise.
     */
    boolean canExtract();
}
