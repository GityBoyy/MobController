package org.chubby.github.mobcontroller.debug.data;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

import java.util.*;

public record DataComponentData(Minecraft minecraft, LivingEntity entity) {

    private static final List<EquipmentSlot> allSlots = new ArrayList<>();

    public Set<DataComponentType<?>> getActiveComponentsForSlot(EquipmentSlot slotId) {
        ItemStack slotItem = this.entity.getItemBySlot(slotId);
        DataComponentMap map = slotItem.getComponents();
        return map.keySet();
    }

    public Set<DataComponentType<?>> getActiveComponentsForAllSlots() {
        allSlots.clear(); // Clear previous slots to avoid duplicates
        allSlots.add(EquipmentSlot.HEAD);
        allSlots.add(EquipmentSlot.CHEST);
        allSlots.add(EquipmentSlot.LEGS);
        allSlots.add(EquipmentSlot.FEET);
        allSlots.add(EquipmentSlot.MAINHAND);
        allSlots.add(EquipmentSlot.OFFHAND);

        Set<DataComponentType<?>> allComponents = new HashSet<>();
        for(EquipmentSlot slot : allSlots) {
            ItemStack stack = this.entity.getItemBySlot(slot);
            allComponents.addAll(stack.getComponents().keySet());
        }
        return allComponents;
    }
}
