package xyz.faewulf.backpack.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import xyz.faewulf.backpack.Constants;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class onReloadResource {
    @SubscribeEvent
    public static void onReloadResourceEvent(AddPackFindersEvent event) {
    }
}