package com.minelittlepony.client.render.entity.npc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.AbstractPonyFeature;
import com.minelittlepony.client.util.render.TextureFlattener;
import com.minelittlepony.util.ResourceUtil;

import java.util.*;

class NpcClothingFeature<
        T extends LivingEntity & VillagerDataContainer,
        M extends EntityModel<T> & PonyModel<T>,
        C extends FeatureRendererContext<T, M> & PonyRenderContext<T, M>> extends AbstractPonyFeature<T, M> {

    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap<>(), a -> {
        a.put(1, Identifier.ofVanilla("stone"));
        a.put(2, Identifier.ofVanilla("iron"));
        a.put(3, Identifier.ofVanilla("gold"));
        a.put(4, Identifier.ofVanilla("emerald"));
        a.put(5, Identifier.ofVanilla("diamond"));
    });
    private final Set<Identifier> loadedTextures = new HashSet<>();

    private final String entityType;

    public NpcClothingFeature(C context, String type) {
        super(context);
        entityType = type;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        if (entity.isInvisible()) {
            return;
        }

        VillagerData data = entity.getVillagerData();
        M entityModel = getContextModel();

        if (entity.isBaby() || data.getProfession() == VillagerProfession.NONE) {
            Identifier typeSkin = createTexture("type", Registries.VILLAGER_TYPE.getId(data.getType()));
            if (!ResourceUtil.textureExists(typeSkin)) {
                typeSkin = createTexture("type", Registries.VILLAGER_TYPE.getId(VillagerType.PLAINS));
            }
            renderModel(entityModel, typeSkin, matrixStack, provider, i, entity, Colors.WHITE);
        } else {
            renderModel(entityModel, getMergedTexture(data), matrixStack, provider, i, entity, Colors.WHITE);
        }
    }

    public Identifier getMergedTexture(VillagerData data) {
        VillagerType type = data.getType();
        VillagerProfession profession = data.getProfession();
        int level = MathHelper.clamp(data.getLevel(), 1, LEVEL_TO_ID.size());

        Identifier typeId = Registries.VILLAGER_TYPE.getId(type);
        Identifier profId = Registries.VILLAGER_PROFESSION.getId(profession);

        Identifier key = MineLittlePony.id((typeId + "/" + profId + "/" + level).replace(':', '_'));

        if (loadedTextures.add(key) && !ResourceUtil.textureExists(key)) {
            TextureFlattener.flatten(computeTextures(typeId, profId, profession == VillagerProfession.NITWIT ? -1 : level), key);
        }

        return key;
    }

    private List<Identifier> computeTextures(Identifier typeId, Identifier profId, int level) {
        List<Identifier> skins = new ArrayList<>();

        Identifier typeTexture = createTexture("type", typeId);
        if (ResourceUtil.textureExists(typeTexture)) {
            skins.add(typeTexture);
        }

        Identifier profTexture = createTexture("profession", profId);
        skins.add(ResourceUtil.textureExists(profTexture) ? profTexture : createTexture("profession", Identifier.of(VillagerProfession.NITWIT.id())));

        if (level != -1) {
            skins.add(createTexture("profession_level", LEVEL_TO_ID.get(level)));
        }

        return skins;
    }

    public Identifier createTexture(VillagerDataContainer entity, String category) {
        return createTexture(category, Registries.VILLAGER_PROFESSION.getId(entity.getVillagerData().getProfession()));
    }

    private Identifier createTexture(String category, Identifier identifier) {
        return MineLittlePony.id(String.format("textures/entity/%s/%s/%s.png", entityType, category, identifier.getPath()));
    }
}
