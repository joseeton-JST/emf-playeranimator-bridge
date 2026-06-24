package com.joseetoon.emfplayeranimatorbridge.compat;

public final class Compatibility {
    private static final boolean EMF_AVAILABLE = isClassPresent("traben.entity_model_features.EMFAnimationApi");
    private static final boolean REAL_CAMERA_AVAILABLE = isClassPresent("com.xtracr.realcamera.RealCamera");

    private Compatibility() {
    }

    public static boolean isEmfAvailable() {
        return EMF_AVAILABLE;
    }

    public static boolean isRealCameraAvailable() {
        return REAL_CAMERA_AVAILABLE;
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, Compatibility.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
