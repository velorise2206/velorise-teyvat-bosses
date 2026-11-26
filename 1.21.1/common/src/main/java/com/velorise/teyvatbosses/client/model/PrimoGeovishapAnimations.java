package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase-2 AAA 1:1 Genshin Impact keyframe animations for {@link PrimoGeovishapModel} (14 clips, 18 bones).
 *
 * <p>Crafted frame-by-frame from canonical Primo Geovishap gameplay footage:
 * <ul>
 *   <li>Grounded quadruped stance (zero floating hind legs during rear-ups, slams, or walk cycles).</li>
 *   <li>Authentic handstand scorpion tail-flick for {@code attack_scattered_rocks} &amp; {@code attack_primeval_spring}.</li>
 *   <li>Visible protruding dorsal crystal-mane plowing during {@code attack_burrow} subterranean travel.</li>
 *   <li>Chained 3-hit {@code attack_combo_slash} with impact hitstop, ultra-fast look-back {@code attack_whirlwind}
 *       with skid-brake overshoot, low-compressed {@code attack_primordial_shower} charge, and full 5.0s
 *       {@code countered_stagger} snap-back collapse.</li>
 *   <li>Fritsch-Carlson overshoot-safe cubic Hermite interpolation, C1 periodic loop tangents, and
 *       smoothstep cross-fade pose blending.</li>
 * </ul>
 */
public final class PrimoGeovishapAnimations {
    private static final float DEG = Mth.DEG_TO_RAD;
    private static final Map<String, Clip> CLIPS = new HashMap<>();

    static {
        CLIPS.put("idle", build_idle());
        CLIPS.put("walk", build_walk());
        CLIPS.put("spawn_awaken", build_spawn_awaken());
        CLIPS.put("attack_left_claw", build_attack_left_claw());
        CLIPS.put("attack_right_claw", build_attack_right_claw());
        CLIPS.put("attack_combo_slash", build_attack_combo_slash());
        CLIPS.put("attack_whirlwind", build_attack_whirlwind());
        CLIPS.put("attack_burrow", build_attack_burrow());
        CLIPS.put("attack_scattered_rocks", build_attack_scattered_rocks());
        CLIPS.put("attack_elemental_breath", build_attack_elemental_breath());
        CLIPS.put("attack_primordial_shower", build_attack_primordial_shower());
        CLIPS.put("attack_primeval_spring", build_attack_primeval_spring());
        CLIPS.put("countered_stagger", build_countered_stagger());
        CLIPS.put("death", build_death());
    }

    private PrimoGeovishapAnimations() {}

    public static float duration(String clipName) {
        Clip c = CLIPS.get(clipName);
        return c != null ? c.length : 1.0F;
    }

    public static final class BlendState {
        private static final int BONE_COUNT = PrimoGeovishapModel.ALL_BONES.length;
        private final float[] prevX = new float[BONE_COUNT];
        private final float[] prevY = new float[BONE_COUNT];
        private final float[] prevZ = new float[BONE_COUNT];
        private final float[] prevRx = new float[BONE_COUNT];
        private final float[] prevRy = new float[BONE_COUNT];
        private final float[] prevRz = new float[BONE_COUNT];

        private String activeClip = null;
        private float activeTime = 0.0F;
        private float blendElapsed = 1.0F;
        private float blendDuration = 0.24F;
        private float lastRenderSec = -1.0F;
        private boolean hasSnapshot = false;

        public void reset() {
            activeClip = null;
            hasSnapshot = false;
            lastRenderSec = -1.0F;
        }
    }

    public static void apply(PrimoGeovishapModel<?> model, String clipName, float seconds) {
        model.resetPose();
        applyClipDirect(model, clipName, seconds);
    }

    public static void applyBlended(
            PrimoGeovishapModel<?> model,
            BlendState state,
            String clipName,
            float clipSeconds,
            float continuousClientSeconds,
            float headLookYawDeg,
            float headLookPitchDeg
    ) {
        if (state == null) {
            apply(model, clipName, clipSeconds);
            return;
        }

        float dt = state.lastRenderSec >= 0.0F ? Mth.clamp(continuousClientSeconds - state.lastRenderSec, 0.0F, 0.15F) : 0.0F;
        state.lastRenderSec = continuousClientSeconds;

        if (state.activeClip == null) {
            state.activeClip = clipName;
            state.activeTime = clipSeconds;
            state.blendElapsed = state.blendDuration;
            state.hasSnapshot = false;
        } else if (!state.activeClip.equals(clipName)) {
            model.resetPose();
            applyClipDirect(model, state.activeClip, state.activeTime);
            captureSnapshot(model, state);
            state.activeClip = clipName;
            state.activeTime = clipSeconds;
            state.blendElapsed = 0.0F;
            state.blendDuration = ("countered_stagger".equals(clipName) || "attack_whirlwind".equals(clipName))
                    ? 0.12F
                    : 0.24F;
        } else {
            state.activeTime = clipSeconds;
            if (state.blendElapsed < state.blendDuration) {
                state.blendElapsed = Math.min(state.blendDuration, state.blendElapsed + dt);
            }
        }

        model.resetPose();
        applyClipDirect(model, clipName, clipSeconds);

        if (state.hasSnapshot && state.blendElapsed < state.blendDuration) {
            float raw = Mth.clamp(state.blendElapsed / state.blendDuration, 0.0F, 1.0F);
            float alpha = raw * raw * (3.0F - 2.0F * raw);
            blendFromSnapshot(model, state, alpha);
        }

        if ("idle".equals(clipName) || "walk".equals(clipName)) {
            float yawRad = Mth.clamp(headLookYawDeg, -42.0F, 42.0F) * DEG;
            float pitchRad = Mth.clamp(headLookPitchDeg, -18.0F, 18.0F) * DEG;
            ModelPart neck = model.getBone("neck");
            ModelPart head = model.getBone("head");
            if (neck != null) {
                neck.yRot += yawRad * 0.35F;
                neck.xRot += pitchRad * 0.28F;
            }
            if (head != null) {
                head.yRot += yawRad * 0.55F;
                head.xRot += pitchRad * 0.48F;
            }
        }
    }

    private static void captureSnapshot(PrimoGeovishapModel<?> model, BlendState state) {
        String[] names = PrimoGeovishapModel.ALL_BONES;
        for (int i = 0; i < names.length; i++) {
            ModelPart p = model.getBone(names[i]);
            if (p == null) continue;
            state.prevX[i] = p.x; state.prevY[i] = p.y; state.prevZ[i] = p.z;
            state.prevRx[i] = p.xRot; state.prevRy[i] = p.yRot; state.prevRz[i] = p.zRot;
        }
        state.hasSnapshot = true;
    }

