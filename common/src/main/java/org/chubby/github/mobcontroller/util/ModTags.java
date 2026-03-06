package org.chubby.github.mobcontroller.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static class Items {
        public static final TagKey<Item> CONTROLLER = TagKey.create(Registries.ITEM,Utils.resource("controllers"));
    }
}
