package xyz.faewulf.backpack.platform;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import top.theillusivec4.curios.api.CuriosApi;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.mixin.ItemStackHandlerInvoker;
import xyz.faewulf.backpack.platform.services.IServerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

        // If trinkets mod installed
        if (Constants.CURIOS_LOADED) {
            AtomicBoolean resultInTrinket = new AtomicBoolean(false);

            // result = true if has backpack in trinket
            CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
                resultInTrinket.set(iCuriosItemHandler.isEquipped(item -> {

                    boolean result_ = false;
                    String itemName = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();

                    // Sophisticate backpack
                    if (Constants.SOPHISTICATED_BACKPACKS_LOADED
                            && itemName.contains("sophisticatedbackpacks:")
                    ) {
                        result_ = true;
                    }

                    // Travelers backpack
                    if (Constants.TRAVELERS_BACKPACK_LOADED && item.getItem() instanceof TravelersBackpackItem) {
                        result_ = true;
                    }

                    return result_;
                }));

            });

            // if has backpack in trinket slot then result true
            if (resultInTrinket.get())
                return true;
        }

        // Below this is if trinket not installed and when done after checking all trinket slots
        boolean resultOutsideTrinket = false;

        // Sophisticate backpack
        if (Constants.SOPHISTICATED_BACKPACKS_LOADED) {
            resultOutsideTrinket = PlayerInventoryProvider.get().getBackpackFromRendered(player).isPresent();
        }

        // Travelers backpack
        if (!resultOutsideTrinket && Constants.TRAVELERS_BACKPACK_LOADED) {
            resultOutsideTrinket = CapabilityUtils.isWearingBackpack(player);
        }

        return resultOutsideTrinket;
    }

    @Override
    public List<ItemStack> getBackpackInventory(Player player) {

        // Traveler backpack compat
        if (Constants.TRAVELERS_BACKPACK_LOADED) {
            ItemStack backpack = CapabilityUtils.getWearingBackpack(player);
            if (CapabilityUtils.getBackpackWrapper(player, backpack) != null) {
                List<ItemStack> itemStackList = new ArrayList<>();

                itemStackList.addAll(((ItemStackHandlerInvoker) CapabilityUtils.getBackpackWrapper(player, backpack).getStorage()).getStacks());
                itemStackList.addAll(((ItemStackHandlerInvoker) CapabilityUtils.getBackpackWrapper(player, backpack).getTools()).getStacks());

                return itemStackList;
            }
        }

        return IServerHelper.super.getBackpackInventory(player);
    }
}
