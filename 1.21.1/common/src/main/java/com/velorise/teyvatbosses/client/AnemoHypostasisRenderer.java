package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisAnimations;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.entity.AnemoHypostasisEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Genshin-accurate renderer for Anemo Hypostasis (Beth) with:
 * - Pose-Snapshot 8-tick cross-fade animation blending (zero 1-frame snaps)
 * - Dual-pass Diffuse + Emissive Rune Glow rendering
 * - 3D Magic Circle Mandala, Ground Eruption Telegraph Ring, Vacuum Vortex & Exposed Core Halo
 * - Automatic looping Anemo Hypostasis combat boss music
 */
public class AnemoHypostasisRenderer extends EntityRenderer<AnemoHypostasisEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.anemo_hypostasis_theme");

    private static final float RENDER_SCALE = 1.25F;
    private static final float BLEND_TICKS = 7.0F;

    private static final String[] ALL_BONES = {
            "root", "body_pivot", "core_group", "core_frame", "shell_ring",
            "shell_0", "shell_1", "shell_2", "shell_3", "shell_4", "shell_5", "shell_6", "shell_7",
            "mini_0", "mini_1", "mini_2", "mini_3"
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

    private static final class AnimSM {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStartTick = -100.0F;
        boolean hasLast = false;
        final Map<String, BonePose> from = new HashMap<>();
        final Map<String, BonePose> last = new HashMap<>();
        BossMusicInstance musicInstance = null;
    }

    private static final class BossMusicInstance extends AbstractTickableSoundInstance {
        private final AnemoHypostasisEntity boss;

        BossMusicInstance(SoundEvent event, AnemoHypostasisEntity boss) {
            super(event, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.boss = boss;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.72F;
            this.pitch = 1.0F;
            this.relative = true;
        }

        @Override
        public void tick() {
            int st = boss.getBossState();
            if (boss.isRemoved() || !boss.isAlive() || st == AnemoHypostasisEntity.STATE_DORMANT || st == AnemoHypostasisEntity.STATE_DEATH) {
                this.stop();
            }
        }
    }

    private final AnemoHypostasisModel<AnemoHypostasisEntity> model;
    private final Map<AnemoHypostasisEntity, AnimSM> stateMachines = new WeakHashMap<>();

    public AnemoHypostasisRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.35F;
        this.model = new AnemoHypostasisModel<>(context.bakeLayer(AnemoHypostasisModel.LAYER_LOCATION));
    }

    private static float wrapRad(float rad) {
        float twoPi = (float) (Math.PI * 2.0D);
        float r = rad % twoPi;
        if (r > Math.PI) r -= twoPi;
        if (r < -Math.PI) r += twoPi;
        return r;
    }

    private static float lerpAngle(float from, float to, float a) {
        return from + wrapRad(to - from) * a;
    }

    @Override
    public void render(
            AnemoHypostasisEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float liveTick = entity.tickCount + partialTicks;
        float animTicksF = entity.getAnimTick() + partialTicks;
        float sec = animTicksF / 20.0F;
        float liveSec = liveTick / 20.0F;
        String clip = entity.getCurrentAnimName();
        int epoch = entity.getAnimEpoch();

        AnimSM sm = stateMachines.computeIfAbsent(entity, k -> new AnimSM());
        updateBossMusic(entity, sm);

        if (sm.lastClip != null && (!sm.lastClip.equals(clip) || sm.lastEpoch != epoch)) {
            if (sm.hasLast) {
                for (Map.Entry<String, BonePose> e : sm.last.entrySet()) {
                    sm.from.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
                }
                sm.blendStartTick = liveTick;
            }
        }
        sm.lastClip = clip;
        sm.lastEpoch = epoch;

        // World-space VFX (Ground Telegraph Ring, Vacuum Vortex, Magic Circle, Butterfly Wing Glow)
        renderCombatVFX(entity, animTicksF, liveTick, partialTicks, poseStack, bufferSource);

        poseStack.pushPose();
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-RENDER_SCALE, -RENDER_SCALE, RENDER_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        AnemoHypostasisAnimations.apply(model, clip, sec, liveSec);

        // Smooth shortest-arc Pose-Snapshot cross-fade
        float elapsed = liveTick - sm.blendStartTick;
        if (sm.hasLast && elapsed >= 0.0F && elapsed < BLEND_TICKS) {
            float rawA = Mth.clamp(elapsed / BLEND_TICKS, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String b : ALL_BONES) {
                ModelPart p = model.getBone(b);
                BonePose prev = sm.from.get(b);
                if (p == null || prev == null) continue;
                p.x = Mth.lerp(alpha, prev.x, p.x);
                p.y = Mth.lerp(alpha, prev.y, p.y);
                p.z = Mth.lerp(alpha, prev.z, p.z);
                p.xRot = lerpAngle(prev.xRot, p.xRot, alpha);
                p.yRot = lerpAngle(prev.yRot, p.yRot, alpha);
                p.zRot = lerpAngle(prev.zRot, p.zRot, alpha);
                p.xScale = Mth.lerp(alpha, prev.xScale, p.xScale);
                p.yScale = Mth.lerp(alpha, prev.yScale, p.yScale);
                p.zScale = Mth.lerp(alpha, prev.zScale, p.zScale);
            }
        }

        for (String b : ALL_BONES) {
            ModelPart p = model.getBone(b);
            if (p != null) {
                sm.last.computeIfAbsent(b, k -> new BonePose()).capture(p);
            }
        }
        sm.hasLast = true;

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        // Pass 1: Solid dark-slate carved stone cubes + core
        VertexConsumer baseVc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, baseVc, packedLight, overlay, 0xFFFFFFFF);

        // Pass 2: Full-bright glowing Anemo runes & luminous crystal core!
        VertexConsumer emisVc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
        model.renderToBuffer(poseStack, emisVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void updateBossMusic(AnemoHypostasisEntity entity, AnimSM sm) {
        int st = entity.getBossState();
        boolean active = entity.isAlive() && st != AnemoHypostasisEntity.STATE_DORMANT && st != AnemoHypostasisEntity.STATE_DEATH;
        BossMusicManager.update(entity, MUSIC_ID, active);
    }

    private static void renderCombatVFX(
            AnemoHypostasisEntity entity,
            float animTick,
            float liveTick,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        int anim = entity.getAnimId();
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        // 1. Magic Circle Mandala inside the 8-cube ring during Missile Volley!
        if (anim == AnemoHypostasisEntity.ANIM_MISSILE_CIRCLE && animTick >= 10.0F && animTick <= 132.0F) {
            float fade = animTick < 22.0F ? (animTick - 10.0F) / 12.0F
                    : (animTick > 120.0F ? Math.max(0.0F, (132.0F - animTick) / 12.0F) : 1.0F);
            poseStack.pushPose();
            poseStack.translate(0.0D, 3.65D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
            poseStack.translate(0.0D, 0.0D, -0.78D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(liveTick * 3.0F));
            Matrix4f m = poseStack.last().pose();
            drawVerticalRing(vc, m, 1.85F, 2.08F, 28, 120, 252, 224, (int) (185 * fade));
            drawVerticalRing(vc, m, 1.15F, 1.30F, 24, 210, 255, 246, (int) (165 * fade));
            poseStack.popPose();
        }

        // 2. Ground Eruption Telegraph Ring under player's marked position!
        if (entity.isTelegraphActive()) {
            Vec3 tp = entity.getTelegraphPos();
            double rx = tp.x - Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ry = tp.y - Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.12D;
            double rz = tp.z - Mth.lerp(partialTicks, entity.zo, entity.getZ());
            poseStack.pushPose();
            poseStack.translate(rx, ry, rz);
            Matrix4f m = poseStack.last().pose();
            float tF = entity.getAnimTick() + partialTicks;
            float prog = (tF < 36.0F) ? Mth.clamp((tF - 12.0F) / 18.0F, 0.0F, 1.0F)
                    : (tF < 66.0F ? Mth.clamp((tF - 42.0F) / 18.0F, 0.0F, 1.0F)
                    : Mth.clamp((tF - 72.0F) / 18.0F, 0.0F, 1.0F));
            float outerR = 3.25F;
            float fillR = Math.max(0.15F, outerR * prog);
            drawHorizontalRing(vc, m, outerR - 0.30F, outerR, 36, 235, 28, 36, 225);
            drawHorizontalRing(vc, m, 0.08F, outerR - 0.30F, 32, 200, 18, 24, 42);
            drawHorizontalRing(vc, m, 0.08F, fillR, 32, 255, 42, 48, 86);
            drawHorizontalRing(vc, m, Math.max(0.08F, fillR - 0.20F), fillR, 36, 255, 85, 78, 210);
            poseStack.popPose();
        }

        // 3. Vacuum Pull Vortex Rings on arena floor
        if (anim == AnemoHypostasisEntity.ANIM_VACUUM_IMPLOSION && animTick >= 8.0F && animTick <= 76.0F) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.14D, 0.0D);
            Matrix4f m = poseStack.last().pose();
            for (int rIdx = 0; rIdx < 3; rIdx++) {
                float frac = 1.0F - (((liveTick * 0.055F) + rIdx / 3.0F) % 1.0F);
                float rad = 1.8F + frac * 10.5F;
                drawHorizontalRing(vc, m, rad - 0.25F, rad, 32, 115, 248, 222, (int) (145 * (1.0F - Math.abs(frac - 0.5F) * 1.6F)));
            }
            poseStack.popPose();
        }

        // 4. Exposed Core DPS Halo on floor
        if (entity.isCoreExposed()) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.12D, 0.0D);
            Matrix4f m = poseStack.last().pose();
            float p = 0.9F + 0.1F * Mth.sin(liveTick * 0.35F);
            drawHorizontalRing(vc, m, 1.35F * p, 1.65F * p, 24, 175, 255, 240, 150);
            poseStack.popPose();
        }
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

    private static void drawVerticalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        if (a <= 2) return;
        for (int i = 0; i < segs; i++) {
            double a0 = (Math.PI * 2.0D * i) / segs;
            double a1 = (Math.PI * 2.0D * (i + 1)) / segs;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, 0.0F).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AnemoHypostasisEntity entity) {
        return TEXTURE;
    }
}
