package com.velorise.teyvatbosses.fabric.mixin;

import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Blocks vanilla attack, block-breaking, item use, and hand swing while piloting
 * the Ruin Submersible on Fabric, matching NeoForge's InteractionKeyMappingTriggered.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void velorise$cancelSubAttack(CallbackInfoReturnable<Boolean> cir) {
        if (RuinSubClientController.isPilotingSubmersible() || GeoDrillerClientController.isPilotingDriller()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void velorise$cancelSubContinueAttack(boolean leftClick, CallbackInfo ci) {
        if (RuinSubClientController.isPilotingSubmersible() || GeoDrillerClientController.isPilotingDriller()) {
            ci.cancel();
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void velorise$cancelSubUseItem(CallbackInfo ci) {
        if (RuinSubClientController.isPilotingSubmersible() || GeoDrillerClientController.isPilotingDriller()) {
            ci.cancel();
        }
    }
}
