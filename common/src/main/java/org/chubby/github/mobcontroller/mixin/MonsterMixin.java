package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Monster.class)
public abstract class MonsterMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void aiStep(CallbackInfo ci) {
        Monster self = (Monster)(Object)this;

        Player controllingPlayer = ItemController.getplayerMobControlMap().entrySet().stream()
                .filter(entry -> entry.getValue() == self)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (controllingPlayer != null) {
            double distanceSquared = self.distanceToSqr(controllingPlayer);
            if (distanceSquared >= 4.0D) {
                self.getNavigation().moveTo(controllingPlayer, 1.0D);
            } else {
                self.getNavigation().stop();
            }

            self.getLookControl().setLookAt(controllingPlayer, 10.0F, (float)self.getMaxHeadXRot());

            self.setAggressive(false);
            self.setTarget(null);
        }
    }
}