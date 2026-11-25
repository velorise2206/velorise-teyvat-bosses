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
    private static void applyButterfly(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        float rise = smoothstep(sec / 0.85F);
        float endFade = sec > 3.8F ? smoothstep((4.5F - sec) / 0.7F) : 1.0F;
        body.y = Mth.lerp(rise * endFade, -28.0F, -52.0F);

        // 3 powerful wing flaps during sec = 1.0 .. 3.6
        float flap = 0.0F;
        if (sec >= 0.9F && sec <= 3.75F) {
            float cycle = ((sec - 0.9F) / 0.95F) % 1.0F;
            // Anticipation (pull wings back) -> Snap forward -> Follow-through
            if (cycle < 0.42F) {
                flap = -0.65F * smoothstep(cycle / 0.42F);
            } else if (cycle < 0.62F) {
                flap = Mth.lerp((cycle - 0.42F) / 0.20F, -0.65F, 0.85F);
            } else {
                flap = Mth.lerp(smoothstep((cycle - 0.62F) / 0.38F), 0.85F, 0.0F);
            }
        }

        // Left wing (0..3) and Right wing (4..7)
        float[][] wingBase = {
                {-22.0F, -14.0F, -3.0F}, {-35.0F, -6.0F, -1.0F}, {-30.0F, 8.0F, 2.0F}, {-18.0F, 12.0F, 4.0F},
                { 22.0F, -14.0F, -3.0F}, { 35.0F, -6.0F, -1.0F}, { 30.0F, 8.0F, 2.0F}, { 18.0F, 12.0F, 4.0F}
        };
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float side = (i < 4) ? -1.0F : 1.0F;
            float wx = wingBase[i][0];
            float wy = wingBase[i][1];
            float wz = wingBase[i][2];
            // Swing wing around Y axis for realistic butterfly flap
            float cosF = Mth.cos(flap * side);
            float sinF = Mth.sin(flap * side);
            s.x = wx * cosF;
            s.z = wz - Math.abs(wx) * sinF * 0.55F;
            s.y = wy + flap * 4.0F;
            s.yRot = -flap * side * 0.75F;
            s.zRot = side * (0.28F + flap * 0.22F);
            s.xScale = s.yScale = s.zScale = 0.92F;
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
    private static void applyPillarEruption(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        body.y = -22.0F + Mth.sin(sec * 4.0F) * 2.0F;
        ring.yRot = sec * 2.4F;
        float pulse = 1.0F + 0.22F * Math.abs(Mth.sin(sec * Mth.PI));
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F);
            s.x = Mth.cos(a) * 20.0F * pulse;
            s.z = Mth.sin(a) * 20.0F * pulse;
            s.y = -12.0F + ((i % 2 == 0) ? -4.0F : 4.0F);
            s.xRot = 0.4F;
            s.xScale = s.yScale = s.zScale = 0.78F;
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
    private static void applyClapSlam(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        body.y = -24.0F;
        float wallX;
        float wallScale;
        float vib = 0.0F;
        if (sec < 1.15F) {
            float p = smoothstep(sec / 1.15F);
            wallX = Mth.lerp(p, 14.0F, 38.0F);
            wallScale = Mth.lerp(p, 0.9F, 1.32F);
        } else if (sec < 1.75F) {
            // Telegraph Hold with high-frequency tension vibration
            wallX = 38.0F + Mth.sin(sec * 42.0F) * 1.3F;
            wallScale = 1.35F;
            vib = Mth.sin(sec * 48.0F) * 0.06F;
        } else if (sec < 2.00F) {
            // Explosive Snap Slam!
            float p = (sec - 1.75F) / 0.25F;
            wallX = Mth.lerp(p * p, 38.0F, 10.5F);
            wallScale = 1.35F;
        } else if (sec < 2.35F) {
            // Impact Hitstop
            wallX = 10.5F;
            wallScale = 1.35F;
        } else {
            // Shatter recovery
            float p = smoothstep((sec - 2.35F) / 1.4F);
            wallX = Mth.lerp(p, 10.5F, 30.0F);
            wallScale = Mth.lerp(p, 1.32F, 0.65F);
        }

        // Left wall: 0,2,4,6 ; Right wall: 1,3,5,7
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float side = (i % 2 == 0) ? -1.0F : 1.0F;
            float yOff = (i < 4) ? -11.5F : 11.5F;
            float zOff = ((i / 2) % 2 == 0) ? -11.5F : 11.5F;
            s.x = side * wallX;
            s.y = yOff;
            s.z = zOff;
            s.xRot = vib;
            s.zRot = vib * side;
            s.xScale = s.yScale = s.zScale = wallScale;
        }
    }

    // Attack 6: Vacuum Pull & Shockwave Implosion (6.5s = 130 ticks)
    private static void applyVacuumImplosion(ModelPart body, ModelPart core, ModelPart frame, ModelPart ring, AnemoHypostasisModel<?> model, float sec) {
        body.y = -26.0F;
        float rad;
        if (sec < 3.4F) {
            // Pull phase: cubes contract inward while spinning rapidly
            float p = smoothstep(sec / 3.4F);
            rad = Mth.lerp(p, 26.0F, 13.0F) + Mth.sin(sec * 20.0F) * 0.8F;
            ring.yRot = sec * (2.2F + p * 4.5F);
        } else if (sec < 3.75F) {
            // Explosive outward shockwave snap!
            float p = (sec - 3.4F) / 0.35F;
            rad = Mth.lerp(p, 13.0F, 40.0F);
            ring.yRot = sec * 6.0F;
        } else {
            float p = smoothstep((sec - 3.75F) / 2.2F);
            rad = Mth.lerp(p, 40.0F, 22.0F);
            ring.yRot = sec * 2.0F;
        }
        for (int i = 0; i < 8; i++) {
            ModelPart s = model.getBone("shell_" + i);
            float a = i * (Mth.PI * 0.25F);
            s.x = Mth.cos(a) * rad;
            s.z = Mth.sin(a) * rad;
            s.y = ((i % 2 == 0) ? -7.0F : 7.0F);
            s.xScale = s.yScale = s.zScale = 0.85F;
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
