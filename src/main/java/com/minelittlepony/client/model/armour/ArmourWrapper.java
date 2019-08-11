package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPonyData;

import java.util.function.Supplier;

public class ArmourWrapper<T extends LivingEntity> implements IEquestrianArmour<ModelPonyArmour<T>> {

    private final ModelPonyArmour<T> outerLayer;
    private final ModelPonyArmour<T> innerLayer;

    public ArmourWrapper(Supplier<ModelPonyArmour<T>> supplier) {
        outerLayer = supplier.get();
        innerLayer = supplier.get();
    }

    @Override
    public void apply(IPonyData meta) {
        outerLayer.apply(meta);
        innerLayer.apply(meta);
    }

    @Override
    public void init() {
        outerLayer.init(0, 1.05F);
        innerLayer.init(0, 0.5F);
    }

    @Override
    public ModelPonyArmour<T> getArmorForLayer(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
