package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 14 AAA Cataclysm-grade Catmull-Rom keyframe animations for {@link PrimoGeovishapModel},
 * synced 1:1 with {@code models/primo_geovishap_boss.bbmodel} across all 18 articulated bones.
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

    public static void apply(PrimoGeovishapModel<?> model, String clipName, float seconds) {
        model.resetPose();
        Clip c = CLIPS.get(clipName);
        if (c == null) return;
        float t = c.loop ? ((seconds % c.length) + c.length) % c.length : Mth.clamp(seconds, 0.0F, c.length);
        for (Track tr : c.tracks) {
            ModelPart part = model.getBone(tr.bone);
            if (part == null) continue;
            float vx = sampleCatmullRom(tr.t, tr.x, t);
            float vy = sampleCatmullRom(tr.t, tr.y, t);
            float vz = sampleCatmullRom(tr.t, tr.z, t);
            if (tr.isPos) {
                // Blockbench +Y is up; Minecraft ModelPart +Y is down
                part.x += vx;
                part.y -= vy;
                part.z += vz;
            } else {
                // Blockbench -> Minecraft PartPose rotation sign conversion (-rx, -ry, +rz)
                part.xRot -= vx * DEG;
                part.yRot -= vy * DEG;
                part.zRot += vz * DEG;
            }
        }
    }

    private static float sampleCatmullRom(float[] times, float[] vals, float t) {
        int n = times.length;
        if (n == 0) return 0.0F;
        if (n == 1 || t <= times[0]) return vals[0];
        if (t >= times[n - 1]) return vals[n - 1];
        int i = 0;
        while (i < n - 2 && t > times[i + 1]) i++;
        float dt = times[i + 1] - times[i];
        float u = dt > 1.0E-5F ? (t - times[i]) / dt : 0.0F;
        float p0 = vals[Math.max(0, i - 1)];
        float p1 = vals[i];
        float p2 = vals[i + 1];
        float p3 = vals[Math.min(n - 1, i + 2)];
        float u2 = u * u;
        float u3 = u2 * u;
        return 0.5F * ((2.0F * p1)
                + (-p0 + p2) * u
                + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * u2
                + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * u3);
    }

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

    private static Clip build_idle() {
        Clip c = new Clip("idle", 4.000F, true);
        c.pos("body", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -0.13F, -0.45F, -0.77F, -0.90F, -0.77F, -0.45F, -0.13F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 1.27F, 1.80F, 1.27F, 0.00F, -1.27F, -1.80F, -1.27F, -0.00F}, new float[]{0.00F, 1.13F, 1.60F, 1.13F, 0.00F, -1.13F, -1.60F, -1.13F, -0.00F}, new float[]{0.47F, 1.11F, 1.10F, 0.45F, -0.47F, -1.11F, -1.10F, -0.45F, 0.47F});
        c.rot("neck", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{1.03F, -1.26F, -2.82F, -2.72F, -1.03F, 1.26F, 2.82F, 2.72F, 1.03F}, new float[]{0.00F, 3.18F, 4.50F, 3.18F, 0.00F, -3.18F, -4.50F, -3.18F, -0.00F}, new float[]{-0.00F, -0.71F, -1.00F, -0.71F, -0.00F, 0.71F, 1.00F, 0.71F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-1.24F, 0.41F, 1.82F, 2.16F, 1.24F, -0.41F, -1.82F, -2.16F, -1.24F}, new float[]{-1.48F, 3.06F, 5.81F, 5.16F, 1.48F, -3.06F, -5.81F, -5.16F, -1.48F}, new float[]{0.86F, 1.73F, 1.58F, 0.51F, -0.86F, -1.73F, -1.58F, -0.51F, 0.86F});
        c.rot("jaw", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{2.34F, 5.77F, 8.45F, 8.82F, 6.66F, 3.23F, 0.55F, 0.18F, 2.34F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-0.00F, -2.26F, -3.20F, -2.26F, -0.00F, 2.26F, 3.20F, 2.26F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 1.06F, 1.50F, 1.06F, 0.00F, -1.06F, -1.50F, -1.06F, -0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 2.26F, 3.20F, 2.26F, 0.00F, -2.26F, -3.20F, -2.26F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -1.06F, -1.50F, -1.06F, -0.00F, 1.06F, 1.50F, 1.06F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-0.00F, -2.26F, -3.20F, -2.26F, -0.00F, 2.26F, 3.20F, 2.26F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -1.06F, -1.50F, -1.06F, -0.00F, 1.06F, 1.50F, 1.06F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 2.26F, 3.20F, 2.26F, 0.00F, -2.26F, -3.20F, -2.26F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 1.06F, 1.50F, 1.06F, 0.00F, -1.06F, -1.50F, -1.06F, -0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 1.84F, 2.60F, 1.84F, 0.00F, -1.84F, -2.60F, -1.84F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.57F, 0.80F, 0.57F, 0.00F, -0.57F, -0.80F, -0.57F, -0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-0.00F, -1.84F, -2.60F, -1.84F, -0.00F, 1.84F, 2.60F, 1.84F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -0.57F, -0.80F, -0.57F, -0.00F, 0.57F, 0.80F, 0.57F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 1.84F, 2.60F, 1.84F, 0.00F, -1.84F, -2.60F, -1.84F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -0.57F, -0.80F, -0.57F, -0.00F, 0.57F, 0.80F, 0.57F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-0.00F, -1.84F, -2.60F, -1.84F, -0.00F, 1.84F, 2.60F, 1.84F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.57F, 0.80F, 0.57F, 0.00F, -0.57F, -0.80F, -0.57F, -0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{0.00F, 1.41F, 2.00F, 1.41F, 0.00F, -1.41F, -2.00F, -1.41F, -0.00F}, new float[]{0.00F, 5.30F, 7.50F, 5.30F, 0.00F, -5.30F, -7.50F, -5.30F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-1.20F, 0.70F, 2.19F, 2.40F, 1.20F, -0.70F, -2.19F, -2.40F, -1.20F}, new float[]{-6.35F, 1.42F, 8.36F, 10.40F, 6.35F, -1.42F, -8.36F, -10.40F, -6.35F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-2.52F, -0.64F, 1.62F, 2.93F, 2.52F, 0.64F, -1.62F, -2.93F, -2.52F}, new float[]{-12.53F, -6.40F, 3.48F, 11.32F, 12.53F, 6.40F, -3.48F, -11.32F, -12.53F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.500F, 1.000F, 1.500F, 2.000F, 2.500F, 3.000F, 3.500F, 4.000F}, new float[]{-3.49F, -2.29F, 0.25F, 2.64F, 3.49F, 2.29F, -0.25F, -2.64F, -3.49F}, new float[]{-14.40F, -14.24F, -5.74F, 6.12F, 14.40F, 14.24F, 5.74F, -6.12F, -14.40F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_walk() {
        Clip c = new Clip("walk", 1.800F, true);
        c.pos("body", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.00F, 0.45F, 0.78F, 0.90F, 0.78F, 0.45F, 0.00F, -0.45F, -0.78F, -0.90F, -0.78F, -0.45F, -0.00F}, new float[]{0.15F, 1.20F, 1.70F, 1.15F, 0.10F, -0.40F, 0.15F, 1.20F, 1.70F, 1.15F, 0.10F, -0.40F, 0.15F}, new float[]{0.00F, 0.35F, 0.35F, 0.00F, -0.35F, -0.35F, -0.00F, 0.35F, 0.35F, 0.00F, -0.35F, -0.35F, -0.00F});
        c.rot("torso", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.77F, 2.54F, 1.77F, -0.77F, -2.54F, -1.77F, 0.77F, 2.54F, 1.77F, -0.77F, -2.54F, -1.77F, 0.77F}, new float[]{0.00F, 2.75F, 4.76F, 5.50F, 4.76F, 2.75F, 0.00F, -2.75F, -4.76F, -5.50F, -4.76F, -2.75F, -0.00F}, new float[]{-1.44F, 0.72F, 2.70F, 3.94F, 4.14F, 3.22F, 1.44F, -0.72F, -2.70F, -3.94F, -4.14F, -3.22F, -1.44F});
        c.rot("neck", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.60F, -2.25F, -2.84F, -0.60F, 2.25F, 2.84F, 0.60F, -2.25F, -2.84F, -0.60F, 2.25F, 2.84F, 0.60F}, new float[]{0.80F, -1.27F, -3.00F, -3.92F, -3.79F, -2.65F, -0.80F, 1.27F, 3.00F, 3.92F, 3.79F, 2.65F, 0.80F}, new float[]{0.59F, -0.44F, -1.36F, -1.91F, -1.95F, -1.47F, -0.59F, 0.44F, 1.36F, 1.91F, 1.95F, 1.47F, 0.59F});
        c.rot("head", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-1.35F, 1.04F, 2.39F, 1.35F, -1.04F, -2.39F, -1.35F, 1.04F, 2.39F, 1.35F, -1.04F, -2.39F, -1.35F}, new float[]{0.89F, -0.67F, -2.04F, -2.87F, -2.92F, -2.20F, -0.89F, 0.67F, 2.04F, 2.87F, 2.92F, 2.20F, 0.89F}, new float[]{0.58F, -0.18F, -0.90F, -1.38F, -1.49F, -1.20F, -0.58F, 0.18F, 0.90F, 1.38F, 1.49F, 1.20F, 0.58F});
        c.rot("jaw", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{2.49F, 5.86F, 8.37F, 7.51F, 4.14F, 1.63F, 2.49F, 5.86F, 8.37F, 7.51F, 4.14F, 1.63F, 2.49F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-8.00F, -18.93F, -24.79F, -24.00F, -20.79F, -12.00F, -0.00F, 12.00F, 20.79F, 24.00F, 16.79F, 5.07F, -8.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{4.50F, 3.90F, 2.25F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 2.25F, 3.90F, 4.50F});
        c.rot("forearm_left", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-28.00F, -19.25F, -5.34F, 10.00F, 8.66F, 5.00F, 0.00F, 0.00F, 0.00F, 0.00F, -14.00F, -24.25F, -28.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-0.00F, 12.00F, 20.79F, 24.00F, 16.79F, 5.07F, -8.00F, -18.93F, -24.79F, -24.00F, -20.79F, -12.00F, -0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-0.00F, -0.00F, -0.00F, -0.00F, -2.25F, -3.90F, -4.50F, -3.90F, -2.25F, -0.00F, -0.00F, -0.00F, -0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, -14.00F, -24.25F, -28.00F, -19.25F, -5.34F, 10.00F, 8.66F, 5.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{3.30F, -7.45F, -16.21F, -20.62F, -19.95F, -16.54F, -8.70F, 1.47F, 11.25F, 18.01F, 19.50F, 13.16F, 3.30F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-3.15F, -3.49F, -2.89F, -1.52F, -0.00F, -0.00F, -0.00F, -0.00F, -0.00F, -0.00F, -0.26F, -1.97F, -3.15F});
        c.rot("shin_right", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{19.93F, 25.93F, 21.50F, 11.31F, 0.00F, 0.00F, 0.00F, -0.59F, -4.50F, -7.20F, -6.07F, 8.01F, 19.93F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-8.70F, 1.47F, 11.25F, 18.01F, 19.50F, 13.16F, 3.30F, -7.45F, -16.21F, -20.62F, -19.95F, -16.54F, -8.70F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.26F, 1.97F, 3.15F, 3.49F, 2.89F, 1.52F, 0.00F, 0.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.00F, -0.59F, -4.50F, -7.20F, -6.07F, 8.01F, 19.93F, 25.93F, 21.50F, 11.31F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{0.00F, 2.17F, 2.17F, 0.00F, -2.17F, -2.17F, -0.00F, 2.17F, 2.17F, 0.00F, -2.17F, -2.17F, -0.00F}, new float[]{2.23F, -2.43F, -6.44F, -8.72F, -8.66F, -6.29F, -2.23F, 2.43F, 6.44F, 8.72F, 8.66F, 6.29F, 2.23F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-1.69F, 1.30F, 2.99F, 1.69F, -1.30F, -2.99F, -1.69F, 1.30F, 2.99F, 1.69F, -1.30F, -2.99F, -1.69F}, new float[]{9.39F, 4.01F, -2.45F, -8.25F, -11.84F, -12.26F, -9.39F, -4.01F, 2.45F, 8.25F, 11.84F, 12.26F, 9.39F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-3.26F, -0.53F, 2.73F, 3.26F, 0.53F, -2.73F, -3.26F, -0.53F, 2.73F, 3.26F, 0.53F, -2.73F, -3.26F}, new float[]{15.39F, 12.39F, 6.08F, -1.87F, -9.31F, -14.26F, -15.39F, -12.39F, -6.08F, 1.87F, 9.31F, 14.26F, 15.39F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.150F, 0.300F, 0.450F, 0.600F, 0.750F, 0.900F, 1.050F, 1.200F, 1.350F, 1.500F, 1.650F, 1.800F}, new float[]{-3.90F, -2.73F, 1.16F, 3.90F, 2.73F, -1.16F, -3.90F, -2.73F, 1.16F, 3.90F, 2.73F, -1.16F, -3.90F}, new float[]{15.97F, 17.98F, 15.17F, 8.30F, -0.80F, -9.68F, -15.97F, -17.98F, -15.17F, -8.30F, 0.80F, 9.68F, 15.97F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_spawn_awaken() {
        Clip c = new Clip("spawn_awaken", 3.400F, false);
        c.pos("body", new float[]{0.000F, 0.550F, 0.950F, 1.300F, 1.800F, 2.550F, 2.950F, 3.150F, 3.400F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{-11.00F, -10.50F, -3.50F, -1.50F, 4.80F, 4.40F, -2.40F, -0.80F, 0.00F}, new float[]{0.00F, 0.00F, -2.00F, -1.00F, 1.50F, 1.20F, -1.50F, -0.50F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.550F, 1.050F, 1.350F, 1.800F, 2.000F, 2.200F, 2.400F, 2.600F, 2.950F, 3.400F}, new float[]{12.00F, 16.00F, 8.00F, 14.00F, -24.00F, -26.00F, -23.00F, -25.00F, -20.00F, 9.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 2.00F, -2.00F, 2.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 1.00F, -1.00F, 1.00F, 0.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.600F, 1.300F, 1.800F, 2.050F, 2.300F, 2.550F, 2.950F, 3.400F}, new float[]{24.00F, 20.00F, 18.00F, -24.00F, -28.00F, -25.00F, -22.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 4.00F, -4.00F, 2.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 1.300F, 1.800F, 2.000F, 2.200F, 2.450F, 2.950F, 3.400F}, new float[]{14.00F, 10.00F, -14.00F, -18.00F, -15.00F, -16.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 5.00F, -5.00F, 4.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 2.00F, -2.00F, 1.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 1.400F, 1.800F, 2.050F, 2.300F, 2.550F, 2.950F, 3.400F}, new float[]{0.00F, 4.00F, 46.00F, 42.00F, 48.00F, 40.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.950F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-30.00F, -46.00F, 12.00F, -48.00F, -42.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, -14.00F, -10.00F, 0.00F, 0.00F}, new float[]{-14.00F, 16.00F, 8.00F, 28.00F, 24.00F, 10.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.950F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-35.00F, -24.00F, -12.00F, -42.00F, -36.00F, -16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 10.00F, 8.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.950F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-30.00F, -46.00F, 12.00F, -48.00F, -42.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 14.00F, 10.00F, 0.00F, 0.00F}, new float[]{14.00F, -16.00F, -8.00F, -28.00F, -24.00F, -10.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.950F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-35.00F, -24.00F, -12.00F, -42.00F, -36.00F, -16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, -10.00F, -8.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-14.00F, -10.00F, 22.00F, 20.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.00F, 8.00F, 8.00F, 4.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{18.00F, 14.00F, 6.00F, 6.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-14.00F, -10.00F, 22.00F, 20.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -4.00F, -8.00F, -8.00F, -4.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{18.00F, 14.00F, 6.00F, 6.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 1.300F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-10.00F, -4.00F, 22.00F, 20.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 6.00F, -6.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-8.00F, 10.00F, 8.00F, 6.00F, 0.00F}, new float[]{0.00F, -8.00F, 10.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-6.00F, 6.00F, 6.00F, 8.00F, 0.00F}, new float[]{0.00F, -12.00F, 14.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.800F, 2.550F, 2.950F, 3.400F}, new float[]{-4.00F, 5.00F, 5.00F, 10.00F, 0.00F}, new float[]{0.00F, -15.00F, 16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_left_claw() {
        Clip c = new Clip("attack_left_claw", 2.200F, false);
        c.pos("body", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, 1.80F, -2.20F, -1.80F, -0.50F, 0.00F}, new float[]{0.00F, -1.80F, 0.20F, 0.00F, -0.60F, 0.00F}, new float[]{0.00F, 2.00F, -4.20F, -3.50F, -1.00F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, -10.00F, 6.00F, 12.00F, -4.00F, 0.00F}, new float[]{0.00F, 22.00F, -26.00F, -20.00F, 6.00F, 0.00F}, new float[]{0.00F, -8.00F, 10.00F, 8.00F, -3.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 2.200F}, new float[]{0.00F, -6.00F, 10.00F, 8.00F, 0.00F}, new float[]{0.00F, -12.00F, 14.00F, 10.00F, 0.00F}, new float[]{0.00F, 4.00F, -6.00F, -4.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 2.200F}, new float[]{0.00F, -8.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, -14.00F, 16.00F, 12.00F, 0.00F}, new float[]{0.00F, 6.00F, -8.00F, -5.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.500F, 0.780F, 1.200F, 1.800F, 2.200F}, new float[]{0.00F, 12.00F, 36.00F, 24.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.550F, 0.780F, 1.180F, 1.600F, 2.200F}, new float[]{0.00F, -78.00F, 38.00F, 34.00F, -18.00F, 0.00F}, new float[]{0.00F, -14.00F, 28.00F, 22.00F, -4.00F, 0.00F}, new float[]{0.00F, 26.00F, -18.00F, -14.00F, 8.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.550F, 0.780F, 1.180F, 1.600F, 2.200F}, new float[]{0.00F, -42.00F, -18.00F, 6.00F, -22.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 12.00F, -8.00F, -4.00F, 4.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.550F, 0.780F, 1.250F, 2.200F}, new float[]{0.00F, 14.00F, -18.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -10.00F, -14.00F, -8.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.550F, 0.780F, 1.250F, 2.200F}, new float[]{0.00F, -18.00F, -24.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, -12.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.00F, 6.00F, 4.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, 14.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, -12.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -4.00F, -6.00F, -4.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, 14.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.550F, 0.850F, 1.350F, 2.200F}, new float[]{0.00F, 8.00F, 4.00F, 2.00F, 0.00F}, new float[]{0.00F, -14.00F, 18.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.620F, 0.950F, 1.450F, 2.200F}, new float[]{0.00F, 6.00F, 6.00F, 3.00F, 0.00F}, new float[]{0.00F, -18.00F, 22.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.700F, 1.050F, 1.550F, 2.200F}, new float[]{0.00F, 4.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, -22.00F, 26.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, 4.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, -26.00F, 30.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_right_claw() {
        Clip c = new Clip("attack_right_claw", 2.200F, false);
        c.pos("body", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, -1.80F, 2.20F, 1.80F, 0.50F, 0.00F}, new float[]{0.00F, -1.80F, 0.20F, 0.00F, -0.60F, 0.00F}, new float[]{0.00F, 2.00F, -4.20F, -3.50F, -1.00F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, -10.00F, 6.00F, 12.00F, -4.00F, 0.00F}, new float[]{0.00F, -22.00F, 26.00F, 20.00F, -6.00F, 0.00F}, new float[]{0.00F, 8.00F, -10.00F, -8.00F, 3.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 2.200F}, new float[]{0.00F, -6.00F, 10.00F, 8.00F, 0.00F}, new float[]{0.00F, 12.00F, -14.00F, -10.00F, 0.00F}, new float[]{0.00F, -4.00F, 6.00F, 4.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.550F, 0.780F, 1.150F, 2.200F}, new float[]{0.00F, -8.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, 14.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, -6.00F, 8.00F, 5.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.500F, 0.780F, 1.200F, 1.800F, 2.200F}, new float[]{0.00F, 12.00F, 36.00F, 24.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.550F, 0.780F, 1.180F, 1.600F, 2.200F}, new float[]{0.00F, -78.00F, 38.00F, 34.00F, -18.00F, 0.00F}, new float[]{0.00F, 14.00F, -28.00F, -22.00F, 4.00F, 0.00F}, new float[]{0.00F, -26.00F, 18.00F, 14.00F, -8.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.550F, 0.780F, 1.180F, 1.600F, 2.200F}, new float[]{0.00F, -42.00F, -18.00F, 6.00F, -22.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -12.00F, 8.00F, 4.00F, -4.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.550F, 0.780F, 1.250F, 2.200F}, new float[]{0.00F, 14.00F, -18.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 10.00F, 14.00F, 8.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.550F, 0.780F, 1.250F, 2.200F}, new float[]{0.00F, -18.00F, -24.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, -12.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.00F, 6.00F, 4.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, 14.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, -12.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -4.00F, -6.00F, -4.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.550F, 0.780F, 1.200F, 2.200F}, new float[]{0.00F, 8.00F, 14.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.550F, 0.850F, 1.350F, 2.200F}, new float[]{0.00F, 8.00F, 4.00F, 2.00F, 0.00F}, new float[]{0.00F, 14.00F, -18.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.620F, 0.950F, 1.450F, 2.200F}, new float[]{0.00F, 6.00F, 6.00F, 3.00F, 0.00F}, new float[]{0.00F, 18.00F, -22.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.700F, 1.050F, 1.550F, 2.200F}, new float[]{0.00F, 4.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, 22.00F, -26.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.780F, 1.150F, 1.650F, 2.200F}, new float[]{0.00F, 4.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, 26.00F, -30.00F, -16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_combo_slash() {
        Clip c = new Clip("attack_combo_slash", 3.600F, false);
        c.pos("body", new float[]{0.000F, 0.450F, 0.720F, 1.180F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -1.60F, 2.00F, 1.80F, -2.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -1.60F, -2.00F, -1.60F, -2.20F, 5.20F, -3.20F, -2.60F, 0.00F}, new float[]{0.00F, 1.50F, -3.20F, -1.20F, -4.00F, 1.00F, -5.00F, -4.20F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.450F, 0.720F, 1.180F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -8.00F, 12.00F, -8.00F, 14.00F, -25.00F, 18.00F, 14.00F, 0.00F}, new float[]{0.00F, -22.00F, 24.00F, 24.00F, -26.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, -10.00F, -8.00F, 10.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.450F, 0.720F, 1.180F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -6.00F, 8.00F, -6.00F, 10.00F, -24.00F, 18.00F, 12.00F, 0.00F}, new float[]{0.00F, 14.00F, -14.00F, -14.00F, 16.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.720F, 1.450F, 2.050F, 2.380F, 3.600F}, new float[]{0.00F, 6.00F, 6.00F, -16.00F, 12.00F, 0.00F}, new float[]{0.00F, -12.00F, 12.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.720F, 1.450F, 2.050F, 2.400F, 3.000F, 3.600F}, new float[]{0.00F, 30.00F, 34.00F, 46.00F, 42.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.450F, 0.720F, 1.250F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -76.00F, 38.00F, -16.00F, -86.00F, 26.00F, 22.00F, 0.00F}, new float[]{0.00F, 14.00F, -26.00F, 0.00F, 12.00F, -10.00F, -6.00F, 0.00F}, new float[]{0.00F, -26.00F, 16.00F, -10.00F, -24.00F, 10.00F, 6.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.450F, 0.720F, 2.050F, 2.380F, 3.600F}, new float[]{0.00F, -40.00F, 10.00F, -44.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -10.00F, 6.00F, -12.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.720F, 1.180F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -18.00F, -78.00F, 38.00F, -86.00F, 26.00F, 22.00F, 0.00F}, new float[]{0.00F, 0.00F, -14.00F, 26.00F, -12.00F, 10.00F, 6.00F, 0.00F}, new float[]{0.00F, 12.00F, 26.00F, -16.00F, 24.00F, -10.00F, -6.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 1.180F, 1.450F, 2.050F, 2.380F, 3.600F}, new float[]{0.00F, -40.00F, 10.00F, -44.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 10.00F, -6.00F, 12.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.720F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -10.00F, -12.00F, 24.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.00F, 4.00F, 8.00F, 6.00F, 4.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.720F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, 12.00F, 6.00F, 20.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.720F, 1.450F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, -10.00F, -12.00F, 24.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -4.00F, -4.00F, -8.00F, -6.00F, -4.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.720F, 2.050F, 2.380F, 2.850F, 3.600F}, new float[]{0.00F, 12.00F, 6.00F, 20.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.720F, 1.450F, 2.050F, 2.420F, 3.600F}, new float[]{0.00F, 4.00F, 4.00F, 24.00F, 14.00F, 0.00F}, new float[]{0.00F, -18.00F, 18.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.820F, 1.550F, 2.100F, 2.520F, 3.600F}, new float[]{0.00F, 5.00F, 5.00F, 10.00F, 16.00F, 0.00F}, new float[]{0.00F, -22.00F, 22.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.920F, 1.650F, 2.150F, 2.620F, 3.600F}, new float[]{0.00F, 6.00F, 6.00F, 6.00F, 18.00F, 0.00F}, new float[]{0.00F, -26.00F, 26.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.020F, 1.750F, 2.200F, 2.720F, 3.600F}, new float[]{0.00F, 6.00F, 6.00F, 5.00F, 20.00F, 0.00F}, new float[]{0.00F, -28.00F, 28.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_whirlwind() {
        Clip c = new Clip("attack_whirlwind", 3.000F, false);
        c.pos("body", new float[]{0.000F, 0.550F, 1.250F, 2.050F, 3.000F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -2.50F, -1.50F, -2.20F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("body", new float[]{0.000F, 0.550F, 1.100F, 1.750F, 2.150F, 2.550F, 3.000F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -52.00F, 145.00F, 325.00F, 376.00F, 356.00F, 360.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 8.00F, 10.00F, 6.00F, 0.00F}, new float[]{0.00F, -16.00F, 18.00F, 10.00F, 0.00F}, new float[]{0.00F, -8.00F, 12.00F, 6.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 8.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, -18.00F, 26.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 4.00F, 4.00F, 2.00F, 0.00F}, new float[]{0.00F, -20.00F, 24.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.650F, 1.750F, 2.400F, 3.000F}, new float[]{0.00F, 28.00F, 34.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, -20.00F, 18.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 18.00F, 28.00F, 16.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, -28.00F, -32.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 16.00F, -16.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -18.00F, -26.00F, -16.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, -24.00F, -30.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, -12.00F, 14.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, 10.00F, 6.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 18.00F, 12.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 12.00F, -14.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, -10.00F, -6.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.550F, 1.250F, 2.150F, 3.000F}, new float[]{0.00F, 16.00F, 14.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.550F, 1.150F, 1.950F, 2.350F, 3.000F}, new float[]{0.00F, 6.00F, 10.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, 24.00F, -28.00F, -24.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.600F, 1.250F, 2.050F, 2.450F, 3.000F}, new float[]{0.00F, 4.00F, 8.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, 28.00F, -34.00F, -28.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.650F, 1.350F, 2.150F, 2.550F, 3.000F}, new float[]{0.00F, 4.00F, 6.00F, 4.00F, 3.00F, 0.00F}, new float[]{0.00F, 32.00F, -38.00F, -32.00F, 20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.700F, 1.450F, 2.250F, 2.650F, 3.000F}, new float[]{0.00F, 4.00F, 4.00F, 4.00F, 2.00F, 0.00F}, new float[]{0.00F, 36.00F, -42.00F, -34.00F, 22.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_burrow() {
        Clip c = new Clip("attack_burrow", 4.400F, false);
        c.pos("body", new float[]{0.000F, 0.450F, 0.950F, 1.450F, 2.650F, 3.250F, 3.750F, 4.050F, 4.400F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 3.50F, -8.00F, -44.00F, -44.00F, 10.00F, -3.00F, -0.80F, 0.00F}, new float[]{0.00F, 1.00F, -3.00F, -4.00F, 0.00F, -3.00F, -5.00F, -1.00F, 0.00F});
        c.rot("body", new float[]{0.000F, 0.450F, 0.950F, 1.450F, 2.650F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, -16.00F, 44.00F, 54.00F, -44.00F, -28.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 16.00F, -22.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 12.00F, -16.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.850F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 22.00F, 46.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.550F, 0.800F, 1.050F, 1.300F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, -52.00F, 36.00F, -48.00F, 36.00F, -54.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, -12.00F, 0.00F, 0.00F}, new float[]{0.00F, 16.00F, 8.00F, 14.00F, 8.00F, 30.00F, 8.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.550F, 0.800F, 1.050F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, -36.00F, 10.00F, -36.00F, -36.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 10.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.550F, 0.800F, 1.050F, 1.300F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 30.00F, -52.00F, 36.00F, -48.00F, -54.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 12.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, -16.00F, -8.00F, -14.00F, -30.00F, -8.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.800F, 1.050F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, -36.00F, 10.00F, -36.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, -10.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 28.00F, 24.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 6.00F, 10.00F, 6.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 22.00F, 16.00F, 20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 28.00F, 24.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -6.00F, -10.00F, -6.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.950F, 3.250F, 3.750F, 4.400F}, new float[]{0.00F, 22.00F, 16.00F, 20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.950F, 3.250F, 3.850F, 4.400F}, new float[]{0.00F, -14.00F, 18.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 1.050F, 3.350F, 3.950F, 4.400F}, new float[]{0.00F, -16.00F, 14.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 1.150F, 3.450F, 4.050F, 4.400F}, new float[]{0.00F, -18.00F, 10.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.250F, 3.550F, 4.150F, 4.400F}, new float[]{0.00F, -20.00F, 8.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_scattered_rocks() {
        Clip c = new Clip("attack_scattered_rocks", 3.200F, false);
        c.pos("body", new float[]{0.000F, 0.750F, 1.180F, 1.750F, 2.150F, 3.200F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -2.00F, -2.60F, -1.50F, -2.20F, 0.00F}, new float[]{0.00F, -1.50F, -2.00F, -1.00F, -1.50F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.750F, 1.180F, 1.750F, 2.150F, 3.200F}, new float[]{0.00F, 14.00F, 18.00F, 12.00F, 16.00F, 0.00F}, new float[]{0.00F, -16.00F, 18.00F, 14.00F, -18.00F, 0.00F}, new float[]{0.00F, 6.00F, -8.00F, -6.00F, 8.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -12.00F, -10.00F, -10.00F, 0.00F}, new float[]{0.00F, 12.00F, -12.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -8.00F, -6.00F, -6.00F, 0.00F}, new float[]{0.00F, 14.00F, -14.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, 32.00F, 30.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -18.00F, -22.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 10.00F, 14.00F, 12.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -16.00F, -20.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -18.00F, -22.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -10.00F, -14.00F, -12.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -16.00F, -20.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -14.00F, -10.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 6.00F, 8.00F, 6.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, 18.00F, 14.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, -14.00F, -10.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -6.00F, -8.00F, -6.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.750F, 1.180F, 2.150F, 3.200F}, new float[]{0.00F, 18.00F, 14.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.750F, 1.180F, 1.750F, 2.150F, 3.200F}, new float[]{0.00F, 34.00F, -14.00F, 28.00F, -14.00F, 0.00F}, new float[]{0.00F, 24.00F, -28.00F, -22.00F, 28.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.820F, 1.240F, 1.820F, 2.220F, 3.200F}, new float[]{0.00F, 26.00F, -10.00F, 22.00F, -10.00F, 0.00F}, new float[]{0.00F, 28.00F, -34.00F, -26.00F, 34.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.880F, 1.300F, 1.880F, 2.280F, 3.200F}, new float[]{0.00F, 20.00F, -8.00F, 18.00F, -8.00F, 0.00F}, new float[]{0.00F, 32.00F, -38.00F, -30.00F, 38.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.940F, 1.360F, 1.940F, 2.340F, 3.200F}, new float[]{0.00F, 16.00F, -6.00F, 14.00F, -6.00F, 0.00F}, new float[]{0.00F, 36.00F, -42.00F, -34.00F, 42.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_elemental_breath() {
        Clip c = new Clip("attack_elemental_breath", 4.200F, false);
        c.pos("body", new float[]{0.000F, 0.850F, 1.150F, 3.100F, 4.200F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -2.20F, -1.60F, -1.80F, 0.00F}, new float[]{0.00F, 2.50F, 3.80F, 3.50F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.850F, 1.150F, 1.800F, 2.500F, 3.100F, 4.200F}, new float[]{0.00F, -12.00F, -6.00F, -4.00F, -4.00F, -6.00F, 0.00F}, new float[]{0.00F, 0.00F, 16.00F, 4.00F, -16.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -4.00F, -1.00F, 4.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.850F, 1.150F, 2.150F, 3.050F, 3.550F, 4.200F}, new float[]{0.00F, -18.00F, 6.00F, 8.00F, 6.00F, -6.00F, 0.00F}, new float[]{0.00F, 0.00F, 24.00F, 0.00F, -24.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.850F, 1.150F, 2.150F, 3.050F, 3.600F, 4.200F}, new float[]{0.00F, -12.00F, 4.00F, 6.00F, 4.00F, -4.00F, 0.00F}, new float[]{0.00F, 0.00F, 18.00F, 0.00F, -18.00F, 6.00F, 0.00F}, new float[]{0.00F, 0.00F, 4.00F, 0.00F, -4.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.850F, 1.150F, 1.650F, 2.150F, 2.650F, 3.100F, 3.600F, 4.200F}, new float[]{0.00F, 14.00F, 46.00F, 42.00F, 48.00F, 42.00F, 44.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.850F, 1.150F, 3.100F, 4.200F}, new float[]{0.00F, -16.00F, -20.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 10.00F, 14.00F, 12.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.850F, 1.150F, 3.100F, 4.200F}, new float[]{0.00F, -18.00F, -22.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.850F, 1.150F, 3.100F, 4.200F}, new float[]{0.00F, -16.00F, -20.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -10.00F, -14.00F, -12.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.850F, 1.150F, 3.100F, 4.200F}, new float[]{0.00F, -18.00F, -22.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.850F, 3.100F, 4.200F}, new float[]{0.00F, 12.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 6.00F, 6.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.850F, 3.100F, 4.200F}, new float[]{0.00F, 14.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.850F, 3.100F, 4.200F}, new float[]{0.00F, 12.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -6.00F, -6.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.850F, 3.100F, 4.200F}, new float[]{0.00F, 14.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 1.150F, 2.150F, 3.050F, 4.200F}, new float[]{0.00F, 8.00F, 6.00F, 8.00F, 0.00F}, new float[]{0.00F, -14.00F, 0.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 1.250F, 2.250F, 3.150F, 4.200F}, new float[]{0.00F, 6.00F, 5.00F, 6.00F, 0.00F}, new float[]{0.00F, -18.00F, 0.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 1.350F, 2.350F, 3.250F, 4.200F}, new float[]{0.00F, 4.00F, 4.00F, 4.00F, 0.00F}, new float[]{0.00F, -22.00F, 0.00F, 22.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.450F, 2.450F, 3.350F, 4.200F}, new float[]{0.00F, 4.00F, 4.00F, 4.00F, 0.00F}, new float[]{0.00F, -26.00F, 0.00F, 26.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_primordial_shower() {
        Clip c = new Clip("attack_primordial_shower", 4.600F, false);
        c.pos("body", new float[]{0.000F, 0.750F, 1.250F, 1.950F, 2.320F, 2.650F, 3.450F, 4.600F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -3.40F, -3.80F, 5.40F, -3.60F, -2.80F, -2.40F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 1.20F, -2.50F, -2.00F, -1.60F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.750F, 1.000F, 1.250F, 1.950F, 2.320F, 2.600F, 2.900F, 3.200F, 3.550F, 4.600F}, new float[]{0.00F, 14.00F, 16.00F, 15.00F, -26.00F, 20.00F, 16.00F, 18.00F, 15.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 2.00F, -2.00F, 0.00F, 0.00F, 3.00F, -3.00F, 2.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 1.00F, -1.00F, 0.00F, 0.00F, 2.00F, -2.00F, 1.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 2.750F, 3.150F, 3.600F, 4.600F}, new float[]{0.00F, 22.00F, -28.00F, 14.00F, -16.00F, -18.00F, -8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 4.00F, -4.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 2.750F, 3.150F, 4.600F}, new float[]{0.00F, 14.00F, -18.00F, 10.00F, -14.00F, -14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 5.00F, -5.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 2.00F, -2.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 2.850F, 3.350F, 3.950F, 4.600F}, new float[]{0.00F, 12.00F, 44.00F, 50.00F, 46.00F, 48.00F, 14.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -18.00F, -84.00F, 24.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, -16.00F, 8.00F, 4.00F, 0.00F}, new float[]{0.00F, 16.00F, 32.00F, -10.00F, -6.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -26.00F, -46.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 12.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -18.00F, -84.00F, 24.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 16.00F, -8.00F, -4.00F, 0.00F}, new float[]{0.00F, -16.00F, -32.00F, 10.00F, 6.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -26.00F, -46.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -12.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -16.00F, 25.00F, -18.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, 10.00F, 8.00F, 6.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, 24.00F, 6.00F, 24.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, -16.00F, 25.00F, -18.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, -10.00F, -8.00F, -6.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 1.200F, 1.950F, 2.320F, 3.450F, 4.600F}, new float[]{0.00F, 24.00F, 6.00F, 24.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 1.200F, 1.950F, 2.400F, 3.450F, 4.600F}, new float[]{0.00F, 14.00F, 26.00F, 18.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 1.250F, 2.000F, 2.500F, 3.550F, 4.600F}, new float[]{0.00F, 12.00F, 12.00F, 20.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 1.300F, 2.050F, 2.600F, 3.650F, 4.600F}, new float[]{0.00F, 10.00F, 8.00F, 22.00F, 6.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.350F, 2.100F, 2.700F, 3.750F, 4.600F}, new float[]{0.00F, 8.00F, 6.00F, 24.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_attack_primeval_spring() {
        Clip c = new Clip("attack_primeval_spring", 3.400F, false);
        c.pos("body", new float[]{0.000F, 0.450F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -2.20F, 4.00F, -3.20F, -2.50F, 0.00F}, new float[]{0.00F, 0.50F, 0.80F, -2.40F, -1.80F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.450F, 0.880F, 1.180F, 1.650F, 2.200F, 3.400F}, new float[]{0.00F, 10.00F, -20.00F, 18.00F, 15.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -20.00F, 16.00F, 10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -14.00F, 12.00F, 6.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.880F, 1.220F, 2.200F, 3.400F}, new float[]{0.00F, 36.00F, 44.00F, 18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -78.00F, 26.00F, 20.00F, 0.00F}, new float[]{0.00F, -10.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, 24.00F, -8.00F, -4.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -42.00F, -14.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -78.00F, 26.00F, 20.00F, 0.00F}, new float[]{0.00F, 10.00F, -6.00F, -4.00F, 0.00F}, new float[]{0.00F, -24.00F, 8.00F, 4.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, -42.00F, -14.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, 19.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, 6.00F, 4.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, 6.00F, 22.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, 19.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, -6.00F, -4.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.880F, 1.180F, 2.100F, 3.400F}, new float[]{0.00F, 6.00F, 22.00F, 16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.880F, 1.280F, 2.200F, 3.400F}, new float[]{0.00F, 20.00F, 16.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.940F, 1.360F, 2.300F, 3.400F}, new float[]{0.00F, 10.00F, 18.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 1.000F, 1.440F, 2.400F, 3.400F}, new float[]{0.00F, 6.00F, 20.00F, 6.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 1.060F, 1.520F, 2.500F, 3.400F}, new float[]{0.00F, 4.00F, 22.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_countered_stagger() {
        Clip c = new Clip("countered_stagger", 3.600F, false);
        c.pos("body", new float[]{0.000F, 0.350F, 0.850F, 1.100F, 1.300F, 2.550F, 3.100F, 3.600F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.20F, -3.40F, -2.70F, -3.30F, -3.00F, -2.00F, 0.00F}, new float[]{0.00F, 5.50F, 7.00F, 7.00F, 7.00F, 6.50F, 2.50F, 0.00F});
        c.rot("body", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.600F}, new float[]{0.00F, -22.00F, 8.00F, 6.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 6.00F, 14.00F, 12.00F, 0.00F});
        c.rot("torso", new float[]{0.000F, 0.350F, 0.850F, 1.700F, 2.550F, 3.600F}, new float[]{0.00F, -12.00F, 10.00F, 13.00F, 8.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("neck", new float[]{0.000F, 0.350F, 0.850F, 1.450F, 2.100F, 2.750F, 3.600F}, new float[]{0.00F, -24.00F, 22.00F, 20.00F, 22.00F, 12.00F, 0.00F}, new float[]{0.00F, 0.00F, 10.00F, -14.00F, 12.00F, -6.00F, 0.00F}, new float[]{0.00F, 0.00F, 6.00F, -6.00F, 5.00F, 0.00F, 0.00F});
        c.rot("head", new float[]{0.000F, 0.350F, 0.850F, 1.500F, 2.150F, 3.000F, 3.600F}, new float[]{0.00F, -18.00F, 14.00F, 12.00F, 14.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, 14.00F, -18.00F, 14.00F, -10.00F, 0.00F}, new float[]{0.00F, 0.00F, 10.00F, -10.00F, 8.00F, 0.00F, 0.00F});
        c.rot("jaw", new float[]{0.000F, 0.350F, 0.900F, 1.800F, 2.600F, 3.600F}, new float[]{0.00F, 42.00F, 28.00F, 20.00F, 24.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.100F, 3.600F}, new float[]{0.00F, -64.00F, -24.00F, -20.00F, 12.00F, 0.00F}, new float[]{0.00F, -12.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 30.00F, 34.00F, 28.00F, 10.00F, 0.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.100F, 3.600F}, new float[]{0.00F, -32.00F, -42.00F, -38.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 12.00F, 10.00F, 0.00F, 0.00F});
        c.rot("arm_right", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.100F, 3.600F}, new float[]{0.00F, -58.00F, -16.00F, -14.00F, 12.00F, 0.00F}, new float[]{0.00F, 12.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -28.00F, -26.00F, -22.00F, -10.00F, 0.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.100F, 3.600F}, new float[]{0.00F, -28.00F, -38.00F, -34.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -10.00F, -8.00F, 0.00F, 0.00F});
        c.rot("leg_left", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.600F}, new float[]{0.00F, 18.00F, -26.00F, -24.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 10.00F, 22.00F, 18.00F, 0.00F});
        c.rot("shin_left", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.600F}, new float[]{0.00F, 12.00F, 36.00F, 32.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.600F}, new float[]{0.00F, 18.00F, -22.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -10.00F, -18.00F, -14.00F, 0.00F});
        c.rot("shin_right", new float[]{0.000F, 0.350F, 0.850F, 2.550F, 3.600F}, new float[]{0.00F, 12.00F, 32.00F, 28.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.350F, 0.950F, 2.550F, 3.600F}, new float[]{0.00F, 20.00F, 6.00F, 4.00F, 0.00F}, new float[]{0.00F, 0.00F, -16.00F, -12.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_2", new float[]{0.000F, 0.450F, 1.050F, 2.550F, 3.600F}, new float[]{0.00F, 12.00F, 4.00F, 3.00F, 0.00F}, new float[]{0.00F, 0.00F, -20.00F, -16.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_3", new float[]{0.000F, 0.550F, 1.150F, 2.550F, 3.600F}, new float[]{0.00F, 8.00F, 2.00F, 2.00F, 0.00F}, new float[]{0.00F, 0.00F, -22.00F, -18.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_4", new float[]{0.000F, 0.650F, 1.250F, 2.550F, 3.600F}, new float[]{0.00F, 6.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -24.00F, -20.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        return c;
    }
    private static Clip build_death() {
        Clip c = new Clip("death", 3.600F, false);
        c.pos("body", new float[]{0.000F, 0.550F, 1.300F, 1.500F, 2.250F, 2.500F, 2.750F, 3.600F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 3.50F, 3.80F, 4.00F, 4.00F}, new float[]{0.00F, 3.60F, -3.20F, -2.50F, -3.80F, -3.20F, -3.60F, -3.60F}, new float[]{0.00F, 2.00F, 2.50F, 2.50F, 2.50F, 2.50F, 2.50F, 2.50F});
        c.rot("body", new float[]{0.000F, 0.550F, 1.300F, 2.250F, 2.500F, 2.750F, 3.600F}, new float[]{0.00F, -18.00F, 12.00F, 6.00F, 6.00F, 6.00F, 6.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 4.00F, 14.00F, 74.00F, 68.00F, 72.00F, 72.00F});
        c.rot("torso", new float[]{0.000F, 0.550F, 1.300F, 2.350F, 3.600F}, new float[]{0.00F, -10.00F, 10.00F, 4.00F, 4.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 6.00F, 6.00F});
        c.rot("neck", new float[]{0.000F, 0.550F, 1.300F, 2.400F, 2.650F, 3.600F}, new float[]{0.00F, -26.00F, 18.00F, 14.00F, 12.00F, 14.00F}, new float[]{0.00F, 0.00F, 8.00F, 16.00F, 14.00F, 16.00F}, new float[]{0.00F, 0.00F, 0.00F, -14.00F, -10.00F, -14.00F});
        c.rot("head", new float[]{0.000F, 0.550F, 1.300F, 2.500F, 2.750F, 3.600F}, new float[]{0.00F, -18.00F, 14.00F, 12.00F, 10.00F, 12.00F}, new float[]{0.00F, 0.00F, 12.00F, 18.00F, 16.00F, 18.00F}, new float[]{0.00F, 0.00F, 0.00F, -16.00F, -12.00F, -16.00F});
        c.rot("jaw", new float[]{0.000F, 0.550F, 1.300F, 2.500F, 3.600F}, new float[]{0.00F, 48.00F, 30.00F, 22.00F, 22.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("arm_left", new float[]{0.000F, 0.550F, 1.300F, 2.350F, 2.650F, 3.600F}, new float[]{0.00F, -54.00F, -26.00F, -18.00F, -22.00F, -20.00F}, new float[]{0.00F, -10.00F, 0.00F, 12.00F, 10.00F, 10.00F}, new float[]{0.00F, 24.00F, 32.00F, 42.00F, 36.00F, 38.00F});
        c.rot("forearm_left", new float[]{0.000F, 0.550F, 1.300F, 2.450F, 3.600F}, new float[]{0.00F, -36.00F, -44.00F, -28.00F, -28.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 10.00F, 14.00F, 14.00F});
        c.rot("arm_right", new float[]{0.000F, 0.550F, 1.300F, 2.350F, 3.600F}, new float[]{0.00F, -54.00F, -22.00F, -32.00F, -32.00F}, new float[]{0.00F, 10.00F, 0.00F, -10.00F, -10.00F}, new float[]{0.00F, -24.00F, -28.00F, -16.00F, -16.00F});
        c.rot("forearm_right", new float[]{0.000F, 0.550F, 1.300F, 2.450F, 3.600F}, new float[]{0.00F, -36.00F, -40.00F, -24.00F, -24.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -8.00F, -12.00F, -12.00F});
        c.rot("leg_left", new float[]{0.000F, 0.550F, 1.300F, 2.350F, 3.600F}, new float[]{0.00F, 18.00F, -28.00F, -16.00F, -16.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 8.00F, 22.00F, 32.00F, 32.00F});
        c.rot("shin_left", new float[]{0.000F, 0.550F, 1.300F, 2.450F, 3.600F}, new float[]{0.00F, 8.00F, 40.00F, 28.00F, 28.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("leg_right", new float[]{0.000F, 0.550F, 1.300F, 2.350F, 3.600F}, new float[]{0.00F, 18.00F, -26.00F, -30.00F, -30.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, -8.00F, -20.00F, -12.00F, -12.00F});
        c.rot("shin_right", new float[]{0.000F, 0.550F, 1.300F, 2.450F, 3.600F}, new float[]{0.00F, 8.00F, 38.00F, 24.00F, 24.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, 0.00F, 0.00F, 0.00F});
        c.rot("tail_1", new float[]{0.000F, 0.550F, 1.350F, 2.450F, 3.600F}, new float[]{0.00F, 20.00F, 6.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -14.00F, -18.00F, -18.00F}, new float[]{0.00F, 0.00F, 0.00F, -12.00F, -12.00F});
        c.rot("tail_2", new float[]{0.000F, 0.650F, 1.450F, 2.550F, 3.600F}, new float[]{0.00F, 10.00F, 4.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -20.00F, -24.00F, -24.00F}, new float[]{0.00F, 0.00F, 0.00F, -10.00F, -10.00F});
        c.rot("tail_3", new float[]{0.000F, 0.750F, 1.550F, 2.650F, 3.600F}, new float[]{0.00F, 6.00F, 2.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -26.00F, -28.00F, -28.00F}, new float[]{0.00F, 0.00F, 0.00F, -8.00F, -8.00F});
        c.rot("tail_4", new float[]{0.000F, 0.850F, 1.650F, 2.750F, 3.600F}, new float[]{0.00F, 4.00F, 0.00F, 0.00F, 0.00F}, new float[]{0.00F, 0.00F, -30.00F, -32.00F, -32.00F}, new float[]{0.00F, 0.00F, 0.00F, -6.00F, -6.00F});
        return c;
    }
}
