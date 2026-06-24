package com.joseetoon.emfplayeranimatorbridge;

public final class BridgeConfig {
    private static volatile boolean enableBridge = true;
    private static volatile boolean debugLogging = false;
    private static volatile boolean emfPerPartPause = false;
    private static volatile int emfPauseCooldownTicks = 5;
    private static volatile boolean realCameraFirstPersonFix = true;

    private BridgeConfig() {
    }

    public static boolean isBridgeEnabled() {
        return enableBridge;
    }

    public static boolean isDebugLoggingEnabled() {
        return debugLogging;
    }

    public static boolean isEmfPerPartPauseEnabled() {
        return emfPerPartPause;
    }

    public static int getEmfPauseCooldownTicks() {
        return Math.max(0, emfPauseCooldownTicks);
    }

    public static boolean isRealCameraFirstPersonFixEnabled() {
        return realCameraFirstPersonFix;
    }

    public static void update(boolean bridgeEnabled, boolean debugEnabled) {
        enableBridge = bridgeEnabled;
        debugLogging = debugEnabled;
    }

    public static void update(boolean bridgeEnabled, boolean debugEnabled, boolean perPartPause, int cooldownTicks,
                              boolean realCameraFixEnabled) {
        enableBridge = bridgeEnabled;
        debugLogging = debugEnabled;
        emfPerPartPause = perPartPause;
        emfPauseCooldownTicks = cooldownTicks;
        realCameraFirstPersonFix = realCameraFixEnabled;
    }
}
