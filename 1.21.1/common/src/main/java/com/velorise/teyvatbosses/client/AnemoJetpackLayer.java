package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.AnemoJetpackModel;
import com.velorise.teyvatbosses.item.AnemoJetpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the 3D Anemo Jet-Wing directly on the player's back (attached to torso body bone)
 * with emissive core glow, dynamic wing flare, and thruster plumes while flying.
 */
public class AnemoJetpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEX = TeyvatBossesConstants.id("textures/entity/anemo_jetpack.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/anemo_jetpack_emissive.png");

    private final AnemoJetpackModel<T> jetpackModel;

    public AnemoJetpackLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.jetpackModel = new AnemoJetpackModel<>(modelSet.bakeLayer(AnemoJetpackModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof AnemoJetpackItem)) return;

        boolean boosting = AnemoJetpackItem.isBoosting(entity.getUUID());
        boolean airborne = !entity.onGround();
        jetpackModel.updatePose(airborne, boosting, entity.tickCount + partialTicks);

        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);
        jetpackModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEX)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF
        );
        jetpackModel.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.eyes(EMISSIVE)),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF
        );
        poseStack.popPose();
    }
}
