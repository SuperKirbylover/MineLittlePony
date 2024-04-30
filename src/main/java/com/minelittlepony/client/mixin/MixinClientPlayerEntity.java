package com.minelittlepony.client.mixin;

import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements EquineRenderManager.RegistrationHandler {
    public MixinClientPlayerEntity() { super(null, null); }

    private final EquineRenderManager.SyncedPony syncedPony = new EquineRenderManager.SyncedPony();

    @Override
    public EquineRenderManager.SyncedPony getSyncedPony() {
        return syncedPony;
    }
}
