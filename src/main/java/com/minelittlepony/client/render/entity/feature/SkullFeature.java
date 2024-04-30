package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;

import java.util.Map;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.VillagerDataContainer;

public class SkullFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {

    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public SkullFeature(PonyRenderContext<T, M> renderPony, EntityModelLoader entityModelLoader) {
        super(renderPony);
        headModels = SkullBlockEntityRenderer.getModels(entityModelLoader);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ItemStack itemstack = entity.getEquippedStack(EquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            M model = getModelWrapper().body();
            Item item = itemstack.getItem();

            stack.push();

            model.transform(BodyPart.HEAD, stack);
            model.getHead().rotate(stack);

            if (model instanceof AbstractPonyModel) {
                stack.translate(0, 0.225F, 0);
            } else {
                stack.translate(0, 0, 0.15F);
            }

            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                boolean isVillager = entity instanceof VillagerDataContainer;

                renderSkull(stack, renderContext, itemstack, isVillager, limbDistance, lightUv);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
                renderBlock(stack, renderContext, entity, itemstack, lightUv);
            }

            stack.pop();
        }

    }

    private void renderBlock(MatrixStack stack, VertexConsumerProvider renderContext, T entity, ItemStack itemstack, int lightUv) {
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.scale(0.625F, -0.625F, -0.625F);
        stack.translate(0, 0.6F, -0.21F);

        MinecraftClient.getInstance().getItemRenderer()
            .renderItem(entity, itemstack, ModelTransformationMode.HEAD, false, stack, renderContext, entity.getWorld(), lightUv, OverlayTexture.DEFAULT_UV, entity.getId() + ModelTransformationMode.HEAD.ordinal());
    }

    private void renderSkull(MatrixStack stack, VertexConsumerProvider renderContext, ItemStack itemstack, boolean isVillager, float limbDistance, int lightUv) {
        stack.translate(0, 0, -0.14F);
        float f = 1.1875f;
        stack.scale(f, -f, -f);
        if (isVillager) {
            stack.translate(0, 0.0625F, 0);
        }

        stack.translate(-0.5, 0, -0.5);
        SkullType type = ((AbstractSkullBlock) ((BlockItem) itemstack.getItem()).getBlock()).getSkullType();
        SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.get(type);
        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(type, itemstack.get(DataComponentTypes.PROFILE));

        SkullBlockEntityRenderer.renderSkull(null, 180, f, stack, renderContext, lightUv, skullBlockEntityModel, renderLayer);
    }
}
