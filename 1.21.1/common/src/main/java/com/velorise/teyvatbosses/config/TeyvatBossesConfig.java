package com.velorise.teyvatbosses.config;

import com.google.gson.Gson;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Loader-agnostic TOML configuration for modpack authors and server admins.
 * Stored at {@code config/velorise_teyvat_bosses.toml} on both Fabric 1.21.1 and NeoForge 1.21.1
 * (with automatic one-time migration from legacy {@code velorise_teyvat_bosses.json} if present).
 */
public final class TeyvatBossesConfig {
    private static final Path CONFIG_PATH = Path.of("config", TeyvatBossesConstants.MOD_ID + ".toml");
    private static final Path LEGACY_JSON_PATH = Path.of("config", TeyvatBossesConstants.MOD_ID + ".json");
    private static final ResourceLocation HEALTH_MOD_ID = TeyvatBossesConstants.id("config_health_scale");
    private static final ResourceLocation DAMAGE_MOD_ID = TeyvatBossesConstants.id("config_damage_scale");

    private static volatile TeyvatBossesConfig instance = new TeyvatBossesConfig();

    // [damage_cap] — Cataclysm-inspired anti-cheese & damage caps
    public boolean enableDamageCap = true;
    public boolean enableRangeCap = true;
    public double rangeCapFalloffStart = 22.0D;
    public double rangeCapImmuneDistance = 30.0D;
    public boolean enableAntiMobArmyCap = true;
    public float softPerHitCapRatio = 0.06F;
    public float hardPerHitCapRatio = 0.10F;
    public float dpsCapPerSecondRatio = 0.15F;

    // [world_and_visuals]
    public boolean enableBossBlockBreaking = true;
    public boolean enableCameraShake = true;
    public boolean enableHitstop = true;
    public int locatorSearchRadiusChunks = 128;

    // [global_scaling] — Global boss stat multipliers for modpacks (Apotheosis / Iron's Spells / etc.)
    public double globalHealthMultiplier = 1.0D;
    public double globalDamageMultiplier = 1.0D;

    // [per_boss_multipliers] — Fine-grained per-boss HP & Damage multipliers
    public double cryoRegisvineHealthMultiplier = 1.0D;
    public double cryoRegisvineDamageMultiplier = 1.0D;
    public double anemoHypostasisHealthMultiplier = 1.0D;
    public double anemoHypostasisDamageMultiplier = 1.0D;
    public double ruinGuardHealthMultiplier = 1.0D;
    public double ruinGuardDamageMultiplier = 1.0D;
    public double ruinNautilusHealthMultiplier = 1.0D;
    public double ruinNautilusDamageMultiplier = 1.0D;
    public double primoGeovishapHealthMultiplier = 1.0D;
    public double primoGeovishapDamageMultiplier = 1.0D;

    public static TeyvatBossesConfig get() {
        return instance;
    }

    public double getHealthMultiplier(String bossId) {
        double perBoss = switch (bossId) {
            case "cryo_regisvine" -> cryoRegisvineHealthMultiplier;
            case "anemo_hypostasis" -> anemoHypostasisHealthMultiplier;
            case "ruin_guard" -> ruinGuardHealthMultiplier;
            case "ruin_nautilus" -> ruinNautilusHealthMultiplier;
            case "primo_geovishap" -> primoGeovishapHealthMultiplier;
            default -> 1.0D;
        };
        return clampDouble(globalHealthMultiplier * perBoss, 0.1D, 50.0D);
    }

    public double getDamageMultiplier(String bossId) {
        double perBoss = switch (bossId) {
            case "cryo_regisvine" -> cryoRegisvineDamageMultiplier;
            case "anemo_hypostasis" -> anemoHypostasisDamageMultiplier;
            case "ruin_guard" -> ruinGuardDamageMultiplier;
            case "ruin_nautilus" -> ruinNautilusDamageMultiplier;
            case "primo_geovishap" -> primoGeovishapDamageMultiplier;
            default -> 1.0D;
        };
        return clampDouble(globalDamageMultiplier * perBoss, 0.1D, 50.0D);
    }

    /** Scales custom ability/projectile damage by global * per-boss damage multiplier. */
    public static float scaleDamage(String bossId, float baseDamage) {
        return (float) (baseDamage * get().getDamageMultiplier(bossId));
    }

