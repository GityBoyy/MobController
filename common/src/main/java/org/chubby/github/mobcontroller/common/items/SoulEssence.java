package org.chubby.github.mobcontroller.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;

import java.io.DataInput;
import java.util.List;

public class SoulEssence extends Item {
    public SoulEssence(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public Item asItem() {
        return super.asItem();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.has(DataComponentRegistry.SOUL_ESSENCE.get())) {
            stack.set(DataComponentRegistry.SOUL_ESSENCE.get(), new SoulEssenceData());
        }
        return InteractionResultHolder.success(stack);
    }
}