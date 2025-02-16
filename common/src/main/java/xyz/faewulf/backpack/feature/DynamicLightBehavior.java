package xyz.faewulf.backpack.feature;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.config.ModConfigs;

import java.util.List;

public class DynamicLightBehavior {
    public static int playerLuminance(Player player) {
        int maxLight = 0;

        if (!ModConfigs.enable_dynamiclight_compat)
            return maxLight;

        if (player instanceof AbstractClientPlayer abstractClientPlayer) {

            BackpackStatus backpackStatus = Constants.PLAYER_INV_STATUS.get(abstractClientPlayer.getName().getString());
            List<ItemStack> itemStackList = Constants.PLAYER_INV.get(abstractClientPlayer.getName().getString());

            if (backpackStatus == null)
                return maxLight;

            DetailBackpack detailBackpack = BackpackModelRegistry.getBackpackDetail(backpackStatus.getBackpackType(), backpackStatus.getBackpackVariant());

            // If the current backpack doesn't have light source module
            if (detailBackpack == null || detailBackpack.light_source == null)
                return maxLight;

            // If not wearing any backpack
            if (!backpackStatus.isWearingBackpack())
                return maxLight;

            // Infomation from serverside
            if (backpackStatus.isHasLightSource())
                maxLight = 10;

            if (itemStackList != null)
                for (ItemStack item : itemStackList) {
                    int lightValue = Services.DYNAMIC_LIGHT_HELPER.getLuminance(item);
                    if (lightValue > maxLight)
                        maxLight = lightValue;
                }
        }

        return maxLight;
    }
}
