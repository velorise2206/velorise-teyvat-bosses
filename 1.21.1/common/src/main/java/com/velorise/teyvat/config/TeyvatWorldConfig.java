package com.velorise.teyvat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lightweight loader-agnostic JSON config ({@code config/velorise_teyvat.json}).
 * {@link #defaultWorldScale} defaults to {@code 1.0} (true 1m = 1 block Mondstadt scale).
 * Players who want faster exploration can set {@code 0.5} (1:2) or {@code 0.25} (1:4)
 * before creating a new world; each world save locks its own scale in SavedData.
 */
public final class TeyvatWorldConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile TeyvatWorldConfig instance = new TeyvatWorldConfig();

    public double defaultWorldScale = 1.0;
    public boolean enableLockedRegionBarrier = true;

    public static TeyvatWorldConfig get() {
        return instance;
    }

    public double clampedScale() {
        return Math.max(0.25, Math.min(1.0, defaultWorldScale));
    }

    public static void loadOrCreate(Path configDir) {
        Path file = configDir.resolve("velorise_teyvat.json");
        try {
            if (Files.exists(file)) {
                TeyvatWorldConfig loaded = GSON.fromJson(Files.readString(file), TeyvatWorldConfig.class);
                if (loaded != null) {
                    loaded.defaultWorldScale = loaded.clampedScale();
                    instance = loaded;
                    return;
                }
            }
            Files.createDirectories(configDir);
            Files.writeString(file, GSON.toJson(instance));
        } catch (IOException ignored) {
        }
    }
}
