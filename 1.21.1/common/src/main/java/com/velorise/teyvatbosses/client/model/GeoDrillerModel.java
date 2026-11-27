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

public class GeoDrillerModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("geo_driller"), "main");

    public static final String[] ALL_BONES = {
            "root", "chassis_main",
            "tread_l", "wheel_l_0", "wheel_l_1", "wheel_l_2",
            "tread_r", "wheel_r_0", "wheel_r_1", "wheel_r_2",
            "cockpit_cage", "lever_l", "lever_r",
            "drill_assembly", "drill_head",
            "engine_unit", "flapper_l", "flapper_r",
            "chest_mount"
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

    private static void addSprocketWheel(PartDefinition parent, String name, float zPos, int gearTexU, int gearTexV, int hubTexU, int hubTexV) {
        // 8-tooth industrial sprocket wheel centered cleanly inside the track channel (y = -0.25)
        PartDefinition wheel = parent.addOrReplaceChild(name, CubeListBuilder.create()
                // Inner steel drum
                .texOffs(hubTexU, hubTexV).addBox(-3.550F, -2.450F, -2.450F, 7.100F, 4.900F, 4.900F)
                // Central heavy axle hub
                .texOffs(hubTexU, hubTexV).addBox(-4.300F, -1.300F, -1.300F, 8.600F, 2.600F, 2.600F)
                // 4 cardinal sprocket teeth meshing into the upper & lower track rails
                .texOffs(gearTexU, gearTexV).addBox(-3.900F, -3.650F, -0.950F, 7.800F, 7.300F, 1.900F)
                .texOffs(gearTexU, gearTexV).addBox(-3.900F, -0.950F, -3.650F, 7.800F, 1.900F, 7.300F),
                PartPose.offset(0.000F, -0.250F, zPos));

        // 4 diagonal sprocket teeth rotated 45 degrees around X for full 8-tooth gear profile
        wheel.addOrReplaceChild(name + "_diag", CubeListBuilder.create()
                .texOffs(gearTexU, gearTexV).addBox(-3.820F, -3.550F, -0.900F, 7.640F, 7.100F, 1.800F)
                .texOffs(gearTexU, gearTexV).addBox(-3.820F, -0.900F, -3.550F, 7.640F, 1.800F, 7.100F),
                PartPose.rotation(0.785398F, 0.000F, 0.000F));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(0.000F, 24.000F, 0.000F));

        // Main armored chassis with lowered front hood so 1st-person view is completely unobstructed
        PartDefinition b_chassis_main = b_root.addOrReplaceChild("chassis_main", CubeListBuilder.create()
                .texOffs(1, 1).addBox(-11.000F, -3.000F, -18.000F, 22.000F, 6.000F, 38.000F)
                .texOffs(122, 1).addBox(-12.000F, -6.000F, -16.000F, 2.000F, 4.000F, 34.000F)
                .texOffs(195, 1).addBox(10.000F, -6.000F, -16.000F, 2.000F, 4.000F, 34.000F)
                .texOffs(268, 1).addBox(-11.000F, -6.500F, -20.000F, 22.000F, 8.500F, 4.000F)
                .texOffs(321, 1).addBox(-9.000F, -7.200F, -21.000F, 3.000F, 2.600F, 2.000F)
                .texOffs(332, 1).addBox(6.000F, -7.200F, -21.000F, 3.000F, 2.600F, 2.000F),
                PartPose.offset(0.000F, -10.000F, 0.000F));

        // Left track frame + 3 interlocking 8-tooth sprocket gears
        PartDefinition b_tread_l = b_chassis_main.addOrReplaceChild("tread_l", CubeListBuilder.create()
                .texOffs(343, 1).addBox(-3.500F, -6.500F, -18.000F, 7.000F, 2.500F, 36.000F)
                .texOffs(1, 46).addBox(-3.500F, 3.500F, -18.000F, 7.000F, 2.500F, 36.000F)
                .texOffs(88, 46).addBox(-3.500F, -4.500F, -19.500F, 7.000F, 8.500F, 2.500F)
                .texOffs(107, 46).addBox(-3.500F, -4.500F, 17.000F, 7.000F, 8.500F, 2.500F)
                .texOffs(126, 46).addBox(-3.800F, -7.700F, -17.000F, 7.600F, 1.500F, 34.000F),
                PartPose.offset(-14.500F, 4.000F, 1.000F));
        addSprocketWheel(b_tread_l, "wheel_l_0", -12.000F, 209, 46, 238, 46);
        addSprocketWheel(b_tread_l, "wheel_l_1",   0.000F, 261, 46, 290, 46);
        addSprocketWheel(b_tread_l, "wheel_l_2",  12.000F, 313, 46, 342, 46);

        // Right track frame + 3 interlocking 8-tooth sprocket gears
        PartDefinition b_tread_r = b_chassis_main.addOrReplaceChild("tread_r", CubeListBuilder.create()
                .texOffs(365, 46).addBox(-3.500F, -6.500F, -18.000F, 7.000F, 2.500F, 36.000F)
                .texOffs(1, 85).addBox(-3.500F, 3.500F, -18.000F, 7.000F, 2.500F, 36.000F)
                .texOffs(88, 85).addBox(-3.500F, -4.500F, -19.500F, 7.000F, 8.500F, 2.500F)
                .texOffs(107, 85).addBox(-3.500F, -4.500F, 17.000F, 7.000F, 8.500F, 2.500F)
                .texOffs(126, 85).addBox(-3.800F, -7.700F, -17.000F, 7.600F, 1.500F, 34.000F),
                PartPose.offset(14.500F, 4.000F, 1.000F));
        addSprocketWheel(b_tread_r, "wheel_r_0", -12.000F, 209, 85, 238, 85);
        addSprocketWheel(b_tread_r, "wheel_r_1",   0.000F, 261, 85, 290, 85);
        addSprocketWheel(b_tread_r, "wheel_r_2",  12.000F, 313, 85, 342, 85);

        // Solidly anchored industrial roll-cage cockpit: zero UV gaps, armored roof canopy, work-lights & instrument dials
        PartDefinition b_cockpit_cage = b_chassis_main.addOrReplaceChild("cockpit_cage", CubeListBuilder.create()
                // Seat cushion & backrest anchored solidly to cockpit floor
                .texOffs(365, 85).addBox(-5.200F, -3.500F, -4.000F, 10.400F, 3.800F, 8.200F)
                .texOffs(402, 85).addBox(-5.200F, -13.200F, 2.500F, 10.400F, 10.600F, 2.200F)
                // Full-width armored dashboard + illuminated instrument gauge cluster
                .texOffs(4, 180).addBox(-10.200F, -4.800F, -11.200F, 20.400F, 5.200F, 4.400F)
                .texOffs(370, 180).addBox(-6.000F, -6.200F, -10.400F, 12.000F, 2.000F, 1.400F)
                // 4 vertical roll-cage pillars seated deep into the side walls & meeting roof beams flush
                .texOffs(64, 180).addBox(-11.400F, -24.200F, -11.000F, 1.800F, 24.800F, 1.800F)
                .texOffs(64, 180).addBox(9.600F, -24.200F, -11.000F, 1.800F, 24.800F, 1.800F)
                .texOffs(64, 180).addBox(-11.400F, -24.200F, 4.000F, 1.800F, 24.800F, 1.800F)
                .texOffs(64, 180).addBox(9.600F, -24.200F, 4.000F, 1.800F, 24.800F, 1.800F)
                // 4 upper corner gusset reinforcement brackets
                .texOffs(250, 220).addBox(-11.700F, -24.800F, -11.300F, 2.400F, 3.200F, 2.400F)
                .texOffs(250, 220).addBox(9.300F, -24.800F, -11.300F, 2.400F, 3.200F, 2.400F)
                .texOffs(250, 220).addBox(-11.700F, -24.800F, 3.700F, 2.400F, 3.200F, 2.400F)
                .texOffs(250, 220).addBox(9.300F, -24.800F, 3.700F, 2.400F, 3.200F, 2.400F)
                // Side mid-height safety rails connecting front & rear pillars cleanly
                .texOffs(84, 180).addBox(-11.300F, -11.200F, -10.200F, 1.600F, 1.600F, 15.000F)
                .texOffs(84, 180).addBox(9.700F, -11.200F, -10.200F, 1.600F, 1.600F, 15.000F)
                // Rear horizontal cross-brace tying left/right rear pillars directly into the seat backrest
                .texOffs(136, 180).addBox(-10.400F, -11.200F, 4.100F, 20.800F, 1.600F, 1.600F)
                // Continuous top roof perimeter frame meeting all 4 pillar caps flush
                .texOffs(84, 180).addBox(-11.400F, -25.800F, -11.000F, 1.800F, 1.800F, 16.800F)
                .texOffs(84, 180).addBox(9.600F, -25.800F, -11.000F, 1.800F, 1.800F, 16.800F)
                .texOffs(136, 180).addBox(-11.400F, -25.800F, -11.000F, 22.800F, 1.800F, 1.800F)
                .texOffs(136, 180).addBox(-11.400F, -25.800F, 4.000F, 22.800F, 1.800F, 1.800F)
                // Solid armored louvered canopy roof plate + front brow sun-visor + twin overhead amber work-lights
                .texOffs(200, 180).addBox(-10.400F, -26.300F, -10.400F, 20.800F, 1.200F, 15.600F)
                .texOffs(286, 180).addBox(-10.600F, -25.900F, -13.200F, 21.200F, 1.400F, 3.000F)
                .texOffs(348, 180).addBox(-8.600F, -25.700F, -13.800F, 3.200F, 1.300F, 1.200F)
                .texOffs(348, 180).addBox(5.400F, -25.700F, -13.800F, 3.200F, 1.300F, 1.200F),
                PartPose.offset(0.000F, -3.000F, -4.000F));

        // Dual control levers aligned precisely with the seated driver's outstretched hands
        b_cockpit_cage.addOrReplaceChild("lever_l", CubeListBuilder.create()
                .texOffs(149, 124).addBox(-0.550F, -4.500F, -0.550F, 1.100F, 4.500F, 1.100F)
                .texOffs(154, 124).addBox(-1.050F, -5.600F, -1.050F, 2.100F, 1.300F, 2.100F),
                PartPose.offsetAndRotation(-4.600F, -3.800F, -5.800F, 0.2400F, 0.0000F, 0.0000F));
        b_cockpit_cage.addOrReplaceChild("lever_r", CubeListBuilder.create()
                .texOffs(163, 124).addBox(-0.550F, -4.500F, -0.550F, 1.100F, 4.500F, 1.100F)
                .texOffs(168, 124).addBox(-1.050F, -5.600F, -1.050F, 2.100F, 1.300F, 2.100F),
                PartPose.offsetAndRotation(4.600F, -3.800F, -5.800F, 0.2400F, 0.0000F, 0.0000F));

        // Front bore assembly + spiral drill head
        PartDefinition b_drill_assembly = b_chassis_main.addOrReplaceChild("drill_assembly", CubeListBuilder.create()
                .texOffs(177, 124).addBox(-9.500F, -9.500F, -4.000F, 19.000F, 19.000F, 5.000F)
                .texOffs(226, 124).addBox(-10.000F, -10.000F, -2.500F, 20.000F, 20.000F, 2.000F),
                PartPose.offset(0.000F, -4.000F, -20.000F));
        b_drill_assembly.addOrReplaceChild("drill_head", CubeListBuilder.create()
                .texOffs(271, 124).addBox(-8.500F, -8.500F, -5.500F, 17.000F, 17.000F, 5.500F)
                .texOffs(316, 124).addBox(7.540F, -1.300F, -4.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(327, 124).addBox(-1.300F, -10.140F, -4.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(338, 124).addBox(-10.140F, -1.300F, -4.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(349, 124).addBox(-1.300F, 7.540F, -4.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(360, 124).addBox(-7.000F, -7.000F, -10.500F, 14.000F, 14.000F, 5.500F)
                .texOffs(399, 124).addBox(5.128F, -4.718F, -9.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(410, 124).addBox(-4.718F, -7.728F, -9.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(421, 124).addBox(-7.728F, 2.118F, -9.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(432, 124).addBox(2.118F, 5.128F, -9.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(443, 124).addBox(-5.500F, -5.500F, -15.500F, 11.000F, 11.000F, 5.500F)
                .texOffs(476, 124).addBox(1.899F, -6.042F, -14.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(487, 124).addBox(-6.042F, -4.499F, -14.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(498, 124).addBox(-4.499F, 3.442F, -14.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(1, 149).addBox(3.442F, 1.899F, -14.500F, 2.600F, 2.600F, 3.000F)
                .texOffs(12, 149).addBox(-3.800F, -3.800F, -20.000F, 7.600F, 7.600F, 5.000F)
                .texOffs(37, 149).addBox(-0.887F, -5.230F, -19.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(48, 149).addBox(-5.230F, -1.713F, -19.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(59, 149).addBox(-1.713F, 2.630F, -19.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(70, 149).addBox(2.630F, -0.887F, -19.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(81, 149).addBox(-2.000F, -2.000F, -24.000F, 4.000F, 4.000F, 4.500F)
                .texOffs(98, 149).addBox(-2.079F, -3.229F, -23.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(109, 149).addBox(-3.229F, -0.521F, -23.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(120, 149).addBox(-0.521F, 0.629F, -23.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(131, 149).addBox(0.629F, -2.079F, -23.000F, 2.600F, 2.600F, 3.000F)
                .texOffs(142, 149).addBox(-1.200F, -1.200F, -26.500F, 2.400F, 2.400F, 3.000F),
                PartPose.offset(0.000F, 0.000F, -4.000F));

        // Professional low-profile industrial engine block + twin vertical exhaust smokestacks (100% solid UVs)
        PartDefinition b_engine_unit = b_chassis_main.addOrReplaceChild("engine_unit", CubeListBuilder.create()
                // Low armored diesel/Geo engine manifold block (keeps 3rd-person center sightline wide open)
                .texOffs(4, 220).addBox(-7.500F, -5.500F, -3.500F, 15.000F, 5.500F, 8.500F)
                // Top armored valve cover & radiator slats
                .texOffs(64, 220).addBox(-5.500F, -6.800F, -2.500F, 11.000F, 1.400F, 6.500F)
                // Recessed Geo furnace vent strip (emissive accent, never obstructs camera)
                .texOffs(112, 220).addBox(-4.000F, -4.400F, 4.600F, 8.000F, 2.400F, 0.800F)
                // Left exhaust muffler drum + vertical smokestack pipe + brass heat-shield collars
                .texOffs(144, 220).addBox(-9.800F, -6.500F, -0.500F, 3.400F, 5.500F, 3.600F)
                .texOffs(172, 220).addBox(-9.400F, -15.500F, 0.000F, 2.600F, 9.500F, 2.600F)
                .texOffs(196, 220).addBox(-9.850F, -9.500F, -0.450F, 3.500F, 1.500F, 3.500F)
                .texOffs(196, 220).addBox(-9.850F, -13.200F, -0.450F, 3.500F, 1.500F, 3.500F)
                .texOffs(224, 220).addBox(-9.600F, -16.100F, -0.200F, 3.000F, 0.800F, 3.000F)
                // Right exhaust muffler drum + vertical smokestack pipe + brass heat-shield collars
                .texOffs(144, 220).addBox(6.400F, -6.500F, -0.500F, 3.400F, 5.500F, 3.600F)
                .texOffs(172, 220).addBox(6.800F, -15.500F, 0.000F, 2.600F, 9.500F, 2.600F)
                .texOffs(196, 220).addBox(6.350F, -9.500F, -0.450F, 3.500F, 1.500F, 3.500F)
                .texOffs(196, 220).addBox(6.350F, -13.200F, -0.450F, 3.500F, 1.500F, 3.500F)
                .texOffs(224, 220).addBox(6.600F, -16.100F, -0.200F, 3.000F, 0.800F, 3.000F),
                PartPose.offset(0.000F, -3.000F, 6.200F));

        // Hinged industrial rain-flapper caps on top of both twin smokestacks
        b_engine_unit.addOrReplaceChild("flapper_l", CubeListBuilder.create()
                .texOffs(224, 220).addBox(-1.600F, -0.700F, -3.100F, 3.200F, 0.800F, 3.200F),
                PartPose.offset(-8.100F, -16.100F, 2.800F));
        b_engine_unit.addOrReplaceChild("flapper_r", CubeListBuilder.create()
                .texOffs(224, 220).addBox(-1.600F, -0.700F, -3.100F, 3.200F, 0.800F, 3.200F),
                PartPose.offset(8.100F, -16.100F, 2.800F));

        b_chassis_main.addOrReplaceChild("chest_mount", CubeListBuilder.create(),
                PartPose.offset(0.000F, -3.000F, 15.500F));
        return LayerDefinition.create(mesh, 512, 512);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
