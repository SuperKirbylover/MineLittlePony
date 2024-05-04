package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.PonyElytra;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ElytraFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/elytra.png");

    private final PonyElytra<T> model = ModelType.ELYTRA.createModel();

    public ElytraFeature(PonyRenderContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.CHEST, ArmourLayer.OUTER)) {
            float alpha = plugin.getElytraAlpha(stack, model, entity);
            if (alpha <= 0) {
                return;
            }

            VertexConsumer vertexConsumer = plugin.getElytraConsumer(stack, model, entity, provider, getElytraTexture(entity));
            if (vertexConsumer == null) {
                return;
            }

            matrices.push();
            preRenderCallback(matrices);

            getContextModel().copyStateTo(model);
            model.isSneaking = PonyPosture.isCrouching(getContext().getEntityPony(entity), entity);
            model.setAngles(entity, limbDistance, limbAngle, age, headYaw, headPitch);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, alpha);

            matrices.pop();
        }
    }

    protected void preRenderCallback(MatrixStack stack) {
        M body = getModelWrapper().body();
        stack.translate(0, body.getRiderYOffset(), 0.125);
        body.transform(BodyPart.BODY, stack);
    }

    protected Identifier getElytraTexture(T entity) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            SkinTextures textures = player.getSkinTextures();

            if (textures.elytraTexture() != null) {
                return textures.elytraTexture();
            }

            if (textures.capeTexture() != null && player.isPartVisible(PlayerModelPart.CAPE)) {
                return textures.capeTexture();
            }
        }

        return TEXTURE;
    }
}
