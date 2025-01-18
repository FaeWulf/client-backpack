package xyz.faewulf.backpack.platform;

import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.platform.services.IDynamicLightHelper;

public class NeoForgeDynamicLightHelper implements IDynamicLightHelper {
    @Override
    public int getLuminance(ItemStack itemStack) {
        return IDynamicLightHelper.super.getLuminance(itemStack);
    }
}
