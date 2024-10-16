package org.chubby.github.mobcontroller.common.items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ControllerType
{
    public static final ControllerType COPPER = new ControllerType("copper",1200, Blocks.COPPER_BLOCK);
    public static final ControllerType IRON = new ControllerType("iron",1800, Blocks.IRON_BLOCK);
    public static final ControllerType GOLD = new ControllerType("gold",900, Blocks.GOLD_BLOCK);
    public static final ControllerType DIAMOND = new ControllerType("diamond",2400, Blocks.DIAMOND_BLOCK);

    public String name;
    public ControllerType type;
    public int controlTime;
    public Block associatedBlock;
    public ControllerType(String name, int controlTime,Block associatedBlock)
    {
        this.name = name;
        this.controlTime = controlTime;
        this.associatedBlock = associatedBlock;
    }

    public String getName() {
        return name;
    }

    public int getControlTime() {
        return controlTime;
    }

    public ControllerType getType() {
        return type;
    }

    public Block getAssociatedBlock()
    {
        return associatedBlock;
    }
}
