// Optimized Genshin/Cataclysm-style CryoRegisvineModel (124 cubes, 256x256 Box-UV)
package com.velorise.teyvatbosses.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import java.util.HashMap;
import java.util.Map;

public class CryoRegisvineModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TeyvatBossesConstants.id("cryo_regisvine"), "main");
    public final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public CryoRegisvineModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        registerBone("root", rootPart.getChild("root"));
        registerBone("root_base", rootPart.getChild("root").getChild("root_base"));
        registerBone("root_core", rootPart.getChild("root").getChild("root_base").getChild("root_core"));
        registerBone("stem_1", rootPart.getChild("root").getChild("root_base").getChild("stem_1"));
        registerBone("stem_2", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2"));
        registerBone("stem_3", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3"));
        registerBone("neck", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck"));
        registerBone("corolla_head", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head"));
        registerBone("corolla_core", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("corolla_core"));
        registerBone("rotary_beam", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("rotary_beam"));
        registerBone("petal_ur", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("petal_ur"));
        registerBone("petal_ul", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("petal_ul"));
        registerBone("petal_lr", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("petal_lr"));
        registerBone("petal_ll", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("petal_ll"));
        registerBone("back_sepal_r", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("back_sepal_r"));
        registerBone("back_sepal_l", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("back_sepal_l"));
        registerBone("lash_r_base", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_r_base"));
        registerBone("lash_r_mid", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_r_base").getChild("lash_r_mid"));
        registerBone("lash_r_tip", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_r_base").getChild("lash_r_mid").getChild("lash_r_tip"));
        registerBone("lash_beam_r", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_r_base").getChild("lash_r_mid").getChild("lash_r_tip").getChild("lash_beam_r"));
        registerBone("lash_l_base", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_l_base"));
        registerBone("lash_l_mid", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_l_base").getChild("lash_l_mid"));
        registerBone("lash_l_tip", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_l_base").getChild("lash_l_mid").getChild("lash_l_tip"));
        registerBone("lash_beam_l", rootPart.getChild("root").getChild("root_base").getChild("stem_1").getChild("stem_2").getChild("stem_3").getChild("neck").getChild("corolla_head").getChild("lash_l_base").getChild("lash_l_mid").getChild("lash_l_tip").getChild("lash_beam_l"));
        registerBone("frost_ring", rootPart.getChild("root").getChild("frost_ring"));
        registerBone("fx_anchor", rootPart.getChild("root").getChild("fx_anchor"));
    }

    private void registerBone(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
    }

    public ModelPart getBone(String name) {
        return bones.get(name);
    }

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
        return LayerDefinition.create(mesh, 256, 256);
    }

    private static PartDefinition build_root_core(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("root_core", cubes, PartPose.offset(0.0000F, -7.0000F, -10.0000F));
        part.addOrReplaceChild("rc_0", CubeListBuilder.create().texOffs(154, 156).addBox(-2.5000F, -3.0000F, -2.5000F, 5.0000F, 6.0000F, 5.0000F), PartPose.offsetAndRotation(0.0000F, 0.6000F, 0.2000F, -0.1745F, 0.7854F, -0.0000F));
        part.addOrReplaceChild("rc_1", CubeListBuilder.create().texOffs(20, 172).addBox(-1.5000F, -2.5000F, -1.5000F, 3.0000F, 5.0000F, 3.0000F), PartPose.offsetAndRotation(-3.0000F, 0.8000F, 0.8000F, -0.0000F, 0.0000F, -0.3142F));
        part.addOrReplaceChild("rc_2", CubeListBuilder.create().texOffs(20, 172).addBox(-1.5000F, -2.5000F, -1.5000F, 3.0000F, 5.0000F, 3.0000F), PartPose.offsetAndRotation(3.0000F, 0.8000F, 0.8000F, -0.0000F, 0.0000F, 0.3142F));
        return part;
    }

    private static PartDefinition build_corolla_core(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("corolla_core", cubes, PartPose.offset(0.0000F, -0.0000F, -5.0000F));
        part.addOrReplaceChild("rc_3", CubeListBuilder.create().texOffs(102, 156).addBox(-3.0000F, -3.5000F, -2.5000F, 6.0000F, 7.0000F, 5.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, -1.2000F, -0.0000F, 0.0000F, -0.7854F));
        part.addOrReplaceChild("rc_4", CubeListBuilder.create().texOffs(2, 172).addBox(-2.0000F, -2.0000F, -2.0000F, 4.0000F, 4.0000F, 4.0000F), PartPose.offsetAndRotation(0.0000F, -0.0000F, -1.8000F, -0.0000F, 0.0000F, -0.7854F));
        part.addOrReplaceChild("rc_5", CubeListBuilder.create().texOffs(20, 172).addBox(-1.5000F, -2.5000F, -1.5000F, 3.0000F, 5.0000F, 3.0000F), PartPose.offsetAndRotation(-3.8000F, -0.0000F, -0.6000F, -0.0000F, 0.0000F, -0.3840F));
        part.addOrReplaceChild("rc_6", CubeListBuilder.create().texOffs(20, 172).addBox(-1.5000F, -2.5000F, -1.5000F, 3.0000F, 5.0000F, 3.0000F), PartPose.offsetAndRotation(3.8000F, -0.0000F, -0.6000F, -0.0000F, 0.0000F, 0.3840F));
        return part;
    }

    private static PartDefinition build_rotary_beam(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(104, 2).addBox(-2.0000F, -2.0000F, -44.0000F, 4.0000F, 4.0000F, 44.0000F);
        cubes.texOffs(2, 2).addBox(-3.0000F, -3.0000F, -44.0000F, 6.0000F, 6.0000F, 44.0000F);
        PartDefinition part = parent.addOrReplaceChild("rotary_beam", cubes, PartPose.offset(0.0000F, -0.0000F, -7.0000F));
        return part;
    }

    private static PartDefinition build_petal_ur(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("petal_ur", cubes, PartPose.offset(4.0000F, -3.0000F, 0.0000F));
        part.addOrReplaceChild("rc_7", CubeListBuilder.create().texOffs(112, 96).addBox(-2.0000F, -11.0000F, -1.0000F, 4.0000F, 22.0000F, 2.0000F), PartPose.offsetAndRotation(6.5070F, -8.4730F, -3.6210F, 0.2443F, 0.1745F, 0.6981F));
        part.addOrReplaceChild("rc_8", CubeListBuilder.create().texOffs(74, 134).addBox(-1.5000F, -9.0000F, -1.0000F, 3.0000F, 18.0000F, 2.0000F), PartPose.offsetAndRotation(17.1540F, -22.3380F, -7.9090F, 0.2443F, 0.1745F, 0.6981F));
        part.addOrReplaceChild("rc_9", CubeListBuilder.create().texOffs(84, 54).addBox(-10.0000F, -20.0000F, 0.0000F, 20.0000F, 40.0000F, 0.0000F), PartPose.offsetAndRotation(12.1260F, -15.7910F, -5.8840F, 0.2443F, 0.1745F, 0.6981F));
        part.addOrReplaceChild("rc_10", CubeListBuilder.create().texOffs(204, 54).addBox(-9.0000F, -19.0000F, 0.4000F, 18.0000F, 38.0000F, 0.0000F), PartPose.offsetAndRotation(12.1260F, -15.7910F, -5.8840F, 0.2443F, 0.1745F, 0.6981F));
        return part;
    }

    private static PartDefinition build_petal_ul(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("petal_ul", cubes, PartPose.offset(-4.0000F, -3.0000F, 0.0000F));
        part.addOrReplaceChild("rc_11", CubeListBuilder.create().texOffs(112, 96).addBox(-2.0000F, -11.0000F, -1.0000F, 4.0000F, 22.0000F, 2.0000F), PartPose.offsetAndRotation(-6.5070F, -8.4730F, -3.6210F, 0.2443F, -0.1745F, -0.6981F));
        part.addOrReplaceChild("rc_12", CubeListBuilder.create().texOffs(74, 134).addBox(-1.5000F, -9.0000F, -1.0000F, 3.0000F, 18.0000F, 2.0000F), PartPose.offsetAndRotation(-17.1540F, -22.3380F, -7.9090F, 0.2443F, -0.1745F, -0.6981F));
        part.addOrReplaceChild("rc_13", CubeListBuilder.create().texOffs(84, 54).addBox(-10.0000F, -20.0000F, 0.0000F, 20.0000F, 40.0000F, 0.0000F), PartPose.offsetAndRotation(-12.1260F, -15.7910F, -5.8840F, 0.2443F, -0.1745F, -0.6981F));
        part.addOrReplaceChild("rc_14", CubeListBuilder.create().texOffs(204, 54).addBox(-9.0000F, -19.0000F, 0.4000F, 18.0000F, 38.0000F, 0.0000F), PartPose.offsetAndRotation(-12.1260F, -15.7910F, -5.8840F, 0.2443F, -0.1745F, -0.6981F));
        return part;
    }

    private static PartDefinition build_petal_lr(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("petal_lr", cubes, PartPose.offset(4.0000F, 3.0000F, 0.0000F));
        part.addOrReplaceChild("rc_15", CubeListBuilder.create().texOffs(126, 96).addBox(-2.0000F, -10.0000F, -1.0000F, 4.0000F, 20.0000F, 2.0000F), PartPose.offsetAndRotation(9.2790F, 5.0780F, -3.3820F, 0.2443F, 0.1745F, 2.1642F));
        part.addOrReplaceChild("rc_16", CubeListBuilder.create().texOffs(86, 134).addBox(-1.5000F, -8.0000F, -1.0000F, 3.0000F, 16.0000F, 2.0000F), PartPose.offsetAndRotation(22.5250F, 13.2020F, -7.1940F, 0.2443F, 0.1745F, 2.1642F));
        part.addOrReplaceChild("rc_17", CubeListBuilder.create().texOffs(2, 96).addBox(-9.5000F, -18.0000F, 0.0000F, 19.0000F, 36.0000F, 0.0000F), PartPose.offsetAndRotation(16.3160F, 9.3930F, -5.4080F, 0.2443F, 0.1745F, 2.1642F));
        part.addOrReplaceChild("rc_18", CubeListBuilder.create().texOffs(42, 96).addBox(-8.5000F, -17.0000F, 0.4000F, 17.0000F, 34.0000F, 0.0000F), PartPose.offsetAndRotation(16.3160F, 9.3930F, -5.4080F, 0.2443F, 0.1745F, 2.1642F));
        return part;
    }

    private static PartDefinition build_petal_ll(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("petal_ll", cubes, PartPose.offset(-4.0000F, 3.0000F, 0.0000F));
        part.addOrReplaceChild("rc_19", CubeListBuilder.create().texOffs(126, 96).addBox(-2.0000F, -10.0000F, -1.0000F, 4.0000F, 20.0000F, 2.0000F), PartPose.offsetAndRotation(-9.2790F, 5.0780F, -3.3820F, 0.2443F, -0.1745F, -2.1642F));
        part.addOrReplaceChild("rc_20", CubeListBuilder.create().texOffs(86, 134).addBox(-1.5000F, -8.0000F, -1.0000F, 3.0000F, 16.0000F, 2.0000F), PartPose.offsetAndRotation(-22.5250F, 13.2020F, -7.1940F, 0.2443F, -0.1745F, -2.1642F));
        part.addOrReplaceChild("rc_21", CubeListBuilder.create().texOffs(2, 96).addBox(-9.5000F, -18.0000F, 0.0000F, 19.0000F, 36.0000F, 0.0000F), PartPose.offsetAndRotation(-16.3160F, 9.3930F, -5.4080F, 0.2443F, -0.1745F, -2.1642F));
        part.addOrReplaceChild("rc_22", CubeListBuilder.create().texOffs(42, 96).addBox(-8.5000F, -17.0000F, 0.4000F, 17.0000F, 34.0000F, 0.0000F), PartPose.offsetAndRotation(-16.3160F, 9.3930F, -5.4080F, 0.2443F, -0.1745F, -2.1642F));
        return part;
    }

    private static PartDefinition build_back_sepal_r(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("back_sepal_r", cubes, PartPose.offset(5.0000F, -4.0000F, 5.0000F));
        part.addOrReplaceChild("rc_23", CubeListBuilder.create().texOffs(170, 134).addBox(-4.0000F, -7.0000F, -1.0000F, 8.0000F, 14.0000F, 2.0000F), PartPose.offsetAndRotation(2.4310F, -6.2830F, 1.9000F, -0.2793F, -0.1745F, 0.4189F));
        part.addOrReplaceChild("rc_24", CubeListBuilder.create().texOffs(78, 96).addBox(-8.0000F, -12.0000F, 0.0000F, 16.0000F, 24.0000F, 0.0000F), PartPose.offsetAndRotation(5.2090F, -13.4640F, 4.0720F, -0.2793F, -0.1745F, 0.4189F));
        part.addOrReplaceChild("rc_25", CubeListBuilder.create().texOffs(192, 134).addBox(-2.0000F, -7.0000F, -1.0000F, 4.0000F, 14.0000F, 2.0000F), PartPose.offsetAndRotation(5.5560F, -14.3620F, 4.3430F, -0.2793F, -0.1745F, 0.4189F));
        return part;
    }

    private static PartDefinition build_back_sepal_l(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("back_sepal_l", cubes, PartPose.offset(-5.0000F, -4.0000F, 5.0000F));
        part.addOrReplaceChild("rc_26", CubeListBuilder.create().texOffs(170, 134).addBox(-4.0000F, -7.0000F, -1.0000F, 8.0000F, 14.0000F, 2.0000F), PartPose.offsetAndRotation(-2.4310F, -6.2830F, 1.9000F, -0.2793F, 0.1745F, -0.4189F));
        part.addOrReplaceChild("rc_27", CubeListBuilder.create().texOffs(78, 96).addBox(-8.0000F, -12.0000F, 0.0000F, 16.0000F, 24.0000F, 0.0000F), PartPose.offsetAndRotation(-5.2090F, -13.4640F, 4.0720F, -0.2793F, 0.1745F, -0.4189F));
        part.addOrReplaceChild("rc_28", CubeListBuilder.create().texOffs(192, 134).addBox(-2.0000F, -7.0000F, -1.0000F, 4.0000F, 14.0000F, 2.0000F), PartPose.offsetAndRotation(-5.5560F, -14.3620F, 4.3430F, -0.2793F, 0.1745F, -0.4189F));
        return part;
    }

    private static PartDefinition build_lash_beam_r(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(126, 54).addBox(-1.0000F, -1.0000F, -36.0000F, 2.0000F, 2.0000F, 36.0000F);
        cubes.texOffs(2, 54).addBox(-2.0000F, -2.0000F, -36.0000F, 4.0000F, 4.0000F, 36.0000F);
        PartDefinition part = parent.addOrReplaceChild("lash_beam_r", cubes, PartPose.offset(4.0000F, -1.0000F, -1.0000F));
        return part;
    }

    private static PartDefinition build_lash_r_tip(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_r_tip", cubes, PartPose.offset(12.0000F, -2.0000F, 0.0000F));
        part.addOrReplaceChild("rc_29", CubeListBuilder.create().texOffs(144, 172).addBox(-3.5000F, -1.5000F, -1.5000F, 7.0000F, 3.0000F, 3.0000F), PartPose.offsetAndRotation(2.9100F, -0.7280F, 0.0000F, -0.0000F, 0.0000F, -0.2450F));
        part.addOrReplaceChild("rc_30", CubeListBuilder.create().texOffs(226, 172).addBox(-1.0000F, -1.0000F, -1.0000F, 2.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(6.4030F, -1.6010F, 0.0000F, -0.0000F, 0.0000F, -0.2450F));
        part.addOrReplaceChild("rc_31", CubeListBuilder.create().texOffs(34, 172).addBox(-9.5000F, -3.0000F, 0.0000F, 19.0000F, 6.0000F, 0.0000F), PartPose.offsetAndRotation(15.3280F, -3.8320F, 0.0000F, -0.0000F, 0.0000F, -0.2450F));
        part.addOrReplaceChild("rc_32", CubeListBuilder.create().texOffs(34, 172).addBox(-9.5000F, -3.0000F, 0.0000F, 19.0000F, 6.0000F, 0.0000F), PartPose.offsetAndRotation(15.3280F, -3.8320F, 0.0000F, -1.5708F, 0.0000F, -0.2450F));
        part.addOrReplaceChild("rc_33", CubeListBuilder.create().texOffs(192, 156).addBox(-3.0000F, -5.5000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), PartPose.offsetAndRotation(8.9730F, -5.8830F, -1.6070F, 0.3142F, 0.0000F, 0.5236F));
        part.addOrReplaceChild("rc_34", CubeListBuilder.create().texOffs(192, 156).addBox(-3.0000F, -5.5000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), PartPose.offsetAndRotation(9.0230F, 2.7700F, -1.2580F, 0.2443F, 0.0000F, 2.6180F));
        part.addOrReplaceChild("rc_35", CubeListBuilder.create().texOffs(206, 156).addBox(-3.0000F, -5.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), PartPose.offsetAndRotation(10.0010F, 0.3660F, 2.6310F, -0.7330F, 0.6109F, 1.6057F));
        build_lash_beam_r(part);
        return part;
    }

    private static PartDefinition build_lash_r_mid(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_r_mid", cubes, PartPose.offset(12.0000F, -1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_36", CubeListBuilder.create().texOffs(192, 172).addBox(-7.0000F, -1.0000F, -1.0000F, 14.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(6.1160F, -1.0190F, 0.0000F, -0.0000F, 0.0000F, -0.1651F));
        part.addOrReplaceChild("rc_37", CubeListBuilder.create().texOffs(166, 172).addBox(-6.0000F, -2.5000F, 0.0000F, 12.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(6.1160F, -1.0190F, 0.0000F, -0.0000F, 0.0000F, -0.1651F));
        build_lash_r_tip(part);
        return part;
    }

    private static PartDefinition build_lash_r_base(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_r_base", cubes, PartPose.offset(6.0000F, -0.0000F, 2.0000F));
        part.addOrReplaceChild("rc_38", CubeListBuilder.create().texOffs(192, 172).addBox(-7.0000F, -1.0000F, -1.0000F, 14.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(6.1790F, -0.5150F, 0.0000F, -0.0000F, 0.0000F, -0.0831F));
        part.addOrReplaceChild("rc_39", CubeListBuilder.create().texOffs(166, 172).addBox(-6.0000F, -2.5000F, 0.0000F, 12.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(6.1790F, -0.5150F, 0.0000F, -0.0000F, 0.0000F, -0.0831F));
        build_lash_r_mid(part);
        return part;
    }

    private static PartDefinition build_lash_beam_l(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(126, 54).addBox(-1.0000F, -1.0000F, -36.0000F, 2.0000F, 2.0000F, 36.0000F);
        cubes.texOffs(2, 54).addBox(-2.0000F, -2.0000F, -36.0000F, 4.0000F, 4.0000F, 36.0000F);
        PartDefinition part = parent.addOrReplaceChild("lash_beam_l", cubes, PartPose.offset(-4.0000F, -1.0000F, -1.0000F));
        return part;
    }

    private static PartDefinition build_lash_l_tip(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_l_tip", cubes, PartPose.offset(-12.0000F, -2.0000F, 0.0000F));
        part.addOrReplaceChild("rc_40", CubeListBuilder.create().texOffs(144, 172).addBox(-3.5000F, -1.5000F, -1.5000F, 7.0000F, 3.0000F, 3.0000F), PartPose.offsetAndRotation(-2.9100F, -0.7280F, 0.0000F, -0.0000F, 0.0000F, 0.2450F));
        part.addOrReplaceChild("rc_41", CubeListBuilder.create().texOffs(226, 172).addBox(-1.0000F, -1.0000F, -1.0000F, 2.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(-6.4030F, -1.6010F, 0.0000F, -0.0000F, 0.0000F, 0.2450F));
        part.addOrReplaceChild("rc_42", CubeListBuilder.create().texOffs(74, 172).addBox(-9.5000F, -3.0000F, 0.0000F, 19.0000F, 6.0000F, 0.0000F), PartPose.offsetAndRotation(-15.3280F, -3.8320F, 0.0000F, -0.0000F, 0.0000F, 0.2450F));
        part.addOrReplaceChild("rc_43", CubeListBuilder.create().texOffs(74, 172).addBox(-9.5000F, -3.0000F, 0.0000F, 19.0000F, 6.0000F, 0.0000F), PartPose.offsetAndRotation(-15.3280F, -3.8320F, 0.0000F, -1.5708F, 0.0000F, 0.2450F));
        part.addOrReplaceChild("rc_44", CubeListBuilder.create().texOffs(192, 156).addBox(-3.0000F, -5.5000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), PartPose.offsetAndRotation(-8.9730F, -5.8830F, -1.6070F, 0.3142F, 0.0000F, -0.5236F));
        part.addOrReplaceChild("rc_45", CubeListBuilder.create().texOffs(192, 156).addBox(-3.0000F, -5.5000F, 0.0000F, 6.0000F, 11.0000F, 0.0000F), PartPose.offsetAndRotation(-9.0230F, 2.7700F, -1.2580F, 0.2443F, 0.0000F, -2.6180F));
        part.addOrReplaceChild("rc_46", CubeListBuilder.create().texOffs(206, 156).addBox(-3.0000F, -5.0000F, 0.0000F, 6.0000F, 10.0000F, 0.0000F), PartPose.offsetAndRotation(-10.0010F, 0.3660F, 2.6310F, -0.7330F, -0.6109F, -1.6057F));
        build_lash_beam_l(part);
        return part;
    }

    private static PartDefinition build_lash_l_mid(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_l_mid", cubes, PartPose.offset(-12.0000F, -1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_47", CubeListBuilder.create().texOffs(192, 172).addBox(-7.0000F, -1.0000F, -1.0000F, 14.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(-6.1160F, -1.0190F, 0.0000F, -0.0000F, 0.0000F, 0.1651F));
        part.addOrReplaceChild("rc_48", CubeListBuilder.create().texOffs(166, 172).addBox(-6.0000F, -2.5000F, 0.0000F, 12.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(-6.1160F, -1.0190F, 0.0000F, -0.0000F, 0.0000F, 0.1651F));
        build_lash_l_tip(part);
        return part;
    }

    private static PartDefinition build_lash_l_base(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("lash_l_base", cubes, PartPose.offset(-6.0000F, -0.0000F, 2.0000F));
        part.addOrReplaceChild("rc_49", CubeListBuilder.create().texOffs(192, 172).addBox(-7.0000F, -1.0000F, -1.0000F, 14.0000F, 2.0000F, 2.0000F), PartPose.offsetAndRotation(-6.1790F, -0.5150F, 0.0000F, -0.0000F, 0.0000F, 0.0831F));
        part.addOrReplaceChild("rc_50", CubeListBuilder.create().texOffs(166, 172).addBox(-6.0000F, -2.5000F, 0.0000F, 12.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(-6.1790F, -0.5150F, 0.0000F, -0.0000F, 0.0000F, 0.0831F));
        build_lash_l_mid(part);
        return part;
    }

    private static PartDefinition build_corolla_head(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(2, 134).addBox(-6.0000F, -6.0000F, -4.5000F, 12.0000F, 12.0000F, 8.0000F);
        cubes.texOffs(206, 134).addBox(-5.0000F, -5.0000F, 2.0000F, 10.0000F, 10.0000F, 5.0000F);
        PartDefinition part = parent.addOrReplaceChild("corolla_head", cubes, PartPose.offset(0.0000F, -6.0000F, -2.0000F));
        part.addOrReplaceChild("rc_51", CubeListBuilder.create().texOffs(2, 156).addBox(-2.5000F, -5.5000F, -1.5000F, 5.0000F, 11.0000F, 3.0000F), PartPose.offsetAndRotation(0.0000F, -8.5000F, -3.5000F, 0.2094F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_52", CubeListBuilder.create().texOffs(126, 156).addBox(-2.0000F, -4.5000F, -1.5000F, 4.0000F, 9.0000F, 3.0000F), PartPose.offsetAndRotation(-7.0000F, -3.0000F, -3.2000F, -0.0000F, 0.0000F, -0.5585F));
        part.addOrReplaceChild("rc_53", CubeListBuilder.create().texOffs(126, 156).addBox(-2.0000F, -4.5000F, -1.5000F, 4.0000F, 9.0000F, 3.0000F), PartPose.offsetAndRotation(7.0000F, -3.0000F, -3.2000F, -0.0000F, 0.0000F, 0.5585F));
        part.addOrReplaceChild("rc_54", CubeListBuilder.create().texOffs(176, 156).addBox(-2.0000F, -4.0000F, -1.5000F, 4.0000F, 8.0000F, 3.0000F), PartPose.offsetAndRotation(-6.2000F, 4.5000F, -3.0000F, -0.0000F, 0.0000F, -2.4435F));
        part.addOrReplaceChild("rc_55", CubeListBuilder.create().texOffs(176, 156).addBox(-2.0000F, -4.0000F, -1.5000F, 4.0000F, 8.0000F, 3.0000F), PartPose.offsetAndRotation(6.2000F, 4.5000F, -3.0000F, -0.0000F, 0.0000F, 2.4435F));
        build_corolla_core(part);
        build_rotary_beam(part);
        build_petal_ur(part);
        build_petal_ul(part);
        build_petal_lr(part);
        build_petal_ll(part);
        build_back_sepal_r(part);
        build_back_sepal_l(part);
        build_lash_r_base(part);
        build_lash_l_base(part);
        return part;
    }

    private static PartDefinition build_neck(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(144, 134).addBox(-3.0000F, -8.2000F, -3.8000F, 6.0000F, 10.0000F, 6.0000F);
        PartDefinition part = parent.addOrReplaceChild("neck", cubes, PartPose.offset(0.0000F, -10.0000F, -1.0000F));
        part.addOrReplaceChild("rc_56", CubeListBuilder.create().texOffs(142, 156).addBox(-1.0000F, -4.5000F, -1.5000F, 2.0000F, 9.0000F, 3.0000F), PartPose.offsetAndRotation(-3.4000F, -3.2000F, -0.8000F, -0.0000F, 0.0000F, -0.1047F));
        part.addOrReplaceChild("rc_57", CubeListBuilder.create().texOffs(142, 156).addBox(-1.0000F, -4.5000F, -1.5000F, 2.0000F, 9.0000F, 3.0000F), PartPose.offsetAndRotation(3.4000F, -3.2000F, -0.8000F, -0.0000F, 0.0000F, 0.1047F));
        part.addOrReplaceChild("rc_58", CubeListBuilder.create().texOffs(220, 156).addBox(-1.5000F, -4.0000F, -1.0000F, 3.0000F, 8.0000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -3.5000F, 2.8000F, -0.1745F, 0.0000F, -0.0000F));
        build_corolla_head(part);
        return part;
    }

    private static PartDefinition build_stem_3(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(44, 134).addBox(-3.5000F, -11.5000F, -4.0000F, 7.0000F, 13.0000F, 7.0000F);
        PartDefinition part = parent.addOrReplaceChild("stem_3", cubes, PartPose.offset(0.0000F, -10.0000F, -1.0000F));
        part.addOrReplaceChild("rc_59", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(-3.9000F, -5.0000F, -0.5000F, -0.0000F, 0.0000F, -0.1047F));
        part.addOrReplaceChild("rc_60", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(3.9000F, -5.0000F, -0.5000F, -0.0000F, 0.0000F, 0.1047F));
        part.addOrReplaceChild("rc_61", CubeListBuilder.create().texOffs(66, 156).addBox(-1.5000F, -5.5000F, -1.0000F, 3.0000F, 11.0000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -5.3000F, 3.6000F, -0.1745F, 0.0000F, -0.0000F));
        build_neck(part);
        return part;
    }

    private static PartDefinition build_stem_2(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(44, 134).addBox(-3.5000F, -11.5000F, -4.0000F, 7.0000F, 13.0000F, 7.0000F);
        PartDefinition part = parent.addOrReplaceChild("stem_2", cubes, PartPose.offset(0.0000F, -10.0000F, 0.0000F));
        part.addOrReplaceChild("rc_62", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(-3.9000F, -5.0000F, -0.5000F, -0.0000F, 0.0000F, -0.1047F));
        part.addOrReplaceChild("rc_63", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(3.9000F, -5.0000F, -0.5000F, -0.0000F, 0.0000F, 0.1047F));
        part.addOrReplaceChild("rc_64", CubeListBuilder.create().texOffs(66, 156).addBox(-1.5000F, -5.5000F, -1.0000F, 3.0000F, 11.0000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -5.3000F, 3.6000F, -0.1745F, 0.0000F, -0.0000F));
        build_stem_3(part);
        return part;
    }

    private static PartDefinition build_stem_1(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(202, 96).addBox(-4.0000F, -11.5000F, -4.0000F, 8.0000F, 13.0000F, 8.0000F);
        PartDefinition part = parent.addOrReplaceChild("stem_1", cubes, PartPose.offset(0.0000F, -8.0000F, 0.0000F));
        part.addOrReplaceChild("rc_65", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(-4.4000F, -5.0000F, 0.0000F, -0.0000F, 0.0000F, -0.1047F));
        part.addOrReplaceChild("rc_66", CubeListBuilder.create().texOffs(238, 134).addBox(-1.0000F, -6.0000F, -1.5000F, 2.0000F, 12.0000F, 3.0000F), PartPose.offsetAndRotation(4.4000F, -5.0000F, 0.0000F, -0.0000F, 0.0000F, 0.1047F));
        part.addOrReplaceChild("rc_67", CubeListBuilder.create().texOffs(66, 156).addBox(-1.5000F, -5.5000F, -1.0000F, 3.0000F, 11.0000F, 2.0000F), PartPose.offsetAndRotation(0.0000F, -5.3000F, 4.6000F, -0.1745F, 0.0000F, -0.0000F));
        build_stem_2(part);
        return part;
    }

    private static PartDefinition build_root_base(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(2, 186).addBox(-7.0000F, -7.2000F, -7.0000F, 14.0000F, 6.0000F, 14.0000F);
        cubes.texOffs(60, 186).addBox(-5.0000F, -10.0000F, -5.0000F, 10.0000F, 4.0000F, 10.0000F);
        PartDefinition part = parent.addOrReplaceChild("root_base", cubes, PartPose.offset(0.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_68", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(3.5590F, -5.4000F, 8.8080F, -0.2443F, 0.3840F, -0.0000F));
        part.addOrReplaceChild("rc_69", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(5.4320F, -2.5000F, 13.4440F, -0.5934F, 0.3840F, -0.0000F));
        part.addOrReplaceChild("rc_70", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(8.8080F, -5.4000F, 3.5590F, -0.2443F, 1.1868F, -0.0000F));
        part.addOrReplaceChild("rc_71", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(13.4440F, -2.5000F, 5.4320F, -0.5934F, 1.1868F, -0.0000F));
        part.addOrReplaceChild("rc_72", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(8.8080F, -5.4000F, -3.5590F, -0.2443F, 1.9548F, -0.0000F));
        part.addOrReplaceChild("rc_73", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(13.4440F, -2.5000F, -5.4320F, -0.5934F, 1.9548F, -0.0000F));
        part.addOrReplaceChild("rc_74", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(3.5590F, -5.4000F, -8.8080F, -0.2443F, 2.7576F, -0.0000F));
        part.addOrReplaceChild("rc_75", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(5.4320F, -2.5000F, -13.4440F, -0.5934F, 2.7576F, -0.0000F));
        part.addOrReplaceChild("rc_76", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(-3.5590F, -5.4000F, -8.8080F, -0.2443F, 3.5256F, -0.0000F));
        part.addOrReplaceChild("rc_77", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(-5.4320F, -2.5000F, -13.4440F, -0.5934F, 3.5256F, -0.0000F));
        part.addOrReplaceChild("rc_78", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(-8.8080F, -5.4000F, -3.5590F, -0.2443F, 4.3284F, -0.0000F));
        part.addOrReplaceChild("rc_79", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(-13.4440F, -2.5000F, -5.4320F, -0.5934F, 4.3284F, -0.0000F));
        part.addOrReplaceChild("rc_80", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(-8.8080F, -5.4000F, 3.5590F, -0.2443F, 5.0964F, -0.0000F));
        part.addOrReplaceChild("rc_81", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(-13.4440F, -2.5000F, 5.4320F, -0.5934F, 5.0964F, -0.0000F));
        part.addOrReplaceChild("rc_82", CubeListBuilder.create().texOffs(102, 186).addBox(-2.0000F, -2.0000F, -4.5000F, 4.0000F, 4.0000F, 9.0000F), PartPose.offsetAndRotation(-3.5590F, -5.4000F, 8.8080F, -0.2443F, 5.8992F, -0.0000F));
        part.addOrReplaceChild("rc_83", CubeListBuilder.create().texOffs(130, 186).addBox(-1.5000F, -3.5000F, -1.5000F, 3.0000F, 7.0000F, 3.0000F), PartPose.offsetAndRotation(-5.4320F, -2.5000F, 13.4440F, -0.5934F, 5.8992F, -0.0000F));
        part.addOrReplaceChild("rc_84", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(3.9000F, -9.6000F, 5.5700F, -1.1170F, 0.6109F, -0.0000F));
        part.addOrReplaceChild("rc_85", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(6.5390F, -6.8000F, 9.3380F, -2.4784F, 0.6109F, -0.0000F));
        part.addOrReplaceChild("rc_86", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(6.7740F, -9.6000F, -0.5930F, -1.1170F, 1.6581F, -0.0000F));
        part.addOrReplaceChild("rc_87", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(11.3570F, -6.8000F, -0.9940F, -2.4784F, 1.6581F, -0.0000F));
        part.addOrReplaceChild("rc_88", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(3.4000F, -9.6000F, -5.8890F, -1.1170F, 2.6180F, -0.0000F));
        part.addOrReplaceChild("rc_89", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(5.7000F, -6.8000F, -9.8730F, -2.4784F, 2.6180F, -0.0000F));
        part.addOrReplaceChild("rc_90", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(-3.4000F, -9.6000F, -5.8890F, -1.1170F, 3.6652F, -0.0000F));
        part.addOrReplaceChild("rc_91", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(-5.7000F, -6.8000F, -9.8730F, -2.4784F, 3.6652F, -0.0000F));
        part.addOrReplaceChild("rc_92", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(-6.7740F, -9.6000F, -0.5930F, -1.1170F, 4.6251F, -0.0000F));
        part.addOrReplaceChild("rc_93", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(-11.3570F, -6.8000F, -0.9940F, -2.4784F, 4.6251F, -0.0000F));
        part.addOrReplaceChild("rc_94", CubeListBuilder.create().texOffs(144, 186).addBox(-5.0000F, -3.5000F, 0.0000F, 10.0000F, 7.0000F, 0.0000F), PartPose.offsetAndRotation(-3.9000F, -9.6000F, 5.5700F, -1.1170F, 5.6723F, -0.0000F));
        part.addOrReplaceChild("rc_95", CubeListBuilder.create().texOffs(166, 186).addBox(-5.5000F, -4.0000F, 0.0000F, 11.0000F, 8.0000F, 0.0000F), PartPose.offsetAndRotation(-6.5390F, -6.8000F, 9.3380F, -2.4784F, 5.6723F, -0.0000F));
        part.addOrReplaceChild("rc_96", CubeListBuilder.create().texOffs(190, 186).addBox(-2.0000F, -2.5000F, 0.0000F, 4.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(7.4320F, -7.2000F, 3.4650F, -0.8727F, 1.1345F, -0.0000F));
        part.addOrReplaceChild("rc_97", CubeListBuilder.create().texOffs(190, 186).addBox(-2.0000F, -2.5000F, 0.0000F, 4.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(6.7170F, -7.2000F, -4.7030F, -0.8727F, 2.1817F, -0.0000F));
        part.addOrReplaceChild("rc_98", CubeListBuilder.create().texOffs(190, 186).addBox(-2.0000F, -2.5000F, 0.0000F, 4.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(-6.7170F, -7.2000F, -4.7030F, -0.8727F, 4.1015F, -0.0000F));
        part.addOrReplaceChild("rc_99", CubeListBuilder.create().texOffs(190, 186).addBox(-2.0000F, -2.5000F, 0.0000F, 4.0000F, 5.0000F, 0.0000F), PartPose.offsetAndRotation(-7.4320F, -7.2000F, 3.4650F, -0.8727F, 5.1487F, -0.0000F));
        build_root_core(part);
        build_stem_1(part);
        return part;
    }

    private static PartDefinition build_frost_ring(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("frost_ring", cubes, PartPose.offset(0.0000F, -1.0000F, 0.0000F));
        part.addOrReplaceChild("rc_100", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(0.0000F, -3.5000F, 26.0000F, -0.3142F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_101", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(22.5170F, -3.5000F, 13.0000F, -0.3142F, 1.0472F, -0.0000F));
        part.addOrReplaceChild("rc_102", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(22.5170F, -3.5000F, -13.0000F, -0.3142F, 2.0944F, -0.0000F));
        part.addOrReplaceChild("rc_103", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(0.0000F, -3.5000F, -26.0000F, -0.3142F, 3.1416F, -0.0000F));
        part.addOrReplaceChild("rc_104", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(-22.5170F, -3.5000F, -13.0000F, -0.3142F, 4.1888F, -0.0000F));
        part.addOrReplaceChild("rc_105", CubeListBuilder.create().texOffs(48, 156).addBox(-2.0000F, -4.5000F, -2.0000F, 4.0000F, 9.0000F, 4.0000F), PartPose.offsetAndRotation(-22.5170F, -3.5000F, 13.0000F, -0.3142F, 5.2360F, -0.0000F));
        part.addOrReplaceChild("rc_106", CubeListBuilder.create().texOffs(114, 172).addBox(-5.0000F, -1.0000F, -2.0000F, 10.0000F, 2.0000F, 4.0000F), PartPose.offsetAndRotation(18.0310F, -0.2000F, 18.0310F, -0.0000F, 0.7854F, -0.0000F));
        part.addOrReplaceChild("rc_107", CubeListBuilder.create().texOffs(114, 172).addBox(-5.0000F, -1.0000F, -2.0000F, 10.0000F, 2.0000F, 4.0000F), PartPose.offsetAndRotation(18.0310F, -0.2000F, -18.0310F, -0.0000F, 2.3562F, -0.0000F));
        part.addOrReplaceChild("rc_108", CubeListBuilder.create().texOffs(114, 172).addBox(-5.0000F, -1.0000F, -2.0000F, 10.0000F, 2.0000F, 4.0000F), PartPose.offsetAndRotation(-18.0310F, -0.2000F, -18.0310F, -0.0000F, 3.9270F, -0.0000F));
        part.addOrReplaceChild("rc_109", CubeListBuilder.create().texOffs(114, 172).addBox(-5.0000F, -1.0000F, -2.0000F, 10.0000F, 2.0000F, 4.0000F), PartPose.offsetAndRotation(-18.0310F, -0.2000F, 18.0310F, -0.0000F, 5.4978F, -0.0000F));
        return part;
    }

    private static PartDefinition build_fx_anchor(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("fx_anchor", cubes, PartPose.offset(0.0000F, -45.5800F, -34.3800F));
        return part;
    }

    private static PartDefinition build_root(PartDefinition parent) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("root", cubes, PartPose.offset(0.0000F, 24.0000F, 0.0000F));
        build_root_base(part);
        build_frost_ring(part);
        build_fx_anchor(part);
        return part;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
