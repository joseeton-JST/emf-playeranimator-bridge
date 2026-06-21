package com.joseetoon.emfplayeranimatorbridge.mixin.client;

import com.joseetoon.emfplayeranimatorbridge.compat.Compatibility;
import com.joseetoon.emfplayeranimatorbridge.compat.EmfAnimationBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 900)
public abstract class LivingEntityRendererPauseMixin<
        T extends LivingEntity,
        M extends EntityModel<T>>
        extends EntityRenderer<T>
        implements RenderLayerParent<T, M> {
    @Shadow
    public abstract M getModel();

    protected LivingEntityRendererPauseMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "m_7392_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void emfplayeranimatorbridge$preRenderToBuffer(T entity, float entityYaw, float tickDelta, PoseStack poseStack,
                                                            MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (Compatibility.isEmfAvailable()) {
            EmfAnimationBridge.preRenderToBuffer(entity, this.getModel(), tickDelta);
        }
    }
}
