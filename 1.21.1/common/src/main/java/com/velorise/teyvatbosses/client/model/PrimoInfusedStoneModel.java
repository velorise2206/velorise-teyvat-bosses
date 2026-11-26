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
 * Sleek Tapered Hollowed-Out ("Đục Ruột") Elemental-Infused Stone Obelisk model
 * matching Genshin Impact's Primo Geovishap monolith shards (2.mp4 00:28–00:30, 01:11–01:13):
 * <ul>
 *   <li>{@code rock_shell}: Compact jagged dark-slate base + 4 sleek inward-tapering stone blades
 *       forming a sharp pointed hollow obelisk with luminous open channels on all 4 sides.</li>
 *   <li>{@code crystal_core}: Tall pointed elemental crystal spear suspended &amp; pulsing inside the
 *       hollowed-out stone obelisk.</li>
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
        // 1. SLEEK TAPERED HOLLOWED-OUT DARK-SLATE OBELISK SHELL
        // =====================================================================
        PartDefinition shell = root.addOrReplaceChild("rock_shell", CubeListBuilder.create()
                // Compact stepped basalt base
                .texOffs(64, 0).addBox(-6.0F, -2.5F, -6.0F, 12.0F, 2.5F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.8F, -4.5F, -4.8F, 9.6F, 2.5F, 9.6F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // 4 sleek inward-tilted dark-slate obelisk blades framing a sharp hollowed-out pyramid cavity!
        float[][] blades = {
                {-3.8F, -3.8F,  0.16F,  0.16F},
                { 3.8F, -3.8F,  0.16F, -0.16F},
                {-3.8F,  3.8F, -0.16F,  0.16F},
                { 3.8F,  3.8F, -0.16F, -0.16F}
        };
        for (int i = 0; i < 4; i++) {
            float bx = blades[i][0], bz = blades[i][1];
            float rx = blades[i][2], rz = blades[i][3];
            shell.addOrReplaceChild("blade_" + i, CubeListBuilder.create()
                    // Lower tapered dark-slate flank
                    .texOffs(0, 0).addBox(-1.9F, -19.0F, -1.9F, 3.8F, 16.5F, 3.8F, new CubeDeformation(0.0F))
                    // Mid converging obelisk shoulder
                    .texOffs(0, 0).addBox(-1.45F, -29.0F, -1.45F, 2.9F, 11.0F, 2.9F, new CubeDeformation(0.0F))
                    // Sharp pointed apex shard
                    .texOffs(64, 0).addBox(-0.95F, -35.5F, -0.95F, 1.9F, 7.5F, 1.9F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(bx, -3.0F, bz, rx, 0.7854F, rz));
        }

        // 3 compact angular ground shards burst around the obelisk foot
        shell.addOrReplaceChild("spur_0", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -7.5F, -1.5F, 3.0F, 8.0F, 3.0F),
                PartPose.offsetAndRotation(-5.6F, -0.5F, 1.4F, 0.16F, 0.35F, -0.34F));
        shell.addOrReplaceChild("spur_1", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-1.4F, -6.8F, -1.4F, 2.8F, 7.2F, 2.8F),
                PartPose.offsetAndRotation(5.4F, -0.5F, -2.0F, -0.18F, -0.45F, 0.32F));
        shell.addOrReplaceChild("spur_2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.4F, -6.0F, -1.4F, 2.8F, 6.5F, 2.8F),
                PartPose.offsetAndRotation(0.8F, -0.5F, 5.5F, 0.36F, 0.2F, 0.12F));

        // =====================================================================
        // 2. SUSPENDED POINTED ELEMENTAL CRYSTAL CORE (glows through the 4 hollow slits!)
        // =====================================================================
        PartDefinition core = root.addOrReplaceChild("crystal_core", CubeListBuilder.create()
                // Tall central glowing elemental core prism
                .texOffs(0, 64).addBox(-2.2F, -10.5F, -2.2F, 4.4F, 21.0F, 4.4F, new CubeDeformation(0.0F))
                // Sharp crystal spire tip extending cleanly up into the open obelisk crown
                .texOffs(64, 64).addBox(-1.3F, -16.5F, -1.3F, 2.6F, 27.0F, 2.6F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 7.0F, 0.0F));

        core.addOrReplaceChild("core_facet", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-1.5F, -7.5F, -1.5F, 3.0F, 15.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(PrimoInfusedStoneEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float urgency = Mth.clamp(1.0F - (entity.getFuseLeft() / (float) entity.getFuseMax()), 0.0F, 1.0F);
        float spinSpeed = 0.08F + urgency * 0.26F;
        this.crystalCore.yRot = ageInTicks * spinSpeed;
        this.crystalCore.y = 7.0F + Mth.sin(ageInTicks * (0.20F + urgency * 0.50F)) * 1.15F;
        // Subtle organic obelisk lean like Genshin's jagged stones
        this.rockShell.zRot = 0.06F;
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
