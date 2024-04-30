package com.minelittlepony.client;

import com.minelittlepony.api.pony.Pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PonyBounds {
    private static Vec3d getBaseRidingOffset(LivingEntity entity) {
        float delta = MinecraftClient.getInstance().getTickDelta();
        return new Vec3d(
                MathHelper.lerp(delta, entity.prevX, entity.getX()),
                MathHelper.lerp(delta, entity.prevY, entity.getY()),
                MathHelper.lerp(delta, entity.prevZ, entity.getZ())
        );
    }

    public static Box getBoundingBox(Pony pony, LivingEntity entity) {
        final float scale = pony.size().scaleFactor();
        final float width = entity.getWidth() * scale;
        final float height = entity.getHeight() * scale;

        return new Box(-width, 0, -width, width, height, width).offset(getBaseRidingOffset(entity));
    }
}
