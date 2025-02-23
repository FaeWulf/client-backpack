package xyz.faewulf.backpack.platform;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.mixin.ItemStackHandlerInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeServerHelper implements IServerHelper {
    @Override
    public boolean isWearingBackpack(Player player) {
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
            return AttachmentUtils.isWearingBackpack(player);
        }

        return IServerHelper.super.isWearingBackpack(player);
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
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
