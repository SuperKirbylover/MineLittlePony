package com.minelittlepony.api.pony;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.api.pony.meta.Race;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = Pony.id("textures/entity/player/wide/steve_pony.png");

    public static final Identifier SEAPONY_SKIN_TYPE_ID = Pony.id("seapony");
    public static final Identifier NIRIK_SKIN_TYPE_ID = Pony.id("nirik");

    private static final Function<SkinTextures, SkinTextures> SKINS = Util.memoize(original -> new SkinTextures(
            Pony.id(original.texture().getPath().replace(".png", "_pony.png")),
            null,
            null,
            null,
            original.model(),
            false
    ));

    public static SkinTextures getTextures(SkinTextures original) {
        return SKINS.apply(original);
    }

    public static String getModelType(UUID id) {
        SkinTextures textures = DefaultSkinHelper.getSkinTextures(id);
        return getModelType(Pony.getManager().getPony(textures.texture(), id).race(), textures.model());
    }

    public static String getModelType(Race race, SkinTextures.Model armShape) {
        if (race.isHuman()) {
            return armShape.getName();
        }
        return (armShape == SkinTextures.Model.SLIM) ? armShape.getName() + race.name().toLowerCase(Locale.ROOT) : race.name().toLowerCase(Locale.ROOT);
    }
}
