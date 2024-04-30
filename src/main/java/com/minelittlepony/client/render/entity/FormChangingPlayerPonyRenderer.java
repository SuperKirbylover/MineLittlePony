package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.*;

import java.util.function.Predicate;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class FormChangingPlayerPonyRenderer extends PlayerPonyRenderer {
    protected boolean transformed;

    private final Identifier alternateFormSkinId;
    private final Predicate<AbstractClientPlayerEntity> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererFactory.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<AbstractClientPlayerEntity> formModifierPredicate) {
        super(context, slim);
        this.alternateFormSkinId = alternateFormSkinId;
        this.formModifierPredicate = formModifierPredicate;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        if (transformed) {
            return SkinsProxy.instance.getSkin(alternateFormSkinId, player).orElseGet(() -> super.getTexture(player));
        }
        return super.getTexture(player);
    }

    @Override
    protected final void preRender(AbstractClientPlayerEntity player, ModelAttributes.Mode mode) {
        super.preRender(player, mode);
        updateForm(player);
    }

    protected void updateForm(AbstractClientPlayerEntity player) {
        transformed = formModifierPredicate.test(player);
    }
}
