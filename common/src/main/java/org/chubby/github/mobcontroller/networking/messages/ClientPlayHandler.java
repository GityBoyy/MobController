package org.chubby.github.mobcontroller.networking.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.chubby.github.mobcontroller.common.blocks.entity.fluid.FluidContainer;
import org.chubby.github.mobcontroller.common.blocks.entity.fluid.IFluidContainerBlock;

public class ClientPlayHandler {

    public static void handleMessageSyncFluid(MessageSyncFluid message) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level != null) {
            BlockEntity blockEntity = level.getBlockEntity(message.pos());
            if (blockEntity instanceof IFluidContainerBlock provider) {
                FluidContainer container = provider.getFluidContainer();
                if (container != null) {
                    container.handleSync(level, message.fluid(), message.amount());
                }
            }
        }
    }
}