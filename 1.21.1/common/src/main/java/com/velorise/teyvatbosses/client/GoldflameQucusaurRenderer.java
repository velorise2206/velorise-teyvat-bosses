package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.BedrockAnimationLoader;
import com.velorise.teyvatbosses.client.model.GoldflameQucusaurModel;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurEntity;
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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Renderer for Boss #6 {@link GoldflameQucusaurEntity}:
 * <ul>
 *   <li>Uses the shared {@link GoldflameQucusaurModel} ({@code main} + {@code emissive} layers) with {@code saddle} hidden.</li>
 *   <li>Drives all 15 Boss clips directly from {@code animations/goldflame_qucusaur.bbmodel} via {@link BedrockAnimationLoader}.</li>
 *   <li>Renders a 4-layer undulating volumetric Goldflame breath torrent + dual helical vortex ribbons
 *       (matching {@link PrimoGeovishapRenderer}) during {@code breath_sweep}.</li>
 * </ul>
 */
public class GoldflameQucusaurRenderer extends EntityRenderer<GoldflameQucusaurEntity> {
    public static final ResourceLocation TEXTURE =
            TeyvatBossesConstants.id("textures/entity/goldflame_qucusaur.png");
    public static final ResourceLocation EMISSIVE_TEXTURE =
            TeyvatBossesConstants.id("textures/entity/goldflame_qucusaur_emissive.png");
    private static final ResourceLocation BOSS_MUSIC_ID =
            TeyvatBossesConstants.id("music.goldflame_qucusaur_theme");

    private static final float BOSS_BLEND_DURATION_SEC = 0.35F;

    private static final class BossBlendState {
        String prevClip;
        float prevTimeSec;
        String curClip;
        float blendStartSec;
        float lastClientSec;
        float lastStateSec;
    }

    private final Map<GoldflameQucusaurEntity, BossBlendState> bossBlendStates = new WeakHashMap<>();
    private final GoldflameQucusaurModel<GoldflameQucusaurEntity> bodyModel;
    private final GoldflameQucusaurModel<GoldflameQucusaurEntity> emissiveModel;

