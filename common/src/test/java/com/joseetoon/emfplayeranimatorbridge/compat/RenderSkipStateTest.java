package com.joseetoon.emfplayeranimatorbridge.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RenderSkipStateTest {

    @Test
    void consumeReturnsTrueOnlyOnceAfterArm() {
        RenderSkipState.clear();

        RenderSkipState.arm();

        assertTrue(RenderSkipState.consume());
        assertFalse(RenderSkipState.consume());
    }

    @Test
    void clearRemovesArmedState() {
        RenderSkipState.arm();

        RenderSkipState.clear();

        assertFalse(RenderSkipState.consume());
    }
}
