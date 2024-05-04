package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.*;

public class CapeFeature<M extends ClientPonyModel<AbstractClientPlayerEntity>> extends AbstractPonyFeature<AbstractClientPlayerEntity, M> {

    public CapeFeature(PonyRenderContext<AbstractClientPlayerEntity, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, AbstractClientPlayerEntity player, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        M model = getModelWrapper().body();

        if (!player.isInvisible()
            && player.isPartVisible(PlayerModelPart.CAPE)
            && player.getSkinTextures().capeTexture() != null) {

            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

            VertexConsumer vertices = plugin.getCapeConsumer(player, provider, player.getSkinTextures().capeTexture());
            if (vertices == null) {
                return;
            }

            matrices.push();

            matrices.translate(0, 0.24F, 0);
            if (model.getAttributes().isLyingDown) {
                matrices.translate(0, -0.05F, 0);
            }
            model.transform(BodyPart.BODY, matrices);
            model.getBodyPart(BodyPart.BODY).rotate(matrices);

            double capeX = MathHelper.lerp(tickDelta, player.capeX, player.prevCapeX) - MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double capeY = MathHelper.lerp(tickDelta, player.capeY, player.prevCapeY) - MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double capeZ = MathHelper.lerp(tickDelta, player.capeZ, player.prevCapeZ) - MathHelper.lerp(tickDelta, player.prevZ, player.getZ());

            float motionYaw = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);

            double sin = MathHelper.sin(motionYaw * MathHelper.RADIANS_PER_DEGREE);
            double cos = -MathHelper.cos(motionYaw * MathHelper.RADIANS_PER_DEGREE);

            float capeMotionY = (float) capeY * 10;

            if (capeMotionY < -6) capeMotionY = -6;
            if (capeMotionY > 32) capeMotionY = 32;

            float capeMotionX = (float) (capeX * sin + capeZ * cos) * 100;

            float diagMotion =  (float) (capeX * cos - capeZ * sin) * 100;

            if (capeMotionX < 0) capeMotionX = 0;

            float camera = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
            capeMotionY += MathHelper.sin(MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6) * 32 * camera;

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(2 + capeMotionX / 12 + capeMotionY));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees( diagMotion / 2));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-diagMotion / 2));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

            model.renderCape(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();

            plugin.onArmourRendered(player, matrices, provider, EquipmentSlot.BODY, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.CAPE);
        }
    }
}
