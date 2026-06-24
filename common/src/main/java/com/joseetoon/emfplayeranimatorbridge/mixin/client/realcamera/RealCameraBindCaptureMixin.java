package com.joseetoon.emfplayeranimatorbridge.mixin.client.realcamera;

import com.joseetoon.emfplayeranimatorbridge.compat.RealCameraRenderPhaseTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.RealCameraCore")
abstract class RealCameraBindCaptureMixin {

    @Inject(
            method = "computeCamera(Lnet/minecraft/client/Minecraft;F)V",
            at = @At("HEAD"),
            require = 0
    )
    private static void emfplayeranimatorbridge$enterBindCapture(CallbackInfo ci) {
        RealCameraRenderPhaseTracker.enterBindCapture();
    }

    @Inject(
            method = "computeCamera(Lnet/minecraft/client/Minecraft;F)V",
            at = @At("RETURN"),
            require = 0
    )
    private static void emfplayeranimatorbridge$exitBindCapture(CallbackInfo ci) {
        RealCameraRenderPhaseTracker.clear();
    }
}
