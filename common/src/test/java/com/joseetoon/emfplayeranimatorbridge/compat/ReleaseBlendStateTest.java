package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import net.minecraft.client.model.geom.PartPose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReleaseBlendStateTest {

    @Test
    void earlyReleaseBlendReachesFullAlphaAtConfiguredStopTick() {
        ReleaseBlendState state = ReleaseBlendState.forStopTick(zeroPose(), 100L, 103L);

        assertTrue(state.isActive(100L));
        assertTrue(state.isActive(103L));
        assertFalse(state.isActive(104L));

        assertEquals(0.0F, state.blendAlpha(100L));
        assertEquals(1.0F / 3.0F, state.blendAlpha(101L));
        assertEquals(2.0F / 3.0F, state.blendAlpha(102L));
        assertEquals(1.0F, state.blendAlpha(103L));
    }

    @Test
    void singleTickStopWindowStartsFullyBlended() {
        ReleaseBlendState state = ReleaseBlendState.forStopTick(zeroPose(), 100L, 100L);

        assertTrue(state.isActive(100L));
        assertEquals(1.0F, state.blendAlpha(100L));
    }

    @Test
    void releaseBlendStaysActiveForConfiguredDuration() {
        ReleaseBlendState state = ReleaseBlendState.forDuration(zeroPose(), 100L, 4);

        assertTrue(state.isActive(100L));
        assertTrue(state.isActive(103L));
        assertFalse(state.isActive(104L));
    }

    @Test
    void releaseBlendAlphaAdvancesEachTick() {
        ReleaseBlendState state = ReleaseBlendState.forDuration(zeroPose(), 100L, 4);

        assertEquals(0.25F, state.blendAlpha(100L));
        assertEquals(0.5F, state.blendAlpha(101L));
        assertEquals(0.75F, state.blendAlpha(102L));
        assertEquals(1.0F, state.blendAlpha(103L));
    }

    private static HumanoidBodyPose zeroPose() {
        PartPose zero = PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        return new HumanoidBodyPose(zero, zero, zero, zero, zero, zero);
    }
}
