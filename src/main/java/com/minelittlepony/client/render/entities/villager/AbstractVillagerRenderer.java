package com.minelittlepony.client.render.entities.villager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entities.RenderPonyMob;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.util.resources.ITextureSupplier;

abstract class AbstractVillagerRenderer<
    T extends MobEntity & VillagerDataContainer,
    M extends ClientPonyModel<T> & IUnicorn<PonyRenderer> & ModelWithHat> extends RenderPonyMob.Caster<T, M> {

    private final ITextureSupplier<T> professions;

    private final String entityType;

    public AbstractVillagerRenderer(EntityRenderDispatcher manager, M model, String type, ITextureSupplier<String> formatter) {
        super(manager, model);

        entityType = type;
        professions = new PonyTextures<>(formatter);
        addFeature(new ClothingLayer<>(this, entityType));
    }

    @Override
    public boolean shouldRender(M model, T entity, IGear gear) {

        boolean special = PonyTextures.isBestPony(entity);

        if (gear == LayerGear.SADDLE_BAGS) {
            VillagerProfession profession = entity.getVillagerData().getProfession();
            return !special && profession != VillagerProfession.NONE && (
                    profession == VillagerProfession.CARTOGRAPHER
                 || profession == VillagerProfession.FARMER
                 || profession == VillagerProfession.FISHERMAN
                 || profession == VillagerProfession.LIBRARIAN
                 || profession == VillagerProfession.SHEPHERD);
        }

        if (gear == LayerGear.MUFFIN) {
            return PonyTextures.isCrownPony(entity);
        }

        return super.shouldRender(model, entity, gear);
    }

    @Override
    public Identifier getDefaultTexture(T villager, IGear gear) {
        if (gear == LayerGear.SADDLE_BAGS) {
            return ClothingLayer.getClothingTexture(villager, entityType);
        }
        return super.getDefaultTexture(villager, gear);
    }

    @Override
    public Identifier findTexture(T villager) {
        return professions.supplyTexture(villager);
    }
}