package org.chubby.github.mobcontroller.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.core.NonNullList;

import java.util.*;
import java.util.stream.Collectors;

public class MCCodec
{
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);

    /**
     * Generic codec for NonNullList that encodes/decodes as a regular List.
     * @param <T> The type of elements in the list
     * @return A codec that can encode/decode NonNullList<T>
     */
    public static <T> Codec<NonNullList<T>> nonNullList(Codec<T> elementCodec) {
        return Codec.list(elementCodec).xmap(
                list -> {
                    NonNullList<T> nonNullList = NonNullList.create();
                    nonNullList.addAll(list);
                    return nonNullList;
                },
                nonNullList -> new ArrayList<>(nonNullList)
        );
    }

    /**
     * NonNullList codec with a default element value.
     * @param <T> The type of elements in the list
     * @param defaultValue The default value for empty elements
     * @return A codec that can encode/decode NonNullList<T> with a default value
     */
    public static <T> Codec<NonNullList<T>> nonNullListWithDefault(Codec<T> elementCodec, T defaultValue) {
        return Codec.list(elementCodec).xmap(
                list -> {
                    NonNullList<T> nonNullList = NonNullList.withSize(list.size(), defaultValue);
                    for (int i = 0; i < list.size(); i++) {
                        nonNullList.set(i, list.get(i));
                    }
                    return nonNullList;
                },
                nonNullList -> new ArrayList<>(nonNullList)
        );
    }

    /**
     * NonNullList codec with a fixed size and default element value.
     * @param <T> The type of elements in the list
     * @param size The size of the list
     * @param defaultValue The default value for empty elements
     * @return A codec that can encode/decode fixed-size NonNullList<T> with a default value
     */
    public static <T> Codec<NonNullList<T>> fixedSizeNonNullList(Codec<T> elementCodec, int size, T defaultValue) {
        return Codec.list(elementCodec).comapFlatMap(
                list -> {
                    if (list.size() != size) {
                        return DataResult.error(() -> "Expected list of size " + size + ", got " + list.size());
                    }
                    NonNullList<T> nonNullList = NonNullList.withSize(size, defaultValue);
                    for (int i = 0; i < list.size(); i++) {
                        nonNullList.set(i, list.get(i));
                    }
                    return DataResult.success(nonNullList);
                },
                nonNullList -> new ArrayList<>(nonNullList)
        );
    }
}