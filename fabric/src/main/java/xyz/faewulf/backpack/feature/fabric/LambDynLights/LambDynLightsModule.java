package xyz.faewulf.backpack.feature.fabric.LambDynLights;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.DynamicLightBehavior;
import xyz.faewulf.backpack.inter.IDynamicLightCompatLayer;

public class LambDynLightsModule implements IDynamicLightCompatLayer {

    public static void init() {
        Constants.DYNAMIC_LIGHT_COMPAT_LAYER = new LambDynLightsModule();
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, DynamicLightBehavior::playerLuminance);
    }

    @Override
    public int getLuminance(ItemStack itemstack, boolean careWater) {
        return LambDynLights.getLuminanceFromItemStack(itemstack, careWater);
    }
}
