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
import org.joml.Vector3f;

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
    private static final float BLEND_DURATION_SEC = 0.26F;
    private static final float BREATH_BLEND_DURATION_SEC = 0.45F;

    private static final class BlendState {
        String prevClip;
        float prevTimeSec;
        String curClip;
        float blendStartSec;
        float blendDurationSec = BLEND_DURATION_SEC;
        float lastClientSec;
        float lastAnimSec;
        float walkAnimSec;
        float breathAimBlend;
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
                || act == GoldflameQucusaurMountEntity.ACTION_DASH
                || act == GoldflameQucusaurMountEntity.ACTION_COMBO
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

        float dt = Math.max(0.0F, Math.min(0.15F, clientSec - st.lastClientSec));
        st.lastClientSec = clientSec;
        if ("walk".equals(targetClipName)) {
            double hSpeed = Math.hypot(entity.getX() - entity.xo, entity.getZ() - entity.zo);
            float walkRate = Mth.clamp((float) (hSpeed * 8.2D), 0.55F, 1.85F);
            st.walkAnimSec += dt * walkRate;
            curAnimSec = st.walkAnimSec;
        }
        if (!targetClipName.equals(st.curClip)) {
            boolean breathTransition = "breath_sweep".equals(targetClipName) || "breath_sweep".equals(st.curClip)
                    || "mount_breath_air".equals(targetClipName) || "mount_breath_air".equals(st.curClip);
            st.prevClip = st.curClip;
            st.prevTimeSec = st.lastAnimSec;
            st.curClip = targetClipName;
            st.blendStartSec = clientSec;
            st.blendDurationSec = breathTransition ? BREATH_BLEND_DURATION_SEC : BLEND_DURATION_SEC;
        } else if (st.prevClip != null) {
            st.prevTimeSec += dt;
        }
        st.lastAnimSec = curAnimSec;

        float rawBlend = Mth.clamp((clientSec - st.blendStartSec) / Math.max(0.05F, st.blendDurationSec), 0.0F, 1.0F);
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
            float targetBreathAim = breathing ? 1.0F : 0.0F;
            float aimStep = Math.min(1.0F, dt / 0.28F);
            st.breathAimBlend = Mth.lerp(aimStep, st.breathAimBlend, targetBreathAim);
            float smoothBreath = st.breathAimBlend * st.breathAimBlend * (3.0F - 2.0F * st.breathAimBlend);
            String[] neckChain = {"neck_1", "neck_2", "neck_3", "neck_4", "head"};
            float[] pitchWeights = {0.16F, 0.20F, 0.22F, 0.20F, 0.22F};
            float[] yawWeights   = {0.15F, 0.18F, 0.20F, 0.22F, 0.25F};
            for (int i = 0; i < neckChain.length; i++) {
                var bone = bodyModel.getBone(neckChain[i]);
                var init = bodyModel.getInitialPose(neckChain[i]);
                if (bone != null && init != null) {
                    float addPitch = bone.xRot + pitchRad * pitchWeights[i];
                    float addYaw   = bone.yRot + yawRad * yawWeights[i];
                    float aimPitch = init.xRot + pitchRad * pitchWeights[i];
                    float aimYaw   = init.yRot + yawRad * yawWeights[i];
                    bone.xRot = Mth.lerp(smoothBreath, addPitch, aimPitch);
                    bone.yRot = Mth.lerp(smoothBreath, addYaw, aimYaw);
                }
            }
            if (smoothBreath > 0.01F) {
                var jaw = bodyModel.getBone("jaw");
                var jawInit = bodyModel.getInitialPose("jaw");
                if (jaw != null && jawInit != null) {
                    jaw.xRot = Mth.lerp(smoothBreath, jaw.xRot, jawInit.xRot + 38.0F * Mth.DEG_TO_RAD);
                }
            }
        }
        emissiveModel.copyPoseFrom(bodyModel);
        syncRiderToAnimatedSaddle(entity, bodyModel);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        if (entity.isFlying()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(smoothBank));
            poseStack.mulPose(Axis.XP.rotationDegrees(-smoothPitch));
        }
        poseStack.scale(-MOUNT_SCALE, -MOUNT_SCALE, MOUNT_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer bodyVc = buffer.getBuffer(RenderType.entityCutout(GoldflameQucusaurRenderer.TEXTURE));
        bodyModel.renderToBuffer(poseStack, bodyVc, packedLight, overlay, 0xFFFFFFFF);

        VertexConsumer emVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(GoldflameQucusaurRenderer.EMISSIVE_TEXTURE));
        emissiveModel.renderToBuffer(poseStack, emVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xD8FFFFFF);
        poseStack.popPose();


        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /**
     * Evaluates the exact live forward-kinematics transform of the {@code root -> pelvis -> torso -> saddle}
     * bone chain and pushes the animated displacement and tilt to the mount entity so the rider stays
     * locked 1:1 to the saddle cushion on every wingbeat, hover bob, dive, and walk stride.
     */
    private static void syncRiderToAnimatedSaddle(GoldflameQucusaurMountEntity entity, GoldflameQucusaurModel<GoldflameQucusaurMountEntity> model) {
        var root = model.getBone("root");
        var pelvis = model.getBone("pelvis");
        var torso = model.getBone("torso");
        var saddle = model.getBone("saddle");
        if (root == null || pelvis == null || torso == null || saddle == null) return;

        PoseStack fk = new PoseStack();
        fk.scale(-MOUNT_SCALE, -MOUNT_SCALE, MOUNT_SCALE);
        fk.translate(0.0D, -1.501D, 0.0D);
        root.translateAndRotate(fk);
        pelvis.translateAndRotate(fk);
        torso.translateAndRotate(fk);
        saddle.translateAndRotate(fk);

        var mat = fk.last().pose();
        // Exact saddle cushion top center in saddle-local blocks (-3.8px Y, +0.3px Z)
        Vector3f seatPos = mat.transformPosition(new Vector3f(0.0F, -3.8F / 16.0F, 0.3F / 16.0F));
        Vector3f seatUp  = mat.transformPosition(new Vector3f(0.0F, -19.8F / 16.0F, 0.3F / 16.0F));
        Vector3f upDir = seatUp.sub(seatPos).normalize();

        // Bind-pose cushion reference: (0.0, 1.562, -0.124) in dragon-local (180 - yBodyRot) space
        float dx = seatPos.x;
        float dy = seatPos.y - 1.562F;
        float dz = seatPos.z - (-0.124F);
        float pitchDeg = (float) Math.toDegrees(Math.atan2(upDir.z, Math.max(0.05F, upDir.y)));
        float rollDeg  = (float) Math.toDegrees(Math.atan2(-upDir.x, Math.max(0.05F, upDir.y)));

        entity.setClientSaddleSync(
                Mth.clamp(dx, -0.8F, 0.8F),
                Mth.clamp(dy, -0.9F, 0.9F),
                Mth.clamp(dz, -0.9F, 0.9F),
                Mth.clamp(pitchDeg, -38.0F, 38.0F),
                Mth.clamp(rollDeg, -30.0F, 30.0F)
        );
    }
}
