package com.minelittlepony.client.model.armour;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.util.ResourceUtil;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The default texture resolver used by Mine Little Pony.
 * <p>
 * Textures found are of the format:
 * <p>
 * namespace:textures/models/armor/material_layer_[outer|1|inner|2](_overlay)(_custom_#)(_pony).png
 * <p>
 * <p>
 * - Textures ending _pony are returned first if found
 * - _custom_# corresponds to a CustomModelData NBT integer value on the item passed, if available
 * - _overlay is used for the second layer of leather armour, or mods if they make use of it. Can be anything! Check your mod's documentation for values supported.
 * - outer|1|inner|2 is the layer. outer is an alias for 1 and inner is an alias for 2. Named versions are used instead of numbers if available.
 * - the "minecraft" namespace is always replaced with "minelittlepony"
 * <p>
 */
public class ArmourTextureResolver {
    public static final ArmourTextureResolver INSTANCE = new ArmourTextureResolver();

    private static final String CUSTOM_NONE = "none";

    private final Cache<String, ArmourTexture> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .<String, ArmourTexture>build();
    private final LoadingCache<String, ArmourTexture> layerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(texture -> {
                String[] parts = texture.split("#");
                if (!parts[1].equals(CUSTOM_NONE)) {
                    parts[0] = parts[0].replace(".png", parts[1] + ".png");
                }
                List<ArmourTexture> options = new ArrayList<>();
                ArmourTexture.resolveHumanOrPony(new Identifier(parts[0].replace("1", "inner").replace("2", "outer")), options);
                ArmourTexture.resolveHumanOrPony(new Identifier(parts[0]), options);
                ArmourTexture result = ArmourTexture.pick(options);
                if (result == ArmourTexture.UNKNOWN) {
                    MineLittlePony.logger.warn("Could not identify correct texture to use for {}. Was none of: [" + System.lineSeparator() + "{}" + System.lineSeparator() + "]", texture, options.stream()
                            .map(ArmourTexture::texture)
                            .map(Identifier::toString)
                            .collect(Collectors.joining("," + System.lineSeparator())));
                    return new ArmourTexture(new Identifier(parts[0]), ArmourVariant.LEGACY);
                }
                return result;
            }));
    private final LoadingCache<Identifier, List<ArmorMaterial.Layer>> nonDyedLayers = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(material -> List.of(new ArmorMaterial.Layer(material, "", false))));
    private final LoadingCache<Identifier, List<ArmorMaterial.Layer>> dyedLayers = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(material -> List.of(
                    new ArmorMaterial.Layer(material, "", false),
                    new ArmorMaterial.Layer(material, "overlay", true)
            )));

    public void invalidate() {
        cache.invalidateAll();
    }

    public ArmourTexture getTexture(ItemStack stack, ArmourLayer layer, ArmorMaterial.Layer armorLayer) {
        return layerCache.getUnchecked(armorLayer.getTexture(layer == ArmourLayer.OUTER) + "#" + getCustom(stack));
    }

    public List<ArmorMaterial.Layer> getArmorLayers(ItemStack stack, int dyeColor) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getMaterial().value().layers();
        }

        return (dyeColor == Colors.WHITE ? nonDyedLayers : dyedLayers).getUnchecked(Registries.ITEM.getId(stack.getItem()));
    }

    private String getCustom(ItemStack stack) {
        int custom = stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value();
        return custom == 0 ? "none" : String.valueOf(custom);
    }

    public record ArmourTexture(Identifier texture, ArmourVariant variant) {
        public static final ArmourTexture UNKNOWN = new ArmourTexture(TextureManager.MISSING_IDENTIFIER, ArmourVariant.LEGACY);

        public boolean validate() {
            return texture != TextureManager.MISSING_IDENTIFIER && ResourceUtil.textureExists(texture);
        }

        public static ArmourTexture pick(List<ArmourTexture> options) {
            return options.stream().filter(ArmourTexture::validate).findFirst().orElse(ArmourTexture.UNKNOWN);
        }

        @Nullable
        private static void resolveHumanOrPony(Identifier human, List<ArmourTexture> output) {
            String domain = human.getNamespace();
            if (Identifier.DEFAULT_NAMESPACE.contentEquals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }

            if (!PonyConfig.getInstance().disablePonifiedArmour.get()) {
                output.add(new ArmourTexture(new Identifier(domain, human.getPath().replace(".png", "_pony.png")), ArmourVariant.NORMAL));
            }

            output.add(new ArmourTexture(human, ArmourVariant.LEGACY));
        }
    }
}
