package com.joseetoon.emfplayeranimatorbridge.compat;

public final class Compatibility {
    private static final boolean EMF_AVAILABLE = isClassPresent("traben.entity_model_features.EMFAnimationApi");

    private Compatibility() {
    }

    public static boolean isEmfAvailable() {
        return EMF_AVAILABLE;
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
