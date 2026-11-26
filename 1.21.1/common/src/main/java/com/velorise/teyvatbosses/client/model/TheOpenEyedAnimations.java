// AAA Overhauled 15 frame-accurate keyframe animations for The Open-Eyed (Omni Boss / Watcher: Fallen Vigil)
// Features 7 physics-driven easing curves (Catmull-Rom, EaseInBack, EaseOutExpo, EaseInExpo, EaseOutBack,
// EaseInOutSine, Linear-spin), full 22-bone kinetic chains, staggered secondary motion, and 1:1 hit-tick sync.
package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TheOpenEyedAnimations {
    private TheOpenEyedAnimations() {}

    public static final int CH_ROT   = 0;
    public static final int CH_POS   = 1;
    public static final int CH_SCALE = 2;

    public static final int INTERP_SMOOTH          = 0;
    public static final int INTERP_LINEAR          = 1;
    public static final int INTERP_EASE_IN_BACK    = 2;
    public static final int INTERP_EASE_OUT_EXPO   = 3;
    public static final int INTERP_EASE_IN_EXPO    = 4;
    public static final int INTERP_EASE_OUT_BACK   = 5;
    public static final int INTERP_EASE_IN_OUT_SINE = 6;

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

    // 0: idle (4.00s = 80 ticks loop) — regal levitation with staggered breathing & secondary lag
    private static void initClip_0() {
        Clip c = new Clip("animation.the_open_eyed.idle", 4.0000F, true);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 5.6000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("body", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F, 3.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 1.5000F, 0.0000F, -1.5000F, 0.0000F}, new float[]{0.0000F, -1.0000F, 0.0000F, 1.0000F, 0.0000F}, new int[]{6, 6, 6, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 2.0800F, 4.0000F}, new float[]{2.0000F, -3.5000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0500F, 2.1200F, 3.0800F, 4.0000F}, new float[]{-2.0000F, 0.5000F, 3.5000F, 0.5000F, -2.0000F}, new float[]{0.0000F, 3.8000F, 0.0000F, -3.8000F, 0.0000F}, new float[]{0.0000F, -1.8000F, 0.0000F, 1.8000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.9600F, 1.1400F, 0.9600F}, new float[]{0.9600F, 1.1400F, 0.9600F}, new float[]{0.9600F, 1.1400F, 0.9600F}, new int[]{6, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 2.0600F, 4.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F}, new float[]{0.0000F, -15.0000F, 0.0000F}, new float[]{0.0000F, 11.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 2.2600F, 4.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F}, new float[]{2.0000F, -14.0000F, 2.0000F}, new float[]{-2.0000F, 10.0000F, -2.0000F}, new int[]{6, 6, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.3800F, 1.5000F, 1.7400F, 4.0000F}, new float[]{0.0000F, 0.0000F, 3.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 12.0000F, 3.5000F, 0.0000F}, new float[]{0.0000F, -3.0000F, -9.5000F, -2.5000F, 0.0000F}, new int[]{6, 3, 5, 6, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 2.1400F, 4.0000F}, new float[]{0.0000F, 9.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 11.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 2.1400F, 4.0000F}, new float[]{0.0000F, 9.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, -11.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{4.0000F, -6.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{-4.0000F, 7.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 2.0500F, 4.0000F}, new float[]{0.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F}, new float[]{-6.0000F, -14.0000F, -6.0000F}, new int[]{6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{-8.0000F, -17.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 2.2400F, 4.0000F}, new float[]{0.0000F, 3.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.5000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 2.0500F, 4.0000F}, new float[]{0.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, 0.0000F}, new float[]{6.0000F, 14.0000F, 6.0000F}, new int[]{6, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{-10.0000F, -19.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        CLIPS.put("animation.the_open_eyed.idle", c);
    }

    // 1: descend (2.20s = 44t; spear impact 0.45s=9t, touchdown burst 1.30s=26t)
    private static void initClip_1() {
        Clip c = new Clip("animation.the_open_eyed.descend", 2.2000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.6500F, 1.0500F, 1.3000F, 1.5200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{36.0000F, 29.0000F, 25.5000F, -4.5000F, 0.8000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 4, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 1.5800F, 2.2000F}, new float[]{-14.0000F, -10.0000F, 26.0000F, 12.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 4, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3600F, 1.6800F, 2.2000F}, new float[]{18.0000F, 20.0000F, -18.0000F, -7.0000F, -2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 4, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 1.6200F, 2.2000F}, new float[]{-26.0000F, -20.0000F, -52.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 10.0000F, 4.0000F, 0.0000F}, new float[]{-30.0000F, -24.0000F, -16.0000F, -10.0000F, -6.0000F}, new int[]{6, 4, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3600F, 2.2000F}, new float[]{-34.0000F, -28.0000F, -10.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 4, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3200F, 1.6500F, 2.2000F}, new float[]{-20.0000F, -14.0000F, 22.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 14.0000F, 5.0000F, 0.0000F}, new float[]{30.0000F, 24.0000F, 38.0000F, 18.0000F, 6.0000F}, new int[]{6, 4, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3600F, 2.2000F}, new float[]{-30.0000F, -24.0000F, -8.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 4, 5, 6});
        c.add("celestial_spear", CH_POS, new float[]{0.0000F, 0.4500F, 0.5500F, 1.0500F, 1.3000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{18.0000F, -25.0000F, -23.5000F, -24.0000F, 0.0000F, 0.0000F}, new float[]{-14.0000F, -14.0000F, -14.0000F, -14.0000F, 0.0000F, 0.0000F}, new int[]{4, 5, 6, 3, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4500F, 0.5400F, 1.0500F, 1.3200F, 2.2000F}, new float[]{112.0000F, 110.0000F, 114.0000F, 110.0000F, -4.0000F, 0.0000F}, new float[]{10.0000F, 10.0000F, 10.0000F, 10.0000F, 0.0000F, 0.0000F}, new float[]{16.0000F, 16.0000F, 16.0000F, 16.0000F, 0.0000F, 0.0000F}, new int[]{4, 5, 6, 3, 6, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.0500F, 1.3400F, 1.6200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{30.0000F, 36.0000F, -40.0000F, -24.0000F, 0.0000F}, new float[]{-22.0000F, -28.0000F, 32.0000F, 18.0000F, 0.0000F}, new int[]{6, 3, 5, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.0800F, 1.4200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{20.0000F, 26.0000F, -32.0000F, 2.0000F}, new float[]{-14.0000F, -18.0000F, 26.0000F, -2.0000F}, new int[]{6, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3600F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-20.0000F, -24.0000F, 28.0000F, 0.0000F}, new float[]{16.0000F, 18.0000F, -22.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3800F, 2.2000F}, new float[]{26.0000F, 28.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -14.0000F, 0.0000F}, new float[]{-18.0000F, -20.0000F, 28.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3800F, 2.2000F}, new float[]{26.0000F, 28.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 14.0000F, 0.0000F}, new float[]{18.0000F, 20.0000F, -28.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3800F, 2.2000F}, new float[]{-20.0000F, -26.0000F, 26.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.0800F, 1.3800F, 2.2000F}, new float[]{22.0000F, 28.0000F, -28.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.2800F, 1.6500F, 2.2000F}, new float[]{0.6800F, 1.5200F, 1.0800F, 0.9600F}, new float[]{0.6800F, 1.5200F, 1.0800F, 0.9600F}, new float[]{0.6800F, 1.5200F, 1.0800F, 0.9600F}, new int[]{6, 5, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.descend", c);
    }

    // 2: spear_slam (2.10s = 42t; windup 0-18t, snap impact 21t=1.05s, recoil/settle 21-42t)
    private static void initClip_2() {
        Clip c = new Clip("animation.the_open_eyed.spear_slam", 2.1000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.5500F, 0.9000F, 1.0500F, 1.2200F, 1.5200F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 13.5000F, 15.0000F, -5.5000F, -1.8000F, -2.5000F, 2.0000F}, new float[]{0.0000F, 6.5000F, 8.0000F, -15.5000F, -13.0000F, -11.5000F, 0.0000F}, new int[]{2, 6, 4, 5, 6, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7300F, 0.9000F, 1.0500F, 1.2200F, 1.5200F, 2.1000F}, new float[]{2.0000F, -26.0000F, -30.0000F, -28.0000F, 40.0000F, 30.0000F, 24.0000F, 2.0000F}, new float[]{0.0000F, -30.0000F, -33.0000F, -31.0000F, 32.0000F, 24.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, -3.0000F, 2.5000F, -2.5000F, 4.0000F, 2.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 6, 3, 5, 6, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5800F, 0.9200F, 1.0900F, 1.5500F, 2.1000F}, new float[]{-2.0000F, -14.0000F, -16.0000F, 22.0000F, 14.0000F, -2.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F, -18.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9000F, 1.0500F, 1.2400F, 1.5400F, 2.1000F}, new float[]{0.0000F, -148.0000F, -158.0000F, -28.0000F, -36.0000F, -30.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -18.0000F, 16.0000F, 12.0000F, 8.0000F, 0.0000F}, new float[]{-6.0000F, -24.0000F, -26.0000F, -10.0000F, -12.0000F, -10.0000F, -6.0000F}, new int[]{2, 6, 3, 5, 6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5900F, 0.9200F, 1.0700F, 1.2600F, 2.1000F}, new float[]{-8.0000F, -36.0000F, -42.0000F, -6.0000F, -16.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("celestial_spear", CH_POS, new float[]{0.0000F, 0.5800F, 0.9200F, 1.0600F, 1.5400F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 6.0000F, -4.0000F, -2.0000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 3.0000F, -6.0000F, -3.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.6000F, 0.9300F, 1.0700F, 1.5400F, 2.1000F}, new float[]{0.0000F, 48.0000F, 56.0000F, 118.0000F, 92.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 12.0000F, 6.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 10.0000F, 6.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.9000F, 1.0700F, 1.5400F, 2.1000F}, new float[]{0.0000F, -42.0000F, -48.0000F, 36.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 22.0000F, -18.0000F, -8.0000F, 0.0000F}, new float[]{6.0000F, 30.0000F, 34.0000F, 40.0000F, 22.0000F, 6.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.6000F, 0.9200F, 1.1000F, 2.1000F}, new float[]{-10.0000F, -38.0000F, -42.0000F, -8.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.9200F, 1.1000F, 1.5800F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 36.0000F, -42.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -24.0000F, 30.0000F, 16.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.6400F, 0.9600F, 1.1600F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 24.0000F, 28.0000F, -34.0000F, 2.0000F}, new float[]{-2.0000F, -14.0000F, -18.0000F, 24.0000F, -2.0000F}, new int[]{2, 6, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.9200F, 1.1200F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -22.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.9200F, 1.1200F, 2.1000F}, new float[]{0.0000F, 20.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 26.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.9200F, 1.1200F, 2.1000F}, new float[]{0.0000F, 20.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -26.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.9200F, 1.1400F, 2.1000F}, new float[]{4.0000F, -24.0000F, 30.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.9200F, 1.1400F, 2.1000F}, new float[]{-4.0000F, 24.0000F, -32.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.9000F, 1.0600F, 2.1000F}, new float[]{0.9600F, 1.3800F, 1.5500F, 0.9600F}, new float[]{0.9600F, 1.3800F, 1.5500F, 0.9600F}, new float[]{0.9600F, 1.3800F, 1.5500F, 0.9600F}, new int[]{6, 3, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.spear_slam", c);
    }

    // 3: spear_thrust (2.30s = 46t; retreat+coil 0-19t, snap lance 23t=1.15s, settle 23-46t)
    private static void initClip_3() {
        Clip c = new Clip("animation.the_open_eyed.spear_thrust", 2.3000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.5500F, 0.9500F, 1.1500F, 1.3400F, 1.6800F, 2.3000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 5.5000F, 4.5000F, -1.5000F, -0.5000F, 0.5000F, 2.0000F}, new float[]{0.0000F, 10.5000F, 12.0000F, -18.5000F, -15.5000F, -10.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6, 6});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9500F, 1.1500F, 1.3600F, 2.3000F}, new float[]{0.0000F, -8.0000F, -10.0000F, 18.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, -38.0000F, 24.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5800F, 0.9600F, 1.1600F, 1.4000F, 2.3000F}, new float[]{2.0000F, -14.0000F, -16.0000F, 22.0000F, 14.0000F, 2.0000F}, new float[]{0.0000F, -28.0000F, -34.0000F, 32.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, -6.0000F, 6.0000F, 3.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.6000F, 0.9800F, 1.1800F, 1.7000F, 2.3000F}, new float[]{-2.0000F, 6.0000F, 8.0000F, -16.0000F, -8.0000F, -2.0000F}, new float[]{0.0000F, 48.0000F, 56.0000F, -44.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9500F, 1.1500F, 1.3800F, 1.7000F, 2.3000F}, new float[]{0.0000F, 42.0000F, 50.0000F, -96.0000F, -82.0000F, -48.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, -30.0000F, 14.0000F, 10.0000F, 5.0000F, 0.0000F}, new float[]{-6.0000F, -36.0000F, -40.0000F, -8.0000F, -10.0000F, -8.0000F, -6.0000F}, new int[]{2, 6, 3, 5, 6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5900F, 0.9700F, 1.1700F, 1.7200F, 2.3000F}, new float[]{-8.0000F, -68.0000F, -76.0000F, -4.0000F, -16.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.6000F, 0.9800F, 1.1700F, 1.7200F, 2.3000F}, new float[]{0.0000F, 74.0000F, 82.0000F, 134.0000F, 72.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -10.0000F, 8.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 14.0000F, 8.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.9600F, 1.1700F, 1.7200F, 2.3000F}, new float[]{0.0000F, -56.0000F, -64.0000F, 46.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, 32.0000F, -24.0000F, -8.0000F, 0.0000F}, new float[]{6.0000F, 22.0000F, 26.0000F, 36.0000F, 16.0000F, 6.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2000F, 2.3000F}, new float[]{-10.0000F, -28.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.9600F, 1.2000F, 1.7500F, 2.3000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -44.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 26.0000F, 10.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.0000F, 1.2600F, 2.3000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 22.0000F, -34.0000F, 2.0000F}, new float[]{-2.0000F, -14.0000F, 22.0000F, -2.0000F}, new int[]{2, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2200F, 2.3000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 30.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -20.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2200F, 2.3000F}, new float[]{0.0000F, -14.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 22.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2200F, 2.3000F}, new float[]{0.0000F, -14.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, -22.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2400F, 2.3000F}, new float[]{4.0000F, -16.0000F, 34.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.9800F, 1.2400F, 2.3000F}, new float[]{-4.0000F, 18.0000F, -36.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.9500F, 1.1600F, 2.3000F}, new float[]{0.9600F, 1.3200F, 1.5000F, 0.9600F}, new float[]{0.9600F, 1.3200F, 1.5000F, 0.9600F}, new float[]{0.9600F, 1.3200F, 1.5000F, 0.9600F}, new int[]{6, 3, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.3000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.3000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.3000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.3000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.spear_thrust", c);
    }

    // 4: double_lunge (2.60s = 52t; hit1 16t=0.80s, hit2 34t=1.70s, full 22-bone chain)
    private static void initClip_4() {
        Clip c = new Clip("animation.the_open_eyed.double_lunge", 2.6000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.4200F, 0.6400F, 0.8000F, 0.9800F, 1.2800F, 1.5200F, 1.7000F, 1.9200F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 5.0000F, 4.2000F, -1.0000F, 1.5000F, 7.5000F, 6.5000F, -2.5000F, -0.5000F, 2.0000F}, new float[]{0.0000F, 6.5000F, 8.0000F, -13.0000F, -8.0000F, 5.0000F, 8.5000F, -18.0000F, -14.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 2, 6, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4400F, 0.6500F, 0.8000F, 1.2800F, 1.5300F, 1.7000F, 1.9600F, 2.6000F}, new float[]{2.0000F, -12.0000F, -15.0000F, 20.0000F, -18.0000F, -22.0000F, 28.0000F, 16.0000F, 2.0000F}, new float[]{0.0000F, -34.0000F, -38.0000F, 32.0000F, -44.0000F, -48.0000F, 42.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -6.0000F, 6.0000F, -8.0000F, -8.0000F, 8.0000F, 4.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 6, 3, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.6700F, 0.8400F, 1.5500F, 1.7400F, 2.6000F}, new float[]{-2.0000F, 6.0000F, -12.0000F, 8.0000F, -18.0000F, -2.0000F}, new float[]{0.0000F, 34.0000F, -28.0000F, 42.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4400F, 0.6500F, 0.8000F, 1.2800F, 1.5200F, 1.7000F, 2.0400F, 2.6000F}, new float[]{0.0000F, 34.0000F, 42.0000F, -88.0000F, 46.0000F, 54.0000F, -102.0000F, -56.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, -26.0000F, 14.0000F, -28.0000F, -34.0000F, 18.0000F, 10.0000F, 0.0000F}, new float[]{-6.0000F, -30.0000F, -34.0000F, -10.0000F, -38.0000F, -42.0000F, -8.0000F, -10.0000F, -6.0000F}, new int[]{2, 6, 3, 5, 6, 3, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.6700F, 0.8200F, 1.5400F, 1.7200F, 2.6000F}, new float[]{-8.0000F, -66.0000F, -6.0000F, -76.0000F, -4.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8300F, 1.5500F, 1.7300F, 2.1200F, 2.6000F}, new float[]{0.0000F, 72.0000F, 126.0000F, 80.0000F, 138.0000F, 64.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 8.0000F, -8.0000F, 10.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, 14.0000F, 16.0000F, 8.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.6600F, 0.8200F, 1.5400F, 1.7200F, 2.6000F}, new float[]{0.0000F, -48.0000F, 34.0000F, -58.0000F, 46.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -16.0000F, 28.0000F, -24.0000F, 0.0000F}, new float[]{6.0000F, 24.0000F, 32.0000F, 28.0000F, 40.0000F, 6.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8400F, 1.5600F, 1.7500F, 2.6000F}, new float[]{-10.0000F, -32.0000F, -12.0000F, -38.0000F, -10.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.6600F, 0.8500F, 1.5400F, 1.7600F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -34.0000F, 32.0000F, -46.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 22.0000F, -20.0000F, 30.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.7000F, 0.9000F, 1.5800F, 1.8200F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 20.0000F, -26.0000F, 26.0000F, -36.0000F, 2.0000F}, new float[]{-2.0000F, -12.0000F, 18.0000F, -16.0000F, 24.0000F, -2.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.8600F, 1.5600F, 1.7800F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -24.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 16.0000F, -24.0000F, 0.0000F}, new int[]{2, 5, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.8600F, 1.7800F, 2.6000F}, new float[]{0.0000F, 20.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 24.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.8600F, 1.7800F, 2.6000F}, new float[]{0.0000F, 20.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -24.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.8800F, 1.5600F, 1.8000F, 2.6000F}, new float[]{4.0000F, 26.0000F, -18.0000F, 34.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.8800F, 1.5600F, 1.8000F, 2.6000F}, new float[]{-4.0000F, -28.0000F, 20.0000F, -36.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.8000F, 1.3000F, 1.7000F, 2.6000F}, new float[]{0.9600F, 1.3800F, 1.1200F, 1.5400F, 0.9600F}, new float[]{0.9600F, 1.3800F, 1.1200F, 1.5400F, 0.9600F}, new float[]{0.9600F, 1.3800F, 1.1200F, 1.5400F, 0.9600F}, new int[]{6, 6, 3, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.double_lunge", c);
    }

    // 5: double_slash (2.50s = 50t; slash1 14t=0.70s, slash2 29t=1.45s, full 22-bone chain)
    private static void initClip_5() {
        Clip c = new Clip("animation.the_open_eyed.double_slash", 2.5000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.5400F, 0.7000F, 1.2600F, 1.4500F, 1.8500F, 2.5000F}, new float[]{0.0000F, -2.5000F, 2.5000F, 3.5000F, -3.5000F, -1.0000F, 0.0000F}, new float[]{2.0000F, 5.2000F, 0.0000F, 4.8000F, -1.2000F, 0.5000F, 2.0000F}, new float[]{0.0000F, 4.5000F, -7.5000F, 3.0000F, -10.5000F, -5.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3600F, 0.5500F, 0.7000F, 0.9200F, 1.2800F, 1.4500F, 1.7600F, 2.5000F}, new float[]{2.0000F, -10.0000F, -14.0000F, 18.0000F, 12.0000F, -12.0000F, 22.0000F, 12.0000F, 2.0000F}, new float[]{0.0000F, -44.0000F, -52.0000F, 48.0000F, 42.0000F, 56.0000F, -54.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, 8.0000F, 6.0000F, 8.0000F, -10.0000F, -4.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 2, 3, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7400F, 1.3000F, 1.4900F, 2.5000F}, new float[]{-2.0000F, 4.0000F, -10.0000F, 6.0000F, -12.0000F, -2.0000F}, new float[]{0.0000F, 42.0000F, -38.0000F, -44.0000F, 40.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3600F, 0.5500F, 0.7000F, 0.9200F, 1.2800F, 1.4500F, 1.7800F, 2.5000F}, new float[]{0.0000F, -84.0000F, -96.0000F, -24.0000F, -32.0000F, -76.0000F, -18.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -48.0000F, -56.0000F, 54.0000F, 60.0000F, 66.0000F, -64.0000F, -38.0000F, 0.0000F}, new float[]{-6.0000F, -62.0000F, -70.0000F, -12.0000F, -16.0000F, 24.0000F, -74.0000F, -42.0000F, -6.0000F}, new int[]{2, 6, 3, 5, 2, 3, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7200F, 1.3000F, 1.4700F, 2.5000F}, new float[]{-8.0000F, -34.0000F, -10.0000F, -38.0000F, -8.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.3800F, 0.5700F, 0.7300F, 1.3100F, 1.4800F, 2.5000F}, new float[]{0.0000F, 68.0000F, 74.0000F, 98.0000F, 88.0000F, 108.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -24.0000F, 22.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, 26.0000F, -18.0000F, 24.0000F, -26.0000F, 0.0000F}, new int[]{2, 6, 3, 5, 3, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7200F, 1.2900F, 1.4700F, 2.5000F}, new float[]{0.0000F, -24.0000F, 30.0000F, -38.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -24.0000F, -22.0000F, 32.0000F, 0.0000F}, new float[]{6.0000F, 32.0000F, 40.0000F, 26.0000F, 44.0000F, 6.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.7400F, 1.4900F, 2.5000F}, new float[]{-10.0000F, -22.0000F, -26.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7600F, 1.3000F, 1.5100F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -34.0000F, -26.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 22.0000F, 14.0000F, -24.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.8200F, 1.5700F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -28.0000F, 30.0000F, 2.0000F}, new float[]{-2.0000F, 18.0000F, -20.0000F, -2.0000F}, new int[]{2, 5, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.7600F, 1.5200F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 20.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.7600F, 1.5200F, 2.5000F}, new float[]{0.0000F, 16.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -18.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.7600F, 1.5200F, 2.5000F}, new float[]{0.0000F, 16.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, 18.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.7800F, 1.5400F, 2.5000F}, new float[]{4.0000F, 22.0000F, 26.0000F, 4.0000F}, new float[]{0.0000F, 12.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.7800F, 1.5400F, 2.5000F}, new float[]{-4.0000F, -24.0000F, -28.0000F, -4.0000F}, new float[]{0.0000F, 12.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.7000F, 1.4500F, 2.5000F}, new float[]{0.9600F, 1.3500F, 1.5000F, 0.9600F}, new float[]{0.9600F, 1.3500F, 1.5000F, 0.9600F}, new float[]{0.9600F, 1.3500F, 1.5000F, 0.9600F}, new int[]{6, 6, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.double_slash", c);
    }

    // 6: forced_teleport (2.10s = 42t; ensnare 15t=0.75s, dimensional yank 25t=1.25s)
    private static void initClip_6() {
        Clip c = new Clip("animation.the_open_eyed.forced_teleport", 2.1000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.7500F, 1.1000F, 1.2500F, 1.5500F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 8.5000F, 10.0000F, 4.5000F, 3.5000F, 2.0000F}, new float[]{0.0000F, -2.5000F, -4.0000F, 6.5000F, 3.0000F, 0.0000F}, new int[]{6, 2, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F, 1.2500F, 2.1000F}, new float[]{2.0000F, 12.0000F, 16.0000F, -22.0000F, 2.0000F}, new float[]{0.0000F, 24.0000F, 28.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 2, 3, 5, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.7800F, 1.2900F, 2.1000F}, new float[]{-2.0000F, -8.0000F, 14.0000F, -2.0000F}, new float[]{0.0000F, -20.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F, 1.2500F, 2.1000F}, new float[]{0.0000F, -94.0000F, -104.0000F, -26.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -18.0000F, 36.0000F, 0.0000F}, new float[]{6.0000F, 14.0000F, 18.0000F, 28.0000F, 6.0000F}, new int[]{6, 2, 3, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.7800F, 1.1200F, 1.2800F, 2.1000F}, new float[]{-10.0000F, -8.0000F, -14.0000F, -82.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 2, 3, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.7500F, 1.1000F, 1.2600F, 2.1000F}, new float[]{0.0000F, -24.0000F, -30.0000F, -64.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -20.0000F, 12.0000F, 0.0000F}, new float[]{-6.0000F, -22.0000F, -26.0000F, -20.0000F, -6.0000F}, new int[]{6, 2, 3, 5, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.1200F, 1.2800F, 2.1000F}, new float[]{-8.0000F, -24.0000F, -36.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.7800F, 1.2900F, 2.1000F}, new float[]{0.0000F, 18.0000F, 44.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 14.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.1000F, 1.3100F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -34.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 24.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.1400F, 1.3700F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 18.0000F, -26.0000F, 2.0000F}, new float[]{-2.0000F, -10.0000F, 18.0000F, -2.0000F}, new int[]{6, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.1200F, 1.3300F, 2.1000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, -20.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.3300F, 2.1000F}, new float[]{0.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, 0.0000F}, new int[]{6, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.3300F, 2.1000F}, new float[]{0.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 0.0000F}, new int[]{6, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.1200F, 1.3400F, 2.1000F}, new float[]{4.0000F, 16.0000F, -22.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.1200F, 1.3400F, 2.1000F}, new float[]{-4.0000F, -16.0000F, 24.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.2500F, 2.1000F}, new float[]{0.9600F, 1.4000F, 1.6500F, 0.9600F}, new float[]{0.9600F, 1.4000F, 1.6500F, 0.9600F}, new float[]{0.9600F, 1.4000F, 1.6500F, 0.9600F}, new int[]{6, 3, 6, 6});
        c.add("corrupt_cubes_right", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.2500F, 2.1000F}, new float[]{1.0000F, 1.4500F, 1.7500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.7500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.7500F, 1.0000F}, new int[]{6, 3, 5, 6});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.1000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.forced_teleport", c);
    }

    // 7: leaping_slam (2.70s = 54t; crouch 0-9t, vault+zenith 9-31t, meteor slam 35t=1.75s)
    private static void initClip_7() {
        Clip c = new Clip("animation.the_open_eyed.leaping_slam", 2.7000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.4500F, 0.9500F, 1.5500F, 1.7500F, 1.9600F, 2.2200F, 2.7000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -4.5000F, 12.0000F, 16.0000F, -7.2000F, -2.5000F, -1.5000F, 2.0000F}, new float[]{0.0000F, 4.5000F, 2.0000F, 3.0000F, -12.0000F, -9.0000F, -6.0000F, 0.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1500F, 1.5500F, 1.7500F, 1.9800F, 2.2500F, 2.7000F}, new float[]{2.0000F, 28.0000F, -34.0000F, -38.0000F, 46.0000F, 32.0000F, 20.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, -22.0000F, -26.0000F, 24.0000F, 16.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4800F, 1.2000F, 1.5700F, 1.7900F, 2.2500F, 2.7000F}, new float[]{-2.0000F, -16.0000F, 24.0000F, 26.0000F, -24.0000F, -10.0000F, -2.0000F}, new float[]{0.0000F, 0.0000F, 14.0000F, 16.0000F, -14.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1500F, 1.5500F, 1.7500F, 1.9800F, 2.7000F}, new float[]{0.0000F, 28.0000F, -164.0000F, -172.0000F, -24.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -14.0000F, -18.0000F, 16.0000F, 10.0000F, 0.0000F}, new float[]{-6.0000F, -20.0000F, -26.0000F, -28.0000F, -10.0000F, -12.0000F, -6.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4800F, 1.1800F, 1.5700F, 1.7700F, 2.0200F, 2.7000F}, new float[]{-8.0000F, -32.0000F, -44.0000F, -48.0000F, -4.0000F, -16.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 1.2000F, 1.5800F, 1.7800F, 2.2400F, 2.7000F}, new float[]{0.0000F, 62.0000F, 68.0000F, 124.0000F, 68.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 12.0000F, 6.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, 12.0000F, 6.0000F, 0.0000F}, new int[]{2, 6, 4, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1500F, 1.5500F, 1.7700F, 2.2500F, 2.7000F}, new float[]{0.0000F, 24.0000F, -124.0000F, -132.0000F, 42.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 26.0000F, -20.0000F, -8.0000F, 0.0000F}, new float[]{6.0000F, 26.0000F, 38.0000F, 42.0000F, 46.0000F, 20.0000F, 6.0000F}, new int[]{2, 3, 6, 4, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.5700F, 1.8000F, 2.7000F}, new float[]{-10.0000F, -36.0000F, -8.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4800F, 1.1800F, 1.5700F, 1.8100F, 2.7000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -42.0000F, 36.0000F, -48.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 32.0000F, -26.0000F, 36.0000F, 0.0000F}, new int[]{2, 3, 6, 4, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.2400F, 1.6100F, 1.8700F, 2.7000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -32.0000F, 28.0000F, -38.0000F, 2.0000F}, new float[]{-2.0000F, 24.0000F, -20.0000F, 28.0000F, -2.0000F}, new int[]{2, 6, 4, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.5700F, 1.8200F, 2.7000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, -26.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.5700F, 1.8300F, 2.7000F}, new float[]{0.0000F, 26.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 30.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.5700F, 1.8300F, 2.7000F}, new float[]{0.0000F, 26.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -30.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.5800F, 1.8400F, 2.7000F}, new float[]{4.0000F, -28.0000F, 34.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.5800F, 1.8400F, 2.7000F}, new float[]{-4.0000F, 30.0000F, -36.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 4, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.5500F, 1.7600F, 2.7000F}, new float[]{0.9600F, 1.4800F, 1.6500F, 0.9600F}, new float[]{0.9600F, 1.4800F, 1.6500F, 0.9600F}, new float[]{0.9600F, 1.4800F, 1.6500F, 0.9600F}, new int[]{6, 4, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.7000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.7000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.7000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.7000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.leaping_slam", c);
    }

    // 8: supreme_overseer (2.20s = 44t; spin AoE 8t=0.40s, regal monitor stance held at 42t)
    private static void initClip_8() {
        Clip c = new Clip("animation.the_open_eyed.supreme_overseer", 2.2000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.4000F, 0.9000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 6.5000F, 12.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.2200F, 0.4200F, 0.8500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, 340.0000F, 360.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4000F, 0.9500F, 2.2000F}, new float[]{2.0000F, 14.0000F, -14.0000F, -14.0000F}, new float[]{0.0000F, 22.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4400F, 1.0500F, 2.2000F}, new float[]{-2.0000F, -8.0000F, 14.0000F, 14.0000F}, new float[]{0.0000F, -14.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.2400F, 0.4200F, 1.0500F, 2.2000F}, new float[]{0.0000F, -72.0000F, -52.0000F, -118.0000F, -118.0000F}, new float[]{0.0000F, -20.0000F, 32.0000F, -10.0000F, -10.0000F}, new float[]{-6.0000F, -56.0000F, -38.0000F, -22.0000F, -22.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4400F, 1.1000F, 2.2000F}, new float[]{-8.0000F, -18.0000F, -34.0000F, -34.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 5, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.2500F, 0.4200F, 1.0500F, 2.2000F}, new float[]{0.0000F, 86.0000F, 446.0000F, 360.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 16.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4200F, 1.0500F, 2.2000F}, new float[]{0.0000F, 22.0000F, -62.0000F, -62.0000F}, new float[]{0.0000F, -16.0000F, 18.0000F, 18.0000F}, new float[]{6.0000F, 38.0000F, 34.0000F, 34.0000F}, new int[]{2, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{-10.0000F, -28.0000F, -28.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4600F, 1.1000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, -38.0000F, -38.0000F}, new float[]{0.0000F, 22.0000F, 26.0000F, 26.0000F}, new int[]{6, 5, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.5200F, 1.1800F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -24.0000F, -28.0000F, -28.0000F}, new float[]{-2.0000F, 18.0000F, 22.0000F, 22.0000F}, new int[]{6, 5, 6, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.4800F, 1.1200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, 20.0000F, 20.0000F}, new float[]{0.0000F, -18.0000F, -14.0000F, -14.0000F}, new int[]{6, 5, 6, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.1200F, 2.2000F}, new float[]{0.0000F, 16.0000F, 16.0000F}, new float[]{0.0000F, -14.0000F, -14.0000F}, new float[]{0.0000F, 22.0000F, 22.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.1200F, 2.2000F}, new float[]{0.0000F, 16.0000F, 16.0000F}, new float[]{0.0000F, 14.0000F, 14.0000F}, new float[]{0.0000F, -22.0000F, -22.0000F}, new int[]{6, 6, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.4800F, 1.1400F, 2.2000F}, new float[]{4.0000F, -18.0000F, -14.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 5, 6, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.4800F, 1.1400F, 2.2000F}, new float[]{-4.0000F, 20.0000F, 16.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{6, 5, 6, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.4000F, 1.0500F, 2.2000F}, new float[]{0.9600F, 1.4800F, 1.5600F, 1.5600F}, new float[]{0.9600F, 1.4800F, 1.5600F, 1.5600F}, new float[]{0.9600F, 1.4800F, 1.5600F, 1.5600F}, new int[]{6, 5, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.supreme_overseer", c);
    }

    // 9: phenomena_delirium (3.00s = 60t; apocalyptic ascension & twin star summon, held at 56t)
    private static void initClip_9() {
        Clip c = new Clip("animation.the_open_eyed.phenomena_delirium", 3.0000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.9500F, 2.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 20.0000F, 22.0000F, 21.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.9500F, 2.1000F, 3.0000F}, new float[]{2.0000F, -24.0000F, -28.0000F, -26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0000F, 3.0000F}, new float[]{-2.0000F, 22.0000F, 24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.9500F, 3.0000F}, new float[]{0.0000F, -138.0000F, -142.0000F}, new float[]{0.0000F, -18.0000F, -20.0000F}, new float[]{-6.0000F, -48.0000F, -50.0000F}, new int[]{2, 5, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.0000F, 3.0000F}, new float[]{-8.0000F, -26.0000F, -26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 1.0200F, 3.0000F}, new float[]{0.0000F, 26.0000F, 28.0000F}, new float[]{0.0000F, 8.0000F, 8.0000F}, new float[]{0.0000F, 12.0000F, 12.0000F}, new int[]{2, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.9500F, 3.0000F}, new float[]{0.0000F, -132.0000F, -136.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F}, new float[]{6.0000F, 48.0000F, 50.0000F}, new int[]{2, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.0000F, 3.0000F}, new float[]{-10.0000F, -24.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -46.0000F, -48.0000F}, new float[]{0.0000F, 34.0000F, 36.0000F}, new int[]{2, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.0800F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, -34.0000F, -36.0000F}, new float[]{-2.0000F, 26.0000F, 28.0000F}, new int[]{2, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.0200F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, 34.0000F}, new float[]{0.0000F, -24.0000F, -26.0000F}, new int[]{2, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.0400F, 3.0000F}, new float[]{0.0000F, 22.0000F, 24.0000F}, new float[]{0.0000F, -18.0000F, -18.0000F}, new float[]{0.0000F, 28.0000F, 28.0000F}, new int[]{2, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.0400F, 3.0000F}, new float[]{0.0000F, 22.0000F, 24.0000F}, new float[]{0.0000F, 18.0000F, 18.0000F}, new float[]{0.0000F, -28.0000F, -28.0000F}, new int[]{2, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.0600F, 3.0000F}, new float[]{4.0000F, -24.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.0600F, 3.0000F}, new float[]{-4.0000F, 26.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.0000F, 3.0000F}, new float[]{0.9600F, 1.7500F, 1.7800F}, new float[]{0.9600F, 1.7500F, 1.7800F}, new float[]{0.9600F, 1.7500F, 1.7800F}, new int[]{6, 5, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F, 0.4000F, 1.1000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new int[]{6, 5, 6, 6});
        c.add("elemental_star_left", CH_POS, new float[]{0.0000F, 1.1000F, 3.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F}, new float[]{0.0000F, 22.0000F, 22.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F}, new int[]{6, 5, 6});
        c.add("elemental_star_left", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F, 0.4000F, 1.1000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.4500F}, new int[]{6, 5, 6, 6});
        c.add("elemental_star_right", CH_POS, new float[]{0.0000F, 1.1000F, 3.0000F}, new float[]{0.0000F, 6.0000F, 6.0000F}, new float[]{0.0000F, 22.0000F, 22.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F}, new int[]{6, 5, 6});
        c.add("elemental_star_right", CH_ROT, new float[]{0.0000F, 3.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.phenomena_delirium", c);
    }

    // 10: stunned (2.50s = 50t loop) — collapsed vulnerability with convulsive tremor & dimmed eye
    private static void initClip_10() {
        Clip c = new Clip("animation.the_open_eyed.stunned", 2.5000F, true);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-8.5000F, -10.5000F, -8.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{30.0000F, 36.0000F, 30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-3.0000F, 3.0000F, -3.0000F}, new int[]{6, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.8800F, 1.0000F, 1.2800F, 2.5000F}, new float[]{26.0000F, 30.0000F, 20.0000F, 32.0000F, 26.0000F}, new float[]{-6.0000F, -8.0000F, 12.0000F, 4.0000F, -6.0000F}, new float[]{4.0000F, 6.0000F, -8.0000F, -4.0000F, 4.0000F}, new int[]{6, 3, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.3000F, 2.5000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -8.0000F, -4.0000F}, new int[]{6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.3500F, 2.5000F}, new float[]{-14.0000F, -22.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{42.0000F, 48.0000F, 42.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new int[]{6, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.3000F, 2.5000F}, new float[]{22.0000F, 28.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 8.0000F, 4.0000F}, new int[]{6, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.3500F, 2.5000F}, new float[]{-12.0000F, -20.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.3000F, 2.5000F}, new float[]{24.0000F, 30.0000F, 24.0000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new float[]{-34.0000F, -40.0000F, -34.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.3800F, 2.5000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{-24.0000F, -30.0000F, -24.0000F}, new int[]{6, 6, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{-16.0000F, -20.0000F, -16.0000F}, new float[]{28.0000F, 34.0000F, 28.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{-18.0000F, -24.0000F, -18.0000F}, new float[]{12.0000F, 14.0000F, 12.0000F}, new float[]{-22.0000F, -26.0000F, -22.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{-18.0000F, -24.0000F, -18.0000F}, new float[]{-12.0000F, -14.0000F, -12.0000F}, new float[]{22.0000F, 26.0000F, 22.0000F}, new int[]{6, 6, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.3200F, 2.5000F}, new float[]{-14.0000F, -18.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.6500F, 0.8200F, 0.6500F}, new float[]{0.6500F, 0.8200F, 0.6500F}, new float[]{0.6500F, 0.8200F, 0.6500F}, new int[]{6, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{-10.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 1.2500F, 2.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        CLIPS.put("animation.the_open_eyed.stunned", c);
    }

    // 11: death (3.00s = 60t) — core overload recoil, shattering plunge & weighted collapse
    private static void initClip_11() {
        Clip c = new Clip("animation.the_open_eyed.death", 3.0000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.3500F, 1.0500F, 2.0800F, 2.3200F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{2.0000F, 10.5000F, 13.0000F, -16.0000F, -14.2000F, -16.5000F}, new float[]{0.0000F, 4.0000F, 2.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 4, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3500F, 1.0500F, 2.1000F, 2.3600F, 3.0000F}, new float[]{2.0000F, -30.0000F, -36.0000F, 56.0000F, 48.0000F, 60.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 6.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 4, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.3800F, 1.1000F, 2.1400F, 3.0000F}, new float[]{-2.0000F, -32.0000F, -26.0000F, 46.0000F, 52.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 8.0000F, 10.0000F}, new float[]{0.0000F, -8.0000F, 10.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 4, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3600F, 1.1000F, 2.1200F, 3.0000F}, new float[]{0.0000F, -124.0000F, -138.0000F, 34.0000F, 42.0000F}, new float[]{0.0000F, -16.0000F, -12.0000F, 12.0000F, 14.0000F}, new float[]{-6.0000F, -34.0000F, -28.0000F, -18.0000F, -12.0000F}, new int[]{3, 6, 4, 5, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4000F, 2.1600F, 3.0000F}, new float[]{-8.0000F, -34.0000F, -8.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.4000F, 2.1600F, 3.0000F}, new float[]{0.0000F, 28.0000F, 118.0000F, 126.0000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 22.0000F}, new float[]{0.0000F, 12.0000F, 42.0000F, 48.0000F}, new int[]{3, 6, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.3600F, 1.1000F, 2.1200F, 3.0000F}, new float[]{0.0000F, -118.0000F, -132.0000F, 38.0000F, 44.0000F}, new float[]{0.0000F, 16.0000F, 12.0000F, -14.0000F, -16.0000F}, new float[]{6.0000F, 34.0000F, 28.0000F, 20.0000F, 14.0000F}, new int[]{3, 6, 4, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.4000F, 2.1600F, 3.0000F}, new float[]{-10.0000F, -32.0000F, -6.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.4000F, 2.1800F, 3.0000F}, new float[]{0.0000F, 0.0000F, 28.0000F, 34.0000F}, new float[]{0.0000F, -44.0000F, 26.0000F, 30.0000F}, new float[]{0.0000F, 24.0000F, -52.0000F, -58.0000F}, new int[]{3, 6, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.4600F, 2.2400F, 3.0000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 28.0000F}, new float[]{2.0000F, -32.0000F, 20.0000F, 24.0000F}, new float[]{-2.0000F, 18.0000F, -42.0000F, -48.0000F}, new int[]{3, 6, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.4200F, 2.2000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 24.0000F, 30.0000F}, new float[]{0.0000F, 30.0000F, -22.0000F, -26.0000F}, new float[]{0.0000F, -18.0000F, 44.0000F, 50.0000F}, new int[]{3, 6, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 2.2000F, 3.0000F}, new float[]{0.0000F, -28.0000F, -32.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F}, new float[]{0.0000F, -34.0000F, -38.0000F}, new int[]{3, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 2.2000F, 3.0000F}, new float[]{0.0000F, -28.0000F, -32.0000F}, new float[]{0.0000F, -18.0000F, -20.0000F}, new float[]{0.0000F, 34.0000F, 38.0000F}, new int[]{3, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 2.2200F, 3.0000F}, new float[]{4.0000F, 34.0000F, 38.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 2.2200F, 3.0000F}, new float[]{-4.0000F, -26.0000F, -30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.1500F, 1.6500F, 3.0000F}, new float[]{0.9600F, 2.1500F, 0.0200F, 0.0200F}, new float[]{0.9600F, 2.1500F, 0.0200F, 0.0200F}, new float[]{0.9600F, 2.1500F, 0.0200F, 0.0200F}, new int[]{3, 4, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.6000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 28.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F, 745.0000F}, new int[]{3, 6, 6});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.6000F, 3.0000F}, new float[]{0.0000F, 0.0000F, -22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -420.0000F, -435.0000F}, new int[]{3, 6, 6});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 1.6000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F, -375.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 6});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 1.6000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F, 375.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{3, 6, 6});
        CLIPS.put("animation.the_open_eyed.death", c);
    }

    // 12: move_glide (2.00s = 40t loop) — aerodynamic hover-glide with staggered wing/skirt flow
    private static void initClip_12() {
        Clip c = new Clip("animation.the_open_eyed.move_glide", 2.0000F, true);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 1.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{3.8000F, 6.4000F, 3.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("body", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.0400F, 2.0000F}, new float[]{10.0000F, 6.0000F, 10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0600F, 2.0000F}, new float[]{-18.0000F, -22.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.0400F, 2.0000F}, new float[]{22.0000F, 28.0000F, 22.0000F}, new float[]{-8.0000F, -12.0000F, -8.0000F}, new float[]{-22.0000F, -28.0000F, -22.0000F}, new int[]{6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{-14.0000F, -20.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 1.1200F, 2.0000F}, new float[]{48.0000F, 54.0000F, 48.0000F}, new float[]{8.0000F, 10.0000F, 8.0000F}, new float[]{12.0000F, 14.0000F, 12.0000F}, new int[]{6, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.0400F, 2.0000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{8.0000F, 12.0000F, 8.0000F}, new float[]{22.0000F, 28.0000F, 22.0000F}, new int[]{6, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{-16.0000F, -22.0000F, -16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 1.0200F, 2.0000F}, new float[]{12.0000F, 16.0000F, 12.0000F}, new float[]{-28.0000F, -38.0000F, -28.0000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new int[]{6, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 1.1400F, 2.0000F}, new float[]{10.0000F, 14.0000F, 10.0000F}, new float[]{-20.0000F, -30.0000F, -20.0000F}, new float[]{8.0000F, 14.0000F, 8.0000F}, new int[]{6, 6, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 1.0600F, 2.0000F}, new float[]{10.0000F, 14.0000F, 10.0000F}, new float[]{20.0000F, 28.0000F, 20.0000F}, new float[]{-10.0000F, -14.0000F, -10.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 1.0800F, 2.0000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{-12.0000F, -18.0000F, -12.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new int[]{6, 6, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 1.0800F, 2.0000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{-14.0000F, -18.0000F, -14.0000F}, new int[]{6, 6, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{18.0000F, 26.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 1.1000F, 2.0000F}, new float[]{-24.0000F, -32.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{6, 6, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 1.0000F, 2.0000F}, new float[]{1.0200F, 1.1600F, 1.0200F}, new float[]{1.0200F, 1.1600F, 1.0200F}, new float[]{1.0200F, 1.1600F, 1.0200F}, new int[]{6, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.move_glide", c);
    }

    // 13: overseer_sweep (1.40s = 28t; coil 0-6t, elemental cleave snap 9t=0.45s, settle 9-28t)
    private static void initClip_13() {
        Clip c = new Clip("animation.the_open_eyed.overseer_sweep", 1.4000F, false);
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("body", CH_POS, new float[]{0.0000F, 0.3000F, 0.4500F, 0.7000F, 1.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 8.5000F, 0.5000F, 1.5000F, 2.0000F}, new float[]{0.0000F, 6.0000F, -10.0000F, -6.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.3000F, 0.4500F, 0.7200F, 1.4000F}, new float[]{0.0000F, -8.0000F, 16.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, -48.0000F, 44.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.3000F, 0.4500F, 0.7500F, 1.4000F}, new float[]{-14.0000F, -16.0000F, 24.0000F, 12.0000F, 2.0000F}, new float[]{0.0000F, -34.0000F, 48.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 10.0000F, 4.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.3100F, 0.4800F, 1.4000F}, new float[]{12.0000F, 6.0000F, -14.0000F, -2.0000F}, new float[]{0.0000F, 44.0000F, -42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.3000F, 0.4500F, 0.7800F, 1.4000F}, new float[]{-116.0000F, -96.0000F, -18.0000F, -26.0000F, 0.0000F}, new float[]{-10.0000F, -64.0000F, 68.0000F, 38.0000F, 0.0000F}, new float[]{-22.0000F, -76.0000F, -10.0000F, -12.0000F, -6.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.3100F, 0.4700F, 1.4000F}, new float[]{-34.0000F, -40.0000F, -6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.3100F, 0.4800F, 1.4000F}, new float[]{0.0000F, 78.0000F, 108.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -26.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -24.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.3000F, 0.4700F, 1.4000F}, new float[]{-60.0000F, -32.0000F, 38.0000F, 0.0000F}, new float[]{18.0000F, 34.0000F, -28.0000F, 0.0000F}, new float[]{34.0000F, 38.0000F, 48.0000F, 6.0000F}, new int[]{2, 3, 5, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.4900F, 1.4000F}, new float[]{-28.0000F, -12.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.3100F, 0.5100F, 1.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-36.0000F, 30.0000F, -48.0000F, 0.0000F}, new float[]{24.0000F, -20.0000F, 34.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.3400F, 0.5600F, 1.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-26.0000F, 24.0000F, -36.0000F, 2.0000F}, new float[]{20.0000F, -14.0000F, 26.0000F, -2.0000F}, new int[]{2, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.3200F, 0.5200F, 1.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{20.0000F, -24.0000F, 34.0000F, 0.0000F}, new float[]{-14.0000F, 16.0000F, -24.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.5200F, 1.4000F}, new float[]{16.0000F, -20.0000F, 0.0000F}, new float[]{-14.0000F, -20.0000F, 0.0000F}, new float[]{22.0000F, 28.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.5200F, 1.4000F}, new float[]{16.0000F, -20.0000F, 0.0000F}, new float[]{14.0000F, 20.0000F, 0.0000F}, new float[]{-22.0000F, -28.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4000F}, new float[]{-14.0000F, 28.0000F, 4.0000F}, new float[]{0.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4000F}, new float[]{16.0000F, -30.0000F, -4.0000F}, new float[]{0.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.4500F, 1.4000F}, new float[]{1.5600F, 1.7200F, 0.9600F}, new float[]{1.5600F, 1.7200F, 0.9600F}, new float[]{1.5600F, 1.7200F, 0.9600F}, new int[]{3, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.4000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.4000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 1.4000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 1.4000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.overseer_sweep", c);
    }

    // 14: delirium_finisher (2.60s = 52t; decree #1 15t=0.75s, decree #2 21t=1.05s, descent 21-52t)
    private static void initClip_14() {
        Clip c = new Clip("animation.the_open_eyed.delirium_finisher", 2.6000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.5500F, 0.7500F, 0.9200F, 1.0500F, 1.6000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{21.0000F, 28.0000F, 14.0000F, 16.5000F, 10.0000F, 6.0000F, 2.0000F}, new float[]{0.0000F, 2.0000F, -6.0000F, -4.0000F, -8.5000F, -3.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6, 6});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7500F, 0.9200F, 1.0500F, 1.6500F, 2.6000F}, new float[]{-26.0000F, -36.0000F, 36.0000F, 18.0000F, 34.0000F, 14.0000F, 2.0000F}, new float[]{0.0000F, -18.0000F, 22.0000F, 28.0000F, -24.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 4.0000F, 0.0000F, -6.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 3, 5, 6, 6});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7800F, 1.0800F, 1.7000F, 2.6000F}, new float[]{24.0000F, 30.0000F, -22.0000F, -20.0000F, -8.0000F, -2.0000F}, new float[]{0.0000F, 14.0000F, -16.0000F, 18.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 5, 6, 6});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7500F, 1.0800F, 1.5500F, 2.6000F}, new float[]{-142.0000F, -168.0000F, -18.0000F, -28.0000F, -18.0000F, 0.0000F}, new float[]{-20.0000F, -12.0000F, 20.0000F, 12.0000F, 6.0000F, 0.0000F}, new float[]{-50.0000F, -24.0000F, -12.0000F, -14.0000F, -10.0000F, -6.0000F}, new int[]{2, 3, 5, 6, 6, 6});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7700F, 2.6000F}, new float[]{-26.0000F, -38.0000F, -6.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("celestial_spear", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7800F, 1.6000F, 2.6000F}, new float[]{28.0000F, 46.0000F, 124.0000F, 56.0000F, 0.0000F}, new float[]{8.0000F, 10.0000F, 6.0000F, 4.0000F, 0.0000F}, new float[]{12.0000F, 16.0000F, 10.0000F, 6.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5500F, 0.9000F, 1.0500F, 1.6000F, 2.6000F}, new float[]{-136.0000F, -158.0000F, -164.0000F, -16.0000F, -12.0000F, 0.0000F}, new float[]{20.0000F, 14.0000F, 18.0000F, -24.0000F, -8.0000F, 0.0000F}, new float[]{50.0000F, 28.0000F, 32.0000F, 18.0000F, 14.0000F, 6.0000F}, new int[]{2, 6, 3, 5, 6, 6});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.9200F, 1.0700F, 2.6000F}, new float[]{-24.0000F, -36.0000F, -6.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("giant_wing_left", CH_ROT, new float[]{0.0000F, 0.5700F, 0.8000F, 1.1200F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-48.0000F, 36.0000F, -52.0000F, -44.0000F, 0.0000F}, new float[]{36.0000F, -24.0000F, 38.0000F, 32.0000F, 0.0000F}, new int[]{2, 3, 5, 6, 6});
        c.add("giant_wing_left_lower", CH_ROT, new float[]{0.0000F, 0.6100F, 0.8600F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-36.0000F, 28.0000F, -40.0000F, 2.0000F}, new float[]{28.0000F, -18.0000F, 30.0000F, -2.0000F}, new int[]{2, 3, 5, 6});
        c.add("broken_wing_right", CH_ROT, new float[]{0.0000F, 0.5800F, 0.8200F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{34.0000F, -26.0000F, 38.0000F, 0.0000F}, new float[]{-26.0000F, 18.0000F, -28.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("lower_wing_left", CH_ROT, new float[]{0.0000F, 0.8200F, 2.6000F}, new float[]{24.0000F, -24.0000F, 0.0000F}, new float[]{-18.0000F, -22.0000F, 0.0000F}, new float[]{28.0000F, 32.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("lower_wing_right", CH_ROT, new float[]{0.0000F, 0.8200F, 2.6000F}, new float[]{24.0000F, -24.0000F, 0.0000F}, new float[]{18.0000F, 22.0000F, 0.0000F}, new float[]{-28.0000F, -32.0000F, 0.0000F}, new int[]{2, 5, 6});
        c.add("skirt_front", CH_ROT, new float[]{0.0000F, 0.5800F, 0.8200F, 2.6000F}, new float[]{-24.0000F, -30.0000F, 36.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("skirt_back", CH_ROT, new float[]{0.0000F, 0.5800F, 0.8200F, 2.6000F}, new float[]{26.0000F, 32.0000F, -34.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{2, 3, 5, 6});
        c.add("chest_eye", CH_SCALE, new float[]{0.0000F, 0.5500F, 0.7800F, 1.0500F, 2.6000F}, new float[]{1.7800F, 2.2000F, 1.3500F, 1.6800F, 0.9600F}, new float[]{1.7800F, 2.2000F, 1.3500F, 1.6800F, 0.9600F}, new float[]{1.7800F, 2.2000F, 1.3500F, 1.6800F, 0.9600F}, new int[]{6, 3, 6, 6, 6});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1440.0000F}, new int[]{1, 1});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1080.0000F}, new int[]{1, 1});
        c.add("cube_tail", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1080.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("corrupt_cubes_right", CH_ROT, new float[]{0.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("elemental_star_left", CH_SCALE, new float[]{0.0000F, 0.5500F, 0.7500F, 0.9500F}, new float[]{1.4500F, 1.9500F, 2.2000F, 0.0000F}, new float[]{1.4500F, 1.9500F, 2.2000F, 0.0000F}, new float[]{1.4500F, 1.9500F, 2.2000F, 0.0000F}, new int[]{6, 3, 4, 1});
        c.add("elemental_star_left", CH_POS, new float[]{0.0000F, 0.5500F, 0.7800F}, new float[]{-6.0000F, -10.0000F, 14.0000F}, new float[]{22.0000F, 30.0000F, -28.0000F}, new float[]{-6.0000F, -10.0000F, -32.0000F}, new int[]{6, 3, 6});
        c.add("elemental_star_left", CH_ROT, new float[]{0.0000F, 0.9500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("elemental_star_right", CH_SCALE, new float[]{0.0000F, 0.8500F, 1.0500F, 1.2500F}, new float[]{1.4500F, 2.0500F, 2.2500F, 0.0000F}, new float[]{1.4500F, 2.0500F, 2.2500F, 0.0000F}, new float[]{1.4500F, 2.0500F, 2.2500F, 0.0000F}, new int[]{6, 3, 4, 1});
        c.add("elemental_star_right", CH_POS, new float[]{0.0000F, 0.8500F, 1.0800F}, new float[]{6.0000F, 10.0000F, -14.0000F}, new float[]{22.0000F, 32.0000F, -28.0000F}, new float[]{-6.0000F, -10.0000F, -32.0000F}, new int[]{6, 3, 6});
        c.add("elemental_star_right", CH_ROT, new float[]{0.0000F, 1.2500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new float[]{0.0000F, -720.0000F}, new int[]{1, 1});
        CLIPS.put("animation.the_open_eyed.delirium_finisher", c);
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
        float t = timeSeconds;
        if (clip.lengthSeconds > 0.001F) {
            t = clip.loop ? (t % clip.lengthSeconds) : Math.min(t, clip.lengthSeconds);
            if (t < 0.0F) t += clip.lengthSeconds;
        }
        return t;
    }

    private static float applyEasing(int mode, float u) {
        return switch (mode) {
            case INTERP_EASE_IN_BACK -> {
                float c1 = 1.70158F;
                float c3 = c1 + 1.0F;
                yield c3 * u * u * u - c1 * u * u;
            }
            case INTERP_EASE_OUT_EXPO -> (u >= 1.0F) ? 1.0F : (1.0F - (float) Math.pow(2.0D, -10.0D * u));
            case INTERP_EASE_IN_EXPO -> (u <= 0.0F) ? 0.0F : (float) Math.pow(2.0D, 10.0D * (u - 1.0D));
            case INTERP_EASE_OUT_BACK -> {
                float c1 = 1.70158F;
                float c3 = c1 + 1.0F;
                float m = u - 1.0F;
                yield 1.0F + c3 * m * m * m + c1 * m * m;
            }
            case INTERP_EASE_IN_OUT_SINE -> (float) (-(Math.cos(Math.PI * u) - 1.0D) * 0.5D);
            default -> u;
        };
    }

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
        float h00 = 2.0F * u3 - 3.0F * u2 + 1.0F;
        float h10 = u3 - 2.0F * u2 + u;
        float h01 = -2.0F * u3 + 3.0F * u2;
        float h11 = u3 - u2;
        return h00 * p1 + h10 * m1 + h01 * p2 + h11 * m2;
    }

    private static void sampleTrack(Track tr, float t, boolean loop, float[] out) {
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
        float dt = Math.max(1e-5F, tr.times[i + 1] - tr.times[i]);
        float u = (t - tr.times[i]) / dt;
        int mode = tr.interps[i];
        if (mode == INTERP_SMOOTH) {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
        } else {
            float e = applyEasing(mode, u);
            out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * e;
            out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * e;
            out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * e;
        }
    }

    private static void applyWeighted(TheOpenEyedModel<?> model, Clip clip, float timeSeconds, float weight) {
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
