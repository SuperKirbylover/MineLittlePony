package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public interface ArmourRendererPlugin {
    AtomicReference<ArmourRendererPlugin> INSTANCE = new AtomicReference<>(new ArmourRendererPlugin() {});

    static void register(Function<ArmourRendererPlugin, ArmourRendererPlugin> constructor) {
        INSTANCE.set(constructor.apply(INSTANCE.get()));
    }

    default ArmourTextureLookup getTextureLookup() {
        return ArmourTextureResolver.INSTANCE;
    }

    default ItemStack[] getArmorStacks(LivingEntity entity, EquipmentSlot armorSlot, ArmourLayer layer) {
        return new ItemStack[] { entity.getEquippedStack(armorSlot) };
    }

    default float getGlintAlpha(EquipmentSlot slot, ItemStack stack) {
        return stack.hasGlint() ? 1 : 0;
    }

    default int getDyeColor(EquipmentSlot slot, ItemStack stack) {
        return stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, -6265536) : Colors.WHITE;
    }

    default float getArmourAlpha(EquipmentSlot slot, ArmourLayer layer) {
        return 1F;
    }

    default float getTrimAlpha(EquipmentSlot slot, RegistryEntry<ArmorMaterial> material, ArmorTrim trim, ArmourLayer layer) {
        return 1F;
    }

    @Nullable
    default VertexConsumer getTrimConsumer(EquipmentSlot slot, VertexConsumerProvider provider, RegistryEntry<ArmorMaterial> material, ArmorTrim trim, ArmourLayer layer) {
        @Nullable RenderLayer renderLayer = getTrimLayer(slot, material, trim, layer);
        if (renderLayer == null) {
            return null;
        }
        SpriteAtlasTexture armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        Sprite sprite = armorTrimsAtlas.getSprite(layer == ArmourLayer.INNER ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        return sprite.getTextureSpecificVertexConsumer(provider.getBuffer(renderLayer));
    }

    @Nullable
    default RenderLayer getTrimLayer(EquipmentSlot slot, RegistryEntry<ArmorMaterial> material, ArmorTrim trim, ArmourLayer layer) {
        return TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal());
    }

    @Nullable
    default VertexConsumer getArmourConsumer(EquipmentSlot slot, VertexConsumerProvider provider, Identifier texture, ArmourLayer layer) {
        @Nullable RenderLayer renderLayer = getArmourLayer(slot, texture, layer);
        return renderLayer == null ? null : provider.getBuffer(renderLayer);
    }

    @Nullable
    default RenderLayer getArmourLayer(EquipmentSlot slot, Identifier texture, ArmourLayer layer) {
        return RenderLayer.getArmorCutoutNoCull(texture);
    }

    @Nullable
    default VertexConsumer getGlintConsumer(EquipmentSlot slot, VertexConsumerProvider provider, ArmourLayer layer) {
        @Nullable RenderLayer renderLayer = getGlintLayer(slot, layer);
        return renderLayer == null ? null : provider.getBuffer(renderLayer);
    }

    @Nullable
    default RenderLayer getGlintLayer(EquipmentSlot slot, ArmourLayer layer) {
        return RenderLayer.getArmorEntityGlint();
    }
}
