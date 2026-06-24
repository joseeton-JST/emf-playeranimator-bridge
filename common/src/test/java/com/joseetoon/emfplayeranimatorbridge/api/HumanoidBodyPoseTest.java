package com.joseetoon.emfplayeranimatorbridge.api;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import net.minecraft.client.model.geom.PartPose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class HumanoidBodyPoseTest {

    @Test
    void partPoseLookupMatchesLogicalBodyPart() {
        PartPose head = PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        PartPose body = PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        PartPose leftArm = PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        PartPose rightArm = PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        PartPose leftLeg = PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        PartPose rightLeg = PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        HumanoidBodyPose pose = new HumanoidBodyPose(head, body, leftArm, rightArm, leftLeg, rightLeg);

        assertSame(head, pose.partPose(BridgeBodyPart.HEAD));
        assertSame(body, pose.partPose(BridgeBodyPart.TORSO));
        assertSame(leftArm, pose.partPose(BridgeBodyPart.LEFT_ARM));
        assertSame(rightArm, pose.partPose(BridgeBodyPart.RIGHT_ARM));
        assertSame(leftLeg, pose.partPose(BridgeBodyPart.LEFT_LEG));
        assertSame(rightLeg, pose.partPose(BridgeBodyPart.RIGHT_LEG));
    }

    @Test
    void interpolateBlendsAllBodyPartPoses() {
        HumanoidBodyPose from = new HumanoidBodyPose(
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );
        HumanoidBodyPose to = new HumanoidBodyPose(
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F),
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F),
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F),
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F),
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F),
                PartPose.offsetAndRotation(8.0F, 6.0F, 4.0F, 0.8F, 0.6F, 0.4F)
        );

        HumanoidBodyPose result = HumanoidBodyPose.interpolate(from, to, 0.5F);

        TestHumanoidModelAccess access = new TestHumanoidModelAccess();
        result.applyTo(access);

        assertEquals(4.0F, access.head.storePose().x);
        assertEquals(3.0F, access.head.storePose().y);
        assertEquals(2.0F, access.head.storePose().z);
        assertEquals(0.4F, access.head.storePose().xRot);
        assertEquals(0.3F, access.head.storePose().yRot);
        assertEquals(0.2F, access.head.storePose().zRot);
    }

    private static final class TestHumanoidModelAccess implements HumanoidModelAccess {
        private final net.minecraft.client.model.geom.ModelPart head = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart hat = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart body = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart leftArm = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart rightArm = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart leftLeg = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
        private final net.minecraft.client.model.geom.ModelPart rightLeg = new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getHead() {
            return this.head;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getHat() {
            return this.hat;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getBody() {
            return this.body;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getLeftArm() {
            return this.leftArm;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getRightArm() {
            return this.rightArm;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getLeftLeg() {
            return this.leftLeg;
        }

        @Override
        public net.minecraft.client.model.geom.ModelPart emfbridge$getRightLeg() {
            return this.rightLeg;
        }
    }
}
