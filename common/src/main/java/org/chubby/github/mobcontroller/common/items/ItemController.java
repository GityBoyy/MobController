package org.chubby.github.mobcontroller.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.data.ControllerTierData;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemController extends Item implements Equipable {
    public static final Map<UUID, Integer> playerControlledMobs = new ConcurrentHashMap<>();
    private static final int DRAIN_INTERVAL = 20;

    public ItemController(Properties properties) {
        super(properties);
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide() || !(entity instanceof LivingEntity living)) {
            return;
        }

        ItemStack headStack = living.getItemBySlot(EquipmentSlot.HEAD);
        if (headStack != stack) {
            return;
        }

        if (level.getGameTime() % DRAIN_INTERVAL == 0) {
            ControllerTierData data = stack.get(DataComponentRegistry.CONTROLLER_TIER.get());
            if (data != null && data.getElectrolyteAmount() > 0) {
                int newAmount = data.getElectrolyteAmount() - 1;

                ControllerTierData newData = new ControllerTierData(data.getType(), newAmount);
                stack.set(DataComponentRegistry.CONTROLLER_TIER.get(), newData);

                if (newAmount <= 0) {
                    living.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);

                    if (!level.isClientSide()) {
                        living.spawnAtLocation(stack.copy(), 0.0F);
                    }

                    stack.setCount(0);
                }
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        ControllerTierData data = stack.get(DataComponentRegistry.CONTROLLER_TIER.get());
        if (data == null) return 0x0000FF;

        int max = this.type.getElectrolyteAmount();
        int current = data.getElectrolyteAmount();
        float ratio = current / (float) max;

        if (ratio > 0.5f) {
            return 0x00AAFF;
        } else if (ratio > 0.25f) {
            return 0xFFAA00;
        } else {
            return 0xFF0000;
        }
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = this.type.getElectrolyteAmount();
        ControllerTierData data = stack.get(DataComponentRegistry.CONTROLLER_TIER.get());
        if (data == null) {
            return 13;
        }
        int current = data.getElectrolyteAmount();
        return Math.min(13, Math.round((current / (float) max) * 13));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.mobcontroller.type." + type.getName()).withStyle(ChatFormatting.GOLD));

        ControllerTierData data = stack.get(DataComponentRegistry.CONTROLLER_TIER.get());
        if (data != null) {
            int current = data.getElectrolyteAmount();
            int max = this.type.getElectrolyteAmount();
            tooltipComponents.add(Component.literal("Electrolyte: " + current + "/" + max).withStyle(ChatFormatting.AQUA));
        }
    }
}