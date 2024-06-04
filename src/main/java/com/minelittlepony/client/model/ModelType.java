package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.*;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.model.entity.*;
import com.minelittlepony.client.model.entity.race.*;
import com.minelittlepony.client.model.gear.*;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public final class ModelType {
    private static final Map<Race, PlayerModelKey<?, ?>> PLAYER_MODELS = new HashMap<>();
    private static final Map<Wearable, GearModelKey<? extends Gear>> GEAR_MODELS = new HashMap<>();

    public static final ModelKey<DJPon3EarsModel> DJ_PON_3 = register("dj_pon_three", DJPon3EarsModel::new);

    public static final ModelKey<WitchPonyModel> WITCH = register("witch", WitchPonyModel::new);
    public static final ModelKey<ZomponyModel<?>> ZOMBIE = register("zombie", ZomponyModel::new);
    public static final ModelKey<PiglinPonyModel> PIGLIN = register("piglin", PiglinPonyModel::new);
    public static final ModelKey<SkeleponyModel<?>> SKELETON = register("skeleton", SkeleponyModel::new);
    public static final ModelKey<SkeleponyModel<?>> SKELETON_CLOTHES = register("skeleton_clothes", SkeleponyModel::new);
    public static final ModelKey<PillagerPonyModel<?>> PILLAGER = register("pillager", PillagerPonyModel::new);
    public static final ModelKey<IllagerPonyModel<?>> ILLAGER = register("illager", IllagerPonyModel::new);
    public static final ModelKey<GuardianPonyModel> GUARDIAN = register("guardian", GuardianPonyModel::new);
    public static final ModelKey<EnderStallionModel> ENDERMAN = register("enderman", EnderStallionModel::new);
    public static final ModelKey<ParaspriteModel<VexEntity>> VEX = register("vex", ParaspriteModel::new);
    public static final ModelKey<SpikeModel<StriderEntity>> STRIDER = register("strider", SpikeModel::new);
    public static final ModelKey<SaddleModel<StriderEntity>> STRIDER_SADDLE = register("strider_saddle", SaddleModel::new);
    public static final ModelKey<BreezieModel<AllayEntity>> ALLAY = register("allay", BreezieModel::new);

    public static final ModelKey<PonyElytra<?>> ELYTRA = register("elytra", PonyElytra::new);

    public static final ModelKey<ArmorStandEntityModel> ARMOUR_STAND = register("armour_stand", PonyArmourStandModel::new);
    public static final ModelKey<PonyArmourModel<?>> INNER_VANILLA_ARMOR = register("armor/inner_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<PonyArmourModel<?>> OUTER_VANILLA_ARMOR = register("armor/outer_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<PonyArmourModel<?>> INNER_PONY_ARMOR = register("armor/inner_pony_armor", PonyArmourModel::new);
    public static final ModelKey<PonyArmourModel<?>> OUTER_PONY_ARMOR = register("armor/outer_pony_armor", PonyArmourModel::new);

    public static final GearModelKey<AbstractGearModel> STETSON = registerGear("stetson", Wearable.STETSON, t -> new WearableGear(Wearable.STETSON, BodyPart.HEAD, 0.15F).addPart(t));
    public static final GearModelKey<SaddleBags> SADDLEBAGS_BOTH = registerGear("saddlebags", Wearable.SADDLE_BAGS_BOTH, t -> new SaddleBags(t, Wearable.SADDLE_BAGS_BOTH));
    public static final GearModelKey<SaddleBags> SADDLEBAGS_LEFT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_LEFT, t -> new SaddleBags(t, Wearable.SADDLE_BAGS_LEFT));
    public static final GearModelKey<SaddleBags> SADDLEBAGS_RIGHT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_RIGHT, t -> new SaddleBags(t, Wearable.SADDLE_BAGS_RIGHT));
    public static final GearModelKey<Crown> CROWN = registerGear("crown", Wearable.CROWN, Crown::new);
    public static final GearModelKey<AbstractGearModel> MUFFIN = registerGear("muffin", Wearable.MUFFIN, t -> new WearableGear(Wearable.MUFFIN, BodyPart.HEAD, 0.45F).addPart(t.getChild("crown")));
    public static final GearModelKey<AbstractGearModel> WITCH_HAT = registerGear("witch_hat", Wearable.HAT, t -> new WearableGear(Wearable.HAT, BodyPart.HEAD, 0.7F).addPart(t.getChild("hat")));
    public static final GearModelKey<DeerAntlers> ANTLERS = registerGear("antlers", Wearable.ANTLERS, DeerAntlers::new);

    public static final PlayerModelKey<LivingEntity, AlicornModel<?>> ALICORN = registerPlayer("alicorn", Race.ALICORN, AlicornModel::new);
    public static final PlayerModelKey<LivingEntity, UnicornModel<?>> UNICORN = registerPlayer("unicorn", Race.UNICORN, UnicornModel::new);
    public static final PlayerModelKey<LivingEntity, KirinModel<?>> KIRIN = registerPlayer("kirin", Race.KIRIN, KirinModel::new);
    public static final PlayerModelKey<LivingEntity, PegasusModel<?>> PEGASUS = registerPlayer("pegasus", Race.PEGASUS, PegasusModel::new);
    public static final PlayerModelKey<LivingEntity, PegasusModel<?>> GRYPHON = registerPlayer("gryphon", Race.GRYPHON, PegasusModel::new);
    public static final PlayerModelKey<LivingEntity, PegasusModel<?>> HIPPOGRIFF = registerPlayer("hippogriff", Race.HIPPOGRIFF, PegasusModel::new, PonyArmourModel::new);
    public static final PlayerModelKey<LivingEntity, EarthPonyModel<?>> EARTH_PONY = registerPlayer("earth_pony", Race.EARTH, EarthPonyModel::new);
    public static final PlayerModelKey<LivingEntity, SeaponyModel<?>> SEA_PONY = registerPlayer("sea_pony", Race.SEAPONY, SeaponyModel::new, SeaponyModel.Armour::new);
    public static final PlayerModelKey<LivingEntity, PegasusModel<?>> BAT_PONY = registerPlayer("bat_pony", Race.BATPONY, PegasusModel::new);
    public static final PlayerModelKey<LivingEntity, ChangelingModel<?>> CHANGELING = registerPlayer("changeling", Race.CHANGELING, ChangelingModel::new);
    public static final PlayerModelKey<LivingEntity, ChangelingModel<?>> CHANGEDLING = registerPlayer("reformed_changeling", Race.CHANGEDLING, ChangelingModel::new);
    public static final PlayerModelKey<LivingEntity, EarthPonyModel<?>> ZEBRA = registerPlayer("zebra", Race.ZEBRA, EarthPonyModel::new);

    static <E extends LivingEntity, T extends Model & MsonModel & PonyModel<?>> PlayerModelKey<E, T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor) {
        return registerPlayer(name, race, constructor, PonyArmourModel::new);
    }

    @SuppressWarnings("unchecked")
    static <E extends LivingEntity, T extends Model & MsonModel & PonyModel<?>> PlayerModelKey<E, T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor,
            MsonModel.Factory<PonyArmourModel<E>> armorFactory) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.computeIfAbsent(race, r -> new PlayerModelKey<>(name, constructor, armorFactory));
    }

    @SuppressWarnings("unchecked")
    static <T extends AbstractGearModel> GearModelKey<T> registerGear(String name, Wearable wearable, MsonModel.Factory<T> constructor) {
        return (GearModelKey<T>)GEAR_MODELS.computeIfAbsent(wearable, w -> {
            return new GearModelKey<T>(Mson.getInstance().registerModel(MineLittlePony.id("gear/" + name), constructor), constructor);
        });
    }

    @SuppressWarnings("unchecked")
    static <T extends AbstractGearModel> GearModelKey<T> registerGear(GearModelKey<T> key, Wearable wearable, MsonModel.Factory<T> constructor) {
        return (GearModelKey<T>)GEAR_MODELS.computeIfAbsent(wearable, w -> new GearModelKey<T>(key.key, constructor));
    }

    static <T extends Model> ModelKey<T> register(String name, MsonModel.Factory<T> constructor) {
        return new ModelKeyImpl<T>(MineLittlePony.id(name), constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends LivingEntity, T extends Model & MsonModel & PonyModel<?>> PlayerModelKey<E, T> getPlayerModel(Race race) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.get(race);
    }

    public static Stream<Map.Entry<Wearable, GearModelKey<? extends Gear>>> getWearables() {
        return GEAR_MODELS.entrySet().stream();
    }

    public static void bootstrap() { }

    public record GearModelKey<T extends Gear>(ModelKey<T> key, MsonModel.Factory<T> constructor) {
        public T createModel() {
            return key.createModel(constructor);
        }
    }
}
