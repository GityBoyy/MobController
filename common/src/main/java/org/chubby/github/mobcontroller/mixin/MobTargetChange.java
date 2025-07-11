package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Mob.class)
public class MobTargetChange {

    @Unique
    private final Mob mobcontroller$mob = (Mob)(Object)this;

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void onTargetChange(LivingEntity target, CallbackInfo ci) {
        if (!(mobcontroller$mob instanceof Monster monster)) return;
        if (target == null) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        UUID playerUUID = attachment.getControllingPlayer();
        if (target.getUUID().equals(playerUUID)) {
            ci.cancel();
        }
    }
}
