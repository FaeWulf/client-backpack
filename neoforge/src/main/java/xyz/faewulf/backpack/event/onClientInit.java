package xyz.faewulf.backpack.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.neoforge.RyoamicDynLights.RyoamicDynLightsModule;
import xyz.faewulf.backpack.platform.Services;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class onClientInit {
    @SubscribeEvent
    public static void onClientInitiation(FMLClientSetupEvent event) {
        if (Services.PLATFORM.isModLoaded("ryoamiclights")) {
            RyoamicDynLightsModule.init();
        }
    }
}