    private static void blendFromSnapshot(PrimoGeovishapModel<?> model, BlendState state, float alpha) {
        String[] names = PrimoGeovishapModel.ALL_BONES;
        for (int i = 0; i < names.length; i++) {
            ModelPart p = model.getBone(names[i]);
            if (p == null) continue;
            p.x = Mth.lerp(alpha, state.prevX[i], p.x);
            p.y = Mth.lerp(alpha, state.prevY[i], p.y);
            p.z = Mth.lerp(alpha, state.prevZ[i], p.z);
            p.xRot = lerpAngleRad(alpha, state.prevRx[i], p.xRot);
            p.yRot = lerpAngleRad(alpha, state.prevRy[i], p.yRot);
            p.zRot = lerpAngleRad(alpha, state.prevRz[i], p.zRot);
        }
    }

    private static float lerpAngleRad(float alpha, float from, float to) {
        float diff = to - from;
        while (diff > Mth.PI) diff -= Mth.TWO_PI;
        while (diff < -Mth.PI) diff += Mth.TWO_PI;
        return from + diff * alpha;
    }

    /**
     * Predatory quadruped base stance: low grounded center of gravity, muscular bent elbows/knees,
     * and heavy S-curved lotus tail resting low near the ground.
     */
    private static void applyPredatoryBaseStance(PrimoGeovishapModel<?> model, String clipName) {
        if ("death".equals(clipName)) return;
        boolean isBurrow = "attack_burrow".equals(clipName);

        ModelPart body = model.getBone("body");
        if (body != null && !isBurrow) body.y += 1.6F;

        ModelPart armL = model.getBone("arm_left");
        ModelPart armR = model.getBone("arm_right");
        ModelPart foreL = model.getBone("forearm_left");
        ModelPart foreR = model.getBone("forearm_right");
        if (armL != null) { armL.xRot -= 10.0F * DEG; armL.zRot += 9.0F * DEG; }
        if (armR != null) { armR.xRot -= 10.0F * DEG; armR.zRot -= 9.0F * DEG; }
        if (foreL != null) { foreL.xRot += 12.0F * DEG; foreL.zRot -= 8.0F * DEG; }
        if (foreR != null) { foreR.xRot += 12.0F * DEG; foreR.zRot += 8.0F * DEG; }

        ModelPart legL = model.getBone("leg_left");
        ModelPart legR = model.getBone("leg_right");
        ModelPart shinL = model.getBone("shin_left");
        ModelPart shinR = model.getBone("shin_right");
        if (legL != null) { legL.xRot += 12.0F * DEG; legL.zRot += 5.0F * DEG; }
        if (legR != null) { legR.xRot += 12.0F * DEG; legR.zRot -= 5.0F * DEG; }
        if (shinL != null) { shinL.xRot -= 14.0F * DEG; shinL.zRot -= 4.0F * DEG; }
        if (shinR != null) { shinR.xRot -= 14.0F * DEG; shinR.zRot += 4.0F * DEG; }

        ModelPart t1 = model.getBone("tail_1");
        ModelPart t2 = model.getBone("tail_2");
        ModelPart t3 = model.getBone("tail_3");
        ModelPart t4 = model.getBone("tail_4");
        if (t1 != null) t1.xRot += 12.0F * DEG;
        if (t2 != null) t2.xRot += 10.0F * DEG;
        if (t3 != null) t3.xRot -= 6.0F * DEG;
        if (t4 != null) t4.xRot -= 9.0F * DEG;
    }

    private static void applyClipDirect(PrimoGeovishapModel<?> model, String clipName, float seconds) {
        Clip c = CLIPS.get(clipName);
        if (c == null) return;
        applyPredatoryBaseStance(model, clipName);
        float t = c.loop ? ((seconds % c.length) + c.length) % c.length : Mth.clamp(seconds, 0.0F, c.length);
        for (Track tr : c.tracks) {
            ModelPart part = model.getBone(tr.bone);
            if (part == null) continue;
            float vx = sampleSmoothHermite(tr.t, tr.x, t, c.loop);
            float vy = sampleSmoothHermite(tr.t, tr.y, t, c.loop);
            float vz = sampleSmoothHermite(tr.t, tr.z, t, c.loop);
            if (tr.isPos) {
                part.x += vx;
                part.y -= vy;
                part.z += vz;
            } else {
                part.xRot -= vx * DEG;
                part.yRot -= vy * DEG;
                part.zRot += vz * DEG;
            }
        }
    }

    private static float sampleSmoothHermite(float[] times, float[] vals, float t, boolean loop) {
        int n = times.length;
        if (n == 0) return 0.0F;
        if (n == 1 || t <= times[0]) return vals[0];
        if (t >= times[n - 1]) return vals[n - 1];

        int i = 0;
        while (i < n - 2 && t > times[i + 1]) i++;
        float dt = times[i + 1] - times[i];
        if (dt <= 1.0E-5F) return vals[i];
        float u = (t - times[i]) / dt;

        float p1 = vals[i];
        float p2 = vals[i + 1];
        float secant = (p2 - p1) / dt;

        float m1 = computeTangent(times, vals, i, loop);
        float m2 = computeTangent(times, vals, i + 1, loop);

        if (Math.abs(secant) < 1.0E-5F) {
            m1 = 0.0F;
            m2 = 0.0F;
        } else {
            float a = m1 / secant;
            float b = m2 / secant;
            if (a < 0.0F) m1 = 0.0F;
            if (b < 0.0F) m2 = 0.0F;
            float r2 = a * a + b * b;
            if (r2 > 9.0F) {
                float tau = 3.0F / Mth.sqrt(r2);
                m1 = tau * a * secant;
                m2 = tau * b * secant;
            }
        }

        float u2 = u * u;
        float u3 = u2 * u;
        return (2.0F * u3 - 3.0F * u2 + 1.0F) * p1
                + (u3 - 2.0F * u2 + u) * (m1 * dt)
                + (-2.0F * u3 + 3.0F * u2) * p2
                + (u3 - u2) * (m2 * dt);
    }

    private static float computeTangent(float[] times, float[] vals, int idx, boolean loop) {
        int n = times.length;
        if (idx == 0 || idx == n - 1) {
            if (loop && n >= 3) {
                float dtPrev = times[n - 1] - times[n - 2];
                float dtNext = times[1] - times[0];
                float sPrev = dtPrev > 1.0E-5F ? (vals[n - 1] - vals[n - 2]) / dtPrev : 0.0F;
                float sNext = dtNext > 1.0E-5F ? (vals[1] - vals[0]) / dtNext : 0.0F;
                return 0.5F * (sPrev + sNext);
            }
            int a = (idx == 0) ? 0 : n - 2;
            float dt = times[a + 1] - times[a];
            return dt > 1.0E-5F ? (vals[a + 1] - vals[a]) / dt : 0.0F;
        }
        float dtPrev = times[idx] - times[idx - 1];
        float dtNext = times[idx + 1] - times[idx];
        float sPrev = dtPrev > 1.0E-5F ? (vals[idx] - vals[idx - 1]) / dtPrev : 0.0F;
        float sNext = dtNext > 1.0E-5F ? (vals[idx + 1] - vals[idx]) / dtNext : 0.0F;
        return 0.5F * (sPrev + sNext);
    }

    private static float[] f(float... v) { return v; }

