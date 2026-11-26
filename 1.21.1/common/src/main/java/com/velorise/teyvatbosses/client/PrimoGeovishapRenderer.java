package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapAnimations;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.entity.PrimoGeovishapEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Phase-3 AAA Genshin-grade 3D VFX renderer for {@link PrimoGeovishapEntity}:
 * <ul>
 *   <li>Dedicated normal-inflated emissive model layer ({@code LAYER_LOCATION_EMISSIVE}) for 100% zero Z-fighting.</li>
 *   <li>Canonical 1:1 scale ({@code 1.35F}) with smooth cross-faded Hermite animations.</li>
 *   <li>3D Golden Geo Crescent Slash Arcs for Left/Right Claw, Combo Slash, and 360° Whirlwind.</li>
 *   <li>3D Elemental-Infused Stone Spires + rotating 6-pointed star ground runes + elemental detonation bursts.</li>
 *   <li>Primordial Shower mouth charge orb, 18-block floor wave, and Shield-Reflect Counter Beam.</li>
 *   <li>Mouth-anchored volumetric Elemental Breath cone with pulsing plasma rings + golden Geo slam shockwaves.</li>
 * </ul>
 */
public class PrimoGeovishapRenderer extends EntityRenderer<PrimoGeovishapEntity> {
    public static final float BOSS_SCALE = 1.35F;

    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss_emissive.png");

    private final PrimoGeovishapModel<PrimoGeovishapEntity> model;
    private final PrimoGeovishapModel<PrimoGeovishapEntity> emissiveModel;
    private final Map<PrimoGeovishapEntity, PrimoGeovishapAnimations.BlendState> blendStates = new WeakHashMap<>();

