// Auto-compiled from astralion_prismatic_sovereign.bbmodel (672 cubes, 512x512 Box-UV, 40 bones)
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

public class AstralionModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TeyvatBossesConstants.id("astralion"), "main");
    public final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public AstralionModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        registerBone("root", rootPart.getChild("root"));
        registerBone("body", rootPart.getChild("root").getChild("body"));
        registerBone("torso", rootPart.getChild("root").getChild("body").getChild("torso"));
        registerBone("omni_core", rootPart.getChild("root").getChild("body").getChild("torso").getChild("omni_core"));
        registerBone("head", rootPart.getChild("root").getChild("body").getChild("torso").getChild("head"));
        registerBone("crown_horns", rootPart.getChild("root").getChild("body").getChild("torso").getChild("head").getChild("crown_horns"));
        registerBone("jaw_lower", rootPart.getChild("root").getChild("body").getChild("torso").getChild("head").getChild("jaw_lower"));
        registerBone("halo_outer", rootPart.getChild("root").getChild("body").getChild("torso").getChild("halo_outer"));
        registerBone("halo_inner", rootPart.getChild("root").getChild("body").getChild("torso").getChild("halo_inner"));
        registerBone("halo_shards", rootPart.getChild("root").getChild("body").getChild("torso").getChild("halo_inner").getChild("halo_shards"));
        registerBone("cape_tail_1", rootPart.getChild("root").getChild("body").getChild("torso").getChild("cape_tail_1"));
        registerBone("cape_tail_2", rootPart.getChild("root").getChild("body").getChild("torso").getChild("cape_tail_1").getChild("cape_tail_2"));
        registerBone("cape_tail_3", rootPart.getChild("root").getChild("body").getChild("torso").getChild("cape_tail_1").getChild("cape_tail_2").getChild("cape_tail_3"));
        registerBone("cape_tail_4", rootPart.getChild("root").getChild("body").getChild("torso").getChild("cape_tail_1").getChild("cape_tail_2").getChild("cape_tail_3").getChild("cape_tail_4"));
        registerBone("cape_tail_tip", rootPart.getChild("root").getChild("body").getChild("torso").getChild("cape_tail_1").getChild("cape_tail_2").getChild("cape_tail_3").getChild("cape_tail_4").getChild("cape_tail_tip"));
        registerBone("wings_root", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root"));
        registerBone("wing_upper_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_upper_left"));
        registerBone("wing_upper_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_upper_right"));
        registerBone("wing_mid_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_mid_left"));
        registerBone("wing_mid_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_mid_right"));
        registerBone("wing_lower_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_lower_left"));
        registerBone("wing_lower_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("wings_root").getChild("wing_lower_right"));
        registerBone("arm_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_left"));
        registerBone("pauldron_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_left").getChild("pauldron_left"));
        registerBone("forearm_left", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_left").getChild("forearm_left"));
        registerBone("omni_aegis", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_left").getChild("forearm_left").getChild("omni_aegis"));
        registerBone("arm_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_right"));
        registerBone("pauldron_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_right").getChild("pauldron_right"));
        registerBone("forearm_right", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_right").getChild("forearm_right"));
        registerBone("omni_greatsword", rootPart.getChild("root").getChild("body").getChild("torso").getChild("arm_right").getChild("forearm_right").getChild("omni_greatsword"));
        registerBone("waist_skirt", rootPart.getChild("root").getChild("body").getChild("waist_skirt"));
        registerBone("tasset_front", rootPart.getChild("root").getChild("body").getChild("waist_skirt").getChild("tasset_front"));
        registerBone("tasset_back", rootPart.getChild("root").getChild("body").getChild("waist_skirt").getChild("tasset_back"));
        registerBone("tasset_left", rootPart.getChild("root").getChild("body").getChild("waist_skirt").getChild("tasset_left"));
        registerBone("tasset_right", rootPart.getChild("root").getChild("body").getChild("waist_skirt").getChild("tasset_right"));
        registerBone("leg_left", rootPart.getChild("root").getChild("body").getChild("leg_left"));
        registerBone("shin_left", rootPart.getChild("root").getChild("body").getChild("leg_left").getChild("shin_left"));
        registerBone("leg_right", rootPart.getChild("root").getChild("body").getChild("leg_right"));
        registerBone("shin_right", rootPart.getChild("root").getChild("body").getChild("leg_right").getChild("shin_right"));
        registerBone("fx_anchor", rootPart.getChild("root").getChild("body").getChild("fx_anchor"));
    }

    private void registerBone(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
    }

    public ModelPart getBone(String name) { return bones.get(name); }

    public void resetPose() {
        for (Map.Entry<String, ModelPart> entry : bones.entrySet()) {
            PartPose pose = initialPoses.get(entry.getKey());
            if (pose != null) entry.getValue().loadPose(pose);
            entry.getValue().xScale = 1.0F;
            entry.getValue().yScale = 1.0F;
            entry.getValue().zScale = 1.0F;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDef = mesh.getRoot();
        build_root(partDef);
        return LayerDefinition.create(mesh, 512, 512);
    }

    private static PartDefinition build_omni_core(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(9, 21).addBox(4.2000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F);
        PartDefinition part = parent.addOrReplaceChild("omni_core", cubes, PartPose.offset(0.0000F, -14.5000F, -3.5000F));
        part.addOrReplaceChild("rc_0", CubeListBuilder.create().texOffs(449, 0).addBox(-2.2500F, -2.2500F, -2.2500F, 4.5000F, 4.5000F, 4.5000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.7854F, -0.7854F, 0.0000F));
        part.addOrReplaceChild("rc_1", CubeListBuilder.create().texOffs(466, 0).addBox(-1.7500F, -3.7500F, -1.7500F, 3.5000F, 7.5000F, 3.5000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.0000F, -0.7854F, 0.0000F));
        part.addOrReplaceChild("rc_2", CubeListBuilder.create().texOffs(483, 0).addBox(-3.7500F, -1.7500F, -1.7500F, 7.5000F, 3.5000F, 3.5000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.7854F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_3", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0000F, -4.7500F, -1.0000F, 2.0000F, 9.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("rc_4", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0000F, -4.7500F, -1.0000F, 2.0000F, 9.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.0000F, -0.0000F, -0.7854F));
        part.addOrReplaceChild("rc_5", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(3.3900F, -3.3900F, 0.0000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("rc_6", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(0.0000F, -4.8000F, 0.0000F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("rc_7", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(-3.3900F, -3.3900F, 0.0000F, -0.0000F, -0.0000F, 2.3562F));
        part.addOrReplaceChild("rc_8", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(-4.8000F, -0.0000F, 0.0000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("rc_9", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(-3.3900F, 3.3900F, 0.0000F, -0.0000F, -0.0000F, 3.9270F));
        part.addOrReplaceChild("rc_10", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(-0.0000F, 4.8000F, 0.0000F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("rc_11", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6000F, -1.3000F, -0.6000F, 1.2000F, 2.6000F, 1.2000F), PartPose.offsetAndRotation(3.3900F, 3.3900F, 0.0000F, -0.0000F, -0.0000F, 5.4978F));
        return part;
    }
    private static PartDefinition build_crown_horns(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("crown_horns", cubes, PartPose.offset(0.0000F, -7.0000F, 0.0000F));
        part.addOrReplaceChild("rc_12", CubeListBuilder.create().texOffs(198, 21).addBox(-1.1000F, -3.2500F, -1.1000F, 2.2000F, 6.5000F, 2.2000F), PartPose.offsetAndRotation(0.0000F, -4.5000F, -2.0000F, 0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_13", CubeListBuilder.create().texOffs(207, 21).addBox(-0.7000F, -2.2500F, -0.7000F, 1.4000F, 4.5000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, -8.5000F, -2.5000F, 0.2443F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_14", CubeListBuilder.create().texOffs(212, 21).addBox(-2.2500F, -1.2500F, -1.0000F, 4.5000F, 2.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -2.2000F, -3.5000F, 0.2793F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_15", CubeListBuilder.create().texOffs(225, 21).addBox(-1.4000F, -1.6000F, -1.4000F, 2.8000F, 3.2000F, 2.8000F), PartPose.offsetAndRotation(3.6000F, -0.5000F, -0.5000F, -0.3142F, -0.0000F, -0.4887F));
        part.addOrReplaceChild("rc_16", CubeListBuilder.create().texOffs(225, 21).addBox(-1.2600F, -1.6000F, -1.2700F, 2.5300F, 3.2000F, 2.5300F), PartPose.offsetAndRotation(6.2800F, -2.8300F, 0.7500F, -0.3782F, -0.0000F, -0.4480F));
        part.addOrReplaceChild("rc_17", CubeListBuilder.create().texOffs(238, 21).addBox(-1.1300F, -1.6000F, -1.1300F, 2.2700F, 3.2000F, 2.2700F), PartPose.offsetAndRotation(8.6700F, -5.1700F, 2.0000F, -0.4421F, -0.0000F, -0.4072F));
        part.addOrReplaceChild("rc_18", CubeListBuilder.create().texOffs(238, 21).addBox(-1.0000F, -1.6000F, -1.0000F, 2.0000F, 3.2000F, 2.0000F), PartPose.offsetAndRotation(10.5500F, -7.5000F, 3.2500F, -0.5061F, -0.0000F, -0.3665F));
        part.addOrReplaceChild("rc_19", CubeListBuilder.create().texOffs(247, 21).addBox(-0.8700F, -1.6000F, -0.8700F, 1.7300F, 3.2000F, 1.7300F), PartPose.offsetAndRotation(11.8400F, -9.8300F, 4.5000F, -0.5702F, -0.0000F, -0.3259F));
        part.addOrReplaceChild("rc_20", CubeListBuilder.create().texOffs(9, 21).addBox(-0.7400F, -1.6000F, -0.7300F, 1.4700F, 3.2000F, 1.4700F), PartPose.offsetAndRotation(12.6200F, -12.1700F, 5.7500F, -0.6341F, -0.0000F, -0.2850F));
        part.addOrReplaceChild("rc_21", CubeListBuilder.create().texOffs(256, 21).addBox(-0.6000F, -1.6000F, -0.6000F, 1.2000F, 3.2000F, 1.2000F), PartPose.offsetAndRotation(13.1000F, -14.5000F, 7.0000F, -0.6981F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("rc_22", CubeListBuilder.create().texOffs(247, 21).addBox(-0.8000F, -1.3000F, -0.8000F, 1.6000F, 2.6000F, 1.6000F), PartPose.offsetAndRotation(2.2000F, -2.0000F, 1.0000F, -0.4189F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_23", CubeListBuilder.create().texOffs(9, 21).addBox(-0.7200F, -1.3000F, -0.7200F, 1.4300F, 2.6000F, 1.4300F), PartPose.offsetAndRotation(3.4700F, -4.5000F, 2.1700F, -0.4189F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_24", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6300F, -1.3000F, -0.6300F, 1.2700F, 2.6000F, 1.2700F), PartPose.offsetAndRotation(4.7300F, -7.0000F, 3.3300F, -0.4189F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_25", CubeListBuilder.create().texOffs(256, 21).addBox(-0.5500F, -1.3000F, -0.5500F, 1.1000F, 2.6000F, 1.1000F), PartPose.offsetAndRotation(6.0000F, -9.5000F, 4.5000F, -0.4189F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_26", CubeListBuilder.create().texOffs(225, 21).addBox(-1.4000F, -1.6000F, -1.4000F, 2.8000F, 3.2000F, 2.8000F), PartPose.offsetAndRotation(-3.6000F, -0.5000F, -0.5000F, -0.3142F, -0.0000F, 0.4887F));
        part.addOrReplaceChild("rc_27", CubeListBuilder.create().texOffs(225, 21).addBox(-1.2700F, -1.6000F, -1.2700F, 2.5300F, 3.2000F, 2.5300F), PartPose.offsetAndRotation(-6.2800F, -2.8300F, 0.7500F, -0.3782F, -0.0000F, 0.4480F));
        part.addOrReplaceChild("rc_28", CubeListBuilder.create().texOffs(238, 21).addBox(-1.1400F, -1.6000F, -1.1300F, 2.2700F, 3.2000F, 2.2700F), PartPose.offsetAndRotation(-8.6700F, -5.1700F, 2.0000F, -0.4421F, -0.0000F, 0.4072F));
        part.addOrReplaceChild("rc_29", CubeListBuilder.create().texOffs(238, 21).addBox(-1.0000F, -1.6000F, -1.0000F, 2.0000F, 3.2000F, 2.0000F), PartPose.offsetAndRotation(-10.5500F, -7.5000F, 3.2500F, -0.5061F, -0.0000F, 0.3665F));
        part.addOrReplaceChild("rc_30", CubeListBuilder.create().texOffs(247, 21).addBox(-0.8700F, -1.6000F, -0.8700F, 1.7300F, 3.2000F, 1.7300F), PartPose.offsetAndRotation(-11.8400F, -9.8300F, 4.5000F, -0.5702F, -0.0000F, 0.3259F));
        part.addOrReplaceChild("rc_31", CubeListBuilder.create().texOffs(9, 21).addBox(-0.7300F, -1.6000F, -0.7300F, 1.4700F, 3.2000F, 1.4700F), PartPose.offsetAndRotation(-12.6200F, -12.1700F, 5.7500F, -0.6341F, -0.0000F, 0.2850F));
        part.addOrReplaceChild("rc_32", CubeListBuilder.create().texOffs(256, 21).addBox(-0.6000F, -1.6000F, -0.6000F, 1.2000F, 3.2000F, 1.2000F), PartPose.offsetAndRotation(-13.1000F, -14.5000F, 7.0000F, -0.6981F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("rc_33", CubeListBuilder.create().texOffs(247, 21).addBox(-0.8000F, -1.3000F, -0.8000F, 1.6000F, 2.6000F, 1.6000F), PartPose.offsetAndRotation(-2.2000F, -2.0000F, 1.0000F, -0.4189F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_34", CubeListBuilder.create().texOffs(9, 21).addBox(-0.7100F, -1.3000F, -0.7200F, 1.4300F, 2.6000F, 1.4300F), PartPose.offsetAndRotation(-3.4700F, -4.5000F, 2.1700F, -0.4189F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_35", CubeListBuilder.create().texOffs(9, 21).addBox(-0.6400F, -1.3000F, -0.6300F, 1.2700F, 2.6000F, 1.2700F), PartPose.offsetAndRotation(-4.7300F, -7.0000F, 3.3300F, -0.4189F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_36", CubeListBuilder.create().texOffs(256, 21).addBox(-0.5500F, -1.3000F, -0.5500F, 1.1000F, 2.6000F, 1.1000F), PartPose.offsetAndRotation(-6.0000F, -9.5000F, 4.5000F, -0.4189F, -0.0000F, 0.2793F));
        return part;
    }
    private static PartDefinition build_jaw_lower(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(159, 21).addBox(-2.1000F, -0.7000F, -3.2500F, 4.2000F, 2.0000F, 4.5000F);
        cubes.texOffs(176, 21).addBox(-1.5000F, 0.1000F, -4.3000F, 3.0000F, 1.6000F, 3.2000F);
        cubes.texOffs(154, 21).addBox(1.1000F, -1.3000F, -2.6000F, 1.0000F, 1.6000F, 1.0000F);
        cubes.texOffs(189, 21).addBox(1.6000F, -0.6000F, -2.3000F, 1.2000F, 1.8000F, 3.2000F);
        cubes.texOffs(154, 21).addBox(-2.1000F, -1.3000F, -2.6000F, 1.0000F, 1.6000F, 1.0000F);
        cubes.texOffs(189, 21).addBox(-2.8000F, -0.6000F, -2.3000F, 1.2000F, 1.8000F, 3.2000F);
        PartDefinition part = parent.addOrReplaceChild("jaw_lower", cubes, PartPose.offset(0.0000F, -0.5000F, -3.5000F));
        return part;
    }
    private static PartDefinition build_head(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(14, 21).addBox(-3.7500F, -7.7500F, -4.2500F, 7.5000F, 8.5000F, 8.5000F);
        cubes.texOffs(47, 21).addBox(-1.3000F, -6.7500F, -5.1500F, 2.6000F, 5.5000F, 1.5000F);
        cubes.texOffs(58, 21).addBox(-0.6000F, -7.2500F, -5.4000F, 1.2000F, 6.5000F, 1.0000F);
        cubes.texOffs(154, 21).addBox(1.0000F, -2.1000F, -6.3000F, 1.0000F, 1.8000F, 1.0000F);
        cubes.texOffs(154, 21).addBox(-2.0000F, -2.1000F, -6.3000F, 1.0000F, 1.8000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("head", cubes, PartPose.offset(0.0000F, -23.0000F, 0.0000F));
        part.addOrReplaceChild("rc_37", CubeListBuilder.create().texOffs(63, 21).addBox(-3.0000F, -1.5000F, -4.0000F, 6.0000F, 3.0000F, 8.0000F), PartPose.offsetAndRotation(0.0000F, -8.0000F, -0.5000F, 0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_38", CubeListBuilder.create().texOffs(289, 0).addBox(-3.0000F, -3.7500F, -1.2500F, 6.0000F, 7.5000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -4.0000F, 4.8000F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_39", CubeListBuilder.create().texOffs(92, 21).addBox(-1.0000F, -3.7500F, -3.7500F, 2.0000F, 7.5000F, 7.5000F), PartPose.offsetAndRotation(4.2000F, -4.0000F, -0.5000F, -0.0000F, 0.1745F, -0.1047F));
        part.addOrReplaceChild("rc_40", CubeListBuilder.create().texOffs(113, 21).addBox(-1.0000F, -2.0000F, -2.5000F, 2.0000F, 4.0000F, 5.0000F), PartPose.offsetAndRotation(4.6000F, -2.2000F, -2.5000F, -0.1396F, 0.2793F, -0.1745F));
        part.addOrReplaceChild("rc_41", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -0.8000F, -1.0000F, 3.0000F, 1.6000F, 2.0000F), PartPose.offsetAndRotation(2.4000F, -6.8000F, -4.4000F, 0.2094F, 0.2443F, 0.3142F));
        part.addOrReplaceChild("rc_42", CubeListBuilder.create().texOffs(128, 21).addBox(-1.0000F, -2.2500F, -2.0000F, 2.0000F, 4.5000F, 4.0000F), PartPose.offsetAndRotation(3.8000F, -6.2000F, 3.8000F, -0.2793F, -0.2443F, 0.0000F));
        part.addOrReplaceChild("rc_43", CubeListBuilder.create().texOffs(141, 21).addBox(-1.1000F, -1.1000F, -1.7500F, 2.2000F, 2.2000F, 3.5000F), PartPose.offsetAndRotation(1.8000F, -2.5000F, -5.2000F, -0.1396F, 0.2094F, 0.0000F));
        part.addOrReplaceChild("rc_44", CubeListBuilder.create().texOffs(92, 21).addBox(-1.0000F, -3.7500F, -3.7500F, 2.0000F, 7.5000F, 7.5000F), PartPose.offsetAndRotation(-4.2000F, -4.0000F, -0.5000F, -0.0000F, -0.1745F, 0.1047F));
        part.addOrReplaceChild("rc_45", CubeListBuilder.create().texOffs(113, 21).addBox(-1.0000F, -2.0000F, -2.5000F, 2.0000F, 4.0000F, 5.0000F), PartPose.offsetAndRotation(-4.6000F, -2.2000F, -2.5000F, -0.1396F, -0.2793F, 0.1745F));
        part.addOrReplaceChild("rc_46", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -0.8000F, -1.0000F, 3.0000F, 1.6000F, 2.0000F), PartPose.offsetAndRotation(-2.4000F, -6.8000F, -4.4000F, 0.2094F, -0.2443F, -0.3142F));
        part.addOrReplaceChild("rc_47", CubeListBuilder.create().texOffs(128, 21).addBox(-1.0000F, -2.2500F, -2.0000F, 2.0000F, 4.5000F, 4.0000F), PartPose.offsetAndRotation(-3.8000F, -6.2000F, 3.8000F, -0.2793F, 0.2443F, 0.0000F));
        part.addOrReplaceChild("rc_48", CubeListBuilder.create().texOffs(141, 21).addBox(-1.1000F, -1.1000F, -1.7500F, 2.2000F, 2.2000F, 3.5000F), PartPose.offsetAndRotation(-1.8000F, -2.5000F, -5.2000F, -0.1396F, -0.2094F, 0.0000F));
        build_crown_horns(part);
        build_jaw_lower(part);
        return part;
    }
    private static PartDefinition build_halo_outer(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(261, 21).addBox(25.5000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F));
        cubes.texOffs(270, 21).addBox(27.9000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F);
        PartDefinition part = parent.addOrReplaceChild("halo_outer", cubes, PartPose.offsetAndRotation(0.0000F, -29.0000F, 11.0000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_49", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(26.1000F, -4.6000F, 0.0000F, -0.0000F, -0.0000F, 0.1745F));
        part.addOrReplaceChild("rc_50", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(24.9000F, -9.0600F, 0.0000F, -0.0000F, -0.0000F, 0.3491F));
        part.addOrReplaceChild("rc_51", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(22.9500F, -13.2500F, 0.0000F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("rc_52", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(26.4100F, -15.2500F, 0.0000F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("rc_53", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(20.3000F, -17.0300F, 0.0000F, -0.0000F, -0.0000F, 0.6981F));
        part.addOrReplaceChild("rc_54", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(17.0300F, -20.3000F, 0.0000F, -0.0000F, -0.0000F, 0.8727F));
        part.addOrReplaceChild("rc_55", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(13.2500F, -22.9500F, 0.0000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("rc_56", CubeListBuilder.create().texOffs(270, 21).addBox(-2.6000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(15.2500F, -26.4100F, 0.0000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("rc_57", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(9.0600F, -24.9000F, 0.0000F, -0.0000F, -0.0000F, 1.2217F));
        part.addOrReplaceChild("rc_58", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(4.6000F, -26.1000F, 0.0000F, -0.0000F, -0.0000F, 1.3963F));
        part.addOrReplaceChild("rc_59", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(0.0000F, -26.5000F, 0.0000F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("rc_60", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(0.0000F, -30.5000F, 0.0000F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("rc_61", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-4.6000F, -26.1000F, 0.0000F, -0.0000F, -0.0000F, 1.7453F));
        part.addOrReplaceChild("rc_62", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-9.0600F, -24.9000F, 0.0000F, -0.0000F, -0.0000F, 1.9199F));
        part.addOrReplaceChild("rc_63", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-13.2500F, -22.9500F, 0.0000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("rc_64", CubeListBuilder.create().texOffs(270, 21).addBox(-2.6000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-15.2500F, -26.4100F, 0.0000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("rc_65", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-17.0300F, -20.3000F, 0.0000F, -0.0000F, -0.0000F, 2.2689F));
        part.addOrReplaceChild("rc_66", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-20.3000F, -17.0300F, 0.0000F, -0.0000F, -0.0000F, 2.4435F));
        part.addOrReplaceChild("rc_67", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-22.9500F, -13.2500F, 0.0000F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("rc_68", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-26.4100F, -15.2500F, 0.0000F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("rc_69", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-24.9000F, -9.0600F, 0.0000F, -0.0000F, -0.0000F, 2.7925F));
        part.addOrReplaceChild("rc_70", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-26.1000F, -4.6000F, 0.0000F, -0.0000F, -0.0000F, 2.9671F));
        part.addOrReplaceChild("rc_71", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-26.5000F, -0.0000F, 0.0000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("rc_72", CubeListBuilder.create().texOffs(270, 21).addBox(-2.6000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-30.5000F, -0.0000F, 0.0000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("rc_73", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-26.1000F, 4.6000F, 0.0000F, -0.0000F, -0.0000F, 3.3161F));
        part.addOrReplaceChild("rc_74", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-24.9000F, 9.0600F, 0.0000F, -0.0000F, -0.0000F, 3.4907F));
        part.addOrReplaceChild("rc_75", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-22.9500F, 13.2500F, 0.0000F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("rc_76", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-26.4100F, 15.2500F, 0.0000F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("rc_77", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-20.3000F, 17.0300F, 0.0000F, -0.0000F, -0.0000F, 3.8397F));
        part.addOrReplaceChild("rc_78", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-17.0300F, 20.3000F, 0.0000F, -0.0000F, -0.0000F, 4.0143F));
        part.addOrReplaceChild("rc_79", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-13.2500F, 22.9500F, 0.0000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("rc_80", CubeListBuilder.create().texOffs(270, 21).addBox(-2.6000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-15.2500F, 26.4100F, 0.0000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("rc_81", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-9.0600F, 24.9000F, 0.0000F, -0.0000F, -0.0000F, 4.3633F));
        part.addOrReplaceChild("rc_82", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-4.6000F, 26.1000F, 0.0000F, -0.0000F, -0.0000F, 4.5379F));
        part.addOrReplaceChild("rc_83", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-0.0000F, 26.5000F, 0.0000F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("rc_84", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-0.0000F, 30.5000F, 0.0000F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("rc_85", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(4.6000F, 26.1000F, 0.0000F, -0.0000F, -0.0000F, 4.8869F));
        part.addOrReplaceChild("rc_86", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(9.0600F, 24.9000F, 0.0000F, -0.0000F, -0.0000F, 5.0615F));
        part.addOrReplaceChild("rc_87", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(13.2500F, 22.9500F, 0.0000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("rc_88", CubeListBuilder.create().texOffs(270, 21).addBox(-2.6000F, -0.8000F, -0.8000F, 5.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(15.2500F, 26.4100F, 0.0000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("rc_89", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(17.0300F, 20.3000F, 0.0000F, -0.0000F, -0.0000F, 5.4105F));
        part.addOrReplaceChild("rc_90", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(20.3000F, 17.0300F, 0.0000F, -0.0000F, -0.0000F, 5.5851F));
        part.addOrReplaceChild("rc_91", CubeListBuilder.create().texOffs(261, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(22.9500F, 13.2500F, 0.0000F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("rc_92", CubeListBuilder.create().texOffs(416, 0).addBox(-1.6000F, -0.8000F, -0.8000F, 3.2000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(26.4100F, 15.2500F, 0.0000F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("rc_93", CubeListBuilder.create().texOffs(285, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(24.9000F, 9.0600F, 0.0000F, -0.0000F, -0.0000F, 5.9341F));
        part.addOrReplaceChild("rc_94", CubeListBuilder.create().texOffs(294, 21).addBox(-1.0000F, -2.6000F, -0.9000F, 2.0000F, 5.2000F, 1.8000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(26.1000F, 4.6000F, 0.0000F, -0.0000F, -0.0000F, 6.1087F));
        return part;
    }
    private static PartDefinition build_halo_shards(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(317, 21).addBox(-1.2000F, -24.8000F, -1.8000F, 2.4000F, 5.2000F, 2.0000F);
        cubes.texOffs(326, 21).addBox(-0.7000F, -25.8000F, -1.5000F, 1.4000F, 7.2000F, 1.4000F);
        cubes.texOffs(331, 21).addBox(-1.6000F, -23.4000F, -0.1000F, 3.2000F, 2.4000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("halo_shards", cubes, PartPose.offset(0.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_95", CubeListBuilder.create().texOffs(340, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(-17.3600F, -13.8400F, -0.8000F, -0.0000F, -0.0000F, 0.8976F));
        part.addOrReplaceChild("rc_96", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(-17.3600F, -13.8400F, -0.8000F, -0.0000F, -0.0000F, 0.8976F));
        part.addOrReplaceChild("rc_97", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(-17.3600F, -13.8400F, 0.4000F, -0.0000F, -0.0000F, 0.8976F));
        part.addOrReplaceChild("rc_98", CubeListBuilder.create().texOffs(349, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(-21.6400F, 4.9400F, -0.8000F, -0.0000F, -0.0000F, 1.7952F));
        part.addOrReplaceChild("rc_99", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(-21.6400F, 4.9400F, -0.8000F, -0.0000F, -0.0000F, 1.7952F));
        part.addOrReplaceChild("rc_100", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(-21.6400F, 4.9400F, 0.4000F, -0.0000F, -0.0000F, 1.7952F));
        part.addOrReplaceChild("rc_101", CubeListBuilder.create().texOffs(358, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(-9.6300F, 20.0000F, -0.8000F, -0.0000F, -0.0000F, 2.6929F));
        part.addOrReplaceChild("rc_102", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(-9.6300F, 20.0000F, -0.8000F, -0.0000F, -0.0000F, 2.6929F));
        part.addOrReplaceChild("rc_103", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(-9.6300F, 20.0000F, 0.4000F, -0.0000F, -0.0000F, 2.6929F));
        part.addOrReplaceChild("rc_104", CubeListBuilder.create().texOffs(367, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(9.6300F, 20.0000F, -0.8000F, -0.0000F, -0.0000F, 3.5903F));
        part.addOrReplaceChild("rc_105", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(9.6300F, 20.0000F, -0.8000F, -0.0000F, -0.0000F, 3.5903F));
        part.addOrReplaceChild("rc_106", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(9.6300F, 20.0000F, 0.4000F, -0.0000F, -0.0000F, 3.5903F));
        part.addOrReplaceChild("rc_107", CubeListBuilder.create().texOffs(376, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(21.6400F, 4.9400F, -0.8000F, -0.0000F, -0.0000F, 4.4879F));
        part.addOrReplaceChild("rc_108", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(21.6400F, 4.9400F, -0.8000F, -0.0000F, -0.0000F, 4.4879F));
        part.addOrReplaceChild("rc_109", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(21.6400F, 4.9400F, 0.4000F, -0.0000F, -0.0000F, 4.4879F));
        part.addOrReplaceChild("rc_110", CubeListBuilder.create().texOffs(385, 21).addBox(-1.2000F, -2.6000F, -1.0000F, 2.4000F, 5.2000F, 2.0000F), PartPose.offsetAndRotation(17.3600F, -13.8400F, -0.8000F, -0.0000F, -0.0000F, 5.3856F));
        part.addOrReplaceChild("rc_111", CubeListBuilder.create().texOffs(326, 21).addBox(-0.7000F, -3.6000F, -0.7000F, 1.4000F, 7.2000F, 1.4000F), PartPose.offsetAndRotation(17.3600F, -13.8400F, -0.8000F, -0.0000F, -0.0000F, 5.3856F));
        part.addOrReplaceChild("rc_112", CubeListBuilder.create().texOffs(331, 21).addBox(-1.6000F, -1.2000F, -0.5000F, 3.2000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(17.3600F, -13.8400F, 0.4000F, -0.0000F, -0.0000F, 5.3856F));
        return part;
    }
    private static PartDefinition build_halo_inner(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(303, 21).addBox(17.2500F, -2.5000F, -1.2000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F));
        PartDefinition part = parent.addOrReplaceChild("halo_inner", cubes, PartPose.offsetAndRotation(0.0000F, -29.0000F, 11.0000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_113", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(17.3900F, -4.6600F, -0.5000F, -0.0000F, -0.0000F, 0.2618F));
        part.addOrReplaceChild("rc_114", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(15.5900F, -9.0000F, -0.5000F, -0.0000F, -0.0000F, 0.5236F));
        part.addOrReplaceChild("rc_115", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(12.7300F, -12.7300F, -0.5000F, -0.0000F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("rc_116", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(9.0000F, -15.5900F, -0.5000F, -0.0000F, -0.0000F, 1.0472F));
        part.addOrReplaceChild("rc_117", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(4.6600F, -17.3900F, -0.5000F, -0.0000F, -0.0000F, 1.3090F));
        part.addOrReplaceChild("rc_118", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(0.0000F, -18.0000F, -0.5000F, -0.0000F, -0.0000F, 1.5708F));
        part.addOrReplaceChild("rc_119", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-4.6600F, -17.3900F, -0.5000F, -0.0000F, -0.0000F, 1.8326F));
        part.addOrReplaceChild("rc_120", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-9.0000F, -15.5900F, -0.5000F, -0.0000F, -0.0000F, 2.0944F));
        part.addOrReplaceChild("rc_121", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-12.7300F, -12.7300F, -0.5000F, -0.0000F, -0.0000F, 2.3562F));
        part.addOrReplaceChild("rc_122", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-15.5900F, -9.0000F, -0.5000F, -0.0000F, -0.0000F, 2.6180F));
        part.addOrReplaceChild("rc_123", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-17.3900F, -4.6600F, -0.5000F, -0.0000F, -0.0000F, 2.8798F));
        part.addOrReplaceChild("rc_124", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-18.0000F, -0.0000F, -0.5000F, -0.0000F, -0.0000F, 3.1416F));
        part.addOrReplaceChild("rc_125", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-17.3900F, 4.6600F, -0.5000F, -0.0000F, -0.0000F, 3.4034F));
        part.addOrReplaceChild("rc_126", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-15.5900F, 9.0000F, -0.5000F, -0.0000F, -0.0000F, 3.6652F));
        part.addOrReplaceChild("rc_127", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-12.7300F, 12.7300F, -0.5000F, -0.0000F, -0.0000F, 3.9270F));
        part.addOrReplaceChild("rc_128", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-9.0000F, 15.5900F, -0.5000F, -0.0000F, -0.0000F, 4.1888F));
        part.addOrReplaceChild("rc_129", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-4.6600F, 17.3900F, -0.5000F, -0.0000F, -0.0000F, 4.4506F));
        part.addOrReplaceChild("rc_130", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(-0.0000F, 18.0000F, -0.5000F, -0.0000F, -0.0000F, 4.7124F));
        part.addOrReplaceChild("rc_131", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(4.6600F, 17.3900F, -0.5000F, -0.0000F, -0.0000F, 4.9742F));
        part.addOrReplaceChild("rc_132", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(9.0000F, 15.5900F, -0.5000F, -0.0000F, -0.0000F, 5.2360F));
        part.addOrReplaceChild("rc_133", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(12.7300F, 12.7300F, -0.5000F, -0.0000F, -0.0000F, 5.4978F));
        part.addOrReplaceChild("rc_134", CubeListBuilder.create().texOffs(303, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(15.5900F, 9.0000F, -0.5000F, -0.0000F, -0.0000F, 5.7596F));
        part.addOrReplaceChild("rc_135", CubeListBuilder.create().texOffs(310, 21).addBox(-0.7500F, -2.5000F, -0.7000F, 1.5000F, 5.0000F, 1.4000F, new CubeDeformation(-0.0200F)), PartPose.offsetAndRotation(17.3900F, 4.6600F, -0.5000F, -0.0000F, -0.0000F, 6.0214F));
        build_halo_shards(part);
        return part;
    }
    private static PartDefinition build_cape_tail_tip(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("cape_tail_tip", cubes, PartPose.offset(0.0000F, 8.0000F, 1.5000F));
        part.addOrReplaceChild("rc_136", CubeListBuilder.create().texOffs(403, 0).addBox(-2.0000F, -3.5000F, -1.0000F, 4.0000F, 7.0000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 0.5000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_137", CubeListBuilder.create().texOffs(17, 38).addBox(-0.7500F, -2.8000F, -0.9000F, 1.5000F, 5.6000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 1.6000F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_138", CubeListBuilder.create().texOffs(26, 38).addBox(-0.6000F, -2.2800F, -1.1000F, 1.2000F, 4.5500F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 2.3000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_139", CubeListBuilder.create().texOffs(40, 38).addBox(-0.7000F, -2.9700F, -0.7500F, 1.4000F, 5.9500F, 1.5000F), PartPose.offsetAndRotation(2.0800F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_140", CubeListBuilder.create().texOffs(40, 38).addBox(-0.7000F, -2.9700F, -0.7500F, 1.4000F, 5.9500F, 1.5000F), PartPose.offsetAndRotation(-2.0800F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, 0.1745F));
        part.addOrReplaceChild("rc_141", CubeListBuilder.create().texOffs(47, 38).addBox(-0.9000F, -2.7500F, -0.7000F, 1.8000F, 5.5000F, 1.4000F), PartPose.offsetAndRotation(-3.8000F, 7.5000F, 3.0000F, -0.3142F, -0.0000F, 0.3840F));
        part.addOrReplaceChild("rc_142", CubeListBuilder.create().texOffs(54, 38).addBox(-1.2000F, -3.7500F, -0.7000F, 2.4000F, 7.5000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 7.5000F, 3.0000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_143", CubeListBuilder.create().texOffs(47, 38).addBox(-0.9000F, -2.7500F, -0.7000F, 1.8000F, 5.5000F, 1.4000F), PartPose.offsetAndRotation(3.8000F, 7.5000F, 3.0000F, -0.3142F, -0.0000F, -0.3840F));
        return part;
    }
    private static PartDefinition build_cape_tail_4(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("cape_tail_4", cubes, PartPose.offset(0.0000F, 9.0000F, 1.5000F));
        part.addOrReplaceChild("rc_144", CubeListBuilder.create().texOffs(0, 38).addBox(-2.7500F, -4.0000F, -1.2000F, 5.5000F, 8.0000F, 2.4000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 0.5000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_145", CubeListBuilder.create().texOffs(17, 38).addBox(-0.9600F, -3.2000F, -0.9000F, 1.9200F, 6.4000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 1.8200F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_146", CubeListBuilder.create().texOffs(26, 38).addBox(-0.6000F, -2.6000F, -1.1000F, 1.2000F, 5.2000F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 2.6600F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_147", CubeListBuilder.create().texOffs(33, 38).addBox(-0.7000F, -3.4000F, -0.9000F, 1.4000F, 6.8000F, 1.8000F), PartPose.offsetAndRotation(2.8600F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_148", CubeListBuilder.create().texOffs(33, 38).addBox(-0.7000F, -3.4000F, -0.9000F, 1.4000F, 6.8000F, 1.8000F), PartPose.offsetAndRotation(-2.8600F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, 0.1745F));
        build_cape_tail_tip(part);
        return part;
    }
    private static PartDefinition build_cape_tail_3(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("cape_tail_3", cubes, PartPose.offset(0.0000F, 10.0000F, 1.5000F));
        part.addOrReplaceChild("rc_149", CubeListBuilder.create().texOffs(478, 21).addBox(-3.5000F, -4.5000F, -1.4000F, 7.0000F, 9.0000F, 2.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 0.5000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_150", CubeListBuilder.create().texOffs(499, 21).addBox(-1.2200F, -3.6000F, -0.9000F, 2.4500F, 7.2000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 2.0400F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_151", CubeListBuilder.create().texOffs(432, 21).addBox(-0.6000F, -2.9200F, -1.1000F, 1.2000F, 5.8500F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 3.0200F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_152", CubeListBuilder.create().texOffs(471, 21).addBox(-0.7000F, -3.8300F, -1.0500F, 1.4000F, 7.6500F, 2.1000F), PartPose.offsetAndRotation(3.6400F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_153", CubeListBuilder.create().texOffs(471, 21).addBox(-0.7000F, -3.8300F, -1.0500F, 1.4000F, 7.6500F, 2.1000F), PartPose.offsetAndRotation(-3.6400F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, 0.1745F));
        build_cape_tail_4(part);
        return part;
    }
    private static PartDefinition build_cape_tail_2(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("cape_tail_2", cubes, PartPose.offset(0.0000F, 10.0000F, 1.5000F));
        part.addOrReplaceChild("rc_154", CubeListBuilder.create().texOffs(448, 21).addBox(-4.0000F, -4.5000F, -1.6000F, 8.0000F, 9.0000F, 3.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 0.5000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_155", CubeListBuilder.create().texOffs(421, 21).addBox(-1.4000F, -3.6000F, -0.9000F, 2.8000F, 7.2000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 2.2600F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_156", CubeListBuilder.create().texOffs(432, 21).addBox(-0.6000F, -2.9200F, -1.1000F, 1.2000F, 5.8500F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 3.3800F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_157", CubeListBuilder.create().texOffs(471, 21).addBox(-0.7000F, -3.8300F, -1.2000F, 1.4000F, 7.6500F, 2.4000F), PartPose.offsetAndRotation(4.1600F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_158", CubeListBuilder.create().texOffs(471, 21).addBox(-0.7000F, -3.8300F, -1.2000F, 1.4000F, 7.6500F, 2.4000F), PartPose.offsetAndRotation(-4.1600F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, 0.1745F));
        build_cape_tail_3(part);
        return part;
    }
    private static PartDefinition build_cape_tail_1(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("cape_tail_1", cubes, PartPose.offset(0.0000F, -20.0000F, 6.0000F));
        part.addOrReplaceChild("rc_159", CubeListBuilder.create().texOffs(394, 21).addBox(-4.5000F, -4.5000F, -1.7500F, 9.0000F, 9.0000F, 3.5000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 0.5000F, -0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_160", CubeListBuilder.create().texOffs(421, 21).addBox(-1.5700F, -3.6000F, -0.9000F, 3.1500F, 7.2000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 2.4300F, -0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_161", CubeListBuilder.create().texOffs(432, 21).addBox(-0.6000F, -2.9300F, -1.1000F, 1.2000F, 5.8500F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 3.0000F, 3.6500F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_162", CubeListBuilder.create().texOffs(439, 21).addBox(-0.7000F, -3.8200F, -1.3100F, 1.4000F, 7.6500F, 2.6200F), PartPose.offsetAndRotation(4.6800F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_163", CubeListBuilder.create().texOffs(439, 21).addBox(-0.7000F, -3.8200F, -1.3100F, 1.4000F, 7.6500F, 2.6200F), PartPose.offsetAndRotation(-4.6800F, 3.0000F, 0.2000F, -0.1396F, -0.0000F, 0.1745F));
        build_cape_tail_2(part);
        return part;
    }
    private static PartDefinition build_wing_upper_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_upper_left", cubes, PartPose.offset(4.0000F, -2.0000F, 0.0000F));
        part.addOrReplaceChild("rc_164", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -1.2000F, -1.1000F, 6.6000F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(0.5000F, -0.0000F, 0.0000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_165", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(0.5000F, -1.2000F, -0.5000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_166", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -1.1200F, -1.0400F, 6.6000F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(5.7800F, -5.8300F, 3.8600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_167", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(5.7800F, -7.0300F, 3.3600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_168", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -1.0400F, -0.9800F, 6.6000F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(11.0600F, -11.0600F, 6.9300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_169", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(11.0600F, -12.2600F, 6.4300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_170", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -0.9600F, -0.9200F, 6.6000F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(16.3400F, -15.2000F, 8.7300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_171", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(16.3400F, -16.4000F, 8.2300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_172", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -0.8800F, -0.8600F, 6.6000F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(21.6200F, -17.9500F, 9.2600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_173", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(21.6200F, -19.1500F, 8.7600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_174", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -0.8000F, -0.8000F, 6.6000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(26.9000F, -19.2500F, 9.0000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_175", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(26.9000F, -20.4500F, 8.5000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_176", CubeListBuilder.create().texOffs(112, 38).addBox(-1.1000F, -5.9700F, -0.6000F, 2.2000F, 11.9400F, 1.2000F), PartPose.offsetAndRotation(3.0000F, 6.1800F, 2.5000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_177", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.6500F, -0.5000F, 1.1000F, 9.3100F, 1.0000F), PartPose.offsetAndRotation(3.0000F, 7.0100F, 2.0000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_178", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.5000F, -0.6500F, 2.6000F, 5.0100F, 1.3000F), PartPose.offsetAndRotation(3.0000F, 3.4300F, 1.1000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_179", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -1.7900F, -0.5000F, 1.4000F, 3.5800F, 1.0000F), PartPose.offsetAndRotation(3.0000F, 3.9100F, 0.7000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_180", CubeListBuilder.create().texOffs(138, 38).addBox(-1.1000F, -7.1500F, -0.6000F, 2.2000F, 14.3000F, 1.2000F), PartPose.offsetAndRotation(5.7600F, 4.3000F, 4.3800F, -0.2443F, 0.3421F, -0.3211F));
        part.addOrReplaceChild("rc_181", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.5800F, -0.5000F, 1.1000F, 11.1500F, 1.0000F), PartPose.offsetAndRotation(5.7600F, 5.3100F, 3.8800F, -0.2443F, 0.3421F, -0.3211F));
        part.addOrReplaceChild("rc_182", CubeListBuilder.create().texOffs(150, 38).addBox(-1.1000F, -8.1700F, -0.6000F, 2.2000F, 16.3300F, 1.2000F), PartPose.offsetAndRotation(8.5200F, 2.3800F, 6.1600F, -0.2443F, 0.3700F, -0.3979F));
        part.addOrReplaceChild("rc_183", CubeListBuilder.create().texOffs(157, 38).addBox(-0.5500F, -6.3700F, -0.5000F, 1.1000F, 12.7400F, 1.0000F), PartPose.offsetAndRotation(8.5200F, 3.5200F, 5.6600F, -0.2443F, 0.3700F, -0.3979F));
        part.addOrReplaceChild("rc_184", CubeListBuilder.create().texOffs(162, 38).addBox(-1.3000F, -3.4300F, -0.6500F, 2.6000F, 6.8600F, 1.3000F), PartPose.offsetAndRotation(8.5200F, -1.3800F, 4.7600F, -0.2094F, 0.3700F, -0.3979F));
        part.addOrReplaceChild("rc_185", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.4500F, -0.5000F, 1.4000F, 4.9000F, 1.0000F), PartPose.offsetAndRotation(8.5200F, -0.7200F, 4.3600F, -0.2094F, 0.3700F, -0.3979F));
        part.addOrReplaceChild("rc_186", CubeListBuilder.create().texOffs(176, 38).addBox(-1.1000F, -8.9400F, -0.6000F, 2.2000F, 17.8800F, 1.2000F), PartPose.offsetAndRotation(11.2800F, 0.4100F, 7.7800F, -0.2443F, 0.3979F, -0.4747F));
        part.addOrReplaceChild("rc_187", CubeListBuilder.create().texOffs(183, 38).addBox(-0.5500F, -6.9700F, -0.5000F, 1.1000F, 13.9400F, 1.0000F), PartPose.offsetAndRotation(11.2800F, 1.6700F, 7.2800F, -0.2443F, 0.3979F, -0.4747F));
        part.addOrReplaceChild("rc_188", CubeListBuilder.create().texOffs(188, 38).addBox(-1.1000F, -9.4200F, -0.6000F, 2.2000F, 18.8400F, 1.2000F), PartPose.offsetAndRotation(14.0400F, -1.5700F, 9.1500F, -0.2443F, 0.4259F, -0.5515F));
        part.addOrReplaceChild("rc_189", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.3400F, -0.5000F, 1.1000F, 14.6900F, 1.0000F), PartPose.offsetAndRotation(14.0400F, -0.2500F, 8.6500F, -0.2443F, 0.4259F, -0.5515F));
        part.addOrReplaceChild("rc_190", CubeListBuilder.create().texOffs(200, 38).addBox(-1.3000F, -3.9600F, -0.6500F, 2.6000F, 7.9100F, 1.3000F), PartPose.offsetAndRotation(14.0400F, -5.9000F, 7.7500F, -0.2094F, 0.4259F, -0.5515F));
        part.addOrReplaceChild("rc_191", CubeListBuilder.create().texOffs(209, 38).addBox(-0.7000F, -2.8200F, -0.5000F, 1.4000F, 5.6500F, 1.0000F), PartPose.offsetAndRotation(14.0400F, -5.1500F, 7.3500F, -0.2094F, 0.4259F, -0.5515F));
        part.addOrReplaceChild("rc_192", CubeListBuilder.create().texOffs(188, 38).addBox(-1.1000F, -9.5700F, -0.6000F, 2.2000F, 19.1400F, 1.2000F), PartPose.offsetAndRotation(16.8000F, -3.5500F, 10.2500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_193", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.4600F, -0.5000F, 1.1000F, 14.9300F, 1.0000F), PartPose.offsetAndRotation(16.8000F, -2.2100F, 9.7500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_194", CubeListBuilder.create().texOffs(188, 38).addBox(-1.1000F, -9.3800F, -0.6000F, 2.2000F, 18.7600F, 1.2000F), PartPose.offsetAndRotation(19.5600F, -5.4900F, 11.0500F, -0.2443F, 0.4817F, -0.7051F));
        part.addOrReplaceChild("rc_195", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.3300F, -0.5000F, 1.1000F, 14.6400F, 1.0000F), PartPose.offsetAndRotation(19.5600F, -4.1700F, 10.5500F, -0.2443F, 0.4817F, -0.7051F));
        part.addOrReplaceChild("rc_196", CubeListBuilder.create().texOffs(200, 38).addBox(-1.3000F, -3.9400F, -0.6500F, 2.6000F, 7.8800F, 1.3000F), PartPose.offsetAndRotation(19.5600F, -9.8000F, 9.6500F, -0.2094F, 0.4817F, -0.7051F));
        part.addOrReplaceChild("rc_197", CubeListBuilder.create().texOffs(209, 38).addBox(-0.7000F, -2.8200F, -0.5000F, 1.4000F, 5.6300F, 1.0000F), PartPose.offsetAndRotation(19.5600F, -9.0500F, 9.2500F, -0.2094F, 0.4817F, -0.7051F));
        part.addOrReplaceChild("rc_198", CubeListBuilder.create().texOffs(176, 38).addBox(-1.1000F, -8.8700F, -0.6000F, 2.2000F, 17.7400F, 1.2000F), PartPose.offsetAndRotation(22.3200F, -7.3600F, 11.5800F, -0.2443F, 0.5096F, -0.7819F));
        part.addOrReplaceChild("rc_199", CubeListBuilder.create().texOffs(183, 38).addBox(-0.5500F, -6.9200F, -0.5000F, 1.1000F, 13.8300F, 1.0000F), PartPose.offsetAndRotation(22.3200F, -6.1100F, 11.0800F, -0.2443F, 0.5096F, -0.7819F));
        part.addOrReplaceChild("rc_200", CubeListBuilder.create().texOffs(150, 38).addBox(-1.1000F, -8.0700F, -0.6000F, 2.2000F, 16.1300F, 1.2000F), PartPose.offsetAndRotation(25.0800F, -9.1100F, 11.8600F, -0.2443F, 0.5376F, -0.8587F));
        part.addOrReplaceChild("rc_201", CubeListBuilder.create().texOffs(157, 38).addBox(-0.5500F, -6.2900F, -0.5000F, 1.1000F, 12.5800F, 1.0000F), PartPose.offsetAndRotation(25.0800F, -7.9800F, 11.3600F, -0.2443F, 0.5376F, -0.8587F));
        part.addOrReplaceChild("rc_202", CubeListBuilder.create().texOffs(162, 38).addBox(-1.3000F, -3.3800F, -0.6500F, 2.6000F, 6.7700F, 1.3000F), PartPose.offsetAndRotation(25.0800F, -12.8200F, 10.4600F, -0.2094F, 0.5376F, -0.8587F));
        part.addOrReplaceChild("rc_203", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.4200F, -0.5000F, 1.4000F, 4.8400F, 1.0000F), PartPose.offsetAndRotation(25.0800F, -12.1800F, 10.0600F, -0.2094F, 0.5376F, -0.8587F));
        part.addOrReplaceChild("rc_204", CubeListBuilder.create().texOffs(138, 38).addBox(-1.1000F, -7.0300F, -0.6000F, 2.2000F, 14.0600F, 1.2000F), PartPose.offsetAndRotation(27.8400F, -10.7200F, 11.9800F, -0.2443F, 0.5655F, -0.9355F));
        part.addOrReplaceChild("rc_205", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.4900F, -0.5000F, 1.1000F, 10.9700F, 1.0000F), PartPose.offsetAndRotation(27.8400F, -9.7300F, 11.4800F, -0.2443F, 0.5655F, -0.9355F));
        part.addOrReplaceChild("rc_206", CubeListBuilder.create().texOffs(112, 38).addBox(-1.1000F, -5.8400F, -0.6000F, 2.2000F, 11.6700F, 1.2000F), PartPose.offsetAndRotation(30.6000F, -12.1300F, 12.0000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_207", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.5500F, -0.5000F, 1.1000F, 9.1000F, 1.0000F), PartPose.offsetAndRotation(30.6000F, -11.3100F, 11.5000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_208", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.4500F, -0.6500F, 2.6000F, 4.9000F, 1.3000F), PartPose.offsetAndRotation(30.6000F, -14.8100F, 10.6000F, -0.2094F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_209", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -1.7500F, -0.5000F, 1.4000F, 3.5000F, 1.0000F), PartPose.offsetAndRotation(30.6000F, -14.3500F, 10.2000F, -0.2094F, 0.5934F, -1.0123F));
        return part;
    }
    private static PartDefinition build_wing_upper_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_upper_right", cubes, PartPose.offset(-4.0000F, -2.0000F, 0.0000F));
        part.addOrReplaceChild("rc_210", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -1.2000F, -1.1000F, 6.6000F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(-0.5000F, -0.0000F, 0.0000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_211", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-0.5000F, -1.2000F, -0.5000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_212", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -1.1200F, -1.0400F, 6.6000F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(-5.7800F, -5.8300F, 3.8600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_213", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-5.7800F, -7.0300F, 3.3600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_214", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -1.0400F, -0.9800F, 6.6000F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(-11.0600F, -11.0600F, 6.9300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_215", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-11.0600F, -12.2600F, 6.4300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_216", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -0.9600F, -0.9200F, 6.6000F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(-16.3400F, -15.2000F, 8.7300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_217", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-16.3400F, -16.4000F, 8.2300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_218", CubeListBuilder.create().texOffs(61, 38).addBox(-3.3000F, -0.8800F, -0.8600F, 6.6000F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(-21.6200F, -17.9500F, 9.2600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_219", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-21.6200F, -19.1500F, 8.7600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_220", CubeListBuilder.create().texOffs(93, 38).addBox(-3.3000F, -0.8000F, -0.8000F, 6.6000F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-26.9000F, -19.2500F, 9.0000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_221", CubeListBuilder.create().texOffs(80, 38).addBox(-2.7000F, -0.6000F, -0.7000F, 5.4000F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-26.9000F, -20.4500F, 8.5000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_222", CubeListBuilder.create().texOffs(214, 38).addBox(-1.1000F, -5.9700F, -0.6000F, 2.2000F, 11.9400F, 1.2000F), PartPose.offsetAndRotation(-3.0000F, 6.1800F, 2.5000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_223", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.6500F, -0.5000F, 1.1000F, 9.3100F, 1.0000F), PartPose.offsetAndRotation(-3.0000F, 7.0100F, 2.0000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_224", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.5000F, -0.6500F, 2.6000F, 5.0100F, 1.3000F), PartPose.offsetAndRotation(-3.0000F, 3.4300F, 1.1000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_225", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -1.7900F, -0.5000F, 1.4000F, 3.5800F, 1.0000F), PartPose.offsetAndRotation(-3.0000F, 3.9100F, 0.7000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_226", CubeListBuilder.create().texOffs(221, 38).addBox(-1.1000F, -7.1500F, -0.6000F, 2.2000F, 14.3000F, 1.2000F), PartPose.offsetAndRotation(-5.7600F, 4.3000F, 4.3800F, -0.2443F, -0.3421F, 0.3211F));
        part.addOrReplaceChild("rc_227", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.5800F, -0.5000F, 1.1000F, 11.1500F, 1.0000F), PartPose.offsetAndRotation(-5.7600F, 5.3100F, 3.8800F, -0.2443F, -0.3421F, 0.3211F));
        part.addOrReplaceChild("rc_228", CubeListBuilder.create().texOffs(228, 38).addBox(-1.1000F, -8.1700F, -0.6000F, 2.2000F, 16.3300F, 1.2000F), PartPose.offsetAndRotation(-8.5200F, 2.3800F, 6.1600F, -0.2443F, -0.3700F, 0.3979F));
        part.addOrReplaceChild("rc_229", CubeListBuilder.create().texOffs(157, 38).addBox(-0.5500F, -6.3700F, -0.5000F, 1.1000F, 12.7400F, 1.0000F), PartPose.offsetAndRotation(-8.5200F, 3.5200F, 5.6600F, -0.2443F, -0.3700F, 0.3979F));
        part.addOrReplaceChild("rc_230", CubeListBuilder.create().texOffs(162, 38).addBox(-1.3000F, -3.4300F, -0.6500F, 2.6000F, 6.8600F, 1.3000F), PartPose.offsetAndRotation(-8.5200F, -1.3800F, 4.7600F, -0.2094F, -0.3700F, 0.3979F));
        part.addOrReplaceChild("rc_231", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.4500F, -0.5000F, 1.4000F, 4.9000F, 1.0000F), PartPose.offsetAndRotation(-8.5200F, -0.7200F, 4.3600F, -0.2094F, -0.3700F, 0.3979F));
        part.addOrReplaceChild("rc_232", CubeListBuilder.create().texOffs(235, 38).addBox(-1.1000F, -8.9400F, -0.6000F, 2.2000F, 17.8800F, 1.2000F), PartPose.offsetAndRotation(-11.2800F, 0.4100F, 7.7800F, -0.2443F, -0.3979F, 0.4747F));
        part.addOrReplaceChild("rc_233", CubeListBuilder.create().texOffs(183, 38).addBox(-0.5500F, -6.9700F, -0.5000F, 1.1000F, 13.9400F, 1.0000F), PartPose.offsetAndRotation(-11.2800F, 1.6700F, 7.2800F, -0.2443F, -0.3979F, 0.4747F));
        part.addOrReplaceChild("rc_234", CubeListBuilder.create().texOffs(242, 38).addBox(-1.1000F, -9.4200F, -0.6000F, 2.2000F, 18.8400F, 1.2000F), PartPose.offsetAndRotation(-14.0400F, -1.5700F, 9.1500F, -0.2443F, -0.4259F, 0.5515F));
        part.addOrReplaceChild("rc_235", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.3400F, -0.5000F, 1.1000F, 14.6900F, 1.0000F), PartPose.offsetAndRotation(-14.0400F, -0.2500F, 8.6500F, -0.2443F, -0.4259F, 0.5515F));
        part.addOrReplaceChild("rc_236", CubeListBuilder.create().texOffs(200, 38).addBox(-1.3000F, -3.9600F, -0.6500F, 2.6000F, 7.9100F, 1.3000F), PartPose.offsetAndRotation(-14.0400F, -5.9000F, 7.7500F, -0.2094F, -0.4259F, 0.5515F));
        part.addOrReplaceChild("rc_237", CubeListBuilder.create().texOffs(209, 38).addBox(-0.7000F, -2.8200F, -0.5000F, 1.4000F, 5.6500F, 1.0000F), PartPose.offsetAndRotation(-14.0400F, -5.1500F, 7.3500F, -0.2094F, -0.4259F, 0.5515F));
        part.addOrReplaceChild("rc_238", CubeListBuilder.create().texOffs(242, 38).addBox(-1.1000F, -9.5700F, -0.6000F, 2.2000F, 19.1400F, 1.2000F), PartPose.offsetAndRotation(-16.8000F, -3.5500F, 10.2500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_239", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.4600F, -0.5000F, 1.1000F, 14.9300F, 1.0000F), PartPose.offsetAndRotation(-16.8000F, -2.2100F, 9.7500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_240", CubeListBuilder.create().texOffs(242, 38).addBox(-1.1000F, -9.3800F, -0.6000F, 2.2000F, 18.7600F, 1.2000F), PartPose.offsetAndRotation(-19.5600F, -5.4900F, 11.0500F, -0.2443F, -0.4817F, 0.7051F));
        part.addOrReplaceChild("rc_241", CubeListBuilder.create().texOffs(195, 38).addBox(-0.5500F, -7.3300F, -0.5000F, 1.1000F, 14.6400F, 1.0000F), PartPose.offsetAndRotation(-19.5600F, -4.1700F, 10.5500F, -0.2443F, -0.4817F, 0.7051F));
        part.addOrReplaceChild("rc_242", CubeListBuilder.create().texOffs(200, 38).addBox(-1.3000F, -3.9400F, -0.6500F, 2.6000F, 7.8800F, 1.3000F), PartPose.offsetAndRotation(-19.5600F, -9.8000F, 9.6500F, -0.2094F, -0.4817F, 0.7051F));
        part.addOrReplaceChild("rc_243", CubeListBuilder.create().texOffs(209, 38).addBox(-0.7000F, -2.8200F, -0.5000F, 1.4000F, 5.6300F, 1.0000F), PartPose.offsetAndRotation(-19.5600F, -9.0500F, 9.2500F, -0.2094F, -0.4817F, 0.7051F));
        part.addOrReplaceChild("rc_244", CubeListBuilder.create().texOffs(235, 38).addBox(-1.1000F, -8.8700F, -0.6000F, 2.2000F, 17.7400F, 1.2000F), PartPose.offsetAndRotation(-22.3200F, -7.3600F, 11.5800F, -0.2443F, -0.5096F, 0.7819F));
        part.addOrReplaceChild("rc_245", CubeListBuilder.create().texOffs(183, 38).addBox(-0.5500F, -6.9200F, -0.5000F, 1.1000F, 13.8300F, 1.0000F), PartPose.offsetAndRotation(-22.3200F, -6.1100F, 11.0800F, -0.2443F, -0.5096F, 0.7819F));
        part.addOrReplaceChild("rc_246", CubeListBuilder.create().texOffs(228, 38).addBox(-1.1000F, -8.0700F, -0.6000F, 2.2000F, 16.1300F, 1.2000F), PartPose.offsetAndRotation(-25.0800F, -9.1100F, 11.8600F, -0.2443F, -0.5376F, 0.8587F));
        part.addOrReplaceChild("rc_247", CubeListBuilder.create().texOffs(157, 38).addBox(-0.5500F, -6.2900F, -0.5000F, 1.1000F, 12.5800F, 1.0000F), PartPose.offsetAndRotation(-25.0800F, -7.9800F, 11.3600F, -0.2443F, -0.5376F, 0.8587F));
        part.addOrReplaceChild("rc_248", CubeListBuilder.create().texOffs(162, 38).addBox(-1.3000F, -3.3800F, -0.6500F, 2.6000F, 6.7700F, 1.3000F), PartPose.offsetAndRotation(-25.0800F, -12.8200F, 10.4600F, -0.2094F, -0.5376F, 0.8587F));
        part.addOrReplaceChild("rc_249", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.4200F, -0.5000F, 1.4000F, 4.8400F, 1.0000F), PartPose.offsetAndRotation(-25.0800F, -12.1800F, 10.0600F, -0.2094F, -0.5376F, 0.8587F));
        part.addOrReplaceChild("rc_250", CubeListBuilder.create().texOffs(221, 38).addBox(-1.1000F, -7.0300F, -0.6000F, 2.2000F, 14.0600F, 1.2000F), PartPose.offsetAndRotation(-27.8400F, -10.7200F, 11.9800F, -0.2443F, -0.5655F, 0.9355F));
        part.addOrReplaceChild("rc_251", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.4900F, -0.5000F, 1.1000F, 10.9700F, 1.0000F), PartPose.offsetAndRotation(-27.8400F, -9.7300F, 11.4800F, -0.2443F, -0.5655F, 0.9355F));
        part.addOrReplaceChild("rc_252", CubeListBuilder.create().texOffs(214, 38).addBox(-1.1000F, -5.8400F, -0.6000F, 2.2000F, 11.6700F, 1.2000F), PartPose.offsetAndRotation(-30.6000F, -12.1300F, 12.0000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_253", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.5500F, -0.5000F, 1.1000F, 9.1000F, 1.0000F), PartPose.offsetAndRotation(-30.6000F, -11.3100F, 11.5000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_254", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.4500F, -0.6500F, 2.6000F, 4.9000F, 1.3000F), PartPose.offsetAndRotation(-30.6000F, -14.8100F, 10.6000F, -0.2094F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_255", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -1.7500F, -0.5000F, 1.4000F, 3.5000F, 1.0000F), PartPose.offsetAndRotation(-30.6000F, -14.3500F, 10.2000F, -0.2094F, -0.5934F, 1.0123F));
        return part;
    }
    private static PartDefinition build_wing_mid_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_mid_left", cubes, PartPose.offset(4.0000F, 4.0000F, 0.0000F));
        part.addOrReplaceChild("rc_256", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -1.2000F, -1.1000F, 5.2800F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(0.5000F, -0.0000F, 0.0000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_257", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(0.5000F, -1.2000F, -0.5000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_258", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -1.1200F, -1.0400F, 5.2800F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(4.7200F, -3.1400F, 3.8600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_259", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(4.7200F, -4.3400F, 3.3600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_260", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -1.0400F, -0.9800F, 5.2800F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(8.9500F, -5.8000F, 6.9300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_261", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(8.9500F, -7.0000F, 6.4300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_262", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -0.9600F, -0.9200F, 5.2800F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(13.1700F, -7.6000F, 8.7300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_263", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(13.1700F, -8.8000F, 8.2300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_264", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -0.8800F, -0.8600F, 5.2800F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(17.4000F, -8.2800F, 9.2600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_265", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(17.4000F, -9.4800F, 8.7600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_266", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -0.8000F, -0.8000F, 5.2800F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(21.6200F, -7.8000F, 9.0000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_267", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(21.6200F, -9.0000F, 8.5000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_268", CubeListBuilder.create().texOffs(290, 38).addBox(-1.1000F, -4.7700F, -0.6000F, 2.2000F, 9.5500F, 1.2000F), PartPose.offsetAndRotation(3.0000F, 5.3400F, 2.5000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_269", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.7200F, -0.5000F, 1.1000F, 7.4500F, 1.0000F), PartPose.offsetAndRotation(3.0000F, 6.0100F, 2.0000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_270", CubeListBuilder.create().texOffs(297, 38).addBox(-1.3000F, -2.0100F, -0.6500F, 2.6000F, 4.0100F, 1.3000F), PartPose.offsetAndRotation(3.0000F, 3.1500F, 1.1000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_271", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.4400F, -0.5000F, 1.4000F, 2.8700F, 1.0000F), PartPose.offsetAndRotation(3.0000F, 3.5300F, 0.7000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_272", CubeListBuilder.create().texOffs(311, 38).addBox(-1.1000F, -5.9400F, -0.6000F, 2.2000F, 11.8800F, 1.2000F), PartPose.offsetAndRotation(5.7600F, 4.4100F, 4.8400F, -0.2443F, 0.3491F, -0.3403F));
        part.addOrReplaceChild("rc_273", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.6300F, -0.5000F, 1.1000F, 9.2600F, 1.0000F), PartPose.offsetAndRotation(5.7600F, 5.2400F, 4.3400F, -0.2443F, 0.3491F, -0.3403F));
        part.addOrReplaceChild("rc_274", CubeListBuilder.create().texOffs(318, 38).addBox(-1.1000F, -6.8700F, -0.6000F, 2.2000F, 13.7400F, 1.2000F), PartPose.offsetAndRotation(8.5200F, 3.4200F, 7.0000F, -0.2443F, 0.3840F, -0.4363F));
        part.addOrReplaceChild("rc_275", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.3500F, -0.5000F, 1.1000F, 10.7100F, 1.0000F), PartPose.offsetAndRotation(8.5200F, 4.3800F, 6.5000F, -0.2443F, 0.3840F, -0.4363F));
        part.addOrReplaceChild("rc_276", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -2.8800F, -0.6500F, 2.6000F, 5.7700F, 1.3000F), PartPose.offsetAndRotation(8.5200F, 0.2600F, 5.6000F, -0.2094F, 0.3840F, -0.4363F));
        part.addOrReplaceChild("rc_277", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -2.0600F, -0.5000F, 1.4000F, 4.1200F, 1.0000F), PartPose.offsetAndRotation(8.5200F, 0.8100F, 5.2000F, -0.2094F, 0.3840F, -0.4363F));
        part.addOrReplaceChild("rc_278", CubeListBuilder.create().texOffs(334, 38).addBox(-1.1000F, -7.4600F, -0.6000F, 2.2000F, 14.9200F, 1.2000F), PartPose.offsetAndRotation(11.2800F, 2.3900F, 8.8300F, -0.2443F, 0.4189F, -0.5323F));
        part.addOrReplaceChild("rc_279", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.8200F, -0.5000F, 1.1000F, 11.6400F, 1.0000F), PartPose.offsetAndRotation(11.2800F, 3.4400F, 8.3300F, -0.2443F, 0.4189F, -0.5323F));
        part.addOrReplaceChild("rc_280", CubeListBuilder.create().texOffs(334, 38).addBox(-1.1000F, -7.6500F, -0.6000F, 2.2000F, 15.3100F, 1.2000F), PartPose.offsetAndRotation(14.0400F, 1.3600F, 10.2500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_281", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.9600F, -0.5000F, 1.1000F, 11.9400F, 1.0000F), PartPose.offsetAndRotation(14.0400F, 2.4300F, 9.7500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_282", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -3.2100F, -0.6500F, 2.6000F, 6.4300F, 1.3000F), PartPose.offsetAndRotation(14.0400F, -2.1600F, 8.8500F, -0.2094F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_283", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.2900F, -0.5000F, 1.4000F, 4.5900F, 1.0000F), PartPose.offsetAndRotation(14.0400F, -1.5500F, 8.4500F, -0.2094F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_284", CubeListBuilder.create().texOffs(334, 38).addBox(-1.1000F, -7.4200F, -0.6000F, 2.2000F, 14.8500F, 1.2000F), PartPose.offsetAndRotation(16.8000F, 0.3800F, 11.2100F, -0.2443F, 0.4887F, -0.7243F));
        part.addOrReplaceChild("rc_285", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.8000F, -0.5000F, 1.1000F, 11.5900F, 1.0000F), PartPose.offsetAndRotation(16.8000F, 1.4200F, 10.7100F, -0.2443F, 0.4887F, -0.7243F));
        part.addOrReplaceChild("rc_286", CubeListBuilder.create().texOffs(318, 38).addBox(-1.1000F, -6.8000F, -0.6000F, 2.2000F, 13.6000F, 1.2000F), PartPose.offsetAndRotation(19.5600F, -0.5000F, 11.7500F, -0.2443F, 0.5236F, -0.8203F));
        part.addOrReplaceChild("rc_287", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.3000F, -0.5000F, 1.1000F, 10.6100F, 1.0000F), PartPose.offsetAndRotation(19.5600F, 0.4500F, 11.2500F, -0.2443F, 0.5236F, -0.8203F));
        part.addOrReplaceChild("rc_288", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -2.8500F, -0.6500F, 2.6000F, 5.7100F, 1.3000F), PartPose.offsetAndRotation(19.5600F, -3.6300F, 10.3500F, -0.2094F, 0.5236F, -0.8203F));
        part.addOrReplaceChild("rc_289", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -2.0400F, -0.5000F, 1.4000F, 4.0800F, 1.0000F), PartPose.offsetAndRotation(19.5600F, -3.0800F, 9.9500F, -0.2094F, 0.5236F, -0.8203F));
        part.addOrReplaceChild("rc_290", CubeListBuilder.create().texOffs(311, 38).addBox(-1.1000F, -5.8500F, -0.6000F, 2.2000F, 11.6900F, 1.2000F), PartPose.offsetAndRotation(22.3200F, -1.2100F, 11.9600F, -0.2443F, 0.5585F, -0.9163F));
        part.addOrReplaceChild("rc_291", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.5600F, -0.5000F, 1.1000F, 9.1200F, 1.0000F), PartPose.offsetAndRotation(22.3200F, -0.4000F, 11.4600F, -0.2443F, 0.5585F, -0.9163F));
        part.addOrReplaceChild("rc_292", CubeListBuilder.create().texOffs(346, 38).addBox(-1.1000F, -4.6800F, -0.6000F, 2.2000F, 9.3400F, 1.2000F), PartPose.offsetAndRotation(25.0800F, -1.7000F, 12.0000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_293", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6400F, -0.5000F, 1.1000F, 7.2800F, 1.0000F), PartPose.offsetAndRotation(25.0800F, -1.0500F, 11.5000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_294", CubeListBuilder.create().texOffs(297, 38).addBox(-1.3000F, -1.9600F, -0.6500F, 2.6000F, 3.9200F, 1.3000F), PartPose.offsetAndRotation(25.0800F, -3.8500F, 10.6000F, -0.2094F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_295", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.4000F, -0.5000F, 1.4000F, 2.8000F, 1.0000F), PartPose.offsetAndRotation(25.0800F, -3.4800F, 10.2000F, -0.2094F, 0.5934F, -1.0123F));
        return part;
    }
    private static PartDefinition build_wing_mid_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_mid_right", cubes, PartPose.offset(-4.0000F, 4.0000F, 0.0000F));
        part.addOrReplaceChild("rc_296", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -1.2000F, -1.1000F, 5.2800F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(-0.5000F, -0.0000F, 0.0000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_297", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-0.5000F, -1.2000F, -0.5000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_298", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -1.1200F, -1.0400F, 5.2800F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(-4.7200F, -3.1400F, 3.8600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_299", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-4.7200F, -4.3400F, 3.3600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_300", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -1.0400F, -0.9800F, 5.2800F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(-8.9500F, -5.8000F, 6.9300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_301", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-8.9500F, -7.0000F, 6.4300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_302", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -0.9600F, -0.9200F, 5.2800F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(-13.1700F, -7.6000F, 8.7300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_303", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-13.1700F, -8.8000F, 8.2300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_304", CubeListBuilder.create().texOffs(249, 38).addBox(-2.6400F, -0.8800F, -0.8600F, 5.2800F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(-17.4000F, -8.2800F, 9.2600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_305", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-17.4000F, -9.4800F, 8.7600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_306", CubeListBuilder.create().texOffs(275, 38).addBox(-2.6400F, -0.8000F, -0.8000F, 5.2800F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-21.6200F, -7.8000F, 9.0000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_307", CubeListBuilder.create().texOffs(264, 38).addBox(-2.1600F, -0.6000F, -0.7000F, 4.3200F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-21.6200F, -9.0000F, 8.5000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_308", CubeListBuilder.create().texOffs(353, 38).addBox(-1.1000F, -4.7700F, -0.6000F, 2.2000F, 9.5500F, 1.2000F), PartPose.offsetAndRotation(-3.0000F, 5.3400F, 2.5000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_309", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.7200F, -0.5000F, 1.1000F, 7.4500F, 1.0000F), PartPose.offsetAndRotation(-3.0000F, 6.0100F, 2.0000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_310", CubeListBuilder.create().texOffs(297, 38).addBox(-1.3000F, -2.0100F, -0.6500F, 2.6000F, 4.0100F, 1.3000F), PartPose.offsetAndRotation(-3.0000F, 3.1500F, 1.1000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_311", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.4400F, -0.5000F, 1.4000F, 2.8700F, 1.0000F), PartPose.offsetAndRotation(-3.0000F, 3.5300F, 0.7000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_312", CubeListBuilder.create().texOffs(360, 38).addBox(-1.1000F, -5.9400F, -0.6000F, 2.2000F, 11.8800F, 1.2000F), PartPose.offsetAndRotation(-5.7600F, 4.4100F, 4.8400F, -0.2443F, -0.3491F, 0.3403F));
        part.addOrReplaceChild("rc_313", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.6300F, -0.5000F, 1.1000F, 9.2600F, 1.0000F), PartPose.offsetAndRotation(-5.7600F, 5.2400F, 4.3400F, -0.2443F, -0.3491F, 0.3403F));
        part.addOrReplaceChild("rc_314", CubeListBuilder.create().texOffs(367, 38).addBox(-1.1000F, -6.8700F, -0.6000F, 2.2000F, 13.7400F, 1.2000F), PartPose.offsetAndRotation(-8.5200F, 3.4200F, 7.0000F, -0.2443F, -0.3840F, 0.4363F));
        part.addOrReplaceChild("rc_315", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.3500F, -0.5000F, 1.1000F, 10.7100F, 1.0000F), PartPose.offsetAndRotation(-8.5200F, 4.3800F, 6.5000F, -0.2443F, -0.3840F, 0.4363F));
        part.addOrReplaceChild("rc_316", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -2.8800F, -0.6500F, 2.6000F, 5.7700F, 1.3000F), PartPose.offsetAndRotation(-8.5200F, 0.2600F, 5.6000F, -0.2094F, -0.3840F, 0.4363F));
        part.addOrReplaceChild("rc_317", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -2.0600F, -0.5000F, 1.4000F, 4.1200F, 1.0000F), PartPose.offsetAndRotation(-8.5200F, 0.8100F, 5.2000F, -0.2094F, -0.3840F, 0.4363F));
        part.addOrReplaceChild("rc_318", CubeListBuilder.create().texOffs(374, 38).addBox(-1.1000F, -7.4600F, -0.6000F, 2.2000F, 14.9200F, 1.2000F), PartPose.offsetAndRotation(-11.2800F, 2.3900F, 8.8300F, -0.2443F, -0.4189F, 0.5323F));
        part.addOrReplaceChild("rc_319", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.8200F, -0.5000F, 1.1000F, 11.6400F, 1.0000F), PartPose.offsetAndRotation(-11.2800F, 3.4400F, 8.3300F, -0.2443F, -0.4189F, 0.5323F));
        part.addOrReplaceChild("rc_320", CubeListBuilder.create().texOffs(374, 38).addBox(-1.1000F, -7.6500F, -0.6000F, 2.2000F, 15.3100F, 1.2000F), PartPose.offsetAndRotation(-14.0400F, 1.3600F, 10.2500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_321", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.9600F, -0.5000F, 1.1000F, 11.9400F, 1.0000F), PartPose.offsetAndRotation(-14.0400F, 2.4300F, 9.7500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_322", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -3.2100F, -0.6500F, 2.6000F, 6.4300F, 1.3000F), PartPose.offsetAndRotation(-14.0400F, -2.1600F, 8.8500F, -0.2094F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_323", CubeListBuilder.create().texOffs(171, 38).addBox(-0.7000F, -2.2900F, -0.5000F, 1.4000F, 4.5900F, 1.0000F), PartPose.offsetAndRotation(-14.0400F, -1.5500F, 8.4500F, -0.2094F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_324", CubeListBuilder.create().texOffs(374, 38).addBox(-1.1000F, -7.4200F, -0.6000F, 2.2000F, 14.8500F, 1.2000F), PartPose.offsetAndRotation(-16.8000F, 0.3800F, 11.2100F, -0.2443F, -0.4887F, 0.7243F));
        part.addOrReplaceChild("rc_325", CubeListBuilder.create().texOffs(341, 38).addBox(-0.5500F, -5.8000F, -0.5000F, 1.1000F, 11.5900F, 1.0000F), PartPose.offsetAndRotation(-16.8000F, 1.4200F, 10.7100F, -0.2443F, -0.4887F, 0.7243F));
        part.addOrReplaceChild("rc_326", CubeListBuilder.create().texOffs(367, 38).addBox(-1.1000F, -6.8000F, -0.6000F, 2.2000F, 13.6000F, 1.2000F), PartPose.offsetAndRotation(-19.5600F, -0.5000F, 11.7500F, -0.2443F, -0.5236F, 0.8203F));
        part.addOrReplaceChild("rc_327", CubeListBuilder.create().texOffs(145, 38).addBox(-0.5500F, -5.3000F, -0.5000F, 1.1000F, 10.6100F, 1.0000F), PartPose.offsetAndRotation(-19.5600F, 0.4500F, 11.2500F, -0.2443F, -0.5236F, 0.8203F));
        part.addOrReplaceChild("rc_328", CubeListBuilder.create().texOffs(325, 38).addBox(-1.3000F, -2.8500F, -0.6500F, 2.6000F, 5.7100F, 1.3000F), PartPose.offsetAndRotation(-19.5600F, -3.6300F, 10.3500F, -0.2094F, -0.5236F, 0.8203F));
        part.addOrReplaceChild("rc_329", CubeListBuilder.create().texOffs(133, 38).addBox(-0.7000F, -2.0400F, -0.5000F, 1.4000F, 4.0800F, 1.0000F), PartPose.offsetAndRotation(-19.5600F, -3.0800F, 9.9500F, -0.2094F, -0.5236F, 0.8203F));
        part.addOrReplaceChild("rc_330", CubeListBuilder.create().texOffs(360, 38).addBox(-1.1000F, -5.8500F, -0.6000F, 2.2000F, 11.6900F, 1.2000F), PartPose.offsetAndRotation(-22.3200F, -1.2100F, 11.9600F, -0.2443F, -0.5585F, 0.9163F));
        part.addOrReplaceChild("rc_331", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.5600F, -0.5000F, 1.1000F, 9.1200F, 1.0000F), PartPose.offsetAndRotation(-22.3200F, -0.4000F, 11.4600F, -0.2443F, -0.5585F, 0.9163F));
        part.addOrReplaceChild("rc_332", CubeListBuilder.create().texOffs(381, 38).addBox(-1.1000F, -4.6800F, -0.6000F, 2.2000F, 9.3400F, 1.2000F), PartPose.offsetAndRotation(-25.0800F, -1.7000F, 12.0000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_333", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6400F, -0.5000F, 1.1000F, 7.2800F, 1.0000F), PartPose.offsetAndRotation(-25.0800F, -1.0500F, 11.5000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_334", CubeListBuilder.create().texOffs(297, 38).addBox(-1.3000F, -1.9600F, -0.6500F, 2.6000F, 3.9200F, 1.3000F), PartPose.offsetAndRotation(-25.0800F, -3.8500F, 10.6000F, -0.2094F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_335", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.4000F, -0.5000F, 1.4000F, 2.8000F, 1.0000F), PartPose.offsetAndRotation(-25.0800F, -3.4800F, 10.2000F, -0.2094F, -0.5934F, 1.0123F));
        return part;
    }
    private static PartDefinition build_wing_lower_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_lower_left", cubes, PartPose.offset(3.0000F, 10.0000F, 0.0000F));
        part.addOrReplaceChild("rc_336", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -1.2000F, -1.1000F, 3.9600F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(1.5000F, -0.0000F, 0.0000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_337", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(1.5000F, -1.2000F, -0.5000F, -0.1745F, 0.2793F, 0.4538F));
        part.addOrReplaceChild("rc_338", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -1.1200F, -1.0400F, 3.9600F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(4.6700F, -0.4600F, 3.8600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_339", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(4.6700F, -1.6600F, 3.3600F, -0.2164F, 0.3281F, 0.3072F));
        part.addOrReplaceChild("rc_340", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -1.0400F, -0.9800F, 3.9600F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(7.8400F, -0.5500F, 6.9300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_341", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(7.8400F, -1.7500F, 6.4300F, -0.2583F, 0.3770F, 0.1606F));
        part.addOrReplaceChild("rc_342", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -0.9600F, -0.9200F, 3.9600F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(11.0000F, -0.0000F, 8.7300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_343", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(11.0000F, -1.2000F, 8.2300F, -0.3002F, 0.4259F, 0.0140F));
        part.addOrReplaceChild("rc_344", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -0.8800F, -0.8600F, 3.9600F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(14.1700F, 1.3900F, 9.2600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_345", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(14.1700F, 0.1900F, 8.7600F, -0.3421F, 0.4747F, -0.1326F));
        part.addOrReplaceChild("rc_346", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -0.8000F, -0.8000F, 3.9600F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(17.3400F, 3.6500F, 9.0000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_347", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(17.3400F, 2.4500F, 8.5000F, -0.3840F, 0.5236F, -0.2793F));
        part.addOrReplaceChild("rc_348", CubeListBuilder.create().texOffs(410, 38).addBox(-1.1000F, -3.5800F, -0.6000F, 2.2000F, 7.1600F, 1.2000F), PartPose.offsetAndRotation(4.0000F, 4.5100F, 2.5000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_349", CubeListBuilder.create().texOffs(58, 21).addBox(-0.5500F, -2.8000F, -0.5000F, 1.1000F, 5.5900F, 1.0000F), PartPose.offsetAndRotation(4.0000F, 5.0100F, 2.0000F, -0.2443F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_350", CubeListBuilder.create().texOffs(417, 38).addBox(-1.3000F, -1.5100F, -0.6500F, 2.6000F, 3.0100F, 1.3000F), PartPose.offsetAndRotation(4.0000F, 2.8600F, 1.1000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_351", CubeListBuilder.create().texOffs(426, 38).addBox(-0.7000F, -1.0800F, -0.5000F, 1.4000F, 2.1500F, 1.0000F), PartPose.offsetAndRotation(4.0000F, 3.1500F, 0.7000F, -0.2094F, 0.3142F, -0.2443F));
        part.addOrReplaceChild("rc_352", CubeListBuilder.create().texOffs(431, 38).addBox(-1.1000F, -4.7100F, -0.6000F, 2.2000F, 9.4200F, 1.2000F), PartPose.offsetAndRotation(6.7600F, 5.1500F, 5.5800F, -0.2443F, 0.3608F, -0.3723F));
        part.addOrReplaceChild("rc_353", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6800F, -0.5000F, 1.1000F, 7.3500F, 1.0000F), PartPose.offsetAndRotation(6.7600F, 5.8100F, 5.0800F, -0.2443F, 0.3608F, -0.3723F));
        part.addOrReplaceChild("rc_354", CubeListBuilder.create().texOffs(438, 38).addBox(-1.1000F, -5.4800F, -0.6000F, 2.2000F, 10.9600F, 1.2000F), PartPose.offsetAndRotation(9.5200F, 5.7200F, 8.2600F, -0.2443F, 0.4072F, -0.5004F));
        part.addOrReplaceChild("rc_355", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.2800F, -0.5000F, 1.1000F, 8.5500F, 1.0000F), PartPose.offsetAndRotation(9.5200F, 6.4900F, 7.7600F, -0.2443F, 0.4072F, -0.5004F));
        part.addOrReplaceChild("rc_356", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.3000F, -0.6500F, 2.6000F, 4.6000F, 1.3000F), PartPose.offsetAndRotation(9.5200F, 3.2000F, 6.8600F, -0.2094F, 0.4072F, -0.5004F));
        part.addOrReplaceChild("rc_357", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.6500F, -0.5000F, 1.4000F, 3.2900F, 1.0000F), PartPose.offsetAndRotation(9.5200F, 3.6400F, 6.4600F, -0.2094F, 0.4072F, -0.5004F));
        part.addOrReplaceChild("rc_358", CubeListBuilder.create().texOffs(438, 38).addBox(-1.1000F, -5.7400F, -0.6000F, 2.2000F, 11.4800F, 1.2000F), PartPose.offsetAndRotation(12.2800F, 6.2700F, 10.2500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_359", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.4900F, -0.5000F, 1.1000F, 8.9600F, 1.0000F), PartPose.offsetAndRotation(12.2800F, 7.0800F, 9.7500F, -0.2443F, 0.4538F, -0.6283F));
        part.addOrReplaceChild("rc_360", CubeListBuilder.create().texOffs(438, 38).addBox(-1.1000F, -5.4500F, -0.6000F, 2.2000F, 10.8900F, 1.2000F), PartPose.offsetAndRotation(15.0400F, 6.8900F, 11.4300F, -0.2443F, 0.5004F, -0.7563F));
        part.addOrReplaceChild("rc_361", CubeListBuilder.create().texOffs(445, 38).addBox(-0.5500F, -4.2400F, -0.5000F, 1.1000F, 8.4900F, 1.0000F), PartPose.offsetAndRotation(15.0400F, 7.6500F, 10.9300F, -0.2443F, 0.5004F, -0.7563F));
        part.addOrReplaceChild("rc_362", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.2800F, -0.6500F, 2.6000F, 4.5700F, 1.3000F), PartPose.offsetAndRotation(15.0400F, 4.3800F, 10.0300F, -0.2094F, 0.5004F, -0.7563F));
        part.addOrReplaceChild("rc_363", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.6400F, -0.5000F, 1.4000F, 3.2700F, 1.0000F), PartPose.offsetAndRotation(15.0400F, 4.8200F, 9.6300F, -0.2094F, 0.5004F, -0.7563F));
        part.addOrReplaceChild("rc_364", CubeListBuilder.create().texOffs(431, 38).addBox(-1.1000F, -4.6500F, -0.6000F, 2.2000F, 9.2900F, 1.2000F), PartPose.offsetAndRotation(17.8000F, 7.6700F, 11.9200F, -0.2443F, 0.5468F, -0.8844F));
        part.addOrReplaceChild("rc_365", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6300F, -0.5000F, 1.1000F, 7.2500F, 1.0000F), PartPose.offsetAndRotation(17.8000F, 8.3200F, 11.4200F, -0.2443F, 0.5468F, -0.8844F));
        part.addOrReplaceChild("rc_366", CubeListBuilder.create().texOffs(410, 38).addBox(-1.1000F, -3.5000F, -0.6000F, 2.2000F, 7.0000F, 1.2000F), PartPose.offsetAndRotation(20.5600F, 8.7200F, 12.0000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_367", CubeListBuilder.create().texOffs(450, 38).addBox(-0.5500F, -2.7300F, -0.5000F, 1.1000F, 5.4600F, 1.0000F), PartPose.offsetAndRotation(20.5600F, 9.2100F, 11.5000F, -0.2443F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_368", CubeListBuilder.create().texOffs(417, 38).addBox(-1.3000F, -1.4700F, -0.6500F, 2.6000F, 2.9400F, 1.3000F), PartPose.offsetAndRotation(20.5600F, 7.1100F, 10.6000F, -0.2094F, 0.5934F, -1.0123F));
        part.addOrReplaceChild("rc_369", CubeListBuilder.create().texOffs(426, 38).addBox(-0.7000F, -1.0500F, -0.5000F, 1.4000F, 2.1000F, 1.0000F), PartPose.offsetAndRotation(20.5600F, 7.3900F, 10.2000F, -0.2094F, 0.5934F, -1.0123F));
        return part;
    }
    private static PartDefinition build_wing_lower_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wing_lower_right", cubes, PartPose.offset(-3.0000F, 10.0000F, 0.0000F));
        part.addOrReplaceChild("rc_370", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -1.2000F, -1.1000F, 3.9600F, 2.4000F, 2.2000F), PartPose.offsetAndRotation(-1.5000F, -0.0000F, 0.0000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_371", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-1.5000F, -1.2000F, -0.5000F, -0.1745F, -0.2793F, -0.4538F));
        part.addOrReplaceChild("rc_372", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -1.1200F, -1.0400F, 3.9600F, 2.2400F, 2.0800F), PartPose.offsetAndRotation(-4.6700F, -0.4600F, 3.8600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_373", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-4.6700F, -1.6600F, 3.3600F, -0.2164F, -0.3281F, -0.3072F));
        part.addOrReplaceChild("rc_374", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -1.0400F, -0.9800F, 3.9600F, 2.0800F, 1.9600F), PartPose.offsetAndRotation(-7.8400F, -0.5500F, 6.9300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_375", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-7.8400F, -1.7500F, 6.4300F, -0.2583F, -0.3770F, -0.1606F));
        part.addOrReplaceChild("rc_376", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -0.9600F, -0.9200F, 3.9600F, 1.9200F, 1.8400F), PartPose.offsetAndRotation(-11.0000F, -0.0000F, 8.7300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_377", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-11.0000F, -1.2000F, 8.2300F, -0.3002F, -0.4259F, -0.0140F));
        part.addOrReplaceChild("rc_378", CubeListBuilder.create().texOffs(388, 38).addBox(-1.9800F, -0.8800F, -0.8600F, 3.9600F, 1.7600F, 1.7200F), PartPose.offsetAndRotation(-14.1700F, 1.3900F, 9.2600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_379", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-14.1700F, 0.1900F, 8.7600F, -0.3421F, -0.4747F, 0.1326F));
        part.addOrReplaceChild("rc_380", CubeListBuilder.create().texOffs(334, 0).addBox(-1.9800F, -0.8000F, -0.8000F, 3.9600F, 1.6000F, 1.6000F), PartPose.offsetAndRotation(-17.3400F, 3.6500F, 9.0000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_381", CubeListBuilder.create().texOffs(401, 38).addBox(-1.6200F, -0.6000F, -0.7000F, 3.2400F, 1.2000F, 1.4000F), PartPose.offsetAndRotation(-17.3400F, 2.4500F, 8.5000F, -0.3840F, -0.5236F, 0.2793F));
        part.addOrReplaceChild("rc_382", CubeListBuilder.create().texOffs(455, 38).addBox(-1.1000F, -3.5800F, -0.6000F, 2.2000F, 7.1600F, 1.2000F), PartPose.offsetAndRotation(-4.0000F, 4.5100F, 2.5000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_383", CubeListBuilder.create().texOffs(58, 21).addBox(-0.5500F, -2.8000F, -0.5000F, 1.1000F, 5.5900F, 1.0000F), PartPose.offsetAndRotation(-4.0000F, 5.0100F, 2.0000F, -0.2443F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_384", CubeListBuilder.create().texOffs(417, 38).addBox(-1.3000F, -1.5100F, -0.6500F, 2.6000F, 3.0100F, 1.3000F), PartPose.offsetAndRotation(-4.0000F, 2.8600F, 1.1000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_385", CubeListBuilder.create().texOffs(426, 38).addBox(-0.7000F, -1.0800F, -0.5000F, 1.4000F, 2.1500F, 1.0000F), PartPose.offsetAndRotation(-4.0000F, 3.1500F, 0.7000F, -0.2094F, -0.3142F, 0.2443F));
        part.addOrReplaceChild("rc_386", CubeListBuilder.create().texOffs(462, 38).addBox(-1.1000F, -4.7100F, -0.6000F, 2.2000F, 9.4200F, 1.2000F), PartPose.offsetAndRotation(-6.7600F, 5.1500F, 5.5800F, -0.2443F, -0.3608F, 0.3723F));
        part.addOrReplaceChild("rc_387", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6800F, -0.5000F, 1.1000F, 7.3500F, 1.0000F), PartPose.offsetAndRotation(-6.7600F, 5.8100F, 5.0800F, -0.2443F, -0.3608F, 0.3723F));
        part.addOrReplaceChild("rc_388", CubeListBuilder.create().texOffs(469, 38).addBox(-1.1000F, -5.4800F, -0.6000F, 2.2000F, 10.9600F, 1.2000F), PartPose.offsetAndRotation(-9.5200F, 5.7200F, 8.2600F, -0.2443F, -0.4072F, 0.5004F));
        part.addOrReplaceChild("rc_389", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.2800F, -0.5000F, 1.1000F, 8.5500F, 1.0000F), PartPose.offsetAndRotation(-9.5200F, 6.4900F, 7.7600F, -0.2443F, -0.4072F, 0.5004F));
        part.addOrReplaceChild("rc_390", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.3000F, -0.6500F, 2.6000F, 4.6000F, 1.3000F), PartPose.offsetAndRotation(-9.5200F, 3.2000F, 6.8600F, -0.2094F, -0.4072F, 0.5004F));
        part.addOrReplaceChild("rc_391", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.6500F, -0.5000F, 1.4000F, 3.2900F, 1.0000F), PartPose.offsetAndRotation(-9.5200F, 3.6400F, 6.4600F, -0.2094F, -0.4072F, 0.5004F));
        part.addOrReplaceChild("rc_392", CubeListBuilder.create().texOffs(469, 38).addBox(-1.1000F, -5.7400F, -0.6000F, 2.2000F, 11.4800F, 1.2000F), PartPose.offsetAndRotation(-12.2800F, 6.2700F, 10.2500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_393", CubeListBuilder.create().texOffs(119, 38).addBox(-0.5500F, -4.4900F, -0.5000F, 1.1000F, 8.9600F, 1.0000F), PartPose.offsetAndRotation(-12.2800F, 7.0800F, 9.7500F, -0.2443F, -0.4538F, 0.6283F));
        part.addOrReplaceChild("rc_394", CubeListBuilder.create().texOffs(469, 38).addBox(-1.1000F, -5.4500F, -0.6000F, 2.2000F, 10.8900F, 1.2000F), PartPose.offsetAndRotation(-15.0400F, 6.8900F, 11.4300F, -0.2443F, -0.5004F, 0.7563F));
        part.addOrReplaceChild("rc_395", CubeListBuilder.create().texOffs(445, 38).addBox(-0.5500F, -4.2400F, -0.5000F, 1.1000F, 8.4900F, 1.0000F), PartPose.offsetAndRotation(-15.0400F, 7.6500F, 10.9300F, -0.2443F, -0.5004F, 0.7563F));
        part.addOrReplaceChild("rc_396", CubeListBuilder.create().texOffs(124, 38).addBox(-1.3000F, -2.2800F, -0.6500F, 2.6000F, 4.5700F, 1.3000F), PartPose.offsetAndRotation(-15.0400F, 4.3800F, 10.0300F, -0.2094F, -0.5004F, 0.7563F));
        part.addOrReplaceChild("rc_397", CubeListBuilder.create().texOffs(306, 38).addBox(-0.7000F, -1.6400F, -0.5000F, 1.4000F, 3.2700F, 1.0000F), PartPose.offsetAndRotation(-15.0400F, 4.8200F, 9.6300F, -0.2094F, -0.5004F, 0.7563F));
        part.addOrReplaceChild("rc_398", CubeListBuilder.create().texOffs(462, 38).addBox(-1.1000F, -4.6500F, -0.6000F, 2.2000F, 9.2900F, 1.2000F), PartPose.offsetAndRotation(-17.8000F, 7.6700F, 11.9200F, -0.2443F, -0.5468F, 0.8844F));
        part.addOrReplaceChild("rc_399", CubeListBuilder.create().texOffs(326, 21).addBox(-0.5500F, -3.6300F, -0.5000F, 1.1000F, 7.2500F, 1.0000F), PartPose.offsetAndRotation(-17.8000F, 8.3200F, 11.4200F, -0.2443F, -0.5468F, 0.8844F));
        part.addOrReplaceChild("rc_400", CubeListBuilder.create().texOffs(455, 38).addBox(-1.1000F, -3.5000F, -0.6000F, 2.2000F, 7.0000F, 1.2000F), PartPose.offsetAndRotation(-20.5600F, 8.7200F, 12.0000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_401", CubeListBuilder.create().texOffs(450, 38).addBox(-0.5500F, -2.7300F, -0.5000F, 1.1000F, 5.4600F, 1.0000F), PartPose.offsetAndRotation(-20.5600F, 9.2100F, 11.5000F, -0.2443F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_402", CubeListBuilder.create().texOffs(417, 38).addBox(-1.3000F, -1.4700F, -0.6500F, 2.6000F, 2.9400F, 1.3000F), PartPose.offsetAndRotation(-20.5600F, 7.1100F, 10.6000F, -0.2094F, -0.5934F, 1.0123F));
        part.addOrReplaceChild("rc_403", CubeListBuilder.create().texOffs(426, 38).addBox(-0.7000F, -1.0500F, -0.5000F, 1.4000F, 2.1000F, 1.0000F), PartPose.offsetAndRotation(-20.5600F, 7.3900F, 10.2000F, -0.2094F, -0.5934F, 1.0123F));
        return part;
    }
    private static PartDefinition build_wings_root(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("wings_root", cubes, PartPose.offset(0.0000F, -18.0000F, 6.0000F));
        build_wing_upper_left(part);
        build_wing_upper_right(part);
        build_wing_mid_left(part);
        build_wing_mid_right(part);
        build_wing_lower_left(part);
        build_wing_lower_right(part);
        return part;
    }
    private static PartDefinition build_pauldron_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("pauldron_left", cubes, PartPose.offset(0.0000F, -1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_404", CubeListBuilder.create().texOffs(476, 38).addBox(-3.7500F, -3.0000F, -4.2500F, 7.5000F, 6.0000F, 8.5000F), PartPose.offsetAndRotation(1.5000F, -0.5000F, 0.0000F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_405", CubeListBuilder.create().texOffs(63, 21).addBox(-3.2500F, -1.5000F, -3.7500F, 6.5000F, 3.0000F, 7.5000F), PartPose.offsetAndRotation(2.8000F, -3.0000F, 0.0000F, -0.0000F, -0.0000F, 0.3840F));
        part.addOrReplaceChild("rc_406", CubeListBuilder.create().texOffs(0, 59).addBox(-2.5000F, -1.0000F, -2.7500F, 5.0000F, 2.0000F, 5.5000F), PartPose.offsetAndRotation(3.8000F, -4.5000F, 0.0000F, -0.0000F, -0.0000F, 0.4538F));
        part.addOrReplaceChild("rc_407", CubeListBuilder.create().texOffs(212, 21).addBox(-2.2500F, -0.9000F, -1.1000F, 4.5000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(4.5000F, -3.0000F, 1.5000F, -0.2094F, 0.2443F, 0.5585F));
        part.addOrReplaceChild("rc_408", CubeListBuilder.create().texOffs(23, 59).addBox(-2.0000F, -0.9000F, -1.1000F, 4.0000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(6.5000F, -4.8000F, 2.1000F, -0.2094F, 0.2443F, 0.4189F));
        part.addOrReplaceChild("rc_409", CubeListBuilder.create().texOffs(212, 21).addBox(-1.7500F, -0.9000F, -1.1000F, 3.5000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(8.5000F, -6.6000F, 2.7000F, -0.2094F, 0.2443F, 0.2793F));
        part.addOrReplaceChild("rc_410", CubeListBuilder.create().texOffs(36, 59).addBox(-1.5000F, -0.9000F, -1.1000F, 3.0000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(10.5000F, -8.4000F, 3.3000F, -0.2094F, 0.2443F, 0.1396F));
        part.addOrReplaceChild("rc_411", CubeListBuilder.create().texOffs(47, 59).addBox(-2.5000F, -2.5000F, -0.9000F, 5.0000F, 5.0000F, 1.8000F), PartPose.offsetAndRotation(2.0000F, 0.5000F, -4.2000F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_412", CubeListBuilder.create().texOffs(47, 59).addBox(-2.5000F, -2.5000F, -0.9000F, 5.0000F, 5.0000F, 1.8000F), PartPose.offsetAndRotation(2.0000F, 0.5000F, 4.2000F, -0.0000F, -0.0000F, 0.2793F));
        return part;
    }
    private static PartDefinition build_omni_aegis(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(215, 59).addBox(-1.3000F, -2.0000F, -2.0000F, 2.6000F, 4.0000F, 4.0000F);
        cubes.texOffs(230, 59).addBox(-0.9000F, -1.1000F, 4.4000F, 1.8000F, 2.2000F, 10.2000F);
        cubes.texOffs(255, 59).addBox(0.0000F, -0.7000F, 5.2500F, 1.2000F, 1.4000F, 8.5000F);
        cubes.texOffs(274, 59).addBox(-0.7000F, -1.9000F, 11.2000F, 1.4000F, 3.8000F, 2.0000F);
        PartDefinition part = parent.addOrReplaceChild("omni_aegis", cubes, PartPose.offset(6.0000F, 5.0000F, -6.0000F));
        part.addOrReplaceChild("rc_413", CubeListBuilder.create().texOffs(198, 59).addBox(-1.0000F, -3.0000F, -3.0000F, 2.0000F, 6.0000F, 6.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, 0.0000F, -0.7854F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_414", CubeListBuilder.create().texOffs(281, 59).addBox(-0.9000F, -1.1000F, -5.1000F, 1.8000F, 2.2000F, 10.2000F), PartPose.offsetAndRotation(0.0000F, -8.2300F, 4.7500F, -1.0472F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_415", CubeListBuilder.create().texOffs(306, 59).addBox(-0.6000F, -0.7000F, -4.2500F, 1.2000F, 1.4000F, 8.5000F), PartPose.offsetAndRotation(0.6000F, -8.2300F, 4.7500F, -1.0472F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_416", CubeListBuilder.create().texOffs(274, 59).addBox(-0.7000F, -1.9000F, -1.0000F, 1.4000F, 3.8000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -10.5700F, 6.1000F, -1.0472F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_417", CubeListBuilder.create().texOffs(230, 59).addBox(-0.9000F, -1.1000F, -5.1000F, 1.8000F, 2.2000F, 10.2000F), PartPose.offsetAndRotation(0.0000F, -8.2300F, -4.7500F, -2.0944F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_418", CubeListBuilder.create().texOffs(325, 59).addBox(-0.6000F, -0.7000F, -4.2500F, 1.2000F, 1.4000F, 8.5000F), PartPose.offsetAndRotation(0.6000F, -8.2300F, -4.7500F, -2.0944F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_419", CubeListBuilder.create().texOffs(274, 59).addBox(-0.7000F, -1.9000F, -1.0000F, 1.4000F, 3.8000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -10.5700F, -6.1000F, -2.0944F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_420", CubeListBuilder.create().texOffs(281, 59).addBox(-0.9000F, -1.1000F, -5.1000F, 1.8000F, 2.2000F, 10.2000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, -9.5000F, -3.1416F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_421", CubeListBuilder.create().texOffs(344, 59).addBox(-0.6000F, -0.7000F, -4.2500F, 1.2000F, 1.4000F, 8.5000F), PartPose.offsetAndRotation(0.6000F, -0.0000F, -9.5000F, -3.1416F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_422", CubeListBuilder.create().texOffs(274, 59).addBox(-0.7000F, -1.9000F, -1.0000F, 1.4000F, 3.8000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, -12.2000F, -3.1416F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_423", CubeListBuilder.create().texOffs(230, 59).addBox(-0.9000F, -1.1000F, -5.1000F, 1.8000F, 2.2000F, 10.2000F), PartPose.offsetAndRotation(0.0000F, 8.2300F, -4.7500F, -4.1888F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_424", CubeListBuilder.create().texOffs(363, 59).addBox(-0.6000F, -0.7000F, -4.2500F, 1.2000F, 1.4000F, 8.5000F), PartPose.offsetAndRotation(0.6000F, 8.2300F, -4.7500F, -4.1888F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_425", CubeListBuilder.create().texOffs(274, 59).addBox(-0.7000F, -1.9000F, -1.0000F, 1.4000F, 3.8000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, 10.5700F, -6.1000F, -4.1888F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_426", CubeListBuilder.create().texOffs(281, 59).addBox(-0.9000F, -1.1000F, -5.1000F, 1.8000F, 2.2000F, 10.2000F), PartPose.offsetAndRotation(0.0000F, 8.2300F, 4.7500F, -5.2360F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_427", CubeListBuilder.create().texOffs(382, 59).addBox(-0.6000F, -0.7000F, -4.2500F, 1.2000F, 1.4000F, 8.5000F), PartPose.offsetAndRotation(0.6000F, 8.2300F, 4.7500F, -5.2360F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_428", CubeListBuilder.create().texOffs(274, 59).addBox(-0.7000F, -1.9000F, -1.0000F, 1.4000F, 3.8000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, 10.5700F, 6.1000F, -5.2360F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_forearm_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(120, 59).addBox(-2.4000F, -0.2500F, -2.9000F, 4.8000F, 11.5000F, 4.8000F);
        cubes.texOffs(141, 59).addBox(0.6000F, 0.2500F, -2.5000F, 2.0000F, 9.5000F, 4.0000F);
        cubes.texOffs(154, 59).addBox(1.8000F, 1.2500F, -1.9000F, 1.2000F, 7.5000F, 1.8000F);
        cubes.texOffs(172, 59).addBox(-2.7000F, 9.4000F, -3.2000F, 5.4000F, 2.2000F, 5.4000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, -2.2000F, 1.0000F, 3.0000F, 1.0000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, -1.0000F, 1.0000F, 3.0000F, 1.0000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, 0.2000F, 1.0000F, 3.0000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("forearm_left", cubes, PartPose.offset(3.0000F, 12.0000F, -1.0000F));
        part.addOrReplaceChild("rc_429", CubeListBuilder.create().texOffs(161, 59).addBox(-1.0000F, -2.7500F, -1.5000F, 2.0000F, 5.5000F, 3.0000F), PartPose.offsetAndRotation(1.2000F, -0.0000F, 3.2000F, -0.4189F, -0.0000F, 0.0000F));
        build_omni_aegis(part);
        return part;
    }
    private static PartDefinition build_arm_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(62, 59).addBox(-0.6000F, -0.2500F, -2.6000F, 4.2000F, 11.5000F, 4.2000F);
        cubes.texOffs(79, 59).addBox(2.0000F, 0.5000F, -2.3000F, 2.0000F, 9.0000F, 3.6000F);
        cubes.texOffs(92, 59).addBox(3.2000F, 1.5000F, -2.1000F, 1.2000F, 7.0000F, 1.8000F);
        cubes.texOffs(99, 59).addBox(-1.0000F, 9.5000F, -3.0000F, 5.0000F, 2.0000F, 5.0000F);
        PartDefinition part = parent.addOrReplaceChild("arm_left", cubes, PartPose.offset(11.0000F, -19.0000F, 0.0000F));
        build_pauldron_left(part);
        build_forearm_left(part);
        return part;
    }
    private static PartDefinition build_pauldron_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("pauldron_right", cubes, PartPose.offset(0.0000F, -1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_430", CubeListBuilder.create().texOffs(476, 38).addBox(-3.7500F, -3.0000F, -4.2500F, 7.5000F, 6.0000F, 8.5000F), PartPose.offsetAndRotation(-1.5000F, -0.5000F, 0.0000F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_431", CubeListBuilder.create().texOffs(63, 21).addBox(-3.2500F, -1.5000F, -3.7500F, 6.5000F, 3.0000F, 7.5000F), PartPose.offsetAndRotation(-2.8000F, -3.0000F, 0.0000F, -0.0000F, -0.0000F, -0.3840F));
        part.addOrReplaceChild("rc_432", CubeListBuilder.create().texOffs(0, 59).addBox(-2.5000F, -1.0000F, -2.7500F, 5.0000F, 2.0000F, 5.5000F), PartPose.offsetAndRotation(-3.8000F, -4.5000F, 0.0000F, -0.0000F, -0.0000F, -0.4538F));
        part.addOrReplaceChild("rc_433", CubeListBuilder.create().texOffs(212, 21).addBox(-2.2500F, -0.9000F, -1.1000F, 4.5000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(-4.5000F, -3.0000F, 1.5000F, -0.2094F, -0.2443F, -0.5585F));
        part.addOrReplaceChild("rc_434", CubeListBuilder.create().texOffs(23, 59).addBox(-2.0000F, -0.9000F, -1.1000F, 4.0000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(-6.5000F, -4.8000F, 2.1000F, -0.2094F, -0.2443F, -0.4189F));
        part.addOrReplaceChild("rc_435", CubeListBuilder.create().texOffs(212, 21).addBox(-1.7500F, -0.9000F, -1.1000F, 3.5000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(-8.5000F, -6.6000F, 2.7000F, -0.2094F, -0.2443F, -0.2793F));
        part.addOrReplaceChild("rc_436", CubeListBuilder.create().texOffs(36, 59).addBox(-1.5000F, -0.9000F, -1.1000F, 3.0000F, 1.8000F, 2.2000F), PartPose.offsetAndRotation(-10.5000F, -8.4000F, 3.3000F, -0.2094F, -0.2443F, -0.1396F));
        part.addOrReplaceChild("rc_437", CubeListBuilder.create().texOffs(47, 59).addBox(-2.5000F, -2.5000F, -0.9000F, 5.0000F, 5.0000F, 1.8000F), PartPose.offsetAndRotation(-2.0000F, 0.5000F, -4.2000F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_438", CubeListBuilder.create().texOffs(47, 59).addBox(-2.5000F, -2.5000F, -0.9000F, 5.0000F, 5.0000F, 1.8000F), PartPose.offsetAndRotation(-2.0000F, 0.5000F, 4.2000F, -0.0000F, -0.0000F, -0.2793F));
        return part;
    }
    private static PartDefinition build_omni_greatsword(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(401, 59).addBox(-1.0000F, 0.3000F, -1.1000F, 2.0000F, 2.4000F, 2.2000F);
        cubes.texOffs(410, 59).addBox(-1.0000F, 2.5000F, -1.1000F, 2.0000F, 2.4000F, 2.2000F);
        cubes.texOffs(401, 59).addBox(-1.0000F, 4.7000F, -1.1000F, 2.0000F, 2.4000F, 2.2000F);
        cubes.texOffs(410, 59).addBox(-1.0000F, 6.9000F, -1.1000F, 2.0000F, 2.4000F, 2.2000F);
        cubes.texOffs(401, 59).addBox(-1.0000F, 9.1000F, -1.1000F, 2.0000F, 2.4000F, 2.2000F);
        cubes.texOffs(436, 59).addBox(-1.1000F, 10.4000F, -1.1000F, 2.2000F, 5.2000F, 2.2000F);
        cubes.texOffs(445, 59).addBox(-2.1000F, -3.3000F, -6.0000F, 4.2000F, 3.6000F, 12.0000F);
        cubes.texOffs(11, 77).addBox(-0.7000F, -6.4000F, -3.1000F, 1.4000F, 2.8000F, 6.2000F);
        cubes.texOffs(26, 77).addBox(-1.1000F, -6.3000F, -1.3000F, 2.2000F, 2.6000F, 2.6000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -6.3000F, 2.6200F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -6.3000F, -3.8200F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(11, 77).addBox(-0.7000F, -8.9000F, -2.9400F, 1.4000F, 2.8000F, 5.8800F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -8.8000F, -1.2300F, 2.2000F, 2.6000F, 2.4700F);
        cubes.texOffs(11, 77).addBox(-0.7000F, -11.4000F, -2.7800F, 1.4000F, 2.8000F, 5.5600F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -11.3000F, -1.1700F, 2.2000F, 2.6000F, 2.3300F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -11.3000F, 2.2900F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -11.3000F, -3.4900F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(46, 77).addBox(-0.7000F, -13.9000F, -2.6200F, 1.4000F, 2.8000F, 5.2300F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -13.8000F, -1.1000F, 2.2000F, 2.6000F, 2.2000F);
        cubes.texOffs(46, 77).addBox(-0.7000F, -16.4000F, -2.4600F, 1.4000F, 2.8000F, 4.9100F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -16.3000F, -1.0300F, 2.2000F, 2.6000F, 2.0600F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -16.3000F, 1.9500F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -16.3000F, -3.1500F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(46, 77).addBox(-0.7000F, -18.9000F, -2.2900F, 1.4000F, 2.8000F, 4.5900F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -18.8000F, -0.9600F, 2.2000F, 2.6000F, 1.9300F);
        cubes.texOffs(59, 77).addBox(-0.7000F, -21.4000F, -2.1300F, 1.4000F, 2.8000F, 4.2700F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -21.3000F, -0.9000F, 2.2000F, 2.6000F, 1.7900F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -21.3000F, 1.6200F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -21.3000F, -2.8200F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(59, 77).addBox(-0.7000F, -23.9000F, -1.9700F, 1.4000F, 2.8000F, 3.9400F);
        cubes.texOffs(37, 77).addBox(-1.1000F, -23.8000F, -0.8300F, 2.2000F, 2.6000F, 1.6600F);
        cubes.texOffs(59, 77).addBox(-0.7000F, -26.4000F, -1.8100F, 1.4000F, 2.8000F, 3.6200F);
        cubes.texOffs(70, 77).addBox(-1.1000F, -26.3000F, -0.7600F, 2.2000F, 2.6000F, 1.5200F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -26.3000F, 1.2800F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -26.3000F, -2.4800F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(79, 77).addBox(-0.7000F, -28.9000F, -1.6500F, 1.4000F, 2.8000F, 3.3000F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -28.8000F, -0.6900F, 2.2000F, 2.6000F, 1.3900F);
        cubes.texOffs(79, 77).addBox(-0.7000F, -31.4000F, -1.4900F, 1.4000F, 2.8000F, 2.9800F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -31.3000F, -0.6200F, 2.2000F, 2.6000F, 1.2500F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -31.3000F, 0.9500F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(9, 21).addBox(-0.5000F, -31.3000F, -2.1500F, 1.0000F, 2.6000F, 1.2000F);
        cubes.texOffs(79, 77).addBox(-0.7000F, -33.9000F, -1.3300F, 1.4000F, 2.8000F, 2.6500F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -33.8000F, -0.5600F, 2.2000F, 2.6000F, 1.1100F);
        cubes.texOffs(95, 77).addBox(-0.7000F, -36.4000F, -1.1700F, 1.4000F, 2.8000F, 2.3300F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -36.3000F, -0.5000F, 2.2000F, 2.6000F, 1.0000F);
        cubes.texOffs(102, 77).addBox(-0.7000F, -38.9000F, -1.0000F, 1.4000F, 2.8000F, 2.0100F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -38.8000F, -0.5000F, 2.2000F, 2.6000F, 1.0000F);
        cubes.texOffs(102, 77).addBox(-0.7000F, -41.4000F, -0.8400F, 1.4000F, 2.8000F, 1.6900F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -41.3000F, -0.5000F, 2.2000F, 2.6000F, 1.0000F);
        cubes.texOffs(256, 21).addBox(-0.7000F, -43.9000F, -0.6800F, 1.4000F, 2.8000F, 1.3600F);
        cubes.texOffs(88, 77).addBox(-1.1000F, -43.8000F, -0.5000F, 2.2000F, 2.6000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("omni_greatsword", cubes, PartPose.offsetAndRotation(0.0000F, 11.0000F, -3.0000F, 1.1868F, -0.2443F, -0.3840F));
        part.addOrReplaceChild("rc_439", CubeListBuilder.create().texOffs(419, 59).addBox(-1.8000F, -1.8000F, -1.8000F, 3.6000F, 3.6000F, 3.6000F), PartPose.offsetAndRotation(0.0000F, 13.0000F, 0.0000F, -0.7854F, -0.0000F, 0.7854F));
        part.addOrReplaceChild("rc_440", CubeListBuilder.create().texOffs(478, 59).addBox(-2.4000F, -2.2500F, -2.2500F, 4.8000F, 4.5000F, 4.5000F), PartPose.offsetAndRotation(0.0000F, -1.5000F, 0.0000F, -0.7854F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_441", CubeListBuilder.create().texOffs(176, 21).addBox(-1.4000F, -1.2000F, -1.5000F, 2.8000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -2.5000F, 5.0000F, -0.4189F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_442", CubeListBuilder.create().texOffs(497, 59).addBox(-1.2000F, -1.2000F, -1.5000F, 2.4000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -3.9000F, 7.2000F, -0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_443", CubeListBuilder.create().texOffs(0, 77).addBox(-1.0000F, -1.2000F, -1.5000F, 2.0000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -5.3000F, 9.4000F, -0.6981F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_444", CubeListBuilder.create().texOffs(497, 59).addBox(-0.8000F, -1.2000F, -1.5000F, 1.6000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -6.7000F, 11.6000F, -0.8378F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_445", CubeListBuilder.create().texOffs(176, 21).addBox(-1.4000F, -1.2000F, -1.5000F, 2.8000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -2.5000F, -5.0000F, 0.4189F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_446", CubeListBuilder.create().texOffs(497, 59).addBox(-1.2000F, -1.2000F, -1.5000F, 2.4000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -3.9000F, -7.2000F, 0.5585F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_447", CubeListBuilder.create().texOffs(0, 77).addBox(-1.0000F, -1.2000F, -1.5000F, 2.0000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -5.3000F, -9.4000F, 0.6981F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_448", CubeListBuilder.create().texOffs(497, 59).addBox(-0.8000F, -1.2000F, -1.5000F, 1.6000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -6.7000F, -11.6000F, 0.8378F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_forearm_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(120, 59).addBox(-2.4000F, -0.2500F, -2.9000F, 4.8000F, 11.5000F, 4.8000F);
        cubes.texOffs(141, 59).addBox(-2.6000F, 0.2500F, -2.5000F, 2.0000F, 9.5000F, 4.0000F);
        cubes.texOffs(154, 59).addBox(-3.0000F, 1.2500F, -1.9000F, 1.2000F, 7.5000F, 1.8000F);
        cubes.texOffs(172, 59).addBox(-2.7000F, 9.4000F, -3.2000F, 5.4000F, 2.2000F, 5.4000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, -2.2000F, 1.0000F, 3.0000F, 1.0000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, -1.0000F, 1.0000F, 3.0000F, 1.0000F);
        cubes.texOffs(193, 59).addBox(-0.5000F, 12.0000F, 0.2000F, 1.0000F, 3.0000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("forearm_right", cubes, PartPose.offset(-3.0000F, 12.0000F, -1.0000F));
        part.addOrReplaceChild("rc_449", CubeListBuilder.create().texOffs(161, 59).addBox(-1.0000F, -2.7500F, -1.5000F, 2.0000F, 5.5000F, 3.0000F), PartPose.offsetAndRotation(-1.2000F, -0.0000F, 3.2000F, -0.4189F, -0.0000F, 0.0000F));
        build_omni_greatsword(part);
        return part;
    }
    private static PartDefinition build_arm_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(62, 59).addBox(-3.6000F, -0.2500F, -2.6000F, 4.2000F, 11.5000F, 4.2000F);
        cubes.texOffs(79, 59).addBox(-4.0000F, 0.5000F, -2.3000F, 2.0000F, 9.0000F, 3.6000F);
        cubes.texOffs(92, 59).addBox(-4.4000F, 1.5000F, -2.1000F, 1.2000F, 7.0000F, 1.8000F);
        cubes.texOffs(99, 59).addBox(-4.0000F, 9.5000F, -3.0000F, 5.0000F, 2.0000F, 5.0000F);
        PartDefinition part = parent.addOrReplaceChild("arm_right", cubes, PartPose.offset(-11.0000F, -19.0000F, 0.0000F));
        build_pauldron_right(part);
        build_forearm_right(part);
        return part;
    }
    private static PartDefinition build_torso(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(0, 0).addBox(-5.0000F, -10.0000F, -3.5000F, 10.0000F, 10.0000F, 7.0000F);
        cubes.texOffs(35, 0).addBox(-7.0000F, -21.0000F, -3.0000F, 14.0000F, 12.0000F, 8.0000F);
        cubes.texOffs(80, 0).addBox(-9.0000F, -22.5000F, -4.5000F, 18.0000F, 5.0000F, 9.0000F);
        cubes.texOffs(135, 0).addBox(-7.0000F, -22.5000F, -5.5000F, 14.0000F, 2.0000F, 2.0000F);
        cubes.texOffs(168, 0).addBox(-4.0000F, -3.5000F, -5.2000F, 8.0000F, 3.0000F, 2.0000F);
        cubes.texOffs(189, 0).addBox(-2.0000F, -4.0000F, -5.8000F, 4.0000F, 4.0000F, 2.0000F);
        cubes.texOffs(202, 0).addBox(-1.0000F, -3.0000F, -5.7000F, 2.0000F, 2.0000F, 1.0000F);
        cubes.texOffs(202, 0).addBox(-1.0000F, -4.7500F, -4.9000F, 2.0000F, 1.5000F, 1.0000F);
        cubes.texOffs(202, 0).addBox(-1.0000F, -7.2500F, -4.9000F, 2.0000F, 1.5000F, 1.0000F);
        cubes.texOffs(202, 0).addBox(-1.0000F, -9.7500F, -4.9000F, 2.0000F, 1.5000F, 1.0000F);
        PartDefinition part = parent.addOrReplaceChild("torso", cubes, PartPose.offset(0.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_450", CubeListBuilder.create().texOffs(209, 0).addBox(-3.5000F, -1.0000F, -0.7500F, 7.0000F, 2.0000F, 1.5000F), PartPose.offsetAndRotation(0.0000F, -4.0000F, -3.8000F, 0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_451", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(4.7000F, -4.0000F, -1.0000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("rc_452", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(-4.7000F, -4.0000F, -1.0000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("rc_453", CubeListBuilder.create().texOffs(245, 0).addBox(-4.0000F, -1.0000F, -0.7500F, 8.0000F, 2.0000F, 1.5000F), PartPose.offsetAndRotation(0.0000F, -6.5000F, -3.8000F, 0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_454", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(5.2000F, -6.5000F, -1.0000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("rc_455", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(-5.2000F, -6.5000F, -1.0000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("rc_456", CubeListBuilder.create().texOffs(266, 0).addBox(-4.5000F, -1.0000F, -0.7500F, 9.0000F, 2.0000F, 1.5000F), PartPose.offsetAndRotation(0.0000F, -9.0000F, -3.8000F, 0.1396F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_457", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(5.7000F, -9.0000F, -1.0000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("rc_458", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0000F, -1.0000F, -3.0000F, 2.0000F, 2.0000F, 6.0000F), PartPose.offsetAndRotation(-5.7000F, -9.0000F, -1.0000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("rc_459", CubeListBuilder.create().texOffs(289, 0).addBox(-3.0000F, -4.0000F, -1.2500F, 6.0000F, 8.0000F, 2.5000F), PartPose.offsetAndRotation(6.2000F, -15.5000F, -4.8000F, 0.1745F, 0.3142F, 0.1047F));
        part.addOrReplaceChild("rc_460", CubeListBuilder.create().texOffs(306, 0).addBox(-2.2500F, -2.7500F, -0.7500F, 4.5000F, 5.5000F, 1.5000F), PartPose.offsetAndRotation(6.8000F, -16.5000F, -5.8000F, 0.1745F, 0.3142F, 0.1047F));
        part.addOrReplaceChild("rc_461", CubeListBuilder.create().texOffs(319, 0).addBox(-2.5000F, -0.7500F, -0.7500F, 5.0000F, 1.5000F, 1.5000F), PartPose.offsetAndRotation(5.8000F, -18.5000F, -6.2000F, 0.1745F, 0.3142F, 0.2094F));
        part.addOrReplaceChild("rc_462", CubeListBuilder.create().texOffs(334, 0).addBox(-2.0000F, -0.7500F, -0.7500F, 4.0000F, 1.5000F, 1.5000F), PartPose.offsetAndRotation(7.2000F, -12.5000F, -5.6000F, 0.1396F, 0.3142F, -0.1396F));
        part.addOrReplaceChild("rc_463", CubeListBuilder.create().texOffs(347, 0).addBox(-1.5000F, -5.0000F, -4.0000F, 3.0000F, 10.0000F, 8.0000F), PartPose.offsetAndRotation(9.2000F, -16.0000F, 0.0000F, -0.0000F, -0.0000F, -0.1396F));
        part.addOrReplaceChild("rc_464", CubeListBuilder.create().texOffs(370, 0).addBox(-1.0000F, -3.5000F, -1.5000F, 2.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(10.2000F, -17.0000F, -2.0000F, -0.0000F, -0.0000F, -0.1396F));
        part.addOrReplaceChild("rc_465", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(5.5000F, -9.0000F, -3.5000F, -0.0000F, 0.3840F, 0.2793F));
        part.addOrReplaceChild("rc_466", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(4.5000F, -11.0000F, 5.2000F, -0.2094F, -0.3142F, -0.1745F));
        part.addOrReplaceChild("rc_467", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(5.9000F, -11.2000F, -3.5000F, -0.0000F, 0.3840F, 0.2793F));
        part.addOrReplaceChild("rc_468", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(5.0000F, -13.4000F, 5.2000F, -0.2094F, -0.3142F, -0.1745F));
        part.addOrReplaceChild("rc_469", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(6.3000F, -13.4000F, -3.5000F, -0.0000F, 0.3840F, 0.2793F));
        part.addOrReplaceChild("rc_470", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(5.5000F, -15.8000F, 5.2000F, -0.2094F, -0.3142F, -0.1745F));
        part.addOrReplaceChild("rc_471", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(6.7000F, -15.6000F, -3.5000F, -0.0000F, 0.3840F, 0.2793F));
        part.addOrReplaceChild("rc_472", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(6.0000F, -18.2000F, 5.2000F, -0.2094F, -0.3142F, -0.1745F));
        part.addOrReplaceChild("rc_473", CubeListBuilder.create().texOffs(289, 0).addBox(-3.0000F, -4.0000F, -1.2500F, 6.0000F, 8.0000F, 2.5000F), PartPose.offsetAndRotation(-6.2000F, -15.5000F, -4.8000F, 0.1745F, -0.3142F, -0.1047F));
        part.addOrReplaceChild("rc_474", CubeListBuilder.create().texOffs(306, 0).addBox(-2.2500F, -2.7500F, -0.7500F, 4.5000F, 5.5000F, 1.5000F), PartPose.offsetAndRotation(-6.8000F, -16.5000F, -5.8000F, 0.1745F, -0.3142F, -0.1047F));
        part.addOrReplaceChild("rc_475", CubeListBuilder.create().texOffs(319, 0).addBox(-2.5000F, -0.7500F, -0.7500F, 5.0000F, 1.5000F, 1.5000F), PartPose.offsetAndRotation(-5.8000F, -18.5000F, -6.2000F, 0.1745F, -0.3142F, -0.2094F));
        part.addOrReplaceChild("rc_476", CubeListBuilder.create().texOffs(334, 0).addBox(-2.0000F, -0.7500F, -0.7500F, 4.0000F, 1.5000F, 1.5000F), PartPose.offsetAndRotation(-7.2000F, -12.5000F, -5.6000F, 0.1396F, -0.3142F, 0.1396F));
        part.addOrReplaceChild("rc_477", CubeListBuilder.create().texOffs(347, 0).addBox(-1.5000F, -5.0000F, -4.0000F, 3.0000F, 10.0000F, 8.0000F), PartPose.offsetAndRotation(-9.2000F, -16.0000F, 0.0000F, -0.0000F, -0.0000F, 0.1396F));
        part.addOrReplaceChild("rc_478", CubeListBuilder.create().texOffs(370, 0).addBox(-1.0000F, -3.5000F, -1.5000F, 2.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(-10.2000F, -17.0000F, -2.0000F, -0.0000F, -0.0000F, 0.1396F));
        part.addOrReplaceChild("rc_479", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(-5.5000F, -9.0000F, -3.5000F, -0.0000F, -0.3840F, -0.2793F));
        part.addOrReplaceChild("rc_480", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(-4.5000F, -11.0000F, 5.2000F, -0.2094F, 0.3142F, 0.1745F));
        part.addOrReplaceChild("rc_481", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(-5.9000F, -11.2000F, -3.5000F, -0.0000F, -0.3840F, -0.2793F));
        part.addOrReplaceChild("rc_482", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(-5.0000F, -13.4000F, 5.2000F, -0.2094F, 0.3142F, 0.1745F));
        part.addOrReplaceChild("rc_483", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(-6.3000F, -13.4000F, -3.5000F, -0.0000F, -0.3840F, -0.2793F));
        part.addOrReplaceChild("rc_484", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(-5.5000F, -15.8000F, 5.2000F, -0.2094F, 0.3142F, 0.1745F));
        part.addOrReplaceChild("rc_485", CubeListBuilder.create().texOffs(381, 0).addBox(-1.5000F, -0.7500F, -1.0000F, 3.0000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(-6.7000F, -15.6000F, -3.5000F, -0.0000F, -0.3840F, -0.2793F));
        part.addOrReplaceChild("rc_486", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.0000F, -1.2500F, 3.0000F, 2.0000F, 2.5000F), PartPose.offsetAndRotation(-6.0000F, -18.2000F, 5.2000F, -0.2094F, 0.3142F, 0.1745F));
        part.addOrReplaceChild("rc_487", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(-7.2400F, -24.5000F, 2.9200F, -0.3142F, -1.3090F, 0.0000F));
        part.addOrReplaceChild("rc_488", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(-7.2400F, -27.5000F, 3.7200F, -0.3840F, -1.3090F, 0.0000F));
        part.addOrReplaceChild("rc_489", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(-5.7500F, -24.5000F, 5.0400F, -0.3142F, -0.8727F, 0.0000F));
        part.addOrReplaceChild("rc_490", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(-5.7500F, -27.5000F, 5.8400F, -0.3840F, -0.8727F, 0.0000F));
        part.addOrReplaceChild("rc_491", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(-3.1700F, -24.5000F, 6.4800F, -0.3142F, -0.4363F, 0.0000F));
        part.addOrReplaceChild("rc_492", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(-3.1700F, -27.5000F, 7.2800F, -0.3840F, -0.4363F, 0.0000F));
        part.addOrReplaceChild("rc_493", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(0.0000F, -24.5000F, 7.0000F, -0.3142F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_494", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(0.0000F, -27.5000F, 7.8000F, -0.3840F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_495", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(3.1700F, -24.5000F, 6.4800F, -0.3142F, 0.4363F, 0.0000F));
        part.addOrReplaceChild("rc_496", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(3.1700F, -27.5000F, 7.2800F, -0.3840F, 0.4363F, 0.0000F));
        part.addOrReplaceChild("rc_497", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(5.7500F, -24.5000F, 5.0400F, -0.3142F, 0.8727F, 0.0000F));
        part.addOrReplaceChild("rc_498", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(5.7500F, -27.5000F, 5.8400F, -0.3840F, 0.8727F, 0.0000F));
        part.addOrReplaceChild("rc_499", CubeListBuilder.create().texOffs(403, 0).addBox(-1.7500F, -3.5000F, -0.9000F, 3.5000F, 7.0000F, 1.8000F), PartPose.offsetAndRotation(7.2400F, -24.5000F, 2.9200F, -0.3142F, 1.3090F, 0.0000F));
        part.addOrReplaceChild("rc_500", CubeListBuilder.create().texOffs(416, 0).addBox(-1.5000F, -1.2500F, -0.7500F, 3.0000F, 2.5000F, 1.5000F), PartPose.offsetAndRotation(7.2400F, -27.5000F, 3.7200F, -0.3840F, 1.3090F, 0.0000F));
        part.addOrReplaceChild("rc_501", CubeListBuilder.create().texOffs(334, 0).addBox(-2.0000F, -1.1000F, -1.2500F, 4.0000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -6.0000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_502", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -6.0000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_503", CubeListBuilder.create().texOffs(436, 0).addBox(-1.9000F, -1.1000F, -1.2500F, 3.8000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -8.8000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_504", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -8.8000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_505", CubeListBuilder.create().texOffs(334, 0).addBox(-1.8000F, -1.1000F, -1.2500F, 3.6000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -11.6000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_506", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -11.6000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_507", CubeListBuilder.create().texOffs(392, 0).addBox(-1.7000F, -1.1000F, -1.2500F, 3.4000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -14.4000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_508", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -14.4000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_509", CubeListBuilder.create().texOffs(381, 0).addBox(-1.6000F, -1.1000F, -1.2500F, 3.2000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -17.2000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_510", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -17.2000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_511", CubeListBuilder.create().texOffs(392, 0).addBox(-1.5000F, -1.1000F, -1.2500F, 3.0000F, 2.2000F, 2.5000F), PartPose.offsetAndRotation(0.0000F, -20.0000F, 5.5000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_512", CubeListBuilder.create().texOffs(427, 0).addBox(-0.7500F, -0.7500F, -1.0000F, 1.5000F, 1.5000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -20.0000F, 6.8000F, -0.2094F, -0.0000F, 0.0000F));
        build_omni_core(part);
        build_head(part);
        build_halo_outer(part);
        build_halo_inner(part);
        build_cape_tail_1(part);
        build_wings_root(part);
        build_arm_left(part);
        build_arm_right(part);
        return part;
    }
    private static PartDefinition build_tasset_front(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tasset_front", cubes, PartPose.offset(0.0000F, -0.0000F, -5.0000F));
        part.addOrReplaceChild("rc_513", CubeListBuilder.create().texOffs(109, 77).addBox(-3.5000F, -1.7000F, -0.7000F, 7.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 2.0000F, -0.2000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_514", CubeListBuilder.create().texOffs(126, 77).addBox(-2.5000F, -1.2000F, -0.5000F, 5.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 2.0000F, -0.8000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_515", CubeListBuilder.create().texOffs(177, 77).addBox(-3.0000F, -1.7000F, -0.7000F, 6.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 5.2000F, -0.6000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_516", CubeListBuilder.create().texOffs(192, 77).addBox(-2.0000F, -1.2000F, -0.5000F, 4.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 5.2000F, -1.2000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_517", CubeListBuilder.create().texOffs(237, 77).addBox(-2.5000F, -1.7000F, -0.7000F, 5.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 8.4000F, -1.0000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_518", CubeListBuilder.create().texOffs(331, 21).addBox(-1.5000F, -1.2000F, -0.5000F, 3.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 8.4000F, -1.6000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_519", CubeListBuilder.create().texOffs(250, 77).addBox(-2.0000F, -1.7000F, -0.7000F, 4.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 11.6000F, -1.4000F, 0.1745F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_520", CubeListBuilder.create().texOffs(261, 77).addBox(-1.0000F, -1.2000F, -0.5000F, 2.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 11.6000F, -2.0000F, 0.1745F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_tasset_back(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tasset_back", cubes, PartPose.offset(0.0000F, -0.0000F, 5.0000F));
        part.addOrReplaceChild("rc_521", CubeListBuilder.create().texOffs(139, 77).addBox(-4.5000F, -1.7000F, -0.7000F, 9.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 2.0000F, 0.2000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_522", CubeListBuilder.create().texOffs(160, 77).addBox(-3.5000F, -1.2000F, -0.5000F, 7.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 2.0000F, 0.8000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_523", CubeListBuilder.create().texOffs(203, 77).addBox(-4.0000F, -1.7000F, -0.7000F, 8.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 5.2000F, 0.6000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_524", CubeListBuilder.create().texOffs(222, 77).addBox(-3.0000F, -1.2000F, -0.5000F, 6.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 5.2000F, 1.2000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_525", CubeListBuilder.create().texOffs(109, 77).addBox(-3.5000F, -1.7000F, -0.7000F, 7.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 8.4000F, 1.0000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_526", CubeListBuilder.create().texOffs(126, 77).addBox(-2.5000F, -1.2000F, -0.5000F, 5.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 8.4000F, 1.6000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_527", CubeListBuilder.create().texOffs(268, 77).addBox(-3.0000F, -1.7000F, -0.7000F, 6.0000F, 3.4000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 11.6000F, 1.4000F, -0.2094F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_528", CubeListBuilder.create().texOffs(283, 77).addBox(-2.0000F, -1.2000F, -0.5000F, 4.0000F, 2.4000F, 1.0000F), PartPose.offsetAndRotation(0.0000F, 11.6000F, 2.0000F, -0.2094F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_tasset_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tasset_left", cubes, PartPose.offset(7.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_529", CubeListBuilder.create().texOffs(294, 77).addBox(-0.9000F, -1.7000F, -4.0000F, 1.8000F, 3.4000F, 8.0000F), PartPose.offsetAndRotation(0.4000F, 1.5000F, 0.0000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("rc_530", CubeListBuilder.create().texOffs(315, 77).addBox(-0.5000F, -1.2000F, -3.0000F, 1.0000F, 2.4000F, 6.0000F), PartPose.offsetAndRotation(1.2000F, 1.5000F, -0.5000F, -0.0000F, -0.0000F, -0.2094F));
        part.addOrReplaceChild("rc_531", CubeListBuilder.create().texOffs(330, 77).addBox(-0.9000F, -1.7000F, -3.5000F, 1.8000F, 3.4000F, 7.0000F), PartPose.offsetAndRotation(1.0000F, 4.7000F, 0.0000F, -0.0000F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("rc_532", CubeListBuilder.create().texOffs(349, 77).addBox(-0.5000F, -1.2000F, -2.5000F, 1.0000F, 2.4000F, 5.0000F), PartPose.offsetAndRotation(1.8000F, 4.7000F, -0.5000F, -0.0000F, -0.0000F, -0.2443F));
        part.addOrReplaceChild("rc_533", CubeListBuilder.create().texOffs(362, 77).addBox(-0.9000F, -1.7000F, -3.0000F, 1.8000F, 3.4000F, 6.0000F), PartPose.offsetAndRotation(1.6000F, 7.9000F, 0.0000F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_534", CubeListBuilder.create().texOffs(379, 77).addBox(-0.5000F, -1.2000F, -2.0000F, 1.0000F, 2.4000F, 4.0000F), PartPose.offsetAndRotation(2.4000F, 7.9000F, -0.5000F, -0.0000F, -0.0000F, -0.2793F));
        part.addOrReplaceChild("rc_535", CubeListBuilder.create().texOffs(390, 77).addBox(-0.9000F, -1.7000F, -2.5000F, 1.8000F, 3.4000F, 5.0000F), PartPose.offsetAndRotation(2.2000F, 11.1000F, 0.0000F, -0.0000F, -0.0000F, -0.3142F));
        part.addOrReplaceChild("rc_536", CubeListBuilder.create().texOffs(405, 77).addBox(-0.5000F, -1.2000F, -1.5000F, 1.0000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(3.0000F, 11.1000F, -0.5000F, -0.0000F, -0.0000F, -0.3142F));
        return part;
    }
    private static PartDefinition build_tasset_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tasset_right", cubes, PartPose.offset(-7.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_537", CubeListBuilder.create().texOffs(294, 77).addBox(-0.9000F, -1.7000F, -4.0000F, 1.8000F, 3.4000F, 8.0000F), PartPose.offsetAndRotation(-0.4000F, 1.5000F, 0.0000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("rc_538", CubeListBuilder.create().texOffs(315, 77).addBox(-0.5000F, -1.2000F, -3.0000F, 1.0000F, 2.4000F, 6.0000F), PartPose.offsetAndRotation(-1.2000F, 1.5000F, -0.5000F, -0.0000F, -0.0000F, 0.2094F));
        part.addOrReplaceChild("rc_539", CubeListBuilder.create().texOffs(330, 77).addBox(-0.9000F, -1.7000F, -3.5000F, 1.8000F, 3.4000F, 7.0000F), PartPose.offsetAndRotation(-1.0000F, 4.7000F, 0.0000F, -0.0000F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("rc_540", CubeListBuilder.create().texOffs(349, 77).addBox(-0.5000F, -1.2000F, -2.5000F, 1.0000F, 2.4000F, 5.0000F), PartPose.offsetAndRotation(-1.8000F, 4.7000F, -0.5000F, -0.0000F, -0.0000F, 0.2443F));
        part.addOrReplaceChild("rc_541", CubeListBuilder.create().texOffs(362, 77).addBox(-0.9000F, -1.7000F, -3.0000F, 1.8000F, 3.4000F, 6.0000F), PartPose.offsetAndRotation(-1.6000F, 7.9000F, 0.0000F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_542", CubeListBuilder.create().texOffs(379, 77).addBox(-0.5000F, -1.2000F, -2.0000F, 1.0000F, 2.4000F, 4.0000F), PartPose.offsetAndRotation(-2.4000F, 7.9000F, -0.5000F, -0.0000F, -0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_543", CubeListBuilder.create().texOffs(390, 77).addBox(-0.9000F, -1.7000F, -2.5000F, 1.8000F, 3.4000F, 5.0000F), PartPose.offsetAndRotation(-2.2000F, 11.1000F, 0.0000F, -0.0000F, -0.0000F, 0.3142F));
        part.addOrReplaceChild("rc_544", CubeListBuilder.create().texOffs(405, 77).addBox(-0.5000F, -1.2000F, -1.5000F, 1.0000F, 2.4000F, 3.0000F), PartPose.offsetAndRotation(-3.0000F, 11.1000F, -0.5000F, -0.0000F, -0.0000F, 0.3142F));
        return part;
    }
    private static PartDefinition build_waist_skirt(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("waist_skirt", cubes, PartPose.offset(0.0000F, -0.0000F, 0.0000F));
        build_tasset_front(part);
        build_tasset_back(part);
        build_tasset_left(part);
        build_tasset_right(part);
        return part;
    }
    private static PartDefinition build_shin_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(430, 77).addBox(-2.1000F, -0.2500F, -2.3000F, 4.2000F, 11.5000F, 4.6000F);
        cubes.texOffs(79, 59).addBox(0.4000F, 1.0000F, -1.9000F, 2.0000F, 9.0000F, 3.8000F);
        cubes.texOffs(458, 77).addBox(-2.0000F, 9.2000F, -4.9000F, 4.0000F, 3.2000F, 6.8000F);
        cubes.texOffs(481, 77).addBox(-1.1000F, 9.7000F, 1.3000F, 2.2000F, 2.2000F, 3.0000F);
        PartDefinition part = parent.addOrReplaceChild("shin_left", cubes, PartPose.offset(0.0000F, 12.0000F, 0.0000F));
        part.addOrReplaceChild("rc_545", CubeListBuilder.create().texOffs(449, 77).addBox(-0.7000F, -3.2500F, -1.6000F, 1.4000F, 6.5000F, 3.2000F), PartPose.offsetAndRotation(1.8000F, 6.5000F, 2.6000F, -0.3840F, -0.0000F, -0.1745F));
        part.addOrReplaceChild("rc_546", CubeListBuilder.create().texOffs(176, 21).addBox(-1.3000F, -1.1000F, -1.6000F, 2.6000F, 2.2000F, 3.2000F), PartPose.offsetAndRotation(0.0000F, 11.0000F, -4.5000F, 0.2094F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_leg_left(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(120, 59).addBox(-2.5000F, -0.5000F, -2.5000F, 5.0000F, 12.0000F, 5.0000F);
        cubes.texOffs(414, 77).addBox(-1.5000F, 1.2500F, -3.5000F, 3.0000F, 8.5000F, 1.4000F);
        PartDefinition part = parent.addOrReplaceChild("leg_left", cubes, PartPose.offset(5.0000F, 1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_547", CubeListBuilder.create().texOffs(141, 59).addBox(-1.1000F, -4.7500F, -2.1000F, 2.2000F, 9.5000F, 4.2000F), PartPose.offsetAndRotation(1.5000F, 5.0000F, 0.0000F, -0.0000F, -0.0000F, -0.1047F));
        part.addOrReplaceChild("rc_548", CubeListBuilder.create().texOffs(189, 0).addBox(-2.1000F, -2.2500F, -1.1000F, 4.2000F, 4.5000F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 11.5000F, -3.2000F, 0.2443F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_549", CubeListBuilder.create().texOffs(423, 77).addBox(-1.0000F, -1.5000F, -0.7000F, 2.0000F, 3.0000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 9.8000F, -3.8000F, 0.3142F, -0.0000F, 0.0000F));
        build_shin_left(part);
        return part;
    }
    private static PartDefinition build_shin_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(430, 77).addBox(-2.1000F, -0.2500F, -2.3000F, 4.2000F, 11.5000F, 4.6000F);
        cubes.texOffs(79, 59).addBox(-2.4000F, 1.0000F, -1.9000F, 2.0000F, 9.0000F, 3.8000F);
        cubes.texOffs(458, 77).addBox(-2.0000F, 9.2000F, -4.9000F, 4.0000F, 3.2000F, 6.8000F);
        cubes.texOffs(481, 77).addBox(-1.1000F, 9.7000F, 1.3000F, 2.2000F, 2.2000F, 3.0000F);
        PartDefinition part = parent.addOrReplaceChild("shin_right", cubes, PartPose.offset(0.0000F, 12.0000F, 0.0000F));
        part.addOrReplaceChild("rc_550", CubeListBuilder.create().texOffs(449, 77).addBox(-0.7000F, -3.2500F, -1.6000F, 1.4000F, 6.5000F, 3.2000F), PartPose.offsetAndRotation(-1.8000F, 6.5000F, 2.6000F, -0.3840F, -0.0000F, 0.1745F));
        part.addOrReplaceChild("rc_551", CubeListBuilder.create().texOffs(176, 21).addBox(-1.3000F, -1.1000F, -1.6000F, 2.6000F, 2.2000F, 3.2000F), PartPose.offsetAndRotation(0.0000F, 11.0000F, -4.5000F, 0.2094F, -0.0000F, 0.0000F));
        return part;
    }
    private static PartDefinition build_leg_right(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(120, 59).addBox(-2.5000F, -0.5000F, -2.5000F, 5.0000F, 12.0000F, 5.0000F);
        cubes.texOffs(414, 77).addBox(-1.5000F, 1.2500F, -3.5000F, 3.0000F, 8.5000F, 1.4000F);
        PartDefinition part = parent.addOrReplaceChild("leg_right", cubes, PartPose.offset(-5.0000F, 1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_552", CubeListBuilder.create().texOffs(141, 59).addBox(-1.1000F, -4.7500F, -2.1000F, 2.2000F, 9.5000F, 4.2000F), PartPose.offsetAndRotation(-1.5000F, 5.0000F, 0.0000F, -0.0000F, -0.0000F, 0.1047F));
        part.addOrReplaceChild("rc_553", CubeListBuilder.create().texOffs(189, 0).addBox(-2.1000F, -2.2500F, -1.1000F, 4.2000F, 4.5000F, 2.2000F), PartPose.offsetAndRotation(0.0000F, 11.5000F, -3.2000F, 0.2443F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_554", CubeListBuilder.create().texOffs(423, 77).addBox(-1.0000F, -1.5000F, -0.7000F, 2.0000F, 3.0000F, 1.4000F), PartPose.offsetAndRotation(0.0000F, 9.8000F, -3.8000F, 0.3142F, -0.0000F, 0.0000F));
        build_shin_right(part);
        return part;
    }
    private static PartDefinition build_fx_anchor(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("fx_anchor", cubes, PartPose.offset(0.0000F, -18.5000F, -10.4000F));
        return part;
    }
    private static PartDefinition build_body(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("body", cubes, PartPose.offset(0.0000F, -26.0000F, 0.0000F));
        build_torso(part);
        build_waist_skirt(part);
        build_leg_left(part);
        build_leg_right(part);
        build_fx_anchor(part);
        return part;
    }
    private static PartDefinition build_root(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("root", cubes, PartPose.offset(0.0000F, 24.0000F, 0.0000F));
        build_body(part);
        return part;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
