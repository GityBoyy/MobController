package org.chubby.github.mobcontroller.api.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public interface IMenuData<T>
{
    StreamCodec<RegistryFriendlyByteBuf, T> codec();
}