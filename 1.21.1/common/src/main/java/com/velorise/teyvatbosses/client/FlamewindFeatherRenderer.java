package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.entity.FlamewindFeatherEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * Renders {@link FlamewindFeatherEntity} as a spinning glowing Pyro feather pillar with a golden heat aura.
 */
public class FlamewindFeatherRenderer extends EntityRenderer<FlamewindFeatherEntity> {
    public FlamewindFeatherRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(FlamewindFeatherEntity entity) {
        return GoldflameQucusaurRenderer.TEXTURE;
    }

    @Override
    public void render(FlamewindFeatherEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float t = (entity.tickCount + partialTicks) / 20.0F;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 95.0F));
        VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
        Matrix4f mat = poseStack.last().pose();
        // Glowing golden-crimson feather blade
        float w = 0.42F;
        float h = 2.1F;
        vc.addVertex(mat, -w, 0.2F, 0.0F).setColor(255, 92, 24, 210);
        vc.addVertex(mat,  w, 0.2F, 0.0F).setColor(255, 92, 24, 210);
        vc.addVertex(mat,  w * 0.35F, h, 0.0F).setColor(255, 240, 128, 240);
        vc.addVertex(mat, -w * 0.35F, h, 0.0F).setColor(255, 240, 128, 240);

        vc.addVertex(mat, 0.0F, 0.2F, -w).setColor(255, 168, 36, 190);
        vc.addVertex(mat, 0.0F, 0.2F,  w).setColor(255, 168, 36, 190);
        vc.addVertex(mat, 0.0F, h,  w * 0.35F).setColor(255, 252, 188, 235);
        vc.addVertex(mat, 0.0F, h, -w * 0.35F).setColor(255, 252, 188, 235);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
