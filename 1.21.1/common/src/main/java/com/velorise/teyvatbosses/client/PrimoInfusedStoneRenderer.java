package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.PrimoInfusedStoneModel;
import com.velorise.teyvatbosses.entity.PrimoGeovishapEntity;
import com.velorise.teyvatbosses.entity.PrimoInfusedStoneEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders {@link PrimoInfusedStoneEntity}:
 * <ul>
 *   <li>Springy ground-burst eruption animation + pre-detonation tremor &amp; glow surge.</li>
 *   <li>Solid 3D hollowed-out basalt/limestone monolith shell ({@code entityCutoutNoCull}) with glowing mineral cracks.</li>
 *   <li>Levitating, spinning faceted elemental crystal core inside the hollowed interior cavity ({@code RenderType.eyes}).</li>
 *   <li>Double-sided rotating 6-pointed star rune &amp; danger ring flat on the arena floor.</li>
 * </ul>
 */
public class PrimoInfusedStoneRenderer extends EntityRenderer<PrimoInfusedStoneEntity> {

    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_infused_stone.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_infused_stone_emissive.png");
    private static final ResourceLocation BLAST_ZONE_PYRO = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_pyro.png");
    private static final ResourceLocation BLAST_ZONE_PYRO_EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_pyro_emissive.png");
    private static final ResourceLocation BLAST_ZONE_HYDRO = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_hydro.png");
    private static final ResourceLocation BLAST_ZONE_HYDRO_EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_hydro_emissive.png");
    private static final ResourceLocation BLAST_ZONE_ELECTRO = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_electro.png");
    private static final ResourceLocation BLAST_ZONE_ELECTRO_EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_electro_emissive.png");
    private static final ResourceLocation BLAST_ZONE_CRYO = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_cryo.png");
    private static final ResourceLocation BLAST_ZONE_CRYO_EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_infused_blast_zone_cryo_emissive.png");

    private static ResourceLocation blastZoneFor(int infusion) {
        return switch (infusion) {
            case PrimoGeovishapEntity.INFUSION_HYDRO -> BLAST_ZONE_HYDRO;
            case PrimoGeovishapEntity.INFUSION_ELECTRO -> BLAST_ZONE_ELECTRO;
            case PrimoGeovishapEntity.INFUSION_CRYO -> BLAST_ZONE_CRYO;
            default -> BLAST_ZONE_PYRO;
        };
    }

    private static ResourceLocation blastZoneEmissiveFor(int infusion) {
        return switch (infusion) {
            case PrimoGeovishapEntity.INFUSION_HYDRO -> BLAST_ZONE_HYDRO_EMISSIVE;
            case PrimoGeovishapEntity.INFUSION_ELECTRO -> BLAST_ZONE_ELECTRO_EMISSIVE;
            case PrimoGeovishapEntity.INFUSION_CRYO -> BLAST_ZONE_CRYO_EMISSIVE;
            default -> BLAST_ZONE_PYRO_EMISSIVE;
        };
    }

    private final PrimoInfusedStoneModel model;

