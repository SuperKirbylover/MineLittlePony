package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.SkinsProxy;
import com.mojang.authlib.GameProfile;

public class ClientSkinsProxy extends SkinsProxy {
    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        return MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile).texture();
    }
}
