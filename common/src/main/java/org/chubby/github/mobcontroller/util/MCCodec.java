package org.chubby.github.mobcontroller.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCCodec
{
    public static Codec<UUID> UUID_CODEC = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
}
