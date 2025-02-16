package xyz.faewulf.backpack.feature.fabric.LambDynLights;

import dev.lambdaurora.lambdynlights.LambDynLights;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.inter.IDynamicLightCompatLayer;

public class LambDynLightsModule implements IDynamicLightCompatLayer {
    @Override
    public int getLuminance(ItemStack itemstack, boolean careWater) {
        return LambDynLights.getLuminanceFromItemStack(itemstack, careWater);
    }
}
