package com.joseetoon.emfplayeranimatorbridge.mixin.client;

import com.joseetoon.emfplayeranimatorbridge.compat.Compatibility;
import com.joseetoon.emfplayeranimatorbridge.compat.EmfAnimationBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 900)
public abstract class LivingEntityRendererPauseMixin<
        T extends LivingEntity,
        M extends EntityModel<T>>
        extends EntityRenderer<T>
        implements RenderLayerParent<T, M> {
    private static final VertexConsumer NO_OP_VERTEX_CONSUMER = new VertexConsumer() {
        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    };

    @Shadow
    public abstract M getModel();

    protected LivingEntityRendererPauseMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
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

    @ModifyArg(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    ordinal = 0
            )
    )
    private VertexConsumer emfplayeranimatorbridge$skipMainRenderWhenNeeded(VertexConsumer original) {
        return EmfAnimationBridge.consumeRenderSkipFlag() ? NO_OP_VERTEX_CONSUMER : original;
    }
}
