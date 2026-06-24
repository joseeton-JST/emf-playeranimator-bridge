package com.joseetoon.emfplayeranimatorbridge.compat;

public final class RealCameraRenderPhaseTracker {
    private static final ThreadLocal<RealCameraRenderPhase> CURRENT_PHASE =
            ThreadLocal.withInitial(() -> RealCameraRenderPhase.NONE);

    private RealCameraRenderPhaseTracker() {
    }

    public static RealCameraRenderPhase currentPhase() {
        return CURRENT_PHASE.get();
    }

    public static void enterBindCapture() {
        CURRENT_PHASE.set(RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE);
    }

    public static void enterBodyRender() {
        CURRENT_PHASE.set(RealCameraRenderPhase.REAL_CAMERA_BODY_RENDER);
    }

    public static void clear() {
        CURRENT_PHASE.remove();
    }
}
