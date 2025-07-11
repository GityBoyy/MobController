package org.chubby.github.mobcontroller.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.data.MobControllerData;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "save", at = @At("HEAD"))
    private void onLevelSave(CallbackInfo ci) {
        ServerLevel level = (ServerLevel)(Object)this;

        for (var entry : ItemController.getPlayerMobControlMap().entrySet()) {
            UUID playerUUID = entry.getKey();
            int mobId = entry.getValue();

            var player = level.getPlayerByUUID(playerUUID);
            if (player == null) continue;

            var entity = level.getEntity(mobId);
            if (!(entity instanceof Monster monster)) continue;

            ItemStack headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
            if (headItem.has(DataComponentRegistry.CONTROLLER.get())) {
                MobControllerData controllerData = headItem.get(DataComponentRegistry.CONTROLLER.get());
                headItem.set(DataComponentRegistry.CONTROLLER.get(), controllerData); // Refresh to ensure save
                monster.setItemSlot(EquipmentSlot.HEAD, headItem); // Apply back
            }
        }
    }
}
