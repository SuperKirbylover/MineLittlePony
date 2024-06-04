package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.mson.api.model.PartBuilder;

public class PonySnout implements SubModel, MsonModel {

    private final ModelPart mare;
    private final ModelPart stallion;

    public PonySnout(ModelPart tree) {
        mare = tree.getChild("mare");
        stallion = tree.getChild("stallion");
    }

    @Override
    public void init(ModelView context) {
        PartBuilder head = context.getThis();
        head.addChild("mare", mare);
        head.addChild("stallion", stallion);
    }

    public void rotate(float x, float y, float z) {
        mare.setAngles(x, y, z);
        stallion.setAngles(x, y, z);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, ModelAttributes attributes) {
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        visible &= !attributes.isHorsey
                && !attributes.metadata.race().isHuman()
                && PonyConfig.getInstance().snuzzles.get();
        Gender gender = attributes.metadata.gender();

        mare.visible = (visible && gender.isMare());
        stallion.visible = (visible && gender.isStallion());
    }
}
