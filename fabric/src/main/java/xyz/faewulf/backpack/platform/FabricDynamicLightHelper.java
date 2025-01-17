package xyz.faewulf.backpack.platform;

import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.feature.DynamicLightInitializer;
import xyz.faewulf.backpack.platform.services.IDynamicLightHelper;

public class FabricDynamicLightHelper implements IDynamicLightHelper {
    @Override
    public int getLuminance(ItemStack itemStack) {
        if (DynamicLightInitializer.itemLightSourceManager == null)
            return 0;

        return DynamicLightInitializer.itemLightSourceManager.getLuminance(itemStack);
    }
}