    public PrimoGeovishapRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION));
        this.emissiveModel = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 3.1F;
    }

    @Override
    public void render(PrimoGeovishapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        int bossState = entity.getBossState();
        int animId = entity.getAnimId();
        int animTick = entity.getAnimTick();
        float clipSec = (animTick + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;
        String clip = PrimoGeovishapEntity.clipNameForAnim(animId);

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headLookYaw = Mth.wrapDegrees(headYaw - bodyYaw);
        float headLookPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        PrimoGeovishapAnimations.BlendState blend = blendStates.computeIfAbsent(
                entity, k -> new PrimoGeovishapAnimations.BlendState());
        PrimoGeovishapAnimations.applyBlended(model, blend, clip, clipSec, clientSec, headLookYaw, headLookPitch);
        emissiveModel.copyPoseFrom(model);

        int[] rgb = entity.getInfusionRgb();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-BOSS_SCALE, -BOSS_SCALE, BOSS_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = entity.hurtTime > 0
                ? OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true))
                : OverlayTexture.NO_OVERLAY;
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, overlay, 0xFFFFFFFF);

        boolean chargingUltimate = (animId == PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER
                || animId == PrimoGeovishapEntity.ANIM_ELEMENTAL_BREATH
                || animId == PrimoGeovishapEntity.ANIM_PRIMEVAL_SPRING
                || animId == PrimoGeovishapEntity.ANIM_SCATTERED_ROCKS);
        float glowPulse = chargingUltimate
                ? (0.90F + 0.10F * Mth.sin(clientSec * 14.0F))
                : (0.80F + 0.20F * Mth.sin(clientSec * 2.8F));
        int er = Mth.clamp((int) (rgb[0] * glowPulse), 0, 255);
        int eg = Mth.clamp((int) (rgb[1] * glowPulse), 0, 255);
        int eb = Mth.clamp((int) (rgb[2] * glowPulse), 0, 255);
        int emColor = 0xFF000000 | (er << 16) | (eg << 8) | eb;
        emissiveModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);
        poseStack.popPose();

        // =====================================================================
        // AAA 3D GENSHIN-FAITHFUL COMBAT VFX PASS
        // =====================================================================
        VertexConsumer lvc = buf.getBuffer(RenderType.lightning());

        // 1. Golden Geo Crescent Slash Arcs (Left/Right Claw, Combo Slash 1 & 2, Whirlwind 360°)
        renderGoldenSlashVfx(poseStack, lvc, animId, animTick, partialTicks, bodyYaw);

        // 2. Dual-ring pulsing telegraph during Primordial Shower / Burrow emergence
        float teleRadius = entity.getTelegraphRadius();
        if (teleRadius > 0.5F) {
            poseStack.pushPose();
            Matrix4f mat = poseStack.last().pose();
            drawRing(lvc, mat, teleRadius - 0.55F, teleRadius, 52, 0.09F, rgb[0], rgb[1], rgb[2], 210);
            float pulseFrac = (clientSec * 1.55F) % 1.0F;
            float innerR = Math.max(0.8F, teleRadius * pulseFrac);
            int innerA = (int) (165 * (1.0F - pulseFrac));
            if (innerA > 15) {
                drawRing(lvc, mat, Math.max(0.2F, innerR - 0.42F), innerR, 44, 0.10F, rgb[0], rgb[1], rgb[2], innerA);
            }
            poseStack.popPose();
        }

        // 3. Primordial Shower: Mouth Charge Orb + Massive 18-Block Floor Wave + Sky Pillar
        if (animId == PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER) {
            renderPrimordialShowerVfx(poseStack, lvc, animTick, partialTicks, clientSec, bodyYaw, rgb);
        }

        // 4. Shield-Reflect Counter Beam (shoots straight back from countering player into boss face!)
        if (bossState == PrimoGeovishapEntity.STATE_STAGGER && animTick >= 1 && animTick <= 16) {
            renderShieldCounterBeam(entity, poseStack, lvc, animTick, partialTicks, rgb);
        }

        // 5. Elemental Breath: Mouth-anchored 3D volumetric sweeping beam + pulsing rings
        if (animId == PrimoGeovishapEntity.ANIM_ELEMENTAL_BREATH && animTick >= 22 && animTick <= 63) {
            renderElementalBreathVfx(poseStack, lvc, animTick, partialTicks, clientSec, bodyYaw, rgb);
        }

        // 6. Golden Geo Ground Shockwaves on heavy slams (Combo Hit 3, Burrow emerge, Primeval Spring)
        renderImpactShockwave(poseStack, lvc, animId, animTick, partialTicks, rgb);

        // 7. 3D Elemental-Infused Stone Spires + Rotating 6-Point Star Ground Runes
        renderSyncedInfusedStones(entity, poseStack, lvc, partialTicks, clientSec, rgb);

        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private static void renderGoldenSlashVfx(PoseStack poseStack, VertexConsumer lvc, int animId, int animTick, float partialTicks, float bodyYaw) {
        if (animId == PrimoGeovishapEntity.ANIM_RIGHT_CLAW && animTick >= 11 && animTick <= 20) {
            float p = Mth.clamp((animTick - 11 + partialTicks) / 9.0F, 0.0F, 1.0F);
            drawSlashArc(poseStack, lvc, bodyYaw, 1.8F, 5.8F, -85.0F, 65.0F, p, 12.0F);
        } else if (animId == PrimoGeovishapEntity.ANIM_LEFT_CLAW && animTick >= 11 && animTick <= 20) {
            float p = Mth.clamp((animTick - 11 + partialTicks) / 9.0F, 0.0F, 1.0F);
            drawSlashArc(poseStack, lvc, bodyYaw, 1.8F, 5.8F, 85.0F, -65.0F, p, -12.0F);
        } else if (animId == PrimoGeovishapEntity.ANIM_COMBO_SLASH) {
            if (animTick >= 11 && animTick <= 19) {
                float p = Mth.clamp((animTick - 11 + partialTicks) / 8.0F, 0.0F, 1.0F);
                drawSlashArc(poseStack, lvc, bodyYaw, 1.8F, 6.2F, -90.0F, 70.0F, p, 10.0F);
            } else if (animTick >= 26 && animTick <= 34) {
                float p = Mth.clamp((animTick - 26 + partialTicks) / 8.0F, 0.0F, 1.0F);
                drawSlashArc(poseStack, lvc, bodyYaw, 1.8F, 6.4F, 90.0F, -70.0F, p, -10.0F);
            }
        } else if (animId == PrimoGeovishapEntity.ANIM_WHIRLWIND && animTick >= 11 && animTick <= 27) {
            float p = Mth.clamp((animTick - 11 + partialTicks) / 16.0F, 0.0F, 1.0F);
            float fade = 1.0F - p * 0.75F;
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.1D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw - p * 380.0F));
            Matrix4f mat = poseStack.last().pose();
            drawArcRibbon(lvc, mat, 3.6F, 7.6F, 0.0F, 250.0F, 32, 255, 195, 75, (int) (185 * fade));
            drawArcRibbon(lvc, mat, 6.4F, 7.8F, 10.0F, 250.0F, 32, 255, 245, 200, (int) (220 * fade));
            poseStack.popPose();
        }
    }

    private static void drawSlashArc(PoseStack poseStack, VertexConsumer lvc, float bodyYaw, float yHeight, float radius,
                                     float startDeg, float endDeg, float progress, float tiltDeg) {
        float fade = (progress < 0.25F) ? (progress / 0.25F) : (1.0F - (progress - 0.25F) / 0.75F);
        int alpha = Mth.clamp((int) (215 * fade), 0, 255);
        if (alpha <= 10) return;
        float curEnd = Mth.lerp(progress, startDeg, endDeg);
        float curStart = Mth.lerp(Math.max(0.0F, progress - 0.42F), startDeg, endDeg);
        poseStack.pushPose();
        poseStack.translate(0.0D, yHeight, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(tiltDeg));
        Matrix4f mat = poseStack.last().pose();
        drawArcRibbon(lvc, mat, radius * 0.52F, radius, curStart, curEnd, 22, 255, 185, 55, alpha);
        drawArcRibbon(lvc, mat, radius * 0.84F, radius * 1.03F, curStart, curEnd, 22, 255, 248, 210, Math.min(255, alpha + 25));
        poseStack.popPose();
    }

    private static void drawArcRibbon(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, float deg0, float deg1,
                                      int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float f0 = i / (float) segs;
            float f1 = (i + 1) / (float) segs;
            float a0 = Mth.lerp(f0, deg0, deg1) * Mth.DEG_TO_RAD;
            float a1 = Mth.lerp(f1, deg0, deg1) * Mth.DEG_TO_RAD;
            // Taper alpha smoothly at leading/trailing tips
            int al0 = (int) (a * Mth.sin(f0 * Mth.PI));
            int al1 = (int) (a * Mth.sin(f1 * Mth.PI));
            float x0 = Mth.sin(a0), z0 = -Mth.cos(a0);
            float x1 = Mth.sin(a1), z1 = -Mth.cos(a1);
            vc.addVertex(mat, x0 * rIn, 0.0F, z0 * rIn).setColor(r, g, b, 0);
            vc.addVertex(mat, x0 * rOut, 0.0F, z0 * rOut).setColor(r, g, b, al0);
            vc.addVertex(mat, x1 * rOut, 0.0F, z1 * rOut).setColor(r, g, b, al1);
            vc.addVertex(mat, x1 * rIn, 0.0F, z1 * rIn).setColor(r, g, b, 0);
        }
    }

    private static void renderPrimordialShowerVfx(PoseStack poseStack, VertexConsumer lvc, int animTick,
                                                  float partialTicks, float clientSec, float bodyYaw, int[] rgb) {
        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        if (animTick >= 6 && animTick < 47) {
            float p = Mth.clamp((animTick - 6 + partialTicks) / 41.0F, 0.0F, 1.0F);
            // Blazing elemental orb gathering in front of boss's compressed jaws/chest!
            float radYaw = (180.0F - bodyYaw) * Mth.DEG_TO_RAD;
            float ox = -Mth.sin(radYaw) * 2.6F;
            float oz = -Mth.cos(radYaw) * 2.6F;
            float orbR = 0.55F + p * 1.45F + Mth.sin(clientSec * 24.0F) * 0.14F;
            drawGlowingOctahedron(lvc, mat, ox, 1.65F, oz, orbR * 1.25F, rgb[0], rgb[1], rgb[2], (int) (185 * p));
            drawGlowingOctahedron(lvc, mat, ox, 1.65F, oz, orbR * 0.68F, 255, 255, 255, (int) (235 * p));
            // Rising energy vortex column
            drawEnergyColumn(lvc, mat, 1.2F + p * 1.8F, 0.2F, 5.5F + p * 7.0F, 20, rgb[0], rgb[1], rgb[2], (int) (115 * p));
        } else if (animTick >= 47 && animTick <= 68) {
            float p = Mth.clamp((animTick - 47 + partialTicks) / 21.0F, 0.0F, 1.0F);
            float ease = 1.0F - (1.0F - p) * (1.0F - p);
            float waveR = 2.0F + ease * 16.5F;
            int alpha = (int) (230 * (1.0F - p));
            // Massive flat elemental floor wave (matches 1.mp4 00:37-00:38 & 2.mp4 00:22-00:23!)
            drawRing(lvc, mat, Math.max(0.5F, waveR - 3.8F), waveR, 60, 0.18F, rgb[0], rgb[1], rgb[2], alpha);
            drawRing(lvc, mat, Math.max(0.5F, waveR - 1.1F), waveR, 60, 0.28F, 255, 255, 255, Math.min(255, alpha + 20));
            drawEnergyColumn(lvc, mat, 3.0F * (1.0F - p * 0.55F), 0.1F, 16.0F, 24, rgb[0], rgb[1], rgb[2], (int) (175 * (1.0F - p)));
        }
        poseStack.popPose();
    }

    private static void renderShieldCounterBeam(PrimoGeovishapEntity entity, PoseStack poseStack, VertexConsumer lvc,
                                                int animTick, float partialTicks, int[] rgb) {
        CompoundTag cTag = entity.getCounterBeamTag();
        if (!cTag.contains("dx")) return;
        float dx = cTag.getFloat("dx");
        float dy = cTag.getFloat("dy");
        float dz = cTag.getFloat("dz");
        float p = Mth.clamp((animTick - 1 + partialTicks) / 15.0F, 0.0F, 1.0F);
        int alpha = (int) (235 * (1.0F - p));
        if (alpha <= 10) return;

        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        // Thick glowing counter-reflection beam from player's shield (dx,dy,dz) straight into boss face (0, 2.2, 0)
        float w = 0.55F * (1.0F - p * 0.5F);
        drawRibbonSegment(lvc, mat, dx, dy + 1.0F, dz, 0.0F, 2.2F, 0.0F, w, rgb[0], rgb[1], rgb[2], alpha);
        drawRibbonSegment(lvc, mat, dx, dy + 1.0F, dz, 0.0F, 2.2F, 0.0F, w * 0.45F, 255, 255, 255, Math.min(255, alpha + 20));
        drawGlowingOctahedron(lvc, mat, 0.0F, 2.2F, 0.0F, 1.4F + p * 2.2F, 255, 240, 180, (int) (190 * (1.0F - p)));
        poseStack.popPose();
    }

    private static void renderElementalBreathVfx(PoseStack poseStack, VertexConsumer lvc, int animTick,
                                                 float partialTicks, float clientSec, float bodyYaw, int[] rgb) {
        float progress = Mth.clamp((animTick - 22 + partialTicks) / 40.0F, 0.0F, 1.0F);
        float sweepYaw = bodyYaw + Mth.lerp(progress, 44.0F, -48.0F);
        float fade = (progress < 0.08F) ? (progress / 0.08F) : (progress > 0.90F ? (1.0F - progress) / 0.10F : 1.0F);
        poseStack.pushPose();
        poseStack.translate(0.0D, 2.35D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - sweepYaw));
        Matrix4f mat = poseStack.last().pose();
        // Mouth glow orb
        drawGlowingOctahedron(lvc, mat, 0.0F, 0.0F, -2.8F, 0.95F + Mth.sin(clientSec * 25.0F) * 0.12F, 255, 255, 255, (int) (220 * fade));
        // Outer elemental plasma cone
        drawBeamCone(lvc, mat, 2.8F, 16.5F, 0.48F, 2.35F, 18, rgb[0], rgb[1], rgb[2], (int) (175 * fade));
        // Inner white-hot high-pressure beam core
        drawBeamCone(lvc, mat, 2.8F, 16.0F, 0.24F, 1.05F, 14, 255, 255, 255, (int) (225 * fade));
        poseStack.popPose();
    }

    private static void renderImpactShockwave(PoseStack poseStack, VertexConsumer lvc, int animId, int animTick, float partialTicks, int[] rgb) {
        int start = -1;
        float maxR = 9.0F;
        boolean goldenGeo = true;
        if (animId == PrimoGeovishapEntity.ANIM_COMBO_SLASH && animTick >= 47 && animTick <= 62) {
            start = 47; maxR = 9.8F;
        } else if (animId == PrimoGeovishapEntity.ANIM_BURROW && animTick >= 64 && animTick <= 79) {
            start = 64; maxR = 10.2F;
        } else if (animId == PrimoGeovishapEntity.ANIM_PRIMEVAL_SPRING && animTick >= 24 && animTick <= 39) {
            start = 24; maxR = 10.5F; goldenGeo = false;
        }
        if (start < 0) return;
        float p = Mth.clamp((animTick - start + partialTicks) / 15.0F, 0.0F, 1.0F);
        float ease = 1.0F - (1.0F - p) * (1.0F - p);
        float r = 1.6F + ease * (maxR - 1.6F);
        int a = (int) (225 * (1.0F - p));
        if (a <= 10) return;
        int cr = goldenGeo ? 255 : rgb[0];
        int cg = goldenGeo ? 205 : rgb[1];
        int cb = goldenGeo ? 75  : rgb[2];
        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        drawRing(lvc, mat, Math.max(0.4F, r - 1.5F), r, 48, 0.14F, cr, cg, cb, a);
        drawRing(lvc, mat, Math.max(0.4F, r - 0.45F), r, 48, 0.20F, 255, 250, 220, Math.min(255, a + 20));
        poseStack.popPose();
    }

    /**
     * Renders every synced Elemental-Infused Stone as a real 3D crystalline monolith obelisk
     * with a rotating 6-pointed star ground rune (1.mp4 01:11, 2.mp4 00:28-00:30, 01:11-01:14)!
     */
    private static void renderSyncedInfusedStones(PrimoGeovishapEntity entity, PoseStack poseStack, VertexConsumer lvc,
                                                  float partialTicks, float clientSec, int[] rgb) {
        CompoundTag tag = entity.getStonesTag();
        if (!tag.contains("stones", Tag.TAG_LIST)) return;
        ListTag list = tag.getList("stones", Tag.TAG_COMPOUND);
        if (list.isEmpty()) return;

        double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        for (int i = 0; i < list.size(); i++) {
            CompoundTag st = list.getCompound(i);
            float sx = (float) (st.getDouble("x") - ex);
            float sy = (float) (st.getDouble("y") - ey);
            float sz = (float) (st.getDouble("z") - ez);
            int fuse = st.getInt("f");
            float urgency = Mth.clamp(1.0F - (fuse / 48.0F), 0.0F, 1.0F);
            float pulse = 0.85F + 0.15F * Mth.sin(clientSec * (8.0F + urgency * 24.0F));

            poseStack.pushPose();
            poseStack.translate(sx, sy, sz);
            Matrix4f mat = poseStack.last().pose();

            // 1. Rotating 6-pointed star rune circle flat on the ground
            drawStarRune(lvc, mat, 1.45F + urgency * 0.35F, clientSec * (1.2F + urgency * 2.5F),
                    rgb[0], rgb[1], rgb[2], (int) (185 * pulse));
            drawRing(lvc, mat, 1.35F, 1.50F, 24, 0.06F, 255, 255, 255, (int) (145 * pulse));

            // 2. 3D Crystalline Monolith Obelisk spire (dark outer sheath + glowing inner crystal core)
            float h = 1.95F + urgency * 0.30F;
            drawCrystalSpire(lvc, mat, 0.42F, h, 45, 50, 62, 235);
            drawCrystalSpire(lvc, mat, 0.28F, h * 1.06F, rgb[0], rgb[1], rgb[2], (int) (220 * pulse));
            drawCrystalSpire(lvc, mat, 0.14F, h * 0.92F, 255, 255, 255, (int) (240 * pulse));

            poseStack.popPose();
        }
    }

    private static void drawCrystalSpire(VertexConsumer vc, Matrix4f mat, float r, float h, int cr, int cg, int cb, int a) {
        // 4-sided tapered crystal obelisk
        float[][] pts = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            vc.addVertex(mat, pts[i][0], 0.02F, pts[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[j][0], 0.02F, pts[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[j][0] * 0.22F, h, pts[j][1] * 0.22F).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[i][0] * 0.22F, h, pts[i][1] * 0.22F).setColor(cr, cg, cb, a);
        }
    }

    private static void drawStarRune(VertexConsumer vc, Matrix4f mat, float radius, float rotRad, int r, int g, int b, int a) {
        // Two overlapping equilateral triangles forming a 6-pointed elemental hexagram rune on the floor
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
            }
        }
    }

    private static void drawGlowingOctahedron(VertexConsumer vc, Matrix4f mat, float cx, float cy, float cz, float r,
                                              int cr, int cg, int cb, int a) {
        if (a <= 5) return;
        float[][] eq = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            // Upper pyramid
            vc.addVertex(mat, cx + eq[i][0], cy, cz + eq[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx + eq[j][0], cy, cz + eq[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy + r, cz).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy + r, cz).setColor(cr, cg, cb, a);
            // Lower pyramid
            vc.addVertex(mat, cx + eq[j][0], cy, cz + eq[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx + eq[i][0], cy, cz + eq[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy - r, cz).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy - r, cz).setColor(cr, cg, cb, a);
        }
    }

    private static void drawRibbonSegment(VertexConsumer vc, Matrix4f mat, float x0, float y0, float z0,
                                          float x1, float y1, float z1, float halfW, int r, int g, int b, int a) {
        vc.addVertex(mat, x0, y0 - halfW, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x0, y0 + halfW, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y1 + halfW, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y1 - halfW, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x0 - halfW, y0, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x0 + halfW, y0, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1 + halfW, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1 - halfW, y1, z1).setColor(r, g, b, a);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, float y, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, y, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, y, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, y, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, y, s1 * rIn).setColor(r, g, b, a);
        }
    }

    private static void drawEnergyColumn(VertexConsumer vc, Matrix4f mat, float radius, float y0, float y1, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0) * radius, s0 = Mth.sin(a0) * radius;
            float c1 = Mth.cos(a1) * radius, s1 = Mth.sin(a1) * radius;
            vc.addVertex(mat, c0, y0, s0).setColor(r, g, b, a);
            vc.addVertex(mat, c1, y0, s1).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * 1.35F, y1, s1 * 1.35F).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * 1.35F, y1, s0 * 1.35F).setColor(r, g, b, 0);
        }
    }

    private static void drawBeamCone(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd, float rStart, float rEnd, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        float z0 = -zStart;
        float z1 = -zEnd;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rStart, s0 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rStart, s1 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rEnd, s1 * rEnd, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * rEnd, s0 * rEnd, z1).setColor(r, g, b, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoGeovishapEntity entity) {
        return TEXTURE;
    }
}