    private static final class Track {
        final String bone;
        final boolean isPos;
        final float[] t, x, y, z;
        Track(String bone, boolean isPos, float[] t, float[] x, float[] y, float[] z) {
            this.bone = bone; this.isPos = isPos; this.t = t; this.x = x; this.y = y; this.z = z;
        }
    }

    private static final class Clip {
        final String name;
        final float length;
        final boolean loop;
        final List<Track> tracks = new ArrayList<>();
        Clip(String name, float length, boolean loop) {
            this.name = name; this.length = length; this.loop = loop;
        }
        void rot(String bone, float[] t, float[] x, float[] y, float[] z) {
            tracks.add(new Track(bone, false, t, x, y, z));
        }
        void pos(String bone, float[] t, float[] x, float[] y, float[] z) {
            tracks.add(new Track(bone, true, t, x, y, z));
        }
    }

    // =========================================================================
    // 1. IDLE (3.20s C1-periodic): Heavy breathing, alert head scan, low S-wave lotus tail
    // =========================================================================
    private static Clip build_idle() {
        Clip c = new Clip("idle", 3.20F, true);
        float[] t = f(0.0F, 0.8F, 1.6F, 2.4F, 3.2F);
        c.pos("body", t, f(0, 0, 0, 0, 0), f(0.0F, -0.75F, -1.10F, -0.55F, 0.0F), f(0, 0, 0, 0, 0));
        c.rot("torso", t, f(0.0F, 2.0F, 0.0F, -2.0F, 0.0F), f(0.0F, 2.0F, 0.0F, -2.0F, 0.0F), f(0.0F, 1.2F, 0.0F, -1.2F, 0.0F));
        c.rot("neck", t, f(1.5F, -2.5F, -1.5F, 2.5F, 1.5F), f(0.0F, 4.5F, 0.0F, -4.5F, 0.0F), f(0, 0, 0, 0, 0));
        c.rot("head", t, f(-1.5F, 2.0F, 1.5F, -2.0F, -1.5F), f(-2.0F, 5.5F, 2.0F, -5.5F, -2.0F), f(0.8F, 1.6F, -0.8F, -1.6F, 0.8F));
        c.rot("jaw", t, f(3.0F, 8.5F, 5.0F, 1.5F, 3.0F), f(0, 0, 0, 0, 0), f(0, 0, 0, 0, 0));
        c.rot("arm_left", t, f(0.0F, -3.5F, 0.0F, 2.5F, 0.0F), f(0, 0, 0, 0, 0), f(0.0F, 1.8F, 0.0F, -1.2F, 0.0F));
        c.rot("forearm_left", t, f(0.0F, 3.5F, 0.0F, -2.5F, 0.0F), f(0, 0, 0, 0, 0), f(0, 0, 0, 0, 0));
        c.rot("arm_right", t, f(0.0F, -3.5F, 0.0F, 2.5F, 0.0F), f(0, 0, 0, 0, 0), f(0.0F, -1.8F, 0.0F, 1.2F, 0.0F));
        c.rot("forearm_right", t, f(0.0F, 3.5F, 0.0F, -2.5F, 0.0F), f(0, 0, 0, 0, 0), f(0, 0, 0, 0, 0));
        c.rot("tail_1", t, f(0.0F, 2.5F, 0.0F, -2.5F, 0.0F), f(0.0F, 8.0F, 0.0F, -8.0F, 0.0F), f(0, 0, 0, 0, 0));
        c.rot("tail_2", t, f(-2.0F, 2.5F, 2.0F, -2.5F, -2.0F), f(-7.0F, 11.0F, 7.0F, -11.0F, -7.0F), f(0, 0, 0, 0, 0));
        c.rot("tail_3", t, f(-3.0F, 1.5F, 3.0F, -1.5F, -3.0F), f(-13.0F, 4.0F, 13.0F, -4.0F, -13.0F), f(0, 0, 0, 0, 0));
        c.rot("tail_4", t, f(-3.5F, -1.0F, 3.5F, 1.0F, -3.5F), f(-15.0F, -7.0F, 15.0F, 7.0F, -15.0F), f(0, 0, 0, 0, 0));
        return c;
    }

