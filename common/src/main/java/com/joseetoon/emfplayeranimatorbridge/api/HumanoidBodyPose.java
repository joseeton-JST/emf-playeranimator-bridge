package com.joseetoon.emfplayeranimatorbridge.api;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

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
}
