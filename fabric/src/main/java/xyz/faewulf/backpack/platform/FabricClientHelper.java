package xyz.faewulf.backpack.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.platform.services.IClientHelper;

public class FabricClientHelper implements IClientHelper {
    @Override
    public BakedModel getCustomBakedModel(ResourceLocation location) {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        return manager.getModel(location);
    }
}
