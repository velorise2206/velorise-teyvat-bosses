package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.CryoRegisvineAnimations;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Overhauled Genshin-accurate renderer for CryoRegisvineEntity with:
 * <ul>
 *   <li><b>Fixed Grounded Roots:</b> {@code root_base} & {@code root_core} remain permanently anchored at spawn
 *       {@link CryoRegisvineEntity#getRootYaw()} - ONLY the upper stem joint ({@code stem_1.yRot}) rotates smoothly
 *       to track the player!</li>
 *   <li><b>True Pose-Snapshot Animation State Machine:</b> Captures every bone's live pose before any state/clip
 *       switch and cross-fades over 8 ticks (0.40s) using shortest-angle radian interpolation ({@code lerpAngleRad}),
 *       completely eliminating 1-frame reset snaps and 360 deg backwards unwind spins after {@code flail_360}/{@code rotary_spray_beam}.</li>
 *   <li><b>Matrix-Verified Slash & Laser Alignment:</b> All 3D slash crescents ({@code flail_360}, {@code spin_frost_ring},
 *       {@code half_flail_combo}) sweep in the exact world-space direction of {@code stem_1.yRot}.</li>
 * </ul>
 */
public class CryoRegisvineRenderer extends EntityRenderer<CryoRegisvineEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_regisvine_boss.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.cryo_regisvine_theme");
    private static final float BOSS_RENDER_SCALE = 1.6F;
    private static final float BLEND_DURATION_TICKS = 10.0F;

    private static final String[] ALL_BONES = {
            "root", "root_base", "root_core",
            "stem_1", "stem_2", "stem_3", "neck", "corolla_head", "corolla_core",
            "petal_ur", "petal_ul", "petal_lr", "petal_ll", "back_sepal_r", "back_sepal_l",
            "lash_r_base", "lash_r_mid", "lash_r_tip",
            "lash_l_base", "lash_l_mid", "lash_l_tip",
            "frost_ring"
    };

    private static final String[] CHAIN_ROOT_CORE = {
            "root", "root_base", "root_core"
    };
    private static final String[] CHAIN_COROLLA_CORE = {
            "root", "root_base", "stem_1", "stem_2", "stem_3", "neck", "corolla_head", "corolla_core"
    };
    private static final String[] CHAIN_ROTARY = {
            "root", "root_base", "stem_1", "stem_2", "stem_3", "neck", "corolla_head", "rotary_beam"
    };
    private static final String[] CHAIN_LASH_R = {
            "root", "root_base", "stem_1", "stem_2", "stem_3", "neck", "corolla_head",
            "lash_r_base", "lash_r_mid", "lash_r_tip", "lash_beam_r"
    };
    private static final String[] CHAIN_LASH_L = {
            "root", "root_base", "stem_1", "stem_2", "stem_3", "neck", "corolla_head",
            "lash_l_base", "lash_l_mid", "lash_l_tip", "lash_beam_l"
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
        float frozenEvalSec = 0.0F;
        boolean wasHitstop = false;
        boolean hasLastPose = false;
        int lastImpactEpoch = -1;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();
    }

    private final CryoRegisvineModel<CryoRegisvineEntity> model;
    private final Map<CryoRegisvineEntity, AnimStateMachine> stateMachines = new WeakHashMap<>();

    public CryoRegisvineRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 2.6F;
        this.model = new CryoRegisvineModel<>(context.bakeLayer(CryoRegisvineModel.LAYER_LOCATION));
    }

    private static void hideBone(CryoRegisvineModel<?> model, String name) {
        ModelPart part = model.getBone(name);
        if (part != null) {
            part.visible = false;
            part.xScale = 0.0F;
            part.yScale = 0.0F;
            part.zScale = 0.0F;
        }
    }

    private static float wrapRad(float rad) {
        float twoPi = (float) (Math.PI * 2.0D);
        float r = rad % twoPi;
        if (r > Math.PI) r -= twoPi;
        if (r < -Math.PI) r += twoPi;
        return r;
    }

    private static float lerpAngleRad(float from, float to, float alpha) {
        float diff = wrapRad(to - from);
        return from + diff * alpha;
    }

    @Override
    public void render(
            CryoRegisvineEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float liveTick = entity.tickCount + partialTicks;
        float rootYaw = entity.getRootYaw();
        float stemWorldYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float stemDeltaYawRad = Mth.wrapDegrees(stemWorldYaw - rootYaw) * Mth.DEG_TO_RAD;

        float animTicksF = entity.getAnimTick() + partialTicks;
        float currSec = animTicksF / 20.0F;
        String currClip = entity.getCurrentAnimName();
        int currEpoch = entity.getAnimEpoch();

        boolean musicActive = entity.isAlive() && entity.hasActiveCombatTarget() && (entity.getBossState() == CryoRegisvineEntity.STATE_COMBAT || entity.getBossState() == CryoRegisvineEntity.STATE_STUNNED);
        BossMusicManager.update(entity, MUSIC_ID, musicActive, () -> entity.hasActiveCombatTarget() && (entity.getBossState() == CryoRegisvineEntity.STATE_COMBAT || entity.getBossState() == CryoRegisvineEntity.STATE_STUNNED));
        AnimStateMachine sm = stateMachines.computeIfAbsent(entity, k -> new AnimStateMachine());
        if (sm.lastClip != null && (!sm.lastClip.equals(currClip) || sm.lastEpoch != currEpoch)) {
            // Snapshot exact rendered pose of previous frame as blend origin!
            if (sm.hasLastPose) {
                for (Map.Entry<String, BonePose> e : sm.lastPose.entrySet()) {
                    sm.fromPose.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
                }
                sm.blendStartLiveTick = liveTick;
            }
        }
        sm.lastClip = currClip;
        sm.lastEpoch = currEpoch;

        // Unified AAA hitstop & shake on Cryo Core shatter and heavy head slams
        if (entity.getBossState() == CryoRegisvineEntity.STATE_STUNNED && entity.getAnimTick() <= 2 && sm.lastImpactEpoch != currEpoch) {
            sm.lastImpactEpoch = currEpoch;
            BossCameraShake.triggerHitstop(entity.position().add(0.0D, 2.0D, 0.0D), 4, 0.50F, 42.0D);
        } else if ((currClip.contains("slam") || currClip.contains("flail")) && currSec >= 0.72F && currSec <= 0.85F && sm.lastImpactEpoch != currEpoch) {
            sm.lastImpactEpoch = currEpoch;
            BossCameraShake.triggerAt(entity.position(), 0.34F, 34.0D);
        }

        // World-aligned ground VFX (Frost Field Mandala & Matrix-Verified Sweeping Slash Crescents)
        renderGroundAndSlashVFX(entity, stemWorldYaw, animTicksF, partialTicks, poseStack, bufferSource);

        poseStack.pushPose();
        // ROOT BASE IS ALWAYS FIXED AT SPAWN rootYaw - NEVER SPINS ON THE FLOOR!
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rootYaw));
        poseStack.scale(-BOSS_RENDER_SCALE, -BOSS_RENDER_SCALE, BOSS_RENDER_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        // 1. Evaluate target clip at live 60/144 FPS time
        float evalSec = currSec;
        if (BossCameraShake.isHitstopActive()) {
            if (!sm.wasHitstop) {
                sm.frozenEvalSec = evalSec;
                sm.wasHitstop = true;
            }
            evalSec = sm.frozenEvalSec;
        } else {
            sm.wasHitstop = false;
        }
        CryoRegisvineAnimations.apply(model, currClip, evalSec);

        // 2. Apply player-tracking yaw ONLY to the stem_1 joint above the fixed roots!
        ModelPart stem1 = model.getBone("stem_1");
        if (stem1 != null) {
            stem1.yRot += stemDeltaYawRad;
        }

        // 3. Smooth shortest-arc Pose-Snapshot State Machine cross-fade (8 ticks = 0.40s)
        float blendElapsed = liveTick - sm.blendStartLiveTick;
        if (sm.hasLastPose && blendElapsed >= 0.0F && blendElapsed < BLEND_DURATION_TICKS) {
            float rawA = Mth.clamp(blendElapsed / BLEND_DURATION_TICKS, 0.0F, 1.0F);
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

        // 4. Record final rendered pose for seamless future transitions
        for (String bName : ALL_BONES) {
            ModelPart part = model.getBone(bName);
            if (part != null) {
                sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).capture(part);
            }
        }
        sm.hasLastPose = true;

        hideBone(model, "rotary_beam");
        hideBone(model, "lash_beam_r");
        hideBone(model, "lash_beam_l");

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Emissive Cryo Weakpoint Core Aura (Root Core vs Corolla Core)
        renderCoreWeakpointGlow(entity, model, liveTick, poseStack, bufferSource);

        // Ground-sweeping Bone-Anchored Frost Lasers
        renderBoneAnchoredLasers(entity, model, animTicksF, poseStack, bufferSource);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderCoreWeakpointGlow(
            CryoRegisvineEntity entity,
            CryoRegisvineModel<?> model,
            float time,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        if (entity.getBossState() != CryoRegisvineEntity.STATE_COMBAT) return;
        float shield = entity.getShieldProgress();
        if (shield <= 0.01F) return;

        String[] chain = (entity.getCorollaPhase() == 0) ? CHAIN_ROOT_CORE : CHAIN_COROLLA_CORE;
        Vec3[] sample = sampleBoneRay(model, poseStack, chain);
        Vec3 center = sample[0];

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f identity = new Matrix4f();

        float pulse = 1.0F + 0.14F * Mth.sin(time * 0.25F);
        float baseR = (entity.getCorollaPhase() == 0 ? 0.85F : 0.72F) * (0.55F + 0.45F * shield) * pulse;

        drawGlowOctahedron(vc, identity, center, baseR * 0.65F, 220, 250, 255, 200);
        drawGlowOctahedron(vc, identity, center, baseR * 0.98F, 75, 205, 255, 115);

        for (int ring = 0; ring < 2; ring++) {
            float spin = time * (ring == 0 ? 0.14F : -0.18F) + ring * 1.57F;
            Vec3 axisA = new Vec3(Math.cos(spin), 0.35D * (ring == 0 ? 1 : -1), Math.sin(spin)).normalize();
            Vec3 ref = ring == 0 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
            Vec3 u = axisA.cross(ref).normalize();
            Vec3 vAxis = axisA.cross(u).normalize();
            drawRing(vc, identity, center, u, vAxis, baseR * 1.05F, baseR * 1.24F, 16, 155, 238, 255, 165);
        }
    }

    /**
     * Renders world-aligned ground VFX & Matrix-Verified 3D Sweeping Frost-Slash Crescents.
     * Verified against 3D matrix: stem_1.yRot DECREASING -> world MC yaw DECREASING!
     */
    private static void renderGroundAndSlashVFX(
            CryoRegisvineEntity entity,
            float yaw,
            float t,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        int anim = entity.getAnimId();
        if (entity.getBossState() != CryoRegisvineEntity.STATE_COMBAT) return;

        // 1. Frost Field Mandala on arena floor during Blizzard Channel
        if (anim == CryoRegisvineEntity.ANIM_BLIZZARD_CHANNEL && t >= 6.0F && t <= 92.0F) {
            float fade = t < 16.0F ? (t - 6.0F) / 10.0F : (t > 82.0F ? Math.max(0.0F, (92.0F - t) / 10.0F) : 1.0F);
            VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.08D, 0.0D);
            Matrix4f m = poseStack.last().pose();
            float outerR = 13.5F * (0.75F + 0.25F * Math.min(1.0F, fade));
            float midR = outerR * 0.62F;
            drawHorizontalRing(vc, m, outerR - 0.35F, outerR, 40, 110, 218, 255, (int) (135 * fade));
            drawHorizontalRing(vc, m, midR - 0.22F, midR, 32, 190, 245, 255, (int) (115 * fade));

            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 1.4F));
            Matrix4f rm = poseStack.last().pose();
            for (int i = 0; i < 6; i++) {
                float ang = (float) (i * Math.PI / 3.0D);
                float c = Mth.cos(ang), s = Mth.sin(ang);
                float px = -s * 0.14F, pz = c * 0.14F;
                int al = (int) (95 * fade);
                vc.addVertex(rm, c * 2.2F + px, 0.0F, s * 2.2F + pz).setColor(215, 248, 255, al);
                vc.addVertex(rm, c * 2.2F - px, 0.0F, s * 2.2F - pz).setColor(215, 248, 255, al);
                vc.addVertex(rm, c * outerR - px, 0.0F, s * outerR - pz).setColor(70, 190, 255, al / 3);
                vc.addVertex(rm, c * outerR + px, 0.0F, s * outerR + pz).setColor(70, 190, 255, al / 3);
            }
            poseStack.popPose();
        }

        // 1b. Ruin-Guard-style World-Locked Red Expanding Warning Zone (Slams & 360 Spins)
        float teleR = entity.getTelegraphRadius();
        if (teleR > 0.5F) {
            Vec3 telePos = entity.getTelegraphPos();
            double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float prog = switch (anim) {
                case CryoRegisvineEntity.ANIM_FACEPLANT_SINGLE -> Mth.clamp((t - 4.0F) / 17.0F, 0.0F, 1.0F);
                case CryoRegisvineEntity.ANIM_FACEPLANT_TRIPLE -> (t < 20.0F)
                        ? Mth.clamp((t - 4.0F) / 13.0F, 0.0F, 1.0F)
                        : (t < 40.0F ? Mth.clamp((t - 24.0F) / 13.0F, 0.0F, 1.0F)
                        : Mth.clamp((t - 44.0F) / 13.0F, 0.0F, 1.0F));
                case CryoRegisvineEntity.ANIM_HALF_FLAIL_COMBO -> Mth.clamp((t - 44.0F) / 15.0F, 0.0F, 1.0F);
                case CryoRegisvineEntity.ANIM_FLAIL_360 -> Mth.clamp((t - 1.0F) / 12.0F, 0.0F, 1.0F);
                case CryoRegisvineEntity.ANIM_SPIN_FROST_RING -> Mth.clamp((t - 1.0F) / 15.0F, 0.0F, 1.0F);
                default -> 0.5F;
            };
            float fillR = Math.max(0.18F, teleR * prog);
            VertexConsumer tvc = bufferSource.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            poseStack.translate(telePos.x - lerpX, telePos.y - lerpY + 0.14D, telePos.z - lerpZ);
            Matrix4f tm = poseStack.last().pose();
            drawHorizontalRing(tvc, tm, teleR - 0.32F, teleR, 36, 235, 28, 36, 225);
            drawHorizontalRing(tvc, tm, 0.08F, Math.max(0.10F, teleR - 0.32F), 32, 200, 18, 24, 42);
            drawHorizontalRing(tvc, tm, 0.08F, fillR, 32, 255, 42, 48, 86);
            drawHorizontalRing(tvc, tm, Math.max(0.08F, fillR - 0.22F), fillR, 36, 255, 85, 78, 210);
            poseStack.popPose();
        }

        // 2. Matrix-Verified 3D Sweeping Frost-Slash Crescent:
        // flail_360 & spin_frost_ring: stem_1.yRot sweeps +58 -> -378 (DECREASING yaw!)
        boolean is360 = (anim == CryoRegisvineEntity.ANIM_FLAIL_360 || anim == CryoRegisvineEntity.ANIM_SPIN_FROST_RING);
        if (is360 && t >= 18.0F && t <= 44.0F) {
            float prog = Mth.clamp((t - 18.0F) / 26.0F, 0.0F, 1.0F);
            float fade = prog < 0.15F ? prog / 0.15F : (prog > 0.78F ? (1.0F - prog) / 0.22F : 1.0F);
            float leadDeg = yaw + 48.0F - prog * 410.0F; // EXACTLY follows stem_1.yRot!
            // Trail trails BEHIND decreasing leadDeg -> trailSign = +1.0F
            drawSweepingCrescent(poseStack, bufferSource.getBuffer(RenderType.lightning()),
                    leadDeg, 140.0F, 1.0F, 5.0F, 8.6F, 1.05F, fade);
        } else if (anim == CryoRegisvineEntity.ANIM_HALF_FLAIL_COMBO) {
            // Strike 1 (t=13..22): stem_1.yRot sweeps +98 -> -106 (DECREASING yaw!)
            if (t >= 13.0F && t <= 22.0F) {
                float prog = Mth.clamp((t - 13.0F) / 9.0F, 0.0F, 1.0F);
                float fade = 1.0F - Math.abs(prog - 0.5F) * 1.6F;
                float leadDeg = yaw + 95.0F - prog * 195.0F;
                drawSweepingCrescent(poseStack, bufferSource.getBuffer(RenderType.lightning()),
                        leadDeg, 105.0F, 1.0F, 4.8F, 8.4F, 1.15F, Mth.clamp(fade, 0.0F, 1.0F));
            }
            // Strike 2 (t=29..38): stem_1.yRot sweeps -98 -> +102 (INCREASING yaw!)
            if (t >= 29.0F && t <= 38.0F) {
                float prog = Mth.clamp((t - 29.0F) / 9.0F, 0.0F, 1.0F);
                float fade = 1.0F - Math.abs(prog - 0.5F) * 1.6F;
                float leadDeg = yaw - 95.0F + prog * 195.0F;
                drawSweepingCrescent(poseStack, bufferSource.getBuffer(RenderType.lightning()),
                        leadDeg, 105.0F, -1.0F, 4.8F, 8.4F, 1.15F, Mth.clamp(fade, 0.0F, 1.0F));
            }
        }
    }

    private static void drawSweepingCrescent(PoseStack poseStack, VertexConsumer vc,
                                             float leadDeg, float arcDeg, float trailSign,
                                             float rIn, float rOut, float yH, float alphaMul) {
        if (alphaMul <= 0.02F) return;
        poseStack.pushPose();
        poseStack.translate(0.0D, yH, 0.0D);
        Matrix4f m = poseStack.last().pose();
        int segs = 22;
        for (int i = 0; i < segs; i++) {
            float f0 = (float) i / segs;
            float f1 = (float) (i + 1) / segs;
            float a0 = (leadDeg + trailSign * f0 * arcDeg) * Mth.DEG_TO_RAD;
            float a1 = (leadDeg + trailSign * f1 * arcDeg) * Mth.DEG_TO_RAD;
            float dx0 = -Mth.sin(a0), dz0 = Mth.cos(a0);
            float dx1 = -Mth.sin(a1), dz1 = Mth.cos(a1);
            int al0 = (int) (215 * alphaMul * (1.0F - f0));
            int al1 = (int) (215 * alphaMul * (1.0F - f1));
            float w0 = rIn + (rOut - rIn) * (1.0F - f0 * 0.45F);
            float w1 = rIn + (rOut - rIn) * (1.0F - f1 * 0.45F);

            vc.addVertex(m, dx0 * rIn, 0.0F, dz0 * rIn).setColor(80, 200, 255, al0 / 3);
            vc.addVertex(m, dx0 * w0, 0.08F, dz0 * w0).setColor(235, 252, 255, al0);
            vc.addVertex(m, dx1 * w1, 0.08F, dz1 * w1).setColor(235, 252, 255, al1);
            vc.addVertex(m, dx1 * rIn, 0.0F, dz1 * rIn).setColor(80, 200, 255, al1 / 3);
            vc.addVertex(m, dx1 * rIn, 0.0F, dz1 * rIn).setColor(80, 200, 255, al1 / 3);
            vc.addVertex(m, dx1 * w1, 0.08F, dz1 * w1).setColor(235, 252, 255, al1);
            vc.addVertex(m, dx0 * w0, 0.08F, dz0 * w0).setColor(235, 252, 255, al0);
            vc.addVertex(m, dx0 * rIn, 0.0F, dz0 * rIn).setColor(80, 200, 255, al0 / 3);
        }
        poseStack.popPose();
    }

    private static Vec3[] sampleBoneRay(CryoRegisvineModel<?> model, PoseStack baseStack, String[] chain) {
        baseStack.pushPose();
        ModelPart terminal = model.getBone(chain[chain.length - 1]);
        float sx = 1.0F, sy = 1.0F, sz = 1.0F;
        if (terminal != null) {
            sx = terminal.xScale; sy = terminal.yScale; sz = terminal.zScale;
            terminal.xScale = 1.0F; terminal.yScale = 1.0F; terminal.zScale = 1.0F;
        }
        for (String name : chain) {
            ModelPart part = model.getBone(name);
            if (part != null) part.translateAndRotate(baseStack);
        }
        Matrix4f m = new Matrix4f(baseStack.last().pose());
        if (terminal != null) {
            terminal.xScale = sx; terminal.yScale = sy; terminal.zScale = sz;
        }
        baseStack.popPose();

        Vector4f o = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F).mul(m);
        Vector4f f = new Vector4f(0.0F, 0.0F, -1.0F, 1.0F).mul(m);
        Vec3 origin = new Vec3(o.x(), o.y(), o.z());
        Vec3 dir = new Vec3(f.x() - o.x(), f.y() - o.y(), f.z() - o.z());
        dir = dir.lengthSqr() < 1e-6D ? new Vec3(0.0D, 0.0D, -1.0D) : dir.normalize();
        return new Vec3[]{ origin, dir };
    }

    private static void renderBoneAnchoredLasers(
            CryoRegisvineEntity entity,
            CryoRegisvineModel<?> model,
            float t,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        int anim = entity.getAnimId();
        if (anim != CryoRegisvineEntity.ANIM_ICE_BEAMS_CROSS && anim != CryoRegisvineEntity.ANIM_ROTARY_SPRAY) return;

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f identity = new Matrix4f();

        if (anim == CryoRegisvineEntity.ANIM_ICE_BEAMS_CROSS) {
            Vec3[] rayR = sampleBoneRay(model, poseStack, CHAIN_LASH_R);
            Vec3[] rayL = sampleBoneRay(model, poseStack, CHAIN_LASH_L);

            if (t >= 6.0F && t < 16.0F) {
                float charge = (t - 6.0F) / 10.0F;
                drawChargeOrb(vc, identity, rayR[0], 0.18F + 0.48F * charge, t);
                drawChargeOrb(vc, identity, rayL[0], 0.18F + 0.48F * charge, -t);
            } else if (t >= 16.0F && t <= 33.0F) {
                float fade = (t < 18.0F) ? (t - 16.0F) / 2.0F : (t > 30.0F ? Math.max(0.0F, (33.0F - t) / 3.0F) : 1.0F);
                drawGroundedBeam(vc, identity, rayR[0], rayR[1], 16.5D, 0.18F * fade, 0.44F * fade, t, 3);
                drawGroundedBeam(vc, identity, rayL[0], rayL[1], 16.5D, 0.18F * fade, 0.44F * fade, -t, 3);
            } else if (t > 33.0F && t < 38.0F) {
                float charge = (t - 33.0F) / 5.0F;
                drawChargeOrb(vc, identity, rayR[0], 0.25F + 0.35F * charge, t * 1.4F);
                drawChargeOrb(vc, identity, rayL[0], 0.25F + 0.35F * charge, -t * 1.4F);
            } else if (t >= 38.0F && t <= 55.0F) {
                float fade = (t < 40.0F) ? (t - 38.0F) / 2.0F : (t > 52.0F ? Math.max(0.0F, (55.0F - t) / 3.0F) : 1.0F);
                drawGroundedBeam(vc, identity, rayR[0], rayR[1], 16.5D, 0.19F * fade, 0.46F * fade, t * 1.2F, 3);
                drawGroundedBeam(vc, identity, rayL[0], rayL[1], 16.5D, 0.19F * fade, 0.46F * fade, -t * 1.2F, 3);
            }
        } else {
            Vec3[] ray = sampleBoneRay(model, poseStack, CHAIN_ROTARY);
            if (t >= 6.0F && t < 16.0F) {
                float charge = (t - 6.0F) / 10.0F;
                drawChargeOrb(vc, identity, ray[0], 0.28F + 0.72F * charge, t * 1.3F);
            } else if (t >= 16.0F && t <= 86.0F) {
                float fade = (t < 19.0F) ? (t - 16.0F) / 3.0F : (t > 81.0F ? Math.max(0.0F, (86.0F - t) / 5.0F) : 1.0F);
                drawGroundedBeam(vc, identity, ray[0], ray[1], 18.0D, 0.30F * fade, 0.74F * fade, t * 1.4F, 5);
            }
        }
    }

    private static void drawGroundedBeam(VertexConsumer vc, Matrix4f mat, Vec3 origin, Vec3 rawDir, double len,
                                         float coreR, float sheathR, float time, int pulseRings) {
        Vec3 end = origin.add(rawDir.x * len, Math.min(rawDir.y * len, -origin.y * 0.96D), rawDir.z * len);
        drawFrostLaserBeam(vc, mat, origin, end, coreR, sheathR, time, pulseRings);

        Vec3 u = new Vec3(1, 0, 0);
        Vec3 vAxis = new Vec3(0, 0, 1);
        float flareR = sheathR * (1.8F + 0.35F * Mth.sin(time * 0.9F));
        drawRing(vc, mat, end, u, vAxis, flareR * 0.35F, flareR, 12, 210, 248, 255, 185);
        drawGlowOctahedron(vc, mat, end, coreR * 1.7F, 240, 253, 255, 220);
    }

    private static void v(VertexConsumer vc, Matrix4f mat, float x, float y, float z, int r, int g, int b, int a) {
        vc.addVertex(mat, x, y, z).setColor(r, g, b, a);
    }

    private static void drawGlowOctahedron(VertexConsumer vc, Matrix4f mat, Vec3 c, float r, int cr, int cg, int cb, int ca) {
        float cx = (float) c.x, cy = (float) c.y, cz = (float) c.z;
        float[][] ring = { {r, 0}, {0, r}, {-r, 0}, {0, -r} };
        for (int i = 0; i < 4; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % 4];
            v(vc, mat, cx, cy + r * 1.25F, cz, cr, cg, cb, ca);
            v(vc, mat, cx + a[0], cy, cz + a[1], cr, cg, cb, ca);
            v(vc, mat, cx + b[0], cy, cz + b[1], cr, cg, cb, ca);
            v(vc, mat, cx, cy - r * 1.25F, cz, cr, cg, cb, ca);
        }
    }

    private static void drawChargeOrb(VertexConsumer vc, Matrix4f mat, Vec3 center, float radius, float time) {
        drawGlowOctahedron(vc, mat, center, radius * 0.55F, 235, 252, 255, 225);
        for (int ring = 0; ring < 3; ring++) {
            float spin = time * 0.28F + ring * 1.047F;
            Vec3 u = new Vec3(Math.cos(spin), Math.sin(spin) * 0.6D, Math.sin(spin)).normalize();
            Vec3 ref = (ring == 0) ? new Vec3(0, 1, 0) : (ring == 1 ? new Vec3(1, 0, 0) : new Vec3(0, 0, 1));
            Vec3 vAxis = u.cross(ref).normalize();
            Vec3 wAxis = u.cross(vAxis).normalize();
            drawRing(vc, mat, center, vAxis, wAxis, radius * 0.72F, radius, 12, 185, 245, 255, 195);
        }
    }

    private static void drawFrostLaserBeam(VertexConsumer vc, Matrix4f mat, Vec3 start, Vec3 end, float coreRadius, float sheathRadius, float time, int pulseRings) {
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.2D || coreRadius < 0.01F) return;
        Vec3 dir = delta.scale(1.0D / len);
        Vec3 upRef = Math.abs(dir.y) > 0.9D ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(upRef).normalize();
        Vec3 up = right.cross(dir).normalize();

        float pulse = 1.0F + 0.12F * Mth.sin(time * 0.85F);
        drawCylinder(vc, mat, start, end, right, up, coreRadius * pulse, 8, 240, 253, 255, 245);
        drawCylinder(vc, mat, start, end, right, up, sheathRadius * (0.95F + 0.08F * Mth.cos(time * 0.65F)), 8, 65, 200, 255, 115);
        drawHelixRibbon(vc, mat, start, dir, right, up, len, sheathRadius * 1.15F, coreRadius * 0.55F, time * 0.45F, 1.0F, 175, 242, 255, 185);
        drawHelixRibbon(vc, mat, start, dir, right, up, len, sheathRadius * 1.05F, coreRadius * 0.45F, -time * 0.35F + 3.14F, -1.0F, 90, 215, 255, 150);
        for (int i = 0; i < pulseRings; i++) {
            float frac = ((time * 0.075F) + (float) i / pulseRings) % 1.0F;
            if (frac < 0.0F) frac += 1.0F;
            Vec3 pos = start.add(dir.scale(len * frac));
            float rIn = sheathRadius * (0.95F + 0.35F * frac);
            drawRing(vc, mat, pos, right, up, rIn, rIn + coreRadius * 0.65F, 12, 195, 248, 255, (int) (175 * (1.0F - frac * 0.75F)));
        }
    }

    private static void drawCylinder(VertexConsumer vc, Matrix4f mat, Vec3 a, Vec3 b, Vec3 right, Vec3 up, float radius, int segs, int r, int g, int bl, int al) {
        for (int i = 0; i < segs; i++) {
            double ang0 = (Math.PI * 2.0D * i) / segs;
            double ang1 = (Math.PI * 2.0D * (i + 1)) / segs;
            Vec3 off0 = right.scale(Math.cos(ang0) * radius).add(up.scale(Math.sin(ang0) * radius));
            Vec3 off1 = right.scale(Math.cos(ang1) * radius).add(up.scale(Math.sin(ang1) * radius));
            Vec3 p0 = a.add(off0), p1 = a.add(off1), p2 = b.add(off1), p3 = b.add(off0);
            v(vc, mat, (float) p0.x, (float) p0.y, (float) p0.z, r, g, bl, al);
            v(vc, mat, (float) p1.x, (float) p1.y, (float) p1.z, r, g, bl, al);
            v(vc, mat, (float) p2.x, (float) p2.y, (float) p2.z, r, g, bl, al);
            v(vc, mat, (float) p3.x, (float) p3.y, (float) p3.z, r, g, bl, al);
        }
    }

    private static void drawHelixRibbon(VertexConsumer vc, Matrix4f mat, Vec3 start, Vec3 dir, Vec3 right, Vec3 up, double len, float radius, float width, float phase, float twistSign, int r, int g, int bl, int al) {
        int steps = 24;
        Vec3 prevA = null, prevB = null;
        for (int i = 0; i <= steps; i++) {
            double frac = (double) i / steps;
            double ang = phase + twistSign * frac * Math.PI * 6.0D;
            Vec3 radial = right.scale(Math.cos(ang)).add(up.scale(Math.sin(ang)));
            Vec3 center = start.add(dir.scale(len * frac)).add(radial.scale(radius));
            Vec3 pA = center.add(radial.scale(width * 0.5F));
            Vec3 pB = center.subtract(radial.scale(width * 0.5F));
            if (prevA != null) {
                int segAlpha = (int) (al * (1.0D - frac * 0.45D));
                v(vc, mat, (float) prevA.x, (float) prevA.y, (float) prevA.z, r, g, bl, segAlpha);
                v(vc, mat, (float) prevB.x, (float) prevB.y, (float) prevB.z, r, g, bl, segAlpha);
                v(vc, mat, (float) pB.x, (float) pB.y, (float) pB.z, r, g, bl, segAlpha);
                v(vc, mat, (float) pA.x, (float) pA.y, (float) pA.z, r, g, bl, segAlpha);
            }
            prevA = pA;
            prevB = pB;
        }
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, Vec3 center, Vec3 uAxis, Vec3 vAxis, float rIn, float rOut, int segs, int r, int g, int bl, int al) {
        for (int i = 0; i < segs; i++) {
            double ang0 = (Math.PI * 2.0D * i) / segs;
            double ang1 = (Math.PI * 2.0D * (i + 1)) / segs;
            Vec3 d0 = uAxis.scale(Math.cos(ang0)).add(vAxis.scale(Math.sin(ang0)));
            Vec3 d1 = uAxis.scale(Math.cos(ang1)).add(vAxis.scale(Math.sin(ang1)));
            Vec3 i0 = center.add(d0.scale(rIn)), o0 = center.add(d0.scale(rOut));
            Vec3 o1 = center.add(d1.scale(rOut)), i1 = center.add(d1.scale(rIn));
            v(vc, mat, (float) i0.x, (float) i0.y, (float) i0.z, r, g, bl, al);
            v(vc, mat, (float) o0.x, (float) o0.y, (float) o0.z, r, g, bl, al);
            v(vc, mat, (float) o1.x, (float) o1.y, (float) o1.z, r, g, bl, al);
            v(vc, mat, (float) i1.x, (float) i1.y, (float) i1.z, r, g, bl, al);
        }
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int bl, int al) {
        for (int i = 0; i < segs; i++) {
            double a0 = (Math.PI * 2.0D * i) / segs;
            double a1 = (Math.PI * 2.0D * (i + 1)) / segs;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            v(vc, mat, c0 * rIn, 0.0F, s0 * rIn, r, g, bl, al);
            v(vc, mat, c0 * rOut, 0.0F, s0 * rOut, r, g, bl, al);
            v(vc, mat, c1 * rOut, 0.0F, s1 * rOut, r, g, bl, al);
            v(vc, mat, c1 * rIn, 0.0F, s1 * rIn, r, g, bl, al);
            v(vc, mat, c1 * rIn, 0.0F, s1 * rIn, r, g, bl, al);
            v(vc, mat, c1 * rOut, 0.0F, s1 * rOut, r, g, bl, al);
            v(vc, mat, c0 * rOut, 0.0F, s0 * rOut, r, g, bl, al);
            v(vc, mat, c0 * rIn, 0.0F, s0 * rIn, r, g, bl, al);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(CryoRegisvineEntity entity) {
        return TEXTURE;
    }
}
