// Frame-accurate, high-impact Genshin Ruin Guard animations (15 Catmull-Rom clips)
// Tuned with slow mechanical anticipation -> 3-4 frame ultra-fast LINEAR snap -> heavy impact hold & recoil.
// Coordinate space verified directly against RuinGuardModel.java:
//   +X = mob's right, +Y = down, -Z = forward
//   xRot: downward limbs negative = forward/up, positive = backward; upward torso positive = lean forward
//   yRot: positive = turn left, negative = turn right
//   zRot: right limb (-X/+X): right arm negative = outward right; left arm positive = outward left
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

    // 0: dormant_kneeling
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
                new float[]{82.0F, 82.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-18.0F, -18.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-48.0F, -48.0F}, new float[]{0.0F, 0.0F}, new float[]{6.0F, 6.0F}, new int[]{0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{82.0F, 82.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 3.2F},
                new float[]{-18.0F, -18.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        CLIPS.put("dormant_kneeling", c);
    }

    // 1: awaken_bootup (2.60s)
    private static void initClip_1() {
        Clip c = new Clip("awaken_bootup", 2.6000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.70F, 1.65F, 2.05F, 2.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-12.0F, -11.2F, 1.2F, -1.5F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.70F, 1.70F, 2.60F},
                new float[]{16.0F, 12.0F, -3.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.70F, 1.60F, 2.10F, 2.60F},
                new float[]{30.0F, 22.0F, -8.0F, 6.0F, 4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.35F, 0.55F, 0.85F, 2.60F},
                new float[]{0.25F, 1.35F, 0.60F, 1.45F, 1.00F}, new float[]{0.25F, 1.35F, 0.60F, 1.45F, 1.00F}, new float[]{0.25F, 1.35F, 0.60F, 1.45F, 1.00F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.50F, 2.10F, 2.60F},
                new float[]{-28.0F, 10.0F, -8.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-14.0F, -28.0F, -18.0F, -8.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.50F, 2.60F},
                new float[]{-20.0F, -10.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.50F, 2.10F, 2.60F},
                new float[]{-28.0F, 10.0F, -8.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{14.0F, 28.0F, 18.0F, 8.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.50F, 2.60F},
                new float[]{-20.0F, -10.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-48.0F, -6.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-6.0F, -2.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{82.0F, 10.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-18.0F, -4.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-48.0F, -6.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{6.0F, 2.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{82.0F, 10.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.65F, 2.60F},
                new float[]{-18.0F, -4.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        CLIPS.put("awaken_bootup", c);
    }

    // 2: idle_combat (2.80s loop)
    private static void initClip_2() {
        Clip c = new Clip("idle_combat", 2.8000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -1.2F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{4.0F, 7.0F, 4.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{0.96F, 1.12F, 0.96F}, new float[]{0.96F, 1.12F, 0.96F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-4.0F, -8.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -11.0F, -8.0F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -16.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-4.0F, -8.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{8.0F, 11.0F, 8.0F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.4F, 2.8F},
                new float[]{-12.0F, -16.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        CLIPS.put("idle_combat", c);
    }

    // 3: walk_combat (1.60s loop) — Heavy mechanical march with crisp footfall drop
    private static void initClip_3() {
        Clip c = new Clip("walk_combat", 1.6000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.22F, 0.40F, 0.80F, 1.02F, 1.20F, 1.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 1.8F, -2.6F, 0.0F, 1.8F, -2.6F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0, 0, 1, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{0.0F, 2.5F, 0.0F, 2.5F, 0.0F},
                new float[]{0.0F, -6.0F, 0.0F, 6.0F, 0.0F},
                new float[]{0.0F, 6.0F, 0.0F, -6.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 9.0F, 0.0F, -9.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{6.0F, 10.0F, 6.0F, 10.0F, 6.0F},
                new float[]{0.0F, 4.0F, 0.0F, -4.0F, 0.0F},
                new float[]{0.0F, -7.0F, 0.0F, 7.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.22F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{0.0F, -30.0F, -16.0F, 8.0F, 22.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.22F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{4.0F, 42.0F, 14.0F, 4.0F, 14.0F, 4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.22F, 0.40F, 1.20F, 1.60F},
                new float[]{-4.0F, -12.0F, 2.0F, -14.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.02F, 1.20F, 1.60F},
                new float[]{0.0F, 22.0F, 0.0F, -30.0F, -16.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.02F, 1.20F, 1.60F},
                new float[]{4.0F, 14.0F, 4.0F, 42.0F, 14.0F, 4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.02F, 1.20F, 1.60F},
                new float[]{-4.0F, -14.0F, -4.0F, -12.0F, 2.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{0.0F, 22.0F, 0.0F, -24.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-9.0F, -12.0F, -9.0F, -12.0F, -9.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.40F, 1.20F, 1.60F},
                new float[]{-14.0F, -10.0F, -24.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.40F, 0.80F, 1.20F, 1.60F},
                new float[]{0.0F, -24.0F, 0.0F, 22.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{9.0F, 12.0F, 9.0F, 12.0F, 9.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.40F, 1.20F, 1.60F},
                new float[]{-14.0F, -24.0F, -10.0F, -14.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("walk_combat", c);
    }

    // 4: missile_barrage (2.40s = 48 ticks; fire window 1.15s..1.75s)
    private static void initClip_4() {
        Clip c = new Clip("missile_barrage", 2.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.65F, 1.00F, 1.20F, 1.40F, 1.60F, 1.80F, 2.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -10.2F, -10.6F, -12.0F, -10.8F, -12.2F, -10.8F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.65F, 1.00F, 1.20F, 1.40F, 1.60F, 1.80F, 2.40F},
                new float[]{4.0F, 32.0F, 34.0F, 39.0F, 32.0F, 39.0F, 33.0F, 4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 2.5F, -2.5F, 2.5F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 0, 1, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, 28.0F, 28.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -10.0F, -10.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, 28.0F, 28.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 10.0F, 10.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.65F, 1.20F, 1.80F, 2.40F},
                new float[]{1.0F, 1.38F, 1.60F, 1.32F, 1.0F}, new float[]{1.0F, 1.38F, 1.60F, 1.32F, 1.0F}, new float[]{1.0F, 1.15F, 1.25F, 1.10F, 1.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.65F, 1.20F, 1.80F, 2.40F},
                new float[]{-4.0F, -36.0F, -40.0F, -34.0F, -4.0F},
                new float[]{0.0F, -14.0F, -16.0F, -12.0F, 0.0F},
                new float[]{-8.0F, -46.0F, -50.0F, -44.0F, -8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{-12.0F, -22.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 12.0F, 10.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.65F, 1.20F, 1.80F, 2.40F},
                new float[]{-4.0F, -36.0F, -40.0F, -34.0F, -4.0F},
                new float[]{0.0F, 14.0F, 16.0F, 12.0F, 0.0F},
                new float[]{8.0F, 46.0F, 50.0F, 44.0F, 8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{-12.0F, -22.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -12.0F, -10.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, -44.0F, -46.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -13.0F, -13.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, 76.0F, 78.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, -32.0F, -32.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 8.0F, 8.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, -44.0F, -46.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 13.0F, 13.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, 76.0F, 78.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.65F, 1.80F, 2.40F},
                new float[]{0.0F, -32.0F, -32.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -8.0F, -8.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("missile_barrage", c);
    }

    // 5: melee_combo_3hit (3.40s = 68 ticks) — HEAVY SNAP-CURVE OVERHAUL:
    // Hit 1: slow coil to 0.73s -> 0.12s EXPLOSIVE SNAP to 0.85s (tick 17) -> overshoot settle
    // Hit 2: slow coil to 1.43s -> 0.12s EXPLOSIVE SNAP to 1.55s (tick 31) -> overshoot settle
    // Hit 3: pre-hop crouch 1.98s -> airborne apex 2.34s -> 0.16s VIOLENT SLAM at 2.50s (tick 50) -> heavy ground hold
    private static void initClip_5() {
        Clip c = new Clip("melee_combo_3hit", 3.4000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.73F, 0.85F, 1.02F, 1.43F, 1.55F, 1.98F, 2.34F, 2.50F, 2.64F, 2.86F, 3.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -2.5F, -5.2F, -4.0F, -2.5F, -5.2F, -7.5F, 4.5F, -12.5F, -11.0F, -11.6F, 0.0F},
                new float[]{0.0F, 2.0F, -3.8F, -3.0F, 1.5F, -4.0F, 1.5F, -2.0F, -5.0F, -4.5F, -4.5F, 0.0F},
                new int[]{0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0});
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.55F, 0.73F, 0.85F, 1.02F, 1.43F, 1.55F, 1.98F, 2.34F, 2.50F, 3.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -52.0F, -58.0F, 56.0F, 60.0F, 58.0F, -56.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.73F, 0.85F, 1.43F, 1.55F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{4.0F, -12.0F, 24.0F, -10.0F, 24.0F, 22.0F, -18.0F, 48.0F, 42.0F, 4.0F},
                new float[]{0.0F, -16.0F, 18.0F, 16.0F, -18.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 10.0F, -11.0F, -10.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 1, 1, 1, 1, 0, 1, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.55F, 0.73F, 0.85F, 1.02F, 1.43F, 1.55F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{-4.0F, -24.0F, -30.0F, -88.0F, -82.0F, 20.0F, 24.0F, 28.0F, -162.0F, -54.0F, -56.0F, -4.0F},
                new float[]{0.0F, -36.0F, -44.0F, 48.0F, 44.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-8.0F, -84.0F, -92.0F, -14.0F, -16.0F, -24.0F, -26.0F, -24.0F, -18.0F, -12.0F, -12.0F, -8.0F},
                new int[]{0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.73F, 0.85F, 2.34F, 2.50F, 3.40F},
                new float[]{-12.0F, -14.0F, -26.0F, -18.0F, -8.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 1, 0});
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.73F, 0.85F, 1.28F, 1.43F, 1.55F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{-4.0F, 18.0F, 22.0F, -24.0F, -30.0F, -88.0F, 28.0F, -162.0F, -54.0F, -56.0F, -4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 36.0F, 44.0F, -48.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{8.0F, 24.0F, 28.0F, 84.0F, 92.0F, 14.0F, 24.0F, 18.0F, 12.0F, 12.0F, 8.0F},
                new int[]{0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 1.43F, 1.55F, 2.34F, 2.50F, 3.40F},
                new float[]{-12.0F, -14.0F, -26.0F, -18.0F, -8.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 1, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{0.0F, -38.0F, -24.0F, -52.0F, -48.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -8.0F, -6.0F, -12.0F, -12.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{0.0F, 68.0F, 52.0F, 88.0F, 82.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 3.40F},
                new float[]{0.0F, -28.0F, 18.0F, -36.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{0.0F, -38.0F, -24.0F, -52.0F, -48.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 8.0F, 6.0F, 12.0F, 12.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 2.86F, 3.40F},
                new float[]{0.0F, 68.0F, 52.0F, 88.0F, 82.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.98F, 2.34F, 2.50F, 3.40F},
                new float[]{0.0F, -28.0F, 18.0F, -36.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 1, 0});
        CLIPS.put("melee_combo_3hit", c);
    }

    // 6: double_clap (2.10s = 42 ticks; 0.12s EXPLOSIVE CLAP SNAP at 0.93s -> 1.05s = tick 21)
    private static void initClip_6() {
        Clip c = new Clip("double_clap", 2.1000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.65F, 0.93F, 1.05F, 1.28F, 2.10F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -2.5F, -3.2F, -6.2F, -5.0F, 0.0F},
                new float[]{0.0F, 2.6F, 3.2F, -4.5F, -3.6F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.65F, 0.93F, 1.05F, 1.28F, 2.10F},
                new float[]{4.0F, -16.0F, -20.0F, 28.0F, 22.0F, 4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.65F, 0.93F, 1.05F, 1.28F, 2.10F},
                new float[]{-4.0F, -64.0F, -72.0F, -86.0F, -82.0F, -4.0F},
                new float[]{0.0F, -50.0F, -58.0F, 42.0F, 36.0F, 0.0F},
                new float[]{-8.0F, -82.0F, -88.0F, 22.0F, 16.0F, -8.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{-12.0F, -8.0F, -18.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 18.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.65F, 0.93F, 1.05F, 1.28F, 2.10F},
                new float[]{-4.0F, -64.0F, -72.0F, -86.0F, -82.0F, -4.0F},
                new float[]{0.0F, 50.0F, 58.0F, -42.0F, -36.0F, 0.0F},
                new float[]{8.0F, 82.0F, 88.0F, -22.0F, -16.0F, 8.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{-12.0F, -8.0F, -18.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, -18.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{0.0F, -18.0F, -28.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -6.0F, -10.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{0.0F, 34.0F, 54.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{0.0F, -18.0F, -28.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 6.0F, 10.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.93F, 1.05F, 2.10F},
                new float[]{0.0F, 34.0F, 54.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        CLIPS.put("double_clap", c);
    }

    // 7: stomp_shockwave (1.80s = 36 ticks; 0.12s EXPLOSIVE STOMP at 0.78s -> 0.90s = tick 18)
    private static void initClip_7() {
        Clip c = new Clip("stomp_shockwave", 1.8000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.58F, 0.78F, 0.90F, 1.16F, 1.80F},
                new float[]{0.0F, -2.8F, -3.2F, 0.8F, 0.0F, 0.0F},
                new float[]{0.0F, 2.6F, 3.2F, -6.5F, -5.2F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.58F, 0.78F, 0.90F, 1.80F},
                new float[]{0.0F, -10.0F, -12.0F, 12.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -12.0F, -14.0F, 6.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.58F, 0.78F, 0.90F, 1.80F},
                new float[]{4.0F, -12.0F, -15.0F, 24.0F, 4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -9.0F, -11.0F, 6.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.58F, 0.78F, 0.90F, 1.80F},
                new float[]{0.0F, -66.0F, -72.0F, -16.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -11.0F, -12.0F, -2.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.58F, 0.78F, 0.90F, 1.80F},
                new float[]{0.0F, 92.0F, 98.0F, 30.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.78F, 0.90F, 1.80F},
                new float[]{0.0F, -26.0F, -14.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.78F, 0.90F, 1.80F},
                new float[]{-4.0F, 22.0F, -26.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -58.0F, -34.0F, -8.0F}, new int[]{0, 1, 1, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.78F, 0.90F, 1.80F},
                new float[]{-12.0F, -8.0F, -22.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.78F, 0.90F, 1.80F},
                new float[]{-4.0F, 22.0F, -26.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 58.0F, 34.0F, 8.0F}, new int[]{0, 1, 1, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.78F, 0.90F, 1.80F},
                new float[]{-12.0F, -8.0F, -22.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        CLIPS.put("stomp_shockwave", c);
    }

    // 8: cyclone_spin_windup (1.10s = 22 ticks)
    private static void initClip_8() {
        Clip c = new Clip("cyclone_spin_windup", 1.1000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.65F, 1.10F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -4.8F, -4.4F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.65F, 1.10F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -52.0F, 120.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.65F, 1.10F},
                new float[]{4.0F, 9.0F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -16.0F, -16.0F}, new int[]{0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 16.0F, 16.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{-4.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -76.0F, -76.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.0F, 1.14F, 1.16F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{-12.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{-4.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{8.0F, 76.0F, 76.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.0F, 1.14F, 1.16F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{-12.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, -22.0F, -20.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -9.0F, -9.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, 44.0F, 40.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, -22.0F, -20.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 6.0F, 6.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, -22.0F, -20.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 9.0F, 9.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, 44.0F, 40.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.70F, 1.10F},
                new float[]{0.0F, -22.0F, -20.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -6.0F, -6.0F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_windup", c);
    }

    // 9: cyclone_spin_active (0.52s per 360-deg revolution, looping)
    private static void initClip_9() {
        Clip c = new Clip("cyclone_spin_active", 0.5200F, true);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.4F, -2.6F, -4.8F, -2.6F, -4.4F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 90.0F, 180.0F, 270.0F, 360.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{1, 1, 1, 1, 1});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.26F, 0.52F},
                new float[]{7.0F, 9.0F, 7.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-16.0F, -16.0F}, new int[]{0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{16.0F, 16.0F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-76.0F, -76.0F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 0.52F},
                new float[]{1.0F, 1.0F}, new float[]{1.16F, 1.16F}, new float[]{1.0F, 1.0F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{76.0F, 76.0F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 0.52F},
                new float[]{1.0F, 1.0F}, new float[]{1.16F, 1.16F}, new float[]{1.0F, 1.0F}, new int[]{0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.52F},
                new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{-24.0F, -38.0F, -12.0F, -6.0F, -24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -8.0F, -8.0F, -8.0F, -8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{42.0F, 64.0F, 24.0F, 20.0F, 42.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.26F, 0.52F},
                new float[]{-18.0F, -12.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{5.0F, 5.0F, 5.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{-12.0F, -6.0F, -24.0F, -38.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 8.0F, 8.0F, 8.0F, 8.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.13F, 0.26F, 0.39F, 0.52F},
                new float[]{24.0F, 20.0F, 42.0F, 64.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.26F, 0.52F},
                new float[]{-12.0F, -18.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-5.0F, -5.0F, -5.0F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_active", c);
    }

    // 10: cyclone_spin_recovery (1.20s = 24 ticks)
    private static void initClip_10() {
        Clip c = new Clip("cyclone_spin_recovery", 1.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.60F, 1.20F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{-4.4F, -2.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0F, 0.50F, 0.88F, 1.20F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 110.0F, 10.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-16.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{16.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{0.0F, -10.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{-76.0F, -14.0F, -8.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.16F, 1.0F, 1.0F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{0.0F, -10.0F, -4.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{76.0F, 14.0F, 8.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0F, 0.75F, 1.20F},
                new float[]{1.0F, 1.0F, 1.0F}, new float[]{1.16F, 1.0F, 1.0F}, new float[]{1.0F, 1.0F, 1.0F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{0.0F, -12.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{0.0F, -12.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{-20.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-8.0F, 0.0F}, new int[]{0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{40.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{-20.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{5.0F, 0.0F}, new int[]{0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{-20.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{8.0F, 0.0F}, new int[]{0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{40.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{0.0F, 0.0F}, new int[]{0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.20F},
                new float[]{-20.0F, 0.0F}, new float[]{0.0F, 0.0F}, new float[]{-5.0F, 0.0F}, new int[]{0, 0});
        CLIPS.put("cyclone_spin_recovery", c);
    }

    // 11: leap_slam (2.70s = 54 ticks; airborne 0.65s..1.30s; VIOLENT 0.17s IMPACT at 1.18s -> 1.35s = tick 27)
    private static void initClip_11() {
        Clip c = new Clip("leap_slam", 2.7000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.55F, 0.65F, 1.05F, 1.18F, 1.35F, 1.50F, 1.85F, 2.70F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -10.5F, -10.8F, 4.5F, 3.0F, -13.5F, -11.8F, -12.2F, 0.0F},
                new float[]{0.0F, 2.2F, 2.2F, -2.0F, -3.0F, -4.5F, -4.2F, -4.2F, 0.0F}, new int[]{0, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{4.0F, 30.0F, -18.0F, 50.0F, 44.0F, 4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{-4.0F, 38.0F, -162.0F, -54.0F, -56.0F, -4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-8.0F, -28.0F, -18.0F, -14.0F, -14.0F, -8.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 2.70F},
                new float[]{-12.0F, -24.0F, -16.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{-4.0F, 38.0F, -162.0F, -54.0F, -56.0F, -4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{8.0F, 28.0F, 18.0F, 14.0F, 14.0F, 8.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 2.70F},
                new float[]{-12.0F, -24.0F, -16.0F, -8.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{0.0F, -48.0F, -24.0F, -54.0F, -50.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -10.0F, -6.0F, -12.0F, -12.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{0.0F, 82.0F, 50.0F, 92.0F, 86.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 2.70F},
                new float[]{0.0F, -34.0F, 16.0F, -38.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 4.0F, 0.0F, 4.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{0.0F, -48.0F, -24.0F, -54.0F, -50.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 10.0F, 6.0F, 12.0F, 12.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 1.85F, 2.70F},
                new float[]{0.0F, 82.0F, 50.0F, 92.0F, 86.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.55F, 1.18F, 1.35F, 2.70F},
                new float[]{0.0F, -34.0F, 16.0F, -38.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -4.0F, 0.0F, -4.0F, 0.0F}, new int[]{0, 0, 1, 1, 0});
        CLIPS.put("leap_slam", c);
    }

    // 12: phase2_overdrive (2.40s)
    private static void initClip_12() {
        Clip c = new Clip("phase2_overdrive", 2.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -6.5F, 1.5F, 1.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{4.0F, 24.0F, -24.0F, -18.0F, 4.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{1.0F, 1.45F, 1.70F, 1.25F}, new float[]{1.0F, 1.45F, 1.70F, 1.25F}, new float[]{1.0F, 1.20F, 1.30F, 1.10F}, new int[]{0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{1.0F, 1.45F, 1.20F}, new float[]{1.0F, 1.45F, 1.20F}, new float[]{1.0F, 1.20F, 1.10F}, new int[]{0, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{0.0F, 28.0F, 8.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -14.0F, -4.0F}, new int[]{0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0F, 1.15F, 2.40F},
                new float[]{0.0F, 28.0F, 8.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 14.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{-4.0F, -42.0F, 22.0F, 16.0F, -4.0F},
                new float[]{0.0F, 26.0F, -28.0F, -24.0F, 0.0F},
                new float[]{-8.0F, -16.0F, -78.0F, -68.0F, -10.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -44.0F, -14.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 1.65F, 2.40F},
                new float[]{-4.0F, -42.0F, 22.0F, 16.0F, -4.0F},
                new float[]{0.0F, -26.0F, 28.0F, 24.0F, 0.0F},
                new float[]{8.0F, 16.0F, 78.0F, 68.0F, 10.0F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.15F, 2.40F},
                new float[]{-12.0F, -44.0F, -14.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("phase2_overdrive", c);
    }

    // 13: stunned_collapse (2.00s)
    private static void initClip_13() {
        Clip c = new Clip("stunned_collapse", 2.0000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.35F, 1.10F, 1.40F, 2.00F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 1.0F, -12.8F, -11.8F, -12.5F},
                new float[]{0.0F, 2.0F, -2.0F, -2.0F, -2.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.35F, 0.65F, 1.15F, 2.00F},
                new float[]{4.0F, -18.0F, -8.0F, 38.0F, 40.0F},
                new float[]{0.0F, -8.0F, 8.0F, 0.0F, 0.0F},
                new float[]{0.0F, 6.0F, -6.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.30F, 0.55F, 0.85F, 2.00F},
                new float[]{1.20F, 0.10F, 0.90F, 0.15F, 0.18F}, new float[]{1.20F, 0.10F, 0.90F, 0.15F, 0.18F}, new float[]{1.00F, 0.10F, 0.90F, 0.15F, 0.18F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-4.0F, 22.0F, -32.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -32.0F, -16.0F, -16.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.15F, 2.00F},
                new float[]{-12.0F, -16.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-4.0F, 22.0F, -32.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 32.0F, 16.0F, 16.0F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.15F, 2.00F},
                new float[]{-12.0F, -16.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, -48.0F, -48.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -10.0F, -10.0F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, 84.0F, 84.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, -34.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 4.0F, 4.0F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, -48.0F, -48.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 10.0F, 10.0F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, 84.0F, 84.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.10F, 2.00F},
                new float[]{0.0F, -34.0F, -34.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, -4.0F, -4.0F}, new int[]{0, 0, 0});
        CLIPS.put("stunned_collapse", c);
    }

    // 14: death_shutdown (2.80s)
    private static void initClip_14() {
        Clip c = new Clip("death_shutdown", 2.8000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.55F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -2.0F, -13.0F, -21.0F, -21.0F},
                new float[]{0.0F, 2.0F, -4.0F, -14.0F, -14.0F}, new int[]{0, 0, 0, 1, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 18.0F, 78.0F, 80.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 4.0F, 4.0F}, new int[]{0, 0, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.55F, 1.30F, 2.15F, 2.80F},
                new float[]{4.0F, -16.0F, 26.0F, 10.0F, 10.0F},
                new float[]{0.0F, -12.0F, 6.0F, 0.0F, 0.0F},
                new float[]{0.0F, 8.0F, -4.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.45F, 2.80F},
                new float[]{1.40F, 0.0F, 0.0F}, new float[]{1.40F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 0.60F, 2.80F},
                new float[]{1.20F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new float[]{1.20F, 0.0F, 0.0F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-4.0F, -24.0F, -64.0F, -66.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-8.0F, -34.0F, -40.0F, -42.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{-4.0F, -24.0F, -64.0F, -66.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{8.0F, 34.0F, 40.0F, 42.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, -46.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -8.0F, -10.0F, -10.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 82.0F, 22.0F, 22.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, -46.0F, -10.0F, -10.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 8.0F, 10.0F, 10.0F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.30F, 2.15F, 2.80F},
                new float[]{0.0F, 82.0F, 22.0F, 22.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 0, 0});
        CLIPS.put("death_shutdown", c);
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

    private static float evalCatmullHermite(float[] v, float[] times, int i, float u, float dt, boolean loop) {
        int n = v.length;
        int i0 = i > 0 ? i - 1 : (loop ? n - 2 : 0);
        int i3 = i + 2 < n ? i + 2 : (loop ? 1 : n - 1);
        float p0 = v[i0], p1 = v[i], p2 = v[i + 1], p3 = v[i3];
        float m1 = 0.5F * (p2 - p0);
        float m2 = 0.5F * (p3 - p1);
        float u2 = u * u, u3 = u2 * u;
        return (2 * u3 - 3 * u2 + 1) * p1 + (u3 - 2 * u2 + u) * m1 + (-2 * u3 + 3 * u2) * p2 + (u3 - u2) * m2;
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
