package com.velorise.teyvat.worldgen.density;

import com.mojang.serialization.MapCodec;
import com.velorise.teyvat.worldgen.region.RegionMacroMap;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * True 3D DensityFunction for {@code veloriseteyvat:teyvat}.
 *
 * <p>Combines:
 * <ol>
 *   <li>Bilinear {@link RegionMacroMap} base elevation ({@code TargetY})</li>
 *   <li>Stepped {@code smoothstep} terrain terraces tailored for 3D Wall Climbing &amp; Wind Glider</li>
 *   <li>3D domain-warped overhang noise (for Starsnatch Cliff, Jueyun Karst, Stormterror's Lair)</li>
 *   <li>3D cave carving modulated by {@code Sculpt3DMask} (zeroed out on Structure Pads)</li>
 * </ol>
 */
public final class TeyvatDensityFunction implements DensityFunction.SimpleFunction {
    public static final TeyvatDensityFunction INSTANCE = new TeyvatDensityFunction();
    public static final MapCodec<TeyvatDensityFunction> CODEC = MapCodec.unit(INSTANCE);
    public static final KeyDispatchDataCodec<TeyvatDensityFunction> KEY_CODEC =
            KeyDispatchDataCodec.of(CODEC);

    private TeyvatDensityFunction() {}

    @Override
    public double compute(FunctionContext ctx) {
        int x = ctx.blockX();
        int y = ctx.blockY();
        int z = ctx.blockZ();

        RegionMacroMap map = RegionMacroMap.getInstance();
        double targetY = map.sampleTargetY(x, z);
        double sculpt = map.sampleSculptMask(x, z); // 0 = flat pad, 1..127 = cave, 128..255 = overhang

        // Smooth terraced cliffs above sea level (Y > 68) except on Structure Pads
        double padFactor = clamp01(sculpt / 24.0);
        if (targetY > 68.0 && padFactor > 0.01) {
            double rel = (targetY - 68.0) / 14.0;
            double step = Math.floor(rel) + smoothstep(0.15, 0.85, rel - Math.floor(rel));
            double terraced = 68.0 + step * 14.0;
            targetY = targetY + (terraced - targetY) * (0.45 * padFactor);
        }

        // Base column density (> 0 solid, < 0 air)
        double density = (targetY - y) * 0.085;

        // 3D Domain-Warped Overhangs (active when sculpt > 128)
        if (sculpt > 128.0 && y > 48 && y < 260) {
            double overhangWeight = (sculpt - 128.0) / 127.0;
            double wx = trigNoise3D(x * 0.022, y * 0.030, z * 0.022) * 14.0;
            double wz = trigNoise3D(x * 0.022 + 31.7, y * 0.030, z * 0.022 - 19.3) * 14.0;
            double warpedY = map.sampleTargetY(x + wx, z + wz);
            double overhangDelta = (warpedY - targetY) * 0.045 * overhangWeight;
            density += overhangDelta;
        }

        // 3D Cave Carving (active below surface and outside Structure Pads)
        if (padFactor > 0.2 && y < targetY - 6.0 && y > -118) {
            double caveWeight = sculpt <= 127.0 ? (sculpt / 127.0) : 0.45;
            double c1 = trigNoise3D(x * 0.032, y * 0.045, z * 0.032);
            double c2 = trigNoise3D(x * 0.064 + 17.1, y * 0.070 - 11.4, z * 0.064);
            double cavern = Math.abs(c1) * 0.65 + Math.abs(c2) * 0.35;
            if (cavern < 0.16 * caveWeight) {
                density -= (0.16 * caveWeight - cavern) * 8.5;
            }
        }

        return Math.max(-64.0, Math.min(64.0, density));
    }

    @Override
    public double minValue() {
        return -64.0;
    }

    @Override
    public double maxValue() {
        return 64.0;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return KEY_CODEC;
    }

    private static double trigNoise3D(double x, double y, double z) {
        return Math.sin(x) * Math.cos(z) * 0.55
                + Math.sin(x * 1.73 - y * 1.29 + z * 0.87) * 0.30
                + Math.cos(x * 2.91 + y * 2.13 - z * 2.47) * 0.15;
    }

    private static double smoothstep(double e0, double e1, double x) {
        double t = clamp01((x - e0) / (e1 - e0));
        return t * t * (3.0 - 2.0 * t);
    }

    private static double clamp01(double v) {
        return v < 0.0 ? 0.0 : Math.min(v, 1.0);
    }
}
