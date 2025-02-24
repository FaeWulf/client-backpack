package xyz.faewulf.backpack.platform;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.TrinketsApi;
import draylar.inmis.item.BackpackItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.mixin.compat.travelerbackpack.ComponentUtilsInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;

public class FabricServerHelper implements IServerHelper {
    @Override
    public boolean isWearingBackpack(Player player) {

        // If don't have any backpack mod installed then return true
        if (
                !Constants.INMIS_BACKPACK_LOADED
                        && !Constants.SOPHISTICATED_BACKPACKS_LOADED
                        && !Constants.TRAVELERS_BACKPACK_LOADED
        ) {
            return true;
        }

        // If trinkets mod installed
        if (Constants.TRINKETS_LOADED) {
            boolean resultInTrinket = false;

            // checking backpack in slots
            if (TrinketsApi.getTrinketComponent(player).isPresent()) {

                // result = true if has backpack in trinket
                resultInTrinket = TrinketsApi.getTrinketComponent(player).get().isEquipped(item -> {

                    boolean result_ = false;
                    String itemName = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();

                    // Sophisticate backpack
                    if (Constants.SOPHISTICATED_BACKPACKS_LOADED
                            && itemName.contains("sophisticatedbackpacks:")
                    ) {
                        result_ = true;
                    }

                    // Inmis backpack
                    if (Constants.INMIS_BACKPACK_LOADED && item.getItem() instanceof BackpackItem) {
                        result_ = true;
                    }

                    // Travelers backpack
                    if (Constants.TRAVELERS_BACKPACK_LOADED && item.getItem() instanceof TravelersBackpackItem) {
                        result_ = true;
                    }

                    return result_;
                });
            }

            // if has backpack in trinket slot then result true
            if (resultInTrinket)
                return true;
        }

        // Below this is if trinket not installed and when done after checking all trinket slots
        boolean resultOutsideTrinket = false;

        // Sophisticate backpack
        if (Constants.SOPHISTICATED_BACKPACKS_LOADED) {
            resultOutsideTrinket = PlayerInventoryProvider.get().getBackpackFromRendered(player).isPresent();
        }

        // Inmis
        if (!resultOutsideTrinket && Constants.INMIS_BACKPACK_LOADED) {
            resultOutsideTrinket = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BackpackItem;
        }

        // Travelers backpack
        if (!resultOutsideTrinket && Constants.TRAVELERS_BACKPACK_LOADED) {
            resultOutsideTrinket = ComponentUtilsInvoker.isWearingBackpackInvoked(player);
        }

        return resultOutsideTrinket;
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Constants.TRAVELERS_BACKPACK_LOADED) {
            ItemStack backpack = ComponentUtilsInvoker.getWearingBackpackInvoked(player);
            if (ComponentUtilsInvoker.getBackpackWrapper(player, backpack) != null) {
                List<ItemStack> itemStackList = new ArrayList<>();

                BackpackWrapper backpackWrapper = ComponentUtilsInvoker.getBackpackWrapper(player, backpack);

                if (backpackWrapper != null) {

                    if (backpackWrapper.getStorage() != null)
                        for (int i = 0; i < backpackWrapper.getStorage().getContainerSize(); i++) {
                            itemStackList.add(backpackWrapper.getStorage().getItem(i));
                        }

                    if (backpackWrapper.getTools() != null)
                        for (int i = 0; i < backpackWrapper.getTools().getContainerSize(); i++) {
                            itemStackList.add(backpackWrapper.getTools().getItem(i));
                        }
                }

                return itemStackList;
            }
        }

        return IServerHelper.super.getBackpackInventory(player);
    }
}
