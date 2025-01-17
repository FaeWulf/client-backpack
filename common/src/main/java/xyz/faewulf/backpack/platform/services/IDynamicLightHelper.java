package xyz.faewulf.backpack.platform.services;

import net.minecraft.world.item.ItemStack;

public interface IDynamicLightHelper {
    default int getLuminance(ItemStack itemStack) {
        return 0;
    }
}
