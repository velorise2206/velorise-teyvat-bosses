// Auto-compiled from ruin_guard_boss.bbmodel (254 cubes, 28 bones, 512x512 Box-UV)
package com.velorise.teyvatbosses.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import java.util.HashMap;
import java.util.Map;

public class RuinGuardModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TeyvatBossesConstants.id("ruin_guard"), "main");
    public final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public RuinGuardModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        registerBone("root", rootPart.getChild("root"));
        registerBone("pelvis", rootPart.getChild("root").getChild("pelvis"));
        registerBone("waist", rootPart.getChild("root").getChild("pelvis").getChild("waist"));
        registerBone("torso", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso"));
        registerBone("eye_assembly", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("eye_assembly"));
        registerBone("eye_core", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("eye_assembly").getChild("eye_core"));
        registerBone("back_reactor", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("back_reactor"));
        registerBone("missile_pod_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_r"));
        registerBone("missile_pod_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_l"));
        registerBone("missile_tip_0", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_r").getChild("missile_tip_0"));
        registerBone("missile_tip_2", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_r").getChild("missile_tip_2"));
        registerBone("missile_tip_4", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_r").getChild("missile_tip_4"));
        registerBone("missile_tip_1", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_l").getChild("missile_tip_1"));
        registerBone("missile_tip_3", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_l").getChild("missile_tip_3"));
        registerBone("missile_tip_5", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("missile_pod_l").getChild("missile_tip_5"));
        registerBone("shoulder_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_r"));
        registerBone("upper_arm_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_r").getChild("upper_arm_r"));
        registerBone("forearm_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_r").getChild("upper_arm_r").getChild("forearm_r"));
        registerBone("hand_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_r").getChild("upper_arm_r").getChild("forearm_r").getChild("hand_r"));
        registerBone("vfx_fist_r", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_r").getChild("upper_arm_r").getChild("forearm_r").getChild("hand_r").getChild("vfx_fist_r"));
        registerBone("shoulder_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_l"));
        registerBone("upper_arm_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_l").getChild("upper_arm_l"));
        registerBone("forearm_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_l").getChild("upper_arm_l").getChild("forearm_l"));
        registerBone("hand_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_l").getChild("upper_arm_l").getChild("forearm_l").getChild("hand_l"));
        registerBone("vfx_fist_l", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("shoulder_l").getChild("upper_arm_l").getChild("forearm_l").getChild("hand_l").getChild("vfx_fist_l"));
        registerBone("vfx_eye_anchor", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("vfx_eye_anchor"));
        registerBone("vfx_chest_anchor", rootPart.getChild("root").getChild("pelvis").getChild("waist").getChild("torso").getChild("vfx_chest_anchor"));
        registerBone("hip_r", rootPart.getChild("root").getChild("pelvis").getChild("hip_r"));
        registerBone("knee_r", rootPart.getChild("root").getChild("pelvis").getChild("hip_r").getChild("knee_r"));
        registerBone("foot_r", rootPart.getChild("root").getChild("pelvis").getChild("hip_r").getChild("knee_r").getChild("foot_r"));
        registerBone("hip_l", rootPart.getChild("root").getChild("pelvis").getChild("hip_l"));
        registerBone("knee_l", rootPart.getChild("root").getChild("pelvis").getChild("hip_l").getChild("knee_l"));
        registerBone("foot_l", rootPart.getChild("root").getChild("pelvis").getChild("hip_l").getChild("knee_l").getChild("foot_l"));
        registerBone("vfx_ground_ring", rootPart.getChild("root").getChild("vfx_ground_ring"));
    }

    private void registerBone(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
    }

    public ModelPart getBone(String name) { return bones.get(name); }

    public java.util.Set<String> getBoneNames() { return bones.keySet(); }

    public void resetPose() {
        for (Map.Entry<String, PartPose> e : initialPoses.entrySet()) {
            ModelPart p = bones.get(e.getKey());
            if (p != null) {
                p.loadPose(e.getValue());
                p.xScale = 1.0F;
                p.yScale = 1.0F;
                p.zScale = 1.0F;
            }
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Driven by RuinGuardAnimations.apply / applyBlended in Renderer
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition b_root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create()
            , PartPose.offset(0.00F, 24.00F, 0.00F));
        PartDefinition b_pelvis = b_root.addOrReplaceChild("pelvis", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-9.00F, -4.00F, -6.00F, 18.0F, 7.0F, 12.0F)
            .texOffs(61, 0).addBox(-10.00F, -3.00F, -5.00F, 20.0F, 5.0F, 10.0F, new CubeDeformation(-0.02F))
            .texOffs(122, 0).addBox(-4.00F, -2.00F, -7.50F, 8.0F, 7.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(143, 0).addBox(-2.00F, -1.00F, -8.00F, 4.0F, 5.0F, 1.0F, new CubeDeformation(-0.022F))
            .texOffs(154, 0).addBox(-3.00F, 3.00F, -7.00F, 6.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(171, 0).addBox(-5.00F, -2.00F, 5.50F, 10.0F, 6.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(196, 0).addBox(-3.00F, -1.00F, 7.00F, 6.0F, 4.0F, 1.0F, new CubeDeformation(-0.008F))
            .texOffs(211, 0).addBox(-11.00F, -2.50F, -3.00F, 3.0F, 6.0F, 6.0F, new CubeDeformation(-0.015F))
            .texOffs(230, 0).addBox(-11.50F, -1.50F, -2.00F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.022F))
            .texOffs(243, 0).addBox(-12.50F, -3.50F, -6.00F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.029F))
            .texOffs(258, 0).addBox(-12.50F, -3.50F, 1.00F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.036F))
            .texOffs(273, 0).addBox(-6.50F, -4.50F, -6.50F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(282, 0).addBox(-6.50F, -4.50F, 4.50F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(291, 0).addBox(8.00F, -2.50F, -3.00F, 3.0F, 6.0F, 6.0F, new CubeDeformation(-0.022F))
            .texOffs(310, 0).addBox(9.50F, -1.50F, -2.00F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.029F))
            .texOffs(323, 0).addBox(10.50F, -3.50F, -6.00F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.036F))
            .texOffs(338, 0).addBox(10.50F, -3.50F, 1.00F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.008F))
            .texOffs(353, 0).addBox(4.50F, -4.50F, -6.50F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(362, 0).addBox(4.50F, -4.50F, 4.50F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.022F))
            , PartPose.offset(0.00F, -26.00F, 0.00F));
        PartDefinition b_waist = b_pelvis.addOrReplaceChild("waist", CubeListBuilder.create()
            .texOffs(371, 0).addBox(-7.00F, -4.50F, -5.00F, 14.0F, 6.0F, 10.0F)
            .texOffs(420, 0).addBox(-5.00F, -4.50F, -7.00F, 10.0F, 6.0F, 14.0F, new CubeDeformation(-0.02F))
            .texOffs(0, 21).addBox(-8.00F, -2.00F, -5.50F, 16.0F, 2.0F, 11.0F, new CubeDeformation(-0.015F))
            .texOffs(55, 21).addBox(-5.50F, -2.00F, -8.00F, 11.0F, 2.0F, 16.0F, new CubeDeformation(-0.02F))
            .texOffs(110, 21).addBox(-8.00F, -4.50F, -5.50F, 16.0F, 2.0F, 11.0F, new CubeDeformation(-0.022F))
            .texOffs(165, 21).addBox(-5.50F, -4.50F, -8.00F, 11.0F, 2.0F, 16.0F, new CubeDeformation(-0.02F))
            .texOffs(220, 21).addBox(-2.50F, -1.50F, -8.10F, 5.0F, 1.0F, 1.0F, new CubeDeformation(-0.029F))
            .texOffs(233, 21).addBox(-2.50F, -1.50F, 7.10F, 5.0F, 1.0F, 1.0F, new CubeDeformation(-0.036F))
            .texOffs(246, 21).addBox(7.10F, -1.50F, -2.50F, 1.0F, 1.0F, 5.0F, new CubeDeformation(-0.008F))
            .texOffs(259, 21).addBox(-8.10F, -1.50F, -2.50F, 1.0F, 1.0F, 5.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(0.00F, -5.00F, 0.00F));
        PartDefinition b_torso = b_waist.addOrReplaceChild("torso", CubeListBuilder.create()
            .texOffs(272, 21).addBox(-10.00F, -6.00F, -7.00F, 20.0F, 6.0F, 14.0F)
            .texOffs(341, 21).addBox(-7.00F, -6.00F, -10.00F, 14.0F, 6.0F, 20.0F, new CubeDeformation(-0.02F))
            .texOffs(410, 21).addBox(-10.50F, -1.00F, -7.50F, 21.0F, 1.0F, 15.0F, new CubeDeformation(-0.015F))
            .texOffs(0, 48).addBox(-10.50F, -6.00F, -7.50F, 21.0F, 1.0F, 15.0F, new CubeDeformation(0.03F))
            // Split core hull around recessed rear battery bay (x: -4.5..+4.5, y: -16.0..-7.5, z: +5.5..+12.3)
            .texOffs(73, 48).addBox(-12.00F, -19.00F, -8.50F, 24.0F, 13.0F, 14.0F, new CubeDeformation(-0.022F))
            .texOffs(73, 48).addBox(-12.00F, -19.00F, 5.50F, 7.5F, 13.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(73, 48).addBox(4.50F, -19.00F, 5.50F, 7.5F, 13.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(73, 48).addBox(-4.50F, -19.00F, 5.50F, 9.0F, 3.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(73, 48).addBox(-4.50F, -7.50F, 5.50F, 9.0F, 1.5F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(156, 48).addBox(-8.50F, -19.00F, -12.00F, 17.0F, 13.0F, 17.5F, new CubeDeformation(-0.02F))
            .texOffs(156, 48).addBox(-8.50F, -19.00F, 5.50F, 17.0F, 3.0F, 6.5F, new CubeDeformation(-0.02F))
            .texOffs(156, 48).addBox(-8.50F, -7.50F, 5.50F, 17.0F, 1.5F, 6.5F, new CubeDeformation(-0.02F))
            .texOffs(156, 48).addBox(-8.50F, -16.00F, 5.50F, 4.0F, 8.5F, 6.5F, new CubeDeformation(-0.02F))
            .texOffs(156, 48).addBox(4.50F, -16.00F, 5.50F, 4.0F, 8.5F, 6.5F, new CubeDeformation(-0.02F))
            .texOffs(239, 48).addBox(-8.50F, -18.50F, -12.30F, 17.0F, 13.0F, 3.0F, new CubeDeformation(-0.029F))
            .texOffs(280, 48).addBox(-6.00F, -8.50F, -12.80F, 12.0F, 4.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(309, 48).addBox(-5.00F, -5.00F, -11.80F, 10.0F, 3.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(334, 48).addBox(-3.00F, -2.50F, -11.20F, 6.0F, 2.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(351, 48).addBox(-11.50F, -18.00F, -12.20F, 5.0F, 11.0F, 4.0F, new CubeDeformation(-0.022F))
            .texOffs(370, 48).addBox(-11.50F, -17.00F, -12.30F, 3.0F, 9.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(381, 48).addBox(-13.50F, -16.50F, -9.00F, 3.0F, 12.0F, 8.0F, new CubeDeformation(-0.036F))
            .texOffs(404, 48).addBox(-13.50F, -16.50F, 1.00F, 3.0F, 12.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(427, 48).addBox(-8.00F, -6.00F, -11.50F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(436, 48).addBox(-13.50F, -5.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.022F))
            .texOffs(451, 48).addBox(-13.50F, -8.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.029F))
            .texOffs(466, 48).addBox(-13.50F, -11.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.036F))
            .texOffs(481, 48).addBox(-13.50F, -14.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.008F))
            .texOffs(496, 48).addBox(-13.50F, -17.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.015F))
            .texOffs(0, 86).addBox(-15.50F, -20.50F, -4.00F, 5.0F, 6.0F, 8.0F, new CubeDeformation(-0.022F))
            .texOffs(27, 86).addBox(-16.00F, -21.00F, -5.00F, 4.0F, 3.0F, 10.0F, new CubeDeformation(-0.029F))
            .texOffs(56, 86).addBox(-10.00F, -21.50F, -10.50F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(69, 86).addBox(6.50F, -18.00F, -12.20F, 5.0F, 11.0F, 4.0F, new CubeDeformation(-0.008F))
            .texOffs(88, 86).addBox(8.50F, -17.00F, -12.30F, 3.0F, 9.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(99, 86).addBox(10.50F, -16.50F, -9.00F, 3.0F, 12.0F, 8.0F, new CubeDeformation(-0.022F))
            .texOffs(122, 86).addBox(10.50F, -16.50F, 1.00F, 3.0F, 12.0F, 8.0F, new CubeDeformation(-0.029F))
            .texOffs(145, 86).addBox(6.00F, -6.00F, -11.50F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(154, 86).addBox(12.50F, -5.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.008F))
            .texOffs(169, 86).addBox(12.50F, -8.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.015F))
            .texOffs(184, 86).addBox(12.50F, -11.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.022F))
            .texOffs(199, 86).addBox(12.50F, -14.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.029F))
            .texOffs(214, 86).addBox(12.50F, -17.50F, -3.00F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.036F))
            .texOffs(229, 86).addBox(10.50F, -20.50F, -4.00F, 5.0F, 6.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(256, 86).addBox(12.00F, -21.00F, -5.00F, 4.0F, 3.0F, 10.0F, new CubeDeformation(-0.015F))
            .texOffs(285, 86).addBox(6.00F, -21.50F, -10.50F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(298, 86).addBox(-12.00F, -22.00F, -8.50F, 24.0F, 3.0F, 17.0F, new CubeDeformation(-0.029F))
            .texOffs(381, 86).addBox(-8.50F, -22.00F, -12.00F, 17.0F, 3.0F, 24.0F, new CubeDeformation(-0.02F))
            .texOffs(0, 114).addBox(-12.50F, -20.00F, -9.00F, 25.0F, 1.0F, 18.0F, new CubeDeformation(-0.036F))
            .texOffs(87, 114).addBox(-12.50F, -22.00F, -9.00F, 25.0F, 1.0F, 18.0F, new CubeDeformation(0.03F))
            .texOffs(174, 114).addBox(-10.00F, -25.00F, -7.00F, 20.0F, 3.0F, 14.0F, new CubeDeformation(-0.008F))
            .texOffs(243, 114).addBox(-7.00F, -25.00F, -10.00F, 14.0F, 3.0F, 20.0F, new CubeDeformation(-0.02F))
            .texOffs(312, 114).addBox(-10.50F, -23.00F, -7.50F, 21.0F, 1.0F, 15.0F, new CubeDeformation(-0.015F))
            .texOffs(385, 114).addBox(-10.50F, -25.00F, -7.50F, 21.0F, 1.0F, 15.0F, new CubeDeformation(0.03F))
            .texOffs(458, 114).addBox(-7.00F, -27.00F, -5.00F, 14.0F, 2.0F, 10.0F, new CubeDeformation(-0.022F))
            .texOffs(0, 138).addBox(-5.00F, -27.00F, -7.00F, 10.0F, 2.0F, 14.0F, new CubeDeformation(-0.02F))
            .texOffs(49, 138).addBox(-4.00F, -29.00F, -3.00F, 8.0F, 2.0F, 6.0F, new CubeDeformation(-0.029F))
            .texOffs(78, 138).addBox(-3.00F, -29.00F, -4.00F, 6.0F, 2.0F, 8.0F, new CubeDeformation(-0.02F))
            .texOffs(107, 138).addBox(-1.50F, -29.00F, -4.50F, 3.0F, 1.0F, 9.0F, new CubeDeformation(0.04F))
            .texOffs(132, 138).addBox(-4.50F, -29.00F, -1.50F, 9.0F, 1.0F, 3.0F, new CubeDeformation(0.02F))
            .texOffs(157, 138).addBox(-2.00F, -28.50F, -7.50F, 4.0F, 2.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(170, 138).addBox(-3.00F, -27.50F, 5.00F, 6.0F, 2.0F, 3.0F, new CubeDeformation(-0.008F))
            // Framed rear armor bezel surrounding the hollowed battery chamber opening
            .texOffs(189, 138).addBox(-8.50F, -18.50F, 9.30F, 17.0F, 2.5F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(189, 138).addBox(-8.50F, -7.50F, 9.30F, 17.0F, 2.0F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(189, 138).addBox(-8.50F, -16.00F, 9.30F, 4.0F, 8.5F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(189, 138).addBox(4.50F, -16.00F, 9.30F, 4.0F, 8.5F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(230, 138).addBox(-6.00F, -7.00F, 9.20F, 12.0F, 5.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(259, 138).addBox(-11.00F, -18.00F, 9.30F, 4.0F, 11.0F, 3.0F, new CubeDeformation(-0.029F))
            .texOffs(274, 138).addBox(-8.00F, -21.00F, 8.00F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.036F))
            .texOffs(287, 138).addBox(-8.50F, -21.50F, 7.50F, 4.0F, 1.0F, 4.0F, new CubeDeformation(-0.008F))
            .texOffs(304, 138).addBox(7.00F, -18.00F, 9.30F, 4.0F, 11.0F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(319, 138).addBox(5.00F, -21.00F, 8.00F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(332, 138).addBox(4.50F, -21.50F, 7.50F, 4.0F, 1.0F, 4.0F, new CubeDeformation(-0.029F))
            , PartPose.offset(0.00F, -4.00F, 0.00F));
        PartDefinition b_eye_assembly = b_torso.addOrReplaceChild("eye_assembly", CubeListBuilder.create()
            .texOffs(349, 138).addBox(-5.50F, -5.50F, -1.80F, 11.0F, 11.0F, 2.0F)
            .texOffs(376, 138).addBox(-4.50F, -5.50F, -2.00F, 9.0F, 11.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(397, 138).addBox(-5.50F, -4.50F, -2.00F, 11.0F, 9.0F, 1.0F, new CubeDeformation(-0.02F))
            .texOffs(422, 138).addBox(-3.00F, -6.50F, -2.80F, 6.0F, 2.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(439, 138).addBox(-3.00F, 4.50F, -2.80F, 6.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(456, 138).addBox(4.50F, -3.00F, -2.80F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(465, 138).addBox(-6.50F, -3.00F, -2.80F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(474, 138).addBox(-5.50F, 3.50F, -2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(481, 138).addBox(-5.50F, -5.50F, -2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.022F))
            .texOffs(488, 138).addBox(3.50F, 3.50F, -2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.029F))
            .texOffs(495, 138).addBox(3.50F, -5.50F, -2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.036F))
            , PartPose.offset(0.00F, -14.00F, -11.00F));
        PartDefinition b_eye_core = b_eye_assembly.addOrReplaceChild("eye_core", CubeListBuilder.create()
            .texOffs(0, 155).addBox(-3.00F, -3.00F, -1.40F, 6.0F, 6.0F, 1.0F)
            .texOffs(15, 155).addBox(-2.00F, -2.00F, -1.80F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(26, 155).addBox(-1.00F, -1.00F, -2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.022F))
            , PartPose.offset(0.00F, -0.00F, -1.00F));
        PartDefinition b_back_reactor = b_torso.addOrReplaceChild("back_reactor", CubeListBuilder.create()
            .texOffs(33, 155).addBox(-4.50F, -4.50F, 0.20F, 9.0F, 9.0F, 2.0F)
            .texOffs(56, 155).addBox(-3.00F, -3.00F, 1.50F, 6.0F, 6.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(71, 155).addBox(-2.00F, -2.00F, 1.80F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.022F))
            .texOffs(82, 155).addBox(-1.00F, -1.00F, 2.10F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.029F))
            .texOffs(89, 155).addBox(-3.50F, -6.00F, 0.80F, 7.0F, 2.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(108, 155).addBox(-3.50F, 4.00F, 0.80F, 7.0F, 2.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(127, 155).addBox(-6.00F, -3.50F, 0.80F, 2.0F, 7.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(136, 155).addBox(4.00F, -3.50F, 0.80F, 2.0F, 7.0F, 2.0F, new CubeDeformation(-0.022F))
            , PartPose.offset(0.00F, -12.00F, 11.00F));
        PartDefinition b_missile_pod_r = b_torso.addOrReplaceChild("missile_pod_r", CubeListBuilder.create()
            .texOffs(145, 155).addBox(-3.50F, -5.50F, -3.00F, 7.0F, 8.0F, 7.0F)
            .texOffs(174, 155).addBox(-4.00F, -5.50F, -2.50F, 8.0F, 6.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(203, 155).addBox(-3.50F, -6.50F, -3.00F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.03F))
            .texOffs(232, 155).addBox(-2.80F, -7.50F, -2.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(246, 155).addBox(0.80F, -7.50F, -2.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(260, 155).addBox(-1.00F, -7.50F, 1.30F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(274, 155).addBox(-2.50F, -1.50F, 3.50F, 5.0F, 4.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(289, 155).addBox(-1.50F, -0.50F, 4.80F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.008F))
            , PartPose.offsetAndRotation(11.00F, -21.00F, 5.00F, 0.2443F, -0.2094F, -0.1745F));
        b_missile_pod_r.addOrReplaceChild("missile_tip_0", CubeListBuilder.create().texOffs(241, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(-1.80F, -7.30F, -1.00F));
        b_missile_pod_r.addOrReplaceChild("missile_tip_2", CubeListBuilder.create().texOffs(255, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(1.80F, -7.30F, -1.00F));
        b_missile_pod_r.addOrReplaceChild("missile_tip_4", CubeListBuilder.create().texOffs(269, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.00F, -7.30F, 2.30F));
        PartDefinition b_missile_pod_l = b_torso.addOrReplaceChild("missile_pod_l", CubeListBuilder.create()
            .texOffs(298, 155).addBox(-3.50F, -5.50F, -3.00F, 7.0F, 8.0F, 7.0F)
            .texOffs(327, 155).addBox(-4.00F, -5.50F, -2.50F, 8.0F, 6.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(356, 155).addBox(-3.50F, -6.50F, -3.00F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.03F))
            .texOffs(385, 155).addBox(-2.80F, -7.50F, -2.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(399, 155).addBox(0.80F, -7.50F, -2.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(413, 155).addBox(-1.00F, -7.50F, 1.30F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(427, 155).addBox(-2.50F, -1.50F, 3.50F, 5.0F, 4.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(442, 155).addBox(-1.50F, -0.50F, 4.80F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.008F))
            , PartPose.offsetAndRotation(-11.00F, -21.00F, 5.00F, 0.2443F, 0.2094F, 0.1745F));
        b_missile_pod_l.addOrReplaceChild("missile_tip_1", CubeListBuilder.create().texOffs(394, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(-1.80F, -7.30F, -1.00F));
        b_missile_pod_l.addOrReplaceChild("missile_tip_3", CubeListBuilder.create().texOffs(408, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(1.80F, -7.30F, -1.00F));
        b_missile_pod_l.addOrReplaceChild("missile_tip_5", CubeListBuilder.create().texOffs(422, 155).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.00F, -7.30F, 2.30F));
        PartDefinition b_shoulder_r = b_torso.addOrReplaceChild("shoulder_r", CubeListBuilder.create()
            .texOffs(451, 155).addBox(-1.50F, -3.50F, -4.00F, 7.0F, 7.0F, 8.0F)
            .texOffs(0, 171).addBox(-0.50F, -5.00F, -6.00F, 10.0F, 6.0F, 12.0F, new CubeDeformation(-0.015F))
            .texOffs(45, 171).addBox(1.50F, -7.00F, -5.00F, 8.0F, 3.0F, 10.0F, new CubeDeformation(-0.022F))
            .texOffs(82, 171).addBox(5.50F, -6.00F, -6.00F, 2.0F, 8.0F, 12.0F, new CubeDeformation(-0.029F))
            .texOffs(111, 171).addBox(2.50F, -8.00F, -4.00F, 4.0F, 2.0F, 8.0F, new CubeDeformation(-0.036F))
            .texOffs(136, 171).addBox(0.50F, -4.50F, -7.50F, 8.0F, 5.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(157, 171).addBox(0.50F, -4.50F, 5.50F, 8.0F, 5.0F, 2.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(16.00F, -17.00F, 0.00F));
        PartDefinition b_upper_arm_r = b_shoulder_r.addOrReplaceChild("upper_arm_r", CubeListBuilder.create()
            .texOffs(178, 171).addBox(-2.00F, -1.00F, -2.00F, 4.0F, 16.0F, 4.0F)
            .texOffs(195, 171).addBox(-4.00F, -0.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.015F))
            .texOffs(228, 171).addBox(-4.50F, -0.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(259, 171).addBox(-3.50F, 2.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.022F))
            .texOffs(288, 171).addBox(-4.00F, 5.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.029F))
            .texOffs(321, 171).addBox(-4.50F, 6.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(352, 171).addBox(-3.50F, 8.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.036F))
            .texOffs(381, 171).addBox(-4.00F, 11.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(414, 171).addBox(-4.50F, 12.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            , PartPose.offset(5.00F, 5.00F, 0.00F));
        PartDefinition b_forearm_r = b_upper_arm_r.addOrReplaceChild("forearm_r", CubeListBuilder.create()
            .texOffs(445, 171).addBox(-3.00F, -2.00F, -3.50F, 6.0F, 4.0F, 7.0F)
            .texOffs(472, 171).addBox(2.80F, -1.50F, -1.50F, 1.0F, 3.0F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(481, 171).addBox(-2.50F, -0.00F, -2.50F, 5.0F, 14.0F, 5.0F, new CubeDeformation(-0.022F))
            .texOffs(0, 192).addBox(-4.00F, 2.00F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.029F))
            .texOffs(33, 192).addBox(-4.00F, 4.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.036F))
            .texOffs(66, 192).addBox(-4.50F, 5.00F, -4.50F, 9.0F, 1.0F, 9.0F, new CubeDeformation(-0.02F))
            .texOffs(103, 192).addBox(-4.50F, 7.00F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.008F))
            .texOffs(140, 192).addBox(-4.50F, 9.50F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.015F))
            .texOffs(177, 192).addBox(-4.50F, 12.00F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.022F))
            .texOffs(214, 192).addBox(-5.00F, 12.50F, -5.00F, 10.0F, 1.0F, 10.0F, new CubeDeformation(-0.02F))
            .texOffs(255, 192).addBox(4.10F, 6.00F, -1.00F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.029F))
            , PartPose.offset(0.00F, 15.00F, 0.00F));
        PartDefinition b_hand_r = b_forearm_r.addOrReplaceChild("hand_r", CubeListBuilder.create()
            .texOffs(262, 192).addBox(-4.00F, -0.00F, -4.00F, 8.0F, 4.0F, 8.0F)
            .texOffs(295, 192).addBox(2.80F, -0.00F, -3.50F, 2.0F, 4.0F, 7.0F, new CubeDeformation(-0.015F))
            .texOffs(314, 192).addBox(0.00F, 3.20F, -3.80F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(325, 192).addBox(-0.50F, 6.20F, -3.80F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(334, 192).addBox(0.00F, 3.20F, -1.00F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(345, 192).addBox(-0.50F, 6.20F, -1.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(354, 192).addBox(0.00F, 3.20F, 1.80F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(365, 192).addBox(-0.50F, 6.20F, 1.80F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(374, 192).addBox(-4.00F, 2.50F, -3.50F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(385, 192).addBox(-2.50F, 5.50F, -3.50F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.036F))
            , PartPose.offset(0.00F, 14.00F, 0.00F));
        PartDefinition b_vfx_fist_r = b_hand_r.addOrReplaceChild("vfx_fist_r", CubeListBuilder.create()
            .texOffs(196, 268).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.49F))
            , PartPose.offset(0.00F, 6.00F, 0.00F));
        PartDefinition b_shoulder_l = b_torso.addOrReplaceChild("shoulder_l", CubeListBuilder.create()
            .texOffs(394, 192).addBox(-5.50F, -3.50F, -4.00F, 7.0F, 7.0F, 8.0F)
            .texOffs(425, 192).addBox(-9.50F, -5.00F, -6.00F, 10.0F, 6.0F, 12.0F, new CubeDeformation(-0.015F))
            .texOffs(470, 192).addBox(-9.50F, -7.00F, -5.00F, 8.0F, 3.0F, 10.0F, new CubeDeformation(-0.022F))
            .texOffs(0, 211).addBox(-7.50F, -6.00F, -6.00F, 2.0F, 8.0F, 12.0F, new CubeDeformation(-0.029F))
            .texOffs(29, 211).addBox(-6.50F, -8.00F, -4.00F, 4.0F, 2.0F, 8.0F, new CubeDeformation(-0.036F))
            .texOffs(54, 211).addBox(-8.50F, -4.50F, -7.50F, 8.0F, 5.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(75, 211).addBox(-8.50F, -4.50F, 5.50F, 8.0F, 5.0F, 2.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(-16.00F, -17.00F, 0.00F));
        PartDefinition b_upper_arm_l = b_shoulder_l.addOrReplaceChild("upper_arm_l", CubeListBuilder.create()
            .texOffs(96, 211).addBox(-2.00F, -1.00F, -2.00F, 4.0F, 16.0F, 4.0F)
            .texOffs(113, 211).addBox(-4.00F, -0.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.015F))
            .texOffs(146, 211).addBox(-4.50F, -0.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(177, 211).addBox(-3.50F, 2.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.022F))
            .texOffs(206, 211).addBox(-4.00F, 5.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.029F))
            .texOffs(239, 211).addBox(-4.50F, 6.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            .texOffs(270, 211).addBox(-3.50F, 8.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.036F))
            .texOffs(299, 211).addBox(-4.00F, 11.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(332, 211).addBox(-4.50F, 12.00F, -3.00F, 9.0F, 1.0F, 6.0F, new CubeDeformation(-0.02F))
            , PartPose.offset(-5.00F, 5.00F, 0.00F));
        PartDefinition b_forearm_l = b_upper_arm_l.addOrReplaceChild("forearm_l", CubeListBuilder.create()
            .texOffs(363, 211).addBox(-3.00F, -2.00F, -3.50F, 6.0F, 4.0F, 7.0F)
            .texOffs(390, 211).addBox(-3.80F, -1.50F, -1.50F, 1.0F, 3.0F, 3.0F, new CubeDeformation(-0.015F))
            .texOffs(399, 211).addBox(-2.50F, -0.00F, -2.50F, 5.0F, 14.0F, 5.0F, new CubeDeformation(-0.022F))
            .texOffs(420, 211).addBox(-4.00F, 2.00F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.029F))
            .texOffs(453, 211).addBox(-4.00F, 4.50F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.036F))
            .texOffs(0, 232).addBox(-4.50F, 5.00F, -4.50F, 9.0F, 1.0F, 9.0F, new CubeDeformation(-0.02F))
            .texOffs(37, 232).addBox(-4.50F, 7.00F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.008F))
            .texOffs(74, 232).addBox(-4.50F, 9.50F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.015F))
            .texOffs(111, 232).addBox(-4.50F, 12.00F, -4.50F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.022F))
            .texOffs(148, 232).addBox(-5.00F, 12.50F, -5.00F, 10.0F, 1.0F, 10.0F, new CubeDeformation(-0.02F))
            .texOffs(189, 232).addBox(-5.10F, 6.00F, -1.00F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.029F))
            , PartPose.offset(0.00F, 15.00F, 0.00F));
        PartDefinition b_hand_l = b_forearm_l.addOrReplaceChild("hand_l", CubeListBuilder.create()
            .texOffs(196, 232).addBox(-4.00F, -0.00F, -4.00F, 8.0F, 4.0F, 8.0F)
            .texOffs(229, 232).addBox(-4.80F, -0.00F, -3.50F, 2.0F, 4.0F, 7.0F, new CubeDeformation(-0.015F))
            .texOffs(248, 232).addBox(-3.00F, 3.20F, -3.80F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(259, 232).addBox(-1.50F, 6.20F, -3.80F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(268, 232).addBox(-3.00F, 3.20F, -1.00F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.036F))
            .texOffs(279, 232).addBox(-1.50F, 6.20F, -1.00F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.008F))
            .texOffs(288, 232).addBox(-3.00F, 3.20F, 1.80F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
            .texOffs(299, 232).addBox(-1.50F, 6.20F, 1.80F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.022F))
            .texOffs(308, 232).addBox(1.00F, 2.50F, -3.50F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.029F))
            .texOffs(319, 232).addBox(0.50F, 5.50F, -3.50F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.036F))
            , PartPose.offset(0.00F, 14.00F, 0.00F));
        PartDefinition b_vfx_fist_l = b_hand_l.addOrReplaceChild("vfx_fist_l", CubeListBuilder.create()
            .texOffs(201, 268).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.49F))
            , PartPose.offset(0.00F, 6.00F, 0.00F));
        PartDefinition b_vfx_eye_anchor = b_torso.addOrReplaceChild("vfx_eye_anchor", CubeListBuilder.create()
            .texOffs(206, 268).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.49F))
            , PartPose.offset(0.00F, -14.00F, -14.00F));
        PartDefinition b_vfx_chest_anchor = b_torso.addOrReplaceChild("vfx_chest_anchor", CubeListBuilder.create()
            .texOffs(211, 268).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.49F))
            , PartPose.offset(0.00F, -10.00F, 0.00F));
        PartDefinition b_hip_r = b_pelvis.addOrReplaceChild("hip_r", CubeListBuilder.create()
            .texOffs(328, 232).addBox(-2.50F, -0.50F, -2.50F, 5.0F, 12.0F, 5.0F)
            .texOffs(349, 232).addBox(-4.00F, 0.50F, -4.00F, 8.0F, 4.0F, 8.0F, new CubeDeformation(-0.015F))
            .texOffs(382, 232).addBox(-3.50F, 5.00F, -3.50F, 7.0F, 3.0F, 7.0F, new CubeDeformation(-0.022F))
            .texOffs(411, 232).addBox(-3.50F, 8.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.029F))
            .texOffs(440, 232).addBox(3.20F, 1.50F, -2.50F, 2.0F, 7.0F, 5.0F, new CubeDeformation(-0.036F))
            .texOffs(455, 232).addBox(4.30F, 2.50F, -1.50F, 1.0F, 5.0F, 3.0F, new CubeDeformation(-0.008F))
            , PartPose.offset(10.00F, 1.00F, 0.00F));
        PartDefinition b_knee_r = b_hip_r.addOrReplaceChild("knee_r", CubeListBuilder.create()
            .texOffs(464, 232).addBox(-3.50F, -2.00F, -5.00F, 7.0F, 5.0F, 3.0F)
            .texOffs(485, 232).addBox(-2.00F, -1.00F, -5.70F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(0, 250).addBox(-2.50F, -0.00F, -2.50F, 5.0F, 10.0F, 5.0F, new CubeDeformation(-0.022F))
            .texOffs(21, 250).addBox(-3.50F, 1.00F, -4.00F, 7.0F, 7.0F, 3.0F, new CubeDeformation(-0.029F))
            .texOffs(42, 250).addBox(-3.00F, 1.00F, 1.00F, 6.0F, 7.0F, 3.0F, new CubeDeformation(-0.036F))
            .texOffs(61, 250).addBox(-4.00F, 7.00F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(94, 250).addBox(-1.00F, 0.50F, 3.20F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(0.00F, 11.00F, 0.00F));
        PartDefinition b_foot_r = b_knee_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
            .texOffs(103, 250).addBox(-4.00F, -0.00F, -4.00F, 8.0F, 4.0F, 8.0F)
            .texOffs(136, 250).addBox(-3.00F, -0.00F, -4.50F, 6.0F, 3.0F, 9.0F, new CubeDeformation(-0.02F))
            .texOffs(167, 250).addBox(-2.00F, 1.00F, -8.00F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.015F))
            .texOffs(184, 250).addBox(-1.50F, 1.00F, -7.50F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(197, 250).addBox(2.50F, 2.00F, -6.50F, 3.0F, 2.0F, 4.0F, new CubeDeformation(-0.029F))
            .texOffs(212, 250).addBox(-5.00F, 2.00F, -6.50F, 3.0F, 2.0F, 4.0F, new CubeDeformation(-0.036F))
            .texOffs(227, 250).addBox(-2.00F, 1.00F, 4.00F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.008F))
            .texOffs(244, 250).addBox(-1.50F, 1.00F, 4.50F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(0.00F, 10.00F, 0.00F));
        PartDefinition b_hip_l = b_pelvis.addOrReplaceChild("hip_l", CubeListBuilder.create()
            .texOffs(257, 250).addBox(-2.50F, -0.50F, -2.50F, 5.0F, 12.0F, 5.0F)
            .texOffs(278, 250).addBox(-4.00F, 0.50F, -4.00F, 8.0F, 4.0F, 8.0F, new CubeDeformation(-0.015F))
            .texOffs(311, 250).addBox(-3.50F, 5.00F, -3.50F, 7.0F, 3.0F, 7.0F, new CubeDeformation(-0.022F))
            .texOffs(340, 250).addBox(-3.50F, 8.50F, -3.50F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.029F))
            .texOffs(369, 250).addBox(-5.20F, 1.50F, -2.50F, 2.0F, 7.0F, 5.0F, new CubeDeformation(-0.036F))
            .texOffs(384, 250).addBox(-5.30F, 2.50F, -1.50F, 1.0F, 5.0F, 3.0F, new CubeDeformation(-0.008F))
            , PartPose.offset(-10.00F, 1.00F, 0.00F));
        PartDefinition b_knee_l = b_hip_l.addOrReplaceChild("knee_l", CubeListBuilder.create()
            .texOffs(393, 250).addBox(-3.50F, -2.00F, -5.00F, 7.0F, 5.0F, 3.0F)
            .texOffs(414, 250).addBox(-2.00F, -1.00F, -5.70F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.015F))
            .texOffs(425, 250).addBox(-2.50F, -0.00F, -2.50F, 5.0F, 10.0F, 5.0F, new CubeDeformation(-0.022F))
            .texOffs(446, 250).addBox(-3.50F, 1.00F, -4.00F, 7.0F, 7.0F, 3.0F, new CubeDeformation(-0.029F))
            .texOffs(467, 250).addBox(-3.00F, 1.00F, 1.00F, 6.0F, 7.0F, 3.0F, new CubeDeformation(-0.036F))
            .texOffs(0, 268).addBox(-4.00F, 7.00F, -4.00F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.008F))
            .texOffs(33, 268).addBox(-1.00F, 0.50F, 3.20F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(0.00F, 11.00F, 0.00F));
        PartDefinition b_foot_l = b_knee_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
            .texOffs(42, 268).addBox(-4.00F, -0.00F, -4.00F, 8.0F, 4.0F, 8.0F)
            .texOffs(75, 268).addBox(-3.00F, -0.00F, -4.50F, 6.0F, 3.0F, 9.0F, new CubeDeformation(-0.02F))
            .texOffs(106, 268).addBox(-2.00F, 1.00F, -8.00F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.015F))
            .texOffs(123, 268).addBox(-1.50F, 1.00F, -7.50F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.022F))
            .texOffs(136, 268).addBox(-2.50F, 2.00F, -6.50F, 3.0F, 2.0F, 4.0F, new CubeDeformation(-0.029F))
            .texOffs(151, 268).addBox(2.00F, 2.00F, -6.50F, 3.0F, 2.0F, 4.0F, new CubeDeformation(-0.036F))
            .texOffs(166, 268).addBox(-2.00F, 1.00F, 4.00F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.008F))
            .texOffs(183, 268).addBox(-1.50F, 1.00F, 4.50F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.015F))
            , PartPose.offset(0.00F, 10.00F, 0.00F));
        PartDefinition b_vfx_ground_ring = b_root.addOrReplaceChild("vfx_ground_ring", CubeListBuilder.create()
            .texOffs(216, 268).addBox(-0.50F, -0.50F, -0.50F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.49F))
            , PartPose.offset(0.00F, -1.00F, 0.00F));
        return LayerDefinition.create(meshdefinition, 512, 512);
    }
}
