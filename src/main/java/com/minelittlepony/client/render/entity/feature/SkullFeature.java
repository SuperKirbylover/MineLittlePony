package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;

import java.util.Map;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
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

    private final ItemRenderer itemRenderer;

    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public SkullFeature(PonyRenderContext<T, M> renderPony, EntityModelLoader entityModelLoader, ItemRenderer itemRenderer) {
        super(renderPony);
        headModels = SkullBlockEntityRenderer.getModels(entityModelLoader);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.HEAD, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.SKULL)) {
            if (stack.isEmpty()) {
                continue;
            }

            M model = getModelWrapper().body();
            Item item = stack.getItem();

            matrices.push();

            model.transform(BodyPart.HEAD, matrices);
            model.getHead().rotate(matrices);

            if (model instanceof AbstractPonyModel) {
                matrices.translate(0, 0.225F, 0);
            } else {
                matrices.translate(0, 0, 0.15F);
            }

            if (item instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock) {
                boolean isVillager = entity instanceof VillagerDataContainer;

                renderSkull(matrices, provider, stack, isVillager, limbDistance, light);
            } else if (!(item instanceof ArmorItem a) || a.getSlotType() != EquipmentSlot.HEAD) {
                renderBlock(matrices, provider, entity, stack, light);
            }

            matrices.pop();
        }

        plugin.onArmourRendered(entity, matrices, provider, EquipmentSlot.BODY, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.SKULL);
    }

    private void renderBlock(MatrixStack matrices, VertexConsumerProvider provider, T entity, ItemStack stack, int light) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrices.scale(0.625F, -0.625F, -0.625F);
        matrices.translate(0, 0.6F, -0.21F);

        itemRenderer.renderItem(entity, stack, ModelTransformationMode.HEAD, false, matrices, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, entity.getId() + ModelTransformationMode.HEAD.ordinal());
    }

    private void renderSkull(MatrixStack matrices, VertexConsumerProvider provider, ItemStack stack, boolean isVillager, float limbDistance, int light) {
        matrices.translate(0, 0, -0.14F);
        float f = 1.1875f;
        matrices.scale(f, -f, -f);
        if (isVillager) {
            matrices.translate(0, 0.0625F, 0);
        }

        matrices.translate(-0.5, 0, -0.5);
        SkullType type = ((AbstractSkullBlock) ((BlockItem) stack.getItem()).getBlock()).getSkullType();
        SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.get(type);
        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(type, stack.get(DataComponentTypes.PROFILE));

        SkullBlockEntityRenderer.renderSkull(null, 180, f, matrices, provider, light, skullBlockEntityModel, renderLayer);
    }
}
