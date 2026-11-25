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
 * <p>Guarantees that at most ONE boss music track can ever play at once, even when multiple
 * bosses are active simultaneously or during asynchronous OGG stream startup:
 * <ul>
 *   <li><b>Self-Terminating Ownership Check:</b> Every {@link BossMusicInstance} checks
 *       {@code activeTrack == this} every tick and stops itself immediately if superseded.</li>
 *   <li><b>Unconditional Cleanup:</b> {@link #startTrack} always stops every previously tracked
 *       instance and clears the {@link SoundSource#RECORDS} channel for the previous sound ID
 *       before queuing a new stream.</li>
 *   <li><b>Same-Theme Continuity:</b> If another boss using the same theme becomes closer
 *       (e.g., two Ruin Guards), ownership transfers seamlessly without restarting the track.</li>
 *   <li><b>Takeover Hysteresis:</b> A competing boss with a different theme must be at least
 *       {@code TAKEOVER_MARGIN_BLOCKS} closer and respect a {@code SWITCH_COOLDOWN_MS} debounce
 *       before taking over, preventing rapid ping-ponging.</li>
 * </ul>
 */
public final class BossMusicManager {
    private static final double START_DIST_SQ = 48.0D * 48.0D;
    private static final double STOP_DIST_SQ  = 56.0D * 56.0D;
    private static final double TAKEOVER_MARGIN_BLOCKS = 6.0D;
    private static final long START_DEBOUNCE_MS  = 1500L;
    private static final long SWITCH_COOLDOWN_MS = 3000L;

    private static BossMusicInstance activeTrack = null;
    private static LivingEntity activeBoss = null;
    private static ResourceLocation activeSoundId = null;
    private static Level activeLevel = null;
    private static long lastPlayRequestMs = 0L;
    private static long lastSwitchMs = 0L;

    /** Tracks all started instances until fully stopped so async-loading streams can never leak or overlap. */
    private static final Set<BossMusicInstance> TRACKED_INSTANCES = new HashSet<>();

    private BossMusicManager() {}

    public static void update(LivingEntity boss, ResourceLocation soundId, boolean bossActive) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            stopAll(mc);
            return;
        }

        // Clean up immediately if the player changed dimensions / worlds
        if (activeLevel != null && activeLevel != mc.level) {
            stopAll(mc);
        }

        // Prune already-stopped instances from tracking set
        pruneStopped();

        double distSq = mc.player.distanceToSqr(boss);
        boolean shouldPlay = bossActive && boss.isAlive() && !boss.isRemoved() && distSq <= START_DIST_SQ;

        // Case 1: This boss already owns the music channel
        if (activeBoss == boss) {
            if (!shouldPlay || distSq > STOP_DIST_SQ) {
                stopAll(mc);
                return;
            }
            if (activeTrack != null && !activeTrack.isStopped()) {
                // Still playing or asynchronously starting up — never re-queue!
                if (mc.getSoundManager().isActive(activeTrack)
                        || System.currentTimeMillis() - lastPlayRequestMs < START_DEBOUNCE_MS) {
                    return;
                }
            }
            if (System.currentTimeMillis() - lastPlayRequestMs < START_DEBOUNCE_MS) {
                return;
            }
            startTrack(mc, boss, soundId);
            return;
        }

        if (!shouldPlay) return;

        // Case 2: Another boss currently owns the music channel
        if (activeBoss != null && activeTrack != null && !activeTrack.isStopped()) {
            boolean ownerValid = activeBoss.isAlive() && !activeBoss.isRemoved()
                    && activeBoss.level() == mc.level;
            double ownerDistSq = ownerValid ? mc.player.distanceToSqr(activeBoss) : Double.MAX_VALUE;
            boolean ownerInRange = ownerValid && ownerDistSq <= STOP_DIST_SQ;

            if (ownerInRange) {
                // Same theme (e.g., two Ruin Guards): transfer ownership cleanly without restarting the song!
                if (soundId.equals(activeSoundId)) {
                    if (distSq < ownerDistSq) {
                        activeBoss = boss;
                        activeTrack.setKeepAlive(makeKeepAlive(mc, boss));
                    }
                    return;
                }

                // Different theme: require clear distance margin + switch cooldown to prevent ping-ponging
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
        startTrack(mc, boss, soundId);
    }

    private static BooleanSupplier makeKeepAlive(Minecraft mc, LivingEntity boss) {
        return () -> {
            if (mc.player == null || mc.level == null) return false;
            if (boss.level() != mc.level) return false;
            return boss.isAlive() && !boss.isRemoved()
                    && mc.player.distanceToSqr(boss) <= STOP_DIST_SQ;
        };
    }

    private static void startTrack(Minecraft mc, LivingEntity boss, ResourceLocation soundId) {
        // Always stop any existing or async-loading boss tracks first so zero overlap is possible
        stopAll(mc);

        SoundEvent ev = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (ev == null) {
            ev = SoundEvent.createVariableRangeEvent(soundId);
        }
        mc.getMusicManager().stopPlaying();

        BossMusicInstance inst = new BossMusicInstance(ev, makeKeepAlive(mc, boss));
        activeBoss = boss;
        activeTrack = inst;
        activeSoundId = soundId;
        activeLevel = mc.level;
        lastPlayRequestMs = System.currentTimeMillis();
        TRACKED_INSTANCES.add(inst);
        mc.getSoundManager().play(inst);
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

        BossMusicInstance(SoundEvent event, BooleanSupplier keepAlive) {
            super(event, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
            this.keepAlive = keepAlive;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.85F;
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

        void forceStop() {
            this.stop();
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            // Hard single-track invariant: any superseded instance self-terminates on its very first tick
            if (activeTrack != this || !keepAlive.getAsBoolean()) {
                this.stop();
            }
        }
    }
}
