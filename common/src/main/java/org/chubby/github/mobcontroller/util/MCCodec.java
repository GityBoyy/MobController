package org.chubby.github.mobcontroller.util;

import com.mojang.serialization.Codec;

import java.util.UUID;

public class MCCodec
{
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
}