package com.joseetoon.emfplayeranimatorbridge.mixin.client;

import com.joseetoon.emfplayeranimatorbridge.compat.VanillaPoseTracker;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 1100)
public abstract class PlayerModelVanillaPoseCaptureMixin<T extends LivingEntity> {

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void emfplayeranimatorbridge$captureVanillaPoseBeforePlayerAnimator(T entity, float limbSwing, float limbSwingAmount,
                                                                                 float ageInTicks, float netHeadYaw, float headPitch,
                                                                                 CallbackInfo ci) {
        VanillaPoseTracker.capture((PlayerModel<?>) (Object) this);
    }
}
