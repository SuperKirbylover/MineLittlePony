package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.model.armour.ArmourTextureResolver.ArmourTexture;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.common.util.Color;

import java.util.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {

    public ArmourFeature(PonyRenderContext<T, M> context, BakedModelManager bakery) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        Models<T, M> pony = getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER);
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER);
            }
        }
    }

    public static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer) {

        ItemStack stack = entity.getEquippedStack(armorSlot);

        if (stack.isEmpty()) {
            return;
        }

        boolean glint = stack.hasGlint();

        int color = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, -6265536) : Colors.WHITE;

        Set<PonyArmourModel<?>> models = glint ? new HashSet<>() : null;

        for (ArmorMaterial.Layer armorLayer : ArmourTextureResolver.INSTANCE.getArmorLayers(stack, color)) {
            ArmourTexture layerTexture = ArmourTextureResolver.INSTANCE.getTexture(stack, layer, armorLayer);

            var m = pony.getArmourModel(stack, layer, layerTexture.variant()).orElse(null);
            if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                float red = 1;
                float green = 1;
                float blue = 1;
                if (armorLayer.isDyeable() && color != Colors.WHITE) {
                    red = Color.r(color);
                    green = Color.g(color);
                    blue = Color.b(color);
                }
                m.render(matrices, provider.getBuffer(RenderLayer.getArmorCutoutNoCull(layerTexture.texture())), light, OverlayTexture.DEFAULT_UV, red, green, blue, 1);
                if (glint) {
                    models.add(m);
                }
            }
        }

        ArmorTrim trim = stack.get(DataComponentTypes.TRIM);

        if (trim != null && stack.getItem() instanceof ArmorItem armor) {
            var m = pony.getArmourModel(stack, layer, ArmourVariant.TRIM).orElse(null);
            if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                m.render(matrices, getTrimConsumer(provider, armor.getMaterial(), trim, layer), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            }
        }

        if (glint) {
            VertexConsumer glintConsumer = provider.getBuffer(RenderLayer.getArmorEntityGlint());
            for (var m : models) {
                m.render(matrices, glintConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            }
        }
    }

    private static VertexConsumer getTrimConsumer(VertexConsumerProvider provider, RegistryEntry<ArmorMaterial> material, ArmorTrim trim, ArmourLayer layer) {
        SpriteAtlasTexture armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        Sprite sprite = armorTrimsAtlas.getSprite(
            layer == ArmourLayer.INNER ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material)
        );
        return sprite.getTextureSpecificVertexConsumer(
                provider.getBuffer(TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal()))
        );
    }
}
