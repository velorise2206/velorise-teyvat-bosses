// Auto-compiled 14 Catmull-Rom spline animations synced 1:1 with cryo_regisvine_boss.bbmodel
package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CryoRegisvineAnimations {
    private CryoRegisvineAnimations() {}

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
        Clip c = new Clip("dormant_buried", 3.0000F, true);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{26.0000F, 29.0000F, 26.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 1.6500F, 3.0000F}, new float[]{22.0000F, 25.0000F, 22.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 1.8000F, 3.0000F}, new float[]{18.0000F, 20.0000F, 18.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{8.0000F, 11.0000F, 8.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-52.0000F, -55.0000F, -52.0000F}, new float[]{24.0000F, 26.0000F, 24.0000F}, new int[]{0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{52.0000F, 55.0000F, 52.0000F}, new float[]{-24.0000F, -26.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-52.0000F, -55.0000F, -52.0000F}, new float[]{-24.0000F, -26.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{52.0000F, 55.0000F, 52.0000F}, new float[]{24.0000F, 26.0000F, 24.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-55.0000F, -58.0000F, -55.0000F}, new float[]{-28.0000F, -30.0000F, -28.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{55.0000F, 58.0000F, 55.0000F}, new float[]{28.0000F, 30.0000F, 28.0000F}, new int[]{0, 0, 0});
        c.add("root_core", CH_SCALE, new float[]{0.0000F, 1.5000F, 3.0000F}, new float[]{0.9600F, 1.0800F, 0.9600F}, new float[]{0.9600F, 1.0800F, 0.9600F}, new float[]{0.9600F, 1.0800F, 0.9600F}, new int[]{0, 0, 0});
        CLIPS.put("dormant_buried", c);
    }

    private static void initClip_1() {
        Clip c = new Clip("awaken_emerge", 2.2000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.1500F, 0.3000F, 0.4500F, 0.7500F, 0.9500F, 1.1500F, 1.3500F, 1.8500F, 2.2000F}, new float[]{28.0000F, 31.0000F, 32.0000F, 34.0000F, -24.0000F, -26.0000F, -23.0000F, -20.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, -3.0000F, 3.0000F, 0.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8200F, 1.3500F, 1.9000F, 2.2000F}, new float[]{22.0000F, 26.0000F, -20.0000F, -16.0000F, 5.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8800F, 1.4000F, 1.9500F, 2.2000F}, new float[]{18.0000F, 22.0000F, -18.0000F, -14.0000F, 4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8200F, 0.9800F, 1.1400F, 1.3000F, 1.8500F, 2.2000F}, new float[]{10.0000F, 16.0000F, -26.0000F, -28.0000F, -24.0000F, -26.0000F, 5.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 4.0000F, -4.0000F, 3.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, -2.0000F, 2.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 1, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8200F, 1.3500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-52.0000F, -58.0000F, 26.0000F, 22.0000F, 0.0000F}, new float[]{24.0000F, 28.0000F, -18.0000F, -14.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8200F, 1.3500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{52.0000F, 58.0000F, -26.0000F, -22.0000F, 0.0000F}, new float[]{-24.0000F, -28.0000F, 18.0000F, 14.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8200F, 1.3500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-52.0000F, -58.0000F, 26.0000F, 22.0000F, 0.0000F}, new float[]{-24.0000F, -28.0000F, 18.0000F, 14.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8200F, 1.3500F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{52.0000F, 58.0000F, -26.0000F, -22.0000F, 0.0000F}, new float[]{24.0000F, 28.0000F, -18.0000F, -14.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.4000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-55.0000F, -60.0000F, 32.0000F, 24.0000F, 0.0000F}, new float[]{-28.0000F, -32.0000F, 24.0000F, 16.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.4000F, 2.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{55.0000F, 60.0000F, -32.0000F, -24.0000F, 0.0000F}, new float[]{28.0000F, 32.0000F, -24.0000F, -16.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 0.5000F, 0.8500F, 1.4000F, 2.2000F}, new float[]{0.8500F, 0.9000F, 1.3800F, 1.2500F, 1.0000F}, new float[]{0.8500F, 0.9000F, 1.3800F, 1.2500F, 1.0000F}, new float[]{0.8500F, 0.9000F, 1.3800F, 1.2500F, 1.0000F}, new int[]{0, 1, 1, 0, 0});
        CLIPS.put("awaken_emerge", c);
    }

    private static void initClip_2() {
        Clip c = new Clip("idle_combat", 4.0000F, true);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F, 3.0000F, 4.0000F}, new float[]{2.0000F, -2.0000F, -5.0000F, -1.0000F, 2.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F, 5.0000F, 0.0000F}, new float[]{0.0000F, 3.0000F, 0.0000F, -3.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 1.1500F, 2.1500F, 3.1500F, 4.0000F}, new float[]{3.0000F, -1.0000F, -4.0000F, 0.0000F, 3.0000F}, new float[]{2.0000F, 5.0000F, -2.0000F, -5.0000F, 2.0000F}, new float[]{-1.0000F, -3.0000F, 1.0000F, 3.0000F, -1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 1.3000F, 2.3000F, 3.3000F, 4.0000F}, new float[]{1.0000F, -2.0000F, -3.0000F, 1.0000F, 1.0000F}, new float[]{-2.0000F, -4.0000F, 2.0000F, 4.0000F, -2.0000F}, new float[]{1.0000F, 2.0000F, -1.0000F, -2.0000F, 1.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{-4.0000F, 5.0000F, -4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 0.0000F}, new float[]{0.0000F, -5.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{4.0000F, -14.0000F, 4.0000F}, new float[]{3.0000F, -12.0000F, 3.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_tip", CH_ROT, new float[]{0.0000F, 2.4000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-6.0000F, 14.0000F, -6.0000F}, new float[]{-4.0000F, 10.0000F, -4.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 2.2000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-4.0000F, 14.0000F, -4.0000F}, new float[]{-3.0000F, 12.0000F, -3.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_tip", CH_ROT, new float[]{0.0000F, 2.4000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{6.0000F, -14.0000F, 6.0000F}, new float[]{4.0000F, -10.0000F, 4.0000F}, new int[]{0, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new float[]{0.9600F, 1.1200F, 0.9600F}, new int[]{0, 0, 0});
        CLIPS.put("idle_combat", c);
    }

    private static void initClip_3() {
        Clip c = new Clip("ice_beams_cross", 3.0000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7500F, 0.8500F, 1.3000F, 1.6500F, 1.8500F, 1.9500F, 2.4000F, 2.7500F, 3.0000F}, new float[]{0.0000F, -14.0000F, -18.0000F, -24.0000F, 8.0000F, 10.0000F, -18.0000F, -22.0000F, 10.0000F, 8.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.8500F, 1.3500F, 1.8500F, 2.4500F, 3.0000F}, new float[]{0.0000F, -10.0000F, -16.0000F, 6.0000F, -12.0000F, 7.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.3000F, 1.8500F, 2.4000F, 3.0000F}, new float[]{0.0000F, -12.0000F, -18.0000F, 6.0000F, -14.0000F, 6.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.6500F, 1.9500F, 2.7000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 24.0000F, 16.0000F, 26.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, -14.0000F, -10.0000F, -16.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.6500F, 1.9500F, 2.7000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -24.0000F, -16.0000F, -26.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, 14.0000F, 10.0000F, 16.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.9500F, 2.7000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 20.0000F, 22.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 12.0000F, 14.0000F, 8.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.5000F, 0.8500F, 1.9500F, 2.7000F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -20.0000F, -22.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -12.0000F, -14.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8000F, 1.2500F, 1.6500F, 1.9000F, 2.3500F, 2.7500F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 36.0000F, 40.0000F, -10.0000F, -52.0000F, -46.0000F, 4.0000F, 44.0000F, 0.0000F}, new float[]{0.0000F, 38.0000F, 40.0000F, 12.0000F, -16.0000F, -10.0000F, 16.0000F, 34.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 0.5200F, 0.8800F, 1.3500F, 1.7200F, 2.0000F, 2.4500F, 2.8200F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 18.0000F, -18.0000F, -24.0000F, -16.0000F, 18.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 16.0000F, -8.0000F, -14.0000F, -6.0000F, 10.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_r_tip", CH_ROT, new float[]{0.0000F, 0.8500F, 1.6500F, 1.9500F, 2.7500F, 3.0000F}, new float[]{0.0000F, 16.0000F, 22.0000F, 18.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 8.0000F, -12.0000F, -10.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.4500F, 0.8000F, 1.2500F, 1.6500F, 1.9000F, 2.3500F, 2.7500F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, -40.0000F, 10.0000F, 52.0000F, 46.0000F, -4.0000F, -44.0000F, 0.0000F}, new float[]{0.0000F, -38.0000F, -40.0000F, -12.0000F, 16.0000F, 10.0000F, -16.0000F, -34.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 0.5200F, 0.8800F, 1.3500F, 1.7200F, 2.0000F, 2.4500F, 2.8200F, 3.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -18.0000F, 18.0000F, 24.0000F, 16.0000F, -18.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -16.0000F, 8.0000F, 14.0000F, 6.0000F, -10.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_l_tip", CH_ROT, new float[]{0.0000F, 0.8500F, 1.6500F, 1.9500F, 2.7500F, 3.0000F}, new float[]{0.0000F, 16.0000F, 22.0000F, 18.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, -8.0000F, 12.0000F, 10.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("lash_r_tip", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.6500F, 1.9000F, 2.7500F, 3.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("lash_l_tip", CH_SCALE, new float[]{0.0000F, 0.7500F, 1.6500F, 1.9000F, 2.7500F, 3.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.2800F, 1.4800F, 1.2200F, 1.0000F}, new int[]{0, 0, 0, 0, 0, 0});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        CLIPS.put("ice_beams_cross", c);
    }

    private static void initClip_4() {
        Clip c = new Clip("homing_icicles", 2.6000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7000F, 0.9000F, 1.1000F, 1.3000F, 1.5000F, 1.7000F, 1.9000F, 2.1000F, 2.6000F}, new float[]{0.0000F, -14.0000F, -18.0000F, -18.0000F, -20.0000F, -20.0000F, -21.0000F, -21.0000F, -22.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 5.0000F, -5.0000F, 5.0000F, -5.0000F, 5.0000F, -5.0000F, 5.0000F, -5.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.5000F, 1.4000F, 2.1500F, 2.6000F}, new float[]{0.0000F, -10.0000F, -14.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.5000F, 1.4000F, 2.1500F, 2.6000F}, new float[]{0.0000F, -10.0000F, -14.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.5500F, 0.7200F, 0.9200F, 1.1200F, 1.3200F, 1.5200F, 1.7200F, 1.9200F, 2.2500F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -30.0000F, 22.0000F, -28.0000F, 24.0000F, -28.0000F, 24.0000F, -28.0000F, 26.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -8.0000F, 22.0000F, -8.0000F, 22.0000F, -8.0000F, 22.0000F, -10.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 0.6200F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F, 1.8000F, 2.0000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 20.0000F, -18.0000F, 20.0000F, -18.0000F, 20.0000F, -18.0000F, 22.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -10.0000F, 14.0000F, -10.0000F, 14.0000F, -10.0000F, 14.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.7200F, 0.9200F, 1.1200F, 1.3200F, 1.5200F, 1.7200F, 1.9200F, 2.1200F, 2.4000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, -22.0000F, 28.0000F, -24.0000F, 28.0000F, -24.0000F, 28.0000F, -26.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 8.0000F, -22.0000F, 8.0000F, -22.0000F, 8.0000F, -22.0000F, 10.0000F, -8.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 0.8000F, 1.0000F, 1.2000F, 1.4000F, 1.6000F, 1.8000F, 2.0000F, 2.2000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, -20.0000F, 18.0000F, -20.0000F, 18.0000F, -20.0000F, 18.0000F, -22.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 10.0000F, -14.0000F, 10.0000F, -14.0000F, 10.0000F, -14.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        c.add("lash_r_tip", CH_SCALE, new float[]{0.0000F, 0.7000F, 0.8500F, 1.1000F, 1.2500F, 1.5000F, 1.6500F, 1.9000F, 2.1500F, 2.6000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("lash_l_tip", CH_SCALE, new float[]{0.0000F, 0.9000F, 1.0500F, 1.3000F, 1.4500F, 1.7000F, 1.8500F, 2.1000F, 2.3500F, 2.6000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new float[]{1.0000F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.4500F, 0.9500F, 1.0000F}, new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.5500F, 2.1000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.5500F, 2.1000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.5500F, 2.1000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 10.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.5500F, 2.1000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -10.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0});
        CLIPS.put("homing_icicles", c);
    }

    private static void initClip_5() {
        Clip c = new Clip("faceplant_single", 2.9000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.6000F, 0.7200F, 0.8500F, 0.9600F, 1.1200F, 1.6800F, 2.1400F, 2.4000F}, new float[]{0.0000F, -35.0000F, -37.0000F, -36.0000F, 53.0000F, 46.0000F, 50.0000F, 49.0000F, -5.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7400F, 0.8700F, 0.9900F, 1.1500F, 1.7000F, 2.1800F, 2.4000F}, new float[]{0.0000F, -27.0000F, -29.0000F, 40.0000F, 34.0000F, 37.0000F, 36.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.5000F, 0.7600F, 0.8900F, 1.0200F, 1.1800F, 1.7200F, 2.2000F, 2.4000F}, new float[]{0.0000F, -19.0000F, -21.0000F, 26.0000F, 20.0000F, 22.0000F, 21.0000F, -3.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.4500F, 0.5600F, 0.6800F, 0.8700F, 1.0200F, 1.6800F, 2.4000F}, new float[]{0.0000F, -18.0000F, -20.0000F, -19.0000F, 17.0000F, 8.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 3.0000F, -3.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -2.0000F, 2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8700F, 1.1200F, 1.7000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, -28.0000F, 28.0000F, 16.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, -18.0000F, -10.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8700F, 1.1200F, 1.7000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, 28.0000F, -28.0000F, -16.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 18.0000F, 10.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8700F, 1.1200F, 1.7000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, -28.0000F, 28.0000F, 16.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 18.0000F, 10.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8700F, 1.1200F, 1.7000F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, 28.0000F, -28.0000F, -16.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, -18.0000F, -10.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8800F, 1.6800F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -30.0000F, -34.0000F, 36.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 34.0000F, 38.0000F, -24.0000F, -16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8800F, 1.6800F, 2.4000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 30.0000F, 34.0000F, -36.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, -34.0000F, -38.0000F, 24.0000F, 16.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("frost_ring", CH_POS, new float[]{0.8400F, 1.7500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-28.0000F, -28.0000F}, new int[]{1, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.8400F, 0.9400F, 1.6500F, 1.9500F}, new float[]{0.0000F, 0.0000F, 1.4000F, 1.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.1500F, 0.2000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.4000F, 1.5000F, 0.0000F}, new int[]{1, 1, 1, 0, 1});
        CLIPS.put("faceplant_single", c);
    }

    private static void initClip_6() {
        Clip c = new Clip("faceplant_triple", 4.2000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7000F, 0.8500F, 0.9600F, 1.4500F, 1.7000F, 1.8500F, 1.9600F, 2.4200F, 2.5600F, 2.7000F, 2.8500F, 2.9600F, 3.1000F, 3.5500F, 3.9800F, 4.2000F}, new float[]{0.0000F, -28.0000F, -29.0000F, 48.0000F, 42.0000F, -30.0000F, -31.0000F, 50.0000F, 44.0000F, -36.0000F, -37.0000F, -36.0000F, 52.0000F, 45.0000F, 48.0000F, 46.0000F, -4.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, -26.0000F, -26.0000F, -26.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 26.0000F, 28.0000F, 24.0000F, 26.0000F, 26.0000F, 26.0000F, 26.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 0.8700F, 0.9800F, 1.4800F, 1.7200F, 1.8700F, 1.9800F, 2.4500F, 2.7200F, 2.8700F, 2.9800F, 3.5800F, 4.0200F, 4.2000F}, new float[]{0.0000F, -22.0000F, -23.0000F, 36.0000F, 30.0000F, -24.0000F, -25.0000F, 38.0000F, 32.0000F, -28.0000F, -29.0000F, 40.0000F, 34.0000F, 34.0000F, -3.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.5000F, 0.7400F, 0.8900F, 1.5000F, 1.7400F, 1.8900F, 2.4800F, 2.7400F, 2.8900F, 3.6000F, 4.2000F}, new float[]{0.0000F, -14.0000F, -15.0000F, 22.0000F, -16.0000F, -17.0000F, 24.0000F, -20.0000F, -21.0000F, 26.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.7000F, 0.8700F, 1.0500F, 1.7000F, 1.8700F, 2.0500F, 2.7000F, 2.8700F, 3.5500F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 22.0000F, 6.0000F, -24.0000F, 24.0000F, 6.0000F, -28.0000F, 28.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, -14.0000F, -4.0000F, 14.0000F, -16.0000F, -4.0000F, 18.0000F, -18.0000F, -6.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.7000F, 0.8700F, 1.0500F, 1.7000F, 1.8700F, 2.0500F, 2.7000F, 2.8700F, 3.5500F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -22.0000F, -6.0000F, 24.0000F, -24.0000F, -6.0000F, 28.0000F, -28.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, 14.0000F, 4.0000F, -14.0000F, 16.0000F, 4.0000F, -18.0000F, 18.0000F, 6.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.7000F, 0.8800F, 1.7000F, 1.8800F, 2.7000F, 2.8800F, 3.6000F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 28.0000F, -28.0000F, 30.0000F, -34.0000F, 36.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 28.0000F, -18.0000F, 30.0000F, -20.0000F, 36.0000F, -24.0000F, -12.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.7000F, 0.8800F, 1.7000F, 1.8800F, 2.7000F, 2.8800F, 3.6000F, 4.2000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -28.0000F, 28.0000F, -30.0000F, 34.0000F, -36.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -28.0000F, 18.0000F, -30.0000F, 20.0000F, -36.0000F, 24.0000F, 12.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 1, 1, 0, 0});
        c.add("frost_ring", CH_POS, new float[]{0.8400F, 1.3500F, 1.8400F, 2.3500F, 2.8400F, 3.5500F}, new float[]{14.0000F, 14.0000F, 0.0000F, 0.0000F, -14.0000F, -14.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{-24.0000F, -24.0000F, -28.0000F, -28.0000F, -24.0000F, -24.0000F}, new int[]{1, 1, 1, 1, 1, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.8400F, 0.9400F, 1.3000F, 1.8400F, 1.9400F, 2.3000F, 2.8400F, 2.9400F, 3.5500F}, new float[]{0.0000F, 0.0000F, 1.2000F, 0.0000F, 0.0000F, 1.3000F, 0.0000F, 0.0000F, 1.4500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.0500F, 0.0000F, 0.0000F, 1.1000F, 0.0000F, 0.0000F, 1.2000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.2000F, 0.0000F, 0.0000F, 1.3000F, 0.0000F, 0.0000F, 1.4500F, 0.0000F}, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        CLIPS.put("faceplant_triple", c);
    }

    private static void initClip_7() {
        Clip c = new Clip("spin_frost_ring", 3.4000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6000F, 1.9500F, 2.3000F, 2.8000F}, new float[]{0.0000F, 24.0000F, 26.0000F, 22.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 52.0000F, 56.0000F, -1104.0000F, -1080.0000F, -1080.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 1.1500F, 1.7500F, 2.2000F, 2.8000F}, new float[]{0.0000F, -22.0000F, -24.0000F, -18.0000F, -18.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -26.0000F, 12.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 2.0500F, 2.8000F}, new float[]{0.0000F, -74.0000F, -78.0000F, -72.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 0.8500F, 2.0500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, -22.0000F, 28.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, -24.0000F, -18.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7200F, 0.8500F, 2.0500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 22.0000F, -28.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -16.0000F, 24.0000F, 18.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.7200F, 0.9000F, 2.0500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -36.0000F, 42.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 24.0000F, 12.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.7200F, 0.9000F, 2.0500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 36.0000F, -42.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -24.0000F, -12.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 0.7200F, 1.7000F, 2.8000F}, new float[]{1.0000F, 1.4200F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.4200F, 1.2000F, 1.0000F}, new float[]{1.0000F, 1.4200F, 1.2000F, 1.0000F}, new int[]{0, 1, 0, 0});
        c.add("frost_ring", CH_ROT, new float[]{0.6000F, 2.3500F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -1080.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.7500F, 0.9000F, 1.8000F, 2.4500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.6000F, 1.6500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.1500F, 0.8000F, 0.1500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.6000F, 1.6500F, 0.0000F}, new int[]{1, 1, 1, 0, 0, 1});
        CLIPS.put("spin_frost_ring", c);
    }

    private static void initClip_8() {
        Clip c = new Clip("blizzard_channel", 4.8000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_ROT, new float[]{0.0000F, 4.8000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F}, new int[]{1, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.5500F, 1.5000F, 2.4000F, 3.3000F, 4.2500F, 4.8000F}, new float[]{0.0000F, 1.3500F, 1.5500F, 1.3500F, 1.5500F, 1.3500F, 0.0000F}, new float[]{0.0000F, 0.9000F, 1.1500F, 0.9500F, 1.1500F, 0.8500F, 0.0000F}, new float[]{0.0000F, 1.3500F, 1.5500F, 1.3500F, 1.5500F, 1.3500F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, -20.0000F, -28.0000F, -18.0000F, -28.0000F, -20.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 4.0000F, -4.0000F, 4.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 2.0000F, -2.0000F, 2.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.6500F, 1.6500F, 2.5500F, 3.4500F, 4.3000F, 4.8000F}, new float[]{0.0000F, -12.0000F, -18.0000F, -10.0000F, -18.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -4.0000F, 4.0000F, -4.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, -68.0000F, -78.0000F, -66.0000F, -78.0000F, -68.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 30.0000F, 16.0000F, 30.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -22.0000F, -12.0000F, -22.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -30.0000F, -16.0000F, -30.0000F, -18.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 22.0000F, 12.0000F, 22.0000F, 14.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 26.0000F, 14.0000F, 26.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 20.0000F, 10.0000F, 20.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -26.0000F, -14.0000F, -26.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -20.0000F, -10.0000F, -20.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, -24.0000F, -10.0000F, -24.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, 52.0000F, 68.0000F, 48.0000F, 68.0000F, 52.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 0.7500F, 1.7000F, 2.6000F, 3.5000F, 4.3500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -16.0000F, 14.0000F, -16.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 18.0000F, -16.0000F, 18.0000F, -10.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 24.0000F, 10.0000F, 24.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, -52.0000F, -68.0000F, -48.0000F, -68.0000F, -52.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 0.7500F, 1.7000F, 2.6000F, 3.5000F, 4.3500F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 16.0000F, -14.0000F, 16.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -18.0000F, 16.0000F, -18.0000F, 10.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 0.6000F, 1.5500F, 2.4500F, 3.3500F, 4.2500F, 4.8000F}, new float[]{1.0000F, 1.3800F, 1.0800F, 1.4600F, 1.0800F, 1.4200F, 1.0000F}, new float[]{1.0000F, 1.3800F, 1.0800F, 1.4600F, 1.0800F, 1.4200F, 1.0000F}, new float[]{1.0000F, 1.3800F, 1.0800F, 1.4600F, 1.0800F, 1.4200F, 1.0000F}, new int[]{0, 0, 0, 0, 0, 0, 0});
        CLIPS.put("blizzard_channel", c);
    }

    private static void initClip_9() {
        Clip c = new Clip("flail_360", 3.4000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4200F, 0.6200F, 2.0200F, 2.3500F, 2.8000F}, new float[]{0.0000F, 46.0000F, 48.0000F, 48.0000F, 40.0000F, 0.0000F}, new float[]{0.0000F, 62.0000F, 68.0000F, -1104.0000F, -1080.0000F, -1080.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.7500F, 1.2500F, 1.7800F, 2.1500F, 2.8000F}, new float[]{0.0000F, 34.0000F, 36.0000F, 34.0000F, 34.0000F, 30.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, -28.0000F, 14.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.5000F, 0.7800F, 1.3000F, 1.8500F, 2.8000F}, new float[]{0.0000F, 20.0000F, 22.0000F, 20.0000F, 20.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, 16.0000F, -24.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2500F, 2.1000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -35.0000F, 48.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, -18.0000F, -8.0000F, 0.0000F}, new int[]{0, 1, 0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.7500F, 1.2500F, 2.1000F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 35.0000F, -48.0000F, -14.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, 18.0000F, 8.0000F, 0.0000F}, new int[]{0, 1, 0, 0, 0});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.7500F, 0.9000F, 2.0000F, 2.5500F}, new float[]{0.0000F, 0.0000F, 1.3500F, 1.4500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.0000F, 0.4000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.3500F, 1.4500F, 0.0000F}, new int[]{1, 1, 1, 0, 1});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 0.8000F, 1.3500F, 2.1500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 38.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -16.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 0.8000F, 1.3500F, 2.1500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -38.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 16.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 0.8000F, 1.3500F, 2.1500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -24.0000F, 38.0000F, 12.0000F, 0.0000F}, new float[]{0.0000F, 14.0000F, -16.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 0.8000F, 1.3500F, 2.1500F, 2.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 24.0000F, -38.0000F, -12.0000F, 0.0000F}, new float[]{0.0000F, -14.0000F, 16.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        CLIPS.put("flail_360", c);
    }

    private static void initClip_10() {
        Clip c = new Clip("half_flail_combo", 4.0000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.6500F, 1.0200F, 1.1600F, 1.4500F, 1.8500F, 1.9800F, 2.4500F, 2.6200F, 2.7800F, 2.9500F, 3.0600F, 3.2000F, 3.6000F, 4.0000F}, new float[]{0.0000F, 44.0000F, 46.0000F, 44.0000F, 44.0000F, 46.0000F, 44.0000F, 40.0000F, -34.0000F, -36.0000F, -35.0000F, 52.0000F, 45.0000F, 48.0000F, 46.0000F, 0.0000F}, new float[]{0.0000F, 95.0000F, 98.0000F, -106.0000F, -95.0000F, -98.0000F, 102.0000F, 88.0000F, 0.0000F, 2.0000F, -2.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4800F, 0.6800F, 1.0600F, 1.4800F, 1.8900F, 2.4800F, 2.8000F, 2.9700F, 3.1000F, 3.6200F, 4.0000F}, new float[]{0.0000F, 32.0000F, 34.0000F, 32.0000F, 34.0000F, 32.0000F, -26.0000F, -27.0000F, 38.0000F, 32.0000F, 34.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, -26.0000F, -16.0000F, 26.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.5000F, 1.9200F, 2.5000F, 2.8200F, 2.9900F, 3.6200F, 4.0000F}, new float[]{0.0000F, 18.0000F, 18.0000F, -18.0000F, -19.0000F, 24.0000F, 18.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 1, 1, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 2.4500F, 2.7800F, 2.9700F, 3.6000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, -28.0000F, 28.0000F, 10.0000F, 0.0000F}, new float[]{0.0000F, 16.0000F, 18.0000F, -18.0000F, -6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 2.4500F, 2.7800F, 2.9700F, 3.6000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, 28.0000F, -28.0000F, -10.0000F, 0.0000F}, new float[]{0.0000F, -16.0000F, -18.0000F, 18.0000F, 6.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0500F, 1.4500F, 1.8800F, 2.7800F, 2.9800F, 3.6000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -32.0000F, 42.0000F, 32.0000F, -42.0000F, -34.0000F, 36.0000F, 16.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, -14.0000F, -18.0000F, 14.0000F, 36.0000F, -24.0000F, -12.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.6500F, 1.0500F, 1.4500F, 1.8800F, 2.7800F, 2.9800F, 3.6000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 32.0000F, -42.0000F, -32.0000F, 42.0000F, 34.0000F, -36.0000F, -16.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, 14.0000F, 18.0000F, -14.0000F, -36.0000F, 24.0000F, 12.0000F, 0.0000F}, new int[]{0, 1, 1, 1, 1, 1, 1, 0, 0});
        c.add("frost_ring", CH_POS, new float[]{2.9400F, 3.7000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{-28.0000F, -28.0000F}, new int[]{1, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 2.9400F, 3.0400F, 3.7000F}, new float[]{0.0000F, 0.0000F, 1.4000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.1500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.4000F, 0.0000F}, new int[]{1, 1, 1, 1});
        CLIPS.put("half_flail_combo", c);
    }

    private static void initClip_11() {
        Clip c = new Clip("rotary_spray_beam", 4.8000F, false);
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7000F, 4.1000F, 4.8000F}, new float[]{0.0000F, -10.0000F, 26.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, -360.0000F, -360.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7000F, 4.1000F, 4.8000F}, new float[]{0.0000F, -6.0000F, 14.0000F, 14.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7000F, 0.8200F, 1.6000F, 2.4000F, 3.2000F, 4.1000F, 4.8000F}, new float[]{0.0000F, -14.0000F, -22.0000F, -28.0000F, -21.0000F, -24.0000F, -21.0000F, -23.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, -22.0000F, 28.0000F, 26.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -16.0000F, -14.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 22.0000F, -28.0000F, -26.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 16.0000F, 14.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, -22.0000F, 26.0000F, 24.0000F, 0.0000F}, new float[]{0.0000F, -12.0000F, -14.0000F, 14.0000F, 12.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.4500F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, 22.0000F, -26.0000F, -24.0000F, 0.0000F}, new float[]{0.0000F, 12.0000F, 14.0000F, -14.0000F, -12.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -22.0000F, 32.0000F, 28.0000F, 0.0000F}, new float[]{0.0000F, 18.0000F, 26.0000F, 22.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.7200F, 0.8500F, 4.1000F, 4.8000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 22.0000F, -32.0000F, -28.0000F, 0.0000F}, new float[]{0.0000F, -18.0000F, -26.0000F, -22.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 0.7200F, 4.1000F, 4.8000F}, new float[]{1.0000F, 1.4800F, 1.3500F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.3500F, 1.0000F}, new float[]{1.0000F, 1.4800F, 1.3500F, 1.0000F}, new int[]{0, 1, 0, 0});
        c.add("rotary_beam", CH_ROT, new float[]{0.8000F, 4.2000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F}, new float[]{0.0000F, 720.0000F}, new int[]{1, 1});
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F, 0.7500F, 0.8500F, 2.4000F, 4.1000F, 4.4000F}, new float[]{0.0000F, 0.0000F, 1.2500F, 1.4000F, 1.2500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.2500F, 1.4000F, 1.2500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.4500F, 1.5000F, 1.4500F, 0.0000F}, new int[]{1, 1, 1, 0, 0, 1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F, 0.8000F, 0.9500F, 4.2000F, 4.6000F}, new float[]{0.0000F, 0.0000F, 1.5500F, 1.6500F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.5500F, 0.5000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 1.5500F, 1.6500F, 0.0000F}, new int[]{1, 1, 1, 0, 1});
        CLIPS.put("rotary_spray_beam", c);
    }

    private static void initClip_12() {
        Clip c = new Clip("stunned_paralyzed", 4.0000F, true);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 1.0000F, 2.0000F, 3.0000F, 4.0000F}, new float[]{53.0000F, 56.0000F, 57.0000F, 55.0000F, 53.0000F}, new float[]{0.0000F, 2.0000F, 0.0000F, -2.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 1.1000F, 2.1500F, 3.1500F, 4.0000F}, new float[]{35.0000F, 38.0000F, 39.0000F, 37.0000F, 35.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 1.2000F, 2.3000F, 3.3000F, 4.0000F}, new float[]{17.0000F, 20.0000F, 21.0000F, 19.0000F, 17.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 0, 0, 0});
        c.add("corolla_head", CH_ROT, new float[]{0.0000F, 1.2500F, 1.4000F, 1.5500F, 1.7500F, 3.0000F, 4.0000F}, new float[]{5.0000F, 8.0000F, 3.0000F, 9.0000F, 7.0000F, 6.0000F, 5.0000F}, new float[]{0.0000F, 0.0000F, 6.0000F, -5.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, -4.0000F, 3.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 0, 1, 1, 0, 0, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-32.0000F, -38.0000F, -32.0000F}, new float[]{24.0000F, 28.0000F, 24.0000F}, new int[]{0, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{32.0000F, 38.0000F, 32.0000F}, new float[]{-24.0000F, -28.0000F, -24.0000F}, new int[]{0, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-28.0000F, -34.0000F, -28.0000F}, new float[]{-20.0000F, -24.0000F, -20.0000F}, new int[]{0, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{28.0000F, 34.0000F, 28.0000F}, new float[]{20.0000F, 24.0000F, 20.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-40.0000F, -44.0000F, -40.0000F}, new float[]{-42.0000F, -46.0000F, -42.0000F}, new int[]{0, 0, 0});
        c.add("lash_r_mid", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{-16.0000F, -20.0000F, -16.0000F}, new float[]{-18.0000F, -22.0000F, -18.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{40.0000F, 44.0000F, 40.0000F}, new float[]{42.0000F, 46.0000F, 42.0000F}, new int[]{0, 0, 0});
        c.add("lash_l_mid", CH_ROT, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F}, new float[]{16.0000F, 20.0000F, 16.0000F}, new float[]{18.0000F, 22.0000F, 18.0000F}, new int[]{0, 0, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 2.0000F, 4.0000F}, new float[]{0.5800F, 0.4800F, 0.5800F}, new float[]{0.5800F, 0.4800F, 0.5800F}, new float[]{0.5800F, 0.4800F, 0.5800F}, new int[]{0, 0, 0});
        CLIPS.put("stunned_paralyzed", c);
    }

    private static void initClip_13() {
        Clip c = new Clip("death_wither", 2.6000F, false);
        c.add("rotary_beam", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_r", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("lash_beam_l", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("frost_ring", CH_SCALE, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new float[]{0.0000F}, new int[]{1});
        c.add("stem_1", CH_ROT, new float[]{0.0000F, 0.2500F, 0.5500F, 0.8500F, 1.8500F, 2.0500F, 2.6000F}, new float[]{0.0000F, -24.0000F, -18.0000F, -8.0000F, 60.0000F, 53.0000F, 58.0000F}, new float[]{0.0000F, -16.0000F, 18.0000F, -10.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 4.0000F, -5.0000F, 3.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 1, 0, 1, 0, 0});
        c.add("stem_2", CH_ROT, new float[]{0.0000F, 0.3000F, 0.9000F, 1.9000F, 2.1000F, 2.6000F}, new float[]{0.0000F, -18.0000F, -4.0000F, 50.0000F, 44.0000F, 48.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0, 1, 0, 0});
        c.add("stem_3", CH_ROT, new float[]{0.0000F, 0.3500F, 0.9500F, 1.9500F, 2.6000F}, new float[]{0.0000F, -14.0000F, 0.0000F, 32.0000F, 30.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F, 0.0000F}, new int[]{0, 1, 0, 1, 0});
        c.add("petal_ur", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -64.0000F, -68.0000F}, new float[]{0.0000F, -14.0000F, 34.0000F, 36.0000F}, new int[]{0, 1, 0, 0});
        c.add("petal_ul", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, 64.0000F, 68.0000F}, new float[]{0.0000F, 14.0000F, -34.0000F, -36.0000F}, new int[]{0, 1, 0, 0});
        c.add("petal_lr", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 20.0000F, -64.0000F, -68.0000F}, new float[]{0.0000F, 14.0000F, -34.0000F, -36.0000F}, new int[]{0, 1, 0, 0});
        c.add("petal_ll", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9000F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -20.0000F, 64.0000F, 68.0000F}, new float[]{0.0000F, -14.0000F, 34.0000F, 36.0000F}, new int[]{0, 1, 0, 0});
        c.add("lash_r_base", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9500F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, 26.0000F, -56.0000F, -60.0000F}, new float[]{0.0000F, 22.0000F, -34.0000F, -38.0000F}, new int[]{0, 1, 0, 0});
        c.add("lash_l_base", CH_ROT, new float[]{0.0000F, 0.3000F, 1.9500F, 2.6000F}, new float[]{0.0000F, 0.0000F, 0.0000F, 0.0000F}, new float[]{0.0000F, -26.0000F, 56.0000F, 60.0000F}, new float[]{0.0000F, -22.0000F, 34.0000F, 38.0000F}, new int[]{0, 1, 0, 0});
        c.add("root_core", CH_SCALE, new float[]{0.0000F, 0.3000F, 0.6000F, 2.1000F}, new float[]{1.0000F, 1.3500F, 0.7000F, 0.0500F}, new float[]{1.0000F, 1.3500F, 0.7000F, 0.0500F}, new float[]{1.0000F, 1.3500F, 0.7000F, 0.0500F}, new int[]{0, 1, 1, 0});
        c.add("corolla_core", CH_SCALE, new float[]{0.0000F, 0.3000F, 0.6000F, 2.1000F}, new float[]{1.0000F, 1.4000F, 0.7000F, 0.0500F}, new float[]{1.0000F, 1.4000F, 0.7000F, 0.0500F}, new float[]{1.0000F, 1.4000F, 0.7000F, 0.0500F}, new int[]{0, 1, 1, 0});
        CLIPS.put("death_wither", c);
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
        // Fritsch-Carlson style tangent clamp to prevent wild spline overshoot on sharp transitions
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
        if (tr.interps[i] == INTERP_LINEAR || tr.interps[i + 1] == INTERP_LINEAR) {
            out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * u;
            out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * u;
            out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * u;
        } else {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
        }
    }

    private static void applyWeighted(CryoRegisvineModel<?> model, Clip clip, float timeSeconds, float weight) {
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

    private static void applyClipWeighted(CryoRegisvineModel<?> model, String clipName, float timeSeconds, float weight) {
        if (clipName == null || weight <= 1e-4F) return;
        BedrockAnimationLoader.Clip ext = BedrockAnimationLoader.getClip("cryo_regisvine_boss", clipName);
        if (ext != null) {
            BedrockAnimationLoader.applyWeighted(model::getBone, ext, timeSeconds, weight);
        } else {
            applyWeighted(model, CLIPS.get(clipName), timeSeconds, weight);
        }
    }

    public static void apply(CryoRegisvineModel<?> model, String clipName, float timeSeconds) {
        model.resetPose();
        applyClipWeighted(model, clipName, timeSeconds, 1.0F);
    }

    public static void applyBlended(CryoRegisvineModel<?> model, String prevClipName, float prevTimeSeconds, String currClipName, float currTimeSeconds, float blendAlpha) {
        model.resetPose();
        float a = Math.max(0.0F, Math.min(1.0F, blendAlpha));
        float smoothA = a * a * (3.0F - 2.0F * a);
        if (prevClipName != null && smoothA < 0.999F) {
            applyClipWeighted(model, prevClipName, prevTimeSeconds, 1.0F - smoothA);
        }
        applyClipWeighted(model, currClipName, currTimeSeconds, smoothA);
    }
}
