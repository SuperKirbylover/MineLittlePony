package com.minelittlepony.client.model.armour;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import com.google.common.cache.*;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.minelittlepony.client.MineLittlePony;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class ArmourTextureResolver implements ArmourTextureLookup, IdentifiableResourceReloadListener {
    public static final Identifier ID = MineLittlePony.id("armor_textures");
    public static final ArmourTextureResolver INSTANCE = new ArmourTextureResolver();

    private static final Interner<ArmorMaterial.Layer> LAYER_INTERNER = Interners.newWeakInterner();

    private final LoadingCache<ArmourParameters, ArmourTexture> layerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(parameters -> {
                return Stream.of(ArmourTexture.legacy(parameters.material().getTexture(parameters.layer() == ArmourLayer.OUTER))).flatMap(i -> {
                    if (parameters.layer() == ArmourLayer.OUTER) {
                        return Stream.of(i, ArmourTexture.legacy(parameters.material().getTexture(false)));
                    }
                    return Stream.of(i);
                }).flatMap(i -> {
                    if (parameters.customModelId() != 0) {
                        return Stream.of(ArmourTexture.legacy(i.texture().withPath(p -> p.replace(".png", parameters.customModelId() + ".png"))), i);
                    }
                    return Stream.of(i);
                }).flatMap(this::performLookup).findFirst().orElse(ArmourTexture.UNKNOWN);
            }));
    private final LoadingCache<Identifier, List<ArmorMaterial.Layer>> nonDyedLayers = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(material -> List.of(LAYER_INTERNER.intern(new ArmorMaterial.Layer(material, "", false)))));
    private final LoadingCache<Identifier, List<ArmorMaterial.Layer>> dyedLayers = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(material -> List.of(
                    LAYER_INTERNER.intern(new ArmorMaterial.Layer(material, "", false)),
                    LAYER_INTERNER.intern(new ArmorMaterial.Layer(material, "overlay", true))
            )));

    private Stream<ArmourTexture> performLookup(ArmourTexture id) {
        List<ArmourTexture> options = Stream.of(id)
                .flatMap(ArmourTexture::named)
                .flatMap(ArmourTexture::ponify)
                .toList();
        return options.stream().distinct()
                .filter(ArmourTexture::validate)
                .findFirst()
                .or(() -> {
            MineLittlePony.LOGGER.warn("Could not identify correct texture to use for {}. Was none of: [" + System.lineSeparator() + "{}" + System.lineSeparator() + "]", id, options.stream()
                    .map(ArmourTexture::texture)
                    .map(Identifier::toString)
                    .collect(Collectors.joining("," + System.lineSeparator())));
            return Optional.empty();
        }).stream();
    }

    public void invalidate() {
        layerCache.invalidateAll();
        nonDyedLayers.invalidateAll();
        dyedLayers.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(this::invalidate, prepareExecutor).thenCompose(synchronizer::whenPrepared);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public ArmourTexture getTexture(ItemStack stack, ArmourLayer layer, ArmorMaterial.Layer armorLayer) {
        return layerCache.getUnchecked(new ArmourParameters(layer, armorLayer, getCustom(stack)));
    }

    @Override
    public List<ArmorMaterial.Layer> getArmorLayers(ItemStack stack, int dyeColor) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getMaterial().value().layers();
        }

        return (dyeColor == Colors.WHITE ? nonDyedLayers : dyedLayers).getUnchecked(Registries.ITEM.getId(stack.getItem()));
    }

    private int getCustom(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value();
    }

    private record ArmourParameters(ArmourLayer layer, ArmorMaterial.Layer material, int customModelId) {

    }
}
