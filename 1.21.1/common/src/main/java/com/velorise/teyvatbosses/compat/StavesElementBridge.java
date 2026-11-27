package com.velorise.teyvatbosses.compat;

import com.velorise.teyvatbosses.config.TeyvatBossesConfig;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;

/**
 * Reflection-based soft integration with Velorise's Staves ({@code VeloriseElementAPI} &
 * {@code VeloriseElementEvents}). Keeps {@code velorise_teyvat_bosses} compiling cleanly
 * without requiring a hard compile-time JAR while activating full EGT shields, innate auras,
 * elemental reactions, and aura-hit monitoring automatically whenever Velorise's Staves is loaded.
 */
public final class StavesElementBridge {

    @FunctionalInterface
    public interface ShieldBreakCallback {
        void onShieldBroken(ServerLevel level, LivingEntity target, @Nullable LivingEntity attacker, String shieldElement, @Nullable String breakerElement);
    }

    @FunctionalInterface
    public interface PreAuraCallback {
        /** @return false to cancel the elemental application, true to allow it. */
        boolean onPreApplyAura(ServerLevel level, LivingEntity target, @Nullable LivingEntity attacker, String element, float gaugeUnits);
    }

    private static boolean initialized = false;
    private static boolean available = false;

    private static Class<?> clsApi;
    private static Class<?> clsEvents;
    private static Class<?> clsElement;
    private static Class<?> clsIcdPolicy;

    private static Method mParseElement;
    private static Method mDealElementalDamage;
    private static Method mSetInnateElement;
    private static Method mClearInnateElement;
    private static Method mSetShield5;
    private static Method mClearShield;
    private static Method mHasShield;
    private static Method mGetShieldProgress;
    private static Method mRegisterShieldBreak;
    private static Method mRegisterPreAura;

    private StavesElementBridge() {}

    public static synchronized boolean isAvailable() {
        if (!initialized) {
            initialized = true;
            try {
                clsApi = Class.forName("com.velorise.staves.api.VeloriseElementAPI");
                clsEvents = Class.forName("com.velorise.staves.api.VeloriseElementEvents");
                clsElement = Class.forName("com.velorise.staves.element.Element");
                clsIcdPolicy = Class.forName("com.velorise.staves.api.IcdPolicy");

                mParseElement = clsApi.getMethod("parseElement", String.class);
                mDealElementalDamage = clsApi.getMethod(
                        "dealElementalDamage",
                        LivingEntity.class, LivingEntity.class, clsElement, float.class, float.class, clsIcdPolicy
                );
                mSetInnateElement = clsApi.getMethod("setInnateElement", LivingEntity.class, clsElement);
                mClearInnateElement = clsApi.getMethod("clearInnateElement", LivingEntity.class);
                mSetShield5 = clsApi.getMethod(
                        "setElementalShield",
                        LivingEntity.class, clsElement, float.class, boolean.class, float.class
                );
                mClearShield = clsApi.getMethod("clearElementalShield", LivingEntity.class);
                mHasShield = clsApi.getMethod("hasElementalShield", LivingEntity.class);
                mGetShieldProgress = clsApi.getMethod("getShieldProgress", LivingEntity.class);

                Class<?> clsShieldListener = Class.forName("com.velorise.staves.api.VeloriseElementEvents$ShieldBreakListener");
                Class<?> clsPreAuraListener = Class.forName("com.velorise.staves.api.VeloriseElementEvents$PreAuraListener");
                mRegisterShieldBreak = clsEvents.getMethod("registerShieldBreakListener", clsShieldListener);
                mRegisterPreAura = clsEvents.getMethod("registerPreAuraListener", clsPreAuraListener);

                available = true;
                TeyvatBossesConstants.LOGGER.info("Connected to VeloriseElementAPI (full EGT boss mechanics enabled).");
            } catch (Throwable ignored) {
                available = false;
                TeyvatBossesConstants.LOGGER.info("Velorise's Staves not detected; running boss elemental fallback mode.");
            }
        }
        return available;
    }

