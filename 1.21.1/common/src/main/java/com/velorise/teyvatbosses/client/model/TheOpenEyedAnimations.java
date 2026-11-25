// Auto-compiled 12 upgraded keyframe animations from the_open_eyed.bbmodel
package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TheOpenEyedAnimations {
    private TheOpenEyedAnimations() {}

    public static final int CH_ROT = 0;
    public static final int CH_POS = 1;
    public static final int CH_SCALE = 2;

    public static final int INTERP_SMOOTH = 0;
    public static final int INTERP_LINEAR = 1;

    private static final class Track {
        final String bone;
        final int channel;
        final float[] times;
        final float[] xs, ys, zs;
        final int[] interps;
        Track(String bone, int channel, float[] times, float[] xs, float[] ys, float[] zs, int[] interps) {
            this.bone = bone; this.channel = channel; this.times = times;
            this.xs = xs; this.ys = ys; this.zs = zs; this.interps = interps;
        }
    }

    public static final class Clip {
        public final String name;
        public final float lengthSeconds;
        public final boolean loop;
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
        Clip c = new Clip("animation.the_open_eyed.idle", 4.0000F, true);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 5.5000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{2.0000F, -3.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F, 3.0000F, 4.0000F}, new float[]{-2.0000F, 0.0000F, 3.0000F, 0.0000F, -2.0000F}, new float[]{0.0000F, 3.0000F, 0.0000F, -3.0000F, 0.0000F}, new float[]{0.0000F, -1.5000F, 0.0000F, 1.5000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -180.0000F}, new int[]{1, 1});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 2.3000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -12.0000F, 2.0000F}, new float[]{-2.0000F, 9.0000F, -2.0000F}, new int[]{0, 0, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.4000F, 1.5500F, 1.7500F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 10.0000F, 3.0000F, 0.0000F}, new float[]{0.0000F, -3.0000F, -8.0000F, -2.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, -7.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 7.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{4.0000F, -5.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{-4.0000F, 6.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, -7.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -13.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{-8.0000F, -16.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 13.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{-10.0000F, -18.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.the_open_eyed.idle", c);
    }

    private static void initClip_1() {
        Clip c = new Clip("animation.the_open_eyed.descend", 2.2000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.6500F, 1.0500F, 1.3000F, 1.4800F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{34.0000F, 28.0000F, 24.0000F, -3.0000F, 0.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 1.5500F, 2.2000F}, new float[]{-12.0000F, -8.0000F, 22.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3400F, 1.6500F, 2.2000F}, new float[]{16.0000F, 18.0000F, -14.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 2.2000F}, new float[]{-24.0000F, -18.0000F, -46.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 8.0000F, 0.0000F}, new float[]{-28.0000F, -22.0000F, -14.0000F, -6.0000F}, new int[]{0, 1, 1, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.2000F}, new float[]{-32.0000F, -12.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 2.2000F}, new float[]{-18.0000F, -12.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 12.0000F, 0.0000F}, new float[]{28.0000F, 22.0000F, 34.0000F, 6.0000F}, new int[]{0, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.3200F, 2.2000F}, new float[]{-28.0000F, -10.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("celestial_spear", CH_POS, new float[]{0.0000F, 0.4500F, 1.0500F, 1.3000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{16.0000F, -24.0000F, -24.0000F, 0.0000F}, new float[]{-14.0000F, -14.0000F, -14.0000F, 0.0000F}, new int[]{0, 1, 1, 1});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4500F, 1.3000F}, new float[]{110.0000F, 110.0000F, 0.0000F}, new float[]{10.0000F, 10.0000F, 0.0000F}, new float[]{16.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 1.5500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{28.0000F, 32.0000F, -36.0000F, -26.0000F, 0.0000F}, new float[]{-20.0000F, -24.0000F, 28.0000F, 20.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3800F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{18.0000F, 22.0000F, -28.0000F, 0.0000F}, new float[]{-12.0000F, -16.0000F, 22.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-18.0000F, 24.0000F, 0.0000F}, new float[]{14.0000F, -18.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.3200F, 2.2000F}, new float[]{24.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 0.0000F}, new float[]{-16.0000F, 24.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.2000F}, new float[]{24.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 0.0000F}, new float[]{16.0000F, -24.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3500F, 2.2000F}, new float[]{-18.0000F, -24.0000F, 22.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3500F, 2.2000F}, new float[]{20.0000F, 26.0000F, -24.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.2800F, 1.6500F, 2.2000F}, new float[]{0.7000F, 1.4500F, 1.1000F, 1.0000F}, new float[]{0.7000F, 1.4500F, 1.1000F, 1.0000F}, new float[]{0.7000F, 1.4500F, 1.1000F, 1.0000F}, new int[]{0, 1, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 480.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -240.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.descend", c);
    }

    private static void initClip_2() {
        Clip c = new Clip("animation.the_open_eyed.spear_slam", 1.6000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F, 0.9400F, 1.1800F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 12.0000F, 13.0000F, -4.0000F, -1.0000F, -2.0000F, 2.0000F}, new float[]{0.0000F, 6.0000F, 7.0000F, -14.0000F, -12.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4200F, 0.5500F, 0.6800F, 0.8200F, 0.9400F, 1.1800F, 1.6000F}, new float[]{0.0000F, -24.0000F, -26.0000F, -24.0000F, 34.0000F, 28.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, -30.0000F, -28.0000F, 28.0000F, 24.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8400F, 1.1800F, 1.6000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 18.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, 24.0000F, -18.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F, 0.9400F, 1.1800F, 1.6000F}, new float[]{0.0000F, -162.0000F, -166.0000F, -32.0000F, -40.0000F, -38.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -16.0000F, 16.0000F, 14.0000F, 12.0000F, 0.0000F}, new float[]{-6.0000F, -30.0000F, -32.0000F, -8.0000F, -10.0000F, -10.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F, 0.9600F, 1.6000F}, new float[]{-8.0000F, -52.0000F, -56.0000F, 6.0000F, -8.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("celestial_spear", CH_POS, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 1, 1});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F, 1.1800F, 1.6000F}, new float[]{0.0000F, 108.0000F, 112.0000F, 96.0000F, 96.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8200F, 1.1800F, 1.6000F}, new float[]{0.0000F, -128.0000F, -134.0000F, 34.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, 18.0000F, 14.0000F, 0.0000F}, new float[]{6.0000F, 26.0000F, 28.0000F, 42.0000F, 34.0000F, 6.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.4200F, 0.8200F, 1.6000F}, new float[]{-10.0000F, -40.0000F, -6.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6800F, 0.8400F, 1.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, -40.0000F, 30.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 34.0000F, -14.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.4800F, 0.8800F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -16.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 18.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8400F, 1.6000F}, new float[]{0.0000F, 24.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 20.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8400F, 1.6000F}, new float[]{0.0000F, 24.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -18.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8600F, 1.6000F}, new float[]{4.0000F, -18.0000F, 26.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8600F, 1.6000F}, new float[]{-4.0000F, 16.0000F, -28.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.spear_slam", c);
    }

    private static void initClip_3() {
        Clip c = new Clip("animation.the_open_eyed.spear_thrust", 1.7500F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.0400F, 1.2800F, 1.7500F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 6.0000F, 6.5000F, 1.0000F, 2.0000F, 2.0000F, 2.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F, -24.0000F, -21.0000F, -20.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6300F, 0.7800F, 0.9200F, 1.0600F, 1.2800F, 1.7500F}, new float[]{0.0000F, -14.0000F, -15.0000F, -14.0000F, 24.0000F, 18.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, -34.0000F, -36.0000F, -34.0000F, 22.0000F, 18.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.5000F, -1.5000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.7500F}, new float[]{0.0000F, 8.0000F, 10.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 32.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.0600F, 1.2800F, 1.7500F}, new float[]{0.0000F, 34.0000F, 38.0000F, -98.0000F, -92.0000F, -90.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 6.0000F, 4.0000F, 4.0000F, 0.0000F}, new float[]{-6.0000F, -34.0000F, -36.0000F, -8.0000F, -10.0000F, -10.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.2800F, 1.7500F}, new float[]{-8.0000F, -68.0000F, -72.0000F, 4.0000F, 0.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.2800F, 1.7500F}, new float[]{0.0000F, 10.0000F, 8.0000F, 118.0000F, 114.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9200F, 1.2800F, 1.7500F}, new float[]{0.0000F, -88.0000F, -92.0000F, 42.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, -30.0000F, 16.0000F, 14.0000F, 0.0000F}, new float[]{6.0000F, -4.0000F, -6.0000F, 40.0000F, 34.0000F, 6.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.4800F, 0.9200F, 1.7500F}, new float[]{-10.0000F, -36.0000F, -4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7800F, 0.9400F, 1.7500F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -40.0000F, -44.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 18.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.5400F, 0.9800F, 1.7500F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.7500F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -14.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.7500F}, new float[]{0.0000F, -18.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.7500F}, new float[]{0.0000F, -18.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -16.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.7500F}, new float[]{4.0000F, -14.0000F, 32.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.7500F}, new float[]{-4.0000F, 12.0000F, 34.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.7800F, 1.1500F, 1.7500F}, new float[]{1.0000F, 1.3500F, 1.0500F, 1.0000F}, new float[]{1.0000F, 1.3500F, 1.0500F, 1.0000F}, new float[]{1.0000F, 1.3500F, 1.0500F, 1.0000F}, new int[]{0, 1, 0, 0});
        CLIPS.put("animation.the_open_eyed.spear_thrust", c);
    }

    private static void initClip_4() {
        Clip c = new Clip("animation.the_open_eyed.double_lunge", 2.0000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.2800F, 0.4400F, 0.5800F, 0.6800F, 0.9200F, 1.1400F, 1.2800F, 1.4000F, 1.6000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 1.0000F, 1.0000F, -1.0000F, 0.0000F, 3.0000F, 3.5000F, -2.0000F, 0.0000F, 0.0000F, 2.0000F}, new float[]{0.0000F, 7.0000F, 8.0000F, -14.0000F, -12.0000F, -5.0000F, -3.0000F, -26.0000F, -23.0000F, -22.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.2800F, 0.4400F, 0.5800F, 0.9200F, 1.1400F, 1.2800F, 1.4200F, 2.0000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 22.0000F, -14.0000F, -16.0000F, 28.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, -34.0000F, 26.0000F, -40.0000F, -42.0000F, 34.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4400F, 0.5800F, 1.1400F, 1.2800F, 2.0000F}, new float[]{0.0000F, 6.0000F, -14.0000F, 8.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -22.0000F, 34.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.2800F, 0.4400F, 0.5800F, 0.9200F, 1.1400F, 1.2800F, 1.5800F, 2.0000F}, new float[]{0.0000F, 26.0000F, 30.0000F, -94.0000F, 34.0000F, 38.0000F, -100.0000F, -92.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -16.0000F, 6.0000F, -16.0000F, -18.0000F, 8.0000F, 6.0000F, 0.0000F}, new float[]{-6.0000F, -30.0000F, -32.0000F, -8.0000F, -34.0000F, -36.0000F, -8.0000F, -10.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4400F, 0.5800F, 1.1400F, 1.2800F, 2.0000F}, new float[]{-8.0000F, -64.0000F, 4.0000F, -74.0000F, 6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4400F, 0.5800F, 1.1400F, 1.2800F, 2.0000F}, new float[]{0.0000F, -76.0000F, 32.0000F, -88.0000F, 42.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 16.0000F, -28.0000F, 20.0000F, 0.0000F}, new float[]{6.0000F, 0.0000F, 36.0000F, -4.0000F, 44.0000F, 6.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.4400F, 0.5800F, 1.1400F, 1.2800F, 2.0000F}, new float[]{-10.0000F, -34.0000F, -6.0000F, -40.0000F, -4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4400F, 0.5800F, 1.1400F, 1.2800F, 1.6000F, 2.0000F}, new float[]{0.0000F, 10.0000F, 116.0000F, 8.0000F, 118.0000F, 114.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4400F, 0.6000F, 1.1400F, 1.3000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -34.0000F, 22.0000F, -42.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, -6.0000F, 16.0000F, 18.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6400F, 1.1800F, 1.3500F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, 20.0000F, -28.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -6.0000F, 12.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.5800F, 1.2800F, 2.0000F}, new float[]{0.0000F, -26.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 20.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.5800F, 1.2800F, 2.0000F}, new float[]{0.0000F, 24.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -20.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.5800F, 1.1400F, 1.3000F, 2.0000F}, new float[]{4.0000F, 24.0000F, -10.0000F, 34.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.5800F, 1.1400F, 1.3000F, 2.0000F}, new float[]{-4.0000F, 26.0000F, -8.0000F, 38.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 0});
        CLIPS.put("animation.the_open_eyed.double_lunge", c);
    }

    private static void initClip_5() {
        Clip c = new Clip("animation.the_open_eyed.double_slash", 1.9000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.3800F, 0.5200F, 0.9400F, 1.0800F, 1.3500F, 1.9000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 3.0000F, 0.0000F, 2.0000F, -1.0000F, 0.0000F, 2.0000F}, new float[]{0.0000F, 4.0000F, -10.0000F, -8.0000F, -18.0000F, -16.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.2400F, 0.3800F, 0.5200F, 0.6400F, 0.9400F, 1.0800F, 1.2200F, 1.9000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 18.0000F, 14.0000F, 16.0000F, 22.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, -46.0000F, -48.0000F, 52.0000F, 44.0000F, 50.0000F, -54.0000F, -44.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.3800F, 0.5200F, 0.9400F, 1.0800F, 1.9000F}, new float[]{0.0000F, 4.0000F, -8.0000F, -8.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 36.0000F, -40.0000F, -38.0000F, 42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.2400F, 0.3800F, 0.5200F, 0.6400F, 0.9400F, 1.0800F, 1.2400F, 1.9000F}, new float[]{0.0000F, -82.0000F, -84.0000F, -88.0000F, -84.0000F, -86.0000F, -74.0000F, -72.0000F, 0.0000F}, new float[]{0.0000F, -34.0000F, -38.0000F, 42.0000F, 34.0000F, 38.0000F, -44.0000F, -36.0000F, 0.0000F}, new float[]{-6.0000F, -64.0000F, -68.0000F, -8.0000F, -12.0000F, -10.0000F, -68.0000F, -58.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.3800F, 0.5200F, 0.9400F, 1.0800F, 1.9000F}, new float[]{-8.0000F, -48.0000F, 4.0000F, -44.0000F, 6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.2400F, 0.5200F, 1.0800F, 1.9000F}, new float[]{0.0000F, 114.0000F, 118.0000F, 118.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.3800F, 0.5200F, 0.9400F, 1.0800F, 1.9000F}, new float[]{0.0000F, -34.0000F, 34.0000F, 30.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 22.0000F, 18.0000F, -20.0000F, 0.0000F}, new float[]{6.0000F, 18.0000F, 48.0000F, 42.0000F, 42.0000F, 6.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.5200F, 1.0800F, 1.9000F}, new float[]{-10.0000F, -6.0000F, -28.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.3800F, 0.5400F, 0.9400F, 1.1000F, 1.9000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, 26.0000F, 28.0000F, -34.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -8.0000F, -10.0000F, 22.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.5800F, 1.1400F, 1.9000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 18.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.5200F, 1.0800F, 1.9000F}, new float[]{4.0000F, 18.0000F, 22.0000F, 4.0000F}, new float[]{0.0000F, 16.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.5200F, 1.0800F, 1.9000F}, new float[]{-4.0000F, 20.0000F, 24.0000F, -4.0000F}, new float[]{0.0000F, 18.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        CLIPS.put("animation.the_open_eyed.double_slash", c);
    }

    private static void initClip_6() {
        Clip c = new Clip("animation.the_open_eyed.forced_teleport", 1.6000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.5500F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 9.0000F, 10.0000F, 3.0000F, 2.0000F}, new float[]{0.0000F, 4.0000F, 5.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, -14.0000F, -16.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9400F, 1.6000F}, new float[]{0.0000F, -10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, -96.0000F, -102.0000F, -54.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -10.0000F, 18.0000F, 0.0000F}, new float[]{-6.0000F, -22.0000F, -24.0000F, -32.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.6000F}, new float[]{-8.0000F, -36.0000F, 4.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9400F, 1.6000F}, new float[]{0.0000F, 110.0000F, 114.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("corrupt_cubes_right", CH_SCALE, new float[]{0.0000F, 0.5500F, 0.7800F, 0.9400F, 1.6000F}, new float[]{1.0000F, 1.8500F, 2.0500F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.8500F, 2.0500F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.8500F, 2.0500F, 1.2000F, 1.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 0.7800F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 540.0000F, 900.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{1, 1, 1});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, -84.0000F, -90.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, -24.0000F, 14.0000F, 0.0000F}, new float[]{6.0000F, 24.0000F, 28.0000F, 34.0000F, 6.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.6000F}, new float[]{-10.0000F, -42.0000F, -4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -16.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.7800F, 0.9400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.7800F, 0.9600F, 1.6000F}, new float[]{1.0000F, 1.5500F, 0.9000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 0.9000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 0.9000F, 1.0000F}, new int[]{0, 1, 1, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.forced_teleport", c);
    }

    private static void initClip_7() {
        Clip c = new Clip("animation.the_open_eyed.leaping_slam", 2.1000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.3200F, 0.4200F, 0.8500F, 1.1800F, 1.3600F, 1.4800F, 1.7200F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -4.0000F, -4.5000F, 27.0000F, 28.0000F, -5.0000F, -1.0000F, -2.0000F, 2.0000F}, new float[]{0.0000F, 5.0000F, 5.0000F, -4.0000F, -5.0000F, -24.0000F, -22.0000F, -21.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 1, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.0200F, 1.1800F, 1.3600F, 1.5000F, 1.7200F, 2.1000F}, new float[]{0.0000F, 22.0000F, -26.0000F, -28.0000F, -26.0000F, 38.0000F, 30.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -20.0000F, -22.0000F, -20.0000F, 20.0000F, 16.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 1, 1, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.1800F, 1.3800F, 1.7200F, 2.1000F}, new float[]{0.0000F, -14.0000F, 22.0000F, 24.0000F, -20.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 16.0000F, 18.0000F, -14.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.1800F, 1.3600F, 1.5000F, 1.7200F, 2.1000F}, new float[]{0.0000F, 24.0000F, -164.0000F, -168.0000F, -30.0000F, -38.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -14.0000F, -16.0000F, 14.0000F, 12.0000F, 10.0000F, 0.0000F}, new float[]{-6.0000F, -20.0000F, -32.0000F, -34.0000F, -10.0000F, -12.0000F, -12.0000F, -6.0000F}, new int[]{0, 0, 0, 1, 1, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.1800F, 1.3600F, 1.5200F, 2.1000F}, new float[]{-8.0000F, -36.0000F, -56.0000F, -60.0000F, 8.0000F, -8.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1800F, 1.3600F, 1.7200F, 2.1000F}, new float[]{0.0000F, 106.0000F, 110.0000F, 98.0000F, 98.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.1800F, 1.3600F, 1.7200F, 2.1000F}, new float[]{0.0000F, 20.0000F, -142.0000F, -148.0000F, 38.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 16.0000F, 18.0000F, 22.0000F, 18.0000F, 0.0000F}, new float[]{6.0000F, 20.0000F, 32.0000F, 36.0000F, 48.0000F, 40.0000F, 6.0000F}, new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.1800F, 1.3600F, 2.1000F}, new float[]{-10.0000F, -44.0000F, -4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 1.1800F, 1.3800F, 1.5500F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -44.0000F, -48.0000F, 36.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 40.0000F, 44.0000F, -16.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 1, 0, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.9200F, 1.4200F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -30.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -18.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 22.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 32.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 26.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.1800F, 1.3800F, 2.1000F}, new float[]{0.0000F, 32.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, -26.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1800F, 1.3800F, 1.5500F, 2.1000F}, new float[]{4.0000F, 22.0000F, 14.0000F, -32.0000F, 24.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1800F, 1.3800F, 1.5500F, 2.1000F}, new float[]{-4.0000F, -20.0000F, -12.0000F, 36.0000F, -24.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 540.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.leaping_slam", c);
    }

    private static void initClip_8() {
        Clip c = new Clip("animation.the_open_eyed.supreme_overseer", 3.2000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.7500F, 2.3200F, 2.5200F, 2.6800F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 10.0000F, 12.0000F, -1.0000F, 1.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 4.0000F, -16.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.7500F, 2.3200F, 2.5200F, 2.7000F, 3.2000F}, new float[]{0.0000F, 0.0000F, -8.0000F, 24.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F, 342.0000F, 412.0000F, 396.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.2000F, 2.3200F, 2.5200F, 3.2000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 16.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3000F, 0.8500F, 1.3000F, 2.1000F, 2.3200F, 2.5200F, 2.7200F, 3.2000F}, new float[]{0.0000F, -14.0000F, -14.0000F, -24.0000F, -82.0000F, -86.0000F, -90.0000F, -84.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, -18.0000F, -36.0000F, -40.0000F, 44.0000F, 34.0000F, 0.0000F}, new float[]{-6.0000F, -90.0000F, -90.0000F, -62.0000F, -64.0000F, -68.0000F, -8.0000F, -12.0000F, -6.0000F}, new int[]{0, 0, 1, 0, 0, 1, 1, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.3200F, 2.5200F, 3.2000F}, new float[]{-8.0000F, -54.0000F, 6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.3000F, 0.8500F, 1.2500F, 2.5200F, 3.2000F}, new float[]{0.0000F, 122.0000F, 122.0000F, 114.0000F, 118.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.8500F, 2.3200F, 2.5200F, 3.2000F}, new float[]{0.0000F, -24.0000F, -28.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 20.0000F, 0.0000F}, new float[]{6.0000F, 68.0000F, 72.0000F, 46.0000F, 6.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.3200F, 2.5200F, 3.2000F}, new float[]{-10.0000F, -32.0000F, -4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.3200F, 2.5400F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -40.0000F, -48.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, 38.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.2000F, 2.5800F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 2.3200F, 2.5400F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -26.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 18.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.7500F, 2.5200F, 3.2000F}, new float[]{4.0000F, -16.0000F, 28.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.7500F, 2.5200F, 3.2000F}, new float[]{-4.0000F, 18.0000F, 30.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.3200F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 900.0000F, 1440.0000F}, new int[]{1, 1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.3200F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -540.0000F, -900.0000F}, new int[]{1, 1, 1});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.2000F, 1.7500F, 2.3200F, 2.5500F, 3.2000F}, new float[]{1.0000F, 1.5500F, 1.4000F, 1.7500F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 1.4000F, 1.7500F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 1.4000F, 1.7500F, 1.2000F, 1.0000F}, new int[]{0, 0, 0, 1, 1, 0});
        CLIPS.put("animation.the_open_eyed.supreme_overseer", c);
    }

    private static void initClip_9() {
        Clip c = new Clip("animation.the_open_eyed.phenomena_delirium", 4.0000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0000F, 3.1800F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 18.0000F, 21.0000F, 4.0000F, 6.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 3.0000F, -14.0000F, -11.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.8500F, 2.3000F, 2.5200F, 2.7500F, 3.0000F, 3.2000F, 4.0000F}, new float[]{0.0000F, -22.0000F, -26.0000F, -26.0000F, -28.0000F, 32.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0000F, 4.0000F}, new float[]{0.0000F, -24.0000F, -28.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0000F, 3.2200F, 4.0000F}, new float[]{0.0000F, -160.0000F, -166.0000F, -48.0000F, -56.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 12.0000F, 8.0000F, 0.0000F}, new float[]{-6.0000F, -28.0000F, -32.0000F, -12.0000F, -14.0000F, -6.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.7500F, 3.0000F, 4.0000F}, new float[]{-8.0000F, -48.0000F, 6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0000F, 4.0000F}, new float[]{0.0000F, 106.0000F, 108.0000F, 102.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0000F, 4.0000F}, new float[]{0.0000F, -148.0000F, -156.0000F, -42.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -14.0000F, 0.0000F}, new float[]{6.0000F, 34.0000F, 38.0000F, 22.0000F, 6.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.7500F, 3.0000F, 4.0000F}, new float[]{-10.0000F, -44.0000F, 4.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0200F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -38.0000F, -48.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 42.0000F, 50.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.9500F, 3.0800F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 2.7500F, 3.0200F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 20.0000F, 0.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0200F, 4.0000F}, new float[]{4.0000F, -20.0000F, -24.0000F, 30.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0200F, 4.0000F}, new float[]{-4.0000F, 22.0000F, 26.0000F, -28.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.8500F, 2.7500F, 3.0500F, 4.0000F}, new float[]{1.0000F, 1.4500F, 1.8500F, 1.1000F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.8500F, 1.1000F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.8500F, 1.1000F, 1.0000F}, new int[]{0, 0, 1, 1, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.7500F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F, 1800.0000F}, new int[]{1, 1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.7500F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F, -1260.0000F}, new int[]{1, 1, 1});
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F, 0.6500F, 2.7500F, 3.0500F, 3.2500F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new int[]{1, 0, 1, 1, 1});
        c.add("elemental_star_left", CH_POS, new float[]{0.6500F, 2.7500F, 3.0200F}, new float[]{0.0000F, -6.0000F, 12.0000F}, new float[]{10.0000F, 22.0000F, -28.0000F}, new float[]{0.0000F, -6.0000F, -28.0000F}, new int[]{0, 1, 1});
        c.add("elemental_star_left", CH_ROT, new float[]{0.0000F, 3.2500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -900.0000F}, new float[]{0.0000F, 900.0000F}, new int[]{1, 1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F, 0.6500F, 2.7500F, 3.0500F, 3.2500F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new float[]{0.0000F, 1.2000F, 1.5500F, 1.8000F, 0.0000F}, new int[]{1, 0, 1, 1, 1});
        c.add("elemental_star_right", CH_POS, new float[]{0.6500F, 2.7500F, 3.0200F}, new float[]{0.0000F, 6.0000F, -12.0000F}, new float[]{10.0000F, 22.0000F, -28.0000F}, new float[]{0.0000F, -6.0000F, -28.0000F}, new int[]{0, 1, 1});
        c.add("elemental_star_right", CH_ROT, new float[]{0.0000F, 3.2500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 900.0000F}, new float[]{0.0000F, -900.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.phenomena_delirium", c);
    }

    private static void initClip_10() {
        Clip c = new Clip("animation.the_open_eyed.stunned", 2.5000F, true);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-8.0000F, -10.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{30.0000F, 36.0000F, 30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{5.0000F, -5.0000F, 5.0000F}, new int[]{0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.9000F, 1.0200F, 1.2500F, 2.5000F}, new float[]{26.0000F, 29.0000F, 24.0000F, 32.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F, 6.0000F, 0.0000F, 0.0000F}, new float[]{8.0000F, 2.0000F, -6.0000F, -8.0000F, 8.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{-14.0000F, -8.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-24.0000F, -30.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{-24.0000F, -18.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 2.5000F}, new float[]{56.0000F, 56.0000F}, new float[]{10.0000F, 10.0000F}, new float[]{16.0000F, 16.0000F}, new int[]{0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{-18.0000F, -12.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new int[]{0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{-22.0000F, -16.0000F, -22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{-34.0000F, -42.0000F, -34.0000F}, new int[]{0, 0, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 20.0000F, 14.0000F}, new float[]{-22.0000F, -28.0000F, -22.0000F}, new int[]{0, 0, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-12.0000F, -18.0000F, -12.0000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new int[]{0, 0, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.7000F, 0.5800F, 0.7000F}, new float[]{0.7000F, 0.5800F, 0.7000F}, new float[]{0.7000F, 0.5800F, 0.7000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{22.0000F, 26.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 18.0000F, 10.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.the_open_eyed.stunned", c);
    }

    private static void initClip_11() {
        Clip c = new Clip("animation.the_open_eyed.death", 3.0000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.3500F, 0.8500F, 2.0500F, 2.2500F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 7.0000F, 4.0000F, -17.0000F, -14.5000F, -16.0000F}, new float[]{0.0000F, 2.0000F, 2.0000F, 4.0000F, 4.0000F, 4.0000F}, new int[]{0, 1, 0, 1, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3500F, 0.8500F, 2.0500F, 2.2800F, 3.0000F}, new float[]{0.0000F, -24.0000F, -10.0000F, 56.0000F, 48.0000F, 52.0000F}, new float[]{0.0000F, -14.0000F, 12.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 6.0000F, 14.0000F, 11.0000F, 12.0000F}, new int[]{0, 1, 0, 1, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, -28.0000F, 48.0000F, 44.0000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 18.0000F}, new float[]{0.0000F, -8.0000F, 16.0000F, 14.0000F}, new int[]{0, 1, 1, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, -36.0000F, -18.0000F, -16.0000F}, new float[]{0.0000F, -12.0000F, -14.0000F, -12.0000F}, new float[]{-6.0000F, -38.0000F, -34.0000F, -30.0000F}, new int[]{0, 1, 1, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{-8.0000F, -42.0000F, -10.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0, 0});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, 22.0000F, 68.0000F, 62.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 10.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 16.0000F}, new int[]{0, 1, 1, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, -32.0000F, 26.0000F, 24.0000F}, new float[]{0.0000F, 10.0000F, -10.0000F, -8.0000F}, new float[]{6.0000F, 42.0000F, 26.0000F, 24.0000F}, new int[]{0, 1, 1, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.3500F, 3.0000F}, new float[]{-10.0000F, -38.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, 48.0000F, 44.0000F}, new float[]{0.0000F, 26.0000F, -56.0000F, -52.0000F}, new int[]{0, 1, 1, 0});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.4000F, 2.2000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 38.0000F, 34.0000F}, new float[]{0.0000F, 18.0000F, -42.0000F, -38.0000F}, new int[]{0, 1, 1, 0});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.3500F, 2.1000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -32.0000F, -28.0000F}, new float[]{0.0000F, -18.0000F, 34.0000F, 30.0000F}, new int[]{0, 1, 1, 0});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 2.1000F, 3.0000F}, new float[]{4.0000F, -24.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 2.1000F, 3.0000F}, new float[]{-4.0000F, 28.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 0.4000F, 2.2000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 58.0000F, 54.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 120.0000F, 44.0000F, 40.0000F}, new int[]{0, 1, 1, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 0.4000F, 2.2000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 52.0000F, 48.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -90.0000F, -38.0000F, -35.0000F}, new int[]{0, 1, 1, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.3500F, 0.8000F, 2.2000F}, new float[]{1.0000F, 1.6000F, 0.8000F, 0.0500F}, new float[]{1.0000F, 1.6000F, 0.8000F, 0.0500F}, new float[]{1.0000F, 1.6000F, 0.8000F, 0.0500F}, new int[]{0, 1, 1, 0});
        c.add("cube_tail", CH_SCALE, new float[]{0.0000F, 2.4000F, 3.0000F}, new float[]{1.0000F, 0.1000F, 0.0500F}, new float[]{1.0000F, 0.1000F, 0.0500F}, new float[]{1.0000F, 0.1000F, 0.0500F}, new int[]{0, 0, 0});
        CLIPS.put("animation.the_open_eyed.death", c);
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
    }

    public static Clip getClip(String name) { return CLIPS.get(name); }

    private static float normalizeTime(Clip clip, float timeSeconds) {
        float t = timeSeconds;
        if (clip.lengthSeconds > 0.001F) {
            t = clip.loop ? (t % clip.lengthSeconds) : Math.min(t, clip.lengthSeconds);
            if (t < 0.0F) t = 0.0F;
        }
        return t;
    }

    private static void sampleTrack(Track tr, float t, float[] out) {
        if (tr.times.length == 1 || t <= tr.times[0]) {
            out[0] = tr.xs[0]; out[1] = tr.ys[0]; out[2] = tr.zs[0];
            return;
        }
        int last = tr.times.length - 1;
        if (t >= tr.times[last]) {
            out[0] = tr.xs[last]; out[1] = tr.ys[last]; out[2] = tr.zs[last];
            return;
        }
        int i = 0;
        while (i + 1 < tr.times.length && tr.times[i + 1] < t) i++;
        float span = tr.times[i + 1] - tr.times[i];
        float a = span > 1e-5F ? (t - tr.times[i]) / span : 0.0F;
        float s = (tr.interps[i] == INTERP_LINEAR || tr.interps[i + 1] == INTERP_LINEAR)
                ? a
                : (a * a * (3.0F - 2.0F * a));
        out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * s;
        out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * s;
        out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * s;
    }

    private static void applyWeighted(TheOpenEyedModel<?> model, Clip clip, float timeSeconds, float weight) {
        if (clip == null || weight <= 1e-4F) return;
        float t = normalizeTime(clip, timeSeconds);
        float[] v = new float[3];
        for (Track tr : clip.tracks) {
            ModelPart part = model.getBone(tr.bone);
            if (part == null || tr.times.length == 0) continue;
            sampleTrack(tr, t, v);
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

    public static void apply(TheOpenEyedModel<?> model, String clipName, float timeSeconds) {
        model.resetPose();
        applyWeighted(model, CLIPS.get(clipName), timeSeconds, 1.0F);
    }

    public static void applyBlended(TheOpenEyedModel<?> model, String prevClipName, float prevTimeSeconds, String currClipName, float currTimeSeconds, float blendAlpha) {
        model.resetPose();
        float a = Math.max(0.0F, Math.min(1.0F, blendAlpha));
        float smoothA = a * a * (3.0F - 2.0F * a);
        if (prevClipName != null && smoothA < 0.999F) {
            applyWeighted(model, CLIPS.get(prevClipName), prevTimeSeconds, 1.0F - smoothA);
        }
        applyWeighted(model, CLIPS.get(currClipName), currTimeSeconds, smoothA);
    }
}
