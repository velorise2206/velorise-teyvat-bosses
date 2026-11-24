package com.velorise.teyvat.worldgen.region;

import com.velorise.teyvat.worldgen.TeyvatDimensions;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.InflaterInputStream;

/**
 * Loads and bilinearly samples {@code data/veloriseteyvat/worldgen/teyvat_macro.bin} (TVM1 format).
 * Supports per-save {@code worldScale} (default {@code 1.0} = 1m:1block; e.g. {@code 0.5} for 2x faster exploration).
 */
public final class RegionMacroMap {
    public static final int BASE_Y_OFFSET = 24;
    private static volatile RegionMacroMap instance;
    private static volatile double activeWorldScale = 1.0;

    private final int width;
    private final int height;
    private final int worldMinX;
    private final int worldMinZ;
    private final int blocksPerPixel;
    private final byte[] rgba;

    private RegionMacroMap(int width, int height, int worldMinX, int worldMinZ, int blocksPerPixel, byte[] rgba) {
        this.width = width;
        this.height = height;
        this.worldMinX = worldMinX;
        this.worldMinZ = worldMinZ;
        this.blocksPerPixel = blocksPerPixel;
        this.rgba = rgba;
    }

    public static void setActiveWorldScale(double scale) {
        activeWorldScale = Math.max(0.25, Math.min(1.0, scale));
    }

    public static double getActiveWorldScale() {
        return activeWorldScale;
    }

    public static RegionMacroMap getInstance() {
        RegionMacroMap current = instance;
        if (current != null) {
            return current;
        }
        synchronized (RegionMacroMap.class) {
            if (instance == null) {
                String cp = "/data/" + TeyvatDimensions.NAMESPACE + "/" + TeyvatDimensions.MACRO_MAP_LOCATION.getPath();
                try (InputStream in = RegionMacroMap.class.getResourceAsStream(cp)) {
                    if (in == null) {
                        throw new IllegalStateException("Missing macro-map resource on classpath: " + cp);
                    }
                    instance = load(in);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load Teyvat macro-map: " + cp, e);
                }
            }
            return instance;
        }
    }

    public static RegionMacroMap load(InputStream rawIn) throws IOException {
        DataInputStream dis = new DataInputStream(rawIn);
        byte[] magic = new byte[4];
        dis.readFully(magic);
        String header = new String(magic, StandardCharsets.US_ASCII);
        if (!"TVM1".equals(header)) {
            throw new IOException("Invalid Teyvat macro-map magic: " + header);
        }
        int width = dis.readInt();
        int height = dis.readInt();
        int minX = dis.readInt();
        int minZ = dis.readInt();
        int bpp = dis.readInt();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(width * height * 4);
        try (InflaterInputStream inflater = new InflaterInputStream(dis)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = inflater.read(buf)) > 0) {
                baos.write(buf, 0, n);
            }
        }
        byte[] rgba = baos.toByteArray();
        if (rgba.length != width * height * 4) {
            throw new IOException("Unexpected TVM1 payload size: " + rgba.length);
        }
        return new RegionMacroMap(width, height, minX, minZ, bpp, rgba);
    }

    private double toCanonicalX(double blockX) {
        return blockX / activeWorldScale;
    }

    private double toCanonicalZ(double blockZ) {
        return blockZ / activeWorldScale;
    }

    public boolean isInsideWorldBounds(int blockX, int blockZ) {
        double cx = toCanonicalX(blockX);
        double cz = toCanonicalZ(blockZ);
        int maxX = worldMinX + width * blocksPerPixel;
        int maxZ = worldMinZ + height * blocksPerPixel;
        return cx >= worldMinX && cx < maxX && cz >= worldMinZ && cz < maxZ;
    }

    public double sampleTargetY(double blockX, double blockZ) {
        double fx = (toCanonicalX(blockX) - worldMinX) / blocksPerPixel - 0.5;
        double fz = (toCanonicalZ(blockZ) - worldMinZ) / blocksPerPixel - 0.5;
        int x0 = clamp((int) Math.floor(fx), 0, width - 1);
        int z0 = clamp((int) Math.floor(fz), 0, height - 1);
        int x1 = Math.min(x0 + 1, width - 1);
        int z1 = Math.min(z0 + 1, height - 1);
        double tx = clamp01(fx - x0);
        double tz = clamp01(fz - z0);

        double y00 = u8(x0, z0, 0);
        double y10 = u8(x1, z0, 0);
        double y01 = u8(x0, z1, 0);
        double y11 = u8(x1, z1, 0);

        double top = y00 + (y10 - y00) * tx;
        double bot = y01 + (y11 - y01) * tx;
        return BASE_Y_OFFSET + top + (bot - top) * tz;
    }

    public TeyvatRegion sampleRegion(int blockX, int blockZ) {
        if (!isInsideWorldBounds(blockX, blockZ)) {
            return TeyvatRegion.DARK_SEA;
        }
        int px = clamp((int) ((toCanonicalX(blockX) - worldMinX) / blocksPerPixel), 0, width - 1);
        int pz = clamp((int) ((toCanonicalZ(blockZ) - worldMinZ) / blocksPerPixel), 0, height - 1);
        return TeyvatRegion.byId((u8(px, pz, 1) >> 4) & 0x0F);
    }

    public int sampleSubBiomeId(int blockX, int blockZ) {
        if (!isInsideWorldBounds(blockX, blockZ)) {
            return 0;
        }
        int px = clamp((int) ((toCanonicalX(blockX) - worldMinX) / blocksPerPixel), 0, width - 1);
        int pz = clamp((int) ((toCanonicalZ(blockZ) - worldMinZ) / blocksPerPixel), 0, height - 1);
        return u8(px, pz, 1) & 0x0F;
    }

    public double sampleSculptMask(double blockX, double blockZ) {
        double fx = (toCanonicalX(blockX) - worldMinX) / blocksPerPixel - 0.5;
        double fz = (toCanonicalZ(blockZ) - worldMinZ) / blocksPerPixel - 0.5;
        int x0 = clamp((int) Math.floor(fx), 0, width - 1);
        int z0 = clamp((int) Math.floor(fz), 0, height - 1);
        int x1 = Math.min(x0 + 1, width - 1);
        int z1 = Math.min(z0 + 1, height - 1);
        double tx = clamp01(fx - x0);
        double tz = clamp01(fz - z0);

        double b00 = u8(x0, z0, 2);
        double b10 = u8(x1, z0, 2);
        double b01 = u8(x0, z1, 2);
        double b11 = u8(x1, z1, 2);

        double top = b00 + (b10 - b00) * tx;
        double bot = b01 + (b11 - b01) * tx;
        return top + (bot - top) * tz;
    }

    /** Channel A: >= 160 indicates extracted cream road; >= 200 indicates Fontaine elevated water. */
    public int sampleWaterRoadMask(int blockX, int blockZ) {
        if (!isInsideWorldBounds(blockX, blockZ)) {
            return 0;
        }
        int px = clamp((int) ((toCanonicalX(blockX) - worldMinX) / blocksPerPixel), 0, width - 1);
        int pz = clamp((int) ((toCanonicalZ(blockZ) - worldMinZ) / blocksPerPixel), 0, height - 1);
        return u8(px, pz, 3);
    }

    private int u8(int x, int z, int channel) {
        return rgba[(z * width + x) * 4 + channel] & 0xFF;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double clamp01(double v) {
        return v < 0.0 ? 0.0 : Math.min(v, 1.0);
    }
}
