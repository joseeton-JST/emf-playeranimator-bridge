package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmfReleaseBlendRenderStateTest {

    @AfterEach
    void tearDown() {
        EmfReleaseBlendRenderState.clear();
    }

    @Test
    void appliesInterpolatedPoseOnlyToMappedParts() {
        ModelPart head = new ModelPart(List.of(), Map.of());
        ModelPart body = new ModelPart(List.of(), Map.of());
        head.loadPose(PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F));
        body.loadPose(PartPose.offsetAndRotation(20.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F));

        EmfHumanoidBlendTargets targets = EmfHumanoidBlendTargets.createIdentityBacked(Map.of(head, BridgeBodyPart.HEAD));
        HumanoidBodyPose startPose = new HumanoidBodyPose(
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        EmfReleaseBlendRenderState.arm(targets, startPose, 0.5F);
        EmfReleaseBlendRenderState.applyIfArmed(head);
        EmfReleaseBlendRenderState.applyIfArmed(body);

        PartPose headPose = head.storePose();
        PartPose bodyPose = body.storePose();
        assertEquals(5.0F, headPose.x);
        assertEquals(0.5F, headPose.xRot);
        assertEquals(20.0F, bodyPose.x);
        assertEquals(2.0F, bodyPose.xRot);
    }
}
