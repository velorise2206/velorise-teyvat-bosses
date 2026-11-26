package com.velorise.teyvatbosses.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

/**
 * Large-hitbox-aware ground A* navigation (inspired by L_Ender's Cataclysm / Mowzie's Mobs):
 * prevents wide bosses (2.4–4.4 blocks) from stalling on single-voxel waypoints, shallow craters,
 * or 1-block grass ledges, while routing cleanly around cliffs and trees.
 */
public class BossGroundNavigation extends GroundPathNavigation {

    public BossGroundNavigation(Mob mob, Level level) {
        super(mob, level);
        this.setCanFloat(true);
    }

    @Override
    protected void followThePath() {
        Vec3 mobPos = this.getTempMobPos();
        // Wide bosses must advance waypoints using a hitbox-scaled radius instead of vanilla's tiny 0.5-block threshold
        this.maxDistanceToWaypoint = Math.max(1.25F, this.mob.getBbWidth() * 0.62F);
        Path current = this.path;
        if (current != null && !current.isDone()) {
            Vec3 nodePos = current.getNextEntityPos(this.mob);
            double dx = Math.abs(this.mob.getX() - nodePos.x);
            double dy = Math.abs(this.mob.getY() - nodePos.y);
            double dz = Math.abs(this.mob.getZ() - nodePos.z);
            if (dx < this.maxDistanceToWaypoint && dz < this.maxDistanceToWaypoint && dy < 1.8D) {
                current.advance();
            }
        }
        this.doStuckDetection(mobPos);
    }

    /**
     * Returns a unit horizontal steering vector toward the next A* path node if a valid path exists,
     * falling back to direct target direction when unobstructed or at close range.
     */
    public Vec3 getSteerDirection(Vec3 targetPos) {
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
