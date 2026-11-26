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
 * Phase-1 AAA 340-cube articulated Primo Geovishap model:
 * front-heavy muscular forelimbs, 4-petal blooming stone lotus tail tip, zero floating crystals/horns,
 * de-coplanar opaque geometry, and dedicated normal-inflated emissive layer for 100% zero Z-fighting.
 */
public class PrimoGeovishapModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("primo_geovishap"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_EMISSIVE =
            new ModelLayerLocation(TeyvatBossesConstants.id("primo_geovishap"), "emissive");

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

    public ModelPart getBone(String name) { return bones.get(name); }
    public PartPose getInitialPose(String name) { return initialPoses.get(name); }

    public void resetPose() {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            PartPose pose = initialPoses.get(e.getKey());
            if (pose != null) e.getValue().loadPose(pose);
            e.getValue().visible = true;
        }
    }

    public void copyPoseFrom(PrimoGeovishapModel<?> other) {
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
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(0.000F, 24.000F, 0.000F));
        PartDefinition b_body = build_body(b_root, inflate);
        PartDefinition b_torso = build_torso(b_body, inflate);
        PartDefinition b_neck = build_neck(b_torso, inflate);
        PartDefinition b_head = build_head(b_neck, inflate);
        build_jaw(b_head, inflate);
        PartDefinition b_arm_left = build_arm_left(b_torso, inflate);
        build_forearm_left(b_arm_left, inflate);
        PartDefinition b_arm_right = build_arm_right(b_torso, inflate);
        build_forearm_right(b_arm_right, inflate);
        PartDefinition b_leg_left = build_leg_left(b_torso, inflate);
        build_shin_left(b_leg_left, inflate);
        PartDefinition b_leg_right = build_leg_right(b_torso, inflate);
        build_shin_right(b_leg_right, inflate);
        PartDefinition b_tail_1 = build_tail_1(b_torso, inflate);
        PartDefinition b_tail_2 = build_tail_2(b_tail_1, inflate);
        PartDefinition b_tail_3 = build_tail_3(b_tail_2, inflate);
        build_tail_4(b_tail_3, inflate);
        build_fx_anchor(b_body, inflate);
        return LayerDefinition.create(mesh, 512, 512);
    }

    private static PartDefinition build_body(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("body", CubeListBuilder.create()
                , PartPose.offset(0.000F, -22.000F, 0.000F));
        return part;
    }
    private static PartDefinition build_torso(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-12.500F, -11.500F, -16.500F, 25.000F, 17.500F, 15.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 64).addBox(-11.200F, -9.500F, -2.000F, 22.400F, 16.000F, 12.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 64).addBox(-10.000F, -6.500F, 9.000F, 20.000F, 13.500F, 9.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 64).addBox(-10.200F, 4.500F, -15.500F, 20.400F, 4.000F, 13.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 64).addBox(-9.000F, 5.000F, -2.500F, 18.000F, 3.200F, 12.000F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 64).addBox(-7.800F, 5.200F, 9.000F, 15.600F, 2.600F, 8.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(-11.500F, -14.500F, -15.000F, 23.000F, 5.000F, 13.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-10.500F, -12.800F, -2.500F, 21.000F, 5.000F, 12.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 0).addBox(-9.200F, -9.800F, 9.000F, 18.400F, 4.600F, 9.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(-4.200F, -17.200F, -14.000F, 8.400F, 4.200F, 13.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(64, 0).addBox(-3.600F, -15.000F, -1.500F, 7.200F, 4.000F, 11.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-3.000F, -12.000F, 9.500F, 6.000F, 3.800F, 8.000F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(-14.800F, -13.000F, -16.000F, 7.600F, 8.000F, 9.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 0).addBox(-15.500F, -14.200F, -14.500F, 6.000F, 7.200F, 6.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 64).addBox(-9.800F, -1.500F, -18.200F, 7.200F, 7.000F, 3.700F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(-10.200F, -5.500F, -17.500F, 6.400F, 5.500F, 3.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-12.400F, -6.500F, 8.000F, 5.200F, 8.500F, 9.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(7.200F, -13.000F, -16.000F, 7.600F, 8.000F, 9.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 0).addBox(9.500F, -14.200F, -14.500F, 6.000F, 7.200F, 6.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 64).addBox(2.600F, -1.500F, -18.200F, 7.200F, 7.000F, 3.700F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(3.800F, -5.500F, -17.500F, 6.400F, 5.500F, 3.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(7.200F, -6.500F, 8.000F, 5.200F, 8.500F, 9.500F, new CubeDeformation(inflate + 0.015F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-2.200F, -9.500F, -2.200F, 4.400F, 11.000F, 4.400F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.150F, -9.800F, -1.150F, 2.300F, 3.200F, 2.300F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-2.700F, -10.500F, -2.700F, 5.400F, 12.000F, 5.400F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-2.850F, -11.400F, -2.850F, 5.700F, 12.800F, 5.700F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(192, 0).addBox(-1.350F, -13.200F, -1.350F, 2.700F, 3.400F, 2.700F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-2.600F, -10.200F, -2.600F, 5.200F, 11.600F, 5.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(-13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, 0.4189F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-2.100F, -10.500F, -2.100F, 4.200F, 12.000F, 4.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -10.600F, -1.100F, 2.200F, 3.200F, 2.200F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(128, 0).addBox(-1.800F, -8.500F, -1.800F, 3.600F, 10.000F, 3.600F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -6.500F, -1.500F, 3.000F, 8.000F, 3.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -7.200F, -0.850F, 1.700F, 2.800F, 1.700F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(128, 128).addBox(-2.220F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(-12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(128, 128).addBox(-2.220F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(128, 128).addBox(-2.220F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(128, 128).addBox(-2.220F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(128, 0).addBox(-2.200F, -9.500F, -2.200F, 4.400F, 11.000F, 4.400F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_20", CubeListBuilder.create().texOffs(192, 0).addBox(-1.150F, -9.800F, -1.150F, 2.300F, 3.200F, 2.300F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(11.500F, -12.500F, -13.500F, -0.5585F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("c_21", CubeListBuilder.create().texOffs(128, 0).addBox(-2.700F, -10.500F, -2.700F, 5.400F, 12.000F, 5.400F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(13.500F, -13.500F, -8.500F, -0.6283F, -0.0000F, -0.5236F));
        part.addOrReplaceChild("c_22", CubeListBuilder.create().texOffs(128, 0).addBox(-2.850F, -11.400F, -2.850F, 5.700F, 12.800F, 5.700F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_23", CubeListBuilder.create().texOffs(192, 0).addBox(-1.350F, -13.200F, -1.350F, 2.700F, 3.400F, 2.700F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(14.000F, -13.000F, -3.000F, -0.6632F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_24", CubeListBuilder.create().texOffs(128, 0).addBox(-2.600F, -10.200F, -2.600F, 5.200F, 11.600F, 5.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(13.000F, -11.800F, 2.500F, -0.6981F, -0.0000F, -0.4189F));
        part.addOrReplaceChild("c_25", CubeListBuilder.create().texOffs(128, 0).addBox(-2.100F, -10.500F, -2.100F, 4.200F, 12.000F, 4.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_26", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -10.600F, -1.100F, 2.200F, 3.200F, 2.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(11.500F, -10.000F, 7.500F, -0.7330F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_27", CubeListBuilder.create().texOffs(128, 0).addBox(-1.800F, -8.500F, -1.800F, 3.600F, 10.000F, 3.600F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(9.500F, -7.800F, 12.000F, -0.7679F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_28", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -6.500F, -1.500F, 3.000F, 8.000F, 3.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_29", CubeListBuilder.create().texOffs(192, 0).addBox(-0.850F, -7.200F, -0.850F, 1.700F, 2.800F, 1.700F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(7.500F, -5.800F, 16.000F, -0.7854F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_30", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_31", CubeListBuilder.create().texOffs(128, 128).addBox(1.520F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(12.800F, -2.500F, -13.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_32", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_33", CubeListBuilder.create().texOffs(128, 128).addBox(1.520F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(12.000F, -1.300F, -4.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_34", CubeListBuilder.create().texOffs(0, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_35", CubeListBuilder.create().texOffs(128, 128).addBox(1.520F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(11.200F, -0.100F, 4.000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_36", CubeListBuilder.create().texOffs(64, 0).addBox(-2.000F, -4.200F, -3.800F, 4.000F, 8.400F, 7.600F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_37", CubeListBuilder.create().texOffs(128, 128).addBox(1.520F, -0.950F, -2.300F, 0.700F, 1.900F, 4.600F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(10.400F, 1.100F, 12.500F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("c_38", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -15.500F, -14.500F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_39", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -14.100F, -7.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_40", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -8.000F, -1.400F, 3.200F, 9.000F, 2.800F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(0.000F, -12.700F, -0.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_41", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, -11.300F, 5.900F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_42", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -5.500F, -1.400F, 3.200F, 6.500F, 2.800F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(0.000F, -9.900F, 12.700F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_43", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_44", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_45", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_46", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(11.200F, 3.000F, -14.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_47", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(10.400F, 3.000F, -3.600F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_48", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -2.500F, -2.200F, 3.200F, 5.000F, 4.400F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(9.600F, 3.000F, 6.800F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_49", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -4.500F, -2.500F, 3.000F, 6.500F, 4.500F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_50", CubeListBuilder.create().texOffs(192, 0).addBox(-1.300F, -4.000F, -2.000F, 2.600F, 6.000F, 4.000F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, 0.5934F));
        part.addOrReplaceChild("c_51", CubeListBuilder.create().texOffs(128, 0).addBox(-1.400F, -4.000F, -2.500F, 2.800F, 6.000F, 5.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_52", CubeListBuilder.create().texOffs(128, 0).addBox(-1.500F, -4.500F, -2.500F, 3.000F, 6.500F, 4.500F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(13.800F, -12.000F, -13.000F, -0.3491F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_53", CubeListBuilder.create().texOffs(192, 0).addBox(-1.300F, -4.000F, -2.000F, 2.600F, 6.000F, 4.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(14.200F, -8.500F, -9.500F, -0.3840F, -0.0000F, -0.5934F));
        part.addOrReplaceChild("c_54", CubeListBuilder.create().texOffs(128, 0).addBox(-1.400F, -4.000F, -2.500F, 2.800F, 6.000F, 5.000F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(11.200F, -4.500F, 13.000F, -0.5236F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_55", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_56", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_57", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("c_58", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(6.800F, -12.000F, -13.500F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_59", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(6.100F, -10.200F, -2.300F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("c_60", CubeListBuilder.create().texOffs(0, 0).addBox(-3.000F, -1.600F, -2.600F, 6.000F, 3.000F, 5.200F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(5.400F, -8.400F, 8.900F, -0.0000F, -0.0000F, -0.2793F));
        return part;
    }
    private static PartDefinition build_neck(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-7.500F, -6.000F, -8.500F, 15.000F, 11.000F, 10.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 64).addBox(-6.200F, 4.000F, -8.000F, 12.400F, 2.500F, 9.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-6.800F, -8.500F, -7.500F, 13.600F, 3.500F, 8.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(-5.200F, -10.500F, -6.500F, 10.400F, 3.000F, 6.000F, new CubeDeformation(inflate + 0.006F))
                , PartPose.offset(0.000F, -5.000F, -16.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(-7.800F, 3.500F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-7.000F, 4.500F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-7.800F, -1.700F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-7.000F, -0.700F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-7.800F, -6.900F, -6.500F, -0.2094F, -0.3840F, 0.2793F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-7.000F, -5.900F, -6.500F, -0.4189F, -0.5236F, 0.4538F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(7.800F, 3.500F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(7.000F, 4.500F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(7.800F, -1.700F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(128, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(7.000F, -0.700F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(0, 0).addBox(-1.600F, -1.800F, -2.600F, 3.200F, 3.600F, 5.200F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(7.800F, -6.900F, -6.500F, -0.2094F, 0.3840F, -0.2793F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -1.100F, -1.800F, 2.200F, 2.200F, 4.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(7.000F, -5.900F, -6.500F, -0.4189F, 0.5236F, -0.4538F));
        return part;
    }
    private static PartDefinition build_head(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.500F, -4.500F, -10.000F, 13.000F, 7.700F, 11.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(-5.200F, -2.500F, -15.500F, 10.400F, 6.000F, 6.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(-3.600F, -1.800F, -17.200F, 7.200F, 4.800F, 2.700F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 0).addBox(-1.200F, -7.600F, -12.500F, 2.400F, 2.800F, 10.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(256, 128).addBox(-4.000F, 2.400F, -13.500F, 8.000F, 1.400F, 11.000F, new CubeDeformation(inflate + 0.015F))
                
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -15.100F, 1.200F, 3.200F, 1.200F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 64).addBox(-4.800F, 3.200F, -10.400F, 1.200F, 2.400F, 1.200F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-5.700F, -2.200F, -15.000F, 2.400F, 3.200F, 5.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 0).addBox(-4.200F, -4.000F, -14.500F, 2.400F, 2.000F, 6.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(128, 128).addBox(-5.300F, 0.800F, -13.500F, 1.000F, 1.200F, 6.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-6.400F, -5.800F, -7.000F, 3.200F, 2.300F, 7.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 64).addBox(-7.400F, -0.000F, -5.500F, 2.400F, 3.000F, 6.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 128).addBox(-3.200F, -2.400F, -17.500F, 1.400F, 1.600F, 2.000F, new CubeDeformation(inflate + 0.009F))
                
                .texOffs(128, 64).addBox(3.600F, 3.200F, -15.100F, 1.200F, 3.200F, 1.200F, new CubeDeformation(inflate + 0.000F))
                .texOffs(128, 64).addBox(3.600F, 3.200F, -10.400F, 1.200F, 2.400F, 1.200F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(3.300F, -2.200F, -15.000F, 2.400F, 3.200F, 5.000F, new CubeDeformation(inflate + 0.009F))
                .texOffs(64, 0).addBox(1.800F, -4.000F, -14.500F, 2.400F, 2.000F, 6.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(128, 128).addBox(4.300F, 0.800F, -13.500F, 1.000F, 1.200F, 6.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(3.200F, -5.800F, -7.000F, 3.200F, 2.300F, 7.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 64).addBox(5.000F, -0.000F, -5.500F, 2.400F, 3.000F, 6.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 128).addBox(1.800F, -2.400F, -17.500F, 1.400F, 1.600F, 2.000F, new CubeDeformation(inflate + 0.003F))
                , PartPose.offset(0.000F, -3.000F, -8.000F));
        part.addOrReplaceChild("eye_left", CubeListBuilder.create().texOffs(64, 128).addBox(-6.850F, -1.800F, -10.200F, 1.800F, 2.000F, 3.600F, new CubeDeformation(inflate + 0.020F)), PartPose.ZERO);
        part.addOrReplaceChild("eye_right", CubeListBuilder.create().texOffs(64, 128).addBox(5.050F, -1.800F, -10.200F, 1.800F, 2.000F, 3.600F, new CubeDeformation(inflate + 0.020F)), PartPose.ZERO);
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(64, 0).addBox(-7.100F, -3.800F, -11.000F, 3.000F, 2.200F, 7.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(0, 0).addBox(-7.700F, -5.000F, -7.000F, 2.400F, 2.600F, 6.500F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 64).addBox(-2.200F, -2.200F, -3.800F, 4.400F, 4.400F, 7.600F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-5.500F, -4.200F, -0.500F, -0.3142F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-1.900F, -1.900F, -3.800F, 3.800F, 3.800F, 7.600F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-7.800F, -6.800F, 4.000F, -0.3840F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -1.600F, -3.800F, 3.200F, 3.200F, 7.600F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-10.200F, -9.600F, 8.500F, -0.4538F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -3.800F, 2.400F, 2.400F, 7.600F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-12.200F, -12.600F, 12.500F, -0.5236F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(192, 0).addBox(-0.800F, -0.800F, -3.800F, 1.600F, 1.600F, 7.600F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-13.600F, -15.200F, 15.800F, -0.5934F, -0.3142F, 0.2094F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.400F, 1.500F, -2.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(-7.700F, -2.500F, 4.000F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_9", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-4.200F, 1.200F, -12.500F, -0.2618F, -0.4887F, 0.3491F));
        part.addOrReplaceChild("c_10", CubeListBuilder.create().texOffs(64, 0).addBox(4.100F, -3.800F, -11.000F, 3.000F, 2.200F, 7.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("c_11", CubeListBuilder.create().texOffs(0, 0).addBox(5.300F, -5.000F, -7.000F, 2.400F, 2.600F, 6.500F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("c_12", CubeListBuilder.create().texOffs(128, 64).addBox(-2.200F, -2.200F, -3.800F, 4.400F, 4.400F, 7.600F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(5.500F, -4.200F, -0.500F, -0.3142F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_13", CubeListBuilder.create().texOffs(128, 64).addBox(-1.900F, -1.900F, -3.800F, 3.800F, 3.800F, 7.600F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(7.800F, -6.800F, 4.000F, -0.3840F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_14", CubeListBuilder.create().texOffs(128, 0).addBox(-1.600F, -1.600F, -3.800F, 3.200F, 3.200F, 7.600F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(10.200F, -9.600F, 8.500F, -0.4538F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_15", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -1.200F, -3.800F, 2.400F, 2.400F, 7.600F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(12.200F, -12.600F, 12.500F, -0.5236F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_16", CubeListBuilder.create().texOffs(192, 0).addBox(-0.800F, -0.800F, -3.800F, 1.600F, 1.600F, 7.600F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(13.600F, -15.200F, 15.800F, -0.5934F, 0.3142F, -0.2094F));
        part.addOrReplaceChild("c_17", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(5.400F, 1.500F, -2.500F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_18", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(7.700F, -2.500F, 4.000F, -0.2618F, 0.4887F, -0.3491F));
        part.addOrReplaceChild("c_19", CubeListBuilder.create().texOffs(64, 0).addBox(-1.200F, -1.200F, -2.200F, 2.400F, 2.400F, 4.400F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(4.200F, 1.200F, -12.500F, -0.2618F, 0.4887F, -0.3491F));
        return part;
    }
    private static PartDefinition build_jaw(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.200F, -0.000F, -11.000F, 10.400F, 3.500F, 11.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(64, 0).addBox(-4.200F, 2.500F, -12.200F, 8.400F, 2.300F, 9.200F, new CubeDeformation(inflate + 0.009F))
                .texOffs(256, 128).addBox(-3.800F, -0.200F, -9.000F, 7.600F, 1.700F, 8.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(256, 128).addBox(-2.400F, -0.800F, -7.000F, 4.800F, 1.800F, 5.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 64).addBox(-4.400F, -2.500F, -10.800F, 1.200F, 2.700F, 1.200F, new CubeDeformation(inflate + 0.018F))
                .texOffs(128, 64).addBox(-4.400F, -1.900F, -6.600F, 1.200F, 2.100F, 1.200F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 64).addBox(-4.400F, 3.600F, -11.000F, 2.400F, 1.600F, 5.000F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 64).addBox(3.200F, -2.500F, -10.800F, 1.200F, 2.700F, 1.200F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 64).addBox(3.200F, -1.900F, -6.600F, 1.200F, 2.100F, 1.200F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 64).addBox(2.000F, 3.600F, -11.000F, 2.400F, 1.600F, 5.000F, new CubeDeformation(inflate + 0.006F))
                , PartPose.offset(0.000F, 4.000F, -1.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(64, 0).addBox(-5.700F, 1.500F, -8.500F, 1.800F, 3.000F, 3.000F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 0).addBox(-6.000F, 0.800F, -3.500F, 1.600F, 3.000F, 3.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 64).addBox(-3.000F, 3.800F, -12.800F, 2.000F, 1.700F, 3.300F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(64, 0).addBox(3.900F, 1.500F, -8.500F, 1.800F, 3.000F, 3.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.3491F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(4.400F, 0.800F, -3.500F, 1.600F, 3.000F, 3.000F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.0000F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(1.000F, 3.800F, -12.800F, 2.000F, 1.700F, 3.300F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.3142F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_arm_left(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("arm_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.060F, -4.500F, -4.480F, 9.120F, 9.000F, 8.960F, new CubeDeformation(inflate + 0.012F))
                .texOffs(64, 64).addBox(-5.600F, 2.500F, -3.200F, 7.200F, 8.500F, 7.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 0).addBox(-5.800F, -3.500F, -3.500F, 3.200F, 7.000F, 7.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-5.900F, 3.500F, -2.500F, 2.800F, 6.500F, 5.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 0).addBox(-5.900F, 7.000F, 1.500F, 4.800F, 5.500F, 4.500F, new CubeDeformation(inflate + 0.009F))
                , PartPose.offset(-14.000F, -0.000F, -11.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.700F, 2.400F, 6.500F, 3.500F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, 0.5585F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -3.500F, -2.000F, 2.400F, 5.000F, 4.000F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, 0.3142F));
        return part;
    }
    private static PartDefinition build_forearm_left(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("forearm_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.788F, -0.800F, -7.040F, 9.576F, 9.500F, 10.080F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 0).addBox(-4.580F, -0.300F, -7.540F, 4.560F, 8.500F, 10.080F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-5.016F, 5.700F, -8.600F, 10.032F, 4.500F, 11.200F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 0).addBox(-5.244F, 7.700F, -10.130F, 10.488F, 4.500F, 11.760F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 128).addBox(-3.900F, 0.700F, -5.000F, 1.200F, 6.500F, 5.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 128).addBox(-3.600F, 6.200F, -7.500F, 7.200F, 1.500F, 1.200F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 0).addBox(-4.200F, 3.700F, -6.000F, 3.000F, 4.000F, 3.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(128, 0).addBox(-4.300F, 0.200F, -2.000F, 2.800F, 3.500F, 3.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(2.200F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-1.300F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(-4.800F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 128).addBox(3.900F, 9.000F, -7.000F, 2.200F, 2.700F, 3.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 64).addBox(4.300F, 10.000F, -9.700F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 64).addBox(-3.800F, 10.400F, -7.500F, 7.600F, 1.800F, 7.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(64, 64).addBox(-2.600F, 4.700F, 1.000F, 5.200F, 4.000F, 3.000F, new CubeDeformation(inflate + 0.012F))
                , PartPose.offset(-2.500F, 9.800F, -0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_arm_right(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("arm_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.060F, -4.500F, -4.480F, 9.120F, 9.000F, 8.960F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 64).addBox(-1.600F, 2.500F, -3.200F, 7.200F, 8.500F, 7.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 0).addBox(2.600F, -3.500F, -3.500F, 3.200F, 7.000F, 7.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(3.100F, 3.500F, -2.500F, 2.800F, 6.500F, 5.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(1.100F, 7.000F, 1.500F, 4.800F, 5.500F, 4.500F, new CubeDeformation(inflate + 0.012F))
                , PartPose.offset(14.000F, -0.000F, -11.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.700F, 2.400F, 6.500F, 3.500F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(5.200F, -0.500F, 0.200F, -0.2618F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(5.500F, 5.500F, 2.500F, -0.4363F, -0.0000F, -0.5585F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -3.500F, -2.000F, 2.400F, 5.000F, 4.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(4.000F, 9.000F, 6.500F, -0.6109F, -0.0000F, -0.3142F));
        return part;
    }
    private static PartDefinition build_forearm_right(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("forearm_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.788F, -0.800F, -7.040F, 9.576F, 9.500F, 10.080F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 0).addBox(0.020F, -0.300F, -7.540F, 4.560F, 8.500F, 10.080F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-5.016F, 5.700F, -8.600F, 10.032F, 4.500F, 11.200F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(-5.244F, 7.700F, -10.130F, 10.488F, 4.500F, 11.760F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 128).addBox(2.700F, 0.700F, -5.000F, 1.200F, 6.500F, 5.000F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 128).addBox(-3.600F, 6.200F, -7.500F, 7.200F, 1.500F, 1.200F, new CubeDeformation(inflate + 0.012F))
                .texOffs(128, 0).addBox(1.200F, 3.700F, -6.000F, 3.000F, 4.000F, 3.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 0).addBox(1.500F, 0.200F, -2.000F, 2.800F, 3.500F, 3.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-4.800F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-1.300F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(2.200F, 8.600F, -12.000F, 2.600F, 3.400F, 4.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 128).addBox(-6.100F, 9.000F, -7.000F, 2.200F, 2.700F, 3.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 64).addBox(-5.700F, 10.000F, -9.700F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 64).addBox(-3.800F, 10.400F, -7.500F, 7.600F, 1.800F, 7.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(64, 64).addBox(-2.600F, 4.700F, 1.000F, 5.200F, 4.000F, 3.000F, new CubeDeformation(inflate + 0.015F))
                , PartPose.offset(2.500F, 9.800F, -0.500F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(0.000F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(0, 128).addBox(-1.100F, -1.500F, -2.200F, 2.200F, 3.000F, 4.200F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(3.500F, 10.700F, -13.500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 64).addBox(-0.700F, -1.000F, -1.600F, 1.400F, 2.000F, 3.200F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(3.500F, 11.200F, -16.900F, -0.3840F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_leg_left(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_left", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-4.300F, -3.500F, -4.500F, 7.600F, 11.000F, 10.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-5.000F, -2.800F, -4.000F, 3.600F, 8.800F, 9.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 0).addBox(-5.200F, -1.500F, -2.500F, 2.800F, 6.000F, 6.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(-4.200F, 4.000F, -6.000F, 6.400F, 6.500F, 6.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 0).addBox(-2.900F, 4.500F, -8.000F, 2.800F, 4.000F, 3.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 128).addBox(-4.900F, -0.500F, -2.500F, 1.000F, 6.000F, 5.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-3.700F, 1.500F, 0.500F, 2.400F, 2.500F, 3.000F, new CubeDeformation(inflate + 0.000F))
                , PartPose.offset(-12.000F, 2.000F, 12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.800F, 2.400F, 6.500F, 3.500F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, 0.4363F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, 0.3840F));
        return part;
    }
    private static PartDefinition build_shin_left(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("shin_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.400F, -0.700F, -3.500F, 6.800F, 9.000F, 7.200F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(-3.900F, -0.000F, -3.000F, 3.000F, 7.300F, 6.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(64, 0).addBox(-2.600F, -1.200F, 2.000F, 5.200F, 5.000F, 4.000F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 64).addBox(-1.100F, -1.200F, 5.000F, 2.200F, 3.500F, 3.300F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(-4.000F, 7.300F, -7.000F, 8.000F, 4.500F, 10.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 64).addBox(-3.400F, 10.200F, -6.000F, 6.800F, 1.600F, 8.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(128, 0).addBox(-4.000F, 5.300F, -2.500F, 2.400F, 3.000F, 3.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(1.700F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 128).addBox(1.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 64).addBox(2.200F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-1.100F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 128).addBox(-0.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 64).addBox(-0.600F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-3.900F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 128).addBox(-3.700F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 64).addBox(-3.400F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 128).addBox(-1.400F, 8.800F, 2.000F, 2.800F, 2.600F, 3.300F, new CubeDeformation(inflate + 0.009F))
                .texOffs(128, 64).addBox(-0.900F, 9.400F, 4.700F, 1.800F, 1.800F, 2.600F, new CubeDeformation(inflate + 0.012F))
                , PartPose.offset(-1.000F, 8.200F, 1.500F));
        return part;
    }
    private static PartDefinition build_leg_right(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("leg_right", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-3.300F, -3.500F, -4.500F, 7.600F, 11.000F, 10.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(1.400F, -2.800F, -4.000F, 3.600F, 8.800F, 9.000F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 0).addBox(2.400F, -1.500F, -2.500F, 2.800F, 6.000F, 6.000F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-2.200F, 4.000F, -6.000F, 6.400F, 6.500F, 6.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 0).addBox(0.100F, 4.500F, -8.000F, 2.800F, 4.000F, 3.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 128).addBox(3.900F, -0.500F, -2.500F, 1.000F, 6.000F, 5.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(1.300F, 1.500F, 0.500F, 2.400F, 2.500F, 3.000F, new CubeDeformation(inflate + 0.018F))
                , PartPose.offset(12.000F, 2.000F, 12.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.200F, -4.500F, -1.800F, 2.400F, 6.500F, 3.500F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(4.200F, -0.000F, -0.200F, -0.3840F, -0.0000F, -0.4363F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.000F, -3.500F, -1.500F, 2.000F, 5.500F, 3.000F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(4.000F, 4.000F, 3.500F, -0.5236F, -0.0000F, -0.3840F));
        return part;
    }
    private static PartDefinition build_shin_right(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("shin_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.400F, -0.700F, -3.500F, 6.800F, 9.000F, 7.200F, new CubeDeformation(inflate + 0.000F))
                .texOffs(64, 0).addBox(0.900F, -0.000F, -3.000F, 3.000F, 7.300F, 6.000F, new CubeDeformation(inflate + 0.003F))
                .texOffs(64, 0).addBox(-2.600F, -1.200F, 2.000F, 5.200F, 5.000F, 4.000F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 64).addBox(-1.100F, -1.200F, 5.000F, 2.200F, 3.500F, 3.300F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-4.000F, 7.300F, -7.000F, 8.000F, 4.500F, 10.000F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 64).addBox(-3.400F, 10.200F, -6.000F, 6.800F, 1.600F, 8.000F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 0).addBox(1.600F, 5.300F, -2.500F, 2.400F, 3.000F, 3.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-3.900F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 128).addBox(-3.700F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 64).addBox(-3.400F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(-1.100F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 128).addBox(-0.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.012F))
                .texOffs(128, 64).addBox(-0.600F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(1.700F, 8.600F, -9.300F, 2.200F, 3.000F, 3.300F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 128).addBox(1.900F, 9.300F, -12.300F, 1.800F, 2.500F, 3.400F, new CubeDeformation(inflate + 0.000F))
                .texOffs(128, 64).addBox(2.200F, 10.100F, -14.500F, 1.200F, 1.700F, 2.600F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 128).addBox(-1.400F, 8.800F, 2.000F, 2.800F, 2.600F, 3.300F, new CubeDeformation(inflate + 0.006F))
                .texOffs(128, 64).addBox(-0.900F, 9.400F, 4.700F, 1.800F, 1.800F, 2.600F, new CubeDeformation(inflate + 0.009F))
                , PartPose.offset(1.000F, 8.200F, 1.500F));
        return part;
    }
    private static PartDefinition build_tail_1(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_1", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-7.000F, -5.400F, -1.000F, 14.000F, 10.800F, 12.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 64).addBox(-5.600F, 4.600F, 0.000F, 11.200F, 1.800F, 10.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(-6.300F, -7.400F, 0.000F, 12.600F, 2.600F, 5.500F, new CubeDeformation(inflate + 0.018F))
                .texOffs(64, 0).addBox(-5.950F, -7.200F, 5.000F, 11.900F, 2.400F, 5.800F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 0.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 3.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(-8.500F, -3.240F, 6.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(6.300F, -3.240F, 0.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(6.300F, -3.240F, 3.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(6.300F, -3.240F, 6.300F, 2.200F, 6.480F, 2.400F, new CubeDeformation(inflate + 0.018F))
                , PartPose.offset(0.000F, 2.500F, 18.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -7.400F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(0.000F, -7.400F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(-8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(-8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(8.800F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(8.800F, -1.400F, 4.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(8.800F, -1.400F, 7.500F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_2(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_2", CubeListBuilder.create()
                .texOffs(128, 128).addBox(5.200F, -1.000F, 2.000F, 1.000F, 2.000F, 6.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(128, 128).addBox(-6.200F, -1.000F, 2.000F, 1.000F, 2.000F, 6.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(64, 64).addBox(-5.600F, -4.600F, -1.000F, 11.200F, 9.200F, 12.500F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 64).addBox(-4.480F, 3.800F, 0.000F, 8.960F, 1.800F, 10.500F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(-5.040F, -6.600F, 0.000F, 10.080F, 2.600F, 5.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(64, 0).addBox(-4.760F, -6.400F, 5.000F, 9.520F, 2.400F, 5.800F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 0.300F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.006F))
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 3.900F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(-7.100F, -2.760F, 7.500F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(4.900F, -2.760F, 0.300F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(4.900F, -2.760F, 3.900F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(4.900F, -2.760F, 7.500F, 2.200F, 5.520F, 2.400F, new CubeDeformation(inflate + 0.015F))
                , PartPose.offset(0.000F, 2.000F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(0.000F, -6.600F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(0.000F, -6.600F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(-7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(7.400F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(7.400F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(7.400F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_3(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_3", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-4.400F, -3.800F, -1.000F, 8.800F, 7.600F, 12.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 64).addBox(-3.520F, 3.000F, 0.000F, 7.040F, 1.800F, 10.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(-3.960F, -5.800F, 0.000F, 7.920F, 2.600F, 5.500F, new CubeDeformation(inflate + 0.006F))
                .texOffs(64, 0).addBox(-3.740F, -5.600F, 5.000F, 7.480F, 2.400F, 5.800F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 0.300F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.018F))
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 3.900F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 0).addBox(-5.900F, -2.280F, 7.500F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.009F))
                .texOffs(0, 0).addBox(3.700F, -2.280F, 0.300F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.015F))
                .texOffs(0, 0).addBox(3.700F, -2.280F, 3.900F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.000F))
                .texOffs(0, 0).addBox(3.700F, -2.280F, 7.500F, 2.200F, 4.560F, 2.400F, new CubeDeformation(inflate + 0.006F))
                , PartPose.offset(0.000F, 1.500F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-1.300F, -3.500F, -1.500F, 2.600F, 4.300F, 3.000F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, -5.800F, 3.000F, -0.4887F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(192, 0).addBox(-1.100F, -2.800F, -1.500F, 2.200F, 3.800F, 3.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(0.000F, -5.800F, 8.000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(-6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(6.200F, -1.400F, 1.500F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(6.200F, -1.400F, 5.100F, -0.3840F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(128, 0).addBox(-0.900F, -1.800F, -0.900F, 1.800F, 2.800F, 1.800F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(6.200F, -1.400F, 8.700F, -0.3840F, -0.0000F, -0.4887F));
        return part;
    }
    private static PartDefinition build_tail_4(PartDefinition parent, float inflate) {
        PartDefinition part = parent.addOrReplaceChild("tail_4", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-3.800F, -3.500F, -1.000F, 7.600F, 7.000F, 11.000F, new CubeDeformation(inflate + 0.012F))
                .texOffs(0, 0).addBox(-5.400F, -4.500F, 3.000F, 10.800F, 9.000F, 10.500F, new CubeDeformation(inflate + 0.015F))
                .texOffs(128, 0).addBox(-2.600F, -2.400F, 13.000F, 5.200F, 4.800F, 5.500F, new CubeDeformation(inflate + 0.000F))
                .texOffs(192, 0).addBox(-1.400F, -1.400F, 18.000F, 2.800F, 2.800F, 4.500F, new CubeDeformation(inflate + 0.003F))
                .texOffs(0, 64).addBox(-3.200F, 3.200F, 0.000F, 6.400F, 1.600F, 11.000F, new CubeDeformation(inflate + 0.006F))
                , PartPose.offset(0.000F, 1.000F, 10.000F));
        part.addOrReplaceChild("c_0", CubeListBuilder.create().texOffs(128, 0).addBox(-4.600F, -3.800F, -4.000F, 9.200F, 7.600F, 8.500F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(0.000F, -0.000F, 8.000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("c_1", CubeListBuilder.create().texOffs(64, 0).addBox(-2.800F, -1.100F, -6.000F, 5.600F, 2.200F, 12.000F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(0.000F, -4.800F, 12.500F, -0.4538F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_2", CubeListBuilder.create().texOffs(64, 0).addBox(-2.800F, -1.100F, -6.000F, 5.600F, 2.200F, 12.000F, new CubeDeformation(inflate + 0.012F)), PartPose.offsetAndRotation(0.000F, 4.800F, 12.500F, 0.4538F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("c_3", CubeListBuilder.create().texOffs(64, 0).addBox(-1.100F, -2.800F, -6.000F, 2.200F, 5.600F, 12.000F, new CubeDeformation(inflate + 0.015F)), PartPose.offsetAndRotation(-5.200F, -0.000F, 12.500F, -0.0000F, 0.4538F, 0.0000F));
        part.addOrReplaceChild("c_4", CubeListBuilder.create().texOffs(64, 0).addBox(-1.100F, -2.800F, -6.000F, 2.200F, 5.600F, 12.000F, new CubeDeformation(inflate + 0.018F)), PartPose.offsetAndRotation(5.200F, -0.000F, 12.500F, -0.0000F, -0.4538F, 0.0000F));
        part.addOrReplaceChild("c_5", CubeListBuilder.create().texOffs(192, 0).addBox(-1.200F, -1.200F, -4.750F, 2.400F, 2.400F, 9.500F, new CubeDeformation(inflate + 0.000F)), PartPose.offsetAndRotation(-3.800F, -3.800F, 11.500F, -0.3491F, 0.3491F, 0.7854F));
        part.addOrReplaceChild("c_6", CubeListBuilder.create().texOffs(192, 0).addBox(-1.200F, -1.200F, -4.750F, 2.400F, 2.400F, 9.500F, new CubeDeformation(inflate + 0.003F)), PartPose.offsetAndRotation(3.800F, -3.800F, 11.500F, -0.3491F, -0.3491F, -0.7854F));
        part.addOrReplaceChild("c_7", CubeListBuilder.create().texOffs(192, 0).addBox(-1.200F, -1.200F, -4.750F, 2.400F, 2.400F, 9.500F, new CubeDeformation(inflate + 0.006F)), PartPose.offsetAndRotation(-3.800F, 3.800F, 11.500F, 0.3491F, 0.3491F, -0.7854F));
        part.addOrReplaceChild("c_8", CubeListBuilder.create().texOffs(192, 0).addBox(-1.200F, -1.200F, -4.750F, 2.400F, 2.400F, 9.500F, new CubeDeformation(inflate + 0.009F)), PartPose.offsetAndRotation(3.800F, 3.800F, 11.500F, 0.3491F, -0.3491F, 0.7854F));
        return part;
    }
    private static PartDefinition build_fx_anchor(PartDefinition parent, float inflate) {
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

    /** Renders ONLY the golden-amber dragon eyes along the animated root->body->torso->neck->head transform chain. */
    public void renderGoldenEyes(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        ModelPart bBody = bones.get("body");
        ModelPart bTorso = bones.get("torso");
        ModelPart bNeck = bones.get("neck");
        ModelPart bHead = bones.get("head");
        if (bBody == null || bTorso == null || bNeck == null || bHead == null) return;
        if (!bHead.hasChild("eye_left") || !bHead.hasChild("eye_right")) return;
        poseStack.pushPose();
        root.translateAndRotate(poseStack);
        bBody.translateAndRotate(poseStack);
        bTorso.translateAndRotate(poseStack);
        bNeck.translateAndRotate(poseStack);
        bHead.translateAndRotate(poseStack);
        bHead.getChild("eye_left").render(poseStack, buffer, packedLight, packedOverlay, color);
        bHead.getChild("eye_right").render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
