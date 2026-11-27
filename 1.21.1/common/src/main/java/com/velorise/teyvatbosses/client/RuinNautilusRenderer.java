package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinNautilusAnimations;
import com.velorise.teyvatbosses.client.model.DepthMineModel;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import net.minecraft.client.renderer.LightTexture;
import com.velorise.teyvatbosses.entity.RuinNautilusEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

/**
 * AAA Renderer for {@link RuinNautilusEntity} (scale 2.35x) with 75-bone shortest-arc cross-fading,
 * 512x512 diffuse + pixel-aligned emissive maps, volumetric 3D multi-layer Abyssal Hydro Beam &amp;
 * Phase 2 Dorsal Railgun VFX (with laser telegraph sightline), and Harpoon Tether energy cables.
 */
public class RuinNautilusRenderer extends EntityRenderer<RuinNautilusEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final ResourceLocation GLASS_TEX = TeyvatBossesConstants.id("textures/entity/ruin_submersible_glass.png");
    private static final ResourceLocation MINE_TEX = TeyvatBossesConstants.id("textures/entity/ruin_depth_mine.png");
    private static final ResourceLocation MINE_EM = TeyvatBossesConstants.id("textures/entity/ruin_depth_mine_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.ruin_nautilus_theme");
    private static final float BOSS_SCALE = 2.35F;
    private static final float BLEND_TICKS = 7.0F;

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot;
        void capture(ModelPart p) { x = p.x; y = p.y; z = p.z; xRot = p.xRot; yRot = p.yRot; zRot = p.zRot; }
        void copyFrom(BonePose o) { x = o.x; y = o.y; z = o.z; xRot = o.xRot; yRot = o.yRot; zRot = o.zRot; }
    }

    private static final class SmoothState {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStart = -100.0F;
        float frozenEvalSec = 0.0F;
        boolean wasHitstop = false;
        boolean hasLast = false;
        int lastImpactEpoch = -1;
        final Map<String, BonePose> from = new HashMap<>();
        final Map<String, BonePose> last = new HashMap<>();
    }

    private final RuinNautilusModel<RuinNautilusEntity> model;
    private final DepthMineModel<RuinNautilusEntity> mineModel;
    private final Map<Integer, SmoothState> smoothMap = new HashMap<>();

    public RuinNautilusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 3.2F;
        this.model = new RuinNautilusModel<>(ctx.bakeLayer(RuinNautilusModel.LAYER_LOCATION));
        this.mineModel = new DepthMineModel<>(ctx.bakeLayer(DepthMineModel.LAYER_LOCATION));
    }

    private static float lerpAngle(float from, float to, float a) {
        float d = (to - from) % Mth.TWO_PI;
        if (d > Mth.PI) d -= Mth.TWO_PI;
        if (d < -Mth.PI) d += Mth.TWO_PI;
        return from + d * a;
    }

    @Override
    public void render(RuinNautilusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float liveTick = entity.tickCount + partialTicks;
        float sec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String clip = entity.getCurrentClipName();
        int epoch = entity.getAnimEpoch();

        int st = entity.getBossState();
        boolean musicActive = entity.isAlive()
                && entity.hasActiveCombatTarget() && (st == RuinNautilusEntity.STATE_COMBAT || st == RuinNautilusEntity.STATE_STUNNED || st == RuinNautilusEntity.STATE_PHASE2_CIN);
        BossMusicManager.update(entity, MUSIC_ID, musicActive, () -> entity.hasActiveCombatTarget() && (entity.getBossState() == RuinNautilusEntity.STATE_COMBAT || entity.getBossState() == RuinNautilusEntity.STATE_STUNNED || entity.getBossState() == RuinNautilusEntity.STATE_PHASE2_CIN));

        SmoothState sm = smoothMap.computeIfAbsent(entity.getId(), k -> new SmoothState());
        if (sm.lastClip != null && (!sm.lastClip.equals(clip) || sm.lastEpoch != epoch) && sm.hasLast) {
            for (Map.Entry<String, BonePose> e : sm.last.entrySet()) {
                sm.from.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
            }
            sm.blendStart = liveTick;
        }
        sm.lastClip = clip;
        sm.lastEpoch = epoch;

        // Unified AAA hitstop & camera shake on core stun & Phase 2 overdrive transition
        if ((st == RuinNautilusEntity.STATE_STUNNED || st == RuinNautilusEntity.STATE_PHASE2_CIN)
                && entity.getAnimTick() <= 2 && sm.lastImpactEpoch != epoch) {
            sm.lastImpactEpoch = epoch;
            BossCameraShake.triggerHitstop(entity.position(), 4, 0.54F, 48.0D);
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-BOSS_SCALE, -BOSS_SCALE, BOSS_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        float evalSec = sec;
        if (BossCameraShake.isHitstopActive()) {
            if (!sm.wasHitstop) {
                sm.frozenEvalSec = evalSec;
                sm.wasHitstop = true;
            }
            evalSec = sm.frozenEvalSec;
        } else {
            sm.wasHitstop = false;
        }
        RuinNautilusAnimations.apply(model, clip, evalSec, entity.isPhase2());
        ModelPart hullPitch = model.getBone("hull_pitch");
        if (hullPitch != null) {
            hullPitch.xRot += entity.getHullPitchDeg() * Mth.DEG_TO_RAD;
        }

        // Smooth shortest-arc cross-fade across all 75 bones
        float elapsed = liveTick - sm.blendStart;
        if (sm.hasLast && elapsed >= 0.0F && elapsed < BLEND_TICKS) {
            float raw = Mth.clamp(elapsed / BLEND_TICKS, 0.0F, 1.0F);
            float a = raw * raw * (3.0F - 2.0F * raw);
            for (String b : RuinNautilusModel.ALL_BONES) {
                ModelPart p = model.getBone(b);
                BonePose prev = sm.from.get(b);
                if (p == null || prev == null) continue;
                p.x = Mth.lerp(a, prev.x, p.x);
                p.y = Mth.lerp(a, prev.y, p.y);
                p.z = Mth.lerp(a, prev.z, p.z);
                p.xRot = lerpAngle(prev.xRot, p.xRot, a);
                p.yRot = lerpAngle(prev.yRot, p.yRot, a);
                p.zRot = lerpAngle(prev.zRot, p.zRot, a);
            }
        }
        for (String b : RuinNautilusModel.ALL_BONES) {
            ModelPart p = model.getBone(b);
            if (p != null) sm.last.computeIfAbsent(b, k -> new BonePose()).capture(p);
        }
        sm.hasLast = true;

        // Show steel-framed cockpit + Drowned Captain Pilot on the Boss (glass rendered in translucent pass)
        setVis("sub_cockpit_frame", true);
        setVis("drowned_pilot", true);
        setVis("sub_cockpit_glass", false);
        setVis("bow_dome", true);
        setVis("dorsal_railgun_base", true);
        setVis("harpoon_arm_l", true);
        setVis("harpoon_arm_r", true);

        int firedMask = entity.getTorpedoFiredMask();
        for (int i = 0; i < 6; i++) {
            setVis("torpedo_tip_" + i, (firedMask & (1 << i)) == 0);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer baseVc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, baseVc, packedLight, overlay, 0xFFFFFFFF);

        if (st != RuinNautilusEntity.STATE_DORMANT) {
            VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        // Translucent 4-face panoramic cockpit glass so the Drowned Captain & levers are clearly visible inside!
        ModelPart glassBone = model.getBone("sub_cockpit_glass");
        if (glassBone != null && hullPitch != null) {
            poseStack.pushPose();
            ModelPart rootPart = model.getBone("root");
            if (rootPart != null) rootPart.translateAndRotate(poseStack);
            hullPitch.translateAndRotate(poseStack);
            ModelPart hullMain = model.getBone("hull_main");
            if (hullMain != null) hullMain.translateAndRotate(poseStack);
            glassBone.visible = true;
            VertexConsumer glassVc = buffers.getBuffer(RenderType.entityTranslucent(GLASS_TEX));
            glassBone.render(poseStack, glassVc, packedLight, OverlayTexture.NO_OVERLAY, 0xCCFFFFFF);
            poseStack.popPose();
        }

        // Restore defaults
        setVis("sub_cockpit_frame", true);
        setVis("sub_cockpit_glass", true);
        for (int i = 0; i < 6; i++) setVis("torpedo_tip_" + i, true);
        poseStack.popPose();

        // Volumetric 3D Sweeping Abyssal Eye Beam (t=24..58)
        int animId = entity.getAnimId();
        float t = entity.getAnimTick() + partialTicks;
        if (animId == RuinNautilusEntity.ANIM_BEAM && t >= 24.0F && t <= 58.0F) {
            float prog = Mth.clamp((t - 24.0F) / 34.0F, 0.0F, 1.0F);
            float beamYaw = yaw + Mth.lerp(prog, -42.0F, 42.0F);
            renderVolumetricBeam(poseStack, buffers, beamYaw, entity.getHullPitchDeg(), 1.85D, 3.8F, 22.0F, 0.36F, liveTick, false);
        }

        // Phase 2 Dorsal Railgun: Laser Telegraph Sightline (t=4..35) -> Superbeam Discharge (t=36..62)
        if (animId == RuinNautilusEntity.ANIM_RAILGUN) {
            float rYaw = entity.getRailgunYawDeg();
            float rPitch = entity.getRailgunPitchDeg();
            if (t >= 4.0F && t < 36.0F) {
                renderTelegraphSightline(poseStack, buffers, rYaw, rPitch, 3.85D, 3.2F, 32.0F, t);
            } else if (t >= 36.0F && t <= 62.0F) {
                renderVolumetricBeam(poseStack, buffers, rYaw, rPitch, 3.85D, 3.2F, 32.0F, 0.62F, liveTick, true);
            }
        }

        // Render all active Acoustic Depth Mines as TRUE SOLID 3D Naval Sea Mines!
        renderSolid3DDepthMines(entity, partialTicks, poseStack, buffers, packedLight, liveTick);

        // Ruin-Guard-style Red Expanding AoE Warning Zone (Sonar, Fin Sweep, Whirlpool, Depth Mines, Breach)
        float teleR = entity.getTelegraphRadius();
        if (teleR > 0.5F) {
            float prog = switch (animId) {
                case RuinNautilusEntity.ANIM_SONAR -> Mth.clamp((t - 1.0F) / 17.0F, 0.0F, 1.0F);
                case RuinNautilusEntity.ANIM_FIN_SWEEP -> Mth.clamp((t - 1.0F) / 15.0F, 0.0F, 1.0F);
                case RuinNautilusEntity.ANIM_WHIRLPOOL -> Mth.clamp((t - 1.0F) / 15.0F, 0.0F, 1.0F);
                case RuinNautilusEntity.ANIM_DEPTH_MINES -> Mth.clamp((t - 1.0F) / 21.0F, 0.0F, 1.0F);
                case RuinNautilusEntity.ANIM_SONAR_BREACH -> Mth.clamp((t - 1.0F) / 23.0F, 0.0F, 1.0F);
                default -> 0.5F;
            };
            float fillR = Math.max(0.20F, teleR * prog);
            VertexConsumer tvc = buffers.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.65D, 0.0D);
            Matrix4f tm = poseStack.last().pose();
            drawHorizontalRing(tvc, tm, teleR - 0.34F, teleR, 40, 235, 28, 36, 225);
            drawHorizontalRing(tvc, tm, 0.10F, Math.max(0.12F, teleR - 0.34F), 36, 200, 18, 24, 42);
            drawHorizontalRing(tvc, tm, 0.10F, fillR, 36, 255, 42, 48, 86);
            drawHorizontalRing(tvc, tm, Math.max(0.10F, fillR - 0.24F), fillR, 40, 255, 85, 78, 210);
            poseStack.popPose();
        }

        // Render 3D Expanding Hydro-Cyan Sonar Shockwave Rings during ANIM_SONAR
        if (animId == RuinNautilusEntity.ANIM_SONAR && t >= 16.0F && t <= 42.0F) {
            renderSonarRings3D(poseStack, buffers, t);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private void setVis(String bone, boolean v) {
        ModelPart p = model.getBone(bone);
        if (p != null) p.visible = v;
    }

    private static void renderTelegraphSightline(PoseStack poseStack, MultiBufferSource buffers,
                                                 float yawDeg, float pitchDeg, double yOff, float zStart, float len, float t) {
        poseStack.pushPose();
        poseStack.translate(0.0D, yOff, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchDeg));
        Matrix4f m = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        float pulse = 0.06F + 0.04F * Mth.sin(t * 0.9F);
        int alpha = t > 26.0F ? 235 : 155;
        drawRibbon(vc, m, pulse, zStart, len, 255, 70, 90, alpha);
        poseStack.popPose();
    }

    private static void renderVolumetricBeam(PoseStack poseStack, MultiBufferSource buffers,
                                             float yawDeg, float pitchDeg, double yOff, float zStart, float len,
                                             float outerRadius, float liveTick, boolean railgun) {
        poseStack.pushPose();
        poseStack.translate(0.0D, yOff, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchDeg));
        poseStack.mulPose(Axis.ZP.rotationDegrees(liveTick * 18.0F));
        Matrix4f m = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());

        // Inner blazing white-cyan core (4 crossed planes for full 360-deg thickness)
        float coreR = outerRadius * 0.45F;
        drawRibbon(vc, m, coreR, zStart, len, 240, 255, 255, 245);

        // Outer rotating plasma sheath (cyan for Eye Beam, golden-cyan overdrive for Railgun)
        int r = railgun ? 255 : 60;
        int g = railgun ? 215 : 230;
        int b = railgun ? 110 : 255;
        drawRibbon(vc, m, outerRadius, zStart, len, r, g, b, 155);

        // 45-degree rotated secondary sheath for volumetric hexagonal tube appearance
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        Matrix4f m2 = poseStack.last().pose();
        drawRibbon(vc, m2, outerRadius * 0.92F, zStart, len, 80, 240, 255, 130);
        poseStack.popPose();
    }

    private static void drawRibbon(VertexConsumer vc, Matrix4f m, float r, float zStart, float len, int red, int grn, int blu, int a) {
        float z0 = -zStart;
        float z1 = -zStart - len;
        vc.addVertex(m, -r, 0, z0).setColor(red, grn, blu, a);
        vc.addVertex(m,  r, 0, z0).setColor(red, grn, blu, a);
        vc.addVertex(m,  r, 0, z1).setColor(red, grn, blu, Math.max(80, a - 40));
        vc.addVertex(m, -r, 0, z1).setColor(red, grn, blu, Math.max(80, a - 40));

        vc.addVertex(m, 0, -r, z0).setColor(red, grn, blu, a);
        vc.addVertex(m, 0,  r, z0).setColor(red, grn, blu, a);
        vc.addVertex(m, 0,  r, z1).setColor(red, grn, blu, Math.max(80, a - 40));
        vc.addVertex(m, 0, -r, z1).setColor(red, grn, blu, Math.max(80, a - 40));
    }


    private void renderSolid3DDepthMines(RuinNautilusEntity entity, float partialTicks, PoseStack poseStack,
                                         MultiBufferSource buffers, int packedLight, float liveTick) {
        String data = entity.getSyncedMinesData();
        if (data == null || data.isEmpty()) return;
        double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        String[] entries = data.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] xyz = entries[i].split(",");
            if (xyz.length != 3) continue;
            try {
                double mx = Double.parseDouble(xyz[0]);
                double my = Double.parseDouble(xyz[1]);
                double mz = Double.parseDouble(xyz[2]);
                poseStack.pushPose();
                float bob = Mth.sin(liveTick * 0.12F + i * 1.3F) * 0.18F;
                poseStack.translate(mx - ex, my - ey + bob, mz - ez);
                poseStack.mulPose(Axis.YP.rotationDegrees(liveTick * 2.5F + i * 52.0F));
                poseStack.scale(-1.15F, -1.15F, 1.15F);
                VertexConsumer mvc = buffers.getBuffer(RenderType.entityCutoutNoCull(MINE_TEX));
                mineModel.renderToBuffer(poseStack, mvc, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                // Pulsing red proximity warning beacon
                if (((int) (liveTick + i * 5) / 6) % 2 == 0) {
                    VertexConsumer mevc = buffers.getBuffer(RenderType.eyes(MINE_EM));
                    mineModel.renderToBuffer(poseStack, mevc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                }
                poseStack.popPose();
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void renderSonarRings3D(PoseStack poseStack, MultiBufferSource buffers, float t) {
        int[] starts = {18, 26, 34};
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        for (int st : starts) {
            if (t < st || t > st + 12.0F) continue;
            float p = (t - st) / 12.0F;
            float r0 = 2.0F + p * 8.5F;
            float r1 = r0 + 0.35F;
            int alpha = (int) ((1.0F - p) * 210.0F);
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.8D, 0.0D);
            Matrix4f m = poseStack.last().pose();
            int segs = 24;
            for (int i = 0; i < segs; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / segs);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / segs);
                float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
                float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
                vc.addVertex(m, c0 * r0, 0.0F, s0 * r0).setColor(80, 245, 255, alpha);
                vc.addVertex(m, c1 * r0, 0.0F, s1 * r0).setColor(80, 245, 255, alpha);
                vc.addVertex(m, c1 * r1, 0.0F, s1 * r1).setColor(190, 255, 255, alpha / 2);
                vc.addVertex(m, c0 * r1, 0.0F, s0 * r1).setColor(190, 255, 255, alpha / 2);
            }
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RuinNautilusEntity entity) {
        return TEXTURE;
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        if (a <= 2) return;
        for (int i = 0; i < segs; i++) {
            double a0 = (Math.PI * 2.0D * i) / segs;
            double a1 = (Math.PI * 2.0D * (i + 1)) / segs;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
        }
    }
}
