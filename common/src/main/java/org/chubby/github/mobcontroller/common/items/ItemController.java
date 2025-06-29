package org.chubby.github.mobcontroller.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemController extends Item implements Equipable {
    private final ControllerType type;
    private static ItemController INSTANCE;
    public static final Map<UUID, Integer> playerControlledMobs = new ConcurrentHashMap<>();
    public static MonsterStates currentState = MonsterStates.PASSIVE;

    public ItemController(Properties properties, ControllerType type) {
        super(properties);
        this.type = type;
        INSTANCE = this;
    }

    public static void setMonsterState(MonsterStates newMonsterState)
    {
        currentState = newMonsterState;
    }

    public ControllerType getType() {
        return type;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public static Map<UUID, Integer> getPlayerMobControlMap()
    {
        return playerControlledMobs;
    }
    public enum MonsterStates {
        AGGRESSIVE,
        PASSIVE,
        DEFENSIVE
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.mobcontroller.type",
                Component.literal(type.getName()).withStyle(ChatFormatting.GOLD)));
    }

}