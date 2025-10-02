package org.chubby.github.mobcontroller.common.items;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class LithiumBattery extends Item {
    private final int energyCapacity;
    private final int energyTransfer;

    public LithiumBattery(Properties properties, int energyCapacity, int energyTransfer) {
        super(properties.durability(energyCapacity));
        this.energyCapacity = energyCapacity;
        this.energyTransfer = energyTransfer;
    }

    public LithiumBattery(Properties properties) {
        this(properties, 100, 10);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.mobcontroller.lithium_battery.capacity", energyCapacity));
            tooltipComponents.add(Component.translatable("tooltip.mobcontroller.lithium_battery.transfer", energyTransfer));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.mobcontroller.hold_shift"));
        }
    }

    public int getEnergyCapacity() {
        return energyCapacity;
    }

    public int getEnergyTransfer() {
        return energyTransfer;
    }
}
