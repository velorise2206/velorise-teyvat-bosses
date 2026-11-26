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

public class GeoDrillerModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("geo_driller"), "main");

    public static final String[] ALL_BONES = {
            "root", "chassis_main", "tread_l", "wheel_l_0", "wheel_l_1", "wheel_l_2", "tread_r", "wheel_r_0", "wheel_r_1", "wheel_r_2", "cockpit_cage", "lever_l", "lever_r", "drill_assembly", "drill_head", "engine_unit", "reactor_core", "chest_mount"
    };

    private final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public GeoDrillerModel(ModelPart rootPart) {
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
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
                , PartPose.offset(0.000F, 24.000F, 0.000F));
        PartDefinition b_chassis_main = b_root.addOrReplaceChild("chassis_main", CubeListBuilder.create()
                .texOffs(1, 1).addBox(-11.000F, -3.000F, -18.000F, 22.0F, 6.0F, 38.0F)
                .texOffs(122, 1).addBox(-12.000F, -6.000F, -16.000F, 2.0F, 4.0F, 34.0F)
                .texOffs(195, 1).addBox(10.000F, -6.000F, -16.000F, 2.0F, 4.0F, 34.0F)
                .texOffs(268, 1).addBox(-11.000F, -10.000F, -20.000F, 22.0F, 12.0F, 4.0F)
                .texOffs(321, 1).addBox(-9.000F, -10.000F, -21.000F, 3.0F, 3.0F, 2.0F)
                .texOffs(332, 1).addBox(6.000F, -10.000F, -21.000F, 3.0F, 3.0F, 2.0F)
                , PartPose.offset(0.000F, -10.000F, 0.000F));
        PartDefinition b_tread_l = b_chassis_main.addOrReplaceChild("tread_l", CubeListBuilder.create()
                .texOffs(343, 1).addBox(-3.500F, -6.500F, -18.000F, 7.0F, 2.5F, 36.0F)
                .texOffs(1, 46).addBox(-3.500F, 3.500F, -18.000F, 7.0F, 2.5F, 36.0F)
                .texOffs(88, 46).addBox(-3.500F, -4.500F, -19.500F, 7.0F, 8.5F, 2.5F)
                .texOffs(107, 46).addBox(-3.500F, -4.500F, 17.000F, 7.0F, 8.5F, 2.5F)
                .texOffs(126, 46).addBox(-3.800F, -7.700F, -17.000F, 7.6F, 1.5F, 34.0F)
                , PartPose.offset(-14.500F, 4.000F, 1.000F));
        PartDefinition b_wheel_l_0 = b_tread_l.addOrReplaceChild("wheel_l_0", CubeListBuilder.create()
                .texOffs(209, 46).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(238, 46).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, -12.000F));
        PartDefinition b_wheel_l_1 = b_tread_l.addOrReplaceChild("wheel_l_1", CubeListBuilder.create()
                .texOffs(261, 46).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(290, 46).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, 0.000F));
        PartDefinition b_wheel_l_2 = b_tread_l.addOrReplaceChild("wheel_l_2", CubeListBuilder.create()
                .texOffs(313, 46).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(342, 46).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, 12.000F));
        PartDefinition b_tread_r = b_chassis_main.addOrReplaceChild("tread_r", CubeListBuilder.create()
                .texOffs(365, 46).addBox(-3.500F, -6.500F, -18.000F, 7.0F, 2.5F, 36.0F)
                .texOffs(1, 85).addBox(-3.500F, 3.500F, -18.000F, 7.0F, 2.5F, 36.0F)
                .texOffs(88, 85).addBox(-3.500F, -4.500F, -19.500F, 7.0F, 8.5F, 2.5F)
                .texOffs(107, 85).addBox(-3.500F, -4.500F, 17.000F, 7.0F, 8.5F, 2.5F)
                .texOffs(126, 85).addBox(-3.800F, -7.700F, -17.000F, 7.6F, 1.5F, 34.0F)
                , PartPose.offset(14.500F, 4.000F, 1.000F));
        PartDefinition b_wheel_r_0 = b_tread_r.addOrReplaceChild("wheel_r_0", CubeListBuilder.create()
                .texOffs(209, 85).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(238, 85).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, -12.000F));
        PartDefinition b_wheel_r_1 = b_tread_r.addOrReplaceChild("wheel_r_1", CubeListBuilder.create()
                .texOffs(261, 85).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(290, 85).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, 0.000F));
        PartDefinition b_wheel_r_2 = b_tread_r.addOrReplaceChild("wheel_r_2", CubeListBuilder.create()
                .texOffs(313, 85).addBox(-3.800F, -3.500F, -3.500F, 7.6F, 7.0F, 7.0F)
                .texOffs(342, 85).addBox(-4.200F, -1.500F, -1.500F, 8.4F, 3.0F, 3.0F)
                , PartPose.offset(0.000F, 0.500F, 12.000F));
        PartDefinition b_cockpit_cage = b_chassis_main.addOrReplaceChild("cockpit_cage", CubeListBuilder.create()
                .texOffs(365, 85).addBox(-5.000F, -3.000F, -4.000F, 10.0F, 3.0F, 8.0F)
                .texOffs(402, 85).addBox(-5.000F, -13.000F, 2.500F, 10.0F, 10.0F, 2.0F)
                .texOffs(427, 85).addBox(-8.000F, -6.000F, -11.000F, 16.0F, 6.0F, 4.0F)
                .texOffs(468, 85).addBox(-9.000F, -16.000F, -11.000F, 1.6F, 14.0F, 1.6F)
                .texOffs(473, 85).addBox(7.400F, -16.000F, -11.000F, 1.6F, 14.0F, 1.6F)
                .texOffs(478, 85).addBox(-9.000F, -17.000F, 4.000F, 1.6F, 15.0F, 1.6F)
                .texOffs(483, 85).addBox(7.400F, -17.000F, 4.000F, 1.6F, 15.0F, 1.6F)
                .texOffs(1, 124).addBox(-9.000F, -17.100F, -11.000F, 1.6F, 1.6F, 16.6F)
                .texOffs(36, 124).addBox(7.400F, -17.100F, -11.000F, 1.6F, 1.6F, 16.6F)
                .texOffs(71, 124).addBox(-9.000F, -17.100F, -11.000F, 18.0F, 1.6F, 1.6F)
                .texOffs(110, 124).addBox(-9.000F, -17.100F, 4.000F, 18.0F, 1.6F, 1.6F)
                , PartPose.offset(0.000F, -3.000F, -4.000F));
        PartDefinition b_lever_l = b_cockpit_cage.addOrReplaceChild("lever_l", CubeListBuilder.create()
                .texOffs(149, 124).addBox(-0.600F, -5.000F, -0.600F, 1.2F, 5.0F, 1.2F)
                .texOffs(154, 124).addBox(-1.100F, -6.200F, -1.100F, 2.2F, 1.4F, 2.2F)
                , PartPose.offsetAndRotation(-4.500F, -5.500F, -8.500F, 0.2618F, -0.0000F, 0.0000F));
        PartDefinition b_lever_r = b_cockpit_cage.addOrReplaceChild("lever_r", CubeListBuilder.create()
                .texOffs(163, 124).addBox(-0.600F, -5.000F, -0.600F, 1.2F, 5.0F, 1.2F)
                .texOffs(168, 124).addBox(-1.100F, -6.200F, -1.100F, 2.2F, 1.4F, 2.2F)
                , PartPose.offsetAndRotation(4.500F, -5.500F, -8.500F, 0.2618F, -0.0000F, 0.0000F));
        PartDefinition b_drill_assembly = b_chassis_main.addOrReplaceChild("drill_assembly", CubeListBuilder.create()
                .texOffs(177, 124).addBox(-9.500F, -9.500F, -4.000F, 19.0F, 19.0F, 5.0F)
                .texOffs(226, 124).addBox(-10.000F, -10.000F, -2.500F, 20.0F, 20.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -4.000F, -20.000F));
        PartDefinition b_drill_head = b_drill_assembly.addOrReplaceChild("drill_head", CubeListBuilder.create()
                .texOffs(271, 124).addBox(-8.500F, -8.500F, -5.500F, 17.0F, 17.0F, 5.5F)
                .texOffs(316, 124).addBox(7.540F, -1.300F, -4.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(327, 124).addBox(-1.300F, -10.140F, -4.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(338, 124).addBox(-10.140F, -1.300F, -4.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(349, 124).addBox(-1.300F, 7.540F, -4.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(360, 124).addBox(-7.000F, -7.000F, -10.500F, 14.0F, 14.0F, 5.5F)
                .texOffs(399, 124).addBox(5.128F, -4.718F, -9.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(410, 124).addBox(-4.718F, -7.728F, -9.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(421, 124).addBox(-7.728F, 2.118F, -9.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(432, 124).addBox(2.118F, 5.128F, -9.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(443, 124).addBox(-5.500F, -5.500F, -15.500F, 11.0F, 11.0F, 5.5F)
                .texOffs(476, 124).addBox(1.899F, -6.042F, -14.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(487, 124).addBox(-6.042F, -4.499F, -14.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(498, 124).addBox(-4.499F, 3.442F, -14.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(1, 149).addBox(3.442F, 1.899F, -14.500F, 2.6F, 2.6F, 3.0F)
                .texOffs(12, 149).addBox(-3.800F, -3.800F, -20.000F, 7.6F, 7.6F, 5.0F)
                .texOffs(37, 149).addBox(-0.887F, -5.230F, -19.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(48, 149).addBox(-5.230F, -1.713F, -19.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(59, 149).addBox(-1.713F, 2.630F, -19.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(70, 149).addBox(2.630F, -0.887F, -19.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(81, 149).addBox(-2.000F, -2.000F, -24.000F, 4.0F, 4.0F, 4.5F)
                .texOffs(98, 149).addBox(-2.079F, -3.229F, -23.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(109, 149).addBox(-3.229F, -0.521F, -23.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(120, 149).addBox(-0.521F, 0.629F, -23.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(131, 149).addBox(0.629F, -2.079F, -23.000F, 2.6F, 2.6F, 3.0F)
                .texOffs(142, 149).addBox(-1.200F, -1.200F, -26.500F, 2.4F, 2.4F, 3.0F)
                , PartPose.offset(0.000F, -0.000F, -4.000F));
        PartDefinition b_engine_unit = b_chassis_main.addOrReplaceChild("engine_unit", CubeListBuilder.create()
                .texOffs(153, 149).addBox(-8.500F, -5.000F, -4.000F, 17.0F, 5.0F, 9.0F)
                .texOffs(206, 149).addBox(-9.500F, -9.000F, -3.000F, 2.0F, 7.0F, 2.0F)
                .texOffs(215, 149).addBox(7.500F, -9.000F, -3.000F, 2.0F, 7.0F, 2.0F)
                .texOffs(224, 149).addBox(-9.500F, -8.000F, 2.000F, 2.0F, 6.0F, 2.0F)
                .texOffs(233, 149).addBox(7.500F, -8.000F, 2.000F, 2.0F, 6.0F, 2.0F)
                , PartPose.offset(0.000F, -3.000F, 6.000F));
        PartDefinition b_reactor_core = b_engine_unit.addOrReplaceChild("reactor_core", CubeListBuilder.create()
                .texOffs(242, 149).addBox(-4.500F, -4.500F, -4.500F, 9.0F, 9.0F, 9.0F)
                .texOffs(279, 149).addBox(-2.500F, -2.500F, -2.500F, 5.0F, 5.0F, 5.0F)
                .texOffs(300, 149).addBox(-5.000F, -6.000F, -5.000F, 10.0F, 1.5F, 10.0F)
                , PartPose.offset(0.000F, -9.500F, 0.500F));
        // Rear mounting anchor for 1x (single) or 2x (double) vanilla chest attached via right-click
        b_chassis_main.addOrReplaceChild("chest_mount", CubeListBuilder.create()
                , PartPose.offset(0.000F, -3.000F, 15.500F));
        return LayerDefinition.create(mesh, 512, 512);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
