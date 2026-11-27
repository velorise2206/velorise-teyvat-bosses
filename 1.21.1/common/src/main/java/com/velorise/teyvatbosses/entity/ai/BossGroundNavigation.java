package com.velorise.teyvatbosses.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

/**
 * Large-hitbox-aware ground A* navigation (inspired by L_Ender's Cataclysm / Mowzie's Mobs):
 * computes A* paths around pits, cliffs, and obstacles without letting vanilla MoveControl
 * double-stack velocity on top of custom stride-synced boss movement.
 */
public class BossGroundNavigation extends GroundPathNavigation {

    public BossGroundNavigation(Mob mob, Level level) {
        super(mob, level);
        this.setCanFloat(true);
    }

    /**
     * Computes and stores an A* path to {@code target} for steering queries only
     * (does not engage vanilla MoveControl velocity stacking).
     */
    public void computePathTo(LivingEntity target) {
        Path p = this.createPath(target, 1);
        if (p != null) {
            this.path = p;
            advanceWaypoints();
        }
    }

    /**
     * Computes and stores an A* path to a fixed world coordinate (e.g. arena center during return-home).
     */
    public void computePathTo(Vec3 pos) {
        Path p = this.createPath(BlockPos.containing(pos.x, pos.y, pos.z), 1);
        if (p != null) {
            this.path = p;
            advanceWaypoints();
        }
    }

    @Override
    protected void followThePath() {
        Vec3 mobPos = this.getTempMobPos();
        advanceWaypoints();
        this.doStuckDetection(mobPos);
    }

    private void advanceWaypoints() {
        this.maxDistanceToWaypoint = Math.max(1.35F, this.mob.getBbWidth() * 0.68F);
        Path current = this.path;
        while (current != null && !current.isDone()) {
            Vec3 nodePos = current.getNextEntityPos(this.mob);
            double dx = Math.abs(this.mob.getX() - nodePos.x);
            double dy = Math.abs(this.mob.getY() - nodePos.y);
            double dz = Math.abs(this.mob.getZ() - nodePos.z);
            if (dx < this.maxDistanceToWaypoint && dz < this.maxDistanceToWaypoint && dy < 2.0D) {
                current.advance();
            } else {
                break;
            }
        }
    }

    /**
     * Returns a unit horizontal steering vector toward the next A* path node if a valid path exists,
     * falling back to direct target direction when unobstructed or at close range.
     */
    public Vec3 getSteerDirection(Vec3 targetPos) {
        advanceWaypoints();
        Path current = this.path;
        if (current != null && !current.isDone()) {
            Vec3 next = current.getNextEntityPos(this.mob);
            Vec3 toNode = new Vec3(next.x - this.mob.getX(), 0.0D, next.z - this.mob.getZ());
            if (toNode.lengthSqr() > 0.09D) {
                return toNode.normalize();
            }
        }
        Vec3 direct = new Vec3(targetPos.x - this.mob.getX(), 0.0D, targetPos.z - this.mob.getZ());
        return direct.lengthSqr() > 1.0E-4D ? direct.normalize() : Vec3.ZERO;
    }
}
