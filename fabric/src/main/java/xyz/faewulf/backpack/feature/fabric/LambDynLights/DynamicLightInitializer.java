package xyz.faewulf.backpack.feature.fabric.LambDynLights;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.EntityType;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.DynamicLightBehavior;

public class DynamicLightInitializer implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {
        Constants.DYNAMIC_LIGHT_COMPAT_LAYER = new LambDynLightsModule();
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, DynamicLightBehavior::playerLuminance);
    }
}
