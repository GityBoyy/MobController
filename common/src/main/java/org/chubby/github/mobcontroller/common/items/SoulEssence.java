package org.chubby.github.mobcontroller.common.items;

import net.minecraft.world.item.Item;

public class SoulEssence extends Item {
    public SoulEssence(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }
}