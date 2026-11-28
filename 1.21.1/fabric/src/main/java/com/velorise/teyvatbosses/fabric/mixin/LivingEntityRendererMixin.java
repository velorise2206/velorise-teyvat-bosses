package com.velorise.teyvatbosses.fabric.mixin;

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
 * Locks the entire rider model (head, torso, limbs, armor, and held items) 1:1 to
 * {@link GoldflameQucusaurMountEntity}'s live animated saddle bone position, pitch,
 * and aerodynamic bank roll across ground walking, hovering, boosting, and diving.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(require = 0, method = "setupRotations", at = @At("TAIL"))
    private void velorise$tiltDragonRider(
            T entity, PoseStack poseStack, float bob, float yBodyRot,
            float partialTick, float scale, CallbackInfo ci
    ) {
        if (!(entity.getVehicle() instanceof GoldflameQucusaurMountEntity dragon)) {
            return;
        }
        float smoothBank = dragon.isFlying() ? Mth.lerp(partialTick, dragon.getPrevBankRoll(), dragon.getBankRoll()) : 0.0F;
        float smoothPitch = dragon.isFlying() ? Mth.lerp(partialTick, dragon.getPrevFlightPitch(), dragon.getFlightPitch()) : 0.0F;

        // 1. Apply exact live animated saddle displacement (eliminates floating/clipping on wingbeats & walk strides)
        poseStack.translate(
                dragon.getClientSaddleDx(),
                dragon.getClientSaddleDy(),
                dragon.getClientSaddleDz()
        );

        // 2. Pivot cohesive rider tilt around seated hips on the saddle cushion
        float totalRoll = smoothBank * 0.85F + dragon.getClientSaddleRollDeg() * 0.65F;
        float totalPitch = -smoothPitch * 0.55F + dragon.getClientSaddlePitchDeg() * 0.65F;
        poseStack.translate(0.0D, 0.56D, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(totalRoll));
        poseStack.mulPose(Axis.XP.rotationDegrees(totalPitch));
        poseStack.translate(0.0D, -0.56D, 0.0D);
    }
}
