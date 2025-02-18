package xyz.faewulf.backpack.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.neoforge.RyoamicDynLights.RyoamicDynLightsModule;
import xyz.faewulf.backpack.platform.Services;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class onClientInit {
    @SubscribeEvent
    public static void onClientInitiation(FMLClientSetupEvent event) {
        if (Services.PLATFORM.isModLoaded("ryoamiclights")) {
            RyoamicDynLightsModule.init();
        }
    }
}