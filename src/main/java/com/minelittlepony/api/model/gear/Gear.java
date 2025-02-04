package com.minelittlepony.api.model.gear;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Interface for an accessory on a pony's body.
 */
public interface Gear {
    List<Supplier<Gear>> MOD_GEARS = new ArrayList<>();

    /**
     * Registers a custom gear to be used with the mod.
     * <p>
     * This would be awesome for creating socks.
     */
    static Supplier<Gear> register(Supplier<Gear> gear) {
        MOD_GEARS.add(gear);
        return gear;
    }

    /**
     * Determines if this wearable can and is worn by the selected entity.
     *
     * @param model     The primary model
     * @param entity    The entity being rendered
     *
     * @return True to render this wearable
     */
    boolean canRender(PonyModel<?> model, Entity entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    default boolean isStackable() {
        return false;
    }

    /**
     * The vertical height of this gear when present in a stack.
     *
     * Any gear rendered after this one will be shifted to sit on top of it.
     */
    default float getStackingHeight() {
        return 0;
    }

    /**
     * Gets the texture to use for this wearable.
     *
     * If you need to use the player's own skin, use {@link IRenderContext#getDefaultTexture(entity, wearable)}
     */
    <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context);

    /**
     * Gets the layer used to render this piece of gear.
     */
    default <T extends Entity> RenderLayer getLayer(T entity, Context<T, ?> context) {
        return RenderLayer.getEntityTranslucent(getTexture(entity, context));
    }

    /**
     * Applies body transformations for this wearable
     */
    default <M extends EntityModel<?> & PonyModel<?>> void transform(M model, MatrixStack matrices) {
        BodyPart part = getGearLocation();
        model.transform(part,  matrices);
        model.getBodyPart(part).rotate(matrices);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyMode.setRotationAndAngle} for an explanation of the various parameters.
     */
    default void pose(PonyModel<?> model, Entity entity, boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {

    }

    /**
     * Renders this model component.
     */
    void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color, UUID interpolatorId);

    /**
     * A render context for instance of IGear.
     *
     * @param <T> The type of entity being rendered.
     * @param <M> The type of the entity's primary model.
     */
    public interface Context<T extends Entity, M extends PonyModel<?>> {
        /**
         * The empty context.
         */
        Context<?, ?> NULL = (e, g) -> g.getDefaultTexture();

        /**
         * Checks whether the given wearable and gear are able to render for this specific entity and its renderer.
         */
        default boolean shouldRender(M model, T entity, Wearable wearable, Gear gear) {
            return gear.canRender(model, entity);
        }

        /**
         * Gets the default texture to use for this entity and wearable.
         *
         * May be the entity's own texture or a specific texture allocated for that wearable.
         */
        Identifier getDefaultTexture(T entity, Wearable wearable);
    }
}
