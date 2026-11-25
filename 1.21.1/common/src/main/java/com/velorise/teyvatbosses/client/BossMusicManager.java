package com.velorise.teyvatbosses.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BooleanSupplier;

/**
 * Unified client-side boss music controller for all Velorise: Teyvat Bosses.
 * Plays looping boss themes anywhere in the world (inside arenas OR spawned outdoors)
 * whenever an awakened boss is within 48 blocks of the player.
 *
 * <p>Uses {@link SoundSource#RECORDS} + {@link Attenuation#NONE} + {@code relative = true}
 * (matching L_Ender's Cataclysm conventions) and pauses vanilla background music cleanly.
 */
public final class BossMusicManager {
    private static final double START_DIST_SQ = 48.0D * 48.0D;
    private static final double STOP_DIST_SQ  = 56.0D * 56.0D;

    private static BossMusicInstance activeTrack = null;
    private static LivingEntity activeBoss = null;
    private static long lastPlayRequestMs = 0L;

    private BossMusicManager() {}

    public static void update(LivingEntity boss, ResourceLocation soundId, boolean bossActive) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        double distSq = mc.player.distanceToSqr(boss);
        boolean shouldPlay = bossActive && boss.isAlive() && !boss.isRemoved() && distSq <= START_DIST_SQ;

        if (activeBoss == boss) {
            if (!shouldPlay || distSq > STOP_DIST_SQ) {
                stopActive(mc);
                return;
            }
            if (activeTrack != null && mc.getSoundManager().isActive(activeTrack)) {
                return;
            }
            // Wait at least 1.5s between play requests so async OGG stream startup is never spammed
            if (System.currentTimeMillis() - lastPlayRequestMs < 1500L) {
                return;
            }
            startTrack(mc, boss, soundId, () -> boss.isAlive() && !boss.isRemoved()
                    && mc.player != null && mc.player.distanceToSqr(boss) <= STOP_DIST_SQ);
            return;
        }

        if (!shouldPlay) return;

        // If another boss track is active, only take over if that boss is gone/farther
        if (activeBoss != null && activeTrack != null && mc.getSoundManager().isActive(activeTrack)) {
            if (activeBoss.isAlive() && !activeBoss.isRemoved()
                    && mc.player.distanceToSqr(activeBoss) < distSq) {
                return;
            }
            stopActive(mc);
        }

        if (System.currentTimeMillis() - lastPlayRequestMs < 1500L) {
            return;
        }
        startTrack(mc, boss, soundId, () -> boss.isAlive() && !boss.isRemoved()
                && mc.player != null && mc.player.distanceToSqr(boss) <= STOP_DIST_SQ);
    }

    private static void startTrack(Minecraft mc, LivingEntity boss, ResourceLocation soundId, BooleanSupplier keepAlive) {
        SoundEvent ev = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (ev == null) {
            ev = SoundEvent.createVariableRangeEvent(soundId);
        }
        mc.getMusicManager().stopPlaying();
        BossMusicInstance inst = new BossMusicInstance(ev, keepAlive);
        activeBoss = boss;
        activeTrack = inst;
        lastPlayRequestMs = System.currentTimeMillis();
        mc.getSoundManager().play(inst);
    }

    private static void stopActive(Minecraft mc) {
        if (activeTrack != null) {
            mc.getSoundManager().stop(activeTrack);
            activeTrack = null;
        }
        activeBoss = null;
    }

    private static final class BossMusicInstance extends AbstractTickableSoundInstance {
        private final BooleanSupplier keepAlive;

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

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            if (!keepAlive.getAsBoolean()) {
                this.stop();
            }
        }
    }
}
