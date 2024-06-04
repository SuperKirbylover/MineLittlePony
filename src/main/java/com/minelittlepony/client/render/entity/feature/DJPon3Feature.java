package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.DJPon3EarsModel;
import com.minelittlepony.client.render.PonyRenderContext;

public class DJPon3Feature<T extends AbstractClientPlayerEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {

    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    public DJPon3Feature(PonyRenderContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        if ("deadmau5".equals(entity.getName().getString())) {
            stack.push();

            M body = getModelWrapper().body();

            body.transform(BodyPart.HEAD, stack);
            body.getHead().rotate(stack);

            stack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            stack.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);

            VertexConsumer vertices = renderContext.getBuffer(deadMau5.getLayer(entity.getSkinTextures().texture()));

            deadMau5.render(stack, vertices, OverlayTexture.DEFAULT_UV, light, Colors.WHITE);

            stack.pop();
        }
    }
}
