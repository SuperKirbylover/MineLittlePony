package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.meta.Race;

public class EnderStallionModel extends SkeleponyModel<EndermanEntity> {

    public boolean isCarrying;
    public boolean isAttacking;

    public boolean isAlicorn;
    public boolean isBoss;

    private final ModelPart leftHorn;
    private final ModelPart rightHorn;

    public EnderStallionModel(ModelPart tree) {
        super(tree);
        leftHorn = tree.getChild("left_horn");
        rightHorn = tree.getChild("right_horn");
    }

    @Override
    public void animateModel(EndermanEntity entity, float move, float swing, float ticks) {
        rightArmPose = isCarrying ? ArmPose.BLOCK : ArmPose.EMPTY;
        leftArmPose = rightArmPose;

        isUnicorn = true;
        isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
        isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;

        leftHorn.visible = rightHorn.visible = isBoss;
        horn.setVisible(!isBoss, attributes);
    }

    @Override
    public void setModelAngles(EndermanEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        if (isAttacking) {
            head.pivotY -= 5;
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        stack.push();
        stack.translate(0, -1.15F, 0);
        super.render(stack, vertices, overlay, light, color);
        stack.pop();
    }

    @Override
    public Race getRace() {
        return isAlicorn ? (super.getRace().hasHorn() ? Race.ALICORN : Race.PEGASUS) : super.getRace();
    }

    @Override
    public void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        arm.pitch = -0.3707964F;
        arm.pitch += 0.4F + MathHelper.sin(ticks * 0.067F) / 10;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        tail.setVisible(false, attributes);
        snout.setVisible(false, attributes);

        leftSleeve.visible = false;
        rightSleeve.visible = false;

        leftPants.visible = false;
        rightPants.visible = false;
    }

    @Override
    public boolean wingsAreOpen() {
        return isAttacking;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        return MathHelper.sin(ticks) + WINGS_HALF_SPREAD_ANGLE;
    }
}
