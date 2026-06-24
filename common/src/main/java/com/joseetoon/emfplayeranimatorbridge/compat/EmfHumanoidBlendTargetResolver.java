package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

final class EmfHumanoidBlendTargetResolver {
    private EmfHumanoidBlendTargetResolver() {
    }

    @Nullable
    static EmfHumanoidBlendTargets resolve(EntityModel<?> model, ModelPart root) {
        EmfHumanoidModelKind modelKind = EmfHumanoidModelKind.of(model);
        if (modelKind == null) {
            return null;
        }
        return resolve(modelKind, root);
    }

    @Nullable
    static EmfHumanoidBlendTargets resolve(EmfHumanoidModelKind modelKind, ModelPart root) {
        ModelPart head = childOrNull(root, "head");
        ModelPart body = childOrNull(root, "body");
        ModelPart leftArm = childOrNull(root, "left_arm");
        ModelPart rightArm = childOrNull(root, "right_arm");
        ModelPart leftLeg = childOrNull(root, "left_leg");
        ModelPart rightLeg = childOrNull(root, "right_leg");
        if (head == null || body == null || leftArm == null || rightArm == null || leftLeg == null || rightLeg == null) {
            return null;
        }

        IdentityHashMap<ModelPart, BridgeBodyPart> partTargets = new IdentityHashMap<>();
        partTargets.put(head, BridgeBodyPart.HEAD);
        partTargets.put(body, BridgeBodyPart.TORSO);
        partTargets.put(leftArm, BridgeBodyPart.LEFT_ARM);
        partTargets.put(rightArm, BridgeBodyPart.RIGHT_ARM);
        partTargets.put(leftLeg, BridgeBodyPart.LEFT_LEG);
        partTargets.put(rightLeg, BridgeBodyPart.RIGHT_LEG);

        if (modelKind == EmfHumanoidModelKind.PLAYER) {
            putIfPresent(partTargets, childOrNull(root, "hat"), BridgeBodyPart.HEAD);
            putIfPresent(partTargets, childOrNull(root, "jacket"), BridgeBodyPart.TORSO);
            putIfPresent(partTargets, childOrNull(root, "left_sleeve"), BridgeBodyPart.LEFT_ARM);
            putIfPresent(partTargets, childOrNull(root, "right_sleeve"), BridgeBodyPart.RIGHT_ARM);
            putIfPresent(partTargets, childOrNull(root, "left_pants"), BridgeBodyPart.LEFT_LEG);
            putIfPresent(partTargets, childOrNull(root, "right_pants"), BridgeBodyPart.RIGHT_LEG);
        } else if (modelKind == EmfHumanoidModelKind.HUMANOID) {
            putIfPresent(partTargets, childOrNull(root, "hat"), BridgeBodyPart.HEAD);
        }

        return EmfHumanoidBlendTargets.createIdentityBacked(partTargets);
    }

    private static void putIfPresent(Map<ModelPart, BridgeBodyPart> partTargets,
                                     @Nullable ModelPart part,
                                     BridgeBodyPart bodyPart) {
        if (part != null) {
            partTargets.put(part, bodyPart);
        }
    }

    @Nullable
    private static ModelPart childOrNull(ModelPart root, String childName) {
        try {
            return root.getChild(childName);
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }
}
