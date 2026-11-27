package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * 60/144 FPS procedural + phase-keyframed animation driver for Anemo Hypostasis (Beth).
 * Every combat clip implements Anticipation -> Hold (with micro-motion) -> Snap -> Hitstop -> Recovery,
 * matching the exact formations observed in Genshin Impact gameplay.
 */
public final class AnemoHypostasisAnimations {

    private static final float[][] DORMANT_OFFSETS = {
            {-9.0F, -9.0F, -9.0F}, { 9.0F, -9.0F, -9.0F},
            {-9.0F, -9.0F,  9.0F}, { 9.0F, -9.0F,  9.0F},
            {-9.0F,  9.0F, -9.0F}, { 9.0F,  9.0F, -9.0F},
            {-9.0F,  9.0F,  9.0F}, { 9.0F,  9.0F,  9.0F},
    };

    private AnemoHypostasisAnimations() {}

    private static float smoothstep(float t) {
        float c = Mth.clamp(t, 0.0F, 1.0F);
        return c * c * (3.0F - 2.0F * c);
    }

    public static void apply(AnemoHypostasisModel<?> model, String clip, float sec, float liveSec) {
        model.resetPose();
        ModelPart body = model.getBone("body_pivot");
        ModelPart core = model.getBone("core_group");
        ModelPart frame = model.getBone("core_frame");
        ModelPart ring = model.getBone("shell_ring");

        // Continuous subtle core rotation & mini-cube orbit
        if (core != null) {
            core.yRot = liveSec * 1.4F;
            core.xRot = 0.7854F + Mth.sin(liveSec * 1.9F) * 0.14F;
        }
        if (frame != null) {
            frame.yRot = -liveSec * 0.9F;
            frame.zRot = Mth.sin(liveSec * 1.1F) * 0.22F;
            frame.xRot = Mth.cos(liveSec * 0.8F) * 0.18F;
        }
        for (int i = 0; i < 4; i++) {
            ModelPart m = model.getBone("mini_" + i);
            if (m != null) {
                float a = liveSec * 1.8F + i * (Mth.PI * 0.5F);
                float r = "dormant".equals(clip) ? 14.0F : 25.0F;
                m.x = Mth.cos(a) * r;
                m.z = Mth.sin(a) * r;
                m.y = Mth.sin(liveSec * 2.4F + i * 1.4F) * 6.0F;
                m.xRot = liveSec * 2.1F + i;
                m.yRot = liveSec * 1.7F;
                if ("dormant".equals(clip) || "revival".equals(clip) || "death".equals(clip)) {
                    m.xScale = m.yScale = m.zScale = 0.0F;
                }
            }
        }

        switch (clip) {
            case "dormant" -> applyDormant(body, core, frame, ring, model, liveSec);
            case "awaken" -> applyAwaken(body, core, frame, ring, model, sec);
            case "vulnerable" -> applyVulnerable(body, core, frame, ring, model, sec, liveSec);
            case "butterfly_sweep" -> applyButterfly(body, core, frame, ring, model, sec);
            case "missile_circle" -> applyMissileCircle(body, core, frame, ring, model, sec);
            case "pillar_eruption" -> applyPillarEruption(body, core, frame, ring, model, sec);
            case "tornado_storm" -> applyTornadoStorm(body, core, frame, ring, model, sec);
            case "clap_slam" -> applyClapSlam(body, core, frame, ring, model, sec);
            case "vacuum_implosion" -> applyVacuumImplosion(body, core, frame, ring, model, sec);
            case "revival" -> applyRevival(body, core, frame, ring, model, liveSec);
            case "death" -> applyDeath(body, core, frame, ring, model, sec);
            default -> applyIdle(body, core, frame, ring, model, liveSec);
        }
    }

