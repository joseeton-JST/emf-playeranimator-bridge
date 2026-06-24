package com.joseetoon.emfplayeranimatorbridge.compat;

final class RealCameraCompatPolicy {

    private RealCameraCompatPolicy() {
    }

    static boolean shouldUseLocalFirstPersonFix(boolean realCameraAvailable, boolean configEnabled,
                                                boolean localPlayer, boolean firstPersonCamera) {
        return realCameraAvailable && configEnabled && localPlayer && firstPersonCamera;
    }
}
