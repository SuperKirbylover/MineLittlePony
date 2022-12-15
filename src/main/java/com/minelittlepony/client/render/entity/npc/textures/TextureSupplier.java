package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.util.FunctionUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A texture pool for generating multiple associated textures.
 */
@FunctionalInterface
public interface TextureSupplier<T> extends Function<T, Identifier> {
    /**
     * Supplies a new texture. May be generated for returned from a pool indexed by the given key.
     */
    @Override
    Identifier apply(T key);

    static TextureSupplier<String> formatted(String domain, String path) {
        return key -> new Identifier(domain, String.format(path, key));
    }

    static <T extends LivingEntity> TextureSupplier<T> ofPool(Identifier poolId, TextureSupplier<T> fallback) {
        final Function<T, Identifier> cache = FunctionUtil.memoize(entity -> {
            return MineLittlePony.getInstance().getVariatedTextures()
                    .get(poolId)
                    .getByName(entity.getCustomName().getString(), entity.getUuid())
                    .orElse(null);
        }, entity -> entity.getCustomName().getString() + "_" + entity.getUuidAsString());
        return entity -> {
            Identifier override = entity.hasCustomName() ? cache.apply(entity) : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }

    static <A> TextureSupplier<A> memoize(Function<A, Identifier> func, Function<A, String> keyFunc) {
        final Map<String, Identifier> cache = new ConcurrentHashMap<>();
        return a -> cache.computeIfAbsent(keyFunc.apply(a), k -> func.apply(a));
    }
}
