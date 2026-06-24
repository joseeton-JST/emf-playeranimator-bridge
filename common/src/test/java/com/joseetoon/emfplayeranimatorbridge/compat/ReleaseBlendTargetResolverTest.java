package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import net.minecraft.client.model.geom.PartPose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ReleaseBlendTargetResolverTest {

    @Test
    void prefersCapturedVanillaPoseWhenAvailable() {
        HumanoidBodyPose vanillaPose = pose(1.0F);
        HumanoidBodyPose currentPose = pose(2.0F);

        assertSame(vanillaPose, ReleaseBlendTargetResolver.resolve(vanillaPose, currentPose));
    }

    @Test
    void fallsBackToCurrentPoseWhenVanillaPoseIsMissing() {
        HumanoidBodyPose currentPose = pose(2.0F);

        assertSame(currentPose, ReleaseBlendTargetResolver.resolve(null, currentPose));
    }

    @Test
    void returnsNullWhenNoTargetPoseExists() {
        assertNull(ReleaseBlendTargetResolver.resolve(null, null));
    }

    private static HumanoidBodyPose pose(float value) {
        PartPose partPose = PartPose.offsetAndRotation(value, value, value, value, value, value);
        return new HumanoidBodyPose(partPose, partPose, partPose, partPose, partPose, partPose);
    }
}
