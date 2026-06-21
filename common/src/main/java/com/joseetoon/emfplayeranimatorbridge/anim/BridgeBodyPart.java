package com.joseetoon.emfplayeranimatorbridge.anim;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public enum BridgeBodyPart {
    HEAD("head"),
    TORSO("torso"),
    LEFT_ARM("leftArm"),
    RIGHT_ARM("rightArm"),
    LEFT_LEG("leftLeg"),
    RIGHT_LEG("rightLeg");

    private static final Map<String, BridgeBodyPart> BY_ANIMATION_NAME = createLookup();

    private final String animationName;

    BridgeBodyPart(String animationName) {
        this.animationName = animationName;
    }

    @Nullable
    public static BridgeBodyPart fromAnimationName(String animationName) {
        return BY_ANIMATION_NAME.get(animationName);
    }

    private static Map<String, BridgeBodyPart> createLookup() {
        Map<String, BridgeBodyPart> lookup = new HashMap<>();
        for (BridgeBodyPart bodyPart : values()) {
            lookup.put(bodyPart.animationName, bodyPart);
        }
        // Player Animator uses "body" for root transforms that should still halt torso-driven EMF motion.
        lookup.put("body", TORSO);
        return lookup;
    }
}
