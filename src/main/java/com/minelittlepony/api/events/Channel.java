package com.minelittlepony.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.api.pony.PonyData;

@Environment(EnvType.CLIENT)
public class Channel {
    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony:Networking");

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
           registered = false;
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(PonyDataRequest.INSTANCE);
        });

        PayloadTypeRegistry.playS2C().register(PonyDataRequest.ID, PonyDataRequest.CODEC);
        PayloadTypeRegistry.playS2C().register(PonyDataPayload.ID, PonyDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PonyDataPayload.ID, PonyDataPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(PonyDataRequest.ID, (packet, context) -> {
            registered = true;
            LOGGER.info("Server has just consented");
        });

        ServerPlayNetworking.registerGlobalReceiver(PonyDataPayload.ID, (packet, context) -> {
            context.player().server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(context.player(), packet.data(), EnvType.SERVER);
            });
        });
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static boolean broadcastPonyData(PonyData packet) {
        if (!isRegistered()) {
            return false;
        }
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Client packet send called by the server");
        }

        ClientPlayNetworking.send(new PonyDataPayload(packet));
        return true;
    }

    record PonyDataPayload(PonyData data) implements CustomPayload {
        public static final Id<PonyDataPayload> ID = new Id<>(Identifier.of("minelittlepony", "pony_data"));
        public static final PacketCodec<PacketByteBuf, PonyDataPayload> CODEC = CustomPayload.codecOf(
                (p, buffer) -> MsgPonyData.write(p.data(), buffer),
                buffer -> new PonyDataPayload(MsgPonyData.read(buffer))
        );

        @Override
        public Id<PonyDataPayload> getId() {
            return ID;
        }
    }

    record PonyDataRequest() implements CustomPayload {
        public static final PonyDataRequest INSTANCE = new PonyDataRequest();
        private static final Id<PonyDataRequest> ID = new Id<>(Identifier.of("minelittlepony", "request_pony_data"));
        public static final PacketCodec<PacketByteBuf, PonyDataRequest> CODEC = PacketCodec.unit(INSTANCE);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}