package com.joseetoon.emfplayeranimatorbridge.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealCameraCompatPolicyTest {

    @Test
    void enablesFixOnlyWhenEveryCompatConditionMatches() {
        assertTrue(RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(true, true, true, true));
    }

    @Test
    void disablesFixWhenRealCameraIsMissing() {
        assertFalse(RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(false, true, true, true));
    }

    @Test
    void disablesFixWhenConfigIsOff() {
        assertFalse(RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(true, false, true, true));
    }

    @Test
    void disablesFixForNonLocalEntities() {
        assertFalse(RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(true, true, false, true));
    }

    @Test
    void disablesFixOutsideFirstPerson() {
        assertFalse(RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(true, true, true, false));
    }
}
