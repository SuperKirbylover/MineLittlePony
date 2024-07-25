package com.minelittlepony.api.pony;

import java.util.Optional;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

/**
 * Proxy handler for getting player skin data from HDSkins
 */
public class SkinsProxy {
    public static SkinsProxy INSTANCE = new SkinsProxy();
    private static final SkinsProxy DEFAULT = INSTANCE;

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
        return null;
    }

    public Optional<Identifier> getSkin(Identifier skinTypeId, PlayerEntity player) {
        return Optional.empty();
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {
        return Set.of();
    }
}
