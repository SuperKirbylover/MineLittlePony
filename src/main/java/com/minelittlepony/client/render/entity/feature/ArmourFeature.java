package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.common.util.Color;

import java.util.*;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Colors;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {
    public ArmourFeature(PonyRenderContext<T, M> context, BakedModelManager bakery) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        renderArmor(getModelWrapper(), matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch);
    }

    public static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER, plugin);
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER, plugin);
            }
        }
    }

    private static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer, ArmourRendererPlugin plugin) {

        for (ItemStack stack : plugin.getArmorStacks(entity, armorSlot, layer)) {
            if (stack.isEmpty()) {
                continue;
            }

            float glintAlpha = plugin.getGlintAlpha(armorSlot, stack);
            boolean glint = glintAlpha > 0;
            int color = plugin.getDyeColor(armorSlot, stack);

            Set<PonyArmourModel<?>> models = glint ? new HashSet<>() : null;

            ArmourTextureLookup textureLookup = plugin.getTextureLookup();

            float alpha = plugin.getArmourAlpha(armorSlot, layer);

            if (alpha > 0) {
                for (ArmorMaterial.Layer armorLayer : textureLookup.getArmorLayers(stack, color)) {
                    ArmourTexture layerTexture = textureLookup.getTexture(stack, layer, armorLayer);

                    if (layerTexture == ArmourTexture.UNKNOWN) {
                        continue;
                    }

                    var m = pony.getArmourModel(stack, layer, layerTexture.variant()).orElse(null);
                    if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                        VertexConsumer armorConsumer = plugin.getArmourConsumer(armorSlot, provider, layerTexture.texture(), layer);
                        if (armorConsumer != null) {
                            float red = 1;
                            float green = 1;
                            float blue = 1;
                            if (armorLayer.isDyeable() && color != Colors.WHITE) {
                                red = Color.r(color);
                                green = Color.g(color);
                                blue = Color.b(color);
                            }
                            m.render(matrices, armorConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
                        }
                        if (glint) {
                            models.add(m);
                        }
                    }
                }
            }

            ArmorTrim trim = stack.get(DataComponentTypes.TRIM);

            if (trim != null && stack.getItem() instanceof ArmorItem armor) {
                float trimAlpha = plugin.getTrimAlpha(armorSlot, armor.getMaterial(), trim, layer);
                if (trimAlpha > 0) {
                    var m = pony.getArmourModel(stack, layer, ArmourVariant.TRIM).orElse(null);
                    if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                        VertexConsumer trimConsumer = plugin.getTrimConsumer(armorSlot, provider, armor.getMaterial(), trim, layer);
                        if (trimConsumer != null) {
                            m.render(matrices, trimConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                        }
                    }
                }
            }

            if (glint) {
                VertexConsumer glintConsumer = plugin.getGlintConsumer(armorSlot, provider, layer);
                if (glintConsumer != null) {
                    for (var m : models) {
                        m.render(matrices, glintConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, glintAlpha);
                    }
                }
            }
        }
    }
}
