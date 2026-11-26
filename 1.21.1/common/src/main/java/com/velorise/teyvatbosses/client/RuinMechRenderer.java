package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;
import java.util.HashMap;
import java.util.Map;

/**
 * Renderer for the Pilotable Ruin Mech:
 * <ul>
 *   <li>Accurate anatomical 5-part outdoor assembly bone toggling (accounting for Mojang X-mirroring).</li>
 *   <li>Zero-lag walk animation + upper-body turret aim tracking (+-38 deg).</li>
 *   <li>58-tick AAA Rear-Reactor Redstone Block Reload: level service crouch, shoulder pods slide outward,
 *       rear hatch hinges downward/backward, glowing magnetic rail pulls a real 3D textured Redstone Block
 *       horizontally into the illuminated back socket with recoil & power surge.</li>
 *   <li>Unobstructed 1st-person cockpit visibility.</li>
 * </ul>
 */
public class RuinMechRenderer extends EntityRenderer<RuinMechEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final float MECH_SCALE = 1.12F;

    private final RuinGuardModel<RuinMechEntity> model;
    private final Map<Integer, SmoothState> smoothStates = new HashMap<>();

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot, xScale, yScale, zScale;
        void copyFrom(ModelPart p) {
            this.x = p.x; this.y = p.y; this.z = p.z;
            this.xRot = p.xRot; this.yRot = p.yRot; this.zRot = p.zRot;
            this.xScale = p.xScale; this.yScale = p.yScale; this.zScale = p.zScale;
        }
    }

    private static final class SmoothState {
        String lastClip = null;
        float blendStartTick = -100.0F;
        float blendDurationTicks = 5.0F;
        boolean hasLastPose = false;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();
    }

    private static float lerpAngleRad(float alpha, float from, float to) {
        float diff = (to - from) % Mth.TWO_PI;
        if (diff > Mth.PI) diff -= Mth.TWO_PI;
        if (diff < -Mth.PI) diff += Mth.TWO_PI;
        return from + diff * alpha;
    }

    public RuinMechRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.45F;
        this.model = new RuinGuardModel<>(context.bakeLayer(RuinGuardModel.LAYER_LOCATION));
    }

    private static float easeInOutCubic(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3.0F) * 0.5F;
    }

    private static float easeOutBack(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        return 1.0F + c3 * (float) Math.pow(t - 1.0F, 3.0F) + c1 * (float) Math.pow(t - 1.0F, 2.0F);
    }

    private static float easeOutExpo(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        return t >= 1.0F ? 1.0F : 1.0F - (float) Math.pow(2.0F, -10.0F * t);
    }

    @Override
    public void render(RuinMechEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float animTicksF = entity.getAnimTick() + partialTicks;
        float currSec = animTicksF / 20.0F;
        int animId = entity.getAnimId();

        Minecraft mc = Minecraft.getInstance();
        boolean firstPersonPilot = mc.player != null
                && entity.hasPassenger(mc.player)
                && mc.options.getCameraType().isFirstPerson();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-MECH_SCALE, -MECH_SCALE, MECH_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        // Zero-lag walk clip fallback while local pilot is steering
        String clipName = entity.getBaseClipName();
        float clipSec = currSec;
        if (animId == RuinMechEntity.ANIM_SUPER_LEAP) {
            // Map 46-tick supercharged flight (coil 0..6, flight 6..31, recover 31..46) cleanly onto 38-tick mobility_leap clip
            if (animTicksF <= 6.0F) clipSec = animTicksF / 20.0F;
            else if (animTicksF <= 31.0F) clipSec = (6.0F + ((animTicksF - 6.0F) / 25.0F) * 17.0F) / 20.0F;
            else clipSec = (23.0F + Math.min(15.0F, animTicksF - 31.0F)) / 20.0F;
        }
        if (animId == RuinMechEntity.ANIM_IDLE && entity.isVehicle()
                && entity.getDeltaMovement().horizontalDistanceSqr() > 0.00035D) {
            clipName = "walk_combat";
            clipSec = (entity.tickCount + partialTicks) / 20.0F;
        }
        RuinGuardAnimations.apply(model, clipName, clipSec);

        // Upper-body turret aim lag: torso tracks pilot camera (+-38 deg) while heavy legs pivot slowly behind
        if (animId == RuinMechEntity.ANIM_IDLE || animId == RuinMechEntity.ANIM_WALK) {
            float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
            float torsoTwist = Mth.clamp(Mth.wrapDegrees(headYaw - yaw), -38.0F, 38.0F);
            ModelPart waist = model.getBone("waist");
            if (waist != null) {
                waist.yRot += torsoTwist * Mth.DEG_TO_RAD;
            }
        }

        // Procedural cockpit entry animation
        if (animId == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float p = Mth.clamp(animTicksF / 26.0F, 0.0F, 1.0F);
            float dip = Mth.sin(p * Mth.PI);
            ModelPart pelvis = model.getBone("pelvis");
            ModelPart torso = model.getBone("torso");
            if (pelvis != null) pelvis.y += dip * 6.5F;
            if (torso != null) torso.xRot += dip * 0.28F;
        }

        // 58-tick (2.9s) AAA Mechanical Rear-Reactor Redstone Block Reload Choreography
        float hatchOpen = 0.0F;
        float shroudOpen = 0.0F;
        float cellVisible = 0.0F;
        float cellSlideZ = 18.0F;
        float cellScale = 0.56F;
        float socketGlow = 0.0F;
        float beamAlpha = 0.0F;

        if (animId == RuinMechEntity.ANIM_RECHARGE_BACK) {
            // 1. Smooth service posture envelope: straighten torso slightly from deep dormant kneel so rear bay faces HORIZONTALLY!
            float env;
            if (animTicksF < 12.0F) env = easeInOutCubic(animTicksF / 12.0F);
            else if (animTicksF < 44.0F) env = 1.0F;
            else env = 1.0F - easeInOutCubic((animTicksF - 44.0F) / 14.0F);

            // 2. Shoulder missile shrouds slide outward on mechanical rails (8..20 open, 42..52 lock shut)
            if (animTicksF >= 8.0F && animTicksF < 20.0F) {
                shroudOpen = easeOutBack((animTicksF - 8.0F) / 12.0F);
            } else if (animTicksF >= 20.0F && animTicksF < 42.0F) {
                shroudOpen = 1.0F;
            } else if (animTicksF >= 42.0F && animTicksF < 52.0F) {
                shroudOpen = 1.0F - easeInOutCubic((animTicksF - 42.0F) / 10.0F);
            }

            // 3. Rear reactor hatch hinges WIDE UPWARD out of the battery path BEFORE the Redstone Block approaches (8..18 open, 40..49 slam shut)
            if (animTicksF >= 8.0F && animTicksF < 18.0F) {
                hatchOpen = easeOutBack((animTicksF - 8.0F) / 10.0F);
            } else if (animTicksF >= 18.0F && animTicksF < 40.0F) {
                hatchOpen = 1.0F;
            } else if (animTicksF >= 40.0F && animTicksF < 49.0F) {
                hatchOpen = 1.0F - easeOutExpo((animTicksF - 40.0F) / 9.0F);
            }

            // 4. Crisp mechanical recoil jolts at cartridge lock-in (t=36) and hatch seal (t=47)
            float recoil = 0.0F;
            if (animTicksF >= 35.0F && animTicksF < 43.0F) {
                float rp = (animTicksF - 35.0F) / 8.0F;
                recoil += Mth.sin(rp * Mth.PI * 2.0F) * (1.0F - rp) * 0.12F;
            }
            if (animTicksF >= 46.0F && animTicksF < 53.0F) {
                float sp = (animTicksF - 46.0F) / 7.0F;
                recoil += Mth.sin(sp * Mth.PI) * (1.0F - sp) * 0.06F;
            }

            ModelPart pelvis = model.getBone("pelvis");
            ModelPart waist = model.getBone("waist");
            ModelPart torso = model.getBone("torso");
            ModelPart reactor = model.getBone("back_reactor");
            ModelPart podL = model.getBone("missile_pod_l");
            ModelPart podR = model.getBone("missile_pod_r");
            ModelPart shL = model.getBone("shoulder_l");
            ModelPart shR = model.getBone("shoulder_r");

            // Level the kneeling torso slightly (-xRot) so the rear reactor bay faces straight back at eye level
            if (pelvis != null) pelvis.y += recoil * 3.5F;
            if (waist != null) waist.xRot -= env * 0.14F - recoil * 0.45F;
            if (torso != null) torso.xRot -= env * 0.20F - recoil;
            if (shL != null) shL.zRot -= env * 0.12F;
            if (shR != null) shR.zRot += env * 0.12F;

            // Slide + angle shoulder shrouds cleanly outward away from the back bay
            if (podL != null) {
                podL.x -= shroudOpen * 4.2F;
                podL.zRot -= shroudOpen * 0.48F;
                podL.yRot += shroudOpen * 0.25F;
            }
            if (podR != null) {
                podR.x += shroudOpen * 4.2F;
                podR.zRot += shroudOpen * 0.48F;
                podR.yRot -= shroudOpen * 0.25F;
            }
            // Swing hatch UPWARD (+112 deg) and tuck above the rear bay so it NEVER collides with the incoming Redstone Block!
            if (reactor != null) {
                reactor.xRot += hatchOpen * 1.95F;
                reactor.y -= hatchOpen * 4.8F;
                reactor.z += hatchOpen * 1.4F;
            }

            // 5. Large textured 3D Redstone Core Cartridge sliding horizontally into the hollowed wired chamber (t=20..38)
            socketGlow = Mth.clamp(hatchOpen, 0.0F, 1.0F);
            if (animTicksF >= 20.0F && animTicksF <= 38.0F) {
                cellVisible = 1.0F;
                if (animTicksF < 27.0F) {
                    float p = easeInOutCubic((animTicksF - 20.0F) / 7.0F);
                    cellSlideZ = Mth.lerp(p, 20.0F, 8.5F);
                    cellScale = Mth.lerp(p, 0.25F, 0.56F);
                    beamAlpha = p;
                } else if (animTicksF < 30.0F) {
                    // Magnetic lock hover right in front of open bay
                    float vib = Mth.sin((animTicksF - 26.0F) * 5.2F) * 0.30F;
                    cellSlideZ = 8.5F + vib;
                    cellScale = 0.56F;
                    beamAlpha = 1.0F;
                } else if (animTicksF < 36.0F) {
                    // Hydraulic slam straight into reactor chamber
                    float p = easeOutExpo((animTicksF - 30.0F) / 6.0F);
                    cellSlideZ = Mth.lerp(p, 8.5F, 0.0F);
                    cellScale = Mth.lerp(p, 0.56F, 0.44F);
                    beamAlpha = 1.0F - p;
                } else {
                    cellSlideZ = 0.0F;
                    cellScale = 0.44F;
                    beamAlpha = 0.0F;
                }
            }
        }

        // Smooth 28-bone shortest-arc pose-snapshot cross-fade (eliminates snapping when starting/stopping walk or skills!)
        float liveTick = entity.tickCount + partialTicks;
        SmoothState sm = smoothStates.computeIfAbsent(entity.getId(), k -> new SmoothState());
        if (sm.lastClip != null && !sm.lastClip.equals(clipName) && sm.hasLastPose) {
            for (Map.Entry<String, BonePose> entry : sm.lastPose.entrySet()) {
                BonePose dst = sm.fromPose.computeIfAbsent(entry.getKey(), k -> new BonePose());
                BonePose srcPose = entry.getValue();
                dst.x = srcPose.x; dst.y = srcPose.y; dst.z = srcPose.z;
                dst.xRot = srcPose.xRot; dst.yRot = srcPose.yRot; dst.zRot = srcPose.zRot;
                dst.xScale = srcPose.xScale; dst.yScale = srcPose.yScale; dst.zScale = srcPose.zScale;
            }
            sm.blendStartTick = liveTick;
            sm.blendDurationTicks = "idle_combat".equals(clipName) ? 6.0F : 4.5F;
        }
        sm.lastClip = clipName;

        float blendElapsed = liveTick - sm.blendStartTick;
        if (sm.hasLastPose && blendElapsed >= 0.0F && blendElapsed < sm.blendDurationTicks) {
            float rawA = Mth.clamp(blendElapsed / sm.blendDurationTicks, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String bName : model.getBoneNames()) {
                ModelPart part = model.getBone(bName);
                BonePose prev = sm.fromPose.get(bName);
                if (part == null || prev == null) continue;
                part.x = Mth.lerp(alpha, prev.x, part.x);
                part.y = Mth.lerp(alpha, prev.y, part.y);
                part.z = Mth.lerp(alpha, prev.z, part.z);
                part.xRot = lerpAngleRad(alpha, prev.xRot, part.xRot);
                part.yRot = lerpAngleRad(alpha, prev.yRot, part.yRot);
                part.zRot = lerpAngleRad(alpha, prev.zRot, part.zRot);
                part.xScale = Mth.lerp(alpha, prev.xScale, part.xScale);
                part.yScale = Mth.lerp(alpha, prev.yScale, part.yScale);
                part.zScale = Mth.lerp(alpha, prev.zScale, part.zScale);
            }
        }
        for (String bName : model.getBoneNames()) {
            ModelPart part = model.getBone(bName);
            if (part != null) {
                sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).copyFrom(part);
            }
        }
        sm.hasLastPose = true;

        // Exact anatomical 5-part bone visibility (in RuinGuardModel, +X = shoulder_r/hip_r = robot's TRUE LEFT,
        // and -X = shoulder_l/hip_l = robot's TRUE RIGHT after Mojang's scale(-1,-1,1) + 180° Y-rot!)
        setBoneVisible("shoulder_r", entity.hasPart(RuinMechEntity.PART_LEFT_ARM));
        setBoneVisible("shoulder_l", entity.hasPart(RuinMechEntity.PART_RIGHT_ARM));
        setBoneVisible("hip_r", entity.hasPart(RuinMechEntity.PART_LEFT_LEG));
        setBoneVisible("hip_l", entity.hasPart(RuinMechEntity.PART_RIGHT_LEG));

        // Dynamic 6-tube shoulder missile warhead sync + pop-out launch animation!
        int ammo = entity.getMissileAmmo();
        for (int i = 0; i < 6; i++) {
            ModelPart tip = model.getBone("missile_tip_" + i);
            if (tip == null) continue;
            if (animId == RuinMechEntity.ANIM_MISSILE_SALVO) {
                float fireTick = 16.0F + i * 3.0F;
                if (animTicksF < fireTick) {
                    tip.visible = true;
                } else if (animTicksF < fireTick + 3.5F) {
                    // Pop upward out of the exact shoulder launch tube as it ignites!
                    tip.visible = true;
                    float pop = (animTicksF - fireTick) / 3.5F;
                    tip.y -= pop * 5.5F;
                } else {
                    tip.visible = i < ammo;
                }
            } else {
                tip.visible = i < ammo;
            }
        }

        // Unobstructed 1st-person cockpit view
        setBoneVisible("eye_assembly", !firstPersonPilot);
        setBoneVisible("back_reactor", !firstPersonPilot);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        if (entity.isFullyAssembled() && (entity.getEnergy() > 0.1F || (animId == RuinMechEntity.ANIM_RECHARGE_BACK && animTicksF >= 36.0F))) {
            VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        // Render glowing back socket + magnetic tractor beam + REAL textured 3D Redstone Block in torso local space!
        if (socketGlow > 0.05F || cellVisible > 0.05F) {
            poseStack.pushPose();
            transformToBone(poseStack, "root");
            transformToBone(poseStack, "pelvis");
            transformToBone(poseStack, "waist");
            transformToBone(poseStack, "torso");

            // Anchor at exact back_reactor coordinates inside torso: (0, -12/16, +11/16) -> TRUE BACK!
            poseStack.translate(0.0D, -12.0D / 16.0D, 11.0D / 16.0D);

            if (socketGlow > 0.05F) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, -0.02D);
                // Recessed hollowed dark battery chamber + internal copper power cables + brass contact coils
                drawRecessedBatteryBay(bufferSource.getBuffer(RenderType.debugQuads()), poseStack.last().pose(), socketGlow);
                drawSocketGlow(bufferSource.getBuffer(RenderType.lightning()), poseStack.last().pose(), 0.28F * socketGlow);
                poseStack.popPose();
            }

            if (beamAlpha > 0.05F) {
                drawTractorBeam(bufferSource.getBuffer(RenderType.lightning()), poseStack.last().pose(), cellSlideZ / 16.0F, beamAlpha);
            }

            if (cellVisible > 0.05F) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, cellSlideZ / 16.0D);
                // Cancel outer Mojang scale(-MECH_SCALE, -MECH_SCALE, MECH_SCALE) with (-s, -s, +s) so determinant is +1
                // -> prevents inverted backface culling & distortion on all 6 faces of the real 3D Redstone Block!
                float spinDeg = (animTicksF < 30.0F) ? (animTicksF - 17.0F) * 6.92F : 90.0F;
                poseStack.mulPose(Axis.ZP.rotationDegrees(spinDeg));
                float uniformScale = 0.48F;
                poseStack.scale(-uniformScale, -uniformScale, uniformScale);
                poseStack.translate(-0.5D, -0.5D, -0.5D);
                mc.getBlockRenderer().renderSingleBlock(
                        Blocks.REDSTONE_BLOCK.defaultBlockState(),
                        poseStack,
                        bufferSource,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY
                );
                poseStack.popPose();
            }

            poseStack.popPose();
        }

        // Restore visibility flags
        setBoneVisible("shoulder_l", true);
        setBoneVisible("shoulder_r", true);
        setBoneVisible("hip_l", true);
        setBoneVisible("hip_r", true);
        setBoneVisible("eye_assembly", true);
        setBoneVisible("back_reactor", true);
        for (int i = 0; i < 6; i++) setBoneVisible("missile_tip_" + i, true);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void transformToBone(PoseStack poseStack, String name) {
        ModelPart part = model.getBone(name);
        if (part != null) {
            part.translateAndRotate(poseStack);
        }
    }

    private void setBoneVisible(String name, boolean vis) {
        ModelPart part = model.getBone(name);
        if (part != null) part.visible = vis;
    }

    /**
     * Renders a recessed hollowed dark battery chamber inside the rear reactor bay,
     * complete with dark inner cavity walls, 4 braided copper/redstone power cables,
     * and glowing brass terminal rails so the bay visibly looks hollowed out.
     */
    private static void drawRecessedBatteryBay(VertexConsumer vc, Matrix4f m, float openAlpha) {
        int a = (int) (Mth.clamp(openAlpha, 0.0F, 1.0F) * 245.0F);
        float r = 0.28F;
        float depth = -0.22F;
        // 1. Deep dark recessed backplate (gives strong hollowed-hole depth illusion)
        vc.addVertex(m, -r, -r, 0.03F).setColor(10, 12, 16, a);
        vc.addVertex(m,  r, -r, 0.03F).setColor(10, 12, 16, a);
        vc.addVertex(m,  r,  r, 0.03F).setColor(16, 18, 24, a);
        vc.addVertex(m, -r,  r, 0.03F).setColor(16, 18, 24, a);

        // 2. Inner recessed metallic frame border (top, bottom, left, right walls)
        float b = 0.045F;
        // Top & bottom brass contact rails
        drawBayQuad(vc, m, -r, -r, r, -r + b, 0.04F, 168, 124, 58, a);
        drawBayQuad(vc, m, -r, r - b, r, r, 0.04F, 168, 124, 58, a);
        // Left & right dark iron socket guides
        drawBayQuad(vc, m, -r, -r, -r + b, r, 0.04F, 52, 56, 64, a);
        drawBayQuad(vc, m, r - b, -r, r, r, 0.04F, 52, 56, 64, a);

        // 3. 4 Internal Copper & Redstone Power Cables running from corners into the center core
        drawBayQuad(vc, m, -r + b, -0.14F, -0.06F, -0.10F, 0.045F, 186, 98, 56, a);
        drawBayQuad(vc, m,  0.06F, -0.14F,  r - b, -0.10F, 0.045F, 186, 98, 56, a);
        drawBayQuad(vc, m, -r + b,  0.10F, -0.06F,  0.14F, 0.045F, 210, 48, 42, a);
        drawBayQuad(vc, m,  0.06F,  0.10F,  r - b,  0.14F, 0.045F, 210, 48, 42, a);
        // Vertical central power conduit busbar
        drawBayQuad(vc, m, -0.025F, -r + b, 0.025F, r - b, 0.042F, 130, 82, 42, a);
    }

    private static void drawBayQuad(VertexConsumer vc, Matrix4f m, float x0, float y0, float x1, float y1, float z, int r, int g, int b, int a) {
        vc.addVertex(m, x0, y0, z).setColor(r, g, b, a);
        vc.addVertex(m, x1, y0, z).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z).setColor(r, g, b, a);
    }

    private static void drawSocketGlow(VertexConsumer vc, Matrix4f m, float s) {
        vc.addVertex(m, -s, -s, 0.0F).setColor(255, 80, 25, 210);
        vc.addVertex(m,  s, -s, 0.0F).setColor(255, 80, 25, 210);
        vc.addVertex(m,  s,  s, 0.0F).setColor(255, 215, 70, 230);
        vc.addVertex(m, -s,  s, 0.0F).setColor(255, 215, 70, 230);
    }

    private static void drawTractorBeam(VertexConsumer vc, Matrix4f m, float lenZ, float alpha) {
        int a = (int) (135 * alpha);
        float w = 0.09F;
        // Horizontal & vertical glowing magnetic guide rail pulling the Redstone Block straight into the core
        vc.addVertex(m, -w, 0.0F, 0.0F).setColor(255, 65, 45, a);
        vc.addVertex(m,  w, 0.0F, 0.0F).setColor(255, 65, 45, a);
        vc.addVertex(m,  w, 0.0F, lenZ).setColor(255, 200, 90, a / 2);
        vc.addVertex(m, -w, 0.0F, lenZ).setColor(255, 200, 90, a / 2);

        vc.addVertex(m, 0.0F, -w, 0.0F).setColor(255, 65, 45, a);
        vc.addVertex(m, 0.0F,  w, 0.0F).setColor(255, 65, 45, a);
        vc.addVertex(m, 0.0F,  w, lenZ).setColor(255, 200, 90, a / 2);
        vc.addVertex(m, 0.0F, -w, lenZ).setColor(255, 200, 90, a / 2);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinMechEntity entity) {
        return TEXTURE;
    }
}
