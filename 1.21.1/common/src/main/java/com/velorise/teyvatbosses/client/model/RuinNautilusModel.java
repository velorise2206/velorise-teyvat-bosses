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
 * <p>Auto-generated 1:1: 296 cubes, 135 bones, 512x512 non-overlapping Box UVs.
 */
public class RuinNautilusModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("ruin_nautilus"), "main");

    public static final String[] ALL_BONES = {
            "root", "hull_pitch", "hull_main", "hull_bevel_top_l", "hull_bevel_bot_l", "hull_bevel_top_r",
            "hull_bevel_bot_r", "gauge_front_tilt", "belly_gear", "belly_gear_t_0", "belly_gear_t_1", "belly_gear_t_2",
            "belly_gear_t_3", "belly_gear_t_4", "belly_gear_t_5", "belly_gear_t_6", "belly_gear_t_7", "belly_gear_2",
            "belly_gear_2_t_0", "belly_gear_2_t_1", "belly_gear_2_t_2", "belly_gear_2_t_3", "belly_gear_2_t_4", "belly_gear_2_t_5",
            "belly_gear_2_t_6", "belly_gear_2_t_7", "sub_cockpit_frame", "cockpit_roof_brow", "rib_arch_side_l", "rib_ws_top_l",
            "rib_arch_side_r", "rib_ws_top_r", "control_lever_l", "control_lever_r", "sub_cockpit_glass", "glass_front_curve_top",
            "glass_cheek_l", "glass_cheek_top_l", "glass_cheek_r", "glass_cheek_top_r", "drowned_pilot", "drowned_head",
            "drowned_arm_l", "drowned_arm_r", "harpoon_arm_l", "harpoon_claw_l", "claw_jaw_top_l", "claw_jaw_bot_l",
            "harpoon_arm_r", "harpoon_claw_r", "claw_jaw_top_r", "claw_jaw_bot_r", "bow_dome", "eye_iris_outer",
            "eye_iris_ring", "eye_core", "jaw_guard_top", "jaw_guard_bot", "conning_tower", "clockwork_gear_l",
            "clockwork_gear_l_t_0", "clockwork_gear_l_t_1", "clockwork_gear_l_t_2", "clockwork_gear_l_t_3", "clockwork_gear_l_t_4", "clockwork_gear_l_t_5",
            "clockwork_gear_l_t_6", "clockwork_gear_l_t_7", "clockwork_gear_r", "clockwork_gear_r_t_0", "clockwork_gear_r_t_1", "clockwork_gear_r_t_2",
            "clockwork_gear_r_t_3", "clockwork_gear_r_t_4", "clockwork_gear_r_t_5", "clockwork_gear_r_t_6", "clockwork_gear_r_t_7", "periscope_head",
            "dorsal_bay_l", "dorsal_bay_r", "dorsal_railgun_base", "dorsal_railgun_barrel", "fin_pectoral_l", "wing_strut_l_f",
            "wing_strut_l_b", "ballast_vent_l", "vent_flap_l", "fin_pectoral_r", "wing_strut_r_f", "wing_strut_r_b",
            "ballast_vent_r", "vent_flap_r", "pod_left", "pod_hatch_l", "torpedo_tip_0", "torpedo_tip_1",
            "torpedo_tip_2", "pod_right", "pod_hatch_r", "torpedo_tip_3", "torpedo_tip_4", "torpedo_tip_5",
            "tail_cone", "rudder_top", "rudder_bot", "propeller_shroud", "shroud_seg_0", "shroud_seg_1",
            "shroud_seg_2", "shroud_seg_3", "shroud_seg_4", "shroud_seg_5", "shroud_seg_6", "shroud_seg_7",
            "shroud_seg_8", "shroud_seg_9", "shroud_seg_10", "shroud_seg_11", "shroud_strut_0", "shroud_strut_1",
            "shroud_strut_2", "shroud_strut_3", "propeller_shaft", "prop_blade_0", "prop_blade_1", "prop_blade_2",
            "prop_blade_3", "prop_blade_4", "prop_blade_5", "prop_blade_6", "prop_blade_7", "prop_blade_8",
            "prop_blade_9", "prop_blade_10", "prop_blade_11"
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
                .texOffs(438, 141).addBox(-8.000F, -9.200F, -14.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(475, 141).addBox(-8.000F, 7.200F, -14.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(337, 76).addBox(-11.400F, -7.000F, -14.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(346, 76).addBox(9.400F, -7.000F, -14.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(1, 148).addBox(-8.000F, -9.200F, -4.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(38, 148).addBox(-8.000F, 7.200F, -4.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(355, 76).addBox(-11.400F, -7.000F, -4.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(364, 76).addBox(9.400F, -7.000F, -4.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(75, 148).addBox(-8.000F, -9.200F, 6.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(112, 148).addBox(-8.000F, 7.200F, 6.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(373, 76).addBox(-11.400F, -7.000F, 6.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(382, 76).addBox(9.400F, -7.000F, 6.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(149, 148).addBox(-8.000F, -9.200F, 14.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(186, 148).addBox(-8.000F, 7.200F, 14.000F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.065F))
                .texOffs(391, 76).addBox(-11.400F, -7.000F, 14.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(400, 76).addBox(9.400F, -7.000F, 14.000F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.040F))
                .texOffs(245, 111).addBox(-11.600F, -3.000F, -10.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(87, 111).addBox(-11.600F, -2.000F, -11.000F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.030F))
                .texOffs(283, 123).addBox(-11.700F, -2.000F, -10.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(258, 111).addBox(-11.600F, -3.000F, -1.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(104, 111).addBox(-11.600F, -2.000F, -2.000F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.030F))
                .texOffs(294, 123).addBox(-11.700F, -2.000F, -1.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(249, 46).addBox(-7.500F, -10.300F, -8.000F, 1.0F, 1.0F, 20.0F)
                .texOffs(464, 76).addBox(-9.000F, -8.700F, -3.000F, 1.0F, 1.0F, 14.0F)
                .texOffs(333, 148).addBox(-8.800F, -10.000F, -8.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(342, 148).addBox(-8.800F, -10.000F, 1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(351, 148).addBox(-8.800F, -10.000F, 10.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(398, 141).addBox(-9.300F, -9.200F, -8.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(403, 141).addBox(-9.300F, -9.200F, 11.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(271, 111).addBox(9.600F, -3.000F, -10.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(121, 111).addBox(9.600F, -2.000F, -11.000F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.030F))
                .texOffs(305, 123).addBox(10.700F, -2.000F, -10.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(284, 111).addBox(9.600F, -3.000F, -1.000F, 2.0F, 6.0F, 4.0F)
                .texOffs(138, 111).addBox(9.600F, -2.000F, -2.000F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.030F))
                .texOffs(316, 123).addBox(10.700F, -2.000F, -1.000F, 1.0F, 4.0F, 4.0F)
                .texOffs(292, 46).addBox(6.500F, -10.300F, -8.000F, 1.0F, 1.0F, 20.0F)
                .texOffs(1, 95).addBox(8.000F, -8.700F, -3.000F, 1.0F, 1.0F, 14.0F)
                .texOffs(360, 148).addBox(6.800F, -10.000F, -8.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(369, 148).addBox(6.800F, -10.000F, 1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(378, 148).addBox(6.800F, -10.000F, 10.500F, 2.0F, 2.0F, 2.0F)
                .texOffs(408, 141).addBox(8.300F, -9.200F, -8.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(413, 141).addBox(8.300F, -9.200F, 11.000F, 1.0F, 4.0F, 1.0F)
                .texOffs(303, 141).addBox(-6.000F, -12.300F, 10.500F, 3.0F, 3.0F, 2.0F)
                .texOffs(177, 153).addBox(-5.500F, -11.800F, 9.900F, 2.0F, 2.0F, 1.0F)
                .texOffs(314, 141).addBox(3.000F, -12.300F, 10.500F, 3.0F, 3.0F, 2.0F)
                .texOffs(184, 153).addBox(3.500F, -11.800F, 9.900F, 2.0F, 2.0F, 1.0F)
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
                .texOffs(325, 141).addBox(-1.500F, -1.500F, -1.000F, 3.0F, 3.0F, 2.0F)
                .texOffs(191, 153).addBox(-1.000F, -1.000F, -1.600F, 2.0F, 2.0F, 1.0F)
                , PartPose.offsetAndRotation(-6.000F, -9.500F, -8.500F, -0.436F, -0.000F, -0.436F));
        PartDefinition b_belly_gear = b_hull_main.addOrReplaceChild("belly_gear", CubeListBuilder.create()
                .texOffs(223, 148).addBox(-1.500F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                .texOffs(462, 133).addBox(-1.000F, -3.200F, -0.500F, 2.0F, 6.4F, 1.0F)
                .texOffs(325, 133).addBox(-1.000F, -0.500F, -3.200F, 2.0F, 1.0F, 6.4F)
                , PartPose.offset(0.000F, 10.200F, 5.000F));
        PartDefinition b_belly_gear_t_0 = b_belly_gear.addOrReplaceChild("belly_gear_t_0", CubeListBuilder.create()
                .texOffs(234, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(1, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_1 = b_belly_gear.addOrReplaceChild("belly_gear_t_1", CubeListBuilder.create()
                .texOffs(245, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(8, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_2 = b_belly_gear.addOrReplaceChild("belly_gear_t_2", CubeListBuilder.create()
                .texOffs(256, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(15, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_3 = b_belly_gear.addOrReplaceChild("belly_gear_t_3", CubeListBuilder.create()
                .texOffs(267, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(22, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_4 = b_belly_gear.addOrReplaceChild("belly_gear_t_4", CubeListBuilder.create()
                .texOffs(278, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(29, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.142F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_5 = b_belly_gear.addOrReplaceChild("belly_gear_t_5", CubeListBuilder.create()
                .texOffs(289, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(36, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.927F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_6 = b_belly_gear.addOrReplaceChild("belly_gear_t_6", CubeListBuilder.create()
                .texOffs(300, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(43, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -4.712F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_t_7 = b_belly_gear.addOrReplaceChild("belly_gear_t_7", CubeListBuilder.create()
                .texOffs(311, 148).addBox(-1.000F, -3.850F, -1.750F, 2.0F, 1.35F, 3.50F)
                .texOffs(50, 157).addBox(-1.000F, -4.950F, -0.600F, 2.0F, 1.40F, 1.20F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -5.498F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2 = b_hull_main.addOrReplaceChild("belly_gear_2", CubeListBuilder.create()
                .texOffs(322, 148).addBox(-1.500F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                .texOffs(391, 141).addBox(-1.000F, -2.200F, -0.500F, 2.0F, 4.4F, 1.0F)
                .texOffs(290, 141).addBox(-1.000F, -0.500F, -2.200F, 2.0F, 1.0F, 4.4F)
                , PartPose.offset(0.000F, 10.000F, -3.500F));
        PartDefinition b_belly_gear_2_t_0 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_0", CubeListBuilder.create()
                .texOffs(78, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(57, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_1 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_1", CubeListBuilder.create()
                .texOffs(87, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(64, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_2 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_2", CubeListBuilder.create()
                .texOffs(96, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(71, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_3 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_3", CubeListBuilder.create()
                .texOffs(105, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(78, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_4 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_4", CubeListBuilder.create()
                .texOffs(114, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(85, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.142F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_5 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_5", CubeListBuilder.create()
                .texOffs(123, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(92, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.927F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_6 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_6", CubeListBuilder.create()
                .texOffs(132, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(99, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -4.712F, -0.000F, 0.000F));
        PartDefinition b_belly_gear_2_t_7 = b_belly_gear_2.addOrReplaceChild("belly_gear_2_t_7", CubeListBuilder.create()
                .texOffs(141, 153).addBox(-1.000F, -2.850F, -1.250F, 2.0F, 1.25F, 2.50F)
                .texOffs(106, 157).addBox(-1.000F, -3.950F, -0.550F, 2.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -5.498F, -0.000F, 0.000F));
        PartDefinition b_sub_cockpit_frame = b_hull_main.addOrReplaceChild("sub_cockpit_frame", CubeListBuilder.create()
                .texOffs(44, 76).addBox(-10.000F, -7.500F, -1.500F, 20.0F, 15.0F, 2.0F, new CubeDeformation(-0.040F))
                .texOffs(237, 76).addBox(-8.000F, 4.200F, -14.500F, 16.0F, 2.0F, 14.0F)
                .texOffs(409, 76).addBox(-7.000F, 5.800F, -13.700F, 14.0F, 2.0F, 13.0F)
                .texOffs(82, 95).addBox(-6.000F, 7.200F, -12.000F, 12.0F, 2.0F, 11.0F)
                .texOffs(319, 95).addBox(-4.500F, 8.400F, -10.000F, 9.0F, 2.0F, 9.0F)
                .texOffs(466, 123).addBox(-3.000F, 9.900F, -7.500F, 6.0F, 1.0F, 6.0F)
                .texOffs(45, 153).addBox(-2.000F, 5.800F, -14.300F, 4.0F, 2.0F, 1.0F)
                .texOffs(307, 111).addBox(-7.500F, -8.200F, -7.500F, 15.0F, 2.0F, 7.0F)
                .texOffs(137, 123).addBox(-5.500F, -8.700F, -7.500F, 11.0F, 1.0F, 7.0F)
                .texOffs(189, 95).addBox(-8.100F, -6.800F, -2.500F, 1.0F, 12.0F, 1.0F)
                .texOffs(309, 95).addBox(-8.100F, -6.100F, -8.000F, 1.0F, 11.0F, 1.0F)
                .texOffs(18, 111).addBox(-7.600F, -4.800F, -12.300F, 1.0F, 10.0F, 1.0F)
                .texOffs(373, 111).addBox(-8.000F, -7.000F, -9.200F, 1.0F, 1.0F, 8.0F)
                .texOffs(259, 95).addBox(-8.000F, 4.100F, -12.000F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.040F))
                .texOffs(297, 111).addBox(-5.100F, -4.200F, -14.500F, 1.0F, 9.0F, 1.0F)
                .texOffs(194, 95).addBox(7.100F, -6.800F, -2.500F, 1.0F, 12.0F, 1.0F)
                .texOffs(314, 95).addBox(7.100F, -6.100F, -8.000F, 1.0F, 11.0F, 1.0F)
                .texOffs(23, 111).addBox(6.600F, -4.800F, -12.300F, 1.0F, 10.0F, 1.0F)
                .texOffs(392, 111).addBox(7.000F, -7.000F, -9.200F, 1.0F, 1.0F, 8.0F)
                .texOffs(284, 95).addBox(7.000F, 4.100F, -12.000F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.040F))
                .texOffs(302, 111).addBox(4.100F, -4.200F, -14.500F, 1.0F, 9.0F, 1.0F)
                .texOffs(421, 153).addBox(-5.000F, -4.100F, -14.700F, 10.0F, 1.0F, 1.0F)
                .texOffs(371, 153).addBox(-5.500F, -6.600F, -12.100F, 11.0F, 1.0F, 1.0F)
                .texOffs(396, 153).addBox(-5.500F, 4.100F, -14.700F, 11.0F, 1.0F, 1.0F)
                .texOffs(26, 133).addBox(-3.000F, 2.600F, -9.000F, 6.0F, 2.0F, 5.0F)
                .texOffs(342, 133).addBox(-3.000F, -2.200F, -4.700F, 6.0F, 6.0F, 1.0F)
                .texOffs(51, 141).addBox(-4.000F, 1.700F, -12.900F, 8.0F, 3.0F, 3.0F)
                .texOffs(30, 153).addBox(-2.500F, 1.100F, -12.400F, 5.0F, 1.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_cockpit_roof_brow = b_sub_cockpit_frame.addOrReplaceChild("cockpit_roof_brow", CubeListBuilder.create()
                .texOffs(469, 133).addBox(-6.500F, -1.000F, -4.000F, 13.0F, 2.0F, 4.0F)
                .texOffs(242, 141).addBox(-4.500F, -1.400F, -4.000F, 9.0F, 1.0F, 4.0F)
                , PartPose.offsetAndRotation(0.000F, -7.200F, -7.500F, 0.419F, 0.000F, 0.000F));
        PartDefinition b_rib_arch_side_l = b_sub_cockpit_frame.addOrReplaceChild("rib_arch_side_l", CubeListBuilder.create()
                .texOffs(222, 141).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(-6.500F, -4.800F, -11.000F, -0.593F, 0.489F, 0.000F));
        PartDefinition b_rib_ws_top_l = b_sub_cockpit_frame.addOrReplaceChild("rib_ws_top_l", CubeListBuilder.create()
                .texOffs(227, 141).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(-4.600F, -5.000F, -12.800F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_rib_arch_side_r = b_sub_cockpit_frame.addOrReplaceChild("rib_arch_side_r", CubeListBuilder.create()
                .texOffs(232, 141).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(6.500F, -4.800F, -11.000F, -0.593F, -0.489F, 0.000F));
        PartDefinition b_rib_ws_top_r = b_sub_cockpit_frame.addOrReplaceChild("rib_ws_top_r", CubeListBuilder.create()
                .texOffs(237, 141).addBox(-0.500F, -2.500F, -0.500F, 1.0F, 5.0F, 1.0F)
                , PartPose.offsetAndRotation(4.600F, -5.000F, -12.800F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_control_lever_l = b_sub_cockpit_frame.addOrReplaceChild("control_lever_l", CubeListBuilder.create()
                .texOffs(418, 141).addBox(-0.500F, -3.700F, -0.500F, 1.0F, 4.0F, 1.0F)
                .texOffs(150, 153).addBox(-1.000F, -4.300F, -1.000F, 2.0F, 1.0F, 2.0F)
                , PartPose.offset(-3.000F, 1.900F, -10.400F));
        PartDefinition b_control_lever_r = b_sub_cockpit_frame.addOrReplaceChild("control_lever_r", CubeListBuilder.create()
                .texOffs(423, 141).addBox(-0.500F, -3.700F, -0.500F, 1.0F, 4.0F, 1.0F)
                .texOffs(159, 153).addBox(-1.000F, -4.300F, -1.000F, 2.0F, 1.0F, 2.0F)
                , PartPose.offset(3.000F, 1.900F, -10.400F));
        PartDefinition b_drowned_pilot = b_sub_cockpit_frame.addOrReplaceChild("drowned_pilot", CubeListBuilder.create()
                .texOffs(193, 123).addBox(-2.500F, -5.300F, -1.500F, 5.0F, 5.0F, 3.0F)
                .texOffs(168, 153).addBox(-1.500F, -4.000F, -2.100F, 3.0F, 2.0F, 1.0F)
                .texOffs(170, 141).addBox(-2.400F, -1.200F, -3.700F, 2.0F, 2.0F, 4.0F)
                .texOffs(183, 141).addBox(0.400F, -1.200F, -3.700F, 2.0F, 2.0F, 4.0F)
                , PartPose.offset(0.000F, 2.800F, -6.500F));
        PartDefinition b_drowned_head = b_drowned_pilot.addOrReplaceChild("drowned_head", CubeListBuilder.create()
                .texOffs(28, 111).addBox(-2.500F, -4.300F, -2.500F, 5.0F, 5.0F, 5.0F)
                .texOffs(1, 133).addBox(-3.000F, -0.200F, -3.000F, 6.0F, 1.0F, 6.0F)
                .texOffs(387, 148).addBox(-1.500F, -3.400F, -3.100F, 3.0F, 3.0F, 1.0F)
                , PartPose.offset(0.000F, -5.400F, 0.000F));
        PartDefinition b_drowned_arm_l = b_drowned_pilot.addOrReplaceChild("drowned_arm_l", CubeListBuilder.create()
                .texOffs(357, 133).addBox(-1.100F, -1.000F, -4.800F, 2.0F, 2.0F, 5.0F)
                , PartPose.offset(-3.000F, -4.200F, 0.000F));
        PartDefinition b_drowned_arm_r = b_drowned_pilot.addOrReplaceChild("drowned_arm_r", CubeListBuilder.create()
                .texOffs(372, 133).addBox(-0.900F, -1.000F, -4.800F, 2.0F, 2.0F, 5.0F)
                , PartPose.offset(3.000F, -4.200F, 0.000F));
        PartDefinition b_sub_cockpit_glass = b_hull_main.addOrReplaceChild("sub_cockpit_glass", CubeListBuilder.create()
                .texOffs(352, 111).addBox(-4.500F, -3.500F, -14.500F, 9.0F, 8.0F, 1.0F)
                .texOffs(417, 46).addBox(-7.800F, -5.600F, -11.500F, 1.0F, 10.0F, 10.0F)
                .texOffs(440, 46).addBox(6.800F, -5.600F, -11.500F, 1.0F, 10.0F, 10.0F)
                , PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_glass_front_curve_top = b_sub_cockpit_glass.addOrReplaceChild("glass_front_curve_top", CubeListBuilder.create()
                .texOffs(269, 141).addBox(-4.500F, -3.700F, 0.300F, 9.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -3.500F, -14.000F, -0.593F, -0.000F, 0.000F));
        PartDefinition b_glass_cheek_l = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_l", CubeListBuilder.create()
                .texOffs(1, 123).addBox(-2.000F, -4.000F, -0.500F, 4.0F, 8.0F, 1.0F)
                , PartPose.offsetAndRotation(-5.800F, 0.500F, -12.800F, 0.000F, 0.628F, 0.000F));
        PartDefinition b_glass_cheek_top_l = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_top_l", CubeListBuilder.create()
                .texOffs(336, 141).addBox(-2.000F, -2.000F, -0.500F, 4.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(-5.600F, -4.800F, -12.000F, -0.593F, 0.524F, 0.000F));
        PartDefinition b_glass_cheek_r = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_r", CubeListBuilder.create()
                .texOffs(12, 123).addBox(-2.000F, -4.000F, -0.500F, 4.0F, 8.0F, 1.0F)
                , PartPose.offsetAndRotation(5.800F, 0.500F, -12.800F, 0.000F, -0.628F, 0.000F));
        PartDefinition b_glass_cheek_top_r = b_sub_cockpit_glass.addOrReplaceChild("glass_cheek_top_r", CubeListBuilder.create()
                .texOffs(347, 141).addBox(-2.000F, -2.000F, -0.500F, 4.0F, 4.0F, 1.0F)
                , PartPose.offsetAndRotation(5.600F, -4.800F, -12.000F, -0.593F, -0.524F, 0.000F));
        PartDefinition b_harpoon_arm_l = b_hull_main.addOrReplaceChild("harpoon_arm_l", CubeListBuilder.create()
                .texOffs(196, 141).addBox(-2.100F, -1.500F, -1.500F, 3.0F, 3.0F, 3.0F)
                .texOffs(49, 111).addBox(-2.000F, -1.500F, -7.500F, 2.0F, 3.0F, 7.0F)
                .texOffs(387, 133).addBox(-1.500F, 0.900F, -7.000F, 1.0F, 1.0F, 6.0F)
                , PartPose.offset(-6.200F, 6.400F, -18.500F));
        PartDefinition b_harpoon_claw_l = b_harpoon_arm_l.addOrReplaceChild("harpoon_claw_l", CubeListBuilder.create()
                .texOffs(227, 123).addBox(-1.500F, -2.000F, -3.500F, 3.0F, 4.0F, 4.0F)
                , PartPose.offset(-1.000F, -0.000F, -7.500F));
        PartDefinition b_claw_jaw_top_l = b_harpoon_claw_l.addOrReplaceChild("claw_jaw_top_l", CubeListBuilder.create()
                .texOffs(95, 141).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -1.400F, -3.200F));
        PartDefinition b_claw_jaw_bot_l = b_harpoon_claw_l.addOrReplaceChild("claw_jaw_bot_l", CubeListBuilder.create()
                .texOffs(110, 141).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, 1.400F, -3.200F));
        PartDefinition b_harpoon_arm_r = b_hull_main.addOrReplaceChild("harpoon_arm_r", CubeListBuilder.create()
                .texOffs(209, 141).addBox(-0.900F, -1.500F, -1.500F, 3.0F, 3.0F, 3.0F)
                .texOffs(68, 111).addBox(0.000F, -1.500F, -7.500F, 2.0F, 3.0F, 7.0F)
                .texOffs(402, 133).addBox(0.500F, 0.900F, -7.000F, 1.0F, 1.0F, 6.0F)
                , PartPose.offset(6.200F, 6.400F, -18.500F));
        PartDefinition b_harpoon_claw_r = b_harpoon_arm_r.addOrReplaceChild("harpoon_claw_r", CubeListBuilder.create()
                .texOffs(242, 123).addBox(-1.500F, -2.000F, -3.500F, 3.0F, 4.0F, 4.0F)
                , PartPose.offset(1.000F, -0.000F, -7.500F));
        PartDefinition b_claw_jaw_top_r = b_harpoon_claw_r.addOrReplaceChild("claw_jaw_top_r", CubeListBuilder.create()
                .texOffs(125, 141).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -1.400F, -3.200F));
        PartDefinition b_claw_jaw_bot_r = b_harpoon_claw_r.addOrReplaceChild("claw_jaw_bot_r", CubeListBuilder.create()
                .texOffs(140, 141).addBox(-1.000F, -0.500F, -4.800F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, 1.400F, -3.200F));
        PartDefinition b_bow_dome = b_hull_main.addOrReplaceChild("bow_dome", CubeListBuilder.create(), PartPose.offset(0.000F, -0.000F, -14.000F));
        PartDefinition b_eye_iris_outer = b_bow_dome.addOrReplaceChild("eye_iris_outer", CubeListBuilder.create(), PartPose.offset(0.000F, 6.800F, -13.800F));
        PartDefinition b_eye_iris_ring = b_bow_dome.addOrReplaceChild("eye_iris_ring", CubeListBuilder.create(), PartPose.offset(0.000F, 6.800F, -13.900F));
        PartDefinition b_eye_core = b_bow_dome.addOrReplaceChild("eye_core", CubeListBuilder.create()
                .texOffs(56, 153).addBox(-2.000F, -1.000F, -0.500F, 4.0F, 2.0F, 1.0F)
                , PartPose.offset(0.000F, 6.800F, -14.000F));
        PartDefinition b_jaw_guard_top = b_bow_dome.addOrReplaceChild("jaw_guard_top", CubeListBuilder.create(), PartPose.offset(0.000F, -7.200F, -6.000F));
        PartDefinition b_jaw_guard_bot = b_bow_dome.addOrReplaceChild("jaw_guard_bot", CubeListBuilder.create(), PartPose.offset(0.000F, 9.000F, -6.000F));
        PartDefinition b_conning_tower = b_hull_main.addOrReplaceChild("conning_tower", CubeListBuilder.create()
                .texOffs(298, 76).addBox(-4.000F, -5.000F, -5.500F, 8.0F, 5.0F, 11.0F)
                .texOffs(129, 95).addBox(-4.500F, -5.800F, -6.000F, 9.0F, 1.0F, 12.0F)
                .texOffs(67, 153).addBox(-2.000F, -3.700F, -6.100F, 4.0F, 2.0F, 1.0F)
                .texOffs(210, 123).addBox(-2.000F, -9.000F, 1.000F, 4.0F, 4.0F, 4.0F)
                .texOffs(74, 141).addBox(-2.500F, -9.800F, 0.500F, 5.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -9.500F, -1.000F));
        PartDefinition b_clockwork_gear_l = b_conning_tower.addOrReplaceChild("clockwork_gear_l", CubeListBuilder.create()
                .texOffs(396, 148).addBox(-1.000F, -1.000F, -1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(428, 141).addBox(-0.500F, -2.200F, -0.500F, 1.0F, 4.4F, 1.0F)
                .texOffs(358, 141).addBox(-0.500F, -0.500F, -2.200F, 1.0F, 1.0F, 4.4F)
                , PartPose.offset(-4.800F, -2.700F, 4.500F));
        PartDefinition b_clockwork_gear_l_t_0 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_0", CubeListBuilder.create()
                .texOffs(198, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(113, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_1 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_1", CubeListBuilder.create()
                .texOffs(205, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(118, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_2 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_2", CubeListBuilder.create()
                .texOffs(212, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(123, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_3 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_3", CubeListBuilder.create()
                .texOffs(219, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(128, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_4 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_4", CubeListBuilder.create()
                .texOffs(226, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(133, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.142F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_5 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_5", CubeListBuilder.create()
                .texOffs(233, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(138, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.927F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_6 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_6", CubeListBuilder.create()
                .texOffs(240, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(143, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -4.712F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_l_t_7 = b_clockwork_gear_l.addOrReplaceChild("clockwork_gear_l_t_7", CubeListBuilder.create()
                .texOffs(247, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(148, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -5.498F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r = b_conning_tower.addOrReplaceChild("clockwork_gear_r", CubeListBuilder.create()
                .texOffs(405, 148).addBox(-1.000F, -1.000F, -1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(433, 141).addBox(-0.500F, -2.200F, -0.500F, 1.0F, 4.4F, 1.0F)
                .texOffs(369, 141).addBox(-0.500F, -0.500F, -2.200F, 1.0F, 1.0F, 4.4F)
                , PartPose.offset(4.800F, -2.700F, 4.500F));
        PartDefinition b_clockwork_gear_r_t_0 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_0", CubeListBuilder.create()
                .texOffs(254, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(153, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_1 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_1", CubeListBuilder.create()
                .texOffs(261, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(158, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.785F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_2 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_2", CubeListBuilder.create()
                .texOffs(268, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(163, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -1.571F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_3 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_3", CubeListBuilder.create()
                .texOffs(275, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(168, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -2.356F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_4 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_4", CubeListBuilder.create()
                .texOffs(282, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(173, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.142F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_5 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_5", CubeListBuilder.create()
                .texOffs(289, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(178, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -3.927F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_6 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_6", CubeListBuilder.create()
                .texOffs(296, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(183, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -4.712F, -0.000F, 0.000F));
        PartDefinition b_clockwork_gear_r_t_7 = b_clockwork_gear_r.addOrReplaceChild("clockwork_gear_r_t_7", CubeListBuilder.create()
                .texOffs(303, 153).addBox(-0.500F, -2.850F, -1.250F, 1.0F, 1.25F, 2.50F)
                .texOffs(188, 157).addBox(-0.500F, -3.950F, -0.550F, 1.0F, 1.35F, 1.10F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -5.498F, -0.000F, 0.000F));
        PartDefinition b_periscope_head = b_conning_tower.addOrReplaceChild("periscope_head", CubeListBuilder.create()
                .texOffs(349, 123).addBox(-1.000F, -5.800F, -1.000F, 2.0F, 6.0F, 2.0F)
                .texOffs(417, 133).addBox(-1.500F, -7.500F, -2.500F, 3.0F, 3.0F, 4.0F)
                .texOffs(310, 153).addBox(-1.000F, -7.000F, -3.100F, 2.0F, 2.0F, 1.0F)
                , PartPose.offset(0.000F, -5.700F, -2.500F));
        PartDefinition b_dorsal_bay_l = b_hull_main.addOrReplaceChild("dorsal_bay_l", CubeListBuilder.create()
                .texOffs(356, 95).addBox(-0.200F, -0.900F, -5.000F, 5.0F, 1.0F, 10.0F)
                , PartPose.offset(-4.500F, -9.200F, 8.500F));
        PartDefinition b_dorsal_bay_r = b_hull_main.addOrReplaceChild("dorsal_bay_r", CubeListBuilder.create()
                .texOffs(387, 95).addBox(-4.800F, -0.900F, -5.000F, 5.0F, 1.0F, 10.0F)
                , PartPose.offset(4.500F, -9.200F, 8.500F));
        PartDefinition b_dorsal_railgun_base = b_hull_main.addOrReplaceChild("dorsal_railgun_base", CubeListBuilder.create()
                .texOffs(418, 95).addBox(-3.000F, -3.700F, -3.000F, 6.0F, 5.0F, 6.0F)
                .texOffs(174, 123).addBox(-2.000F, -5.000F, -2.500F, 4.0F, 3.0F, 5.0F)
                , PartPose.offset(0.000F, -2.000F, 8.500F));
        PartDefinition b_dorsal_railgun_barrel = b_dorsal_railgun_base.addOrReplaceChild("dorsal_railgun_barrel", CubeListBuilder.create()
                .texOffs(230, 95).addBox(-3.000F, -2.000F, -6.000F, 6.0F, 4.0F, 8.0F)
                .texOffs(335, 46).addBox(-3.500F, -1.500F, -22.500F, 2.0F, 3.0F, 18.0F)
                .texOffs(376, 46).addBox(1.500F, -1.500F, -22.500F, 2.0F, 3.0F, 18.0F)
                .texOffs(463, 46).addBox(-1.000F, -1.000F, -21.500F, 2.0F, 2.0F, 17.0F)
                , PartPose.offset(0.000F, -4.500F, 0.000F));
        PartDefinition b_fin_pectoral_l = b_hull_main.addOrReplaceChild("fin_pectoral_l", CubeListBuilder.create()
                .texOffs(59, 123).addBox(-12.100F, -0.500F, -3.500F, 12.0F, 1.0F, 7.0F)
                .texOffs(468, 148).addBox(-12.600F, -1.000F, -4.000F, 13.0F, 2.0F, 1.0F)
                .texOffs(317, 153).addBox(-12.100F, -0.500F, 3.000F, 12.0F, 1.0F, 1.0F)
                .texOffs(468, 111).addBox(-12.900F, -1.000F, -3.500F, 1.0F, 2.0F, 7.0F)
                .texOffs(432, 133).addBox(-1.400F, -3.800F, -2.500F, 2.0F, 2.0F, 5.0F)
                , PartPose.offsetAndRotation(-10.400F, 4.000F, 0.000F, -0.000F, -0.244F, -0.105F));
        PartDefinition b_wing_strut_l_f = b_fin_pectoral_l.addOrReplaceChild("wing_strut_l_f", CubeListBuilder.create()
                .texOffs(444, 153).addBox(-6.100F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(-0.800F, -2.800F, -1.600F, -0.000F, -0.000F, 0.628F));
        PartDefinition b_wing_strut_l_b = b_fin_pectoral_l.addOrReplaceChild("wing_strut_l_b", CubeListBuilder.create()
                .texOffs(461, 153).addBox(-6.100F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(-0.800F, -2.800F, 1.600F, -0.000F, -0.000F, 0.628F));
        PartDefinition b_ballast_vent_l = b_hull_main.addOrReplaceChild("ballast_vent_l", CubeListBuilder.create()
                .texOffs(443, 95).addBox(-2.000F, -2.000F, -3.500F, 2.0F, 4.0F, 7.0F)
                .texOffs(257, 123).addBox(-2.200F, -1.500F, -2.500F, 1.0F, 3.0F, 5.0F)
                , PartPose.offset(-9.500F, -4.800F, 10.000F));
        PartDefinition b_vent_flap_l = b_ballast_vent_l.addOrReplaceChild("vent_flap_l", CubeListBuilder.create()
                .texOffs(481, 95).addBox(-0.900F, -0.000F, -3.500F, 1.0F, 4.0F, 7.0F)
                , PartPose.offset(-1.900F, -2.000F, 0.000F));
        PartDefinition b_fin_pectoral_r = b_hull_main.addOrReplaceChild("fin_pectoral_r", CubeListBuilder.create()
                .texOffs(98, 123).addBox(0.100F, -0.500F, -3.500F, 12.0F, 1.0F, 7.0F)
                .texOffs(1, 153).addBox(-0.400F, -1.000F, -4.000F, 13.0F, 2.0F, 1.0F)
                .texOffs(344, 153).addBox(0.100F, -0.500F, 3.000F, 12.0F, 1.0F, 1.0F)
                .texOffs(485, 111).addBox(11.900F, -1.000F, -3.500F, 1.0F, 2.0F, 7.0F)
                .texOffs(447, 133).addBox(-0.600F, -3.800F, -2.500F, 2.0F, 2.0F, 5.0F)
                , PartPose.offsetAndRotation(10.400F, 4.000F, 0.000F, -0.000F, 0.244F, 0.105F));
        PartDefinition b_wing_strut_r_f = b_fin_pectoral_r.addOrReplaceChild("wing_strut_r_f", CubeListBuilder.create()
                .texOffs(478, 153).addBox(-0.900F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.800F, -2.800F, -1.600F, -0.000F, -0.000F, -0.628F));
        PartDefinition b_wing_strut_r_b = b_fin_pectoral_r.addOrReplaceChild("wing_strut_r_b", CubeListBuilder.create()
                .texOffs(495, 153).addBox(-0.900F, 0.900F, -0.500F, 7.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.800F, -2.800F, 1.600F, -0.000F, -0.000F, -0.628F));
        PartDefinition b_ballast_vent_r = b_hull_main.addOrReplaceChild("ballast_vent_r", CubeListBuilder.create()
                .texOffs(462, 95).addBox(0.000F, -2.000F, -3.500F, 2.0F, 4.0F, 7.0F)
                .texOffs(270, 123).addBox(1.200F, -1.500F, -2.500F, 1.0F, 3.0F, 5.0F)
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
                .texOffs(327, 123).addBox(0.200F, -3.500F, -0.800F, 4.0F, 7.0F, 1.0F)
                , PartPose.offset(-4.400F, -0.000F, -7.200F));
        PartDefinition b_torpedo_tip_0 = b_pod_left.addOrReplaceChild("torpedo_tip_0", CubeListBuilder.create()
                .texOffs(414, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, 1.800F, -6.000F));
        PartDefinition b_torpedo_tip_1 = b_pod_left.addOrReplaceChild("torpedo_tip_1", CubeListBuilder.create()
                .texOffs(423, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, -0.000F, -6.000F));
        PartDefinition b_torpedo_tip_2 = b_pod_left.addOrReplaceChild("torpedo_tip_2", CubeListBuilder.create()
                .texOffs(432, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(-2.200F, -1.800F, -6.000F));
        PartDefinition b_pod_right = b_hull_main.addOrReplaceChild("pod_right", CubeListBuilder.create()
                .texOffs(163, 76).addBox(0.200F, -3.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(200, 76).addBox(0.200F, 0.300F, -7.000F, 4.0F, 3.0F, 14.0F)
                .texOffs(200, 111).addBox(-0.300F, -4.000F, -6.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(215, 111).addBox(-0.300F, -4.000F, -1.000F, 5.0F, 8.0F, 2.0F)
                .texOffs(230, 111).addBox(-0.300F, -4.000F, 4.000F, 5.0F, 8.0F, 2.0F)
                , PartPose.offset(10.400F, -0.000F, 8.000F));
        PartDefinition b_pod_hatch_r = b_pod_right.addOrReplaceChild("pod_hatch_r", CubeListBuilder.create()
                .texOffs(338, 123).addBox(-4.200F, -3.500F, -0.800F, 4.0F, 7.0F, 1.0F)
                , PartPose.offset(4.400F, -0.000F, -7.200F));
        PartDefinition b_torpedo_tip_3 = b_pod_right.addOrReplaceChild("torpedo_tip_3", CubeListBuilder.create()
                .texOffs(441, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, 1.800F, -6.000F));
        PartDefinition b_torpedo_tip_4 = b_pod_right.addOrReplaceChild("torpedo_tip_4", CubeListBuilder.create()
                .texOffs(450, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, -0.000F, -6.000F));
        PartDefinition b_torpedo_tip_5 = b_pod_right.addOrReplaceChild("torpedo_tip_5", CubeListBuilder.create()
                .texOffs(459, 148).addBox(-1.000F, -1.000F, -1.800F, 2.0F, 2.0F, 2.0F)
                , PartPose.offset(2.200F, -1.800F, -6.000F));
        PartDefinition b_tail_cone = b_hull_main.addOrReplaceChild("tail_cone", CubeListBuilder.create()
                .texOffs(1, 76).addBox(-7.000F, -5.500F, -0.500F, 14.0F, 11.0F, 7.0F)
                .texOffs(199, 95).addBox(-6.500F, -5.000F, 5.500F, 13.0F, 10.0F, 2.0F)
                .texOffs(51, 95).addBox(-4.500F, -4.000F, 6.500F, 9.0F, 8.0F, 6.0F)
                .texOffs(411, 111).addBox(-6.700F, -1.500F, 6.500F, 3.0F, 3.0F, 6.0F)
                .texOffs(1, 141).addBox(-12.000F, -0.500F, 4.500F, 7.0F, 1.0F, 5.0F)
                .texOffs(430, 111).addBox(3.700F, -1.500F, 6.500F, 3.0F, 3.0F, 6.0F)
                .texOffs(26, 141).addBox(5.000F, -0.500F, 4.500F, 7.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -0.000F, 16.000F));
        PartDefinition b_rudder_top = b_tail_cone.addOrReplaceChild("rudder_top", CubeListBuilder.create()
                .texOffs(32, 95).addBox(-1.000F, -8.000F, -1.500F, 2.0F, 8.0F, 7.0F)
                .texOffs(155, 141).addBox(-1.000F, -8.600F, 0.500F, 2.0F, 1.0F, 5.0F)
                , PartPose.offset(0.000F, -5.500F, 4.500F));
        PartDefinition b_rudder_bot = b_tail_cone.addOrReplaceChild("rudder_bot", CubeListBuilder.create()
                .texOffs(172, 95).addBox(-1.000F, -0.000F, -1.000F, 2.0F, 7.0F, 6.0F)
                , PartPose.offset(0.000F, 5.500F, 4.500F));
        PartDefinition b_propeller_shroud = b_tail_cone.addOrReplaceChild("propeller_shroud", CubeListBuilder.create(), PartPose.offset(0.000F, -0.000F, 14.500F));
        PartDefinition b_shroud_seg_0 = b_propeller_shroud.addOrReplaceChild("shroud_seg_0", CubeListBuilder.create()
                .texOffs(49, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_shroud_seg_1 = b_propeller_shroud.addOrReplaceChild("shroud_seg_1", CubeListBuilder.create()
                .texOffs(72, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 0.524F));
        PartDefinition b_shroud_seg_2 = b_propeller_shroud.addOrReplaceChild("shroud_seg_2", CubeListBuilder.create()
                .texOffs(95, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 1.047F));
        PartDefinition b_shroud_seg_3 = b_propeller_shroud.addOrReplaceChild("shroud_seg_3", CubeListBuilder.create()
                .texOffs(118, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 1.571F));
        PartDefinition b_shroud_seg_4 = b_propeller_shroud.addOrReplaceChild("shroud_seg_4", CubeListBuilder.create()
                .texOffs(141, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 2.094F));
        PartDefinition b_shroud_seg_5 = b_propeller_shroud.addOrReplaceChild("shroud_seg_5", CubeListBuilder.create()
                .texOffs(164, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 2.618F));
        PartDefinition b_shroud_seg_6 = b_propeller_shroud.addOrReplaceChild("shroud_seg_6", CubeListBuilder.create()
                .texOffs(187, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 3.142F));
        PartDefinition b_shroud_seg_7 = b_propeller_shroud.addOrReplaceChild("shroud_seg_7", CubeListBuilder.create()
                .texOffs(210, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 3.665F));
        PartDefinition b_shroud_seg_8 = b_propeller_shroud.addOrReplaceChild("shroud_seg_8", CubeListBuilder.create()
                .texOffs(233, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 4.189F));
        PartDefinition b_shroud_seg_9 = b_propeller_shroud.addOrReplaceChild("shroud_seg_9", CubeListBuilder.create()
                .texOffs(256, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 4.712F));
        PartDefinition b_shroud_seg_10 = b_propeller_shroud.addOrReplaceChild("shroud_seg_10", CubeListBuilder.create()
                .texOffs(279, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 5.236F));
        PartDefinition b_shroud_seg_11 = b_propeller_shroud.addOrReplaceChild("shroud_seg_11", CubeListBuilder.create()
                .texOffs(302, 133).addBox(-3.000F, -10.500F, -2.500F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.040F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.000F, 5.760F));
        PartDefinition b_shroud_strut_0 = b_propeller_shroud.addOrReplaceChild("shroud_strut_0", CubeListBuilder.create()
                .texOffs(23, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, -1.500F));
        PartDefinition b_shroud_strut_1 = b_propeller_shroud.addOrReplaceChild("shroud_strut_1", CubeListBuilder.create()
                .texOffs(32, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 1.571F));
        PartDefinition b_shroud_strut_2 = b_propeller_shroud.addOrReplaceChild("shroud_strut_2", CubeListBuilder.create()
                .texOffs(41, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 3.142F));
        PartDefinition b_shroud_strut_3 = b_propeller_shroud.addOrReplaceChild("shroud_strut_3", CubeListBuilder.create()
                .texOffs(50, 123).addBox(-1.000F, -8.500F, -1.000F, 2.0F, 7.0F, 2.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, -1.500F, -0.000F, -0.000F, 4.712F));
        PartDefinition b_propeller_shaft = b_tail_cone.addOrReplaceChild("propeller_shaft", CubeListBuilder.create()
                .texOffs(449, 111).addBox(-2.000F, -2.000F, -2.500F, 4.0F, 4.0F, 5.0F)
                .texOffs(380, 141).addBox(-1.500F, -1.500F, 1.700F, 3.0F, 3.0F, 2.0F)
                , PartPose.offset(0.000F, -0.000F, 14.500F));
        PartDefinition b_prop_blade_0 = b_propeller_shaft.addOrReplaceChild("prop_blade_0", CubeListBuilder.create()
                .texOffs(358, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 0.000F));
        PartDefinition b_prop_blade_1 = b_propeller_shaft.addOrReplaceChild("prop_blade_1", CubeListBuilder.create()
                .texOffs(367, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.033F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 0.524F));
        PartDefinition b_prop_blade_2 = b_propeller_shaft.addOrReplaceChild("prop_blade_2", CubeListBuilder.create()
                .texOffs(376, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.051F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 1.047F));
        PartDefinition b_prop_blade_3 = b_propeller_shaft.addOrReplaceChild("prop_blade_3", CubeListBuilder.create()
                .texOffs(385, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 1.571F));
        PartDefinition b_prop_blade_4 = b_propeller_shaft.addOrReplaceChild("prop_blade_4", CubeListBuilder.create()
                .texOffs(394, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.033F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 2.094F));
        PartDefinition b_prop_blade_5 = b_propeller_shaft.addOrReplaceChild("prop_blade_5", CubeListBuilder.create()
                .texOffs(403, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.051F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 2.618F));
        PartDefinition b_prop_blade_6 = b_propeller_shaft.addOrReplaceChild("prop_blade_6", CubeListBuilder.create()
                .texOffs(412, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 3.142F));
        PartDefinition b_prop_blade_7 = b_propeller_shaft.addOrReplaceChild("prop_blade_7", CubeListBuilder.create()
                .texOffs(421, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.033F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 3.665F));
        PartDefinition b_prop_blade_8 = b_propeller_shaft.addOrReplaceChild("prop_blade_8", CubeListBuilder.create()
                .texOffs(430, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.051F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 4.189F));
        PartDefinition b_prop_blade_9 = b_propeller_shaft.addOrReplaceChild("prop_blade_9", CubeListBuilder.create()
                .texOffs(439, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.015F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 4.712F));
        PartDefinition b_prop_blade_10 = b_propeller_shaft.addOrReplaceChild("prop_blade_10", CubeListBuilder.create()
                .texOffs(448, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.033F))
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.000F, -0.454F, 5.236F));
        PartDefinition b_prop_blade_11 = b_propeller_shaft.addOrReplaceChild("prop_blade_11", CubeListBuilder.create()
                .texOffs(457, 123).addBox(-1.500F, -8.700F, -0.500F, 3.0F, 7.0F, 1.0F, new CubeDeformation(-0.051F))
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
