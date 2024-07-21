package com.minelittlepony.client.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.EquineRenderManager.RegistrationHandler;
import com.minelittlepony.client.render.EquineRenderManager.SyncedPony;

@Mixin(PlayerEntity.class)
abstract class MixinPlayerEntity extends LivingEntity implements RegistrationHandler {
    MixinPlayerEntity() { super(null, null); }

    private final SyncedPony syncedPony = new SyncedPony();

    @Override
    public SyncedPony getSyncedPony() {
        return syncedPony;
    }

    @Inject(method = "getBaseDimensions", at = @At("RETURN"), cancellable = true)
    private void onGetBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> info) {
        float factor = syncedPony.getCachedPonyData().size().eyeHeightFactor();

        if (factor != 1) {
            EntityDimensions dimensions = info.getReturnValue();
            info.setReturnValue(dimensions.withEyeHeight(dimensions.eyeHeight() * factor));
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        syncedPony.synchronize((PlayerEntity)(Object)this);
    }
}
