package xyz.faewulf.backpack.platform.services;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IServerHelper {
    default boolean isWearingBackpack(Player player) {
        return true;
    }

    default List<ItemStack> getBackpackInventory(Player player) {
        return new ArrayList<>();
    }
}
