package xyz.faewulf.backpack.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.platform.services.IClientHelper;

public class ForgeClientHelper implements IClientHelper {

    @Override
    public BakedModel getCustomBakedModel(ResourceLocation location) {
        return Minecraft.getInstance().getModelManager().getModel(location);
    }
}
