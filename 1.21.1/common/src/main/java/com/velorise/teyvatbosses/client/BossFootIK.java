package com.velorise.teyvatbosses.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lightweight 2-Bone Analytical Foot IK &amp; terrain height sampler for multi-legged bosses
 * ({@link RuinGuardRenderer} 2-leg bipedal IK and {@link PrimoGeovishapRenderer} 4-limb quadruped IK).
 *
 * <p>Prevents feet from clipping into 1-block grass steps, stairs, slabs, or floating over shallow
 * craters by solving exact Law-of-Cosines thigh/knee flexion and smooth pelvis height adaptation.
 */
public final class BossFootIK {

    private BossFootIK() {}

    /**
     * Samples the vertical ground offset {@code (groundY - entityY)} directly beneath a foot offset
     * in entity local space ({@code localRightBlocks > 0} = boss right, {@code localFwdBlocks > 0} = boss forward).
     *
     * @return clamped vertical offset in blocks within {@code [-maxDown, +maxUp]}, or {@code 0.0F} if no solid floor is found.
     */
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
        if (level == null) return 0.0F;
        float rad = bodyYawDeg * Mth.DEG_TO_RAD;
        double fwdX = -Mth.sin(rad);
        double fwdZ = Mth.cos(rad);
        double rightX = -Mth.cos(rad);
        double rightZ = -Mth.sin(rad);

        double wx = entityX + fwdX * localFwdBlocks + rightX * localRightBlocks;
        double wz = entityZ + fwdZ * localFwdBlocks + rightZ * localRightBlocks;
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
                // Ensure the block above the surface is open enough for a foot
                BlockState above = level.getBlockState(pos.above());
                if (above.getCollisionShape(level, pos.above()).isEmpty()) {
                    double groundY = y + shape.max(Direction.Axis.Y);
                    double dy = groundY - entityY;
                    if (dy <= maxUp + 0.05D && dy >= -maxDown - 0.05D) {
                        return Mth.clamp((float) dy, -maxDown, maxUp);
                    }
                    return 0.0F;
                }
            }
        }
        return 0.0F;
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
