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

/**
 * Renderer for the Pilotable Ruin Mech:
 * <ul>
 *   <li>Live 5-part outdoor assembly bone toggling.</li>
 *   <li>Upper-body turret aim tracking (+-38 deg) while heavy legs pivot slowly behind.</li>
 *   <li>Seamless 58-tick AAA Rear-Reactor Redstone Block Reload choreography anchored strictly
 *       in {@code torso} local bone space (zero posture popping, real textured {@link Blocks#REDSTONE_BLOCK}).</li>
 *   <li>Unobstructed 1st-person cockpit visibility.</li>
 * </ul>
 */
public class RuinMechRenderer extends EntityRenderer<RuinMechEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final float MECH_SCALE = 1.12F;

    private final RuinGuardModel<RuinMechEntity> model;

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

        RuinGuardAnimations.apply(model, entity.getBaseClipName(), currSec);

        // Upper-body turret aim lag: torso tracks pilot camera (+-38 deg) while heavy legs pivot slowly behind
        if (animId == RuinMechEntity.ANIM_IDLE || animId == RuinMechEntity.ANIM_WALK) {
            float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
            float torsoTwist = Mth.clamp(Mth.wrapDegrees(headYaw - yaw), -38.0F, 38.0F);
            ModelPart waist = model.getBone("waist");
            if (waist != null) {
                waist.yRot += torsoTwist * Mth.DEG_TO_RAD;
            }
        }

        // Procedural cockpit entry animation (kneel -> open -> lock upright)
        if (animId == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float p = Mth.clamp(animTicksF / 24.0F, 0.0F, 1.0F);
            float dip = Mth.sin(p * Mth.PI);
            ModelPart pelvis = model.getBone("pelvis");
            ModelPart torso = model.getBone("torso");
            if (pelvis != null) pelvis.y += dip * 6.5F;
            if (torso != null) torso.xRot += dip * 0.28F;
        }

        // 58-tick (2.9s) AAA Mechanical Rear-Reactor Redstone Block Reload Choreography
        // Seamlessly layered over dormant_kneeling -> ZERO posture popping at start or end!
        float hatchOpen = 0.0F;
        float shroudOpen = 0.0F;
        float cellVisible = 0.0F;
        float cellSlideZ = 16.0F;
        float cellScale = 0.42F;
        float socketGlow = 0.0F;

        if (animId == RuinMechEntity.ANIM_RECHARGE_BACK) {
            // 1. Maintenance torso forward-lean envelope (0..12 lean in, 12..44 hold, 44..58 settle cleanly to 0)
            float leanEnv;
            if (animTicksF < 12.0F) leanEnv = easeInOutCubic(animTicksF / 12.0F);
            else if (animTicksF < 44.0F) leanEnv = 1.0F;
            else leanEnv = 1.0F - easeInOutCubic((animTicksF - 44.0F) / 14.0F);

            // 2. Shoulder missile shrouds split wide (8..20 open with easeOutBack, 42..52 clamp shut)
            if (animTicksF >= 8.0F && animTicksF < 20.0F) {
                shroudOpen = easeOutBack((animTicksF - 8.0F) / 12.0F);
            } else if (animTicksF >= 20.0F && animTicksF < 42.0F) {
                shroudOpen = 1.0F;
            } else if (animTicksF >= 42.0F && animTicksF < 52.0F) {
                shroudOpen = 1.0F - easeInOutCubic((animTicksF - 42.0F) / 10.0F);
            }

            // 3. Rear reactor hatch unlatches & hinges backward/downward (12..23 open, 39..48 slam shut)
            if (animTicksF >= 12.0F && animTicksF < 23.0F) {
                hatchOpen = easeOutBack((animTicksF - 12.0F) / 11.0F);
            } else if (animTicksF >= 23.0F && animTicksF < 39.0F) {
                hatchOpen = 1.0F;
            } else if (animTicksF >= 39.0F && animTicksF < 48.0F) {
                hatchOpen = 1.0F - easeOutExpo((animTicksF - 39.0F) / 9.0F);
            }

            // 4. Recoil jolts on lock-in (t=36) and hatch slam (t=47)
            float recoil = 0.0F;
            if (animTicksF >= 35.0F && animTicksF < 43.0F) {
                float rp = (animTicksF - 35.0F) / 8.0F;
                recoil += Mth.sin(rp * Mth.PI * 2.0F) * (1.0F - rp) * 0.11F;
            }
            if (animTicksF >= 46.0F && animTicksF < 53.0F) {
                float sp = (animTicksF - 46.0F) / 7.0F;
                recoil += Mth.sin(sp * Mth.PI) * (1.0F - sp) * 0.06F;
            }

            // Apply layered multi-bone mechanical offsets
            ModelPart pelvis = model.getBone("pelvis");
            ModelPart waist = model.getBone("waist");
            ModelPart torso = model.getBone("torso");
            ModelPart eye = model.getBone("eye_assembly");
            ModelPart reactor = model.getBone("back_reactor");
            ModelPart podL = model.getBone("missile_pod_l");
            ModelPart podR = model.getBone("missile_pod_r");
            ModelPart shL = model.getBone("shoulder_l");
            ModelPart shR = model.getBone("shoulder_r");

            if (pelvis != null) pelvis.y += leanEnv * 1.8F + recoil * 4.0F;
            if (waist != null) waist.xRot += leanEnv * 0.14F + recoil * 0.5F;
            if (torso != null) torso.xRot += leanEnv * 0.18F + recoil;
            if (eye != null) eye.xRot += leanEnv * 0.16F;
            if (shL != null) { shL.zRot += leanEnv * 0.14F; shL.xRot -= leanEnv * 0.10F; }
            if (shR != null) { shR.zRot -= leanEnv * 0.14F; shR.xRot -= leanEnv * 0.10F; }

            if (podL != null) {
                podL.zRot += shroudOpen * 0.54F;
                podL.yRot -= shroudOpen * 0.28F;
            }
            if (podR != null) {
                podR.zRot -= shroudOpen * 0.54F;
                podR.yRot += shroudOpen * 0.28F;
            }
            if (reactor != null) {
                reactor.xRot += hatchOpen * 1.18F;
                reactor.z += hatchOpen * 4.2F;
                reactor.y += hatchOpen * 1.4F;
            }

            // 5. Real 3D Redstone Block 2-stage mechanical insertion (t=18..38)
            socketGlow = Mth.clamp(hatchOpen, 0.0F, 1.0F);
            if (animTicksF >= 18.0F && animTicksF <= 38.0F) {
                cellVisible = 1.0F;
                if (animTicksF < 26.0F) {
                    // Stage A: Approach & align cleanly behind the open rear bay
                    float p = easeInOutCubic((animTicksF - 18.0F) / 8.0F);
                    cellSlideZ = Mth.lerp(p, 18.0F, 6.5F);
                    cellScale = Mth.lerp(p, 0.18F, 0.44F);
                } else if (animTicksF < 30.0F) {
                    // Stage B: Micro-pause at socket rim with subtle magnetic hum vibration
                    float vib = Mth.sin((animTicksF - 26.0F) * 5.0F) * 0.25F;
                    cellSlideZ = 6.5F + vib;
                    cellScale = 0.44F;
                } else if (animTicksF < 36.0F) {
                    // Stage C: Snappy hydraulic slam home into the reactor core!
                    float p = easeOutExpo((animTicksF - 30.0F) / 6.0F);
                    cellSlideZ = Mth.lerp(p, 6.5F, -0.5F);
                    cellScale = Mth.lerp(p, 0.44F, 0.36F);
                } else {
                    // Seated inside glowing bay as hatch begins closing
                    cellSlideZ = -0.5F;
                    cellScale = 0.36F;
                }
            }
        }

        // Toggle limb visibility based on outdoor 5-part assembly mask
        setBoneVisible("shoulder_l", entity.hasPart(RuinMechEntity.PART_LEFT_ARM));
        setBoneVisible("shoulder_r", entity.hasPart(RuinMechEntity.PART_RIGHT_ARM));
        setBoneVisible("hip_l", entity.hasPart(RuinMechEntity.PART_LEFT_LEG));
        setBoneVisible("hip_r", entity.hasPart(RuinMechEntity.PART_RIGHT_LEG));

        // Unobstructed 1st-person cockpit view
        setBoneVisible("eye_assembly", !firstPersonPilot);
        setBoneVisible("back_reactor", !firstPersonPilot);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Glow emissive eye & reactor whenever assembled and powered (or surging during reload)
        if (entity.isFullyAssembled() && (entity.getEnergy() > 0.1F || (animId == RuinMechEntity.ANIM_RECHARGE_BACK && animTicksF >= 36.0F))) {
            VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        // Render REAL 3D textured Redstone Block + internal socket glow strictly locked to torso local bone space!
        if (socketGlow > 0.05F || cellVisible > 0.05F) {
            poseStack.pushPose();
            transformToBone(poseStack, "root");
            transformToBone(poseStack, "pelvis");
            transformToBone(poseStack, "waist");
            transformToBone(poseStack, "torso");

            // Anchor at exact back_reactor coordinates inside torso: (0, -12/16, +11/16) -> TRUE BACK!
            poseStack.translate(0.0D, -12.0D / 16.0D, 11.0D / 16.0D);

            // Warm golden-red internal reactor socket glow behind the open hatch
            if (socketGlow > 0.05F) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, -0.04D);
                drawSocketGlow(bufferSource.getBuffer(RenderType.lightning()), poseStack.last().pose(), 0.24F * socketGlow);
                poseStack.popPose();
            }

            // Real textured Minecraft Redstone Block sliding cleanly into the rear reactor bay
            if (cellVisible > 0.05F) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, cellSlideZ / 16.0D);
                // Undo model Y-flip so block textures orient upright
                poseStack.scale(cellScale, -cellScale, cellScale);
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

    private static void drawSocketGlow(VertexConsumer vc, Matrix4f m, float s) {
        // Compact glowing amber-crimson core recess inside the open back reactor
        vc.addVertex(m, -s, -s, 0.0F).setColor(255, 110, 35, 190);
        vc.addVertex(m,  s, -s, 0.0F).setColor(255, 110, 35, 190);
        vc.addVertex(m,  s,  s, 0.0F).setColor(255, 210, 75, 210);
        vc.addVertex(m, -s,  s, 0.0F).setColor(255, 210, 75, 210);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinMechEntity entity) {
        return TEXTURE;
    }
}
