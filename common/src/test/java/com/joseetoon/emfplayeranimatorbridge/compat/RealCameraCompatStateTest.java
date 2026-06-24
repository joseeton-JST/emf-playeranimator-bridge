package com.joseetoon.emfplayeranimatorbridge.compat;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealCameraCompatStateTest {

    @Test
    void preservesBaseBridgeStateForRealCameraCompatNonEmfRelevantRender() {
        assertTrue(RealCameraCompatState.shouldPreserveBridgeStateOnNonEmfRender(true));
        assertFalse(RealCameraCompatState.shouldPreserveBridgeStateOnNonEmfRender(false));
    }

    @Test
    void clearingTrackingRemovesOnlyLocalCompatState() {
        RealCameraCompatState state = new RealCameraCompatState();
        UUID entityId = UUID.randomUUID();

        state.recordCompatRender(entityId, 15L, RealCameraRenderPhase.REAL_CAMERA_BODY_RENDER);
        state.setEmoteActive(entityId, true);
        state.setSkipEligible(entityId, true);

        state.clearTracking(entityId);

        assertFalse(state.hasCompatTick(entityId));
        assertFalse(state.hasCompatPhase(entityId));
        assertFalse(state.hasEmoteActive(entityId));
        assertFalse(state.hasSkipEligible(entityId));
    }

    @Test
    void nonEmfCompatPreservationDoesNotMarkRenderSkipEligible() {
        RealCameraCompatState state = new RealCameraCompatState();
        UUID entityId = UUID.randomUUID();

        state.setEmoteActive(entityId, true);
        state.setSkipEligible(entityId, false);

        assertTrue(state.wasEmoteActive(entityId));
        assertFalse(state.wasSkipEligible(entityId));
    }

    @Test
    void repeatedCompatRenderRequiresSameTickAndSamePhase() {
        RealCameraCompatState state = new RealCameraCompatState();
        UUID entityId = UUID.randomUUID();

        state.recordCompatRender(entityId, 20L, RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE);

        assertTrue(state.isRepeatedCompatRender(entityId, 20L, RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE));
        assertFalse(state.isRepeatedCompatRender(entityId, 20L, RealCameraRenderPhase.REAL_CAMERA_BODY_RENDER));
        assertFalse(state.isRepeatedCompatRender(entityId, 21L, RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE));
    }
}
