package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidBodyPose;
import net.minecraft.util.Mth;

final class ReleaseBlendState {
    private final HumanoidBodyPose startPose;
    private final long startTick;
    private final long endTickInclusive;
    private final float startAlpha;

    private ReleaseBlendState(HumanoidBodyPose startPose, long startTick, long endTickInclusive, float startAlpha) {
        this.startPose = startPose;
        this.startTick = startTick;
        this.endTickInclusive = Math.max(startTick, endTickInclusive);
        this.startAlpha = startAlpha;
    }

    static ReleaseBlendState forDuration(HumanoidBodyPose startPose, long startTick, int durationTicks) {
        int clampedDuration = Math.max(1, durationTicks);
        return new ReleaseBlendState(startPose, startTick, startTick + clampedDuration - 1L, 1.0F / clampedDuration);
    }

    static ReleaseBlendState forStopTick(HumanoidBodyPose startPose, long startTick, long stopTickInclusive) {
        return new ReleaseBlendState(startPose, startTick, stopTickInclusive, 0.0F);
    }

    HumanoidBodyPose startPose() {
        return this.startPose;
    }

    boolean isActive(long currentTick) {
        return currentTick <= this.endTickInclusive;
    }

    float blendAlpha(long currentTick) {
        if (this.endTickInclusive <= this.startTick) {
            return 1.0F;
        }
        if (currentTick <= this.startTick) {
            return this.startAlpha;
        }

        long blendSpan = this.endTickInclusive - this.startTick;
        float relativeProgress = (float) (currentTick - this.startTick) / (float) blendSpan;
        return Math.min(1.0F, Mth.lerp(relativeProgress, this.startAlpha, 1.0F));
    }
}
