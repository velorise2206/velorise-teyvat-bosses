package com.velorise.teyvatbosses.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.GoldflameQucusaurClientController;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies Ruin Mech & Ruin Submersible camera roll on Fabric
 * for full parity with NeoForge's ViewportEvent.ComputeCameraAngles.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(require = 0, method = "bobHurt", at = @At("TAIL"))
    private void velorise$applyVehicleCameraRoll(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        float roll = 0.0F;
        if (RuinMechCameraController.getPilotedMech() != null) {
            roll = RuinMechCameraController.computeOffsets(partialTicks)[2];
        } else if (RuinSubClientController.isPilotingSubmersible()) {
            roll = RuinSubClientController.computeCameraOffsets(partialTicks)[2];
        } else if (GeoDrillerClientController.isPilotingDriller()) {
            roll = GeoDrillerClientController.computeCameraOffsets(partialTicks)[2];
        } else if (GoldflameQucusaurClientController.getRiddenMount() != null) {
            roll = GoldflameQucusaurClientController.computeCameraOffsets(partialTicks)[2];
        }
        if (Math.abs(roll) > 0.001F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
        }
    }
}
