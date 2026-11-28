package com.velorise.teyvatbosses.neoforge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tilts the entire rider model (head, torso, limbs, armor, and held items) cohesively
 * with {@link GoldflameQucusaurMountEntity}'s banking roll and flight pitch.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "setupRotations", at = @At("TAIL"))
    private void velorise$tiltDragonRider(
            T entity, PoseStack poseStack, float bob, float yBodyRot,
            float partialTick, float scale, CallbackInfo ci
    ) {
        if (!(entity.getVehicle() instanceof GoldflameQucusaurMountEntity dragon) || !dragon.isFlying()) {
            return;
        }
        float smoothBank = Mth.lerp(partialTick, dragon.getPrevBankRoll(), dragon.getBankRoll());
        float smoothPitch = Mth.lerp(partialTick, dragon.getPrevFlightPitch(), dragon.getFlightPitch());
        // Pivot around the rider's seated hips on the saddle cushion
        poseStack.translate(0.0D, 0.56D, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(smoothBank * 0.85F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-smoothPitch * 0.55F));
        poseStack.translate(0.0D, -0.56D, 0.0D);
    }
}