    /**
     * Reload-safe attribute scaling for boss Max Health and Attack Damage using deterministic modifiers.
     * Safe to invoke in constructors or spawn hooks without stacking across chunk reloads.
     */
    public static void applyBossScaling(LivingEntity boss, String bossId) {
        if (boss == null || boss.level().isClientSide()) return;
        TeyvatBossesConfig cfg = get();

        double hpMult = cfg.getHealthMultiplier(bossId);
        AttributeInstance maxHpAttr = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHpAttr != null) {
            boolean wasFull = boss.getHealth() >= boss.getMaxHealth() - 0.5F;
            maxHpAttr.removeModifier(HEALTH_MOD_ID);
            if (Math.abs(hpMult - 1.0D) > 0.001D) {
                maxHpAttr.addPermanentModifier(new AttributeModifier(
                        HEALTH_MOD_ID, hpMult - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            if (wasFull || boss.getHealth() > boss.getMaxHealth()) {
                boss.setHealth(boss.getMaxHealth());
            }
        }

        double dmgMult = cfg.getDamageMultiplier(bossId);
        AttributeInstance atkAttr = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atkAttr != null) {
            atkAttr.removeModifier(DAMAGE_MOD_ID);
            if (Math.abs(dmgMult - 1.0D) > 0.001D) {
                atkAttr.addPermanentModifier(new AttributeModifier(
                        DAMAGE_MOD_ID, dmgMult - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    public static synchronized void load() {
        try {
            Path parent = CONFIG_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);

            TeyvatBossesConfig loaded = new TeyvatBossesConfig();
            if (Files.exists(CONFIG_PATH)) {
                parseToml(CONFIG_PATH, loaded);
            } else if (Files.exists(LEGACY_JSON_PATH)) {
                try (Reader reader = Files.newBufferedReader(LEGACY_JSON_PATH, StandardCharsets.UTF_8)) {
                    TeyvatBossesConfig migrated = new Gson().fromJson(reader, TeyvatBossesConfig.class);
                    if (migrated != null) loaded = migrated;
                }
            }
            loaded.sanitize();
            instance = loaded;
            save();
        } catch (Throwable t) {
            TeyvatBossesConstants.LOGGER.warn("Failed to load {}, using defaults: {}", CONFIG_PATH, t.getMessage());
        }
    }

    private static void parseToml(Path path, TeyvatBossesConfig cfg) throws Exception {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String rawLine;
            while ((rawLine = br.readLine()) != null) {
                int hash = rawLine.indexOf('#');
                String line = (hash >= 0 ? rawLine.substring(0, hash) : rawLine).trim();
                if (line.isEmpty() || line.startsWith("[")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                try {
                    switch (key) {
                        case "enableDamageCap" -> cfg.enableDamageCap = Boolean.parseBoolean(val);
                        case "enableRangeCap" -> cfg.enableRangeCap = Boolean.parseBoolean(val);
                        case "rangeCapFalloffStart" -> cfg.rangeCapFalloffStart = Double.parseDouble(val);
                        case "rangeCapImmuneDistance" -> cfg.rangeCapImmuneDistance = Double.parseDouble(val);
                        case "enableAntiMobArmyCap" -> cfg.enableAntiMobArmyCap = Boolean.parseBoolean(val);
                        case "softPerHitCapRatio" -> cfg.softPerHitCapRatio = Float.parseFloat(val);
                        case "hardPerHitCapRatio" -> cfg.hardPerHitCapRatio = Float.parseFloat(val);
                        case "dpsCapPerSecondRatio" -> cfg.dpsCapPerSecondRatio = Float.parseFloat(val);
                        case "enableBossBlockBreaking" -> cfg.enableBossBlockBreaking = Boolean.parseBoolean(val);
                        case "enableCameraShake" -> cfg.enableCameraShake = Boolean.parseBoolean(val);
                        case "enableHitstop" -> cfg.enableHitstop = Boolean.parseBoolean(val);
                        case "locatorSearchRadiusChunks" -> cfg.locatorSearchRadiusChunks = Integer.parseInt(val);
                        case "globalHealthMultiplier" -> cfg.globalHealthMultiplier = Double.parseDouble(val);
                        case "globalDamageMultiplier" -> cfg.globalDamageMultiplier = Double.parseDouble(val);
                        case "cryoRegisvineHealthMultiplier" -> cfg.cryoRegisvineHealthMultiplier = Double.parseDouble(val);
                        case "cryoRegisvineDamageMultiplier" -> cfg.cryoRegisvineDamageMultiplier = Double.parseDouble(val);
                        case "anemoHypostasisHealthMultiplier" -> cfg.anemoHypostasisHealthMultiplier = Double.parseDouble(val);
                        case "anemoHypostasisDamageMultiplier" -> cfg.anemoHypostasisDamageMultiplier = Double.parseDouble(val);
                        case "ruinGuardHealthMultiplier" -> cfg.ruinGuardHealthMultiplier = Double.parseDouble(val);
                        case "ruinGuardDamageMultiplier" -> cfg.ruinGuardDamageMultiplier = Double.parseDouble(val);
                        case "ruinNautilusHealthMultiplier" -> cfg.ruinNautilusHealthMultiplier = Double.parseDouble(val);
                        case "ruinNautilusDamageMultiplier" -> cfg.ruinNautilusDamageMultiplier = Double.parseDouble(val);
                        case "primoGeovishapHealthMultiplier" -> cfg.primoGeovishapHealthMultiplier = Double.parseDouble(val);
                        case "primoGeovishapDamageMultiplier" -> cfg.primoGeovishapDamageMultiplier = Double.parseDouble(val);
                        default -> {}
                    }
                } catch (NumberFormatException ignored) {
                    TeyvatBossesConstants.LOGGER.warn("Ignoring invalid config value for {}: {}", key, val);
                }
            }
        }
    }

    public static synchronized void save() {
        try {
            Path parent = CONFIG_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);
            TeyvatBossesConfig c = instance;
            String toml = String.format(Locale.ROOT,
                    "# Velorise: Teyvat Bosses — Modpack & Server Configuration (Fabric + NeoForge 1.21.1)%n"
                    + "# Edit values below and restart the server/game to apply changes.%n%n"
                    + "[damage_cap]%n"
                    + "# Master toggle for Cataclysm-style per-hit & DPS caps on all 5 bosses%n"
                    + "enableDamageCap = %s%n"
                    + "# Reduce/nullify damage from players attacking far outside arena combat range%n"
                    + "enableRangeCap = %s%n"
                    + "# Distance (blocks) where ranged damage falloff starts (8.0 - 128.0)%n"
                    + "rangeCapFalloffStart = %.2f%n"
                    + "# Distance (blocks) where bosses become immune to sniping (falloffStart+2 - 160.0)%n"
                    + "rangeCapImmuneDistance = %.2f%n"
                    + "# Shared DPS cap against Iron Golem / summon armies%n"
                    + "enableAntiMobArmyCap = %s%n"
                    + "# Soft single-hit cap as fraction of Max HP (default 0.06 = 6%%)%n"
                    + "softPerHitCapRatio = %.4f%n"
                    + "# Hard single-hit cap as fraction of Max HP (default 0.10 = 10%%)%n"
                    + "hardPerHitCapRatio = %.4f%n"
                    + "# Rolling 20-tick (1s) DPS cap as fraction of Max HP (default 0.15 = 15%%)%n"
                    + "dpsCapPerSecondRatio = %.4f%n%n"
                    + "[world_and_visuals]%n"
                    + "# Allow Ruin Guard & Primo Geovishap to break obstructing blocks during combat%n"
                    + "enableBossBlockBreaking = %s%n"
                    + "# Enable client-side camera shake on heavy slams and footsteps%n"
                    + "enableCameraShake = %s%n"
                    + "# Enable client-side impact hitstop and boss pose freeze%n"
                    + "enableHitstop = %s%n"
                    + "# Boss Compass structure search radius in chunks (16 - 256)%n"
                    + "locatorSearchRadiusChunks = %d%n%n"
                    + "[global_scaling]%n"
                    + "# Global multipliers applied on top of per-boss multipliers (0.1 - 20.0)%n"
                    + "globalHealthMultiplier = %.2f%n"
                    + "globalDamageMultiplier = %.2f%n%n"
                    + "[per_boss_multipliers]%n"
                    + "# Per-boss Max Health & Damage multipliers (0.1 - 20.0)%n"
                    + "cryoRegisvineHealthMultiplier = %.2f%n"
                    + "cryoRegisvineDamageMultiplier = %.2f%n"
                    + "anemoHypostasisHealthMultiplier = %.2f%n"
                    + "anemoHypostasisDamageMultiplier = %.2f%n"
                    + "ruinGuardHealthMultiplier = %.2f%n"
                    + "ruinGuardDamageMultiplier = %.2f%n"
                    + "ruinNautilusHealthMultiplier = %.2f%n"
                    + "ruinNautilusDamageMultiplier = %.2f%n"
                    + "primoGeovishapHealthMultiplier = %.2f%n"
                    + "primoGeovishapDamageMultiplier = %.2f%n",
                    c.enableDamageCap, c.enableRangeCap, c.rangeCapFalloffStart, c.rangeCapImmuneDistance,
                    c.enableAntiMobArmyCap, c.softPerHitCapRatio, c.hardPerHitCapRatio, c.dpsCapPerSecondRatio,
                    c.enableBossBlockBreaking, c.enableCameraShake, c.enableHitstop, c.locatorSearchRadiusChunks,
                    c.globalHealthMultiplier, c.globalDamageMultiplier,
                    c.cryoRegisvineHealthMultiplier, c.cryoRegisvineDamageMultiplier,
                    c.anemoHypostasisHealthMultiplier, c.anemoHypostasisDamageMultiplier,
                    c.ruinGuardHealthMultiplier, c.ruinGuardDamageMultiplier,
                    c.ruinNautilusHealthMultiplier, c.ruinNautilusDamageMultiplier,
                    c.primoGeovishapHealthMultiplier, c.primoGeovishapDamageMultiplier
            );
            try (BufferedWriter bw = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                bw.write(toml);
            }
        } catch (Throwable t) {
            TeyvatBossesConstants.LOGGER.warn("Failed to save {}: {}", CONFIG_PATH, t.getMessage());
        }
    }

    private void sanitize() {
        rangeCapFalloffStart = clampDouble(rangeCapFalloffStart, 8.0D, 128.0D);
        rangeCapImmuneDistance = clampDouble(rangeCapImmuneDistance, rangeCapFalloffStart + 2.0D, 160.0D);
        softPerHitCapRatio = (float) clampDouble(softPerHitCapRatio, 0.01D, 1.0D);
        hardPerHitCapRatio = (float) clampDouble(hardPerHitCapRatio, softPerHitCapRatio, 1.0D);
        dpsCapPerSecondRatio = (float) clampDouble(dpsCapPerSecondRatio, 0.03D, 2.0D);
        locatorSearchRadiusChunks = Math.max(16, Math.min(256, locatorSearchRadiusChunks));

        globalHealthMultiplier = clampDouble(globalHealthMultiplier, 0.1D, 20.0D);
        globalDamageMultiplier = clampDouble(globalDamageMultiplier, 0.1D, 20.0D);
        cryoRegisvineHealthMultiplier = clampDouble(cryoRegisvineHealthMultiplier, 0.1D, 20.0D);
        cryoRegisvineDamageMultiplier = clampDouble(cryoRegisvineDamageMultiplier, 0.1D, 20.0D);
        anemoHypostasisHealthMultiplier = clampDouble(anemoHypostasisHealthMultiplier, 0.1D, 20.0D);
        anemoHypostasisDamageMultiplier = clampDouble(anemoHypostasisDamageMultiplier, 0.1D, 20.0D);
        ruinGuardHealthMultiplier = clampDouble(ruinGuardHealthMultiplier, 0.1D, 20.0D);
        ruinGuardDamageMultiplier = clampDouble(ruinGuardDamageMultiplier, 0.1D, 20.0D);
        ruinNautilusHealthMultiplier = clampDouble(ruinNautilusHealthMultiplier, 0.1D, 20.0D);
        ruinNautilusDamageMultiplier = clampDouble(ruinNautilusDamageMultiplier, 0.1D, 20.0D);
        primoGeovishapHealthMultiplier = clampDouble(primoGeovishapHealthMultiplier, 0.1D, 20.0D);
        primoGeovishapDamageMultiplier = clampDouble(primoGeovishapDamageMultiplier, 0.1D, 20.0D);
    }

    private static double clampDouble(double v, double lo, double hi) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return 1.0D;
        return Math.max(lo, Math.min(hi, v));
    }
}
