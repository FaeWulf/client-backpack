package xyz.faewulf.backpack.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.platform.Services;

import java.util.List;

public class Compare {
    public static boolean hasInventoryChanged(Player player) {
        List<ItemStack> previousSnapshot = Constants.PLAYER_INV.get(player.getName().getString());

        if (previousSnapshot == null) {
            return true;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack currentItem = player.getInventory().getItem(slot);
            ItemStack previousItem = previousSnapshot.get(slot);

            if (!ItemStack.matches(currentItem, previousItem)) {
                return true; // Inventory has changed
            }
        }

        // For backpack
        int slotIndex = player.getInventory().getContainerSize();
        for (ItemStack itemStack : Services.SERVER_HELPER.getBackpackInventory(player)) {

            if (slotIndex >= previousSnapshot.size())
                continue;

            ItemStack previousItem = previousSnapshot.get(slotIndex);
            slotIndex++;

            if (!ItemStack.matches(itemStack, previousItem)) {
                return true; // Inventory has changed
            }
        }

        return false; // No changes
    }
}
