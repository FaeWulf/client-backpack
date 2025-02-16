package xyz.faewulf.backpack.platform;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.mixin.compat.travelerbackpack.ComponentUtilsInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;

public class FabricServerHelper implements IServerHelper {
    @Override
    public boolean isWearingBackpack(Player player) {
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
            return ComponentUtilsInvoker.isWearingBackpackInvoked(player);
        }

        return IServerHelper.super.isWearingBackpack(player);
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Services.PLATFORM.isModLoaded("travelersbackpack")) {
            ItemStack backpack = ComponentUtilsInvoker.getWearingBackpackInvoked(player);
            if (ComponentUtilsInvoker.getBackpackWrapper(player, backpack) != null) {
                List<ItemStack> itemStackList = new ArrayList<>();

                itemStackList.addAll(ComponentUtilsInvoker.getBackpackWrapper(player, backpack).getStorage().stacks);
                itemStackList.addAll(ComponentUtilsInvoker.getBackpackWrapper(player, backpack).getTools().stacks);

                return itemStackList;
            }
        }

        return IServerHelper.super.getBackpackInventory(player);
    }
}
