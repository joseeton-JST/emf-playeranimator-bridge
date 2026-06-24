package com.joseetoon.emfplayeranimatorbridge.mixin.client.realcamera;

import com.joseetoon.emfplayeranimatorbridge.compat.RealCameraRenderPhaseTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.RealCameraCore")
abstract class RealCameraBodyRenderMixin {

    @Inject(
            method = "renderCameraEntity(Lnet/minecraft/client/Minecraft;FLnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At("HEAD"),
            require = 0
    )
    private static void emfplayeranimatorbridge$enterBodyRender(CallbackInfo ci) {
        RealCameraRenderPhaseTracker.enterBodyRender();
    }

    @Inject(
            method = "renderCameraEntity(Lnet/minecraft/client/Minecraft;FLnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At("RETURN"),
            require = 0
    )
    private static void emfplayeranimatorbridge$exitBodyRender(CallbackInfo ci) {
        RealCameraRenderPhaseTracker.clear();
    }
}
