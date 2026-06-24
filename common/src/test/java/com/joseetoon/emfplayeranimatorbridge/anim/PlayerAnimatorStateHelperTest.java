package com.joseetoon.emfplayeranimatorbridge.anim;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.AnimationFormat;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.AnimationState;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.IgnoreReason;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

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

    @Test
    void finiteRelevantKeyframeReportsExactRemainingTicks() {
        KeyframeAnimation.AnimationBuilder builder = new KeyframeAnimation.AnimationBuilder(AnimationFormat.JSON_EMOTECRAFT);
        builder.endTick = 20;
        builder.stopTick = 20;
        builder.head.setEnabled(true);
        KeyframeAnimation animation = builder.build();
        KeyframeAnimationPlayer player = new KeyframeAnimationPlayer(animation, 16);

        OptionalInt remainingTicks = PlayerAnimatorStateHelper.getFiniteRelevantRemainingTicks(player);

        assertTrue(remainingTicks.isPresent());
        assertEquals(player.getStopTick() - player.getCurrentTick(), remainingTicks.getAsInt());
    }
}
