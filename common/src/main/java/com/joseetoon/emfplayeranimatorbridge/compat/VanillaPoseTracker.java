package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import com.joseetoon.emfplayeranimatorbridge.api.HumanoidModelAccess;
import net.minecraft.client.model.EntityModel;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

public final class VanillaPoseTracker {
    private static final Map<EntityModel<?>, HumanoidBodyPose> CAPTURED_POSES = new WeakHashMap<>();

    private VanillaPoseTracker() {
    }

    public static void capture(EntityModel<?> model) {
        if (model instanceof HumanoidModelAccess modelAccess) {
            CAPTURED_POSES.put(model, HumanoidBodyPose.capture(modelAccess));
        }
    }

    @Nullable
    public static HumanoidBodyPose get(EntityModel<?> model) {
        return CAPTURED_POSES.get(model);
    }
}
