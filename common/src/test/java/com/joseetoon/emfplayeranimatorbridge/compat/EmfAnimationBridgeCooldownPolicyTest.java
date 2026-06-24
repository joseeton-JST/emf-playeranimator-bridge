package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.AnimationState;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.IgnoreReason;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmfAnimationBridgeCooldownPolicyTest {

    @Test
    void stackInactiveDoesNotHoldBridgeStateDuringCooldown() {
        AnimationState state = new AnimationState(false, Set.of(), IgnoreReason.STACK_INACTIVE);

        assertFalse(BridgeHoldPolicy.shouldHold(state, true));
    }

    @Test
    void blankLoopMayHoldBridgeStateDuringCooldown() {
        AnimationState state = new AnimationState(false, Set.of(), IgnoreReason.ONLY_BLANK_LOOP);

        assertTrue(BridgeHoldPolicy.shouldHold(state, true));
    }

    @Test
    void relevantAnimationAlwaysHoldsBridgeState() {
        AnimationState state = new AnimationState(true, Set.of(BridgeBodyPart.HEAD), IgnoreReason.HAS_RELEVANT_ANIMATION);

        assertTrue(BridgeHoldPolicy.shouldHold(state, false));
    }
}
