package com.velorise.teyvatbosses.neoforge.mixin;

import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Locks the driver into a focused, realistic seated driving posture while piloting {@link GeoDrillerEntity}:
 * both hands firmly gripping the left/right control levers, feet planted cleanly on the floorboard,
 * and head locked forward in 3rd-person view.
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart hat;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void velorise$applyGeoDrillerDrivingPose(
            T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch, CallbackInfo ci
    ) {
        if (entity.getVehicle() instanceof GoldflameQucusaurMountEntity dragon) {
            // Natural dragon-riding posture: legs straddling the saddle flank pads, torso leaning into flight
            float lean = dragon.isBoosting() ? 0.30F : (dragon.isFlying() ? 0.16F : 0.06F);
            this.body.xRot = lean;
            this.body.yRot = 0.0F;
            this.body.zRot = 0.0F;

            this.leftLeg.xRot = -1.22F;
            this.rightLeg.xRot = -1.22F;
            this.leftLeg.yRot = -0.34F;
            this.rightLeg.yRot = 0.34F;
            this.leftLeg.zRot = -0.16F;
            this.rightLeg.zRot = 0.16F;

            if (entity.attackAnim <= 0.0F && !entity.isUsingItem()) {
                this.leftArm.xRot = -0.78F + lean * 0.45F;
                this.rightArm.xRot = -0.78F + lean * 0.45F;
                this.leftArm.yRot = 0.18F;
                this.rightArm.yRot = -0.18F;
                this.leftArm.zRot = -0.06F;
                this.rightArm.zRot = 0.06F;
            }

            if ((Object) this instanceof PlayerModel<?> pm) {
                pm.leftSleeve.copyFrom(this.leftArm);
                pm.rightSleeve.copyFrom(this.rightArm);
                pm.leftPants.copyFrom(this.leftLeg);
                pm.rightPants.copyFrom(this.rightLeg);
                pm.jacket.copyFrom(this.body);
            }
            return;
        }
        if (!(entity.getVehicle() instanceof GeoDrillerEntity driller)) {
            return;
        }

        // Upright, attentive torso
        this.body.xRot = 0.06F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        // Serious seated leg posture with knees aligned forward inside the cockpit
        this.leftLeg.xRot = -1.38F;
        this.rightLeg.xRot = -1.38F;
        this.leftLeg.yRot = -0.14F;
        this.rightLeg.yRot = 0.14F;
        this.leftLeg.zRot = 0.0F;
        this.rightLeg.zRot = 0.0F;

        // Both hands firmly gripping lever_l (throttle) and lever_r (drill engage/pitch)
        float lAng = Mth.clamp(driller.leverLAngle, -0.50F, 0.50F);
        float rAng = Mth.clamp(driller.leverRAngle, -0.50F, 0.50F);
        this.leftArm.xRot = -1.08F + lAng * 0.72F;
        this.rightArm.xRot = -1.08F + rAng * 0.72F;
        this.leftArm.yRot = 0.14F;
        this.rightArm.yRot = -0.14F;
        this.leftArm.zRot = 0.04F;
        this.rightArm.zRot = -0.04F;

        // Lock head straight ahead in 3rd-person; allow clamped 180-deg glance in 1st-person
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == entity && mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            this.head.yRot = 0.0F;
            this.head.xRot = 0.08F;
        } else {
            this.head.yRot = Mth.clamp(this.head.yRot, -1.55F, 1.55F);
        }
        this.hat.copyFrom(this.head);

        if ((Object) this instanceof PlayerModel<?> pm) {
            pm.leftSleeve.copyFrom(this.leftArm);
            pm.rightSleeve.copyFrom(this.rightArm);
            pm.leftPants.copyFrom(this.leftLeg);
            pm.rightPants.copyFrom(this.rightLeg);
            pm.jacket.copyFrom(this.body);
        }
    }
}
