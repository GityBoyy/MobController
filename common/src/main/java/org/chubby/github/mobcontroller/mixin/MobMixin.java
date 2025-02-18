package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(Mob.class)
public class MobMixin
{
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void aiStep(CallbackInfo ci) {
        Mob self = (Mob)(Object)this;

//        UUID uuid = ItemController.getplayerMobControlMap().entrySet().stream()
//                .filter(entry -> entry.getValue() == self)
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElse(null);
//        Player controllingPlayer = self.level().getPlayerByUUID(uuid);
//
//        if (controllingPlayer != null) {
//            double distanceSquared = self.distanceToSqr(controllingPlayer);
//            if (distanceSquared >= 4.0D) {
//                self.getNavigation().moveTo(controllingPlayer, 1.0D);
//            } else {
//                self.getNavigation().stop();
//            }
//
//            self.getLookControl().setLookAt(controllingPlayer, 10.0F, (float)self.getMaxHeadXRot());
//
//            self.setAggressive(false);
//            self.setTarget(null);
//        }
    }
}
