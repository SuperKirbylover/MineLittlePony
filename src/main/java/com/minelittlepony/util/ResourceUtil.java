package com.minelittlepony.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;

import java.util.Optional;

public final class ResourceUtil {
    public static String format(String template, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof Number)) {
                args[i] = toPathComponent(args[i]);
            }
        }
        return String.format(template, args);
    }

    private static String toPathComponent(Object value) {
        return value.toString().toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
    }

    public static boolean textureExists(Identifier texture) {
        return
            MinecraftClient.getInstance().getTextureManager().getOrDefault(texture, null) != null
            || MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent();
    }

    public static Optional<Identifier> verifyTexture(Identifier texture) {
        return textureExists(texture) ? Optional.of(texture) : Optional.empty();
    }

    public static Identifier ponify(Identifier texture) {
        String path = texture.getPath();
        if (path.endsWith("_pony.png")) {
            return texture;
        }
        if (Identifier.DEFAULT_NAMESPACE.contentEquals(texture.getNamespace())) {
            return MineLittlePony.id(path.replace(".png", "_pony.png")); // it's in the vanilla namespace, we provide these.
        }

        return texture.withPath(p -> p.replace(".png", "_pony.png"));
    }
}
