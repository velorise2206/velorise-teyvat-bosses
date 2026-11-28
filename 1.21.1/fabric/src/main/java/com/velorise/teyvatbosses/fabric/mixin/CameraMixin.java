package com.velorise.teyvatbosses.fabric.mixin;

import com.velorise.teyvatbosses.client.BossCameraShake;
import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.GoldflameQucusaurClientController;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private float yRot;
    @Shadow private float xRot;
    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @ModifyArg(
            require = 0,
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"),
            index = 0
    )
    private float velorise$extendMechThirdPersonZoom(float originalDistance) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            return RuinMechCameraController.THIRD_PERSON_ZOOM;
        }
        if (RuinSubClientController.isPilotingSubmersible()) {
            return RuinSubClientController.THIRD_PERSON_ZOOM;
        }
        if (GeoDrillerClientController.isPilotingDriller()) {
            return GeoDrillerClientController.THIRD_PERSON_ZOOM;
        }
        if (GoldflameQucusaurClientController.getRiddenMount() != null) {
            return GoldflameQucusaurClientController.getThirdPersonZoom();
        }
        return originalDistance;
    }

    @Inject(require = 0, method = "setup", at = @At("TAIL"))
    private void velorise$applyMechCameraShake(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            float[] off = RuinMechCameraController.computeOffsets(partialTick);
            this.setRotation(this.yRot + off[0], this.xRot + off[1]);
        } else if (RuinSubClientController.isPilotingSubmersible()) {
            float[] off = RuinSubClientController.computeCameraOffsets(partialTick);
            this.setRotation(this.yRot + off[0], this.xRot + off[1]);
        } else if (GeoDrillerClientController.isPilotingDriller()) {
            float[] off = GeoDrillerClientController.computeCameraOffsets(partialTick);
            this.setRotation(this.yRot + off[0], this.xRot + off[1]);
        }
        if (BossCameraShake.isActive()) {
            float[] off = BossCameraShake.computeOffsets(partialTick);
            this.setRotation(this.yRot + off[0], this.xRot + off[1]);
        }
    }
}
