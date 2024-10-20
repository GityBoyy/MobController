package org.chubby.github.mobcontroller.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Monster.class)
public abstract class MixinRideControlledMob extends LivingEntity {

    protected MixinRideControlledMob(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
//        if(this.isVehicle() )
//        {
//            Player player = ItemController.playerMobControlMap.keySet().stream().findFirst().get();
//            if(player.getVehicle() instanceof Monster monster)
//            {
//                if(ItemController.playerMobControlMap.containsValue(monster))
//                {
//                    LivingEntity livingentity = this.getControllingPassenger();
//                    this.setYRot(livingentity.getYRot());
//                    this.yRotO = this.getYRot();
//                    this.setXRot(livingentity.getXRot() * 0.5F);
//                    this.setRot(this.getYRot(), this.getXRot());
//                    this.yBodyRot = this.getYRot();
//                    this.yHeadRot = this.yBodyRot;
//                    float f = livingentity.xxa * 0.5F;
//                    float f1 = livingentity.zza;
//
//
//                    if (this.isControlledByLocalInstance()) {
//                        float newSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
//
//                        if(Minecraft.getInstance().options.keySprint.isDown()) {
//                            newSpeed *= 2f;
//                        }
//
//                        this.setSpeed(newSpeed);
//                        super.travel(new Vec3(f, pTravelVector.y, f1));
//                    }
//                }
//
//            }
//
//        }
    }
}
