package com.velorise.teyvatbosses.registry;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for custom synthesized Ruin Mech sound effects.
 */
public final class TeyvatBossesSounds {
    private static Supplier<SoundEvent> mechEngineHum;
    private static Supplier<SoundEvent> mechLockonBeep;
    private static Supplier<SoundEvent> mechVisorSeal;

    private TeyvatBossesSounds() {}

    public static void bind(
            Supplier<SoundEvent> engineHumSupplier,
            Supplier<SoundEvent> lockonBeepSupplier,
            Supplier<SoundEvent> visorSealSupplier
    ) {
        mechEngineHum = engineHumSupplier;
        mechLockonBeep = lockonBeepSupplier;
        mechVisorSeal = visorSealSupplier;
    }

    public static SoundEvent mechEngineHum() { return mechEngineHum != null ? mechEngineHum.get() : null; }
    public static SoundEvent mechLockonBeep() { return mechLockonBeep != null ? mechLockonBeep.get() : null; }
    public static SoundEvent mechVisorSeal() { return mechVisorSeal != null ? mechVisorSeal.get() : null; }
}
