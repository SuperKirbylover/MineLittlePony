package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.armour.ArmourLayer;
import com.minelittlepony.api.pony.IPony;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class SeaponyModel<T extends LivingEntity> extends UnicornModel<T> {

    private final ModelPart leftFin;
    private final ModelPart centerFin;
    private final ModelPart rightFin;

    public SeaponyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        leftFin = tree.getChild("left_fin");
        rightFin = tree.getChild("right_fin");
        centerFin = tree.getChild("center_fin");
    }

    public SeaponyModel(ModelPart tree) {
        this(tree, false);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        setVisible(true);
        bodyRenderList.clear();
        bodyRenderList.add(body).add(body::rotate).add(forPart(tail)).add(leftFin, centerFin, rightFin);
    }

    @Override
    public void updateLivingState(T entity, IPony pony, ModelAttributes.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        // Seaponies can't sneak, silly
        sneaking = false;
        attributes.isCrouching = false;
    }

    @Override
    protected void ponySleep() {}

    @Override
    protected void ponySit() {}

    @Override
    public void setModelAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        float flapMotion = MathHelper.cos(ticks / 10) / 5;

        if (attributes.isSleeping) {
            flapMotion /= 2;
        }

        float finAngle = FIN_ROT_Y + flapMotion;

        leftFin.yaw = finAngle;
        rightFin.yaw = -finAngle;

        if (!attributes.isSleeping) {
            centerFin.roll = flapMotion;
        }

        if (!entity.isSubmergedInWater()) {
            leftArm.pitch -= 0.5F;
            rightArm.pitch -= 0.5F;
        }

        if (!entity.isSubmergedInWater() || entity.isOnGround()) {
            leftArm.yaw -= 0.5F;
            rightArm.yaw += 0.5F;
        }
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        super.rotateLegs(move, swing, ticks, entity);
        leftArm.pitch -= 1.4F;
        leftArm.yaw -= 0.3F;
        rightArm.pitch -= 1.4F;
        rightArm.yaw += 0.3F;
    }

    @Override
    protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {
        super.rotateLegsOnGround(move, swing, ticks, entity);
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {
        stack.translate(0, 0.6F, 0);
        super.transform(part, stack);
    }

    @Override
    public boolean hasMagic() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        leftSleeve.visible = false;
        rightSleeve.visible = false;
        jacket.visible = false;

        // hide the back legs
        leftLeg.visible = false;
        rightLeg.visible = false;
        leftPants.visible = false;
        rightPants.visible = false;

        leftFin.visible = visible;
        centerFin.visible = visible;
        rightFin.visible = visible;
    }

    public static class Armour<T extends LivingEntity> extends PonyArmourModel<T> {

        public Armour(ModelPart tree) {
            super(tree);
        }

        @Override
        public boolean setVisibilities(EquipmentSlot slot, ArmourLayer layer) {
            boolean result = super.setVisibilities(slot, layer);
            rightLeg.visible = false;
            leftLeg.visible = false;
            return result;
        }

        @Override
        public void updateLivingState(T entity, IPony pony, ModelAttributes.Mode mode) {
            super.updateLivingState(entity, pony, mode);

            // Seaponies can't sneak, silly
            sneaking = false;
        }

        @Override
        protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {
            super.rotateLegsOnGround(move, swing, ticks, entity);
        }

        @Override
        public void transform(BodyPart part, MatrixStack stack) {
            stack.translate(0, 0.6F, 0);

            super.transform(part, stack);
        }
    }
}
