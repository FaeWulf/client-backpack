package xyz.faewulf.backpack.inter;

import net.minecraft.world.item.ItemStack;

public interface IDynamicLightCompatLayer {
    default int getLuminance(ItemStack itemStack, boolean canLightInWater) {
        return 0;
    }
}
