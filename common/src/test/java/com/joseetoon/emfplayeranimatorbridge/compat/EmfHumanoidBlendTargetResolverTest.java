package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmfHumanoidBlendTargetResolverTest {

    @Test
    void playerRootMapsCoreAndOverlayParts() {
        ModelPart head = part();
        ModelPart hat = part();
        ModelPart body = part();
        ModelPart jacket = part();
        ModelPart leftArm = part();
        ModelPart leftSleeve = part();
        ModelPart rightArm = part();
        ModelPart rightSleeve = part();
        ModelPart leftLeg = part();
        ModelPart leftPants = part();
        ModelPart rightLeg = part();
        ModelPart rightPants = part();
        ModelPart root = root(Map.ofEntries(
                Map.entry("head", head),
                Map.entry("hat", hat),
                Map.entry("body", body),
                Map.entry("jacket", jacket),
                Map.entry("left_arm", leftArm),
                Map.entry("left_sleeve", leftSleeve),
                Map.entry("right_arm", rightArm),
                Map.entry("right_sleeve", rightSleeve),
                Map.entry("left_leg", leftLeg),
                Map.entry("left_pants", leftPants),
                Map.entry("right_leg", rightLeg),
                Map.entry("right_pants", rightPants)
        ));

        EmfHumanoidBlendTargets targets = EmfHumanoidBlendTargetResolver.resolve(EmfHumanoidModelKind.PLAYER, root);

        assertNotNull(targets);
        assertEquals(BridgeBodyPart.HEAD, targets.logicalPartOf(head));
        assertEquals(BridgeBodyPart.HEAD, targets.logicalPartOf(hat));
        assertEquals(BridgeBodyPart.TORSO, targets.logicalPartOf(body));
        assertEquals(BridgeBodyPart.TORSO, targets.logicalPartOf(jacket));
        assertEquals(BridgeBodyPart.LEFT_ARM, targets.logicalPartOf(leftArm));
        assertEquals(BridgeBodyPart.LEFT_ARM, targets.logicalPartOf(leftSleeve));
        assertEquals(BridgeBodyPart.RIGHT_ARM, targets.logicalPartOf(rightArm));
        assertEquals(BridgeBodyPart.RIGHT_ARM, targets.logicalPartOf(rightSleeve));
        assertEquals(BridgeBodyPart.LEFT_LEG, targets.logicalPartOf(leftLeg));
        assertEquals(BridgeBodyPart.LEFT_LEG, targets.logicalPartOf(leftPants));
        assertEquals(BridgeBodyPart.RIGHT_LEG, targets.logicalPartOf(rightLeg));
        assertEquals(BridgeBodyPart.RIGHT_LEG, targets.logicalPartOf(rightPants));
    }

    @Test
    void humanoidRootRequiresExactCoreParts() {
        ModelPart root = root(Map.of(
                "head", part(),
                "body", part(),
                "left_arm", part(),
                "right_arm", part(),
                "left_leg", part()
        ));

        EmfHumanoidBlendTargets targets = EmfHumanoidBlendTargetResolver.resolve(EmfHumanoidModelKind.HUMANOID, root);

        assertNull(targets);
    }

    @Test
    void illagerRootMapsExactSupportedCoreParts() {
        ModelPart head = part();
        ModelPart body = part();
        ModelPart leftArm = part();
        ModelPart rightArm = part();
        ModelPart leftLeg = part();
        ModelPart rightLeg = part();
        ModelPart foldedArms = part();
        ModelPart root = root(Map.of(
                "head", head,
                "body", body,
                "left_arm", leftArm,
                "right_arm", rightArm,
                "left_leg", leftLeg,
                "right_leg", rightLeg,
                "arms", foldedArms
        ));

        EmfHumanoidBlendTargets targets = EmfHumanoidBlendTargetResolver.resolve(EmfHumanoidModelKind.ILLAGER, root);

        assertNotNull(targets);
        assertEquals(BridgeBodyPart.HEAD, targets.logicalPartOf(head));
        assertEquals(BridgeBodyPart.TORSO, targets.logicalPartOf(body));
        assertEquals(BridgeBodyPart.LEFT_ARM, targets.logicalPartOf(leftArm));
        assertEquals(BridgeBodyPart.RIGHT_ARM, targets.logicalPartOf(rightArm));
        assertEquals(BridgeBodyPart.LEFT_LEG, targets.logicalPartOf(leftLeg));
        assertEquals(BridgeBodyPart.RIGHT_LEG, targets.logicalPartOf(rightLeg));
        assertNull(targets.logicalPartOf(foldedArms));
    }

    private static ModelPart root(Map<String, ModelPart> children) {
        return new ModelPart(List.of(), children);
    }

    private static ModelPart part() {
        return new ModelPart(List.of(), Map.of());
    }
}
