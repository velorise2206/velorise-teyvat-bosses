package com.velorise.teyvatbosses.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies Ruin Mech camera roll (heavy footstep tilt and impact shake roll) on Fabric
 * for full parity with NeoForge's ViewportEvent.ComputeCameraAngles.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("TAIL"))
    private void velorise$applyMechCameraRoll(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            float[] off = RuinMechCameraController.computeOffsets(partialTicks);
            if (Math.abs(off[2]) > 0.001F) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(off[2]));
            }
        }
    }
}
