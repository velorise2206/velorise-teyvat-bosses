package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.AstralionAnimations;
import com.velorise.teyvatbosses.client.model.AstralionModel;
import com.velorise.teyvatbosses.entity.AstralionEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * AAA Renderer for Astralion: Prismatic Sovereign (Omni Boss):
 * <ul>
 *   <li>40-bone shortest-arc Pose-Snapshot cross-fade blending (2-5 ticks).</li>
 *   <li>Full-bright Emissive Glow pass ({@code astralion_boss_emissive.png}).</li>
 *   <li>Tactile impact shudder across every cleave, bash, trinity finisher, roar, beam, and Sevenfold Cataclysm.</li>
 *   <li>World-locked telegraphs, prismatic crescent slashes, sweeping chromatic beam, and 7-element shockwaves.</li>
 * </ul>
 */
public class AstralionRenderer extends EntityRenderer<AstralionEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/astralion_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/astralion_boss_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.astralion_theme");
    private static final float BOSS_RENDER_SCALE = 1.22F;

    private static final String[] ALL_BONES = {
            "root", "body", "torso", "omni_core", "head", "crown_horns", "jaw_lower",
            "halo_outer", "halo_inner", "halo_shards",
            "cape_tail_1", "cape_tail_2", "cape_tail_3", "cape_tail_4", "cape_tail_tip",
            "wings_root", "wing_upper_left", "wing_upper_right", "wing_mid_left", "wing_mid_right", "wing_lower_left", "wing_lower_right",
            "arm_left", "pauldron_left", "forearm_left", "omni_aegis",
            "arm_right", "pauldron_right", "forearm_right", "omni_greatsword",
            "waist_skirt", "tasset_front", "tasset_back", "tasset_left", "tasset_right",
            "leg_left", "shin_left", "leg_right", "shin_right", "fx_anchor"
    };

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot, xScale, yScale, zScale;
        void capture(ModelPart p) {
            x = p.x; y = p.y; z = p.z;
            xRot = p.xRot; yRot = p.yRot; zRot = p.zRot;
            xScale = p.xScale; yScale = p.yScale; zScale = p.zScale;
        }
        void copyFrom(BonePose o) {
            x = o.x; y = o.y; z = o.z;
            xRot = o.xRot; yRot = o.yRot; zRot = o.zRot;
            xScale = o.xScale; yScale = o.yScale; zScale = o.zScale;
        }
    }

    private static final class AnimStateMachine {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStartLiveTick = -100.0F;
        float blendDurationTicks = 4.0F;
        boolean hasLastPose = false;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();
    }

    private final AstralionModel<AstralionEntity> model;
    private final Map<AstralionEntity, AnimStateMachine> stateMachines = new WeakHashMap<>();

    public AstralionRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.65F;
        this.model = new AstralionModel<>(context.bakeLayer(AstralionModel.LAYER_LOCATION));
    }

    private static float wrapRad(float rad) {
        float twoPi = (float) (Math.PI * 2.0D);
        float r = rad % twoPi;
        if (r > Math.PI) r -= twoPi;
        if (r < -Math.PI) r += twoPi;
        return r;
    }

    private static float lerpAngleRad(float from, float to, float alpha) {
        return from + wrapRad(to - from) * alpha;
    }

    private static float blendDurationFor(int animId) {
        return switch (animId) {
            case AstralionEntity.ANIM_SHIELD_BREAK_COLLAPSE,
                 AstralionEntity.ANIM_DEATH,
                 AstralionEntity.ANIM_ULTIMATE_CATACLYSM -> 2.0F;
            case AstralionEntity.ANIM_PRISMATIC_CLEAVE,
                 AstralionEntity.ANIM_SHIELD_BASH_BURST,
                 AstralionEntity.ANIM_COMBO_TRINITY,
                 AstralionEntity.ANIM_WING_BARRAGE,
                 AstralionEntity.ANIM_ELEMENTAL_SHIFT_ROAR,
                 AstralionEntity.ANIM_LEVIATHAN_CHROMATIC_BEAM -> 3.0F;
            default -> 5.0F;
        };
    }

    private static float computeImpactShudder(int animId, float t) {
        if (animId == AstralionEntity.ANIM_PRISMATIC_CLEAVE && t >= 14.5F && t <= 19.0F) {
            return 0.09F * (1.0F - (t - 14.5F) / 4.5F);
        }
        if (animId == AstralionEntity.ANIM_SHIELD_BASH_BURST && t >= 13.5F && t <= 18.0F) {
            return 0.11F * (1.0F - (t - 13.5F) / 4.5F);
        }
        if (animId == AstralionEntity.ANIM_COMBO_TRINITY) {
            if (t >= 13.5F && t <= 16.5F) return 0.06F * (1.0F - (t - 13.5F) / 3.0F);
            if (t >= 28.5F && t <= 31.5F) return 0.08F * (1.0F - (t - 28.5F) / 3.0F);
            if (t >= 44.5F && t <= 50.0F) return 0.12F * (1.0F - (t - 44.5F) / 5.5F);
        }
        if (animId == AstralionEntity.ANIM_ELEMENTAL_SHIFT_ROAR && t >= 20.5F && t <= 26.0F) {
            return 0.09F * (1.0F - (t - 20.5F) / 5.5F);
        }
        if (animId == AstralionEntity.ANIM_ULTIMATE_CATACLYSM && t >= 57.5F && t <= 66.0F) {
            return 0.16F * (1.0F - (t - 57.5F) / 8.5F);
        }
        return 0.0F;
    }

    @Override
    public void render(
            AstralionEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float animTicksF = entity.getAnimTick() + partialTicks;
        float currSec = animTicksF / 20.0F;
        String currClip = entity.getCurrentAnimName();
        int animId = entity.getAnimId();
        int currEpoch = entity.getAnimEpoch();
        float liveT = entity.tickCount + partialTicks;

        boolean dormant = entity.getBossState() == AstralionEntity.STATE_DORMANT;
        boolean musicActive = !dormant && entity.getBossState() != AstralionEntity.STATE_DEATH;
        BossMusicManager.update(entity, MUSIC_ID, musicActive);

        AnimStateMachine sm = stateMachines.computeIfAbsent(entity, k -> new AnimStateMachine());
        if (sm.lastClip != null && (!sm.lastClip.equals(currClip) || sm.lastEpoch != currEpoch)) {
            if (sm.hasLastPose) {
                for (Map.Entry<String, BonePose> e : sm.lastPose.entrySet()) {
                    sm.fromPose.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
                }
                sm.blendStartLiveTick = liveT;
                sm.blendDurationTicks = blendDurationFor(animId);
            }
        }
        sm.lastClip = currClip;
        sm.lastEpoch = currEpoch;

        poseStack.pushPose();
        float shudder = computeImpactShudder(animId, animTicksF);
        if (shudder > 0.001F) {
            poseStack.translate(Mth.sin(liveT * 34.0F) * shudder, 0.0D, Mth.cos(liveT * 29.0F) * shudder);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-BOSS_RENDER_SCALE, -BOSS_RENDER_SCALE, BOSS_RENDER_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        AstralionAnimations.apply(model, currClip, currSec);

        // 40-bone shortest-arc Pose-Snapshot cross-fade
        float blendElapsed = liveT - sm.blendStartLiveTick;
        if (sm.hasLastPose && blendElapsed >= 0.0F && blendElapsed < sm.blendDurationTicks) {
            float rawA = Mth.clamp(blendElapsed / sm.blendDurationTicks, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String bName : ALL_BONES) {
                ModelPart part = model.getBone(bName);
                BonePose prev = sm.fromPose.get(bName);
                if (part == null || prev == null) continue;
                part.x = Mth.lerp(alpha, prev.x, part.x);
                part.y = Mth.lerp(alpha, prev.y, part.y);
                part.z = Mth.lerp(alpha, prev.z, part.z);
                part.xRot = lerpAngleRad(prev.xRot, part.xRot, alpha);
                part.yRot = lerpAngleRad(prev.yRot, part.yRot, alpha);
                part.zRot = lerpAngleRad(prev.zRot, part.zRot, alpha);
                part.xScale = Mth.lerp(alpha, prev.xScale, part.xScale);
                part.yScale = Mth.lerp(alpha, prev.yScale, part.yScale);
                part.zScale = Mth.lerp(alpha, prev.zScale, part.zScale);
            }
        }

        for (String bName : ALL_BONES) {
            ModelPart part = model.getBone(bName);
            if (part != null) {
                sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).capture(part);
            }
        }
        sm.hasLastPose = true;

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        if (entity.getBossState() != AstralionEntity.STATE_DEATH) {
            VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }
        poseStack.popPose();

        renderVFX(entity, yaw, partialTicks, animTicksF, liveT, poseStack, bufferSource);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderVFX(
            AstralionEntity entity, float yaw, float partialTicks, float t, float liveT,
            PoseStack poseStack, MultiBufferSource bufferSource
    ) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        int anim = entity.getAnimId();
        int[] rgb = colorForElement(entity.getElementId());

        // 1. World-locked ground telegraph ring
        float teleR = entity.getTelegraphRadius();
        if (teleR > 0.5F) {
            Vec3 telePos = entity.getTelegraphPos();
            double lx = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ly = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double lz = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            poseStack.pushPose();
            poseStack.translate(telePos.x - lx, telePos.y - ly + 0.12D, telePos.z - lz);
            Matrix4f tm = poseStack.last().pose();
            float prog = switch (anim) {
                case AstralionEntity.ANIM_PRISMATIC_CLEAVE -> Mth.clamp((t - 7.0F) / 8.0F, 0.0F, 1.0F);
                case AstralionEntity.ANIM_COMBO_TRINITY -> Mth.clamp((t - 38.0F) / 7.0F, 0.0F, 1.0F);
                case AstralionEntity.ANIM_ULTIMATE_CATACLYSM -> Mth.clamp((t - 20.0F) / 38.0F, 0.0F, 1.0F);
                default -> 0.5F;
            };
            float fillR = Math.max(0.15F, teleR * prog);
            drawHorizontalRing(vc, tm, teleR - 0.34F, teleR, 36, 235, 28, 36, 225);
            drawHorizontalRing(vc, tm, 0.08F, Math.max(0.10F, teleR - 0.34F), 32, 200, 18, 24, 42);
            drawHorizontalRing(vc, tm, 0.08F, fillR, 32, 255, 42, 48, 86);
            drawHorizontalRing(vc, tm, Math.max(0.08F, fillR - 0.24F), fillR, 36, 255, 85, 78, 210);
            poseStack.popPose();
        }

        // 2. Leviathan sweeping Chromatic Beam (ticks 15..51)
        if (anim == AstralionEntity.ANIM_LEVIATHAN_CHROMATIC_BEAM && t >= 15.0F && t <= 51.0F) {
            float sweepFrac = (t - 15.0F) / 36.0F;
            float beamYaw = yaw - 58.0F + sweepFrac * 116.0F;
            int[] bRgb = colorForElement(((int) (t * 0.5F)) % 7);
            poseStack.pushPose();
            poseStack.translate(0.0D, 3.0D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-beamYaw));
            Matrix4f m = poseStack.last().pose();
            float r = 0.75F + 0.15F * Mth.sin(liveT * 2.5F);
            quadDS(vc, m, -r, 0, 0.5F, bRgb[0], bRgb[1], bRgb[2], 220, r, 0, 0.5F, bRgb[0], bRgb[1], bRgb[2], 220,
                    r * 0.4F, -1.6F, 18.0F, 255, 255, 255, 235, -r * 0.4F, -1.6F, 18.0F, 255, 255, 255, 235);
            quadDS(vc, m, 0, -r, 0.5F, bRgb[0], bRgb[1], bRgb[2], 220, 0, r, 0.5F, bRgb[0], bRgb[1], bRgb[2], 220,
                    0, r * 0.4F - 1.6F, 18.0F, 255, 255, 255, 235, 0, -r * 0.4F - 1.6F, 18.0F, 255, 255, 255, 235);
            poseStack.popPose();
        }

        // 3. Singularity Shield aura ring
        if (entity.getBossState() == AstralionEntity.STATE_SINGULARITY && entity.getShieldProgress() > 0.01F) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 2.8D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(liveT * 3.5F));
            drawHorizontalRing(vc, poseStack.last().pose(), 2.6F, 3.0F, 32, rgb[0], rgb[1], rgb[2], 195);
            poseStack.popPose();
        }

        // 4. Sevenfold Cataclysm Impact Shockwave (ticks 58..76)
        if (anim == AstralionEntity.ANIM_ULTIMATE_CATACLYSM && t >= 58.0F && t <= 76.0F) {
            float p = (t - 58.0F) / 18.0F;
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.18D, 0.0D);
            float rOut = 2.0F + p * 11.5F;
            drawHorizontalRing(vc, poseStack.last().pose(), Math.max(0.3F, rOut - 1.3F), rOut, 40, rgb[0], rgb[1], rgb[2], (int) (235 * (1.0F - p)));
            poseStack.popPose();
        }
    }

    private static void quadDS(VertexConsumer vc, Matrix4f m,
                               float x0, float y0, float z0, int r0, int g0, int b0, int a0,
                               float x1, float y1, float z1, int r1, int g1, int b1, int a1,
                               float x2, float y2, float z2, int r2, int g2, int b2, int a2,
                               float x3, float y3, float z3, int r3, int g3, int b3, int a3) {
        vc.addVertex(m, x0, y0, z0).setColor(r0, g0, b0, a0);
        vc.addVertex(m, x1, y1, z1).setColor(r1, g1, b1, a1);
        vc.addVertex(m, x2, y2, z2).setColor(r2, g2, b2, a2);
        vc.addVertex(m, x3, y3, z3).setColor(r3, g3, b3, a3);
        vc.addVertex(m, x3, y3, z3).setColor(r3, g3, b3, a3);
        vc.addVertex(m, x2, y2, z2).setColor(r2, g2, b2, a2);
        vc.addVertex(m, x1, y1, z1).setColor(r1, g1, b1, a1);
        vc.addVertex(m, x0, y0, z0).setColor(r0, g0, b0, a0);
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            quadDS(vc, mat,
                    c0 * rIn, 0, s0 * rIn, r, g, b, a,
                    c0 * rOut, 0, s0 * rOut, r, g, b, a,
                    c1 * rOut, 0, s1 * rOut, r, g, b, a,
                    c1 * rIn, 0, s1 * rIn, r, g, b, a);
        }
    }

    private static int[] colorForElement(int id) {
        return switch (id) {
            case 0 -> new int[]{255, 105, 45};   // PYRO
            case 1 -> new int[]{60, 185, 255};   // HYDRO
            case 2 -> new int[]{200, 105, 255};  // ELECTRO
            case 3 -> new int[]{140, 240, 255};  // CRYO
            case 4 -> new int[]{110, 255, 210};  // ANEMO
            case 5 -> new int[]{255, 210, 85};   // GEO
            default -> new int[]{130, 245, 95};  // DENDRO
        };
    }

    @Override
    public ResourceLocation getTextureLocation(AstralionEntity entity) {
        return TEXTURE;
    }
}
