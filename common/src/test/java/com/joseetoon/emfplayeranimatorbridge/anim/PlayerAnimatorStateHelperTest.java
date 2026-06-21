package com.joseetoon.emfplayeranimatorbridge.anim;

import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.AnimationState;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.IgnoreReason;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerAnimatorStateHelperTest {

    @Test
    void inactiveStateIsInactive() {
        AnimationState state = AnimationState.INACTIVE;
        assertFalse(state.hasRelevantAnimation());
        assertTrue(state.activeParts().isEmpty());
        assertSame(IgnoreReason.NOT_ANIMATED, state.reason());
    }

    @Test
    void reasonEnumExposesAllDiagnosticCategories() {
        assertNotNull(IgnoreReason.valueOf("NOT_ANIMATED"));
        assertNotNull(IgnoreReason.valueOf("STACK_INACTIVE"));
        assertNotNull(IgnoreReason.valueOf("ONLY_BLANK_LOOP"));
        assertNotNull(IgnoreReason.valueOf("NO_ENABLED_PARTS"));
        assertNotNull(IgnoreReason.valueOf("HAS_RELEVANT_ANIMATION"));
    }

    @Test
    void animationStateCanBeConstructed() {
        AnimationState state = new AnimationState(true, java.util.Set.of(BridgeBodyPart.HEAD), IgnoreReason.HAS_RELEVANT_ANIMATION);
        assertTrue(state.hasRelevantAnimation());
        assertEquals(1, state.activeParts().size());
        assertSame(IgnoreReason.HAS_RELEVANT_ANIMATION, state.reason());
    }
}
