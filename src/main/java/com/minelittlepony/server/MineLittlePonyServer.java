package com.minelittlepony.server;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.events.CommonChannel;

public class MineLittlePonyServer implements ModInitializer {

    public static Identifier id(String name) {
        return Identifier.of("minelittlepony", name);
    }

    @Override
    public void onInitialize() {
        CommonChannel.bootstrap();
    }

}
