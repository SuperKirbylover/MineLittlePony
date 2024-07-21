package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.npc.*;
import com.minelittlepony.common.util.settings.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.entity.EntityType;

/**
 * Central location where new entity renderers are registered and applied.
 */
public class MobRenderers {
    public static final Map<String, MobRenderers> REGISTRY = new HashMap<>();

    public static final MobRenderers VILLAGER = register("villagers", (state, pony) -> {
        pony.switchRenderer(state, EntityType.VILLAGER, VillagerPonyRenderer::new);
        pony.switchRenderer(state, EntityType.WITCH, WitchRenderer::new);
        pony.switchRenderer(state, EntityType.ZOMBIE_VILLAGER, ZomponyVillagerRenderer::new);
        pony.switchRenderer(state, EntityType.WANDERING_TRADER, TraderRenderer::new);
    });
    public static final MobRenderers ILLAGER = register("illagers", (state, pony) -> {
        pony.switchRenderer(state, EntityType.VEX, VexRenderer::new);
        pony.switchRenderer(state, EntityType.EVOKER, IllagerPonyRenderer::evoker);
        pony.switchRenderer(state, EntityType.VINDICATOR, IllagerPonyRenderer::vindicator);
        pony.switchRenderer(state, EntityType.ILLUSIONER, IllagerPonyRenderer.Illusionist::new);
        pony.switchRenderer(state, EntityType.PILLAGER, PillagerRenderer::new);
    });
    public static final MobRenderers ZOMBIE = register("zombies", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ZOMBIE, ZomponyRenderer::zombie);
        pony.switchRenderer(state, EntityType.HUSK, ZomponyRenderer::husk);
        pony.switchRenderer(state, EntityType.GIANT, ZomponyRenderer::giant);
        pony.switchRenderer(state, EntityType.DROWNED, ZomponyRenderer::drowned);
    });
    public static final MobRenderers PIGLIN = register("pigzombies", (state, pony) -> {
        pony.switchRenderer(state, EntityType.PIGLIN, PonyPiglinRenderer::piglin);
        pony.switchRenderer(state, EntityType.PIGLIN_BRUTE, PonyPiglinRenderer::brute);
        pony.switchRenderer(state, EntityType.ZOMBIFIED_PIGLIN, PonyPiglinRenderer::zombified);
        if (!PonyConfig.getInstance().noFun.get()) {
            pony.switchRenderer(state, EntityType.PIG, PonyPigRenderer::new);
        }
    });
    public static final MobRenderers SKELETON = register("skeletons", (state, pony) -> {
        pony.switchRenderer(state, EntityType.SKELETON, SkeleponyRenderer::skeleton);
        pony.switchRenderer(state, EntityType.STRAY, SkeleponyRenderer::stray);
        pony.switchRenderer(state, EntityType.WITHER_SKELETON, SkeleponyRenderer::wither);
    });
    public static final MobRenderers GUARDIAN = register("guardians", (state, pony) -> {
        pony.switchRenderer(state, EntityType.GUARDIAN, SeaponyRenderer::guardian);
        pony.switchRenderer(state, EntityType.ELDER_GUARDIAN, SeaponyRenderer::elder);
    });
    public static final MobRenderers ENDERMAN = register("endermen", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ENDERMAN, EnderStallionRenderer::new);
    });
    public static final MobRenderers INANIMATE = register("inanimates", (state, pony) -> {
       pony.switchRenderer(state, EntityType.ARMOR_STAND, PonyStandRenderer::new);
    });
    public static final MobRenderers STRIDER = register("striders", (state, pony) -> {
        pony.switchRenderer(state, EntityType.STRIDER, StriderRenderer::new);
    });
    public static final MobRenderers ALLAY = register("allays", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ALLAY, AllayRenderer::new);
    });

    private final BiConsumer<MobRenderers, PonyRenderDispatcher> changer;

    public final String name;

    private boolean lastState;

    private MobRenderers(String name, BiConsumer<MobRenderers, PonyRenderDispatcher> changer) {
        this.name = name;
        this.changer = changer;
    }

    public Setting<Boolean> option() {
        return PonyConfig.getInstance().getCategory("entities").<Boolean>get(name);
    }

    public boolean set(boolean value) {
        value = option().set(value);
        apply(MineLittlePony.getInstance().getRenderDispatcher(), false);
        return value;
    }

    public boolean get() {
        return option().get();
    }

    public static MobRenderers register(String name, BiConsumer<MobRenderers, PonyRenderDispatcher> changer) {
        return REGISTRY.computeIfAbsent(name, n -> new MobRenderers(name, changer));
    }

    void apply(PonyRenderDispatcher dispatcher, boolean force) {
        boolean state = get();
        if (state != lastState || force) {
            lastState = state;
            changer.accept(this, dispatcher);
        }
    }
}