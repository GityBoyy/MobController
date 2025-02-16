package org.chubby.github.mobcontroller.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemGoggles extends Item implements Equipable {
    private static boolean gogglesEquipped = false;
    public ItemGoggles(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof Player player && !level.isClientSide()) {
            gogglesEquipped = player.getInventory().getArmor(3).getItem() instanceof ItemGoggles;
        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    public static boolean isGogglesEquipped() {
        return gogglesEquipped;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}