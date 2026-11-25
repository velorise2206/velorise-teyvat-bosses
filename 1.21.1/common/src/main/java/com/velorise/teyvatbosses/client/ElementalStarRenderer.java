package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders Active and Inert Elemental Stars summoned by The Open-Eyed as glowing rotating celestial stars.
 */
public class ElementalStarRenderer extends EntityRenderer<ElementalStarEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/the_open_eyed.png");

    public ElementalStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ElementalStarEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        int[] rgb = colorForElement(entity.getElementId());

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.8D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 4.5F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(t * 0.08F) * 22.0F));

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();
        float s = 0.62F;
        float[][] ring = { {s, 0}, {0, s}, {-s, 0}, {0, -s} };
        for (int i = 0; i < 4; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % 4];
            // Upper pyramid
            vc.addVertex(m, 0, s * 1.25F, 0).setColor(255, 255, 255, 235);
            vc.addVertex(m, a[0], 0, a[1]).setColor(rgb[0], rgb[1], rgb[2], 210);
            vc.addVertex(m, b[0], 0, b[1]).setColor(rgb[0], rgb[1], rgb[2], 210);
            vc.addVertex(m, 0, s * 1.25F, 0).setColor(255, 255, 255, 235);
            // Lower pyramid
            vc.addVertex(m, 0, -s * 1.25F, 0).setColor(255, 255, 255, 235);
            vc.addVertex(m, b[0], 0, b[1]).setColor(rgb[0], rgb[1], rgb[2], 210);
            vc.addVertex(m, a[0], 0, a[1]).setColor(rgb[0], rgb[1], rgb[2], 210);
            vc.addVertex(m, 0, -s * 1.25F, 0).setColor(255, 255, 255, 235);
        }

        // Orbiting halo ring
        float rIn = 0.92F, rOut = 1.04F;
        for (int i = 0; i < 16; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / 16);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / 16);
            vc.addVertex(m, Mth.cos(a0) * rIn, 0, Mth.sin(a0) * rIn).setColor(rgb[0], rgb[1], rgb[2], 170);
            vc.addVertex(m, Mth.cos(a0) * rOut, 0, Mth.sin(a0) * rOut).setColor(rgb[0], rgb[1], rgb[2], 170);
            vc.addVertex(m, Mth.cos(a1) * rOut, 0, Mth.sin(a1) * rOut).setColor(rgb[0], rgb[1], rgb[2], 170);
            vc.addVertex(m, Mth.cos(a1) * rIn, 0, Mth.sin(a1) * rIn).setColor(rgb[0], rgb[1], rgb[2], 170);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static int[] colorForElement(int id) {
        return switch (id) {
            case ElementalStarEntity.ELEM_PYRO    -> new int[]{255, 110, 55};
            case ElementalStarEntity.ELEM_HYDRO   -> new int[]{60, 175, 255};
            case ElementalStarEntity.ELEM_ELECTRO -> new int[]{195, 110, 255};
            case ElementalStarEntity.ELEM_CRYO    -> new int[]{140, 235, 255};
            default                               -> new int[]{225, 190, 95}; // Inert Geo
        };
    }

    @Override
    public ResourceLocation getTextureLocation(ElementalStarEntity entity) {
        return TEXTURE;
    }
}
