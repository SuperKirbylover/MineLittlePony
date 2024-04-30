package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.Channel;
import com.minelittlepony.api.events.PonyDataCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.util.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Objects;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.jetbrains.annotations.Nullable;

public class EquineRenderManager<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> {

    private Models<T, M> models;
    @Nullable
    private Function<T, Models<T, M>> modelsLookup;

    private final PonyRenderContext<T, M> context;
    private final Transformer<T> transformer;

    private final FrustrumCheck<T> frustrum;

    public static void disableModelRenderProfile() {
        RenderSystem.disableBlend();
    }

    public EquineRenderManager(PonyRenderContext<T, M> context, Transformer<T> transformer, Models<T, M> models) {
        this.context = context;
        this.transformer = transformer;
        this.models = models;
        frustrum = new FrustrumCheck<>(context);
        context.setModel(models.body());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EquineRenderManager(PonyRenderContext<T, M> context, Transformer<T> transformer, ModelKey<? super M> key) {
        this(context, transformer, new Models(key));
    }

    public void setModelsLookup(@Nullable Function<T, Models<T, M>> modelsLookup) {
        this.modelsLookup = modelsLookup;
    }

    public Models<T, M> getModels() {
        return models;
    }

    public Frustum getFrustrum(T entity, Frustum vanilla) {
        if (RenderPass.getCurrent() == RenderPass.HUD) {
            return FrustrumCheck.ALWAYS_VISIBLE;
        }

        if (entity.isSleeping() || !PonyConfig.getInstance().frustrum.get()) {
            return vanilla;
        }
        return frustrum.withCamera(entity, vanilla);
    }

    public void preRender(T entity, ModelAttributes.Mode mode) {
        Pony pony = context.getEntityPony(entity);
        if (modelsLookup != null) {
            models = modelsLookup.apply(entity);
            context.setModel(models.body());
        }
        models.applyMetadata(pony.metadata());
        models.body().updateLivingState(entity, pony, mode);

        if (entity instanceof PlayerEntity player && entity instanceof RegistrationHandler handler) {
            handler.getSyncedPony().synchronize(player, pony);
        }
    }

    public void setupTransforms(T entity, MatrixStack stack, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        float s = getScaleFactor();
        stack.scale(s, s, s);

        if (entity instanceof PlayerEntity) {
            if (getModels().body().getAttributes().isSitting) {
                stack.translate(0, 0.125D, 0);
            }
        }

        bodyYaw = getMountedYaw(entity, bodyYaw, tickDelta);
        transformer.setupTransforms(entity, stack, animationProgress, bodyYaw, tickDelta, scale);

        PonyPosture.of(getModels().body().getAttributes()).apply(entity, getModels().body(), stack, bodyYaw, tickDelta, 1);
    }

    private float getMountedYaw(T entity, float bodyYaw, float tickDelta) {
        if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity mount) {
            return bodyYaw + MathUtil.interpolateDegress(mount.prevBodyYaw, mount.bodyYaw, tickDelta);
        }
        return bodyYaw;
    }

    public float getScaleFactor() {
        return getModels().body().getSize().scaleFactor();
    }

    public float getShadowSize() {
        return getModels().body().getSize().shadowSize();
    }

    public double getNamePlateYOffset(T entity) {
        // We start by negating the height calculation done by mahjong.
        float y = -(entity.getHeight() + 0.5F);

        // Then we add our own offsets.
        y += getModels().body().getAttributes().visualHeight * getScaleFactor() + 0.25F;

        if (entity.isSneaking()) {
            y -= 0.25F;
        }

        if (entity.hasVehicle()) {
            y += entity.getVehicle().getEyeHeight(entity.getPose());
        }

        if (entity.isSleeping()) {
            y /= 2;
        }

        return y;
    }

    public interface Transformer<T extends LivingEntity> {
        void setupTransforms(T entity, MatrixStack stack, float animationProgress, float bodyYaw, float tickDelta, float scale);
    }

    public interface RegistrationHandler {
        SyncedPony getSyncedPony();
    }

    public interface ModelHolder<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> {
        void setModel(M model);
    }

    public static class SyncedPony {
        @Nullable
        private Pony lastRenderedPony;
        @Nullable
        private Pony lastTransmittedPony;

        public void synchronize(PlayerEntity player, Pony pony) {
            boolean changed = pony.compareTo(lastRenderedPony) != 0;

            if (changed) {
                lastRenderedPony = pony;
                player.calculateDimensions();
            }

            if (!(player instanceof PreviewModel)) {
                @Nullable
                PlayerEntity clientPlayer = MinecraftClient.getInstance().player;

                if (Channel.isRegistered() && pony.compareTo(lastTransmittedPony) != 0) {
                    if (clientPlayer != null && (Objects.equals(player, clientPlayer) || Objects.equals(player.getGameProfile(), clientPlayer.getGameProfile()))) {
                        if (Channel.broadcastPonyData(pony.metadata())) {
                            lastTransmittedPony = pony;
                        }
                    }
                }

                if (changed) {
                    PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, pony.metadata(), EnvType.CLIENT);
                }
            }
        }
    }
}
