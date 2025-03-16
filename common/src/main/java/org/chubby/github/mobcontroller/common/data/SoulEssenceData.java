package org.chubby.github.mobcontroller.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.chubby.github.mobcontroller.util.MCCodec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SoulEssenceData  {
    public static final Codec<SoulEssenceData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.listOf().fieldOf("controlledMobIds").forGetter(SoulEssenceData::getControlledMobIds),
            Codec.STRING.listOf().fieldOf("controllerIds").forGetter(data ->
                    data.controllerIds.stream().map(UUID::toString).toList())
    ).apply(inst, (mobIds, controllerIdStrings) -> {
        List<UUID> controllerIds = controllerIdStrings.stream()
                .map(UUID::fromString)
                .toList();
        return new SoulEssenceData(mobIds, controllerIds);
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulEssenceData> STREAM_CODEC =
            StreamCodec.of(SoulEssenceData::write, SoulEssenceData::read);

    private final List<Integer> controlledMobIds;
    private final List<UUID> controllerIds;

    public SoulEssenceData() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public SoulEssenceData(List<Integer> controlledMobIds, List<UUID> controllerIds) {
        this.controlledMobIds = new ArrayList<>(controlledMobIds);
        this.controllerIds = new ArrayList<>(controllerIds);
    }

    public List<Integer> getControlledMobIds() {
        return controlledMobIds;
    }

    public List<UUID> getControllerIds() {
        return controllerIds;
    }

    public void addControlledMob(int mobId, UUID controllerId) {
        if (!controlledMobIds.contains(mobId)) {
            controlledMobIds.add(mobId);
        }
        if (!controllerIds.contains(controllerId)) {
            controllerIds.add(controllerId);
        }
    }

    public boolean isControllingMob(int mobId) {
        return controlledMobIds.contains(mobId);
    }

    private static SoulEssenceData read(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Integer> mobIds = new ArrayList<>(size);
        List<UUID> controllerIds = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            mobIds.add(buf.readInt());
            controllerIds.add(buf.readUUID());
        }

        return new SoulEssenceData(mobIds, controllerIds);
    }

    private static void write(RegistryFriendlyByteBuf buf, SoulEssenceData data) {
        buf.writeInt(data.controlledMobIds.size());
        for (int i = 0; i < data.controlledMobIds.size(); i++) {
            buf.writeInt(data.controlledMobIds.get(i));
            buf.writeUUID(data.controllerIds.get(i < data.controllerIds.size() ? i : 0));
        }
    }

    public @Nullable Codec<SoulEssenceData> codec() {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, SoulEssenceData> streamCodec() {
        return STREAM_CODEC;
    }

    public SoulEssenceData copy()
    {
        return new SoulEssenceData(getControlledMobIds(),getControllerIds());
    }
}