package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SkullFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {
    private final ItemRenderer itemRenderer;

    public SkullFeature(PonyRenderContext<T, M> renderPony, EntityModelLoader entityModelLoader, ItemRenderer itemRenderer) {
        super(renderPony);
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

            if (entity.isBaby() && !(entity instanceof VillagerEntity)) {
                matrices.translate(0, 0.03125F, 0);
                matrices.scale(0.7F, 0.7F, 0.7F);
                matrices.translate(0, 1, 0);
            }

            model.transform(BodyPart.HEAD, matrices);
            model.getHead().rotate(matrices);

            boolean isVillager = entity instanceof VillagerEntity || entity instanceof ZombieVillagerEntity;

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (item instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock) {
                float n = 1.1875F;
                matrices.scale(n, -n, -n);
                matrices.translate(0, -0.1F, 0.1F);
                matrices.translate(-0.5, 0, -0.5);
                PonySkullRenderer.INSTANCE.renderSkull(matrices, provider, stack, entity, tickDelta, light, true);
            } else if (!(item instanceof ArmorItem a) || a.getSlotType() != EquipmentSlot.HEAD) {
                matrices.translate(0, 0.1F, -0.1F);
                HeadFeatureRenderer.translate(matrices, isVillager);
                itemRenderer.renderItem(entity, stack, ModelTransformationMode.HEAD, false, matrices, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, entity.getId() + ModelTransformationMode.HEAD.ordinal());
            }

            matrices.pop();
        }

        plugin.onArmourRendered(entity, matrices, provider, EquipmentSlot.BODY, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.SKULL);
    }
}
