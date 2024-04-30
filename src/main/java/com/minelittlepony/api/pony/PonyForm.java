package com.minelittlepony.api.pony;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.render.entity.PlayerPonyRenderer;

import java.util.*;
import java.util.function.Predicate;

/**
 * The different forms a pony can take.
 * <p>
 * The default is land, which is your typical pony with four legs.
 * Other options are water (seaponies that go shoop-de-doo)
 * And Niriks (the burning form of kirins)
 */
public record PonyForm(Identifier id, Predicate<PlayerEntity> shouldApply, RendererFactory<?> factory) {
    public static final Identifier DEFAULT = DefaultPonySkinHelper.id("land");
    public static final Identifier SEAPONY = DefaultPonySkinHelper.id("seapony");
    public static final Identifier NIRIK = DefaultPonySkinHelper.id("nirik");

    public static final List<Identifier> VALUES = new ArrayList<>();
    public static final Map<Identifier, PonyForm> REGISTRY = new HashMap<>();

    public static void register(Identifier id, Predicate<PlayerEntity> shouldApply, RendererFactory<?> factory) {
        VALUES.add(0, id);
        REGISTRY.put(id, new PonyForm(id, shouldApply, factory));
    }

    @Nullable
    public static PonyForm of(PlayerEntity player) {
        for (Identifier id : VALUES) {
            PonyForm form = REGISTRY.get(id);
            if (form != null && form.shouldApply().test(player)) {
                return form;
            }
        }

        return null;
    }

    public interface RendererFactory<T extends PlayerPonyRenderer> {
        T create(EntityRendererFactory.Context context, boolean slimArms);
    }
}
