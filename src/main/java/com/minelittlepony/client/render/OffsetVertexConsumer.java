package com.minelittlepony.client.render;

import net.minecraft.client.render.VertexConsumer;

import org.joml.Vector3f;

// TODO: This works but it only outsets the faces making them look disjointed. We need to scale the vertices relative to the quad center as well.
public class OffsetVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;

    private double x, y, z;
    private int r, g, b, a;
    private float textureU, textureV;
    private int overlayU, overlayV;
    private int lightU, lightV;
    private float normalX, normalY, normalZ;

    private final Vector3f normalVector = new Vector3f();

    private final float offsetDistance;

    public OffsetVertexConsumer(VertexConsumer delegate, float offsetDistance) {
        this.delegate = delegate;
        this.offsetDistance = offsetDistance;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        textureU = u;
        textureV = v;
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        overlayU = u;
        overlayV = v;
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        lightU = u;
        lightV = v;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        normalX = x;
        normalY = y;
        normalZ = z;
        return this;
    }

    @Override
    public void next() {
        normalVector.set(normalX, normalY, normalZ).normalize(offsetDistance);
        delegate.vertex(
                (normalVector.x + (float)x),
                (normalVector.y + (float)y),
                (normalVector.z + (float)z),
                r / 255F, g / 255F, b / 255F, a / 255F,
                textureU, textureV,
                overlayU | overlayV << 16,
                lightU | lightV << 16,
                normalX, normalY, normalZ
        );
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        delegate.fixedColor(red, green, blue, alpha);
    }

    @Override
    public void unfixColor() {
        delegate.unfixColor();
    }
}
