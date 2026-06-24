package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

final class EmfReleaseBlendRenderState {
    private static final ThreadLocal<ActiveBlend> ACTIVE_BLEND = new ThreadLocal<>();

    private EmfReleaseBlendRenderState() {
    }

    static void arm(EmfHumanoidBlendTargets targets, HumanoidBodyPose startPose, float alpha) {
        ACTIVE_BLEND.set(new ActiveBlend(targets, startPose, Mth.clamp(alpha, 0.0F, 1.0F)));
    }

    static void clear() {
        ACTIVE_BLEND.remove();
    }

    static void applyIfArmed(ModelPart modelPart) {
        ActiveBlend activeBlend = ACTIVE_BLEND.get();
        if (activeBlend == null) {
            return;
        }

        BridgeBodyPart bodyPart = activeBlend.targets.logicalPartOf(modelPart);
        if (bodyPart == null) {
            return;
        }

        PartPose startPose = activeBlend.startPose.partPose(bodyPart);
        PartPose emfPose = modelPart.storePose();
        modelPart.loadPose(interpolate(startPose, emfPose, activeBlend.alpha));
    }

    private static PartPose interpolate(PartPose from, PartPose to, float alpha) {
        return PartPose.offsetAndRotation(
                Mth.lerp(alpha, from.x, to.x),
                Mth.lerp(alpha, from.y, to.y),
                Mth.lerp(alpha, from.z, to.z),
                Mth.lerp(alpha, from.xRot, to.xRot),
                Mth.lerp(alpha, from.yRot, to.yRot),
                Mth.lerp(alpha, from.zRot, to.zRot)
        );
    }

    private record ActiveBlend(EmfHumanoidBlendTargets targets, HumanoidBodyPose startPose, float alpha) {
    }
}
