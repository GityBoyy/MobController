package org.chubby.github.mobcontroller.common.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.chubby.github.mobcontroller.common.items.ItemController;

import java.util.EnumSet;
import java.util.Map;

public class FollowPlayerControllingMob extends Goal {
    private final Mob mob;
    private LivingEntity target;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;
    private double oldPosX;
    private double oldPosY;
    private double oldPosZ;

    public FollowPlayerControllingMob(Mob mob, double speedModifier, float startDistance, float stopDistance) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Player controllingPlayer = ItemController.getplayerMobControlMap().entrySet().stream()
                .filter(entry -> entry.getValue() == this.mob)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (controllingPlayer == null) {
            return false;
        }

        this.target = controllingPlayer;

        double distanceSquared = this.mob.distanceToSqr(this.target);
        return distanceSquared >= (double)(this.startDistance * this.startDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.target.isAlive()) {
            return false;
        }

        double distanceSquared = this.mob.distanceToSqr(this.target);
        return distanceSquared >= (double)(this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.oldPosX = this.mob.getX();
        this.oldPosY = this.mob.getY();
        this.oldPosZ = this.mob.getZ();
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
        this.mob.getNavigation().recomputePath();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.target, 10.0F, (float)this.mob.getMaxHeadXRot());

        if (this.mob.isLeashed()) {
            return;
        }

        double distanceSquared = this.mob.distanceToSqr(this.target);
        if (distanceSquared <= (double)(this.stopDistance * this.stopDistance)) {
            this.mob.getNavigation().stop();
            return;
        }

        this.mob.getNavigation().moveTo(this.target, this.speedModifier);

        this.oldPosX = this.mob.getX();
        this.oldPosY = this.mob.getY();
        this.oldPosZ = this.mob.getZ();
    }
}