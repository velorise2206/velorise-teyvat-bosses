package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.client.model.BedrockAnimationLoader;
import com.velorise.teyvatbosses.client.model.GoldflameQucusaurModel;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Renderer for {@link GoldflameQucusaurMountEntity} sharing the unified {@link GoldflameQucusaurModel}
 * at {@code 0.62x} scale with:
 * <ul>
 *   <li>Smooth 0.24s smoothstep crossfade blending between all locomotion and action clips (zero frame snaps).</li>
 *   <li>Additive {@code mount_bank_left} / {@code mount_bank_right} aerodynamic banking flex.</li>
 *   <li>Additive {@code mount_fireball_spit} neck/jaw overlay so wings keep flapping while firing.</li>
 *   <li>Live rider crosshair neck/head tracking and smooth flight pitch &amp; bank roll.</li>
 * </ul>
 */
public class GoldflameQucusaurMountRenderer extends EntityRenderer<GoldflameQucusaurMountEntity> {
    private static final float MOUNT_SCALE = 0.62F;
    private static final float BLEND_DURATION_SEC = 0.24F;

    private static final class BlendState {
        String prevClip;
        float prevTimeSec;
        String curClip;
        float blendStartSec;
        float lastClientSec;
    }

    private final Map<GoldflameQucusaurMountEntity, BlendState> blendStates = new WeakHashMap<>();
    private final GoldflameQucusaurModel<GoldflameQucusaurMountEntity> bodyModel;
    private final GoldflameQucusaurModel<GoldflameQucusaurMountEntity> emissiveModel;

    public GoldflameQucusaurMountRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.bodyModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION));
        this.emissiveModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 1.45F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurMountEntity entity) {
        return GoldflameQucusaurRenderer.TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurMountEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;
        int act = entity.getActionState();
        float actionSec = (entity.getActionTicks() + partialTicks) / 20.0F;
        boolean usesActionClock = (act == GoldflameQucusaurMountEntity.ACTION_TAKEOFF
                || act == GoldflameQucusaurMountEntity.ACTION_DIVE
                || act == GoldflameQucusaurMountEntity.ACTION_BREATH);
        float curAnimSec = usesActionClock ? actionSec : clientSec;

        String targetClipName = entity.getActiveAnimationClip();
        BlendState st = blendStates.computeIfAbsent(entity, e -> {
            BlendState b = new BlendState();
            b.curClip = targetClipName;
            b.blendStartSec = clientSec - BLEND_DURATION_SEC;
            b.lastClientSec = clientSec;
            return b;
        });

        float dt = Math.max(0.0F, clientSec - st.lastClientSec);
        st.lastClientSec = clientSec;
        if (!targetClipName.equals(st.curClip)) {
            st.prevClip = st.curClip;
            st.prevTimeSec = curAnimSec;
            st.curClip = targetClipName;
            st.blendStartSec = clientSec;
        } else if (st.prevClip != null) {
            st.prevTimeSec += dt;
        }

        float rawBlend = Mth.clamp((clientSec - st.blendStartSec) / BLEND_DURATION_SEC, 0.0F, 1.0F);
        float blend = rawBlend * rawBlend * (3.0F - 2.0F * rawBlend); // smoothstep

        bodyModel.resetPose(true);
        BedrockAnimationLoader.Clip curClip = BedrockAnimationLoader.getClip("goldflame_qucusaur", st.curClip);
        if (st.prevClip != null && blend < 0.999F) {
            BedrockAnimationLoader.Clip prevClip = BedrockAnimationLoader.getClip("goldflame_qucusaur", st.prevClip);
            if (prevClip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, prevClip, st.prevTimeSec, 1.0F - blend, false);
            }
            if (curClip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, curClip, curAnimSec, blend, false);
            }
        } else {
            st.prevClip = null;
            if (curClip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, curClip, curAnimSec, 1.0F, false);
            }
        }

        float smoothBank = Mth.lerp(partialTicks, entity.getPrevBankRoll(), entity.getBankRoll());
        float smoothPitch = Mth.lerp(partialTicks, entity.getPrevFlightPitch(), entity.getFlightPitch());

        // Additive aerodynamic wing/tail bank flex (mount_bank_left / mount_bank_right)
        if (entity.isFlying() && Math.abs(smoothBank) > 0.5F) {
            float bankWeight = Mth.clamp(Math.abs(smoothBank) / 24.0F, 0.0F, 1.0F) * 0.85F;
            String bankClipName = smoothBank > 0.0F ? "mount_bank_left" : "mount_bank_right";
            BedrockAnimationLoader.Clip bankClip = BedrockAnimationLoader.getClip("goldflame_qucusaur", bankClipName);
            if (bankClip != null) {
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, bankClip, 0.35F, bankWeight, false);
            }
        }

        // Additive fireball spit overlay on neck/head/jaw so wings keep flapping seamlessly
        if (act == GoldflameQucusaurMountEntity.ACTION_SPIT) {
            BedrockAnimationLoader.Clip spitClip = BedrockAnimationLoader.getClip("goldflame_qucusaur", "mount_fireball_spit");
            if (spitClip != null) {
                float spitFade = actionSec < 0.10F ? (actionSec / 0.10F)
                        : (actionSec > 0.45F ? Mth.clamp((0.60F - actionSec) / 0.15F, 0.0F, 1.0F) : 1.0F);
                BedrockAnimationLoader.applyWeighted(bodyModel::getBone, spitClip, actionSec, spitFade, false);
            }
        }

        // Dynamically aim dragon neck & head along rider's live crosshair yaw/pitch
        LivingEntity ctrlRider = entity.getControllingPassenger();
        if (ctrlRider != null) {
            float riderYaw = Mth.rotLerp(partialTicks, ctrlRider.yRotO, ctrlRider.getYRot());
            float riderPitch = Mth.clamp(Mth.lerp(partialTicks, ctrlRider.xRotO, ctrlRider.getXRot()), -55.0F, 55.0F);
            float relYaw = Mth.clamp(Mth.wrapDegrees(riderYaw - bodyYaw), -55.0F, 55.0F);
            float pitchRad = riderPitch * Mth.DEG_TO_RAD;
            float yawRad = relYaw * Mth.DEG_TO_RAD;
            boolean breathing = entity.isBreathing();
            String[] neckChain = {"neck_1", "neck_2", "neck_3", "neck_4", "head"};
            float[] pitchWeights = {0.16F, 0.20F, 0.22F, 0.20F, 0.22F};
            float[] yawWeights   = {0.15F, 0.18F, 0.20F, 0.22F, 0.25F};
            for (int i = 0; i < neckChain.length; i++) {
                var bone = bodyModel.getBone(neckChain[i]);
                var init = bodyModel.getInitialPose(neckChain[i]);
                if (bone != null && init != null) {
                    if (breathing) {
                        bone.xRot = init.xRot + pitchRad * pitchWeights[i];
                        bone.yRot = init.yRot + yawRad * yawWeights[i];
                    } else {
                        bone.xRot += pitchRad * pitchWeights[i];
                        bone.yRot += yawRad * yawWeights[i];
                    }
                }
            }
            if (breathing) {
                var jaw = bodyModel.getBone("jaw");
                var jawInit = bodyModel.getInitialPose("jaw");
                if (jaw != null && jawInit != null) {
                    jaw.xRot = jawInit.xRot + 38.0F * Mth.DEG_TO_RAD;
                }
            }
        }
        emissiveModel.copyPoseFrom(bodyModel);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        if (entity.isFlying()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(smoothBank));
            poseStack.mulPose(Axis.XP.rotationDegrees(-smoothPitch));
        }
        poseStack.scale(-MOUNT_SCALE, -MOUNT_SCALE, MOUNT_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer bodyVc = buffer.getBuffer(RenderType.entityCutoutNoCull(GoldflameQucusaurRenderer.TEXTURE));
        bodyModel.renderToBuffer(poseStack, bodyVc, packedLight, overlay, 0xFFFFFFFF);

        VertexConsumer emVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(GoldflameQucusaurRenderer.EMISSIVE_TEXTURE));
        emissiveModel.renderToBuffer(poseStack, emVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xD8FFFFFF);
        poseStack.popPose();

        // Volumetric Goldflame Breath Torrent aimed along rider look direction
        if (entity.isBreathing()) {
            LivingEntity rider = entity.getControllingPassenger();
            float aimYaw = rider != null ? Mth.rotLerp(partialTicks, rider.yRotO, rider.getYRot()) : bodyYaw;
            float aimPitch = rider != null ? Mth.lerp(partialTicks, rider.xRotO, rider.getXRot()) : 0.0F;
            VertexConsumer lvc = buffer.getBuffer(RenderType.lightning());
            GoldflameQucusaurRenderer.renderGoldflameBreathVfx(
                    poseStack, lvc, aimYaw, aimPitch, 1.65D, 1.55D, 16.5F, clientSec, 0.95F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
