package com.velorise.teyvatbosses.entity.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Shared 4-phase boss disengage lifecycle:
 * <ol>
 *   <li><b>PHASE_SEARCH</b> (4.0s / 80 ticks): lingers alert near last known target position before disengaging.</li>
 *   <li><b>PHASE_RETURN</b>: physically walks / flies / swims back to {@code arenaCenter} while gradually regenerating HP (~6% MaxHP/sec).</li>
 *   <li><b>PHASE_SETTLE</b>: smoothly aligns facing to {@code savedSpawnYaw} once within 2.5 blocks of {@code arenaCenter}.</li>
 *   <li><b>Dormant</b>: caller transitions cleanly to {@code STATE_DORMANT} with zero jarring instant teleports.</li>
 * </ol>
 * Only falls back to a smoke-masked teleport if genuinely stuck (less than 1.5 blocks progress over 10 seconds).
 */
public final class BossReturnHomeHelper {
    public static final int PHASE_NONE   = 0;
    public static final int PHASE_SEARCH = 1;
    public static final int PHASE_RETURN = 2;
    public static final int PHASE_SETTLE = 3;

    public static final int DEFAULT_SEARCH_TICKS = 80; // 4.0s alert search before returning
    private static final int STUCK_WINDOW_TICKS  = 60; // check progress every 3.0s
    private static final int MAX_STUCK_STRIKES   = 3;  // 9.0s+ of <1.5m progress before smoke fallback

    private int phase = PHASE_NONE;
    private int phaseTicks = 0;
    private boolean hardReset = false;
    private Vec3 lastKnownTargetPos = null;
    private double lastProgressDist = Double.MAX_VALUE;
    private int progressCheckTick = 0;
    private int stuckStrikes = 0;

    public int getPhase() { return phase; }
    public int getPhaseTicks() { return phaseTicks; }
    public boolean isActive() { return phase != PHASE_NONE; }
    public boolean isReturning() { return phase == PHASE_RETURN || phase == PHASE_SETTLE; }
    public boolean isHardReset() { return hardReset; }
    public Vec3 getLastKnownTargetPos() { return lastKnownTargetPos; }

    public void recordTargetPos(Vec3 pos) {
        this.lastKnownTargetPos = pos;
    }

    public void reset() {
        this.phase = PHASE_NONE;
        this.phaseTicks = 0;
        this.hardReset = false;
        this.lastProgressDist = Double.MAX_VALUE;
        this.progressCheckTick = 0;
        this.stuckStrikes = 0;
    }

    /** Begins short alert search phase when target is lost inside the arena. */
    public void beginSearch() {
        if (this.phase == PHASE_NONE) {
            this.phase = PHASE_SEARCH;
            this.phaseTicks = 0;
            this.hardReset = false;
        }
    }

    /** Begins physical return-to-center walk/fly/swim (hardReset = true when triggered by leash). */
    public void beginReturn(boolean hardReset, double currentDistToHome) {
        this.phase = PHASE_RETURN;
        this.phaseTicks = 0;
        this.hardReset = this.hardReset || hardReset;
        this.lastProgressDist = currentDistToHome;
        this.progressCheckTick = 0;
        this.stuckStrikes = 0;
    }

    /** Advances search phase; transitions to PHASE_RETURN once search duration elapses. */
    public boolean tickSearch(double currentDistToHome) {
        phaseTicks++;
        if (phaseTicks >= DEFAULT_SEARCH_TICKS) {
            beginReturn(false, currentDistToHome);
            return true;
        }
        return false;
    }

    /** Smoothly regenerates ~6% Max HP per second (0.3% per tick) during return & settle. */
    public static void tickGradualRegen(LivingEntity boss, float percentPerSecond) {
        if (!boss.isAlive()) return;
        float maxHp = boss.getMaxHealth();
        if (boss.getHealth() < maxHp) {
            float perTick = maxHp * (percentPerSecond / 20.0F);
            boss.setHealth(Math.min(maxHp, boss.getHealth() + perTick));
        }
    }

    /**
     * Checks whether the boss has arrived at {@code arenaCenter} or is genuinely stuck.
     * Returns {@code true} only if stuck-fallback teleported the boss in a smoke burst.
     */
    public boolean checkReturnProgress(ServerLevel sl, LivingEntity boss, Vec3 arenaCenter, double horizDist, double arriveRadius) {
        phaseTicks++;
        if (horizDist <= arriveRadius) {
            this.phase = PHASE_SETTLE;
            this.phaseTicks = 0;
            this.stuckStrikes = 0;
            return false;
        }
        progressCheckTick++;
        if (progressCheckTick >= STUCK_WINDOW_TICKS) {
            progressCheckTick = 0;
            double progress = lastProgressDist - horizDist;
            if (progress < 1.5D) {
                stuckStrikes++;
                if (stuckStrikes >= MAX_STUCK_STRIKES) {
                    sl.sendParticles(ParticleTypes.POOF, boss.getX(), boss.getY() + 1.5D, boss.getZ(), 24, 1.2D, 1.0D, 1.2D, 0.05D);
                    boss.teleportTo(arenaCenter.x, arenaCenter.y, arenaCenter.z);
                    boss.setDeltaMovement(Vec3.ZERO);
                    sl.sendParticles(ParticleTypes.CLOUD, arenaCenter.x, arenaCenter.y + 1.5D, arenaCenter.z, 24, 1.2D, 1.0D, 1.2D, 0.05D);
                    this.phase = PHASE_SETTLE;
                    this.phaseTicks = 0;
                    this.stuckStrikes = 0;
                    return true;
                }
            } else {
                stuckStrikes = 0;
            }
            lastProgressDist = horizDist;
        }
        return false;
    }

    /** Smoothly rotates boss toward {@code savedSpawnYaw}; returns true when aligned and ready for STATE_DORMANT. */
    public boolean tickSettleYaw(LivingEntity boss, float savedSpawnYaw, float maxTurnDeg) {
        phaseTicks++;
        float newYaw = Mth.approachDegrees(boss.getYRot(), savedSpawnYaw, maxTurnDeg);
        boss.setYRot(newYaw);
        boss.yBodyRot = newYaw;
        boss.yHeadRot = newYaw;
        float diff = Math.abs(Mth.wrapDegrees(savedSpawnYaw - newYaw));
        return diff <= 3.0F || phaseTicks >= 35;
    }
}
