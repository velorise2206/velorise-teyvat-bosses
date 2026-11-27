package com.velorise.teyvatbosses.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lightweight 2-Bone Analytical Foot IK &amp; multi-point footprint terrain sampler for multi-legged bosses
 * ({@link RuinGuardRenderer} 2-leg bipedal IK and {@link PrimoGeovishapRenderer} 4-limb quadruped IK).
 *
 * <p>Solves true split-level stance (one foot on a 1-block ledge, the other on the lower floor)
 * regardless of which Y-level Minecraft's single AABB hitbox currently snaps to.
 */
public final class BossFootIK {

    private BossFootIK() {}

    /**
     * Samples the highest solid floor offset {@code (groundY - entityY)} across a 5-point foot footprint
     * (center + front/back/left/right rim of radius {@code footRadius}) so wide boots and paws never clip
     * into block edges or stairs when their center sits just outside a ledge.
     */
    public static float sampleFootFootprint(
            Level level,
            double entityX,
            double entityY,
            double entityZ,
            float bodyYawDeg,
            double localRightBlocks,
            double localFwdBlocks,
            double footRadius,
            float maxUp,
            float maxDown
    ) {
        if (level == null) return 0.0F;
        float rad = bodyYawDeg * Mth.DEG_TO_RAD;
        double fwdX = -Mth.sin(rad);
        double fwdZ = Mth.cos(rad);
        // Matches poseStack.mulPose(Axis.YP.rotationDegrees(180 - bodyYaw)); poseStack.scale(-1, -1, 1);
        // where model +X (right limb bones) maps to world (+cos(yaw), +sin(yaw)).
        double rightX = Mth.cos(rad);
        double rightZ = Mth.sin(rad);

        double cx = entityX + fwdX * localFwdBlocks + rightX * localRightBlocks;
        double cz = entityZ + fwdZ * localFwdBlocks + rightZ * localRightBlocks;

        float best = sampleSinglePoint(level, cx, entityY, cz, maxUp, maxDown);
        if (footRadius > 0.05D) {
            best = Math.max(best, sampleSinglePoint(level, cx + fwdX * footRadius, entityY, cz + fwdZ * footRadius, maxUp, maxDown));
            best = Math.max(best, sampleSinglePoint(level, cx - fwdX * footRadius, entityY, cz - fwdZ * footRadius, maxUp, maxDown));
            best = Math.max(best, sampleSinglePoint(level, cx + rightX * footRadius, entityY, cz + rightZ * footRadius, maxUp, maxDown));
            best = Math.max(best, sampleSinglePoint(level, cx - rightX * footRadius, entityY, cz - rightZ * footRadius, maxUp, maxDown));
        }
        return best == Float.NEGATIVE_INFINITY ? 0.0F : Mth.clamp(best, -maxDown, maxUp);
    }

    /** Single-point compatibility overload. */
    public static float sampleFootGroundOffset(
            Level level,
            double entityX,
            double entityY,
            double entityZ,
            float bodyYawDeg,
            double localRightBlocks,
            double localFwdBlocks,
            float maxUp,
            float maxDown
    ) {
        return sampleFootFootprint(level, entityX, entityY, entityZ, bodyYawDeg, localRightBlocks, localFwdBlocks, 0.0D, maxUp, maxDown);
    }

    private static float sampleSinglePoint(Level level, double wx, double entityY, double wz, float maxUp, float maxDown) {
        int bx = Mth.floor(wx);
        int bz = Mth.floor(wz);

        int topY = Mth.floor(entityY + maxUp);
        int botY = Math.max(level.getMinBuildHeight(), Mth.floor(entityY - maxDown) - 1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = topY; y >= botY; y--) {
            pos.set(bx, y, bz);
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
                BlockState above = level.getBlockState(pos.above());
                if (above.getCollisionShape(level, pos.above()).isEmpty()) {
                    double groundY = y + shape.max(Direction.Axis.Y);
                    double dy = groundY - entityY;
                    if (dy <= maxUp + 0.08D && dy >= -maxDown - 0.08D) {
                        return (float) dy;
                    }
                    return Float.NEGATIVE_INFINITY;
                }
            }
        }
        return Float.NEGATIVE_INFINITY;
    }

    /**
     * Analytical 2-Bone IK solver using the Law of Cosines.
     * Given a positive vertical lift {@code liftBlocks > 0} (how much higher the terrain is than
     * the resting foot sole), computes the additional hip-lift and knee-bend angles in radians
     * needed so the foot rests flush on top of the higher block without clipping through it.
     *
     * @param liftBlocks   vertical height to raise the foot (blocks, &ge; 0)
     * @param upperLen     upper leg/arm length in blocks
     * @param lowerLen     lower shin/forearm length in blocks
     * @param outHipKnee   length-2 array receiving {@code [hipLiftRad, kneeBendRad]} (both &ge; 0)
     */
    public static void solveTwoBoneLift(float liftBlocks, float upperLen, float lowerLen, float[] outHipKnee) {
        outHipKnee[0] = 0.0F;
        outHipKnee[1] = 0.0F;
        if (liftBlocks <= 0.015F) return;

        float maxReach = upperLen + lowerLen - 0.02F;
        float minReach = Math.abs(upperLen - lowerLen) + 0.14F;
        float targetD = Mth.clamp(maxReach - liftBlocks, minReach, maxReach);

        float cosHip = (upperLen * upperLen + targetD * targetD - lowerLen * lowerLen) / (2.0F * upperLen * targetD);
        float cosKneeInner = (upperLen * upperLen + lowerLen * lowerLen - targetD * targetD) / (2.0F * upperLen * lowerLen);

        float hipAngle = (float) Math.acos(Mth.clamp(cosHip, -1.0F, 1.0F));
        float kneeBend = Mth.PI - (float) Math.acos(Mth.clamp(cosKneeInner, -1.0F, 1.0F));

        outHipKnee[0] = hipAngle;
        outHipKnee[1] = kneeBend;
    }
}
