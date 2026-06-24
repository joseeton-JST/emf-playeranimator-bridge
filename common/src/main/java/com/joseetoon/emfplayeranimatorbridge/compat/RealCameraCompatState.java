package com.joseetoon.emfplayeranimatorbridge.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class RealCameraCompatState {
    private final Map<UUID, Long> lastCompatTick = new HashMap<>();
    private final Map<UUID, RealCameraRenderPhase> lastCompatPhase = new HashMap<>();
    private final Map<UUID, Boolean> lastEmoteActive = new HashMap<>();
    private final Map<UUID, Boolean> lastSkipEligible = new HashMap<>();

    boolean isRepeatedCompatRender(UUID entityId, long currentTick, RealCameraRenderPhase phase) {
        Long lastTick = lastCompatTick.get(entityId);
        RealCameraRenderPhase lastPhase = lastCompatPhase.get(entityId);
        return lastTick != null && lastTick == currentTick && lastPhase == phase;
    }

    void recordCompatRender(UUID entityId, long currentTick, RealCameraRenderPhase phase) {
        lastCompatTick.put(entityId, currentTick);
        lastCompatPhase.put(entityId, phase);
    }

    boolean wasEmoteActive(UUID entityId) {
        return Boolean.TRUE.equals(lastEmoteActive.get(entityId));
    }

    void setEmoteActive(UUID entityId, boolean active) {
        lastEmoteActive.put(entityId, active);
    }

    boolean wasSkipEligible(UUID entityId) {
        return Boolean.TRUE.equals(lastSkipEligible.get(entityId));
    }

    void setSkipEligible(UUID entityId, boolean eligible) {
        lastSkipEligible.put(entityId, eligible);
    }

    void clearTracking(UUID entityId) {
        lastCompatTick.remove(entityId);
        lastCompatPhase.remove(entityId);
        lastEmoteActive.remove(entityId);
        lastSkipEligible.remove(entityId);
    }

    boolean hasCompatTick(UUID entityId) {
        return lastCompatTick.containsKey(entityId);
    }

    boolean hasCompatPhase(UUID entityId) {
        return lastCompatPhase.containsKey(entityId);
    }

    boolean hasEmoteActive(UUID entityId) {
        return lastEmoteActive.containsKey(entityId);
    }

    boolean hasSkipEligible(UUID entityId) {
        return lastSkipEligible.containsKey(entityId);
    }

    static boolean shouldPreserveBridgeStateOnNonEmfRender(boolean shouldHold) {
        return shouldHold;
    }
}
