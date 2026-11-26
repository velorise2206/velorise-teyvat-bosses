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
import net.minecraft.world.entity.LivingEntity;

/**
 * 3D Wearable Anemo Jet-Wing Model (inspired by Iron Jetpacks, Create Backtank & Mondstadt Wind-Wings):
 * <ul>
 *   <li>Minimal gold-trimmed shoulder harness straps wrapping the player's torso.</li>
 *   <li>Central diamond-tilted Anemo Hypostasis Core housing.</li>
 *   <li>Dual angled aerodynamic thruster pods with tapered intake caps & flared nozzles.</li>
 *   <li>Articulated 3-feather swept Wind-Wings that flare open while airborne/boosting.</li>
 *   <li>Dynamic thruster flame cones shown when actively boosting.</li>
 * </ul>
 */
public class AnemoJetpackModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("anemo_jetpack"), "main");

    private final ModelPart root;
    private final ModelPart coreCrystal;
    private final ModelPart wingLeft;
    private final ModelPart wingRight;
    private final ModelPart flameLeft;
    private final ModelPart flameRight;

    public AnemoJetpackModel(ModelPart root) {
        this.root = root.getChild("jetpack_root");
        this.coreCrystal = this.root.getChild("core_crystal");
        this.wingLeft = this.root.getChild("wing_left");
        this.wingRight = this.root.getChild("wing_right");
        this.flameLeft = this.root.getChild("pod_left").getChild("flame_left");
        this.flameRight = this.root.getChild("pod_right").getChild("flame_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDef = mesh.getRoot();

        // Anchored to player body origin (0, 0, 0); back of player torso is +Z = +2.0px
        PartDefinition root = partDef.addOrReplaceChild("jetpack_root", CubeListBuilder.create()
                // Contoured shoulder harness straps & chest clasp (sits snugly on player skin)
                .texOffs(0, 0).addBox(-4.2F, 0.2F, -2.3F, 1.4F, 5.5F, 4.6F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(2.8F, 0.2F, -2.3F, 1.4F, 5.5F, 4.6F, new CubeDeformation(0.05F))
                .texOffs(0, 12).addBox(-3.5F, 3.8F, -2.4F, 7.0F, 1.4F, 1.0F, new CubeDeformation(0.05F))
                // Sleek central spine mount plate on back
                .texOffs(16, 0).addBox(-3.0F, 1.4F, 2.0F, 6.0F, 7.2F, 2.2F)
                // Gold top/bottom mounting braces
                .texOffs(16, 12).addBox(-4.5F, 2.2F, 2.6F, 9.0F, 1.2F, 1.8F)
                .texOffs(16, 16).addBox(-4.5F, 6.8F, 2.6F, 9.0F, 1.2F, 1.8F),
                PartPose.ZERO);

        // Diamond-tilted glowing Anemo Hypostasis Core in center back
        root.addOrReplaceChild("core_crystal", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-1.8F, -1.8F, -1.2F, 3.6F, 3.6F, 2.4F),
                PartPose.offsetAndRotation(0.0F, 5.0F, 4.4F, 0.0F, 0.0F, 0.7854F));

        // Left angled aerodynamic thruster pod (angled outward & slightly swept back)
        PartDefinition podL = root.addOrReplaceChild("pod_left", CubeListBuilder.create()
                .texOffs(34, 0).addBox(-1.6F, -4.0F, -1.6F, 3.2F, 8.2F, 3.2F)
                // Tapered aerodynamic intake cap
                .texOffs(34, 14).addBox(-1.2F, -5.6F, -1.2F, 2.4F, 1.8F, 2.4F)
                // Gold collar ring
                .texOffs(34, 20).addBox(-1.8F, -2.8F, -1.8F, 3.6F, 1.1F, 3.6F)
                // Flared thruster nozzle
                .texOffs(34, 26).addBox(-1.9F, 4.0F, -1.9F, 3.8F, 1.5F, 3.8F),
                PartPose.offsetAndRotation(-4.2F, 5.2F, 4.2F, 0.14F, -0.18F, 0.16F));

        podL.addOrReplaceChild("flame_left", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-1.2F, 0.0F, -1.2F, 2.4F, 5.5F, 2.4F),
                PartPose.offset(0.0F, 5.4F, 0.0F));

        // Right angled aerodynamic thruster pod (mirrored)
        PartDefinition podR = root.addOrReplaceChild("pod_right", CubeListBuilder.create()
                .texOffs(34, 0).addBox(-1.6F, -4.0F, -1.6F, 3.2F, 8.2F, 3.2F)
                .texOffs(34, 14).addBox(-1.2F, -5.6F, -1.2F, 2.4F, 1.8F, 2.4F)
                .texOffs(34, 20).addBox(-1.8F, -2.8F, -1.8F, 3.6F, 1.1F, 3.6F)
                .texOffs(34, 26).addBox(-1.9F, 4.0F, -1.9F, 3.8F, 1.5F, 3.8F),
                PartPose.offsetAndRotation(4.2F, 5.2F, 4.2F, 0.14F, 0.18F, -0.16F));

        podR.addOrReplaceChild("flame_right", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-1.2F, 0.0F, -1.2F, 2.4F, 5.5F, 2.4F),
                PartPose.offset(0.0F, 5.4F, 0.0F));

        // Articulated 3-feather swept Mondstadt Anemo Wind-Wings (left & right)
        root.addOrReplaceChild("wing_left", CubeListBuilder.create()
                .texOffs(0, 34).addBox(-9.0F, -1.2F, -0.5F, 9.0F, 2.2F, 0.9F)
                .texOffs(0, 40).addBox(-7.4F, 1.0F, -0.4F, 7.2F, 1.8F, 0.8F)
                .texOffs(0, 46).addBox(-5.2F, 2.8F, -0.3F, 5.0F, 1.5F, 0.7F),
                PartPose.offsetAndRotation(-4.6F, 3.2F, 4.6F, 0.18F, 0.52F, -0.22F));

        root.addOrReplaceChild("wing_right", CubeListBuilder.create()
                .texOffs(0, 34).addBox(0.0F, -1.2F, -0.5F, 9.0F, 2.2F, 0.9F)
                .texOffs(0, 40).addBox(0.2F, 1.0F, -0.4F, 7.2F, 1.8F, 0.8F)
                .texOffs(0, 46).addBox(0.2F, 2.8F, -0.3F, 5.0F, 1.5F, 0.7F),
                PartPose.offsetAndRotation(4.6F, 3.2F, 4.6F, 0.18F, -0.52F, 0.22F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void updatePose(boolean airborne, boolean boosting, float ageTicks) {
        // Core crystal gently spins faster while boosting
        coreCrystal.zRot = 0.7854F + ageTicks * (boosting ? 0.22F : 0.045F);

        // Swept Wind-Wings flare open smoothly while flying/boosting and fold sleekly on ground
        float flare = boosting ? 1.0F : (airborne ? 0.55F : 0.0F);
        float flutter = boosting ? Mth.sin(ageTicks * 1.4F) * 0.08F : Mth.sin(ageTicks * 0.14F) * 0.03F;
        wingLeft.yRot  = Mth.lerp(flare, 0.72F, 0.16F) + flutter;
        wingLeft.zRot  = Mth.lerp(flare, -0.38F, -0.05F);
        wingRight.yRot = -Mth.lerp(flare, 0.72F, 0.16F) - flutter;
        wingRight.zRot = Mth.lerp(flare, 0.38F, 0.05F);

        // Dynamic pulsing thruster plumes only when boosting
        flameLeft.visible = boosting;
        flameRight.visible = boosting;
        if (boosting) {
            float flicker = 0.85F + 0.35F * Mth.sin(ageTicks * 2.6F);
            flameLeft.yScale = flicker;
            flameRight.yScale = 0.85F + 0.35F * Mth.cos(ageTicks * 2.6F);
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
