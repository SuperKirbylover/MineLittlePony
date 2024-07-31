package com.minelittlepony.api.pony;

import java.util.*;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.google.common.cache.*;
import com.minelittlepony.server.MineLittlePonyServer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;

/**
 * Proxy handler for getting player skin data from HDSkins
 */
public class SkinsProxy {
    public static SkinsProxy INSTANCE = new SkinsProxy();
    private static final SkinsProxy DEFAULT = INSTANCE;

    private final LoadingCache<GameProfile, GameProfile> profileCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(profile -> {
                var result = MineLittlePonyServer.getServer().getSessionService().fetchProfile(profile.getId(), false);
                return result == null ? profile : result.profile();
            }));

    public static SkinsProxy getInstance() {
        return INSTANCE;
    }

    protected SkinsProxy() {
        if (INSTANCE == DEFAULT) {
            INSTANCE = this;
        }
    }

    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        MinecraftServer server = MineLittlePonyServer.getServer();
        if (server != null) {
            profile = profileCache.getUnchecked(profile);

            MinecraftProfileTextures textures = server.getSessionService().getTextures(profile);

            if (textures != MinecraftProfileTextures.EMPTY) {
                return Identifier.of(textures.skin().getUrl());
            }
        }
        return null;
    }

    public Optional<Identifier> getSkin(Identifier skinTypeId, PlayerEntity player) {
        return Optional.empty();
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {
        return Set.of();
    }
}