    public GoldflameQucusaurRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.bodyModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION));
        this.emissiveModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 2.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean musicActive = entity.isAlive()
                && entity.hasCombatTarget()
                && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DORMANT
                && entity.getAnimState() != GoldflameQucusaurEntity.STATE_RETURN_FLIGHT
                && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DEATH_COLLAPSE
                && entity.getAnimState() != GoldflameQucusaurEntity.STATE_SUBDUED;
        BossMusicManager.update(entity, BOSS_MUSIC_ID, musicActive,
                () -> entity.isAlive()
                        && entity.hasCombatTarget()
                        && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DORMANT
                        && entity.getAnimState() != GoldflameQucusaurEntity.STATE_RETURN_FLIGHT
                        && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DEATH_COLLAPSE
                && entity.getAnimState() != GoldflameQucusaurEntity.STATE_SUBDUED);
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float stateSec = (entity.getStateTicks() + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;

        String targetClipName = entity.getActiveAnimationClip();
        BossBlendState bst = bossBlendStates.computeIfAbsent(entity, e -> {
            BossBlendState b = new BossBlendState();
            b.curClip = targetClipName;
            b.blendStartSec = clientSec - BOSS_BLEND_DURATION_SEC;
            b.lastClientSec = clientSec;
            b.lastStateSec = stateSec;
            return b;
        });
        float bdt = Math.max(0.0F, Math.min(0.15F, clientSec - bst.lastClientSec));
        bst.lastClientSec = clientSec;
        if (!targetClipName.equals(bst.curClip)) {
            bst.prevClip = bst.curClip;
            bst.prevTimeSec = bst.lastStateSec;
            bst.curClip = targetClipName;
            bst.blendStartSec = clientSec;
        } else if (bst.prevClip != null) {
            bst.prevTimeSec += bdt;
        }
        bst.lastStateSec = stateSec;

        float rawBlend = Mth.clamp((clientSec - bst.blendStartSec) / BOSS_BLEND_DURATION_SEC, 0.0F, 1.0F);
        float blend = rawBlend * rawBlend * (3.0F - 2.0F * rawBlend);

        bodyModel.resetPose(false);
        BedrockAnimationLoader.Clip clip = BedrockAnimationLoader.getClip("goldflame_qucusaur", bst.curClip);
        if (bst.prevClip != null && blend < 0.999F) {
            BedrockAnimationLoader.Clip prevClip = BedrockAnimationLoader.getClip("goldflame_qucusaur", bst.prevClip);
            if (prevClip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, prevClip, bst.prevTimeSec, 1.0F - blend, false);
            }
            if (clip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, clip, stateSec, blend, false);
            }
        } else {
            bst.prevClip = null;
            if (clip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, clip, stateSec, 1.0F, false);
            }
        }
        emissiveModel.copyPoseFrom(bodyModel);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer bodyVc = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        bodyModel.renderToBuffer(poseStack, bodyVc, packedLight, overlay, 0xFFFFFFFF);

        // Pulsing molten-gold emissive layer (brightens during Golden Flame form)
        float pulse = entity.isDormant()
                ? 0.28F + 0.10F * Mth.sin(clientSec * 1.6F)
                : (entity.isGoldenFlameActive()
                    ? 0.85F + 0.15F * Mth.sin(clientSec * 8.0F)
                    : 0.65F + 0.20F * Mth.sin(clientSec * 3.5F));
        int alpha = Mth.clamp((int) (pulse * 255.0F), 90, 255);
        int emissiveColor = (alpha << 24) | 0x00FFFFFF;
        VertexConsumer emVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(EMISSIVE_TEXTURE));
        emissiveModel.renderToBuffer(poseStack, emVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emissiveColor);
        poseStack.popPose();


        // Genshin-style Ground Danger Telegraphs (circle / cone / charge lane / dive target)
        renderTelegraphZones(entity, bodyYaw, partialTicks, clientSec, poseStack, buffer);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /**
     * Shared 4-layer undulating volumetric Goldflame breath torrent + dual helical vortex ribbons
     * (reusing PrimoGeovishapRenderer's volumetric shaderless VertexConsumer architecture).
     */
    public static void renderGoldflameBreathVfx(PoseStack poseStack, VertexConsumer lvc, float yawDeg, float pitchDeg,
                                                double mouthHeight, double forwardOffset, float length,
                                                float clientSec, float fade) {
        if (fade <= 0.02F) return;
        poseStack.pushPose();
        float rad = (float) Math.toRadians(yawDeg);
        poseStack.translate(-Math.sin(rad) * forwardOffset, mouthHeight, Math.cos(rad) * forwardOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitchDeg));
        Matrix4f mat = poseStack.last().pose();

        // 3 turbulent jagged star-faceted flame layers (replaces smooth round cones with roaring asymmetric fire!)
        drawTurbulentFlameJet(lvc, mat, 0.04F, length,        0.26F, 2.05F, 12, 9, clientSec,        0.36F, 235,  52,  18, (int) (95 * fade));
        drawTurbulentFlameJet(lvc, mat, 0.08F, length * 0.92F, 0.18F, 1.32F, 11, 8, clientSec * 1.22F, 0.30F, 255, 148,  22, (int) (155 * fade));
        drawTurbulentFlameJet(lvc, mat, 0.12F, length * 0.78F, 0.09F, 0.52F, 10, 7, clientSec * 1.48F, 0.22F, 255, 244, 172, (int) (215 * fade));

        // 8 licking jagged outer flame-tongue blades racing outward along the torrent
        drawFlameTongues(lvc, mat, 0.12F, length * 0.98F, 0.24F, 2.15F, 14, clientSec, fade);

        // 14 high-speed streaking diamond ember shards flaring off the jet
        drawEmberStreaks(lvc, mat, 0.20F, length * 1.05F, 0.28F, 2.45F, clientSec, fade);

        poseStack.popPose();
    }

    private static void drawTurbulentFlameJet(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
                                              float rStart, float rEnd, int lenSegs, int radSegs,
                                              float timeSec, float jagAmp, int r, int g, int b, int maxAlpha) {
        if (maxAlpha <= 5) return;
        for (int s = 0; s < lenSegs; s++) {
            float u0 = s / (float) lenSegs;
            float u1 = (s + 1) / (float) lenSegs;
            float z0 = -Mth.lerp(u0, zStart, zEnd);
            float z1 = -Mth.lerp(u1, zStart, zEnd);

            // Dynamic S-whip centerline turbulence so the stream never looks like a rigid pipe
            float whip0 = u0 * u0;
            float whip1 = u1 * u1;
            float cx0 = Mth.sin(u0 * 7.5F - timeSec * 17.0F) * 0.34F * whip0;
            float cy0 = Mth.cos(u0 * 6.2F - timeSec * 14.0F) * 0.28F * whip0;
            float cx1 = Mth.sin(u1 * 7.5F - timeSec * 17.0F) * 0.34F * whip1;
            float cy1 = Mth.cos(u1 * 6.2F - timeSec * 14.0F) * 0.28F * whip1;

            float baseR0 = Mth.lerp(u0 * 0.72F + u0 * u0 * 0.28F, rStart, rEnd);
            float baseR1 = Mth.lerp(u1 * 0.72F + u1 * u1 * 0.28F, rStart, rEnd);
            float env0 = (u0 < 0.10F ? (u0 / 0.10F) : 1.0F) * (1.0F - u0 * u0);
            float env1 = (u1 < 0.10F ? (u1 / 0.10F) : 1.0F) * (1.0F - u1 * u1);
            int a0 = Mth.clamp((int) (maxAlpha * env0), 0, 255);
            int a1 = Mth.clamp((int) (maxAlpha * env1), 0, 255);

            for (int i = 0; i < radSegs; i++) {
                float ang0 = (float) (Math.PI * 2.0D * i / radSegs);
                float ang1 = (float) (Math.PI * 2.0D * (i + 1) / radSegs);

                // Multi-lobe jagged star-flame profile (breaks circular cross-section!)
                float jag00 = 1.0F + jagAmp * Mth.sin(ang0 * 5.0F + u0 * 9.0F - timeSec * 20.0F)
                        + (jagAmp * 0.6F) * Mth.cos(ang0 * 3.0F - u0 * 12.0F + timeSec * 15.0F);
                float jag01 = 1.0F + jagAmp * Mth.sin(ang1 * 5.0F + u0 * 9.0F - timeSec * 20.0F)
                        + (jagAmp * 0.6F) * Mth.cos(ang1 * 3.0F - u0 * 12.0F + timeSec * 15.0F);
                float jag10 = 1.0F + jagAmp * Mth.sin(ang0 * 5.0F + u1 * 9.0F - timeSec * 20.0F)
                        + (jagAmp * 0.6F) * Mth.cos(ang0 * 3.0F - u1 * 12.0F + timeSec * 15.0F);
                float jag11 = 1.0F + jagAmp * Mth.sin(ang1 * 5.0F + u1 * 9.0F - timeSec * 20.0F)
                        + (jagAmp * 0.6F) * Mth.cos(ang1 * 3.0F - u1 * 12.0F + timeSec * 15.0F);

                float r00 = Math.max(0.03F, baseR0 * jag00);
                float r01 = Math.max(0.03F, baseR0 * jag01);
                float r10 = Math.max(0.03F, baseR1 * jag10);
                float r11 = Math.max(0.03F, baseR1 * jag11);

                float c0 = Mth.cos(ang0), s0 = Mth.sin(ang0);
                float c1 = Mth.cos(ang1), s1 = Mth.sin(ang1);
                vc.addVertex(mat, cx0 + c0 * r00, cy0 + s0 * r00, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, cx0 + c1 * r01, cy0 + s1 * r01, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, cx1 + c1 * r11, cy1 + s1 * r11, z1).setColor(r, g, b, a1);
                vc.addVertex(mat, cx1 + c0 * r10, cy1 + s0 * r10, z1).setColor(r, g, b, a1);
            }
        }
    }

    /** Jagged roaring flame-tongue blades licking outward around the breath jet. */
    private static void drawFlameTongues(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
                                         float rStart, float rEnd, int segs, float timeSec, float fade) {
        int tongues = 8;
        for (int t = 0; t < tongues; t++) {
            float baseAng = t * (Mth.TWO_PI / tongues) + (t % 2 == 0 ? 0.15F : -0.15F);
            boolean golden = (t % 2 == 0);
            int cr = 255;
            int cg = golden ? 192 : 92;
            int cb = golden ? 52 : 20;
            for (int i = 0; i < segs; i++) {
                float u0 = i / (float) segs;
                float u1 = (i + 1) / (float) segs;
                float z0 = -Mth.lerp(u0, zStart, zEnd);
                float z1 = -Mth.lerp(u1, zStart, zEnd);

                // Jagged sawtooth flame-lick flare along each blade
                float spike0 = 0.55F + 0.65F * Math.abs(Mth.sin(u0 * 14.0F - timeSec * 24.0F + t * 1.7F));
                float spike1 = 0.55F + 0.65F * Math.abs(Mth.sin(u1 * 14.0F - timeSec * 24.0F + t * 1.7F));
                float rIn0 = Mth.lerp(u0, rStart * 0.6F, rEnd * 0.55F);
                float rIn1 = Mth.lerp(u1, rStart * 0.6F, rEnd * 0.55F);
                float rOut0 = rIn0 + (0.22F + u0 * 1.15F) * spike0;
                float rOut1 = rIn1 + (0.22F + u1 * 1.15F) * spike1;

                float ang0 = baseAng + u0 * 3.2F * (golden ? 1.0F : -1.0F) - timeSec * (golden ? 7.5F : -6.2F);
                float ang1 = baseAng + u1 * 3.2F * (golden ? 1.0F : -1.0F) - timeSec * (golden ? 7.5F : -6.2F);

                int aIn0 = Mth.clamp((int) (185 * fade * (1.0F - u0 * 0.85F)), 0, 255);
                int aIn1 = Mth.clamp((int) (185 * fade * (1.0F - u1 * 0.85F)), 0, 255);
                int aOut0 = Mth.clamp((int) (120 * fade * (1.0F - u0)), 0, 255);
                int aOut1 = Mth.clamp((int) (120 * fade * (1.0F - u1)), 0, 255);

                float c0 = Mth.cos(ang0), s0 = Mth.sin(ang0);
                float c1 = Mth.cos(ang1), s1 = Mth.sin(ang1);
                vc.addVertex(mat, c0 * rIn0, s0 * rIn0, z0).setColor(255, 240, 155, aIn0);
                vc.addVertex(mat, c0 * rOut0, s0 * rOut0, z0).setColor(cr, cg, cb, aOut0);
                vc.addVertex(mat, c1 * rOut1, s1 * rOut1, z1).setColor(cr, cg, cb, aOut1);
                vc.addVertex(mat, c1 * rIn1, s1 * rIn1, z1).setColor(255, 240, 155, aIn1);
            }
        }
    }

    /** Fast-streaking diamond ember shards shooting outward along the breath torrent. */
    private static void drawEmberStreaks(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
                                         float rStart, float rEnd, float timeSec, float fade) {
        int count = 14;
        for (int i = 0; i < count; i++) {
            float cycle = ((timeSec * (2.2F + (i % 4) * 0.35F) + i * 0.19F) % 1.0F);
            float uTail = Math.max(0.0F, cycle - 0.14F);
            float uHead = cycle;
            float zTail = -Mth.lerp(uTail, zStart, zEnd);
            float zHead = -Mth.lerp(uHead, zStart, zEnd);
            float ang = i * 2.39996F + cycle * 2.4F;
            float radTail = Mth.lerp(uTail, rStart, rEnd) * (0.85F + (i % 3) * 0.18F);
            float radHead = Mth.lerp(uHead, rStart, rEnd) * (0.95F + (i % 3) * 0.22F);
            float c = Mth.cos(ang), s = Mth.sin(ang);
            float px = -s * 0.08F, py = c * 0.08F;
            int aHead = Mth.clamp((int) (235 * fade * (1.0F - uHead * 0.7F)), 0, 255);
            int aTail = Mth.clamp((int) (80 * fade * (1.0F - uTail)), 0, 255);
            vc.addVertex(mat, c * radTail - px, s * radTail - py, zTail).setColor(255, 110, 24, aTail);
            vc.addVertex(mat, c * radTail + px, s * radTail + py, zTail).setColor(255, 110, 24, aTail);
            vc.addVertex(mat, c * radHead + px * 0.3F, s * radHead + py * 0.3F, zHead).setColor(255, 248, 185, aHead);
            vc.addVertex(mat, c * radHead - px * 0.3F, s * radHead - py * 0.3F, zHead).setColor(255, 248, 185, aHead);
        }
    }

    private static void renderTelegraphZones(GoldflameQucusaurEntity entity, float bodyYaw, float partialTicks,
                                             float clientSec, PoseStack poseStack, MultiBufferSource buffer) {
        int st = entity.getAnimState();
        float ticks = entity.getStateTicks() + partialTicks;
        VertexConsumer lvc = buffer.getBuffer(RenderType.lightning());
        double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        Vec3 tele = entity.getSyncedTelegraphPos();
        double ox = tele.x - ex;
        double oy = tele.y - ey;
        double oz = tele.z - ez;
        float lockedYaw = entity.getSyncedTelegraphYaw();

        if (st == GoldflameQucusaurEntity.STATE_SHOCKWAVE_SLAM && ticks <= 17.0F) {
            float prog = Mth.clamp(ticks / 16.0F, 0.0F, 1.0F);
            drawTelegraphCircle(poseStack, lvc, ox, oy + 0.06D, oz, 9.0F, prog, 255, 78, 28);
        } else if (st == GoldflameQucusaurEntity.STATE_WING_SWIPE) {
            if (ticks <= 15.0F) {
                poseStack.pushPose();
                poseStack.translate(ox, oy, oz);
                drawTelegraphCone(poseStack, lvc, lockedYaw, 7.5F, 105.0F, Mth.clamp(ticks / 15.0F, 0.0F, 1.0F));
                poseStack.popPose();
            } else if (ticks >= 22.0F && ticks <= 38.0F) {
                poseStack.pushPose();
                poseStack.translate(ox, oy, oz);
                drawTelegraphCone(poseStack, lvc, lockedYaw, 7.5F, 105.0F, Mth.clamp((ticks - 22.0F) / 16.0F, 0.0F, 1.0F));
                poseStack.popPose();
            } else if (ticks >= 46.0F && ticks <= 60.0F) {
                drawTelegraphCircle(poseStack, lvc, ox, oy + 0.06D, oz, 6.8F, Mth.clamp((ticks - 46.0F) / 14.0F, 0.0F, 1.0F), 255, 130, 24);
            }
        } else if (st == GoldflameQucusaurEntity.STATE_CHARGE_DRILL && ticks <= 13.0F) {
            poseStack.pushPose();
            poseStack.translate(ox, oy, oz);
            drawTelegraphLane(poseStack, lvc, lockedYaw, 3.6F, 15.0F, Mth.clamp(ticks / 12.0F, 0.0F, 1.0F));
            poseStack.popPose();
        } else if (st == GoldflameQucusaurEntity.STATE_DIVE_BOMB && ticks <= 24.0F) {
            Vec3 tgt = entity.getSyncedDiveTarget();
            float rad = entity.isFeatherBrokenThisCycle() ? 5.5F : 8.0F;
            float prog = Mth.clamp(ticks / 20.0F, 0.0F, 1.0F);
            drawTelegraphCircle(poseStack, lvc, tgt.x - ex, tgt.y - ey + 0.08D, tgt.z - ez,
                    rad, prog, 255, 56, 22);
        }
    }

    private static void drawTelegraphCircle(PoseStack poseStack, VertexConsumer vc, double ox, double oy, double oz,
                                            float radius, float progress, int r, int g, int b) {
        poseStack.pushPose();
        poseStack.translate(ox, oy, oz);
        Matrix4f mat = poseStack.last().pose();
        int segs = 36;
        float fillR = Math.max(0.25F, radius * progress);
        int ringA = 205;
        int fillA = (int) (75 + 85 * progress);
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            // Outer border ring
            float rIn = Math.max(0.1F, radius - 0.35F);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, ringA);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, ringA);
            vc.addVertex(mat, c1 * radius, 0.0F, s1 * radius).setColor(255, 226, 118, ringA);
            vc.addVertex(mat, c0 * radius, 0.0F, s0 * radius).setColor(255, 226, 118, ringA);
            // Expanding inner warning fill
            vc.addVertex(mat, 0.0F, 0.01F, 0.0F).setColor(r, g, b, fillA / 2);
            vc.addVertex(mat, c0 * fillR, 0.01F, s0 * fillR).setColor(r, g, b, fillA);
            vc.addVertex(mat, c1 * fillR, 0.01F, s1 * fillR).setColor(r, g, b, fillA);
            vc.addVertex(mat, 0.0F, 0.01F, 0.0F).setColor(r, g, b, fillA / 2);
        }
        poseStack.popPose();
    }

    private static void drawTelegraphCone(PoseStack poseStack, VertexConsumer vc, float bodyYaw,
                                          float radius, float halfAngleDeg, float progress) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.06D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        Matrix4f mat = poseStack.last().pose();
        int segs = 18;
        float halfRad = (float) Math.toRadians(halfAngleDeg);
        float fillR = Math.max(0.5F, radius * progress);
        int fillA = (int) (75 + 95 * progress);
        for (int i = 0; i < segs; i++) {
            float a0 = Mth.lerp(i / (float) segs, -halfRad, halfRad);
            float a1 = Mth.lerp((i + 1) / (float) segs, -halfRad, halfRad);
            float x0 = Mth.sin(a0), z0 = -Mth.cos(a0);
            float x1 = Mth.sin(a1), z1 = -Mth.cos(a1);
            // Outer arc
            float rIn = radius - 0.35F;
            vc.addVertex(mat, x0 * rIn, 0.0F, z0 * rIn).setColor(255, 92, 24, 210);
            vc.addVertex(mat, x1 * rIn, 0.0F, z1 * rIn).setColor(255, 92, 24, 210);
            vc.addVertex(mat, x1 * radius, 0.0F, z1 * radius).setColor(255, 224, 112, 215);
            vc.addVertex(mat, x0 * radius, 0.0F, z0 * radius).setColor(255, 224, 112, 215);
            // Inner progress wedge
            vc.addVertex(mat, 0.0F, 0.01F, 0.0F).setColor(255, 72, 20, fillA / 2);
            vc.addVertex(mat, x0 * fillR, 0.01F, z0 * fillR).setColor(255, 112, 28, fillA);
            vc.addVertex(mat, x1 * fillR, 0.01F, z1 * fillR).setColor(255, 112, 28, fillA);
            vc.addVertex(mat, 0.0F, 0.01F, 0.0F).setColor(255, 72, 20, fillA / 2);
        }
        poseStack.popPose();
    }

    private static void drawTelegraphLane(PoseStack poseStack, VertexConsumer vc, float bodyYaw,
                                          float halfWidth, float length, float progress) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.06D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        Matrix4f mat = poseStack.last().pose();
        float curL = Math.max(1.0F, length * progress);
        int fillA = (int) (80 + 100 * progress);
        // Outer outline rectangle
        vc.addVertex(mat, -halfWidth, 0.0F, 0.0F).setColor(255, 80, 22, 110);
        vc.addVertex(mat,  halfWidth, 0.0F, 0.0F).setColor(255, 80, 22, 110);
        vc.addVertex(mat,  halfWidth, 0.0F, -length).setColor(255, 218, 96, 150);
        vc.addVertex(mat, -halfWidth, 0.0F, -length).setColor(255, 218, 96, 150);
        // Advancing charge progress fill
        vc.addVertex(mat, -halfWidth * 0.85F, 0.015F, 0.0F).setColor(255, 110, 28, fillA);
        vc.addVertex(mat,  halfWidth * 0.85F, 0.015F, 0.0F).setColor(255, 110, 28, fillA);
        vc.addVertex(mat,  halfWidth * 0.85F, 0.015F, -curL).setColor(255, 240, 140, fillA);
        vc.addVertex(mat, -halfWidth * 0.85F, 0.015F, -curL).setColor(255, 240, 140, fillA);
        poseStack.popPose();
    }
}
