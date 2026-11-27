package com.velorise.teyvatbosses.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Unified, single-track client-side boss music controller for all Velorise: Teyvat Bosses.
 *
 * <p>Features smooth fade-in and fade-out: the instant a boss is no longer actively fighting
 * any target (even if frustum-culled behind the player), the track smoothly fades down to
 * silence over ~1.7 seconds and stops cleanly with zero abrupt cuts or overlapping streams.
 */
public final class BossMusicManager {
    private static final double START_DIST_SQ = 48.0D * 48.0D;
    private static final double STOP_DIST_SQ  = 56.0D * 56.0D;
    private static final double TAKEOVER_MARGIN_BLOCKS = 6.0D;
    private static final long START_DEBOUNCE_MS  = 1500L;
    private static final long SWITCH_COOLDOWN_MS = 3000L;
    private static final float TARGET_VOLUME = 0.85F;
    private static final float FADE_IN_STEP  = 0.08F;
    private static final float FADE_OUT_STEP = 0.025F; // ~1.7s smooth fade-out (34 ticks)

    private static BossMusicInstance activeTrack = null;
    private static LivingEntity activeBoss = null;
    private static ResourceLocation activeSoundId = null;
    private static Level activeLevel = null;
    private static long lastPlayRequestMs = 0L;
    private static long lastSwitchMs = 0L;

    private static final Set<BossMusicInstance> TRACKED_INSTANCES = new HashSet<>();

    private BossMusicManager() {}

    public static void update(LivingEntity boss, ResourceLocation soundId, boolean bossActive) {
        update(boss, soundId, bossActive, () -> bossActive);
    }

