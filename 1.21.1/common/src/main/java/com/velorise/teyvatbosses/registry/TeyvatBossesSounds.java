package com.velorise.teyvatbosses.registry;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for custom synthesized Ruin Mech & Ruin Guard sound effects.
 */
public final class TeyvatBossesSounds {
    private static Supplier<SoundEvent> mechEngineHum;
    private static Supplier<SoundEvent> mechLockonBeep;
    private static Supplier<SoundEvent> mechVisorSeal;
    private static Supplier<SoundEvent> mechServoWhir;
    private static Supplier<SoundEvent> mechJointCreak;
    private static Supplier<SoundEvent> mechMetalClank;
    private static Supplier<SoundEvent> mechHydraulicHiss;
    private static Supplier<SoundEvent> subPropellerHum;
    private static Supplier<SoundEvent> subSonarPing;
    private static Supplier<SoundEvent> subTorpedoLaunch;
    private static Supplier<SoundEvent> subBallastHiss;

    private TeyvatBossesSounds() {}

    public static void bind(
            Supplier<SoundEvent> engineHumSupplier,
            Supplier<SoundEvent> lockonBeepSupplier,
            Supplier<SoundEvent> visorSealSupplier,
            Supplier<SoundEvent> servoWhirSupplier,
            Supplier<SoundEvent> jointCreakSupplier,
            Supplier<SoundEvent> metalClankSupplier,
            Supplier<SoundEvent> hydraulicHissSupplier,
            Supplier<SoundEvent> subPropellerHumSupplier,
            Supplier<SoundEvent> subSonarPingSupplier,
            Supplier<SoundEvent> subTorpedoLaunchSupplier,
            Supplier<SoundEvent> subBallastHissSupplier
    ) {
        mechEngineHum = engineHumSupplier;
        mechLockonBeep = lockonBeepSupplier;
        mechVisorSeal = visorSealSupplier;
        mechServoWhir = servoWhirSupplier;
        mechJointCreak = jointCreakSupplier;
        mechMetalClank = metalClankSupplier;
        mechHydraulicHiss = hydraulicHissSupplier;
        subPropellerHum = subPropellerHumSupplier;
        subSonarPing = subSonarPingSupplier;
        subTorpedoLaunch = subTorpedoLaunchSupplier;
        subBallastHiss = subBallastHissSupplier;
    }

    public static SoundEvent mechEngineHum() { return mechEngineHum != null ? mechEngineHum.get() : null; }
    public static SoundEvent mechLockonBeep() { return mechLockonBeep != null ? mechLockonBeep.get() : null; }
    public static SoundEvent mechVisorSeal() { return mechVisorSeal != null ? mechVisorSeal.get() : null; }
    public static SoundEvent mechServoWhir() { return mechServoWhir != null ? mechServoWhir.get() : null; }
    public static SoundEvent mechJointCreak() { return mechJointCreak != null ? mechJointCreak.get() : null; }
    public static SoundEvent mechMetalClank() { return mechMetalClank != null ? mechMetalClank.get() : null; }
    public static SoundEvent mechHydraulicHiss() { return mechHydraulicHiss != null ? mechHydraulicHiss.get() : null; }
    public static SoundEvent subPropellerHum() { return subPropellerHum != null ? subPropellerHum.get() : null; }
    public static SoundEvent subSonarPing() { return subSonarPing != null ? subSonarPing.get() : null; }
    public static SoundEvent subTorpedoLaunch() { return subTorpedoLaunch != null ? subTorpedoLaunch.get() : null; }
    public static SoundEvent subBallastHiss() { return subBallastHiss != null ? subBallastHiss.get() : null; }
}
