package com.minelittlepony.client.mixin;

import net.minecraft.client.render.*;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.api.model.RenderPass;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void beforeRenderWorld(RenderTickCounter counter, CallbackInfo info) {
        RenderPass.swap(RenderPass.WORLD);
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void afterRenderWorld(RenderTickCounter counter, CallbackInfo info) {
        RenderPass.swap(RenderPass.GUI);
    }
}

@Mixin(value = WorldRenderer.class, priority = 0)
abstract class MixinWorldRenderer {
    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "net.minecraft.client.render.VertexConsumerProvider$Immediate.drawCurrentLayer()V",
            ordinal = 0
    ))
    private void onRender(
            RenderTickCounter counter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            Matrix4f matrix4f2, CallbackInfo info) {
        RenderPass.swap(RenderPass.HUD);
    }
}