package org.chubby.github.mobcontroller.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityMobController extends Entity {


    @Shadow public abstract void setSpeed(float f);

    @Shadow public abstract void travel(Vec3 arg);

    @Shadow public abstract double getAttributeValue(Holder<Attribute> arg);

    public MixinLivingEntityMobController(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void mobcontroller$travel(Vec3 vec3, CallbackInfo ci) {
        for (Player player : ItemController.playerMobControlMap.keySet()) {
            if (ItemController.playerMobControlMap.get(player).equals(this)) {
                if (this.isVehicle() && this.getControllingPassenger() instanceof Player controllingPlayer) {
                    this.setYRot(controllingPlayer.getYRot());
                    this.yRotO = this.getYRot();
                    this.xRotO = controllingPlayer.getXRot() * 0.5F;

                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    this.travel(new Vec3(controllingPlayer.xxa * 0.5F, vec3.y, controllingPlayer.zza * 0.5F));

                    ci.cancel();
                    return;
                }
            }
        }
    }
}
