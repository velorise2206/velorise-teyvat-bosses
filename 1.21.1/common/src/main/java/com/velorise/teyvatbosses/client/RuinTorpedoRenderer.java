package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinTorpedoModel;
import com.velorise.teyvatbosses.entity.RuinTorpedoProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders {@link RuinTorpedoProjectile} using the baked solid 3D {@link RuinTorpedoModel}
 * (dedicated 64x64 diffuse + emissive textures, spinning 4-blade bronze tail propeller,
 * and additive Hydro plasma wake).
 */
public class RuinTorpedoRenderer extends EntityRenderer<RuinTorpedoProjectile> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_torpedo.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_torpedo_emissive.png");

    private final RuinTorpedoModel<RuinTorpedoProjectile> model;

    public RuinTorpedoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new RuinTorpedoModel<>(ctx.bakeLayer(RuinTorpedoModel.LAYER_LOCATION));
    }

    @Override
    public void render(RuinTorpedoProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float age = entity.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));

        if (model.propeller != null) {
            model.propeller.zRot = age * 0.85F;
        }

        // Render true solid 3D baked torpedo model (diffuse + emissive warhead)
        poseStack.pushPose();
        poseStack.scale(-0.95F, -0.95F, 0.95F);
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        VertexConsumer evc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
        model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poseStack.popPose();

        // Subtle additive Hydro plasma wake behind spinning tail propeller
        Matrix4f m = poseStack.last().pose();
        VertexConsumer lvc = buffers.getBuffer(RenderType.lightning());
        float r = 0.08F + 0.02F * Mth.sin(age * 0.9F);
        lvc.addVertex(m, -r, 0, -0.55F).setColor(90, 245, 255, 160);
        lvc.addVertex(m,  r, 0, -0.55F).setColor(90, 245, 255, 160);
        lvc.addVertex(m,  0.02F, 0, -1.45F).setColor(60, 220, 255, 18);
        lvc.addVertex(m, -0.02F, 0, -1.45F).setColor(60, 220, 255, 18);

        lvc.addVertex(m, 0, -r, -0.55F).setColor(90, 245, 255, 160);
        lvc.addVertex(m, 0,  r, -0.55F).setColor(90, 245, 255, 160);
        lvc.addVertex(m, 0,  0.02F, -1.45F).setColor(60, 220, 255, 18);
        lvc.addVertex(m, 0, -0.02F, -1.45F).setColor(60, 220, 255, 18);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinTorpedoProjectile entity) {
        return TEXTURE;
    }
}
