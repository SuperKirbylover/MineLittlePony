package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGearModel extends Model implements Gear {

    private final List<ModelPart> parts = new ArrayList<>();

    private final float stackingHeight;

    public AbstractGearModel(float stackingHeight) {
        super(RenderLayer::getEntitySolid);
        this.stackingHeight = stackingHeight;
    }

    public AbstractGearModel addPart(ModelPart t) {
        parts.add(t);
        return this;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, UUID interpolatorId) {
        render(stack, vertices, overlay, light, color);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer renderContext, int overlay, int light, int color) {
        parts.forEach(part -> {
            part.render(stack, renderContext, overlay, light, color);
        });
    }

    @Override
    public boolean isStackable() {
        return stackingHeight > 0;
    }

    @Override
    public float getStackingHeight() {
        return stackingHeight;
    }
}