    private static void applyDormant(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float liveSec) {
        body.y = -18.5F + Mth.sin(liveSec * 1.6F) * 1.2F;
        if (frame != null) { frame.xScale = frame.yScale = frame.zScale = 0.0F; }
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            s.x = DORMANT_OFFSETS[i][0];
            s.y = DORMANT_OFFSETS[i][1];
            s.z = DORMANT_OFFSETS[i][2];
            s.xRot = s.yRot = s.zRot = 0.0F;
        }
    }

    private static void applyAwaken(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float p = smoothstep(sec / 2.2F);
        body.y = Mth.lerp(p, -18.5F, -32.0F);
        ring.yRot = p * Mth.TWO_PI * 0.75F;
        float spread = 1.0F + 1.15F * Mth.sin(p * Mth.PI * 0.85F);
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            s.x = DORMANT_OFFSETS[i][0] * spread;
            s.y = DORMANT_OFFSETS[i][1] * spread;
            s.z = DORMANT_OFFSETS[i][2] * spread;
            s.xRot = p * 0.9F * ((i % 2 == 0) ? 1.0F : -1.0F);
            s.zRot = p * 0.7F * ((i < 4) ? 1.0F : -1.0F);
        }
        float pulse = 1.0F + 0.35F * Mth.sin(p * Mth.PI);
        core.xScale = core.yScale = core.zScale = pulse;
    }

    private static void applyIdle(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float liveSec) {
        body.y = -32.0F + Mth.sin(liveSec * 2.1F) * 2.8F;
        ring.yRot = liveSec * 0.75F;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float ang = (i * Mth.PI * 0.25F) + liveSec * 0.35F;
            float rad = 18.5F + Mth.sin(liveSec * 2.0F + i * 0.9F) * 1.8F;
            float yOff = ((i % 2 == 0) ? -9.5F : 9.5F) + Mth.cos(liveSec * 1.8F + i) * 2.2F;
            s.x = Mth.cos(ang) * rad;
            s.z = Mth.sin(ang) * rad;
            s.y = yOff;
            s.xRot = liveSec * 0.6F + i * 0.5F;
            s.zRot = Mth.sin(liveSec + i) * 0.45F;
            s.xScale = s.yScale = s.zScale = 0.88F;
        }
    }

    private static void applyVulnerable(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec, float liveSec) {
        float enter = smoothstep(sec / 0.45F);
        body.y = Mth.lerp(enter, -28.0F, -13.5F) + Mth.sin(liveSec * 3.0F) * 1.4F;
        float corePulse = 1.18F + 0.14F * Mth.sin(liveSec * 6.0F);
        core.xScale = core.yScale = core.zScale = corePulse;
        if (frame != null) {
            frame.xScale = frame.yScale = frame.zScale = 1.15F;
        }
        // Shell cubes scatter low and shrink cleanly out of the way so core is clearly exposed
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float ang = i * (Mth.PI * 0.25F);
            float dist = Mth.lerp(enter, 18.0F, 34.0F);
            s.x = Mth.cos(ang) * dist;
            s.z = Mth.sin(ang) * dist;
            s.y = Mth.lerp(enter, 0.0F, 6.0F);
            float sc = Mth.lerp(enter, 0.85F, 0.28F);
            s.xScale = s.yScale = s.zScale = sc;
            s.xRot = 0.5F + i;
            s.zRot = 0.4F * i;
        }
    }

    // Attack 1: Butterfly / Winged Gust Sweep (4.5s = 90 ticks)
    // Attack 1: Butterfly / Winged Gust Sweep (4.5s = 90 ticks) — Snappy whip flaps + recoil kickback
    private static void applyButterfly(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float rise = smoothstep(sec / 0.85F);
        float endFade = sec > 3.8F ? smoothstep((4.5F - sec) / 0.7F) : 1.0F;
        float recoilZ = 0.0F;
        float flap = 0.0F;
        if (sec >= 0.9F && sec <= 3.75F) {
            float cycle = ((sec - 0.9F) / 0.95F) % 1.0F;
            // Deep anticipation pull-back -> ultra-fast 0.13s whip forward -> recoil follow-through
            if (cycle < 0.44F) {
                float p = smoothstep(cycle / 0.44F);
                flap = -0.95F * p;
                recoilZ = -2.2F * p;
            } else if (cycle < 0.58F) {
                float p = (cycle - 0.44F) / 0.14F;
                flap = Mth.lerp(p * p * p, -0.95F, 1.15F);
                recoilZ = Mth.lerp(p * p, -2.2F, 5.2F);
            } else {
                float p = smoothstep((cycle - 0.58F) / 0.42F);
                flap = Mth.lerp(p, 1.15F, 0.0F);
                recoilZ = Mth.lerp(p, 5.2F, 0.0F);
            }
        }
        body.y = Mth.lerp(rise * endFade, -28.0F, -52.0F);
        body.z = recoilZ;

        float[][] wingBase = {
                {-22.0F, -14.0F, -3.0F}, {-36.0F, -6.0F, -1.0F}, {-31.0F, 8.0F, 2.0F}, {-18.0F, 12.0F, 4.0F},
                { 22.0F, -14.0F, -3.0F}, { 36.0F, -6.0F, -1.0F}, { 31.0F, 8.0F, 2.0F}, { 18.0F, 12.0F, 4.0F}
        };
        float stretch = 0.92F + Math.max(0.0F, flap) * 0.14F;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float side = (i < 4) ? -1.0F : 1.0F;
            float wx = wingBase[i][0];
            float wy = wingBase[i][1];
            float wz = wingBase[i][2];
            float cosF = Mth.cos(flap * side);
            float sinF = Mth.sin(flap * side);
            s.x = wx * cosF;
            s.z = wz - Math.abs(wx) * sinF * 0.62F;
            s.y = wy + flap * 4.8F;
            s.yRot = -flap * side * 0.85F;
            s.zRot = side * (0.28F + flap * 0.28F);
            s.xScale = stretch;
            s.yScale = 0.92F;
            s.zScale = 0.92F;
        }
    }

    // Attack 2: Magic Circle Missile Volley (7.0s = 140 ticks)
    private static void applyMissileCircle(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float enter = smoothstep(sec / 0.9F);
        body.y = Mth.lerp(enter, -28.0F, -48.0F) + Mth.sin(sec * 2.5F) * 1.5F;
        float spin = sec * 1.35F;
        float rad = Mth.lerp(enter, 18.0F, 30.0F);
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F) + spin;
            s.x = Mth.cos(a) * rad;
            s.y = Mth.sin(a) * rad;
            s.z = -10.0F + Mth.sin(sec * 6.0F + i) * 1.2F;
            s.zRot = a;
            s.xScale = s.yScale = s.zScale = 0.82F;
        }
        core.xScale = core.yScale = core.zScale = 1.15F + 0.12F * Mth.sin(sec * 9.0F);
    }

    // Attack 3: Ground Wind Pillar Eruptions (5.0s = 100 ticks)
    // Attack 3: Ground Wind Pillar Eruptions (5.0s = 100 ticks) — 3 Hydraulic Ground-Slam Beats!
    private static void applyPillarEruption(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        // 3 rhythmic hydraulic rises -> brutal downward ground slams @ ~1.0s, 2.35s, 3.70s
        float beatPeriod = 1.35F;
        float phase = (sec % beatPeriod) / beatPeriod;
        float liftY;
        float rad;
        float xzScale = 0.86F;
        float yScale = 0.86F;
        if (phase < 0.62F) {
            // Anticipation: rise high & gather shells overhead
            float p = smoothstep(phase / 0.62F);
            liftY = Mth.lerp(p, -22.0F, -37.0F);
            rad = Mth.lerp(p, 21.0F, 15.5F);
            xzScale = Mth.lerp(p, 0.86F, 0.80F);
            yScale = Mth.lerp(p, 0.86F, 1.05F);
        } else if (phase < 0.74F) {
            // Explosive downward hydraulic slam!
            float p = (phase - 0.62F) / 0.12F;
            float snap = p * p * p;
            liftY = Mth.lerp(snap, -37.0F, -14.5F);
            rad = Mth.lerp(snap, 15.5F, 30.0F);
            xzScale = Mth.lerp(snap, 0.80F, 1.28F);
            yScale = Mth.lerp(snap, 1.05F, 0.68F);
        } else {
            // Impact shudder & rebound back to neutral
            float p = smoothstep((phase - 0.74F) / 0.26F);
            float shudder = (1.0F - p) * Mth.sin(sec * 75.0F) * 1.4F;
            liftY = Mth.lerp(p, -14.5F, -22.0F) + shudder;
            rad = Mth.lerp(p, 30.0F, 21.0F);
            xzScale = Mth.lerp(p, 1.28F, 0.86F);
            yScale = Mth.lerp(p, 0.68F, 0.86F);
        }
        body.y = liftY;
        ring.yRot = sec * 2.8F;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F);
            s.x = Mth.cos(a) * rad;
            s.z = Mth.sin(a) * rad;
            s.y = ((i % 2 == 0) ? -5.0F : 5.0F);
            s.xRot = (phase >= 0.62F && phase < 0.82F) ? 0.15F : 0.45F;
            s.xScale = xzScale;
            s.yScale = yScale;
            s.zScale = xzScale;
        }
    }

    // Attack 4: Hurricane Tornadoes + Anemo Orbs (14.0s = 280 ticks)
    private static void applyTornadoStorm(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float rise = smoothstep(sec / 1.5F);
        float drop = sec > 12.5F ? smoothstep((14.0F - sec) / 1.5F) : 1.0F;
        body.y = Mth.lerp(rise * drop, -28.0F, -74.0F);
        ring.yRot = sec * 4.2F;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float tier = (i < 4) ? -14.0F : 12.0F;
            float rad = (i < 4) ? 26.0F : 19.0F;
            float a = (i % 4) * (Mth.PI * 0.5F) + ((i < 4) ? 0.0F : 0.4F);
            s.x = Mth.cos(a) * rad;
            s.z = Mth.sin(a) * rad;
            s.y = tier + Mth.sin(sec * 5.0F + i) * 2.5F;
            s.xRot = sec * 2.0F + i;
            s.zRot = sec * 1.5F;
            s.xScale = s.yScale = s.zScale = 0.86F;
        }
    }

    // Attack 5: Crushing Box Clap Slam (4.5s = 90 ticks)
    // Windup/Hold (0..1.75s): 2 huge walls spread wide at X=±36 with intense vibration
    // Snap (1.75..2.0s): Slam violently to X=±10!
    // Hitstop (2.0..2.35s) -> Shatter outward (2.35..4.5s)
    // Attack 5: Crushing Box Clap Slam (4.5s = 90 ticks) — Heavy Sakuga Anticipation -> 3-Frame Cubic Crush -> Squash & Shudder!
    private static void applyClapSlam(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float wallX;
        float sx, syz;
        float tiltZ = 0.0F;
        float vib = 0.0F;
        float bodyY = -25.0F;

        if (sec < 1.10F) {
            // Wide cock-back: two massive stone walls spread out to +-44 and cant outward like giant palms
            float p = smoothstep(sec / 1.10F);
            wallX = Mth.lerp(p, 14.0F, 44.0F);
            sx = Mth.lerp(p, 0.92F, 1.26F);
            syz = Mth.lerp(p, 0.92F, 1.38F);
            tiltZ = p * 0.22F;
            bodyY = Mth.lerp(p, -24.0F, -29.0F);
        } else if (sec < 1.60F) {
            // Escalating high-tension tremble while holding wide
            float tension = (sec - 1.10F) / 0.50F;
            wallX = 44.0F + Mth.sin(sec * 56.0F) * (1.2F + tension * 1.1F);
            sx = 1.28F;
            syz = 1.40F;
            tiltZ = 0.22F + Mth.sin(sec * 64.0F) * 0.05F;
            vib = Mth.sin(sec * 60.0F) * (0.04F + tension * 0.05F);
            bodyY = -29.0F + Mth.sin(sec * 48.0F) * 0.9F;
            core.xScale = core.yScale = core.zScale = 1.25F + tension * 0.20F;
        } else if (sec < 1.72F) {
            // Final anticipation pop-out (+4.5F) right before the explosive crush!
            float p = smoothstep((sec - 1.60F) / 0.12F);
            wallX = Mth.lerp(p, 44.0F, 48.5F);
            sx = 1.22F;
            syz = 1.44F;
            tiltZ = 0.28F;
            bodyY = -30.5F;
            core.xScale = core.yScale = core.zScale = 1.45F;
        } else if (sec < 1.86F) {
            // BRUTAL 3-FRAME CUBIC SNAP CRUSH! (48.5 -> 7.6 in 0.14s)
            float p = (sec - 1.72F) / 0.14F;
            float snap = p * p * p;
            wallX = Mth.lerp(snap, 48.5F, 7.6F);
            sx = Mth.lerp(snap, 1.35F, 0.76F);
            syz = Mth.lerp(snap, 1.35F, 1.60F);
            tiltZ = Mth.lerp(snap, 0.28F, -0.06F);
            bodyY = Mth.lerp(snap, -30.5F, -18.5F);
        } else if (sec < 2.34F) {
            // IMPACT HITSTOP + SQUASH-AND-STRETCH + DAMPED SHUDDER + MICRO-REBOUND!
            float dt = sec - 1.86F;
            float decay = (float) Math.exp(-dt * 7.5D);
            float ringOsc = Mth.sin(dt * 95.0F) * decay;
            float rebound = (dt < 0.14F) ? Mth.sin((dt / 0.14F) * Mth.PI) * 2.2F : 0.0F;
            wallX = 7.6F + rebound + ringOsc * 1.4F;
            sx = 0.78F + (1.0F - decay) * 0.42F;
            syz = 1.58F - (1.0F - decay) * 0.22F;
            vib = ringOsc * 0.11F;
            bodyY = -18.5F + ringOsc * 2.0F;
            core.xScale = core.yScale = core.zScale = 1.0F + decay * 0.55F;
        } else {
            // Explosive shatter-release outward with tumble recoil
            float p = smoothstep((sec - 2.34F) / 1.45F);
            wallX = Mth.lerp(p, 8.8F, 32.0F);
            sx = Mth.lerp(p, 1.20F, 0.68F);
            syz = Mth.lerp(p, 1.34F, 0.68F);
            tiltZ = Mth.sin(p * Mth.PI) * -0.35F;
            bodyY = Mth.lerp(p, -19.5F, -26.0F);
        }

        body.y = bodyY;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float side = (i % 2 == 0) ? -1.0F : 1.0F;
            float yOff = (i < 4) ? -11.5F : 11.5F;
            float zOff = ((i / 2) % 2 == 0) ? -11.5F : 11.5F;
            s.x = side * wallX;
            s.y = yOff;
            s.z = zOff;
            s.xRot = vib;
            s.zRot = side * tiltZ + vib * side;
            s.xScale = sx;
            s.yScale = syz;
            s.zScale = syz;
        }
    }

    // Attack 6: Vacuum Pull & Shockwave Implosion (6.5s = 130 ticks)
    // Attack 6: Vacuum Pull & Meteoric Shockwave Detonation (6.5s = 130 ticks)
    private static void applyVacuumImplosion(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float rad;
        float sc = 0.90F;
        float bodyY;
        float tilt = 0.0F;
        if (sec < 3.15F) {
            // Pull phase: body rises while cubes spiral tightly inward with accelerating vortex spin
            float p = smoothstep(sec / 3.15F);
            bodyY = Mth.lerp(p, -26.0F, -35.0F);
            rad = Mth.lerp(p, 29.0F, 12.0F) + Mth.sin(sec * 24.0F) * 0.9F;
            ring.yRot = sec * (2.4F + p * 5.2F);
            sc = Mth.lerp(p, 0.86F, 1.05F);
            tilt = p * 0.45F;
        } else if (sec < 3.38F) {
            // Hyper-dense compression pinch right before detonation!
            float p = smoothstep((sec - 3.15F) / 0.23F);
            bodyY = Mth.lerp(p, -35.0F, -39.5F) + Mth.sin(sec * 75.0F) * 1.1F;
            rad = Mth.lerp(p, 12.0F, 8.2F) + Mth.sin(sec * 82.0F) * 0.8F;
            ring.yRot = sec * 8.2F;
            sc = Mth.lerp(p, 1.05F, 1.18F);
            tilt = 0.65F;
            core.xScale = core.yScale = core.zScale = 1.48F;
        } else if (sec < 3.60F) {
            // METEORIC DOWNWARD SLAM + EXPLOSIVE OUTWARD SHOCKWAVE BLAST! (0.22s snap)
            float p = (sec - 3.38F) / 0.22F;
            float snap = p * p * p;
            bodyY = Mth.lerp(snap, -39.5F, -16.5F);
            rad = Mth.lerp(snap, 8.2F, 48.0F);
            ring.yRot = sec * 9.5F;
            sc = Mth.lerp(snap, 1.18F, 1.42F);
            tilt = Mth.lerp(snap, 0.65F, -0.55F);
            core.xScale = core.yScale = core.zScale = 1.60F;
        } else {
            // Impact shudder & recovery
            float p = smoothstep((sec - 3.60F) / 2.20F);
            float decay = (float) Math.exp(-(sec - 3.60F) * 6.5D);
            float shudder = Mth.sin((sec - 3.60F) * 85.0F) * decay * 2.2F;
            bodyY = Mth.lerp(p, -16.5F, -26.0F) + shudder;
            rad = Mth.lerp(p, 48.0F, 22.0F);
            ring.yRot = sec * 2.4F;
            sc = Mth.lerp(p, 1.42F, 0.85F);
            tilt = Mth.lerp(p, -0.55F, 0.0F);
        }
        body.y = bodyY;
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F);
            s.x = Mth.cos(a) * rad;
            s.z = Mth.sin(a) * rad;
            s.y = ((i % 2 == 0) ? -7.0F : 7.0F);
            s.xRot = tilt * ((i % 2 == 0) ? 1.0F : -1.0F);
            s.zRot = tilt * 0.6F;
            s.xScale = s.yScale = s.zScale = sc;
        }
    }

    // Revival Phase: Pure wireframe crystal hovering at center while 4 Wind Crystals orbit arena
    private static void applyRevival(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float liveSec) {
        body.y = -36.0F + Mth.sin(liveSec * 2.6F) * 3.0F;
        float pulse = 1.25F + 0.18F * Mth.sin(liveSec * 5.0F);
        core.xScale = core.yScale = core.zScale = pulse;
        if (frame != null) {
            frame.xScale = frame.yScale = frame.zScale = 1.65F;
            frame.yRot = liveSec * 2.2F;
            frame.xRot = liveSec * 1.4F;
        }
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            s.xScale = s.yScale = s.zScale = 0.0F;
            s.visible = false;
        }
    }

    private static void applyDeath(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float p = smoothstep(sec / 1.4F);
        body.y = Mth.lerp(p, -24.0F, -18.0F);
        float coreScale = Math.max(0.0F, 1.0F - p);
        core.xScale = core.yScale = core.zScale = coreScale * (1.0F + 0.8F * Mth.sin(p * Mth.PI));
        if (frame != null) {
            frame.xScale = frame.yScale = frame.zScale = coreScale * 1.5F;
        }
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F);
            s.x = Mth.cos(a) * (16.0F + p * 36.0F);
            s.z = Mth.sin(a) * (16.0F + p * 36.0F);
            s.y = -p * 12.0F;
            s.xRot = p * 4.0F + i;
            s.zRot = p * 3.0F;
            s.xScale = s.yScale = s.zScale = Math.max(0.0F, 0.85F * (1.0F - p));
        }
    }
}
