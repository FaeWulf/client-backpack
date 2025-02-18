package xyz.faewulf.backpack.platform;

import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.platform.services.IDynamicLightHelper;

public class NeoForgeDynamicLightHelper implements IDynamicLightHelper {
    @Override
    public int getLuminance(ItemStack itemStack) {
        if (Constants.DYNAMIC_LIGHT_COMPAT_LAYER == null)
            return 0;

        return Constants.DYNAMIC_LIGHT_COMPAT_LAYER.getLuminance(itemStack, false);
    }
}
