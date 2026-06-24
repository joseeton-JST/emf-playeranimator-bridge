package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.AnimationState;

final class BridgeHoldPolicy {

    private BridgeHoldPolicy() {
    }

    static boolean shouldHold(AnimationState state, boolean withinCooldown) {
        if (state.hasRelevantAnimation()) {
            return true;
        }

        return switch (state.reason()) {
            case ONLY_BLANK_LOOP, NO_ENABLED_PARTS -> withinCooldown;
            case NOT_ANIMATED, STACK_INACTIVE, HAS_RELEVANT_ANIMATION -> false;
        };
    }
}
