package org.chubby.github.mobcontroller.common.items;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.menu.DataDisplayerMenu;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TabletItem extends Item
{

    public TabletItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide()) return InteractionResultHolder.fail(stack);
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.fail(stack);

        ItemStack soulEssence = null;
        for (ItemStack invStack : player.getInventory().items) {
            if (invStack.getItem() instanceof SoulEssence &&
                    invStack.has(DataComponentRegistry.SOUL_ESSENCE.get())) {
                soulEssence = invStack;
                break;
            }
        }

        if (soulEssence == null) {
            player.displayClientMessage(Component.translatable("message.tablet.no_soul_essence"), true);
            return InteractionResultHolder.fail(stack);
        }

        SoulEssenceData data = soulEssence.get(DataComponentRegistry.SOUL_ESSENCE.get());
        if (data == null || data.getControlledMobIds().isEmpty()) {
            player.displayClientMessage(Component.translatable("message.tablet.no_controlled_mobs"), true);
            return InteractionResultHolder.fail(stack);
        }

        MenuRegistry.openExtendedMenu(serverPlayer, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeInt(data.getControlledMobIds().size());
                for (int i = 0; i < data.getControlledMobIds().size(); i++) {
                    buf.writeInt(data.getControlledMobIds().get(i));
                    buf.writeUUID(data.getControllerIds().get(i < data.getControllerIds().size() ? i : 0));
                }
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.mobcontroller.tablet");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return new DataDisplayerMenu(windowId, playerInventory, data.getControlledMobIds());
            }
        });

        return InteractionResultHolder.success(stack);
    }
}
