package com.velorise.teyvatbosses.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.PrimoInfusedStoneEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 3D Hollowed-Out ("Đục Ruột") Elemental-Infused Stone Monolith model:
 * <ul>
 *   <li>{@code rock_shell}: Grounded jagged basalt plinth + 4 arched outer stone ribs &amp; crown jaws
 *       framing 4 wide carved-out openings so the hollow interior cavity is clearly visible from all sides.</li>
 *   <li>{@code crystal_core}: Levitating, rotating faceted elemental crystal core suspended inside the
 *       hollowed-out stone cavity, rendered separately with full-bright elemental tinting.</li>
 * </ul>
 */
public class PrimoInfusedStoneModel extends EntityModel<PrimoInfusedStoneEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("primo_infused_stone"), "main");

    private final ModelPart rockShell;
    private final ModelPart crystalCore;

    public PrimoInfusedStoneModel(ModelPart root) {
        this.rockShell = root.getChild("rock_shell");
        this.crystalCore = root.getChild("crystal_core");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // =====================================================================
        // 1. HOLLOWED-OUT ROCK CARAPACE (4 thick corner ribs + open windows + base plinth)
        // =====================================================================
        PartDefinition shell = root.addOrReplaceChild("rock_shell", CubeListBuilder.create()
                // Solid rugged limestone/basalt foundation plinth
                .texOffs(64, 0).addBox(-9.5F, -4.0F, -9.5F, 19.0F, 4.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, -6.5F, -8.0F, 16.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // 4 angled corner rock ribs forming the hollowed-out cage with wide open windows on N/S/E/W!
        float[][] corners = {
                {-6.8F, -6.8F,  0.14F,  0.14F},
                { 6.8F, -6.8F,  0.14F, -0.14F},
                {-6.8F,  6.8F, -0.14F,  0.14F},
                { 6.8F,  6.8F, -0.14F, -0.14F}
        };
        for (int i = 0; i < 4; i++) {
            float cx = corners[i][0], cz = corners[i][1];
            float rx = corners[i][2], rz = corners[i][3];
            shell.addOrReplaceChild("rib_" + i, CubeListBuilder.create()
                    // Thick carved stone corner pillar
                    .texOffs(0, 0).addBox(-2.6F, -22.0F, -2.6F, 5.2F, 17.5F, 5.2F, new CubeDeformation(0.0F))
                    // Upper inward-arching stone tooth framing the open top chimney
                    .texOffs(64, 0).addBox(-2.0F, -29.5F, -2.0F, 4.0F, 8.5F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(cx, -4.0F, cz, rx, 0.7854F * (i % 2), rz));
        }

        // 3 jagged outer rubble spurs burst out around the base
        shell.addOrReplaceChild("spur_0", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -10.5F, -2.5F, 5.0F, 11.0F, 5.0F),
                PartPose.offsetAndRotation(-9.2F, -1.0F, 2.0F, 0.18F, 0.35F, -0.42F));
        shell.addOrReplaceChild("spur_1", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-2.2F, -9.5F, -2.2F, 4.4F, 10.0F, 4.4F),
                PartPose.offsetAndRotation(9.0F, -1.0F, -3.2F, -0.22F, -0.5F, 0.38F));
        shell.addOrReplaceChild("spur_2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.4F, -8.5F, -2.4F, 4.8F, 9.0F, 4.8F),
                PartPose.offsetAndRotation(1.5F, -1.0F, 9.4F, 0.44F, 0.2F, 0.15F));

        // =====================================================================
        // 2. SUSPENDED HOLLOW-INTERIOR ELEMENTAL CRYSTAL CORE (levitates & spins inside!)
        // =====================================================================
        PartDefinition core = root.addOrReplaceChild("crystal_core", CubeListBuilder.create()
                // Central faceted crystal diamond pillar
                .texOffs(0, 64).addBox(-3.2F, -9.0F, -3.2F, 6.4F, 18.0F, 6.4F, new CubeDeformation(0.0F))
                // Cross-rotated inner crystal prism for rich 8-faceted silhouette
                .texOffs(64, 64).addBox(-2.4F, -11.5F, -2.4F, 4.8F, 23.0F, 4.8F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        core.addOrReplaceChild("core_orbit_a", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-1.6F, -5.0F, -1.6F, 3.2F, 10.0F, 3.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.32F, 0.7854F, 0.24F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(PrimoInfusedStoneEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float urgency = Mth.clamp(1.0F - (entity.getFuseLeft() / (float) entity.getFuseMax()), 0.0F, 1.0F);
        float spinSpeed = 0.09F + urgency * 0.28F;
        this.crystalCore.yRot = ageInTicks * spinSpeed;
        this.crystalCore.y = 8.0F + Mth.sin(ageInTicks * (0.22F + urgency * 0.55F)) * 1.45F;
        this.rockShell.yRot = 0.0F;
    }

    public void renderRockShell(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.rockShell.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void renderCrystalCore(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.crystalCore.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.rockShell.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.crystalCore.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
