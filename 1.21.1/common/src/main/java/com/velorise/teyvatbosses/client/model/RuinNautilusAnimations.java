package com.velorise.teyvatbosses.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * 16 AAA procedural underwater combat animations for {@link RuinNautilusModel},
 * featuring anticipation, recoil, banking, outer-hinged torpedo hatches,
 * dual counter-rotating iris rings, articulated harpoon arms, amber thermal vents,
 * and Phase 2 Dorsal Hydro-Railgun deployment.
 */
public final class RuinNautilusAnimations {
    private static final float DEG = Mth.DEG_TO_RAD;

    private RuinNautilusAnimations() {}

        public static void apply(RuinNautilusModel<?> model, String clip, float sec) {
        apply(model, clip, sec, false);
    }

    public static void apply(RuinNautilusModel<?> model, String clip, float sec, boolean phase2) {
        model.resetPose();
        ModelPart hullPitch = model.getBone("hull_pitch");
        ModelPart jawTop = model.getBone("jaw_guard_top");
        ModelPart jawBot = model.getBone("jaw_guard_bot");
        ModelPart eyeIris = model.getBone("eye_iris_ring");
        ModelPart eyeOuter = model.getBone("eye_iris_outer");
        ModelPart periscope = model.getBone("periscope_head");
        ModelPart finL = model.getBone("fin_pectoral_l");
        ModelPart finR = model.getBone("fin_pectoral_r");
        ModelPart hatchL = model.getBone("pod_hatch_l");
        ModelPart hatchR = model.getBone("pod_hatch_r");
        ModelPart rudderTop = model.getBone("rudder_top");
        ModelPart rudderBot = model.getBone("rudder_bot");
        ModelPart prop = model.getBone("propeller_shaft");
        ModelPart tail = model.getBone("tail_cone");

        ModelPart dorsalBayL = model.getBone("dorsal_bay_l");
        ModelPart dorsalBayR = model.getBone("dorsal_bay_r");
        ModelPart railBase = model.getBone("dorsal_railgun_base");
        ModelPart railBarrel = model.getBone("dorsal_railgun_barrel");
        ModelPart ventFlapL = model.getBone("vent_flap_l");
        ModelPart ventFlapR = model.getBone("vent_flap_r");
        ModelPart harpoonL = model.getBone("harpoon_arm_l");
        ModelPart harpoonR = model.getBone("harpoon_arm_r");
        ModelPart drownedHead = model.getBone("drowned_head");
        ModelPart drownedArmL = model.getBone("drowned_arm_l");
        ModelPart drownedArmR = model.getBone("drowned_arm_r");
        ModelPart leverL = model.getBone("control_lever_l");
        ModelPart leverR = model.getBone("control_lever_r");
        ModelPart gearL = model.getBone("clockwork_gear_l");
        ModelPart gearR = model.getBone("clockwork_gear_r");
        ModelPart bellyGear = model.getBone("belly_gear");
        ModelPart bellyGear2 = model.getBone("belly_gear_2");
        ModelPart clawWristL = model.getBone("harpoon_claw_l");
        ModelPart clawWristR = model.getBone("harpoon_claw_r");
        ModelPart jawTopL = model.getBone("claw_jaw_top_l");
        ModelPart jawBotL = model.getBone("claw_jaw_bot_l");
        ModelPart jawTopR = model.getBone("claw_jaw_top_r");
        ModelPart jawBotR = model.getBone("claw_jaw_bot_r");

        if (hullPitch == null) return;

        // Keep Dorsal Hydro-Railgun deployed & thermal vents cracked open throughout Phase 2
        if (phase2 && !"phase2_overdrive".equals(clip) && !"dormant_sunken".equals(clip)) {
            applyRailgunDeploy(dorsalBayL, dorsalBayR, railBase, 1.0F);
            if (ventFlapL != null) ventFlapL.zRot += 22.0F * DEG;
            if (ventFlapR != null) ventFlapR.zRot -= 22.0F * DEG;
        }

        switch (clip) {
            case "dormant_sunken" -> {
                hullPitch.y += 4.5F;
                hullPitch.xRot += 11.0F * DEG;
                hullPitch.zRot += 15.0F * DEG;
                if (finL != null) finL.zRot -= 24.0F * DEG;
                if (finR != null) finR.zRot += 24.0F * DEG;
            }
            case "awaken_surface" -> {
                float p = Mth.clamp(sec / 2.8F, 0.0F, 1.0F);
                float ease = smoothstep(p);
                hullPitch.y += Mth.lerp(ease, 4.5F, 0.0F) + Mth.sin(sec * 11.0F) * (1.0F - ease) * 0.45F;
                hullPitch.xRot += Mth.lerp(ease, 12.0F, 0.0F) * DEG;
                hullPitch.zRot += Mth.lerp(ease, 15.0F, 0.0F) * DEG;
                if (prop != null) prop.zRot += sec * sec * 5.5F;
                if (eyeIris != null) eyeIris.zRot += ease * Mth.TWO_PI;
                if (eyeOuter != null) eyeOuter.zRot -= ease * Mth.PI;
                if (jawTop != null) jawTop.xRot -= Mth.sin(ease * Mth.PI) * 28.0F * DEG;
                if (jawBot != null) jawBot.xRot += Mth.sin(ease * Mth.PI) * 28.0F * DEG;
            }
            case "swim_patrol" -> {
                float w = sec * 3.6F;
                hullPitch.y += Mth.sin(w) * 1.1F;
                hullPitch.zRot += Mth.sin(w * 0.5F) * 5.0F * DEG;
                if (finL != null) { finL.yRot += 14.0F * DEG; finL.zRot += Mth.sin(w) * 9.0F * DEG; }
                if (finR != null) { finR.yRot -= 14.0F * DEG; finR.zRot -= Mth.sin(w) * 9.0F * DEG; }
                if (tail != null) tail.yRot += Mth.sin(w) * 4.5F * DEG;
                if (rudderTop != null) rudderTop.yRot += Mth.sin(w) * 14.0F * DEG;
                if (rudderBot != null) rudderBot.yRot += Mth.sin(w) * 14.0F * DEG;
                if (prop != null) prop.zRot += sec * 15.0F;
                if (eyeIris != null) eyeIris.zRot += sec * 1.5F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 0.8F;
            }
            case "torpedo_salvo" -> {
                float p = Mth.clamp(sec / 3.2F, 0.0F, 1.0F);
                float open = p < 0.20F ? smoothstep(p / 0.20F) : (p > 0.82F ? smoothstep((1.0F - p) / 0.18F) : 1.0F);
                float recoil = (p > 0.26F && p < 0.72F) ? Mth.sin((sec - 0.85F) * 24.0F) * 0.85F : 0.0F;
                hullPitch.z += recoil;
                hullPitch.xRot -= open * 5.5F * DEG;
                // True outer-hinged hatches swing cleanly outward without clipping
                if (hatchL != null) hatchL.yRot += open * 100.0F * DEG;
                if (hatchR != null) hatchR.yRot -= open * 100.0F * DEG;
                if (prop != null) prop.zRot += sec * 7.5F;
                if (eyeIris != null) eyeIris.zRot += sec * 3.8F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 2.0F;
            }
            case "hydro_jet_ram" -> {
                float p = Mth.clamp(sec / 3.0F, 0.0F, 1.0F);
                if (p < 0.34F) {
                    // Telegraph windup: nose pulls back, jaws open wide, fins sweep tight, turbine roars
                    float w = smoothstep(p / 0.34F);
                    hullPitch.z += w * 6.0F;
                    hullPitch.xRot -= w * 9.0F * DEG;
                    if (jawTop != null) jawTop.xRot -= w * 32.0F * DEG;
                    if (jawBot != null) jawBot.xRot += w * 32.0F * DEG;
                    if (finL != null) finL.yRot -= w * 28.0F * DEG;
                    if (finR != null) finR.yRot += w * 28.0F * DEG;
                    if (prop != null) prop.zRot += sec * 28.0F;
                } else if (p < 0.78F) {
                    // High-speed torpedo surge with snapping jaws
                    float s = (p - 0.34F) / 0.44F;
                    hullPitch.z -= Mth.sin(s * Mth.PI) * 8.0F;
                    hullPitch.xRot += 4.5F * DEG;
                    if (jawTop != null) jawTop.xRot -= (1.0F - s) * 18.0F * DEG;
                    if (jawBot != null) jawBot.xRot += (1.0F - s) * 18.0F * DEG;
                    if (finL != null) finL.yRot -= 32.0F * DEG;
                    if (finR != null) finR.yRot += 32.0F * DEG;
                    if (prop != null) prop.zRot += sec * 44.0F;
                } else {
                    float r = smoothstep((p - 0.78F) / 0.22F);
                    hullPitch.xRot += (1.0F - r) * 6.0F * DEG;
                    if (prop != null) prop.zRot += sec * 10.0F;
                }
            }
            case "sonar_shockwave" -> {
                float p = Mth.clamp(sec / 2.3F, 0.0F, 1.0F);
                float flare = Mth.sin(p * Mth.PI);
                hullPitch.z += flare * 2.8F;
                if (jawTop != null) jawTop.xRot -= flare * 42.0F * DEG;
                if (jawBot != null) jawBot.xRot += flare * 42.0F * DEG;
                if (eyeIris != null) eyeIris.zRot += sec * 9.0F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 6.0F;
                if (prop != null) prop.zRot += sec * 8.0F;
            }
            case "fin_blade_sweep" -> {
                // Close-range banked 360-degree hydro-plane & tail sweep
                float p = Mth.clamp(sec / 2.2F, 0.0F, 1.0F);
                float spin = smoothstep(Mth.clamp((p - 0.18F) / 0.64F, 0.0F, 1.0F));
                hullPitch.yRot += spin * Mth.TWO_PI;
                hullPitch.zRot += Mth.sin(p * Mth.PI) * 28.0F * DEG;
                if (finL != null) finL.zRot += Mth.sin(p * Mth.PI) * 20.0F * DEG;
                if (finR != null) finR.zRot -= Mth.sin(p * Mth.PI) * 20.0F * DEG;
                if (prop != null) prop.zRot += sec * 26.0F;
            }
            case "harpoon_tether" -> {
                // Articulated ventral harpoon arms extend forward and reel target in
                float p = Mth.clamp(sec / 2.6F, 0.0F, 1.0F);
                float reach = p < 0.25F ? smoothstep(p / 0.25F) : (p > 0.78F ? smoothstep((1.0F - p) / 0.22F) : 1.0F);
                hullPitch.xRot -= reach * 6.0F * DEG;
                if (harpoonL != null) {
                    harpoonL.xRot -= reach * 22.0F * DEG;
                    harpoonL.yRot += reach * 24.0F * DEG;
                    harpoonL.z -= reach * 5.0F;
                }
                if (harpoonR != null) {
                    harpoonR.xRot -= reach * 22.0F * DEG;
                    harpoonR.yRot -= reach * 24.0F * DEG;
                    harpoonR.z -= reach * 5.0F;
                }
                if (jawTop != null) jawTop.xRot -= reach * 24.0F * DEG;
                if (jawBot != null) jawBot.xRot += reach * 24.0F * DEG;
                if (prop != null) prop.zRot += sec * 12.0F;
            }
            case "depth_mine_barrage" -> {
                // Pitches nose slightly down while rear vents release floating depth mines
                float p = Mth.clamp(sec / 2.4F, 0.0F, 1.0F);
                float pulse = Mth.sin(p * Mth.PI);
                hullPitch.xRot += pulse * 14.0F * DEG;
                hullPitch.y -= pulse * 1.8F;
                if (ventFlapL != null) ventFlapL.zRot += pulse * 45.0F * DEG;
                if (ventFlapR != null) ventFlapR.zRot -= pulse * 45.0F * DEG;
                if (prop != null) prop.zRot += sec * 18.0F;
            }
            case "whirlpool_vortex" -> {
                // Fixed! Smooth banked high-torque hydro-vortex surge (no buggy 58-deg vertical fidget spinner!)
                float p = Mth.clamp(sec / 4.4F, 0.0F, 1.0F);
                float amp = p < 0.16F ? smoothstep(p / 0.16F) : (p > 0.84F ? smoothstep((1.0F - p) / 0.16F) : 1.0F);
                hullPitch.zRot += amp * 26.0F * DEG;
                hullPitch.xRot -= amp * 10.0F * DEG;
                hullPitch.y += Mth.sin(sec * 5.0F) * 1.2F * amp;
                if (finL != null) finL.zRot += amp * 22.0F * DEG;
                if (finR != null) finR.zRot -= amp * 22.0F * DEG;
                if (prop != null) prop.zRot += sec * 46.0F;
                if (eyeIris != null) eyeIris.zRot += sec * 10.0F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 7.0F;
            }
            case "abyssal_beam" -> {
                float p = Mth.clamp(sec / 3.7F, 0.0F, 1.0F);
                float open = p < 0.25F ? smoothstep(p / 0.25F) : (p > 0.85F ? smoothstep((1.0F - p) / 0.15F) : 1.0F);
                if (jawTop != null) jawTop.xRot -= open * 46.0F * DEG;
                if (jawBot != null) jawBot.xRot += open * 46.0F * DEG;
                if (eyeIris != null) eyeIris.zRot += sec * 14.0F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 9.0F;
                if (p >= 0.32F && p <= 0.78F) {
                    float sweep = (p - 0.32F) / 0.46F;
                    hullPitch.yRot += Mth.lerp(sweep, -42.0F, 42.0F) * DEG;
                }
                if (prop != null) prop.zRot += sec * 9.0F;
            }
            case "phase2_overdrive" -> {
                // Dramatic 4.0s Phase 2 Transformation: bay doors open, Dorsal Hydro-Railgun elevates!
                float p = Mth.clamp(sec / 4.0F, 0.0F, 1.0F);
                float deploy = smoothstep(Mth.clamp((p - 0.18F) / 0.62F, 0.0F, 1.0F));
                float shake = (p > 0.15F && p < 0.85F) ? Mth.sin(sec * 42.0F) * 0.45F : 0.0F;
                hullPitch.y -= deploy * 2.2F;
                hullPitch.x += shake;
                hullPitch.xRot -= Mth.sin(p * Mth.PI) * 14.0F * DEG;
                applyRailgunDeploy(dorsalBayL, dorsalBayR, railBase, deploy);
                if (ventFlapL != null) ventFlapL.zRot += deploy * 56.0F * DEG;
                if (ventFlapR != null) ventFlapR.zRot -= deploy * 56.0F * DEG;
                if (jawTop != null) jawTop.xRot -= Mth.sin(p * Mth.PI) * 44.0F * DEG;
                if (jawBot != null) jawBot.xRot += Mth.sin(p * Mth.PI) * 44.0F * DEG;
                if (eyeIris != null) eyeIris.zRot += sec * 16.0F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 11.0F;
                if (prop != null) prop.zRot += sec * 30.0F;
            }
            case "dorsal_railgun" -> {
                // Phase 2 Signature Superweapon: 1.8s lock-on charge -> heavy beam fire -> 3.5s overheat vents!
                float p = Mth.clamp(sec / 5.4F, 0.0F, 1.0F);
                applyRailgunDeploy(dorsalBayL, dorsalBayR, railBase, 1.0F);
                if (p < 0.34F) {
                    // Charge-up & target lock
                    float c = smoothstep(p / 0.34F);
                    hullPitch.xRot -= c * 5.0F * DEG;
                    if (railBarrel != null) railBarrel.xRot -= c * 4.0F * DEG;
                    if (eyeIris != null) eyeIris.zRot += sec * 14.0F;
                } else if (p < 0.60F) {
                    // Railgun discharge + heavy kickback recoil
                    float fire = (p - 0.34F) / 0.26F;
                    float kick = Mth.sin(fire * Mth.PI);
                    hullPitch.z += kick * 4.5F;
                    if (railBarrel != null) railBarrel.z += kick * 4.0F;
                    if (ventFlapL != null) ventFlapL.zRot += 55.0F * DEG;
                    if (ventFlapR != null) ventFlapR.zRot -= 55.0F * DEG;
                } else {
                    // Overheat cooling window (vulnerable to burst -> stun!)
                    float cool = Mth.sin(((p - 0.60F) / 0.40F) * Mth.PI);
                    hullPitch.xRot += cool * 8.0F * DEG;
                    if (ventFlapL != null) ventFlapL.zRot += 62.0F * DEG;
                    if (ventFlapR != null) ventFlapR.zRot -= 62.0F * DEG;
                }
                if (prop != null) prop.zRot += sec * 12.0F;
            }
            case "sonar_breach" -> {
                // Phase 2 Dive & Breach Ambush
                float p = Mth.clamp(sec / 3.4F, 0.0F, 1.0F);
                if (p < 0.35F) {
                    float d = smoothstep(p / 0.35F);
                    hullPitch.xRot += d * 34.0F * DEG;
                    if (prop != null) prop.zRot += sec * 32.0F;
                } else if (p < 0.78F) {
                    float b = smoothstep((p - 0.35F) / 0.43F);
                    hullPitch.xRot += Mth.lerp(b, -38.0F, 0.0F) * DEG;
                    hullPitch.zRot += Mth.sin(b * Mth.TWO_PI) * 18.0F * DEG;
                    if (jawTop != null) jawTop.xRot -= 36.0F * DEG;
                    if (jawBot != null) jawBot.xRot += 36.0F * DEG;
                    if (prop != null) prop.zRot += sec * 48.0F;
                } else {
                    if (prop != null) prop.zRot += sec * 14.0F;
                }
            }
            case "core_overheat_stun" -> {
                hullPitch.y += 3.5F + Mth.sin(sec * 5.0F) * 0.6F;
                hullPitch.xRot += 20.0F * DEG;
                hullPitch.zRot += Mth.sin(sec * 3.5F) * 9.0F * DEG;
                if (jawTop != null) jawTop.xRot -= 52.0F * DEG;
                if (jawBot != null) jawBot.xRot += 52.0F * DEG;
                if (hatchL != null) hatchL.yRot += 90.0F * DEG;
                if (hatchR != null) hatchR.yRot -= 90.0F * DEG;
                if (ventFlapL != null) ventFlapL.zRot += 68.0F * DEG;
                if (ventFlapR != null) ventFlapR.zRot -= 68.0F * DEG;
            }
            case "recover_repressurize" -> {
                float p = Mth.clamp(sec / 1.7F, 0.0F, 1.0F);
                float ease = 1.0F - smoothstep(p);
                hullPitch.xRot += ease * 20.0F * DEG;
                if (jawTop != null) jawTop.xRot -= ease * 45.0F * DEG;
                if (jawBot != null) jawBot.xRot += ease * 45.0F * DEG;
                if (ventFlapL != null) ventFlapL.zRot += ease * 58.0F * DEG;
                if (ventFlapR != null) ventFlapR.zRot -= ease * 58.0F * DEG;
                if (prop != null) prop.zRot += sec * 14.0F;
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
                hullPitch.y += Mth.sin(w) * 1.3F;
                hullPitch.xRot += Mth.sin(w * 0.7F) * 2.5F * DEG;
                if (finL != null) finL.zRot += Mth.sin(w) * 8.0F * DEG;
                if (finR != null) finR.zRot -= Mth.sin(w) * 8.0F * DEG;
                if (periscope != null) periscope.yRot += Mth.sin(sec * 0.9F) * 45.0F * DEG;
                if (prop != null) prop.zRot += sec * 7.0F;
                if (eyeIris != null) eyeIris.zRot += sec * 0.9F;
                if (eyeOuter != null) eyeOuter.zRot -= sec * 0.5F;
            }
        }
        // Synchronize Drowned Captain & twin control levers with current maneuver
        float lAng = 0.0F;
        float rAng = 0.0F;
        float headYaw = Mth.sin(sec * 1.1F) * 16.0F * DEG;
        float headPitch = 0.0F;
        switch (clip) {
            case "dormant_sunken" -> { lAng = 18.0F * DEG; rAng = 18.0F * DEG; headPitch = 28.0F * DEG; headYaw = 0.0F; }
            case "swim_patrol" -> { lAng = -16.0F * DEG + Mth.sin(sec * 3.6F) * 12.0F * DEG; rAng = -16.0F * DEG - Mth.sin(sec * 3.6F) * 12.0F * DEG; }
            case "hydro_jet_ram", "sonar_breach" -> { lAng = -34.0F * DEG; rAng = -34.0F * DEG; headPitch = -12.0F * DEG; headYaw = 0.0F; }
            case "torpedo_salvo" -> { lAng = Mth.sin(sec * 12.0F) * 24.0F * DEG; rAng = -Mth.sin(sec * 12.0F) * 24.0F * DEG; }
            case "phase2_overdrive", "dorsal_railgun" -> { lAng = 32.0F * DEG; rAng = 32.0F * DEG; headPitch = -18.0F * DEG; headYaw = 0.0F; }
            case "core_overheat_stun" -> { lAng = 22.0F * DEG; rAng = -22.0F * DEG; headPitch = 26.0F * DEG; headYaw = Mth.sin(sec * 14.0F) * 18.0F * DEG; }
            default -> { lAng = Mth.sin(sec * 2.1F) * 8.0F * DEG; rAng = -Mth.sin(sec * 2.1F) * 8.0F * DEG; }
        }
        if (leverL != null) leverL.xRot += lAng;
        if (leverR != null) leverR.xRot += rAng;
        if (drownedArmL != null) drownedArmL.xRot += lAng * 0.65F;
        if (drownedArmR != null) drownedArmR.xRot += rAng * 0.65F;
        if (drownedHead != null) { drownedHead.yRot += headYaw; drownedHead.xRot += headPitch; }

        // Continuous spinning Fontaine brass clockwork gears (faster in Phase 2 Overdrive!)
        float gearSpeed = "dormant_sunken".equals(clip) ? 0.0F : (phase2 ? 4.5F : 2.2F);
        if (gearL != null) gearL.xRot += sec * gearSpeed;
        if (gearR != null) gearR.xRot -= sec * gearSpeed;
        if (bellyGear != null) bellyGear.xRot += sec * gearSpeed * 0.85F;
        if (bellyGear2 != null) bellyGear2.xRot -= sec * gearSpeed * 1.15F;

        // Retractable Twin Hydraulic Pincer Claws: folded flush in cruise, deployed & snapping in combat!
        boolean deployClaws = "harpoon_tether".equals(clip) || "hydro_jet_ram".equals(clip)
                || "fin_blade_sweep".equals(clip) || "phase2_overdrive".equals(clip);
        if (!deployClaws) {
            if (harpoonL != null) { harpoonL.yRot -= 145.0F * DEG; harpoonL.z += 2.0F; }
            if (harpoonR != null) { harpoonR.yRot += 145.0F * DEG; harpoonR.z += 2.0F; }
            if (clawWristL != null) clawWristL.yRot -= 25.0F * DEG;
            if (clawWristR != null) clawWristR.yRot += 25.0F * DEG;
        } else {
            float snap = 14.0F + 16.0F * Math.abs(Mth.sin(sec * 5.0F));
            if (jawTopL != null) jawTopL.xRot -= snap * DEG;
            if (jawBotL != null) jawBotL.xRot += snap * DEG;
            if (jawTopR != null) jawTopR.xRot -= snap * DEG;
            if (jawBotR != null) jawBotR.xRot += snap * DEG;
        }
    }

    private static void applyRailgunDeploy(ModelPart bayL, ModelPart bayR, ModelPart railBase, float deploy) {
        if (bayL != null) {
            bayL.x -= deploy * 3.5F;
            bayL.zRot += deploy * 35.0F * DEG;
        }
        if (bayR != null) {
            bayR.x += deploy * 3.5F;
            bayR.zRot -= deploy * 35.0F * DEG;
        }
        if (railBase != null) {
            // In Mojang LayerDefinition coords, -Y is UP
            railBase.y -= deploy * 11.5F;
        }
    }

    private static float smoothstep(float x) {
        float c = Mth.clamp(x, 0.0F, 1.0F);
        return c * c * (3.0F - 2.0F * c);
    }
}
