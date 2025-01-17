package xyz.faewulf.backpack.feature;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.Constants;

public class DynamicLightInitializer implements DynamicLightsInitializer {
    public static final PlayerLuminance.Type CUSTOM_ENTITY_LUMINANCE
            = PlayerLuminance.Type.register(
            ResourceLocation.tryBuild(Constants.MOD_ID, "light_module"),
            PlayerLuminance.CODEC
    );

    public static ItemLightSourceManager itemLightSourceManager = null;

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        itemLightSourceManager = context.itemLightSourceManager();
    }
}
