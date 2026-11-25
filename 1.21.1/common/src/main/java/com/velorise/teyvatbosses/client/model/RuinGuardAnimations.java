// Auto-compiled 15 Catmull-Rom spline animations synced 1:1 with ruin_guard_boss.bbmodel
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
        final String bone; final int channel;
        final float[] times, xs, ys, zs; final int[] interps;
        Track(String bone, int channel, float[] times, float[] xs, float[] ys, float[] zs, int[] interps) {
            this.bone = bone; this.channel = channel; this.times = times;
            this.xs = xs; this.ys = ys; this.zs = zs; this.interps = interps;
        }
    }

    public static final class Clip {
        public final String name; public final float lengthSeconds; public final boolean loop;
        final List<Track> tracks = new ArrayList<>();
        Clip(String name, float lengthSeconds, boolean loop) {
            this.name = name; this.lengthSeconds = lengthSeconds; this.loop = loop;
        }
        void add(String bone, int ch, float[] t, float[] x, float[] y, float[] z, int[] interps) {
            tracks.add(new Track(bone, ch, t, x, y, z, interps));
        }
    }

    private static final Map<String, Clip> CLIPS = new HashMap<>();
    private static final float DEG2RAD = (float) (Math.PI / 180.0D);

    private static void initClip_0() {
        Clip c = new Clip("dormant_kneeling", 3.2000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 1.6000F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-12.0000F, -12.4000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-18.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.6000F, 3.2000F}, new float[]{-28.0000F, -30.0000F, -28.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 1.6000F, 3.2000F}, new float[]{0.2500F, 0.3500F, 0.2500F}, new float[]{0.2500F, 0.3500F, 0.2500F}, new float[]{0.2500F, 0.3500F, 0.2500F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{26.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{12.0000F, 12.0000F}, new int[]{0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{18.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-6.0000F, -6.0000F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{26.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-12.0000F, -12.0000F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{18.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{6.0000F, 6.0000F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-45.0000F, -45.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{4.0000F, 4.0000F}, new int[]{0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{78.0000F, 78.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-15.0000F, -15.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-45.0000F, -45.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-4.0000F, -4.0000F}, new int[]{0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{78.0000F, 78.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-15.0000F, -15.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        CLIPS.put("dormant_kneeling", c);
    }

    private static void initClip_1() {
        Clip c = new Clip("awaken_bootup", 2.6000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.7000F, 1.6000F, 2.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-12.0000F, -11.5000F, 1.5000F, -1.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0000F, 0.7000F, 1.7000F, 2.6000F}, new float[]{-18.0000F, -14.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.7000F, 1.6000F, 2.1000F, 2.6000F}, new float[]{-28.0000F, -20.0000F, 10.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 0.3500F, 0.5500F, 0.8500F, 2.6000F}, new float[]{0.2500F, 1.3500F, 0.6000F, 1.4500F, 1.0000F}, new float[]{0.2500F, 1.3500F, 0.6000F, 1.4500F, 1.0000F}, new float[]{0.2500F, 1.3500F, 0.6000F, 1.4500F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 1.5000F, 2.1000F, 2.6000F}, new float[]{26.0000F, -10.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{12.0000F, 26.0000F, 18.0000F, 6.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 1.5000F, 2.6000F}, new float[]{18.0000F, -15.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 1.5000F, 2.1000F, 2.6000F}, new float[]{26.0000F, -10.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-12.0000F, -26.0000F, -18.0000F, -6.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 1.5000F, 2.6000F}, new float[]{18.0000F, -15.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{-45.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 2.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{78.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{-15.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{-45.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -2.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{78.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 1.6000F, 2.6000F}, new float[]{-15.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("awaken_bootup", c);
    }

    private static void initClip_2() {
        Clip c = new Clip("idle_combat", 2.8000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -1.2000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{-2.0000F, -5.0000F, -2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{4.0000F, 8.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 9.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{-10.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{4.0000F, 8.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -9.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{-10.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("idle_combat", c);
    }

    private static void initClip_3() {
        Clip c = new Clip("walk_combat", 1.6000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.2000F, 0.4000F, 0.8000F, 1.0000F, 1.2000F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 1.8000F, -1.8000F, 0.0000F, 1.8000F, -1.8000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{-4.0000F, -6.0000F, -4.0000F, -6.0000F, -4.0000F}, new float[]{0.0000F, -6.0000F, 0.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 0.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{0.0000F, -26.0000F, -16.0000F, 8.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{4.0000F, 34.0000F, 14.0000F, 4.0000F, 12.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 1.2000F, 1.6000F}, new float[]{-4.0000F, -8.0000F, 2.0000F, -12.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.0000F, 1.2000F, 1.6000F}, new float[]{0.0000F, 18.0000F, 0.0000F, -26.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.0000F, 1.2000F, 1.6000F}, new float[]{4.0000F, 12.0000F, 4.0000F, 34.0000F, 14.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.0000F, 1.2000F, 1.6000F}, new float[]{-4.0000F, -12.0000F, -4.0000F, -8.0000F, 2.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{0.0000F, 20.0000F, 0.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{7.0000F, 9.0000F, 7.0000F, 9.0000F, 7.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.4000F, 1.2000F, 1.6000F}, new float[]{-10.0000F, -18.0000F, -6.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F, 1.2000F, 1.6000F}, new float[]{0.0000F, -18.0000F, 0.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-7.0000F, -9.0000F, -7.0000F, -9.0000F, -7.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.4000F, 1.2000F, 1.6000F}, new float[]{-10.0000F, -6.0000F, -18.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("walk_combat", c);
    }

    private static void initClip_4() {
        Clip c = new Clip("missile_barrage", 2.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6500F, 0.9000F, 1.1500F, 1.3000F, 1.5000F, 1.7000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -7.5000F, -7.9000F, -7.5000F, -9.2000F, -8.5000F, -9.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6500F, 0.9000F, 1.1500F, 1.3000F, 1.5500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -24.0000F, -26.0000F, -25.0000F, -32.0000F, -26.0000F, -30.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.5000F, -1.5000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -22.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -22.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 0.6500F, 1.1500F, 1.7500F, 2.4000F}, new float[]{1.0000F, 1.3500F, 1.5000F, 1.3000F, 1.0000F}, new float[]{1.0000F, 1.3500F, 1.5000F, 1.3000F, 1.0000F}, new float[]{1.0000F, 1.1000F, 1.2000F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1500F, 1.7500F, 2.4000F}, new float[]{0.0000F, 32.0000F, 34.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -14.0000F, -10.0000F, 0.0000F}, new float[]{6.0000F, 34.0000F, 38.0000F, 32.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{-8.0000F, -26.0000F, -22.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 8.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1500F, 1.7500F, 2.4000F}, new float[]{0.0000F, 32.0000F, 34.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 14.0000F, 10.0000F, 0.0000F}, new float[]{-6.0000F, -34.0000F, -38.0000F, -32.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{-8.0000F, -26.0000F, -22.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -34.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, 62.0000F, 64.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -28.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -34.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, 62.0000F, 64.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.7500F, 2.4000F}, new float[]{0.0000F, -28.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("missile_barrage", c);
    }

    private static void initClip_5() {
        Clip c = new Clip("melee_combo_3hit", 3.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.5500F, 0.9000F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, -3.5000F, -2.0000F, -3.5000F, 3.0000F, -7.0000F, -6.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -2.0000F, -2.0000F, -3.0000F, -2.0000F, -3.0000F, -3.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 1, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7500F, 0.9200F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 3.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -48.0000F, -52.0000F, 44.0000F, 52.0000F, -46.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9200F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 8.0000F, -16.0000F, 6.0000F, -18.0000F, 18.0000F, -34.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 12.0000F, 12.0000F, -12.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 6.0000F, 6.0000F, -6.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7500F, 0.9200F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 18.0000F, 22.0000F, 74.0000F, -14.0000F, -18.0000F, 142.0000F, 58.0000F, 60.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, -36.0000F, 34.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 82.0000F, 86.0000F, 48.0000F, 22.0000F, 24.0000F, 18.0000F, 10.0000F, 10.0000F, 6.0000F}, new int[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 0.9200F, 2.3000F, 2.5500F, 3.4000F}, new float[]{8.0000F, 12.0000F, 22.0000F, 20.0000F, 10.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9200F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, -14.0000F, -18.0000F, 20.0000F, 76.0000F, 142.0000F, 58.0000F, 60.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 32.0000F, -34.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -20.0000F, -24.0000F, -84.0000F, -48.0000F, -18.0000F, -10.0000F, -10.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 1, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 1.4000F, 1.6200F, 2.3000F, 2.5500F, 3.4000F}, new float[]{8.0000F, 12.0000F, 22.0000F, 20.0000F, 10.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 1.8500F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 0.0000F, -48.0000F, -24.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 6.0000F, 6.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 1.8500F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 0.0000F, 72.0000F, 48.0000F, 48.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 2.3000F, 2.5500F, 3.4000F}, new float[]{0.0000F, -18.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, -10.0000F, -28.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 2.3000F, 2.5500F, 2.8000F, 3.4000F}, new float[]{0.0000F, 18.0000F, 52.0000F, 52.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 2.5500F, 3.4000F}, new float[]{0.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("melee_combo_3hit", c);
    }

    private static void initClip_6() {
        Clip c = new Clip("double_clap", 2.1000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6500F, 1.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.5000F, -2.8000F, -4.5000F, -4.2000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 2.2000F, -3.0000F, -2.8000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 14.0000F, 16.0000F, -20.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 72.0000F, 76.0000F, 82.0000F, 80.0000F, 0.0000F}, new float[]{0.0000F, -52.0000F, -58.0000F, 38.0000F, 34.0000F, 0.0000F}, new float[]{6.0000F, 78.0000F, 82.0000F, 22.0000F, 24.0000F, 6.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1800F, 2.1000F}, new float[]{8.0000F, 12.0000F, 18.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 72.0000F, 76.0000F, 82.0000F, 80.0000F, 0.0000F}, new float[]{0.0000F, 52.0000F, 58.0000F, -38.0000F, -34.0000F, 0.0000F}, new float[]{-6.0000F, -78.0000F, -82.0000F, -22.0000F, -24.0000F, -6.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1800F, 2.1000F}, new float[]{8.0000F, 12.0000F, 18.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 14.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        CLIPS.put("double_clap", c);
    }

    private static void initClip_7() {
        Clip c = new Clip("stomp_shockwave", 1.8000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6000F, 0.8500F, 1.0000F, 1.1800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 2.5000F, 2.8000F, -4.5000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0000F, 0.6000F, 0.8500F, 1.0000F, 1.8000F}, new float[]{0.0000F, 8.0000F, 9.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -9.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6000F, 1.0000F, 1.8000F}, new float[]{0.0000F, 10.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.6000F, 0.8500F, 1.0000F, 1.8000F}, new float[]{0.0000F, -56.0000F, -60.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F, 2.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.6000F, 0.8500F, 1.0000F, 1.8000F}, new float[]{0.0000F, 82.0000F, 86.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.0000F, 1.8000F}, new float[]{0.0000F, -18.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0000F, 1.8000F}, new float[]{0.0000F, -24.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 42.0000F, 26.0000F, 6.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0000F, 1.8000F}, new float[]{0.0000F, -24.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -42.0000F, -26.0000F, -6.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("stomp_shockwave", c);
    }

    private static void initClip_8() {
        Clip c = new Clip("cyclone_spin_windup", 1.1000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.5000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -42.0000F, 90.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6500F, 1.1000F}, new float[]{0.0000F, -6.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F}, new int[]{0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -16.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 74.0000F, 76.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.1400F, 1.1600F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{-8.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -74.0000F, -76.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.1400F, 1.1600F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new int[]{0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{-8.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, -22.0000F, -20.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 42.0000F, 38.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, -20.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, -22.0000F, -20.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -8.0000F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, 42.0000F, 38.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F}, new float[]{0.0000F, -20.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 6.0000F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_windup", c);
    }

    private static void initClip_9() {
        Clip c = new Clip("cyclone_spin_active", 0.8000F, true);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -2.5000F, -4.5000F, -2.5000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 90.0000F, 180.0000F, 270.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{1, 1, 1, 1, 1});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F}, new float[]{-4.0000F, -6.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{16.0000F, 16.0000F}, new int[]{0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-16.0000F, -16.0000F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{76.0000F, 76.0000F}, new int[]{0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0000F, 0.8000F}, new float[]{1.0000F, 1.0000F}, new float[]{1.1600F, 1.1600F}, new float[]{1.0000F, 1.0000F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-76.0000F, -76.0000F}, new int[]{0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0000F, 0.8000F}, new float[]{1.0000F, 1.0000F}, new float[]{1.1600F, 1.1600F}, new float[]{1.0000F, 1.0000F}, new int[]{0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{-24.0000F, -38.0000F, -12.0000F, -6.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 6.0000F, 6.0000F, 6.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{42.0000F, 62.0000F, 24.0000F, 20.0000F, 42.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F}, new float[]{-18.0000F, -12.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -4.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{-12.0000F, -6.0000F, -24.0000F, -38.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -6.0000F, -6.0000F, -6.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F}, new float[]{24.0000F, 20.0000F, 42.0000F, 62.0000F, 24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 0.4000F, 0.8000F}, new float[]{-12.0000F, -18.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 4.0000F, 4.0000F}, new int[]{0, 0, 0});
        CLIPS.put("cyclone_spin_active", c);
    }

    private static void initClip_10() {
        Clip c = new Clip("cyclone_spin_recovery", 1.2000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6000F, 1.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -2.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("waist", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9000F, 1.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 120.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("shoulder_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{16.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("shoulder_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-16.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{76.0000F, 14.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new float[]{1.1600F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-76.0000F, -14.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.2000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new float[]{1.1600F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.0000F, 1.0000F}, new int[]{0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{0.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{0.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{-20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{8.0000F, 0.0000F}, new int[]{0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{-18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-6.0000F, 0.0000F}, new int[]{0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{-20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-8.0000F, 0.0000F}, new int[]{0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 1.2000F}, new float[]{-18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{6.0000F, 0.0000F}, new int[]{0, 0});
        CLIPS.put("cyclone_spin_recovery", c);
    }

    private static void initClip_11() {
        Clip c = new Clip("leap_slam", 2.7000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.6000F, 0.7500F, 1.1500F, 1.3000F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -9.5000F, -9.8000F, 26.0000F, 28.0000F, -11.5000F, -11.0000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 2.0000F, -4.0000F, -5.0000F, -4.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, -24.0000F, 16.0000F, -42.0000F, -38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, -30.0000F, 146.0000F, 56.0000F, 54.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 26.0000F, 22.0000F, 12.0000F, 12.0000F, 6.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 2.7000F}, new float[]{8.0000F, 20.0000F, 22.0000F, 12.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, -30.0000F, 146.0000F, 56.0000F, 54.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -26.0000F, -22.0000F, -12.0000F, -12.0000F, -6.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 2.7000F}, new float[]{8.0000F, 20.0000F, 22.0000F, 12.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, -44.0000F, -22.0000F, -50.0000F, -48.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 6.0000F, 10.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, 78.0000F, 48.0000F, 86.0000F, 84.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 2.7000F}, new float[]{0.0000F, -34.0000F, 16.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, 0.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, -44.0000F, -22.0000F, -50.0000F, -48.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -6.0000F, -10.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 1.8800F, 2.7000F}, new float[]{0.0000F, 78.0000F, 48.0000F, 86.0000F, 84.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 0.6000F, 1.1500F, 1.4800F, 2.7000F}, new float[]{0.0000F, -34.0000F, 16.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        CLIPS.put("leap_slam", c);
    }

    private static void initClip_12() {
        Clip c = new Clip("phase2_overdrive", 2.4000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.7500F, 1.1500F, 1.6500F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 2.0000F, 1.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1500F, 1.6500F, 2.4000F}, new float[]{0.0000F, -20.0000F, 22.0000F, 20.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.1500F, 2.4000F}, new float[]{1.0000F, 1.4500F, 1.6500F, 1.2500F}, new float[]{1.0000F, 1.4500F, 1.6500F, 1.2500F}, new float[]{1.0000F, 1.2000F, 1.3000F, 1.1000F}, new int[]{0, 0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0000F, 1.1500F, 2.4000F}, new float[]{1.0000F, 1.4000F, 1.2000F}, new float[]{1.0000F, 1.4000F, 1.2000F}, new float[]{1.0000F, 1.2000F, 1.1000F}, new int[]{0, 0, 0});
        c.add("missile_pod_r", CH_ROT, new float[]{0.0000F, 1.1500F, 2.4000F}, new float[]{0.0000F, -26.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("missile_pod_l", CH_ROT, new float[]{0.0000F, 1.1500F, 2.4000F}, new float[]{0.0000F, -26.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1500F, 1.6500F, 2.4000F}, new float[]{0.0000F, -45.0000F, 22.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -26.0000F, -24.0000F, 0.0000F}, new float[]{6.0000F, 18.0000F, 72.0000F, 68.0000F, 10.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1500F, 2.4000F}, new float[]{-8.0000F, -48.0000F, -14.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1500F, 1.6500F, 2.4000F}, new float[]{0.0000F, -45.0000F, 22.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 26.0000F, 24.0000F, 0.0000F}, new float[]{-6.0000F, -18.0000F, -72.0000F, -68.0000F, -10.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1500F, 2.4000F}, new float[]{-8.0000F, -48.0000F, -14.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("phase2_overdrive", c);
    }

    private static void initClip_13() {
        Clip c = new Clip("stunned_collapse", 2.0000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.3500F, 1.1000F, 1.4000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 1.0000F, -12.5000F, -11.5000F, -12.2000F}, new float[]{0.0000F, 2.0000F, -2.0000F, -2.0000F, -2.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3500F, 0.6500F, 1.1500F, 2.0000F}, new float[]{0.0000F, 18.0000F, 10.0000F, -36.0000F, -38.0000F}, new float[]{0.0000F, -8.0000F, 8.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -6.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 0.3000F, 0.5500F, 0.8500F, 2.0000F}, new float[]{1.2000F, 0.1000F, 0.9000F, 0.1500F, 0.1800F}, new float[]{1.2000F, 0.1000F, 0.9000F, 0.1500F, 0.1800F}, new float[]{1.0000F, 0.1000F, 0.9000F, 0.1500F, 0.1800F}, new int[]{0, 0, 0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 0.4000F, 1.1500F, 2.0000F}, new float[]{0.0000F, -20.0000F, 28.0000F, 30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 28.0000F, 14.0000F, 14.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_r", CH_ROT, new float[]{0.0000F, 1.1500F, 2.0000F}, new float[]{-8.0000F, 16.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 0.4000F, 1.1500F, 2.0000F}, new float[]{0.0000F, -20.0000F, 28.0000F, 30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -28.0000F, -14.0000F, -14.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_l", CH_ROT, new float[]{0.0000F, 1.1500F, 2.0000F}, new float[]{-8.0000F, 16.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, -46.0000F, -46.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F}, new int[]{0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, 82.0000F, 82.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_r", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, -34.0000F, -34.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, -46.0000F, -46.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -8.0000F}, new int[]{0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, 82.0000F, 82.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("foot_l", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{0.0000F, -34.0000F, -34.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 4.0000F}, new int[]{0, 0, 0});
        CLIPS.put("stunned_collapse", c);
    }

    private static void initClip_14() {
        Clip c = new Clip("death_shutdown", 2.8000F, false);
        c.add("pelvis", CH_POS, new float[]{0.0000F, 0.5500F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, -13.0000F, -21.0000F, -21.0000F}, new float[]{0.0000F, 2.0000F, -4.0000F, -14.0000F, -14.0000F}, new int[]{0, 0, 0, 1, 0});
        c.add("pelvis", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, -18.0000F, -78.0000F, -80.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 4.0000F, 4.0000F}, new int[]{0, 0, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 16.0000F, -26.0000F, -10.0000F, -10.0000F}, new float[]{0.0000F, -12.0000F, 6.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -4.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("eye_core", CH_SCALE, new float[]{0.0000F, 0.4500F, 2.8000F}, new float[]{1.4000F, 0.0000F, 0.0000F}, new float[]{1.4000F, 0.0000F, 0.0000F}, new float[]{1.2000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("back_reactor", CH_SCALE, new float[]{0.0000F, 0.6000F, 2.8000F}, new float[]{1.2000F, 0.0000F, 0.0000F}, new float[]{1.2000F, 0.0000F, 0.0000F}, new float[]{1.2000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("upper_arm_r", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 22.0000F, 62.0000F, 64.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 32.0000F, 38.0000F, 40.0000F}, new int[]{0, 0, 0, 0});
        c.add("upper_arm_l", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 22.0000F, 62.0000F, 64.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -32.0000F, -38.0000F, -40.0000F}, new int[]{0, 0, 0, 0});
        c.add("hip_r", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, -46.0000F, -10.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 8.0000F, 8.0000F}, new int[]{0, 0, 0, 0});
        c.add("knee_r", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 82.0000F, 22.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("hip_l", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, -46.0000F, -10.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, -8.0000F}, new int[]{0, 0, 0, 0});
        c.add("knee_l", CH_ROT, new float[]{0.0000F, 1.3000F, 2.1500F, 2.8000F}, new float[]{0.0000F, 82.0000F, 22.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
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
        return (2*u3 - 3*u2 + 1)*p1 + (u3 - 2*u2 + u)*m1 + (-2*u3 + 3*u2)*p2 + (u3 - u2)*m2;
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
