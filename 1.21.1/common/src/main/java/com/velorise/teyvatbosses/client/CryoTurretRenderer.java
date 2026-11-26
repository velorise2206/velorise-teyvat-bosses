package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.CryoRegisvineAnimations;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.entity.CryoTurretEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders the 3x6x3 miniature Cryo Regisvine Turret using {@link CryoRegisvineModel} scaled to 0.86x,
 * toggling Root / Stem / Corolla bone groups dynamically during 3-part outdoor assembly.
 */
public class CryoTurretRenderer extends EntityRenderer<CryoTurretEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_regisvine_boss.png");
    private static final float TURRET_SCALE = 0.86F;

    private final CryoRegisvineModel<CryoTurretEntity> model;

    public CryoTurretRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.25F;
        this.model = new CryoRegisvineModel<>(context.bakeLayer(CryoRegisvineModel.LAYER_LOCATION));
    }

    @Override
    public void render(CryoTurretEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float sec = (entity.getAnimTick() + partialTicks) / 20.0F;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-TURRET_SCALE, -TURRET_SCALE, TURRET_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        CryoRegisvineAnimations.apply(model, entity.getBaseClipName(), sec);

        // Toggle 3 modular assembly bone groups (Root -> Stem -> Corolla)
        setBoneVisible("stem_1", entity.hasPart(CryoTurretEntity.PART_STEM));
        setBoneVisible("corolla_head", entity.hasPart(CryoTurretEntity.PART_COROLLA));
        setBoneVisible("frost_ring", entity.getAnimId() == CryoTurretEntity.ANIM_FROST_NOVA);
        setBoneVisible("lash_beam_l", false);
        setBoneVisible("lash_beam_r", false);
        setBoneVisible("rotary_beam", false);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Restore visibility flags
        setBoneVisible("stem_1", true);
        setBoneVisible("corolla_head", true);
        setBoneVisible("frost_ring", true);
        setBoneVisible("lash_beam_l", true);
        setBoneVisible("lash_beam_r", true);
        setBoneVisible("rotary_beam", true);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void setBoneVisible(String name, boolean vis) {
        ModelPart part = model.getBone(name);
        if (part != null) part.visible = vis;
    }

    @Override
    public ResourceLocation getTextureLocation(CryoTurretEntity entity) {
        return TEXTURE;
    }
}
