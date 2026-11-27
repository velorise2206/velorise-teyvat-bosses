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
    public static final int INTERP_EASE_IN = 2;
    public static final int INTERP_EASE_OUT = 3;
    public static final int INTERP_EASE_IN_OUT = 4;

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

    // 3: walk_combat (2.40s = 48-tick loop, 24 ticks = 1.20s/step) — Heavy, rigid hydraulic iron-mech march:
    // Locked armored torso (minimal sway) -> stiff hydraulic leg lift & lock -> crisp piston slam stomp @ 0.60s / 1.80s
    private static void initClip_3() {
        Clip c = new Clip("walk_combat", 2.4000F, true);
        // Pronounced vertical march bounce (+0.8px on high knee lift -> -3.4px heavy hydraulic stomp @ 0.60s & 1.80s)
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.34F, 0.48F, 0.60F, 0.76F, 1.20F, 1.54F, 1.68F, 1.80F, 1.96F, 2.40F},
                new float[]{0.0F, -1.2F, -0.6F, 0.9F, 1.0F, 0.0F, 1.2F, 0.6F, -0.9F, -1.0F, 0.0F},
                new float[]{-0.8F, 0.8F, 0.2F, -3.4F, -2.2F, -0.8F, 0.8F, 0.2F, -3.4F, -2.2F, -0.8F},
                new float[]{0.0F, 0.0F, 0.0F, -0.4F, -0.1F, 0.0F, 0.0F, 0.0F, -0.4F, -0.1F, 0.0F},
                new int[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1});
        // Heavy chassis pitch + hip twist supporting long 1.58-block strides
        c.add("pelvis", CH_ROT,
                new float[]{0.0F, 0.34F, 0.60F, 0.76F, 1.20F, 1.54F, 1.80F, 1.96F, 2.40F},
                new float[]{4.5F, 2.0F, 8.5F, 5.5F, 4.5F, 2.0F, 8.5F, 5.5F, 4.5F},
                new float[]{0.0F, -4.5F, -5.5F, -3.0F, 0.0F, 4.5F, 5.5F, 3.0F, 0.0F},
                new float[]{0.0F, -2.4F, 2.4F, 1.4F, 0.0F, 2.4F, -2.4F, -1.4F, 0.0F},
                new int[]{1, 1, 1, 0, 1, 1, 1, 0, 1});
        // Counter-rotating waist torque matching long strides
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.48F, 0.60F, 1.20F, 1.68F, 1.80F, 2.40F},
                new float[]{0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 2.0F, 0.0F},
                new float[]{0.0F, 5.0F, 6.0F, 0.0F, -5.0F, -6.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1});
        // Armored upper hull: heavy forward impact nod on each stomp
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.38F, 0.60F, 0.76F, 1.20F, 1.58F, 1.80F, 1.96F, 2.40F},
                new float[]{8.5F, 6.5F, 14.5F, 10.0F, 8.5F, 6.5F, 14.5F, 10.0F, 8.5F},
                new float[]{0.0F, 2.5F, 3.0F, 1.2F, 0.0F, -2.5F, -3.0F, -1.2F, 0.0F},
                new float[]{0.0F, -1.6F, -2.2F, -1.0F, 0.0F, 1.6F, 2.2F, 1.0F, 0.0F},
                new int[]{1, 1, 1, 0, 1, 1, 1, 0, 1});
        // Right leg: HIGH KNEE LIFT (-74 deg thigh / +102 deg knee @ 0.34s) -> LONG FORWARD PLANT (-42 deg @ 0.60s) -> FULL PUSH-OFF (+36 deg @ 1.80s)
        // Exact geometric stride = 1.58 blocks/step (3.16 blocks / 2.40s cycle)!
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.34F, 0.48F, 0.60F, 1.20F, 1.80F, 2.10F, 2.40F},
                new float[]{-22.0F, -74.0F, -60.0F, -42.0F, -4.0F, 36.0F, 14.0F, -22.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.0F, -4.0F, -4.0F, -4.0F, -4.0F, -4.0F, -4.0F, -4.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.34F, 0.48F, 0.60F, 1.20F, 1.80F, 2.10F, 2.40F},
                new float[]{68.0F, 102.0F, 62.0F, 18.0F, 12.0F, 12.0F, 44.0F, 68.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.34F, 0.48F, 0.60F, 1.20F, 1.80F, 2.10F, 2.40F},
                new float[]{-36.0F, -26.0F, -4.0F, 24.0F, -8.0F, -46.0F, -48.0F, -36.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        // Left leg: exact 1.20s half-cycle offset
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.90F, 1.20F, 1.54F, 1.68F, 1.80F, 2.40F},
                new float[]{-4.0F, 36.0F, 14.0F, -22.0F, -74.0F, -60.0F, -42.0F, -4.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{4.0F, 4.0F, 4.0F, 4.0F, 4.0F, 4.0F, 4.0F, 4.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.90F, 1.20F, 1.54F, 1.68F, 1.80F, 2.40F},
                new float[]{12.0F, 12.0F, 44.0F, 68.0F, 102.0F, 62.0F, 18.0F, 12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.90F, 1.20F, 1.54F, 1.68F, 1.80F, 2.40F},
                new float[]{-8.0F, -46.0F, -48.0F, -36.0F, -26.0F, -4.0F, 24.0F, -8.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        // Wide, rigid heavy-iron arms: stiff industrial stance (+/-18.5 deg Z) with tight piston counter-weight & stomp recoil
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.60F, 0.74F, 1.20F, 1.80F, 1.94F, 2.40F},
                new float[]{-6.0F, 4.0F, 1.5F, -6.0F, -13.0F, -10.5F, -6.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-18.5F, -20.5F, -19.0F, -18.5F, -20.5F, -19.0F, -18.5F},
                new int[]{1, 1, 0, 1, 1, 0, 1});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.60F, 0.74F, 1.20F, 1.80F, 1.94F, 2.40F},
                new float[]{-24.0F, -20.0F, -25.0F, -24.0F, -28.0F, -25.0F, -24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 0, 1, 1, 0, 1});
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.74F, 1.20F, 1.80F, 1.94F, 2.40F},
                new float[]{-6.0F, -13.0F, -10.5F, -6.0F, 4.0F, 1.5F, -6.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{18.5F, 20.5F, 19.0F, 18.5F, 20.5F, 19.0F, 18.5F},
                new int[]{1, 1, 0, 1, 1, 0, 1});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.60F, 0.74F, 1.20F, 1.80F, 1.94F, 2.40F},
                new float[]{-24.0F, -28.0F, -25.0F, -24.0F, -20.0F, -25.0F, -24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 1, 0, 1, 1, 0, 1});
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

    // 5: melee_combo_3hit (7.80s = 156 ticks) — Frame-matched Genshin Ruin Guard 3-Hit NA Combo:
    // Ultra-smooth passing-frame jump whip (+48 -> -94 -> -160), deep forward-lean impact (+46 deg torso),
    // 2-hand ground plant (short braced upper_arm_l + long diagonal upper_arm_r), delayed-arm heavy stand-up.
    private static void initClip_5() {
        Clip c = new Clip("melee_combo_3hit", 7.8000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.25F, 0.40F, 0.85F, 1.92F, 2.20F, 2.48F, 2.85F, 3.02F, 3.20F, 3.55F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{0.0F, 0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{-2.2F,-2.8F, -3.8F, -3.0F, -3.4F, -4.8F, -3.8F, -3.4F, -3.0F, -5.8F, -4.6F,-12.4F,-14.8F,  3.0F,  6.4F,  5.6F, -1.8F,-15.6F,-14.6F,-14.2F, -8.8F, -4.2F, -2.2F},
                new float[]{0.0F, 0.4F,  -0.8F,  0.2F,  1.2F, -2.6F, -1.5F, -0.4F,  0.8F, -3.2F, -1.4F,  1.8F,  2.8F, -0.4F, -1.0F, -1.2F, -1.6F, -2.4F, -2.2F, -2.0F, -1.2F, -0.5F,  0.0F},
                new int[]{  4,    2,     3,     4,     2,     3,     4,     4,     2,     3,     4,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("pelvis", CH_ROT,
                new float[]{0.0F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{0.0F, 9.0F,  13.0F, -5.0F, -6.0F, -3.0F,  6.0F, 16.0F, 14.0F, 13.0F,  7.0F,  2.0F,  0.0F},
                new float[]{0.0F, 0.0F,   0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F, 0.0F,   0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,    4,      3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("waist", CH_ROT,
                new float[]{0.0F, 0.40F, 0.85F, 1.55F, 1.92F, 2.20F, 2.50F, 2.82F, 3.02F, 3.20F, 3.55F, 4.25F, 5.26F, 5.40F, 6.50F, 7.35F, 7.80F},
                new float[]{0.0F, 0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F, 0.0F, 12.0F, 54.0F, 62.0F,-68.0F,-88.0F,-195.0F,-255.0F,-368.0F,-360.0F,-360.0F,-364.0F,-370.0F,-368.0F,-358.0F,-360.0F},
                new float[]{0.0F, 0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,    4,     4,     4,     2,     3,     4,     4,     2,     3,     4,     4,     2,     3,     4,     4,     0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.40F, 0.85F, 1.92F, 2.20F, 2.50F, 2.82F, 3.02F, 3.20F, 3.55F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{7.0F, 5.0F,  7.0F,  4.0F, 14.0F, 10.0F,  8.0F,  4.0F, 18.0F, 12.0F, 19.0F, 24.0F, -6.0F, -8.0F, -2.0F, 20.0F, 46.0F, 42.0F, 40.0F, 24.0F, 12.0F,  7.0F},
                new float[]{0.0F, 0.0F,  0.0F, 14.0F,-16.0F,-12.0F, -8.0F,-12.0F,-18.0F, -4.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F, -5.0F,-10.0F, -9.0F, -8.0F, -3.0F,  2.0F,  0.0F},
                new float[]{0.0F, 0.0F,  0.0F, -5.0F,  7.0F,  4.0F,  2.0F, -6.0F,  9.0F,  3.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  2.0F,  5.0F,  4.0F,  4.0F,  2.0F, -2.0F,  0.0F},
                new int[]{  4,    3,     4,     2,     3,     4,     4,     2,     3,     4,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("shoulder_l", CH_ROT,
                new float[]{0.0F, 0.40F, 0.85F, 1.92F, 2.20F, 2.82F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{0.0F, -6.0F,  0.0F,  0.0F,  0.0F,  0.0F,  8.0F, -8.0F,-18.0F,-18.0F, -2.0F, 14.0F, 12.0F,  4.0F,  0.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F, -6.0F,  0.0F, 14.0F, 10.0F,  0.0F,  8.0F,  6.0F, 20.0F, 20.0F,  6.0F, -6.0F, -6.0F,  2.0F,  2.0F,  0.0F},
                new int[]{  4,     3,     4,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("shoulder_r", CH_ROT,
                new float[]{0.0F, 0.40F, 0.85F, 2.82F, 3.02F, 3.20F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{0.0F, -6.0F,  0.0F, -6.0F,-10.0F,  4.0F,  8.0F, -8.0F,-18.0F,-18.0F, -2.0F, 12.0F, 10.0F,  2.0F, -4.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F,  6.0F,  0.0F,-10.0F,-18.0F,-12.0F, -8.0F, -6.0F,-20.0F,-20.0F, -8.0F,-12.0F,-12.0F,-10.0F, -6.0F,  0.0F},
                new int[]{  4,     3,     4,     4,     2,     3,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        // upper_arm_l (viewer's LEFT):
        //   3.88..4.25s: backward swing windup (+36 -> +48)
        //   4.44s: smooth passing frame straight forward-up (-94, zRot = 0)
        //   4.72..4.98s: peak overhead V-raise (-158 -> -162, zRot = -22)
        //   5.40..6.50s: SHORT/BRACED ground plant near knee (-40, zRot +16, forearm -28)
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.25F, 0.40F, 0.62F, 0.95F, 1.55F, 1.92F, 2.20F, 2.48F, 2.82F, 3.20F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-8.0F,-44.0F,-68.0F,-56.0F,-14.0F,-12.0F,-10.0F,-68.0F,-48.0F,-20.0F, 14.0F, 36.0F, 48.0F,-94.0F,-158.0F,-162.0F,-92.0F,-40.0F,-38.0F,-36.0F,-24.0F,-14.0F, -8.0F},
                new float[]{ 0.0F, 18.0F,-32.0F,-24.0F,  0.0F, 18.0F, 20.0F,-28.0F,-44.0F,-14.0F, 10.0F,  6.0F,  8.0F,  0.0F,  -8.0F, -10.0F, -4.0F, -8.0F, -8.0F, -8.0F, -4.0F,  0.0F,  0.0F},
                new float[]{15.0F, 32.0F, -8.0F, -2.0F, 22.0F, 86.0F, 92.0F, 36.0F, -8.0F, 18.0F, 24.0F, 24.0F, 28.0F,  0.0F, -20.0F, -22.0F,  8.0F, 16.0F, 16.0F, 16.0F, 18.0F, 16.0F, 15.0F},
                new int[]{  4,    2,     3,     4,     4,     4,     2,     3,     4,     4,     4,     4,     3,     3,      4,      2,     2,     3,     4,     4,     4,     4,     0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.25F, 0.40F, 0.62F, 0.95F, 1.92F, 2.20F, 2.48F, 2.82F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-18.0F,-42.0F,-48.0F,-38.0F,-22.0F, -6.0F, -8.0F,-24.0F,-54.0F,-34.0F,-20.0F,-26.0F,-30.0F,-18.0F,-28.0F,-26.0F,-22.0F,-20.0F,-18.0F},
                new float[]{ 0.0F,  0.0F,-12.0F, -8.0F,  0.0F,  0.0F, -6.0F, -8.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ 0.0F,  0.0F, -4.0F, -2.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,    2,     3,     4,     4,     2,     3,     4,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        // upper_arm_r (viewer's RIGHT):
        //   3.88..4.25s: backward swing windup (+36 -> +48)
        //   4.44s: smooth passing frame straight forward-up (-92, zRot = 0)
        //   4.72..4.98s: peak overhead V-raise (-156 -> -160, zRot = +22)
        //   5.40..6.50s: LONG/EXTENDED diagonal ground plant (-66, zRot -30, forearm -8)
        //   7.05..7.45s: delayed heavy arm drag while torso rises first (Genshin 9.50..9.80s!)
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.25F, 0.40F, 0.62F, 0.95F, 1.92F, 2.48F, 2.82F, 3.02F, 3.20F, 3.55F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-8.0F,-44.0F,-68.0F,-56.0F,-14.0F,-16.0F, 12.0F,-22.0F,-48.0F,-68.0F,-32.0F, 36.0F, 48.0F,-92.0F,-156.0F,-160.0F,-104.0F,-66.0F,-64.0F,-62.0F,-36.0F,-16.0F, -8.0F},
                new float[]{ 0.0F,-18.0F, 32.0F, 24.0F,  0.0F, 20.0F, 10.0F,-14.0F,-18.0F, 32.0F, 34.0F, -6.0F, -8.0F,  0.0F,   8.0F,  10.0F,   6.0F, 10.0F, 10.0F, 10.0F,  4.0F,  0.0F,  0.0F},
                new float[]{-15.0F,-32.0F, 8.0F,  2.0F,-22.0F,-16.0F,-26.0F,-82.0F,-108.0F,-38.0F,-20.0F,-24.0F,-28.0F,  0.0F,  20.0F,  22.0F, -14.0F,-30.0F,-30.0F,-28.0F,-44.0F,-26.0F,-15.0F},
                new int[]{  4,    2,     3,     4,     4,     4,     4,     4,     2,     3,     4,     4,     3,     3,      4,      2,      2,     3,     4,     4,     4,     4,     0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.25F, 0.40F, 0.62F, 0.95F, 1.92F, 2.82F, 3.02F, 3.20F, 3.55F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-18.0F,-42.0F,-48.0F,-38.0F,-22.0F,-56.0F,-12.0F, -6.0F,-10.0F,-24.0F,-34.0F,-20.0F,-26.0F,-30.0F,-12.0F, -8.0F, -8.0F,-14.0F,-18.0F,-18.0F},
                new float[]{ 0.0F,  0.0F, 12.0F,  8.0F,  0.0F,  0.0F,  0.0F,  0.0F,  6.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ 0.0F,  0.0F,  4.0F,  2.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,    2,     3,     4,     4,     4,     4,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        // Deep asymmetric lunge-crouch landing (-68/+122 front leg, -54/+106 rear brace leg)
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.95F, 1.92F, 2.20F, 3.02F, 3.20F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-12.0F,-18.0F,-22.0F, 10.0F,-18.0F,-28.0F,-54.0F,-66.0F, 14.0F,-58.0F,-62.0F,-36.0F,-68.0F,-64.0F,-62.0F,-38.0F,-20.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  7.0F,  8.0F, 10.0F,  8.0F,  9.0F, 11.0F, 13.0F, 15.0F,  5.0F, 11.0F, 12.0F, 10.0F, 17.0F, 16.0F, 16.0F, 11.0F,  8.0F,  7.0F},
                new int[]{    4,     4,     2,     3,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.95F, 1.92F, 2.20F, 3.02F, 3.20F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{ 24.0F, 34.0F, 40.0F, 16.0F, 34.0F, 48.0F, 98.0F,116.0F,  6.0F,104.0F,112.0F, 60.0F,122.0F,114.0F,110.0F, 68.0F, 38.0F, 24.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     4,     2,     3,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.95F, 2.20F, 3.20F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-12.0F,-16.0F,-22.0F,-20.0F,-46.0F, 32.0F, 18.0F, 16.0F,  6.0F,-44.0F,-40.0F,-26.0F,-16.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ -4.0F, -4.0F, -4.0F, -4.0F, -6.0F,  0.0F,  0.0F,  0.0F,  0.0F, -8.0F, -7.0F, -5.0F, -4.0F, -4.0F},
                new int[]{    4,     4,     3,     3,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.95F, 1.92F, 2.20F, 3.02F, 3.20F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-12.0F,-18.0F, 10.0F,-24.0F,-22.0F, 12.0F,-54.0F,-66.0F, 14.0F,-40.0F,-44.0F,-32.0F,-54.0F,-50.0F,-48.0F,-32.0F,-18.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ -7.0F, -8.0F, -8.0F,-11.0F, -9.0F, -8.0F,-13.0F,-15.0F, -5.0F, -9.0F,-10.0F,-10.0F,-18.0F,-17.0F,-17.0F,-11.0F, -8.0F, -7.0F},
                new int[]{    4,     4,     2,     3,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.95F, 1.92F, 2.20F, 3.02F, 3.20F, 3.88F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 5.58F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{ 24.0F, 34.0F, 16.0F, 44.0F, 40.0F, 16.0F, 98.0F,116.0F,  6.0F, 76.0F, 84.0F, 56.0F,106.0F, 98.0F, 94.0F, 58.0F, 34.0F, 24.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     4,     2,     3,     2,     3,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     4,     0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.95F, 2.20F, 3.20F, 4.25F, 4.44F, 4.72F, 4.98F, 5.26F, 5.40F, 6.50F, 7.05F, 7.45F, 7.80F},
                new float[]{-12.0F,-16.0F,-20.0F,-24.0F,-46.0F, 32.0F, 14.0F, 12.0F,  6.0F,-38.0F,-36.0F,-24.0F,-15.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  4.0F,  4.0F,  4.0F,  4.0F,  6.0F,  0.0F,  0.0F,  0.0F,  0.0F,  8.0F,  7.0F,  5.0F,  4.0F,  4.0F},
                new int[]{    4,     4,     3,     3,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        CLIPS.put("melee_combo_3hit", c);
    }

    // 6: double_clap (2.60s = 52 ticks; impact @ 1.15s = tick 23) — Authentic Genshin wide wing-spread clap (zero gimbal lock!)
    private static void initClip_6() {
        Clip c = new Clip("double_clap", 2.6000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -4.0F, -4.6F, -6.2F, -5.6F, -3.6F, -2.2F},
                new float[]{0.0F, 1.2F, 1.4F, -2.6F, -2.2F, -0.8F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{7.0F, 12.0F, 16.0F, 20.0F, 18.0F, 12.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{-8.0F, -16.0F, -20.0F, -74.0F, -70.0F, -34.0F, -8.0F},
                new float[]{0.0F, -16.0F, -20.0F, 26.0F, 22.0F, 8.0F, 0.0F},
                new float[]{-15.0F, -72.0F, -82.0F, 8.0F, 4.0F, -12.0F, -15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-18.0F, -10.0F, -20.0F, -18.0F, -18.0F},
                new float[]{0.0F, -4.0F, 14.0F, 10.0F, 0.0F},
                new float[]{0.0F, 0.0F, 6.0F, 4.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.72F, 1.00F, 1.15F, 1.50F, 2.05F, 2.60F},
                new float[]{-8.0F, -16.0F, -20.0F, -74.0F, -70.0F, -34.0F, -8.0F},
                new float[]{0.0F, 16.0F, 20.0F, -26.0F, -22.0F, -8.0F, 0.0F},
                new float[]{15.0F, 72.0F, 82.0F, -8.0F, -4.0F, 12.0F, 15.0F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-18.0F, -10.0F, -20.0F, -18.0F, -18.0F},
                new float[]{0.0F, 4.0F, -14.0F, -10.0F, 0.0F},
                new float[]{0.0F, 0.0F, -6.0F, -4.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -20.0F, -30.0F, -26.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -9.0F, -12.0F, -11.0F, -7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{24.0F, 38.0F, 56.0F, 50.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -18.0F, -26.0F, -24.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F, 4.0F, 4.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -20.0F, -30.0F, -26.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 9.0F, 12.0F, 11.0F, 7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{24.0F, 38.0F, 56.0F, 50.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 1.00F, 1.15F, 1.50F, 2.60F},
                new float[]{-12.0F, -18.0F, -26.0F, -24.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F, -4.0F, -4.0F}, new int[]{0, 1, 1, 0, 0});
        CLIPS.put("double_clap", c);
    }

    // 7: stomp_shockwave (2.20s = 44 ticks; piledriver stomp @ 1.00s = tick 20)
    // Authentic Genshin upright stomp (ruinguard_dam_chan_1.mp4): torso/pelvis stay vertical & grounded (no sideways tipping!)
    private static void initClip_7() {
        Clip c = new Clip("stomp_shockwave", 2.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{0.0F, -0.8F, -1.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -1.4F, -1.2F, -5.8F, -4.6F, -2.2F},
                new float[]{0.0F, 0.3F, 0.4F, -1.2F, -0.8F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{0.0F, -3.0F, -4.0F, 5.0F, 3.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -3.5F, -4.5F, 1.5F, 0.5F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{7.0F, -2.0F, -3.0F, 12.0F, 10.0F, 7.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -2.5F, -3.5F, 1.5F, 0.5F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-12.0F, -64.0F, -68.0F, -16.0F, -14.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-7.0F, -10.0F, -11.0F, -7.0F, -7.0F, -7.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{24.0F, 82.0F, 86.0F, 32.0F, 28.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.56F, 0.86F, 1.00F, 2.20F},
                new float[]{-12.0F, -18.0F, -18.0F, -16.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 4.0F, 4.0F, 4.0F, 4.0F}, new int[]{0, 0, 1, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-12.0F, -13.0F, -24.0F, -20.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{7.0F, 7.0F, 8.0F, 7.5F, 7.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{24.0F, 26.0F, 46.0F, 38.0F, 24.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-12.0F, -13.0F, -22.0F, -12.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -4.0F, -4.0F, -4.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-8.0F, -4.0F, -14.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-15.0F, -28.0F, -20.0F, -18.0F, -15.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-18.0F, -14.0F, -20.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 1.38F, 2.20F},
                new float[]{-8.0F, -4.0F, -14.0F, -12.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{15.0F, 28.0F, 20.0F, 18.0F, 15.0F}, new int[]{0, 1, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.86F, 1.00F, 2.20F},
                new float[]{-18.0F, -14.0F, -20.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 1, 1, 0});
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

    // 11: leap_slam (3.90s = 78 ticks) — ultra-smooth passing-frame overhead whip (+48 -> -94 -> -162)
    // and deep forward-lean (+44 deg torso) 2-hand ground plant @ 1.80s (tick 36) -> heavy hydraulic stand-up
    private static void initClip_11() {
        Clip c = new Clip("leap_slam", 3.9000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.52F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{0.0F, 0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{-2.2F,-10.0F,-14.4F, 2.8F,  6.4F,  5.6F, -1.6F,-15.2F,-14.2F,-13.8F, -6.6F, -2.2F},
                new float[]{0.0F, 1.6F,  2.6F,  -0.4F, -1.0F, -1.2F, -1.8F, -2.6F, -2.4F, -2.2F, -1.0F,  0.0F},
                new int[]{  4,    4,     3,      3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("pelvis", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{0.0F, 12.0F, -5.0F, -6.0F,  6.0F, 15.0F, 13.0F, 12.0F,  5.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,     3,     3,     2,     2,     3,     4,     4,     4,     0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.52F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{7.0F, 19.0F, 25.0F, -6.0F, -8.0F, -2.0F, 20.0F, 44.0F, 40.0F, 38.0F, 18.0F,  7.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{  4,     4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("shoulder_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{0.0F,  8.0F, -8.0F,-18.0F,-18.0F, -2.0F, 12.0F, 10.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F, -8.0F, -6.0F,-20.0F,-20.0F, -8.0F,-10.0F, -8.0F,  0.0F},
                new int[]{  4,     3,     3,     4,     2,     2,     3,     4,     0});
        c.add("shoulder_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{0.0F,  8.0F, -8.0F,-18.0F,-18.0F, -2.0F, 12.0F, 10.0F,  0.0F},
                new float[]{0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{0.0F,  8.0F,  6.0F, 20.0F, 20.0F,  8.0F, 10.0F,  8.0F,  0.0F},
                new int[]{  4,     3,     3,     4,     2,     2,     3,     4,     0});
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.52F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{-8.0F, 36.0F, 48.0F,-94.0F,-158.0F,-162.0F,-102.0F,-58.0F,-56.0F,-54.0F,-26.0F, -8.0F},
                new float[]{ 0.0F, -6.0F, -8.0F,  0.0F,   8.0F,  10.0F,   6.0F,  8.0F,  8.0F,  8.0F,  2.0F,  0.0F},
                new float[]{-15.0F,-24.0F,-28.0F, 0.0F,  20.0F,  22.0F, -12.0F,-24.0F,-24.0F,-22.0F,-20.0F,-15.0F},
                new int[]{  4,     4,     3,     3,      4,      2,      2,     3,     4,     4,     4,     0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{-18.0F,-34.0F,-20.0F,-26.0F,-30.0F,-14.0F,-12.0F,-14.0F,-18.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     0});
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.52F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{-8.0F, 36.0F, 48.0F,-94.0F,-158.0F,-162.0F,-102.0F,-58.0F,-56.0F,-54.0F,-26.0F, -8.0F},
                new float[]{ 0.0F,  6.0F,  8.0F,  0.0F,  -8.0F, -10.0F,  -6.0F, -8.0F, -8.0F, -8.0F, -2.0F,  0.0F},
                new float[]{15.0F, 24.0F, 28.0F,  0.0F, -20.0F, -22.0F,  12.0F, 24.0F, 24.0F, 22.0F, 20.0F, 15.0F},
                new int[]{  4,     4,     3,     3,      4,      2,      2,     3,     4,     4,     4,     0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{-18.0F,-34.0F,-20.0F,-26.0F,-30.0F,-14.0F,-12.0F,-14.0F,-18.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     0});
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{-12.0F,-62.0F, 14.0F,-48.0F,-52.0F,-34.0F,-62.0F,-58.0F,-54.0F,-28.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ -7.0F,-14.0F, -5.0F,-10.0F,-11.0F, -9.0F,-17.0F,-16.0F,-15.0F, -9.0F, -7.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{ 24.0F,110.0F,  6.0F, 88.0F, 96.0F, 58.0F,114.0F,106.0F, 98.0F, 50.0F, 24.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{-12.0F,-44.0F, 30.0F, 16.0F, 14.0F,  6.0F,-42.0F,-36.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  4.0F,  6.0F,  0.0F,  0.0F,  0.0F,  0.0F,  7.0F,  6.0F,  4.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     0});
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{-12.0F,-62.0F, 14.0F,-56.0F,-60.0F,-36.0F,-62.0F,-58.0F,-54.0F,-28.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  7.0F, 14.0F,  5.0F, 11.0F, 12.0F,  9.0F, 17.0F, 16.0F, 15.0F,  9.0F,  7.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 1.98F, 2.65F, 3.25F, 3.90F},
                new float[]{ 24.0F,110.0F,  6.0F,100.0F,108.0F, 62.0F,114.0F,106.0F, 98.0F, 50.0F, 24.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     4,     4,     0});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.85F, 1.00F, 1.24F, 1.44F, 1.66F, 1.80F, 2.65F, 3.90F},
                new float[]{-12.0F,-44.0F, 30.0F, 18.0F, 16.0F,  6.0F,-42.0F,-36.0F,-12.0F},
                new float[]{  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F,  0.0F},
                new float[]{ -4.0F, -6.0F,  0.0F,  0.0F,  0.0F,  0.0F, -7.0F, -6.0F, -4.0F},
                new int[]{    4,     3,     3,     4,     2,     2,     3,     4,     0});
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

    // 13: stunned_collapse (2.00s = 40 ticks) — Video-matched Genshin Paralyzed collapse:
    // Hit-2 sharp recoil -> power shuts off -> knees buckle & splay wide into low squat -> torso bows forward -> arms hang limp to ground
    private static void initClip_13() {
        Clip c = new Clip("stunned_collapse", 2.0000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0F, 0.25F, 0.55F, 1.10F, 1.30F, 2.00F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -1.0F, -5.5F, -14.8F, -14.2F, -14.6F},
                new float[]{0.0F, 1.8F, 0.4F, -2.6F, -2.4F, -2.5F}, new int[]{1, 0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0F, 0.25F, 0.55F, 1.12F, 1.35F, 2.00F},
                new float[]{7.0F, -22.0F, 4.0F, 50.0F, 48.0F, 50.0F},
                new float[]{0.0F, -6.0F, 4.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 5.0F, -3.0F, 0.0F, 0.0F, 0.0F}, new int[]{1, 0, 0, 1, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0F, 0.18F, 0.36F, 0.55F, 2.00F},
                new float[]{1.35F, 0.25F, 0.75F, 0.08F, 0.08F}, new float[]{1.35F, 0.25F, 0.75F, 0.08F, 0.08F}, new float[]{1.10F, 0.25F, 0.75F, 0.08F, 0.08F}, new int[]{1, 0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 0.25F, 0.55F, 2.00F},
                new float[]{1.00F, 0.35F, 0.08F, 0.08F}, new float[]{1.00F, 0.35F, 0.08F, 0.08F}, new float[]{1.00F, 0.35F, 0.08F, 0.08F}, new int[]{0, 0, 0, 0});
        // Arms go completely limp and hang downward near the ground in front/beside splayed knees
        c.add("upper_arm_r", CH_ROT, new float[]{0.0F, 0.30F, 1.12F, 1.35F, 2.00F},
                new float[]{-8.0F, -20.0F, -48.0F, -45.0F, -47.0F}, new float[]{0.0F, 0.0F, -6.0F, -6.0F, -6.0F}, new float[]{-15.0F, -26.0F, -22.0F, -20.0F, -21.0F}, new int[]{1, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-18.0F, -12.0F, -8.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 2.0F, 5.0F, 5.0F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.30F, 1.12F, 1.35F, 2.00F},
                new float[]{-8.0F, -20.0F, -48.0F, -45.0F, -47.0F}, new float[]{0.0F, 0.0F, 6.0F, 6.0F, 6.0F}, new float[]{15.0F, 26.0F, 22.0F, 20.0F, 21.0F}, new int[]{1, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0F, 0.40F, 1.15F, 2.00F},
                new float[]{-18.0F, -12.0F, -8.0F, -8.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, -2.0F, -5.0F, -5.0F}, new int[]{0, 0, 0, 0});
        // Knees buckle deeply & splay wide outward to support collapsed torso
        c.add("hip_r", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{-12.0F, -28.0F, -58.0F, -58.0F}, new float[]{0.0F, -6.0F, -14.0F, -14.0F}, new float[]{-7.0F, -14.0F, -22.0F, -22.0F}, new int[]{0, 0, 1, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{24.0F, 52.0F, 102.0F, 102.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{-12.0F, -24.0F, -42.0F, -42.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{4.0F, 8.0F, 12.0F, 12.0F}, new int[]{0, 0, 1, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{-12.0F, -28.0F, -58.0F, -58.0F}, new float[]{0.0F, 6.0F, 14.0F, 14.0F}, new float[]{7.0F, 14.0F, 22.0F, 22.0F}, new int[]{0, 0, 1, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{24.0F, 52.0F, 102.0F, 102.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new int[]{0, 0, 1, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0F, 0.55F, 1.10F, 2.00F},
                new float[]{-12.0F, -24.0F, -42.0F, -42.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F}, new float[]{-4.0F, -8.0F, -12.0F, -12.0F}, new int[]{0, 0, 1, 0});
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
                new float[]{0.12F, 1.35F, 0.65F, 1.30F, 0.96F}, new float[]{0.12F, 1.35F, 0.65F, 1.30F, 0.96F}, new float[]{0.12F, 1.20F, 0.65F, 1.15F, 1.00F}, new int[]{0, 0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0F, 0.45F, 0.95F, 1.40F},
                new float[]{0.12F, 0.55F, 1.12F, 1.00F}, new float[]{0.12F, 0.55F, 1.12F, 1.00F}, new float[]{0.12F, 0.55F, 1.10F, 1.00F}, new int[]{0, 0, 0, 0});
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
                new float[]{-8.0F, 50.0F, -162.0F, -46.0F, -44.0F, -22.0F, -8.0F},
                new float[]{0.0F, -12.0F, 10.0F, 8.0F, 6.0F, 0.0F, 0.0F},
                new float[]{-15.0F, -34.0F, 22.0F, -12.0F, -14.0F, -18.0F, -15.0F}, new int[]{4, 3, 2, 3, 4, 4, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 3.20F},
                new float[]{-18.0F, -34.0F, -32.0F, -6.0F, -10.0F, -18.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F}, new int[]{4, 3, 2, 3, 4, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0F, 0.75F, 1.08F, 1.25F, 1.90F, 2.45F, 3.20F},
                new float[]{-8.0F, 50.0F, -162.0F, -46.0F, -44.0F, -22.0F, -8.0F},
                new float[]{0.0F, 12.0F, -10.0F, -8.0F, -6.0F, 0.0F, 0.0F},
                new float[]{15.0F, 34.0F, -22.0F, 12.0F, 14.0F, 18.0F, 15.0F}, new int[]{4, 3, 2, 3, 4, 4, 0});
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


    // 17: mobility_leap (1.90s = 38 ticks) — Powerful Space bar jump with BOTH arms whipping UP overhead!
    // Deep coil crouch (0.00..0.28s) -> explosive overhead arm thrust launch & flight (0.28..1.05s) -> heavy shock-absorber landing @ 1.15s (tick 23)
    private static void initClip_17() {
        Clip c = new Clip("mobility_leap", 1.9000F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.24F, 0.28F, 0.55F, 0.85F, 1.05F, 1.15F, 1.35F, 1.65F, 1.90F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-1.0F, -9.8F, -10.2F, 5.2F, 5.8F, 1.4F, -12.0F, -9.4F, -4.2F, -1.0F},
                new float[]{0.0F, 1.8F, 1.8F, -1.6F, -2.0F, -2.2F, -2.8F, -2.0F, -0.6F, 0.0F},
                new int[]{1, 1, 0, 0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.26F, 0.52F, 0.88F, 1.06F, 1.15F, 1.38F, 1.65F, 1.90F},
                new float[]{8.0F, 26.0F, -14.0F, -10.0F, 12.0F, 32.0F, 24.0F, 14.0F, 8.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0, 0, 0});
        // BOTH arms swing BACK (+48 deg) during crouch windup, then whip violently UP OVERHEAD (-156..-162 deg outward V) on takeoff!
        c.add("shoulder_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.15F, 1.90F},
                new float[]{0.0F, 8.0F, -16.0F, -18.0F, 8.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -8.0F, -18.0F, -18.0F, -8.0F, 0.0F},
                new int[]{4, 3, 4, 2, 3, 0});
        c.add("shoulder_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.15F, 1.90F},
                new float[]{0.0F, 8.0F, -16.0F, -18.0F, 8.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 8.0F, 18.0F, 18.0F, 8.0F, 0.0F},
                new int[]{4, 3, 4, 2, 3, 0});
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.04F, 1.15F, 1.45F, 1.90F},
                new float[]{-6.0F, 48.0F, -156.0F, -162.0F, -92.0F, -38.0F, -20.0F, -6.0F},
                new float[]{0.0F, -8.0F, 8.0F, 10.0F, 4.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-18.0F, -28.0F, 20.0F, 22.0F, -12.0F, -24.0F, -20.0F, -18.0F},
                new int[]{4, 3, 4, 2, 2, 3, 4, 0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.15F, 1.90F},
                new float[]{-22.0F, -34.0F, -18.0F, -22.0F, -26.0F, -22.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{4, 3, 4, 2, 3, 0});
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.04F, 1.15F, 1.45F, 1.90F},
                new float[]{-6.0F, 48.0F, -156.0F, -162.0F, -92.0F, -38.0F, -20.0F, -6.0F},
                new float[]{0.0F, 8.0F, -8.0F, -10.0F, -4.0F, 0.0F, 0.0F, 0.0F},
                new float[]{18.0F, 28.0F, -20.0F, -22.0F, 12.0F, 24.0F, 20.0F, 18.0F},
                new int[]{4, 3, 4, 2, 2, 3, 4, 0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.48F, 0.82F, 1.15F, 1.90F},
                new float[]{-22.0F, -34.0F, -18.0F, -22.0F, -26.0F, -22.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{4, 3, 4, 2, 3, 0});
        // Legs: deep hydraulic coil -> explosive extension & mid-air tuck -> heavy two-footed touchdown
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.48F, 1.90F},
                new float[]{-12.0F, -50.0F, -28.0F, -36.0F, -18.0F, -56.0F, -34.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-6.0F, -8.0F, -6.0F, -6.0F, -6.0F, -10.0F, -8.0F, -6.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0, 0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.48F, 1.90F},
                new float[]{24.0F, 86.0F, 54.0F, 68.0F, 34.0F, 96.0F, 58.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0, 0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.90F},
                new float[]{-12.0F, -36.0F, 22.0F, 20.0F, 12.0F, -40.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{4.0F, 4.0F, 0.0F, 0.0F, 0.0F, 4.0F, 4.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0});
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.48F, 1.90F},
                new float[]{-12.0F, -50.0F, -28.0F, -36.0F, -18.0F, -56.0F, -34.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{6.0F, 8.0F, 6.0F, 6.0F, 6.0F, 10.0F, 8.0F, 6.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0, 0});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.48F, 1.90F},
                new float[]{24.0F, 86.0F, 54.0F, 68.0F, 34.0F, 96.0F, 58.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0, 0});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.26F, 0.55F, 0.88F, 1.05F, 1.15F, 1.90F},
                new float[]{-12.0F, -36.0F, 22.0F, 20.0F, 12.0F, -40.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.0F, -4.0F, 0.0F, 0.0F, 0.0F, -4.0F, -4.0F},
                new int[]{1, 0, 0, 0, 1, 1, 0});
        CLIPS.put("mobility_leap", c);
    }

    // 18: weakpoint_flinch (0.95s = 19 ticks) — Video-matched Genshin Hit-1 weakpoint stagger:
    // Legs stay planted wide on ground (no kneeling); torso jerks back slightly -> pitches forward -> rights itself; arms flare mildly & settle
    private static void initClip_18() {
        Clip c = new Clip("weakpoint_flinch", 0.9500F, false);
        c.add("pelvis", CH_POS,
                new float[]{0.0F, 0.14F, 0.40F, 0.68F, 0.95F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-2.2F, -2.5F, -2.9F, -2.4F, -2.2F},
                new float[]{0.0F, 1.4F, -0.8F, -0.2F, 0.0F},
                new int[]{1, 0, 0, 0, 0});
        c.add("torso", CH_ROT,
                new float[]{0.0F, 0.14F, 0.36F, 0.56F, 0.78F, 0.95F},
                new float[]{7.0F, -16.0F, 18.0F, 15.0F, 8.0F, 7.0F},
                new float[]{0.0F, -8.0F, 4.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 5.0F, -3.0F, 0.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE,
                new float[]{0.0F, 0.12F, 0.28F, 0.50F, 0.95F},
                new float[]{1.00F, 0.45F, 0.65F, 1.15F, 0.96F},
                new float[]{1.00F, 0.45F, 0.65F, 1.15F, 0.96F},
                new float[]{1.00F, 0.45F, 0.65F, 1.05F, 1.00F},
                new int[]{1, 0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE,
                new float[]{0.0F, 0.12F, 0.32F, 0.95F},
                new float[]{1.00F, 0.55F, 0.80F, 1.00F},
                new float[]{1.00F, 0.55F, 0.80F, 1.00F},
                new float[]{1.00F, 0.55F, 0.80F, 1.00F},
                new int[]{1, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT,
                new float[]{0.0F, 0.16F, 0.42F, 0.72F, 0.95F},
                new float[]{-8.0F, -18.0F, -12.0F, -9.0F, -8.0F},
                new float[]{0.0F, -6.0F, -3.0F, 0.0F, 0.0F},
                new float[]{-15.0F, -28.0F, -22.0F, -17.0F, -15.0F},
                new int[]{1, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT,
                new float[]{0.0F, 0.18F, 0.50F, 0.95F},
                new float[]{-18.0F, -22.0F, -16.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, -3.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT,
                new float[]{0.0F, 0.16F, 0.42F, 0.72F, 0.95F},
                new float[]{-8.0F, -16.0F, -11.0F, -9.0F, -8.0F},
                new float[]{0.0F, 6.0F, 3.0F, 0.0F, 0.0F},
                new float[]{15.0F, 28.0F, 22.0F, 17.0F, 15.0F},
                new int[]{1, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT,
                new float[]{0.0F, 0.18F, 0.50F, 0.95F},
                new float[]{-18.0F, -22.0F, -16.0F, -18.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 3.0F, 0.0F, 0.0F},
                new int[]{1, 0, 0, 0});
        // Legs remain planted solidly wide (only subtle weight transfer, no kneeling)
        c.add("hip_r", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{-12.0F, -14.0F, -15.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-7.0F, -8.0F, -8.0F, -7.0F},
                new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{24.0F, 27.0F, 28.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0});
        c.add("foot_r", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{-12.0F, -13.0F, -13.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{4.0F, 4.0F, 4.0F, 4.0F},
                new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{-12.0F, -14.0F, -15.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{7.0F, 8.0F, 8.0F, 7.0F},
                new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{24.0F, 27.0F, 28.0F, 24.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new int[]{0, 0, 0, 0});
        c.add("foot_l", CH_ROT,
                new float[]{0.0F, 0.22F, 0.52F, 0.95F},
                new float[]{-12.0F, -13.0F, -13.0F, -12.0F},
                new float[]{0.0F, 0.0F, 0.0F, 0.0F},
                new float[]{-4.0F, -4.0F, -4.0F, -4.0F},
                new int[]{0, 0, 0, 0});
        CLIPS.put("weakpoint_flinch", c);
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
        initClip_17();
        initClip_18();
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

    /** Time-normalized, Fritsch-Carlson monotone Catmull-Rom Hermite spline (zero drift on holds, zero overshoot). */
    private static float evalCatmullHermite(float[] vals, float[] times, int i, float u, float dt, boolean loop) {
        int n = vals.length;
        float p1 = vals[i];
        float p2 = vals[i + 1];
        float secant = (p2 - p1) / dt;
        if (Math.abs(secant) < 1e-4F) {
            return p1;
        }
        float m1, m2;
        if (i > 0) {
            float span = Math.max(1e-4F, times[i + 1] - times[i - 1]);
            m1 = (vals[i + 1] - vals[i - 1]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[1] - times[0]) + (times[n - 1] - times[n - 2]));
            m1 = (vals[1] - vals[n - 2]) / span;
        } else {
            m1 = secant;
        }
        if (i + 2 < n) {
            float span = Math.max(1e-4F, times[i + 2] - times[i]);
            m2 = (vals[i + 2] - vals[i]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[n - 1] - times[n - 2]) + (times[1] - times[0]));
            m2 = (vals[1] - vals[n - 2]) / span;
        } else {
            m2 = secant;
        }
        if (m1 * secant <= 0.0F) m1 = 0.0F;
        if (m2 * secant <= 0.0F) m2 = 0.0F;
        float maxSlope = Math.abs(secant) * 2.2F;
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
        int mode = tr.interps[i];
        if (mode == INTERP_SMOOTH && n >= 3) {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
            return;
        }
        float e = switch (mode) {
            case INTERP_EASE_IN -> u * u * u;
            case INTERP_EASE_OUT -> 1.0F - (1.0F - u) * (1.0F - u) * (1.0F - u);
            case INTERP_EASE_IN_OUT -> u * u * (3.0F - 2.0F * u);
            default -> u;
        };
        out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * e;
        out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * e;
        out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * e;
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

    /**
     * Samples live animated eye-core motion from a clip for 1st-person cockpit camera coupling.
     * {@code out6 = [pelvisX, pelvisY, pelvisZ, torsoPitchDeg, waistYawDeg, torsoRollDeg]}.
     */
    public static void sampleCoreMotion(String clipName, float timeSeconds, float[] out6) {
        for (int i = 0; i < 6; i++) out6[i] = 0.0F;
        Clip clip = CLIPS.get(clipName);
        if (clip == null) return;
        if ("walk_combat".equals(clipName)) timeSeconds *= 1.38F;
        float t = normalizeTime(clip, timeSeconds);
        float[] v = new float[3];
        for (Track tr : clip.tracks) {
            if (tr.times.length == 0) continue;
            if ("pelvis".equals(tr.bone) && tr.channel == CH_POS) {
                sampleTrack(tr, t, clip.loop, v);
                out6[0] = v[0];
                out6[1] = v[1];
                out6[2] = v[2];
            } else if ("torso".equals(tr.bone) && tr.channel == CH_ROT) {
                sampleTrack(tr, t, clip.loop, v);
                out6[3] += v[0];
                out6[4] += v[1];
                out6[5] += v[2];
            } else if ("waist".equals(tr.bone) && tr.channel == CH_ROT) {
                sampleTrack(tr, t, clip.loop, v);
                out6[3] += v[0];
                out6[4] += v[1];
                out6[5] += v[2];
            } else if ("pelvis".equals(tr.bone) && tr.channel == CH_ROT) {
                sampleTrack(tr, t, clip.loop, v);
                out6[3] += v[0] * 0.65F;
                out6[5] += v[2] * 0.65F;
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
