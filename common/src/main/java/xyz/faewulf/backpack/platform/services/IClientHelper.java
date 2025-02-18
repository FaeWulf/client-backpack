package xyz.faewulf.backpack.platform.services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public interface IClientHelper {
    default BakedModel getCustomBakedModel(ResourceLocation location) {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        return manager.getModel(new ModelResourceLocation(location, ""));
    }
}
