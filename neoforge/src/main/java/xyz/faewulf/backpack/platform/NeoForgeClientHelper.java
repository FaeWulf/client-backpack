package xyz.faewulf.backpack.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.platform.services.IClientHelper;

public class NeoForgeClientHelper implements IClientHelper {

    @Override
    public BakedModel getCustomBakedModel(ResourceLocation location) {
        return Minecraft.getInstance().getModelManager().getStandaloneModel(location);
    }
}
