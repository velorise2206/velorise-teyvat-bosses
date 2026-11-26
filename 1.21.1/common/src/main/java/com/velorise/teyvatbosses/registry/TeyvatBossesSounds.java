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
    private static Supplier<SoundEvent> drillEngineIdle;
    private static Supplier<SoundEvent> drillBoreSpin;
    private static Supplier<SoundEvent> ruinGuardSpeech;
    private static Supplier<SoundEvent> ruinGuardPowerDown;
    private static Supplier<SoundEvent> primoRoar;
    private static Supplier<SoundEvent> primoStep;
    private static Supplier<SoundEvent> primoClawSwipe;
    private static Supplier<SoundEvent> primoSlam;
    private static Supplier<SoundEvent> primoBurrow;
    private static Supplier<SoundEvent> primoEmerge;
    private static Supplier<SoundEvent> primoShowerCharge;
    private static Supplier<SoundEvent> primoShowerBlast;
    private static Supplier<SoundEvent> primoShieldReflect;
    private static Supplier<SoundEvent> primoDeath;

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
            Supplier<SoundEvent> subBallastHissSupplier,
            Supplier<SoundEvent> drillEngineIdleSupplier,
            Supplier<SoundEvent> drillBoreSpinSupplier,
            Supplier<SoundEvent> ruinGuardSpeechSupplier,
            Supplier<SoundEvent> ruinGuardPowerDownSupplier,
            Supplier<SoundEvent> primoRoarSupplier,
            Supplier<SoundEvent> primoStepSupplier,
            Supplier<SoundEvent> primoClawSwipeSupplier,
            Supplier<SoundEvent> primoSlamSupplier,
            Supplier<SoundEvent> primoBurrowSupplier,
            Supplier<SoundEvent> primoEmergeSupplier,
            Supplier<SoundEvent> primoShowerChargeSupplier,
            Supplier<SoundEvent> primoShowerBlastSupplier,
            Supplier<SoundEvent> primoShieldReflectSupplier,
            Supplier<SoundEvent> primoDeathSupplier
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
        drillEngineIdle = drillEngineIdleSupplier;
        drillBoreSpin = drillBoreSpinSupplier;
        ruinGuardSpeech = ruinGuardSpeechSupplier;
        ruinGuardPowerDown = ruinGuardPowerDownSupplier;
        primoRoar = primoRoarSupplier;
        primoStep = primoStepSupplier;
        primoClawSwipe = primoClawSwipeSupplier;
        primoSlam = primoSlamSupplier;
        primoBurrow = primoBurrowSupplier;
        primoEmerge = primoEmergeSupplier;
        primoShowerCharge = primoShowerChargeSupplier;
        primoShowerBlast = primoShowerBlastSupplier;
        primoShieldReflect = primoShieldReflectSupplier;
        primoDeath = primoDeathSupplier;
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
    public static SoundEvent drillEngineIdle() { return drillEngineIdle != null ? drillEngineIdle.get() : null; }
    public static SoundEvent drillBoreSpin() { return drillBoreSpin != null ? drillBoreSpin.get() : null; }
    public static SoundEvent ruinGuardSpeech() { return ruinGuardSpeech != null ? ruinGuardSpeech.get() : null; }
    public static SoundEvent ruinGuardPowerDown() { return ruinGuardPowerDown != null ? ruinGuardPowerDown.get() : null; }
    public static SoundEvent primoRoar() { return primoRoar != null ? primoRoar.get() : null; }
    public static SoundEvent primoStep() { return primoStep != null ? primoStep.get() : null; }
    public static SoundEvent primoClawSwipe() { return primoClawSwipe != null ? primoClawSwipe.get() : null; }
    public static SoundEvent primoSlam() { return primoSlam != null ? primoSlam.get() : null; }
    public static SoundEvent primoBurrow() { return primoBurrow != null ? primoBurrow.get() : null; }
    public static SoundEvent primoEmerge() { return primoEmerge != null ? primoEmerge.get() : null; }
    public static SoundEvent primoShowerCharge() { return primoShowerCharge != null ? primoShowerCharge.get() : null; }
    public static SoundEvent primoShowerBlast() { return primoShowerBlast != null ? primoShowerBlast.get() : null; }
    public static SoundEvent primoShieldReflect() { return primoShieldReflect != null ? primoShieldReflect.get() : null; }
    public static SoundEvent primoDeath() { return primoDeath != null ? primoDeath.get() : null; }
}
