package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * 12 smooth procedural underwater combat animations for {@link RuinNautilusModel},
 * engineered for zero-snap shortest-arc cross-fading in both {@code RuinNautilusRenderer}
 * and {@code RuinSubmersibleRenderer}.
 */
public final class RuinNautilusAnimations {
    private static final float DEG = Mth.DEG_TO_RAD;

    private RuinNautilusAnimations() {}

    public static void apply(RuinNautilusModel<?> model, String clip, float sec) {
        model.resetPose();
        ModelPart hullPitch = model.getBone("hull_pitch");
        ModelPart jawTop = model.getBone("jaw_guard_top");
        ModelPart jawBot = model.getBone("jaw_guard_bot");
        ModelPart eyeIris = model.getBone("eye_iris_ring");
        ModelPart periscope = model.getBone("periscope_head");
        ModelPart finL = model.getBone("fin_pectoral_l");
        ModelPart finR = model.getBone("fin_pectoral_r");
        ModelPart hatchL = model.getBone("pod_hatch_l");
        ModelPart hatchR = model.getBone("pod_hatch_r");
        ModelPart rudderTop = model.getBone("rudder_top");
        ModelPart rudderBot = model.getBone("rudder_bot");
        ModelPart prop = model.getBone("propeller_shaft");

        if (hullPitch == null) return;

        switch (clip) {
            case "dormant_sunken" -> {
                hullPitch.y += 3.5F;
                hullPitch.xRot += 10.0F * DEG;
                hullPitch.zRot += 14.0F * DEG;
                if (finL != null) finL.zRot -= 22.0F * DEG;
                if (finR != null) finR.zRot += 22.0F * DEG;
            }
            case "awaken_surface" -> {
                float p = Mth.clamp(sec / 2.8F, 0.0F, 1.0F);
                float ease = p * p * (3.0F - 2.0F * p);
                hullPitch.y += Mth.lerp(ease, 4.0F, 0.0F) + Mth.sin(sec * 10.0F) * (1.0F - ease) * 0.4F;
                hullPitch.xRot += Mth.lerp(ease, 12.0F, 0.0F) * DEG;
                hullPitch.zRot += Mth.lerp(ease, 14.0F, 0.0F) * DEG;
                if (prop != null) prop.zRot += sec * sec * 5.0F;
                if (eyeIris != null) eyeIris.zRot += ease * Mth.TWO_PI;
                if (jawTop != null) jawTop.xRot -= Mth.sin(ease * Mth.PI) * 26.0F * DEG;
                if (jawBot != null) jawBot.xRot += Mth.sin(ease * Mth.PI) * 26.0F * DEG;
            }
            case "swim_patrol" -> {
                float w = sec * 3.5F;
                hullPitch.y += Mth.sin(w) * 1.1F;
                hullPitch.zRot += Mth.sin(w * 0.5F) * 4.0F * DEG;
                if (finL != null) { finL.yRot += 18.0F * DEG; finL.zRot += Mth.sin(w) * 8.0F * DEG; }
                if (finR != null) { finR.yRot -= 18.0F * DEG; finR.zRot -= Mth.sin(w) * 8.0F * DEG; }
                if (rudderTop != null) rudderTop.yRot += Mth.sin(w) * 12.0F * DEG;
                if (rudderBot != null) rudderBot.yRot += Mth.sin(w) * 12.0F * DEG;
                if (prop != null) prop.zRot += sec * 14.0F;
                if (eyeIris != null) eyeIris.zRot += sec * 1.2F;
            }
            case "torpedo_salvo" -> {
                float p = Mth.clamp(sec / 3.2F, 0.0F, 1.0F);
                float open = p < 0.22F ? p / 0.22F : (p > 0.80F ? (1.0F - p) / 0.20F : 1.0F);
                float recoil = (p > 0.28F && p < 0.72F) ? Mth.sin((sec - 0.9F) * 22.0F) * 0.8F : 0.0F;
                hullPitch.z += recoil;
                hullPitch.xRot -= open * 6.0F * DEG;
                if (hatchL != null) hatchL.yRot -= open * 110.0F * DEG;
                if (hatchR != null) hatchR.yRot += open * 110.0F * DEG;
                if (prop != null) prop.zRot += sec * 6.0F;
                if (eyeIris != null) eyeIris.zRot += sec * 3.5F;
            }
            case "hydro_jet_ram" -> {
                float p = Mth.clamp(sec / 2.9F, 0.0F, 1.0F);
                if (p < 0.32F) {
                    // Pull-back windup
                    float w = p / 0.32F;
                    hullPitch.z += w * 5.0F;
                    hullPitch.xRot -= w * 10.0F * DEG;
                    if (finL != null) finL.yRot += w * 40.0F * DEG;
                    if (finR != null) finR.yRot -= w * 40.0F * DEG;
                    if (prop != null) prop.zRot += sec * 26.0F;
                } else if (p < 0.78F) {
                    // High-speed torpedo surge
                    float s = (p - 0.32F) / 0.46F;
                    hullPitch.z -= Mth.sin(s * Mth.PI) * 7.0F;
                    hullPitch.xRot += 4.0F * DEG;
                    if (finL != null) finL.yRot += 48.0F * DEG;
                    if (finR != null) finR.yRot -= 48.0F * DEG;
                    if (prop != null) prop.zRot += sec * 38.0F;
                } else {
                    // Braking recovery
                    float r = (p - 0.78F) / 0.22F;
                    hullPitch.xRot += (1.0F - r) * 6.0F * DEG;
                    if (prop != null) prop.zRot += sec * 10.0F;
                }
            }
            case "sonar_shockwave" -> {
                float p = Mth.clamp(sec / 2.3F, 0.0F, 1.0F);
                float flare = Mth.sin(p * Mth.PI);
                hullPitch.z += flare * 2.5F;
                if (jawTop != null) jawTop.xRot -= flare * 42.0F * DEG;
                if (jawBot != null) jawBot.xRot += flare * 42.0F * DEG;
                if (eyeIris != null) eyeIris.zRot += sec * 8.0F;
                if (prop != null) prop.zRot += sec * 7.0F;
            }
            case "whirlpool_vortex" -> {
                float p = Mth.clamp(sec / 4.6F, 0.0F, 1.0F);
                float tilt = p < 0.18F ? p / 0.18F : (p > 0.82F ? (1.0F - p) / 0.18F : 1.0F);
                hullPitch.xRot -= tilt * 58.0F * DEG;
                hullPitch.yRot += sec * 7.5F * tilt;
                if (finL != null) finL.zRot += tilt * 30.0F * DEG;
                if (finR != null) finR.zRot -= tilt * 30.0F * DEG;
                if (prop != null) prop.zRot += sec * 40.0F;
            }
            case "abyssal_beam" -> {
                float p = Mth.clamp(sec / 3.7F, 0.0F, 1.0F);
                float open = p < 0.25F ? p / 0.25F : (p > 0.85F ? (1.0F - p) / 0.15F : 1.0F);
                if (jawTop != null) jawTop.xRot -= open * 48.0F * DEG;
                if (jawBot != null) jawBot.xRot += open * 48.0F * DEG;
                if (eyeIris != null) eyeIris.zRot += sec * 12.0F;
                if (p >= 0.32F && p <= 0.78F) {
                    float sweep = (p - 0.32F) / 0.46F;
                    hullPitch.yRot += Mth.lerp(sweep, -45.0F, 45.0F) * DEG;
                }
                if (prop != null) prop.zRot += sec * 8.0F;
            }
            case "core_overheat_stun" -> {
                hullPitch.y += 3.0F + Mth.sin(sec * 5.0F) * 0.6F;
                hullPitch.xRot += 22.0F * DEG;
                hullPitch.zRot += Mth.sin(sec * 3.5F) * 8.0F * DEG;
                if (jawTop != null) jawTop.xRot -= 52.0F * DEG;
                if (jawBot != null) jawBot.xRot += 52.0F * DEG;
                if (hatchL != null) hatchL.yRot -= 95.0F * DEG;
                if (hatchR != null) hatchR.yRot += 95.0F * DEG;
            }
            case "recover_repressurize" -> {
                float p = Mth.clamp(sec / 1.7F, 0.0F, 1.0F);
                float ease = 1.0F - p * p * (3.0F - 2.0F * p);
                hullPitch.xRot += ease * 20.0F * DEG;
                if (jawTop != null) jawTop.xRot -= ease * 45.0F * DEG;
                if (jawBot != null) jawBot.xRot += ease * 45.0F * DEG;
                if (prop != null) prop.zRot += sec * 12.0F;
            }
            case "death_sink" -> {
                float p = Mth.clamp(sec / 3.5F, 0.0F, 1.0F);
                hullPitch.y += p * 12.0F;
                hullPitch.xRot += p * 28.0F * DEG;
                hullPitch.zRot += p * 35.0F * DEG;
                if (jawTop != null) jawTop.xRot -= p * 40.0F * DEG;
                if (jawBot != null) jawBot.xRot += p * 40.0F * DEG;
            }
            default -> {
                // idle_hover
                float w = sec * 2.1F;
                hullPitch.y += Mth.sin(w) * 1.4F;
                hullPitch.xRot += Mth.sin(w * 0.7F) * 2.5F * DEG;
                if (finL != null) finL.zRot += Mth.sin(w) * 10.0F * DEG;
                if (finR != null) finR.zRot -= Mth.sin(w) * 10.0F * DEG;
                if (periscope != null) periscope.yRot += Mth.sin(sec * 0.9F) * 45.0F * DEG;
                if (prop != null) prop.zRot += sec * 6.5F;
                if (eyeIris != null) eyeIris.zRot += sec * 0.8F;
            }
        }
    }
}
