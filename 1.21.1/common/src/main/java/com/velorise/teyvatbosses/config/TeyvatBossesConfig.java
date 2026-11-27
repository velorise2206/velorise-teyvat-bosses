package com.velorise.teyvatbosses.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velorise.teyvatbosses.TeyvatBossesConstants;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader-agnostic JSON configuration for modpack authors and server admins.
 * Stored at {@code config/velorise_teyvat_bosses.json} on both Fabric and NeoForge.
 */
public final class TeyvatBossesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", TeyvatBossesConstants.MOD_ID + ".json");

    private static volatile TeyvatBossesConfig instance = new TeyvatBossesConfig();

    // Damage Cap & Anti-Cheese Settings (Cataclysm-inspired)
    public boolean enableDamageCap = true;
    public boolean enableRangeCap = true;
    public double rangeCapFalloffStart = 22.0D;
    public double rangeCapImmuneDistance = 30.0D;
    public boolean enableAntiMobArmyCap = true;
    public float softPerHitCapRatio = 0.06F;
    public float hardPerHitCapRatio = 0.10F;
    public float dpsCapPerSecondRatio = 0.15F;

    // World & Visual Polish Settings
    public boolean enableBossBlockBreaking = true;
    public boolean enableCameraShake = true;
    public boolean enableHitstop = true;
    public int locatorSearchRadiusChunks = 128;

    public static TeyvatBossesConfig get() {
        return instance;
    }

    public static synchronized void load() {
        try {
            Path parent = CONFIG_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);

            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                    TeyvatBossesConfig loaded = GSON.fromJson(reader, TeyvatBossesConfig.class);
                    if (loaded != null) {
                        loaded.sanitize();
                        instance = loaded;
                    }
                }
            }
            save();
        } catch (Throwable t) {
            TeyvatBossesConstants.LOGGER.warn("Failed to load {}, using defaults: {}", CONFIG_PATH, t.getMessage());
        }
    }

    public static synchronized void save() {
        try {
            Path parent = CONFIG_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(instance, writer);
            }
        } catch (Throwable t) {
            TeyvatBossesConstants.LOGGER.warn("Failed to save {}: {}", CONFIG_PATH, t.getMessage());
        }
    }

    private void sanitize() {
        rangeCapFalloffStart = Math.max(8.0D, Math.min(128.0D, rangeCapFalloffStart));
        rangeCapImmuneDistance = Math.max(rangeCapFalloffStart + 2.0D, Math.min(160.0D, rangeCapImmuneDistance));
        softPerHitCapRatio = Math.max(0.01F, Math.min(1.0F, softPerHitCapRatio));
        hardPerHitCapRatio = Math.max(softPerHitCapRatio, Math.min(1.0F, hardPerHitCapRatio));
        dpsCapPerSecondRatio = Math.max(0.03F, Math.min(2.0F, dpsCapPerSecondRatio));
        locatorSearchRadiusChunks = Math.max(16, Math.min(256, locatorSearchRadiusChunks));
    }
}
