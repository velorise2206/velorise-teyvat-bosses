package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Cataclysm-inspired 3-layer Boss Damage Cap (Range Cap + Soft Per-Hit Cap + 20-tick Rolling DPS Cap)
 * with dedicated Anti-Mob-Army / Iron-Golem cheese protection and Stun/Paralyze reward windows.
 */
public final class BossDamageCap {
    private static final int WINDOW_TICKS = 20; // 1.0s rolling window
    private static final double RANGE_FALLOFF_START = 22.0D; // Full damage inside arena (<= 22 blocks)
    private static final double RANGE_IMMUNE_DIST   = 30.0D; // Immune to out-of-arena sniping (>= 30 blocks)

    private long windowStartTick = Long.MIN_VALUE;
    private float playerDamageInWindow = 0.0F;
    private float mobDamageInWindow = 0.0F;
    private long lastRangeWarnTick = Long.MIN_VALUE;

    /**
     * Applies Range Cap, Soft Per-Hit Cap, 1-second Rolling DPS Cap, and strict shared non-player mob-army cap.
     *
     * @param boss          the boss entity receiving damage
     * @param source        incoming damage source
     * @param rawAmount     damage after boss-specific resistances/multipliers
     * @param isVulnerable  true while stunned/paralyzed/core-broken (expands caps by 1.8x to reward mechanics)
     * @return capped damage to pass into {@code super.hurt(...)}
     */
    public float apply(LivingEntity boss, DamageSource source, float rawAmount, boolean isVulnerable) {
        if (rawAmount <= 0.0F) return 0.0F;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return rawAmount; // Never block /kill or void
        }

        TeyvatBossesConfig cfg = TeyvatBossesConfig.get();

        // 0. Cataclysm-style Range Cap: full damage within arena, falloff -> immune beyond configured distance
        Entity attacker = source.getEntity();
        if (cfg.enableRangeCap && attacker != null && attacker != boss) {
            double dist = boss.distanceTo(attacker);
            double immuneDist = cfg.rangeCapImmuneDistance;
            double falloffStart = cfg.rangeCapFalloffStart;
            if (dist >= immuneDist) {
                if (!boss.level().isClientSide()) {
                    boss.level().playSound(null, boss.getX(), boss.getY() + 2.0D, boss.getZ(),
                            SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 0.85F, 1.35F);
                    long gt = boss.level().getGameTime();
                    if (attacker instanceof Player p && (lastRangeWarnTick == Long.MIN_VALUE || gt - lastRangeWarnTick >= 60L)) {
                        lastRangeWarnTick = gt;
                        p.displayClientMessage(
                                Component.literal("Too far! Move within " + (int) immuneDist + " blocks to damage this boss.")
                                        .withStyle(ChatFormatting.GOLD),
                                true
                        );
                    }
                }
                return 0.0F;
            }
            if (dist > falloffStart) {
                float factor = (float) ((immuneDist - dist) / Math.max(1.0D, immuneDist - falloffStart));
                rawAmount *= Math.max(0.0F, Math.min(1.0F, factor));
            }
        }

        if (!cfg.enableDamageCap) {
            return rawAmount;
        }

        float maxHp = Math.max(20.0F, boss.getMaxHealth());
        long now = boss.level().getGameTime();
        if (windowStartTick == Long.MIN_VALUE || (now - windowStartTick) >= WINDOW_TICKS) {
            windowStartTick = now;
            playerDamageInWindow = 0.0F;
            mobDamageInWindow = 0.0F;
        }

        boolean isNonPlayerMob = (attacker instanceof LivingEntity && !(attacker instanceof Player));
        float vulnMult = isVulnerable ? 1.80F : 1.00F;

        if (isNonPlayerMob && cfg.enableAntiMobArmyCap) {
            // Anti-Iron-Golem / Mob-Army Cheese: non-player mobs cannot swarm-kill a Teyvat Boss
            float scaled = rawAmount * 0.30F;
            float mobPerHitCap = Math.min(4.5F, maxHp * 0.015F) * vulnMult;
            float cappedHit = Math.min(scaled, mobPerHitCap);

            // Shared 1-second mob-army DPS cap across all golems/mobs combined (3.5% Max HP/s)
            float mobDpsCap = Math.max(6.0F, maxHp * 0.035F) * vulnMult;
            float remaining = Math.max(0.0F, mobDpsCap - mobDamageInWindow);
            float finalDmg = Math.min(cappedHit, remaining);
            mobDamageInWindow += finalDmg;
            return finalDmg;
        }

        // Player / Environmental damage:
        // 1. Soft Per-Hit Cap: 100% up to 6% Max HP; excess compressed to 15%; hard ceiling 10% Max HP
        float softCap = maxHp * cfg.softPerHitCapRatio * vulnMult;
        float hardCap = maxHp * cfg.hardPerHitCapRatio * vulnMult;
        float cappedHit = rawAmount <= softCap
                ? rawAmount
                : Math.min(hardCap, softCap + (rawAmount - softCap) * 0.15F);

        // 2. Rolling 1-second DPS Cap: 15% Max HP/s (27% Max HP/s while stunned/paralyzed)
        float dpsCap = maxHp * cfg.dpsCapPerSecondRatio * vulnMult;
        float remaining = Math.max(0.0F, dpsCap - playerDamageInWindow);
        if (remaining <= 0.01F) {
            return Math.min(cappedHit, 0.5F);
        }
        float finalDmg = Math.min(cappedHit, remaining);
        playerDamageInWindow += finalDmg;
        return finalDmg;
    }
}
