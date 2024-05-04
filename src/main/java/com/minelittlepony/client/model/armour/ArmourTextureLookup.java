package com.minelittlepony.client.model.armour;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ArmourTextureLookup {
    ArmourTexture getTexture(ItemStack stack, ArmourLayer layer, ArmorMaterial.Layer armorLayer);

    List<ArmorMaterial.Layer> getArmorLayers(ItemStack stack, int dyeColor);
}
