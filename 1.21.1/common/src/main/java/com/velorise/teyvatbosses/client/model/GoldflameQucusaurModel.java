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

/**
 * Shared 237-cube, 33-bone sculpted wyvern model for Goldflame Qucusaur Tyrant (Boss)
 * and Goldflame Qucusaur Mount (with toggleable {@code saddle} bone).
 * Converted 1:1 from {@code models/goldflame_qucusaur.bbmodel} with exact Mojang
 * {@code (-rx, +ry, -rz)} cube rotations and repacked 1024x1024 box-UV atlas.
 */
public class GoldflameQucusaurModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("goldflame_qucusaur"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_EMISSIVE =
            new ModelLayerLocation(TeyvatBossesConstants.id("goldflame_qucusaur"), "emissive");

    public static final String[] ALL_BONES = {
            "root", "pelvis", "torso", "chest", "neck_1", "neck_2", "neck_3", "neck_4", "head", "jaw", "crest_horn", "crest_mid", "crest_tip", "wing_l_upper", "wing_l_forearm", "wing_l_fan", "wing_r_upper", "wing_r_forearm", "wing_r_fan", "saddle", "leg_l_thigh", "leg_l_shin", "leg_l_meta", "leg_l_talon", "leg_r_thigh", "leg_r_shin", "leg_r_meta", "leg_r_talon", "tail_1", "tail_2", "tail_3", "tail_4", "tail_5"
    };

    private final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public GoldflameQucusaurModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        collectBones("root", this.root);
    }

    private void collectBones(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
        for (String childName : ALL_BONES) {
            if (part.hasChild(childName)) {
                collectBones(childName, part.getChild(childName));
            }
        }
    }

    public ModelPart getBone(String name) { return bones.get(name); }
    public PartPose getInitialPose(String name) { return initialPoses.get(name); }

    public void resetPose(boolean showSaddle) {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            PartPose pose = initialPoses.get(e.getKey());
            if (pose != null) e.getValue().loadPose(pose);
            e.getValue().visible = !"saddle".equals(e.getKey()) || showSaddle;
        }
    }

    public void copyPoseFrom(GoldflameQucusaurModel<?> other) {
        for (String name : ALL_BONES) {
            ModelPart dst = bones.get(name);
            ModelPart src = other.getBone(name);
            if (dst != null && src != null) {
                dst.copyFrom(src);
                dst.visible = src.visible;
            }
        }
    }

    public static LayerDefinition createBodyLayer() { return createLayer(0.0F); }
    public static LayerDefinition createEmissiveLayer() { return createLayer(0.055F); }

    private static LayerDefinition createLayer(float inflate) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = build_root(rootDef, inflate);
        PartDefinition b_pelvis = build_pelvis(b_root, inflate);
        PartDefinition b_torso = build_torso(b_pelvis, inflate);
        PartDefinition b_chest = build_chest(b_torso, inflate);
        PartDefinition b_neck_1 = build_neck_1(b_chest, inflate);
        PartDefinition b_neck_2 = build_neck_2(b_neck_1, inflate);
        PartDefinition b_neck_3 = build_neck_3(b_neck_2, inflate);
        PartDefinition b_neck_4 = build_neck_4(b_neck_3, inflate);
        PartDefinition b_head = build_head(b_neck_4, inflate);
        PartDefinition b_jaw = build_jaw(b_head, inflate);
        PartDefinition b_crest_horn = build_crest_horn(b_head, inflate);
        PartDefinition b_crest_mid = build_crest_mid(b_crest_horn, inflate);
        PartDefinition b_crest_tip = build_crest_tip(b_crest_mid, inflate);
        PartDefinition b_wing_l_upper = build_wing_l_upper(b_chest, inflate);
        PartDefinition b_wing_l_forearm = build_wing_l_forearm(b_wing_l_upper, inflate);
        PartDefinition b_wing_l_fan = build_wing_l_fan(b_wing_l_forearm, inflate);
        PartDefinition b_wing_r_upper = build_wing_r_upper(b_chest, inflate);
        PartDefinition b_wing_r_forearm = build_wing_r_forearm(b_wing_r_upper, inflate);
        PartDefinition b_wing_r_fan = build_wing_r_fan(b_wing_r_forearm, inflate);
        PartDefinition b_saddle = build_saddle(b_torso, inflate);
        PartDefinition b_leg_l_thigh = build_leg_l_thigh(b_pelvis, inflate);
        PartDefinition b_leg_l_shin = build_leg_l_shin(b_leg_l_thigh, inflate);
        PartDefinition b_leg_l_meta = build_leg_l_meta(b_leg_l_shin, inflate);
        PartDefinition b_leg_l_talon = build_leg_l_talon(b_leg_l_meta, inflate);
        PartDefinition b_leg_r_thigh = build_leg_r_thigh(b_pelvis, inflate);
        PartDefinition b_leg_r_shin = build_leg_r_shin(b_leg_r_thigh, inflate);
        PartDefinition b_leg_r_meta = build_leg_r_meta(b_leg_r_shin, inflate);
        PartDefinition b_leg_r_talon = build_leg_r_talon(b_leg_r_meta, inflate);
        PartDefinition b_tail_1 = build_tail_1(b_pelvis, inflate);
        PartDefinition b_tail_2 = build_tail_2(b_tail_1, inflate);
        PartDefinition b_tail_3 = build_tail_3(b_tail_2, inflate);
        PartDefinition b_tail_4 = build_tail_4(b_tail_3, inflate);
        PartDefinition b_tail_5 = build_tail_5(b_tail_4, inflate);
        return LayerDefinition.create(mesh, 1024, 1024);
    }

    private static PartDefinition build_root(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("root", CubeListBuilder.create()
                , PartPose.offset(0.000F, 24.000F, 0.000F));
        return part;
    }
    private static PartDefinition build_pelvis(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("pelvis", CubeListBuilder.create()
                , PartPose.offset(0.000F, -24.000F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(317, 189).addBox(-7.290F, -4.960F, -2.356F, 14.580F, 9.920F, 4.712F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 0.100F, 2.250F, 0.3016F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(197, 223).addBox(-5.265F, -2.604F, -2.356F, 10.530F, 5.208F, 4.712F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -2.690F, 2.250F, 0.3016F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(509, 223).addBox(-5.184F, -2.108F, -2.356F, 10.368F, 4.216F, 4.712F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 3.324F, 2.250F, 0.3016F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(605, 208).addBox(-3.564F, -3.472F, -2.356F, 7.128F, 6.944F, 4.712F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-4.698F, 0.410F, 2.250F, 0.3016F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(631, 208).addBox(-3.564F, -3.472F, -2.356F, 7.128F, 6.944F, 4.712F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(4.698F, 0.410F, 2.250F, 0.3016F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(429, 223).addBox(-1.150F, -2.700F, -2.200F, 2.300F, 5.400F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -6.954F, 3.450F, 0.5934F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(50, 169).addBox(-8.370F, -5.840F, -2.693F, 16.740F, 11.680F, 5.386F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.600F, -2.500F, 0.3805F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(510, 208).addBox(-6.045F, -3.066F, -2.693F, 12.090F, 6.132F, 5.386F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -4.885F, -2.500F, 0.3805F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(828, 208).addBox(-5.952F, -2.482F, -2.693F, 11.904F, 4.964F, 5.386F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 2.196F, -2.500F, 0.3805F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(700, 189).addBox(-4.092F, -4.088F, -2.693F, 8.184F, 8.176F, 5.386F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.394F, -1.235F, -2.500F, 0.3805F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(730, 189).addBox(-4.092F, -4.088F, -2.693F, 8.184F, 8.176F, 5.386F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(5.394F, -1.235F, -2.500F, 0.3805F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(445, 223).addBox(-1.150F, -2.700F, -2.200F, 2.300F, 5.400F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -9.666F, -1.300F, 0.5934F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_torso(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("torso", CubeListBuilder.create()
                , PartPose.offset(0.000F, -6.000F, -12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(602, 145).addBox(-9.270F, -6.560F, -2.773F, 18.540F, 13.120F, 5.546F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 2.200F, 4.500F, 0.4475F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(89, 208).addBox(-6.695F, -3.444F, -2.773F, 13.390F, 6.888F, 5.546F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -1.490F, 4.500F, 0.4475F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(470, 208).addBox(-6.592F, -2.788F, -2.773F, 13.184F, 5.576F, 5.546F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 6.464F, 4.500F, 0.4475F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(397, 189).addBox(-4.532F, -4.592F, -2.773F, 9.064F, 9.184F, 5.546F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.974F, 2.610F, 4.500F, 0.4475F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(429, 189).addBox(-4.532F, -4.592F, -2.773F, 9.064F, 9.184F, 5.546F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(5.974F, 2.610F, 4.500F, 0.4475F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(461, 223).addBox(-1.150F, -2.700F, -2.200F, 2.300F, 5.400F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -6.694F, 5.700F, 0.5934F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(100, 145).addBox(-10.080F, -7.200F, -3.042F, 20.160F, 14.400F, 6.084F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -0.300F, -0.750F, 0.4416F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(623, 189).addBox(-7.280F, -3.780F, -3.042F, 14.560F, 7.560F, 6.084F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -4.350F, -0.750F, 0.4416F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(46, 208).addBox(-7.168F, -3.060F, -3.042F, 14.336F, 6.120F, 6.084F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 4.380F, -0.750F, 0.4416F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(973, 169).addBox(-4.928F, -5.040F, -3.042F, 9.856F, 10.080F, 6.084F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-6.496F, 0.150F, -0.750F, 0.4416F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(2, 189).addBox(-4.928F, -5.040F, -3.042F, 9.856F, 10.080F, 6.084F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(6.496F, 0.150F, -0.750F, 0.4416F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(477, 223).addBox(-1.150F, -2.700F, -2.200F, 2.300F, 5.400F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -9.930F, 0.450F, 0.5934F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(560, 117).addBox(-10.800F, -7.840F, -3.086F, 21.600F, 15.680F, 6.172F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -3.000F, -6.250F, 0.4709F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(142, 189).addBox(-7.800F, -4.116F, -3.086F, 15.600F, 8.232F, 6.172F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -7.410F, -6.250F, 0.4709F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(928, 189).addBox(-7.680F, -3.332F, -3.086F, 15.360F, 6.664F, 6.172F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 2.096F, -6.250F, 0.4709F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(571, 169).addBox(-5.280F, -5.488F, -3.086F, 10.560F, 10.976F, 6.172F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-6.960F, -2.510F, -6.250F, 0.4709F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(607, 169).addBox(-5.280F, -5.488F, -3.086F, 10.560F, 10.976F, 6.172F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(6.960F, -2.510F, -6.250F, 0.4709F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(438, 208).addBox(-1.150F, -4.000F, -2.200F, 2.300F, 8.000F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -14.016F, -5.050F, 0.5934F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_chest(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("chest", CubeListBuilder.create()
                , PartPose.offset(0.000F, -6.000F, -13.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(618, 117).addBox(-10.620F, -7.840F, -3.000F, 21.240F, 15.680F, 6.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 0.400F, 1.250F, 0.4114F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(188, 189).addBox(-7.670F, -4.116F, -3.000F, 15.340F, 8.232F, 6.000F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -4.010F, 1.250F, 0.4114F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(974, 189).addBox(-7.552F, -3.332F, -3.000F, 15.104F, 6.664F, 6.000F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 5.496F, 1.250F, 0.4114F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(903, 169).addBox(-5.192F, -5.488F, -3.000F, 10.384F, 10.976F, 6.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-6.844F, 0.890F, 1.250F, 0.4114F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(938, 169).addBox(-5.192F, -5.488F, -3.000F, 10.384F, 10.976F, 6.000F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(6.844F, 0.890F, 1.250F, 0.4114F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(454, 208).addBox(-1.150F, -4.000F, -2.200F, 2.300F, 8.000F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -10.616F, 2.450F, 0.5934F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(653, 145).addBox(-8.910F, -6.760F, -2.516F, 17.820F, 13.520F, 5.032F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.700F, -3.850F, 0.3658F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(129, 208).addBox(-6.435F, -3.549F, -2.517F, 12.870F, 7.098F, 5.034F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -5.502F, -3.850F, 0.3660F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(790, 208).addBox(-6.336F, -2.873F, -2.516F, 12.672F, 5.746F, 5.032F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, 2.694F, -3.850F, 0.3658F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(523, 189).addBox(-4.356F, -4.732F, -2.517F, 8.712F, 9.464F, 5.034F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.742F, -1.278F, -3.850F, 0.3660F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(553, 189).addBox(-4.356F, -4.732F, -2.517F, 8.712F, 9.464F, 5.034F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(5.742F, -1.278F, -3.850F, 0.3660F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(493, 223).addBox(-1.150F, -2.700F, -2.200F, 2.300F, 5.400F, 4.400F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, -10.824F, -2.650F, 0.5934F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(680, 208).addBox(-1.800F, -2.600F, -3.100F, 3.600F, 5.200F, 6.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-6.717F, -3.680F, -5.000F, 0.4189F, 0.5760F, 0.3840F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(702, 208).addBox(-1.800F, -2.600F, -3.100F, 3.600F, 5.200F, 6.200F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-3.850F, -4.855F, -5.000F, 0.4189F, 0.2932F, 0.1955F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(724, 208).addBox(-1.800F, -2.600F, -3.100F, 3.600F, 5.200F, 6.200F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -5.300F, -5.000F, 0.4189F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(746, 208).addBox(-1.800F, -2.600F, -3.100F, 3.600F, 5.200F, 6.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(3.850F, -4.855F, -5.000F, 0.4189F, -0.2932F, -0.1955F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(768, 208).addBox(-1.800F, -2.600F, -3.100F, 3.600F, 5.200F, 6.200F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(6.717F, -3.680F, -5.000F, 0.4189F, -0.5760F, -0.3840F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(108, 189).addBox(-1.300F, -5.750F, -2.250F, 2.600F, 11.500F, 4.500F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-10.600F, -9.680F, -1.200F, 0.4887F, -0.2094F, 0.3840F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(872, 189).addBox(-1.300F, -4.650F, -2.000F, 2.600F, 9.300F, 4.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-12.100F, -7.776F, 2.400F, 0.6283F, -0.3142F, 0.5585F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(167, 223).addBox(-1.300F, -3.550F, -1.750F, 2.600F, 7.100F, 3.500F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-13.600F, -5.872F, 6.000F, 0.7679F, -0.4189F, 0.7330F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(125, 189).addBox(-1.300F, -5.750F, -2.250F, 2.600F, 11.500F, 4.500F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(10.600F, -9.680F, -1.200F, 0.4887F, 0.2094F, -0.3840F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(888, 189).addBox(-1.300F, -4.650F, -2.000F, 2.600F, 9.300F, 4.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(12.100F, -7.776F, 2.400F, 0.6283F, 0.3142F, -0.5585F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(182, 223).addBox(-1.300F, -3.550F, -1.750F, 2.600F, 7.100F, 3.500F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(13.600F, -5.872F, 6.000F, 0.7679F, 0.4189F, -0.7330F));
        return part;
    }
    private static PartDefinition build_neck_1(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("neck_1", CubeListBuilder.create()
                , PartPose.offset(0.000F, -3.500F, -6.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(701, 145).addBox(-6.000F, -5.400F, -3.856F, 12.000F, 10.800F, 7.712F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -2.100F, -2.000F, 0.8067F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(167, 208).addBox(-4.320F, -2.160F, -3.856F, 8.640F, 4.320F, 7.712F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.924F, -2.800F, 0.8067F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(24, 223).addBox(-1.500F, -2.100F, -3.200F, 3.000F, 4.200F, 6.400F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -5.610F, 0.800F, -0.5585F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(726, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.400F, -2.100F, -0.500F, -0.3142F, -0.3142F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(743, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(5.400F, -2.100F, -0.500F, -0.3142F, 0.3142F, -0.0000F));
        return part;
    }
    private static PartDefinition build_neck_2(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("neck_2", CubeListBuilder.create()
                , PartPose.offset(0.000F, -4.500F, -4.300F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(794, 169).addBox(-5.200F, -4.800F, -3.382F, 10.400F, 9.600F, 6.764F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -2.500F, -1.500F, 1.0304F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(865, 208).addBox(-3.744F, -1.920F, -3.382F, 7.488F, 3.840F, 6.764F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.188F, -2.300F, 1.0304F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(45, 223).addBox(-1.500F, -2.100F, -3.200F, 3.000F, 4.200F, 6.400F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -5.620F, 1.300F, -0.5585F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(760, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-4.680F, -2.500F, 0.000F, -0.3142F, -0.3142F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(777, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(4.680F, -2.500F, 0.000F, -0.3142F, 0.3142F, -0.0000F));
        return part;
    }
    private static PartDefinition build_neck_3(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("neck_3", CubeListBuilder.create()
                , PartPose.offset(0.000F, -5.000F, -3.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(74, 189).addBox(-4.700F, -4.400F, -3.137F, 9.400F, 8.800F, 6.274F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -2.250F, -1.500F, 0.9828F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(230, 223).addBox(-3.384F, -1.760F, -3.137F, 6.768F, 3.520F, 6.274F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.214F, -2.300F, 0.9828F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(66, 223).addBox(-1.500F, -2.100F, -3.200F, 3.000F, 4.200F, 6.400F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -5.110F, 1.300F, -0.5585F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(794, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-4.230F, -2.250F, 0.000F, -0.3142F, -0.3142F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(811, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(4.230F, -2.250F, 0.000F, -0.3142F, 0.3142F, -0.0000F));
        return part;
    }
    private static PartDefinition build_neck_4(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("neck_4", CubeListBuilder.create()
                , PartPose.offset(0.000F, -4.500F, -3.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(237, 208).addBox(-4.300F, -4.000F, -2.341F, 8.600F, 8.000F, 4.682F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.500F, -1.350F, 0.8379F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(943, 223).addBox(-3.096F, -1.600F, -2.341F, 6.192F, 3.200F, 4.682F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.740F, -2.150F, 0.8379F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(87, 223).addBox(-1.500F, -2.100F, -3.200F, 3.000F, 4.200F, 6.400F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.100F, 1.450F, -0.5585F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(828, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-3.870F, -1.500F, 0.150F, -0.3142F, -0.3142F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(845, 223).addBox(-1.000F, -1.700F, -2.700F, 2.000F, 3.400F, 5.400F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(3.870F, -1.500F, 0.150F, -0.3142F, 0.3142F, -0.0000F));
        return part;
    }
    private static PartDefinition build_head(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(194, 236).addBox(-3.100F, 1.400F, -11.900F, 1.000F, 2.200F, 1.600F, new CubeDeformation(inflate + 0.002F))
                .texOffs(202, 236).addBox(2.100F, 1.400F, -11.900F, 1.000F, 2.200F, 1.600F, new CubeDeformation(inflate + 0.006F))
                , PartPose.offset(0.000F, -3.000F, -2.700F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(456, 169).addBox(-4.600F, -3.800F, -5.100F, 9.200F, 7.600F, 10.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -0.700F, -2.500F, -0.1047F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(202, 208).addBox(-3.600F, -1.700F, -4.500F, 7.200F, 3.400F, 9.000F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -4.300F, -1.700F, -0.1745F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(667, 189).addBox(-3.400F, -2.600F, -4.100F, 6.800F, 5.200F, 8.200F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -0.400F, -9.100F, -0.2094F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(577, 208).addBox(-2.100F, -1.400F, -4.300F, 4.200F, 2.800F, 8.600F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -2.500F, -9.500F, -0.2443F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(259, 223).addBox(-2.400F, -2.100F, -2.800F, 4.800F, 4.200F, 5.600F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(0.000F, 0.900F, -14.500F, -0.4189F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(967, 223).addBox(-1.500F, -2.200F, -1.600F, 3.000F, 4.400F, 3.200F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(0.000F, 3.500F, -16.700F, -0.7330F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(282, 223).addBox(-1.200F, -1.000F, -3.600F, 2.400F, 2.000F, 7.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-4.500F, -3.300F, -4.500F, -0.2094F, 0.1745F, -0.2443F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(70, 236).addBox(-0.900F, -1.300F, -2.100F, 1.800F, 2.600F, 4.200F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-4.400F, -1.300F, -4.100F, -0.1396F, 0.1396F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(172, 236).addBox(-0.600F, -0.950F, -1.500F, 1.200F, 1.900F, 3.000F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-4.750F, -1.400F, -4.300F, -0.1396F, 0.1396F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(108, 223).addBox(-1.100F, -1.700F, -3.400F, 2.200F, 3.400F, 6.800F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-4.800F, 1.100F, -1.700F, -0.1047F, 0.2094F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(322, 208).addBox(-1.000F, -1.600F, -4.750F, 2.000F, 3.200F, 9.500F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-6.400F, -1.900F, 2.900F, -0.3142F, -0.4887F, 0.2094F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(818, 189).addBox(-0.900F, -1.500F, -5.250F, 1.800F, 3.000F, 10.500F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-6.800F, 0.300F, 3.500F, -0.1396F, -0.5934F, 0.1047F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(990, 208).addBox(-0.800F, -1.200F, -4.100F, 1.600F, 2.400F, 8.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-5.800F, 2.300F, 3.100F, 0.1047F, -0.4538F, -0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(304, 223).addBox(-1.200F, -1.000F, -3.600F, 2.400F, 2.000F, 7.200F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(4.500F, -3.300F, -4.500F, -0.2094F, -0.1745F, 0.2443F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(85, 236).addBox(-0.900F, -1.300F, -2.100F, 1.800F, 2.600F, 4.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(4.400F, -1.300F, -4.100F, -0.1396F, -0.1396F, -0.0000F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(183, 236).addBox(-0.600F, -0.950F, -1.500F, 1.200F, 1.900F, 3.000F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(4.750F, -1.400F, -4.300F, -0.1396F, -0.1396F, -0.0000F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(129, 223).addBox(-1.100F, -1.700F, -3.400F, 2.200F, 3.400F, 6.800F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(4.800F, 1.100F, -1.700F, -0.1047F, -0.2094F, -0.0000F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(347, 208).addBox(-1.000F, -1.600F, -4.750F, 2.000F, 3.200F, 9.500F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(6.400F, -1.900F, 2.900F, -0.3142F, 0.4887F, -0.2094F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(845, 189).addBox(-0.900F, -1.500F, -5.250F, 1.800F, 3.000F, 10.500F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(6.800F, 0.300F, 3.500F, -0.1396F, 0.5934F, -0.1047F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(2, 223).addBox(-0.800F, -1.200F, -4.100F, 1.600F, 2.400F, 8.200F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(5.800F, 2.300F, 3.100F, 0.1047F, 0.4538F, -0.0000F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(461, 189).addBox(-1.700F, -1.700F, -5.462F, 3.400F, 3.400F, 10.924F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-6.850F, -8.000F, 2.750F, 0.6955F, -0.6840F, -0.0000F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(266, 208).addBox(-1.100F, -1.100F, -5.199F, 2.200F, 2.200F, 10.398F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-10.850F, -15.100F, 9.500F, 0.7648F, -0.3681F, -0.0000F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(492, 189).addBox(-1.700F, -1.700F, -5.462F, 3.400F, 3.400F, 10.924F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(6.850F, -8.000F, 2.750F, 0.6955F, 0.6840F, -0.0000F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(294, 208).addBox(-1.100F, -1.100F, -5.199F, 2.200F, 2.200F, 10.398F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(10.850F, -15.100F, 9.500F, 0.7648F, 0.3681F, -0.0000F));
        return part;
    }
    private static PartDefinition build_jaw(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(547, 208).addBox(-2.100F, -1.300F, -10.800F, 4.200F, 1.400F, 9.800F, new CubeDeformation(inflate + 0.004F))
                , PartPose.offset(0.000F, 3.500F, -1.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(358, 189).addBox(-2.900F, -1.400F, -6.100F, 5.800F, 2.800F, 12.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 0.600F, -6.500F, -0.1047F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(2, 236).addBox(-2.000F, -1.200F, -2.300F, 4.000F, 2.400F, 4.600F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 1.600F, -12.100F, -0.2443F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_crest_horn(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("crest_horn", CubeListBuilder.create()
                , PartPose.offset(0.000F, -3.500F, -3.100F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(657, 208).addBox(-1.900F, -2.500F, -3.100F, 3.800F, 5.000F, 6.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.500F, 0.000F, 0.2094F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(372, 208).addBox(-1.700F, -2.700F, -3.400F, 3.400F, 5.400F, 6.800F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -5.600F, 1.200F, 0.3142F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_crest_mid(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("crest_mid", CubeListBuilder.create()
                , PartPose.offset(0.000F, -7.500F, 2.100F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(395, 208).addBox(-1.400F, -2.800F, -3.600F, 2.800F, 5.600F, 7.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -2.700F, 0.900F, 0.4538F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(418, 208).addBox(-1.200F, -2.900F, -3.300F, 2.400F, 5.800F, 6.600F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -7.100F, 3.500F, 0.5934F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_crest_tip(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("crest_tip", CubeListBuilder.create()
                , PartPose.offset(0.000F, -8.000F, 5.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(150, 223).addBox(-0.950F, -2.700F, -2.700F, 1.900F, 5.400F, 5.400F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -2.900F, 1.700F, 0.7679F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(930, 223).addBox(-0.700F, -2.300F, -1.900F, 1.400F, 4.600F, 3.800F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -6.100F, 5.300F, 0.9425F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_l_upper(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_l_upper", CubeListBuilder.create()
                , PartPose.offset(-11.000F, -2.500F, 0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(155, 145).addBox(-3.600F, -3.200F, -6.937F, 7.200F, 6.400F, 13.874F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-5.750F, -2.000F, -2.250F, 0.2925F, -1.9164F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(743, 145).addBox(-2.800F, -2.400F, -6.937F, 5.600F, 4.800F, 13.874F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-5.750F, -0.750F, 0.000F, 0.3304F, -1.8804F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(866, 145).addBox(-3.100F, -2.800F, -6.366F, 6.200F, 5.600F, 12.732F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-17.750F, -6.500F, -5.600F, 0.4035F, -1.7598F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(2, 117).addBox(-4.050F, -0.850F, -11.782F, 8.100F, 1.700F, 23.564F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-1.500F, 1.600F, 11.500F, -0.1363F, -0.1723F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(233, 189).addBox(-3.250F, -0.650F, -6.591F, 6.500F, 1.300F, 13.182F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-0.620F, -0.054F, 6.440F, -0.1286F, -0.1723F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(622, 81).addBox(-4.050F, -0.850F, -14.145F, 8.100F, 1.700F, 28.291F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-6.900F, -0.020F, 12.450F, -0.1262F, -0.1864F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(97, 169).addBox(-3.250F, -0.650F, -7.916F, 6.500F, 1.300F, 15.832F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-5.756F, -1.753F, 6.383F, -0.1199F, -0.1864F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(172, 81).addBox(-4.050F, -0.850F, -15.893F, 8.100F, 1.700F, 31.785F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-12.300F, -1.640F, 12.764F, -0.1236F, -0.2044F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(500, 145).addBox(-3.250F, -0.649F, -8.895F, 6.500F, 1.299F, 17.789F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-10.892F, -3.453F, 5.969F, -0.1180F, -0.2044F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(434, 43).addBox(-4.050F, -0.850F, -16.582F, 8.100F, 1.700F, 33.164F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-17.700F, -3.260F, 11.978F, -0.1295F, -0.2332F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(290, 145).addBox(-3.250F, -0.650F, -9.280F, 6.500F, 1.299F, 18.560F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-16.028F, -5.151F, 4.939F, -0.1241F, -0.2332F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(2, 81).addBox(-4.450F, -0.850F, -16.088F, 8.900F, 1.700F, 32.175F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-23.100F, -4.880F, 9.940F, -0.1447F, -0.2800F, -0.0000F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(396, 145).addBox(-3.250F, -0.650F, -9.001F, 6.500F, 1.300F, 18.003F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-21.164F, -6.851F, 3.207F, -0.1393F, -0.2801F, -0.0000F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(336, 81).addBox(-4.450F, -0.850F, -14.651F, 8.900F, 1.700F, 29.302F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-28.500F, -6.500F, 6.843F, -0.1714F, -0.3536F, -0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(946, 145).addBox(-3.250F, -0.650F, -8.196F, 6.500F, 1.300F, 16.392F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-26.300F, -8.550F, 0.884F, -0.1655F, -0.3536F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_l_forearm(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_l_forearm", CubeListBuilder.create()
                , PartPose.offset(-22.000F, -8.500F, -6.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(772, 81).addBox(-2.500F, -2.200F, -11.637F, 5.000F, 4.400F, 23.274F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-12.000F, -3.000F, 4.150F, 0.2166F, -1.1781F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(896, 208).addBox(-2.000F, -2.000F, -3.269F, 4.000F, 4.000F, 6.538F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-2.750F, -1.750F, -2.950F, 0.3924F, -2.7150F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(944, 208).addBox(-1.300F, -1.300F, -3.824F, 2.600F, 2.600F, 7.648F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-4.250F, 1.000F, -7.450F, -0.9018F, -2.8199F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(370, 223).addBox(-1.100F, -1.100F, -3.482F, 2.200F, 2.200F, 6.964F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-2.250F, 2.000F, -6.450F, -1.0383F, -2.9997F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(2, 2).addBox(-4.800F, -0.780F, -18.358F, 9.600F, 1.560F, 36.716F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-6.500F, 1.900F, 17.300F, -0.1311F, -0.2784F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(246, 117).addBox(-1.400F, -0.550F, -11.375F, 2.800F, 1.100F, 22.750F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-4.600F, 0.138F, 10.650F, -0.1267F, -0.2784F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(192, 2).addBox(-4.461F, -0.780F, -17.741F, 8.923F, 1.561F, 35.483F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-13.779F, 0.358F, 19.341F, -0.1281F, -0.3266F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(374, 2).addBox(-4.372F, -0.780F, -17.240F, 8.745F, 1.561F, 34.481F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-20.575F, -0.706F, 21.337F, -0.1241F, -0.3885F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(407, 117).addBox(-1.400F, -0.550F, -10.683F, 2.800F, 1.100F, 21.367F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-18.112F, -2.366F, 15.320F, -0.1194F, -0.3885F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(726, 2).addBox(-4.283F, -0.780F, -16.849F, 8.566F, 1.560F, 33.698F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-26.871F, -1.352F, 23.293F, -0.1190F, -0.4587F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(88, 43).addBox(-4.444F, -0.780F, -16.575F, 8.888F, 1.560F, 33.151F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-32.727F, -1.641F, 25.213F, -0.1129F, -0.5353F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(775, 117).addBox(-1.400F, -0.549F, -10.271F, 2.800F, 1.099F, 20.542F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-29.534F, -3.200F, 19.830F, -0.1080F, -0.5353F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_l_fan(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_l_fan", CubeListBuilder.create()
                , PartPose.offset(-23.000F, -5.000F, 9.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(494, 81).addBox(-1.800F, -1.500F, -13.588F, 3.600F, 3.000F, 27.176F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-10.250F, 1.750F, 7.500F, -0.1663F, -0.9310F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(262, 43).addBox(-4.355F, -0.780F, -16.432F, 8.711F, 1.560F, 32.863F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-15.213F, 3.370F, 18.106F, -0.1058F, -0.6163F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(519, 43).addBox(-4.117F, -0.780F, -16.424F, 8.234F, 1.560F, 32.848F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-20.403F, 3.619F, 19.974F, -0.0976F, -0.7002F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(824, 117).addBox(-1.400F, -0.550F, -10.178F, 2.800F, 1.100F, 20.356F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-16.400F, 2.161F, 15.224F, -0.0927F, -0.7002F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(604, 43).addBox(-4.028F, -0.780F, -16.557F, 8.056F, 1.560F, 33.115F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-25.378F, 4.046F, 21.825F, -0.0887F, -0.7850F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(900, 2).addBox(-3.939F, -0.780F, -16.832F, 7.877F, 1.560F, 33.665F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-30.216F, 4.593F, 23.665F, -0.0792F, -0.8690F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(675, 117).addBox(-1.401F, -0.549F, -10.432F, 2.801F, 1.099F, 20.864F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-25.347F, 3.236F, 19.549F, -0.0745F, -0.8690F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(552, 2).addBox(-3.850F, -0.780F, -17.246F, 7.700F, 1.560F, 34.492F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-35.000F, 5.200F, 25.500F, -0.0696F, -0.9505F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_r_upper(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_r_upper", CubeListBuilder.create()
                , PartPose.offset(11.000F, -2.500F, 0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(200, 145).addBox(-3.600F, -3.200F, -6.937F, 7.200F, 6.400F, 13.874F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(5.750F, -2.000F, -2.250F, 0.2925F, 1.9164F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(784, 145).addBox(-2.800F, -2.400F, -6.937F, 5.600F, 4.800F, 13.874F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(5.750F, -0.750F, 0.000F, 0.3304F, 1.8804F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(906, 145).addBox(-3.100F, -2.800F, -6.366F, 6.200F, 5.600F, 12.732F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(17.750F, -6.500F, -5.600F, 0.4035F, 1.7598F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(68, 117).addBox(-4.050F, -0.850F, -11.782F, 8.100F, 1.700F, 23.564F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(1.500F, 1.600F, 11.500F, -0.1363F, 0.1723F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(275, 189).addBox(-3.250F, -0.650F, -6.591F, 6.500F, 1.300F, 13.182F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(0.620F, -0.054F, 6.440F, -0.1286F, 0.1723F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(697, 81).addBox(-4.050F, -0.850F, -14.145F, 8.100F, 1.700F, 28.291F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(6.900F, -0.020F, 12.450F, -0.1262F, 0.1864F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(144, 169).addBox(-3.250F, -0.650F, -7.916F, 6.500F, 1.300F, 15.832F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(5.756F, -1.753F, 6.383F, -0.1199F, 0.1864F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(254, 81).addBox(-4.050F, -0.850F, -15.893F, 8.100F, 1.700F, 31.785F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(12.300F, -1.640F, 12.764F, -0.1236F, 0.2044F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(551, 145).addBox(-3.250F, -0.649F, -8.895F, 6.500F, 1.299F, 17.789F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(10.892F, -3.453F, 5.969F, -0.1180F, 0.2044F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(689, 43).addBox(-4.050F, -0.850F, -16.582F, 8.100F, 1.700F, 33.164F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(17.700F, -3.260F, 11.978F, -0.1295F, 0.2332F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(343, 145).addBox(-3.250F, -0.650F, -9.280F, 6.500F, 1.299F, 18.560F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(16.028F, -5.151F, 4.939F, -0.1241F, 0.2332F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(87, 81).addBox(-4.450F, -0.850F, -16.088F, 8.900F, 1.700F, 32.175F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(23.100F, -4.880F, 9.940F, -0.1447F, 0.2800F, -0.0000F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(448, 145).addBox(-3.250F, -0.650F, -9.001F, 6.500F, 1.300F, 18.003F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(21.164F, -6.851F, 3.207F, -0.1393F, 0.2801F, -0.0000F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(415, 81).addBox(-4.450F, -0.850F, -14.651F, 8.900F, 1.700F, 29.302F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(28.500F, -6.500F, 6.843F, -0.1714F, 0.3536F, -0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(2, 169).addBox(-3.250F, -0.650F, -8.196F, 6.500F, 1.300F, 16.392F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(26.300F, -8.550F, 0.884F, -0.1655F, 0.3536F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_r_forearm(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_r_forearm", CubeListBuilder.create()
                , PartPose.offset(22.000F, -8.500F, -6.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(831, 81).addBox(-2.500F, -2.200F, -11.637F, 5.000F, 4.400F, 23.274F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(12.000F, -3.000F, 4.150F, 0.2166F, 1.1781F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(920, 208).addBox(-2.000F, -2.000F, -3.269F, 4.000F, 4.000F, 6.538F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(2.750F, -1.750F, -2.950F, 0.3924F, 2.7150F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(967, 208).addBox(-1.300F, -1.300F, -3.824F, 2.600F, 2.600F, 7.648F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(4.250F, 1.000F, -7.450F, -0.9018F, 2.8199F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(391, 223).addBox(-1.100F, -1.100F, -3.482F, 2.200F, 2.200F, 6.964F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(2.250F, 2.000F, -6.450F, -1.0383F, 2.9997F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(97, 2).addBox(-4.800F, -0.780F, -18.358F, 9.600F, 1.560F, 36.716F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(6.500F, 1.900F, 17.300F, -0.1311F, 0.2784F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(300, 117).addBox(-1.400F, -0.550F, -11.375F, 2.800F, 1.100F, 22.750F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(4.600F, 0.138F, 10.650F, -0.1267F, 0.2784F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(283, 2).addBox(-4.462F, -0.780F, -17.741F, 8.923F, 1.561F, 35.483F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(13.779F, 0.358F, 19.341F, -0.1281F, 0.3266F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(463, 2).addBox(-4.373F, -0.780F, -17.240F, 8.745F, 1.561F, 34.481F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(20.575F, -0.706F, 21.337F, -0.1241F, 0.3885F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(458, 117).addBox(-1.400F, -0.550F, -10.683F, 2.800F, 1.100F, 21.367F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(18.112F, -2.366F, 15.320F, -0.1194F, 0.3885F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(813, 2).addBox(-4.283F, -0.780F, -16.849F, 8.566F, 1.560F, 33.698F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(26.871F, -1.352F, 23.293F, -0.1190F, 0.4587F, -0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(175, 43).addBox(-4.444F, -0.780F, -16.575F, 8.888F, 1.560F, 33.151F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(32.727F, -1.641F, 25.213F, -0.1129F, 0.5353F, -0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(873, 117).addBox(-1.400F, -0.549F, -10.271F, 2.800F, 1.099F, 20.542F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(29.534F, -3.200F, 19.830F, -0.1080F, 0.5353F, -0.0000F));
        return part;
    }
    private static PartDefinition build_wing_r_fan(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("wing_r_fan", CubeListBuilder.create()
                , PartPose.offset(23.000F, -5.000F, 9.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(558, 81).addBox(-1.800F, -1.500F, -13.588F, 3.600F, 3.000F, 27.176F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(10.250F, 1.750F, 7.500F, -0.1663F, 0.9310F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(348, 43).addBox(-4.356F, -0.780F, -16.432F, 8.711F, 1.560F, 32.863F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(15.213F, 3.370F, 18.106F, -0.1058F, 0.6163F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(774, 43).addBox(-4.117F, -0.780F, -16.424F, 8.234F, 1.560F, 32.848F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(20.403F, 3.619F, 19.974F, -0.0976F, 0.7002F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(922, 117).addBox(-1.400F, -0.550F, -10.178F, 2.800F, 1.100F, 20.356F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(16.400F, 2.161F, 15.224F, -0.0927F, 0.7002F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(859, 43).addBox(-4.028F, -0.780F, -16.557F, 8.056F, 1.560F, 33.115F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(25.378F, 4.046F, 21.825F, -0.0887F, 0.7850F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(2, 43).addBox(-3.938F, -0.780F, -16.832F, 7.877F, 1.560F, 33.665F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(30.216F, 4.593F, 23.665F, -0.0792F, 0.8690F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(725, 117).addBox(-1.400F, -0.549F, -10.432F, 2.801F, 1.099F, 20.864F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(25.347F, 3.236F, 19.549F, -0.0745F, 0.8690F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(639, 2).addBox(-3.850F, -0.780F, -17.246F, 7.700F, 1.560F, 34.492F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(35.000F, 5.200F, 25.500F, -0.0696F, 0.9505F, -0.0000F));
        return part;
    }
    private static PartDefinition build_saddle(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("saddle", CubeListBuilder.create()
                .texOffs(982, 223).addBox(-11.400F, 11.700F, -3.300F, 1.800F, 3.600F, 3.600F, new CubeDeformation(inflate + 0.002F))
                .texOffs(995, 223).addBox(9.600F, 11.700F, -3.300F, 1.800F, 3.600F, 3.600F, new CubeDeformation(inflate + 0.010F))
                , PartPose.offset(0.000F, -6.500F, -1.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(697, 169).addBox(-6.600F, -1.400F, -6.750F, 13.200F, 2.800F, 13.500F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.500F, 0.000F, 0.3142F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(2, 208).addBox(-5.250F, -0.900F, -5.250F, 10.500F, 1.800F, 10.500F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -2.900F, 0.300F, 0.3142F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(574, 223).addBox(-5.100F, -2.600F, -1.800F, 10.200F, 5.200F, 3.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -6.100F, -6.500F, 0.4538F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(542, 223).addBox(-5.700F, -2.900F, -1.600F, 11.400F, 5.800F, 3.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -3.900F, 6.700F, -0.2443F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(54, 236).addBox(-1.800F, -1.800F, -1.500F, 3.600F, 3.600F, 3.000F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(0.000F, -7.700F, -7.300F, 0.3840F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(643, 169).addBox(-1.100F, -3.600F, -5.100F, 2.200F, 7.200F, 10.200F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-7.800F, 1.500F, -0.500F, 0.3142F, 0.0000F, 0.2094F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(904, 189).addBox(-0.800F, -5.250F, -1.500F, 1.600F, 10.500F, 3.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-9.800F, 7.700F, -1.000F, 0.2094F, 0.0000F, 0.1047F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(890, 81).addBox(-0.550F, -0.550F, -12.698F, 1.100F, 1.100F, 25.396F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-4.350F, -10.950F, -18.350F, 0.4089F, 3.1287F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(670, 169).addBox(-1.100F, -3.600F, -5.100F, 2.200F, 7.200F, 10.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(7.800F, 1.500F, -0.500F, 0.3142F, 0.0000F, -0.2094F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(916, 189).addBox(-0.800F, -5.250F, -1.500F, 1.600F, 10.500F, 3.000F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(9.800F, 7.700F, -1.000F, 0.2094F, 0.0000F, -0.1047F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(945, 81).addBox(-0.550F, -0.550F, -12.698F, 1.100F, 1.100F, 25.396F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(4.350F, -10.950F, -18.350F, 0.4089F, -3.1287F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_l_thigh(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_l_thigh", CubeListBuilder.create()
                , PartPose.offset(-8.800F, -0.000F, -1.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(497, 169).addBox(-4.300F, -4.500F, -4.414F, 8.600F, 9.000F, 8.828F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-0.600F, 3.750F, -2.250F, -1.0151F, -2.8810F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(583, 189).addBox(-1.200F, -4.250F, -3.100F, 2.400F, 8.500F, 6.200F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-2.200F, 2.500F, 2.500F, 0.3142F, -0.2094F, 0.1745F));
        return part;
    }
    private static PartDefinition build_leg_l_shin(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_l_shin", CubeListBuilder.create()
                , PartPose.offset(-1.200F, 7.500F, -4.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(831, 169).addBox(-2.900F, -3.000F, -5.484F, 5.800F, 6.000F, 10.968F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.100F, 3.750F, 4.000F, -0.7529F, 0.0250F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_l_meta(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_l_meta", CubeListBuilder.create()
                , PartPose.offset(0.200F, 7.500F, 8.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(760, 189).addBox(-2.100F, -2.100F, -4.537F, 4.200F, 4.200F, 9.074F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.150F, 3.400F, -3.000F, -0.8472F, 3.0917F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_l_talon(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_l_talon", CubeListBuilder.create()
                .texOffs(604, 223).addBox(-2.400F, -1.100F, -2.900F, 4.800F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.000F))
                , PartPose.offset(0.300F, 6.800F, -6.000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(650, 223).addBox(-1.150F, -1.150F, -2.986F, 2.300F, 2.300F, 5.972F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-1.491F, 0.700F, -3.556F, -0.1344F, -2.6136F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(100, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-3.639F, 1.550F, -7.460F, -0.2915F, -2.6878F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(326, 223).addBox(-1.150F, -1.150F, -3.622F, 2.300F, 2.300F, 7.244F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, 0.700F, -4.600F, -0.1107F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(112, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(0.000F, 1.550F, -9.700F, -0.2915F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(669, 223).addBox(-1.150F, -1.150F, -2.986F, 2.300F, 2.300F, 5.972F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(1.491F, 0.700F, -3.556F, -0.1344F, 2.6136F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(124, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(3.639F, 1.550F, -7.460F, -0.2915F, 2.6878F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(22, 236).addBox(-0.850F, -0.950F, -2.467F, 1.700F, 1.900F, 4.934F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.250F, 3.850F, -0.3089F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_r_thigh(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_r_thigh", CubeListBuilder.create()
                , PartPose.offset(8.800F, -0.000F, -1.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(534, 169).addBox(-4.300F, -4.500F, -4.414F, 8.600F, 9.000F, 8.828F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.600F, 3.750F, -2.250F, -1.0151F, 2.8810F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(603, 189).addBox(-1.200F, -4.250F, -3.100F, 2.400F, 8.500F, 6.200F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(2.200F, 2.500F, 2.500F, 0.3142F, 0.2094F, -0.1745F));
        return part;
    }
    private static PartDefinition build_leg_r_shin(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_r_shin", CubeListBuilder.create()
                , PartPose.offset(1.200F, 7.500F, -4.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(867, 169).addBox(-2.900F, -3.000F, -5.484F, 5.800F, 6.000F, 10.968F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-0.100F, 3.750F, 4.000F, -0.7529F, -0.0250F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_r_meta(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_r_meta", CubeListBuilder.create()
                , PartPose.offset(-0.200F, 7.500F, 8.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(789, 189).addBox(-2.100F, -2.100F, -4.537F, 4.200F, 4.200F, 9.074F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-0.150F, 3.400F, -3.000F, -0.8472F, -3.0917F, -0.0000F));
        return part;
    }
    private static PartDefinition build_leg_r_talon(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_r_talon", CubeListBuilder.create()
                .texOffs(627, 223).addBox(-2.400F, -1.100F, -2.900F, 4.800F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.000F))
                , PartPose.offset(-0.300F, 6.800F, -6.000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(688, 223).addBox(-1.150F, -1.150F, -2.986F, 2.300F, 2.300F, 5.972F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(-1.491F, 0.700F, -3.556F, -0.1344F, -2.6136F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(136, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(-3.639F, 1.550F, -7.460F, -0.2915F, -2.6878F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(348, 223).addBox(-1.150F, -1.150F, -3.622F, 2.300F, 2.300F, 7.244F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, 0.700F, -4.600F, -0.1107F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(148, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(0.000F, 1.550F, -9.700F, -0.2915F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(707, 223).addBox(-1.150F, -1.150F, -2.986F, 2.300F, 2.300F, 5.972F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(1.491F, 0.700F, -3.556F, -0.1344F, 2.6136F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(160, 236).addBox(-0.750F, -0.900F, -1.566F, 1.500F, 1.800F, 3.132F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(3.639F, 1.550F, -7.460F, -0.2915F, 2.6878F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(38, 236).addBox(-0.850F, -0.950F, -2.467F, 1.700F, 1.900F, 4.934F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.250F, 3.850F, -0.3089F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_tail_1(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_1", CubeListBuilder.create()
                , PartPose.offset(0.000F, 0.500F, 6.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(354, 117).addBox(-5.592F, -4.660F, -7.047F, 11.184F, 9.320F, 14.094F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 0.950F, 5.500F, -0.1571F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(191, 169).addBox(-3.803F, -1.584F, -7.047F, 7.606F, 3.168F, 14.094F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 3.513F, 5.500F, -0.1571F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(412, 223).addBox(-0.800F, -1.833F, -2.800F, 1.600F, 3.667F, 5.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.351F, 5.500F, 0.4189F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_tail_2(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_2", CubeListBuilder.create()
                , PartPose.offset(0.000F, 2.000F, 12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(509, 117).addBox(-4.776F, -3.980F, -7.273F, 9.552F, 7.960F, 14.546F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, 0.400F, 5.750F, -0.0798F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(237, 169).addBox(-3.248F, -1.353F, -7.273F, 6.496F, 2.706F, 14.546F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 2.589F, 5.750F, -0.0798F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(862, 223).addBox(-0.800F, -1.700F, -2.800F, 1.600F, 3.400F, 5.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.302F, 5.750F, 0.4189F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_tail_3(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_3", CubeListBuilder.create()
                , PartPose.offset(0.000F, 1.000F, 12.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(971, 117).addBox(-3.960F, -3.300F, -7.554F, 7.920F, 6.600F, 15.108F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -0.500F, 6.000F, 0.0614F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(327, 169).addBox(-2.693F, -1.122F, -7.554F, 5.386F, 2.244F, 15.108F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 1.315F, 6.000F, 0.0614F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(879, 223).addBox(-0.800F, -1.567F, -2.800F, 1.600F, 3.134F, 5.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.604F, 6.000F, 0.4189F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_tail_4(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_4", CubeListBuilder.create()
                , PartPose.offset(0.000F, -1.000F, 13.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(245, 145).addBox(-3.144F, -2.620F, -7.382F, 6.288F, 5.240F, 14.764F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.100F, 5.750F, 0.1897F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(753, 169).addBox(-2.138F, -0.891F, -7.382F, 4.276F, 1.782F, 14.764F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, 0.341F, 5.750F, 0.1897F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(896, 223).addBox(-0.800F, -1.435F, -2.800F, 1.600F, 2.869F, 5.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.606F, 5.750F, 0.4189F, 0.0000F, -0.0000F));
        return part;
    }
    private static PartDefinition build_tail_5(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_5", CubeListBuilder.create()
                , PartPose.offset(0.000F, -2.500F, 12.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(825, 145).addBox(-2.328F, -1.940F, -7.203F, 4.656F, 3.880F, 14.406F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -1.400F, 5.500F, 0.2606F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(36, 189).addBox(-1.583F, -0.660F, -7.203F, 3.166F, 1.320F, 14.406F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(0.000F, -0.333F, 5.500F, 0.2606F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(913, 223).addBox(-0.800F, -1.301F, -2.800F, 1.600F, 2.603F, 5.600F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(0.000F, -4.307F, 5.500F, 0.4189F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(370, 169).addBox(-2.200F, -0.700F, -7.862F, 4.400F, 1.400F, 15.724F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-3.500F, -1.350F, 9.500F, 0.0955F, -0.4637F, -0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(2, 145).addBox(-1.900F, -0.700F, -9.830F, 3.800F, 1.400F, 19.660F, new CubeDeformation(inflate + 0.008F)), PartPose.offsetAndRotation(-4.750F, -3.070F, 15.200F, 0.1377F, -0.5096F, -0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(134, 117).addBox(-1.600F, -0.700F, -11.824F, 3.200F, 1.400F, 23.648F, new CubeDeformation(inflate + 0.010F)), PartPose.offsetAndRotation(-6.000F, -4.790F, 20.900F, 0.1656F, -0.5404F, -0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(413, 169).addBox(-2.200F, -0.700F, -7.862F, 4.400F, 1.400F, 15.724F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(3.500F, -1.350F, 9.500F, 0.0955F, 0.4637F, -0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(51, 145).addBox(-1.900F, -0.700F, -9.830F, 3.800F, 1.400F, 19.660F, new CubeDeformation(inflate + 0.002F)), PartPose.offsetAndRotation(4.750F, -3.070F, 15.200F, 0.1377F, 0.5096F, -0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(190, 117).addBox(-1.600F, -0.700F, -11.824F, 3.200F, 1.400F, 23.648F, new CubeDeformation(inflate + 0.004F)), PartPose.offsetAndRotation(6.000F, -4.790F, 20.900F, 0.1656F, 0.5404F, -0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(282, 169).addBox(-2.400F, -0.800F, -8.189F, 4.800F, 1.600F, 16.378F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -4.750F, 19.500F, 0.2154F, 0.0000F, -0.0000F));
        return part;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Driven by BedrockAnimationLoader + GoldflameQucusaurRenderer / MountRenderer
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
