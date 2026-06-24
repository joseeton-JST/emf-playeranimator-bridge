package com.joseetoon.emfplayeranimatorbridge.api;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

public final class HumanoidBodyPose {
    private final PartPose headPose;
    private final PartPose bodyPose;
    private final PartPose leftArmPose;
    private final PartPose rightArmPose;
    private final PartPose leftLegPose;
    private final PartPose rightLegPose;

    public HumanoidBodyPose(PartPose headPose, PartPose bodyPose, PartPose leftArmPose, PartPose rightArmPose, PartPose leftLegPose, PartPose rightLegPose) {
        this.headPose = headPose;
        this.bodyPose = bodyPose;
        this.leftArmPose = leftArmPose;
        this.rightArmPose = rightArmPose;
        this.leftLegPose = leftLegPose;
        this.rightLegPose = rightLegPose;
    }

    public static HumanoidBodyPose capture(HumanoidModelAccess modelAccess) {
        return new HumanoidBodyPose(
                modelAccess.emfbridge$getHead().storePose(),
                modelAccess.emfbridge$getBody().storePose(),
                modelAccess.emfbridge$getLeftArm().storePose(),
                modelAccess.emfbridge$getRightArm().storePose(),
                modelAccess.emfbridge$getLeftLeg().storePose(),
                modelAccess.emfbridge$getRightLeg().storePose());
    }

    public PartPose partPose(BridgeBodyPart bodyPart) {
        return switch (bodyPart) {
            case HEAD -> this.headPose;
            case TORSO -> this.bodyPose;
            case LEFT_ARM -> this.leftArmPose;
            case RIGHT_ARM -> this.rightArmPose;
            case LEFT_LEG -> this.leftLegPose;
            case RIGHT_LEG -> this.rightLegPose;
        };
    }

    public void applyTo(HumanoidModelAccess modelAccess) {
        modelAccess.emfbridge$getHead().loadPose(this.headPose);
        modelAccess.emfbridge$getBody().loadPose(this.bodyPose);
        modelAccess.emfbridge$getLeftArm().loadPose(this.leftArmPose);
        modelAccess.emfbridge$getRightArm().loadPose(this.rightArmPose);
        modelAccess.emfbridge$getLeftLeg().loadPose(this.leftLegPose);
        modelAccess.emfbridge$getRightLeg().loadPose(this.rightLegPose);
    }

    public static void applyTo(ModelPart head, ModelPart body, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg, HumanoidBodyPose pose) {
        head.loadPose(pose.headPose);
        body.loadPose(pose.bodyPose);
        leftArm.loadPose(pose.leftArmPose);
        rightArm.loadPose(pose.rightArmPose);
        leftLeg.loadPose(pose.leftLegPose);
        rightLeg.loadPose(pose.rightLegPose);
    }

    public static HumanoidBodyPose interpolate(HumanoidBodyPose from, HumanoidBodyPose to, float alpha) {
        float clampedAlpha = Mth.clamp(alpha, 0.0F, 1.0F);
        return new HumanoidBodyPose(
                interpolate(from.headPose, to.headPose, clampedAlpha),
                interpolate(from.bodyPose, to.bodyPose, clampedAlpha),
                interpolate(from.leftArmPose, to.leftArmPose, clampedAlpha),
                interpolate(from.rightArmPose, to.rightArmPose, clampedAlpha),
                interpolate(from.leftLegPose, to.leftLegPose, clampedAlpha),
                interpolate(from.rightLegPose, to.rightLegPose, clampedAlpha));
    }

    private static PartPose interpolate(PartPose from, PartPose to, float alpha) {
        return PartPose.offsetAndRotation(
                Mth.lerp(alpha, from.x, to.x),
                Mth.lerp(alpha, from.y, to.y),
                Mth.lerp(alpha, from.z, to.z),
                Mth.lerp(alpha, from.xRot, to.xRot),
                Mth.lerp(alpha, from.yRot, to.yRot),
                Mth.lerp(alpha, from.zRot, to.zRot)
        );
    }
}
