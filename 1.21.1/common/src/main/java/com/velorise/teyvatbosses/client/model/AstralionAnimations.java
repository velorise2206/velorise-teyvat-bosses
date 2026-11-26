// Compiled from overhauled astralion_prismatic_sovereign.bbmodel (16 AAA animations, 40-bone rig)
package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AstralionAnimations {
    private AstralionAnimations() {}

    public static final int CH_ROT   = 0;
    public static final int CH_POS   = 1;
    public static final int CH_SCALE = 2;

    public static final int INTERP_SMOOTH = 0;
    public static final int INTERP_LINEAR = 1;

    private static final class Track {
        final String bone;
        final int channel;
        final float[] times, xs, ys, zs;
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
        Clip c = new Clip("animation.astralion.idle", 4.0000F, true);
        c.add("body", CH_POS, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{1.5000F, 4.4000F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F, 3.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 1.5000F, 0.0000F, -1.5000F, 0.0000F}, new float[]{0.0000F, -1.0000F, 0.0000F, 1.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 2.0800F, 4.0000F}, new float[]{0.0000F, -3.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.0500F, 2.1400F, 3.0800F, 4.0000F}, new float[]{0.0000F, 1.5000F, 3.2000F, 1.5000F, 0.0000F}, new float[]{0.0000F, 3.5000F, 0.0000F, -3.5000F, 0.0000F}, new float[]{0.0000F, -1.2000F, 0.0000F, 1.2000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 2.1200F, 4.0000F}, new float[]{0.0000F, 3.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("halo_shards", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_core", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{1.0000F, 1.1400F, 1.0000F}, new float[]{1.0000F, 1.1400F, 1.0000F}, new float[]{1.0000F, 1.1400F, 1.0000F}, new int[]{0, 0, 0});
        c.add("omni_aegis", CH_ROT, new float[]{0.0000F, 4.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 2.0600F, 4.0000F}, new float[]{0.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -13.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.1600F, 4.0000F}, new float[]{-6.0000F, -12.0000F, -6.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 2.2200F, 4.0000F}, new float[]{0.0000F, 3.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 2.0600F, 4.0000F}, new float[]{-6.0000F, -11.0000F, -6.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 13.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.1600F, 4.0000F}, new float[]{-8.0000F, -14.0000F, -8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{4.0000F, 10.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{3.0000F, 4.5000F, 3.0000F}, new int[]{0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{6.0000F, 12.0000F, 6.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 2.1000F, 4.0000F}, new float[]{6.0000F, 12.0000F, 6.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-3.0000F, -4.5000F, -3.0000F}, new int[]{0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 2.1800F, 4.0000F}, new float[]{8.0000F, 14.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 2.1500F, 4.0000F}, new float[]{-4.0000F, -9.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 2.1500F, 4.0000F}, new float[]{5.0000F, 11.0000F, 5.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 2.1500F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 9.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 2.1500F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -9.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 2.0400F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, -22.0000F, -10.0000F}, new float[]{0.0000F, 7.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 2.1200F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-8.0000F, -18.0000F, -8.0000F}, new float[]{0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -14.0000F, -6.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 2.0400F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 22.0000F, 10.0000F}, new float[]{0.0000F, -7.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 2.1200F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{8.0000F, 18.0000F, 8.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 14.0000F, 6.0000F}, new float[]{0.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.5000F, 1.0000F, 1.5000F, 2.0000F, 2.5000F, 3.0000F, 3.5000F, 4.0000F}, new float[]{6.0000F, 8.8280F, 10.0000F, 8.8280F, 6.0000F, 3.1720F, 2.0000F, 3.1720F, 6.0000F}, new float[]{1.5000F, 1.0610F, 0.0000F, -1.0610F, -1.5000F, -1.0610F, -0.0000F, 1.0610F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.5000F, 1.0000F, 1.5000F, 2.0000F, 2.5000F, 3.0000F, 3.5000F, 4.0000F}, new float[]{1.1470F, 4.5290F, 8.1870F, 9.9780F, 8.8530F, 5.4710F, 1.8130F, 0.0220F, 1.1470F}, new float[]{1.2750F, 1.9910F, 1.5410F, 0.1880F, -1.2750F, -1.9910F, -1.5410F, -0.1880F, 1.2750F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.5000F, 1.0000F, 1.5000F, 2.0000F, 2.5000F, 3.0000F, 3.5000F, 4.0000F}, new float[]{-1.8940F, -0.9620F, 2.8760F, 7.3730F, 9.8940F, 8.9620F, 5.1240F, 0.6270F, -1.8940F}, new float[]{-0.4680F, 1.4050F, 2.4560F, 2.0680F, 0.4680F, -1.4050F, -2.4560F, -2.0680F, -0.4680F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.5000F, 1.0000F, 1.5000F, 2.0000F, 2.5000F, 3.0000F, 3.5000F, 4.0000F}, new float[]{-0.3720F, -3.7220F, -3.1340F, 1.0470F, 6.3720F, 9.7220F, 9.1340F, 4.9530F, -0.3720F}, new float[]{-2.6290F, -0.8370F, 1.4450F, 2.8810F, 2.6290F, 0.8370F, -1.4450F, -2.8810F, -2.6290F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.5000F, 1.0000F, 1.5000F, 2.0000F, 2.5000F, 3.0000F, 3.5000F, 4.0000F}, new float[]{4.9450F, -1.1770F, -5.4380F, -5.3420F, -0.9450F, 5.1770F, 9.4380F, 9.3420F, 4.9450F}, new float[]{-3.2540F, -3.2120F, -1.2880F, 1.3900F, 3.2540F, 3.2120F, 1.2880F, -1.3900F, -3.2540F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        CLIPS.put("animation.astralion.idle", c);
    }

    private static void initClip_1() {
        Clip c = new Clip("animation.astralion.hover_glide", 1.6000F, true);
        c.add("body", CH_POS, new float[]{0.0000F, 0.8000F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{5.0000F, 7.6000F, 5.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.8000F, 1.6000F}, new float[]{18.0000F, 23.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.8400F, 1.6000F}, new float[]{4.0000F, 1.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.8600F, 1.6000F}, new float[]{-18.0000F, -22.0000F, -18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.6000F}, new float[]{24.0000F, 30.0000F, 24.0000F}, new float[]{-10.0000F, -12.0000F, -10.0000F}, new float[]{-24.0000F, -30.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{-12.0000F, -18.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 0.9000F, 1.6000F}, new float[]{62.0000F, 68.0000F, 62.0000F}, new float[]{14.0000F, 14.0000F, 14.0000F}, new float[]{14.0000F, 16.0000F, 14.0000F}, new int[]{0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.6000F}, new float[]{-18.0000F, -13.0000F, -18.0000F}, new float[]{-12.0000F, -12.0000F, -12.0000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new int[]{0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{-16.0000F, -22.0000F, -16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_aegis", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.6000F}, new float[]{16.0000F, 23.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 5.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 0.9000F, 1.6000F}, new float[]{18.0000F, 26.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.6000F}, new float[]{22.0000F, 29.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -5.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 0.9000F, 1.6000F}, new float[]{22.0000F, 30.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{10.0000F, 16.0000F, 10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{26.0000F, 35.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{8.0000F, 12.0000F, 8.0000F}, new int[]{0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 0.8800F, 1.6000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-8.0000F, -12.0000F, -8.0000F}, new int[]{0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("omni_core", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.8000F, 1.6000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{-28.0000F, -38.0000F, -28.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.8600F, 1.6000F}, new float[]{10.0000F, 15.0000F, 10.0000F}, new float[]{-24.0000F, -34.0000F, -24.0000F}, new float[]{0.0000F, 3.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.9200F, 1.6000F}, new float[]{8.0000F, 13.0000F, 8.0000F}, new float[]{-20.0000F, -28.0000F, -20.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.8000F, 1.6000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{28.0000F, 38.0000F, 28.0000F}, new float[]{0.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.8600F, 1.6000F}, new float[]{10.0000F, 15.0000F, 10.0000F}, new float[]{24.0000F, 34.0000F, 24.0000F}, new float[]{0.0000F, -3.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.9200F, 1.6000F}, new float[]{8.0000F, 13.0000F, 8.0000F}, new float[]{20.0000F, 28.0000F, 20.0000F}, new float[]{0.0000F, -2.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F}, new float[]{28.0000F, 32.2430F, 34.0000F, 32.2430F, 28.0000F, 23.7570F, 22.0000F, 23.7570F, 28.0000F}, new float[]{2.0000F, 1.4140F, 0.0000F, -1.4140F, -2.0000F, -1.4140F, -0.0000F, 1.4140F, 2.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F}, new float[]{12.6060F, 17.3410F, 22.4620F, 24.9690F, 23.3940F, 18.6590F, 13.5380F, 11.0310F, 12.6060F}, new float[]{1.9120F, 2.9870F, 2.3120F, 0.2820F, -1.9120F, -2.9870F, -2.3120F, -0.2820F, 1.9120F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F}, new float[]{4.1420F, 5.3830F, 10.5010F, 16.4970F, 19.8580F, 18.6170F, 13.4990F, 7.5030F, 4.1420F}, new float[]{-0.7500F, 2.2480F, 3.9290F, 3.3080F, 0.7500F, -2.2480F, -3.9290F, -3.3080F, -0.7500F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F}, new float[]{3.6640F, -0.6430F, 0.1130F, 5.4890F, 12.3360F, 16.6430F, 15.8870F, 10.5110F, 3.6640F}, new float[]{-4.3820F, -1.3950F, 2.4090F, 4.8010F, 4.3820F, 1.3950F, -2.4090F, -4.8010F, -4.3820F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.2000F, 0.4000F, 0.6000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F}, new float[]{8.6810F, 1.0290F, -4.2980F, -4.1780F, 1.3190F, 8.9710F, 14.2980F, 14.1780F, 8.6810F}, new float[]{-5.5790F, -5.5070F, -2.2090F, 2.3830F, 5.5790F, 5.5070F, 2.2090F, -2.3830F, -5.5790F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        CLIPS.put("animation.astralion.hover_glide", c);
    }

    private static void initClip_2() {
        Clip c = new Clip("animation.astralion.attack_prismatic_cleave", 1.6000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4500F, 0.5800F, 0.7600F, 0.8800F, 1.1200F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, -2.5000F, -4.5000F, -3.5000F, -3.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 5.8000F, -12.5000F, -11.0000F, -9.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4500F, 0.5800F, 0.7600F, 0.8800F, 1.1200F, 1.6000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 26.0000F, 22.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, -40.0000F, 40.0000F, 34.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, -10.0000F, 10.0000F, 8.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6000F, 0.7900F, 1.1500F, 1.6000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -18.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 34.0000F, -32.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.6000F, 0.7800F, 1.1500F, 1.6000F}, new float[]{0.0000F, 8.0000F, 24.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4500F, 0.5800F, 0.7600F, 0.8800F, 1.1200F, 1.6000F}, new float[]{0.0000F, -144.0000F, -152.0000F, -26.0000F, -34.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -22.0000F, 22.0000F, 18.0000F, 14.0000F, 0.0000F}, new float[]{-6.0000F, -38.0000F, -42.0000F, -10.0000F, -14.0000F, -12.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7800F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 14.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6000F, 0.7800F, 1.1400F, 1.6000F}, new float[]{0.0000F, -32.0000F, -38.0000F, -6.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6000F, 0.7900F, 1.1400F, 1.6000F}, new float[]{0.0000F, 88.0000F, 94.0000F, 104.0000F, 86.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 10.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 22.0000F, 14.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7800F, 1.1400F, 1.6000F}, new float[]{0.0000F, -30.0000F, 34.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 20.0000F, 12.0000F, 0.0000F}, new float[]{6.0000F, 28.0000F, 42.0000F, 26.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7800F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.6000F, 0.8000F, 1.6000F}, new float[]{0.0000F, -24.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7600F, 1.1200F, 1.6000F}, new float[]{0.0000F, -18.0000F, -46.0000F, -38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 8.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7800F, 1.1400F, 1.6000F}, new float[]{0.0000F, 22.0000F, 34.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7600F, 1.1200F, 1.6000F}, new float[]{0.0000F, 18.0000F, 36.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7800F, 1.6000F}, new float[]{0.0000F, 16.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.5800F, 0.7600F, 1.6000F}, new float[]{1.0000F, 1.3500F, 1.6500F, 1.0000F}, new float[]{1.0000F, 1.3500F, 1.6500F, 1.0000F}, new float[]{1.0000F, 1.3500F, 1.6500F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.6000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7800F, 1.1200F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -42.0000F, -14.7000F, 0.0000F}, new float[]{0.0000F, -6.4000F, 16.0000F, 4.8000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7800F, 1.1200F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 42.0000F, 14.7000F, 0.0000F}, new float[]{0.0000F, 6.4000F, -16.0000F, -4.8000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.5840F, 0.8200F, 1.1400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 23.8000F, -35.7000F, -12.4950F, 0.0000F}, new float[]{0.0000F, -5.4400F, 13.6000F, 4.0800F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.5840F, 0.8200F, 1.1400F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -23.8000F, 35.7000F, 12.4950F, 0.0000F}, new float[]{0.0000F, 5.4400F, -13.6000F, -4.0800F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.6080F, 0.8600F, 1.1600F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 19.6000F, -29.4000F, -10.2900F, 0.0000F}, new float[]{0.0000F, -4.4800F, 11.2000F, 3.3600F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.6080F, 0.8600F, 1.1600F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -19.6000F, 29.4000F, 10.2900F, 0.0000F}, new float[]{0.0000F, 4.4800F, -11.2000F, -3.3600F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.5600F, 0.8000F, 1.1600F, 1.6000F}, new float[]{0.0000F, 10.0000F, 42.0000F, 14.7000F, 0.0000F}, new float[]{0.0000F, 4.8000F, -16.0000F, -4.8000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.5950F, 0.8500F, 1.1900F, 1.6000F}, new float[]{0.0000F, 8.5000F, 35.7000F, 12.4950F, 0.0000F}, new float[]{0.0000F, 4.0800F, -13.6000F, -4.0800F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.6300F, 0.9000F, 1.2200F, 1.6000F}, new float[]{0.0000F, 7.5000F, 31.5000F, 11.0250F, 0.0000F}, new float[]{0.0000F, 3.6000F, -12.0000F, -3.6000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.6650F, 0.9500F, 1.2500F, 1.6000F}, new float[]{0.0000F, 7.0000F, 29.4000F, 10.2900F, 0.0000F}, new float[]{0.0000F, 3.3600F, -11.2000F, -3.3600F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.7000F, 1.0000F, 1.2800F, 1.6000F}, new float[]{0.0000F, 6.5000F, 27.3000F, 9.5550F, 0.0000F}, new float[]{0.0000F, 3.1200F, -10.4000F, -3.1200F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 0.5900F, 0.8300F, 1.6000F}, new float[]{0.0000F, 10.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 0.5900F, 0.8300F, 1.6000F}, new float[]{0.0000F, -10.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 0.5900F, 0.8300F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 20.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 0.5900F, 0.8300F, 1.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -20.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.attack_prismatic_cleave", c);
    }

    private static void initClip_3() {
        Clip c = new Clip("animation.astralion.attack_shield_bash_burst", 1.5000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4200F, 0.5600F, 0.7200F, 0.8400F, 1.0600F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.2000F, -2.8000F, -4.5000F, -3.2000F, -2.8000F, 0.0000F}, new float[]{0.0000F, 6.5000F, 7.5000F, -16.0000F, -13.5000F, -11.5000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4200F, 0.5600F, 0.7200F, 0.8400F, 1.0600F, 1.5000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 22.0000F, 16.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, 38.0000F, -42.0000F, -34.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 10.0000F, -10.0000F, -8.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7500F, 1.0800F, 1.5000F}, new float[]{0.0000F, 8.0000F, -16.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 34.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4200F, 0.5600F, 0.7200F, 0.8400F, 1.0600F, 1.5000F}, new float[]{0.0000F, -32.0000F, -36.0000F, -92.0000F, -84.0000F, -80.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, 30.0000F, -22.0000F, -18.0000F, -14.0000F, 0.0000F}, new float[]{6.0000F, 46.0000F, 50.0000F, 10.0000F, 12.0000F, 12.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7400F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7400F, 1.0800F, 1.5000F}, new float[]{0.0000F, -42.0000F, -8.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 0.5600F, 0.7200F, 1.0600F, 1.5000F}, new float[]{1.0000F, 1.1500F, 1.8500F, 1.6500F, 1.0000F}, new float[]{1.0000F, 1.1500F, 1.8500F, 1.6500F, 1.0000F}, new float[]{1.0000F, 1.1500F, 1.8500F, 1.6500F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_aegis", CH_ROT, new float[]{0.0000F, 1.5000F}, new float[]{0.0000F, -720.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7400F, 1.0800F, 1.5000F}, new float[]{0.0000F, -16.0000F, 38.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 12.0000F, 8.0000F, 0.0000F}, new float[]{-6.0000F, -20.0000F, -38.0000F, -28.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 0.7400F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.7600F, 1.5000F}, new float[]{0.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 0.7800F, 1.5000F}, new float[]{0.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7200F, 1.0600F, 1.5000F}, new float[]{0.0000F, -16.0000F, -44.0000F, -34.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, -8.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 0.7400F, 1.5000F}, new float[]{0.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7200F, 1.0600F, 1.5000F}, new float[]{0.0000F, 16.0000F, 34.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 8.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 0.7400F, 1.5000F}, new float[]{0.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.5600F, 0.7200F, 1.5000F}, new float[]{1.0000F, 0.8500F, 1.7000F, 1.0000F}, new float[]{1.0000F, 0.8500F, 1.7000F, 1.0000F}, new float[]{1.0000F, 0.8500F, 1.7000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.5000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.5400F, 0.7400F, 1.0600F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -44.0000F, -15.4000F, 0.0000F}, new float[]{0.0000F, -7.2000F, 18.0000F, 5.4000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.5400F, 0.7400F, 1.0600F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 44.0000F, 15.4000F, 0.0000F}, new float[]{0.0000F, 7.2000F, -18.0000F, -5.4000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.5640F, 0.7800F, 1.0800F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.1000F, -37.4000F, -13.0900F, 0.0000F}, new float[]{0.0000F, -6.1200F, 15.3000F, 4.5900F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.5640F, 0.7800F, 1.0800F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -22.1000F, 37.4000F, 13.0900F, 0.0000F}, new float[]{0.0000F, 6.1200F, -15.3000F, -4.5900F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.5880F, 0.8200F, 1.1000F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 18.2000F, -30.8000F, -10.7800F, 0.0000F}, new float[]{0.0000F, -5.0400F, 12.6000F, 3.7800F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.5880F, 0.8200F, 1.1000F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.2000F, 30.8000F, 10.7800F, 0.0000F}, new float[]{0.0000F, 5.0400F, -12.6000F, -3.7800F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.5400F, 0.7600F, 1.1000F, 1.5000F}, new float[]{0.0000F, 8.0000F, 44.0000F, 15.4000F, 0.0000F}, new float[]{0.0000F, -5.4000F, 18.0000F, 5.4000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.5750F, 0.8100F, 1.1300F, 1.5000F}, new float[]{0.0000F, 6.8000F, 37.4000F, 13.0900F, 0.0000F}, new float[]{0.0000F, -4.5900F, 15.3000F, 4.5900F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.6100F, 0.8600F, 1.1600F, 1.5000F}, new float[]{0.0000F, 6.0000F, 33.0000F, 11.5500F, 0.0000F}, new float[]{0.0000F, -4.0500F, 13.5000F, 4.0500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.6450F, 0.9100F, 1.1900F, 1.5000F}, new float[]{0.0000F, 5.6000F, 30.8000F, 10.7800F, 0.0000F}, new float[]{0.0000F, -3.7800F, 12.6000F, 3.7800F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.6800F, 0.9600F, 1.2200F, 1.5000F}, new float[]{0.0000F, 5.2000F, 28.6000F, 10.0100F, 0.0000F}, new float[]{0.0000F, -3.5100F, 11.7000F, 3.5100F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7900F, 1.5000F}, new float[]{0.0000F, 10.0000F, -30.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7900F, 1.5000F}, new float[]{0.0000F, -10.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7900F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 22.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7900F, 1.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -22.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.attack_shield_bash_burst", c);
    }

    private static void initClip_4() {
        Clip c = new Clip("animation.astralion.combo_omni_trinity", 2.8000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4500F, 0.5600F, 0.7200F, 1.1500F, 1.2800F, 1.4500F, 1.9500F, 2.0800F, 2.2500F, 2.4000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.0000F, -2.4000F, -3.8000F, -2.2000F, -2.8000F, -4.0000F, 6.5000F, 7.5000F, -4.8000F, -3.0000F, 0.0000F}, new float[]{0.0000F, 4.5000F, 5.2000F, -10.5000F, -5.0000F, -3.5000F, -13.5000F, -8.0000F, -7.0000F, -17.0000F, -14.5000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 1.7000F, 2.2500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 360.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7200F, 1.2600F, 1.4500F, 2.0600F, 2.2500F, 2.4500F, 2.8000F}, new float[]{0.0000F, -16.0000F, 22.0000F, -12.0000F, 24.0000F, -24.0000F, 32.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, -38.0000F, 40.0000F, 36.0000F, -40.0000F, -22.0000F, 24.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 10.0000F, 8.0000F, -10.0000F, -6.0000F, 8.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5800F, 0.7600F, 1.3000F, 1.4900F, 2.0800F, 2.2900F, 2.8000F}, new float[]{0.0000F, 10.0000F, -14.0000F, 8.0000F, -16.0000F, 18.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, -30.0000F, -28.0000F, 30.0000F, 18.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.7200F, 1.4500F, 2.2500F, 2.8000F}, new float[]{0.0000F, 16.0000F, 20.0000F, 32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7200F, 1.2800F, 1.4500F, 2.0600F, 2.2500F, 2.4600F, 2.8000F}, new float[]{0.0000F, -146.0000F, -28.0000F, 20.0000F, 32.0000F, -158.0000F, -22.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 22.0000F, -10.0000F, 0.0000F, -14.0000F, 24.0000F, 14.0000F, 0.0000F}, new float[]{-6.0000F, -38.0000F, -10.0000F, -30.0000F, -38.0000F, -42.0000F, -10.0000F, -12.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5700F, 0.7400F, 1.4700F, 2.0800F, 2.2700F, 2.8000F}, new float[]{0.0000F, -34.0000F, -8.0000F, -22.0000F, -40.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7400F, 2.0600F, 2.2700F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, 12.0000F, -24.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 0.5600F, 0.7500F, 1.4800F, 2.0800F, 2.2800F, 2.8000F}, new float[]{0.0000F, 90.0000F, 102.0000F, 72.0000F, 96.0000F, 108.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 12.0000F, 8.0000F, 16.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 16.0000F, 12.0000F, 22.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.7200F, 1.2600F, 1.4500F, 2.0600F, 2.2500F, 2.8000F}, new float[]{0.0000F, 26.0000F, -38.0000F, -94.0000F, -64.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 26.0000F, -20.0000F, 16.0000F, -18.0000F, 0.0000F}, new float[]{6.0000F, 32.0000F, 44.0000F, 10.0000F, 36.0000F, 44.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.2800F, 1.4700F, 2.2800F, 2.8000F}, new float[]{0.0000F, -38.0000F, -8.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 1.2600F, 1.4700F, 2.2700F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -14.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 1.2600F, 1.4500F, 1.9000F, 2.8000F}, new float[]{1.0000F, 1.1000F, 1.7500F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.1000F, 1.7500F, 1.0000F, 1.0000F}, new float[]{1.0000F, 1.1000F, 1.7500F, 1.0000F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.7200F, 1.4500F, 2.2500F, 2.8000F}, new float[]{0.0000F, -38.0000F, 28.0000F, -48.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 6.0000F, 8.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 0.7400F, 1.4700F, 2.2700F, 2.8000F}, new float[]{0.0000F, 28.0000F, 20.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.7200F, 1.4500F, 2.2500F, 2.8000F}, new float[]{0.0000F, 26.0000F, -40.0000F, 36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 0.7400F, 1.4700F, 2.2700F, 2.8000F}, new float[]{0.0000F, 18.0000F, 30.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.7200F, 1.4500F, 2.2500F, 2.8000F}, new float[]{1.0000F, 1.4500F, 1.6000F, 1.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.6000F, 1.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 1.6000F, 1.9500F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 2.0400F, 2.2700F, 2.5200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 36.0000F, -48.0000F, -16.8000F, 0.0000F}, new float[]{0.0000F, -8.8000F, 22.0000F, 6.6000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 2.0400F, 2.2700F, 2.5200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, 48.0000F, 16.8000F, 0.0000F}, new float[]{0.0000F, 8.8000F, -22.0000F, -6.6000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 2.0640F, 2.3100F, 2.5400F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.6000F, -40.8000F, -14.2800F, 0.0000F}, new float[]{0.0000F, -7.4800F, 18.7000F, 5.6100F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 2.0640F, 2.3100F, 2.5400F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -30.6000F, 40.8000F, 14.2800F, 0.0000F}, new float[]{0.0000F, 7.4800F, -18.7000F, -5.6100F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 2.0880F, 2.3500F, 2.5600F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 25.2000F, -33.6000F, -11.7600F, 0.0000F}, new float[]{0.0000F, -6.1600F, 15.4000F, 4.6200F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 2.0880F, 2.3500F, 2.5600F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -25.2000F, 33.6000F, 11.7600F, 0.0000F}, new float[]{0.0000F, 6.1600F, -15.4000F, -4.6200F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 2.0400F, 2.3000F, 2.5500F, 2.8000F}, new float[]{0.0000F, 12.0000F, 48.0000F, 16.8000F, 0.0000F}, new float[]{0.0000F, 7.2000F, -24.0000F, -7.2000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 2.0750F, 2.3500F, 2.5800F, 2.8000F}, new float[]{0.0000F, 10.2000F, 40.8000F, 14.2800F, 0.0000F}, new float[]{0.0000F, 6.1200F, -20.4000F, -6.1200F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 2.1100F, 2.4000F, 2.6100F, 2.8000F}, new float[]{0.0000F, 9.0000F, 36.0000F, 12.6000F, 0.0000F}, new float[]{0.0000F, 5.4000F, -18.0000F, -5.4000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 2.1450F, 2.4500F, 2.6400F, 2.8000F}, new float[]{0.0000F, 8.4000F, 33.6000F, 11.7600F, 0.0000F}, new float[]{0.0000F, 5.0400F, -16.8000F, -5.0400F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 2.1800F, 2.5000F, 2.6700F, 2.8000F}, new float[]{0.0000F, 7.8000F, 31.2000F, 10.9200F, 0.0000F}, new float[]{0.0000F, 4.6800F, -15.6000F, -4.6800F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 2.0700F, 2.3200F, 2.8000F}, new float[]{0.0000F, 10.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 2.0700F, 2.3200F, 2.8000F}, new float[]{0.0000F, -10.0000F, 38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 2.0700F, 2.3200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 24.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 2.0700F, 2.3200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -24.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.combo_omni_trinity", c);
    }

    private static void initClip_5() {
        Clip c = new Clip("animation.astralion.attack_wing_barrage", 2.2000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4500F, 0.7200F, 1.1400F, 1.5600F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 9.5000F, 8.8000F, 9.2000F, 8.2000F, 0.0000F}, new float[]{0.0000F, 4.5000F, 5.5000F, 5.8000F, 4.8000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 0.8600F, 1.0000F, 1.1400F, 1.2800F, 1.4200F, 1.7200F, 2.2000F}, new float[]{0.0000F, -18.0000F, -12.0000F, -12.0000F, -14.0000F, -14.0000F, -16.0000F, -16.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 12.0000F, -12.0000F, 12.0000F, -12.0000F, 12.0000F, -12.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 4.0000F, -4.0000F, 4.0000F, -4.0000F, 4.0000F, -4.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5200F, 1.5600F, 2.2000F}, new float[]{0.0000F, 14.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.5500F, 1.5600F, 2.2000F}, new float[]{0.0000F, 18.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5200F, 1.6200F, 2.2000F}, new float[]{0.0000F, -22.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -10.0000F, 0.0000F}, new float[]{-6.0000F, -76.0000F, -78.0000F, -6.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5600F, 1.6500F, 2.2000F}, new float[]{0.0000F, -24.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5200F, 1.6200F, 2.2000F}, new float[]{0.0000F, -22.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 10.0000F, 0.0000F}, new float[]{6.0000F, 76.0000F, 78.0000F, 6.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.5600F, 1.6500F, 2.2000F}, new float[]{0.0000F, -24.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.5500F, 1.6500F, 2.2000F}, new float[]{0.0000F, 16.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.5500F, 1.6500F, 2.2000F}, new float[]{0.0000F, 22.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.5500F, 2.2000F}, new float[]{1.0000F, 1.5500F, 1.7000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 1.7000F, 1.0000F}, new float[]{1.0000F, 1.5500F, 1.7000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.4500F, 0.5800F, 0.7200F, 0.9200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, -36.0000F, 56.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 8.0000F, -14.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8600F, 1.0600F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, 36.0000F, -56.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, 14.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8600F, 1.0000F, 1.2000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -30.0000F, -36.0000F, 56.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 8.0000F, -14.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.4500F, 1.0000F, 1.1400F, 1.3400F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 36.0000F, -56.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, -8.0000F, 14.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1400F, 1.2800F, 1.4800F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, -36.0000F, 56.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 8.0000F, -14.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.4500F, 1.2800F, 1.4200F, 1.6200F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, 36.0000F, -56.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -8.0000F, 14.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.5000F, 1.4500F, 1.8000F, 2.2000F}, new float[]{0.0000F, 16.0000F, 34.0000F, 11.9000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.5350F, 1.5000F, 1.8300F, 2.2000F}, new float[]{0.0000F, 13.6000F, 28.9000F, 10.1150F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.5700F, 1.5500F, 1.8600F, 2.2000F}, new float[]{0.0000F, 12.0000F, 25.5000F, 8.9250F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.6050F, 1.6000F, 1.8900F, 2.2000F}, new float[]{0.0000F, 11.2000F, 23.8000F, 8.3300F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.6400F, 1.6500F, 1.9200F, 2.2000F}, new float[]{0.0000F, 10.4000F, 22.1000F, 7.7350F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4700F, 2.2000F}, new float[]{0.0000F, 10.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4700F, 2.2000F}, new float[]{0.0000F, -10.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 0.5300F, 1.4700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -16.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.attack_wing_barrage", c);
    }

    private static void initClip_6() {
        Clip c = new Clip("animation.astralion.elemental_shift_roar", 2.2000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.6500F, 0.8400F, 1.0500F, 1.6500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.5000F, -5.2000F, 9.5000F, 7.5000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 2.5000F, -2.0000F, -1.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.6500F, 0.8400F, 1.0500F, 1.6500F, 2.2000F}, new float[]{0.0000F, 24.0000F, 28.0000F, -28.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.6800F, 0.8600F, 1.0800F, 1.6800F, 2.2000F}, new float[]{0.0000F, 22.0000F, 26.0000F, -34.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.8600F, 1.0600F, 1.6500F, 2.2000F}, new float[]{0.0000F, 4.0000F, 48.0000F, 42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0500F, 1.6500F, 2.2000F}, new float[]{0.0000F, -36.0000F, -28.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -24.0000F, -18.0000F, 0.0000F}, new float[]{-6.0000F, 18.0000F, -84.0000F, -72.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.8600F, 1.0800F, 2.2000F}, new float[]{0.0000F, -54.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0500F, 1.6500F, 2.2000F}, new float[]{0.0000F, -36.0000F, -28.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 24.0000F, 18.0000F, 0.0000F}, new float[]{6.0000F, -18.0000F, 84.0000F, 72.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.8600F, 1.0800F, 2.2000F}, new float[]{0.0000F, -54.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, -24.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 24.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0600F, 2.2000F}, new float[]{0.0000F, -26.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0800F, 2.2000F}, new float[]{0.0000F, 42.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0600F, 2.2000F}, new float[]{0.0000F, -26.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 0.8400F, 1.0800F, 2.2000F}, new float[]{0.0000F, 42.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.8400F, 1.0500F, 1.6500F, 2.2000F}, new float[]{1.0000F, 0.4200F, 2.4500F, 2.1000F, 1.0000F}, new float[]{1.0000F, 0.4200F, 2.4500F, 2.1000F, 1.0000F}, new float[]{1.0000F, 0.4200F, 2.4500F, 2.1000F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1080.0000F}, new int[]{1, 1});
        c.add("halo_shards", CH_ROT, new float[]{0.0000F, 1.0500F, 2.2000F}, new float[]{0.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 45.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.8200F, 1.0600F, 1.6500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 40.0000F, -48.0000F, -16.8000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 20.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.8200F, 1.0600F, 1.6500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -40.0000F, 48.0000F, 16.8000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -20.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.8440F, 1.1000F, 1.6700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 34.0000F, -40.8000F, -14.2800F, 0.0000F}, new float[]{0.0000F, -6.8000F, 17.0000F, 5.1000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.8440F, 1.1000F, 1.6700F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -34.0000F, 40.8000F, 14.2800F, 0.0000F}, new float[]{0.0000F, 6.8000F, -17.0000F, -5.1000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.8680F, 1.1400F, 1.6900F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -33.6000F, -11.7600F, 0.0000F}, new float[]{0.0000F, -5.6000F, 14.0000F, 4.2000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.8680F, 1.1400F, 1.6900F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 33.6000F, 11.7600F, 0.0000F}, new float[]{0.0000F, 5.6000F, -14.0000F, -4.2000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.8200F, 1.1000F, 1.6800F, 2.2000F}, new float[]{0.0000F, -8.0000F, 44.0000F, 15.4000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.8550F, 1.1500F, 1.7100F, 2.2000F}, new float[]{0.0000F, -6.8000F, 37.4000F, 13.0900F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.8900F, 1.2000F, 1.7400F, 2.2000F}, new float[]{0.0000F, -6.0000F, 33.0000F, 11.5500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.9250F, 1.2500F, 1.7700F, 2.2000F}, new float[]{0.0000F, -5.6000F, 30.8000F, 10.7800F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.9600F, 1.3000F, 1.8000F, 2.2000F}, new float[]{0.0000F, -5.2000F, 28.6000F, 10.0100F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1300F, 2.2000F}, new float[]{0.0000F, 10.0000F, -32.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1300F, 2.2000F}, new float[]{0.0000F, -10.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1300F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 26.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 0.8500F, 1.1300F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -26.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.elemental_shift_roar", c);
    }

    private static void initClip_7() {
        Clip c = new Clip("animation.astralion.morph_to_leviathan", 1.8000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.3500F, 0.9500F, 1.4500F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.5000F, 17.5000F, 15.2000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.3500F, 1.3500F, 1.8000F}, new float[]{0.0000F, -14.0000F, 84.0000F, 76.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4000F, 1.4000F, 1.8000F}, new float[]{0.0000F, -16.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 14.0000F, -76.0000F, -68.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.9500F, 1.4500F, 1.8000F}, new float[]{0.0000F, 36.0000F, 22.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.5500F, 1.4200F, 1.8000F}, new float[]{0.0000F, -24.0000F, 32.0000F, 28.0000F}, new float[]{0.0000F, -18.0000F, -16.0000F, -14.0000F}, new float[]{-6.0000F, -56.0000F, -48.0000F, -44.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.5500F, 1.4200F, 1.8000F}, new float[]{0.0000F, -24.0000F, 32.0000F, 28.0000F}, new float[]{0.0000F, 18.0000F, 16.0000F, 14.0000F}, new float[]{6.0000F, 56.0000F, 48.0000F, 44.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_greatsword", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.2500F, 1.8000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.2500F, 1.8000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.5000F, 1.4200F, 1.8000F}, new float[]{0.0000F, -12.0000F, 19.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, 7.0000F, 6.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.5000F, 1.4200F, 1.8000F}, new float[]{0.0000F, -12.0000F, 19.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -4.0000F, -7.0000F, -6.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.3800F, 1.8000F}, new float[]{0.0000F, -92.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 180.0000F, 360.0000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.3800F, 1.8000F}, new float[]{0.0000F, -92.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -180.0000F, -360.0000F}, new int[]{0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.9500F, 1.8000F}, new float[]{1.0000F, 1.8000F, 1.2000F}, new float[]{1.0000F, 1.8000F, 1.2000F}, new float[]{1.0000F, 1.8000F, 1.2000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.5500F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 18.0000F}, new float[]{0.0000F, 26.0000F, -26.0000F, -16.0000F}, new float[]{0.0000F, -12.0000F, 22.0000F, 18.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.5500F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 18.0000F}, new float[]{0.0000F, -26.0000F, 26.0000F, 16.0000F}, new float[]{0.0000F, 12.0000F, -22.0000F, -18.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.6000F, 1.4400F, 1.8000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 14.0000F}, new float[]{0.0000F, 22.0000F, -28.0000F, -20.0000F}, new float[]{0.0000F, -10.0000F, 16.0000F, 14.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.6000F, 1.4400F, 1.8000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 14.0000F}, new float[]{0.0000F, -22.0000F, 28.0000F, 20.0000F}, new float[]{0.0000F, 10.0000F, -16.0000F, -14.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.6500F, 1.5000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 12.0000F, 10.0000F}, new float[]{0.0000F, 18.0000F, -38.0000F, -34.0000F}, new float[]{0.0000F, -8.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.6500F, 1.5000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 12.0000F, 10.0000F}, new float[]{0.0000F, -18.0000F, 38.0000F, 34.0000F}, new float[]{0.0000F, 8.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 1.3500F, 1.8000F}, new float[]{0.0000F, 22.0000F, 12.0000F}, new float[]{0.0000F, 14.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 1.4200F, 1.8000F}, new float[]{0.0000F, -8.0000F, -4.2220F}, new float[]{0.0000F, 14.0000F, 11.7880F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 1.4800F, 1.8000F}, new float[]{0.0000F, -8.0000F, -5.4290F}, new float[]{0.0000F, -8.0000F, -11.0700F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 1.5400F, 1.8000F}, new float[]{0.0000F, -2.0000F, -0.8770F}, new float[]{0.0000F, -22.0000F, -27.7790F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 1.6000F, 1.8000F}, new float[]{0.0000F, 8.0000F, 6.1640F}, new float[]{0.0000F, -18.0000F, -20.3980F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.astralion.morph_to_leviathan", c);
    }

    private static void initClip_8() {
        Clip c = new Clip("animation.astralion.leviathan_flight", 2.2000F, true);
        c.add("body", CH_POS, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 19.0000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.5500F, 1.1000F, 1.6500F, 2.2000F}, new float[]{76.0000F, 73.0000F, 79.0000F, 73.0000F, 76.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.5500F, 1.1000F, 1.6500F, 2.2000F}, new float[]{0.0000F, 2.0000F, -3.0000F, 2.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 0.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.5800F, 1.1200F, 1.6800F, 2.2000F}, new float[]{-68.0000F, -66.0000F, -72.0000F, -66.0000F, -68.0000F}, new float[]{0.0000F, 6.0000F, 0.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{28.0000F, 34.0000F, 28.0000F}, new float[]{-14.0000F, -18.0000F, -14.0000F}, new float[]{-44.0000F, -50.0000F, -44.0000F}, new int[]{0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{28.0000F, 34.0000F, 28.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{44.0000F, 50.0000F, 44.0000F}, new int[]{0, 0, 0});
        c.add("omni_greatsword", CH_SCALE, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 2.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{16.0000F, 24.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 8.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 1.1600F, 2.2000F}, new float[]{12.0000F, 22.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{16.0000F, 24.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -8.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 1.1600F, 2.2000F}, new float[]{12.0000F, 22.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 1.1000F, 2.2000F}, new float[]{1.2000F, 1.3800F, 1.2000F}, new float[]{1.2000F, 1.3800F, 1.2000F}, new float[]{1.2000F, 1.3800F, 1.2000F}, new int[]{0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{-84.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.2000F}, new float[]{-84.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.0500F, 2.2000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new float[]{-16.0000F, -46.0000F, -16.0000F}, new float[]{18.0000F, -20.0000F, 18.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.0500F, 2.2000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new float[]{16.0000F, 46.0000F, 16.0000F}, new float[]{-18.0000F, 20.0000F, -18.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.1400F, 2.2000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{-20.0000F, -48.0000F, -20.0000F}, new float[]{14.0000F, -16.0000F, 14.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.1400F, 2.2000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{20.0000F, 48.0000F, 20.0000F}, new float[]{-14.0000F, 16.0000F, -14.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.2200F, 2.2000F}, new float[]{10.0000F, 14.0000F, 10.0000F}, new float[]{-34.0000F, -44.0000F, -34.0000F}, new float[]{0.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.2200F, 2.2000F}, new float[]{10.0000F, 14.0000F, 10.0000F}, new float[]{34.0000F, 44.0000F, 34.0000F}, new float[]{0.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.2750F, 0.5500F, 0.8250F, 1.1000F, 1.3750F, 1.6500F, 1.9250F, 2.2000F}, new float[]{12.0000F, 14.8280F, 16.0000F, 14.8280F, 12.0000F, 9.1720F, 8.0000F, 9.1720F, 12.0000F}, new float[]{18.0000F, 12.7280F, 0.0000F, -12.7280F, -18.0000F, -12.7280F, -0.0000F, 12.7280F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.2750F, 0.5500F, 0.8250F, 1.1000F, 1.3750F, 1.6500F, 1.9250F, 2.2000F}, new float[]{-4.2220F, -1.0910F, 2.6790F, 4.8800F, 4.2220F, 1.0910F, -2.6790F, -4.8800F, -4.2220F}, new float[]{11.7880F, 21.4700F, 18.5750F, 4.7990F, -11.7880F, -21.4700F, -18.5750F, -4.7990F, 11.7880F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.2750F, 0.5500F, 0.8250F, 1.1000F, 1.3750F, 1.6500F, 1.9250F, 2.2000F}, new float[]{-5.4290F, -5.6450F, -2.5550F, 2.0320F, 5.4290F, 5.6450F, 2.5550F, -2.0320F, -5.4290F}, new float[]{-11.0700F, 8.8070F, 23.5260F, 24.4630F, 11.0700F, -8.8070F, -23.5260F, -24.4630F, -11.0700F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.2750F, 0.5500F, 0.8250F, 1.1000F, 1.3750F, 1.6500F, 1.9250F, 2.2000F}, new float[]{-0.8770F, -5.5310F, -6.9450F, -4.2900F, 0.8770F, 5.5310F, 6.9450F, 4.2900F, -0.8770F}, new float[]{-27.7790F, -17.1610F, 3.5090F, 22.1240F, 27.7790F, 17.1610F, -3.5090F, -22.1240F, -27.7790F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.2750F, 0.5500F, 0.8250F, 1.1000F, 1.3750F, 1.6500F, 1.9250F, 2.2000F}, new float[]{6.1640F, 0.7530F, -5.0990F, -7.9640F, -6.1640F, -0.7530F, 5.0990F, 7.9640F, 6.1640F}, new float[]{-20.3980F, -31.8580F, -24.6560F, -3.0110F, 20.3980F, 31.8580F, 24.6560F, 3.0110F, -20.3980F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        CLIPS.put("animation.astralion.leviathan_flight", c);
    }

    private static void initClip_9() {
        Clip c = new Clip("animation.astralion.leviathan_chromatic_beam", 3.2000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.6500F, 0.8000F, 1.6500F, 2.5500F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 17.5000F, 15.5000F, 16.5000F, 15.5000F, 14.0000F}, new float[]{0.0000F, 4.0000F, 8.0000F, 7.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("body", CH_ROT, new float[]{0.0000F, 0.7500F, 1.6500F, 2.5500F, 3.2000F}, new float[]{76.0000F, 72.0000F, 74.0000F, 72.0000F, 76.0000F}, new float[]{0.0000F, -16.0000F, 0.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 0.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.7500F, 2.5500F, 3.2000F}, new float[]{0.0000F, -8.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.6000F, 0.8000F, 2.5000F, 2.7000F, 3.2000F}, new float[]{-68.0000F, -76.0000F, -68.0000F, -68.0000F, -64.0000F, -68.0000F}, new float[]{0.0000F, -42.0000F, -62.0000F, 62.0000F, 48.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 0.6000F, 0.7600F, 1.6500F, 2.5500F, 3.2000F}, new float[]{18.0000F, 10.0000F, 60.0000F, 56.0000F, 58.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.7800F, 2.5500F, 3.2000F}, new float[]{28.0000F, 38.0000F, 36.0000F, 28.0000F}, new float[]{-14.0000F, -22.0000F, -20.0000F, -14.0000F}, new float[]{-44.0000F, -58.0000F, -56.0000F, -44.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.7800F, 2.5500F, 3.2000F}, new float[]{28.0000F, 38.0000F, 36.0000F, 28.0000F}, new float[]{14.0000F, 22.0000F, 20.0000F, 14.0000F}, new float[]{44.0000F, 58.0000F, 56.0000F, 44.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_greatsword", CH_SCALE, new float[]{0.0000F, 3.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 3.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 0.8000F, 2.5500F, 3.2000F}, new float[]{16.0000F, 26.0000F, 24.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 10.0000F, 10.0000F, 6.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 0.8000F, 2.5500F, 3.2000F}, new float[]{16.0000F, 26.0000F, 24.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -10.0000F, -10.0000F, -6.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.6500F, 0.8000F, 2.5500F, 3.2000F}, new float[]{1.2000F, 2.2000F, 2.6000F, 2.3000F, 1.2000F}, new float[]{1.2000F, 2.2000F, 2.6000F, 2.3000F, 1.2000F}, new float[]{1.2000F, 2.2000F, 2.6000F, 2.3000F, 1.2000F}, new int[]{0, 0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-84.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1080.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 3.2000F}, new float[]{-84.0000F, -84.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1440.0000F}, new int[]{1, 1});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 0.7800F, 1.6500F, 2.5500F, 3.2000F}, new float[]{18.0000F, 22.0000F, 20.0000F, 22.0000F, 18.0000F}, new float[]{-16.0000F, -48.0000F, -38.0000F, -48.0000F, -16.0000F}, new float[]{18.0000F, 28.0000F, 20.0000F, 28.0000F, 18.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 0.7800F, 1.6500F, 2.5500F, 3.2000F}, new float[]{18.0000F, 22.0000F, 20.0000F, 22.0000F, 18.0000F}, new float[]{16.0000F, 48.0000F, 38.0000F, 48.0000F, 16.0000F}, new float[]{-18.0000F, -28.0000F, -20.0000F, -28.0000F, -18.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 0.8400F, 2.6000F, 3.2000F}, new float[]{14.0000F, 18.0000F, 18.0000F, 14.0000F}, new float[]{-20.0000F, -46.0000F, -46.0000F, -20.0000F}, new float[]{14.0000F, 22.0000F, 22.0000F, 14.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 0.8400F, 2.6000F, 3.2000F}, new float[]{14.0000F, 18.0000F, 18.0000F, 14.0000F}, new float[]{20.0000F, 46.0000F, 46.0000F, 20.0000F}, new float[]{-14.0000F, -22.0000F, -22.0000F, -14.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 0.9000F, 2.6500F, 3.2000F}, new float[]{10.0000F, 14.0000F, 14.0000F, 10.0000F}, new float[]{-34.0000F, -48.0000F, -48.0000F, -34.0000F}, new float[]{0.0000F, 12.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 0.9000F, 2.6500F, 3.2000F}, new float[]{10.0000F, 14.0000F, 14.0000F, 10.0000F}, new float[]{34.0000F, 48.0000F, 48.0000F, 34.0000F}, new float[]{0.0000F, -12.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.8000F, 1.7000F, 2.5500F, 3.2000F}, new float[]{12.0000F, 16.0000F, 14.0000F, 16.0000F, 12.0000F}, new float[]{18.0000F, 28.0000F, -26.0000F, 24.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.8800F, 1.7800F, 2.6200F, 3.2000F}, new float[]{-4.2220F, 2.0000F, -2.0000F, 2.0000F, -4.2220F}, new float[]{11.7880F, 30.0000F, -28.0000F, 26.0000F, 11.7880F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.9600F, 1.8600F, 2.7000F, 3.2000F}, new float[]{-5.4290F, -2.0000F, 2.0000F, -2.0000F, -5.4290F}, new float[]{-11.0700F, 32.0000F, -30.0000F, 24.0000F, -11.0700F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 1.0400F, 1.9400F, 2.7800F, 3.2000F}, new float[]{-0.8770F, 4.0000F, -4.0000F, 4.0000F, -0.8770F}, new float[]{-27.7790F, 28.0000F, -32.0000F, 16.0000F, -27.7790F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 1.1200F, 2.0200F, 2.8500F, 3.2000F}, new float[]{6.1640F, 6.0000F, -6.0000F, 6.0000F, 6.1640F}, new float[]{-20.3980F, 26.0000F, -34.0000F, 8.0000F, -20.3980F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        CLIPS.put("animation.astralion.leviathan_chromatic_beam", c);
    }

    private static void initClip_10() {
        Clip c = new Clip("animation.astralion.morph_to_singularity", 1.8000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.3800F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -2.5000F, 16.0000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.4000F, 1.8000F}, new float[]{1.0000F, 0.8800F, 0.6500F, 0.7000F}, new float[]{1.0000F, 0.8800F, 0.6500F, 0.7000F}, new float[]{1.0000F, 0.8800F, 0.6500F, 0.7000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.4200F, 1.3800F, 1.8000F}, new float[]{1.0000F, 0.7500F, 4.1000F, 3.6000F}, new float[]{1.0000F, 0.7500F, 4.1000F, 3.6000F}, new float[]{1.0000F, 0.7500F, 4.1000F, 3.6000F}, new int[]{0, 0, 0, 0});
        c.add("head", CH_SCALE, new float[]{0.0000F, 0.5000F, 1.2500F, 1.8000F}, new float[]{1.0000F, 0.7500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.7500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.3000F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_right", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.3000F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_SCALE, new float[]{0.0000F, 0.4500F, 1.2000F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_SCALE, new float[]{0.0000F, 0.4500F, 1.2000F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("waist_skirt", CH_SCALE, new float[]{0.0000F, 0.5000F, 1.2500F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_1", CH_SCALE, new float[]{0.0000F, 0.5000F, 1.2500F, 1.8000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new float[]{1.0000F, 0.6500F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_POS, new float[]{0.0000F, 0.5500F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, -16.0000F, -14.5000F}, new float[]{0.0000F, -5.0000F, -16.0000F, -14.5000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 40.0000F, 35.0000F}, new float[]{0.0000F, 28.0000F, 25.0000F}, new float[]{0.0000F, 360.0000F, 360.0000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_POS, new float[]{0.0000F, 0.5500F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, -16.0000F, -14.5000F}, new float[]{0.0000F, -5.0000F, -16.0000F, -14.5000F}, new int[]{0, 0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{0.0000F, -50.0000F, -45.0000F}, new float[]{0.0000F, 62.0000F, 55.0000F}, new float[]{0.0000F, -360.0000F, -360.0000F}, new int[]{0, 0, 0});
        c.add("wings_root", CH_POS, new float[]{0.0000F, 0.6000F, 1.4200F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -1.5000F, -4.0000F, -3.5000F}, new float[]{0.0000F, -4.0000F, -10.8000F, -9.5000F}, new int[]{0, 0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 30.0000F, 24.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 10.0000F}, new float[]{0.0000F, -30.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.4200F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.4200F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.4600F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, -30.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.4600F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 10.0000F}, new float[]{0.0000F, 30.0000F, 24.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.astralion.morph_to_singularity", c);
    }

    private static void initClip_11() {
        Clip c = new Clip("animation.astralion.singularity_hover", 2.8000F, true);
        c.add("body", CH_POS, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 18.2000F, 14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.7000F, 0.7000F}, new float[]{0.7000F, 0.7000F}, new float[]{0.7000F, 0.7000F}, new int[]{0, 0});
        c.add("omni_core", CH_ROT, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.7000F, 1.4000F, 2.1000F, 2.8000F}, new float[]{3.6000F, 3.9500F, 3.4500F, 3.9500F, 3.6000F}, new float[]{3.6000F, 3.9500F, 3.4500F, 3.9500F, 3.6000F}, new float[]{3.6000F, 3.9500F, 3.4500F, 3.9500F, 3.6000F}, new int[]{0, 0, 0, 0, 0});
        c.add("head", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("arm_left", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("arm_right", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("leg_left", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("leg_right", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("waist_skirt", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("cape_tail_1", CH_SCALE, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{0, 0});
        c.add("halo_outer", CH_POS, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{-14.5000F, -14.5000F}, new float[]{-14.5000F, -14.5000F}, new int[]{0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 2.8000F}, new float[]{35.0000F, 35.0000F}, new float[]{25.0000F, 385.0000F}, new float[]{0.0000F, 360.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_POS, new float[]{0.0000F, 2.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{-14.5000F, -14.5000F}, new float[]{-14.5000F, -14.5000F}, new int[]{0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 2.8000F}, new float[]{-45.0000F, 315.0000F}, new float[]{55.0000F, 55.0000F}, new float[]{0.0000F, -360.0000F}, new int[]{1, 1});
        c.add("halo_shards", CH_ROT, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{0.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wings_root", CH_POS, new float[]{0.0000F, 1.4000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-3.5000F, -2.5000F, -3.5000F}, new float[]{-9.5000F, -9.5000F, -9.5000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.3600F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, -22.0000F, -10.0000F}, new float[]{24.0000F, 32.0000F, 24.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.3600F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 22.0000F, 10.0000F}, new float[]{-24.0000F, -32.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.4400F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, -24.0000F, -10.0000F}, new float[]{0.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.4400F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 24.0000F, 10.0000F}, new float[]{0.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.5200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, -22.0000F, -10.0000F}, new float[]{-24.0000F, -32.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.5200F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, 22.0000F, 10.0000F}, new float[]{24.0000F, 32.0000F, 24.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.astralion.singularity_hover", c);
    }

    private static void initClip_12() {
        Clip c = new Clip("animation.astralion.ultimate_sevenfold_cataclysm", 4.2000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4500F, 1.2500F, 2.6600F, 2.9000F, 3.0400F, 3.4500F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -3.5000F, 27.0000F, 30.0000F, -6.5000F, -3.8000F, -4.5000F, 0.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F, 2.0000F, -14.0000F, -12.5000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4500F, 1.4000F, 2.6600F, 2.9000F, 3.0400F, 3.4500F, 4.2000F}, new float[]{0.0000F, 18.0000F, -28.0000F, -34.0000F, 42.0000F, 34.0000F, 30.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 1.4500F, 2.6800F, 2.9400F, 3.4800F, 4.2000F}, new float[]{0.0000F, -24.0000F, -28.0000F, 28.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 1.4500F, 2.6800F, 2.9200F, 3.5000F, 4.2000F}, new float[]{0.0000F, 36.0000F, 52.0000F, 44.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.4000F, 2.6600F, 2.9000F, 3.0600F, 3.4500F, 4.2000F}, new float[]{0.0000F, -166.0000F, -174.0000F, -22.0000F, -32.0000F, -26.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 12.0000F, 10.0000F, 8.0000F, 0.0000F}, new float[]{-6.0000F, -18.0000F, -14.0000F, -8.0000F, -10.0000F, -8.0000F, -6.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("pauldron_right", CH_ROT, new float[]{0.0000F, 2.6600F, 2.9200F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 2.6800F, 2.9200F, 3.4800F, 4.2000F}, new float[]{0.0000F, -42.0000F, -4.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 1.4500F, 2.6800F, 2.9300F, 3.4800F, 4.2000F}, new float[]{0.0000F, 90.0000F, 96.0000F, 114.0000F, 88.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 10.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F, 14.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.4000F, 2.6600F, 2.9200F, 3.4500F, 4.2000F}, new float[]{0.0000F, -152.0000F, -160.0000F, 36.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 12.0000F, -16.0000F, -10.0000F, 0.0000F}, new float[]{6.0000F, 26.0000F, 30.0000F, 48.0000F, 34.0000F, 6.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("pauldron_left", CH_ROT, new float[]{0.0000F, 2.6600F, 2.9200F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -16.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 2.6800F, 2.9400F, 4.2000F}, new float[]{0.0000F, -36.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 1.4000F, 2.6600F, 2.9000F, 3.4500F, 4.2000F}, new float[]{0.0000F, 18.0000F, 22.0000F, -52.0000F, -42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 8.0000F, 10.0000F, 8.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 2.6600F, 2.9200F, 3.4500F, 4.2000F}, new float[]{0.0000F, 18.0000F, 54.0000F, 42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 1.4000F, 2.6600F, 2.9000F, 3.4500F, 4.2000F}, new float[]{0.0000F, 24.0000F, 28.0000F, -44.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, -10.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 2.6600F, 2.9200F, 3.4500F, 4.2000F}, new float[]{0.0000F, 22.0000F, 48.0000F, 38.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 2.6600F, 2.9000F, 3.4500F, 4.2000F}, new float[]{1.0000F, 2.6000F, 3.1000F, 2.0000F, 1.0000F}, new float[]{1.0000F, 2.6000F, 3.1000F, 2.0000F, 1.0000F}, new float[]{1.0000F, 2.6000F, 3.1000F, 2.0000F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 4.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 1440.0000F}, new int[]{1, 1});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 4.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1800.0000F}, new int[]{1, 1});
        c.add("halo_shards", CH_ROT, new float[]{0.0000F, 2.6600F, 2.9200F, 4.2000F}, new float[]{0.0000F, 28.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 90.0000F, 180.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 2.6400F, 2.9200F, 3.4800F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -46.0000F, 36.0000F, 12.6000F, 0.0000F}, new float[]{0.0000F, 5.6000F, -14.0000F, -4.2000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 2.6400F, 2.9200F, 3.4800F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 46.0000F, -36.0000F, -12.6000F, 0.0000F}, new float[]{0.0000F, -5.6000F, 14.0000F, 4.2000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 2.6640F, 2.9600F, 3.5000F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -39.1000F, 30.6000F, 10.7100F, 0.0000F}, new float[]{0.0000F, 4.7600F, -11.9000F, -3.5700F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 2.6640F, 2.9600F, 3.5000F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 39.1000F, -30.6000F, -10.7100F, 0.0000F}, new float[]{0.0000F, -4.7600F, 11.9000F, 3.5700F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 2.6880F, 3.0000F, 3.5200F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.2000F, 25.2000F, 8.8200F, 0.0000F}, new float[]{0.0000F, 3.9200F, -9.8000F, -2.9400F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 2.6880F, 3.0000F, 3.5200F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 32.2000F, -25.2000F, -8.8200F, 0.0000F}, new float[]{0.0000F, -3.9200F, 9.8000F, 2.9400F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 2.6400F, 2.9600F, 3.5200F, 4.2000F}, new float[]{0.0000F, -14.0000F, 52.0000F, 18.2000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 2.6750F, 3.0100F, 3.5500F, 4.2000F}, new float[]{0.0000F, -11.9000F, 44.2000F, 15.4700F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 2.7100F, 3.0600F, 3.5800F, 4.2000F}, new float[]{0.0000F, -10.5000F, 39.0000F, 13.6500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 2.7450F, 3.1100F, 3.6100F, 4.2000F}, new float[]{0.0000F, -9.8000F, 36.4000F, 12.7400F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 2.7800F, 3.1600F, 3.6400F, 4.2000F}, new float[]{0.0000F, -9.1000F, 33.8000F, 11.8300F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 2.6700F, 2.9900F, 4.2000F}, new float[]{0.0000F, 10.0000F, -36.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 2.6700F, 2.9900F, 4.2000F}, new float[]{0.0000F, -10.0000F, 42.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_left", CH_ROT, new float[]{0.0000F, 2.6700F, 2.9900F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, 28.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_right", CH_ROT, new float[]{0.0000F, 2.6700F, 2.9900F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, -28.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.ultimate_sevenfold_cataclysm", c);
    }

    private static void initClip_13() {
        Clip c = new Clip("animation.astralion.morph_to_monolith", 1.8000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.5500F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{14.0000F, 9.0000F, -2.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("torso", CH_SCALE, new float[]{0.0000F, 0.6000F, 1.4000F, 1.8000F}, new float[]{0.7000F, 0.8500F, 1.0800F, 1.0000F}, new float[]{0.7000F, 0.8500F, 1.0800F, 1.0000F}, new float[]{0.7000F, 0.8500F, 1.0800F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.3800F, 1.8000F}, new float[]{3.6000F, 2.6000F, 1.1500F, 1.0000F}, new float[]{3.6000F, 2.6000F, 1.1500F, 1.0000F}, new float[]{3.6000F, 2.6000F, 1.1500F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("head", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.3500F, 1.8000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_SCALE, new float[]{0.0000F, 0.6200F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_right", CH_SCALE, new float[]{0.0000F, 0.6200F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_SCALE, new float[]{0.0000F, 0.5000F, 1.3200F, 1.8000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("leg_right", CH_SCALE, new float[]{0.0000F, 0.5000F, 1.3200F, 1.8000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.6000F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("waist_skirt", CH_SCALE, new float[]{0.0000F, 0.5800F, 1.3800F, 1.8000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_1", CH_SCALE, new float[]{0.0000F, 0.6500F, 1.4200F, 1.8000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new float[]{0.0000F, 0.5500F, 1.1000F, 1.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_POS, new float[]{0.0000F, 0.6000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-14.5000F, -8.5000F, 1.2000F, 0.0000F}, new float[]{-14.5000F, -8.5000F, 1.2000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{35.0000F, -4.0000F, 0.0000F}, new float[]{25.0000F, -3.0000F, 0.0000F}, new float[]{0.0000F, 360.0000F, 360.0000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_POS, new float[]{0.0000F, 0.6000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-14.5000F, -8.5000F, 1.2000F, 0.0000F}, new float[]{-14.5000F, -8.5000F, 1.2000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{-45.0000F, 5.0000F, 0.0000F}, new float[]{55.0000F, -6.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F, -360.0000F}, new int[]{0, 0, 0});
        c.add("wings_root", CH_POS, new float[]{0.0000F, 0.6200F, 1.4200F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-3.5000F, -2.0000F, 0.4000F, 0.0000F}, new float[]{-9.5000F, -5.5000F, 0.9000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, 4.0000F, 0.0000F}, new float[]{24.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.4000F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, -4.0000F, 0.0000F}, new float[]{-24.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.4400F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.4400F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.4800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-10.0000F, 4.0000F, 0.0000F}, new float[]{-24.0000F, 4.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.4800F, 1.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{10.0000F, -4.0000F, 0.0000F}, new float[]{24.0000F, -4.0000F, 0.0000F}, new int[]{0, 0, 0});
        CLIPS.put("animation.astralion.morph_to_monolith", c);
    }

    private static void initClip_14() {
        Clip c = new Clip("animation.astralion.shield_break_collapse", 2.4000F, true);
        c.add("body", CH_POS, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-8.5000F, -10.5000F, -8.5000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{28.0000F, 34.0000F, 28.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-2.5000F, 2.5000F, -2.5000F}, new int[]{0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.8600F, 0.9800F, 1.2600F, 2.4000F}, new float[]{24.0000F, 28.0000F, 18.0000F, 30.0000F, 24.0000F}, new float[]{-5.0000F, -7.0000F, 10.0000F, 3.0000F, -5.0000F}, new float[]{3.0000F, 5.0000F, -7.0000F, -3.0000F, 3.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{12.0000F, 18.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 1.2600F, 2.4000F}, new float[]{16.0000F, 22.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, -8.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 1.3000F, 2.4000F}, new float[]{-14.0000F, -22.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 1.2800F, 2.4000F}, new float[]{34.0000F, 40.0000F, 34.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{12.0000F, 16.0000F, 12.0000F}, new int[]{0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 1.2600F, 2.4000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, 8.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 1.3000F, 2.4000F}, new float[]{-12.0000F, -20.0000F, -12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{0.3500F, 0.2500F, 0.3500F}, new float[]{0.3500F, 0.2500F, 0.3500F}, new float[]{0.3500F, 0.2500F, 0.3500F}, new int[]{0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{-42.0000F, -48.0000F, -42.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, 7.0000F, 6.0000F}, new int[]{0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{72.0000F, 78.0000F, 72.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{-36.0000F, -42.0000F, -36.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, -7.0000F, -6.0000F}, new int[]{0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{66.0000F, 72.0000F, 66.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{0.6200F, 0.7800F, 0.6200F}, new float[]{0.6200F, 0.7800F, 0.6200F}, new float[]{0.6200F, 0.7800F, 0.6200F}, new int[]{0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{12.0000F, 16.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.2000F, 2.4000F}, new float[]{-10.0000F, -14.0000F, -10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.2600F, 2.4000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{-18.0000F, -22.0000F, -18.0000F}, new float[]{-30.0000F, -36.0000F, -30.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.3200F, 2.4000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{-16.0000F, -20.0000F, -16.0000F}, new float[]{-26.0000F, -32.0000F, -26.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.3800F, 2.4000F}, new float[]{16.0000F, 22.0000F, 16.0000F}, new float[]{-14.0000F, -18.0000F, -14.0000F}, new float[]{-22.0000F, -28.0000F, -22.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.2600F, 2.4000F}, new float[]{20.0000F, 26.0000F, 20.0000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new float[]{30.0000F, 36.0000F, 30.0000F}, new int[]{0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.3200F, 2.4000F}, new float[]{18.0000F, 24.0000F, 18.0000F}, new float[]{16.0000F, 20.0000F, 16.0000F}, new float[]{26.0000F, 32.0000F, 26.0000F}, new int[]{0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.3800F, 2.4000F}, new float[]{16.0000F, 22.0000F, 16.0000F}, new float[]{14.0000F, 18.0000F, 14.0000F}, new float[]{22.0000F, 28.0000F, 22.0000F}, new int[]{0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 1.2400F, 2.4000F}, new float[]{-24.0000F, -28.0000F, -24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 1.2400F, 2.4000F}, new float[]{16.0000F, 20.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 0.3000F, 0.6000F, 0.9000F, 1.2000F, 1.5000F, 1.8000F, 2.1000F, 2.4000F}, new float[]{18.0000F, 19.4140F, 20.0000F, 19.4140F, 18.0000F, 16.5860F, 16.0000F, 16.5860F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, -0.0000F, -0.0000F, -0.0000F, -0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 0.3000F, 0.6000F, 0.9000F, 1.2000F, 1.5000F, 1.8000F, 2.1000F, 2.4000F}, new float[]{10.0740F, 11.7650F, 13.5940F, 14.4890F, 13.9260F, 12.2350F, 10.4060F, 9.5110F, 10.0740F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, -0.0000F, -0.0000F, -0.0000F, -0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 0.3000F, 0.6000F, 0.9000F, 1.2000F, 1.5000F, 1.8000F, 2.1000F, 2.4000F}, new float[]{5.0530F, 5.5190F, 7.4380F, 9.6860F, 10.9470F, 10.4810F, 8.5620F, 6.3140F, 5.0530F}, new float[]{-0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, -0.0000F, -0.0000F, -0.0000F, -0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 0.3000F, 0.6000F, 0.9000F, 1.2000F, 1.5000F, 1.8000F, 2.1000F, 2.4000F}, new float[]{4.3140F, 2.6390F, 2.9330F, 5.0240F, 7.6860F, 9.3610F, 9.0670F, 6.9760F, 4.3140F}, new float[]{-0.0000F, -0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, -0.0000F, -0.0000F, -0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 0.3000F, 0.6000F, 0.9000F, 1.2000F, 1.5000F, 1.8000F, 2.1000F, 2.4000F}, new float[]{5.4720F, 2.4110F, 0.2810F, 0.3290F, 2.5280F, 5.5890F, 7.7190F, 7.6710F, 5.4720F}, new float[]{-0.0000F, -0.0000F, -0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, -0.0000F, -0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        CLIPS.put("animation.astralion.shield_break_collapse", c);
    }

    private static void initClip_15() {
        Clip c = new Clip("animation.astralion.death", 3.2000F, false);
        c.add("body", CH_POS, new float[]{0.0000F, 0.4200F, 1.1000F, 2.2500F, 2.4800F, 3.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.5000F, 11.5000F, -11.5000F, -9.8000F, -12.5000F}, new float[]{0.0000F, 3.0000F, 2.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("torso", CH_ROT, new float[]{0.0000F, 0.4200F, 1.1000F, 2.3000F, 2.5500F, 3.2000F}, new float[]{0.0000F, -24.0000F, -30.0000F, 52.0000F, 46.0000F, 58.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 5.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("head", CH_ROT, new float[]{0.0000F, 0.4600F, 1.1500F, 2.3600F, 3.2000F}, new float[]{0.0000F, -28.0000F, -24.0000F, 44.0000F, 48.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 6.0000F, 8.0000F}, new float[]{0.0000F, -6.0000F, 8.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("jaw_lower", CH_ROT, new float[]{0.0000F, 1.1000F, 2.3500F, 3.2000F}, new float[]{0.0000F, 48.0000F, 16.0000F, 10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_right", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1500F, 2.3200F, 3.2000F}, new float[]{0.0000F, -116.0000F, -128.0000F, 34.0000F, 42.0000F}, new float[]{0.0000F, -14.0000F, -10.0000F, 10.0000F, 12.0000F}, new float[]{-6.0000F, -36.0000F, -30.0000F, -16.0000F, -12.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_right", CH_ROT, new float[]{0.0000F, 0.5000F, 2.3600F, 3.2000F}, new float[]{0.0000F, -32.0000F, -8.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_greatsword", CH_ROT, new float[]{0.0000F, 0.5000F, 2.3600F, 3.2000F}, new float[]{0.0000F, 24.0000F, 106.0000F, 114.0000F}, new float[]{0.0000F, 0.0000F, 16.0000F, 20.0000F}, new float[]{0.0000F, 12.0000F, 36.0000F, 42.0000F}, new int[]{0, 0, 0, 0});
        c.add("arm_left", CH_ROT, new float[]{0.0000F, 0.4500F, 1.1500F, 2.3200F, 3.2000F}, new float[]{0.0000F, -110.0000F, -122.0000F, 38.0000F, 44.0000F}, new float[]{0.0000F, 14.0000F, 10.0000F, -12.0000F, -14.0000F}, new float[]{6.0000F, 36.0000F, 30.0000F, 18.0000F, 12.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("forearm_left", CH_ROT, new float[]{0.0000F, 0.5000F, 2.3600F, 3.2000F}, new float[]{0.0000F, -30.0000F, -6.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("omni_aegis", CH_SCALE, new float[]{0.0000F, 1.2000F, 1.7500F, 3.2000F}, new float[]{1.0000F, 1.3000F, 0.0100F, 0.0100F}, new float[]{1.0000F, 1.3000F, 0.0100F, 0.0100F}, new float[]{1.0000F, 1.3000F, 0.0100F, 0.0100F}, new int[]{0, 0, 0, 0});
        c.add("omni_core", CH_SCALE, new float[]{0.0000F, 1.2000F, 1.7500F, 3.2000F}, new float[]{1.0000F, 2.5000F, 0.0100F, 0.0100F}, new float[]{1.0000F, 2.5000F, 0.0100F, 0.0100F}, new float[]{1.0000F, 2.5000F, 0.0100F, 0.0100F}, new int[]{0, 0, 0, 0});
        c.add("leg_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.3000F, 3.2000F}, new float[]{0.0000F, 16.0000F, -68.0000F, -74.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 6.0000F, 8.0000F, 8.0000F}, new int[]{0, 0, 0, 0});
        c.add("shin_left", CH_ROT, new float[]{0.0000F, 2.3200F, 3.2000F}, new float[]{0.0000F, 108.0000F, 114.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("leg_right", CH_ROT, new float[]{0.0000F, 1.1000F, 2.3000F, 3.2000F}, new float[]{0.0000F, 16.0000F, -68.0000F, -74.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -6.0000F, -8.0000F, -8.0000F}, new int[]{0, 0, 0, 0});
        c.add("shin_right", CH_ROT, new float[]{0.0000F, 2.3200F, 3.2000F}, new float[]{0.0000F, 108.0000F, 114.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("halo_outer", CH_ROT, new float[]{0.0000F, 1.7000F, 3.2000F}, new float[]{0.0000F, 0.0000F, 24.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F, 742.0000F}, new int[]{0, 0, 0});
        c.add("halo_inner", CH_ROT, new float[]{0.0000F, 1.7000F, 3.2000F}, new float[]{0.0000F, 0.0000F, -20.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -720.0000F, -738.0000F}, new int[]{0, 0, 0});
        c.add("wing_upper_left", CH_ROT, new float[]{0.0000F, 1.0500F, 2.3800F, 3.2000F}, new float[]{0.0000F, 0.0000F, 26.0000F, 30.0000F}, new float[]{0.0000F, -46.0000F, -14.0000F, -12.0000F}, new float[]{0.0000F, 20.0000F, -46.0000F, -52.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_mid_left", CH_ROT, new float[]{0.0000F, 1.1000F, 2.4400F, 3.2000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 26.0000F}, new float[]{0.0000F, -40.0000F, -12.0000F, -10.0000F}, new float[]{0.0000F, 16.0000F, -40.0000F, -46.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_left", CH_ROT, new float[]{0.0000F, 1.1500F, 2.5000F, 3.2000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 22.0000F}, new float[]{0.0000F, -34.0000F, -10.0000F, -8.0000F}, new float[]{0.0000F, 12.0000F, -34.0000F, -40.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_upper_right", CH_ROT, new float[]{0.0000F, 1.0500F, 2.3800F, 3.2000F}, new float[]{0.0000F, 0.0000F, 26.0000F, 30.0000F}, new float[]{0.0000F, 46.0000F, 14.0000F, 12.0000F}, new float[]{0.0000F, -20.0000F, 46.0000F, 52.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_mid_right", CH_ROT, new float[]{0.0000F, 1.1000F, 2.4400F, 3.2000F}, new float[]{0.0000F, 0.0000F, 22.0000F, 26.0000F}, new float[]{0.0000F, 40.0000F, 12.0000F, 10.0000F}, new float[]{0.0000F, -16.0000F, 40.0000F, 46.0000F}, new int[]{0, 0, 0, 0});
        c.add("wing_lower_right", CH_ROT, new float[]{0.0000F, 1.1500F, 2.5000F, 3.2000F}, new float[]{0.0000F, 0.0000F, 18.0000F, 22.0000F}, new float[]{0.0000F, 34.0000F, 10.0000F, 8.0000F}, new float[]{0.0000F, -12.0000F, 34.0000F, 40.0000F}, new int[]{0, 0, 0, 0});
        c.add("tasset_front", CH_ROT, new float[]{0.0000F, 2.3600F, 3.2000F}, new float[]{0.0000F, -34.0000F, -38.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("tasset_back", CH_ROT, new float[]{0.0000F, 2.3600F, 3.2000F}, new float[]{0.0000F, 28.0000F, 32.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("cape_tail_1", CH_ROT, new float[]{0.0000F, 1.1500F, 2.4000F, 3.2000F}, new float[]{0.0000F, 22.0000F, 34.0000F, 38.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_2", CH_ROT, new float[]{0.0000F, 1.2200F, 2.4800F, 3.2000F}, new float[]{0.0000F, 16.0000F, 22.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_3", CH_ROT, new float[]{0.0000F, 1.2800F, 2.5600F, 3.2000F}, new float[]{0.0000F, 10.0000F, 14.0000F, 16.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_4", CH_ROT, new float[]{0.0000F, 1.3400F, 2.6400F, 3.2000F}, new float[]{0.0000F, 6.0000F, 10.0000F, 12.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("cape_tail_tip", CH_ROT, new float[]{0.0000F, 1.4000F, 2.7200F, 3.2000F}, new float[]{0.0000F, 4.0000F, 8.0000F, 10.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("animation.astralion.death", c);
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
        return (2.0F * u3 - 3.0F * u2 + 1.0F) * p1 + (u3 - 2.0F * u2 + u) * m1 + (-2.0F * u3 + 3.0F * u2) * p2 + (u3 - u2) * m2;
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
        if (tr.interps[i] == INTERP_LINEAR) {
            out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * u;
            out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * u;
            out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * u;
        } else {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
        }
    }

    public static void apply(AstralionModel<?> model, String clipName, float timeSeconds) {
        model.resetPose();
        Clip clip = CLIPS.get(clipName);
        if (clip == null) return;
        float t = normalizeTime(clip, timeSeconds);
        float[] v = new float[3];
        for (Track tr : clip.tracks) {
            ModelPart part = model.getBone(tr.bone);
            if (part == null || tr.times.length == 0) continue;
            sampleTrack(tr, t, clip.loop, v);
            if (tr.channel == CH_ROT) {
                part.xRot += v[0] * DEG2RAD;
                part.yRot += v[1] * DEG2RAD;
                part.zRot += v[2] * DEG2RAD;
            } else if (tr.channel == CH_POS) {
                part.x += v[0];
                part.y -= v[1];
                part.z += v[2];
            } else if (tr.channel == CH_SCALE) {
                part.xScale = v[0];
                part.yScale = v[1];
                part.zScale = v[2];
            }
        }
    }
}
