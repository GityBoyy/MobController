package org.chubby.github.mobcontroller.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.networking.packets.ControllerSyncPacket;
import org.chubby.github.mobcontroller.util.UtilityMethods;

public class ServerHandler
{
    public static void handleControllerSync(ControllerSyncPacket packet, NetworkManager.PacketContext context)
    {
        context.queue(()->{
            if(context.getPlayer() instanceof ServerPlayer player)
            {
                if(packet.isAssigning()){
                    Entity entity = player.level().getEntity(packet.mobId());
                    if(entity instanceof Monster monster && player.getMainHandItem().getItem() instanceof ItemController controller)
                    {
                        ItemController.assignControlledMob(packet.playerUUID(),monster,controller.getControllerType());
                    }
                }
                else {
                    ItemController.removeControlledMob(packet.playerUUID());
                }
            }
        });

    }
}