    public PrimoInfusedStoneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new PrimoInfusedStoneModel(ctx.bakeLayer(PrimoInfusedStoneModel.LAYER_LOCATION));
        this.shadowRadius = 0.85F;
    }

    @Override
    public void render(
            PrimoInfusedStoneEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buf,
            int packedLight
    ) {
        float age = entity.tickCount + partialTicks;
        float clientSec = age / 20.0F;
        int[] rgb = entity.getInfusionRgb();

        if (entity.getPhase() == PrimoInfusedStoneEntity.PHASE_LAVA_POOL) {
            // Static cracked volcanic basalt crater (fixed radius, zero wobble!) with gentle magma glow pulse
            float glowPulse = 0.82F + 0.18F * Mth.sin(clientSec * 6.5F);
            float radius = 2.85F;
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getSpinYaw()));
            PoseStack.Pose pose = poseStack.last();
            // Pass 1: Completely static cracked basalt & lava decal anchored cleanly to the ground
            VertexConsumer dvc = buf.getBuffer(RenderType.entityTranslucent(blastZoneFor(entity.getInfusion())));
            drawTexturedGroundQuad(dvc, pose, radius, 0.055F, 255, 255, 255, 240, packedLight);
            // Pass 2: Gentle emissive glow pulse inside the cracks (geometry never scales or wobbles)
            VertexConsumer evc = buf.getBuffer(RenderType.entityTranslucentEmissive(blastZoneEmissiveFor(entity.getInfusion())));
            int ea = Mth.clamp((int) (235 * glowPulse), 0, 255);
            drawTexturedGroundQuad(evc, pose, radius, 0.068F, 255, 255, 255, ea, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
            return;
        }

        float urgency = Mth.clamp(1.0F - ((entity.getFuseLeft() - partialTicks) / (float) entity.getFuseMax()), 0.0F, 1.0F);
        float pulse = 0.84F + 0.16F * Mth.sin(clientSec * (8.0F + urgency * 28.0F));

        // 1. Rotating 6-pointed star ground rune + outer blast warning ring
        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        VertexConsumer lvc = buf.getBuffer(RenderType.lightning());
        float runeR = 1.55F + urgency * 0.45F;
        drawStarRune(lvc, mat, runeR, clientSec * (1.2F + urgency * 3.0F), rgb[0], rgb[1], rgb[2], (int) (195 * pulse));
        drawGroundRing(lvc, mat, runeR - 0.14F, runeR + 0.04F, 28, 255, 255, 255, (int) (155 * pulse));
        poseStack.popPose();

        // 2. Solid 3D hollowed-rock monolith + suspended spinning elemental crystal core
        float popT = Mth.clamp(age / 5.0F, 0.0F, 1.0F);
        float popScale = 1.0F + 1.8F * (float) Math.pow(popT - 1.0F, 3.0D) + 0.8F * (float) Math.pow(popT - 1.0F, 2.0D);
        float scaleY = Mth.clamp(popScale, 0.15F, 1.12F) * (1.0F + urgency * 0.16F);
        float scaleXZ = 1.05F + urgency * 0.10F;

        // Subtle high-frequency tremor right before detonation
        float shake = urgency > 0.65F ? (urgency - 0.65F) * 0.14F : 0.0F;
        float sx = shake * Mth.sin(age * 3.7F);
        float sz = shake * Mth.cos(age * 4.3F);

        model.setupAnim(entity, 0.0F, 0.0F, age, 0.0F, 0.0F);

        poseStack.pushPose();
        poseStack.translate(sx, 0.0D, sz);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getSpinYaw()));
        poseStack.scale(-scaleXZ, -scaleY, scaleXZ);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        // Pass A: Solid textured hollowed-out dark basalt/limestone outer rock shell
        model.renderRockShell(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),
                packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        // Pass B: Subtle glowing elemental fissure cracks on outer rock shell + blazing inner crystal core
        int er = Mth.clamp((int) (rgb[0] * pulse), 0, 255);
        int eg = Mth.clamp((int) (rgb[1] * pulse), 0, 255);
        int eb = Mth.clamp((int) (rgb[2] * pulse), 0, 255);
        int emColor = 0xFF000000 | (er << 16) | (eg << 8) | eb;
        model.renderRockShell(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);
        model.renderCrystalCore(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);

        // Pass C: White-hot inner core flash right before detonation
        if (urgency > 0.55F) {
            float hot = Mth.clamp((urgency - 0.55F) / 0.45F, 0.0F, 1.0F) * (0.6F + 0.4F * Mth.sin(age * 2.2F));
            int hc = (int) (255 * hot);
            int hotColor = 0xFF000000 | (hc << 16) | (hc << 8) | hc;
            poseStack.pushPose();
            poseStack.scale(0.68F, 0.88F, 0.68F);
            model.renderCrystalCore(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)),
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, hotColor);
            poseStack.popPose();
        }
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private static void drawStarRune(VertexConsumer vc, Matrix4f mat, float radius, float rotRad, int r, int g, int b, int a) {
        for (int tri = 0; tri < 2; tri++) {
            float baseAng = rotRad + tri * (Mth.PI / 3.0F);
            for (int edge = 0; edge < 3; edge++) {
                float a0 = baseAng + edge * (Mth.TWO_PI / 3.0F);
                float a1 = baseAng + (edge + 1) * (Mth.TWO_PI / 3.0F);
                float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius;
                float x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;
                float dx = x1 - x0, dz = z1 - z0;
                float len = Mth.sqrt(dx * dx + dz * dz);
                if (len < 1.0E-4F) continue;
                float nx = -dz / len * 0.075F, nz = dx / len * 0.075F;
                vc.addVertex(mat, x0 - nx, 0.07F, z0 - nz).setColor(r, g, b, a);
                vc.addVertex(mat, x0 + nx, 0.07F, z0 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 + nx, 0.07F, z1 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 - nx, 0.07F, z1 - nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 - nx, 0.07F, z1 - nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 + nx, 0.07F, z1 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x0 + nx, 0.07F, z0 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x0 - nx, 0.07F, z0 - nz).setColor(r, g, b, a);
            }
        }
    }

    private static void drawGroundRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.06F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.06F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.06F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.06F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.06F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.06F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.06F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.06F, s0 * rIn).setColor(r, g, b, a);
        }
    }

    private static void drawTexturedGroundQuad(VertexConsumer vc, PoseStack.Pose pose, float r, float y,
                                               int red, int green, int blue, int alpha, int light) {
        Matrix4f mat = pose.pose();
        // Top face
        vc.addVertex(mat, -r, y, -r).setColor(red, green, blue, alpha).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat, -r, y,  r).setColor(red, green, blue, alpha).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat,  r, y,  r).setColor(red, green, blue, alpha).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat,  r, y, -r).setColor(red, green, blue, alpha).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        // Bottom face (double-sided)
        vc.addVertex(mat,  r, y, -r).setColor(red, green, blue, alpha).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat,  r, y,  r).setColor(red, green, blue, alpha).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat, -r, y,  r).setColor(red, green, blue, alpha).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
        vc.addVertex(mat, -r, y, -r).setColor(red, green, blue, alpha).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoInfusedStoneEntity entity) {
        return TEXTURE;
    }
}
