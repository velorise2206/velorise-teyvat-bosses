// Frame-accurate, high-impact Genshin Ruin Guard animations (17 clips)
// Features time-normalized slope-clamped Catmull-Rom splines, slow mechanical anticipation,
// 3-4 frame ultra-fast LINEAR snaps, heavy impact hold & recoil, dedicated Stun Recovery,
// and Phase 2 Overdrive Finisher.
// Coordinate space verified directly against RuinGuardModel.java:
//   +X = mob's right, +Y = down, -Z = forward
//   xRot: downward limbs negative = forward/up, positive = backward; upward torso positive = lean forward
//   yRot: positive = turn left, negative = turn right
//   zRot: right arm negative = outward right; left arm positive = outward left
package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RuinGuardAnimations {
    private RuinGuardAnimations() {}

    public static final int CH_ROT = 0;
    public static final int CH_POS = 1;
    public static final int CH_SCALE = 2;
    public static final int INTERP_SMOOTH = 0;
    public static final int INTERP_LINEAR = 1;

    private static final class Track {
        final String bone;
        final int channel;
        final float[] times, xs, ys, zs;
        final int[] interps;

        Track(String bone, int channel, float[] times, float[] xs, float[] ys, float[] zs, int[] interps) {
            this.bone = bone;
            this.channel = channel;
            this.times = times;
            this.xs = xs;
            this.ys = ys;
            this.zs = zs;
            this.interps = interps;
        }
    }

    public static final class Clip {
        public final String name;
        public final float lengthSeconds;
        public final boolean loop;
        final List<Track> tracks = new ArrayList<>();

        Clip(String name, float lengthSeconds, boolean loop) {
            this.name = name;
            this.lengthSeconds = lengthSeconds;
            this.loop = loop;
        }

        void add(String bone, int ch, float[] t, float[] x, float[] y, float[] z, int[] interps) {
            tracks.add(new Track(bone, ch, t, x, y, z, interps));
        }
    }

    private static final Map<String, Clip> CLIPS = new HashMap<>();
    private static final float DEG2RAD = (float) (Math.PI / 180.0D);

    // 0: dormant_kneeling (3.20s loop)
    private static void initClip_0() {
        Clip c = new Clip("dormant_kneeling", 3.2000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 1.6F, 3.2F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{-12.0F, -12.4F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{16.0F, 16.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 1.6F, 3.2F},
                new float[]{30.0F, 32.0F, 30.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 1.6F, 3.2F},
                new float[]{0.25F, 0.35F, 0.25F}, new float[]{0.25F, 0.35F, 0.25F}, new float[]{0.25F, 0.35F, 0.25F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-28.0F, -28.0F}, new float[]{0.0F, 0.0F}, new float[]{-14.0F, -14.0F}, new int[]{0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-20.0F, -20.0F}, new float[]{0.0F, 0.0F}, new float[]{6.0F, 6.0F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-28.0F, -28.0F}, new float[]{0.0F, 0.0F}, new float[]{14.0F, 14.0F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-20.0F, -20.0F}, new float[]{0.0F, 0.0F}, new float[]{-6.0F, -6.0F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-48.0F, -48.0F}, new float[]{0.0F, 0.0F}, new float[]{-6.0F, -6.0F}, new int[]{0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{44.0F, 44.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-6.0F, -6.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-48.0F, -48.0F}, new float[]{0.0F, 0.0F}, new float[]{6.0F, 6.0F}, new int[]{0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{44.0F, 44.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-6.0F, -6.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        CLIPS.put("dormant_kneeling", c);
    }

    // 1: awaken_bootup (2.60s = 52 ticks) — ends cleanly in idle_combat stance
    private static void initClip_1() {
        Clip c = new Clip("awaken_bootup", 2.6000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.70F, 1.65F, 2.05F, 2.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-12.0F, -11.2F, 0.8F, -3.2F, -2.2F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.70F, 1.70F, 2.60F},
                new float[]{16.0F, 12.0F, -3.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.70F, 1.60F, 2.10F, 2.60F},
                new float[]{30.0F, 22.0F, -8.0F, 9.0F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.35F, 0.55F, 0.85F, 2.60F},
                new float[]{0.25F, 1.35F, 0.60F, 1.45F, 0.96F}, new float[]{0.25F, 1.35F, 0.60F, 1.45F, 0.96F}, new float[]{0.25F, 1.35F, 0.60F, 1.45F, 1.00F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.50F, 2.10F, 2.60F},
                new float[]{-28.0F, 10.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-14.0F, -28.0F, -20.0F, -15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.50F, 2.60F},
                new float[]{-20.0F, -10.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.50F, 2.10F, 2.60F},
                new float[]{-28.0F, 10.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{14.0F, 28.0F, 20.0F, 15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.50F, 2.60F},
                new float[]{-20.0F, -10.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-48.0F, -6.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-6.0F, -4.0F, -7.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{82.0F, 12.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-18.0F, -6.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 2.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-48.0F, -6.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{6.0F, 4.0F, 7.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{82.0F, 12.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-18.0F, -6.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -2.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("awaken_bootup", c);
    }

    // 2: idle_combat (2.80s loop) — Authentic Genshin wide, low-CG mechanical combat stance
    private static void initClip_2() {
        Clip c = new Clip("idle_combat", 2.8000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{-2.2F, -3.4F, -2.2F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{7.0F, 10.5F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.7F, 2.1F, 2.8F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 3.0F, -3.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{0.96F, 1.14F, 0.96F}, new float[]{0.96F, 1.14F, 0.96F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-8.0F, -13.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -19.0F, -15.0F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-18.0F, -24.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-8.0F, -13.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{15.0F, 19.0F, 15.0F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-18.0F, -24.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -15.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -7.5F, -7.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{24.0F, 30.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -15.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -15.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{7.0F, 7.5F, 7.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{24.0F, 30.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -15.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("idle_combat", c);
    }

    // 3: walk_combat (2.40s = 48-tick loop, 24 ticks = 1.20s/step) — Authentic Genshin heavy stomp march:
    // Weight shift -> deliberate high-knee lift -> flat-sole stomp plant -> heavy pendulum arms (zero-slide kinematics)
    private static void initClip_3() {
        Clip c = new Clip("walk_combat", 2.4000F, true);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.36F, 0.60F, 0.78F, 1.20F, 1.56F, 1.80F, 1.98F, 2.40F},
                new float[]{0.0F, -2.5F, 1.6F, 2.0F, 0.0F, 2.5F, -1.6F, -2.0F, 0.0F},
                new float[]{-0.5F, -0.3F, -2.1F, -1.6F, -0.5F, -0.3F, -2.1F, -1.6F, -0.5F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 1, 0, 0, 0, 1, 0, 0});
        c.add("pelvis", CH_ROT,
                new float[]{0.0F, 0.36F, 0.60F, 1.20F, 1.56F, 1.80F, 2.40F},
                new float[]{3.0F, 1.5F, 5.5F, 3.0F, 1.5F, 5.5F, 3.0F},
                new float[]{0.0F, -5.0F, -7.5F, 0.0F, 5.0F, 7.5F, 0.0F},
                new float[]{0.0F, -5.5F, 6.5F, 0.0F, 5.5F, -6.5F, 0.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.60F, 1.20F, 1.80F, 2.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 10.0F, 0.0F, -10.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.60F, 1.20F, 1.80F, 2.40F},
                new float[]{7.0F, 12.0F, 7.0F, 12.0F, 7.0F},
                new float[]{0.0F, 4.0F, 0.0F, -4.0F, 0.0F},
                new float[]{0.0F, -7.5F, 0.0F, 7.5F, 0.0F},
                new int[]{0, 0, 0, 0, 0});
        // Right leg: deliberate high-knee lift @ 0.36s (hip -50, knee +76) -> firm stomp @ 0.60s -> flat-sole stance 0.60s..1.80s
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.36F, 0.50F, 0.60F, 1.20F, 1.80F, 2.08F, 2.40F},
                new float[]{-14.0F, -50.0F, -32.0F, -26.0F, -2.0F, 26.0F, 10.0F, -14.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F},
                new int[]{0, 0, 0, 1, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.36F, 0.50F, 0.60F, 1.20F, 1.80F, 2.08F, 2.40F},
                new float[]{54.0F, 76.0F, 26.0F, 16.0F, 12.0F, 6.0F, 34.0F, 54.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.36F, 0.50F, 0.60F, 1.20F, 1.80F, 2.08F, 2.40F},
                new float[]{-32.0F, -22.0F, 6.0F, 10.0F, -10.0F, -32.0F, -36.0F, -32.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 1, 0, 0});
        // Left leg: exact 1.20s half-cycle offset
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.88F, 1.20F, 1.56F, 1.70F, 1.80F, 2.40F},
                new float[]{-2.0F, 26.0F, 10.0F, -14.0F, -50.0F, -32.0F, -26.0F, -2.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F},
                new int[]{1, 1, 0, 0, 0, 0, 1, 1});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.88F, 1.20F, 1.56F, 1.70F, 1.80F, 2.40F},
                new float[]{12.0F, 6.0F, 34.0F, 54.0F, 76.0F, 26.0F, 16.0F, 12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 0, 0, 0, 0, 1, 1});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.88F, 1.20F, 1.56F, 1.70F, 1.80F, 2.40F},
                new float[]{-10.0F, -32.0F, -36.0F, -32.0F, -22.0F, 6.0F, 10.0F, -10.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 0, 0, 0, 0, 1, 1});
        // Heavy, low-hanging pendulum arms matching Genshin (subtle +/-14 deg lag behind torso)
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.20F, 1.92F, 2.40F},
                new float[]{-6.0F, 12.0F, -6.0F, -16.0F, -6.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-14.0F, -17.0F, -14.0F, -17.0F, -14.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.92F, 2.40F},
                new float[]{-14.0F, -10.0F, -20.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.20F, 1.92F, 2.40F},
                new float[]{-6.0F, -16.0F, -6.0F, 12.0F, -6.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{14.0F, 17.0F, 14.0F, 17.0F, 14.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.92F, 2.40F},
                new float[]{-14.0F, -20.0F, -10.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("walk_combat", c);
    }

    // 4: missile_barrage (3.20s = 64 ticks) — Authentic Genshin 180-deg waist swivel (back faces target),
    // wide spread-eagle stabilizer crouch (arms splayed out-down +/-58 deg), 6-missile salvo 1.00..2.00s, slow swivel back to 360 (=0) deg
    private static void initClip_4() {
        Clip c = new Clip("missile_barrage", 3.2000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.75F, 1.00F, 1.20F, 1.40F, 1.60F, 1.80F, 2.00F, 2.30F, 3.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -10.8F, -11.2F, -12.6F, -11.2F, -12.6F, -11.2F, -12.2F, -10.4F, -2.2F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 180.0F, 180.0F, 360.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.85F, 1.20F, 1.40F, 1.60F, 1.80F, 2.25F, 3.20F},
                new float[]{7.0F, 28.0F, 35.0F, 28.0F, 35.0F, 28.0F, 26.0F, 7.0F},
                new float[]{0.0F, 0.0F, 3.5F, -3.5F, 3.5F, -3.5F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 1, 0, 1, 0, 0, 0});
        c.add("missile_pod_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.20F, 1.40F, 1.60F, 2.25F, 3.20F},
                new float[]{0.0F, 34.0F, 48.0F, 32.0F, 48.0F, 30.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -14.0F, -18.0F, -12.0F, -18.0F, -12.0F, 0.0F},
                new int[]{0, 0, 1, 0, 1, 0, 0});
        c.add("missile_pod_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.30F, 1.50F, 1.70F, 2.25F, 3.20F},
                new float[]{0.0F, 34.0F, 48.0F, 32.0F, 48.0F, 30.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 14.0F, 18.0F, 12.0F, 18.0F, 12.0F, 0.0F},
                new int[]{0, 0, 1, 0, 1, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.85F, 1.50F, 2.25F, 3.20F},
                new float[]{0.96F, 1.45F, 1.65F, 1.30F, 0.96F}, new float[]{0.96F, 1.45F, 1.65F, 1.30F, 0.96F}, new float[]{1.0F, 1.15F, 1.25F, 1.10F, 1.0F}, new int[]{0, 0, 0, 0, 0});
        // Arms splayed wide out-down like heavy stabilizer struts during the barrage
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-8.0F, -18.0F, -18.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -58.0F, -58.0F, -15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-18.0F, -8.0F, -8.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-8.0F, -18.0F, -18.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{15.0F, 58.0F, 58.0F, 15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-18.0F, -8.0F, -8.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-12.0F, -48.0F, -48.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -14.0F, -14.0F, -7.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{24.0F, 82.0F, 82.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-12.0F, -34.0F, -34.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 8.0F, 8.0F, 4.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-12.0F, -48.0F, -48.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 14.0F, 14.0F, 7.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{24.0F, 82.0F, 82.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.85F, 2.25F, 3.20F},
                new float[]{-12.0F, -34.0F, -34.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -8.0F, -8.0F, -4.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("missile_barrage", c);
    }

    // 5: melee_combo_3hit (6.40s = 128 ticks) — Exact 12fps frame-matched Genshin Ruin Guard 3-Hit Combo:
    // 0.00..0.55s: 2-fist clap opener in front of chest (orange spark @ 0.35s)
    // 0.55..1.45s: HIT 1 with RIGHT ARM (upper_arm_r extended zRot -84, waist -15 -> +155 deg, crosses 0 deg @ 1.40s / tick 28)
    // 1.45..2.58s: HIT 2 CONTINUOUS SPIN WITH LEFT ARM! (waist turns back +155 -> +185 deg, right arm retracts, LEFT arm extends zRot +86 and whips diagonal-down as waist completes +185 -> +360 deg, crossing 0 deg @ 2.50s / tick 50 — ends facing straight forward at 360 = 0 deg with ZERO unwind!)
    // 2.58..3.45s: Deep pre-jump crouch -> 3.45..4.25s feet-first body-slam jump -> 4.30s (tick 86) ground slam -> slow hydraulic stand-up (6.40s)
    private static void initClip_5() {
        Clip c = new Clip("melee_combo_3hit", 6.4000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.35F, 0.55F, 1.25F, 1.45F, 1.95F, 2.35F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 4.50F, 5.15F, 5.78F, 6.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -3.8F, -3.6F, -3.0F, -5.8F, -4.0F, -3.2F, -6.8F, -5.0F, -10.5F, 4.2F, 1.2F, -13.6F, -12.0F, -12.6F, -6.4F, -2.2F},
                new float[]{0.0F, 0.6F, 0.6F, 2.0F, -4.0F, -1.5F, 1.4F, -4.8F, -1.8F, 2.4F, -1.6F, -2.6F, -4.4F, -4.0F, -4.2F, -1.8F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0});
        // Single continuous 360-deg spin: Hit 1 (-15 -> +155 with right arm) -> back turn (+185) -> Hit 2 (+185 -> +360 with left arm)!
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.05F, 1.25F, 1.45F, 1.95F, 2.35F, 2.58F, 3.45F, 4.30F, 6.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, -12.0F, -15.0F, 155.0F, 180.0F, 190.0F, 360.0F, 360.0F, 360.0F, 360.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.25F, 1.45F, 1.95F, 2.35F, 2.58F, 3.45F, 3.88F, 4.16F, 4.30F, 4.50F, 5.15F, 5.78F, 6.40F},
                new float[]{7.0F, 14.0F, 14.0F, -6.0F, 20.0F, 10.0F, -8.0F, 34.0F, 28.0F, 12.0F, 18.0F, 38.0F, 34.0F, 36.0F, 20.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, -10.0F, 14.0F, 6.0F, -10.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 8.0F, -10.0F, -4.0F, 12.0F, -16.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("shoulder_r", CH_ROT,
                new float[]{0.0F, 0.55F, 1.25F, 1.45F, 1.95F, 3.45F, 6.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, -14.0F, -14.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("shoulder_l", CH_ROT,
                new float[]{0.0F, 1.45F, 1.95F, 2.35F, 2.58F, 3.45F, 6.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 14.0F, 18.0F, 10.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0});
        // RIGHT ARM: attacks on Hit 1 (zRot -84 horizontal sweep), then retracts as counterbalance during Hit 2
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.05F, 1.25F, 1.45F, 1.95F, 2.35F, 2.58F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{-8.0F, -52.0F, -52.0F, -6.0F, -6.0F, -8.0F, 18.0F, 24.0F, 28.0F, 18.0F, -16.0F, -18.0F, -28.0F, -30.0F, -18.0F, -8.0F},
                new float[]{0.0F, 34.0F, 34.0F, -10.0F, -12.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-15.0F, -8.0F, -8.0F, -80.0F, -84.0F, -84.0F, -26.0F, -24.0F, -32.0F, -28.0F, -34.0F, -36.0F, -42.0F, -38.0F, -22.0F, -15.0F},
                new int[]{0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("upper_arm_r", CH_SCALE,
                new float[]{0.0F, 1.05F, 1.45F, 1.95F, 6.40F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F},
                new float[]{1.0F, 1.08F, 1.10F, 1.0F, 1.0F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F},
                new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.25F, 1.45F, 1.95F, 3.45F, 3.88F, 4.30F, 5.15F, 6.40F},
                new float[]{-18.0F, -42.0F, -42.0F, -8.0F, -8.0F, -24.0F, -24.0F, -28.0F, -20.0F, -20.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0});
        // LEFT ARM: tucks on Hit 1, then extends wide (zRot +88, raised high-back) and whips diagonally downward across front on Hit 2!
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.25F, 1.45F, 1.95F, 2.35F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{-8.0F, -52.0F, -52.0F, 16.0F, 22.0F, -14.0F, -22.0F, -38.0F, -16.0F, 18.0F, -16.0F, -18.0F, -28.0F, -30.0F, -18.0F, -8.0F},
                new float[]{0.0F, -34.0F, -34.0F, 0.0F, 0.0F, 12.0F, 18.0F, -24.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{15.0F, 8.0F, 8.0F, 24.0F, 28.0F, 82.0F, 88.0F, 52.0F, 32.0F, 28.0F, 34.0F, 36.0F, 42.0F, 38.0F, 22.0F, 15.0F},
                new int[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("upper_arm_l", CH_SCALE,
                new float[]{0.0F, 1.95F, 2.58F, 3.00F, 6.40F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F},
                new float[]{1.0F, 1.08F, 1.12F, 1.0F, 1.0F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F},
                new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.35F, 0.55F, 1.95F, 2.35F, 2.58F, 3.45F, 3.88F, 4.30F, 5.15F, 6.40F},
                new float[]{-18.0F, -42.0F, -42.0F, -10.0F, -6.0F, -14.0F, -24.0F, -28.0F, -20.0F, -20.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0});
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 1.25F, 1.45F, 2.20F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{-12.0F, -22.0F, 14.0F, -10.0F, -26.0F, -18.0F, -46.0F, -32.0F, -22.0F, -52.0F, -48.0F, -26.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-7.0F, -9.0F, -8.0F, -9.0F, -11.0F, -8.0F, -10.0F, -6.0F, -6.0F, -14.0F, -14.0F, -9.0F, -7.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 1.25F, 1.45F, 2.20F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{24.0F, 42.0F, 16.0F, 24.0F, 50.0F, 36.0F, 78.0F, 62.0F, 44.0F, 88.0F, 82.0F, 48.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 1.45F, 2.58F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 6.40F},
                new float[]{-12.0F, -28.0F, -24.0F, -32.0F, 18.0F, 12.0F, -36.0F, -34.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F},
                new int[]{0, 0, 0, 0, 0, 1, 1, 0, 0});
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 1.25F, 1.45F, 2.20F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{-12.0F, 10.0F, -24.0F, -18.0F, 14.0F, -18.0F, -46.0F, -32.0F, -22.0F, -52.0F, -48.0F, -26.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{7.0F, 8.0F, 11.0F, 9.0F, 8.0F, 8.0F, 10.0F, 6.0F, 6.0F, 14.0F, 14.0F, 9.0F, 7.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 1.25F, 1.45F, 2.20F, 2.58F, 3.00F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 5.78F, 6.40F},
                new float[]{24.0F, 18.0F, 46.0F, 36.0F, 16.0F, 36.0F, 78.0F, 62.0F, 44.0F, 88.0F, 82.0F, 48.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 1.45F, 2.58F, 3.45F, 3.88F, 4.16F, 4.30F, 5.15F, 6.40F},
                new float[]{-12.0F, -22.0F, -30.0F, -32.0F, 18.0F, 12.0F, -36.0F, -34.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.0F, -4.0F, -4.0F, -4.0F, 0.0F, 0.0F, -4.0F, -4.0F, -4.0F},
                new int[]{0, 0, 0, 0, 0, 1, 1, 0, 0});
        CLIPS.put("melee_combo_3hit", c);
    }

    // 6: double_clap (2.60s = 52 ticks; impact @ 1.15s = tick 23) — Authentic Genshin wide wing-spread clap (zero gimbal lock!)
    private static void initClip_6() {
        Clip c = new Clip("double_clap", 2.6000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -4.2F, -4.8F, -7.8F, -6.8F, -4.2F, -2.2F},
                new float[]{0.0F, 2.0F, 2.4F, -4.6F, -3.8F, -1.6F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{7.0F, 24.0F, 34.0F, 42.0F, 36.0F, 20.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        // Wide wing spread using zRot +/-84 deg -> slams both massive arms together in front (-58 xRot, +/-6 zRot)
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{-8.0F, -10.0F, -12.0F, -58.0F, -54.0F, -28.0F, -8.0F},
                new float[]{0.0F, -14.0F, -20.0F, 34.0F, 28.0F, 10.0F, 0.0F},
                new float[]{-15.0F, -58.0F, -66.0F, 6.0F, 2.0F, -12.0F, -15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-18.0F, -8.0F, -24.0F, -22.0F, -18.0F}, new float[]{0.0F, -6.0F, 16.0F, 12.0F, 0.0F}, new float[]{0.0F, 0.0F, 8.0F, 6.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{-8.0F, -10.0F, -12.0F, -58.0F, -54.0F, -28.0F, -8.0F},
                new float[]{0.0F, 14.0F, 20.0F, -34.0F, -28.0F, -10.0F, 0.0F},
                new float[]{15.0F, 58.0F, 66.0F, -6.0F, -2.0F, 12.0F, 15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-18.0F, -8.0F, -24.0F, -22.0F, -18.0F}, new float[]{0.0F, 6.0F, -16.0F, -12.0F, 0.0F}, new float[]{0.0F, 0.0F, -8.0F, -6.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -22.0F, -34.0F, -30.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -9.0F, -12.0F, -11.0F, -7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{24.0F, 42.0F, 64.0F, 58.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -20.0F, -30.0F, -28.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F, 4.0F, 4.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -22.0F, -34.0F, -30.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 9.0F, 12.0F, 11.0F, 7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{24.0F, 42.0F, 64.0F, 58.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -20.0F, -30.0F, -28.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F, -4.0F, -4.0F}, new int[]{0, 1, 1, 0, 0});
        CLIPS.put("double_clap", c);
    }

    // 7: stomp_shockwave (2.20s = 44 ticks; piledriver stomp @ 1.00s = tick 20)
    // Authentic Genshin upright stomp (ruinguard_dam_chan_1.mp4): torso/pelvis stay vertical & grounded (no sideways tipping!)
    private static void initClip_7() {
        Clip c = new Clip("stomp_shockwave", 2.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{0.0F, -0.6F, -0.8F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -1.6F, -1.4F, -6.2F, -5.0F, -2.2F},
                new float[]{0.0F, 0.2F, 0.3F, -1.4F, -1.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{0.0F, -2.0F, -2.5F, 6.0F, 4.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -2.0F, -2.5F, 1.5F, 0.5F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{7.0F, 2.0F, 1.0F, 16.0F, 12.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -1.5F, -2.0F, 1.5F, 0.5F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        // Right leg lifts cleanly & holds briefly at peak (0.56s..0.86s) before vertical piledriver stomp @ 1.00s
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-12.0F, -54.0F, -58.0F, -16.0F, -14.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -7.0F, -7.0F, -7.0F, -7.0F, -7.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{24.0F, 74.0F, 80.0F, 32.0F, 28.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 2.20F},
                new float[]{-12.0F, -20.0F, -22.0F, -16.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F, 4.0F, 4.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-12.0F, -13.0F, -24.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 7.0F, 8.0F, 7.5F, 7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{24.0F, 26.0F, 46.0F, 38.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-12.0F, -13.0F, -22.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F, -4.0F}, new int[]{0, 1, 1, 0});
        // Arms stay low & relaxed at sides (+/-22 deg) matching Genshin's grounded silhouette
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-8.0F, -4.0F, -14.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -22.0F, -18.0F, -16.0F, -15.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-18.0F, -16.0F, -22.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-8.0F, -4.0F, -14.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{15.0F, 22.0F, 18.0F, 16.0F, 15.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-18.0F, -16.0F, -22.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        CLIPS.put("stomp_shockwave", c);
    }

    // 8: cyclone_spin_windup (1.40s = 28 ticks) — heavy T-pose lift, deep -85 deg counter-coil, rev-up to 0 deg
    private static void initClip_8() {
        Clip c = new Clip("cyclone_spin_windup", 1.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.72F, 1.00F, 1.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-2.2F, -5.8F, -6.4F, -5.4F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.68F, 0.96F, 1.20F, 1.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -56.0F, -85.0F, 120.0F, 360.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 1});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.40F},
                new float[]{7.0F, 12.0F, 14.0F, 13.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -6.0F, -6.0F}, new int[]{0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 6.0F, 6.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-8.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -42.0F, -44.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.0F, 1.12F, 1.16F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-18.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-8.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{15.0F, 42.0F, 44.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.0F, 1.12F, 1.16F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-18.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-12.0F, -26.0F, -22.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -9.0F, -8.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{24.0F, 50.0F, 40.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-12.0F, -24.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 5.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-12.0F, -26.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{7.0F, 9.0F, 8.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{24.0F, 50.0F, 28.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.72F, 1.40F},
                new float[]{-12.0F, -24.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -5.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_windup", c);
    }

    // 9: cyclone_spin_active (1.00s = 20 exact ticks: 3 fierce Genshin torso revolutions @ 1080 deg/s + grounded 2-step march)
    private static void initClip_9() {
        Clip c = new Clip("cyclone_spin_active", 1.0000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.25F, 0.50F, 0.75F, 1.00F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-5.4F, -3.8F, -5.6F, -3.8F, -5.4F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.3333F, 0.6667F, 1.0000F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 360.0F, 720.0F, 1080.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{1, 1, 1, 1});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.50F, 1.00F},
                new float[]{13.0F, 15.0F, 13.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-6.0F, -6.0F}, new int[]{0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{6.0F, 6.0F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-44.0F, -44.0F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 1.00F},
                new float[]{1.0F, 1.0F}, new float[]{1.16F, 1.16F}, new float[]{1.0F, 1.0F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{44.0F, 44.0F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 1.00F},
                new float[]{1.0F, 1.0F}, new float[]{1.16F, 1.16F}, new float[]{1.0F, 1.0F}, new int[]{0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{-10.0F, -10.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.00F},
                new float[]{-10.0F, -10.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.25F, 0.50F, 0.75F, 1.00F},
                new float[]{-22.0F, -42.0F, -14.0F, -8.0F, -22.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -8.0F, -8.0F, -8.0F, -8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.25F, 0.50F, 0.75F, 1.00F},
                new float[]{40.0F, 64.0F, 28.0F, 22.0F, 40.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.50F, 1.00F},
                new float[]{-18.0F, -14.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.25F, 0.50F, 0.75F, 1.00F},
                new float[]{-14.0F, -8.0F, -22.0F, -42.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 8.0F, 8.0F, 8.0F, 8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.25F, 0.50F, 0.75F, 1.00F},
                new float[]{28.0F, 22.0F, 40.0F, 64.0F, 28.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.50F, 1.00F},
                new float[]{-14.0F, -18.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_active", c);
    }

    // 10: cyclone_spin_recovery (2.20s = 44 ticks) — TRUE 2-revolution inertial coast (0 -> 300 -> 545 -> 685 -> 734 -> 716 -> 720),
    // heavy braking stomp @ 1.10s, arms droop gradually as centrifugal force dies, slow hydraulic stand-up
    private static void initClip_10() {
        Clip c = new Clip("cyclone_spin_recovery", 2.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.55F, 1.10F, 1.40F, 1.80F, 2.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-5.4F, -6.2F, -8.0F, -7.2F, -4.2F, -2.2F},
                new float[]{0.0F, 0.0F, -1.8F, -1.4F, -0.5F, 0.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.35F, 0.75F, 1.15F, 1.45F, 1.78F, 2.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 300.0F, 545.0F, 685.0F, 734.0F, 716.0F, 720.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{1, 0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.65F, 1.15F, 1.65F, 2.20F},
                new float[]{13.0F, 16.0F, 22.0F, 14.0F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.60F, 1.25F, 1.75F, 2.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-6.0F, -5.0F, -2.0F, 1.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.60F, 1.25F, 1.75F, 2.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{6.0F, 5.0F, 2.0F, -1.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.55F, 1.15F, 1.65F, 2.20F},
                new float[]{0.0F, -2.0F, -12.0F, -16.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-44.0F, -38.0F, -24.0F, -12.0F, -15.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 0.65F, 1.30F, 2.20F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F}, new float[]{1.16F, 1.10F, 1.0F, 1.0F}, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.55F, 1.15F, 1.65F, 2.20F},
                new float[]{0.0F, -2.0F, -12.0F, -16.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{44.0F, 38.0F, 24.0F, 12.0F, 15.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 0.65F, 1.30F, 2.20F},
                new float[]{1.0F, 1.0F, 1.0F, 1.0F}, new float[]{1.16F, 1.10F, 1.0F, 1.0F}, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-10.0F, -12.0F, -24.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-10.0F, -12.0F, -24.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-22.0F, -34.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -10.0F, -8.0F, -7.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{40.0F, 62.0F, 38.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-18.0F, -28.0F, -18.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{5.0F, 6.0F, 5.0F, 4.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-22.0F, -34.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 10.0F, 8.0F, 7.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{40.0F, 62.0F, 38.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.10F, 1.65F, 2.20F},
                new float[]{-18.0F, -28.0F, -18.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-5.0F, -6.0F, -5.0F, -4.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("cyclone_spin_recovery", c);
    }

    // 11: leap_slam (3.90s = 78 ticks) — deep hydraulic crouch 0.85s, dramatic tucked overhead leap apex 1.32s,
    // GORILLA-CRATER GROUND SLAM @ 1.80s (tick 36), 0.85s heavy crater lock (1.80..2.65s), 1.25s slow 2-stage stand-up (2.65..3.90s)
    private static void initClip_11() {
        Clip c = new Clip("leap_slam", 3.9000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.72F, 0.85F, 1.32F, 1.65F, 1.80F, 2.02F, 2.65F, 3.28F, 3.90F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -11.4F, -11.8F, 5.4F, 1.8F, -14.4F, -12.8F, -13.4F, -6.8F, -2.2F},
                new float[]{0.0F, 2.8F, 2.8F, -2.0F, -3.2F, -5.8F, -5.2F, -5.4F, -2.2F, 0.0F},
                new int[]{0, 0, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.02F, 2.65F, 3.28F, 3.90F},
                new float[]{7.0F, 34.0F, -22.0F, -6.0F, 48.0F, 42.0F, 44.0F, 24.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{-8.0F, 40.0F, -154.0F, -130.0F, -54.0F, -56.0F, -28.0F, -8.0F},
                new float[]{0.0F, 0.0F, -10.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-15.0F, -26.0F, -20.0F, -18.0F, -24.0F, -24.0F, -18.0F, -15.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.90F},
                new float[]{-18.0F, -26.0F, -36.0F, -24.0F, -6.0F, -8.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{-8.0F, 40.0F, -154.0F, -130.0F, -54.0F, -56.0F, -28.0F, -8.0F},
                new float[]{0.0F, 0.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{15.0F, 26.0F, 20.0F, 18.0F, 24.0F, 24.0F, 18.0F, 15.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.90F},
                new float[]{-18.0F, -26.0F, -36.0F, -24.0F, -6.0F, -8.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{-12.0F, -52.0F, -42.0F, -24.0F, -62.0F, -58.0F, -30.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-7.0F, -10.0F, -6.0F, -6.0F, -14.0F, -14.0F, -9.0F, -7.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{24.0F, 88.0F, 84.0F, 48.0F, 102.0F, 96.0F, 54.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.90F},
                new float[]{-12.0F, -36.0F, 22.0F, 14.0F, -40.0F, -38.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{4.0F, 4.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{-12.0F, -52.0F, -42.0F, -24.0F, -62.0F, -58.0F, -30.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{7.0F, 10.0F, 6.0F, 6.0F, 14.0F, 14.0F, 9.0F, 7.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.28F, 3.90F},
                new float[]{24.0F, 88.0F, 84.0F, 48.0F, 102.0F, 96.0F, 54.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.76F, 1.32F, 1.65F, 1.80F, 2.65F, 3.90F},
                new float[]{-12.0F, -36.0F, 22.0F, 14.0F, -40.0F, -38.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.0F, -4.0F, 0.0F, 0.0F, -4.0F, -4.0F, -4.0F},
                new int[]{0, 0, 0, 1, 1, 0, 0});
        CLIPS.put("leap_slam", c);
    }

    // 12: phase2_overdrive (2.40s = 48 ticks)
    private static void initClip_12() {
        Clip c = new Clip("phase2_overdrive", 2.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-2.2F, -6.8F, 1.2F, 0.6F, -2.2F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{7.0F, 24.0F, -24.0F, -18.0F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{1.0F, 1.45F, 1.75F, 1.15F}, new float[]{1.0F, 1.45F, 1.75F, 1.15F}, new float[]{1.0F, 1.20F, 1.30F, 1.05F}, new int[]{0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{1.0F, 1.45F, 1.15F}, new float[]{1.0F, 1.45F, 1.15F}, new float[]{1.0F, 1.20F, 1.05F}, new int[]{0, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{0.0F, 30.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -14.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{0.0F, 30.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 14.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{-8.0F, -42.0F, 22.0F, 16.0F, -8.0F},
                new float[]{0.0F, 26.0F, -28.0F, -24.0F, 0.0F},
                new float[]{-15.0F, -16.0F, -78.0F, -68.0F, -15.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-18.0F, -44.0F, -14.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{-8.0F, -42.0F, 22.0F, 16.0F, -8.0F},
                new float[]{0.0F, -26.0F, 28.0F, 24.0F, 0.0F},
                new float[]{15.0F, 16.0F, 78.0F, 68.0F, 15.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-18.0F, -44.0F, -14.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -28.0F, -10.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -9.0F, -7.0F, -7.0F}, new int[]{0, 0, 1, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{24.0F, 52.0F, 20.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -24.0F, -10.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F, 4.0F}, new int[]{0, 0, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -28.0F, -10.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 9.0F, 7.0F, 7.0F}, new int[]{0, 0, 1, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{24.0F, 52.0F, 20.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -24.0F, -10.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F, -4.0F}, new int[]{0, 0, 1, 0});
        CLIPS.put("phase2_overdrive", c);
    }

    // 13: stunned_collapse (2.00s = 40 ticks; holds collapsed pose while renderer layers electrical shudder)
    private static void initClip_13() {
        Clip c = new Clip("stunned_collapse", 2.0000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.35F, 1.10F, 1.40F, 2.00F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, 0.6F, -12.8F, -11.8F, -12.5F},
                new float[]{0.0F, 2.0F, -2.0F, -2.0F, -2.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.35F, 0.65F, 1.15F, 2.00F},
                new float[]{7.0F, -18.0F, -8.0F, 38.0F, 40.0F},
                new float[]{0.0F, -8.0F, 8.0F, 0.0F, 0.0F},
                new float[]{0.0F, 6.0F, -6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.30F, 0.55F, 0.85F, 2.00F},
                new float[]{1.20F, 0.10F, 0.90F, 0.15F, 0.18F}, new float[]{1.20F, 0.10F, 0.90F, 0.15F, 0.18F}, new float[]{1.00F, 0.10F, 0.90F, 0.15F, 0.18F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-8.0F, 22.0F, -32.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -32.0F, -16.0F, -16.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.15F, 2.00F},
                new float[]{-18.0F, -16.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-8.0F, 22.0F, -32.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{15.0F, 32.0F, 16.0F, 16.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.15F, 2.00F},
                new float[]{-18.0F, -16.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{-12.0F, -48.0F, -48.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -10.0F, -10.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{24.0F, 84.0F, 84.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{-12.0F, -34.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{-12.0F, -48.0F, -48.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{7.0F, 10.0F, 10.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{24.0F, 84.0F, 84.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{-12.0F, -34.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("stunned_collapse", c);
    }

    // 14: death_shutdown (2.80s = 56 ticks; full collapse synced with overridden tickDeath())
    private static void initClip_14() {
        Clip c = new Clip("death_shutdown", 2.8000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.55F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -2.5F, -13.0F, -21.0F, -21.0F},
                new float[]{0.0F, 2.0F, -4.0F, -14.0F, -14.0F}, new int[]{0, 0, 0, 1, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 18.0F, 78.0F, 80.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 4.0F, 4.0F}, new int[]{0, 0, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.55F, 1.30F, 2.15F, 2.80F},
                new float[]{7.0F, -16.0F, 26.0F, 10.0F, 10.0F},
                new float[]{0.0F, -12.0F, 6.0F, 0.0F, 0.0F},
                new float[]{0.0F, 8.0F, -4.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.45F, 2.80F},
                new float[]{1.40F, 0.0F, 0.0F}, new float[]{1.40F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 0.60F, 2.80F},
                new float[]{1.20F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-8.0F, -24.0F, -64.0F, -66.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -34.0F, -40.0F, -42.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-8.0F, -24.0F, -64.0F, -66.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{15.0F, 34.0F, 40.0F, 42.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-12.0F, -46.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -8.0F, -10.0F, -10.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{24.0F, 82.0F, 22.0F, 22.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-12.0F, -46.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 8.0F, 10.0F, 10.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{24.0F, 82.0F, 22.0F, 22.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("death_shutdown", c);
    }

    // 15: stunned_recovery (1.40s = 28 ticks) — Heavy hydraulic stand-up from collapse back to combat stance
    private static void initClip_15() {
        Clip c = new Clip("stunned_recovery", 1.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.45F, 0.95F, 1.18F, 1.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-12.5F, -10.5F, -0.5F, -2.8F, -2.2F},
                new float[]{-2.0F, -1.5F, 0.5F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.45F, 0.95F, 1.20F, 1.40F},
                new float[]{40.0F, 28.0F, -6.0F, 10.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.30F, 0.55F, 0.85F, 1.40F},
                new float[]{0.18F, 1.35F, 0.65F, 1.30F, 0.96F}, new float[]{0.18F, 1.35F, 0.65F, 1.30F, 0.96F}, new float[]{0.18F, 1.20F, 0.65F, 1.15F, 1.00F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.55F, 1.00F, 1.40F},
                new float[]{-34.0F, -18.0F, 8.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-16.0F, -26.0F, -24.0F, -15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-18.0F, -12.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.55F, 1.00F, 1.40F},
                new float[]{-34.0F, -18.0F, 8.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{16.0F, 26.0F, 24.0F, 15.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-18.0F, -12.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-48.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-10.0F, -6.0F, -7.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{84.0F, 16.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-34.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-48.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{10.0F, 6.0F, 7.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{84.0F, 16.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.95F, 1.40F},
                new float[]{-34.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("stunned_recovery", c);
    }

    // 16: overdrive_finisher (3.20s = 64 ticks) — Phase 2 Cataclysmic Core Slam + Overdrive Salvo Finisher:
    // 0.00..0.75s: Deep reactor charge crouch -> 0.75..1.10s: Explosive twin-fist overhead zenith
    // 1.10..1.25s (tick 25): 3-frame LINEAR earth-shatter slam -> 1.25..1.90s: Crater hold & overdrive pod volley
    // 1.90..3.20s: Hydraulic venting & heavy recovery into idle_combat
    private static void initClip_16() {
        Clip c = new Clip("overdrive_finisher", 3.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.55F, 0.75F, 1.08F, 1.25F, 1.42F, 1.90F, 2.45F, 3.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -9.8F, -10.5F, 4.8F, -14.5F, -12.4F, -12.8F, -5.0F, -2.2F},
                new float[]{0.0F, 2.5F, 3.0F, -2.0F, -6.0F, -5.2F, -5.0F, -1.5F, 0.0F}, new int[]{0, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.55F, 0.75F, 1.08F, 1.25F, 1.45F, 1.90F, 2.45F, 3.20F},
                new float[]{7.0F, 26.0F, 30.0F, -26.0F, 54.0F, 46.0F, 44.0F, 16.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{1.0F, 1.65F, 1.95F, 2.10F, 1.55F, 0.96F},
                new float[]{1.0F, 1.65F, 1.95F, 2.10F, 1.55F, 0.96F},
                new float[]{1.0F, 1.30F, 1.40F, 1.45F, 1.20F, 1.00F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 0.75F, 1.25F, 1.90F, 3.20F},
                new float[]{1.0F, 1.55F, 1.75F, 1.45F, 1.0F},
                new float[]{1.0F, 1.55F, 1.75F, 1.45F, 1.0F},
                new float[]{1.0F, 1.30F, 1.35F, 1.20F, 1.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0F, 0.75F, 1.35F, 1.55F, 1.75F, 2.15F, 3.20F},
                new float[]{0.0F, 24.0F, 38.0F, 46.0F, 36.0F, 20.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -12.0F, -18.0F, -22.0F, -16.0F, -8.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0F, 0.75F, 1.35F, 1.55F, 1.75F, 2.15F, 3.20F},
                new float[]{0.0F, 24.0F, 38.0F, 46.0F, 36.0F, 20.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 12.0F, 18.0F, 22.0F, 16.0F, 8.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{0.0F, 8.0F, -16.0F, 14.0F, 10.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -12.0F, -18.0F, -6.0F, -6.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{0.0F, 8.0F, -16.0F, 14.0F, 10.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 12.0F, 18.0F, 6.0F, 6.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 2.45F, 3.20F},
                new float[]{-8.0F, 38.0F, -152.0F, -42.0F, -44.0F, -22.0F, -8.0F},
                new float[]{0.0F, -16.0F, -12.0F, 8.0F, 6.0F, 0.0F, 0.0F},
                new float[]{-15.0F, -38.0F, -18.0F, -10.0F, -12.0F, -18.0F, -15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-18.0F, -32.0F, -38.0F, -6.0F, -10.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 2.45F, 3.20F},
                new float[]{-8.0F, 38.0F, -152.0F, -42.0F, -44.0F, -22.0F, -8.0F},
                new float[]{0.0F, 16.0F, 12.0F, -8.0F, -6.0F, 0.0F, 0.0F},
                new float[]{15.0F, 38.0F, 18.0F, 10.0F, 12.0F, 18.0F, 15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-18.0F, -32.0F, -38.0F, -6.0F, -10.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-12.0F, -46.0F, -20.0F, -56.0F, -52.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -12.0F, -7.0F, -14.0F, -14.0F, -7.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{24.0F, 80.0F, 42.0F, 96.0F, 88.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-12.0F, -34.0F, 14.0F, -40.0F, -36.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 6.0F, 2.0F, 6.0F, 6.0F, 4.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-12.0F, -46.0F, -20.0F, -56.0F, -52.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 12.0F, 7.0F, 14.0F, 14.0F, 7.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{24.0F, 80.0F, 42.0F, 96.0F, 88.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-12.0F, -34.0F, 14.0F, -40.0F, -36.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -6.0F, -2.0F, -6.0F, -6.0F, -4.0F}, new int[]{0, 0, 1, 1, 0, 0});
        CLIPS.put("overdrive_finisher", c);
    }

    static {
        initClip_0();
        initClip_1();
        initClip_2();
        initClip_3();
        initClip_4();
        initClip_5();
        initClip_6();
        initClip_7();
        initClip_8();
        initClip_9();
        initClip_10();
        initClip_11();
        initClip_12();
        initClip_13();
        initClip_14();
        initClip_15();
        initClip_16();
    }

    public static Clip getClip(String name) { return CLIPS.get(name); }

    private static float normalizeTime(Clip clip, float timeSeconds) {
        if (clip.lengthSeconds <= 1e-4F) return 0.0F;
        if (clip.loop) {
            float t = timeSeconds % clip.lengthSeconds;
            return t < 0.0F ? t + clip.lengthSeconds : t;
        }
        return Math.max(0.0F, Math.min(clip.lengthSeconds, timeSeconds));
    }

    /** Time-normalized, slope-clamped Catmull-Rom Hermite spline (eliminates overshoot on uneven keyframe spans). */
    private static float evalCatmullHermite(float[] vals, float[] times, int i, float u, float dt, boolean loop) {
        int n = vals.length;
        float p1 = vals[i];
        float p2 = vals[i + 1];
        float m1, m2;
        if (i > 0) {
            float span = Math.max(1e-4F, times[i + 1] - times[i - 1]);
            m1 = (vals[i + 1] - vals[i - 1]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[1] - times[0]) + (times[n - 1] - times[n - 2]));
            m1 = (vals[1] - vals[n - 2]) / span;
        } else {
            m1 = (p2 - p1) / dt;
        }
        if (i + 2 < n) {
            float span = Math.max(1e-4F, times[i + 2] - times[i]);
            m2 = (vals[i + 2] - vals[i]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[n - 1] - times[n - 2]) + (times[1] - times[0]));
            m2 = (vals[1] - vals[n - 2]) / span;
        } else {
            m2 = (p2 - p1) / dt;
        }
        float secant = (p2 - p1) / dt;
        float maxSlope = Math.abs(secant) * 2.4F + 2.0F;
        m1 = Math.max(-maxSlope, Math.min(maxSlope, m1)) * dt;
        m2 = Math.max(-maxSlope, Math.min(maxSlope, m2)) * dt;
        float u2 = u * u;
        float u3 = u2 * u;
        return (2.0F * u3 - 3.0F * u2 + 1.0F) * p1
                + (u3 - 2.0F * u2 + u) * m1
                + (-2.0F * u3 + 3.0F * u2) * p2
                + (u3 - u2) * m2;
    }

    private static void sampleTrack(Track tr, float t, boolean loop, float[] out) {
        int n = tr.times.length;
        if (n == 1 || t <= tr.times[0]) { out[0] = tr.xs[0]; out[1] = tr.ys[0]; out[2] = tr.zs[0]; return; }
        if (t >= tr.times[n - 1]) { out[0] = tr.xs[n - 1]; out[1] = tr.ys[n - 1]; out[2] = tr.zs[n - 1]; return; }
        int i = 0;
        while (i + 1 < n && tr.times[i + 1] < t) i++;
        float dt = Math.max(1e-5F, tr.times[i + 1] - tr.times[i]);
        float u = (t - tr.times[i]) / dt;
        if (tr.interps[i] == INTERP_LINEAR || tr.interps[i + 1] == INTERP_LINEAR || n < 3) {
            out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * u;
            out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * u;
            out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * u;
        } else {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
        }
    }

    private static void applyWeighted(RuinGuardModel<?> model, Clip clip, float timeSeconds, float weight) {
        if (clip == null || weight <= 1e-4F) return;
        float t = normalizeTime(clip, timeSeconds);
        float[] v = new float[3];
        for (Track tr : clip.tracks) {
            ModelPart part = model.getBone(tr.bone);
            if (part == null || tr.times.length == 0) continue;
            sampleTrack(tr, t, clip.loop, v);
            if (tr.channel == CH_ROT) {
                part.xRot += v[0] * DEG2RAD * weight;
                part.yRot += v[1] * DEG2RAD * weight;
                part.zRot += v[2] * DEG2RAD * weight;
            } else if (tr.channel == CH_POS) {
                part.x += v[0] * weight;
                part.y -= v[1] * weight;
                part.z += v[2] * weight;
            } else if (tr.channel == CH_SCALE) {
                part.xScale = part.xScale + (v[0] - part.xScale) * weight;
                part.yScale = part.yScale + (v[1] - part.yScale) * weight;
                part.zScale = part.zScale + (v[2] - part.zScale) * weight;
            }
        }
    }

    public static void apply(RuinGuardModel<?> model, String clipName, float timeSeconds) {
        model.resetPose();
        applyWeighted(model, CLIPS.get(clipName), timeSeconds, 1.0F);
    }

    public static void applyBlended(RuinGuardModel<?> model, String prevClipName, float prevTimeSeconds, String currClipName, float currTimeSeconds, float blendAlpha) {
        model.resetPose();
        float a = Math.max(0.0F, Math.min(1.0F, blendAlpha));
        float smoothA = a * a * (3.0F - 2.0F * a);
        if (prevClipName != null && smoothA < 0.999F) {
            applyWeighted(model, CLIPS.get(prevClipName), prevTimeSeconds, 1.0F - smoothA);
        }
        applyWeighted(model, CLIPS.get(currClipName), currTimeSeconds, smoothA);
    }
}
