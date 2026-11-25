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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Native Mojang 3D model for Genshin Impact's Trounce Blossom (Ley Line Reward Flower).
 * Features a twisted ivory-gold Ley Line root stem, 6 emerald-slate calyx sepals,
 * 6 curved midnight-plum petals that swirl closed as a bud and bloom open upon interaction,
 * and a luminous cyan-gold Ley Line core orb inside.
 */
public class TrounceBlossomModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("trounce_blossom"), "main");

    public final ModelPart root;
    public final ModelPart stem;
    public final ModelPart head;
    public final ModelPart heartCore;
    public final ModelPart[] petals = new ModelPart[6];

    public TrounceBlossomModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        this.stem = this.root.getChild("stem");
        this.head = this.stem.getChild("head");
        this.heartCore = this.head.getChild("heart_core");
        for (int i = 0; i < 6; i++) {
            this.petals[i] = this.head.getChild("petal_" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();

        PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        // Twisted Ley Line root pedestal & organic curved stem
        PartDefinition stem = root.addOrReplaceChild("stem",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.5F, -4.0F, -5.5F, 11.0F, 4.0F, 11.0F)
                        .texOffs(0, 15).addBox(-3.5F, -12.0F, -3.5F, 7.0F, 9.0F, 7.0F)
                        .texOffs(0, 31).addBox(-4.5F, -15.0F, -4.5F, 9.0F, 4.0F, 9.0F),
                PartPose.ZERO);

        // Calyx & flower head pivot
        PartDefinition head = stem.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(64, 0).addBox(-5.5F, -3.0F, -5.5F, 11.0F, 4.0F, 11.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, -14.5F, 0.0F));

        // Glowing Ley Line heart orb
        head.addOrReplaceChild("heart_core",
                CubeListBuilder.create()
                        .texOffs(64, 48).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -5.5F, 0.0F, 0.7854F, 0.0F, 0.7854F));

        // 6 Curved Swirled Petals
        for (int i = 0; i < 6; i++) {
            float yaw = (float) (i * Math.PI / 3.0D);
            float px = Mth.sin(yaw) * 4.8F;
            float pz = Mth.cos(yaw) * 4.8F;
            head.addOrReplaceChild("petal_" + i,
                    CubeListBuilder.create()
                            .texOffs(0, 48).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 11.0F, 2.0F)
                            .texOffs(20, 48).addBox(-2.8F, -17.5F, -0.75F, 5.6F, 7.0F, 1.5F),
                    PartPose.offsetAndRotation(px, -1.0F, pz, -0.42F, yaw, 0.14F));
        }

        return LayerDefinition.create(mesh, 128, 128);
    }

    public void applyBlossomPose(float ageSec, float emergeProgress, float bloomProgress) {
        // Emerge smoothly from ground
        float e = Mth.clamp(emergeProgress, 0.0F, 1.0F);
        float easeOut = 1.0F - (1.0F - e) * (1.0F - e) * (1.0F - e);
        this.root.xScale = easeOut;
        this.root.yScale = easeOut;
        this.root.zScale = easeOut;

        // Gentle wind sway on stem & head
        this.stem.zRot = Mth.sin(ageSec * 1.4F) * 0.06F;
        this.stem.xRot = Mth.cos(ageSec * 1.1F) * 0.05F;
        this.head.yRot = ageSec * (0.25F + bloomProgress * 0.75F);

        // Glowing heart pulses & rises slightly when bloomed
        float heartScale = 0.85F + 0.35F * bloomProgress + 0.08F * Mth.sin(ageSec * 4.2F);
        this.heartCore.xScale = heartScale;
        this.heartCore.yScale = heartScale;
        this.heartCore.zScale = heartScale;
        this.heartCore.y = -5.5F - bloomProgress * 3.0F;
        this.heartCore.yRot = ageSec * 1.8F;

        // Petals swirl closed (-0.42 rad) when unclaimed, and bloom wide open (+0.98 rad) when claimed!
        float b = Mth.clamp(bloomProgress, 0.0F, 1.0F);
        float bSmooth = b * b * (3.0F - 2.0F * b);
        for (int i = 0; i < 6; i++) {
            float sway = Mth.sin(ageSec * 2.2F + i * 1.05F) * 0.045F;
            this.petals[i].xRot = Mth.lerp(bSmooth, -0.42F, 0.96F) + sway;
            this.petals[i].zRot = Mth.lerp(bSmooth, 0.14F, 0.02F);
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Driven by applyBlossomPose in TrounceBlossomRenderer.
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
