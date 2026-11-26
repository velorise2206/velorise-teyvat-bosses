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

/** Solid 3D Native Mojang Model for Ruin Nautilus Acoustic Depth Mines. */
public class DepthMineModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("ruin_depth_mine"), "main");

    public final ModelPart root;
    public final ModelPart propeller;

    public DepthMineModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        this.propeller = this.root.hasChild("propeller") ? this.root.getChild("propeller") : null;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
                .texOffs(1, 1).addBox(-5.000F, -5.000F, -5.000F, 10.0F, 10.0F, 10.0F)
                .texOffs(75, 1).addBox(-5.500F, -1.500F, -5.500F, 11.0F, 3.0F, 11.0F, new CubeDeformation(-0.020F))
                .texOffs(42, 1).addBox(-4.000F, -5.500F, -4.000F, 8.0F, 11.0F, 8.0F, new CubeDeformation(-0.020F))
                .texOffs(1, 22).addBox(-1.500F, -7.000F, -1.500F, 3.0F, 2.0F, 3.0F)
                .texOffs(14, 22).addBox(-1.500F, 5.000F, -1.500F, 3.0F, 2.0F, 3.0F)
                .texOffs(85, 22).addBox(-8.000F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                .texOffs(107, 22).addBox(-8.500F, -0.500F, -0.500F, 1.0F, 1.0F, 1.0F)
                .texOffs(96, 22).addBox(5.000F, -1.000F, -1.000F, 3.0F, 2.0F, 2.0F)
                .texOffs(112, 22).addBox(7.500F, -0.500F, -0.500F, 1.0F, 1.0F, 1.0F)
                .texOffs(27, 22).addBox(-1.000F, -1.000F, -8.000F, 2.0F, 2.0F, 3.0F)
                .texOffs(117, 22).addBox(-0.500F, -0.500F, -8.500F, 1.0F, 1.0F, 1.0F)
                .texOffs(38, 22).addBox(-1.000F, -1.000F, 5.000F, 2.0F, 2.0F, 3.0F)
                .texOffs(122, 22).addBox(-0.500F, -0.500F, 7.500F, 1.0F, 1.0F, 1.0F)
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_diag_horn_0 = b_root.addOrReplaceChild("diag_horn_0", CubeListBuilder.create()
                .texOffs(49, 22).addBox(-1.000F, -7.700F, -1.000F, 2.0F, 3.0F, 2.0F)
                .texOffs(1, 28).addBox(-0.500F, -8.300F, -0.500F, 1.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.7854F, -0.0000F, 0.7854F));
        PartDefinition b_diag_horn_1 = b_root.addOrReplaceChild("diag_horn_1", CubeListBuilder.create()
                .texOffs(58, 22).addBox(-1.000F, -7.700F, -1.000F, 2.0F, 3.0F, 2.0F)
                .texOffs(6, 28).addBox(-0.500F, -8.300F, -0.500F, 1.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, -0.7854F, -0.0000F, -0.7854F));
        PartDefinition b_diag_horn_2 = b_root.addOrReplaceChild("diag_horn_2", CubeListBuilder.create()
                .texOffs(67, 22).addBox(-1.000F, -7.700F, -1.000F, 2.0F, 3.0F, 2.0F)
                .texOffs(11, 28).addBox(-0.500F, -8.300F, -0.500F, 1.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, 0.7854F, -0.0000F, 0.7854F));
        PartDefinition b_diag_horn_3 = b_root.addOrReplaceChild("diag_horn_3", CubeListBuilder.create()
                .texOffs(76, 22).addBox(-1.000F, -7.700F, -1.000F, 2.0F, 3.0F, 2.0F)
                .texOffs(16, 28).addBox(-0.500F, -8.300F, -0.500F, 1.0F, 1.0F, 1.0F)
                , PartPose.offsetAndRotation(0.000F, -0.000F, 0.000F, 0.7854F, -0.0000F, -0.7854F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
