package com.joseetoon.emfplayeranimatorbridge.mixin.client.emf;

import com.joseetoon.emfplayeranimatorbridge.compat.EmfAnimationBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartWithState")
abstract class EMFModelPartWithStateReleaseBlendMixin {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ltraben/entity_model_features/models/parts/EMFModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
                    shift = At.Shift.BEFORE
            ),
            require = 0
    )
    private void emfplayeranimatorbridge$applyReleaseBlendAfterEmfAnimation(PoseStack poseStack, VertexConsumer vertexConsumer,
                                                                            int packedLight, int packedOverlay, int color,
                                                                            CallbackInfo ci) {
        EmfAnimationBridge.applyReleaseBlendToEmfPartIfArmed((ModelPart) (Object) this);
    }
}
