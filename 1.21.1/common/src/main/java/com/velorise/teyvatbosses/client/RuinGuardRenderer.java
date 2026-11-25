package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.entity.RuinGuardEntity;
import net.minecraft.client.Minecraft;
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
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * High-craft renderer for Ruin Guard (GN/Hu-24601) using Catmull-Rom blended animations,
 * dual-pass diffuse + emissive eye/core glow, ground AoE telegraph rings, Cyclone Spin slash VFX,
 * and automatic looping Ruin Guard orchestral-mech boss music.
 */
public class RuinGuardRenderer extends EntityRenderer<RuinGuardEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.ruin_guard_theme");

    private static final float BLEND_TICKS = 6.0F;

    private static final class AnimSM {
        String prevClip = null;
        float prevTimeSec = 0.0F;
        String currClip = null;
        int lastEpoch = -1;
        float blendStartTick = -100.0F;
        BossMusicInstance music = null;
    }

    private static final class BossMusicInstance extends AbstractTickableSoundInstance {
        private final RuinGuardEntity boss;
        BossMusicInstance(SoundEvent ev, RuinGuardEntity boss) {
            super(ev, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.boss = boss;
            this.looping = true;
            this.volume = 0.72F;
            this.pitch = 1.0F;
            this.relative = true;
        }
        @Override
        public void tick() {
            int st = boss.getBossState();
            if (boss.isRemoved() || !boss.isAlive() || st == RuinGuardEntity.STATE_DORMANT || st == RuinGuardEntity.STATE_DEATH) {
                this.stop();
            }
        }
    }

    private final RuinGuardModel<RuinGuardEntity> model;
    private final Map<RuinGuardEntity, AnimSM> stateMachines = new WeakHashMap<>();

    public RuinGuardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.55F;
        this.model = new RuinGuardModel<>(context.bakeLayer(RuinGuardModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            RuinGuardEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float liveTick = entity.tickCount + partialTicks;
        float currSec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String clip = entity.getCurrentAnimName();
        int epoch = entity.getAnimEpoch();

        AnimSM sm = stateMachines.computeIfAbsent(entity, k -> new AnimSM());
        updateBossMusic(entity, sm);

        if (sm.currClip != null && (!sm.currClip.equals(clip) || sm.lastEpoch != epoch)) {
            sm.prevClip = sm.currClip;
            sm.prevTimeSec = Math.max(0.0F, currSec);
            sm.blendStartTick = liveTick;
        }
        sm.currClip = clip;
        sm.lastEpoch = epoch;

        // World-space combat VFX (AoE telegraph rings & Cyclone Spin wind slash rings)
        renderCombatVFX(entity, liveTick, poseStack, bufferSource);

        poseStack.pushPose();
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        float blendAlpha = Mth.clamp((liveTick - sm.blendStartTick) / BLEND_TICKS, 0.0F, 1.0F);
        if (sm.prevClip != null && blendAlpha < 0.99F) {
            RuinGuardAnimations.applyBlended(model, sm.prevClip, sm.prevTimeSec, clip, currSec, blendAlpha);
        } else {
            RuinGuardAnimations.apply(model, clip, currSec);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Emissive glowing amber eye & reactor core (dimmed during stunned/dormant)
        if (entity.getBossState() != RuinGuardEntity.STATE_DORMANT && entity.getBossState() != RuinGuardEntity.STATE_DEATH) {
            VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void updateBossMusic(RuinGuardEntity entity, AnimSM sm) {
        int st = entity.getBossState();
        boolean active = entity.isAlive() && st != RuinGuardEntity.STATE_DORMANT && st != RuinGuardEntity.STATE_DEATH;
        Minecraft mc = Minecraft.getInstance();
        if (active) {
            if (sm.music == null || !mc.getSoundManager().isActive(sm.music)) {
                SoundEvent ev = BuiltInRegistries.SOUND_EVENT.get(MUSIC_ID);
                if (ev != null) {
                    sm.music = new BossMusicInstance(ev, entity);
                    mc.getSoundManager().play(sm.music);
                }
            }
        } else if (sm.music != null) {
            mc.getSoundManager().stop(sm.music);
            sm.music = null;
        }
    }

    private static void renderCombatVFX(RuinGuardEntity entity, float liveTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        float teleR = entity.getTelegraphRadius();
        if (teleR > 0.5F) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.12D, 0.0D);
            Matrix4f m = poseStack.last().pose();
            float pulse = 0.85F + 0.15F * Mth.sin(liveTick * 0.7F);
            drawHorizontalRing(vc, m, teleR - 0.35F, teleR, 36, 255, 110, 35, (int) (200 * pulse));
            drawHorizontalRing(vc, m, 0.4F, teleR * pulse, 28, 255, 165, 55, 55);
            poseStack.popPose();
        }

        if (entity.getAnimId() == RuinGuardEntity.ANIM_CYCLONE_ACTIVE) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.95D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(liveTick * 26.0F));
            Matrix4f m = poseStack.last().pose();
            drawHorizontalRing(vc, m, 3.35F, 4.75F, 32, 255, 220, 145, 145);
            drawHorizontalRing(vc, m, 4.35F, 4.85F, 32, 255, 140, 45, 195);
            poseStack.popPose();
        }
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
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

    @Override
    public ResourceLocation getTextureLocation(RuinGuardEntity entity) {
        return TEXTURE;
    }
}
