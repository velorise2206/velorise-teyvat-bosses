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
 * AAA Fontaine Clockwork Dreadnought v4 HierarchicalModel: seamless 5-facet curved glass canopy
 * with 1px steel/brass mullions, Drowned Captain in brass diving helmet gripping 2 control levers,
 * twin retractable brass hydraulic pincer claws, real multi-tooth spinning clockwork gears,
 * dual hydraulic wing struts, and a 12-blade pitched bronze turbine inside a 12-segment brass ring.
 * <p>Auto-generated 1:1: 240 cubes, 119 bones, 512x512 non-overlapping Box UVs.
 */
public class RuinNautilusModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("ruin_nautilus"), "main");

    public static final String[] ALL_BONES = {
            "root", "hull_pitch", "hull_main", "hull_bevel_top_l", "hull_bevel_bot_l", "hull_bevel_top_r",
            "hull_bevel_bot_r", "gauge_front_tilt", "belly_gear", "belly_gear_sp_0", "belly_gear_sp_1", "belly_gear_sp_2",
            "belly_gear_sp_3", "belly_gear_2", "belly_gear_2_sp_0", "belly_gear_2_sp_1", "belly_gear_2_sp_2", "belly_gear_2_sp_3",
            "sub_cockpit_frame", "cockpit_roof_brow", "rib_arch_side_l", "rib_ws_top_l", "rib_arch_side_r", "rib_ws_top_r",
            "control_lever_l", "control_lever_r", "sub_cockpit_glass", "glass_front_curve_top", "glass_cheek_l", "glass_cheek_top_l",
            "glass_cheek_r", "glass_cheek_top_r", "drowned_pilot", "drowned_head", "drowned_arm_l", "drowned_arm_r",
            "harpoon_arm_l", "harpoon_claw_l", "claw_jaw_top_l", "claw_jaw_bot_l", "harpoon_arm_r", "harpoon_claw_r",
            "claw_jaw_top_r", "claw_jaw_bot_r", "bow_dome", "eye_iris_outer", "eye_iris_ring", "eye_core",
            "jaw_guard_top", "jaw_guard_bot", "conning_tower", "clockwork_gear_l", "clockwork_gear_l_sp_0", "clockwork_gear_l_sp_1",
            "clockwork_gear_l_sp_2", "clockwork_gear_l_sp_3", "clockwork_gear_r", "clockwork_gear_r_sp_0", "clockwork_gear_r_sp_1", "clockwork_gear_r_sp_2",
            "clockwork_gear_r_sp_3", "periscope_head", "dorsal_bay_l", "dorsal_bay_r", "dorsal_railgun_base", "dorsal_railgun_barrel",
            "fin_pectoral_l", "wing_strut_l_f", "wing_strut_l_b", "ballast_vent_l", "vent_flap_l", "fin_pectoral_r",
            "wing_strut_r_f", "wing_strut_r_b", "ballast_vent_r", "vent_flap_r", "pod_left", "pod_hatch_l",
            "torpedo_tip_0", "torpedo_tip_1", "torpedo_tip_2", "pod_right", "pod_hatch_r", "torpedo_tip_3",
            "torpedo_tip_4", "torpedo_tip_5", "tail_cone", "rudder_top", "rudder_bot", "propeller_shroud",
            "shroud_seg_0", "shroud_seg_1", "shroud_seg_2", "shroud_seg_3", "shroud_seg_4", "shroud_seg_5",
            "shroud_seg_6", "shroud_seg_7", "shroud_seg_8", "shroud_seg_9", "shroud_seg_10", "shroud_seg_11",
            "shroud_strut_0", "shroud_strut_1", "shroud_strut_2", "shroud_strut_3", "propeller_shaft", "prop_blade_0",
            "prop_blade_1", "prop_blade_2", "prop_blade_3", "prop_blade_4", "prop_blade_5", "prop_blade_6",
            "prop_blade_7", "prop_blade_8", "prop_blade_9", "prop_blade_10", "prop_blade_11"
    };

    private final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();

    public RuinNautilusModel(ModelPart root) {
        this.root = root.getChild("root");
        collectBones("root", this.root);
    }

    private void collectBones(String name, ModelPart part) {
        bones.put(name, part);
        for (String childName : ALL_BONES) {
            if (part.hasChild(childName)) {
                collectBones(childName, part.getChild(childName));
            }
        }
    }

    public ModelPart getBone(String name) {
        return bones.get(name);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();

        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.000F, 2.000F, 0.000F));
        PartDefinition b_hull_pitch = b_root.addOrReplaceChild("hull_pitch", CubeListBuilder.create(), PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_hull_main = b_hull_pitch.addOrReplaceChild("hull_main", CubeListBuilder.create()
                .texOffs(1, 1).addBox(-10.000F, -7.000F, -14.000F, 20.0F, 14.0F, 30.0F)
                .texOffs(102, 1).addBox(-7.000F, -9.500F, -13.000F, 14.0F, 3.0F, 28.0F)
                .texOffs(321, 1).addBox(-7.000F, 6.500F, -12.000F, 14.0F, 3.0F, 26.0F)
                .texOffs(186, 46).addBox(-4.500F, 8.800F, -9.000F, 9.0F, 2.0F, 22.0F)
                .texOffs(466, 142).addBox(-8.000F, -9.200F, -14.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(1, 150).addBox(-8.000F, 7.200F, -14.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(337, 76).addBox(-11.400F, -7.000F, -14.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(346, 76).addBox(9.400F, -7.000F, -14.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(38, 150).addBox(-8.000F, -9.200F, -4.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(75, 150).addBox(-8.000F, 7.200F, -4.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(355, 76).addBox(-11.400F, -7.000F, -4.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(364, 76).addBox(9.400F, -7.000F, -4.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(112, 150).addBox(-8.000F, -9.200F, 6.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(149, 150).addBox(-8.000F, 7.200F, 6.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(373, 76).addBox(-11.400F, -7.000F, 6.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(382, 76).addBox(9.400F, -7.000F, 6.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(186, 150).addBox(-8.000F, -9.200F, 14.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(223, 150).addBox(-8.000F, 7.200F, 14.000F, 16.0F, 2.0F, 2.0F)
                .texOffs(391, 76).addBox(-11.400F, -7.000F, 14.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(400, 76).addBox(9.400F, -7.000F, 14.000F, 2.0F, 14.0F, 2.0F)
                .texOffs(245, 111).addBox(-11.600F, -3.000F, -10.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(87, 111).addBox(-11.600F, -2.000F, -11.000F, 2.0F, 4.0F, 6.0F)
                .texOffs(317, 123).addBox(-11.700F, -2.000F, -10.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(258, 111).addBox(-11.600F, -3.000F, -1.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(104, 111).addBox(-11.600F, -2.000F, -2.000F, 2.0F, 4.0F, 6.0F)
                .texOffs(328, 123).addBox(-11.700F, -2.000F, -1.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(249, 46).addBox(-7.500F, -10.300F, -8.000F, 1.0F, 1.0F, 20.0F)
                .texOffs(464, 76).addBox(-9.000F, -8.700F, -3.000F, 1.0F, 1.0F, 14.0F)
                .texOffs(282, 150).addBox(-8.800F, -10.000F, -8.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(291, 150).addBox(-8.800F, -10.000F, 1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(300, 150).addBox(-8.800F, -10.000F, 10.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(436, 142).addBox(-9.300F, -9.200F, -8.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(441, 142).addBox(-9.300F, -9.200F, 11.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(271, 111).addBox(9.600F, -3.000F, -10.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(121, 111).addBox(9.600F, -2.000F, -11.000F, 2.0F, 4.0F, 6.0F)
                .texOffs(339, 123).addBox(10.700F, -2.000F, -10.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(284, 111).addBox(9.600F, -3.000F, -1.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(138, 111).addBox(9.600F, -2.000F, -2.000F, 2.0F, 4.0F, 6.0F)
                .texOffs(350, 123).addBox(10.700F, -2.000F, -1.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(292, 46).addBox(6.500F, -10.300F, -8.000F, 1.0F, 1.0F, 20.0F)
                .texOffs(1, 95).addBox(8.000F, -8.700F, -3.000F, 1.0F, 1.0F, 14.0F)
                .texOffs(309, 150).addBox(6.800F, -10.000F, -8.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(318, 150).addBox(6.800F, -10.000F, 1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(327, 150).addBox(6.800F, -10.000F, 10.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(446, 142).addBox(8.300F, -9.200F, -8.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(451, 142).addBox(8.300F, -9.200F, 11.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(370, 142).addBox(-6.000F, -12.300F, 10.500F, 3.0F, 3.0F, 2.0F)
                .texOffs(39, 155).addBox(-5.500F, -11.800F, 9.900F, 2.0F, 2.0F, 1.0F)
                .texOffs(381, 142).addBox(3.000F, -12.300F, 10.500F, 3.0F, 3.0F, 2.0F)
                .texOffs(46, 155).addBox(3.500F, -11.800F, 9.900F, 2.0F, 2.0F, 1.0F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_hull_bevel_top_l = b_hull_main.addOrReplaceChild("hull_bevel_top_l", CubeListBuilder.create()
                .texOffs(187, 1).addBox(-2.500F, -1.500F, -14.000F, 5.0F, 3.0F, 28.0F, new CubeDeformation(-0.020F))
                .texOffs(64, 46).addBox(-1.000F, -2.000F, -14.000F, 2.0F, 1.0F, 28.0F)
                , PartPose.offsetAndRotation(-8.500F, -6.500F, 1.000F, -0.000F, -0.000F, 0.663F));
        PartDefinition b_hull_bevel_bot_l = b_hull_main.addOrReplaceChild("hull_bevel_bot_l", CubeListBuilder.create()
                .texOffs(402, 1).addBox(-2.500F, -1.500F, -13.000F, 5.0F, 3.0F, 26.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(-8.500F, 6.500F, 1.000F, -0.000F, -0.000F, -0.663F));
        PartDefinition b_hull_bevel_top_r = b_hull_main.addOrReplaceChild("hull_bevel_top_r", CubeListBuilder.create()
                .texOffs(254, 1).addBox(-2.500F, -1.500F, -14.000F, 5.0F, 3.0F, 28.0F, new CubeDeformation(-0.020F))
                .texOffs(125, 46).addBox(-1.000F, -2.000F, -14.000F, 2.0F, 1.0F, 28.0F)
                , PartPose.offsetAndRotation(8.500F, -6.500F, 1.000F, -0.000F, -0.000F, -0.663F));
        PartDefinition b_hull_bevel_bot_r = b_hull_main.addOrReplaceChild("hull_bevel_bot_r", CubeListBuilder.create()
                .texOffs(1, 46).addBox(-2.500F, -1.500F, -13.000F, 5.0F, 3.0F, 26.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(8.500F, 6.500F, 1.000F, -0.000F, -0.000F, 0.663F));
        PartDefinition b_gauge_front_tilt = b_hull_main.addOrReplaceChild("gauge_front_tilt", CubeListBuilder.create()
                .texOffs(392, 142).addBox(-1.500F, -1.500F, -1.000F, 3.0F, 3.0F, 2.0F)
                .texOffs(53, 155).addBox(-1.000F, -1.000F, -1.600F, 2.0F, 2.0F, 1.0F)
                , PartPose.offsetAndRotation(-6.000F, -9.500F, -8.500F, -0.436F, -0.000F, -0.436F));
        PartDefinition b_belly_gear = b_hull_main.addOrReplaceChild("belly_gear", CubeListBuilder.create()
                .texOffs(260, 150).addBox(-1.500F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                , PartPose.offset(0.000F, 10.200F, 5.000F));
        PartDefinition b_belly_gear_sp_0 = b_belly_gear.addOrReplaceChild("belly_gear_sp_0", CubeListBuilder.create()
                .texOffs(297, 111).addBox(-1.000F, -4.000F, -1.000F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_sp_1 = b_belly_gear.addOrReplaceChild("belly_gear_sp_1", CubeListBuilder.create()
                .texOffs(306, 111).addBox(-1.000F, -4.000F, -1.000F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_sp_2 = b_belly_gear.addOrReplaceChild("belly_gear_sp_2", CubeListBuilder.create()
                .texOffs(315, 111).addBox(-1.000F, -4.000F, -1.000F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_sp_3 = b_belly_gear.addOrReplaceChild("belly_gear_sp_3", CubeListBuilder.create()
                .texOffs(324, 111).addBox(-1.000F, -4.000F, -1.000F, 2.0F, 8.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2 = b_hull_main.addOrReplaceChild("belly_gear_2", CubeListBuilder.create()
                .texOffs(271, 150).addBox(-1.500F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                , PartPose.offset(0.000F, 10.000F, -3.500F));
        PartDefinition b_belly_gear_2_sp_0 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_sp_0", CubeListBuilder.create()
                .texOffs(383, 123).addBox(-1.000F, -3.000F, -1.000F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_sp_1 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_sp_1", CubeListBuilder.create()
                .texOffs(392, 123).addBox(-1.000F, -3.000F, -1.000F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_sp_2 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_sp_2", CubeListBuilder.create()
                .texOffs(401, 123).addBox(-1.000F, -3.000F, -1.000F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_sp_3 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_sp_3", CubeListBuilder.create()
                .texOffs(410, 123).addBox(-1.000F, -3.000F, -1.000F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_sub_cockpit_frame = b_hull_main.addOrReplaceChild("sub_cockpit_frame", CubeListBuilder.create()
                .texOffs(44, 76).addBox(-10.000F, -7.500F, -1.500F, 20.0F, 15.0F, 2.0F)
                .texOffs(237, 76).addBox(-8.000F, 4.200F, -14.500F, 16.0F, 2.0F, 14.0F)
                .texOffs(409, 76).addBox(-7.000F, 5.800F, -13.700F, 14.0F, 2.0F, 13.0F)
                .texOffs(82, 95).addBox(-6.000F, 7.200F, -12.000F, 12.0F, 2.0F, 11.0F)
                .texOffs(319, 95).addBox(-4.500F, 8.400F, -10.000F, 9.0F, 2.0F, 9.0F)
                .texOffs(84, 133).addBox(-3.000F, 9.900F, -7.500F, 6.0F, 1.0F, 6.0F)
                .texOffs(490, 150).addBox(-2.000F, 5.800F, -14.300F, 4.0F, 2.0F, 1.0F)
                .texOffs(343, 111).addBox(-7.500F, -8.200F, -7.500F, 15.0F, 2.0F, 7.0F)
                .texOffs(171, 123).addBox(-5.500F, -8.700F, -7.500F, 11.0F, 1.0F, 7.0F)
                .texOffs(189, 95).addBox(-8.100F, -6.800F, -2.500F, 1.0F, 12.0F, 1.0F)
                .texOffs(309, 95).addBox(-8.100F, -6.100F, -8.000F, 1.0F, 11.0F, 1.0F)
                .texOffs(18, 111).addBox(-7.600F, -4.800F, -12.300F, 1.0F, 10.0F, 1.0F)
                .texOffs(409, 111).addBox(-8.000F, -7.000F, -9.200F, 1.0F, 1.0F, 8.0F)
                .texOffs(259, 95).addBox(-8.000F, 4.100F, -12.000F, 1.0F, 1.0F, 11.0F)
                .texOffs(333, 111).addBox(-5.100F, -4.200F, -14.500F, 1.0F, 9.0F, 1.0F)
                .texOffs(194, 95).addBox(7.100F, -6.800F, -2.500F, 1.0F, 12.0F, 1.0F)
                .texOffs(314, 95).addBox(7.100F, -6.100F, -8.000F, 1.0F, 11.0F, 1.0F)
                .texOffs(23, 111).addBox(6.600F, -4.800F, -12.300F, 1.0F, 10.0F, 1.0F)
                .texOffs(428, 111).addBox(7.000F, -7.000F, -9.200F, 1.0F, 1.0F, 8.0F)
                .texOffs(284, 95).addBox(7.000F, 4.100F, -12.000F, 1.0F, 1.0F, 11.0F)
                .texOffs(338, 111).addBox(4.100F, -4.200F, -14.500F, 1.0F, 9.0F, 1.0F)
                .texOffs(171, 155).addBox(-5.000F, -4.100F, -14.700F, 10.0F, 1.0F, 1.0F)
                .texOffs(121, 155).addBox(-5.500F, -6.600F, -12.100F, 11.0F, 1.0F, 1.0F)
                .texOffs(146, 155).addBox(-5.500F, 4.100F, -14.700F, 11.0F, 1.0F, 1.0F)
                .texOffs(134, 133).addBox(-3.000F, 2.600F, -9.000F, 6.0F, 2.0F, 5.0F)
                .texOffs(433, 133).addBox(-3.000F, -2.200F, -4.700F, 6.0F, 6.0F, 1.0F)
                .texOffs(131, 142).addBox(-4.000F, 1.700F, -12.900F, 8.0F, 3.0F, 3.0F)
                .texOffs(475, 150).addBox(-2.500F, 1.100F, -12.400F, 5.0F, 1.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_cockpit_roof_brow = b_sub_cockpit_frame.addOrReplaceChild("cockpit_roof_brow", CubeListBuilder.create()
                .texOffs(46, 142).addBox(-6.500F, -1.000F, -4.000F, 13.0F, 2.0F, 4.0F)
                .texOffs(322, 142).addBox(-4.500F, -1.400F, -4.000F, 9.0F, 1.0F, 4.0F)
                , PartPose.offsetAndRotation(0.000F, -7.200F, -7.500F, -0.419F, -0.000F, 0.000F));
        PartDefinition b_rib_arch_side_l = b_sub_cockpit_frame.addOrReplaceChild("rib_arch_side_l", CubeListBuilder.create()
                .texOffs(302, 142).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(-6.500F, -4.800F, -11.000F, -0.593F, -0.489F, 0.000F));
        PartDefinition b_rib_ws_top_l = b_sub_cockpit_frame.addOrReplaceChild("rib_ws_top_l", CubeListBuilder.create()
                .texOffs(307, 142).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(-4.600F, -5.000F, -12.800F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_rib_arch_side_r = b_sub_cockpit_frame.addOrReplaceChild("rib_arch_side_r", CubeListBuilder.create()
                .texOffs(312, 142).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(6.500F, -4.800F, -11.000F, -0.593F, 0.489F, 0.000F));
        PartDefinition b_rib_ws_top_r = b_sub_cockpit_frame.addOrReplaceChild("rib_ws_top_r", CubeListBuilder.create()
                .texOffs(317, 142).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(4.600F, -5.000F, -12.800F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_control_lever_l = b_sub_cockpit_frame.addOrReplaceChild("control_lever_l", CubeListBuilder.create()
                .texOffs(456, 142).addBox(-0.500F, -3.700F, -0.500F, 1.0F, 4.0F, 1.0F)
                .texOffs(12, 155).addBox(-1.000F, -4.300F, -1.000F, 2.0F, 1.0F, 2.0F)
                , PartPose.offset(-3.000F, 1.900F, -10.400F));
        PartDefinition b_control_lever_r = b_sub_cockpit_frame.addOrReplaceChild("control_lever_r", CubeListBuilder.create()
                .texOffs(461, 142).addBox(-0.500F, -3.700F, -0.500F, 1.0F, 4.0F, 1.0F)
                .texOffs(21, 155).addBox(-1.000F, -4.300F, -1.000F, 2.0F, 1.0F, 2.0F)
                , PartPose.offset(3.000F, 1.900F, -10.400F));
        PartDefinition b_drowned_pilot = b_sub_cockpit_frame.addOrReplaceChild("drowned_pilot", CubeListBuilder.create()
                .texOffs(227, 123).addBox(-2.500F, -5.300F, -1.500F, 5.0F, 5.0F, 3.0F)
                .texOffs(30, 155).addBox(-1.500F, -4.000F, -2.100F, 3.0F, 2.0F, 1.0F)
                .texOffs(250, 142).addBox(-2.400F, -1.200F, -3.700F, 2.0F, 2.0F, 4.0F)
                .texOffs(263, 142).addBox(0.400F, -1.200F, -3.700F, 2.0F, 2.0F, 4.0F)
                , PartPose.offset(0.000F, 2.800F, -6.500F));
        PartDefinition b_drowned_head = b_drowned_pilot.addOrReplaceChild("drowned_head", CubeListBuilder.create()
                .texOffs(28, 111).addBox(-2.500F, -4.300F, -2.500F, 5.0F, 5.0F, 5.0F)
                .texOffs(109, 133).addBox(-3.000F, -0.200F, -3.000F, 6.0F, 1.0F, 6.0F)
                .texOffs(336, 150).addBox(-1.500F, -3.400F, -3.100F, 3.0F, 3.0F, 1.0F)
                , PartPose.offset(0.000F, -5.400F, 0.000F));
        PartDefinition b_drowned_arm_l = b_drowned_pilot.addOrReplaceChild("drowned_arm_l", CubeListBuilder.create()
                .texOffs(448, 133).addBox(-1.100F, -1.000F, -4.800F, 2.0F, 2.0F, 5.0F)
                , PartPose.offset(-3.000F, -4.200F, 0.000F));
        PartDefinition b_drowned_arm_r = b_drowned_pilot.addOrReplaceChild("drowned_arm_r", CubeListBuilder.create()
                .texOffs(463, 133).addBox(-0.900F, -1.000F, -4.800F, 2.0F, 2.0F, 5.0F)
                , PartPose.offset(3.000F, -4.200F, 0.000F));
        PartDefinition b_sub_cockpit_glass = b_hull_main.addOrReplaceChild("sub_cockpit_glass", CubeListBuilder.create()
                .texOffs(388, 111).addBox(-4.500F, -3.500F, -14.500F, 9.0F, 8.0F, 1.0F)
                .texOffs(417, 46).addBox(-7.800F, -5.600F, -11.500F, 1.0F, 10.0F, 10.0F)
                .texOffs(440, 46).addBox(6.800F, -5.600F, -11.500F, 1.0F, 10.0F, 10.0F)
                , PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_glass_front_curve_top = b_sub_cockpit_glass.addOrReplaceChild("glass_front_curve_top", CubeListBuilder.create()
                .texOffs(349, 142).addBox(-4.500F, -3.700F, 0.300F, 9.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -3.500F, -14.000F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_glass_cheek_l = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_l", CubeListBuilder.create()
                .texOffs(35, 123).addBox(-2.000F, -4.000F, -0.500F, 4.0F, 8.0F, 1.0F)
                , PartPose.offsetAndRotation(-5.800F, 0.500F, -12.800F, -0.000F, -0.628F, 0.000F));
        PartDefinition b_glass_cheek_top_l = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_top_l", CubeListBuilder.create()
                .texOffs(403, 142).addBox(-2.000F, -2.000F, -0.500F, 4.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(-5.600F, -4.800F, -12.000F, -0.593F, -0.524F, 0.000F));
        PartDefinition b_glass_cheek_r = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_r", CubeListBuilder.create()
                .texOffs(46, 123).addBox(-2.000F, -4.000F, -0.500F, 4.0F, 8.0F, 1.0F)
                , PartPose.offsetAndRotation(5.800F, 0.500F, -12.800F, -0.000F, 0.628F, 0.000F));
        PartDefinition b_glass_cheek_top_r = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_top_r", CubeListBuilder.create()
                .texOffs(414, 142).addBox(-2.000F, -2.000F, -0.500F, 4.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(5.600F, -4.800F, -12.000F, -0.593F, 0.524F, 0.000F));
        PartDefinition b_harpoon_arm_l = b_hull_main.addOrReplaceChild("harpoon_arm_l", CubeListBuilder.create()
                .texOffs(276, 142).addBox(-2.100F, -1.500F, -1.500F, 3.0F, 3.0F, 3.0F)
                .texOffs(49, 111).addBox(-2.000F, -1.500F, -7.500F, 2.0F, 3.0F, 7.0F)
                .texOffs(478, 133).addBox(-1.500F, 0.900F, -7.000F, 1.0F, 1.0F, 6.0F)
                , PartPose.offset(-6.200F, 6.400F, -18.500F));
        PartDefinition b_harpoon_claw_l = b_harpoon_arm_l.addOrReplaceChild("harpoon_claw_l", CubeListBuilder.create()
                .texOffs(261, 123).addBox(-1.500F, -2.000F, -3.500F, 3.0F, 4.0F, 4.0F)
                , PartPose.offset(-1.000F, -0.000F, -7.500F));
        PartDefinition b_claw_jaw_top_l = b_harpoon_claw_l.addOrReplaceChild("claw_jaw_top_l", CubeListBuilder.create()
                .texOffs(175, 142).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -1.400F, -3.200F));
        PartDefinition b_claw_jaw_bot_l = b_harpoon_claw_l.addOrReplaceChild("claw_jaw_bot_l", CubeListBuilder.create()
                .texOffs(190, 142).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, 1.400F, -3.200F));
        PartDefinition b_harpoon_arm_r = b_hull_main.addOrReplaceChild("harpoon_arm_r", CubeListBuilder.create()
                .texOffs(289, 142).addBox(-0.900F, -1.500F, -1.500F, 3.0F, 3.0F, 3.0F)
                .texOffs(68, 111).addBox(0.000F, -1.500F, -7.500F, 2.0F, 3.0F, 7.0F)
                .texOffs(493, 133).addBox(0.500F, 0.900F, -7.000F, 1.0F, 1.0F, 6.0F)
                , PartPose.offset(6.200F, 6.400F, -18.500F));
        PartDefinition b_harpoon_claw_r = b_harpoon_arm_r.addOrReplaceChild("harpoon_claw_r", CubeListBuilder.create()
                .texOffs(276, 123).addBox(-1.500F, -2.000F, -3.500F, 3.0F, 4.0F, 4.0F)
                , PartPose.offset(1.000F, -0.000F, -7.500F));
        PartDefinition b_claw_jaw_top_r = b_harpoon_claw_r.addOrReplaceChild("claw_jaw_top_r", CubeListBuilder.create()
                .texOffs(205, 142).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -1.400F, -3.200F));
        PartDefinition b_claw_jaw_bot_r = b_harpoon_claw_r.addOrReplaceChild("claw_jaw_bot_r", CubeListBuilder.create()
                .texOffs(220, 142).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, 1.400F, -3.200F));
        PartDefinition b_bow_dome = b_hull_main.addOrReplaceChild("bow_dome", CubeListBuilder.create(), PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_eye_iris_outer = b_bow_dome.addOrReplaceChild("eye_iris_outer", CubeListBuilder.create(), PartPose.offset(0.000F, 6.800F, -13.800F));
        PartDefinition b_eye_iris_ring = b_bow_dome.addOrReplaceChild("eye_iris_ring", CubeListBuilder.create(), PartPose.offset(0.000F, 6.800F, -13.900F));
        PartDefinition b_eye_core = b_bow_dome.addOrReplaceChild("eye_core", CubeListBuilder.create()
                .texOffs(501, 150).addBox(-2.000F, -1.000F, -0.500F, 4.0F, 2.0F, 1.0F)
                , PartPose.offset(0.000F, 6.800F, -14.000F));
        PartDefinition b_jaw_guard_top = b_bow_dome.addOrReplaceChild("jaw_guard_top", CubeListBuilder.create(), PartPose.offset(0.000F, -7.200F, -6.000F));
        PartDefinition b_jaw_guard_bot = b_bow_dome.addOrReplaceChild("jaw_guard_bot", CubeListBuilder.create(), PartPose.offset(0.000F, 9.000F, -6.000F));
        PartDefinition b_conning_tower = b_hull_main.addOrReplaceChild("conning_tower", CubeListBuilder.create()
                .texOffs(298, 76).addBox(-4.000F, -5.000F, -5.500F, 8.0F, 5.0F, 11.0F)
                .texOffs(129, 95).addBox(-4.500F, -5.800F, -6.000F, 9.0F, 1.0F, 12.0F)
                .texOffs(1, 155).addBox(-2.000F, -3.700F, -6.100F, 4.0F, 2.0F, 1.0F)
                .texOffs(244, 123).addBox(-2.000F, -9.000F, 1.000F, 4.0F, 4.0F, 4.0F)
                .texOffs(154, 142).addBox(-2.500F, -9.800F, 0.500F, 5.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -9.500F, -1.000F));
        PartDefinition b_clockwork_gear_l = b_conning_tower.addOrReplaceChild("clockwork_gear_l", CubeListBuilder.create()
                .texOffs(345, 150).addBox(-1.000F, -1.000F, -1.000F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-4.800F, -2.700F, 4.500F));
        PartDefinition b_clockwork_gear_l_sp_0 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_sp_0", CubeListBuilder.create()
                .texOffs(28, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_sp_1 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_sp_1", CubeListBuilder.create()
                .texOffs(35, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_sp_2 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_sp_2", CubeListBuilder.create()
                .texOffs(42, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_sp_3 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_sp_3", CubeListBuilder.create()
                .texOffs(49, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r = b_conning_tower.addOrReplaceChild("clockwork_gear_r", CubeListBuilder.create()
                .texOffs(354, 150).addBox(-1.000F, -1.000F, -1.000F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(4.800F, -2.700F, 4.500F));
        PartDefinition b_clockwork_gear_r_sp_0 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_sp_0", CubeListBuilder.create()
                .texOffs(56, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_sp_1 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_sp_1", CubeListBuilder.create()
                .texOffs(63, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_sp_2 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_sp_2", CubeListBuilder.create()
                .texOffs(70, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_sp_3 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_sp_3", CubeListBuilder.create()
                .texOffs(77, 133).addBox(-0.500F, -3.000F, -1.000F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_periscope_head = b_conning_tower.addOrReplaceChild("periscope_head", CubeListBuilder.create()
                .texOffs(419, 123).addBox(-1.000F, -5.800F, -1.000F, 2.0F, 6.0F, 2.0F)
                .texOffs(1, 142).addBox(-1.500F, -7.500F, -2.500F, 3.0F, 3.0F, 4.0F)
                .texOffs(60, 155).addBox(-1.000F, -7.000F, -3.100F, 2.0F, 2.0F, 1.0F)
                , PartPose.offset(0.000F, -5.700F, -2.500F));
        PartDefinition b_dorsal_bay_l = b_hull_main.addOrReplaceChild("dorsal_bay_l", CubeListBuilder.create()
                .texOffs(356, 95).addBox(-0.200F, -0.900F, -5.000F, 5.0F, 1.0F, 10.0F)
                , PartPose.offset(-4.500F, -9.200F, 8.500F));
        PartDefinition b_dorsal_bay_r = b_hull_main.addOrReplaceChild("dorsal_bay_r", CubeListBuilder.create()
                .texOffs(387, 95).addBox(-4.800F, -0.900F, -5.000F, 5.0F, 1.0F, 10.0F)
                , PartPose.offset(4.500F, -9.200F, 8.500F));
        PartDefinition b_dorsal_railgun_base = b_hull_main.addOrReplaceChild("dorsal_railgun_base", CubeListBuilder.create()
                .texOffs(418, 95).addBox(-3.000F, -3.700F, -3.000F, 6.0F, 5.0F, 6.0F)
                .texOffs(208, 123).addBox(-2.000F, -5.000F, -2.500F, 4.0F, 3.0F, 5.0F)
                , PartPose.offset(0.000F, -2.000F, 8.500F));
        PartDefinition b_dorsal_railgun_barrel = b_dorsal_railgun_base.addOrReplaceChild("dorsal_railgun_barrel", CubeListBuilder.create()
                .texOffs(230, 95).addBox(-3.000F, -2.000F, -6.000F, 6.0F, 4.0F, 8.0F)
                .texOffs(335, 46).addBox(-3.500F, -1.500F, -22.500F, 2.0F, 3.0F, 18.0F)
                .texOffs(376, 46).addBox(1.500F, -1.500F, -22.500F, 2.0F, 3.0F, 18.0F)
                .texOffs(463, 46).addBox(-1.000F, -1.000F, -21.500F, 2.0F, 2.0F, 17.0F)
                , PartPose.offset(0.000F, -4.500F, 0.000F));
        PartDefinition b_fin_pectoral_l = b_hull_main.addOrReplaceChild("fin_pectoral_l", CubeListBuilder.create()
                .texOffs(93, 123).addBox(-12.100F, -0.500F, -3.500F, 12.0F, 1.0F, 7.0F)
                .texOffs(417, 150).addBox(-12.600F, -1.000F, -4.000F, 13.0F, 2.0F, 1.0F)
                .texOffs(67, 155).addBox(-12.100F, -0.500F, 3.000F, 12.0F, 1.0F, 1.0F)
                .texOffs(1, 123).addBox(-12.900F, -1.000F, -3.500F, 1.0F, 2.0F, 7.0F)
                .texOffs(16, 142).addBox(-1.400F, -3.800F, -2.500F, 2.0F, 2.0F, 5.0F)
                , PartPose.offsetAndRotation(-10.400F, 4.000F, 0.000F, -0.000F, -0.244F, -0.105F));
        PartDefinition b_wing_strut_l_f = b_fin_pectoral_l.addOrReplaceChild("wing_strut_l_f", CubeListBuilder.create()
                .texOffs(194, 155).addBox(-6.100F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(-0.800F, -2.800F, -1.600F, -0.000F, -0.000F, 0.628F));
        PartDefinition b_wing_strut_l_b = b_fin_pectoral_l.addOrReplaceChild("wing_strut_l_b", CubeListBuilder.create()
                .texOffs(211, 155).addBox(-6.100F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(-0.800F, -2.800F, 1.600F, -0.000F, -0.000F, 0.628F));
        PartDefinition b_ballast_vent_l = b_hull_main.addOrReplaceChild("ballast_vent_l", CubeListBuilder.create()
                .texOffs(443, 95).addBox(-2.000F, -2.000F, -3.500F, 2.0F, 4.0F, 7.0F)
                .texOffs(291, 123).addBox(-2.200F, -1.500F, -2.500F, 1.0F, 3.0F, 5.0F)
                , PartPose.offset(-9.500F, -4.800F, 10.000F));
        PartDefinition b_vent_flap_l = b_ballast_vent_l.addOrReplaceChild("vent_flap_l", CubeListBuilder.create()
                .texOffs(481, 95).addBox(-0.900F, -0.000F, -3.500F, 1.0F, 4.0F, 7.0F)
                , PartPose.offset(-1.900F, -2.000F, 0.000F));
        PartDefinition b_fin_pectoral_r = b_hull_main.addOrReplaceChild("fin_pectoral_r", CubeListBuilder.create()
                .texOffs(132, 123).addBox(0.100F, -0.500F, -3.500F, 12.0F, 1.0F, 7.0F)
                .texOffs(446, 150).addBox(-0.400F, -1.000F, -4.000F, 13.0F, 2.0F, 1.0F)
                .texOffs(94, 155).addBox(0.100F, -0.500F, 3.000F, 12.0F, 1.0F, 1.0F)
                .texOffs(18, 123).addBox(11.900F, -1.000F, -3.500F, 1.0F, 2.0F, 7.0F)
                .texOffs(31, 142).addBox(-0.600F, -3.800F, -2.500F, 2.0F, 2.0F, 5.0F)
                , PartPose.offsetAndRotation(10.400F, 4.000F, 0.000F, -0.000F, 0.244F, 0.105F));
        PartDefinition b_wing_strut_r_f = b_fin_pectoral_r.addOrReplaceChild("wing_strut_r_f", CubeListBuilder.create()
                .texOffs(228, 155).addBox(-0.900F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.800F, -2.800F, -1.600F, -0.000F, -0.000F, -0.628F));
        PartDefinition b_wing_strut_r_b = b_fin_pectoral_r.addOrReplaceChild("wing_strut_r_b", CubeListBuilder.create()
                .texOffs(245, 155).addBox(-0.900F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.800F, -2.800F, 1.600F, -0.000F, -0.000F, -0.628F));
        PartDefinition b_ballast_vent_r = b_hull_main.addOrReplaceChild("ballast_vent_r", CubeListBuilder.create()
                .texOffs(462, 95).addBox(0.000F, -2.000F, -3.500F, 2.0F, 4.0F, 7.0F)
                .texOffs(304, 123).addBox(1.200F, -1.500F, -2.500F, 1.0F, 3.0F, 5.0F)
                , PartPose.offset(9.500F, -4.800F, 10.000F));
        PartDefinition b_vent_flap_r = b_ballast_vent_r.addOrReplaceChild("vent_flap_r", CubeListBuilder.create()
                .texOffs(1, 111).addBox(-0.100F, -0.000F, -3.500F, 1.0F, 4.0F, 7.0F)
                , PartPose.offset(1.900F, -2.000F, 0.000F));
        PartDefinition b_pod_left = b_hull_main.addOrReplaceChild("pod_left", CubeListBuilder.create()
                .texOffs(89, 76).addBox(-4.200F, -3.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(126, 76).addBox(-4.200F, 0.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(155, 111).addBox(-4.700F, -4.000F, -6.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(170, 111).addBox(-4.700F, -4.000F, -1.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(185, 111).addBox(-4.700F, -4.000F, 4.000F, 5.0F, 8.0F, 2.0F)
                , PartPose.offset(-10.400F, -0.000F, 8.000F));
        PartDefinition b_pod_hatch_l = b_pod_left.addOrReplaceChild("pod_hatch_l", CubeListBuilder.create()
                .texOffs(361, 123).addBox(0.200F, -3.500F, -0.800F, 4.0F, 7.0F, 1.0F)
                , PartPose.offset(-4.400F, -0.000F, -7.200F));
        PartDefinition b_torpedo_tip_0 = b_pod_left.addOrReplaceChild("torpedo_tip_0", CubeListBuilder.create()
                .texOffs(363, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, 1.800F, -6.000F));
        PartDefinition b_torpedo_tip_1 = b_pod_left.addOrReplaceChild("torpedo_tip_1", CubeListBuilder.create()
                .texOffs(372, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, -0.000F, -6.000F));
        PartDefinition b_torpedo_tip_2 = b_pod_left.addOrReplaceChild("torpedo_tip_2", CubeListBuilder.create()
                .texOffs(381, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, -1.800F, -6.000F));
        PartDefinition b_pod_right = b_hull_main.addOrReplaceChild("pod_right", CubeListBuilder.create()
                .texOffs(163, 76).addBox(0.200F, -3.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(200, 76).addBox(0.200F, 0.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(200, 111).addBox(-0.300F, -4.000F, -6.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(215, 111).addBox(-0.300F, -4.000F, -1.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(230, 111).addBox(-0.300F, -4.000F, 4.000F, 5.0F, 8.0F, 2.0F)
                , PartPose.offset(10.400F, -0.000F, 8.000F));
        PartDefinition b_pod_hatch_r = b_pod_right.addOrReplaceChild("pod_hatch_r", CubeListBuilder.create()
                .texOffs(372, 123).addBox(-4.200F, -3.500F, -0.800F, 4.0F, 7.0F, 1.0F)
                , PartPose.offset(4.400F, -0.000F, -7.200F));
        PartDefinition b_torpedo_tip_3 = b_pod_right.addOrReplaceChild("torpedo_tip_3", CubeListBuilder.create()
                .texOffs(390, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, 1.800F, -6.000F));
        PartDefinition b_torpedo_tip_4 = b_pod_right.addOrReplaceChild("torpedo_tip_4", CubeListBuilder.create()
                .texOffs(399, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, -0.000F, -6.000F));
        PartDefinition b_torpedo_tip_5 = b_pod_right.addOrReplaceChild("torpedo_tip_5", CubeListBuilder.create()
                .texOffs(408, 150).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, -1.800F, -6.000F));
        PartDefinition b_tail_cone = b_hull_main.addOrReplaceChild("tail_cone", CubeListBuilder.create()
                .texOffs(1, 76).addBox(-7.000F, -5.500F, -0.500F, 14.0F, 11.0F, 7.0F)
                .texOffs(199, 95).addBox(-6.500F, -5.000F, 5.500F, 13.0F, 10.0F, 2.0F)
                .texOffs(51, 95).addBox(-4.500F, -4.000F, 6.500F, 9.0F, 8.0F, 6.0F)
                .texOffs(447, 111).addBox(-6.700F, -1.500F, 6.500F, 3.0F, 3.0F, 6.0F)
                .texOffs(81, 142).addBox(-12.000F, -0.500F, 4.500F, 7.0F, 1.0F, 5.0F)
                .texOffs(466, 111).addBox(3.700F, -1.500F, 6.500F, 3.0F, 3.0F, 6.0F)
                .texOffs(106, 142).addBox(5.000F, -0.500F, 4.500F, 7.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -0.000F, 16.000F));
        PartDefinition b_rudder_top = b_tail_cone.addOrReplaceChild("rudder_top", CubeListBuilder.create()
                .texOffs(32, 95).addBox(-1.000F, -8.000F, -1.500F, 2.0F, 8.0F, 7.0F)
                .texOffs(235, 142).addBox(-1.000F, -8.600F, 0.500F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -5.500F, 4.500F));
        PartDefinition b_rudder_bot = b_tail_cone.addOrReplaceChild("rudder_bot", CubeListBuilder.create()
                .texOffs(172, 95).addBox(-1.000F, -0.000F, -1.000F, 2.0F, 7.0F, 6.0F)
                , PartPose.offset(0.000F, 5.500F, 4.500F));
        PartDefinition b_propeller_shroud = b_tail_cone.addOrReplaceChild("propeller_shroud", CubeListBuilder.create(), PartPose.offset(0.000F, -0.000F, 14.500F));
        PartDefinition b_shroud_seg_0 = b_propeller_shroud.addOrReplaceChild("shroud_seg_0", CubeListBuilder.create()
                .texOffs(157, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_shroud_seg_1 = b_propeller_shroud.addOrReplaceChild("shroud_seg_1", CubeListBuilder.create()
                .texOffs(180, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 0.524F));
        PartDefinition b_shroud_seg_2 = b_propeller_shroud.addOrReplaceChild("shroud_seg_2", CubeListBuilder.create()
                .texOffs(203, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 1.047F));
        PartDefinition b_shroud_seg_3 = b_propeller_shroud.addOrReplaceChild("shroud_seg_3", CubeListBuilder.create()
                .texOffs(226, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 1.571F));
        PartDefinition b_shroud_seg_4 = b_propeller_shroud.addOrReplaceChild("shroud_seg_4", CubeListBuilder.create()
                .texOffs(249, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 2.094F));
        PartDefinition b_shroud_seg_5 = b_propeller_shroud.addOrReplaceChild("shroud_seg_5", CubeListBuilder.create()
                .texOffs(272, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 2.618F));
        PartDefinition b_shroud_seg_6 = b_propeller_shroud.addOrReplaceChild("shroud_seg_6", CubeListBuilder.create()
                .texOffs(295, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 3.142F));
        PartDefinition b_shroud_seg_7 = b_propeller_shroud.addOrReplaceChild("shroud_seg_7", CubeListBuilder.create()
                .texOffs(318, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 3.665F));
        PartDefinition b_shroud_seg_8 = b_propeller_shroud.addOrReplaceChild("shroud_seg_8", CubeListBuilder.create()
                .texOffs(341, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 4.189F));
        PartDefinition b_shroud_seg_9 = b_propeller_shroud.addOrReplaceChild("shroud_seg_9", CubeListBuilder.create()
                .texOffs(364, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 4.712F));
        PartDefinition b_shroud_seg_10 = b_propeller_shroud.addOrReplaceChild("shroud_seg_10", CubeListBuilder.create()
                .texOffs(387, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 5.236F));
        PartDefinition b_shroud_seg_11 = b_propeller_shroud.addOrReplaceChild("shroud_seg_11", CubeListBuilder.create()
                .texOffs(410, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 5.760F));
        PartDefinition b_shroud_strut_0 = b_propeller_shroud.addOrReplaceChild("shroud_strut_0", CubeListBuilder.create()
                .texOffs(57, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, -1.500F));
        PartDefinition b_shroud_strut_1 = b_propeller_shroud.addOrReplaceChild("shroud_strut_1", CubeListBuilder.create()
                .texOffs(66, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 1.571F));
        PartDefinition b_shroud_strut_2 = b_propeller_shroud.addOrReplaceChild("shroud_strut_2", CubeListBuilder.create()
                .texOffs(75, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 3.142F));
        PartDefinition b_shroud_strut_3 = b_propeller_shroud.addOrReplaceChild("shroud_strut_3", CubeListBuilder.create()
                .texOffs(84, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 4.712F));
        PartDefinition b_propeller_shaft = b_tail_cone.addOrReplaceChild("propeller_shaft", CubeListBuilder.create()
                .texOffs(485, 111).addBox(-2.000F, -2.000F, -2.500F, 4.0F, 4.0F, 5.0F)
                .texOffs(425, 142).addBox(-1.500F, -1.500F, 1.700F, 3.0F, 3.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, 14.500F));
        PartDefinition b_prop_blade_0 = b_propeller_shaft.addOrReplaceChild("prop_blade_0", CubeListBuilder.create()
                .texOffs(428, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 0.000F));
        PartDefinition b_prop_blade_1 = b_propeller_shaft.addOrReplaceChild("prop_blade_1", CubeListBuilder.create()
                .texOffs(437, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 0.524F));
        PartDefinition b_prop_blade_2 = b_propeller_shaft.addOrReplaceChild("prop_blade_2", CubeListBuilder.create()
                .texOffs(446, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 1.047F));
        PartDefinition b_prop_blade_3 = b_propeller_shaft.addOrReplaceChild("prop_blade_3", CubeListBuilder.create()
                .texOffs(455, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 1.571F));
        PartDefinition b_prop_blade_4 = b_propeller_shaft.addOrReplaceChild("prop_blade_4", CubeListBuilder.create()
                .texOffs(464, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 2.094F));
        PartDefinition b_prop_blade_5 = b_propeller_shaft.addOrReplaceChild("prop_blade_5", CubeListBuilder.create()
                .texOffs(473, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 2.618F));
        PartDefinition b_prop_blade_6 = b_propeller_shaft.addOrReplaceChild("prop_blade_6", CubeListBuilder.create()
                .texOffs(482, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 3.142F));
        PartDefinition b_prop_blade_7 = b_propeller_shaft.addOrReplaceChild("prop_blade_7", CubeListBuilder.create()
                .texOffs(491, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 3.665F));
        PartDefinition b_prop_blade_8 = b_propeller_shaft.addOrReplaceChild("prop_blade_8", CubeListBuilder.create()
                .texOffs(500, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 4.189F));
        PartDefinition b_prop_blade_9 = b_propeller_shaft.addOrReplaceChild("prop_blade_9", CubeListBuilder.create()
                .texOffs(1, 133).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 4.712F));
        PartDefinition b_prop_blade_10 = b_propeller_shaft.addOrReplaceChild("prop_blade_10", CubeListBuilder.create()
                .texOffs(10, 133).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 5.236F));
        PartDefinition b_prop_blade_11 = b_propeller_shaft.addOrReplaceChild("prop_blade_11", CubeListBuilder.create()
                .texOffs(19, 133).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.020F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 5.760F));

        return LayerDefinition.create(mesh, 512, 512);
    }

    public void resetPose() {
        for (ModelPart p : bones.values()) {
            p.resetPose();
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
