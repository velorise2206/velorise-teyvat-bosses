package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;

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
        CLIPS.put("dormant_sleep", build_dormant_sleep());
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

        // Exact Forward-Kinematics Floor Clamp: guarantees zero paw/knee sinking below Y=24.0 on flat ground
        enforceFloorContact(model, clipName);
    }

    private static void mulBone(Matrix4f mat, ModelPart part) {
        if (part == null) return;
        mat.translate(part.x, part.y, part.z);
        if (part.zRot != 0.0F) mat.rotateZ(part.zRot);
        if (part.yRot != 0.0F) mat.rotateY(part.yRot);
        if (part.xRot != 0.0F) mat.rotateX(part.xRot);
    }

    private static float sampleLimbLowestY(Matrix4f torsoMat, ModelPart upper, ModelPart lower, float soleY, float toeZ) {
        if (upper == null || lower == null) return 24.0F;
        Matrix4f m = new Matrix4f(torsoMat);
        mulBone(m, upper);
        mulBone(m, lower);
        Vector4f v1 = new Vector4f(0.0F, soleY, 0.0F, 1.0F).mul(m);
        Vector4f v2 = new Vector4f(0.0F, soleY, toeZ, 1.0F).mul(m);
        return Math.max(v1.y, v2.y);
    }

    /**
     * Computes exact FK world-model Y of all 4 paw soles against the Minecraft floor plane (Y = 24.0px).
     * Lifts {@code body.y} by the exact penetration amount whenever any animation crouch, roar rear-up,
     * or walk step would otherwise push a paw or knee beneath the floor.
     */
    private static void enforceFloorContact(PrimoGeovishapModel<?> model, String clipName) {
        if ("attack_burrow".equals(clipName) || "dormant_sleep".equals(clipName) || "death".equals(clipName)) {
            return;
        }
        ModelPart root = model.getBone("root");
        ModelPart body = model.getBone("body");
        ModelPart torso = model.getBone("torso");
        if (root == null || body == null || torso == null) return;

        Matrix4f torsoMat = new Matrix4f();
        mulBone(torsoMat, root);
        mulBone(torsoMat, body);
        mulBone(torsoMat, torso);

        float maxY = 24.0F;
        maxY = Math.max(maxY, sampleLimbLowestY(torsoMat, model.getBone("arm_left"), model.getBone("forearm_left"), 12.2F, -3.5F));
        maxY = Math.max(maxY, sampleLimbLowestY(torsoMat, model.getBone("arm_right"), model.getBone("forearm_right"), 12.2F, -3.5F));
        maxY = Math.max(maxY, sampleLimbLowestY(torsoMat, model.getBone("leg_left"), model.getBone("shin_left"), 11.8F, -2.5F));
        maxY = Math.max(maxY, sampleLimbLowestY(torsoMat, model.getBone("leg_right"), model.getBone("shin_right"), 11.8F, -2.5F));

        if (maxY > 24.0F) {
            body.y -= (maxY - 24.0F);
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
        if ("death".equals(clipName) || "dormant_sleep".equals(clipName)) return;
        boolean isBurrow = "attack_burrow".equals(clipName);

        ModelPart body = model.getBone("body");
        if (body != null && !isBurrow) body.y += 0.45F;

        ModelPart armL = model.getBone("arm_left");
        ModelPart armR = model.getBone("arm_right");
        ModelPart foreL = model.getBone("forearm_left");
        ModelPart foreR = model.getBone("forearm_right");
        if (armL != null) { armL.xRot -= 12.0F * DEG; armL.zRot += 14.0F * DEG; }
        if (armR != null) { armR.xRot -= 12.0F * DEG; armR.zRot -= 14.0F * DEG; }
        if (foreL != null) { foreL.xRot += 14.0F * DEG; foreL.zRot -= 12.0F * DEG; }
        if (foreR != null) { foreR.xRot += 14.0F * DEG; foreR.zRot += 12.0F * DEG; }

        ModelPart legL = model.getBone("leg_left");
        ModelPart legR = model.getBone("leg_right");
        ModelPart shinL = model.getBone("shin_left");
        ModelPart shinR = model.getBone("shin_right");
        if (legL != null) { legL.xRot -= 12.0F * DEG; legL.zRot += 6.0F * DEG; }
        if (legR != null) { legR.xRot -= 12.0F * DEG; legR.zRot -= 6.0F * DEG; }
        if (shinL != null) { shinL.xRot += 14.0F * DEG; shinL.zRot -= 5.0F * DEG; }
        if (shinR != null) { shinR.xRot += 14.0F * DEG; shinR.zRot += 5.0F * DEG; }

        // In Minecraft model coords (+Y down, +Z back), negative xRot droops tail DOWN toward ground
        ModelPart t1 = model.getBone("tail_1");
        ModelPart t2 = model.getBone("tail_2");
        ModelPart t3 = model.getBone("tail_3");
        ModelPart t4 = model.getBone("tail_4");
        if (t1 != null) t1.xRot -= 18.0F * DEG;
        if (t2 != null) t2.xRot -= 12.0F * DEG;
        if (t3 != null) t3.xRot += 10.0F * DEG;
        if (t4 != null) t4.xRot += 14.0F * DEG;
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
    // 2. WALK (1.60s C1-periodic): Fluid whole-body predatory quadruped diagonal stride
    // =========================================================================
    private static Clip build_walk() {
        Clip c = new Clip("walk", 1.60F, true);
        float[] t = f(0.0F, 0.2F, 0.4F, 0.6F, 0.8F, 1.0F, 1.2F, 1.4F, 1.6F);
        // Weighty lateral shoulder-hip transfer + clean double-bounce per cycle
        c.pos("body", t,
                f(0.0F, 1.1F, 1.5F, 0.8F, 0.0F, -1.1F, -1.5F, -0.8F, 0.0F),
                f(-0.5F, 0.35F, -0.65F, -1.45F, -0.5F, 0.35F, -0.65F, -1.45F, -0.5F),
                f(0.0F, 0.5F, 0.1F, -0.5F, 0.0F, 0.5F, 0.1F, -0.5F, 0.0F));
        // Coupled spine yaw + shoulder roll transferring weight cleanly onto each planted forepaw
        c.rot("torso", t,
                f(1.5F, 3.2F, 0.0F, -2.8F, 1.5F, 3.2F, 0.0F, -2.8F, 1.5F),
                f(0.0F, 6.0F, 9.0F, 5.0F, 0.0F, -6.0F, -9.0F, -5.0F, 0.0F),
                f(-2.5F, 2.0F, 5.5F, 4.0F, 2.5F, -2.0F, -5.5F, -4.0F, -2.5F));
        // Head & neck counter-stabilized like a real apex predator stalking forward
        c.rot("neck", t,
                f(-1.0F, -2.8F, -0.5F, 2.2F, -1.0F, -2.8F, -0.5F, 2.2F, -1.0F),
                f(0.5F, -4.5F, -6.5F, -3.5F, -0.5F, 4.5F, 6.5F, 3.5F, 0.5F),
                f(1.5F, -1.2F, -3.2F, -2.2F, -1.5F, 1.2F, 3.2F, 2.2F, 1.5F));
        c.rot("head", t,
                f(-0.5F, 1.8F, 0.5F, -1.6F, -0.5F, 1.8F, 0.5F, -1.6F, -0.5F),
                f(0.5F, -2.5F, -3.5F, -2.0F, -0.5F, 2.5F, 3.5F, 2.0F, 0.5F),
                f(1.0F, -0.8F, -2.2F, -1.5F, -1.0F, 0.8F, 2.2F, 1.5F, 1.0F));
        c.rot("jaw", t, f(3.0F, 6.5F, 8.0F, 4.5F, 3.0F, 6.5F, 8.0F, 4.5F, 3.0F), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        // Diagonal gait (+vx = swing FORWARD, -vx = push BACKWARD): deeper muscular strides + clean elbow/knee fold!
        c.rot("arm_left", t, f(10, 32, 34, 14, -4, -22, -28, -10, 10), f(0,0,0,0,0,0,0,0,0), f(6, 5, 2, 0, 0, 0, 0, 4, 6));
        c.rot("forearm_left", t, f(-22, -8, 12, 8, 4, 0, -10, -26, -22), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(-4, -22, -28, -10, 10, 32, 34, 14, -4), f(0,0,0,0,0,0,0,0,0), f(0, 0, 0, -4, -6, -5, -2, 0, 0));
        c.rot("forearm_right", t, f(4, 0, -10, -26, -22, -8, 12, 8, 4), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(6, 26, 30, 16, -2, -18, -26, -12, 6), f(0,0,0,0,0,0,0,0,0), f(-4, -3, -1, 0, 0, 0, 0, -2, -4));
        c.rot("shin_right", t, f(-24, -14, 6, 4, 2, -2, -12, -22, -24), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(-2, -18, -26, -12, 6, 26, 30, 16, -2), f(0,0,0,0,0,0,0,0,0), f(0, 0, 0, 2, 4, 3, 1, 0, 0));
        c.rot("shin_left", t, f(2, -2, -12, -22, -24, -14, 6, 4, 2), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        // Fluid phase-lagged S-wave lotus tail counterbalance
        c.rot("tail_1", t, f(0, 2, 0, -2, 0, 2, 0, -2, 0), f(4, -6, -11, -7, -4, 6, 11, 7, 4), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(-2, 2, 2, -2, -2, 2, 2, -2, -2), f(12, 2, -11, -15, -12, -2, 11, 15, 12), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(-3, 0, 3, 0, -3, 0, 3, 0, -3), f(18, 12, -4, -16, -18, -12, 4, 16, 18), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(-4, -2, 4, 2, -4, -2, 4, 2, -4), f(22, 19, 8, -10, -22, -19, -8, 10, 22), f(0,0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 3. SPAWN AWAKEN (3.40s — 1.mp4 00:00–00:11): Curled dormant -> heavy grounded push-up -> roar
    // =========================================================================
    private static Clip build_spawn_awaken() {
        Clip c = new Clip("spawn_awaken", 3.40F, false);
        float[] t = f(0.0F, 0.55F, 1.05F, 1.45F, 1.85F, 2.55F, 2.95F, 3.40F);
        c.pos("body", t, f(0,0,0,0,0,0,0,0), f(-11.5F, -10.8F, -4.2F, -2.0F, -1.0F, -1.2F, -2.4F, 0.0F), f(0, 0, -1.5F, -0.5F, 1.2F, 1.0F, -1.0F, 0.0F));
        // Curled low dormant (-16 deg) -> heavy grounded push-up -> proud roaring rear-up (+28 deg)!
        c.rot("torso", t, f(-14, -18, -10, -6, 28, 24, -6, 0), f(0, 0, 0, 0, 0, 3, 0, 0), f(0, 0, 0, 0, 0, 1.5F, 0, 0));
        c.rot("neck", t, f(-22, -18, -10, -4, 22, 18, -6, 0), f(0, 0, 0, 0, 3, -3, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("head", t, f(-14, -10, -6, 0, 16, 14, -4, 0), f(0, 0, 0, 0, 5, -5, 0, 0), f(0,0,0,0,2,-2,0,0));
        c.rot("jaw", t, f(0, 2, 8, 12, 50, 44, 12, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(24, 30, 16, 4, 48, 42, 6, 0), f(0, 0, 0, 0, -14, -10, 0, 0), f(-12, 10, 8, 6, 26, 22, 8, 0));
        c.rot("forearm_left", t, f(22, 18, 12, 6, 28, 24, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,8,6,0,0));
        c.rot("arm_right", t, f(24, 30, 16, 4, 48, 42, 6, 0), f(0, 0, 0, 0, 14, 10, 0, 0), f(12, -10, -8, -6, -26, -22, -8, 0));
        c.rot("forearm_right", t, f(22, 18, 12, 6, 28, 24, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,-8,-6,0,0));
        c.rot("leg_left", t, f(12, 10, 6, 4, -22, -20, 6, 0), f(0,0,0,0,0,0,0,0), f(0,2,4,4,6,6,2,0));
        c.rot("shin_left", t, f(-14, -12, -8, -4, 10, 8, -8, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(12, 10, 6, 4, -22, -20, 6, 0), f(0,0,0,0,0,0,0,0), f(0,-2,-4,-4,-6,-6,-2,0));
        c.rot("shin_right", t, f(-14, -12, -8, -4, 10, 8, -8, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
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
        c.rot("torso", t, f(0, 12, -10, -12, 14, 4, 0), f(0, 28, -30, -24, 16, 4, 0), f(0, -10, 12, 9, -6, -2, 0));
        c.rot("neck", t, f(0, 6, -8, -6, 10, 2, 0), f(0, -16, 18, 14, -10, 0, 0), f(0, 4, -6, -4, 3, 0, 0));
        c.rot("head", t, f(0, 8, -6, -4, 10, 0, 0), f(0, -16, 18, 14, -10, 0, 0), f(0, 6, -8, -6, 4, 0, 0));
        c.rot("jaw", t, f(0, 14, 36, 22, 44, 10, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        // Wide open-shoulder wind-up -> sweeping forward claw slam at 0.74s -> explosive rip-up at 1.42s!
        c.rot("arm_left", t, f(0, 62, 24, 18, 72, 16, 0), f(0, 34, -38, -28, 22, 0, 0), f(0, 46, -14, -10, 38, 10, 0));
        c.rot("forearm_left", t, f(0, 22, 12, 6, 28, 8, 0), f(0,0,0,0,0,0,0), f(0, 16, -8, -4, 14, 0, 0));
        c.rot("arm_right", t, f(0, -12, 14, 12, -8, -4, 0), f(0,0,0,0,0,0,0), f(0, -14, -18, -14, -10, -4, 0));
        c.rot("forearm_right", t, f(0, 12, 16, 14, 8, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
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
        c.rot("torso", t, f(0, 10, -8, -8, 4, 0), f(0, -28, 32, 22, -6, 0), f(0, 10, -12, -8, 3, 0));
        c.rot("neck", t, f(0, 6, -8, -6, 0, 0), f(0, 16, -18, -12, 0, 0), f(0, -4, 6, 4, 0, 0));
        c.rot("head", t, f(0, 8, -6, -4, 0, 0), f(0, 16, -18, -14, 0, 0), f(0, -6, 8, 5, 0, 0));
        c.rot("jaw", t, f(0, 14, 36, 24, 4, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
        // Wide horizontal right-shoulder open -> full sweeping claw slash across front!
        c.rot("arm_right", t, f(0, 60, 24, 18, 12, 0), f(0, -34, 42, 30, -6, 0), f(0, -48, 14, 10, -10, 0));
        c.rot("forearm_right", t, f(0, 20, 12, 8, 6, 0), f(0,0,0,0,0,0), f(0, -16, 8, 4, -4, 0));
        c.rot("arm_left", t, f(0, -10, 16, 10, 0, 0), f(0,0,0,0,0,0), f(0, 14, 18, 10, 0, 0));
        c.rot("forearm_left", t, f(0, 12, 16, 10, 0, 0), f(0,0,0,0,0,0), f(0,0,0,0,0,0));
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
        // Right slash -> Left slash -> proud high Rear-Up (+34 deg chest lift) -> Double-Hand Geo Slam!
        c.rot("torso", t, f(0, 10, -10, 10, -10, 34, -18, -18, -8, 0), f(0, -26, 30, 26, -30, 0, 0, 0, 0, 0), f(0, 9, -11, -9, 11, 0, 0, 0, 0, 0));
        c.rot("neck", t, f(0, 6, -6, 6, -8, 22, -14, -14, -6, 0), f(0, 14, -16, -14, 18, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 6, -4, 6, -6, 16, -10, -10, -4, 0), f(0, 12, -14, -12, 16, 0, 0, 0, 0, 0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 14, 32, 18, 36, 52, 44, 40, 14, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 60, 24, 12, 10, 96, 18, 18, 10, 0), f(0, -34, 42, 0, 0, -18, 10, 10, 4, 0), f(0, -46, 14, -12, -14, -34, -10, -10, -4, 0));
        c.rot("forearm_right", t, f(0, 22, 12, 10, 10, 32, 10, 10, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, -14, 6, 0, 0, -12, 0, 0, 0, 0));
        c.rot("arm_left", t, f(0, 12, 10, 60, 24, 96, 18, 18, 10, 0), f(0, 0, 0, 34, -42, 18, -10, -10, -4, 0), f(0, 12, 14, 46, -14, 34, 10, 10, 4, 0));
        c.rot("forearm_left", t, f(0, 10, 10, 22, 12, 32, 10, 10, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, 0, 0, 14, -6, 12, 0, 0, 0, 0));
        // Hind legs counter-rotate -28 deg at 1.98s rear-up so hind feet stay solidly planted!
        c.rot("leg_left", t, f(0, -6, 8, -6, 10, -28, 14, 14, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,2,4,2,4,8,6,6,4,0));
        c.rot("shin_left", t, f(0, 4, -8, 4, -10, 14, -14, -14, -6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -6, 8, -6, 10, -28, 14, 14, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,-2,-4,-2,-4,-8,-6,-6,-4,0));
        c.rot("shin_right", t, f(0, 4, -8, 4, -10, 14, -14, -14, -6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
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
                // Sinks deep to -36.5px so torso/head/legs submerge completely & ONLY dorsal crystal-mane fins slice above ground!
                f(0, -3.0F, -36.0F, -35.5F, -37.0F, -35.5F, 2.5F, -4.5F, -3.6F, 0.0F),
                f(0, -2.5F, -5.0F, -2.0F, -2.0F, -2.0F, -1.0F, -4.5F, -3.5F, 0.0F));
        // Nose-dive DOWN (-44 deg) into earth -> dorsal mane plowing -> burst UP (+36 deg) -> heavy Geo slam (-18 deg)!
        c.rot("torso", t, f(0, -44, -8, -6, -6, -8, 36, -18, -12, 0), f(0, 0, 0, 8, -8, 6, 0, 0, 0, 0), f(0, 0, 0, 9, -9, 7, 0, 0, 0, 0));
        c.rot("neck", t, f(0, -26, -10, -8, -8, -8, 22, -14, -8, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -18, -8, -6, -6, -6, 16, -10, -6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 24, 10, 8, 8, 10, 52, 42, 16, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, 60, 36, 46, 22, 42, 88, 18, 12, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, 18, 22, 18, 18, 18, 32, 8, 4, 0));
        c.rot("forearm_left", t, f(0, 24, 22, 18, 22, 18, 32, 10, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 60, 36, 22, 46, 22, 88, 18, 12, 0), f(0,0,0,0,0,0,0,0,0,0), f(0, -18, -22, -18, -18, -18, -32, -8, -4, 0));
        c.rot("forearm_right", t, f(0, 24, 22, 22, 18, 22, 32, 10, 6, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, -20, -10, -12, -8, -12, -26, 14, 10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,4,4,4,4,4,8,6,4,0));
        c.rot("shin_left", t, f(0, -10, -12, -10, -12, -10, 12, -16, -10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -20, -10, -8, -12, -8, -26, 14, 10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,-4,-4,-4,-4,-4,-8,-6,-4,0));
        c.rot("shin_right", t, f(0, -10, -12, -12, -10, -12, 12, -16, -10, 0), f(0,0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0,0));
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
        // Lift body +4.2px during handstand so head/forelimbs brace cleanly above floor while hips & tail soar overhead!
        c.pos("body", t, f(0,0,0,0,0,0,0,0), f(0, -1.8F, 4.2F, 3.6F, 4.2F, 3.4F, -2.6F, 0), f(0, -1.5F, -3.5F, -2.6F, -3.5F, -2.4F, -1.0F, 0));
        // True Genshin Handstand! Negative torso vx (-52 deg -> xRot +52) pitches front DOWN & hoists hips/tail HIGH UP!
        c.rot("torso", t, f(0, -20, -52, -44, -52, -42, -10, 0), f(0, 0, -8, 12, 10, -14, 0, 0), f(0, 0, -5, 6, 5, -6, 0, 0));
        c.rot("neck", t, f(0, 12, 34, 28, 34, 26, 6, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 10, 20, 16, 20, 14, 4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 12, 24, 34, 24, 36, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Forelimbs brace straight down into ground as handstand pillars!
        c.rot("arm_left", t, f(0, 22, 48, 42, 48, 40, 8, 0), f(0,0,0,0,0,0,0,0), f(0, 10, 16, 14, 16, 14, 6, 0));
        c.rot("forearm_left", t, f(0, -8, -14, -12, -14, -12, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 22, 48, 42, 48, 40, 8, 0), f(0,0,0,0,0,0,0,0), f(0, -10, -16, -14, -16, -14, -6, 0));
        c.rot("forearm_right", t, f(0, -8, -14, -12, -14, -12, -4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Hind legs tuck cleanly in the air during handstand then plant back down at 2.65s!
        c.rot("leg_left", t, f(0, -8, -32, -28, -32, -26, 8, 0), f(0,0,0,0,0,0,0,0), f(0,4,10,8,10,8,4,0));
        c.rot("shin_left", t, f(0, -12, -28, -24, -28, -22, -8, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -8, -32, -28, -32, -26, 8, 0), f(0,0,0,0,0,0,0,0), f(0,-4,-10,-8,-10,-8,-4,0));
        c.rot("shin_right", t, f(0, -12, -28, -24, -28, -22, -8, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        // Overhead scorpion tail coils UP & snaps FORWARD twice (1.25s & 2.15s) scattering Elemental-Infused Stones!
        c.rot("tail_1", t, f(0, 14, 32, -22, 30, -24, 6, 0), f(0, 0, -18, 24, 18, -26, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 16, 30, -24, 28, -26, 4, 0), f(0, 0, -22, 30, 22, -32, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 14, 24, -26, 22, -28, 2, 0), f(0, 0, -26, 34, 26, -36, 0, 0), f(0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 12, 20, -28, 18, -30, 0, 0), f(0, 0, -30, 38, 30, -40, 0, 0), f(0,0,0,0,0,0,0,0));
        return c;
    }

    // =========================================================================
    // 10. ATTACK ELEMENTAL BREATH (4.20s — 1.mp4 00:40–00:46 & 2.mp4 00:00–00:02)
    //     Pull back & inhale -> 95-deg continuous high-pressure elemental beam sweep with recoil!
    // =========================================================================
    private static Clip build_attack_elemental_breath() {
        Clip c = new Clip("attack_elemental_breath", 4.20F, false);
        // Synchronized 1:1 with renderer & server beam sweep: t=1.10s (-46 deg) -> t=2.10s (0) -> t=3.10s (+46 deg)
        float[] t = f(0.0F, 0.70F, 1.10F, 1.60F, 2.10F, 2.60F, 3.10F, 3.65F, 4.20F);
        c.pos("body", t, f(0, -0.8F, -1.0F, -0.5F, 0.0F, 0.5F, 1.0F, 0.4F, 0), f(0, -2.2F, -2.4F, -2.6F, -2.6F, -2.6F, -2.2F, -1.0F, 0), f(0, 3.0F, 3.4F, 3.6F, 3.8F, 3.6F, 3.0F, 1.0F, 0));
        c.rot("torso", t, f(0, 6, 4, 4, 4, 4, 4, 0, 0), f(0, -11, -14, -7, 0, 7, 14, 4, 0), f(0, 3, 4, 2, 0, -2, -4, 0, 0));
        c.rot("neck", t, f(0, 4, -4, -5, -6, -5, -4, 0, 0), f(0, -15, -18, -9, 0, 9, 18, 6, 0), f(0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 4, -4, -5, -6, -5, -4, 0, 0), f(0, -12, -14, -7, 0, 7, 14, 4, 0), f(0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 32, 52, 54, 54, 54, 48, 12, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, 14, 16, 18, 18, 18, 14, 6, 0), f(0,0,0,0,0,0,0,0,0), f(0, 14, 18, 20, 20, 20, 14, 4, 0));
        c.rot("forearm_left", t, f(0, 10, 12, 12, 12, 12, 10, 4, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 14, 16, 18, 18, 18, 14, 6, 0), f(0,0,0,0,0,0,0,0,0), f(0, -14, -18, -20, -20, -20, -14, -4, 0));
        c.rot("forearm_right", t, f(0, 10, 12, 12, 12, 12, 10, 4, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_1", t, f(0, 4, 6, 6, 6, 6, 4, 2, 0), f(0, 12, 16, 8, 0, -8, -16, -4, 0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_2", t, f(0, 4, 6, 6, 6, 6, 4, 2, 0), f(0, 16, 20, 10, 0, -10, -20, -4, 0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_3", t, f(0, 4, 4, 4, 4, 4, 4, 2, 0), f(0, 20, 24, 12, 0, -12, -24, -4, 0), f(0,0,0,0,0,0,0,0,0));
        c.rot("tail_4", t, f(0, 4, 4, 4, 4, 4, 4, 2, 0), f(0, 22, 28, 14, 0, -14, -28, -4, 0), f(0,0,0,0,0,0,0,0,0));
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
                f(0, 0, 0.9F, -0.9F, 1.0F, 0, 0, 0, 0),
                f(0, -2.8F, -3.2F, -3.4F, -3.5F, 2.4F, 1.6F, -1.2F, 0),
                f(0, 2.4F, 3.0F, 3.2F, 3.4F, -1.8F, -0.8F, 0.4F, 0));
        // True Genshin Primordial Shower: ultra-low trembling crouch (-20 deg) -> explosive sky-roar rear-up (+34 deg)!
        c.rot("torso", t, f(0, -16, -20, -22, -22, 34, 26, -6, 0), f(0, 0, 3, -3, 4, 0, 0, 0, 0), f(0, 0, 3, -3, 3, 0, 0, 0, 0));
        c.rot("neck", t, f(0, -18, -22, -24, -24, 26, 20, -8, 0), f(0,0,4,-4,4,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("head", t, f(0, -12, -16, -18, -18, 20, 14, -4, 0), f(0,0,5,-5,5,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 18, 28, 34, 40, 56, 48, 20, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, 26, 30, 32, 34, 58, 46, 6, 0), f(0, 10, 14, 16, 16, -18, -12, 0, 0), f(0, 22, 26, 28, 30, 36, 26, 6, 0));
        c.rot("forearm_left", t, f(0, 18, 22, 24, 26, 32, 24, 8, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,10,6,0,0));
        c.rot("arm_right", t, f(0, 26, 30, 32, 34, 58, 46, 6, 0), f(0, -10, -14, -16, -16, 18, 12, 0, 0), f(0, -22, -26, -28, -30, -36, -26, -6, 0));
        c.rot("forearm_right", t, f(0, 18, 22, 24, 26, 32, 24, 8, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,-10,-6,0,0));
        c.rot("leg_left", t, f(0, 14, 18, 18, 20, -28, -20, 8, 0), f(0,0,0,0,0,0,0,0,0), f(0,6,8,8,8,8,6,4,0));
        c.rot("shin_left", t, f(0, -18, -22, -22, -24, 14, 8, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, 14, 18, 18, 20, -28, -20, 8, 0), f(0,0,0,0,0,0,0,0,0), f(0,-6,-8,-8,-8,-8,-6,-4,0));
        c.rot("shin_right", t, f(0, -18, -22, -22, -24, 14, 8, -10, 0), f(0,0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0,0));
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
        c.rot("torso", t, f(0, 34, -20, -20, -42, -8, 0), f(0, 0, 0, 0, 14, 0, 0), f(0,0,0,0,0,0,0));
        c.rot("neck", t, f(0, 22, -14, -14, 24, -4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("head", t, f(0, 16, -10, -10, 18, -4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("jaw", t, f(0, 48, 40, 36, 30, 10, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, 90, 20, 20, 42, 6, 0), f(0, -12, 10, 10, 0, 0, 0), f(0, 30, 8, 8, 16, 4, 0));
        c.rot("forearm_left", t, f(0, 32, 10, 10, -12, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 90, 20, 20, 42, 6, 0), f(0, 12, -10, -10, 0, 0, 0), f(0, -30, -8, -8, -16, -4, 0));
        c.rot("forearm_right", t, f(0, 32, 10, 10, -12, 4, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, -28, 14, 14, -26, 6, 0), f(0,0,0,0,0,0,0), f(0,8,6,6,8,2,0));
        c.rot("shin_left", t, f(0, 14, -16, -16, -20, -6, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -28, 14, 14, -26, 6, 0), f(0,0,0,0,0,0,0), f(0,-8,-6,-6,-8,-2,0));
        c.rot("shin_right", t, f(0, 14, -16, -16, -20, -6, 0), f(0,0,0,0,0,0,0), f(0,0,0,0,0,0,0));
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
        c.rot("torso", t, f(0, 36, -14, -16, -12, -14, -6, 0), f(0, 6, -10, -8, -10, -8, -4, 0), f(0, -6, 14, 14, 12, 13, 6, 0));
        c.rot("neck", t, f(0, 28, -20, -24, -20, -22, -8, 0), f(0, 8, -12, 14, -14, 12, 0, 0), f(0, 0, 8, -6, 8, -4, 0, 0));
        c.rot("head", t, f(0, 24, -16, -20, -16, -18, -6, 0), f(0, 10, -16, 20, -20, 16, 0, 0), f(0, 0, 10, -10, 10, -6, 0, 0));
        c.rot("jaw", t, f(0, 48, 28, 24, 26, 22, 10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_left", t, f(0, 64, 22, 20, 22, 18, 8, 0), f(0,0,0,0,0,0,0,0), f(0, 38, 32, 30, 30, 26, 12, 0));
        c.rot("forearm_left", t, f(0, 34, 16, 14, 16, 12, 6, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("arm_right", t, f(0, 64, 14, 12, 14, 10, 4, 0), f(0,0,0,0,0,0,0,0), f(0, -38, -26, -24, -24, -20, -10, 0));
        c.rot("forearm_right", t, f(0, 34, 12, 10, 12, 10, 4, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_left", t, f(0, -28, 18, 20, 18, 18, 8, 0), f(0,0,0,0,0,0,0,0), f(0,8,14,14,12,12,6,0));
        c.rot("shin_left", t, f(0, 12, -22, -24, -22, -20, -10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
        c.rot("leg_right", t, f(0, -28, 18, 20, 18, 18, 8, 0), f(0,0,0,0,0,0,0,0), f(0,-8,-14,-14,-12,-12,-6,0));
        c.rot("shin_right", t, f(0, 12, -22, -24, -22, -20, -10, 0), f(0,0,0,0,0,0,0,0), f(0,0,0,0,0,0,0,0));
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
        c.rot("torso", t, f(0, 32, -10, -16, -18), f(0, 0, -8, -12, -12), f(0, 0, 10, 16, 18));
        c.rot("neck", t, f(0, 26, -16, -24, -26), f(0, 0, -12, -18, -20), f(0, 0, 8, 14, 16));
        c.rot("head", t, f(0, 20, -12, -20, -22), f(0, 0, -14, -22, -24), f(0, 0, 10, 18, 20));
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

    // =========================================================================
    // 15. DORMANT SLEEP (4.40s C1-periodic — Genshin 1.mp4 00:00–00:03):
    //     Curled flat on the arena stone, head resting on folded forepaws, slow rhythmic breathing!
    // =========================================================================
    private static Clip build_dormant_sleep() {
        Clip c = new Clip("dormant_sleep", 4.40F, true);
        float[] t = f(0.0F, 1.10F, 2.20F, 3.30F, 4.40F);
        // Belly & chest resting flat on the floor (-11.6px) with gentle deep breathing rise/fall
        c.pos("body", t, f(0, 0, 0, 0, 0), f(-11.8F, -10.9F, -10.5F, -11.3F, -11.8F), f(0, 0, 0, 0, 0));
        c.rot("torso", t, f(-10.0F, -8.0F, -7.0F, -9.0F, -10.0F), f(6.0F, 6.0F, 6.0F, 6.0F, 6.0F), f(2.0F, 2.0F, 2.0F, 2.0F, 2.0F));
        c.rot("neck", t, f(-16.0F, -14.5F, -13.5F, -15.0F, -16.0F), f(14.0F, 14.0F, 14.0F, 14.0F, 14.0F), f(4.0F, 4.0F, 4.0F, 4.0F, 4.0F));
        c.rot("head", t, f(-10.0F, -9.0F, -8.5F, -9.5F, -10.0F), f(16.0F, 16.0F, 16.0F, 16.0F, 16.0F), f(8.0F, 8.0F, 8.0F, 8.0F, 8.0F));
        c.rot("jaw", t, f(1.0F, 3.5F, 5.0F, 2.5F, 1.0F), f(0,0,0,0,0), f(0,0,0,0,0));
        // Forelimbs folded forward & tucked flat under chin
        c.rot("arm_left", t, f(46.0F, 44.0F, 43.0F, 45.0F, 46.0F), f(-18.0F, -18.0F, -18.0F, -18.0F, -18.0F), f(24.0F, 24.0F, 24.0F, 24.0F, 24.0F));
        c.rot("forearm_left", t, f(26.0F, 25.0F, 24.0F, 25.0F, 26.0F), f(0,0,0,0,0), f(-22.0F, -22.0F, -22.0F, -22.0F, -22.0F));
        c.rot("arm_right", t, f(46.0F, 44.0F, 43.0F, 45.0F, 46.0F), f(18.0F, 18.0F, 18.0F, 18.0F, 18.0F), f(-24.0F, -24.0F, -24.0F, -24.0F, -24.0F));
        c.rot("forearm_right", t, f(26.0F, 25.0F, 24.0F, 25.0F, 26.0F), f(0,0,0,0,0), f(22.0F, 22.0F, 22.0F, 22.0F, 22.0F));
        // Hind legs tucked tightly beside flanks
        c.rot("leg_left", t, f(34.0F, 33.0F, 32.0F, 33.0F, 34.0F), f(0,0,0,0,0), f(18.0F, 18.0F, 18.0F, 18.0F, 18.0F));
        c.rot("shin_left", t, f(-38.0F, -37.0F, -36.0F, -37.0F, -38.0F), f(0,0,0,0,0), f(-10.0F, -10.0F, -10.0F, -10.0F, -10.0F));
        c.rot("leg_right", t, f(34.0F, 33.0F, 32.0F, 33.0F, 34.0F), f(0,0,0,0,0), f(-18.0F, -18.0F, -18.0F, -18.0F, -18.0F));
        c.rot("shin_right", t, f(-38.0F, -37.0F, -36.0F, -37.0F, -38.0F), f(0,0,0,0,0), f(10.0F, 10.0F, 10.0F, 10.0F, 10.0F));
        // Heavy lotus tail curled smoothly around the side on the stone floor
        c.rot("tail_1", t, f(6.0F, 7.0F, 7.5F, 6.5F, 6.0F), f(-20.0F, -20.0F, -20.0F, -20.0F, -20.0F), f(0,0,0,0,0));
        c.rot("tail_2", t, f(4.0F, 4.5F, 5.0F, 4.5F, 4.0F), f(-26.0F, -25.0F, -24.0F, -25.0F, -26.0F), f(0,0,0,0,0));
        c.rot("tail_3", t, f(2.0F, 2.5F, 3.0F, 2.5F, 2.0F), f(-30.0F, -29.0F, -28.0F, -29.0F, -30.0F), f(0,0,0,0,0));
        c.rot("tail_4", t, f(0.0F, 1.0F, 1.5F, 0.5F, 0.0F), f(-32.0F, -30.0F, -29.0F, -31.0F, -32.0F), f(0,0,0,0,0));
        return c;
    }
}
