package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.TheOpenEyedAnimations;
import com.velorise.teyvatbosses.client.model.TheOpenEyedModel;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Renders TheOpenEyedEntity (Watcher: Fallen Vigil) with smooth 4-tick animation cross-fading
 * and procedural golden/violet Overseer aura rings during Supreme Overseer & Phenomena's Delirium.
 */
public class TheOpenEyedRenderer extends EntityRenderer<TheOpenEyedEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/the_open_eyed.png");
    private static final float BOSS_RENDER_SCALE = 1.15F;
    private static final float BLEND_DURATION_TICKS = 4.0F;

    private static final class BlendState {
        String prevClip;
        float prevTimeSec;
        String lastClip;
        int lastEpoch = -1;
        float lastTimeSec;
    }

    private final TheOpenEyedModel<TheOpenEyedEntity> model;
    private final Map<TheOpenEyedEntity, BlendState> blendStates = new WeakHashMap<>();

    public TheOpenEyedRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.4F;
        this.model = new TheOpenEyedModel<>(context.bakeLayer(TheOpenEyedModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            TheOpenEyedEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        int holdAt = entity.getAnimHoldAt();
        int rawTick = entity.getAnimTick();
        boolean holding = (holdAt >= 0 && rawTick >= holdAt);
        float animTicksF = holding ? (float) holdAt : (rawTick + partialTicks);
        float currSec = animTicksF / 20.0F;
        String currClip = entity.getCurrentAnimName();
        int currEpoch = entity.getAnimEpoch();

        BlendState bs = blendStates.computeIfAbsent(entity, k -> new BlendState());
        if (bs.lastClip != null && (!bs.lastClip.equals(currClip) || bs.lastEpoch != currEpoch)) {
            bs.prevClip = bs.lastClip;
            bs.prevTimeSec = bs.lastTimeSec;
        }
        bs.lastClip = currClip;
        bs.lastEpoch = currEpoch;
        bs.lastTimeSec = currSec;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-BOSS_RENDER_SCALE, -BOSS_RENDER_SCALE, BOSS_RENDER_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        if (bs.prevClip != null && animTicksF < BLEND_DURATION_TICKS) {
            float alpha = Mth.clamp(animTicksF / BLEND_DURATION_TICKS, 0.0F, 1.0F);
            TheOpenEyedAnimations.applyBlended(model, bs.prevClip, bs.prevTimeSec, currClip, currSec, alpha);
        } else {
            TheOpenEyedAnimations.apply(model, currClip, currSec);
        }

        if (holding) {
            float liveT = entity.tickCount + partialTicks;
            var bodyBone = model.getBone("body");
            if (bodyBone != null) bodyBone.y += Mth.sin(liveT * 0.16F) * 1.4F;
            var hIn = model.getBone("halo_inner");
            if (hIn != null) hIn.zRot += liveT * 0.24F;
            var hOut = model.getBone("halo_outer");
            if (hOut != null) hOut.zRot -= liveT * 0.16F;
            var cubes = model.getBone("corrupt_cubes_right");
            if (cubes != null) cubes.yRot += liveT * 0.22F;
            var eye = model.getBone("chest_eye");
            if (eye != null) {
                float p = 1.0F + 0.14F * Mth.sin(liveT * 0.35F);
                eye.xScale *= p; eye.yScale *= p; eye.zScale *= p;
            }
        }

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);
        poseStack.popPose();

        // Render Overseer / Delirium glowing ritual halo ring around the Watcher
        int st = entity.getBossState();
        if (st == TheOpenEyedEntity.STATE_OVERSEER || st == TheOpenEyedEntity.STATE_DELIRIUM) {
            renderRitualRing(entity, st, partialTicks, poseStack, bufferSource);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderRitualRing(TheOpenEyedEntity entity, int state, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 2.8D, 0.0D);
        float t = entity.tickCount + partialTicks;
        poseStack.mulPose(Axis.YP.rotationDegrees(t * (state == TheOpenEyedEntity.STATE_DELIRIUM ? 6.0F : 3.5F)));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();
        float rIn = (state == TheOpenEyedEntity.STATE_DELIRIUM) ? 2.4F : 1.9F;
        float rOut = rIn + 0.16F;
        int r = (state == TheOpenEyedEntity.STATE_DELIRIUM) ? 255 : 220;
        int g = (state == TheOpenEyedEntity.STATE_DELIRIUM) ? 140 : 185;
        int b = (state == TheOpenEyedEntity.STATE_DELIRIUM) ? 95  : 255;
        int segs = 24;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            vc.addVertex(m, Mth.cos(a0) * rIn, 0, Mth.sin(a0) * rIn).setColor(r, g, b, 185);
            vc.addVertex(m, Mth.cos(a0) * rOut, 0, Mth.sin(a0) * rOut).setColor(r, g, b, 185);
            vc.addVertex(m, Mth.cos(a1) * rOut, 0, Mth.sin(a1) * rOut).setColor(r, g, b, 185);
            vc.addVertex(m, Mth.cos(a1) * rIn, 0, Mth.sin(a1) * rIn).setColor(r, g, b, 185);
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TheOpenEyedEntity entity) {
        return TEXTURE;
    }
}
