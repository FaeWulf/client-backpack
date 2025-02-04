package xyz.faewulf.backpack.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.util.DataSync;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class onClientTickEvent {
    private static int timer = 0;

    @SubscribeEvent
    public static void onClientEndTickEvent(Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null)
            return;

        timer++;

        if (timer >= 100) {

            System.out.println("ticking");

            timer = 0;

            Constants.PLAYER_INV_STATUS.forEach((s, backpackStatus) -> {
                // Skip LocalPlayer
                if (s.equals(Constants.DUMMY_PLAYER_NAME) || s.equals(minecraft.player.getName().getString()))
                    return;

                // Max 20 to avoid api rate limit
                if (DataSync.UPDATE_QUEUE.size() > 20)
                    return;

                if (!backpackStatus.isHasUpdateBackpackType() && backpackStatus.getUuid() != null) {
                    DataSync.UPDATE_QUEUE.put(s, backpackStatus.getUuid());
                }
            });

            DataSync.requestUpdateData();
        }
    }
}