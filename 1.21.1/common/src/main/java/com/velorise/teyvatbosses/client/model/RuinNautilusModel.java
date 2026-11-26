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
 * Upgraded high-detail 26-bone HierarchicalModel for {@code RuinNautilusEntity} (scale 2.35x)
 * and the player-piloted 4-part {@code RuinSubmersibleEntity} (scale 1.65x).
 *
 * <p>Features an elongated octagonal-chamfered Khaenri'ah armored hull with structural ribs,
 * beveled glass bow cockpit canopy, raised conning tower + periscope, layered pectoral hydro-planes,
 * twin multi-tube side torpedo bays ({@code torpedo_tip_0..5}), and a shrouded 4-blade brass turbine.
 */
public class RuinNautilusModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("ruin_nautilus"), "main");

    public static final String[] ALL_BONES = {
            "root", "hull_pitch", "hull_main",
            "bow_dome", "eye_iris_ring", "eye_core", "jaw_guard_top", "jaw_guard_bot",
            "conning_tower", "periscope_head",
            "fin_pectoral_l", "fin_pectoral_r",
            "ballast_vent_l", "ballast_vent_r",
            "pod_left", "pod_hatch_l", "torpedo_tip_0", "torpedo_tip_1", "torpedo_tip_2",
            "pod_right", "pod_hatch_r", "torpedo_tip_3", "torpedo_tip_4", "torpedo_tip_5",
            "tail_cone", "rudder_top", "rudder_bot", "propeller_shaft"
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

        // Model origin at Y=24 ground baseline; hull center sits at Y=4 (20px up)
        PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition hullPitch = root.addOrReplaceChild("hull_pitch", CubeListBuilder.create(), PartPose.ZERO);

        // 1. Elongated octagonal-chamfered Khaenri'ah armored hull with structural ribs
        PartDefinition hullMain = hullPitch.addOrReplaceChild("hull_main", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-9.5F, -7.5F, -14.0F, 19.0F, 15.0F, 28.0F)
                // Chamfered top & bottom keel plates
                .texOffs(0, 26).addBox(-7.5F, -10.0F, -12.0F, 15.0F, 2.5F, 24.0F, new CubeDeformation(0.03F))
                .texOffs(0, 42).addBox(-7.5F,  7.5F, -12.0F, 15.0F, 2.3F, 24.0F, new CubeDeformation(0.03F))
                // Chamfered side armor bolsters
                .texOffs(0, 16).addBox(-11.0F, -5.0F, -10.0F, 1.5F, 10.0F, 20.0F, new CubeDeformation(0.02F))
                .texOffs(0, 16).addBox(  9.5F, -5.0F, -10.0F, 1.5F, 10.0F, 20.0F, new CubeDeformation(0.02F))
                // Heavy structural ribs wrapping the hull
                .texOffs(64, 96).addBox(-10.4F, -8.2F, -7.0F, 20.8F, 16.4F, 2.2F, new CubeDeformation(0.04F))
                .texOffs(64, 96).addBox(-10.4F, -8.2F,  5.0F, 20.8F, 16.4F, 2.2F, new CubeDeformation(0.04F)),
                PartPose.ZERO);

        // 2. Beveled Bow Dome + Armored Glass Viewport + Eye-Core + Jaw Guards
        PartDefinition bowDome = hullMain.addOrReplaceChild("bow_dome", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-8.0F, -6.5F, -7.5F, 16.0F, 13.0F, 7.5F)
                .texOffs(76, 8).addBox(-6.5F, -5.0F, -11.0F, 13.0F, 10.0F, 3.5F, new CubeDeformation(0.03F)),
                PartPose.offset(0.0F, 0.0F, -14.0F));

        bowDome.addOrReplaceChild("eye_iris_ring", CubeListBuilder.create()
                .texOffs(64, 96).addBox(-4.8F, -4.8F, -0.8F, 9.6F, 9.6F, 1.6F, new CubeDeformation(0.04F)),
                PartPose.offset(0.0F, 0.0F, -11.4F));

        bowDome.addOrReplaceChild("eye_core", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-3.4F, -3.4F, -1.0F, 6.8F, 6.8F, 1.8F),
                PartPose.offset(0.0F, 0.0F, -12.0F));

        bowDome.addOrReplaceChild("jaw_guard_top", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-6.5F, -2.2F, -6.5F, 13.0F, 2.2F, 6.5F, new CubeDeformation(0.03F)),
                PartPose.offset(0.0F, -5.5F, -4.0F));

        bowDome.addOrReplaceChild("jaw_guard_bot", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-6.5F, 0.0F, -6.5F, 13.0F, 2.2F, 6.5F, new CubeDeformation(0.03F)),
                PartPose.offset(0.0F, 5.5F, -4.0F));

        // 3. Conning Tower & Rotating Periscope
        PartDefinition tower = hullMain.addOrReplaceChild("conning_tower", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-4.5F, -6.5F, -6.0F, 9.0F, 6.5F, 12.0F)
                .texOffs(0, 96).addBox(-3.5F, -8.5F, -4.5F, 7.0F, 2.0F, 9.0F, new CubeDeformation(0.03F)),
                PartPose.offset(0.0F, -10.0F, -1.0F));

        tower.addOrReplaceChild("periscope_head", CubeListBuilder.create()
                .texOffs(64, 96).addBox(-1.2F, -5.0F, -1.2F, 2.4F, 5.0F, 2.4F)
                .texOffs(0, 64).addBox(-1.6F, -6.2F, -3.2F, 3.2F, 2.2F, 4.0F),
                PartPose.offset(0.0F, -8.5F, 0.0F));

        // 4. Layered Pectoral Hydro-Planes & Ballast Vents
        hullMain.addOrReplaceChild("fin_pectoral_l", CubeListBuilder.create()
                .texOffs(64, 96).addBox(-8.5F, -0.9F, -3.5F, 8.5F, 1.8F, 7.0F),
                PartPose.offsetAndRotation(-10.5F, 3.8F, -4.5F, 0.0F, 0.24F, -0.14F));

        hullMain.addOrReplaceChild("fin_pectoral_r", CubeListBuilder.create()
                .texOffs(64, 96).addBox(0.0F, -0.9F, -3.5F, 8.5F, 1.8F, 7.0F),
                PartPose.offsetAndRotation(10.5F, 3.8F, -4.5F, 0.0F, -0.24F, 0.14F));

        hullMain.addOrReplaceChild("ballast_vent_l", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-2.0F, -1.8F, -4.0F, 2.4F, 3.6F, 8.0F, new CubeDeformation(0.03F)),
                PartPose.offset(-10.2F, -5.2F, 6.0F));

        hullMain.addOrReplaceChild("ballast_vent_r", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-0.4F, -1.8F, -4.0F, 2.4F, 3.6F, 8.0F, new CubeDeformation(0.03F)),
                PartPose.offset(10.2F, -5.2F, 6.0F));

        // 5. Twin Multi-Tube Torpedo Pods with 6 live warheads (torpedo_tip_0..5)
        PartDefinition podL = hullMain.addOrReplaceChild("pod_left", CubeListBuilder.create()
                .texOffs(32, 64).addBox(-4.8F, -3.75F, -9.0F, 4.8F, 7.5F, 18.0F),
                PartPose.offset(-10.6F, 0.2F, 0.0F));
        podL.addOrReplaceChild("pod_hatch_l", CubeListBuilder.create()
                .texOffs(64, 96).addBox(-2.3F, -3.5F, -0.8F, 4.6F, 7.0F, 1.2F, new CubeDeformation(0.03F)),
                PartPose.offset(-2.4F, 0.0F, -9.2F));
        podL.addOrReplaceChild("torpedo_tip_0", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(-2.4F, -2.2F, -9.0F));
        podL.addOrReplaceChild("torpedo_tip_1", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(-2.4F,  0.0F, -9.0F));
        podL.addOrReplaceChild("torpedo_tip_2", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(-2.4F,  2.2F, -9.0F));

        PartDefinition podR = hullMain.addOrReplaceChild("pod_right", CubeListBuilder.create()
                .texOffs(32, 64).addBox(0.0F, -3.75F, -9.0F, 4.8F, 7.5F, 18.0F),
                PartPose.offset(10.6F, 0.2F, 0.0F));
        podR.addOrReplaceChild("pod_hatch_r", CubeListBuilder.create()
                .texOffs(64, 96).addBox(-2.3F, -3.5F, -0.8F, 4.6F, 7.0F, 1.2F, new CubeDeformation(0.03F)),
                PartPose.offset(2.4F, 0.0F, -9.2F));
        podR.addOrReplaceChild("torpedo_tip_3", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(2.4F, -2.2F, -9.0F));
        podR.addOrReplaceChild("torpedo_tip_4", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(2.4F,  0.0F, -9.0F));
        podR.addOrReplaceChild("torpedo_tip_5", CubeListBuilder.create().texOffs(96, 64).addBox(-1.1F, -1.1F, -2.8F, 2.2F, 2.2F, 3.2F), PartPose.offset(2.4F,  2.2F, -9.0F));

        // 6. Tapered Tail Cone, Rudders & Shrouded 4-Blade Brass Turbine
        PartDefinition tailCone = hullMain.addOrReplaceChild("tail_cone", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.0F, -5.5F, 0.0F, 14.0F, 11.0F, 8.0F)
                .texOffs(64, 96).addBox(-4.5F, -4.0F, 8.0F, 9.0F, 8.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 14.0F));

        tailCone.addOrReplaceChild("rudder_top", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-0.8F, -7.5F, 0.0F, 1.6F, 7.5F, 6.5F),
                PartPose.offset(0.0F, -5.5F, 3.5F));

        tailCone.addOrReplaceChild("rudder_bot", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-0.8F, 0.0F, 0.0F, 1.6F, 6.5F, 6.5F),
                PartPose.offset(0.0F, 5.5F, 3.5F));

        tailCone.addOrReplaceChild("propeller_shaft", CubeListBuilder.create()
                .texOffs(0, 96).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F)
                .texOffs(0, 96).addBox(-1.5F, -9.0F, 1.2F, 3.0F, 18.0F, 1.4F)
                .texOffs(0, 96).addBox(-9.0F, -1.5F, 1.2F, 18.0F, 3.0F, 1.4F)
                .texOffs(64, 96).addBox(-9.5F, -10.0F, 0.5F, 19.0F, 1.4F, 2.8F, new CubeDeformation(0.03F))
                .texOffs(64, 96).addBox(-9.5F,   8.6F, 0.5F, 19.0F, 1.4F, 2.8F, new CubeDeformation(0.03F))
                .texOffs(64, 96).addBox(-10.0F, -9.0F, 0.5F, 1.4F, 18.0F, 2.8F, new CubeDeformation(0.03F))
                .texOffs(64, 96).addBox(  8.6F, -9.0F, 0.5F, 1.4F, 18.0F, 2.8F, new CubeDeformation(0.03F)),
                PartPose.offset(0.0F, 0.0F, 14.0F));

        return LayerDefinition.create(mesh, 128, 128);
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
