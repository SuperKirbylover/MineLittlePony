package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.*;

import com.minelittlepony.util.ResourceUtil;

import java.util.*;

public class ProfessionTextureSupplier<T extends VillagerDataContainer> implements TextureSupplier<T> {

    public static <T extends VillagerDataContainer> TextureSupplier<T> create(TextureSupplier<String> formatter) {
        return TextureSupplier.memoize(new ProfessionTextureSupplier<>(formatter), ProfessionTextureSupplier::getKey);
    }

    private final TextureSupplier<String> formatter;

    private final Identifier fallback;

    public ProfessionTextureSupplier(TextureSupplier<String> formatter) {
        this.formatter = formatter;
        this.fallback = formatter.apply("villager_pony");
    }

    @Override
    public Identifier apply(T container) {
        return apply(container.getVillagerData());
    }

    public Identifier apply(VillagerData t) {
        return getTexture(t.getType(), t.getProfession());
    }

    public static String getKey(VillagerDataContainer container) {
        VillagerData t = container.getVillagerData();
        return ResourceUtil.format("pony/%s/%s", t.getType(), t.getProfession());
    }

    private Identifier getTexture(final VillagerType type, final VillagerProfession profession) {
        String key = ResourceUtil.format("pony/%s/%s", type, profession);
        return verifyTexture(formatter.apply(key)).orElseGet(() -> {
            if (type == VillagerType.PLAINS) {
                // if texture loading fails, use the fallback.
                return fallback;
            }

            return getTexture(VillagerType.PLAINS, profession);
        });
    }

    protected Optional<Identifier> verifyTexture(Identifier texture) {
        return MinecraftClient.getInstance().getResourceManager().getResource(texture).map(i -> texture);
    }

    public static boolean isBestPony(LivingEntity entity) {
        if (!entity.hasCustomName()) {
            return false;
        }
        String name = entity.getCustomName().getString();
        return "Derpy".equals(name) || (entity.isBaby() && "Dinky".equals(name));
    }

    public static boolean isCrownPony(LivingEntity entity) {
        return isBestPony(entity) && entity.getUuid().getLeastSignificantBits() % 20 == 0;
    }
}
