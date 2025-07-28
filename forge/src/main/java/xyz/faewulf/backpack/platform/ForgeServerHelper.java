package xyz.faewulf.backpack.platform;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.mixin.ItemStackHandlerInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;

public class ForgeServerHelper implements IServerHelper {
    @Override
    public boolean isWearingBackpack(Player player) {
        // If don't have any backpack mod installed then return true
        if (
                !Constants.SOPHISTICATED_BACKPACKS_LOADED
                        && !Constants.TRAVELERS_BACKPACK_LOADED
        ) {
            return true;
        }

        // Below this is if trinket not installed and when done after checking all trinket slots
        boolean resultOutsideTrinket = false;

        // Travelers backpack
        if (!resultOutsideTrinket && Constants.TRAVELERS_BACKPACK_LOADED) {
            resultOutsideTrinket = AttachmentUtils.isWearingBackpack(player);
        }

        return resultOutsideTrinket;
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Constants.TRAVELERS_BACKPACK_LOADED) {
            ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
            if (AttachmentUtils.getBackpackWrapper(player, backpack) != null) {
                List<ItemStack> itemStackList = new ArrayList<>();

                itemStackList.addAll(((ItemStackHandlerInvoker) AttachmentUtils.getBackpackWrapper(player, backpack).getStorage()).getStacks());
                itemStackList.addAll(((ItemStackHandlerInvoker) AttachmentUtils.getBackpackWrapper(player, backpack).getTools()).getStacks());

                return itemStackList;
            }
        }

        return IServerHelper.super.getBackpackInventory(player);
    }
}
