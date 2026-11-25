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
 * Native Mojang hierarchical model for Anemo Hypostasis (Beth).
 * Built around 8 rune-carved outer shell cubes (2x2x2 dormant cube), a glowing faceted
 * Anemo crystal core, a rotating wireframe cage, and 4 orbiting mini energy cubes.
 */
public class AnemoHypostasisModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("anemo_hypostasis"), "main");

    public final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public AnemoHypostasisModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        ModelPart bodyPivot = this.root.getChild("body_pivot");
        registerBone("root", this.root);
        registerBone("body_pivot", bodyPivot);
        registerBone("core_group", bodyPivot.getChild("core_group"));
        registerBone("core_frame", bodyPivot.getChild("core_frame"));

        ModelPart shellRing = bodyPivot.getChild("shell_ring");
        registerBone("shell_ring", shellRing);
        for (int i = 0; i < 8; i++) {
            registerBone("shell_" + i, shellRing.getChild("shell_" + i));
        }
        for (int i = 0; i < 4; i++) {
            registerBone("mini_" + i, bodyPivot.getChild("mini_" + i));
        }
    }

    private void registerBone(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
    }

    public ModelPart getBone(String name) {
        return bones.get(name);
    }

    public void resetPose() {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            PartPose pose = initialPoses.get(e.getKey());
            if (pose != null) {
                e.getValue().loadPose(pose);
            }
            e.getValue().visible = true;
            e.getValue().xScale = 1.0F;
            e.getValue().yScale = 1.0F;
            e.getValue().zScale = 1.0F;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();

        // MC model space: Y=24 is ground; negative Y goes upward.
        PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bodyPivot = root.addOrReplaceChild("body_pivot", CubeListBuilder.create(), PartPose.offset(0.0F, -20.0F, 0.0F));

        // Inner glowing Anemo crystal core (octahedron silhouette via 45-deg rotated crystal cube + inner heart)
        bodyPivot.addOrReplaceChild("core_group",
                CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, CubeDeformation.NONE)
                        .texOffs(48, 64).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7853982F, 0.0F, 0.7853982F));

        // Rotating luminous wireframe cage around core
        bodyPivot.addOrReplaceChild("core_frame",
                CubeListBuilder.create()
                        .texOffs(0, 96).addBox(-8.5F, -8.5F, -0.5F, 17.0F, 1.0F, 1.0F)
                        .texOffs(0, 96).addBox(-8.5F, 7.5F, -0.5F, 17.0F, 1.0F, 1.0F)
                        .texOffs(0, 96).addBox(-8.5F, -7.5F, -0.5F, 1.0F, 15.0F, 1.0F)
                        .texOffs(0, 96).addBox(7.5F, -7.5F, -0.5F, 1.0F, 15.0F, 1.0F)
                        .texOffs(0, 96).addBox(-0.5F, -8.5F, -8.5F, 1.0F, 1.0F, 17.0F)
                        .texOffs(0, 96).addBox(-0.5F, 7.5F, -8.5F, 1.0F, 1.0F, 17.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));

        // 8 Outer Shell Cubes (16x16x16 each; resting in a tight 2x2x2 cube with 2-unit central seam)
        PartDefinition shellRing = bodyPivot.addOrReplaceChild("shell_ring", CubeListBuilder.create(), PartPose.ZERO);
        float[][] shellOffsets = {
                {-9.0F, -9.0F, -9.0F, 0,  0},   // 0: top-right-front
                { 9.0F, -9.0F, -9.0F, 0,  32},  // 1: top-left-front
                {-9.0F, -9.0F,  9.0F, 64, 0},   // 2: top-right-back
                { 9.0F, -9.0F,  9.0F, 64, 32},  // 3: top-left-back
                {-9.0F,  9.0F, -9.0F, 64, 32},  // 4: bot-right-front
                { 9.0F,  9.0F, -9.0F, 64, 0},   // 5: bot-left-front
                {-9.0F,  9.0F,  9.0F, 0,  32},  // 6: bot-right-back
                { 9.0F,  9.0F,  9.0F, 0,  0},   // 7: bot-left-back
        };
        for (int i = 0; i < 8; i++) {
            float[] s = shellOffsets[i];
            shellRing.addOrReplaceChild("shell_" + i,
                    CubeListBuilder.create()
                            .texOffs((int) s[3], (int) s[4])
                            .addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, CubeDeformation.NONE),
                    PartPose.offset(s[0], s[1], s[2]));
        }

        // 4 Small orbiting energy cubes
        float[][] miniOffsets = {
                {-22.0F, -4.0F, 0.0F},
                { 22.0F, -4.0F, 0.0F},
                {  0.0F, -4.0F, -22.0F},
                {  0.0F, -4.0F,  22.0F}
        };
        for (int i = 0; i < 4; i++) {
            bodyPivot.addOrReplaceChild("mini_" + i,
                    CubeListBuilder.create()
                            .texOffs(80, 64)
                            .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE),
                    PartPose.offset(miniOffsets[i][0], miniOffsets[i][1], miniOffsets[i][2]));
        }

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Driven by AnemoHypostasisAnimations in AnemoHypostasisRenderer for 60/144 FPS pose blending.
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
