package com.joseetoon.emfplayeranimatorbridge.anim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BridgeBodyPartTest {
    @Test
    void mapsSupportedAnimationNames() {
        assertEquals(BridgeBodyPart.HEAD, BridgeBodyPart.fromAnimationName("head"));
        assertEquals(BridgeBodyPart.TORSO, BridgeBodyPart.fromAnimationName("torso"));
        assertEquals(BridgeBodyPart.TORSO, BridgeBodyPart.fromAnimationName("body"));
        assertEquals(BridgeBodyPart.LEFT_ARM, BridgeBodyPart.fromAnimationName("leftArm"));
        assertEquals(BridgeBodyPart.RIGHT_ARM, BridgeBodyPart.fromAnimationName("rightArm"));
        assertEquals(BridgeBodyPart.LEFT_LEG, BridgeBodyPart.fromAnimationName("leftLeg"));
        assertEquals(BridgeBodyPart.RIGHT_LEG, BridgeBodyPart.fromAnimationName("rightLeg"));
    }

    @Test
    void ignoresUnsupportedAnimationNames() {
        assertNull(BridgeBodyPart.fromAnimationName("leftItem"));
        assertNull(BridgeBodyPart.fromAnimationName("rightItem"));
        assertNull(BridgeBodyPart.fromAnimationName("unknown"));
    }
}
