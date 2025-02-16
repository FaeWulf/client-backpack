package xyz.faewulf.backpack.platform;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.mixin.ItemStackHandlerInvoker;
import xyz.faewulf.backpack.mixin.compat.travelerbackpack.AttachmentUtilsInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeServerHelper implements IServerHelper {
    @Override
    public boolean isWearingBackpack(Player player) {
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
            return AttachmentUtilsInvoker.isWearingBackpackInvoked(player);
        }

        return IServerHelper.super.isWearingBackpack(player);
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
            ItemStack backpack = AttachmentUtilsInvoker.getWearingBackpackInvoked(player);
            if (AttachmentUtilsInvoker.getBackpackWrapper(player, backpack) != null) {
                List<ItemStack> itemStackList = new ArrayList<>();

                itemStackList.addAll(((ItemStackHandlerInvoker) AttachmentUtilsInvoker.getBackpackWrapper(player, backpack).getStorage()).getStacks());
                itemStackList.addAll(((ItemStackHandlerInvoker) AttachmentUtilsInvoker.getBackpackWrapper(player, backpack).getTools()).getStacks());

                return itemStackList;
            }
        }

        return IServerHelper.super.getBackpackInventory(player);
    }
}
