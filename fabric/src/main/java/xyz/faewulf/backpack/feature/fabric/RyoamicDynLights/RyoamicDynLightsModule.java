package xyz.faewulf.backpack.feature.fabric.RyoamicDynLights;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.thinkingstudio.ryoamiclights.RyoamicLights;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.DynamicLightBehavior;
import xyz.faewulf.backpack.inter.IDynamicLightCompatLayer;

public class RyoamicDynLightsModule implements IDynamicLightCompatLayer {

    public static void init() {
        Constants.DYNAMIC_LIGHT_COMPAT_LAYER = new RyoamicDynLightsModule();
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, DynamicLightBehavior::playerLuminance);
    }

    @Override
    public int getLuminance(ItemStack itemstack, boolean careWater) {
        return RyoamicLights.getLuminanceFromItemStack(itemstack, careWater);
    }
}
