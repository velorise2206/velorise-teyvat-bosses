package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.GeoDrillerModel;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders {@link GeoDrillerEntity} with smooth partial-tick interpolated procedural animations:
 * spinning drill head, true-travel wheel &amp; tread rotation, dual independent drive/drill levers,
 * engine spool-up shudder, and a true merged Vanilla Single / Double Chest on the rear deck.
 */
public class GeoDrillerRenderer extends EntityRenderer<GeoDrillerEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/geo_driller.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/geo_driller_emissive.png");

    private final GeoDrillerModel<GeoDrillerEntity> model;
    private final ModelPart singleChestModel;
    private final ModelPart doubleChestLeftModel;
    private final ModelPart doubleChestRightModel;
    private final PartInstallAnimator installAnimator = new PartInstallAnimator();

    private static final int BIT_CHEST_1 = 1 << 8;
    private static final int BIT_CHEST_2 = 1 << 9;

    public GeoDrillerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new GeoDrillerModel<>(ctx.bakeLayer(GeoDrillerModel.LAYER_LOCATION));
        this.singleChestModel = ctx.bakeLayer(ModelLayers.CHEST);
        this.doubleChestLeftModel = ctx.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleChestRightModel = ctx.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.shadowRadius = 1.6F;
    }

    @Override
    public void render(GeoDrillerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        model.resetPose();
        int parts = entity.getPartMask();
        boolean hasDrill = (parts & GeoDrillerEntity.PART_DRILL) != 0;
        boolean hasEngine = (parts & GeoDrillerEntity.PART_ENGINE) != 0;

        setVisible("drill_assembly", hasDrill);
        setVisible("engine_unit", hasEngine);

        int chestCount = entity.getChestCount();
        int trackedMask = parts
                | (chestCount >= 1 ? BIT_CHEST_1 : 0)
                | (chestCount >= 2 ? BIT_CHEST_2 : 0);
        installAnimator.update(entity, trackedMask, partialTicks);
        float drillInstallRem = hasDrill ? installAnimator.getRemaining(entity, GeoDrillerEntity.PART_DRILL, partialTicks) : 0.0F;
        float engineInstallRem = hasEngine ? installAnimator.getRemaining(entity, GeoDrillerEntity.PART_ENGINE, partialTicks) : 0.0F;
        float chest1InstallRem = chestCount >= 1 ? installAnimator.getRemaining(entity, BIT_CHEST_1, partialTicks) : 0.0F;
        float chest2InstallRem = chestCount >= 2 ? installAnimator.getRemaining(entity, BIT_CHEST_2, partialTicks) : 0.0F;

        // Smooth partialTick interpolation for 144fps buttery motion
        float age = entity.tickCount + partialTicks;
        float smoothWheelL = Mth.lerp(partialTicks, entity.prevWheelSpinL, entity.wheelSpinL);
        float smoothWheelR = Mth.lerp(partialTicks, entity.prevWheelSpinR, entity.wheelSpinR);
        float smoothDrill = Mth.lerp(partialTicks, entity.prevDrillSpin, entity.drillSpin);
        float smoothDriveSpool = Mth.lerp(partialTicks, entity.prevDriveSpool, entity.driveSpool);
        float smoothDrillSpool = Mth.lerp(partialTicks, entity.prevDrillSpool, entity.drillSpool);
        float smoothLeverL = Mth.lerp(partialTicks, entity.prevLeverLAngle, entity.leverLAngle);
        float smoothLeverR = Mth.lerp(partialTicks, entity.prevLeverRAngle, entity.leverRAngle);
        float smoothFlapper = Mth.lerp(partialTicks, entity.prevFlapperAngle, entity.flapperAngle);

        // 1. Drill assembly pitch tilt (Level / Down / Up) + smooth high-speed spin
        ModelPart drillAsm = model.getBone("drill_assembly");
        if (drillAsm != null) {
            float pitchTilt = switch (entity.getPitchMode()) {
                case GeoDrillerEntity.PITCH_DOWN -> -0.22F;
                case GeoDrillerEntity.PITCH_UP -> 0.22F;
                default -> 0.0F;
            };
            drillAsm.xRot += pitchTilt;
            PartInstallAnimator.applyPush(drillAsm, drillInstallRem, 0.0F, 0.0F, -11.0F);
        }
        ModelPart drillHead = model.getBone("drill_head");
        if (drillHead != null) {
            drillHead.zRot += smoothDrill + drillInstallRem * 0.65F;
        }

        // 2. Twin industrial smokestack rain-flapper caps lifting under exhaust pressure
        if (hasEngine) {
            PartInstallAnimator.applyPush(model.getBone("engine_unit"), engineInstallRem, 0.0F, -9.5F, 3.0F);
            ModelPart flapL = model.getBone("flapper_l");
            ModelPart flapR = model.getBone("flapper_r");
            if (flapL != null) flapL.xRot += smoothFlapper;
            if (flapR != null) flapR.xRot += smoothFlapper;
        }

        // Trigger smooth pitch/volume coast-down drill sound on client whenever drill head is spinning
        if (hasDrill && smoothDrillSpool > 0.03F) {
            GeoDrillerClientController.ensureSpinSound(entity);
        }

        // 3. All 6 interlocking 8-tooth sprocket gears + 44 moving caterpillar tread cleats synced 1:1 to travel
        for (int i = 0; i < 3; i++) {
            ModelPart wl = model.getBone("wheel_l_" + i);
            ModelPart wr = model.getBone("wheel_r_" + i);
            if (wl != null) wl.xRot += smoothWheelL;
            if (wr != null) wr.xRot += smoothWheelR;
        }
        animateTreadBelt("tread_pad_l_", smoothWheelL * 3.81F);
        animateTreadBelt("tread_pad_r_", smoothWheelR * 3.81F);

        // 4. Independent control levers: Left = Drive Forward/Reverse, Right = Drill Engage & Pitch
        ModelPart levL = model.getBone("lever_l");
        ModelPart levR = model.getBone("lever_r");
        if (levL != null) levL.xRot += smoothLeverL;
        if (levR != null) levR.xRot += smoothLeverR;

        // 5. Heavy engine spool-up rumble + drilling vibration
        float rumble = Math.max(smoothDriveSpool * 0.65F, smoothDrillSpool);
        float vibY = rumble > 0.05F ? Mth.sin(age * 2.8F) * 0.022F * rumble : 0.0F;
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());

        // Subtle whole-chassis slope tilt when boring UP/DOWN so the driller leans into tunnel grades
        float slopePitchDeg = switch (entity.getPitchMode()) {
            case GeoDrillerEntity.PITCH_DOWN -> -5.5F;
            case GeoDrillerEntity.PITCH_UP -> 5.5F;
            default -> 0.0F;
        };

        poseStack.pushPose();
        poseStack.translate(0.0D, vibY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        if (Math.abs(slopePitchDeg) > 0.1F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(slopePitchDeg));
        }

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poseStack.popPose();

        // 6. Render true Vanilla Single Chest (1x) or seamlessly merged Double Chest (2x, single centered latch)
        if (chestCount > 0) {
            float chestRem = chestCount == 1 ? chest1InstallRem : Math.max(chest1InstallRem, chest2InstallRem);
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.82D + chestRem * 0.42D, 0.96D + chestRem * 0.22D);
            float scale = 0.62F;
            poseStack.scale(scale, scale, scale);
            // Rotate whole chest assembly 180 deg so latch faces cleanly backward (+Z) out of rear deck
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (chestCount == 1) {
                renderChestHalf(singleChestModel, Sheets.CHEST_LOCATION, -0.5D, poseStack, buf, packedLight);
            } else {
                // Before parent 180-deg flip: LEFT (-1.0) open seam at +X meets RIGHT (0.0) open seam at -X!
                renderChestHalf(doubleChestLeftModel, Sheets.CHEST_LOCATION_LEFT, -1.0D, poseStack, buf, packedLight);
                renderChestHalf(doubleChestRightModel, Sheets.CHEST_LOCATION_RIGHT, 0.0D, poseStack, buf, packedLight);
            }
            poseStack.popPose();
        }

        // 7. AAA 3D Volumetric Headlight Beam Cone + Spinning Geo Bore-Surge Aura!
        if (entity.isHeadlightOn() || smoothDrillSpool > 0.15F) {
            VertexConsumer lvc = buf.getBuffer(RenderType.lightning());
            Matrix4f mat = poseStack.last().pose();
            if (entity.isHeadlightOn()) {
                // Twin roof-side floodlight beams (left & right canopy edges) angled gently forward
                drawDrillerConeAt(lvc, mat, -0.80F, 2.44F, 0.96F, 2.85F, 0.11F, 0.46F, 12, 255, 242, 195, 30);
                drawDrillerConeAt(lvc, mat,  0.80F, 2.44F, 0.96F, 2.85F, 0.11F, 0.46F, 12, 255, 242, 195, 30);
            }
            if (smoothDrillSpool > 0.15F && hasDrill) {
                boolean surge = entity.isSurging();
                int aRing = (int) ((surge ? 185 : 95) * smoothDrillSpool);
                float boreR = surge ? 1.15F : 0.85F;
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.85D, -1.75D);
                poseStack.mulPose(Axis.ZP.rotation(smoothDrill * 1.4F));
                Matrix4f dm = poseStack.last().pose();
                drawBoreVortexRing(lvc, dm, boreR * 0.55F, boreR, 16, 255, surge ? 225 : 190, surge ? 95 : 55, aRing);
                if (surge) {
                    drawDrillerCone(lvc, dm, 0.0F, 0.0F, 2.6F, 0.95F, 0.15F, 16, 255, 215, 80, 130);
                }
                poseStack.popPose();
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private void animateTreadBelt(String prefix, float travelUnits) {
        final float zHalf = 15.5F;
        final float straightLen = zHalf * 2.0F;
        final float yCenter = -0.25F;
        final float radY = 6.40F;
        final float radZ = 4.35F;
        final float arcLen = (float) (Math.PI * Math.sqrt(0.5D * (radY * radY + radZ * radZ)));
        final float perim = straightLen * 2.0F + arcLen * 2.0F;
        final int count = GeoDrillerModel.TREAD_PAD_COUNT;

        for (int i = 0; i < count; i++) {
            ModelPart pad = model.getBone(prefix + i);
            if (pad == null) continue;
            float s = ((i * (perim / count)) + travelUnits) % perim;
            if (s < 0.0F) s += perim;

            float py, pz, rotX;
            if (s < straightLen) {
                float t = s / straightLen;
                pz = zHalf - t * straightLen;
                py = yCenter - radY;
                rotX = 0.0F;
            } else if (s < straightLen + arcLen) {
                float u = (s - straightLen) / arcLen;
                float theta = u * Mth.PI;
                pz = -zHalf - Mth.sin(theta) * radZ;
                py = yCenter - Mth.cos(theta) * radY;
                rotX = theta;
            } else if (s < straightLen * 2.0F + arcLen) {
                float t = (s - straightLen - arcLen) / straightLen;
                pz = -zHalf + t * straightLen;
                py = yCenter + radY;
                rotX = Mth.PI;
            } else {
                float u = (s - straightLen * 2.0F - arcLen) / arcLen;
                float theta = u * Mth.PI;
                pz = zHalf + Mth.sin(theta) * radZ;
                py = yCenter + Mth.cos(theta) * radY;
                rotX = Mth.PI + theta;
            }
            pad.x = 0.0F;
            pad.y = py;
            pad.z = pz;
            pad.xRot = rotX;
        }
    }

    private static void drawDrillerConeAt(VertexConsumer vc, Matrix4f mat, float xCenter, float yCenter, float zStart, float zEnd,
                                          float rStart, float rEnd, int segs, int r, int g, int b, int a) {
        if (a <= 4) return;
        float z0 = -zStart;
        float z1 = -zEnd;
        float y1 = yCenter - 0.35F;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, xCenter + c0 * rStart, yCenter + s0 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, xCenter + c1 * rStart, yCenter + s1 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, xCenter + c1 * rEnd, y1 + s1 * rEnd, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, xCenter + c0 * rEnd, y1 + s0 * rEnd, z1).setColor(r, g, b, 0);
        }
    }

    private static void drawDrillerCone(VertexConsumer vc, Matrix4f mat, float yCenter, float zStart, float zEnd,
                                        float rStart, float rEnd, int segs, int r, int g, int b, int a) {
        if (a <= 4) return;
        float z0 = -zStart;
        float z1 = -zEnd;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rStart, yCenter + s0 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rStart, yCenter + s1 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rEnd, yCenter + s1 * rEnd, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * rEnd, yCenter + s0 * rEnd, z1).setColor(r, g, b, 0);
        }
    }

    private static void drawBoreVortexRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        if (a <= 4) return;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, 0.0F).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, 0.0F).setColor(r, g, b, 0);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, 0.0F).setColor(r, g, b, 0);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, 0.0F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, 0.0F).setColor(r, g, b, 0);
        }
    }

    private static void renderChestHalf(ModelPart chestPart, Material material, double offsetX, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(offsetX, 0.0D, -0.5D);
        VertexConsumer vc = material.buffer(buf, RenderType::entityCutout);
        chestPart.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void setVisible(String bone, boolean vis) {
        ModelPart p = model.getBone(bone);
        if (p != null) p.visible = vis;
    }

    @Override
    public ResourceLocation getTextureLocation(GeoDrillerEntity entity) {
        return TEXTURE;
    }
}
