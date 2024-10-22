package org.chubby.github.mobcontroller.mixin;

import net.minecraft.client.Minecraft;
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

        // Check if this entity is being ridden and is a controlled Monster
        if (entity.isVehicle() && entity instanceof Monster monster) {
            Player controllingPlayer = (Player) entity.getControllingPassenger(); // Get the player riding this entity

            if (controllingPlayer != null && ItemController.getplayerMobControlMap().containsKey(controllingPlayer)) {
                // Control the mob's rotation to match the player's rotation
                entity.setYRot(controllingPlayer.getYRot());
                entity.yRotO = entity.getYRot();
                entity.setXRot(controllingPlayer.getXRot() * 0.5F);
                entity.setRot(entity.getYRot(), entity.getXRot());
                entity.yBodyRot = entity.getYRot();
                entity.yHeadRot = entity.yBodyRot;

                // Use player's movement inputs for mob's movement
                float strafe = controllingPlayer.xxa * 0.5F; // Strafe movement
                float forward = controllingPlayer.zza; // Forward/backward movement

                if (entity.isControlledByLocalInstance()) {
                    float movementSpeed = (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);

                    // Double speed if sprinting
                    if (Minecraft.getInstance().options.keySprint.isDown()) {
                        movementSpeed *= 2.0F;
                    }

                    entity.setSpeed(movementSpeed); // Set the speed based on the mob's movement speed
                    entity.travel(new Vec3(strafe, travelVector.y, forward)); // Move the mob
                    ci.cancel(); // Cancel further execution of the method to prevent normal movement
                }
            }
        }
    }
}
