package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinRideControlledMob {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void mobcontroller$travel(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check if the entity is being ridden and is a Monster
        if (entity.isVehicle() && entity instanceof Monster monster) {
            if (entity.getControllingPassenger() instanceof Player controllingPlayer
                    && ItemController.getplayerMobControlMap().containsKey(controllingPlayer)) {

                // Control the mob's rotation to match the player's rotation
                entity.setYRot(controllingPlayer.getYRot());
                entity.yRotO = entity.getYRot();
                entity.setXRot(controllingPlayer.getXRot() * 0.5F);
                entity.setRot(entity.getYRot(), entity.getXRot());
                entity.yBodyRot = entity.getYRot();
                entity.yHeadRot = entity.yBodyRot;

                // Get the player's movement inputs
                float strafe = controllingPlayer.xxa * 0.5F;
                float forward = controllingPlayer.zza;

                // Only move if this instance controls the entity
                if (entity.isControlledByLocalInstance()) {
                    double baseSpeed = entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    float movementSpeed = (float) baseSpeed;

                    // Double speed if the player is sprinting
                    if (controllingPlayer.isSprinting()) {
                        movementSpeed *= 2.0F;
                    }

                    // Set the speed and move the mob
                    entity.setSpeed(movementSpeed);
                    entity.travel(new Vec3(strafe, travelVector.y, forward));
                    ci.cancel(); // Cancel further execution to override default movement
                }
            }
        }
    }
}
