package xyz.faewulf.backpack.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.Constants;

public class CustomModelLayers {
    public static final ModelLayerLocation BACKPACK = new ModelLayerLocation(ResourceLocation.tryBuild(Constants.MOD_ID, "backpack"), "main");
}
