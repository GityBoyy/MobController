package org.chubby.github.mobcontroller.common.items;

import net.minecraft.world.item.Item;

// Just a base class for my controller upgrade :kekw:
public class ItemControllerUpgrade extends Item
{
    private final ControllerType type;
    public ItemControllerUpgrade(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
    }

    public ControllerType getType() {
        return type;
    }
}
