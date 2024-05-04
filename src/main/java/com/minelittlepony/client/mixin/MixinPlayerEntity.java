package com.minelittlepony.client.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.api.pony.Pony;

@Mixin(PlayerEntity.class)
abstract class MixinPlayerEntity {
    @Inject(method = "getBaseDimensions", at = @At("RETURN"), cancellable = true)
    private void onGetBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> info) {
        Pony pony = Pony.getManager().getPony((PlayerEntity)(Object)this);

        if (!pony.race().isHuman()) {
            float factor = pony.size().eyeHeightFactor();
            if (factor != 1) {
                EntityDimensions dimensions = info.getReturnValue();
                info.setReturnValue(dimensions.withEyeHeight(dimensions.eyeHeight() * factor));
            }
        }
    }
}
