package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;

import javax.annotation.Nullable;

final class ReleaseBlendTargetResolver {

    private ReleaseBlendTargetResolver() {
    }

    @Nullable
    static HumanoidBodyPose resolve(@Nullable HumanoidBodyPose capturedVanillaPose,
                                    @Nullable HumanoidBodyPose fallbackCurrentPose) {
        return capturedVanillaPose != null ? capturedVanillaPose : fallbackCurrentPose;
    }
}
