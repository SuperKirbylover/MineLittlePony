package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.api.pony.Pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
abstract class MixinCamera {
    @Inject(method = "clipToSpace(F)F",
            at = @At("RETURN"),
            cancellable = true)
    private void redirectCameraDistance(float initial, CallbackInfoReturnable<Float> info) {
        float value = info.getReturnValueF();

        Pony pony = Pony.getManager().getPony(MinecraftClient.getInstance().player);

        if (!pony.race().isHuman()) {
            value *= pony.size().eyeDistanceFactor();
        }

        info.setReturnValue(value);
    }
}
