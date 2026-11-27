package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.PrimoGeovishapPartEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Invisible renderer for {@link PrimoGeovishapPartEntity} (visual geometry is rendered by {@link PrimoGeovishapRenderer}).
 */
public class PrimoGeovishapPartRenderer extends EntityRenderer<PrimoGeovishapPartEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");

    public PrimoGeovishapPartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0F;
    }

    @Override
    public boolean shouldRender(PrimoGeovishapPartEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoGeovishapPartEntity entity) {
        return TEXTURE;
    }
}
