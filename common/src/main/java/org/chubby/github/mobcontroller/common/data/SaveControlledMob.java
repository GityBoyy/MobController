package org.chubby.github.mobcontroller.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveControlledMob extends SavedData {
    private static final String DATA_NAME = "controlled_mobs";
    private final Map<UUID, UUID> controlledMobData = new HashMap<>();

    public SaveControlledMob() {
        super();
    }

    public static SaveControlledMob create() {
        return new SaveControlledMob();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag mobsTag = new CompoundTag();

        controlledMobData.forEach((playerUUID, mobUUID) -> {
            mobsTag.putUUID(playerUUID.toString(), mobUUID);
        });

        compoundTag.put("ControlledMobs", mobsTag);
        return compoundTag;
    }

    public static SaveControlledMob load(CompoundTag tag, HolderLookup.Provider provider) {
        SaveControlledMob savedData = create();
        CompoundTag mobsTag = tag.getCompound("ControlledMobs");

        mobsTag.getAllKeys().forEach(playerUUIDString -> {
            UUID playerUUID = UUID.fromString(playerUUIDString);
            UUID mobUUID = mobsTag.getUUID(playerUUIDString);
            savedData.controlledMobData.put(playerUUID, mobUUID);
        });

        return savedData;
    }

    public void addControlledMob(Player player, Monster monster) {
        controlledMobData.put(player.getUUID(), monster.getUUID());
        setDirty();
    }

    public void removeControlledMob(Player player) {
        controlledMobData.remove(player.getUUID());
        setDirty();
    }

    public void loadControlledMobs(ServerLevel level) {
        controlledMobData.forEach((playerUUID, mobUUID) -> {
            Player player = level.getPlayerByUUID(playerUUID);
            if (player != null) {
                var entity = level.getEntity(mobUUID);
                if (entity instanceof Monster monster) {
                    ItemController.assignControlledMob(player, monster);
                }
            }
        });
    }

    public static SaveControlledMob get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<SaveControlledMob>(SaveControlledMob::new,SaveControlledMob::load, DataFixTypes.ENTITY_CHUNK),
                DATA_NAME
        );
    }
}