    public static void update(LivingEntity boss, ResourceLocation soundId, boolean bossActive, BooleanSupplier liveActiveCheck) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            stopAll(mc);
            return;
        }
        // Ignore dummy/preview entities rendered inside GUIs (JEI, Better Stats, JER)
        if (boss == null || boss.level() != mc.level || boss.getId() <= 0 || mc.level.getEntity(boss.getId()) != boss) {
            return;
        }

        if (activeLevel != null && activeLevel != mc.level) {
            stopAll(mc);
        }

        pruneStopped();

        double distSq = mc.player.distanceToSqr(boss);
        boolean shouldPlay = bossActive && boss.isAlive() && !boss.isRemoved() && distSq <= START_DIST_SQ;

        // Case 1: This boss already owns the music channel
        if (activeBoss == boss) {
            if (!shouldPlay || distSq > STOP_DIST_SQ) {
                fadeOutCurrent();
                return;
            }
            if (activeTrack != null && !activeTrack.isStopped()) {
                activeTrack.cancelFadeOut();
                activeTrack.setKeepAlive(makeKeepAlive(mc, boss, liveActiveCheck));
                if (mc.getSoundManager().isActive(activeTrack)
                        || System.currentTimeMillis() - lastPlayRequestMs < START_DEBOUNCE_MS) {
                    return;
                }
            }
            if (System.currentTimeMillis() - lastPlayRequestMs < START_DEBOUNCE_MS) {
                return;
            }
            startTrack(mc, boss, soundId, liveActiveCheck);
            return;
        }

        if (!shouldPlay) return;

        // Case 2: Another boss currently owns the music channel
        if (activeBoss != null && activeTrack != null && !activeTrack.isStopped() && !activeTrack.isFadingOut()) {
            boolean ownerValid = activeBoss.isAlive() && !activeBoss.isRemoved()
                    && activeBoss.level() == mc.level;
            double ownerDistSq = ownerValid ? mc.player.distanceToSqr(activeBoss) : Double.MAX_VALUE;
            boolean ownerInRange = ownerValid && ownerDistSq <= STOP_DIST_SQ;

            if (ownerInRange) {
                if (soundId.equals(activeSoundId)) {
                    if (distSq < ownerDistSq) {
                        activeBoss = boss;
                        activeTrack.setKeepAlive(makeKeepAlive(mc, boss, liveActiveCheck));
                    }
                    return;
                }

                double ownerDist = Math.sqrt(ownerDistSq);
                double challengerDist = Math.sqrt(distSq);
                if (challengerDist + TAKEOVER_MARGIN_BLOCKS >= ownerDist) {
                    return;
                }
                if (System.currentTimeMillis() - lastSwitchMs < SWITCH_COOLDOWN_MS) {
                    return;
                }
            }
        }

        if (System.currentTimeMillis() - lastPlayRequestMs < START_DEBOUNCE_MS) {
            return;
        }

        lastSwitchMs = System.currentTimeMillis();
        startTrack(mc, boss, soundId, liveActiveCheck);
    }

    private static BooleanSupplier makeKeepAlive(Minecraft mc, LivingEntity boss, BooleanSupplier liveActiveCheck) {
        return () -> {
            if (mc.player == null || mc.level == null) return false;
            if (boss.level() != mc.level) return false;
            if (!boss.isAlive() || boss.isRemoved()) return false;
            if (mc.player.distanceToSqr(boss) > STOP_DIST_SQ) return false;
            return liveActiveCheck == null || liveActiveCheck.getAsBoolean();
        };
    }

    private static void startTrack(Minecraft mc, LivingEntity boss, ResourceLocation soundId, BooleanSupplier liveActiveCheck) {
        stopAll(mc);

        SoundEvent ev = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (ev == null) {
            ev = SoundEvent.createVariableRangeEvent(soundId);
        }
        mc.getMusicManager().stopPlaying();

        BossMusicInstance inst = new BossMusicInstance(ev, makeKeepAlive(mc, boss, liveActiveCheck));
        activeBoss = boss;
        activeTrack = inst;
        activeSoundId = soundId;
        activeLevel = mc.level;
        lastPlayRequestMs = System.currentTimeMillis();
        TRACKED_INSTANCES.add(inst);
        mc.getSoundManager().play(inst);
    }

    /** Smoothly fades out the currently playing boss theme instead of abruptly cutting it off. */
    public static void fadeOutCurrent() {
        if (activeTrack != null) {
            activeTrack.beginFadeOut();
        }
        activeBoss = null;
        activeTrack = null;
        activeSoundId = null;
    }

    public static void stopAll(Minecraft mc) {
        if (activeSoundId != null) {
            mc.getSoundManager().stop(activeSoundId, SoundSource.RECORDS);
        }
        for (BossMusicInstance inst : TRACKED_INSTANCES) {
            inst.forceStop();
            mc.getSoundManager().stop(inst);
        }
        TRACKED_INSTANCES.clear();
        activeTrack = null;
        activeBoss = null;
        activeSoundId = null;
        activeLevel = null;
    }

    private static void pruneStopped() {
        Iterator<BossMusicInstance> it = TRACKED_INSTANCES.iterator();
        while (it.hasNext()) {
            BossMusicInstance inst = it.next();
            if (inst.isStopped() && inst != activeTrack) {
                it.remove();
            }
        }
    }

    private static final class BossMusicInstance extends AbstractTickableSoundInstance {
        private volatile BooleanSupplier keepAlive;
        private volatile boolean fadingOut = false;

        BossMusicInstance(SoundEvent event, BooleanSupplier keepAlive) {
            super(event, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
            this.keepAlive = keepAlive;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.08F; // Smooth fade-in from near-silent
            this.pitch = 1.0F;
            this.relative = true;
            this.attenuation = Attenuation.NONE;
            this.x = 0.0D;
            this.y = 0.0D;
            this.z = 0.0D;
        }

        void setKeepAlive(BooleanSupplier keepAlive) {
            this.keepAlive = keepAlive;
        }

        void beginFadeOut() {
            this.fadingOut = true;
        }

        void cancelFadeOut() {
            this.fadingOut = false;
        }

        boolean isFadingOut() {
            return this.fadingOut;
        }

        void forceStop() {
            this.fadingOut = true;
            this.volume = 0.0F;
            this.stop();
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            if (!fadingOut && (activeTrack != this || !keepAlive.getAsBoolean())) {
                fadingOut = true;
                if (activeTrack == this) {
                    activeTrack = null;
                    activeBoss = null;
                    activeSoundId = null;
                }
            }
            if (fadingOut) {
                this.volume = Math.max(0.0F, this.volume - FADE_OUT_STEP);
                if (this.volume <= 0.005F) {
                    this.stop();
                }
            } else if (this.volume < TARGET_VOLUME) {
                this.volume = Math.min(TARGET_VOLUME, this.volume + FADE_IN_STEP);
            }
        }
    }
}
