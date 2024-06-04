package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.WearableGear;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.Calendar;
import java.util.UUID;

public class DeerAntlers extends WearableGear {
    private static boolean dayChecked = false;
    private static boolean dayResult = false;
    private static boolean isChristmasDay() {
        if (!dayChecked) {
            dayChecked = true;
            Calendar cal = Calendar.getInstance();
            dayResult = cal.get(Calendar.MONTH) == Calendar.DECEMBER
                     && cal.get(Calendar.DAY_OF_MONTH) == 25;
        }


        return dayResult;
    }

    private final ModelPart left;
    private final ModelPart right;

    private int tint;

    public DeerAntlers(ModelPart tree) {
        super(Wearable.ANTLERS, BodyPart.HEAD, 0);
        left = tree.getChild("left");
        right = tree.getChild("right");
    }

    @Override
    public boolean canRender(PonyModel<?> model, Entity entity) {
        return isChristmasDay() || super.canRender(model, entity);
    }

    @Override
    public void pose(PonyModel<?> model, Entity entity, boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float pi = MathHelper.PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662f;
        float srt = swing / 10;

        bodySwing = MathHelper.cos(mve + pi) * srt;

        bodySwing += 0.1F;

        tint = model.getAttributes().metadata.glowColor();
        left.roll = bodySwing;
        right.roll = -bodySwing;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, UUID interpolatorId) {
        if (tint != 0) {
            color = tint;
        }

        left.render(stack, vertices, overlay, light, color);
        right.render(stack, vertices, overlay, light, color);
    }
}
