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
 * AAA 881-cube articulated Primo Geovishap model with 18 skeletal bones
 * (including articulated elbows forearm_left/right and knees shin_left/right).
 */
public class PrimoGeovishapModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("primo_geovishap"), "main");

    public static final String[] ALL_BONES = {
            "root", "body", "torso", "neck", "head", "jaw", "arm_left", "forearm_left", "arm_right", "forearm_right", "leg_left", "shin_left", "leg_right", "shin_right", "tail_1", "tail_2", "tail_3", "tail_4", "fx_anchor"
    };

    private final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public PrimoGeovishapModel(ModelPart rootPart) {
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

    public ModelPart getBone(String name) {
        return bones.get(name);
    }

    public void resetPose() {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            PartPose pose = initialPoses.get(e.getKey());
            if (pose != null) e.getValue().loadPose(pose);
            e.getValue().visible = true;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(0.000F, 24.000F, 0.000F));
        PartDefinition b_body = build_body(b_root);
        PartDefinition b_torso = build_torso(b_body);
        PartDefinition b_neck = build_neck(b_torso);
        PartDefinition b_head = build_head(b_neck);
        build_jaw(b_head);
        PartDefinition b_arm_left = build_arm_left(b_torso);
        build_forearm_left(b_arm_left);
        PartDefinition b_arm_right = build_arm_right(b_torso);
        build_forearm_right(b_arm_right);
        PartDefinition b_leg_left = build_leg_left(b_torso);
        build_shin_left(b_leg_left);
        PartDefinition b_leg_right = build_leg_right(b_torso);
        build_shin_right(b_leg_right);
        PartDefinition b_tail_1 = build_tail_1(b_torso);
        PartDefinition b_tail_2 = build_tail_2(b_tail_1);
        PartDefinition b_tail_3 = build_tail_3(b_tail_2);
        build_tail_4(b_tail_3);
        build_fx_anchor(b_body);
        return LayerDefinition.create(mesh, 512, 512);
    }

    private static PartDefinition build_body(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("body", CubeListBuilder.create()
                , PartPose.offset(0.000F, -22.000F, 0.000F));
        return part;
    }
    private static PartDefinition build_torso(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-12.500F, -11.500F, -16.500F, 25.000F, 17.500F, 15.500F)
                .texOffs(64, 64).addBox(-11.200F, -9.500F, -2.000F, 22.400F, 16.000F, 12.000F)
                .texOffs(64, 64).addBox(-10.000F, -6.500F, 9.000F, 20.000F, 13.500F, 9.500F)
                .texOffs(0, 64).addBox(-10.200F, 4.500F, -15.500F, 20.400F, 4.000F, 13.500F)
                .texOffs(0, 64).addBox(-9.000F, 5.000F, -2.500F, 18.000F, 3.200F, 12.000F)
                .texOffs(0, 64).addBox(-7.800F, 5.200F, 9.000F, 15.600F, 2.600F, 8.500F)
                .texOffs(0, 0).addBox(-11.500F, -14.500F, -15.000F, 23.000F, 5.000F, 13.000F)
                .texOffs(0, 0).addBox(-10.500F, -12.800F, -2.500F, 21.000F, 5.000F, 12.000F)
                .texOffs(64, 0).addBox(-9.200F, -9.800F, 9.000F, 18.400F, 4.600F, 9.000F)
                .texOffs(64, 0).addBox(-4.200F, -17.200F, -14.000F, 8.400F, 4.200F, 13.000F)
                .texOffs(64, 0).addBox(-3.600F, -15.000F, -1.500F, 7.200F, 4.000F, 11.500F)
                .texOffs(0, 0).addBox(-3.000F, -12.000F, 9.500F, 6.000F, 3.800F, 8.000F)
                .texOffs(0, 0).addBox(-14.800F, -13.000F, -16.000F, 7.600F, 8.000F, 9.500F)
                .texOffs(64, 0).addBox(-15.500F, -14.200F, -14.500F, 6.000F, 7.200F, 6.500F)
                .texOffs(0, 64).addBox(-9.800F, -1.500F, -18.200F, 7.200F, 7.000F, 3.700F)
                .texOffs(0, 0).addBox(-10.200F, -5.500F, -17.500F, 6.400F, 5.500F, 3.500F)
                .texOffs(0, 0).addBox(-12.400F, -6.500F, 8.000F, 5.200F, 8.500F, 9.500F)
                .texOffs(0, 0).addBox(7.200F, -13.000F, -16.000F, 7.600F, 8.000F, 9.500F)
                .texOffs(64, 0).addBox(9.500F, -14.200F, -14.500F, 6.000F, 7.200F, 6.500F)
                .texOffs(0, 64).addBox(2.600F, -1.500F, -18.200F, 7.200F, 7.000F, 3.700F)
                .texOffs(0, 0).addBox(3.800F, -5.500F, -17.500F, 6.400F, 5.500F, 3.500F)
                .texOffs(0, 0).addBox(7.200F, -6.500F, 8.000F, 5.200F, 8.500F, 9.500F)
                .texOffs(288, 0).addBox(-8.000F, -8.350F, -17.150F, 16.000F, 11.200F, 0.650F)
                .texOffs(288, 0).addBox(-7.168F, -6.620F, -2.650F, 14.336F, 10.240F, 0.650F)
                .texOffs(288, 0).addBox(-6.400F, -4.070F, 8.350F, 12.800F, 8.640F, 0.650F)
                .texOffs(288, 0).addBox(-7.360F, -13.600F, -15.650F, 14.720F, 3.200F, 0.650F)
                .texOffs(288, 0).addBox(-6.720F, -11.900F, -3.150F, 13.440F, 3.200F, 0.650F)
                .texOffs(288, 0).addBox(-6.528F, 5.220F, -16.060F, 13.056F, 2.560F, 0.560F)
                .texOffs(288, 0).addBox(-5.760F, 5.576F, -2.948F, 11.520F, 2.048F, 0.448F)
                .texOffs(288, 0).addBox(-5.888F, -8.972F, 8.356F, 11.776F, 2.944F, 0.644F)
                .texOffs(288, 0).addBox(-13.432F, -11.560F, -16.650F, 4.864F, 5.120F, 0.650F)
                .texOffs(288, 0).addBox(8.568F, -11.560F, -16.650F, 4.864F, 5.120F, 0.650F)
                .texOffs(288, 0).addBox(-2.688F, -16.444F, -14.588F, 5.376F, 2.688F, 0.588F)
                .texOffs(288, 0).addBox(-4.992F, 5.668F, 8.636F, 9.984F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(-11.464F, -4.970F, 7.350F, 3.328F, 5.440F, 0.650F)
                .texOffs(288, 0).addBox(8.136F, -4.970F, 7.350F, 3.328F, 5.440F, 0.650F)
                .texOffs(288, 0).addBox(-2.304F, -14.280F, -2.060F, 4.608F, 2.560F, 0.560F)
                .texOffs(288, 0).addBox(-14.420F, -12.904F, -15.150F, 3.840F, 4.608F, 0.650F)
                .texOffs(288, 0).addBox(10.580F, -12.904F, -15.150F, 3.840F, 4.608F, 0.650F)
                .texOffs(288, 0).addBox(-8.504F, -0.240F, -18.718F, 4.608F, 4.480F, 0.518F)
                .texOffs(288, 0).addBox(3.896F, -0.240F, -18.718F, 4.608F, 4.480F, 0.518F)
                .texOffs(288, 0).addBox(-1.920F, -11.316F, 8.968F, 3.840F, 2.432F, 0.532F)
                .texOffs(288, 0).addBox(-9.048F, -4.510F, -17.990F, 4.096F, 3.520F, 0.490F)
                .texOffs(288, 0).addBox(4.952F, -4.510F, -17.990F, 4.096F, 3.520F, 0.490F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-2.200F, -9.500F, -2.200F, 4.400F, 11.000F, 4.400F), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.320F, -11.900F, -1.320F, 2.640F, 4.600F, 2.640F), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-2.500F, -12.500F, -2.500F, 5.000F, 14.000F, 5.000F), PartPose.offsetAndRotation(-13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(192, 0).addBox(-1.500F, -14.900F, -1.500F, 3.000F, 4.600F, 3.000F), PartPose.offsetAndRotation(-13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-2.600F, -13.500F, -2.600F, 5.200F, 15.000F, 5.200F), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(192, 0).addBox(-1.560F, -15.900F, -1.560F, 3.120F, 4.600F, 3.120F), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-2.400F, -12.000F, -2.400F, 4.800F, 13.500F, 4.800F), PartPose.offsetAndRotation(-13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, 0.4189F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(192, 0).addBox(-1.440F, -14.400F, -1.440F, 2.880F, 4.600F, 2.880F), PartPose.offsetAndRotation(-13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, 0.4189F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 0).addBox(-2.100F, -10.500F, -2.100F, 4.200F, 12.000F, 4.200F), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(192, 0).addBox(-1.260F, -12.900F, -1.260F, 2.520F, 4.600F, 2.520F), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(128, 0).addBox(-1.800F, -8.500F, -1.800F, 3.600F, 10.000F, 3.600F), PartPose.offsetAndRotation(-9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(192, 0).addBox(-1.080F, -10.900F, -1.080F, 2.160F, 4.600F, 2.160F), PartPose.offsetAndRotation(-9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -6.500F, -1.500F, 3.000F, 8.000F, 3.000F), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(192, 0).addBox(-0.900F, -8.900F, -0.900F, 1.800F, 4.600F, 1.800F), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(128, 128).addBox(-2.000F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(128, 128).addBox(-2.000F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(128, 128).addBox(-2.000F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(128, 128).addBox(-2.000F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(128, 0).addBox(-2.200F, -9.500F, -2.200F, 4.400F, 11.000F, 4.400F), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(192, 0).addBox(-1.320F, -11.900F, -1.320F, 2.640F, 4.600F, 2.640F), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(128, 0).addBox(-2.500F, -12.500F, -2.500F, 5.000F, 14.000F, 5.000F), PartPose.offsetAndRotation(13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, -0.5236F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(192, 0).addBox(-1.500F, -14.900F, -1.500F, 3.000F, 4.600F, 3.000F), PartPose.offsetAndRotation(13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, -0.5236F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(128, 0).addBox(-2.600F, -13.500F, -2.600F, 5.200F, 15.000F, 5.200F), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(192, 0).addBox(-1.560F, -15.900F, -1.560F, 3.120F, 4.600F, 3.120F), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_28", CubeListBuilder.create().texOffs(128, 0).addBox(-2.400F, -12.000F, -2.400F, 4.800F, 13.500F, 4.800F), PartPose.offsetAndRotation(13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, -0.4189F));
        part.addOrReplaceChild("c_29", CubeListBuilder.create().texOffs(192, 0).addBox(-1.440F, -14.400F, -1.440F, 2.880F, 4.600F, 2.880F), PartPose.offsetAndRotation(13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, -0.4189F));
        part.addOrReplaceChild("c_30", CubeListBuilder.create().texOffs(128, 0).addBox(-2.100F, -10.500F, -2.100F, 4.200F, 12.000F, 4.200F), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_31", CubeListBuilder.create().texOffs(192, 0).addBox(-1.260F, -12.900F, -1.260F, 2.520F, 4.600F, 2.520F), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_32", CubeListBuilder.create().texOffs(128, 0).addBox(-1.800F, -8.500F, -1.800F, 3.600F, 10.000F, 3.600F), PartPose.offsetAndRotation(9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_33", CubeListBuilder.create().texOffs(192, 0).addBox(-1.080F, -10.900F, -1.080F, 2.160F, 4.600F, 2.160F), PartPose.offsetAndRotation(9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_34", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -6.500F, -1.500F, 3.000F, 8.000F, 3.000F), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_35", CubeListBuilder.create().texOffs(192, 0).addBox(-0.900F, -8.900F, -0.900F, 1.800F, 4.600F, 1.800F), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_36", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_37", CubeListBuilder.create().texOffs(128, 128).addBox(0.800F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_38", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_39", CubeListBuilder.create().texOffs(128, 128).addBox(0.800F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_40", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_41", CubeListBuilder.create().texOffs(128, 128).addBox(0.800F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_42", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_43", CubeListBuilder.create().texOffs(128, 128).addBox(0.800F, -2.800F, -2.600F, 1.200F, 5.600F, 5.200F), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_44", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -15.500F, -14.500F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_45", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F), PartPose.offsetAndRotation(0.000F, -14.800F, -11.100F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_46", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F), PartPose.offsetAndRotation(0.000F, -14.100F, -7.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_47", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F), PartPose.offsetAndRotation(0.000F, -13.400F, -4.300F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_48", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F), PartPose.offsetAndRotation(0.000F, -12.700F, -0.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_49", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -12.000F, 2.500F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_50", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -11.300F, 5.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_51", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -10.600F, 9.300F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_52", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -9.900F, 12.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_53", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F), PartPose.offsetAndRotation(0.000F, -9.200F, 16.100F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_54", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_55", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-10.800F, 3.000F, -8.800F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_56", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_57", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-10.000F, 3.000F, 1.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_58", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_59", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(-9.200F, 3.000F, 12.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_60", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_61", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(10.800F, 3.000F, -8.800F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_62", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_63", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(10.000F, 3.000F, 1.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_64", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_65", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F), PartPose.offsetAndRotation(9.200F, 3.000F, 12.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_66", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -4.500F, -2.500F, 3.000F, 6.500F, 4.500F), PartPose.offsetAndRotation(-13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_67", CubeListBuilder.create().texOffs(192, 0).addBox(-1.300F, -4.000F, -2.000F, 2.600F, 6.000F, 4.000F), PartPose.offsetAndRotation(-14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, 0.5934F));
        part.addOrReplaceChild("c_68", CubeListBuilder.create().texOffs(128, 0).addBox(-1.400F, -4.000F, -2.500F, 2.800F, 6.000F, 5.000F), PartPose.offsetAndRotation(-11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_69", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -4.500F, -2.500F, 3.000F, 6.500F, 4.500F), PartPose.offsetAndRotation(13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_70", CubeListBuilder.create().texOffs(192, 0).addBox(-1.300F, -4.000F, -2.000F, 2.600F, 6.000F, 4.000F), PartPose.offsetAndRotation(14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, -0.5934F));
        part.addOrReplaceChild("c_71", CubeListBuilder.create().texOffs(128, 0).addBox(-1.400F, -4.000F, -2.500F, 2.800F, 6.000F, 5.000F), PartPose.offsetAndRotation(11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_72", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_73", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-6.450F, -11.100F, -7.900F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_74", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_75", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-5.750F, -9.300F, 3.300F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_76", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_77", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(-5.050F, -7.500F, 14.500F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_78", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(-8.200F, -10.500F, -11.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_79", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(-7.800F, -9.600F, -5.200F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_80", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(-7.400F, -8.700F, 0.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_81", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(-7.000F, -7.800F, 6.400F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_82", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(-6.600F, -6.900F, 12.200F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_83", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_84", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(6.450F, -11.100F, -7.900F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_85", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_86", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(5.750F, -9.300F, 3.300F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_87", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_88", CubeListBuilder.create().texOffs(64, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F), PartPose.offsetAndRotation(5.050F, -7.500F, 14.500F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_89", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(8.200F, -10.500F, -11.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_90", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(7.800F, -9.600F, -5.200F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_91", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(7.400F, -8.700F, 0.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_92", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(7.000F, -7.800F, 6.400F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_93", CubeListBuilder.create().texOffs(128, 128).addBox(-0.700F, -0.700F, -2.200F, 1.400F, 1.400F, 4.400F), PartPose.offsetAndRotation(6.600F, -6.900F, 12.200F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_94", CubeListBuilder.create().texOffs(288, 0).addBox(-1.664F, -10.800F, -3.250F, 3.328F, 9.600F, 0.650F), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_95", CubeListBuilder.create().texOffs(288, 0).addBox(-1.664F, -10.800F, -3.250F, 3.328F, 9.600F, 0.650F), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_96", CubeListBuilder.create().texOffs(288, 0).addBox(-1.600F, -9.980F, -3.150F, 3.200F, 8.960F, 0.650F), PartPose.offsetAndRotation(-13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_97", CubeListBuilder.create().texOffs(288, 0).addBox(-1.600F, -9.980F, -3.150F, 3.200F, 8.960F, 0.650F), PartPose.offsetAndRotation(13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, -0.5236F));
        part.addOrReplaceChild("c_98", CubeListBuilder.create().texOffs(288, 0).addBox(-1.536F, -9.570F, -3.050F, 3.072F, 8.640F, 0.650F), PartPose.offsetAndRotation(-13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, 0.4189F));
        part.addOrReplaceChild("c_99", CubeListBuilder.create().texOffs(288, 0).addBox(-1.536F, -9.570F, -3.050F, 3.072F, 8.640F, 0.650F), PartPose.offsetAndRotation(13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, -0.4189F));
        part.addOrReplaceChild("c_100", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_101", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_102", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_103", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_104", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_105", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_106", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_107", CubeListBuilder.create().texOffs(288, 0).addBox(-1.280F, -2.688F, -4.360F, 2.560F, 5.376F, 0.560F), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_108", CubeListBuilder.create().texOffs(288, 0).addBox(-1.344F, -8.340F, -2.688F, 2.688F, 7.680F, 0.588F), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_109", CubeListBuilder.create().texOffs(288, 0).addBox(-1.344F, -8.340F, -2.688F, 2.688F, 7.680F, 0.588F), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_110", CubeListBuilder.create().texOffs(288, 0).addBox(-1.408F, -7.520F, -2.816F, 2.816F, 7.040F, 0.616F), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_111", CubeListBuilder.create().texOffs(288, 0).addBox(-1.408F, -7.520F, -2.816F, 2.816F, 7.040F, 0.616F), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_112", CubeListBuilder.create().texOffs(288, 0).addBox(-1.152F, -6.700F, -2.304F, 2.304F, 6.400F, 0.504F), PartPose.offsetAndRotation(-9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_113", CubeListBuilder.create().texOffs(288, 0).addBox(-1.152F, -6.700F, -2.304F, 2.304F, 6.400F, 0.504F), PartPose.offsetAndRotation(9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_114", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-6.450F, -11.100F, -7.900F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_115", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(6.450F, -11.100F, -7.900F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_116", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_117", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-5.750F, -9.300F, 3.300F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_118", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_119", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-5.050F, -7.500F, 14.500F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_120", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_121", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(5.750F, -9.300F, 3.300F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_122", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_123", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(5.050F, -7.500F, 14.500F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_124", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(-6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_125", CubeListBuilder.create().texOffs(288, 0).addBox(-1.920F, -1.060F, -3.020F, 3.840F, 1.920F, 0.420F), PartPose.offsetAndRotation(6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_126", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -6.380F, -1.792F, 2.048F, 5.760F, 0.392F), PartPose.offsetAndRotation(0.000F, -14.800F, -11.100F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_127", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -6.380F, -1.792F, 2.048F, 5.760F, 0.392F), PartPose.offsetAndRotation(0.000F, -14.100F, -7.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_128", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -6.380F, -1.792F, 2.048F, 5.760F, 0.392F), PartPose.offsetAndRotation(0.000F, -13.400F, -4.300F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_129", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -6.380F, -1.792F, 2.048F, 5.760F, 0.392F), PartPose.offsetAndRotation(0.000F, -12.700F, -0.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_130", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -3.330F, -2.920F, 1.920F, 4.160F, 0.420F), PartPose.offsetAndRotation(-13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_131", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -3.330F, -2.920F, 1.920F, 4.160F, 0.420F), PartPose.offsetAndRotation(13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_132", CubeListBuilder.create().texOffs(288, 0).addBox(-0.896F, -2.920F, -2.892F, 1.792F, 3.840F, 0.392F), PartPose.offsetAndRotation(-11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_133", CubeListBuilder.create().texOffs(288, 0).addBox(-0.896F, -2.920F, -2.892F, 1.792F, 3.840F, 0.392F), PartPose.offsetAndRotation(11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_134", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -5.060F, -1.920F, 1.920F, 5.120F, 0.420F), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_135", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -5.060F, -1.920F, 1.920F, 5.120F, 0.420F), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_136", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-10.800F, 3.000F, -8.800F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_137", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(10.800F, 3.000F, -8.800F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_138", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_139", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_140", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_141", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-9.200F, 3.000F, 12.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_142", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_143", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_144", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_145", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(9.200F, 3.000F, 12.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_146", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(-10.000F, 3.000F, 1.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_147", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.600F, -2.648F, 2.048F, 3.200F, 0.448F), PartPose.offsetAndRotation(10.000F, 3.000F, 1.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_148", CubeListBuilder.create().texOffs(288, 0).addBox(-0.832F, -2.920F, -2.364F, 1.664F, 3.840F, 0.364F), PartPose.offsetAndRotation(-14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, 0.5934F));
        part.addOrReplaceChild("c_149", CubeListBuilder.create().texOffs(288, 0).addBox(-0.832F, -2.920F, -2.364F, 1.664F, 3.840F, 0.364F), PartPose.offsetAndRotation(14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, -0.5934F));
        part.addOrReplaceChild("c_150", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -9.200F, 16.100F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_151", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -15.500F, -14.500F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_152", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -12.000F, 2.500F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_153", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -11.300F, 5.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_154", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -10.600F, 9.300F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_155", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -4.330F, -1.792F, 2.048F, 4.160F, 0.392F), PartPose.offsetAndRotation(0.000F, -9.900F, 12.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_156", CubeListBuilder.create().texOffs(288, 0).addBox(-1.784F, -3.050F, -1.664F, 0.768F, 0.250F, 3.328F), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_157", CubeListBuilder.create().texOffs(288, 0).addBox(1.016F, -3.050F, -1.664F, 0.768F, 0.250F, 3.328F), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_158", CubeListBuilder.create().texOffs(288, 32).addBox(-1.760F, -3.920F, -1.560F, 0.720F, 1.120F, 3.120F), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_159", CubeListBuilder.create().texOffs(288, 32).addBox(1.040F, -3.920F, -1.560F, 0.720F, 1.120F, 3.120F), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_160", CubeListBuilder.create().texOffs(288, 0).addBox(-1.784F, -3.050F, -1.664F, 0.768F, 0.250F, 3.328F), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_161", CubeListBuilder.create().texOffs(288, 0).addBox(1.016F, -3.050F, -1.664F, 0.768F, 0.250F, 3.328F), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_162", CubeListBuilder.create().texOffs(288, 32).addBox(-1.760F, -3.920F, -1.560F, 0.720F, 1.120F, 3.120F), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_163", CubeListBuilder.create().texOffs(288, 32).addBox(1.040F, -3.920F, -1.560F, 0.720F, 1.120F, 3.120F), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_164", CubeListBuilder.create().texOffs(288, 0).addBox(-0.998F, -15.072F, -1.997F, 1.996F, 2.944F, 0.437F), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_165", CubeListBuilder.create().texOffs(288, 0).addBox(-0.998F, -15.072F, -1.997F, 1.996F, 2.944F, 0.437F), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_166", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -14.072F, -1.920F, 1.920F, 2.944F, 0.420F), PartPose.offsetAndRotation(-13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_167", CubeListBuilder.create().texOffs(288, 0).addBox(-0.960F, -14.072F, -1.920F, 1.920F, 2.944F, 0.420F), PartPose.offsetAndRotation(13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, -0.5236F));
        part.addOrReplaceChild("c_168", CubeListBuilder.create().texOffs(288, 0).addBox(-0.922F, -13.572F, -1.843F, 1.844F, 2.944F, 0.403F), PartPose.offsetAndRotation(-13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, 0.4189F));
        part.addOrReplaceChild("c_169", CubeListBuilder.create().texOffs(288, 0).addBox(-0.922F, -13.572F, -1.843F, 1.844F, 2.944F, 0.403F), PartPose.offsetAndRotation(13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, -0.4189F));
        part.addOrReplaceChild("c_170", CubeListBuilder.create().texOffs(288, 0).addBox(-0.845F, -11.072F, -1.690F, 1.690F, 2.944F, 0.370F), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_171", CubeListBuilder.create().texOffs(288, 0).addBox(-0.845F, -11.072F, -1.690F, 1.690F, 2.944F, 0.370F), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_172", CubeListBuilder.create().texOffs(288, 0).addBox(-0.806F, -12.072F, -1.613F, 1.612F, 2.944F, 0.353F), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_173", CubeListBuilder.create().texOffs(288, 0).addBox(-0.806F, -12.072F, -1.613F, 1.612F, 2.944F, 0.353F), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_174", CubeListBuilder.create().texOffs(288, 0).addBox(-0.691F, -10.072F, -1.382F, 1.382F, 2.944F, 0.302F), PartPose.offsetAndRotation(-9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_175", CubeListBuilder.create().texOffs(288, 0).addBox(-0.691F, -10.072F, -1.382F, 1.382F, 2.944F, 0.302F), PartPose.offsetAndRotation(9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_176", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -8.072F, -1.152F, 1.152F, 2.944F, 0.252F), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_177", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -8.072F, -1.152F, 1.152F, 2.944F, 0.252F), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_178", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.448F, -2.450F, 0.896F, 0.896F, 0.250F), PartPose.offsetAndRotation(-8.200F, -10.500F, -11.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_179", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.448F, -2.450F, 0.896F, 0.896F, 0.250F), PartPose.offsetAndRotation(8.200F, -10.500F, -11.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_180", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(-6.600F, -6.900F, 12.200F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_181", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(6.600F, -6.900F, 12.200F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_182", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(-7.800F, -9.600F, -5.200F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_183", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(-7.000F, -7.800F, 6.400F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_184", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(7.800F, -9.600F, -5.200F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_185", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(7.000F, -7.800F, 6.400F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_186", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(-7.400F, -8.700F, 0.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_187", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -0.950F, -1.408F, 0.896F, 0.250F, 2.816F), PartPose.offsetAndRotation(7.400F, -8.700F, 0.600F, -0.0000F, -0.0000F, -0.3142F));
        return part;
    }
    private static PartDefinition build_neck(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-7.500F, -6.000F, -8.500F, 15.000F, 11.000F, 10.000F)
                .texOffs(0, 64).addBox(-6.200F, 4.000F, -8.000F, 12.400F, 2.500F, 9.000F)
                .texOffs(0, 0).addBox(-6.800F, -8.500F, -7.500F, 13.600F, 3.500F, 8.000F)
                .texOffs(64, 0).addBox(-5.200F, -10.500F, -6.500F, 10.400F, 3.000F, 6.000F)
                .texOffs(288, 0).addBox(-4.800F, -4.020F, -9.150F, 9.600F, 7.040F, 0.650F)
                .texOffs(288, 0).addBox(-4.352F, -7.870F, -7.990F, 8.704F, 2.240F, 0.490F)
                .texOffs(288, 0).addBox(-3.968F, 4.450F, -8.350F, 7.936F, 1.600F, 0.350F)
                .texOffs(288, 0).addBox(-3.328F, -9.960F, -6.920F, 6.656F, 1.920F, 0.420F)
                , PartPose.offset(0.000F, -5.000F, -16.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(-7.800F, 3.500F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(-9.600F, 2.300F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(-9.000F, 0.900F, -3.000F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(-10.800F, -0.300F, -3.000F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(-7.800F, -1.700F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(-9.600F, -2.900F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(-9.000F, -4.300F, -3.000F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(-10.800F, -5.500F, -3.000F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(-7.800F, -6.900F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(-9.600F, -8.100F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(7.800F, 3.500F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(9.600F, 2.300F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(9.000F, 0.900F, -3.000F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(10.800F, -0.300F, -3.000F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(7.800F, -1.700F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(9.600F, -2.900F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(64, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(9.000F, -4.300F, -3.000F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(10.800F, -5.500F, -3.000F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F), PartPose.offsetAndRotation(7.800F, -6.900F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F), PartPose.offsetAndRotation(9.600F, -8.100F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(-7.800F, 3.500F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(-7.800F, -1.700F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(-7.800F, -6.900F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(7.800F, 3.500F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(7.800F, -1.700F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(7.800F, -6.900F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(-9.000F, -4.300F, -3.000F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(9.000F, -4.300F, -3.000F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_28", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(-9.000F, 0.900F, -3.000F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_29", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.152F, -3.048F, 2.048F, 2.304F, 0.448F), PartPose.offsetAndRotation(9.000F, 0.900F, -3.000F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_30", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(-10.800F, -5.500F, -3.000F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_31", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(10.800F, -5.500F, -3.000F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_32", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(-9.600F, -8.100F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_33", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(9.600F, -8.100F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_34", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(-10.800F, -0.300F, -3.000F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_35", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(10.800F, -0.300F, -3.000F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_36", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(-9.600F, 2.300F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_37", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(-9.600F, -2.900F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_38", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(9.600F, 2.300F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_39", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.704F, -2.108F, 1.408F, 1.408F, 0.308F), PartPose.offsetAndRotation(9.600F, -2.900F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        return part;
    }
    private static PartDefinition build_head(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.500F, -4.500F, -10.000F, 13.000F, 7.700F, 11.500F)
                .texOffs(0, 0).addBox(-5.200F, -2.500F, -15.500F, 10.400F, 6.000F, 6.500F)
                .texOffs(64, 0).addBox(-3.600F, -1.800F, -17.200F, 7.200F, 4.800F, 2.700F)
                .texOffs(64, 0).addBox(-2.200F, -5.800F, -15.000F, 4.400F, 3.300F, 15.000F)
                .texOffs(128, 0).addBox(-1.200F, -7.600F, -12.500F, 2.400F, 2.800F, 10.500F)
                .texOffs(128, 0).addBox(-4.000F, 2.400F, -13.500F, 8.000F, 1.400F, 11.000F)
                .texOffs(64, 128).addBox(-6.700F, -1.600F, -9.200F, 1.600F, 1.800F, 3.000F)
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -15.100F, 1.200F, 3.200F, 1.200F)
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -12.800F, 1.200F, 2.400F, 1.200F)
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -10.400F, 1.200F, 2.400F, 1.200F)
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -8.000F, 1.200F, 2.400F, 1.200F)
                .texOffs(0, 0).addBox(-5.700F, -2.200F, -15.000F, 2.400F, 3.200F, 5.000F)
                .texOffs(64, 0).addBox(-4.200F, -4.000F, -14.500F, 2.400F, 2.000F, 6.500F)
                .texOffs(128, 128).addBox(-5.300F, 0.800F, -13.500F, 1.000F, 1.200F, 6.000F)
                .texOffs(0, 0).addBox(-6.400F, -5.800F, -7.000F, 3.200F, 2.300F, 7.500F)
                .texOffs(64, 64).addBox(-7.400F, -0.000F, -5.500F, 2.400F, 3.000F, 6.500F)
                .texOffs(0, 128).addBox(-3.200F, -2.400F, -17.500F, 1.400F, 1.600F, 2.000F)
                .texOffs(64, 128).addBox(5.100F, -1.600F, -9.200F, 1.600F, 1.800F, 3.000F)
                .texOffs(128, 64).addBox(3.600F, 3.200F, -15.100F, 1.200F, 3.200F, 1.200F)
                .texOffs(128, 64).addBox(3.600F, 3.200F, -12.800F, 1.200F, 2.400F, 1.200F)
                .texOffs(128, 64).addBox(3.600F, 3.200F, -10.400F, 1.200F, 2.400F, 1.200F)
                .texOffs(128, 64).addBox(3.600F, 3.200F, -8.000F, 1.200F, 2.400F, 1.200F)
                .texOffs(0, 0).addBox(3.300F, -2.200F, -15.000F, 2.400F, 3.200F, 5.000F)
                .texOffs(64, 0).addBox(1.800F, -4.000F, -14.500F, 2.400F, 2.000F, 6.500F)
                .texOffs(128, 128).addBox(4.300F, 0.800F, -13.500F, 1.000F, 1.200F, 6.000F)
                .texOffs(0, 0).addBox(3.200F, -5.800F, -7.000F, 3.200F, 2.300F, 7.500F)
                .texOffs(64, 64).addBox(5.000F, -0.000F, -5.500F, 2.400F, 3.000F, 6.500F)
                .texOffs(0, 128).addBox(1.800F, -2.400F, -17.500F, 1.400F, 1.600F, 2.000F)
                .texOffs(288, 0).addBox(-4.160F, -3.114F, -10.650F, 8.320F, 4.928F, 0.650F)
                .texOffs(288, 0).addBox(-3.328F, -1.420F, -16.150F, 6.656F, 3.840F, 0.650F)
                .texOffs(288, 0).addBox(-1.408F, -5.206F, -15.462F, 2.816F, 2.112F, 0.462F)
                .texOffs(288, 0).addBox(-2.560F, 2.652F, -13.750F, 5.120F, 0.896F, 0.250F)
                .texOffs(288, 0).addBox(-2.304F, -0.936F, -17.578F, 4.608F, 3.072F, 0.378F)
                .texOffs(288, 0).addBox(-0.768F, -7.096F, -12.836F, 1.536F, 1.792F, 0.336F)
                .texOffs(288, 0).addBox(-5.824F, -5.386F, -7.322F, 2.048F, 1.472F, 0.322F)
                .texOffs(288, 0).addBox(3.776F, -5.386F, -7.322F, 2.048F, 1.472F, 0.322F)
                .texOffs(288, 0).addBox(-6.968F, 0.540F, -5.836F, 1.536F, 1.920F, 0.336F)
                .texOffs(288, 0).addBox(5.432F, 0.540F, -5.836F, 1.536F, 1.920F, 0.336F)
                .texOffs(288, 0).addBox(-5.268F, -1.624F, -15.336F, 1.536F, 2.048F, 0.336F)
                .texOffs(288, 0).addBox(3.732F, -1.624F, -15.336F, 1.536F, 2.048F, 0.336F)
                .texOffs(288, 0).addBox(-3.768F, -3.640F, -14.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(2.232F, -3.640F, -14.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 32).addBox(-5.100F, 0.550F, -12.300F, 0.600F, 0.250F, 3.600F)
                .texOffs(288, 32).addBox(4.500F, 0.550F, -12.300F, 0.600F, 0.250F, 3.600F)
                .texOffs(288, 0).addBox(-6.412F, -1.276F, -9.450F, 1.024F, 1.152F, 0.250F)
                , PartPose.offset(0.000F, -3.000F, -8.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(64, 0).addBox(-7.100F, -3.800F, -11.000F, 3.000F, 2.200F, 7.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(0, 0).addBox(-7.700F, -5.000F, -7.000F, 2.400F, 2.600F, 6.500F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 64).addBox(-2.200F, -2.200F, -2.800F, 4.400F, 4.400F, 5.600F), PartPose.offsetAndRotation(-5.500F, -4.200F, -0.500F, -0.3142F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-1.900F, -1.900F, -2.800F, 3.800F, 3.800F, 5.600F), PartPose.offsetAndRotation(-7.800F, -6.800F, 4.000F, -0.3840F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -1.600F, -2.800F, 3.200F, 3.200F, 5.600F), PartPose.offsetAndRotation(-10.200F, -9.600F, 8.500F, -0.4538F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.800F, 2.400F, 2.400F, 5.600F), PartPose.offsetAndRotation(-12.200F, -12.600F, 12.500F, -0.5236F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(192, 0).addBox(-0.800F, -0.800F, -2.800F, 1.600F, 1.600F, 5.600F), PartPose.offsetAndRotation(-13.600F, -15.200F, 15.800F, -0.5934F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(-7.200F, 1.500F, -2.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(-8.600F, -0.200F, 1.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(-9.500F, -2.500F, 4.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(-8.000F, 3.500F, 0.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(-6.800F, 1.200F, -12.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(64, 0).addBox(4.100F, -3.800F, -11.000F, 3.000F, 2.200F, 7.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(0, 0).addBox(5.300F, -5.000F, -7.000F, 2.400F, 2.600F, 6.500F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(128, 64).addBox(-2.200F, -2.200F, -2.800F, 4.400F, 4.400F, 5.600F), PartPose.offsetAndRotation(5.500F, -4.200F, -0.500F, -0.3142F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(128, 64).addBox(-1.900F, -1.900F, -2.800F, 3.800F, 3.800F, 5.600F), PartPose.offsetAndRotation(7.800F, -6.800F, 4.000F, -0.3840F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -1.600F, -2.800F, 3.200F, 3.200F, 5.600F), PartPose.offsetAndRotation(10.200F, -9.600F, 8.500F, -0.4538F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.800F, 2.400F, 2.400F, 5.600F), PartPose.offsetAndRotation(12.200F, -12.600F, 12.500F, -0.5236F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(192, 0).addBox(-0.800F, -0.800F, -2.800F, 1.600F, 1.600F, 5.600F), PartPose.offsetAndRotation(13.600F, -15.200F, 15.800F, -0.5934F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(7.200F, 1.500F, -2.500F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(8.600F, -0.200F, 1.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(9.500F, -2.500F, 4.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(8.000F, 3.500F, 0.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F), PartPose.offsetAndRotation(6.800F, 1.200F, -12.500F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(288, 0).addBox(-1.408F, -1.408F, -3.416F, 2.816F, 2.816F, 0.616F), PartPose.offsetAndRotation(-5.500F, -4.200F, -0.500F, -0.3142F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(288, 0).addBox(-1.408F, -1.408F, -3.416F, 2.816F, 2.816F, 0.616F), PartPose.offsetAndRotation(5.500F, -4.200F, -0.500F, -0.3142F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(288, 0).addBox(-1.216F, -1.216F, -3.332F, 2.432F, 2.432F, 0.532F), PartPose.offsetAndRotation(-7.800F, -6.800F, 4.000F, -0.3840F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(288, 0).addBox(-1.216F, -1.216F, -3.332F, 2.432F, 2.432F, 0.532F), PartPose.offsetAndRotation(7.800F, -6.800F, 4.000F, -0.3840F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_28", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.024F, -3.248F, 2.048F, 2.048F, 0.448F), PartPose.offsetAndRotation(-10.200F, -9.600F, 8.500F, -0.4538F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_29", CubeListBuilder.create().texOffs(288, 0).addBox(-1.024F, -1.024F, -3.248F, 2.048F, 2.048F, 0.448F), PartPose.offsetAndRotation(10.200F, -9.600F, 8.500F, -0.4538F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_30", CubeListBuilder.create().texOffs(288, 0).addBox(-6.560F, -3.404F, -11.308F, 1.920F, 1.408F, 0.308F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("c_31", CubeListBuilder.create().texOffs(288, 0).addBox(4.640F, -3.404F, -11.308F, 1.920F, 1.408F, 0.308F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("c_32", CubeListBuilder.create().texOffs(288, 0).addBox(-7.268F, -4.532F, -7.336F, 1.536F, 1.664F, 0.336F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_33", CubeListBuilder.create().texOffs(288, 0).addBox(5.732F, -4.532F, -7.336F, 1.536F, 1.664F, 0.336F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_34", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -3.136F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-12.200F, -12.600F, 12.500F, -0.5236F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_35", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -3.136F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(12.200F, -12.600F, 12.500F, -0.5236F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_36", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-6.800F, 1.200F, -12.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_37", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(6.800F, 1.200F, -12.500F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_38", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-9.500F, -2.500F, 4.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_39", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(9.500F, -2.500F, 4.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_40", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-7.200F, 1.500F, -2.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_41", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-8.600F, -0.200F, 1.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_42", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(7.200F, 1.500F, -2.500F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_43", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(8.600F, -0.200F, 1.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_44", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-8.000F, 3.500F, 0.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_45", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -2.536F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(8.000F, 3.500F, 0.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_46", CubeListBuilder.create().texOffs(288, 0).addBox(-0.512F, -0.512F, -3.050F, 1.024F, 1.024F, 0.250F), PartPose.offsetAndRotation(-13.600F, -15.200F, 15.800F, -0.5934F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_47", CubeListBuilder.create().texOffs(288, 0).addBox(-0.512F, -0.512F, -3.050F, 1.024F, 1.024F, 0.250F), PartPose.offsetAndRotation(13.600F, -15.200F, 15.800F, -0.5934F, 0.3142F, -0.2094F));
        return part;
    }
    private static PartDefinition build_jaw(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.200F, -0.000F, -11.000F, 10.400F, 3.500F, 11.500F)
                .texOffs(64, 0).addBox(-4.200F, 2.500F, -12.200F, 8.400F, 2.300F, 9.200F)
                .texOffs(128, 0).addBox(-3.800F, -0.200F, -9.000F, 7.600F, 1.700F, 8.500F)
                .texOffs(192, 0).addBox(-2.400F, -0.800F, -7.000F, 4.800F, 1.800F, 5.500F)
                .texOffs(128, 64).addBox(-4.400F, -2.500F, -10.800F, 1.200F, 2.700F, 1.200F)
                .texOffs(128, 64).addBox(-4.400F, -1.900F, -8.800F, 1.200F, 2.100F, 1.200F)
                .texOffs(128, 64).addBox(-4.400F, -1.900F, -6.600F, 1.200F, 2.100F, 1.200F)
                .texOffs(128, 64).addBox(-4.400F, -1.900F, -4.400F, 1.200F, 2.100F, 1.200F)
                .texOffs(0, 64).addBox(-4.400F, 3.600F, -11.000F, 2.400F, 1.600F, 5.000F)
                .texOffs(128, 64).addBox(3.200F, -2.500F, -10.800F, 1.200F, 2.700F, 1.200F)
                .texOffs(128, 64).addBox(3.200F, -1.900F, -8.800F, 1.200F, 2.100F, 1.200F)
                .texOffs(128, 64).addBox(3.200F, -1.900F, -6.600F, 1.200F, 2.100F, 1.200F)
                .texOffs(128, 64).addBox(3.200F, -1.900F, -4.400F, 1.200F, 2.100F, 1.200F)
                .texOffs(0, 64).addBox(2.000F, 3.600F, -11.000F, 2.400F, 1.600F, 5.000F)
                .texOffs(288, 0).addBox(-3.328F, 0.630F, -11.490F, 6.656F, 2.240F, 0.490F)
                .texOffs(288, 0).addBox(-2.688F, 2.914F, -12.522F, 5.376F, 1.472F, 0.322F)
                .texOffs(288, 0).addBox(-2.432F, 0.106F, -9.250F, 4.864F, 1.088F, 0.250F)
                .texOffs(288, 0).addBox(-1.536F, -0.476F, -7.252F, 3.072F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-3.968F, 3.888F, -11.250F, 1.536F, 1.024F, 0.250F)
                .texOffs(288, 0).addBox(2.432F, 3.888F, -11.250F, 1.536F, 1.024F, 0.250F)
                , PartPose.offset(0.000F, 4.000F, -1.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(64, 0).addBox(-5.700F, 1.500F, -8.500F, 1.800F, 3.000F, 3.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 0).addBox(-6.000F, 0.800F, -3.500F, 1.600F, 3.000F, 3.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 64).addBox(-3.000F, 3.800F, -12.800F, 2.000F, 1.700F, 3.300F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(64, 0).addBox(3.900F, 1.500F, -8.500F, 1.800F, 3.000F, 3.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(4.400F, 0.800F, -3.500F, 1.600F, 3.000F, 3.000F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(1.000F, 3.800F, -12.800F, 2.000F, 1.700F, 3.300F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(288, 0).addBox(-5.376F, 2.040F, -8.752F, 1.152F, 1.920F, 0.252F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(288, 0).addBox(4.224F, 2.040F, -8.752F, 1.152F, 1.920F, 0.252F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(288, 0).addBox(-5.712F, 1.340F, -3.750F, 1.024F, 1.920F, 0.250F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(288, 0).addBox(4.688F, 1.340F, -3.750F, 1.024F, 1.920F, 0.250F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(288, 0).addBox(-2.640F, 4.106F, -13.050F, 1.280F, 1.088F, 0.250F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(288, 0).addBox(1.360F, 4.106F, -13.050F, 1.280F, 1.088F, 0.250F), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_arm_left(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("arm_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.500F, -4.500F, -4.000F, 8.000F, 9.000F, 8.000F)
                .texOffs(64, 64).addBox(-5.600F, 2.500F, -3.200F, 7.200F, 8.500F, 7.000F)
                .texOffs(64, 0).addBox(-5.800F, -3.500F, -3.500F, 3.200F, 7.000F, 7.000F)
                .texOffs(0, 0).addBox(-5.900F, 3.500F, -2.500F, 2.800F, 6.500F, 5.500F)
                .texOffs(64, 0).addBox(-5.900F, 7.000F, 1.500F, 4.800F, 5.500F, 4.500F)
                .texOffs(64, 0).addBox(-4.700F, 7.000F, -0.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(0, 0).addBox(-4.700F, 4.000F, -4.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(64, 0).addBox(-4.700F, 1.000F, -0.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(288, 0).addBox(-3.060F, -2.880F, -4.650F, 5.120F, 5.760F, 0.650F)
                .texOffs(288, 0).addBox(-4.304F, 4.030F, -3.850F, 4.608F, 5.440F, 0.650F)
                .texOffs(288, 0).addBox(-5.224F, -2.240F, -3.948F, 2.048F, 4.480F, 0.448F)
                .texOffs(288, 0).addBox(-5.036F, 7.990F, 0.870F, 3.072F, 3.520F, 0.630F)
                .texOffs(288, 0).addBox(-5.396F, 4.670F, -2.892F, 1.792F, 4.160F, 0.392F)
                .texOffs(288, 0).addBox(-4.268F, 7.360F, -0.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(-4.268F, 4.360F, -4.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(-4.268F, 1.360F, -0.780F, 1.536F, 1.280F, 0.280F)
                , PartPose.offset(-14.000F, -0.000F, -11.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.700F, 2.400F, 6.500F, 3.500F), PartPose.offsetAndRotation(-5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F), PartPose.offsetAndRotation(-5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, 0.5585F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -3.500F, -2.000F, 2.400F, 5.000F, 4.000F), PartPose.offsetAndRotation(-4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -3.330F, -2.036F, 1.536F, 4.160F, 0.336F), PartPose.offsetAndRotation(-5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -2.600F, -2.336F, 1.536F, 3.200F, 0.336F), PartPose.offsetAndRotation(-4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(288, 0).addBox(-0.640F, -2.510F, -1.780F, 1.280F, 3.520F, 0.280F), PartPose.offsetAndRotation(-5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, 0.5585F));
        return part;
    }
    private static PartDefinition build_forearm_left(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("forearm_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.200F, -0.800F, -6.500F, 8.400F, 9.500F, 9.000F)
                .texOffs(64, 0).addBox(-4.300F, -0.300F, -7.000F, 4.000F, 8.500F, 9.000F)
                .texOffs(0, 0).addBox(-4.400F, 5.700F, -8.000F, 8.800F, 4.500F, 10.000F)
                .texOffs(64, 0).addBox(-4.600F, 7.700F, -9.500F, 9.200F, 4.500F, 10.500F)
                .texOffs(128, 128).addBox(-3.900F, 0.700F, -5.000F, 1.200F, 6.500F, 5.000F)
                .texOffs(128, 128).addBox(-3.600F, 6.200F, -7.500F, 7.200F, 1.500F, 1.200F)
                .texOffs(128, 0).addBox(-4.200F, 3.700F, -6.000F, 3.000F, 4.000F, 3.500F)
                .texOffs(128, 0).addBox(-4.300F, 0.200F, -2.000F, 2.800F, 3.500F, 3.500F)
                .texOffs(0, 0).addBox(2.200F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(2.900F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 0).addBox(-1.300F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(-0.600F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 0).addBox(-4.800F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(-4.100F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 128).addBox(3.900F, 9.000F, -7.000F, 2.200F, 2.700F, 3.500F)
                .texOffs(128, 64).addBox(4.300F, 10.000F, -9.700F, 1.400F, 2.000F, 3.200F)
                .texOffs(0, 64).addBox(-3.800F, 10.400F, -7.500F, 7.600F, 1.800F, 7.500F)
                .texOffs(64, 64).addBox(-2.600F, 4.700F, 1.000F, 5.200F, 4.000F, 3.000F)
                .texOffs(0, 0).addBox(-2.200F, 6.200F, -4.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(64, 0).addBox(-2.200F, 3.200F, 0.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(0, 0).addBox(-2.200F, 0.200F, -4.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(288, 0).addBox(-2.688F, 0.910F, -7.150F, 5.376F, 6.080F, 0.650F)
                .texOffs(288, 0).addBox(-2.944F, 8.510F, -10.130F, 5.888F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(-2.816F, 6.510F, -8.630F, 5.632F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(-3.580F, 1.230F, -7.560F, 2.560F, 5.440F, 0.560F)
                .texOffs(288, 0).addBox(-2.432F, 10.724F, -7.752F, 4.864F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-1.664F, 5.420F, 0.580F, 3.328F, 2.560F, 0.420F)
                .texOffs(288, 32).addBox(-3.660F, -0.600F, -4.000F, 0.720F, 1.300F, 3.000F)
                .texOffs(288, 0).addBox(-3.660F, 4.420F, -6.420F, 1.920F, 2.560F, 0.420F)
                .texOffs(288, 0).addBox(2.668F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(-0.832F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(-4.332F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(-3.796F, 0.830F, -2.392F, 1.792F, 2.240F, 0.392F)
                .texOffs(288, 0).addBox(4.296F, 9.486F, -7.308F, 1.408F, 1.728F, 0.308F)
                .texOffs(288, 0).addBox(-2.304F, 6.470F, -7.750F, 4.608F, 0.960F, 0.250F)
                .texOffs(288, 0).addBox(-1.768F, 6.560F, -4.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(-1.768F, 3.560F, -0.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(-1.768F, 0.560F, -4.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(4.552F, 9.750F, -9.124F, 0.896F, 0.250F, 2.048F)
                , PartPose.offset(-2.500F, 9.800F, -0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_arm_right(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("arm_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.500F, -4.500F, -4.000F, 8.000F, 9.000F, 8.000F)
                .texOffs(64, 64).addBox(-1.600F, 2.500F, -3.200F, 7.200F, 8.500F, 7.000F)
                .texOffs(64, 0).addBox(2.600F, -3.500F, -3.500F, 3.200F, 7.000F, 7.000F)
                .texOffs(0, 0).addBox(3.100F, 3.500F, -2.500F, 2.800F, 6.500F, 5.500F)
                .texOffs(64, 0).addBox(1.100F, 7.000F, 1.500F, 4.800F, 5.500F, 4.500F)
                .texOffs(64, 0).addBox(2.300F, 7.000F, -0.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(0, 0).addBox(2.300F, 4.000F, -4.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(64, 0).addBox(2.300F, 1.000F, -0.500F, 2.400F, 2.000F, 2.500F)
                .texOffs(288, 0).addBox(-2.060F, -2.880F, -4.650F, 5.120F, 5.760F, 0.650F)
                .texOffs(288, 0).addBox(-0.304F, 4.030F, -3.850F, 4.608F, 5.440F, 0.650F)
                .texOffs(288, 0).addBox(3.176F, -2.240F, -3.948F, 2.048F, 4.480F, 0.448F)
                .texOffs(288, 0).addBox(1.964F, 7.990F, 0.870F, 3.072F, 3.520F, 0.630F)
                .texOffs(288, 0).addBox(3.604F, 4.670F, -2.892F, 1.792F, 4.160F, 0.392F)
                .texOffs(288, 0).addBox(2.732F, 7.360F, -0.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(2.732F, 4.360F, -4.780F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(2.732F, 1.360F, -0.780F, 1.536F, 1.280F, 0.280F)
                , PartPose.offset(14.000F, -0.000F, -11.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.700F, 2.400F, 6.500F, 3.500F), PartPose.offsetAndRotation(5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F), PartPose.offsetAndRotation(5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, -0.5585F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -3.500F, -2.000F, 2.400F, 5.000F, 4.000F), PartPose.offsetAndRotation(4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -3.330F, -2.036F, 1.536F, 4.160F, 0.336F), PartPose.offsetAndRotation(5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -2.600F, -2.336F, 1.536F, 3.200F, 0.336F), PartPose.offsetAndRotation(4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(288, 0).addBox(-0.640F, -2.510F, -1.780F, 1.280F, 3.520F, 0.280F), PartPose.offsetAndRotation(5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, -0.5585F));
        return part;
    }
    private static PartDefinition build_forearm_right(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("forearm_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.200F, -0.800F, -6.500F, 8.400F, 9.500F, 9.000F)
                .texOffs(64, 0).addBox(0.300F, -0.300F, -7.000F, 4.000F, 8.500F, 9.000F)
                .texOffs(0, 0).addBox(-4.400F, 5.700F, -8.000F, 8.800F, 4.500F, 10.000F)
                .texOffs(64, 0).addBox(-4.600F, 7.700F, -9.500F, 9.200F, 4.500F, 10.500F)
                .texOffs(128, 128).addBox(2.700F, 0.700F, -5.000F, 1.200F, 6.500F, 5.000F)
                .texOffs(128, 128).addBox(-3.600F, 6.200F, -7.500F, 7.200F, 1.500F, 1.200F)
                .texOffs(128, 0).addBox(1.200F, 3.700F, -6.000F, 3.000F, 4.000F, 3.500F)
                .texOffs(128, 0).addBox(1.500F, 0.200F, -2.000F, 2.800F, 3.500F, 3.500F)
                .texOffs(0, 0).addBox(-4.800F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(-4.100F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 0).addBox(-1.300F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(-0.600F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 0).addBox(2.200F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F)
                .texOffs(128, 0).addBox(2.900F, 8.000F, -13.000F, 1.200F, 1.400F, 4.000F)
                .texOffs(0, 128).addBox(-6.100F, 9.000F, -7.000F, 2.200F, 2.700F, 3.500F)
                .texOffs(128, 64).addBox(-5.700F, 10.000F, -9.700F, 1.400F, 2.000F, 3.200F)
                .texOffs(0, 64).addBox(-3.800F, 10.400F, -7.500F, 7.600F, 1.800F, 7.500F)
                .texOffs(64, 64).addBox(-2.600F, 4.700F, 1.000F, 5.200F, 4.000F, 3.000F)
                .texOffs(0, 0).addBox(-0.200F, 6.200F, -4.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(64, 0).addBox(-0.200F, 3.200F, 0.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(0, 0).addBox(-0.200F, 0.200F, -4.000F, 2.400F, 2.000F, 2.500F)
                .texOffs(288, 0).addBox(-2.688F, 0.910F, -7.150F, 5.376F, 6.080F, 0.650F)
                .texOffs(288, 0).addBox(-2.944F, 8.510F, -10.130F, 5.888F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(-2.816F, 6.510F, -8.630F, 5.632F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(1.020F, 1.230F, -7.560F, 2.560F, 5.440F, 0.560F)
                .texOffs(288, 0).addBox(-2.432F, 10.724F, -7.752F, 4.864F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-1.664F, 5.420F, 0.580F, 3.328F, 2.560F, 0.420F)
                .texOffs(288, 32).addBox(2.940F, -0.600F, -4.000F, 0.720F, 1.300F, 3.000F)
                .texOffs(288, 0).addBox(1.740F, 4.420F, -6.420F, 1.920F, 2.560F, 0.420F)
                .texOffs(288, 0).addBox(-4.332F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(-0.832F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(2.668F, 9.212F, -12.364F, 1.664F, 2.176F, 0.364F)
                .texOffs(288, 0).addBox(2.004F, 0.830F, -2.392F, 1.792F, 2.240F, 0.392F)
                .texOffs(288, 0).addBox(-5.704F, 9.486F, -7.308F, 1.408F, 1.728F, 0.308F)
                .texOffs(288, 0).addBox(-2.304F, 6.470F, -7.750F, 4.608F, 0.960F, 0.250F)
                .texOffs(288, 0).addBox(0.232F, 6.560F, -4.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(0.232F, 3.560F, -0.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(0.232F, 0.560F, -4.280F, 1.536F, 1.280F, 0.280F)
                .texOffs(288, 0).addBox(-5.448F, 9.750F, -9.124F, 0.896F, 0.250F, 2.048F)
                , PartPose.offset(2.500F, 9.800F, -0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -0.960F, -2.508F, 1.408F, 1.920F, 0.308F), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(288, 0).addBox(-0.448F, -1.250F, -1.024F, 0.896F, 0.250F, 2.048F), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_leg_left(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("leg_left", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-4.300F, -3.500F, -4.500F, 7.600F, 11.000F, 10.000F)
                .texOffs(0, 0).addBox(-5.000F, -2.800F, -4.000F, 3.600F, 8.800F, 9.000F)
                .texOffs(64, 0).addBox(-5.200F, -1.500F, -2.500F, 2.800F, 6.000F, 6.000F)
                .texOffs(0, 0).addBox(-4.200F, 4.000F, -6.000F, 6.400F, 6.500F, 6.000F)
                .texOffs(128, 0).addBox(-2.900F, 4.500F, -8.000F, 2.800F, 4.000F, 3.500F)
                .texOffs(128, 128).addBox(-4.900F, -0.500F, -2.500F, 1.000F, 6.000F, 5.000F)
                .texOffs(0, 0).addBox(-3.700F, 1.500F, 0.500F, 2.400F, 2.500F, 3.000F)
                .texOffs(288, 0).addBox(-2.932F, -1.520F, -5.150F, 4.864F, 7.040F, 0.650F)
                .texOffs(288, 0).addBox(-4.352F, -1.216F, -4.504F, 2.304F, 5.632F, 0.504F)
                .texOffs(288, 0).addBox(-3.048F, 5.170F, -6.650F, 4.096F, 4.160F, 0.650F)
                .texOffs(288, 0).addBox(-4.696F, -0.420F, -2.892F, 1.792F, 3.840F, 0.392F)
                .texOffs(288, 32).addBox(-4.700F, -1.700F, -1.500F, 0.600F, 1.200F, 3.000F)
                .texOffs(288, 0).addBox(-2.396F, 5.220F, -8.392F, 1.792F, 2.560F, 0.392F)
                .texOffs(288, 0).addBox(-3.268F, 1.950F, 0.164F, 1.536F, 1.600F, 0.336F)
                , PartPose.offset(-12.000F, 2.000F, 12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.800F, 2.400F, 6.500F, 3.500F), PartPose.offsetAndRotation(-4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F), PartPose.offsetAndRotation(-4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, 0.3840F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -3.330F, -2.136F, 1.536F, 4.160F, 0.336F), PartPose.offsetAndRotation(-4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(288, 0).addBox(-0.640F, -2.510F, -1.780F, 1.280F, 3.520F, 0.280F), PartPose.offsetAndRotation(-4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, 0.3840F));
        return part;
    }
    private static PartDefinition build_shin_left(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("shin_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.400F, -0.700F, -3.500F, 6.800F, 9.000F, 7.200F)
                .texOffs(64, 0).addBox(-3.900F, -0.000F, -3.000F, 3.000F, 7.300F, 6.000F)
                .texOffs(64, 0).addBox(-2.600F, -1.200F, 2.000F, 5.200F, 5.000F, 4.000F)
                .texOffs(128, 64).addBox(-1.100F, -1.200F, 5.000F, 2.200F, 3.500F, 3.300F)
                .texOffs(0, 0).addBox(-4.000F, 7.300F, -7.000F, 8.000F, 4.500F, 10.000F)
                .texOffs(0, 64).addBox(-3.400F, 10.200F, -6.000F, 6.800F, 1.600F, 8.000F)
                .texOffs(128, 0).addBox(-4.000F, 5.300F, -2.500F, 2.400F, 3.000F, 3.500F)
                .texOffs(64, 0).addBox(-3.500F, 6.200F, -6.500F, 7.000F, 1.800F, 3.000F)
                .texOffs(0, 0).addBox(-2.700F, 2.300F, -1.500F, 2.400F, 2.500F, 3.000F)
                .texOffs(0, 0).addBox(1.700F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(1.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(2.200F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(2.300F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 0).addBox(-1.100F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(-0.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(-0.600F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(-0.500F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 0).addBox(-3.900F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(-3.700F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(-3.400F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(-3.300F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 128).addBox(-1.400F, 8.800F, 2.000F, 2.800F, 2.600F, 3.300F)
                .texOffs(128, 64).addBox(-0.900F, 9.400F, 4.700F, 1.800F, 1.800F, 2.600F)
                .texOffs(288, 0).addBox(-2.176F, 0.920F, -4.150F, 4.352F, 5.760F, 0.650F)
                .texOffs(288, 0).addBox(-2.560F, 8.110F, -7.630F, 5.120F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(-3.360F, 1.314F, -3.420F, 1.920F, 4.672F, 0.420F)
                .texOffs(288, 0).addBox(-2.176F, 10.488F, -6.250F, 4.352F, 1.024F, 0.250F)
                .texOffs(288, 0).addBox(-1.664F, -0.300F, 1.440F, 3.328F, 3.200F, 0.560F)
                .texOffs(288, 0).addBox(-2.240F, 6.524F, -6.752F, 4.480F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-0.704F, -0.570F, 4.692F, 1.408F, 2.240F, 0.308F)
                .texOffs(288, 0).addBox(-3.568F, 5.840F, -2.836F, 1.536F, 1.920F, 0.336F)
                .texOffs(288, 0).addBox(-0.896F, 9.268F, 1.636F, 1.792F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(2.096F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(-0.704F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(-3.504F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(-2.268F, 2.750F, -1.836F, 1.536F, 1.600F, 0.336F)
                .texOffs(288, 0).addBox(-0.576F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                .texOffs(288, 0).addBox(2.224F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                .texOffs(288, 0).addBox(-3.376F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                , PartPose.offset(-1.000F, 8.200F, 1.500F));
        return part;
    }
    private static PartDefinition build_leg_right(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("leg_right", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-3.300F, -3.500F, -4.500F, 7.600F, 11.000F, 10.000F)
                .texOffs(0, 0).addBox(1.400F, -2.800F, -4.000F, 3.600F, 8.800F, 9.000F)
                .texOffs(64, 0).addBox(2.400F, -1.500F, -2.500F, 2.800F, 6.000F, 6.000F)
                .texOffs(0, 0).addBox(-2.200F, 4.000F, -6.000F, 6.400F, 6.500F, 6.000F)
                .texOffs(128, 0).addBox(0.100F, 4.500F, -8.000F, 2.800F, 4.000F, 3.500F)
                .texOffs(128, 128).addBox(3.900F, -0.500F, -2.500F, 1.000F, 6.000F, 5.000F)
                .texOffs(0, 0).addBox(1.300F, 1.500F, 0.500F, 2.400F, 2.500F, 3.000F)
                .texOffs(288, 0).addBox(-1.932F, -1.520F, -5.150F, 4.864F, 7.040F, 0.650F)
                .texOffs(288, 0).addBox(2.048F, -1.216F, -4.504F, 2.304F, 5.632F, 0.504F)
                .texOffs(288, 0).addBox(-1.048F, 5.170F, -6.650F, 4.096F, 4.160F, 0.650F)
                .texOffs(288, 0).addBox(2.904F, -0.420F, -2.892F, 1.792F, 3.840F, 0.392F)
                .texOffs(288, 32).addBox(4.100F, -1.700F, -1.500F, 0.600F, 1.200F, 3.000F)
                .texOffs(288, 0).addBox(0.604F, 5.220F, -8.392F, 1.792F, 2.560F, 0.392F)
                .texOffs(288, 0).addBox(1.732F, 1.950F, 0.164F, 1.536F, 1.600F, 0.336F)
                , PartPose.offset(12.000F, 2.000F, 12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.800F, 2.400F, 6.500F, 3.500F), PartPose.offsetAndRotation(4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F), PartPose.offsetAndRotation(4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, -0.3840F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -3.330F, -2.136F, 1.536F, 4.160F, 0.336F), PartPose.offsetAndRotation(4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(288, 0).addBox(-0.640F, -2.510F, -1.780F, 1.280F, 3.520F, 0.280F), PartPose.offsetAndRotation(4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, -0.3840F));
        return part;
    }
    private static PartDefinition build_shin_right(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("shin_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.400F, -0.700F, -3.500F, 6.800F, 9.000F, 7.200F)
                .texOffs(64, 0).addBox(0.900F, -0.000F, -3.000F, 3.000F, 7.300F, 6.000F)
                .texOffs(64, 0).addBox(-2.600F, -1.200F, 2.000F, 5.200F, 5.000F, 4.000F)
                .texOffs(128, 64).addBox(-1.100F, -1.200F, 5.000F, 2.200F, 3.500F, 3.300F)
                .texOffs(0, 0).addBox(-4.000F, 7.300F, -7.000F, 8.000F, 4.500F, 10.000F)
                .texOffs(0, 64).addBox(-3.400F, 10.200F, -6.000F, 6.800F, 1.600F, 8.000F)
                .texOffs(128, 0).addBox(1.600F, 5.300F, -2.500F, 2.400F, 3.000F, 3.500F)
                .texOffs(64, 0).addBox(-3.500F, 6.200F, -6.500F, 7.000F, 1.800F, 3.000F)
                .texOffs(0, 0).addBox(0.300F, 2.300F, -1.500F, 2.400F, 2.500F, 3.000F)
                .texOffs(0, 0).addBox(-3.900F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(-3.700F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(-3.400F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(-3.300F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 0).addBox(-1.100F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(-0.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(-0.600F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(-0.500F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 0).addBox(1.700F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F)
                .texOffs(0, 128).addBox(1.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F)
                .texOffs(128, 64).addBox(2.200F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F)
                .texOffs(128, 0).addBox(2.300F, 8.000F, -10.000F, 1.000F, 1.300F, 3.000F)
                .texOffs(0, 128).addBox(-1.400F, 8.800F, 2.000F, 2.800F, 2.600F, 3.300F)
                .texOffs(128, 64).addBox(-0.900F, 9.400F, 4.700F, 1.800F, 1.800F, 2.600F)
                .texOffs(288, 0).addBox(-2.176F, 0.920F, -4.150F, 4.352F, 5.760F, 0.650F)
                .texOffs(288, 0).addBox(-2.560F, 8.110F, -7.630F, 5.120F, 2.880F, 0.630F)
                .texOffs(288, 0).addBox(1.440F, 1.314F, -3.420F, 1.920F, 4.672F, 0.420F)
                .texOffs(288, 0).addBox(-2.176F, 10.488F, -6.250F, 4.352F, 1.024F, 0.250F)
                .texOffs(288, 0).addBox(-1.664F, -0.300F, 1.440F, 3.328F, 3.200F, 0.560F)
                .texOffs(288, 0).addBox(-2.240F, 6.524F, -6.752F, 4.480F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-0.704F, -0.570F, 4.692F, 1.408F, 2.240F, 0.308F)
                .texOffs(288, 0).addBox(2.032F, 5.840F, -2.836F, 1.536F, 1.920F, 0.336F)
                .texOffs(288, 0).addBox(-0.896F, 9.268F, 1.636F, 1.792F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(-3.504F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(-0.704F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(2.096F, 9.140F, -9.608F, 1.408F, 1.920F, 0.308F)
                .texOffs(288, 0).addBox(0.732F, 2.750F, -1.836F, 1.536F, 1.600F, 0.336F)
                .texOffs(288, 0).addBox(-0.576F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                .texOffs(288, 0).addBox(-3.376F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                .texOffs(288, 0).addBox(2.224F, 9.750F, -12.552F, 1.152F, 1.600F, 0.252F)
                , PartPose.offset(1.000F, 8.200F, 1.500F));
        return part;
    }
    private static PartDefinition build_tail_1(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("tail_1", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-7.000F, -5.400F, -1.000F, 14.000F, 10.800F, 12.500F)
                .texOffs(0, 64).addBox(-5.600F, 4.600F, 0.000F, 11.200F, 1.800F, 10.500F)
                .texOffs(0, 0).addBox(-6.300F, -7.400F, 0.000F, 12.600F, 2.600F, 5.500F)
                .texOffs(64, 0).addBox(-5.950F, -7.200F, 5.000F, 11.900F, 2.400F, 5.800F)
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 0.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(-8.500F, -3.240F, 1.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 3.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(-8.500F, -3.240F, 4.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 6.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(-8.500F, -3.240F, 7.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(0, 0).addBox(6.300F, -3.240F, 0.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(6.300F, -3.240F, 1.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(0, 0).addBox(6.300F, -3.240F, 3.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(6.300F, -3.240F, 4.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(0, 0).addBox(6.300F, -3.240F, 6.300F, 2.200F, 6.480F, 2.400F)
                .texOffs(64, 0).addBox(6.300F, -3.240F, 7.800F, 2.200F, 6.480F, 2.400F)
                .texOffs(288, 0).addBox(-4.480F, -3.456F, -1.650F, 8.960F, 6.912F, 0.650F)
                .texOffs(288, 0).addBox(-3.584F, 4.924F, -0.252F, 7.168F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-4.032F, -6.932F, -0.364F, 8.064F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(-3.808F, -6.768F, 4.664F, 7.616F, 1.536F, 0.336F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, -0.008F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, 1.492F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, 2.992F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, 4.492F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, 5.992F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(-8.104F, -2.074F, 7.492F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, -0.008F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, 1.492F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, 2.992F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, 4.492F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, 5.992F, 1.408F, 4.148F, 0.308F)
                .texOffs(288, 0).addBox(6.696F, -2.074F, 7.492F, 1.408F, 4.148F, 0.308F)
                , PartPose.offset(0.000F, 2.500F, 18.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F), PartPose.offsetAndRotation(0.000F, -7.400F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F), PartPose.offsetAndRotation(0.000F, -7.400F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 3.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 6.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-8.800F, -1.400F, 9.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 3.000F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 6.000F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(8.800F, -1.400F, 9.000F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(288, 0).addBox(-0.832F, -2.726F, -1.864F, 1.664F, 2.752F, 0.364F), PartPose.offsetAndRotation(0.000F, -7.400F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -2.116F, -1.808F, 1.408F, 2.432F, 0.308F), PartPose.offsetAndRotation(0.000F, -7.400F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 3.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 6.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-8.800F, -1.400F, 9.000F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 3.000F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 6.000F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(8.800F, -1.400F, 9.000F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_2(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("tail_2", CubeListBuilder.create()
                .texOffs(128, 128).addBox(5.200F, -1.000F, 2.000F, 1.000F, 2.000F, 6.500F)
                .texOffs(128, 128).addBox(-6.200F, -1.000F, 2.000F, 1.000F, 2.000F, 6.500F)
                .texOffs(64, 64).addBox(-5.600F, -4.600F, -1.000F, 11.200F, 9.200F, 12.500F)
                .texOffs(0, 64).addBox(-4.480F, 3.800F, 0.000F, 8.960F, 1.800F, 10.500F)
                .texOffs(0, 0).addBox(-5.040F, -6.600F, 0.000F, 10.080F, 2.600F, 5.500F)
                .texOffs(64, 0).addBox(-4.760F, -6.400F, 5.000F, 9.520F, 2.400F, 5.800F)
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 0.300F, 2.200F, 5.520F, 2.400F)
                .texOffs(64, 0).addBox(-7.100F, -2.760F, 2.100F, 2.200F, 5.520F, 2.400F)
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 3.900F, 2.200F, 5.520F, 2.400F)
                .texOffs(64, 0).addBox(-7.100F, -2.760F, 5.700F, 2.200F, 5.520F, 2.400F)
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 7.500F, 2.200F, 5.520F, 2.400F)
                .texOffs(0, 0).addBox(4.900F, -2.760F, 0.300F, 2.200F, 5.520F, 2.400F)
                .texOffs(64, 0).addBox(4.900F, -2.760F, 2.100F, 2.200F, 5.520F, 2.400F)
                .texOffs(0, 0).addBox(4.900F, -2.760F, 3.900F, 2.200F, 5.520F, 2.400F)
                .texOffs(64, 0).addBox(4.900F, -2.760F, 5.700F, 2.200F, 5.520F, 2.400F)
                .texOffs(0, 0).addBox(4.900F, -2.760F, 7.500F, 2.200F, 5.520F, 2.400F)
                .texOffs(288, 0).addBox(-3.584F, -2.944F, -1.650F, 7.168F, 5.888F, 0.650F)
                .texOffs(288, 0).addBox(-2.867F, 4.124F, -0.252F, 5.734F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-3.226F, -6.132F, -0.364F, 6.452F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(-3.046F, -5.968F, 4.664F, 6.092F, 1.536F, 0.336F)
                .texOffs(288, 0).addBox(-6.704F, -1.766F, -0.008F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(-6.704F, -1.766F, 1.792F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(-6.704F, -1.766F, 3.592F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(-6.704F, -1.766F, 5.392F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(-6.704F, -1.766F, 7.192F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(5.296F, -1.766F, -0.008F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(5.296F, -1.766F, 1.792F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(5.296F, -1.766F, 3.592F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(5.296F, -1.766F, 5.392F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 0).addBox(5.296F, -1.766F, 7.192F, 1.408F, 3.532F, 0.308F)
                .texOffs(288, 32).addBox(-6.000F, -1.400F, 3.300F, 0.600F, 0.400F, 3.900F)
                .texOffs(288, 32).addBox(5.400F, -1.400F, 3.300F, 0.600F, 0.400F, 3.900F)
                , PartPose.offset(0.000F, 2.000F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F), PartPose.offsetAndRotation(0.000F, -6.600F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F), PartPose.offsetAndRotation(0.000F, -6.600F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-7.400F, -1.400F, 3.300F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-7.400F, -1.400F, 6.900F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(7.400F, -1.400F, 3.300F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(7.400F, -1.400F, 6.900F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(288, 0).addBox(-0.832F, -2.726F, -1.864F, 1.664F, 2.752F, 0.364F), PartPose.offsetAndRotation(0.000F, -6.600F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -2.116F, -1.808F, 1.408F, 2.432F, 0.308F), PartPose.offsetAndRotation(0.000F, -6.600F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-7.400F, -1.400F, 3.300F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(7.400F, -1.400F, 3.300F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-7.400F, -1.400F, 6.900F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(7.400F, -1.400F, 6.900F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_3(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("tail_3", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-4.400F, -3.800F, -1.000F, 8.800F, 7.600F, 12.500F)
                .texOffs(0, 64).addBox(-3.520F, 3.000F, 0.000F, 7.040F, 1.800F, 10.500F)
                .texOffs(0, 0).addBox(-3.960F, -5.800F, 0.000F, 7.920F, 2.600F, 5.500F)
                .texOffs(64, 0).addBox(-3.740F, -5.600F, 5.000F, 7.480F, 2.400F, 5.800F)
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 0.300F, 2.200F, 4.560F, 2.400F)
                .texOffs(64, 0).addBox(-5.900F, -2.280F, 2.100F, 2.200F, 4.560F, 2.400F)
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 3.900F, 2.200F, 4.560F, 2.400F)
                .texOffs(64, 0).addBox(-5.900F, -2.280F, 5.700F, 2.200F, 4.560F, 2.400F)
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 7.500F, 2.200F, 4.560F, 2.400F)
                .texOffs(0, 0).addBox(3.700F, -2.280F, 0.300F, 2.200F, 4.560F, 2.400F)
                .texOffs(64, 0).addBox(3.700F, -2.280F, 2.100F, 2.200F, 4.560F, 2.400F)
                .texOffs(0, 0).addBox(3.700F, -2.280F, 3.900F, 2.200F, 4.560F, 2.400F)
                .texOffs(64, 0).addBox(3.700F, -2.280F, 5.700F, 2.200F, 4.560F, 2.400F)
                .texOffs(0, 0).addBox(3.700F, -2.280F, 7.500F, 2.200F, 4.560F, 2.400F)
                .texOffs(288, 0).addBox(-2.816F, -2.432F, -1.650F, 5.632F, 4.864F, 0.650F)
                .texOffs(288, 0).addBox(-2.253F, 3.324F, -0.252F, 4.506F, 1.152F, 0.252F)
                .texOffs(288, 0).addBox(-2.534F, -5.332F, -0.364F, 5.068F, 1.664F, 0.364F)
                .texOffs(288, 0).addBox(-2.394F, -5.168F, 4.664F, 4.788F, 1.536F, 0.336F)
                .texOffs(288, 0).addBox(-5.504F, -1.459F, -0.008F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(4.096F, -1.459F, -0.008F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(-5.504F, -1.459F, 1.792F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(-5.504F, -1.459F, 3.592F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(-5.504F, -1.459F, 5.392F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(-5.504F, -1.459F, 7.192F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(4.096F, -1.459F, 1.792F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(4.096F, -1.459F, 3.592F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(4.096F, -1.459F, 5.392F, 1.408F, 2.918F, 0.308F)
                .texOffs(288, 0).addBox(4.096F, -1.459F, 7.192F, 1.408F, 2.918F, 0.308F)
                , PartPose.offset(0.000F, 1.500F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F), PartPose.offsetAndRotation(0.000F, -5.800F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F), PartPose.offsetAndRotation(0.000F, -5.800F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-6.200F, -1.400F, 3.300F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-6.200F, -1.400F, 6.900F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(-6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(6.200F, -1.400F, 3.300F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(128, 128).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(6.200F, -1.400F, 6.900F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F), PartPose.offsetAndRotation(6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(288, 0).addBox(-0.832F, -2.726F, -1.864F, 1.664F, 2.752F, 0.364F), PartPose.offsetAndRotation(0.000F, -5.800F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(288, 0).addBox(-0.704F, -2.116F, -1.808F, 1.408F, 2.432F, 0.308F), PartPose.offsetAndRotation(0.000F, -5.800F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-6.200F, -1.400F, 3.300F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(6.200F, -1.400F, 3.300F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(-6.200F, -1.400F, 6.900F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(288, 0).addBox(-0.576F, -1.296F, -1.152F, 1.152F, 1.792F, 0.252F), PartPose.offsetAndRotation(6.200F, -1.400F, 6.900F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_4(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("tail_4", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-3.800F, -3.500F, -1.000F, 7.600F, 7.000F, 11.000F)
                .texOffs(0, 0).addBox(-5.400F, -4.500F, 3.000F, 10.800F, 9.000F, 10.500F)
                .texOffs(128, 0).addBox(-2.600F, -2.400F, 13.000F, 5.200F, 4.800F, 5.500F)
                .texOffs(192, 0).addBox(-1.400F, -1.400F, 18.000F, 2.800F, 2.800F, 4.500F)
                .texOffs(0, 64).addBox(-3.200F, 3.200F, 0.000F, 6.400F, 1.600F, 11.000F)
                .texOffs(64, 0).addBox(4.400F, -1.200F, 3.600F, 2.400F, 2.400F, 2.800F)
                .texOffs(128, 0).addBox(7.350F, -0.850F, 4.000F, 1.700F, 1.700F, 2.000F)
                .texOffs(288, 0).addBox(-3.456F, -2.880F, 2.350F, 6.912F, 5.760F, 0.650F)
                .texOffs(288, 0).addBox(-2.432F, -2.240F, -1.650F, 4.864F, 4.480F, 0.650F)
                .texOffs(288, 0).addBox(-2.048F, 3.488F, -0.250F, 4.096F, 1.024F, 0.250F)
                .texOffs(288, 0).addBox(-1.664F, -1.536F, 12.350F, 3.328F, 3.072F, 0.650F)
                .texOffs(288, 0).addBox(-0.896F, -0.896F, 17.608F, 1.792F, 1.792F, 0.392F)
                .texOffs(288, 0).addBox(4.832F, -0.768F, 3.264F, 1.536F, 1.536F, 0.336F)
                , PartPose.offset(0.000F, 1.000F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-4.600F, -3.800F, -4.000F, 9.200F, 7.600F, 8.500F), PartPose.offsetAndRotation(0.000F, -0.000F, 8.000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(2.800F, -4.850F, 5.000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(4.100F, -7.101F, 5.000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-2.800F, -4.850F, 5.000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-4.100F, -7.101F, 5.000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-5.600F, -0.000F, 5.000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-8.200F, -0.000F, 5.000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-2.800F, 4.850F, 5.000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-4.100F, 7.101F, 5.000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(2.800F, 4.850F, 5.000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(4.100F, 7.101F, 5.000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(4.850F, -2.800F, 10.500F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(128, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(7.101F, -4.100F, 10.500F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(0.000F, -5.600F, 10.500F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(0.000F, -8.200F, 10.500F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-4.850F, -2.800F, 10.500F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(128, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-7.101F, -4.100F, 10.500F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-4.850F, 2.800F, 10.500F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-7.101F, 4.100F, 10.500F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(-0.000F, 5.600F, 10.500F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(128, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(-0.000F, 8.200F, 10.500F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -1.400F, 2.400F, 2.400F, 2.800F), PartPose.offsetAndRotation(4.850F, 2.800F, 10.500F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -0.850F, -1.000F, 1.700F, 1.700F, 2.000F), PartPose.offsetAndRotation(7.101F, 4.100F, 10.500F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(288, 0).addBox(-2.944F, -2.432F, -4.650F, 5.888F, 4.864F, 0.650F), PartPose.offsetAndRotation(0.000F, -0.000F, 8.000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(2.800F, -4.850F, 5.000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-2.800F, -4.850F, 5.000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(0.000F, -5.600F, 10.500F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-2.800F, 4.850F, 5.000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("c_28", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(2.800F, 4.850F, 5.000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("c_29", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-4.850F, 2.800F, 10.500F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("c_30", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-0.000F, 5.600F, 10.500F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("c_31", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(4.850F, 2.800F, 10.500F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("c_32", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(4.850F, -2.800F, 10.500F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_33", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-4.850F, -2.800F, 10.500F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("c_34", CubeListBuilder.create().texOffs(288, 0).addBox(-0.768F, -0.768F, -1.736F, 1.536F, 1.536F, 0.336F), PartPose.offsetAndRotation(-5.600F, -0.000F, 5.000F, -0.0000F, -0.0000F, 3.1416F));
        return part;
    }
    private static PartDefinition build_fx_anchor(PartDefinition parent) {
        PartDefinition part = parent.addOrReplaceChild("fx_anchor", CubeListBuilder.create()
                , PartPose.offset(0.000F, -6.000F, -41.250F));
        return part;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
