package org.chubby.github.mobcontroller.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemController extends Item implements Equipable {
    private final ControllerType type;
    private static ItemController INSTANCE;
    public static final Map<UUID, Integer> playerControlledMobs = new ConcurrentHashMap<>();

    public ItemController(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
        INSTANCE = this;
    }

    public ControllerType getType() {
        return type;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public static Map<UUID, Integer> getPlayerMobControlMap()
    {
        return playerControlledMobs;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.mobcontroller.type" + type.getName().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GOLD));
    }
}