    private static @Nullable Object toElement(@Nullable String name) {
        if (!isAvailable() || name == null || name.isBlank()) return null;
        try {
            return mParseElement.invoke(null, name.trim().toUpperCase(Locale.ROOT));
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object toIcdPolicy(boolean bypass) {
        return Enum.valueOf((Class<? extends Enum>) clsIcdPolicy, bypass ? "BYPASS" : "STANDARD");
    }

    public static void dealElementalDamage(
            LivingEntity target,
            @Nullable LivingEntity attacker,
            String element,
            float damage,
            float gaugeUnits,
            boolean bypassIcd
    ) {
        dealElementalDamage(target, attacker, element, damage, gaugeUnits, bypassIcd, 0.0F);
    }

    /**
     * Deals elemental damage with an optional armor-piercing magic portion (0.0..1.0)
     * so heavy boss slams remain threatening against high-armor / Netherite Prot IV gear.
     */
    public static void dealElementalDamage(
            LivingEntity target,
            @Nullable LivingEntity attacker,
            String element,
            float damage,
            float gaugeUnits,
            boolean bypassIcd,
            float magicPierceRatio
    ) {
        if (attacker != null) {
            String _bCls = attacker.getClass().getSimpleName();
            if ("CryoRegisvineEntity".equals(_bCls)) damage = TeyvatBossesConfig.scaleDamage("cryo_regisvine", damage);
            else if ("AnemoHypostasisEntity".equals(_bCls)) damage = TeyvatBossesConfig.scaleDamage("anemo_hypostasis", damage);
            else if ("RuinGuardEntity".equals(_bCls)) damage = TeyvatBossesConfig.scaleDamage("ruin_guard", damage);
            else if ("RuinNautilusEntity".equals(_bCls)) damage = TeyvatBossesConfig.scaleDamage("ruin_nautilus", damage);
            else if ("PrimoGeovishapEntity".equals(_bCls)) damage = TeyvatBossesConfig.scaleDamage("primo_geovishap", damage);
        }
        if (target == null || target.level().isClientSide()) return;
        float ratio = Math.max(0.0F, Math.min(0.65F, magicPierceRatio));
        float mainDmg = damage * (1.0F - ratio);
        float pierceDmg = damage * ratio;

        boolean dealtViaApi = false;
        if (isAvailable()) {
            Object elem = toElement(element);
            if (elem != null) {
                try {
                    mDealElementalDamage.invoke(null, target, attacker, elem, mainDmg, gaugeUnits, toIcdPolicy(bypassIcd));
                    dealtViaApi = true;
                } catch (Throwable ignored) {
                }
            }
        }
        if (!dealtViaApi) {
            DamageSource src = attacker != null ? target.damageSources().mobAttack(attacker) : target.damageSources().magic();
            target.hurt(src, mainDmg);
        }
        if (pierceDmg > 0.5F && target.isAlive()) {
            int savedInvuln = target.invulnerableTime;
            target.invulnerableTime = 0;
            target.hurt(target.damageSources().magic(), pierceDmg);
            target.invulnerableTime = Math.max(savedInvuln, target.invulnerableTime);
        }
    }

    public static void setElementalShield(
            LivingEntity entity,
            String element,
            float maxUnits,
            boolean absorbHealthDamage,
            float passThrough
    ) {
        if (!isAvailable() || entity == null || entity.level().isClientSide()) return;
        Object elem = toElement(element);
        if (elem == null) return;
        try {
            mSetShield5.invoke(null, entity, elem, maxUnits, absorbHealthDamage, passThrough);
        } catch (Throwable ignored) {
        }
    }

    public static void clearElementalShield(@Nullable LivingEntity entity) {
        if (!isAvailable() || entity == null) return;
        try {
            mClearShield.invoke(null, entity);
        } catch (Throwable ignored) {
        }
    }

    public static boolean hasElementalShield(@Nullable LivingEntity entity) {
        if (!isAvailable() || entity == null) return false;
        try {
            return Boolean.TRUE.equals(mHasShield.invoke(null, entity));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static float getShieldProgress(@Nullable LivingEntity entity) {
        if (!isAvailable() || entity == null) return 0.0F;
        try {
            Object res = mGetShieldProgress.invoke(null, entity);
            return res instanceof Float f ? f : 0.0F;
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }

    public static void setInnateElement(LivingEntity entity, @Nullable String element) {
        if (!isAvailable() || entity == null || entity.level().isClientSide()) return;
        try {
            if (element == null || element.isBlank()) {
                mClearInnateElement.invoke(null, entity);
            } else {
                Object elem = toElement(element);
                if (elem != null) mSetInnateElement.invoke(null, entity, elem);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void registerShieldBreakListener(ShieldBreakCallback callback) {
        if (!isAvailable() || callback == null) return;
        try {
            Class<?> listenerIface = Class.forName("com.velorise.staves.api.VeloriseElementEvents$ShieldBreakListener");
            Object proxy = Proxy.newProxyInstance(
                    listenerIface.getClassLoader(),
                    new Class<?>[]{listenerIface},
                    (p, method, args) -> {
                        if ("onShieldBroken".equals(method.getName()) && args != null && args.length == 5) {
                            ServerLevel level = (ServerLevel) args[0];
                            LivingEntity target = (LivingEntity) args[1];
                            LivingEntity attacker = (LivingEntity) args[2];
                            String shieldEl = args[3] != null ? args[3].toString() : "NONE";
                            String breakerEl = args[4] != null ? args[4].toString() : null;
                            callback.onShieldBroken(level, target, attacker, shieldEl, breakerEl);
                        }
                        return null;
                    }
            );
            mRegisterShieldBreak.invoke(null, proxy);
        } catch (Throwable ignored) {
        }
    }

    public static void registerPreAuraListener(PreAuraCallback callback) {
        if (!isAvailable() || callback == null) return;
        try {
            Class<?> listenerIface = Class.forName("com.velorise.staves.api.VeloriseElementEvents$PreAuraListener");
            Object proxy = Proxy.newProxyInstance(
                    listenerIface.getClassLoader(),
                    new Class<?>[]{listenerIface},
                    (p, method, args) -> {
                        if ("onPreApplyAura".equals(method.getName()) && args != null && args.length == 5) {
                            ServerLevel level = (ServerLevel) args[0];
                            LivingEntity target = (LivingEntity) args[1];
                            LivingEntity attacker = (LivingEntity) args[2];
                            String elem = args[3] != null ? args[3].toString() : "NONE";
                            float units = args[4] instanceof Float f ? f : 0.0F;
                            return callback.onPreApplyAura(level, target, attacker, elem, units);
                        }
                        return Boolean.TRUE;
                    }
            );
            mRegisterPreAura.invoke(null, proxy);
        } catch (Throwable ignored) {
        }
    }
}
