package com.joseetoon.emfplayeranimatorbridge.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RealCameraRenderPhaseTrackerTest {

    @Test
    void defaultsToNoneAndClearsBackToNone() {
        RealCameraRenderPhaseTracker.clear();

        assertEquals(RealCameraRenderPhase.NONE, RealCameraRenderPhaseTracker.currentPhase());

        RealCameraRenderPhaseTracker.enterBindCapture();
        assertEquals(RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE, RealCameraRenderPhaseTracker.currentPhase());

        RealCameraRenderPhaseTracker.clear();
        assertEquals(RealCameraRenderPhase.NONE, RealCameraRenderPhaseTracker.currentPhase());
    }

    @Test
    void bodyRenderPhaseOverridesCurrentThreadPhase() {
        RealCameraRenderPhaseTracker.clear();

        RealCameraRenderPhaseTracker.enterBindCapture();
        RealCameraRenderPhaseTracker.enterBodyRender();

        assertEquals(RealCameraRenderPhase.REAL_CAMERA_BODY_RENDER, RealCameraRenderPhaseTracker.currentPhase());
    }
}
