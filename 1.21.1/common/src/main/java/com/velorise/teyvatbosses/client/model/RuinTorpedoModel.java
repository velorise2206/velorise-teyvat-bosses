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

/** Solid 3D Native Mojang Model for RuinTorpedoProjectile (Fontaine Clockwork Torpedo). */
public class RuinTorpedoModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("ruin_torpedo"), "main");

    public final ModelPart root;
    public final ModelPart propeller;

    public RuinTorpedoModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        this.propeller = this.root.hasChild("propeller") ? this.root.getChild("propeller") : null;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
                .texOffs(1, 1).addBox(-2.000F, -2.000F, -5.000F, 4.0F, 4.0F, 10.0F)
                .texOffs(39, 1).addBox(-2.500F, -2.500F, -4.000F, 5.0F, 5.0F, 2.0F, new CubeDeformation(-0.020F))
                .texOffs(1, 16).addBox(-2.500F, -2.500F, 2.000F, 5.0F, 5.0F, 2.0F, new CubeDeformation(-0.020F))
                .texOffs(21, 16).addBox(-1.500F, -1.500F, 4.500F, 3.0F, 3.0F, 3.0F)
                .texOffs(22, 24).addBox(-1.000F, -1.000F, 7.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(34, 16).addBox(-1.500F, -1.500F, -8.000F, 3.0F, 3.0F, 3.0F)
                .texOffs(30, 1).addBox(-0.500F, -3.500F, -7.500F, 1.0F, 7.0F, 3.0F, new CubeDeformation(-0.020F))
                .texOffs(1, 24).addBox(-3.500F, -0.500F, -7.500F, 7.0F, 1.0F, 3.0F, new CubeDeformation(-0.030F))
                , PartPose.offset(0.000F, -0.000F, 0.000F));
        PartDefinition b_propeller = b_root.addOrReplaceChild("propeller", CubeListBuilder.create()
                .texOffs(31, 24).addBox(-1.000F, -1.000F, -1.000F, 2.0F, 2.0F, 2.0F)
                .texOffs(16, 16).addBox(-0.500F, -3.000F, -0.800F, 1.0F, 6.0F, 1.0F, new CubeDeformation(-0.020F))
                .texOffs(40, 24).addBox(-3.000F, -0.500F, -0.800F, 6.0F, 1.0F, 1.0F, new CubeDeformation(-0.030F))
                , PartPose.offset(0.000F, -0.000F, -8.500F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