    // =========================================================================
    // 2. WALK (1.60s C1-periodic): Heavy grounded quadruped diagonal stride
    // =========================================================================
    private static Clip build_walk() {
        Clip c = new Clip("walk", 1.60F, true);
        float[] t = f(0.0F, 0.2F, 0.4F, 0.6F, 0.8F, 1.0F, 1.2F, 1.4F, 1.6F);
        c.pos("body", t,
                f(0.0F, 0.7F, 1.1F, 0.6F, 0.0F, -0.7F, -1.1F, -0.6F, 0.0F),
                f(-0.4F, 0.1F, -0.7F, -1.1F, -0.4F, 0.1F, -0.7F, -1.1F, -0.4F),
                f(0.0F, 0.4F, 0.0F, -0.4F, 0.0F, 0.4F, 0.0F, -0.4F, 0.0F));
        c.rot("torso", t,
                f(1.5F, 3.0F, 0.0F, -2.5F, 1.5F, 3.0F, 0.0F, -2.5F, 1.5F),
                f(0.0F, 4.0F, 6.5F, 3.5F, 0.0F, -4.0F, -6.5F, -3.5F, 0.0F),
                f(-2.0F, 1.5F, 4.5F, 3.5F, 2.0F, -1.5F, -4.5F, -3.5F, -2.0F));
        c.rot("neck", t,
                f(0.0F, -3.0F, -1.0F, 2.5F, 0.0F, -3.0F, -1.0F, 2.5F, 0.0F),
                f(1.0F, -3.0F, -5.0F, -2.5F, -1.0F, 3.0F, 5.0F, 2.5F, 1.0F),
                f(0, 0, 0, 0, 0, 0, 0, 0, 0));
        c.rot("head", t,
                f(-1.5F, 2.0F, 1.0F, -2.0F, -1.5F, 2.0F, 1.0F, -2.0F, -1.5F),
                f(1.0F, -2.0F, -3.5F, -2.0F, -1.0F, 2.0F, 3.5F, 2.0F, 1.0F),
                f(0, 0, 0, 0, 0, 0, 0, 0, 0));
        c.rot("jaw", t, f(3.0F, 7.0F, 8.5F, 4.5F, 3.0F, 7.0F, 8.5F, 4.5F, 3.0F), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        // Diagonal gait: arm_left + leg_right swing forward together
        c.rot("arm_left", t, f(-10, -26, -28, -14, 2, 16, 24, 10, -10), f(0,0,0,0,0,0,0,0,0), f(5, 4, 1, 0, 0, 0, 0, 3, 5));
        c.rot("forearm_left", t, f(-28, -12, 8, 6, 2, 0, -6, -22, -28), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(2, 16, 24, 10, -10, -26, -28, -14, 2), f(0,0,0,0,0,0,0,0,0), f(0, 0, 0, -3, -5, -4, -1, 0, 0));
        c.rot("forearm_right", t, f(2, 0, -6, -22, -28, -12, 8, 6, 2), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(4, -14, -22, -16, -6, 10, 20, 14, 4), f(0,0,0,0,0,0,0,0,0), f(-3, -3, -1, 0, 0, 0, 0, -2, -3));
        c.rot("shin_right", t, f(20, 24, 12, 2, 0, -4, -8, 10, 20), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(-6, 10, 20, 14, 4, -14, -22, -16, -6), f(0,0,0,0,0,0,0,0,0), f(0, 0, 0, 2, 3, 3, 1, 0, 0));
        c.rot("shin_left", t, f(0, -4, -8, 10, 20, 24, 12, 2, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 2, 0, -2, 0, 2, 0, -2, 0), f(3, -5, -9, -6, -3, 5, 9, 6, 3), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(-2, 2, 2, -2, -2, 2, 2, -2, -2), f(10, 1, -9, -13, -10, -1, 9, 13, 10), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(-3, 0, 3, 0, -3, 0, 3, 0, -3), f(16, 10, -3, -14, -16, -10, 3, 14, 16), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(-4, -2, 4, 2, -4, -2, 4, 2, -4), f(18, 17, 7, -8, -18, -17, -7, 8, 18), f(0,0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 3. SPAWN AWAKEN (3.40s — 1.mp4 00:00–00:11): Curled dormant -> heavy grounded push-up -> roar
    // =========================================================================
    private static Clip build_spawn_awaken() {
        Clip c = new Clip("spawn_awaken", 3.40F, false);
        float[] t = f(0.0F, 0.55F, 1.05F, 1.45F, 1.85F, 2.55F, 2.95F, 3.40F);
        c.pos("body", t, f(0,0,0,0,0,0,0,0), f(-11.5F, -10.8F, -4.2F, -2.0F, -1.0F, -1.2F, -2.4F, 0.0F), f(0, 0, -1.5F, -0.5F, 1.2F, 1.0F, -1.0F, 0.0F));
        c.rot("torso", t, f(14, 18, 10, 8, -26, -24, 8, 0), f(0, 0, 0, 0, 0, 3, 0, 0), f(0, 0, 0, 0, 0, 1.5F, 0, 0));
        c.rot("neck", t, f(26, 22, 14, 6, -24, -22, 10, 0), f(0, 0, 0, 0, 3, -3, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("head", t, f(16, 12, 6, 0, -18, -16, 6, 0), f(0, 0, 0, 0, 5, -5, 0, 0), f(0,0,0,0,2,-2,0,0));
        c.rot("jaw", t, f(0, 2, 8, 12, 50, 44, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(-28, -36, -18, 6, -42, -38, 8, 0), f(0, 0, 0, 0, -14, -10, 0, 0), f(-12, 10, 8, 6, 24, 20, 8, 0));
        c.rot("forearm_left", t, f(-32, -26, -16, -8, -36, -30, -14, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,8,6,0,0));
        c.rot("arm_right", t, f(-28, -36, -18, 6, -42, -38, 8, 0), f(0, 0, 0, 0, 14, 10, 0, 0), f(12, -10, -8, -6, -24, -20, -8, 0));
        c.rot("forearm_right", t, f(-32, -26, -16, -8, -36, -30, -14, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,-8,-6,0,0));
        // Hind legs counter-rotate to stay solidly planted while torso pitches up -26 deg!
        c.rot("leg_left", t, f(-16, -14, -8, -4, 24, 22, -8, 0), f(0,0,0,0,0,0,0,0), f(0,2,4,4,6,6,2,0));
        c.rot("shin_left", t, f(20, 18, 12, 6, 2, 2, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(-16, -14, -8, -4, 24, 22, -8, 0), f(0,0,0,0,0,0,0,0), f(0,-2,-4,-4,-6,-6,-2,0));
        c.rot("shin_right", t, f(20, 18, 12, 6, 2, 2, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(-10, -8, -4, 0, 18, 16, 6, 0), f(0, 0, 0, -6, 12, -10, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(-8, -6, -2, 0, 12, 10, 4, 0), f(0, 0, 0, -10, 18, -14, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(-6, -4, 0, 0, 8, 8, 4, 0), f(0, 0, 0, -14, 22, -18, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(-4, -2, 0, 0, 6, 6, 4, 0), f(0, 0, 0, -16, 26, -20, 0, 0), f(0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 4. ATTACK LEFT CLAW (2.30s — Canonical 2-Hit: Slam into Earth + Rip-Up Rupture!)
    // =========================================================================
    private static Clip build_attack_left_claw() {
        Clip c = new Clip("attack_left_claw", 2.30F, false);
        float[] t = f(0.0F, 0.50F, 0.74F, 1.15F, 1.42F, 1.85F, 2.30F);
        c.pos("body", t, f(0, 1.8F, -2.0F, -1.8F, -1.0F, -0.4F, 0), f(0, -2.0F, -3.2F, -3.4F, -1.4F, -0.8F, 0), f(0, 2.2F, -4.4F, -4.2F, -2.4F, -0.8F, 0));
        c.rot("torso", t, f(0, -12, 14, 16, -14, -4, 0), f(0, 26, -28, -22, 14, 4, 0), f(0, -9, 11, 9, -6, -2, 0));
        c.rot("neck", t, f(0, -8, 12, 10, -12, -2, 0), f(0, -14, 16, 12, -8, 0, 0), f(0, 4, -6, -4, 3, 0, 0));
        c.rot("head", t, f(0, -10, 8, 6, -12, 0, 0), f(0, -16, 18, 14, -10, 0, 0), f(0, 6, -8, -6, 4, 0, 0));
        c.rot("jaw", t, f(0, 14, 36, 22, 44, 10, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        // Hit 1: slam left claw down at 0.74s, pin until 1.15s, Hit 2: violently rip upward at 1.42s!
        c.rot("arm_left", t, f(0, -80, 44, 42, -54, -14, 0), f(0, -16, 28, 24, -12, 0, 0), f(0, 28, -16, -14, 24, 6, 0));
        c.rot("forearm_left", t, f(0, -42, -8, 8, -36, -14, 0), f(0,0,0,0,0,0,0), f(0, 12, -6, -4, 10, 0, 0));
        c.rot("arm_right", t, f(0, 16, -16, -14, 10, 4, 0), f(0,0,0,0,0,0,0), f(0, -12, -16, -14, -8, -4, 0));
        c.rot("forearm_right", t, f(0, -18, -24, -20, -12, -6, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, 10, -14, -14, 10, 4, 0), f(0,0,0,0,0,0,0), f(0,4,6,6,4,2,0));
        c.rot("shin_left", t, f(0, 8, 16, 16, 6, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 10, -14, -14, 10, 4, 0), f(0,0,0,0,0,0,0), f(0,-4,-6,-6,-4,-2,0));
        c.rot("shin_right", t, f(0, 8, 16, 16, 6, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 8, 4, 4, 12, 4, 0), f(0, -16, 20, 16, -12, -4, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 6, 6, 6, 10, 3, 0), f(0, -22, 26, 20, -16, -6, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 4, 8, 8, 8, 2, 0), f(0, -26, 30, 24, -20, -8, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 4, 8, 8, 8, 2, 0), f(0, -30, 34, 28, -22, -8, 0), f(0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 5. ATTACK RIGHT CLAW (2.00s — 1.mp4 00:53–00:55): Wide horizontal-diagonal golden slash
    // =========================================================================
    private static Clip build_attack_right_claw() {
        Clip c = new Clip("attack_right_claw", 2.00F, false);
        float[] t = f(0.0F, 0.50F, 0.72F, 1.05F, 1.52F, 2.00F);
        c.pos("body", t, f(0, -1.8F, 2.2F, 1.8F, 0.5F, 0), f(0, -2.2F, -1.6F, -1.8F, -0.8F, 0), f(0, 2.2F, -4.5F, -3.8F, -1.2F, 0));
        c.rot("torso", t, f(0, -10, 8, 10, -4, 0), f(0, -28, 30, 22, -6, 0), f(0, 9, -11, -8, 3, 0));
        c.rot("neck", t, f(0, -6, 10, 8, 0, 0), f(0, 14, -16, -12, 0, 0), f(0, -4, 6, 4, 0, 0));
        c.rot("head", t, f(0, -8, 6, 4, 0, 0), f(0, 16, -18, -14, 0, 0), f(0, -6, 8, 5, 0, 0));
        c.rot("jaw", t, f(0, 14, 36, 24, 4, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -74, 38, 34, -16, 0), f(0, 22, -36, -26, 4, 0), f(0, -32, 20, 16, -8, 0));
        c.rot("forearm_right", t, f(0, -40, -14, 6, -20, 0), f(0,0,0,0,0,0), f(0, -12, 8, 4, -4, 0));
        c.rot("arm_left", t, f(0, 14, -18, -12, 0, 0), f(0,0,0,0,0,0), f(0, 10, 14, 8, 0, 0));
        c.rot("forearm_left", t, f(0, -18, -24, -14, 0, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, 8, -12, -8, 0, 0), f(0,0,0,0,0,0), f(0,4,6,4,0,0));
        c.rot("shin_left", t, f(0, 8, 14, 10, 0, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 8, -12, -8, 0, 0), f(0,0,0,0,0,0), f(0,-4,-6,-4,0,0));
        c.rot("shin_right", t, f(0, 8, 14, 10, 0, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 8, 4, 2, 0, 0), f(0, 16, -22, -12, 0, 0), f(0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 6, 6, 3, 0, 0), f(0, 22, -26, -16, 0, 0), f(0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 4, 8, 4, 0, 0), f(0, 26, -30, -18, 0, 0), f(0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 4, 8, 4, 0, 0), f(0, 30, -34, -20, 0, 0), f(0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 6. ATTACK COMBO SLASH (3.60s — 1.mp4 00:13–00:18 & 2.mp4 00:11–00:16)
    //    Chained Right Slash -> Left Slash -> Grounded Rear-Up Double-Hand Slam + Hitstop!
    // =========================================================================
    private static Clip build_attack_combo_slash() {
        Clip c = new Clip("attack_combo_slash", 3.60F, false);
        float[] t = f(0.0F, 0.45F, 0.72F, 1.15F, 1.45F, 1.98F, 2.36F, 2.54F, 2.95F, 3.60F);
        c.pos("body", t,
                f(0, -1.8F, 2.2F, 1.8F, -2.2F, 0, 0, 0, 0, 0),
                // Grounded hip squat during 1.98s rear-up (-1.6) so hind feet STAY GLUED TO GROUND!
                f(0, -1.8F, -2.2F, -1.8F, -2.4F, -1.6F, -4.0F, -4.0F, -2.4F, 0),
                f(0, 1.6F, -3.4F, -1.6F, -4.6F, 0.8F, -5.6F, -5.6F, -3.8F, 0));
        c.rot("torso", t, f(0, -8, 12, -8, 14, -38, 22, 22, 12, 0), f(0, -24, 28, 26, -30, 0, 0, 0, 0, 0), f(0, 8, -10, -8, 10, 0, 0, 0, 0, 0));
        c.rot("neck", t, f(0, -6, 8, -6, 10, -26, 20, 20, 10, 0), f(0, 14, -16, -14, 18, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -6, 6, -6, 8, -18, 14, 14, 6, 0), f(0, 12, -14, -12, 16, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 14, 32, 18, 36, 52, 44, 40, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -78, 40, -18, -20, -94, 34, 34, 20, 0), f(0, 18, -32, 0, 0, 14, -12, -12, -6, 0), f(0, -28, 18, -10, -12, -26, 12, 12, 6, 0));
        c.rot("forearm_right", t, f(0, -40, 10, -16, -18, -44, -8, -8, -4, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, -10, 6, 0, 0, -12, 0, 0, 0, 0));
        c.rot("arm_left", t, f(0, -16, -20, -80, 42, -94, 34, 34, 20, 0), f(0, 0, 0, -18, 32, -14, 12, 12, 6, 0), f(0, 10, 12, 28, -18, 26, -12, -12, -6, 0));
        c.rot("forearm_left", t, f(0, -14, -16, -40, 10, -44, -8, -8, -4, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, 0, 0, 10, -6, 12, 0, 0, 0, 0));
        // Hind legs counter-rotate +36 deg at 1.98s to keep feet planted flat on ground!
        c.rot("leg_left", t, f(0, 6, -10, 6, -12, 34, -18, -18, -10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,2,4,2,4,8,6,6,4,0));
        c.rot("shin_left", t, f(0, 6, 12, 6, 14, -2, 22, 22, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 6, -10, 6, -12, 34, -18, -18, -10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,-2,-4,-2,-4,-8,-6,-6,-4,0));
        c.rot("shin_right", t, f(0, 6, 12, 6, 14, -2, 22, 22, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 4, 4, 4, 4, 26, 14, 14, 6, 0), f(0, 16, -20, -16, 22, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 5, 5, 5, 5, 14, 16, 16, 8, 0), f(0, 20, -26, -20, 28, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 6, 6, 6, 6, 8, 18, 18, 8, 0), f(0, 24, -30, -24, 32, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 6, 6, 6, 6, 20, 20, 8, 0), f(0, 28, -34, -28, 36, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 7. ATTACK WHIRLWIND (2.60s — 1.mp4 00:56–00:59)
    //    Look behind -> ultra-fast 0.60s 360-deg cyclone whip -> skid-brake overshoot!
    // =========================================================================
    private static Clip build_attack_whirlwind() {
        Clip c = new Clip("attack_whirlwind", 2.60F, false);
        float[] t = f(0.0F, 0.55F, 0.85F, 1.15F, 1.48F, 1.95F, 2.60F);
        c.pos("body", t, f(0,0,0,0,0,0,0), f(0, -3.4F, -2.6F, -2.8F, -3.2F, -1.6F, 0), f(0,0,0,0,0,0,0));
        c.rot("body", t, f(0,0,0,0,0,0,0), f(0, -44, 165, 378, 354, 360, 360), f(0,0,0,0,0,0,0));
        c.rot("torso", t, f(0, 10, 12, 10, 8, 4, 0), f(0, -22, 24, 18, -8, 0, 0), f(0, -10, 14, 10, -4, 0, 0));
        c.rot("neck", t, f(0, 8, 6, 4, 4, 0, 0), f(0, -34, 28, 18, -6, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 4, 4, 2, 2, 0, 0), f(0, -38, 26, 16, -6, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 18, 36, 34, 14, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -22, 22, 16, -12, -4, 0), f(0,0,0,0,0,0,0), f(0, 22, 32, 26, 14, 6, 0));
        c.rot("forearm_left", t, f(0, -28, -34, -26, -16, -6, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 18, -22, -16, 12, 4, 0), f(0,0,0,0,0,0,0), f(0, -22, -32, -26, -14, -6, 0));
        c.rot("forearm_right", t, f(0, -24, -34, -26, -16, -6, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        // Tail extends horizontally like a sweeping lotus mace blade during the spin!
        c.rot("tail_1", t, f(0, -10, -14, -12, -4, 0, 0), f(0, -18, -32, -22, 14, 4, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, -8, -12, -10, -4, 0, 0), f(0, -24, -38, -26, 18, 6, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 4, 4, 4, 2, 0, 0), f(0, -28, -42, -30, 20, 6, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 6, 6, 2, 0, 0), f(0, -32, -46, -34, 22, 6, 0), f(0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 8. ATTACK BURROW (4.40s — 1.mp4 00:19–00:28 & 2.mp4 00:02–00:10, 00:47–00:58)
    //    Nose-dive into earth -> PROTRUDING DORSAL MANE PLOWING (-21px) -> Explosive Eruption Slam!
    // =========================================================================
    private static Clip build_attack_burrow() {
        Clip c = new Clip("attack_burrow", 4.40F, false);
        float[] t = f(0.0F, 0.45F, 1.05F, 1.55F, 2.05F, 2.55F, 2.90F, 3.22F, 3.45F, 4.40F);
        c.pos("body", t,
                f(0, 0, 0, 1.2F, -1.2F, 1.0F, 0, 0, 0, 0),
                // Sinks to -21px so dorsal crystal mane protrudes visibly above ground while traveling!
                f(0, -2.0F, -21.5F, -20.5F, -22.0F, -20.5F, 1.2F, -4.2F, -3.6F, 0.0F),
                f(0, -2.5F, -5.0F, -2.0F, -2.0F, -2.0F, -1.0F, -4.5F, -3.5F, 0.0F));
        c.rot("torso", t, f(0, 48, 8, 6, 6, 8, -36, 22, 16, 0), f(0, 0, 0, 8, -8, 6, 0, 0, 0, 0), f(0, 0, 0, 9, -9, 7, 0, 0, 0, 0));
        c.rot("neck", t, f(0, 30, 12, 10, 10, 10, -24, 18, 12, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 22, 10, 8, 8, 8, -18, 14, 8, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 24, 10, 8, 8, 10, 52, 42, 16, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -68, -40, -52, -24, -48, -90, 34, 22, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, 18, 22, 18, 18, 18, 28, -10, -4, 0));
        c.rot("forearm_left", t, f(0, -30, -26, -22, -26, -22, -40, -10, -6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -68, -40, -24, -52, -26, -90, 34, 22, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, -18, -22, -18, -18, -18, -28, 10, 4, 0));
        c.rot("forearm_right", t, f(0, -30, -26, -26, -22, -26, -40, -10, -6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, 24, 12, 14, 10, 14, 30, -18, -12, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,4,4,4,4,4,8,6,4,0));
        c.rot("shin_left", t, f(0, 14, 16, 14, 16, 14, 0, 22, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 24, 12, 10, 14, 10, 30, -18, -12, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,-4,-4,-4,-4,-4,-8,-6,-4,0));
        c.rot("shin_right", t, f(0, 14, 16, 16, 14, 16, 0, 22, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, -28, -12, -10, -10, -10, 22, 14, 6, 0), f(0, 0, 0, -12, 12, -10, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, -22, -10, -8, -8, -8, 14, 14, 6, 0), f(0, 0, 0, -16, 16, -12, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, -16, -8, -6, -6, -6, 8, 16, 6, 0), f(0, 0, 0, -18, 18, -14, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, -12, -6, -4, -4, -4, 6, 18, 6, 0), f(0, 0, 0, -20, 20, -16, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 9. ATTACK SCATTERED ROCKS (3.20s — ICONIC HANDSTAND TAIL FLICK from 2.mp4 00:25–00:28, 01:08–01:12!)
    //    Plants both massive forelimbs -> pitches hips + lotus tail STRAIGHT UP into handstand -> whips tail twice!
    // =========================================================================
    private static Clip build_attack_scattered_rocks() {
        Clip c = new Clip("attack_scattered_rocks", 3.20F, false);
        float[] t = f(0.0F, 0.50F, 0.95F, 1.25F, 1.70F, 2.15F, 2.65F, 3.20F);
        c.pos("body", t, f(0,0,0,0,0,0,0,0), f(0, -2.6F, -1.8F, -2.2F, -1.8F, -2.4F, -2.8F, 0), f(0, -1.5F, -3.0F, -2.4F, -3.0F, -2.2F, -1.0F, 0));
        // Massive forward torso pitch (+56 deg) hoists hind legs & glowing lotus tail high overhead!
        c.rot("torso", t, f(0, 22, 56, 48, 56, 46, 12, 0), f(0, 0, -8, 12, 10, -14, 0, 0), f(0, 0, -5, 6, 5, -6, 0, 0));
        c.rot("neck", t, f(0, -12, -36, -30, -36, -28, -6, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -10, -22, -18, -22, -16, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 12, 24, 34, 24, 36, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Forelimbs brace downward solidly as handstand pillars!
        c.rot("arm_left", t, f(0, -24, -52, -46, -52, -44, -10, 0), f(0,0,0,0,0,0,0,0), f(0, 10, 16, 14, 16, 14, 6, 0));
        c.rot("forearm_left", t, f(0, 10, 16, 14, 16, 14, 4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -24, -52, -46, -52, -44, -10, 0), f(0,0,0,0,0,0,0,0), f(0, -10, -16, -14, -16, -14, -6, 0));
        c.rot("forearm_right", t, f(0, 10, 16, 14, 16, 14, 4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Hind legs tuck up in the air during the handstand then slam down at 2.65s!
        c.rot("leg_left", t, f(0, 10, 38, 34, 38, 32, -10, 0), f(0,0,0,0,0,0,0,0), f(0,4,10,8,10,8,4,0));
        c.rot("shin_left", t, f(0, 14, 34, 30, 34, 28, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 10, 38, 34, 38, 32, -10, 0), f(0,0,0,0,0,0,0,0), f(0,-4,-10,-8,-10,-8,-4,0));
        c.rot("shin_right", t, f(0, 14, 34, 30, 34, 28, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Overhead lotus tail snaps forward twice (1.25s & 2.15s) scattering Elemental-Infused Stones!
        c.rot("tail_1", t, f(0, -14, -34, 18, -32, 20, -6, 0), f(0, 0, -16, 22, 16, -24, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, -16, -32, 22, -30, 24, -4, 0), f(0, 0, -20, 28, 20, -30, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, -14, -26, 24, -24, 26, -2, 0), f(0, 0, -24, 32, 24, -34, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, -12, -22, 26, -20, 28, 0, 0), f(0, 0, -28, 36, 28, -38, 0, 0), f(0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 10. ATTACK ELEMENTAL BREATH (4.20s — 1.mp4 00:40–00:46 & 2.mp4 00:00–00:02)
    //     Pull back & inhale -> 95-deg continuous high-pressure elemental beam sweep with recoil!
    // =========================================================================
    private static Clip build_attack_elemental_breath() {
        Clip c = new Clip("attack_elemental_breath", 4.20F, false);
        float[] t = f(0.0F, 0.70F, 1.15F, 1.85F, 2.55F, 3.15F, 3.65F, 4.20F);
        c.pos("body", t, f(0, -1.0F, -1.2F, 0.0F, 1.2F, 1.0F, 0.4F, 0), f(0, -2.4F, -2.6F, -2.8F, -2.6F, -2.2F, -1.0F, 0), f(0, 3.4F, 3.8F, 4.2F, 3.8F, 2.8F, 1.0F, 0));
        c.rot("torso", t, f(0, -10, -8, -10, -8, -6, 0, 0), f(0, -16, -20, 0, 20, 16, 4, 0), f(0, 4, 5, 0, -5, -4, 0, 0));
        c.rot("neck", t, f(0, -12, -8, -10, -8, -4, 0, 0), f(0, -26, -28, 0, 28, 22, 4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -10, -6, -8, -6, -2, 0, 0), f(0, -22, -24, 0, 24, 18, 4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 30, 50, 52, 50, 46, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -16, -18, -20, -18, -12, -4, 0), f(0,0,0,0,0,0,0,0), f(0, 14, 18, 20, 18, 12, 4, 0));
        c.rot("forearm_left", t, f(0, -12, -14, -14, -14, -10, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -16, -18, -20, -18, -12, -4, 0), f(0,0,0,0,0,0,0,0), f(0, -14, -18, -20, -18, -12, -4, 0));
        c.rot("forearm_right", t, f(0, -12, -14, -14, -14, -10, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 6, 8, 8, 8, 6, 2, 0), f(0, 14, 18, 0, -18, -14, -4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 6, 8, 8, 8, 6, 2, 0), f(0, 18, 24, 0, -24, -18, -4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 6, 6, 6, 6, 4, 2, 0), f(0, 22, 28, 0, -28, -22, -4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 6, 6, 6, 4, 2, 0), f(0, 26, 32, 0, -32, -26, -4, 0), f(0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 11. ATTACK PRIMORDIAL SHOWER (4.60s — 1.mp4 00:33–00:39 & 2.mp4 00:18–00:24)
    //     Ultra-low compressed trembling charge -> explosive grounded roar unleashing 20m floor wave!
    // =========================================================================
    private static Clip build_attack_primordial_shower() {
        Clip c = new Clip("attack_primordial_shower", 4.60F, false);
        float[] t = f(0.0F, 0.70F, 1.30F, 1.75F, 2.15F, 2.38F, 2.95F, 3.65F, 4.60F);
        c.pos("body", t,
                f(0, 0, 0.8F, -0.8F, 0.9F, 0, 0, 0, 0),
                f(0, -4.4F, -4.8F, -5.0F, -5.2F, -1.4F, -1.8F, -2.6F, 0),
                f(0, 2.2F, 2.8F, 3.0F, 3.2F, -1.5F, -0.5F, 0.5F, 0));
        c.rot("torso", t, f(0, 18, 22, 24, 24, -34, -26, 8, 0), f(0, 0, 3, -3, 4, 0, 0, 0, 0), f(0, 0, 3, -3, 3, 0, 0, 0, 0));
        c.rot("neck", t, f(0, 22, 26, 28, 28, -28, -22, 10, 0), f(0,0,4,-4,4,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 14, 18, 20, 20, -22, -16, 6, 0), f(0,0,5,-5,5,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 18, 28, 34, 40, 56, 48, 20, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -34, -38, -40, -42, -56, -44, 6, 0), f(0, 10, 14, 16, 16, -18, -12, 0, 0), f(0, 22, 26, 28, 30, 34, 24, 6, 0));
        c.rot("forearm_left", t, f(0, -28, -32, -34, -36, -38, -28, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,10,6,0,0));
        c.rot("arm_right", t, f(0, -34, -38, -40, -42, -56, -44, 6, 0), f(0, -10, -14, -16, -16, 18, 12, 0, 0), f(0, -22, -26, -28, -30, -34, -24, -6, 0));
        c.rot("forearm_right", t, f(0, -28, -32, -34, -36, -38, -28, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,-10,-6,0,0));
        c.rot("leg_left", t, f(0, -18, -22, -22, -24, 30, 22, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,6,8,8,8,8,6,4,0));
        c.rot("shin_left", t, f(0, 24, 28, 28, 30, 0, 6, 14, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -18, -22, -22, -24, 30, 22, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,-6,-8,-8,-8,-8,-6,-4,0));
        c.rot("shin_right", t, f(0, 24, 28, 28, 30, 0, 6, 14, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, -12, -14, -16, -16, 22, 16, 4, 0), f(0,0,-8,8,-8,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, -10, -12, -14, -14, 14, 12, 4, 0), f(0,0,-12,12,-12,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, -8, -10, -10, -10, 8, 8, 2, 0), f(0,0,-16,16,-16,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, -6, -8, -8, -8, 6, 6, 2, 0), f(0,0,-20,20,-20,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 12. ATTACK PRIMEVAL SPRING (3.40s — Near-Death <=20% HP Slam + Tail Barrage!)
    // =========================================================================
    private static Clip build_attack_primeval_spring() {
        Clip c = new Clip("attack_primeval_spring", 3.40F, false);
        float[] t = f(0.0F, 0.70F, 1.20F, 1.45F, 2.05F, 2.65F, 3.40F);
        c.pos("body", t, f(0,0,0,0,0,0,0), f(0, -1.5F, -4.4F, -4.4F, -2.2F, -1.6F, 0), f(0, 1.5F, -3.5F, -3.5F, -1.5F, -0.5F, 0));
        c.rot("torso", t, f(0, -34, 24, 24, 42, 10, 0), f(0, 0, 0, 0, 14, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("neck", t, f(0, -24, 18, 18, -24, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -16, 12, 12, -18, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 48, 40, 36, 30, 10, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -90, 32, 32, -40, -6, 0), f(0, -12, 10, 10, 0, 0, 0), f(0, 26, -12, -12, 14, 4, 0));
        c.rot("forearm_left", t, f(0, -42, -10, -10, 12, -4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -90, 32, 32, -40, -6, 0), f(0, 12, -10, -10, 0, 0, 0), f(0, -26, 12, 12, -14, -4, 0));
        c.rot("forearm_right", t, f(0, -42, -10, -10, 12, -4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, 30, -18, -18, 28, -6, 0), f(0,0,0,0,0,0,0), f(0,8,6,6,8,2,0));
        c.rot("shin_left", t, f(0, 0, 22, 22, 24, 8, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 30, -18, -18, 28, -6, 0), f(0,0,0,0,0,0,0), f(0,-8,-6,-6,-8,-2,0));
        c.rot("shin_right", t, f(0, 0, 22, 22, 24, 8, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 22, 14, 14, -28, 4, 0), f(0, 0, 0, 0, -22, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 14, 16, 16, -26, 4, 0), f(0, 0, 0, 0, -28, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 8, 18, 18, -22, 2, 0), f(0, 0, 0, 0, -32, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 20, 20, -18, 2, 0), f(0, 0, 0, 0, -36, 0, 0), f(0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 13. COUNTERED STAGGER (5.00s — 1.mp4 00:38–00:39, 01:15–01:17 & 2.mp4 00:23–00:24)
    //     Violent shield-reflect snap-back -> heavy off-balance collapse -> groggy swaying daze -> recover
    // =========================================================================
    private static Clip build_countered_stagger() {
        Clip c = new Clip("countered_stagger", 5.00F, false);
        float[] t = f(0.0F, 0.38F, 0.85F, 1.65F, 2.55F, 3.45F, 4.15F, 5.00F);
        c.pos("body", t, f(0, 0, 1.5F, 1.5F, 1.2F, 1.4F, 0.6F, 0), f(0, -1.6F, -5.6F, -5.8F, -5.4F, -5.6F, -2.8F, 0), f(0, 5.8F, 7.2F, 7.0F, 6.8F, 6.4F, 2.8F, 0));
        c.rot("torso", t, f(0, -36, 16, 18, 14, 16, 6, 0), f(0, 6, -10, -8, -10, -8, -4, 0), f(0, -6, 14, 14, 12, 13, 6, 0));
        c.rot("neck", t, f(0, -30, 24, 28, 24, 26, 10, 0), f(0, 8, -12, 14, -14, 12, 0, 0), f(0, 0, 8, -6, 8, -4, 0, 0));
        c.rot("head", t, f(0, -26, 20, 24, 20, 22, 8, 0), f(0, 10, -16, 20, -20, 16, 0, 0), f(0, 0, 10, -10, 10, -6, 0, 0));
        c.rot("jaw", t, f(0, 48, 28, 24, 26, 22, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, -68, -24, -22, -24, -20, -8, 0), f(0,0,0,0,0,0,0,0), f(0, 38, 32, 30, 30, 26, 12, 0));
        c.rot("forearm_left", t, f(0, -40, -18, -16, -18, -14, -6, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, -68, -14, -12, -14, -10, -4, 0), f(0,0,0,0,0,0,0,0), f(0, -38, -26, -24, -24, -20, -10, 0));
        c.rot("forearm_right", t, f(0, -40, -12, -10, -12, -10, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, 30, -22, -24, -22, -22, -10, 0), f(0,0,0,0,0,0,0,0), f(0,8,14,14,12,12,6,0));
        c.rot("shin_left", t, f(0, 4, 28, 30, 28, 26, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 30, -22, -24, -22, -22, -10, 0), f(0,0,0,0,0,0,0,0), f(0,-8,-14,-14,-12,-12,-6,0));
        c.rot("shin_right", t, f(0, 4, 28, 30, 28, 26, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 20, -6, -8, -6, -6, -2, 0), f(0, 0, 12, 14, 12, 10, 4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 14, -4, -6, -4, -4, -2, 0), f(0, 0, 16, 18, 16, 14, 4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 8, -2, -4, -2, -2, 0, 0), f(0, 0, 20, 22, 20, 16, 4, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 0, -2, 0, 0, 0, 0), f(0, 0, 22, 24, 22, 18, 4, 0), f(0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 14. DEATH (3.60s): Agonized grounded rear-up -> buckling crash sideways onto earth
    // =========================================================================
    private static Clip build_death() {
        Clip c = new Clip("death", 3.60F, false);
        float[] t = f(0.0F, 0.85F, 1.65F, 2.35F, 3.60F);
        c.pos("body", t, f(0, 0, 2.0F, 3.2F, 3.5F), f(0, -1.4F, -5.8F, -8.2F, -8.4F), f(0, 1.5F, 2.5F, 3.0F, 3.0F));
        c.rot("body", t, f(0, 0, 6, 12, 12), f(0, 0, -6, -10, -10), f(0, 0, 14, 24, 26));
        c.rot("torso", t, f(0, -32, 10, 16, 18), f(0, 0, -8, -12, -12), f(0, 0, 10, 16, 18));
        c.rot("neck", t, f(0, -26, 18, 26, 28), f(0, 0, -12, -18, -20), f(0, 0, 8, 14, 16));
        c.rot("head", t, f(0, -20, 14, 22, 24), f(0, 0, -14, -22, -24), f(0, 0, 10, 18, 20));
        c.rot("jaw", t, f(0, 52, 34, 22, 18), f(0,0,0,0,0), f(0,0,0,0,0));
        c.rot("arm_left", t, f(0, -56, -24, -14, -12), f(0,0,0,0,0), f(0, 26, 44, 56, 58));
        c.rot("forearm_left", t, f(0, -34, -16, -8, -6), f(0,0,0,0,0), f(0,0,0,0,0));
        c.rot("arm_right", t, f(0, -56, -18, -8, -6), f(0,0,0,0,0), f(0, -26, -34, -42, -44));
        c.rot("forearm_right", t, f(0, -34, -12, -6, -4), f(0,0,0,0,0), f(0,0,0,0,0));
        c.rot("leg_left", t, f(0, 28, -20, -28, -30), f(0,0,0,0,0), f(0, 6, 20, 28, 30));
        c.rot("shin_left", t, f(0, 4, 26, 36, 38), f(0,0,0,0,0), f(0,0,0,0,0));
        c.rot("leg_right", t, f(0, 28, -18, -24, -26), f(0,0,0,0,0), f(0, -6, -18, -24, -26));
        c.rot("shin_right", t, f(0, 4, 24, 32, 34), f(0,0,0,0,0), f(0,0,0,0,0));
        c.rot("tail_1", t, f(0, 18, 4, -2, -4), f(0, 0, 16, 24, 26), f(0,0,0,0,0));
        c.rot("tail_2", t, f(0, 12, 2, -2, -4), f(0, 0, 20, 30, 32), f(0,0,0,0,0));
        c.rot("tail_3", t, f(0, 8, 0, -2, -2), f(0, 0, 24, 34, 36), f(0,0,0,0,0));
        c.rot("tail_4", t, f(0, 6, 0, -2, -2), f(0, 0, 26, 38, 40), f(0,0,0,0,0));
        return c;
    }
}
